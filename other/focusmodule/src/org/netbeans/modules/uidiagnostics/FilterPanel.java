/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * FilterPanel.java
 *
 * Created on February 3, 2003, 12:41 PM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.*;
import java.awt.*;
/**
 *
 * @author  Tim Boudreau
 */
public class FilterPanel extends javax.swing.JPanel implements ActionListener, FocusListener {
    
    /** Creates new form ConfigPanel */
    public FilterPanel() {
        initComponents();
        group.add (anythingButton); 
        group.add (nullButton); 
        group.add (classNameButton);
        group.add (toStringButton);
        group.add (specificComponent);
        group.add (nonNullButton);
        DefaultComboBoxModel cbm = new DefaultComboBoxModel ();
        cbm.addElement(" old value or new value ");
        cbm.addElement(" old value ");
        cbm.addElement(" new value ");
        whichCombo.setModel (cbm);
        addListeners();
    }
    
    private void addListeners () {
        whichCombo.addActionListener (this);
        classMatchField.addActionListener (this);
        nameField.addActionListener (this);
        anythingButton.addActionListener (this);
        stackTraceBox.addActionListener (this);
        invertBox.addActionListener (this);
        classNameButton.addActionListener (this);
        nullButton.addActionListener (this);
        nameField.addActionListener (this);
        stackTraceBox.addActionListener(this);
        methodMatchBox.addActionListener(this);
        methodMatchField.addActionListener(this);
        classNameButton.addActionListener(this);
        toStringField.addActionListener(this);
        toStringButton.addActionListener(this);
        specificComponent.addActionListener(this);
        nonNullButton.addActionListener (this);
        anythingButton.setSelected(true);
        toStringField.setEnabled (false);
        methodMatchField.setEnabled(false);
        findbutton.addActionListener (this);
        
        whichCombo.addFocusListener (this);
        classMatchField.addFocusListener (this);
        nameField.addFocusListener (this);
        anythingButton.addFocusListener (this);
        stackTraceBox.addFocusListener (this);
        invertBox.addFocusListener (this);
        classNameButton.addFocusListener (this);
        nullButton.addFocusListener (this);
        nameField.addFocusListener (this);
        stackTraceBox.addFocusListener(this);
        methodMatchBox.addFocusListener(this);
        methodMatchField.addFocusListener(this);
        classNameButton.addFocusListener(this);
        toStringField.addFocusListener(this);
        toStringButton.addFocusListener(this);
        specificComponent.addFocusListener(this);
        nonNullButton.addFocusListener (this);
        anythingButton.setSelected(true);
        findbutton.addFocusListener (this);
        eventBox.addActionListener(this);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension (450, 460);
    }
    
    boolean setting=false;
    EventFilter filter=null;
    public void setFilter (EventFilter ef) {
        if (ef == filter) return;
        setting=true;
        if ((filter != ef) && (filter != null)) {
            updateFilter();
        }
        this.filter = ef;
        updateFromFilter();
        setting=false;
    }
    
    public EventFilter getFilter () {
        return filter;
    }
    
    private void updateFromFilter () {
        if (filter == null) return;
        invertBox.setSelected (filter.isInverted());
        classMatchField.setText (filter.getClassNameFilter());
        methodMatchField.setText (filter.getMethodNameFilter());
        nameField.setText (filter.getPropertyNameFilter());
        stackTraceBox.setSelected (filter.isStackTrace());
        whichCombo.setSelectedIndex(filter.getWhich());
        
        int t = filter.getType();
        anythingButton.setSelected (t == filter.TYPE_ANY);
        nullButton.setSelected (t == filter.TYPE_NULL);
        classNameButton.setSelected (t == filter.TYPE_MATCHCLASS);
        specificComponent.setSelected (t == filter.TYPE_SPECIFIC);
        nonNullButton.setSelected (t == filter.TYPE_NONNULL);
        toStringButton.setSelected (t == filter.TYPE_TOSTRING);
        
        eventBox.setSelected(filter.isShowEvent());
        methodMatchBox.setSelected (filter.isUseMethodFilter());
        filtertext.setText (filter.toString());
        
        updateEnabled();
    }
    
