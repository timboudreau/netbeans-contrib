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

package org.netbeans.modules.tasklist.usertasks.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.openide.awt.StatusDisplayer;
import org.openide.util.MapFormat;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * Settings for the user tasks module.
 *
 * @author Tor Norbye
 * @author tl
 */
public class Settings {
    public static final String PROP_APPEND = "append"; // NOI18N
    public static final String PROP_FILENAME = "filename"; // NOI18N
    public static final String PROP_COLLECT_WORK_PERIODS = 
            "collectWorkPeriods"; // NOI18N
    public static final String PROP_DETECT_INACTIVITY = 
            "detectInactivity"; // NOI18N
    public static final String PROP_AUTO_SWITCH_TO_COMPUTED = 
            "autoSwitchToComputed"; // NOI18N
    public static final String PROP_LAST_USED_EXPORT_FOLDER = 
            "lastUsedExportFolder"; // NOI18N
    public static final String PROP_WORKING_DAY_START = 
            "workingDayStart"; // NOI18N
    public static final String PROP_WORKING_DAY_END = 
            "workingDayEnd"; // NOI18N
    public static final String PROP_PAUSE_START = "pauseStart"; // NOI18N
    public static final String PROP_PAUSE_END = "pauseEnd"; // NOI18N
    
    /** 
     * Will be fired if the "working day" flag for one of the week days 
     * changes
     */
    public static final String PROP_WORKING_DAYS = "workingDays"; // NOI18N
    
    /** Automatic scheduling each time the task list changes. */
    public static final String PROP_AUTO_SCHEDULING = 
            "autoScheduling"; // NOI18N
        
    /** default working days. */
    private static boolean[] DEF_WORKING_DAYS = {
        true, true, true, true, true, false, false};

    
    private static final Settings INSTANCE = new Settings();    
    
    /** -1 means "not yet computed" */
    private int minutesPerDay = -1;

    /** -1 means "not yet computed" */
    private int workingDays = -1;
    
    /** 
     * Return the signleton settings.
     * 
     * @return default instance
     */
    public static Settings getDefault() {
	return INSTANCE;
    }

    /**
     * Returns preferences node.
     * 
     * @return preferences node
     */
    public static Preferences getPreferences() {
        return NbPreferences.forModule(Settings.class);
    }

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * Default constructor.
     */
    public Settings() {
    }
    
    /**
     * Returns the last folder used for export.
     *
     * @return folder
     */
    public File getLastUsedExportFolder() {
        String path = getPreferences().get(PROP_LAST_USED_EXPORT_FOLDER, 
                System.getProperty("user.home"));
        return new File(path);
    }
    
    /**
     * Sets the last folder used for export.
     *
     * @param f folder
     */
    public void setLastUsedExportFolder(File f) {
        getPreferences().put(PROP_LAST_USED_EXPORT_FOLDER, 
                f.getAbsolutePath());
        pcs.firePropertyChange(PROP_LAST_USED_EXPORT_FOLDER,
                null, null);
    }

    /**
     * Getter for the autoSwitchToComputed property.
     *
     * @return true if each time a subtask (b) is added to a task (a)
     * (a) will automatically compute spent time, progress and effort.
     */
    public boolean getAutoSwitchToComputed() {
        return getPreferences().getBoolean(PROP_AUTO_SWITCH_TO_COMPUTED, false);
    }

    /** 
     * Sets the autoSwitchToComputed property
     * 
     * @param b true if each time a subtask (b) is added to a task (a)
     * (a) will automatically compute spent time, progress and effort.
     */
    public void setAutoSwitchToComputed(boolean b) {
        getPreferences().putBoolean(PROP_AUTO_SWITCH_TO_COMPUTED, b);
        pcs.firePropertyChange(PROP_AUTO_SWITCH_TO_COMPUTED,
                null, null);
    }

    /**
     * Getter for the autoScheduling property.
     *
     * @return true if automatic scheduling is enabled
     */
    public boolean getAutoScheduling() {
        return getPreferences().getBoolean(PROP_AUTO_SCHEDULING, false);
    }

