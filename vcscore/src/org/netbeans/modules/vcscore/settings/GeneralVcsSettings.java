/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.settings;

import org.openide.options.ContextSystemOption;
import org.openide.util.NbBundle;
import java.io.File;

/**
 * The settings for all VCS filesystems.
 * @author  Milos Kleint, Martin Entlicher
 */
public class GeneralVcsSettings extends ContextSystemOption {
    public static final String PROP_USE_GLOBAL         = "useGlobal"; // NOI18N
    public static final String PROP_OFFLINE            = "offLine"; // NOI18N
    public static final String PROP_AUTO_REFRESH       = "autoRefresh"; // NOI18N
    public static final String PROP_HOME               = "home"; // NOI18N
    public static final String PROP_HIDE_SHADOW_FILES  = "hideShadowFiles"; // NOI18N
    public static final String PROP_LAST_DIRECTORIES   = "wizardDirectoryCache"; //NOI18N
    public static final String PROP_CVS_COMMAND_PATH   = "wizardCvsCommandPath"; //NOI18N
    public static final String PROP_SH_COMMAND_PATH    = "wizardShellCommandPath"; //NOI18N
    
    public static final int AUTO_REFRESH_NO_REFRESH = 0;
    public static final int AUTO_REFRESH_ON_DIR_OPEN = 1;
    public static final int AUTO_REFRESH_ON_MOUNT = 2;
    public static final int AUTO_REFRESH_ON_RESTART = 3;
    public static final int AUTO_REFRESH_ON_MOUNT_AND_RESTART = 4;
    
    private static final String DEFAULT_CVS_EXEC = "cvs";   // NO I18N
    private static final String DEFAULT_SHELL_EXEC = "sh";  // NO I18N

    private static boolean useGlobal = true;

    private static boolean offLine = false;

    private static int autoRefresh = AUTO_REFRESH_ON_DIR_OPEN;

    private static boolean hideShadowFiles = false;
    
    private static String wizardCvsCommandPath;
    
    private static String wizardShellCommandPath;
    
    private static java.util.LinkedList wizardDirectoryCache;

    
    static final long serialVersionUID = -3279219340064367270L;
    
    /** Creates new VcsSettings */
    public GeneralVcsSettings() {
    }
    
    /** Get human presentable name */
    public String displayName() {
        return NbBundle.getBundle(VcsSettings.class).getString("CTL_VcsSettings");
    }
    
    /** Whether these settings overide the filesystem settings.
     */
    public boolean isUseGlobal() {
        return useGlobal;
    }

    /** Set whether these settings overide the filesystem settings.
     */
    public void setUseGlobal(boolean global) {
        if (useGlobal != global) {
           useGlobal = global;
           firePropertyChange(PROP_USE_GLOBAL, new Boolean(!global), new Boolean(global));
        }
   }
    
    /** Getter for property offLine.
     * @return Value of property offLine.
     */
    public boolean isOffLine() {
        return offLine;
    }
    
    /** Setter for property offLine.
     * @param offLine New value of property offLine.
     */
    public void setOffLine(boolean newOffLine) {
        if (offLine != newOffLine) {
            boolean oldOffLine = offLine;
            offLine = newOffLine;
            firePropertyChange (PROP_OFFLINE, new Boolean (oldOffLine), new Boolean (offLine));
        }
    }
    
    /** Getter for property autoRefresh.
     * @return Value of property autoRefresh.
     */
    public int getAutoRefresh() {
        return autoRefresh;
    }
    
    /** Setter for property autoRefresh.
     * @param autoRefresh New value of property autoRefresh.
     */
    public void setAutoRefresh(int newAutoRefresh) {
        if (autoRefresh != newAutoRefresh) {
            int oldAutoRefresh = autoRefresh;
            autoRefresh = newAutoRefresh;
            firePropertyChange (PROP_AUTO_REFRESH, new Integer (oldAutoRefresh), new Integer (newAutoRefresh));
        }
    }
    
    public File getHome() {
        String home = System.getProperty("Env-HOME");
        if (home == null && org.openide.util.Utilities.isWindows()) {
            String homeDrive = System.getProperty("Env-HOMEDRIVE");
            String homeDir = System.getProperty("Env-HOMEPATH");
            if (homeDrive != null && homeDir != null) {
                home = homeDrive + homeDir;
            }
        }
        return new File(home);
    }
    
    public void setHome(File home) {
        if (home == null) return ;
        String homepath = home.getAbsolutePath();
        System.setProperty("Env-HOME", homepath);
        System.setProperty("env-home", homepath.toLowerCase());
        if (org.openide.util.Utilities.isWindows()) {
            int index = homepath.indexOf(':');
            if (index > 0) {
                String homeDrive = homepath.substring(0, index + 1);
                String homeDir = homepath.substring(index + 1);
                System.setProperty("Env-HOMEDRIVE", homeDrive);
                System.setProperty("env-homedrive", homeDrive.toLowerCase());
                System.setProperty("Env-HOMEPATH", homeDir);
                System.setProperty("env-homepath", homeDir.toLowerCase());
            }
        }
        firePropertyChange(PROP_HOME, null, home);
    }
    
    /**
     * If true, hides all shadow files in the filesystems. 
     * "Shadow" means files that don't exist in working directory.
     * Usually these are Locally-Removed, Needs-Checkout files
     */
    public void setHideShadowFiles(boolean hide) {
        if (hideShadowFiles != hide) {
            boolean oldHide = hideShadowFiles;
            hideShadowFiles = hide;
            firePropertyChange (PROP_HIDE_SHADOW_FILES, new Boolean (oldHide), new Boolean (hideShadowFiles));
        }
    }

    /**
     * If true, hides all shadow files in the filesystems. 
     * "Shadow" means files that don't exist in working directory.
     * Usually these are Locally-Removed, Needs-Checkout files
     */
    public boolean isHideShadowFiles() {
        return hideShadowFiles;
    }
    

    public java.util.LinkedList getWizardDirectoryCache () {
        if (wizardDirectoryCache == null)
            wizardDirectoryCache = new java.util.LinkedList ();
        return wizardDirectoryCache;
    }
    
    public void setWizardDirectoryCache (java.util.LinkedList cache) {
            wizardDirectoryCache = cache;
    }
    
    public String getWizardCvsCommandPath () {
        if (wizardCvsCommandPath == null || wizardCvsCommandPath.length() == 0)
            return DEFAULT_CVS_EXEC;
        else
            return wizardCvsCommandPath;
    }
    
    public void setWizardCvsCommandPath (String cvsCommandPath) {
             wizardCvsCommandPath = cvsCommandPath;
    }
    
    public String getWizardShellCommandPath() {
        if (wizardShellCommandPath == null || wizardShellCommandPath.length() ==0)
            return DEFAULT_SHELL_EXEC;
        else
            return wizardShellCommandPath;
    }
    
    public void setWizardShellCommandPath(String shellCommandPath) {
            wizardShellCommandPath = shellCommandPath;
    }

}
