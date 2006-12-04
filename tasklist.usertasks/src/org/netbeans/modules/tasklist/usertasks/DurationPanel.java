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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.text.JTextComponent;

import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;

/**
 * Panel for duration
 *
 * @author tl
 */
public class DurationPanel extends JComboBox {
    private static final long serialVersionUID = 1;
    
    /**
     * This array must be sorted.
     */
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
    
    private DurationFormat short_ = new DurationFormat(
            DurationFormat.Type.SHORT);
    private DurationFormat long_ = new DurationFormat(DurationFormat.Type.LONG);
    private Duration dur;
    
    /**
     * Creates new form DurationPanel
     */
    public DurationPanel() {
        final ComboBoxEditor def = getEditor();
        setEditor(new ComboBoxEditor() {
            public void addActionListener(ActionListener l) {
                def.addActionListener(l);
            }
            public Component getEditorComponent() {
                return def.getEditorComponent();
            }
            public Object getItem() {
                String text = ((JTextComponent) getEditorComponent()).getText();
                Duration d = null;
                try {
                    d = short_.parse(text);
                } catch (ParseException ex) {
                    // ignore
                }
                if (d == null) {
                    try {
                        d = long_.parse(text);
                    } catch (ParseException ex) {
                        // ignore
                    }
                }
                if (d == null)
                    d = dur;
                if (d == null)
                    return 0;

                return d.toMinutes(Settings.getDefault().getMinutesPerDay(),
                        Settings.getDefault().getDaysPerWeek(), true);
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
                    int mpd = Settings.getDefault().getMinutesPerDay();
                    int dpw = Settings.getDefault().getDaysPerWeek();
                    Duration d = new Duration(m, mpd, dpw, true);
                    text = long_.format(d);
                }
                ((JTextComponent) getEditorComponent()).setText(text);
            }
        });
        setEditable(true);
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        for (int d: DURATIONS) {
            m.addElement(d);
        }
        setModel(m);
        setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                   JList list, Object value, int index, boolean isSelected,
                   boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, 
                        isSelected, cellHasFocus);
                int m = ((Integer) value).intValue();
                int mpd = Settings.getDefault().getMinutesPerDay();
                int dpw = Settings.getDefault().getDaysPerWeek();
                // UTUtils.LOGGER.fine("mpd=" + mpd + ", dpw=" + dpw);
                Duration d = new Duration(m, mpd, dpw, true);
                setText(long_.format(d));
                return this;
            }
        });
    }
    
    /**
     * Sets the duration shown in this panel
     *
     * @param minutes new duration in minutes
     */
    public void setDuration(int minutes) {
        int index = getModel().getSize();
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            int dur = ((Integer) getModel().getElementAt(i)).intValue();
            if (minutes > dur) {
                index = i + 1;
                break;
            }
        }
        int dur = ((Integer) getModel().getElementAt(index - 1)).intValue();
        if (dur == minutes) {
            setSelectedIndex(index - 1);
        } else {
            ((DefaultComboBoxModel) getModel()).insertElementAt(minutes,
                    index);
            setSelectedIndex(index);
        }
    }
    
    /**
     * Returns choosed duration in minutes
     *
     * @return duration in minutes
     */
    public int getDuration() {
        return ((Integer) getSelectedItem()).intValue();
    }
}
