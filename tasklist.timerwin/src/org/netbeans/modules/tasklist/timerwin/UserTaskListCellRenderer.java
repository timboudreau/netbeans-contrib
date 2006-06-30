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

package org.netbeans.modules.tasklist.timerwin;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.renderers.UserTaskIconProvider;
import org.openide.util.NbBundle;

/**
 * ListCellRenderer für UT.
 *
 * @author tl
 */
public class UserTaskListCellRenderer extends DefaultListCellRenderer {
    private static final String NONE = NbBundle.getMessage(
            UserTaskListCellRenderer.class, "None"); // NOI18N
    
    private ImageIcon icon = new ImageIcon();
    
    /** 
     * Creates a new instance of UserTaskListCellRenderer 
     */
    public UserTaskListCellRenderer() {
    }
    
    public Component getListCellRendererComponent(
        JList list,
	Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, 
                cellHasFocus);
        if (value == null) {
            setText(NONE);
            setIcon(null);
        } else {
            UserTask ut = (UserTask) value;
            Settings set = Settings.getDefault();
            Duration d = new Duration(ut.getSpentTime(), set.getHoursPerDay(),
                    set.getDaysPerWeek());
            setText("[" + d.format() + "] " +  ut.getSummary());
            icon.setImage(UserTaskIconProvider.getUserTaskImage(
                    (UserTask) value, false));
            setIcon(icon);
        }
        return this;
    }
}
