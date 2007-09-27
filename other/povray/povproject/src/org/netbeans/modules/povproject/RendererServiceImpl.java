/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * RendererServiceImpl.java
 *
 * Created on February 17, 2005, 4:32 PM
 */

package org.netbeans.modules.povproject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.povproject.RendererService;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openide.windows.WindowManager;

/**
 * A service which launches command-line POV-Ray, available in the 
 * project's lookup as an instance of RendererService.
 *
 * XXX - the output handling is really messy and full of busywaits;  there
 * must be a better way to find out if a process is done than checking its
 * exit code and looking for that not to throw an exception.
 *
 * @author Timothy Boudreau
 */
final class RendererServiceImpl implements RendererService {
    private PovProject project;
    private static File povray = null;
    private static File include = null;
    
    /** Preferences key for the povray executable */
    private static final String KEY_POVRAY_EXEC = "povray";
    /** Preferences key for the povray standard includes dir */
    private static final String KEY_POVRAY_INCLUDES = "include";
    
    /**
     * Set of all currently executing rendering processes in the system.
     */
    static Set runningRenders = Collections.synchronizedSet (new HashSet());

    
    /** Creates a new instance of RendererServiceImpl */
    public RendererServiceImpl(PovProject project) {
        this.project = project;
    }

    //TODO - Cancel render action provided to IOProvider.getIO() for the output toolbar
    

    public File render(File scene) {
        return render (scene, null);
    }

    public File render() {
        MainFileProvider provider = (MainFileProvider) project.getLookup().lookup (MainFileProvider.class);
        FileObject main = provider.getMainFile();
        if (main == null) {
            //XXX ask the user for a main file
            return null;
        }
        return render (FileUtil.toFile(main));
    }

    /**
     * Do the actual rendering.
     * @param scene - the file to render
     * @param renderSettings - properties such as width, height, etc. to send
     *   to the renderer
     */
    public File render(final File scene, Properties renderSettings) {
        assert scene.exists() : "Scene file " + scene + " doesn't exist";
        if (renderSettings == null) {
            renderSettings = getRendererSettings(getPreferredConfigurationName());
        }
        
        //Find the exe, prompting the user if need be
        File povray = getPovray();
        if (povray == null) {
            return null; //the user cancelled
        }
        
        //Make sure we know the standard include dir or many things won't
        //render
        File includes = getStandardIncludeDir(povray);
        if (includes == null) {
            return null; //the user cancelled
        }
        
        //Build the command line to pass to the process, assembling the
        //arguments from the passed data
        StringBuffer cmdline = new StringBuffer (quote(povray.getPath()) + " ");
        for (Iterator i=renderSettings.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = renderSettings.getProperty(key);
            cmdline.append ('+');
            cmdline.append (key);
            cmdline.append (val);
            cmdline.append (' ');
        }
        //Add the input file
        cmdline.append ("+I");
        cmdline.append (quote(scene.getPath()));
        cmdline.append (' ');
        
        //Add the output file
        cmdline.append ("+O");
        FileObject imagesDir = project.getProjectDirectory().getFileObject(PovProject.IMAGES_DIR);
        if (imagesDir == null) {
            try {
                imagesDir = project.getProjectDirectory().createFolder(PovProject.IMAGES_DIR);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ioe);
                return null;
            }
        }
        
        //Strip the .pov extension from the scene name to get the raw file name
        String sceneName = scene.getName();
        
        int endIndex;
        if ((endIndex = sceneName.lastIndexOf('.')) != -1) {
            sceneName = sceneName.substring(0, endIndex);
        }
        
        //Create an image file name (use PNG because NetBeans' image module
        //understands that
        final String imagesFile = quote (FileUtil.toFile(imagesDir).getPath() + File.separator + sceneName + ".png");
        cmdline.append (imagesFile);
        cmdline.append (' ');
        
        //Append the library path (the standard includes dir)
        cmdline.append ("+L");
        cmdline.append (quote(includes.getPath()));
        
