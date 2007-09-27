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

package org.netbeans.modules.jackpot.ui;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.MissingResourceException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.jackpot.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/** 
 * Jackpot Menu Manager action.
 */
public class RefactoringManagerAction extends CallableSystemAction {

    public RefactoringManagerAction() {
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
    }    
    
    public void performAction() {
        performAction(new RefactoringManagerPanel());
    }
    
    void performAction(final RefactoringManagerPanel panel) {
        try {
            JButton newQueryButton = new JButton();
            Mnemonics.setLocalizedText(newQueryButton, NbBundle.getMessage(RefactoringManagerAction.class, "BTN_RefactoringMgr_New")); // NOI18N
            newQueryButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newInspection();
                }
            });
            newQueryButton.setEnabled(rulesModuleInstalled());
            JButton closeButton = new JButton();
            Mnemonics.setLocalizedText(closeButton, NbBundle.getMessage(RefactoringManagerAction.class, "BTN_Panel_CloseButton")); // NOI18N
            JButton openInEditor = new JButton();
            openInEditor.setEnabled(false);
            OpenInEditorListener l = new OpenInEditorListener(panel, openInEditor);
            openInEditor.addActionListener(l);
            panel.addInspectionListSelectionListener(l);
            Mnemonics.setLocalizedText(openInEditor, NbBundle.getMessage(RefactoringManagerAction.class, "BTN_Panel_OpenInEditorButton")); // NOI18N
            DialogDescriptor dd = new DialogDescriptor(panel,
                                    NbBundle.getMessage(RefactoringManagerAction.class, "LBL_Panel_Title"),  // NOI18N
                                    false, // not modal
                                    new Object[] { newQueryButton, openInEditor, closeButton },
                                    closeButton,
                                    DialogDescriptor.DEFAULT_ALIGN,
                                    null,
                                    null);
            dd.setClosingOptions(new Object[] { openInEditor, closeButton });
            // set helpctx to null again, DialogDescriptor replaces null with HelpCtx.DEFAULT_HELP
            dd.setHelpCtx(null);
            java.awt.Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    QuerySetList.instance().saveAll();
                    panel.close();
                }
            });
            dialog.setVisible(true);
        } catch (Throwable t) {
            ErrorManager.getDefault().notify(t);
        }
    }
    
    protected boolean asynchronous() {
        return true;
    }

    public String getName() {
        return NbBundle.getMessage(RefactoringManagerAction.class, "LBL_Action_Name"); // NOI18N
    }

    protected String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Adding hint.
     */
    protected void initialize() {
	super.initialize();
        putProperty(RefactoringManagerAction.SHORT_DESCRIPTION, NbBundle.getMessage(RefactoringManagerAction.class, "HINT_Action")); // NOI18N
    }
    
    private void newInspection() {
        try {
            final NewQueryPanel panel = new NewQueryPanel();
            final InspectionsList inspections = InspectionsList.instance();
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("OK"))
                        try {
                            Inspection insp = inspections.create(
                                    panel.getQueryName(),
                                    panel.getRefactoringName(),
                                    panel.getDescription());
                        } catch (IOException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                }
            };
            final DialogDescriptor dd = new DialogDescriptor(panel,
                    NbBundle.getMessage(RefactoringManagerAction.class, "LBL_NewQuery"),  // NOI18N
                    true,
                    al);
            panel.addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void insertUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void removeUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                private void updateOKButton() {
                    String name = panel.getQueryName();
                    boolean okay = name.length() > 0 && 
                                   inspections.getInspection(name) == null;
                    dd.setValid(okay);
                }
            });
            java.awt.Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
            dialog.setVisible(true);
        } catch (Throwable t) {
            ErrorManager.getDefault().notify(t);
        }        
    }
    
    private boolean rulesModuleInstalled() {
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Templates/Jackpot/Rules.rules"); // NOI18N
            if (fo == null) // true if rules module hasn't installed template
                return false;
            DataObject.find(fo); // throws exception if rules file loader isn't installed
            return true;
        } catch (DataObjectNotFoundException e) {
            return false;
        }
    }
    
    // helper classes
    private static class OpenInEditorListener implements ActionListener, ListSelectionListener {
        RefactoringManagerPanel tp;
        JButton b;
        public OpenInEditorListener(RefactoringManagerPanel panel, JButton button) {
            tp = panel;
            b = button;
        }
        
        // ActionListener
        public void actionPerformed(ActionEvent ev) {
            for (Inspection insp : tp.getSelection()) {
                try {
                    DataObject dobj = insp.getDataObject();
                    EditCookie ec =(EditCookie) dobj.getCookie(EditCookie.class);
                    if(ec != null) {
                        ec.edit();
                    } else {
                        OpenCookie oc =(OpenCookie) dobj.getCookie(OpenCookie.class);
                        if(oc != null)
                            oc.open();
                        else
                            assert false : "DataObject " + dobj + " has to have a EditCookie or OpenCookie.";
                    }
                } catch (DataObjectNotFoundException e) {
                    ; // ignore and continue
                }
            }
        }

        // ListSelectionListener
        public void valueChanged(ListSelectionEvent ev) {
            Inspection[] nodes = tp.getSelection();
            boolean enable = nodes.length > 0;
            int i = 0;
            while(enable && i < nodes.length) {
                try {
                    DataObject dobj = nodes[i].getDataObject();
                    EditCookie ec =(EditCookie) dobj.getCookie(EditCookie.class);
                    enable = ec != null;
                } catch (DataObjectNotFoundException e) {
                    ; // ignore and continue
                }
                i++;
            }
            b.setEnabled(enable);
        }
    }
}
