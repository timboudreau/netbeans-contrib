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
import javax.swing.JLabel;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * PropertyEditor for task priorities.
 *
 * @author tl
 */
public final class PriorityPropertyEditor extends PropertyEditorSupport {
    private static final String[] TAGS = UserTask.getPriorityNames();
    private static final JLabel LABEL = new JLabel();

    /**
     * Constructor
     */
    public PriorityPropertyEditor() {
    }

    public String getAsText() {
        Object v = getValue();
        if (v instanceof Integer) {
            int value = ((Integer) v).intValue();
            return UserTask.getPriorityName(value);
        } else {
            return ""; // NOI18N
        }
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        int index = UserTask.getPriority(text);
        if (index == -1) 
            throw new IllegalArgumentException("Unknown priority"); // NOI18N
        
        setValue(new Integer(index));
    }
    
    public String[] getTags() {
        return TAGS;
    }
}
