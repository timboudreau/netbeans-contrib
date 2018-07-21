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
 * FilterPanel.java
 *
 * Created on February 3, 2003, 12:41 PM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import javax.swing.*;
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        jLabel1.setText("When");

        anythingButton.setMnemonic('A');
        anythingButton.setText("Anything");

        nullButton.setMnemonic('N');
        nullButton.setText("Null");

        classNameButton.setMnemonic('C');
        classNameButton.setText("Classname contains");

        matches.setText("matches");

        stackTraceBox.setText("Print a stack trace");

        methodMatchBox.setText("Match only if stack trace contains a method matching");

        methodMatchField.setColumns(40);

        jLabel3.setText("When the property name matches");

        nameField.setText("focusOwner");

        invertBox.setText("Invert this filter (display only events that don't match)");

        filtertext.setEditable(false);
        filtertext.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        filtertext.setLineWrap(true);
        filtertext.setWrapStyleWord(true);
        filtertext.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(12, 5, 5, 5), javax.swing.BorderFactory.createTitledBorder("Configuration")));

        nonNullButton.setMnemonic('o');
        nonNullButton.setText("Non-null");

        specificComponent.setMnemonic('s');
        specificComponent.setText("A specific component");

        findbutton.setMnemonic('F');
        findbutton.setText("Find...");

        toStringButton.setMnemonic('t');
        toStringButton.setText("toString() contains");

        eventBox.setText("Display the current AWT event");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filtertext)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameField))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(toStringButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(specificComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(nonNullButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(nullButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(anythingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(classNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(findbutton)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(classMatchField)
                                    .addComponent(toStringField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(methodMatchBox)
                                        .addGap(18, 18, 18)
                                        .addComponent(methodMatchField))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(stackTraceBox)
                                            .addComponent(invertBox)
                                            .addComponent(eventBox))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(whichCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(matches, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(275, 275, 275)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(whichCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(matches))
                .addGap(18, 18, 18)
                .addComponent(anythingButton)
                .addGap(18, 18, 18)
                .addComponent(nullButton)
                .addGap(16, 16, 16)
                .addComponent(nonNullButton)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classNameButton)
                    .addComponent(classMatchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toStringButton)
                    .addComponent(toStringField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(specificComponent, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findbutton))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodMatchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(methodMatchBox))
                .addGap(18, 18, 18)
                .addComponent(invertBox)
                .addGap(18, 18, 18)
                .addComponent(stackTraceBox)
                .addGap(18, 18, 18)
                .addComponent(eventBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(filtertext, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    
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
