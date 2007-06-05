/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.edm.editor.widgets.property.editor;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.Object;
import java.lang.Object;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Nithya
 */
public class JoinTypeCustomEditor extends PropertyEditorSupport implements
        ExPropertyEditor, InplaceEditor.Factory  {
    
    private InplaceEditor ed = null;
    
    public JoinTypeCustomEditor() {
    }
    
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }
    
    private PropertyEnv env;
    
    public InplaceEditor getInplaceEditor() {
        if (ed == null){
            ed = (InplaceEditor) new InPlaceTextField();
        }
        return ed;
    }
    
    private static class InPlaceTextField implements InplaceEditor{
        private JComboBox combo;
        private java.beans.PropertyEditor editor = null;
        public InPlaceTextField() {
            initialize();
        }
        
        public void connect(PropertyEditor pe, PropertyEnv env){
            editor = pe;
            reset();
        }
        
        public JComponent getComponent() {
            if(combo == null) {
                initialize();
            }
            return combo;
        }
        
        public void clear() {
            editor = null;
            model = null;
            combo = null;
        }
        
        public Object getValue() {
            return combo.getSelectedItem();
        }
        
        public void setValue(Object object) {
            combo.setSelectedItem(object);
        }
        
        public boolean supportsTextEntry() {
            return false;
        }
        
        public void reset() {
        }
        
        private void initialize() {
            combo = new JComboBox();
            combo.addItem("INNER JOIN");
            combo.addItem("LEFT OUTER JOIN");
            combo.addItem("RIGHT OUTER JOIN");
            combo.addItem("FULL OUTER JOIN");
        }
        
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }
        
        public PropertyEditor getPropertyEditor() {
            return editor;
        }
        
        public PropertyModel getPropertyModel() {
            return  model;
        }
        
        private PropertyModel model;
        
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }
        
        public boolean isKnownComponent(Component component) {
            return component == combo;
        }
        
        public void addActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }
        
        public void removeActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }
    }
}