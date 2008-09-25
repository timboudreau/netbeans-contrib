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

package org.netbeans.modules.modulemanager;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.CharConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.AsyncGUIJob;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.xml.XMLUtil;

/** Module selection panel allows user to enable/disable modules in Module Catalog
 *
 * @author cledantec, jrojcek, Jesse Glick, Jirka Rechtacek (jrechtacek@netbeans.org)
 */
public class ModuleSelectionPanel extends javax.swing.JPanel 
                                  implements PropertyChangeListener {

    private TreeTableView treeTableView;
    private static final Logger err = Logger.getLogger(ModuleSelectionPanel.class.getName ());
    
    /** default size values */
    private static final int DEF_TREE_WIDTH = 420;
    private static final int DEF_0_COL_WIDTH = 60;
    private static final int DEF_1_COL_WIDTH = 150;
    private static final int DEF_HEIGHT = 350;
    
    private static ModuleDeleter deleter;
    
    private Cursor cursor = null;
    static private ModuleSelectionPanel panel = null;
    
    static public ModuleSelectionPanel getGUI (boolean create) {
        assert create || panel != null;
        if (panel == null && create) {
            panel = new ModuleSelectionPanel ();
        }
        return panel;
    }

    private ModuleSelectionPanel ()  {
        initComponents();
        treeTableView = new TreeTableView ();
        treeTableView.setRootVisible(false);
        ExplorerPanel explorerPanel = new ExplorerPanel ();
	explorerPanel.getAccessibleContext ().setAccessibleName (
	    NbBundle.getBundle (ModuleSelectionPanel.class).getString  ("ACN_ModuleSelectionPanel_ExplorerPanel")); // NOI18N
	explorerPanel.getAccessibleContext ().setAccessibleDescription (
	    NbBundle.getBundle (ModuleSelectionPanel.class).getString  ("ACD_ModuleSelectionPanel_ExplorerPanel")); // NOI18N
        explorerPanel.setLayout (new BorderLayout ());
        explorerPanel.add (treeTableView, BorderLayout.CENTER);
        modulesPane.add (explorerPanel, BorderLayout.CENTER);
        manager = explorerPanel.getExplorerManager();

        //Fix for NPE on WinXP L&F - either may be null - Tim
        Font f = UIManager.getFont("controlFont"); // NOI18N
        Integer i = (Integer) UIManager.get("nbDefaultFontSize"); // NOI18N
        if (i == null) {
            i = new Integer(11); //fudge the default if not present
        }
        if (f == null) {
            f = getFont();
        }

        Node.Property [] properties = new Node.Property [] {
                new PropertySupport.ReadWrite<Boolean> (
                    "enabled", // NOI18N
                    Boolean.TYPE,
                    org.openide.util.NbBundle.getMessage (ModuleNode.class, "PROP_modules_enabled"),
                    org.openide.util.NbBundle.getMessage (ModuleNode.class, "HINT_modules_enabled")
                ) {
                    public Boolean getValue () {
                        return null;
                    }

                    public void setValue (Boolean b) {
                    }

                },
                new PropertySupport.ReadOnly<String> (
                    "specificationVersion", // NOI18N
                    String.class,
                    org.openide.util.NbBundle.getMessage (ModuleNode.class, "PROP_modules_specversion"),
                    org.openide.util.NbBundle.getMessage (ModuleNode.class, "HINT_modules_specversion")
                ) {
                    public String getValue () {
                        return null;
                    }
                }
            };

        treeTableView.setProperties (properties);
        
        // perform additional preferred size computations for larger fonts
        if (f.getSize() > i.intValue()) { // NOI18N
            sizeTTVCarefully();
        } else {
            // direct sizing for default situation
            treeTableView.setPreferredSize(
                new Dimension (DEF_1_COL_WIDTH + DEF_0_COL_WIDTH + DEF_TREE_WIDTH,
                               DEF_HEIGHT)
            );
            treeTableView.setTreePreferredWidth(DEF_TREE_WIDTH);
            treeTableView.setTableColumnPreferredWidth(0, DEF_0_COL_WIDTH);
            treeTableView.setTableColumnPreferredWidth(1, DEF_1_COL_WIDTH);
        }
        treeTableView.setPopupAllowed(true);
        treeTableView.setDefaultActionAllowed(false);

        // install proper border
        treeTableView.setBorder((Border)UIManager.get("Nb.ScrollPane.border")); // NOI18N
        treeTableView.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        initAccessibility();
        
        Utilities.attachInitJob (this, new WarmupJob ());
        setWaitingState (true, false);
    }
    
    private boolean isWaiting = false;
    private Dialog d = null;
    
    public void setWaitingState (boolean wait, final boolean showProgress) {
        if (isWaiting == wait) {
            return ;
        }
        isWaiting = wait;
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                doSetWaitingState (isWaiting, showProgress);
            }
        });
    }
    
    private ChangeListener statusTextListener = null;
    
    private void doSetWaitingState (boolean wait, boolean showProgress) {
        assert SwingUtilities.isEventDispatchThread ();
        if (! this.isVisible ()) return ;
        err.log(Level.FINE,
                "Set waiting state on ModuleSelectionPanel to (wait:" + wait +
                ", showProgress: " + showProgress + ")");
        uninstallButton.setEnabled (! wait);
        if (wait) {
            if (cursor == null) cursor = getCursor();
            setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
            if (showProgress) {
                if (d == null) {
                    final ProgressHandle handle = ProgressHandleFactory.createHandle ("modules-update");
                    final ModuleUpdaterProgress progressPanel = new ModuleUpdaterProgress (handle);
                    DialogDescriptor dd = new DialogDescriptor (progressPanel,
                                            NbBundle.getMessage (ModuleUpdaterProgress.class, "CTL_ModuleUpdaterProgress_Title"), // NOI18N
                                            true, // modal
                                            new Object [0],
                                            null,
                                            DialogDescriptor.DEFAULT_ALIGN,
                                            null,
                                            null,
                                            true);
                    statusTextListener = new ChangeListener () {
                        public void stateChanged (ChangeEvent e) {
                            handle.progress (StatusDisplayer.getDefault ().getStatusText ());
                        }
                    };
                    StatusDisplayer.getDefault ().addChangeListener (statusTextListener);
                    d = DialogDisplayer.getDefault ().createDialog (dd);
                    handle.start ();
                    d.setVisible (true);
                }
            }
        } else {
            if (cursor != null) {
                setCursor (cursor);
                cursor = null;
            } else {
                setCursor (Cursor.getDefaultCursor ());
            }
            if (d != null && d.isVisible ()) {
                d.setVisible (false);
                d.dispose ();
                d = null;
                this.requestFocus ();
            }
            if (statusTextListener != null) {
                StatusDisplayer.getDefault ().removeChangeListener (statusTextListener);
            }
        }
    }
    
    public boolean isWaitingState () {
        return isWaiting;
    }
    
    private void prepareComponents () {
        this.uninstallButton.setEnabled (false);
        HTMLEditorKit htmlkit = new HTMLEditorKit ();
        // override the Swing default CSS to make the HTMLEditorKit use the
        // same font as the rest of the UI.

        // XXX the style sheet is shared by all HTMLEditorKits.  We must
        // detect if it has been tweaked by ourselves or someone else
        // (code completion javadoc popup for example) and avoid doing the
        // same thing again

        StyleSheet css = htmlkit.getStyleSheet ();

        if (css.getStyleSheets() == null) {
            StyleSheet css2 = new StyleSheet();
            Font f = treeTableView.getFont ();
            int size = treeTableView.getFont ().getSize ();
            css2.addRule(new StringBuffer("body { font-size: ").append(size) // NOI18N
                        .append("; font-family: ").append(f.getName()).append("; }").toString()); // NOI18N
            css2.addStyleSheet(css);
            htmlkit.setStyleSheet(css2);
        }

        this.description.setEditorKit (htmlkit);
        
        // #66654: use a full set of properties for modules at top level
        ModuleNode node = new ModuleNode();
        manager.setRootContext(node);

        manager.addPropertyChangeListener (this);

        Node[] kids = node.getChildren().getNodes(true);
        if (kids.length > 0) {
            try {
                manager.setSelectedNodes(new Node[] {kids[0]});
            } catch (PropertyVetoException ex) {
                assert false : ex;
            }
        }
    }
    
    /** Computes and sets right preferred sizes of TTV columns.
     * Sizes of columns are computed as maximum of default values and 
     * header text length. Size of tree is aproximate, grows linearly with
     * font size.
     */
    private void sizeTTVCarefully () {
        Font headerFont = (Font)UIManager.getDefaults().get("TableHeader.font");  // NOI18N
        Font tableFont = (Font)UIManager.getDefaults().get("Table.font");  // NOI18N
        FontMetrics headerFm = getFontMetrics(headerFont);
        
        int enabledColWidth = Math.max(DEF_0_COL_WIDTH, headerFm.stringWidth(
            NbBundle.getMessage (ModuleNode.class, "PROP_modules_enabled")) + 20
        );
        int specColWidth = Math.max(DEF_1_COL_WIDTH, headerFm.stringWidth(
            NbBundle.getMessage (ModuleNode.class, "PROP_modules_specversion")) + 20
        );
        int defFontSize = UIManager.getDefaults().getInt("nbDefaultFontSize");
        int treeWidth = DEF_TREE_WIDTH + 10 * (tableFont.getSize() - defFontSize);
        
        treeTableView.setPreferredSize(
            new Dimension (treeWidth + enabledColWidth + specColWidth,
                           DEF_HEIGHT + 10 * (tableFont.getSize() - defFontSize))
        );
        treeTableView.setTreePreferredWidth(treeWidth);
        treeTableView.setTableColumnPreferredWidth(0, enabledColWidth);
        treeTableView.setTableColumnPreferredWidth(1, specColWidth);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        modulesLabel = new javax.swing.JLabel();
        uninstallButton = new javax.swing.JButton();
        descriptionLabel = new javax.swing.JLabel();
        descriptionPane = new javax.swing.JScrollPane();
        description = new javax.swing.JEditorPane();
        modulesPane = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setName(NbBundle.getMessage(ModuleSelectionPanel.class, "LBL_ModuleSelectionPanel_Name"));
        modulesLabel.setLabelFor(modulesPane);
        org.openide.awt.Mnemonics.setLocalizedText(modulesLabel, org.openide.util.NbBundle.getBundle(ModuleSelectionPanel.class).getString("ModuleSelectionPanel_ModuleLabel_text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 11, 0, 0);
        add(modulesLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(uninstallButton, org.openide.util.NbBundle.getMessage(ModuleSelectionPanel.class, "BTN_ModuleSelectionPanel_Uninstall"));
        uninstallButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uninstallButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.15;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 0, 12);
        add(uninstallButton, gridBagConstraints);

        descriptionLabel.setLabelFor(descriptionPane);
        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(ModuleSelectionPanel.class, "LBL_ModuleSelectionPanel_descriptionLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 11, 0, 0);
        add(descriptionLabel, gridBagConstraints);

        descriptionPane.setMinimumSize(new java.awt.Dimension(400, 70));
        descriptionPane.setPreferredSize(new java.awt.Dimension(400, 70));
        description.setEditable(false);
        descriptionPane.setViewportView(description);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.85;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 11, 0, 0);
        add(descriptionPane, gridBagConstraints);

        modulesPane.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.85;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 11, 0, 0);
        add(modulesPane, gridBagConstraints);

    }//GEN-END:initComponents

    private void uninstallButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uninstallButtonActionPerformed
        ModuleNodeUtils.doUninstall (manager.getSelectedNodes ());
    }//GEN-LAST:event_uninstallButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_prevButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane description;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionPane;
    private javax.swing.JLabel modulesLabel;
    private javax.swing.JPanel modulesPane;
    private javax.swing.JButton uninstallButton;
    // End of variables declaration//GEN-END:variables

    private ExplorerManager manager;
    
    private static class ExplorerPanel extends TopComponent implements ExplorerManager.Provider {
        private ExplorerManager mgr;
        public ExplorerPanel () {
            this.mgr = new ExplorerManager ();
            ActionMap map = this.getActionMap ();
            map.put ("delete", ExplorerUtils.actionDelete(mgr, false));
            associateLookup (ExplorerUtils.createLookup (mgr, map));
        }
        
        public ExplorerManager getExplorerManager () {
            return mgr;
        }
        
        @Override
        protected void componentActivated() {
            ExplorerUtils.activateActions (mgr, true);
        }
        
        @Override
        protected void componentDeactivated() {
            ExplorerUtils.activateActions (mgr, false);
        }
    }
    
    /** Handling of property changes in node structure
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ((evt.getSource() == manager) 
            && (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()) || ExplorerManager.PROP_NODE_CHANGE.equals (evt.getPropertyName ()))) {
            
            final Node[] nodes = manager.getSelectedNodes();
            String text = ""; // NOI18N
            
            if (nodes.length == 1) {
                ModuleBean bean = nodes[0].getLookup().lookup(ModuleBean.class);
                if (bean != null) {
                    try {
                        text = "<b>" + XMLUtil.toElementContent(bean.getModule().getDisplayName()) + "</b>"; // NOI18N
                        String longDesc = bean.getLongDescription();
                        if (longDesc != null) {
                            text += "<br>" + XMLUtil.toElementContent(longDesc); // NOI18N
                        }
                    } catch (CharConversionException e) {
                        err.log(Level.WARNING, null, e);
                    }
                }
            }
            
            description.setText(text);
            description.setCaretPosition (0);
            
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    treeTableView.requestFocus ();
                    // bugfix #61904: set enabled in following AWT queue
                    uninstallButton.setEnabled (! isWaitingState () && ModuleNodeUtils.canUninstall (nodes));
                }
            });
        }
    }
    
    /** Initialize accesibility
     */
    public void initAccessibility(){

        java.util.ResourceBundle b = NbBundle.getBundle(this.getClass());
        
        this.getAccessibleContext().setAccessibleDescription(b.getString("ACSD_ModuleSelectionPanel")); // NOI18N
    }
    
    // impl of AsyncGUIJob - perf. issue 61987
    private class WarmupJob implements AsyncGUIJob {

        public void construct () {
            prepareComponents ();
        }
        
        public void finished () {
            setWaitingState (false, false);
            treeTableView.requestFocus();
        }

    }

}
