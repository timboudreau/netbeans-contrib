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

package org.netbeans.jellytools.modules.jndi;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New JNDI Context" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class NewJNDIContextDialog extends NbDialogOperator {

    /** Creates new NewJNDIContextDialog that can handle it.
     */
    public NewJNDIContextDialog() {
        super("New JNDI Context");
    }

    private JLabelOperator _lblContextLabel;
    private JLabelOperator _lblJNDIContextFactory;
    private JLabelOperator _lblJNDIInitialContext;
    private JLabelOperator _lblContextRoot;
    private JLabelOperator _lblAuthentication;
    private JLabelOperator _lblPrincipal;
    private JLabelOperator _lblCredentials;
    private JTextFieldOperator _txtContextLabel;
    private JComboBoxOperator _cboJNDIContextFactory;
    public static final String ITEM_COMSUNJNDINISNISCTXFACTORY = "com.sun.jndi.nis.NISCtxFactory"; 
    public static final String ITEM_COMSUNJNDILDAPLDAPCTXFACTORY = "com.sun.jndi.ldap.LdapCtxFactory"; 
    public static final String ITEM_COMSUNJNDIFSCONTEXTREFFSCONTEXTFACTORY = "com.sun.jndi.fscontext.RefFSContextFactory"; 
    public static final String ITEM_COMSUNJNDIRMIREGISTRYREGISTRYCONTEXTFACTORY = "com.sun.jndi.rmi.registry.RegistryContextFactory"; 
    public static final String ITEM_COMSUNJNDICOSNAMINGCNCTXFACTORY = "com.sun.jndi.cosnaming.CNCtxFactory"; 
    private JTextFieldOperator _txtJNDIInitialContext;
    private JTextFieldOperator _txtContextRoot;
    private JTextFieldOperator _txtAuthentication;
    private JTextFieldOperator _txtPrincipal;
    private JTextFieldOperator _txtCredentials;
    private JLabelOperator _lblOtherProperties;
    private JListOperator _lstOtherProperties;
    private JButtonOperator _btAdd;
    private JButtonOperator _btEdit;
    private JButtonOperator _btRemove;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Context Label:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblContextLabel() {
        if (_lblContextLabel==null) {
            _lblContextLabel = new JLabelOperator(this, "Context Label:");
        }
        return _lblContextLabel;
    }

    /** Tries to find "JNDI Context Factory:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJNDIContextFactory() {
        if (_lblJNDIContextFactory==null) {
            _lblJNDIContextFactory = new JLabelOperator(this, "JNDI Context Factory:");
        }
        return _lblJNDIContextFactory;
    }

    /** Tries to find "JNDI Initial Context:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJNDIInitialContext() {
        if (_lblJNDIInitialContext==null) {
            _lblJNDIInitialContext = new JLabelOperator(this, "JNDI Initial Context:");
        }
        return _lblJNDIInitialContext;
    }

    /** Tries to find "Context Root:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblContextRoot() {
        if (_lblContextRoot==null) {
            _lblContextRoot = new JLabelOperator(this, "Context Root:");
        }
        return _lblContextRoot;
    }

    /** Tries to find "Authentication:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAuthentication() {
        if (_lblAuthentication==null) {
            _lblAuthentication = new JLabelOperator(this, "Authentication:");
        }
        return _lblAuthentication;
    }

    /** Tries to find "Principal:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPrincipal() {
        if (_lblPrincipal==null) {
            _lblPrincipal = new JLabelOperator(this, "Principal:");
        }
        return _lblPrincipal;
    }

    /** Tries to find "Credentials:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCredentials() {
        if (_lblCredentials==null) {
            _lblCredentials = new JLabelOperator(this, "Credentials:");
        }
        return _lblCredentials;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtContextLabel() {
        if (_txtContextLabel==null) {
            _txtContextLabel = new JTextFieldOperator(this);
        }
        return _txtContextLabel;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboJNDIContextFactory() {
        if (_cboJNDIContextFactory==null) {
            _cboJNDIContextFactory = new JComboBoxOperator(this);
        }
        return _cboJNDIContextFactory;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtJNDIInitialContext() {
        if (_txtJNDIInitialContext==null) {
            _txtJNDIInitialContext = new JTextFieldOperator(this, 2);
        }
        return _txtJNDIInitialContext;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtContextRoot() {
        if (_txtContextRoot==null) {
            _txtContextRoot = new JTextFieldOperator(this, 3);
        }
        return _txtContextRoot;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtAuthentication() {
        if (_txtAuthentication==null) {
            _txtAuthentication = new JTextFieldOperator(this, 4);
        }
        return _txtAuthentication;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPrincipal() {
        if (_txtPrincipal==null) {
            _txtPrincipal = new JTextFieldOperator(this, 5);
        }
        return _txtPrincipal;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCredentials() {
        if (_txtCredentials==null) {
            _txtCredentials = new JTextFieldOperator(this, 6);
        }
        return _txtCredentials;
    }

    /** Tries to find "Other Properties:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblOtherProperties() {
        if (_lblOtherProperties==null) {
            _lblOtherProperties = new JLabelOperator(this, "Other Properties:");
        }
        return _lblOtherProperties;
    }

    /** Tries to find null JList in this dialog.
     * @return JListOperator
     */
    public JListOperator lstOtherProperties() {
        if (_lstOtherProperties==null) {
            _lstOtherProperties = new JListOperator(this);
        }
        return _lstOtherProperties;
    }

    /** Tries to find "Add..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAdd() {
        if (_btAdd==null) {
            _btAdd = new JButtonOperator(this, "Add...");
        }
        return _btAdd;
    }

    /** Tries to find "Edit... " JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btEdit() {
        if (_btEdit==null) {
            _btEdit = new JButtonOperator(this, "Edit... ");
        }
        return _btEdit;
    }

    /** Tries to find "Remove" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btRemove() {
        if (_btRemove==null) {
            _btRemove = new JButtonOperator(this, "Remove");
        }
        return _btRemove;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtContextLabel
     * @return String text
     */
    public String getContextLabel() {
        return txtContextLabel().getText();
    }

    /** sets text for txtContextLabel
     * @param text String text
     */
    public void setContextLabel(String text) {
        txtContextLabel().setText(text);
    }

    /** types text for txtContextLabel
     * @param text String text
     */
    public void typeContextLabel(String text) {
        txtContextLabel().typeText(text);
    }

    /** returns selected item for cboJNDIContextFactory
     * @return String item
     */
    public String getSelectedJNDIContextFactory() {
        return cboJNDIContextFactory().getSelectedItem().toString();
    }

    /** selects item for cboJNDIContextFactory
     * @param item String item
     */
    public void selectJNDIContextFactory(String item) {
        cboJNDIContextFactory().selectItem(item);
    }

    /** types text for cboJNDIContextFactory
     * @param text String text
     */
    public void typeJNDIContextFactory(String text) {
        cboJNDIContextFactory().typeText(text);
    }

    /** gets text for txtJNDIInitialContext
     * @return String text
     */
    public String getJNDIInitialContext() {
        return txtJNDIInitialContext().getText();
    }

    /** sets text for txtJNDIInitialContext
     * @param text String text
     */
    public void setJNDIInitialContext(String text) {
        txtJNDIInitialContext().setText(text);
    }

    /** types text for txtJNDIInitialContext
     * @param text String text
     */
    public void typeJNDIInitialContext(String text) {
        txtJNDIInitialContext().typeText(text);
    }

    /** gets text for txtContextRoot
     * @return String text
     */
    public String getContextRoot() {
        return txtContextRoot().getText();
    }

    /** sets text for txtContextRoot
     * @param text String text
     */
    public void setContextRoot(String text) {
        txtContextRoot().setText(text);
    }

    /** types text for txtContextRoot
     * @param text String text
     */
    public void typeContextRoot(String text) {
        txtContextRoot().typeText(text);
    }

    /** gets text for txtAuthentication
     * @return String text
     */
    public String getAuthentication() {
        return txtAuthentication().getText();
    }

    /** sets text for txtAuthentication
     * @param text String text
     */
    public void setAuthentication(String text) {
        txtAuthentication().setText(text);
    }

    /** types text for txtAuthentication
     * @param text String text
     */
    public void typeAuthentication(String text) {
        txtAuthentication().typeText(text);
    }

    /** gets text for txtPrincipal
     * @return String text
     */
    public String getPrincipal() {
        return txtPrincipal().getText();
    }

    /** sets text for txtPrincipal
     * @param text String text
     */
    public void setPrincipal(String text) {
        txtPrincipal().setText(text);
    }

    /** types text for txtPrincipal
     * @param text String text
     */
    public void typePrincipal(String text) {
        txtPrincipal().typeText(text);
    }

    /** gets text for txtCredentials
     * @return String text
     */
    public String getCredentials() {
        return txtCredentials().getText();
    }

    /** sets text for txtCredentials
     * @param text String text
     */
    public void setCredentials(String text) {
        txtCredentials().setText(text);
    }

    /** types text for txtCredentials
     * @param text String text
     */
    public void typeCredentials(String text) {
        txtCredentials().typeText(text);
    }

    /** clicks on "Add..." JButton
     */
    public void add() {
        btAdd().push();
    }

    /** clicks on "Edit... " JButton
     */
    public void edit() {
        btEdit().push();
    }

    /** clicks on "Remove" JButton
     */
    public void remove() {
        btRemove().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NewJNDIContextDialog by accessing all its components.
     */
    public void verify() {
        lblContextLabel();
        lblJNDIContextFactory();
        lblJNDIInitialContext();
        lblContextRoot();
        lblAuthentication();
        lblPrincipal();
        lblCredentials();
        txtContextLabel();
        cboJNDIContextFactory();
        txtJNDIInitialContext();
        txtContextRoot();
        txtAuthentication();
        txtPrincipal();
        txtCredentials();
        lblOtherProperties();
        lstOtherProperties();
        btAdd();
        btEdit();
        btRemove();
    }

    /** Performs simple test of NewJNDIContextDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NewJNDIContextDialog().verify();
        System.out.println("NewJNDIContextDialog verification finished.");
    }
}

