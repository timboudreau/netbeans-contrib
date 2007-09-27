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
 * PovProjectFactory.java
 *
 * Created on February 16, 2005, 5:38 PM
 */

package org.netbeans.modules.povproject;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Mutex;

/**
 * Factory registered into the default Lookup via META-INF/services file entry
 * Responsible for recognizing and loading povray projects.  A povray project
 * is identified as being a directory with a subdirectory "pvproject".
 *
 * @see org.openide.util.Lookup
 * @author Timothy Boudreau
 */
public class PovProjectFactory implements ProjectFactory {
    /** Name of the subdirectory that identifies a POV-Ray project */
    public static final String PROJECT_DIR = "pvproject";
    /** Name of the project properties file that we'll store settings
     * into, e.g. someproject/pvproject/project.properties */
    public static final String PROJECT_PROPFILE = "project.properties";
    /** Name of the scenes directory where user-created files will go */
    public static final String SCENES_DIR = "scenes";
    
    
    /** Path to the sample template in the system filesystem (see layer.xml) */
    private static final String SAMPLE_PROJECT_TEMPLATE = 
            "Povray/NetBeansLogo.pov";
    
    /** Path to the empty template in the system filesystem (see layer.xml) */
    private static final String EMPTY_POVRAY_TEMPLATE = "Povray/Empty.pov";

    /** Name of the sample project template (see layer.xml) - really 
     * we just use this as a marker
     * to decide which of the two possible templates to 
     * instantiate in any new project */
    private static final String SAMPLE_PROJECT_SFS_FILENAME = 
            "samplePovrayProject";    
    
