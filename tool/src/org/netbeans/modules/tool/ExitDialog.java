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
package org.netbeans.modules.tool;


import java.awt.Dimension;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;

import org.openide.awt.Actions;
import org.openide.cookies.SaveCookie;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/** Dialog which lets the user select which open files to close.
 *
 * @author  Ian Formanek, Petr Hrebejk, David Strupl
 */

public class ExitDialog extends JPanel implements java.awt.event.ActionListener {


    private static Object[] exitOptions;

    /** The dialog */
    private static java.awt.Dialog exitDialog;

    /** Result of the dialog */
    private static boolean result = false;

    private JList list;
    private DefaultListModel listModel;

    /** Constructs new dlg */
    private ExitDialog(Set/*<TopComponent>*/ tcs) {
        setLayout (new java.awt.BorderLayout ());

        listModel = new DefaultListModel();
        Iterator iter = tcs.iterator();
        while (iter.hasNext()) {
            listModel.addElement(iter.next());
        }
        init();
    }
    
    /** Constructs rest of dialog.
    */
    private void init() {
        list = new JList(listModel);
        list.setBorder(new EmptyBorder(2, 2, 2, 2));
        list.addListSelectionListener (new javax.swing.event.ListSelectionListener () {
                                           public void valueChanged (javax.swing.event.ListSelectionEvent evt) {
                                               updateSaveButton ();
                                           }
                                       }
                                      );
        // bugfix 37941, select first item in list
        if (!listModel.isEmpty ()) {
            list.setSelectedIndex (0);
        } else {                              
            updateSaveButton ();
        }
        JScrollPane scroll = new JScrollPane (list);
        scroll.setBorder (new CompoundBorder (new EmptyBorder (12, 12, 11, 0), scroll.getBorder ()));
        add(scroll, java.awt.BorderLayout.CENTER);
        list.setCellRenderer(new ExitDlgListCellRenderer());
        list.getAccessibleContext().setAccessibleName((NbBundle.getBundle(ExitDialog.class)).getString("ACSN_ListOfChangedFiles"));
        list.getAccessibleContext().setAccessibleDescription((NbBundle.getBundle(ExitDialog.class)).getString("ACSD_ListOfChangedFiles"));
        this.getAccessibleContext().setAccessibleDescription((NbBundle.getBundle(ExitDialog.class)).getString("ACSD_ExitDialog"));
    }
    
    private void updateSaveButton () {
        ((JButton)exitOptions [0]).setEnabled (list.getSelectedIndex () != -1);
    }

    /** @return preffered size */
    public Dimension getPreferredSize() {
        Dimension prev = super.getPreferredSize();
        return new Dimension(Math.max(300, prev.width), Math.max(150, prev.height));
    }

    /** This method is called when is any of buttons pressed
    */
    public void actionPerformed(final java.awt.event.ActionEvent evt ) {
        if (exitOptions[0].equals (evt.getSource ())) {
            save(false);
        } else if (exitOptions[1].equals (evt.getSource ())) {
            save(true);
        } else if (exitOptions[2].equals (evt.getSource ())) {
            theEnd();
        } else if (NotifyDescriptor.CANCEL_OPTION.equals (evt.getSource ())) {
            exitDialog.setVisible (false);
        }
    }

    /** Save the files from the listbox
    * @param all true- all files, false - just selected
    */
    private void save(boolean all) {
        Object array[] = ((all) ? listModel.toArray() : list.getSelectedValues());
        int i, count = ((array == null) ? 0 : array.length);
        int index = 0;	// index of last removed item

        for (i = 0; i < count; i++) {
            TopComponent nextObject = (TopComponent)array[i];
            index = listModel.indexOf(nextObject);
            save(nextObject);
        }

        if (listModel.isEmpty())
            theEnd();
        else {	// reset selection to new item at the same index if available
            if (index < 0)
                index = 0;
            else if (index > listModel.size() - 1) {
                index = listModel.size() - 1;
            }
            list.setSelectedIndex(index);
        }
    }

