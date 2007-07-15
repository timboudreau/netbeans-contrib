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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.tasklist.usertasks.edit;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.netbeans.modules.tasklist.usertasks.model.LineResource;
import org.netbeans.modules.tasklist.usertasks.model.URLResource;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskResource;
import org.openide.util.Utilities;

/**
 * Renderer for resources associated with a task.
 *
 * @author tl
 */
public class ResourceListCellRenderer extends DefaultListCellRenderer {
    private static final Icon URL_ICON = new ImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/edit/url.png")); // NOI18N
    private static final Icon LINE_ICON = new ImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/edit/line.png")); // NOI18N
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, 
                isSelected, cellHasFocus);
        if (value instanceof URLResource) {
            setIcon(URL_ICON);
        } else if (value instanceof LineResource) {
            setIcon(LINE_ICON);
        } else {
            setIcon(null);
        }
        setText(((UserTaskResource) value).getDisplayName());
        return this;
    }    
}
