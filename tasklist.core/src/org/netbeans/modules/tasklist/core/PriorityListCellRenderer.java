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

package org.netbeans.modules.tasklist.core;

import org.netbeans.modules.tasklist.client.SuggestionPriority;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * ListCellRenderer for priorities
 *
 * @author tl
 */
public class PriorityListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1;

    private static final String[] TAGS = SuggestionPriority.getPriorityNames();

    /**
     * Default colors for diferent priorities
     * [0] - high, [1] - medium-high, ...
     */
    public static final Color[] COLORS = {
        new Color(221, 0, 0),
        new Color(255, 128, 0),
        Color.black,
        new Color(0, 187, 0),
        new Color(0, 128, 0)
    };

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof SuggestionPriority) {
            SuggestionPriority prio = (SuggestionPriority) value;
            setText(TAGS[prio.intValue() - 1]);
            if (!isSelected) {
                setForeground(COLORS[prio.intValue() - 1]);
            }
        }
        return this;
    }
}
