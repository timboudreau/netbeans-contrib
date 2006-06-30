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