    /** 
     * Sets the autoScheduling property
     * 
     * @param b true if automatic scheduling is enabled
     */
    public void setAutoScheduling(boolean b) {
        getPreferences().putBoolean(PROP_AUTO_SCHEDULING, b);
        pcs.firePropertyChange(PROP_AUTO_SCHEDULING,
                null, null);
    }

    /**
     * Returns the collectWorkPeriods property.
     *
     * @return true if the work periods should be collected.
     */
    public boolean getCollectWorkPeriods() {
        return getPreferences().getBoolean(PROP_COLLECT_WORK_PERIODS, true);
    }

    /** 
     * Sets the makeBackups property
     *
     * @param b true if the work periods should be collected
     */
    public void setCollectWorkPeriods(boolean b) {
        getPreferences().putBoolean(PROP_COLLECT_WORK_PERIODS, b);
        pcs.firePropertyChange(PROP_COLLECT_WORK_PERIODS,
                null, null);
    }

    /**
     * Returns the detectInactivity property.
     *
     * @return true if the user inactivity should be detected
     */
    public boolean getDetectInactivity() {
        return getPreferences().getBoolean(PROP_DETECT_INACTIVITY, false);
    }

    /** 
     * Sets the detectInactivity property
     *
     * @param b true if the user inactivity should be detected.
     */
    public void setDetectInactivity(boolean b) {
        getPreferences().putBoolean(PROP_DETECT_INACTIVITY, b);
        pcs.firePropertyChange(PROP_DETECT_INACTIVITY,
                null, null);
    }

    /**
     * @return true iff the user wants to append items to the
     * tasklist instead of prepending.
     */
    public boolean getAppend() {
        return getPreferences().getBoolean(PROP_APPEND, false);
    }

    /** 
     * Indicate if the user wants to append items to the
     * tasklist instead of prepending.
     *
     * @param append True iff you want to append instead of prepend
     */
    public void setAppend(boolean append) {
        getPreferences().putBoolean(PROP_APPEND, append);
        pcs.firePropertyChange(PROP_APPEND,
                null, null);
    }

    /** 
     * Sets the name of the file to read/write the tasklist in
     */
    public void setFilename(String fname) {
        String t = getFilename();
        if (t.equals(fname))
            return;

	if (fname.trim().length() == 0) {
	    // Use default
	    fname = NbBundle.getMessage(Settings.class,
                    "DefaultFilename"); // NOI18N
	}

        // Check that the file is valid? Should at least make sure
        // the parent dir exists
	// Try compiling the regular expression to make sure it's valid
        File f = new File(expand(fname));
        File p = f.getParentFile();
        if (!p.exists()) {
            // Print message in the message window?
            StatusDisplayer.getDefault().setStatusText(
                    NbBundle.getMessage(Settings.class,
                    "NoFolder",  // NOI18N
                    p.getPath()));
            throw new IllegalArgumentException();
        }
        getPreferences().put(PROP_FILENAME, fname);
        pcs.firePropertyChange(PROP_FILENAME,
                null, null);
    }

    /** 
     * Gets the name of the file to read/write the tasklist in
     */
    public String getFilename() {
        return getPreferences().get(PROP_FILENAME,
                NbBundle.getMessage(Settings.class, 
                "DefaultFilename")); // NOI18N
    }

    /** 
     * Gets the name of the file to read/write the tasklist in,
     * expanded such that {userdir} etc. is expanded to the real
     * filename.
     * 
     * @return file name of the default user task list
     */
    public String getExpandedFilename() {
        String fname = getFilename();
        return expand(fname);
    }

    /**
     * Hours per day property
     *
     * @return value of the property
     */
    public int getMinutesPerDay() {
        if (minutesPerDay < 0) {
            minutesPerDay = (getPauseStart() - getWorkingDayStart()) + 
                    (getWorkingDayEnd() - getPauseEnd());
        }
        return minutesPerDay;
    }
    
