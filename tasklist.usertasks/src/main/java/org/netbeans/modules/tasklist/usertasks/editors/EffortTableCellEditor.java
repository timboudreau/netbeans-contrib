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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;

/**
 * TableCellEditor for duration values.
 *
 * @author tl
 */
public class EffortTableCellEditor extends DefaultCellEditor {
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
    
    private List<Integer> durations;
    private List<String> texts;
    private DurationFormat short_ = new DurationFormat(
            DurationFormat.Type.SHORT);
    private DurationFormat long_ = new DurationFormat(DurationFormat.Type.LONG);
    private Duration dur;
    
    /**
     * Creates a new instance.
     */
    public EffortTableCellEditor() {
        super(new JComboBox());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        int d;
        if (value instanceof UserTask) {
            d = ((UserTask) value).getEffort();
        } else if (value instanceof Integer) {
            d = ((Integer) value).intValue();
        } else {
            d = 0;
        }

        durations = new ArrayList<Integer>();
        for (int dur: DURATIONS) {
            durations.add(dur);
        }

        int index = Collections.binarySearch(durations, d);
        if (index < 0) {
            index = -index - 1;
            durations.add(index, d);
        }

        DurationFormat df = new DurationFormat(DurationFormat.Type.LONG);
        texts = new ArrayList<String>(durations.size());
        Settings s = Settings.getDefault();
        int mpd = s.getMinutesPerDay();
        int dpw = s.getDaysPerWeek();
        for (int dur: durations) {
            texts.add(df.format(new Duration(dur, mpd, dpw, true)));
        }
        
        this.dur = new Duration(durations.get(index), mpd, dpw, true);
                
        ((JComboBox) editorComponent).setModel(
                new DefaultComboBoxModel(
                texts.toArray(new String[texts.size()])));
        ((JComboBox) editorComponent).setSelectedIndex(index);
        ((JComboBox) editorComponent).setEditable(true);
        return editorComponent;
    }
    
    public Object getCellEditorValue() {
        int index = ((JComboBox) editorComponent).getSelectedIndex();
        String txt = 
                (String) ((JComboBox) editorComponent).getEditor().getItem();
        Duration d = null;
        try {
            d = short_.parse(txt);
        } catch (ParseException ex) {
            // ignore
        }
        if (d == null) {
            try {
                d = long_.parse(txt);
            } catch (ParseException ex) {
                // ignore
            }
        }
        if (d == null)
            d = dur;
        if (d == null)
            return durations.get(index);
        else {
            int mpd = Settings.getDefault().getMinutesPerDay();
            int dpw = Settings.getDefault().getDaysPerWeek();
            return d.toMinutes(mpd, dpw, true);
        }
    }
}
