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

import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import java.text.MessageFormat;
import java.text.ParseException;
import javax.swing.border.EmptyBorder;
import org.netbeans.modules.tasklist.usertasks.DurationPanel;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for duration in minutes.
 *
 * @author tl
 */
public class DurationPropertyEditor extends PropertyEditorSupport {
    /* see http://www.netbeans.org/issues/show_bug.cgi?id=87729
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
     */
    private static final DurationFormat FORMAT = new DurationFormat(
            DurationFormat.Type.SHORT);

    public String getAsText() {
        Integer value = (Integer) getValue();
        int duration = value == null ? 0 : value.intValue();
        Duration d = new Duration(duration,
            Settings.getDefault().getMinutesPerDay(), 
            Settings.getDefault().getDaysPerWeek(), true);

        return FORMAT.format(d);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        try {
            Duration d = FORMAT.parse(text);
            setValue(new Integer(d.toMinutes(
                    Settings.getDefault().getMinutesPerDay(), 
                    Settings.getDefault().getDaysPerWeek(), true)));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /* this may be used later if we have better editor for durations
    public boolean supportsCustomEditor () {
        return true;
    }

    public Component getCustomEditor() {
        int duration = ((Integer) getValue()).intValue();
	DurationPanel dp = new DurationPanel();
        dp.setPropertyEditor(this);
        dp.setBorder(new EmptyBorder(11, 11, 12, 12));
        if (!editable)
            dp.setEnabled(false);
        return dp;
    }
    
    public void attachEnv(PropertyEnv env) {        
        FeatureDescriptor desc = env.getFeatureDescriptor();
        if (desc instanceof Node.Property){
            Node.Property prop = (Node.Property)desc;
            editable = prop.canWrite();
        }
    }
    */

    /* see http://www.netbeans.org/issues/show_bug.cgi?id=87729
     public String[] getTags() {
        if (TAGS == null) {
            int hpd = Settings.getDefault().getHoursPerDay(); 
            int dpw = Settings.getDefault().getDaysPerWeek();
            TAGS = new String[DURATIONS.length];
            for (int i = 0; i < TAGS.length; i++) {
                TAGS[i] = FORMAT.format(new Duration(DURATIONS[i], hpd, dpw));
            }
        }
        return TAGS;
    }*/
}
