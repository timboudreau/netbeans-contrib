/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.editors;

import java.text.MessageFormat;

import org.netbeans.modules.tasklist.core.editors.StringPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for duration in minutes.
 *
 * @author Tim Lebedkov
 */
public class DurationPropertyEditor extends StringPropertyEditor {
    private static final MessageFormat EFFORT_FORMAT = 
        new MessageFormat(NbBundle.getMessage(DurationPropertyEditor.class, 
            "EffortFormat")); // NOI18N
    
    public String getAsText() {
        int duration = ((Integer) getValue()).intValue();
        Duration d = new Duration(duration,
            Settings.getDefault().getHoursPerDay(), 
            Settings.getDefault().getDaysPerWeek());

        String s = EFFORT_FORMAT.format(new Object[] {
            new Integer(d.weeks),
            new Integer(d.days), new Integer(d.hours), new Integer(d.minutes)
        }).trim();
        return s;
    }
}
