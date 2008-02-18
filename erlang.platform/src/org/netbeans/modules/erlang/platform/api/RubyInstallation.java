/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.erlang.platform.api;

import java.awt.Dialog;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.netbeans.api.gsfpath.classpath.ClassPath;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 * Information about a Ruby installation.
 *
 * @author Tor Norbye
 */
public class RubyInstallation {
    
    
    /** NOTE: Keep this in sync with ruby/jruby/nbproject/project.properties */
    private static final String JRUBY_RELEASE = "5.5.2"; // NOI18N
    
    /** NOTE: Keep this in sync with ruby/jruby/nbproject/project.properties */
    public static final String DEFAULT_RUBY_RELEASE = "1.8"; // NOI18N
    private static final String JRUBY_RELEASEDIR = "C:" + File.separator + "erl" + JRUBY_RELEASE; // NOI18N

    /**
     * MIME type for Ruby. Don't change this without also consulting the various XML files
     * that cannot reference this value directly, as well as RUBY_MIME_TYPE in the editing plugin
     */
    public static final String RUBY_MIME_TYPE = "text/x-erlang"; // NOI18N
    public static final String RHTML_MIME_TYPE = "application/x-httpd-eruby"; // NOI18N
    private static final String KEY_ERL = "erlang.interpreter"; //NOI18N
    private static final RubyInstallation INSTANCE = new RubyInstallation();
    
    // TODO Allow callers to decide if they want rails+dependencies included or not
    static ClassPath cp;
    
    /** Regexp for matching version number in gem packages:  name-x.y.z
     * (we need to pull out x,y,z such that we can do numeric comparisons on them)
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)"); // NOI18N
    
    private FileObject rubylibFo;
    private FileObject rubyStubsFo;
    private String ruby;
    private String rubybin;
    private String rubylib;
    private String gem;
    private String rake;
    private String rails;
    private String rdoc;
    private String jrubyHome;
    private String rubyHomeUrl;
    private PropertyChangeSupport pcs;
    /** Map from gem name to maps from version to File */
    Map<String, Map<String, File>> gemFiles;
    
    private final List<InterpreterLiveChangeListener> interpreterLCLs = new CopyOnWriteArrayList<InterpreterLiveChangeListener>();

    private RubyInstallation() {
    }
    
    /** Protected: for test access only */
    RubyInstallation(String initialRuby) {
        this.ruby = initialRuby;
    }

    public static RubyInstallation getInstance() {
        return INSTANCE;
    }
            
    public String getRuby() {
        if (ruby == null) {
            // Test and preindexing hook
            ruby = System.getProperty(KEY_ERL);
            
            if (ruby == null) { // Usually the case
               ruby = getPreferences().get(KEY_ERL, null);
                
                if (ruby == null) {
                    ruby = chooseRuby();
                    if (ruby != null) {
                        getPreferences().put(KEY_ERL, ruby);
                    }
                }

                if (ruby == null || ruby.equals("jruby")) { // NOI18N
                    ruby = getJRuby();
                }
            }
            
            // Let RepositoryUpdater and friends know where they can root preindexing
            // This should be done in a cleaner way.
            /** @Caoyuan commented */
            //org.netbeans.modules.retouche.source.usages.Index.setPreindexRootUrl(getRubyHomeUrl());
        }
        
        return ruby;
    }

    public String getJRuby() {
        return getRuby();
//        String binDir = getJRubyBin();
//        if (binDir == null) {
//            return null;
//        }
//
//        String binary = Utilities.isWindows() ? "erl.exe" : "erl"; // NOI18N
//        String jruby = binDir + File.separator + binary;
//
//        // Normalize path
//        try {
//            jruby = new File(jruby).getCanonicalFile().getAbsolutePath();
//        } catch (IOException ioe) {
//            Exceptions.printStackTrace(ioe);
//        }
//        
//        return jruby;
    }
            