    /**
     * Working days per week
     *
     * @return value of the property
     */
    public int getDaysPerWeek() {
        if (workingDays < 0) {
            boolean[] wd = getWorkingDays();
            workingDays = 0;
            for (int i = 0; i < wd.length; i++) {
                if (wd[i])
                    workingDays++;
            }
        }
        return workingDays;
    }
    
    /** 
     * Expand a given filename using our map format 
     */
    private static String expand(String fname) {
        Map<String, String> m = new HashMap<String, String>(2);
        m.put("userdir", System.getProperty("netbeans.user")); // NOI18N
        m.put("/", File.separator); // NOI18N
        MapFormat f = new MapFormat(m);
        String result = f.format(fname);
        return result;
    }
    
    /**
     * Adds a property change listener.
     * 
     * @param l new listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Returns offset in minutes for the start of working day.
     * 
     * @return offset in minutes since 00:00 
     */
    public int getWorkingDayStart() {
        return getPreferences().getInt("workingDayStart", 8 * 60);
    }

    /**
     * Returns offset in minutes for the start of pause.
     * 
     * @return offset in minutes since 00:00 
     */
    public int getPauseStart() {
        return getPreferences().getInt("pauseStart", 12 * 60);
    }

    /**
     * Returns offset in minutes for the end of pause.
     * 
     * @return offset in minutes since 00:00 
     */
    public int getPauseEnd() {
        return getPreferences().getInt("pauseEnd", 13 * 60);
    }


    /**
     * Returns offset in minutes for the end of working day.
     * 
     * @return offset in minutes since 00:00 
     */
    public int getWorkingDayEnd() {
        return getPreferences().getInt("workingDayEnd", 17 * 60);
    }
    
    /**
     * Returns offset in minutes for the start of working day.
     * 
     * @param minutes offset in minutes since 00:00 
     */
    public void setWorkingDayStart(int minutes) {
        getPreferences().putInt("workingDayStart", minutes);
        minutesPerDay = -1;
        pcs.firePropertyChange(PROP_WORKING_DAY_START, null, null);
    }

    /**
     * Returns offset in minutes for the start of pause.
     * 
     * @return offset in minutes since 00:00 
     */
    public void setPauseStart(int minutes) {
        getPreferences().putInt("pauseStart", minutes);
        minutesPerDay = -1;
        pcs.firePropertyChange(PROP_PAUSE_START, null, null);
    }

    /**
     * Returns offset in minutes for the end of pause.
     * 
     * @return offset in minutes since 00:00 
     */
    public void setPauseEnd(int minutes) {
        getPreferences().putInt("pauseEnd", minutes);
        minutesPerDay = -1;
        pcs.firePropertyChange(PROP_PAUSE_START, null, null);
    }

    /**
     * Returns offset in minutes for the end of working day.
     * 
     * @return offset in minutes since 00:00 
     */
    public void setWorkingDayEnd(int minutes) {
        getPreferences().putInt("workingDayEnd", minutes);
        minutesPerDay = -1;
        pcs.firePropertyChange(PROP_WORKING_DAY_START, null, null);
    }
    
    /**
     * Sets the flag for working days.
     * 
     * @param index day of the week 0 - monday
     * @param work true = working day 
     */
    public void setWorkingDay(int index, boolean work) {
        getPreferences().putBoolean("workingDay" + index, work);
        workingDays = -1;
        pcs.firePropertyChange(PROP_WORKING_DAYS, null, null);
    }
    
    /**
     * Returns working days.
     * 
     * @return boolean[7] true = working day. [0] - monday
     */
    public boolean[] getWorkingDays() {
        Preferences p = getPreferences();
        boolean[] r = new boolean[7];
        for (int i = 0; i < 7; i++) {
            r[i] = p.getBoolean("workingDay" + i, DEF_WORKING_DAYS[i]);
        }
        return r;
    }
}
