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
    private static final String SELECTED = "selected"; //NOI18N


    private static final String TRACK_OPENED = "track_opened"; //NOI18N

    private static final String TRACK_ACTIVE = "track_active"; //NOI18N

    private static final String CLOSE_OPENED = "close_opened"; //NOI18N

    private static final String CUSTOM_PERSPECTIVE_COUNT = "cp_count"; //NOI18N


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
        return preferences.get(SELECTED, null);//NOI18n
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

    public synchronized String getCustomPerspectiveName() {
        String name = "custom_"; //NOI18N
        int count = preferences.getInt(CUSTOM_PERSPECTIVE_COUNT, 1);
        name += count;
        preferences.putInt(CUSTOM_PERSPECTIVE_COUNT, ++count);
        return name;
    }

    public void reset() {
        preferences.putInt(CUSTOM_PERSPECTIVE_COUNT, 1);
        preferences.putBoolean(CLOSE_OPENED, true);
        preferences.putBoolean(TRACK_ACTIVE, false);
        preferences.putBoolean(TRACK_OPENED, false);
    }

}