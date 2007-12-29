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

import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.dynactions.Sensor;
import org.netbeans.api.dynactions.Sensor.Notifiable;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.pojoeditors.api.PojoDataObject;
import org.netbeans.pojoeditors.api.PojoEditor;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.view.BeanTreeView;

public class CarEditorForm extends javax.swing.JPanel implements Notifiable<SaveCookie>, DocumentListener {
    private static final Logger logger = Logger.getLogger(CarEditorForm.class.getName());
    public CarEditorForm() {
        initComponents();
        makeField.getDocument().addDocumentListener(this);
        yearField.getDocument().addDocumentListener(this);
        modelField.getDocument().addDocumentListener(this);
    }
        
    Sensor<SaveCookie> sensor;
    @Override
    public void addNotify() {
        super.addNotify();
        PojoEditor<Car> provider = (PojoEditor<Car>) SwingUtilities.getAncestorOfClass(PojoEditor.class, this);
        PojoDataObject dob = provider.getLookup().lookup(PojoDataObject.class);
        Sensor.register(dob.getLookup(), SaveCookie.class, this);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        makeLabel = new javax.swing.JLabel();
        makeField = new javax.swing.JTextField();
        modelLabel = new javax.swing.JLabel();
        modelField = new javax.swing.JTextField();
        yearLabel = new javax.swing.JLabel();
        yearField = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        problemLabel = new javax.swing.JLabel();
        jScrollPane1 = new BeanTreeView();

        makeLabel.setText(org.openide.util.NbBundle.getMessage(CarEditorForm.class, "CarEditorForm.makeLabel.text_1")); // NOI18N

        makeField.setText("null");

        modelLabel.setText(org.openide.util.NbBundle.getMessage(CarEditorForm.class, "CarEditorForm.modelLabel.text_1")); // NOI18N

        modelField.setText("null");

        yearLabel.setText(org.openide.util.NbBundle.getMessage(CarEditorForm.class, "CarEditorForm.yearLabel.text_1")); // NOI18N

        yearField.setText("null");

        jButton1.setText(org.openide.util.NbBundle.getMessage(CarEditorForm.class, "CarEditorForm.jButton1.text")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        problemLabel.setForeground(java.awt.Color.red);
        problemLabel.setText(org.openide.util.NbBundle.getMessage(CarEditorForm.class, "CarEditorForm.problemLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(makeLabel)
                            .add(modelLabel)
                            .add(yearLabel))
                        .add(33, 33, 33)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(problemLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                            .add(modelField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                            .add(makeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 296, Short.MAX_VALUE)
                                .add(jButton1)))))
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
                    .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(problemLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        PojoEditor<Car> provider = (PojoEditor<Car>) SwingUtilities.getAncestorOfClass(PojoEditor.class, this);
        PojoDataObject dob = provider.getLookup().lookup(PojoDataObject.class);
        dob.discardModifications();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField makeField;
    private javax.swing.JLabel makeLabel;
    private javax.swing.JTextField modelField;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JFormattedTextField yearField;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables

    public void notify(Collection<SaveCookie> coll, Class target) {
        jButton1.setEnabled (!coll.isEmpty());
    }
    
    boolean inInit = false;
    void set (Car car) {
        try {
            inInit = true;
            makeField.setText (car.getMake());
            modelField.setText (car.getModel());
            try {
                yearField.setText (Integer.toString(car.getYear()));
                problemLabel.setText(" ");
            } catch (NumberFormatException e) {
                problemLabel.setText (e.getLocalizedMessage());
            }
            PojoEditor<Car> provider = (PojoEditor<Car>) SwingUtilities.getAncestorOfClass(PojoEditor.class, this);
            PojoDataObject dob = provider.getLookup().lookup(PojoDataObject.class);
            jButton1.setEnabled (dob.isModified());
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
    
    private void change (DocumentEvent e) {
        if (inInit) {
            return;
        }
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
        }
    }
}
