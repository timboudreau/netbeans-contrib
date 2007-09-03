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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * PerspectivePreferences.java
 */

package org.netbeans.modules.perspective.persistence;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Anuradha G
 */
public class PerspectivePreferences {

    private Preferences preferences = NbPreferences.forModule(PerspectivePreferences.class);
    private static PerspectivePreferences instance;
    private static final String SELECTED = "selected"; //NOI18n
    private static final String NONE = "NONE"; //NOI18n
    private static final String TRACK_OPENED = "track_opened"; //NOI18n
    private static final String TRACK_ACTIVE = "track_active"; //NOI18n
    private static final String CLOSE_OPENED = "close_opened"; //NOI18n

    private PerspectivePreferences() {
    }

    public static synchronized PerspectivePreferences getInstance() {
        if (instance == null) {
            instance = new PerspectivePreferences();
        }
        return instance;
    }

    public void setSelectedPerspective(String name) {

        preferences.put(SELECTED, name);
    }

    public String getSelectedPerspective() {
        return preferences.get(SELECTED, NONE);
    }

    public boolean isTrackOpened() {
        return preferences.getBoolean(TRACK_OPENED, false);
    }

    public void setTrackOpened(boolean b) {
        preferences.putBoolean(TRACK_OPENED, b);
    }

    public boolean isTrackActive() {
        return preferences.getBoolean(TRACK_ACTIVE, false);
    }

    public void setTrackActive(boolean b) {
        preferences.putBoolean(TRACK_ACTIVE, b);
    }

    public boolean isCloseOpened() {
        //true by default
        return preferences.getBoolean(CLOSE_OPENED, true);
    }

    public void setCloseOpened(boolean b) {
        preferences.putBoolean(CLOSE_OPENED, b);
    }
}