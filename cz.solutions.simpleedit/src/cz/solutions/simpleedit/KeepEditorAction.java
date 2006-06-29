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
 * The Original Software is the Simple Edit Module.
 * The Initial Developer of the Original Software is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
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
