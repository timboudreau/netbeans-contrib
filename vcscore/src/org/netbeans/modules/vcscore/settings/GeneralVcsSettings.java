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

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;
import java.io.File;

/**
 * The settings for all VCS filesystems.
 * @author  Milos Kleint, Martin Entlicher
 */
public class GeneralVcsSettings extends SystemOption {
    public static final String PROP_USE_GLOBAL         = "useGlobal"; // NOI18N
    public static final String PROP_OFFLINE            = "offLine"; // NOI18N
    public static final String PROP_AUTO_REFRESH       = "autoRefresh"; // NOI18N
    public static final String PROP_HOME               = "home"; // NOI18N
    public static final String PROP_HIDE_SHADOW_FILES  = "hideShadowFiles"; // NOI18N
    public static final String PROP_LAST_DIRECTORIES   = "wizardDirectoryCache"; //NOI18N
    public static final String PROP_CVS_COMMAND_PATH   = "wizardCvsCommandPath"; //NOI18N
    public static final String PROP_SH_COMMAND_PATH    = "wizardShellCommandPath"; //NOI18N
    public static final String PROP_AUTO_DETECT        = "autoDetect";
    
    public static final int AUTO_REFRESH_NO_REFRESH = 0;
    public static final int AUTO_REFRESH_ON_DIR_OPEN = 1;
    public static final int AUTO_REFRESH_ON_MOUNT = 2;
    public static final int AUTO_REFRESH_ON_RESTART = 3;
    public static final int AUTO_REFRESH_ON_MOUNT_AND_RESTART = 4;
    
    private static final String DEFAULT_CVS_EXEC = "cvs";   // NO I18N
    private static final String DEFAULT_SHELL_EXEC = "sh";  // NO I18N

    private static String wizardCvsCommandPath;
    
    private static String wizardShellCommandPath;
    
    private static java.util.LinkedList wizardDirectoryCache;
    
    static final long serialVersionUID = -3279219340064367270L;
    
    /** Initialize shared state.
     * Should use {@link #putProperty} to set up variables.
     * Subclasses should always call the super method.
     * <p>This method need <em>not</em> be called explicitly; it will be called once
     * the first time a given shared class is used (not for each instance!).
     */
    protected void initialize() {
        super.initialize();
        setAutoDetect(true);
        setAutoRefresh(AUTO_REFRESH_ON_DIR_OPEN);
        setHideShadowFiles(false);
        setOffLine(false);
        setUseGlobal(true);
    }    
    
    /** Get human presentable name */
    public String displayName() {
        return NbBundle.getBundle(VcsSettings.class).getString("CTL_VcsSettings");
    }
    
    /** Whether these settings overide the filesystem settings.
     */
    public boolean isUseGlobal() {
        return ((Boolean)getProperty(PROP_USE_GLOBAL)).booleanValue();;
    }

    /** Set whether these settings overide the filesystem settings.
     */
    public void setUseGlobal(boolean global) {
        putProperty(PROP_USE_GLOBAL, new Boolean(global), true);
   }
    
    /** Getter for property offLine.
     * @return Value of property offLine.
     */
    public boolean isOffLine() {
        return ((Boolean)getProperty(PROP_OFFLINE)).booleanValue();;
    }
    
    /** Setter for property offLine.
     * @param offLine New value of property offLine.
     */
    public void setOffLine(boolean newOffLine) {
        putProperty(PROP_OFFLINE, new Boolean(newOffLine), true);
    }
    
    /** Getter for property autoRefresh.
     * @return Value of property autoRefresh.
     */
    public int getAutoRefresh() {
        return ((Integer)getProperty(PROP_AUTO_REFRESH)).intValue();
    }
    
    /** Setter for property autoRefresh.
     * @param autoRefresh New value of property autoRefresh.
     */
    public void setAutoRefresh(int newAutoRefresh) {
        putProperty(PROP_AUTO_REFRESH, new Integer(newAutoRefresh), true);
    }

    public boolean isAutoDetect() {
        return ((Boolean)getProperty(PROP_AUTO_DETECT)).booleanValue();
    }
    
    public void setAutoDetect(boolean newAutoDetect) {
        putProperty(PROP_AUTO_DETECT, new Boolean(newAutoDetect), true);
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
        if (home == null) {
            home = System.getProperty("user.home");
            File fhome = new File(home);
            setHome(fhome);
            return fhome;
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
                String homeDir = (index + 1 < homepath.length()) ? homepath.substring(index + 1) : "\\";
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
        putProperty(PROP_HIDE_SHADOW_FILES, new Boolean(hide), true);
    }

    /**
     * If true, hides all shadow files in the filesystems. 
     * "Shadow" means files that don't exist in working directory.
     * Usually these are Locally-Removed, Needs-Checkout files
     */
    public boolean isHideShadowFiles() {
        return ((Boolean)getProperty(PROP_HIDE_SHADOW_FILES)).booleanValue();
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
