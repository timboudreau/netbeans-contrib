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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * PropertyEditor for duration in minutes.
 *
 * @author tl
 */
public class DurationPropertyEditor extends PropertyEditorSupport {
     private static final int[] DURATIONS = new int[] {
        0,
        5,
        10,
        15,
        20,
        30,
        45,
        60,
        90,
        120,
        150,
        180,
        240,
        300,
        360,
        420,
        480,
        12 * 60,
        8 * 60 * 2
    };
    private static String[] TAGS;
     
    private static final DurationFormat FORMAT = new DurationFormat(
            DurationFormat.Type.SHORT);
    private DurationFormat LONG = new DurationFormat(DurationFormat.Type.LONG);

    public String getAsText() {
        Integer value = (Integer) getValue();
        int duration = value == null ? 0 : value.intValue();
        Duration d = new Duration(duration,
            Settings.getDefault().getMinutesPerDay(), 
            Settings.getDefault().getDaysPerWeek(), true);

        return FORMAT.format(d);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        Duration d = null;
        try {
            d = FORMAT.parse(text);
        } catch (ParseException ex) {
            try {
                d = LONG.parse(text);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        setValue(new Integer(d.toMinutes(
                Settings.getDefault().getMinutesPerDay(), 
                Settings.getDefault().getDaysPerWeek(), true)));
    }

    public void attachEnv(PropertyEnv env) {        
        env.getFeatureDescriptor().setValue( "canEditAsText", Boolean.TRUE );
    }
    
    public String[] getTags() {
        if (TAGS == null) {
            int mpd = Settings.getDefault().getMinutesPerDay(); 
            int dpw = Settings.getDefault().getDaysPerWeek();
            TAGS = new String[DURATIONS.length];
            for (int i = 0; i < TAGS.length; i++) {
                TAGS[i] = FORMAT.format(new Duration(
                        DURATIONS[i], mpd, dpw, false));
            }
        }
        return TAGS;
    }
}
