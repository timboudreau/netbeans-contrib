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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.moduleresolver.ui;

import org.netbeans.modules.moduleresolver.*;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

public class DescriptionStep implements WizardDescriptor.Panel<WizardDescriptor> {

    private ContentPanel panel;
    private boolean isValid = false;
    private ProgressHandle handle = null;
    private JComponent progressComponent;
    private JLabel mainLabel;
    private MissingModulesModel tableModel;
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener> ();

    public Component getComponent () {
        if (panel == null) {
            panel = new ContentPanel (getBundle ("DescriptionPanel_Name"));
            panel.addPropertyChangeListener (new PropertyChangeListener () {
                        public void propertyChange (PropertyChangeEvent evt) {
                            if (ContentPanel.FINDING_MODULES.equals (evt.getPropertyName ())) {
                                doFindingModues.run ();
                            }
                        }
                    });
        }
        return panel;
    }

    public HelpCtx getHelp () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid () {
        return isValid && tableModel != null && ! tableModel.getChecked ().isEmpty ();
    }

    public synchronized void addChangeListener (ChangeListener l) {
        listeners.add(l);
    }

    public synchronized void removeChangeListener (ChangeListener l) {
        listeners.remove(l);
    }

    private void fireChange () {
        ChangeEvent e = new ChangeEvent (this);
        List<ChangeListener> templist;
        synchronized (this) {
            templist = new ArrayList<ChangeListener> (listeners);
        }
        for (ChangeListener l : templist) {
            l.stateChanged (e);
        }
    }

    private Runnable doFindingModues = new Runnable () {

                public void run () {
                    if (SwingUtilities.isEventDispatchThread ()) {
                        RequestProcessor.getDefault ().post (doFindingModues);
                        return;
                    }
                    RequestProcessor.Task findingTask = FindBrokenModules.getFindingTask ();
                    if (findingTask != null && findingTask.isFinished ()) {
                        presentBrokenModules ();
                    } else {
                        if (findingTask == null) {
                            findingTask = FindBrokenModules.createFindingTask ();
                            findingTask.schedule (10);
                        }
                        if (findingTask.getDelay () > 0) {
                            findingTask.schedule (10);
                        }
                        findingTask.addTaskListener (new TaskListener () {
                                    public void taskFinished (Task task) {
                                        presentBrokenModules ();
                                        return;
                                    }
                                });
                        presentFindingModules ();
                    }
                }
            };

    private void presentBrokenModules () {
        if (handle != null) {
            handle.finish ();
            panel.replaceComponents ();
            handle = null;
        }
        Collection<UpdateElement> elems = FindBrokenModules.getModulesForRepair ();
        if (elems != null && !elems.isEmpty ()) {
            isValid = true;
            panel.replaceComponents (
                    elems.size () > 1 ?
                        new JLabel (getBundle ("DescriptionStep_BrokenModulesFound", elems.size ())) :
                        new JLabel (getBundle ("DescriptionStep_BrokenModuleFound", elems.size ())));
            tableModel = new MissingModulesModel (FindBrokenModules.getModulesForRepair ());
            panel.setTableModel (tableModel);
        } else {
            panel.replaceComponents (
                    new JLabel (getBundle ("DescriptionStep_NoMissingModules1")),
                    new JLabel (),
                    new JLabel (getBundle ("DescriptionStep_NoMissingModules2")));
            isValid = false;
        }
        fireChange ();
    }

    private class MissingModulesModel extends DefaultTableModel {

        private LinkedList<UpdateElement> modules = null;
        private Collection<UpdateElement> checked = null;

        public MissingModulesModel (Collection<UpdateElement> elements) {
            modules = new LinkedList<UpdateElement> (elements);
            checked = new HashSet<UpdateElement> (modules);
        }

        public Collection<? extends UpdateElement> getChecked () {
            return checked;
        }

        @Override
        public int getRowCount () {
            return modules == null ? 0 : modules.size ();
        }

        @Override
        public int getColumnCount () {
            return 3;
        }
        
        @Override
        public Object getValueAt (int rowIndex, int columnIndex) {
            Object res = null;
            switch (columnIndex) {
                case 0:
                    res = checked.contains (modules.get (rowIndex));
                    break;
                case 1:
                    res = modules.get (rowIndex).getDisplayName ();
                    break;
                case 2:
                    res = presentUpdateElements (FindBrokenModules.getMissingModules (modules.get (rowIndex)));
                    break;
                default:
                    assert false : "Unknow columnIndex " + columnIndex;
            }
            return res;
        }

        @Override
        public void setValueAt (Object anValue, int row, int col) {
            if (col == 0) {
                if (anValue != null && anValue instanceof Boolean) {
                    boolean b = (Boolean) anValue;
                    if (b) {
                        checked.add (modules.get (row));
                    } else {
                        checked.remove (modules.get (row));
                    }
                    fireChange ();
                }
            }
        }

        public @Override
        Class getColumnClass ( int c) {
            Class res = null;
            switch (c) {
                case 0:
                    res = Boolean.class;
                    break;
                case 1:
                    res = String.class;
                    break;
                case 2:
                    res = String.class;
                    break;
            }
            return res;
        }

        @Override
        public String getColumnName (int column) {
            String res = null;
            switch (column) {
                case 0:
                    res = getBundle ("DescriptionStep_Column_Repair");
                    break;
                case 1:
                    res = getBundle ("DescriptionStep_Column_ModuleName");
                    break;
                case 2:
                    res = getBundle ("DescriptionStep_Column_MissingModules");
                    break;
            }
            return res;
        }

        @Override
        public boolean isCellEditable (int row, int col) {
            return col == 0;
        }
        
    }

    private static String presentUpdateElements (Collection<UpdateElement> elems) {
        String res = "";
        for (UpdateElement el : new LinkedList<UpdateElement> (elems)) {
            res += res.length () == 0 ? el.getDisplayName () : ", " + el.getDisplayName (); // NOI18N
        }
        return res;
    }

    private void presentFindingModules () {
        handle = ProgressHandleFactory.createHandle (ContentPanel.FINDING_MODULES);
        progressComponent = ProgressHandleFactory.createProgressComponent (handle);
        mainLabel = new JLabel (getBundle ("DescriptionStep_FindingRuns_Wait"));
        JLabel detailLabel = new JLabel (getBundle ("DescriptionStep_FindingRuns"));

        handle.setInitialDelay (0);
        handle.start ();
        panel.setTableModel (null);
        panel.replaceComponents (mainLabel, progressComponent, detailLabel);
    }

    private static String getBundle (String key, Object... params) {
        return NbBundle.getMessage (DescriptionStep.class, key, params);
    }

    public void readSettings (WizardDescriptor settings) {
    }

    public void storeSettings (WizardDescriptor settings) {
        if (tableModel != null) {
            settings.putProperty (InstallMissingModulesIterator.CHOSEN_ELEMENTS, tableModel.getChecked ());
        }
    }
}