    private void updateFilter () {
        if (setting) return;
        if (filter == null) filter = new EventFilter();
        filter.setInverted(invertBox.isSelected());
        filter.setClassNameFilter(classMatchField.getText());
        filter.setMethodNameFilter(methodMatchField.getText());
        try {
            filter.setWhich(whichCombo.getSelectedIndex());
        } catch (java.beans.PropertyVetoException pve) { 
            // foo
        }
        filter.setPropertyNameFilter(nameField.getText());
        int type = anythingButton.isSelected() ? filter.TYPE_ANY :
            nullButton.isSelected() ? filter.TYPE_NULL : 
            nonNullButton.isSelected() ? filter.TYPE_NONNULL :
            specificComponent.isSelected() ? filter.TYPE_SPECIFIC : 
            toStringButton.isSelected() ? filter.TYPE_TOSTRING : 
            filter.TYPE_MATCHCLASS;
        filter.setType (type);
        filter.setStackTrace (stackTraceBox.isSelected());
        filter.setUseMethodFilter(methodMatchBox.isSelected());
        filtertext.setText (filter.toString());
        filter.setStringFilter(toStringField.getText());
        filter.setShowEvent(eventBox.isSelected());
        updateEnabled();
    }
    
    private void updateEnabled () {
        classMatchField.setEnabled (classNameButton.isSelected());
        methodMatchField.setEnabled (methodMatchBox.isSelected());
        findbutton.setEnabled (filter.getType() == filter.TYPE_SPECIFIC);
        toStringField.setEnabled (filter.getType() == filter.TYPE_TOSTRING);
    }
    

    public void focusLost (FocusEvent fe) {
        updateFilter();
        filtertext.setText (filter.toString());
    }
    