        try {
            //Preemptively delete the existing image - otherwise a sill dialog 
            //asking to reload the image can appear if it's already open
            if (new File (imagesFile).exists()) {
                //Use FileObject, not File, so NetBeans immediately notices
                //it's been deleted
                FileUtil.toFileObject(new File(imagesFile)).delete();
            }
            
            //Create a new thread so we don't block the UI with our 
            //external process
            Thread thd = new Thread (new PovRunner (scene, imagesFile, cmdline.toString()), 
                    "Povray Runner - " + cmdline + " started " + new Date());
            
            //Run povray
            thd.start();
            
        } catch (Exception ioe) {
            ErrorManager.getDefault().notify(ioe);
            return null;
        }
        //Return File, not FileObject, since you can't have a FileObject for
        //a file that doesn't exist.  Eventually we could do something really
        //slick like an incremental targa reader (TGA format is dead simple)
        return new File(imagesFile);
    }

    /**
     * Runnable which launches the povray process.
     */
    private final class PovRunner implements Runnable {
        private final File sceneFile;
        private final String cmdline;
        private final String imagesFile;
        private Process process = null;
        private Thread a = null;
        private Thread b = null;
        private InputOutput io = null;
        
        PovRunner (File sceneFile, String imagesFile, String cmdline) {
            this.sceneFile = sceneFile;
            this.cmdline = cmdline;
            this.imagesFile = imagesFile;
            
            //If some other rendering process is already writing the same
            //image file we want to write, kill it.
            for (Iterator i=runningRenders.iterator(); i.hasNext();) {
                PovRunner run = (PovRunner) i.next();
                if (run.equals(this)) {
                    run.halt();
                }
            }
            
            //Add ourselves to the list.  NOTE:  If this class is ever made
            //extensible (bad idea), this call must be moved out of the 
            //constructor.
            runningRenders.add (this);
        }
        
        public void run() {
            
            //Get an InputOutput to write to the output window
            io = IOProvider.getDefault().getIO (
                NbBundle.getMessage (RendererServiceImpl.class, 
                "TTL_Rendering", sceneFile.getName()), false);
            
            try {
                //Reset it, clearing any existing output from a previous run on
                //this same file
                io.getOut().reset();
                
                //Write out the command line sent
                io.getOut().println(cmdline);
                
                synchronized (this) { //protect access to the "process" ivar
                    //Execute the process
                    process = Runtime.getRuntime().exec (cmdline);

                    //Get the standard out
                    InputStream out = new BufferedInputStream (process.getInputStream(), 8192);
                    //Get the standard in
                    InputStream err = new BufferedInputStream (process.getErrorStream(), 8192);

                    //Create readers for each
                    final Reader outReader = new BufferedReader (new InputStreamReader (out));
                    final Reader errReader = new BufferedReader (new InputStreamReader (err));

                    //YUCK!  Do this with NIO channels or PipedOutputStream or
                    //something eventually
                    a = new Thread (new OutHandler (outReader, io.getOut()),
                            "Povray stdout monitor for " + sceneFile);
                    b = new Thread (new OutHandler (errReader, io.getErr()), 
                            "Povray stderr monitor for " + sceneFile);
                }
                a.start();
                b.start();
                
                IllegalThreadStateException e = null;
                do {
                    try {
                        //A truly vile way to see if the process is done - 
                        //exitValue() will throw an exception if it isn't
                        process.exitValue();
                        
                        //If we got here, the process finished
                        e = null;
                        
                        //Interrupt our output handler threads so they finish
                        a.interrupt();
                        b.interrupt();
                        
                        done();
                    } catch (IllegalThreadStateException e1) {
                        //If the process *is* still running, exitValue()
                        //threw an exception.  Sleep and check again.
                        e = e1;
                        Thread.currentThread().sleep (500);
                    }
                } while (e != null);

            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                io.getOut().close();
                runningRenders.remove(this);
            }
        }
        
        public void done() {
            try {
                process.destroy();
                process = null;

                Thread.currentThread().sleep(500);
                
                //Get the images directory
                File imagesDir = new File (imagesFile).getParentFile();
                
                //Force it to refresh - otherwise there may not yet be a
                //FileObject corresponding to the brand new file
                FileObject par = FileUtil.toFileObject(imagesDir);
                if (par == null) {
                    //parse error or something - nothing was rendered
                    return;
                }
                par.refresh();
                
                FileObject fob = FileUtil.toFileObject (new File(imagesFile));
                if (fob == null) {
                    return; //Error - nothing was rendered
                }
                fob.getParent().refresh();

                //Find the DataObject for the file (actually supplied by the
                // Image module)
                DataObject ob = DataObject.find (fob);
                
                //Get its open cookie, if any
                OpenCookie ck = (OpenCookie) ob.getCookie(OpenCookie.class);
                if (ck != null) {
                    //Open the image
                    ck.open();
                }
            } catch (Exception donfe) {
                ErrorManager.getDefault().notify (donfe);
            }
        }
        
        public void halt() {
            try {
                synchronized (this) {
                    if (process != null) {
                        process.destroy();
                    }
                }
            } finally {
                if (a != null) a.interrupt();
                if (b != null) b.interrupt();
                io.getOut().close();
                io = null;
                runningRenders.remove (this);
            }
        }
        
        public int hashCode() {
            return sceneFile.hashCode() ^ 31;
        }
        
        public boolean equals (Object o) {
            if (o instanceof PovRunner) {
                return ((PovRunner) o).sceneFile.equals(sceneFile);
            } else {
                return false;
            }
        }
    }
    
    /**
     * Handler which routes output from the povray process to the output
     * window.  The way this works right now is darned ugly - launching
     * povray means starting 3 threads - process, stdout + stderr.  Don't
     * consider this an example of how to do it right.
     */
    static class OutHandler implements Runnable {
        private Reader out;
        private OutputWriter writer;

        public OutHandler (Reader out, OutputWriter writer) {
            this.out = out;
            this.writer = writer;
        }

        public void run() {
            try {
                while (true) {
                    while (!out.ready()) {
                        try {
                            Thread.currentThread().sleep(200);
                        } catch (InterruptedException e) {
                            close();
                            return;
                        }
                    }
                    if (!readOneBuffer() || Thread.currentThread().isInterrupted()) {
                        close();
                        return;
                    }
                }
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }
        
        private boolean readOneBuffer() throws IOException {
            char[] cbuf = new char[255];
            int read;
            while ((read = out.read(cbuf)) != -1) {
                writer.write(cbuf, 0, read);
            }
            return read != -1;
        }
        
        private void close() {
            try {
                out.close();
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            } finally {
                writer.close();
            }
        }
    }
    
    /**
     * Does platform-specific shell quoting of file names passed on the
     * command line to povray.  Note the cygwin command line povray for
     * windows does not like spaces in path names regardless of quoting
     * (tried it with unix style quoting as well).
     */
    private String quote (String fname) {
        if (Utilities.isWindows()) {
            if (fname.indexOf(' ') != -1) {
                return "\"" + fname + "\"";
            } else {
                return fname;
            }
        } else {
            if (fname.indexOf(' ') != -1) {
                //XXX check this on unix
                StringBuffer sb = new StringBuffer (fname.length() + 5);
                char[] c = fname.toCharArray();
                for (int i=0; i < c.length; i++) {
                    if (Character.isWhitespace(c[i])) {
                        sb.append ('\\');
                        sb.append (c[i]);
                    } else {
                        sb.append (c[i]);
                    }
                }
                return sb.toString();
            } else {
                return fname;
            }
        }
    }
    
    private static final String SETTINGS_DIR = "Povray/RendererSettings";
    
    /**
     * Returns <i>display names</i> of available renderer settings.
     */
    public String[] getAvailableRendererSettings() {
        //Why are we using DataSystems here?  Because the DataObject's getName()
        //method will actually return the localized name from 
        //org.netbeans.modules.povproject.resources.Bundle.properties rather
        //than the file name.
        
        //Get the settings folder defined in our layer file
        FileObject file = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(SETTINGS_DIR);
        
        //Get the DataFolder corresponding to it
        DataFolder fld = null;
        try {
            fld = (DataFolder) DataObject.find (file).getCookie(DataFolder.class);
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(donfe);
            return new String[] {PRODUCTION_SETTINGS_NAME};
        }
        
        //Iterate all its child files and build a list of file names
        DataObject[] propsFiles = fld.getChildren();
        String[] result = new String[propsFiles.length + 1];
        for (int i=0; i < propsFiles.length; i++) {
            result[i] = propsFiles[i].getName();
        }
        result[0] = PRODUCTION_SETTINGS_NAME;
        return result;
    }
    
    public String getPreferredConfigurationName() {
        return preferredSettings == null ? getAvailableRendererSettings()[3] : preferredSettings; //XXX
    }    
    
    private static String PRODUCTION_SETTINGS_NAME =
        NbBundle.getMessage (RendererServiceImpl.class, "NAME_Production").intern();
    
    private String preferredSettings = null;
    
    /**
     * Get a specific Properties instance that corresponds with a display name.
     * @param name - localized name, as returned by getAvailableRendererSettings().
     *  Implementation detail:  If null, will return the last known preferred
     *  settings, or if no preferred settings known, some set of known settings
     */
    public Properties getRendererSettings (String name) {
        boolean isProductionSettings = name != null && PRODUCTION_SETTINGS_NAME.equals(name);
        if (name != null) {
            preferredSettings = name;
        }
        
        //Get the settings folder from the system filesystem as a DataFolder
        //so we can match against localized names for the files
        FileObject file = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(SETTINGS_DIR);
        DataFolder fld = null;
        try {
            fld = (DataFolder) DataObject.find (file).getCookie(DataFolder.class);
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(donfe);
            throw new NullPointerException ("Non-existent renderer settings" +
                    " requested: " + name);
        }
        
        if (isProductionSettings) {
            //If the user selected "production" settings, it will be the 
            //merge of 1024x768 with any settings overridden in project.properties
            FileObject base = file.getFileObject("1024x768hq.properties"); //NOI18N
            try {
                name = DataObject.find (base).getName();
            } catch (DataObjectNotFoundException donfe) {
                ErrorManager.getDefault().notify(donfe);
            }
        }
        
        
        //Get a list of all the files in this directory
        DataObject[] propsFiles = fld.getChildren();
        DataObject dob = null;
        for (int i=0; i < propsFiles.length; i++) {
            if (name == null || propsFiles[i].getName().equals(name)) {
                dob = propsFiles[i];
                break;
            }
        }
        
        if (dob == null) {
            //Could conceivably be null after changing locale.  Pick 
            //something rather than throwing an NPE
            dob = propsFiles[0];
        }
        
        if (dob != null) {
            InputStream is = null;
        
            try {
                is = dob.getPrimaryFile().getInputStream();
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
                throw new NullPointerException ("Non-existent renderer" +
                        " properties requested: " + name);
            }
            Properties result = new Properties();
            try {
                result.load(is);
            } catch (IOException ioe) {
                
                //Annotate the exception with a localized message for the user
                ErrorManager.getDefault().annotate(ioe, NbBundle.getMessage(
                        RendererServiceImpl.class, "MSG_BadPropsFile", 
                        dob.getPrimaryFile().getPath()));
                
                //Notify the user
                ErrorManager.getDefault().notify (ErrorManager.USER, ioe);
                return null;
            }
            
            if (isProductionSettings) {
                //Merge in any properties defined on the project that
                //override the default 1024x768hq production values
                Properties projectProperties = (Properties) project.getLookup().lookup(Properties.class);
                for (Iterator i=projectProperties.keySet().iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    if (key.startsWith(PROJECT_RENDERER_KEY_PREFIX)) {
                        //Remove the leading "renderer."
                        String resultKey = key.substring(PROJECT_RENDERER_KEY_PREFIX.length());
                        
                        result.setProperty (resultKey, 
                            projectProperties.getProperty(key));
                    }
                }
            }
            
            return result;
        }
        return null;
    }

    /**
     * Get the location of the povray executable.  This is stored in 
     * Preferences across sessions.  If it is either unknown or the file 
     * no longer exists, the user will be prompted.
     *
     * @return The executable file, or null if the user cancels
     */
    private static File getPovray() {
        if (povray == null || !povray.exists()) {
            Preferences prefs = Preferences.userNodeForPackage(RendererServiceImpl.class);
            String loc = prefs.get(KEY_POVRAY_EXEC, null);

            if (loc != null) {
                povray = new File (loc);
            }
            
            if (povray == null || !povray.exists()) {
                File maybePov = locate("TTL_FindPovray");
                
                if (maybePov.getPath().endsWith("pvengine.exe")) {
                    //Warn the user to get a command line build
                    NotifyDescriptor msg = new NotifyDescriptor.Confirmation (
                        NbBundle.getMessage(RendererServiceImpl.class, "MSG_WindowsWarning"), 
                        NotifyDescriptor.WARNING_MESSAGE);

                    Object result = DialogDisplayer.getDefault().notify(msg);
                    if (result == NotifyDescriptor.CANCEL_OPTION) {
                        return null;
                    }
                }
                
                povray = maybePov;

                
                if (povray != null) {
                    prefs.put (KEY_POVRAY_EXEC, povray.getPath());
                }
            }
        }
        return povray;
    }
    
    /**
     * Get the standard povray include directory, asking the user where it 
     * is if need be.
     *
     * @return The includes dir, or null if the user cancels.  Usually is
     *  $POVRAY_HOME/include, so we check there first.
     */
    private static File getStandardIncludeDir (File povray) {
        if (include != null) {
            return include;
        }
        Preferences prefs = Preferences.userNodeForPackage(RendererServiceImpl.class);
        String loc = prefs.get(KEY_POVRAY_INCLUDES, null);
        if (loc != null) {
            include = new File (loc);
            if (!include.exists()) {
                include = null;
            }
        }
        if (include == null) {
            include = new File (povray.getParentFile().getParent() + File.separator + "include");
            if (!include.exists()) {
                include = locate ("TTL_FindIncludes");
                if (include != null) {
                    prefs.put(KEY_POVRAY_INCLUDES, include.getPath());
                } else {
                    include = null;
                }
            }
        }
        return include;
    }
    
    /**
     * Displays a JFileChooser with a custom localized string - used to ask
     * the user where is their povray exe and where is the standard include
     * dir (if not found in the povray install).
     */
    private static File locate(String key) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle(NbBundle.getMessage(RendererServiceImpl.class, key));
        jfc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        jfc.showOpenDialog(WindowManager.getDefault().getMainWindow());
        File result = jfc.getSelectedFile();
        return result;
    }
}
