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

import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import java.text.MessageFormat;
import javax.swing.border.EmptyBorder;
import org.netbeans.modules.tasklist.usertasks.DurationPanel;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for duration in minutes.
 *
 * @author tl
 */
public class DurationPropertyEditor extends PropertyEditorSupport 
implements ExPropertyEditor {
    private static final MessageFormat EFFORT_FORMAT = 
        new MessageFormat(NbBundle.getMessage(DurationPropertyEditor.class, 
            "EffortFormat")); // NOI18N

    private boolean editable;
    
    public String getAsText() {
        Integer value = (Integer) getValue();
        int duration = value == null ? 0 : value.intValue();
        Duration d = new Duration(duration,
            Settings.getDefault().getHoursPerDay(), 
            Settings.getDefault().getDaysPerWeek());

        String s = EFFORT_FORMAT.format(new Object[] {
            new Integer(d.weeks),
            new Integer(d.days), new Integer(d.hours), new Integer(d.minutes)
        }).trim();
        return s;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(text);
    }

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
}
