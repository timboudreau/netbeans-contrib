/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.panels;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.StringTokenizer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentListener;
import org.openide.TopManager;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.filesystems.FileObject;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.wizard.CorbaWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.IDLDataObject;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  tzezula
 * @version
 */
public class PackagePanel extends AbstractCORBAWizardPanel implements PropertyChangeListener, VetoableChangeListener, DataFilter, DocumentListener {
    
    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;
    
    private Node root;
    private boolean validIdlName = false;
    private BeanTreeView tv;
    
    /** Creates new form PackagePanel */
    public PackagePanel() {
        initComponents ();
        tv = new BeanTreeView();
        tv.setPopupAllowed (false);
        tv.setDefaultActionAllowed (false);
        this.tree.setMinimumSize ( new Dimension (400,250));
        this.tree.setPreferredSize ( new Dimension (400,250));
        this.tree.setBorder (new EtchedBorder());
        this.tree.add (tv, BorderLayout.CENTER);
        this.root = TopManager.getDefault().getPlaces().nodes().repository(this);
        this.tree.getExplorerManager().setRootContext (this.root);
        this.tree.getExplorerManager().addPropertyChangeListener (this);
        this.tree.getExplorerManager().addVetoableChangeListener (this);
        this.idlName.getDocument().addDocumentListener(this);
        putClientProperty(CorbaWizard.PROP_AUTO_WIZARD_STYLE, new Boolean(true));
        putClientProperty(CorbaWizard.PROP_CONTENT_DISPLAYED, new Boolean(true));
        putClientProperty(CorbaWizard.PROP_CONTENT_NUMBERED, new Boolean(true));
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(0));
        putClientProperty(CorbaWizard.PROP_CONTENT_DATA, new String[] {
            bundle.getString("TXT_Source"),
            bundle.getString("TXT_AppComponents"),
            bundle.getString("TXT_ORBSettings"),
            bundle.getString("TXT_RootInterface"),
            bundle.getString("TXT_BindingDetails"),
            bundle.getString("TXT_Finish")
        });
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_Package" ));
        
        this.tree.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_PackageChooser"));
        this.jLabel3.setDisplayedMnemonic (this.bundle.getString("TXT_Name_MNE").charAt(0));
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_FileSystems_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription(this.bundle.getString ("AD_PackagePanel"));
        this.fireChange(this);
    }
    
    public void readCorbaSettings (CorbaWizardData data) {
        DataObject obj = data.getIdlSource();
        if (obj == null)
            return;
        String resourcePath = obj.getName();
        obj = obj.getFolder();
        while (obj != null) {
            resourcePath = obj.getName()+"."+resourcePath;
            obj = obj.getFolder();
        }
        if (resourcePath.charAt(0)=='.')
            resourcePath=resourcePath.substring(1);
        this.idlName.setText (resourcePath);
    }
    
    public void storeCorbaSettings (CorbaWizardData data) {
        if (this.validIdlName) {
            String resource = this.idlName.getText().replace ('.','/');  // No I18N
            resource = resource + ".idl";   // No I18N
            TopManager tm = TopManager.getDefault();
            Repository rep = tm.getRepository();
            FileObject fo = rep.findResource (resource);
            try {
                DataObject dobj = DataObject.find (fo);
                if (dobj instanceof IDLDataObject)
                    data.setIdlSource ((IDLDataObject)dobj);
            }catch (org.openide.loaders.DataObjectNotFoundException donf) {
                data.setIdlSource (null);
            }
        }
    }
    
    public boolean isValid () {
        if (DEBUG)
            System.out.println("Is valid= " + validIdlName);
        return (this.idlName.getText().length()>0 && validIdlName);
    }
    
    public void propertyChange (PropertyChangeEvent event) {
        Object newValue = event.getNewValue();
        if (newValue != null && (newValue instanceof Node[])){
            Node[] nodes = (Node[]) newValue;
            if (nodes.length == 1) {
                Node node = nodes[0];
                String selection ="";
                while (true) {
                    Node parent = node.getParentNode();
                    if (parent == null || parent == this.root){
                        if (selection.endsWith ("."))
                            selection = selection.substring (0, selection.length() -1);
                        break;
                    }
                    selection = node.getDisplayName()+"."+selection;
                    node = parent;
                }
                this.idlName.setText (selection);
                this.checkFileNameValidity (false);
            }
        }
    }
    
    public void vetoableChange (PropertyChangeEvent event) throws PropertyVetoException {
        Object newValue = event.getNewValue();
        if (newValue != null && (newValue instanceof Node[])){
            Node[] nodes = (Node[]) newValue;
            if (nodes.length != 1)
                throw new PropertyVetoException ("", event);
        }
    }
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tree = new org.openide.explorer.ExplorerPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        idlName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        setMinimumSize(new java.awt.Dimension(480, 320));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(tree, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jLabel3.setText(bundle.getString("TXT_Name"));
        jLabel3.setLabelFor(idlName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel3, gridBagConstraints);

        idlName.setToolTipText(bundle.getString("TIP_NameOfIdlFile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 6, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(idlName, gridBagConstraints);

        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_FileSystems"));
        jLabel1.setName("null");
        jLabel1.setLabelFor(tree);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jLabel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.ExplorerPanel tree;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField idlName;
    // End of variables declaration//GEN-END:variables
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
    
    
    /** Should the data object be displayed or not?
     * @param obj the data object
     * @return <CODE>true</CODE> if the object should be displayed,
     *    <CODE>false</CODE> otherwise
     */
    public boolean acceptDataObject (DataObject obj) {
        FileObject fobj = obj.getPrimaryFile();
        if (obj.isValid() && (fobj.isData() && fobj.getExt().equals("idl"))||(fobj.isFolder())) // No I18N
            return true;
        else
            return false;
    }
    
    private void checkFileNameValidity (boolean select) {
        try {
            String resource = this.idlName.getText().replace ('.','/');  // No I18N
            resource = resource + ".idl";   // No I18N
            TopManager tm = TopManager.getDefault();
            Repository rep = tm.getRepository();
            final FileObject fo = rep.findResource (resource);
            validIdlName = (fo != null);
            if (select && fo != null) {
                RequestProcessor.postRequest(new Runnable () {
                    public void run() {
                        selectPath(fo);
                    }
                });
            }
        }
        catch (Exception e) {
        }
        finally {
            this.fireChange (this);
        }
    }
    
    
    public void removeUpdate(final javax.swing.event.DocumentEvent event) {
        checkFileNameValidity (true);
    }
    
    public void changedUpdate(final javax.swing.event.DocumentEvent event) {
        checkFileNameValidity (true);
    }
    
    public void insertUpdate(final javax.swing.event.DocumentEvent event) {
        checkFileNameValidity (true);
    }
    
    private void selectPath(FileObject fo) {
        this.tree.getExplorerManager().removePropertyChangeListener (this);
        try {
            StringTokenizer packageName = new StringTokenizer(fo.getPackageName('.'), ".");
            Node node = this.root.getChildren().findChild(fo.getFileSystem().getSystemName());
            while (packageName.hasMoreTokens()) {
                node = node.getChildren().findChild(packageName.nextToken());
            }
            this.tree.getExplorerManager().setSelectedNodes (new Node[]{node});
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        this.tree.getExplorerManager().addPropertyChangeListener (this);
    }
}