    private String chooseRuby() {
        // Check the path to see if we find any other Ruby installations
        String path = System.getenv("PATH"); // NOI18N
        if (path == null) {
            path = System.getenv("Path"); // NOI18N
        }
        
        if (path != null) {
            final Set<String> rubies = new TreeSet<String>();
            Set<String> dirs = new TreeSet<String>(Arrays.asList(path.split(File.pathSeparator)));
            for (String dir : dirs) {
                File f = null;
                if (Utilities.isWindows() && (f = new File(dir, "erl.exe")).exists()) { // NOI18N
                    rubies.add(f.getPath());
                } else {
                    f = new File(dir, "erl"); // NOI18N
                    if (f.exists()) {
                        // Don't include /usr/bin/ruby on the Mac - it's no good                        
                        if (Utilities.isMac() && "/opt/local/bin/erl".equals(f.getPath())) { // NOI18N
                            continue;
                        }
                        rubies.add(f.getPath());
                    }
                }
            }
            
            if (rubies.size() > 0) {
                if (SwingUtilities.isEventDispatchThread()) {
                    return askForRuby(rubies);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            String chosen = askForRuby(rubies);
                            if (chosen != null && !chosen.equals("jruby") && !chosen.equals(ruby)) {
                                setRuby(chosen);
                            }
                        }
                    });
                }
                
            } else {
                // No rubies found - just default to using the bundled JRuby
                return "jruby";
            }
        }
            
        return "jruby";
    }
    
    private String askForRuby(final Set<String> rubies) {
                // Ruby found in the path -- offer to use it
                String jrubyLabel = NbBundle.getMessage(RubyInstallation.class, "JRuby");
                String nativeRubyLabel = NbBundle.getMessage(RubyInstallation.class, "NativeRuby") + " "; 

                List<String> displayList = new ArrayList<String>();
                displayList.add(jrubyLabel);
                for (String r : rubies) {
                    displayList.add(nativeRubyLabel + r);
                }
                
                ChooseRubyPanel panel = new ChooseRubyPanel(displayList);

                javax.swing.JButton closeButton =
                    new javax.swing.JButton(NbBundle.getMessage(RubyInstallation.class, "CTL_Close"));
                closeButton.getAccessibleContext()
                           .setAccessibleDescription(NbBundle.getMessage(RubyInstallation.class,
                        "AD_Close"));

                Object[] options = new Object[] { closeButton };
                DialogDescriptor descriptor =
                    new DialogDescriptor(panel,
                        NbBundle.getMessage(RubyInstallation.class, "ChooseRuby"), true, options,
                        closeButton,
                        DialogDescriptor.DEFAULT_ALIGN, new HelpCtx(RubyInstallation.class), null);
                descriptor.setMessageType(NotifyDescriptor.Message.INFORMATION_MESSAGE);

                Dialog dlg = null;
                descriptor.setModal(true);

                try {
                    dlg = DialogDisplayer.getDefault().createDialog(descriptor);
                    dlg.setVisible(true);
                } finally {
                    if (dlg != null) {
                        dlg.dispose();
                    }
                }

                String displayItem = panel.getChosenInterpreter();
                if (displayItem == null) {
                    // Force user to choose
                    displayAdvancedOptions();
                } else {
                    if (displayItem.equals(jrubyLabel)) {
                        return "jruby";
                    } else {
                        assert displayItem.startsWith(nativeRubyLabel);
                String path = displayItem.substring(nativeRubyLabel.length());
                
                try {
                    path = new File(path).getCanonicalPath();
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }

                return path;
            }
        }
            
        return "jruby";
    }
                
    public File getRubyHome() {
        try {
            File r = new File(getRuby());

            // Handle bogus paths like "/" which cannot possibly point to a valid ruby installation
            File p = r.getParentFile();
            if (p == null) {
                return null;
            }

            p = p.getParentFile();
            if (p == null) {
                return null;
            }
            
            return p.getCanonicalFile();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
            
            return null;
        }
    }
        
    private boolean isValidRuby(boolean warn) {
        File file = new File(getRuby());
        boolean valid = file.exists() && getRubyHome() != null;
        
        if (warn && !valid) {
            String msg =
                    NbBundle.getMessage(RubyInstallation.class, "NotInstalled", file.getPath());
            javax.swing.JButton closeButton =
                    new javax.swing.JButton(NbBundle.getMessage(RubyInstallation.class, "CTL_Close"));
            closeButton.getAccessibleContext()
                    .setAccessibleDescription(NbBundle.getMessage(RubyInstallation.class,
                    "AD_Close"));
            
            final JButton optionsButton =
                    new JButton(NbBundle.getMessage(RubyInstallation.class, "EditOptions"));
            Object[] options = new Object[] { optionsButton, closeButton };
            DialogDescriptor descriptor =
                    new DialogDescriptor(msg,
                    NbBundle.getMessage(RubyInstallation.class, "MissingRuby"), true, options,
                    optionsButton, // XXX TODO i18n
                    DialogDescriptor.DEFAULT_ALIGN, new HelpCtx(RubyInstallation.class), null);
            descriptor.setMessageType(NotifyDescriptor.Message.ERROR_MESSAGE);
            
            Dialog dlg = null;
            descriptor.setModal(true);
            
            try {
                dlg = DialogDisplayer.getDefault().createDialog(descriptor);
                dlg.setVisible(true);
            } finally {
                if (dlg != null) {
                    dlg.dispose();
                }
            }
            
            if (descriptor.getValue() == optionsButton) {
                displayAdvancedOptions();
            }
        }

        return valid;
    }
    
    private void displayAdvancedOptions() {
                OptionsDisplayer.getDefault().open("Advanced"); // NOI18N
            }
        
    public String getJRubyHome() {
        if (jrubyHome == null) {
            /** @Caoyuan commented
             * File jrubyDir =
             * InstalledFileLocator.getDefault()
             * .locate(JRUBY_RELEASEDIR, "org.netbeans.modules.ruby.project",
             * false); // NOI18N
             *
             * if ((jrubyDir == null) || !jrubyDir.isDirectory()) {
             * throw new RuntimeException("Can't locate " + JRUBY_RELEASEDIR + // NOI18N
             * " directory. Installation might be damaged"); // NOI18N
             * }
             * jrubyHome = jrubyDir.getPath();
             */
            jrubyHome = JRUBY_RELEASEDIR;
        }
        
        return jrubyHome;
    }
    
    private String getJRubyBin() {
        return getJRubyHome() + File.separator + "bin";
    }
                                
    private static Preferences getPreferences() {
        return NbPreferences.forModule(RubyInstallation.class);
    }
    
    public void setRuby(String ruby) {
        // Normalize path
        try {
            ruby = new File(ruby).getCanonicalFile().getAbsolutePath();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        
        if (!ruby.equals(getRuby())) {
            getPreferences().put(KEY_ERL, ruby);
            this.ruby = ruby;
            // Recompute lazily:
            this.gem = null;
            this.rubybin = null;
            this.rubylib = null;
            this.rubylibFo = null;
            this.rubyStubsFo = null;
            this.rake = null;
            this.rdoc = null;
            this.rails = null;
            this.rubyHomeUrl = null;
            //this.irb = null;
            if (isValidRuby(false)) {
                recomputeRoots();
            }
        }
    }
    
    /**
     * AutoUpdate may not set execute permissions on the bundled JRuby files,
     * so try to fix that here
     * @todo Do this lazily before trying to actually execute any of these bits?
     */
    @Deprecated private void ensureExecutable() {
        // No excute permissions on Windows. On Unix and Mac, try.
        if (Utilities.isWindows()) {
            return;
        }
        
        File binDir = new File(getJRubyBin());
        
        if (!binDir.exists()) {
            // No JRuby bundled installation?
            return;
        }
        
        // Ensure that the binaries are installed as expected
        // The following logic is from CLIHandler in core/bootstrap:
        File chmod = new File("/bin/chmod"); // NOI18N
        
        if (!chmod.isFile()) {
            // Linux uses /bin, Solaris /usr/bin, others hopefully one of those
            chmod = new File("/usr/bin/chmod"); // NOI18N
        }
        
        if (chmod.isFile()) {
            try {
                List<String> argv = new ArrayList<String>();
                argv.add(chmod.getAbsolutePath());
                argv.add("u+rx"); // NOI18N
                
                String[] files = binDir.list();
                
                for (String file : files) {
                    argv.add(file);
                }
                
                ProcessBuilder pb = new ProcessBuilder(argv);
                pb.directory(binDir);
                
                Process process = pb.start();
                
                int chmoded = process.waitFor();
                
                if (chmoded != 0) {
                    throw new IOException("could not run " + argv + " : Exit value=" + chmoded); // NOI18N
                }
            } catch (Throwable e) {
                // 108252 - no loud complaints
                Logger.getLogger(RubyInstallation.class.getName()).log(Level.INFO, "Can't chmod+x JRuby bits", e);
            }
        }
    }
    
    /** @Caoyuan added */
    public void ensureInstallation() {
        File exeFile = new File(getRuby());
        if (exeFile.exists()) {
            /** erl installation path is set properly */
            return;
        }
        
        BufferedWriter stdWriter = null;
        BufferedReader stdReader = null;
        BufferedReader errReader = null;
        try {
            Process process = Runtime.getRuntime().exec("erl");
            
            stdWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            stdWriter.append("code:root_dir().");
            stdWriter.newLine();
            stdWriter.append("init:stop().");
            stdWriter.newLine();
            stdWriter.flush();
            
            try {
                int sucessed = process.waitFor();
                if (sucessed != 0) {
                    //ErrorManager.().notify(new Exception(
                    //        "Erlang installation may not be set, or is invalid.\n" +
                    //        "Please set Erlang installation via [Tools]->[Options]->[Miscellanous]"));
                } else {
                    String line = null;
                    while ((line = errReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    while ((line = stdReader.readLine()) != null) {
                        System.out.println(line);
                        String[] groups = line.split(">");
                        if (groups.length >= 2 && groups[0].trim().equals("1")) {
                            String basePath = groups[1].trim();
                            basePath = basePath.replace("\"", "");
                            String erlExeFileName = Utilities.isWindows() ? "erl.exe" : "erl";
                            setRuby(basePath + File.separator + "bin" + File.separator + erlExeFileName);
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                Exceptions.printStackTrace(e);
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } finally {
            try {
                if (stdWriter != null) stdWriter.close();
                if (stdReader != null) stdReader.close();
                if (errReader != null) errReader.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(listener);
        }
    }
    
    /** The gems installed have changed, or the installed ruby has changed etc. --
     * force a recomputation of the installed classpath roots */
    public void recomputeRoots() {
        this.gemFiles = null;
        this.cp = null;
        
        // Let RepositoryUpdater and friends know where they can root preindexing
        // This should be done in a cleaner way.
        /** @Caoyuan commented */
        //org.netbeans.modules.retouche.source.usages.Index.setPreindexRootUrl(getRubyHomeUrl());
        
        if (pcs != null) {
            pcs.firePropertyChange("roots", null, null);
        }
        
        //        // Force ClassIndex registration
        //        // Dummy class path provider just to trigger recomputation
        //        ClassPathProviderImpl cpProvider = new ClassPathProviderImpl(null, null, null, null);
        //        ClassPath[] cps = cpProvider.getProjectClassPaths(ClassPath.BOOT);
        //        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cps);
        //        GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cps);
        
        // Possibly clean up index from old ruby root as well?
    }
    
    /** Return > 0 if version1 is greater than version 2, 0 if equal and -1 otherwise */
    public static int compareGemVersions(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        
        Matcher matcher1 = VERSION_PATTERN.matcher(version1);
        
        if (matcher1.matches()) {
            int major1 = Integer.parseInt(matcher1.group(1));
            int minor1 = Integer.parseInt(matcher1.group(2));
            int micro1 = Integer.parseInt(matcher1.group(3));
            
            Matcher matcher2 = VERSION_PATTERN.matcher(version2);
            
            if (matcher2.matches()) {
                int major2 = Integer.parseInt(matcher2.group(1));
                int minor2 = Integer.parseInt(matcher2.group(2));
                int micro2 = Integer.parseInt(matcher2.group(3));
                
                if (major1 != major2) {
                    return major1 - major2;
                }
                
                if (minor1 != minor2) {
                    return minor1 - minor2;
                }
                
                if (micro1 != micro2) {
                    return micro1 - micro2;
                }
            } else {
                // TODO uh oh
                //assert false : "no version match on " + version2;
            }
        } else {
            // TODO assert false : "no version match on " + version1;
        }
        
        // Just do silly alphabetical comparison
        return version1.compareTo(version2);
    }
    

    public static interface InterpreterLiveChangeListener extends EventListener {
        void interpreterChanged(String interpreter);
    }

    public void fireInterpreterLiveChange(final String interpreter) {
        for (InterpreterLiveChangeListener listener : interpreterLCLs) {
            listener.interpreterChanged(interpreter);
        }
    }
    
    public void addInterpreterLiveChangeListener(final InterpreterLiveChangeListener ilcl) {
        interpreterLCLs.add(ilcl);
    }
    
    public void removeInterpreterLiveChangeListener(final InterpreterLiveChangeListener ilcl) {
        interpreterLCLs.remove(ilcl);
    }

}
