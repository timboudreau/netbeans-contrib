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

/*
 * FileName.java
 *
 * Created on November 8, 2000, 5:30 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import java.util.ResourceBundle;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import org.openide.*;
import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.util.NbBundle;

/**
 *
 * @author  Tomas Zezula
 * @version
 */
public class FileName extends javax.swing.JPanel implements java.beans.PropertyChangeListener, DataFilter, java.beans.VetoableChangeListener {
    
    private BeanTreeView btv;

    /** Creates new form FileName */
    public FileName() {
        initComponents ();
        postInitComponents();
    }
    
    public String getName () {
        return this.name.getText();
    }
    
    
    public void setName (String name) {
        this.name.setText (name);
        this.name.setSelectionStart(0);
        this.name.setSelectionEnd (name.length());
    }
    
    public DataObject getPackage () {
        Node[] nodes = this.explorer.getExplorerManager().getSelectedNodes();
        if (nodes != null && nodes.length == 1) 
            return (DataFolder) nodes[0].getCookie (DataFolder.class);
        else
            return null;
    }
    
    public void setPackage (Node node) {
        try {
            this.explorer.getExplorerManager().setSelectedNodes (new Node[]{node});
        }catch (java.beans.PropertyVetoException pve) {}
    }
    
    private void postInitComponents () {
        this.pkgName.addFocusListener ( new FocusListener () {
            
            public void focusGained (FocusEvent event) {
                ((JTextField)event.getSource()).selectAll();
            }
            
            public void focusLost (FocusEvent event) {
            }
        });
        this.btv = new BeanTreeView();
        this.explorer.add (btv);
        ExplorerManager mgr = this.explorer.getExplorerManager();
        mgr.setRootContext (TopManager.getDefault().getPlaces().nodes().repository(this));
        mgr.addPropertyChangeListener (this);
        mgr.addVetoableChangeListener (this);
        ResourceBundle b = NbBundle.getBundle (FileName.class);
        this.jLabel1.setDisplayedMnemonic (b.getString ("TXT_NewFileName_MNE").charAt(0));
        this.pkg.setDisplayedMnemonic (b.getString("TXT_Package_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (b.getString("AD_FileName"));
        this.pkgName.getAccessibleContext().setAccessibleDescription (b.getString("AD_Package"));
        this.name.getAccessibleContext().setAccessibleDescription (b.getString("AD_NewFileName"));
        this.explorer.getAccessibleContext().setAccessibleName (b.getString("AN_PackageChooser"));
        this.explorer.getAccessibleContext().setAccessibleDescription (b.getString("AD_PackageChooser"));
    }
    
    public boolean acceptDataObject (DataObject obj) {
        FileObject fobj = obj.getPrimaryFile();
        if (obj.isValid() && fobj.isFolder())
            return true;
        else
            return false;
    }
    
    
    public void propertyChange (java.beans.PropertyChangeEvent event) {
        try {
            Node[] nodes = (Node[])event.getNewValue();
            if (nodes == null || nodes.length !=1)
                return;
            Node node = nodes[0];
                String selection ="";
                while (true) {
                    Node parent = node.getParentNode();
                    if (parent == null || parent == TopManager.getDefault().getPlaces().nodes().repository(this)){
                        String path = node.getDisplayName()+"/" + selection.replace('.','/');
                        if (selection.endsWith ("."))
                            selection = selection.substring (0, selection.length() -1);
                        selection = java.text.MessageFormat.format("{0} [{1}]", new Object[] {selection,path});
                        break;
                    }
                    selection = node.getDisplayName()+"."+selection;
                    node = parent;
                }

            this.pkgName.setText(selection);
        }catch (ClassCastException cce) {}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        explorer = new org.openide.explorer.ExplorerPanel();
        pkg = new javax.swing.JLabel();
        pkgName = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(400, 320));
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/ioranalyzer/Bundle").getString("TXT_NewFileName"));
        jLabel1.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 4);
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 8);
        add(name, gridBagConstraints);

        explorer.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/ioranalyzer/Bundle").getString("TIP_PackageBrowser"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        add(explorer, gridBagConstraints);

        pkg.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/ioranalyzer/Bundle").getString("TXT_Package"));
        pkg.setLabelFor(pkgName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 4);
        add(pkg, gridBagConstraints);

        pkgName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 8, 8);
        add(pkgName, gridBagConstraints);

    }//GEN-END:initComponents

    public void vetoableChange(final java.beans.PropertyChangeEvent event) throws java.beans.PropertyVetoException {
        try {
            Node[] nodes = (Node[]) event.getNewValue();
            if (nodes == null || nodes.length!= 1)
                throw new java.beans.PropertyVetoException ("",event);
            if (nodes[0] == this.explorer.getExplorerManager().getRootContext())
                throw new java.beans.PropertyVetoException ("",event);
        }catch (ClassCastException cce) {}
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField pkgName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField name;
    private javax.swing.JLabel pkg;
    private org.openide.explorer.ExplorerPanel explorer;
    // End of variables declaration//GEN-END:variables

}
