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
 * The Original Software is the Simple Edit Module.
 * The Initial Developer of the Original Software is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
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
 *
 * Contributor(s): David Strupl.
 */
package cz.solutions.simpleedit;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * This action will toggle the open/close behaviour for the currently
 * activated editor in the editor mode.
 * @author David Strupl
 */
public final class KeepEditorAction extends CallableSystemAction implements Presenter.Toolbar {
    private boolean active = Preferences.userNodeForPackage(
                ClosingTabsPanel.class).getBoolean("keepClosing", false);
    
    private JCheckBox checkBox = null;
    
    public void performAction() {
    }
    
    public String getName() {
        return NbBundle.getMessage(KeepEditorAction.class, "CTL_ToggleMainModeAction");
    }
    
    protected String iconResource() {
        return "cz/solutions/simpleedit/se.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public Component getToolbarPresenter() {
        if (checkBox != null) {
            return checkBox;
        }
        checkBox = new JCheckBox();
        checkBox.setToolTipText(NbBundle.getMessage(KeepEditorAction.class,
                "KeepThisEditor"));
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                TopComponent tc = WindowManager.getDefault().getRegistry().
                        getActivated();
                Mode m = WindowManager.getDefault().findMode(tc);
                if ("editor".equals(m.getName())) {
                    DataObject dobj = (DataObject)tc.
                        getLookup().lookup(DataObject.class);
                    if (dobj != null) {
                        if (checkBox.isSelected()) {
                            OpenedFiles.getDefault().addDataObject(dobj);
                        } else {
                            OpenedFiles.getDefault().removeDataObject(dobj);
                        }
                    }
                }
            }
        });
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(
        new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!active) {
                    return;
                }
                if (!evt.getPropertyName().equals("activated")) {
                    return;
                }
                Object n = evt.getNewValue();
                if (n instanceof TopComponent) {
                    TopComponent tc = (TopComponent)n;
                    DataObject d = (DataObject)tc.
                        getLookup().lookup(DataObject.class);
                    checkBox.setSelected(d != null &&
                            OpenedFiles.getDefault().keepOpened(d));
                    FilesHistory.getDefault().removeDataObject(d);
                    Mode m = WindowManager.getDefault().findMode(tc);
                    if ("editor".equals(m.getName())) {
                        TopComponent[] tca = m.getTopComponents();
                        for (int i = 0; i < tca.length; i++) {
                            if (tca[i] != tc) {
                                DataObject dobj = (DataObject)tca[i].
                                        getLookup().lookup(DataObject.class);
                                if ((dobj != null) && (OpenedFiles.getDefault().keepOpened(dobj))) {
                                    continue;
                                }
                                if (dobj != null) {
                                    FilesHistory.getDefault().addDataObject(dobj);
                                }
                                tca[i].close();
                            }
                        }
                    }
                }
                PreviousFileAction pfe = (PreviousFileAction)
                    PreviousFileAction.get(PreviousFileAction.class);
                pfe.fire();
            }
        });
        Preferences.userNodeForPackage(ClosingTabsPanel.class).
            addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                active = Preferences.userNodeForPackage(
                    ClosingTabsPanel.class).getBoolean("keepClosing", false);
            }
        });
        return checkBox;
    }
}