    /** Creates a new instance of PovProjectFactory */
    public PovProjectFactory() {
    }

    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(PROJECT_DIR) != null;
    }
    
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject (dir) ? new PovProject (dir, state) : null;
    }

    public void saveProject(final Project project) throws IOException, ClassCastException {
        FileObject dir = project.getProjectDirectory();
        
        if (dir.getFileObject(PROJECT_DIR) == null) {
            //If a subproject or something, user *could* manage to delete it.
            //Throw an exception and bail out
            throw new IOException ("Project dir " + dir.getPath() + " deleted," +
                    " cannot save project");
        }
        
        //Get the scenes directory, recreate it if deleted
        FileObject scenesDir = ((PovProject) project).getScenesFolder(true);
        
        String propsPath = PROJECT_DIR + "/" + PROJECT_PROPFILE;
        
        //Make sure pvproject/project.properties exists
        FileObject propertiesFile = dir.getFileObject(propsPath);
        if (propertiesFile == null) {
            //Recreate the properties file if needed
            propertiesFile = dir.createData(propsPath);
        }
        Properties properties = 
                (Properties) project.getLookup().lookup (Properties.class);
        //Make sure the project.properties contains the version key/value pair
        if (!properties.containsKey(PovProject.KEY_VERSION)) {
            properties.setProperty(PovProject.KEY_VERSION, 
                Integer.toString(PovProject.VALUE_VERSION));
        }
        
        //Write out the main file, if set
        MainFileProvider provider = (MainFileProvider) 
                project.getLookup().lookup (MainFileProvider.class);
        
        FileObject mainFile = provider.getMainFile();
        if (mainFile != null) {
            String pdPath = dir.getPath();
            
            //Sanity check
            assert mainFile.getPath().startsWith(pdPath) : 
                mainFile.getPath() + " not a substring of " + pdPath;

            //Make it a relative path
            String relPath = mainFile.getPath().substring(pdPath.length() + 1);
            
            //Another sanity check
            assert dir.getFileObject (relPath).equals(mainFile) : 
                relPath + " not path to " + mainFile.getPath();

            //Put it properties object
            properties.setProperty(PovProject.KEY_MAINFILE, relPath);
        }

        FileLock lock = propertiesFile.lock();
        if (!lock.isValid()) {
            throw new IOException ("Invalid lock");
        }
        try {
            //Mysteriously, using FileObject.getOutputStream results in IOE
            //at org.netbeans.modules.masterfs.filebasedfs.fileobjects.MutualExclusion
            //Support.addResource(MutualExclusionSupport.java:67)
            //Hack with file for now
            File f = FileUtil.toFile(propertiesFile);
            properties.store(new FileOutputStream(f), "NetBeans Povray " +
                    "Project Properties");
        } finally {
            lock.releaseLock();
        }
    }

    
    /** Utility method for creating a new povray project from scratch.  This is
     * called by NewProjectIterator.instantiate().
     *
     * @param name The name of the new project
     * @param template The template the user chose in the wizard (empty or 
     *   sample project)
     * @param dest The parent folder for the new project
     * @param sceneFileName the name entered for the scene file to create
     * @return A DataFolder representing the newly created project directory
     */
    public static DataFolder createNewPovrayProject (final String name, 
            final DataObject template, final DataFolder dest, final 
            String sceneFileName) throws IOException {
        
        //Do this inside the write mutex - we don't want anything noticing
        //a new project while we're still writing its files to disk.
        
        //If we succeed, we will return the project folder;  if an exception
        //is encountered, we'll return that
        Object result = ProjectManager.mutex().writeAccess(new Mutex.Action () {
           public Object run() {
               
               //A list for files we'll create, so we can delete them in the
               //event of failure
               final List created = new ArrayList();
               
               try {
                   
                    //Create the project directory
                    DataFolder projectFolder = DataFolder.create (dest, name);
                    created.add (projectFolder);

                    //Create the subdirectory for scene files
                    DataFolder scenesDir = DataFolder.create (projectFolder, 
                            PovProjectFactory.SCENES_DIR);
                    
                    created.add (scenesDir);

                    //Figure out which template we're using, based on the file 
                    //name of the project template the user chose
                    String sceneTemplateName = 
                        template.getName().equals(SAMPLE_PROJECT_SFS_FILENAME) ?
                        SAMPLE_PROJECT_TEMPLATE :
                        EMPTY_POVRAY_TEMPLATE;

                    //Instantiate whichever template should be inside the new 
                    //project
                    DataObject sceneTemplate = null;
                    try {
                        sceneTemplate = DataObject.find(
                            Repository.getDefault().getDefaultFileSystem().
                                getRoot().getFileObject(sceneTemplateName));

                    } catch (DataObjectNotFoundException e) {
                        //Something is very broken if this happens
                        IOException ioe = new IOException ("Could not find " +
                                "template " + sceneTemplateName);
                        
                        //Annotate the original exception and rethrow
                        ErrorManager.getDefault().annotate (ioe, e);
                        throw ioe;
                    }
                    
                    //This will copy the basic scene file, renaming it
                    //to what we want
                    DataObject scene = sceneTemplate.createFromTemplate(
                            scenesDir, sceneFileName);
                    
                    //Add it to the list of files we created so we can
                    //delete it if something goes wrong
                    created.add (scene);

                    //Create the pvproject dir to hold project information
                    DataFolder projectData = DataFolder.create (projectFolder, 
                            PovProjectFactory.PROJECT_DIR);
                    created.add (projectData);

                    //Create the project.properties file inside root/pvproject/ 
                    FileObject fob = projectData.getPrimaryFile();
                    FileObject projectProps = 
                            fob.createData(PovProjectFactory.PROJECT_PROPFILE);
                    created.add (projectProps);

                    //Create and populate a Properties object, and write it to 
                    //the project properties file.
                    //We're just writing version info and the name of the 
                    //template file that will be the initial main file
                    Properties properties = new Properties();
                    
                    properties.setProperty(PovProject.KEY_MAINFILE, 
                            PovProjectFactory.SCENES_DIR + "/" + sceneFileName + 
                            ".pov");
                    properties.setProperty(PovProject.KEY_VERSION, 
                            Integer.toString (PovProject.VALUE_VERSION));
                    
                    //Write it to disk
                    FileLock lock = projectProps.lock();
                    try {
                        OutputStream stream = projectProps.getOutputStream(lock);
                        properties.store(stream, 
                                name + " povray project properties");
                        stream.flush();
                        stream.close();
                    } finally {
                        lock.releaseLock();
                    }
                    
                    //Return the newly created folder, to open it in the UI
                    return projectFolder;
               } catch (IOException ioe) {
                   
                   //Iterate any files created before the exception and 
                   //delete them
                   for (Iterator i=created.iterator(); i.hasNext();) {
                       try {
                           Object o = i.next();
                           FileObject fob;
                           if (o instanceof DataObject) {
                               fob = ((DataObject) o).getPrimaryFile();
                           } else {
                               fob = (FileObject) o;
                           }
                           fob.delete();
                       } catch (Exception e) {
                           ErrorManager.getDefault().notify(e);
                       }
                   }
                   
                   //Return the exception to rethrow it
                   return ioe;
               }
           } 
        });
        
        //Return the result or throw the exception
        if (result instanceof IOException) {
            throw ((IOException) result);
        } else {
            return (DataFolder) result;
        }
    }    
    
}
