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

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.pojoeditors.api.PojoEditor;

public class AlternateCarEditorForm extends javax.swing.JPanel {
    
    private static final Logger logger = Logger.getLogger(AlternateCarEditorForm.class.getName());
    
    // TODO: JGoodies validation

    // no need to worry about serialization or transient fields, at least when
    // used in our TopComponent, because it does its own serialization via the
    // Stub inner class.  it does not try to save the current editor form instance;
    //instead it creates a new one each time.
    private PresentationModel presentationModel;
    
    public AlternateCarEditorForm() {
        initComponents();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        initBinding();
        System.err.println("Added to editor: " + this);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (presentationModel != null) {
            uninitBinding();
        }
    }
    
    // called from TC
    void uninitBinding() {
        logger.info("uninitBinding is underway");

        assert(presentationModel != null);

        presentationModel.setBean(null);
        presentationModel = null;

        makeField.setText(null);
        modelField.setText(null);
        yearField.setText(null);

        logger.info("Unbound Car");
    }
    
    // Called from TC Hook JGoodies data binding up to matisse-generated code.
    void initBinding() {
        PojoEditor<Car> provider = (PojoEditor<Car>) SwingUtilities.getAncestorOfClass(PojoEditor.class, this);
        Car car = provider.getPojo();
        assert car != null;
        logger.info("initBinding is underway for " + car);
        
        presentationModel = new PresentationModel(car);

// You have to use a JFormattedTextField for numeric values. There is no way around it.
        Bindings.bind(yearField, presentationModel.getModel(Car.PROP_YEAR));
        Bindings.bind(makeField, presentationModel.getModel(Car.PROP_MAKE));
        Bindings.bind(modelField, presentationModel.getModel(Car.PROP_MODEL));

        logger.info("Bound Car");
    }    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        makeLabel = new javax.swing.JLabel();
        makeField = new javax.swing.JTextField();
        modelLabel = new javax.swing.JLabel();
        modelField = new javax.swing.JTextField();
        yearLabel = new javax.swing.JLabel();
        yearField = new javax.swing.JFormattedTextField();

        setBackground(new java.awt.Color(204, 204, 255));

        makeLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.makeLabel.text_1")); // NOI18N

        makeField.setText("null");

        modelLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.modelLabel.text_1")); // NOI18N

        modelField.setText("null");

        yearLabel.setText(org.openide.util.NbBundle.getMessage(AlternateCarEditorForm.class, "CarEditorForm.yearLabel.text_1")); // NOI18N

        yearField.setText("null");

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
                    .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(40, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField makeField;
    private javax.swing.JLabel makeLabel;
    private javax.swing.JTextField modelField;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JFormattedTextField yearField;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables

}
