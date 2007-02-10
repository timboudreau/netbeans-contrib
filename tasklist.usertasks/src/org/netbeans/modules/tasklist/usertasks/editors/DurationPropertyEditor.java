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
