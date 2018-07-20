/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    private static final long serialVersionUID = 2;
    
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
                String txt = long_.format(d);
                if (txt.length() == 0)
                    txt = " "; // NOI18N
                setText(txt);
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
        int[] v = new int[getModel().getSize()];
        for (int i = 0; i < v.length; i++) {
            v[i] = (Integer) getModel().getElementAt(i);
        }

        int index = java.util.Arrays.binarySearch(v, minutes);
        if (index < 0) {
            index = -index - 1;
            ((DefaultComboBoxModel) getModel()).insertElementAt(minutes,
                    index);
        }
        setSelectedIndex(index);
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
