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

package org.netbeans.modules.vcscore.ui;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;

import org.openide.util.NbBundle;

/**
 * The container of error output of failed commands.
 *
 * @author  Martin Entlicher
 */
public class ErrorOutputPanel extends javax.swing.JPanel {
    
    private static ErrorOutputPanel defaultInstance;
    private Action discardAction;
    
    /** Creates new form ErrorOutputPanel */
    public ErrorOutputPanel() {
        initComponents();
        java.awt.Font font = errorArea.getFont();
        errorArea.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        initPopupMenu();
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(       
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
            "discard"); //NOI18N
        getActionMap().put("discard", discardAction);//NOI18N
    }
    
    protected void initPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        java.awt.event.ActionListener discardAllListener = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discardAll();
            }
        };
        discardAction = new AbstractAction(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab")) { //NOI18N
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discard(ErrorOutputPanel.this);
            }
        };
        Action clearAction = new AbstractAction(NbBundle.getMessage(ErrorOutputPanel.class, "CMD_ClearOutput")) { // NOI18N
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try {
                    errorArea.replaceRange("", 0, errorArea.getLineEndOffset(errorArea.getLineCount() - 1));
                } catch (BadLocationException blex) {
                    org.openide.ErrorManager.getDefault().notify(blex);
                }
            }
        };
        JMenuItem discardTab = new JMenuItem();//NOI18N
        discardTab.setAction(discardAction);
        discardTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener(discardAllListener);
        JMenuItem clear = new JMenuItem();
        clear.setAction(clearAction);
        menu.add(clear);
        menu.addSeparator();
        menu.add(discardTab);
        menu.add(discardAll);
        errorArea.add(menu);
        
        PopupListener popupListener = new PopupListener(menu);
        errorArea.addMouseListener(popupListener);
        adjustInputMap(errorArea);
                       
        this.addMouseListener(popupListener);
        jScrollPane1.addMouseListener(popupListener);
        
    }
    
    private void adjustInputMap(JComponent c) {
        c.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
            "discard");
        errorArea.getActionMap().put("discard", discardAction);//NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        errorArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        errorArea.setEditable(false);
        jScrollPane1.setViewportView(errorArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents
    
    public String getTitle() {
        return NbBundle.getMessage(ErrorOutputPanel.class, "TTL_ErrorOutput");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea errorArea;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Put the error output to the output panel.
     * @param cmdName The (display) name of the command
     * @param exec The execution string of the failed command
     * @param error The error output of the command.
     */
    public void errorOutput(String cmdName, String exec, String error) {
        errorArea.append(NbBundle.getMessage(ErrorOutputPanel.class, "MSG_CommandFailed", cmdName, exec));
        if (error != null && error.length() > 0) {
            errorArea.append(NbBundle.getMessage(ErrorOutputPanel.class, "MSG_Error_output_follows"));
            errorArea.append("\n");
            errorArea.append(error);
            if (!error.endsWith("\n")) {
                errorArea.append("\n");
            }
        } else {
            errorArea.append(NbBundle.getMessage(ErrorOutputPanel.class, "MSG_No_error_output"));
            errorArea.append("\n");
        }
        errorArea.append("\n");
    }
    
    class PopupListener extends java.awt.event.MouseAdapter {
        
        private JPopupMenu menu;
        
        public PopupListener(JPopupMenu menu) {
            this.menu = menu;
        }
        
        public void mousePressed(java.awt.event.MouseEvent event) {
            if ((event.getModifiers() & java.awt.event.MouseEvent.BUTTON3_MASK) == java.awt.event.MouseEvent.BUTTON3_MASK) {
                menu.show((java.awt.Component)event.getSource(),event.getX(),event.getY());
            }
        }
    }
    
}