    /** Tries to save given top component using its save cookie.
     * Notifies user if excetions appear.
     */
    private void save(TopComponent toc) {
        try {
            Node []activatedNodes = toc.getActivatedNodes();
            if (activatedNodes != null) {
                for (int i=0;i<activatedNodes.length;i++) {
                    SaveCookie sc = (SaveCookie)activatedNodes[i].getCookie(SaveCookie.class);
                    if (sc != null) {
                        sc.save();
                    }
                }
            }
            SaveCookie sc = (SaveCookie)toc.getLookup().lookup(SaveCookie.class);
            if (sc != null) {
                sc.save();
            }
            listModel.removeElement(toc);
        } catch (java.io.IOException exc) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Saving failed.", exc); // NOI18N
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getBundle(ExitDialog.class).getString("EXC_Save"),
                    NotifyDescriptor.ERROR_MESSAGE
            );
            DialogDisplayer dd = DialogDisplayer.getDefault();
            dd.notify(nd);
        }
    }
 
    /** 
     * Exit the IDE. Closes the dialog and sets the result.
     */
    private void theEnd() {
        // XXX(-ttran) result must be set before calling setVisible(false)
        // because this will unblock the thread which called Dialog.show()
        result = true;
        exitDialog.setVisible (false);
        exitDialog.dispose();
    }

    /** Returns modified set of TopComponents
     */
    public static Set/*<TopComponent>*/ getModifiedTCSet() {
        Iterator iter = TopComponent.getRegistry().getOpened().iterator();
        java.util.Set set = new java.util.HashSet();
        while (iter.hasNext()) {
            TopComponent toc = (TopComponent) iter.next();
            Node []activatedNodes = toc.getActivatedNodes();
            if (activatedNodes != null) {
                for (int i=0;i<activatedNodes.length;i++) {
                    SaveCookie sc = (SaveCookie)activatedNodes[i].getCookie(SaveCookie.class);
                    if (sc != null) {
                        set.add(toc);
                        break;
                    }
                }
            }
            SaveCookie sc = (SaveCookie)toc.getLookup().lookup(SaveCookie.class);
            if (sc != null) {
                set.add(toc);
            }
        }
        return set;
    }

    /** Opens the ExitDialog and blocks until it's closed. If dialog doesm't
     * exists it creates new one. Returns true if the IDE should be closed.
     */
    public static boolean showDialog() {
        java.util.Set set = getModifiedTCSet();
        if (!set.isEmpty()) {

            // XXX(-ttran) caching this dialog is fatal.  If the user
            // cancels the Exit action, modifies some more files and tries to
            // Exit again the list of modified DataObject's is not updated,
            // changes made by the user after the first aborted Exit will be
            // lost.
            exitDialog = null;
            
            if (exitDialog == null) {
                ResourceBundle bundle = NbBundle.getBundle(ExitDialog.class);
                JButton buttonSave = new JButton();
                buttonSave.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Save"));
                JButton buttonSaveAll = new JButton();
                buttonSaveAll.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_SaveAll"));
                JButton buttonDiscardAll = new JButton();
                buttonDiscardAll.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_DiscardAll"));

                // special handling to handle a button title with mnemonic 
                // and to allow enable/disable control of the option
                Actions.setMenuText(buttonSave, bundle.getString("CTL_Save"), true);
                Actions.setMenuText(buttonSaveAll, bundle.getString("CTL_SaveAll"), true);
                Actions.setMenuText(buttonDiscardAll, bundle.getString("CTL_DiscardAll"), true);

                exitOptions = new Object[] {
                                  buttonSave,
                                  buttonSaveAll,
                                  buttonDiscardAll,
                              };
                ExitDialog exitComponent = new ExitDialog(set);
                DialogDescriptor exitDlgDescriptor = new DialogDescriptor (
                                                         exitComponent,                                                   // inside component
                                                         bundle.getString("CTL_ExitTitle"), // title
                                                         true,                                                            // modal
                                                         exitOptions,                                                     // options
                                                         NotifyDescriptor.CANCEL_OPTION,                                        // initial value
                                                         DialogDescriptor.RIGHT_ALIGN,                                    // option align
                                                         new org.openide.util.HelpCtx (ExitDialog.class.getName () + ".dialog"), // HelpCtx // NOI18N
                                                         exitComponent                                                    // Action Listener
                                                     );
                exitDlgDescriptor.setAdditionalOptions (new Object[] {NotifyDescriptor.CANCEL_OPTION});
                exitDialog = org.openide.DialogDisplayer.getDefault ().createDialog (exitDlgDescriptor);
            }

            result = false;
            exitDialog.show(); // Show the modal Save dialog
            return result;
        } else {
            return true;
        }
    }

    /** Renderer used in list box of exit dialog
     */
    private class ExitDlgListCellRenderer extends JLabel implements ListCellRenderer {
        /** generated Serialized Version UID */
        static final long serialVersionUID = 1877692790854373689L;

        protected Border hasFocusBorder;
        protected Border noFocusBorder;

        public ExitDlgListCellRenderer() {
            this.setOpaque(true);
            this.setBorder(noFocusBorder);
            hasFocusBorder = new LineBorder(UIManager.getColor("List.focusCellHighlight")); // NOI18N
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        }

        public java.awt.Component getListCellRendererComponent(JList list,
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // the list and the cell have the focus
        {
            final TopComponent toc = (TopComponent)value;
            super.setIcon(new ImageIcon(toc.getIcon()));
            String s = toc.getDisplayName();
            if ((s == null) || (s.length() == 0)) {
                s = toc.getName();
            }
            setText(s);
            if (isSelected){
                this.setBackground(UIManager.getColor("List.selectionBackground")); // NOI18N
                this.setForeground(UIManager.getColor("List.selectionForeground")); // NOI18N
            }
            else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }

            this.setBorder(cellHasFocus ? hasFocusBorder : noFocusBorder);

            return this;
        }
    }
}
