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

package org.netbeans.modules.vcscore.settings;

import java.io.File;
import java.util.LinkedList;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.registry.RecognizedFS;

/**
 * The settings for all VCS filesystems.
 * @author  Milos Kleint, Martin Entlicher
 */
public class GeneralVcsSettings extends SystemOption {
    public static final String PROP_USE_GLOBAL            = "useGlobal"; // NOI18N
    public static final String PROP_OFFLINE               = "offLine"; // NOI18N
    public static final String PROP_AUTO_REFRESH          = "autoRefresh"; // NOI18N
    public static final String PROP_HOME                  = "home"; // NOI18N
    public static final String PROP_HIDE_SHADOW_FILES     = "hideShadowFiles"; // NOI18N
    public static final String PROP_DEFAULT_PROFILE       = "defaultProfile"; // NOI18N
    public static final String PROP_RECOGNIZED_FS         = "recognizedFS"; // NOI18N
    public static final String PROP_ADVANCED_NOTIFICATION = "advancedNotification"; //NOI18N
    public static final String PROP_FILE_ANNOTATION       = "fileAnnotation"; // NOI18N
    
    public static final int AUTO_REFRESH_NO_REFRESH = 0;
    public static final int AUTO_REFRESH_ON_DIR_OPEN = 1;
    public static final int AUTO_REFRESH_ON_MOUNT = 2;
    public static final int AUTO_REFRESH_ON_RESTART = 3;
    public static final int AUTO_REFRESH_ON_MOUNT_AND_RESTART = 4;

    /** Ignores annotation template */
    public static final int FILE_ANNOTATION_NONE = 0;
    /** Substitues {status} annotation template by full status */
    public static final int FILE_ANNOTATION_FULL = 1;
    /** Substitues {status} annotation template by shortedned status */
    public static final int FILE_ANNOTATION_SHORT = 2;
    /** For modified files substitues {status} annotation template by full status otherwise empty */
    public static final int FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY = 3;    
    /** Ignores {status} annotation template and colors file name instead. */
    public static final int FILE_ANNOTATION_COLORED = 4;


    static final long serialVersionUID = -3279219340064367270L;
    
    /** Initialize shared state.
     * Should use {@link #putProperty} to set up variables.
     * Subclasses should always call the super method.
     * <p>This method need <em>not</em> be called explicitly; it will be called once
     * the first time a given shared class is used (not for each instance!).
     */
    protected void initialize() {
        super.initialize();
        setAutoRefresh(AUTO_REFRESH_ON_DIR_OPEN);
        setHideShadowFiles(false);
        setOffLine(false);
        setUseGlobal(true);
        setAdvancedNotification(true);
        setFileAnnotation(FILE_ANNOTATION_FULL);
    }    
    
    /** Get human presentable name */
    public String displayName() {
        return NbBundle.getBundle(GeneralVcsSettings.class).getString("CTL_VcsSettings");
    }
    
    /** Whether these settings overide the filesystem settings.
     */
    public boolean isUseGlobal() {
        return ((Boolean)getProperty(PROP_USE_GLOBAL)).booleanValue();
    }

    /** Set whether these settings overide the filesystem settings.
     */
    public void setUseGlobal(boolean global) {
        putProperty(PROP_USE_GLOBAL, global ? Boolean.TRUE : Boolean.FALSE, true);
   }
    
    /** Getter for property offLine.
     * @return Value of property offLine.
     */
    public boolean isOffLine() {
        return ((Boolean)getProperty(PROP_OFFLINE)).booleanValue();
    }
    
    /** Setter for property offLine.
     * @param offLine New value of property offLine.
     */
    public void setOffLine(boolean newOffLine) {
        putProperty(PROP_OFFLINE, newOffLine ? Boolean.TRUE : Boolean.FALSE, true);
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
    
    public int getFileAnnotation() {
        return ((Integer)getProperty(PROP_FILE_ANNOTATION)).intValue();
    }
    
    public void setFileAnnotation(int fileAnnotation) {
        putProperty(PROP_FILE_ANNOTATION, new Integer(fileAnnotation), true);
    }
    
    /** Getter for advanced notification property.
     * @return Value of advanced notification property.
     */
    public boolean isAdvancedNotification() {
        return ((Boolean)getProperty(PROP_ADVANCED_NOTIFICATION)).booleanValue();
    }
    
    /** Setter for advanced notification property.
     * @param notifyAdv New value of advanced notification property.
     */
    public void setAdvancedNotification(boolean notifyAdv) {
        putProperty(PROP_ADVANCED_NOTIFICATION, notifyAdv ? Boolean.TRUE : Boolean.FALSE, true);
    }

    public File getHome() {
        String home = System.getenv("HOME");
        if (home == null && org.openide.util.Utilities.isWindows()) {
            String homeDrive = System.getenv("HOMEDRIVE");
            String homeDir = System.getenv("HOMEPATH");
            if (homeDrive != null && homeDir != null) {
                home = homeDrive + homeDir;
            }
        }
        if (home == null) {
            home = System.getProperty("user.home");
            File fhome = new File(home);
            return fhome;
        }
        return new File(home);
    }
    
    /**
     * If true, hides all shadow files in the filesystems. 
     * "Shadow" means files that don't exist in working directory.
     * Usually these are Locally-Removed, Needs-Checkout files
     */
    public void setHideShadowFiles(boolean hide) {
        putProperty(PROP_HIDE_SHADOW_FILES, hide ? Boolean.TRUE : Boolean.FALSE, true);
    }

    /**
     * If true, hides all shadow files in the filesystems. 
     * "Shadow" means files that don't exist in working directory.
     * Usually these are Locally-Removed, Needs-Checkout files
     */
    public boolean isHideShadowFiles() {
        return ((Boolean)getProperty(PROP_HIDE_SHADOW_FILES)).booleanValue();
    }
    
    /**
     * Set the default VCS profile that is pre-selected in mount wizard.
     */
    public void setDefaultProfile(String defaultProfile) {
        putProperty(PROP_DEFAULT_PROFILE, defaultProfile, true);
    }
    
    /**
     * Get the default VCS profile that is pre-selected in mount wizard.
     */
    public String getDefaultProfile() {
        return (String) getProperty(PROP_DEFAULT_PROFILE);
    }
    
    public RecognizedFS getRecognizedFS() {
        return (RecognizedFS) getProperty(PROP_RECOGNIZED_FS);
    }
    
    public void setRecognizedFS(RecognizedFS recognizedFS) {
        putProperty(PROP_RECOGNIZED_FS, recognizedFS, true);
    }
    
}
