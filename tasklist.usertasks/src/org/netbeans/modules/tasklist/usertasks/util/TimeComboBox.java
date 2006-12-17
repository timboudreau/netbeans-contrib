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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.util;

import java.awt.Component;
import java.awt.TextComponent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.text.JTextComponent;
import org.openide.util.NbBundle;

/**
 * Time editor.
 * 
 * @author tl
 */
public class TimeComboBox extends JComboBox {
    private static final MessageFormat FORMAT = new MessageFormat(
            NbBundle.getMessage(TimeComboBox.class, "Format"));
    
    /** 
     * Creates a new instance of TimeComboBox.
     */
    public TimeComboBox() {
        final ComboBoxEditor def = getEditor();
        setEditor(new ComboBoxEditor() {
            public void addActionListener(ActionListener l) {
                def.addActionListener(l);
            }
            public Component getEditorComponent() {
                return def.getEditorComponent();
            }
            public Object getItem() {
                try {
                    Object[] obj = FORMAT.parse(
                            ((JTextComponent) getEditorComponent()).getText());
                    if (obj[0] == null || obj[1] == null)
                        return 0;
                    int h = ((Long) obj[0]).intValue();
                    int m = ((Long) obj[1]).intValue();
                    return h * 60 + m;
                } catch (ParseException ex) {
                    return 0;
                }
            }
            public void removeActionListener(ActionListener l) {
                def.removeActionListener(l);
            }
            public void selectAll() {
                def.selectAll();
            }
            public void setItem(Object anObject) {
                String text = "";
                if (anObject != null) {
                    int m = ((Integer) anObject).intValue();
                    int h = m / 60;
                    m = m % 60;
                    text = FORMAT.format(new Object[] {h, m});
                }
                ((JTextComponent) getEditorComponent()).setText(text);
            }
        });
        setEditable(true);
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        for (int i = 0; i < 48; i++) {
            m.addElement(new Integer(i * 30));
        }
        setModel(m);
        setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                   JList list, Object value, int index, boolean isSelected,
                   boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, 
                        isSelected, cellHasFocus);
                int m = ((Integer) value).intValue();
                int h = m / 60;
                m = m % 60;
                setText(FORMAT.format(new Object[] {h, m}));
                return this;
            }
        });
    }
    
    /**
     * Returns selected time.
     * 
     * @return selected time in minutes. 
     */
    public int getTime() {
        return ((Integer) getSelectedItem()).intValue();
    }
    
    /**
     * Sets new time.
     * 
     * @param minutes minutes (e.g. 100 for 01:40)
     */
    public void setTime(int minutes) {
        int[] v = new int[getModel().getSize()];
        for (int i = 0; i < v.length; i++) {
            v[i] = (Integer) getModel().getElementAt(i);
        }

        int index = Arrays.binarySearch(v, minutes);
        if (index < 0) {
            index = -index - 1;
            ((DefaultComboBoxModel) getModel()).insertElementAt(
                    new Integer(minutes), index);
        }
        setSelectedIndex(index);
    }
}
