/*
 * ConfigPanel.java
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
        java.awt.GridBagConstraints gridBagConstraints;

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

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("When");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(jLabel1, gridBagConstraints);

        anythingButton.setText("anything");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 10);
        add(anythingButton, gridBagConstraints);

        nullButton.setText("null");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 10);
        add(nullButton, gridBagConstraints);

        classNameButton.setText("classname contains");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        add(classNameButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(classMatchField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(whichCombo, gridBagConstraints);

        matches.setText("matches");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(matches, gridBagConstraints);

        stackTraceBox.setText("Print a stack trace");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 10);
        add(stackTraceBox, gridBagConstraints);

        methodMatchBox.setText("Match only if stack trace contains a method matching");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(methodMatchBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(methodMatchField, gridBagConstraints);

        jLabel3.setText("When the property name matches");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 10);
        add(nameField, gridBagConstraints);

        invertBox.setText("Invert this filter (display only events that don't match)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(invertBox, gridBagConstraints);

        filtertext.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("control"));
        filtertext.setEditable(false);
        filtertext.setLineWrap(true);
        filtertext.setWrapStyleWord(true);
        filtertext.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filtertext, gridBagConstraints);

        nonNullButton.setText("non-null");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 10);
        add(nonNullButton, gridBagConstraints);

        specificComponent.setText("a specific component");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 20, 0);
        add(specificComponent, gridBagConstraints);

        findbutton.setText("Find...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        add(findbutton, gridBagConstraints);

        toStringButton.setText("toString() contains");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 10);
        add(toStringButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(toStringField, gridBagConstraints);

    }//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox invertBox;
    private javax.swing.JCheckBox methodMatchBox;
    private javax.swing.JLabel matches;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField methodMatchField;
    private javax.swing.JRadioButton specificComponent;
    private javax.swing.JComboBox whichCombo;
    private javax.swing.JTextArea filtertext;
    private javax.swing.JRadioButton classNameButton;
    private javax.swing.JRadioButton toStringButton;
    private javax.swing.JRadioButton nonNullButton;
    private javax.swing.JTextField classMatchField;
    private javax.swing.ButtonGroup group;
    private javax.swing.JTextField toStringField;
    private javax.swing.JCheckBox stackTraceBox;
    private javax.swing.JRadioButton nullButton;
    private javax.swing.JButton findbutton;
    private javax.swing.JRadioButton anythingButton;
    // End of variables declaration//GEN-END:variables
    
}
