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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.util.Vector;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.windows.WindowManager;
import org.openide.nodes.Node;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.widgets.property.JoinNode;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.sql.framework.ui.graph.IOperatorXmlInfoModel;
import org.netbeans.modules.sql.framework.ui.view.IGraphViewContainer;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderView;

import org.openide.DialogDescriptor;
/**
 *
 * @author Nithya
 */
public class JoinConditionCustomEditor extends PropertyEditorSupport implements ExPropertyEditor, Serializable {
    
    private PropertyEnv env;
    
    protected Vector<PropertyChangeListener> mListeners;
    
    private MashupDataObject mObj;
    
    private SQLJoinOperator joinOp;
    
    private ConditionBuilderView conditionView;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public JoinConditionCustomEditor() {
        initializeDataObject();
    }
    
    /**
     * Describe <code>supportsCustomEditor</code> method here.
     *
     * @return a <code>boolean</code> value
     */
    public boolean supportsCustomEditor() {
        return true;
    }
    
    public void attachEnv(PropertyEnv env) {
        this.env = env;
    }
    
    public PropertyEnv getEnv(){
        return env;
    }
    
    @Override
    public void setSource(Object source) {
        if(source instanceof SQLJoinOperator) {
            this.joinOp = (SQLJoinOperator) source;
        }
    }
    
    public String getJavaInitializationString(){
        return this.joinOp.getJoinCondition().getConditionText();
    }
    
    @Override
    public Object getValue() {
        return this.joinOp.getJoinCondition().getConditionText();
    }
    
    @Override
    public void setValue(Object value) {
        value =  this.joinOp.getJoinCondition().getConditionText();
        firePropertyChange();
    }
    
    @Override
    public String getAsText() {
        if(this.joinOp != null) {
            return this.joinOp.getJoinCondition().getConditionText();
            
        }
        return "<NO CONDITION DEFINED>";
    }
    
    @Override
    public void setAsText(String text) {
        this.joinOp.getJoinCondition().setConditionText(text);
        
        firePropertyChange();
    }
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        if (mListeners == null) {
            mListeners = new Vector<PropertyChangeListener>();
        }
        mListeners.addElement(listener);
        env.addPropertyChangeListener(listener);
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.removeElement(listener);
        env.removePropertyChangeListener(listener);
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public Component getCustomEditor(){
        if(mObj == null || joinOp == null) {
            initializeDataObject();
        }
        return conditionView;
    }
    
    private void initializeDataObject() {
        Node[] nodes = WindowManager.getDefault().getRegistry().getActivatedNodes();
        for(Node node : nodes) {
            if(node instanceof JoinNode) {
                this.joinOp = ((JoinNode)node).getJoinOperator();
                this.mObj = ((JoinNode)node).getMashupDataObject();
                break;
            }
        }
        conditionView = new ConditionBuilderView((IGraphViewContainer)mObj.
                getEditorView().getGraphView().getGraphViewContainer(),
                joinOp.getAllSourceTables(), joinOp.getJoinCondition(),
                IOperatorXmlInfoModel.CATEGORY_FILTER);
        
    }
}