    public void focusGained (FocusEvent fe) {}
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == findbutton) {
            findComponent();
        }
        updateFilter();
        filtertext.setText (filter.toString());
    }
    
    private void findComponent() {
        final java.awt.Container parentDlg = this.getTopLevelAncestor();
        if (parentDlg instanceof javax.swing.JDialog == false) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }
        NotifyDescriptor nd = new NotifyDescriptor.Message (NbBundle.getMessage (FilterPanel.class, "MSG_FindComponent"));
        DialogDisplayer.getDefault().notify(nd);

        final long time = System.currentTimeMillis();
        parentDlg.setVisible (false);
        final PropertyChangeListener onceListener = new PropertyChangeListener () {
            public void propertyChange (PropertyChangeEvent pce) {
                if (System.currentTimeMillis() - time < 500) return;
                if (!(pce.getNewValue() instanceof Component)) return;
                if (pce.getPropertyName() == "focusOwner") {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener (this);
                    filter.setComponent ((Component) pce.getNewValue());
                    parentDlg.setVisible (true);
                    updateFromFilter();
                }
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener (onceListener);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        group = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        anythingButton = new javax.swing.JRadioButton();
        nullButton = new javax.swing.JRadioButton();
        classNameButton = new javax.swing.JRadioButton();
        group.add (anythingButton); group.add (nullButton); group.add (classNameButton);
        classMatchField = new javax.swing.JTextField();
        whichCombo = new javax.swing.JComboBox();
        matches = new javax.swing.JLabel();
        stackTraceBox = new javax.swing.JCheckBox();
        methodMatchBox = new javax.swing.JCheckBox();
        methodMatchField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        invertBox = new javax.swing.JCheckBox();
        filtertext = new javax.swing.JTextArea();
        nonNullButton = new javax.swing.JRadioButton();
        specificComponent = new javax.swing.JRadioButton();
        findbutton = new javax.swing.JButton();
        toStringButton = new javax.swing.JRadioButton();
        toStringField = new javax.swing.JTextField();
        eventBox = new javax.swing.JCheckBox();

        setLayout(null);

        jLabel1.setText("When");
        add(jLabel1);
        jLabel1.setBounds(10, 64, 32, 16);

        anythingButton.setMnemonic('A');
        anythingButton.setText("Anything");
        add(anythingButton);
        anythingButton.setBounds(79, 82, 75, 20);

        nullButton.setMnemonic('N');
        nullButton.setText("Null");
        add(nullButton);
        nullButton.setBounds(79, 105, 75, 20);

        classNameButton.setMnemonic('C');
        classNameButton.setText("Classname contains");
        add(classNameButton);
        classNameButton.setBounds(79, 149, 141, 20);

        add(classMatchField);
        classMatchField.setBounds(228, 150, 270, 21);

        add(whichCombo);
        whichCombo.setBounds(49, 61, 280, 20);

        matches.setText("matches");
        add(matches);
        matches.setBounds(342, 63, 375, 16);

        stackTraceBox.setText("Print a stack trace");
        add(stackTraceBox);
        stackTraceBox.setBounds(10, 284, 825, 25);

        methodMatchBox.setText("Match only if stack trace contains a method matching");
        add(methodMatchBox);
        methodMatchBox.setBounds(10, 239, 313, 25);

        add(methodMatchField);
        methodMatchField.setBounds(327, 239, 170, 21);

        jLabel3.setText("When the property name matches");
        add(jLabel3);
        jLabel3.setBounds(10, 20, 185, 16);

        nameField.setText("focusOwner");
        add(nameField);
        nameField.setBounds(208, 20, 280, 21);

        invertBox.setText("Invert this filter (display only events that don't match)");
        add(invertBox);
        invertBox.setBounds(10, 261, 825, 25);

        filtertext.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        filtertext.setEditable(false);
        filtertext.setLineWrap(true);
        filtertext.setWrapStyleWord(true);
        filtertext.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        add(filtertext);
        filtertext.setBounds(10, 340, 490, 100);

        nonNullButton.setMnemonic('o');
        nonNullButton.setText("Non-null");
        add(nonNullButton);
        nonNullButton.setBounds(79, 126, 120, 20);

        specificComponent.setMnemonic('s');
        specificComponent.setText("A specific component");
        add(specificComponent);
        specificComponent.setBounds(79, 195, 143, 20);

        findbutton.setMnemonic('F');
        findbutton.setText("Find...");
        add(findbutton);
        findbutton.setBounds(230, 200, 90, 20);

        toStringButton.setMnemonic('t');
        toStringButton.setText("toString() contains");
        add(toStringButton);
        toStringButton.setBounds(79, 172, 140, 20);

        add(toStringField);
        toStringField.setBounds(228, 173, 270, 21);

        eventBox.setText("Display the current AWT event");
        add(eventBox);
        eventBox.setBounds(10, 305, 187, 25);

    }//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton anythingButton;
    private javax.swing.JTextField classMatchField;
    private javax.swing.JRadioButton classNameButton;
    private javax.swing.JCheckBox eventBox;
    private javax.swing.JTextArea filtertext;
    private javax.swing.JButton findbutton;
    private javax.swing.ButtonGroup group;
    private javax.swing.JCheckBox invertBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel matches;
    private javax.swing.JCheckBox methodMatchBox;
    private javax.swing.JTextField methodMatchField;
    private javax.swing.JTextField nameField;
    private javax.swing.JRadioButton nonNullButton;
    private javax.swing.JRadioButton nullButton;
    private javax.swing.JRadioButton specificComponent;
    private javax.swing.JCheckBox stackTraceBox;
    private javax.swing.JRadioButton toStringButton;
    private javax.swing.JTextField toStringField;
    private javax.swing.JComboBox whichCombo;
    // End of variables declaration//GEN-END:variables
    
}
