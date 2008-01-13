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
 * 
 * Contributor(s): Tom Wheeler, Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.examples.careditor.editor;

import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.pojoeditors.api.PojoEditor;

public class AlternateCarEditorForm extends javax.swing.JPanel implements DocumentListener {
    
    private static final Logger logger = Logger.getLogger(AlternateCarEditorForm.class.getName());
    
    public AlternateCarEditorForm() {
        initComponents();
        makeField.getDocument().addDocumentListener(this);
        yearField.getDocument().addDocumentListener(this);
        modelField.getDocument().addDocumentListener(this);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        makeLabel = new javax.swing.JLabel();
        makeField = new javax.swing.JTextField();
        modelLabel = new javax.swing.JLabel();
        modelField = new javax.swing.JTextField();
        yearLabel = new javax.swing.JLabel();
        yearField = new javax.swing.JFormattedTextField();
        problemLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(204, 204, 255));

        makeLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.makeLabel.text_1")); // NOI18N

        makeField.setText("null");

        modelLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.modelLabel.text_1")); // NOI18N

        modelField.setText("null");

        yearLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.yearLabel.text_1")); // NOI18N

        yearField.setText("null");

        problemLabel.setForeground(java.awt.Color.red);
        problemLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "AlternateCarEditorForm.problemLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(makeLabel)
                    .add(modelLabel)
                    .add(yearLabel))
                .add(33, 33, 33)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelField)
                    .add(makeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(problemLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(17, 17, 17)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(makeLabel)
                    .add(makeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modelField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(modelLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(yearLabel)
                    .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(problemLabel)
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField makeField;
    private javax.swing.JLabel makeLabel;
    private javax.swing.JTextField modelField;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JFormattedTextField yearField;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables
   
    boolean inInit = false;
    void set (Car car) {
        try {
            inInit = true;
            makeField.setText (car.getMake());
            modelField.setText (car.getModel());
            yearField.setText (Integer.toString(car.getYear()));
            problemLabel.setText(" ");
        } finally {
            inInit = false;
        }
    }

    public void insertUpdate(DocumentEvent e) {
        change(e);
    }

    public void removeUpdate(DocumentEvent e) {
        change(e);
    }

    public void changedUpdate(DocumentEvent e) {
        change(e);
    }
    
    private boolean inExternalChange;
    void externalChanged (Car car, String propName, Object old, Object nue) {
        if (inChange) {
            return;
        }
        inExternalChange = true;
        try {
            if (Car.PROP_MAKE.equals(propName)) {
                makeField.setText (nue == null ? "" : nue.toString());
            } else if (Car.PROP_MODEL.equals(propName)) {
                modelField.setText(nue == null ? "" : nue.toString());
            } else if (Car.PROP_YEAR.equals(propName)) {
                yearField.setText(nue == null ? "0" : nue.toString());
            }
        } finally {
            inExternalChange = false;
        }
    }
    
    private boolean inChange;
    private void change (DocumentEvent e) {
        if (inInit || inExternalChange) {
            return;
        }
        inChange = true;
        try {
            PojoEditor<Car> provider = (PojoEditor<Car>) SwingUtilities.getAncestorOfClass(PojoEditor.class, this);
            Car car = provider.getPojo();
            if (car != null) {
                if (e.getDocument() == makeField.getDocument()) {
                    car.setMake(makeField.getText());
                } else if (e.getDocument() == modelField.getDocument()) {
                    car.setModel(modelField.getText());
                } else if (e.getDocument() == yearField.getDocument()) {
                    try {
                        car.setYear (Integer.parseInt(yearField.getText()));
                        problemLabel.setText (" ");
                    } catch (NumberFormatException ex) {
                        problemLabel.setText(ex.getLocalizedMessage());
                    }
                } else {
                    throw new AssertionError (e.getDocument());
                }
            } else {
                throw new IllegalStateException ("Could not find car in " + provider);
            }
        } finally {
            inChange = false;
        }
    }
}
