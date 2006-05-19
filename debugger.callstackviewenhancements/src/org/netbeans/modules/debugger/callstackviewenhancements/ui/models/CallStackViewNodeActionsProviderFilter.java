/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import com.sun.jdi.AbsentInformationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class CallStackViewNodeActionsProviderFilter implements NodeActionsProviderFilter {
    
    public CallStackViewNodeActionsProviderFilter() {
    }
    
    private static class GotoLocalVariableTypeAction implements Models.ActionPerformer {
        private String typeName;
        
        GotoLocalVariableTypeAction(String typeName) {
            this.typeName = typeName;
        }
        
        public boolean isEnabled(Object node) {
            return true;
        }
        
        public void perform(Object[] nodes) {
            Utils.showType(typeName);
        }
    }
    
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        Action [] actions = original.getActions(node);
        List myActions = new ArrayList();
        if (node instanceof CallStackFrame) {
            CallStackFrame callStackFrame = (CallStackFrame) node;
            List alreadyAddedTypeName = new ArrayList();
            This thisOfCallStackFrame = callStackFrame.getThisVariable();
            if (thisOfCallStackFrame != null) {
                final String thisTypeNameFinal = thisOfCallStackFrame.getType();
                if (!callStackFrame.getClassName().equals(thisTypeNameFinal)) {
                    myActions.add(
                            Models.createAction(
                            NbBundle.getBundle(CallStackViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + thisTypeNameFinal + " (this)",
                            new GotoLocalVariableTypeAction(thisTypeNameFinal),
                            Models.MULTISELECTION_TYPE_EXACTLY_ONE
                            ));
                    alreadyAddedTypeName.add(thisTypeNameFinal);
                }
            }
            try {
                LocalVariable[] localVariables = callStackFrame.getLocalVariables();
                for (int i = 0; i < localVariables.length; i++) {
                    LocalVariable localVariable = localVariables[i];
                    String typeName = localVariable.getType();
                    if (typeName != null) {
                        final String typeNameFinal = Utils.stripArray(typeName);
                        if (!Utils.primitivesList.contains(typeNameFinal) && !alreadyAddedTypeName.contains(typeNameFinal)) {
                            myActions.add(
                                    Models.createAction(
                                    NbBundle.getBundle(CallStackViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + typeNameFinal,
                                    new GotoLocalVariableTypeAction(typeNameFinal),
                                    Models.MULTISELECTION_TYPE_EXACTLY_ONE
                                    )
                                    );
                            alreadyAddedTypeName.add(typeNameFinal);
                        }
                    }
                    String declaredTypeName = localVariable.getDeclaredType();
                    if (declaredTypeName != null) {
                        final String declaredTypeNameFinal = Utils.stripArray(declaredTypeName);
                        if (!Utils.primitivesList.contains(declaredTypeNameFinal) && !alreadyAddedTypeName.contains(declaredTypeNameFinal)) {
                            myActions.add(
                                    Models.createAction(
                                    NbBundle.getBundle(CallStackViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + declaredTypeNameFinal,
                                    new GotoLocalVariableTypeAction(declaredTypeNameFinal),
                                    Models.MULTISELECTION_TYPE_EXACTLY_ONE
                                    )
                                    );
                            alreadyAddedTypeName.add(declaredTypeNameFinal);
                        }
                    }
                }
            } catch (AbsentInformationException e) {
                
            }
        } else {
            return actions;
        }
        ArrayList actionToAdd = new ArrayList();
        actionToAdd.addAll(Arrays.asList(actions));
        actionToAdd.addAll(myActions);
        return (Action[]) actionToAdd.toArray(new Action [actionToAdd.size()]);
    }
    
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
        original.performDefaultAction(node);
    }
    
    public void addModelListener(ModelListener l) {
    }
    
    public void removeModelListener(ModelListener l) {
    }
}
