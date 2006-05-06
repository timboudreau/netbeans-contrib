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
    
    private static final String[] primitivesArray = new String[] {
        "boolean", // NOI18N
        "byte",    // NOI18N
        "char",    // NOI18N
        "double",  // NOI18N
        "float",   // NOI18N
        "int",     // NOI18N
        "long",    // NOI18N
        "short",   // NOI18N
    };
    
    private static final List primitivesList = Arrays.asList(primitivesArray);
    
    private static final Action GOTO_TYPE_OF_THIS_ACTION = Models.createAction(
            NbBundle.getBundle(CallStackViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " of this", // NOI18N
            new Models.ActionPerformer() {
        public boolean isEnabled(Object node) {
            return true;
        }
        public void perform(Object[] nodes) {
            gotoTypeOf((CallStackFrame) nodes [0]);
        }
    },
            Models.MULTISELECTION_TYPE_EXACTLY_ONE
            );
    
    private static class GotoLocalVariableTypeAction implements Models.ActionPerformer {
        private String typeName;
        
        GotoLocalVariableTypeAction(String typeName) {
            this.typeName = typeName;
        }
        
        public boolean isEnabled(Object node) {
            return true;
        }
        
        public void perform(Object[] nodes) {
            showType(typeName);
        }
    }
    
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        Action [] actions = original.getActions(node);
        List myActions = new ArrayList();
        if (node instanceof CallStackFrame) {
            CallStackFrame callStackFrame = (CallStackFrame) node;
            This thisOfCallStackFrame = callStackFrame.getThisVariable();
            if (thisOfCallStackFrame != null) {
                if (!callStackFrame.getClassName().equals(thisOfCallStackFrame.getType())) {
                    myActions.add(GOTO_TYPE_OF_THIS_ACTION);
                }
            }
            try {
                LocalVariable[] localVariables = callStackFrame.getLocalVariables();
                ArrayList alreadyAddedTypeName = new ArrayList();
                for (int i = 0; i < localVariables.length; i++) {
                    LocalVariable localVariable = localVariables[i];
                    String typeName = localVariable.getType();
                    if (typeName != null) {
                        final String typeNameFinal = stripArray(typeName);
                        if (!primitivesList.contains(typeNameFinal) && !alreadyAddedTypeName.contains(typeNameFinal)) {
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
                        final String declaredTypeNameFinal = stripArray(declaredTypeName);
                        if (!primitivesList.contains(declaredTypeNameFinal) && !alreadyAddedTypeName.contains(declaredTypeNameFinal)) {
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
    
    private static void gotoTypeOf(CallStackFrame callStackFrame) {
        if (callStackFrame == null) {
            return;
        }
        This thisOfCallStackFrame = callStackFrame.getThisVariable();
        if (thisOfCallStackFrame != null) {
            if (!callStackFrame.getClassName().equals(thisOfCallStackFrame.getType())) {
                showType(thisOfCallStackFrame.getType());
            }
        }
    }
    
    private static void gotoTypeOf(LocalVariable localVariable) {
        String typeName = localVariable.getType();
        
        if (typeName == null) {
            return;
        }
        
        showType(typeName);
    }
    
    private static void gotoDeclaredTypeOf(LocalVariable localVariable) {
        String declaredTypeName = localVariable.getDeclaredType();
        
        if (declaredTypeName == null) {
            return;
        }
        
        showType(declaredTypeName);
    }
    
    private static void showType(String typeName) {
        if (typeName == null) {
            return;
        }
        
        typeName = stripArray(typeName);
        
        if (primitivesList.contains(typeName)) {
            return;
        }
        
        JavaClass clazz = (JavaClass) JavaModel.getDefaultExtent().getType().resolve(typeName.replace('$', '.'));
        if (clazz == null) {
            typeName = stripInner(typeName);
            Session session = DebuggerManager.getDebuggerManager().getCurrentSession();
            if (session != null) {
                DebuggerEngine debuggerEngine = session.getCurrentEngine();
                if (debuggerEngine != null) {
                    SourcePathProvider sourcePathProvider =
                            (SourcePathProvider) debuggerEngine.lookupFirst(null, SourcePathProvider.class);
                    if (sourcePathProvider != null) {
                        String url = sourcePathProvider.getURL(typeName.replace('.', '/') + ".java", true);
                        EditorContext editorContext = (EditorContext) DebuggerManager.getDebuggerManager().lookupFirst(null, EditorContext.class);
                        if (editorContext != null) {
                            editorContext.showSource(url, 1, null);
                        }
                    }
                }
            }
        } else {
            openElement(clazz);
        }
    }
    
    private static void openElement(ClassDefinition element) {
        try {
            try {
                JavaModel.getJavaRepository().beginTrans(false);
                ClassDefinition classDefinition = JMIUtils.getSourceElementIfExists((ClassDefinition) element);
                if (classDefinition != null) {
                    element = classDefinition;
                }
                JMIUtils.openElement(element);
            } finally {
                JavaModel.getJavaRepository().endTrans();
            }
        } catch (javax.jmi.reflect.InvalidObjectException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private static String stripInner(String typeName) {
        if (typeName == null) {
            return null;
        }
        
        typeName = stripArray(typeName);
        
        int dollarAt = typeName.indexOf("$");
        if (dollarAt != -1) {
            // strip inner classes
            typeName = typeName.substring(0, dollarAt);
        }
        
        return typeName;
    }
    
    private static String stripArray(String typeName) {
        if (typeName == null) {
            return null;
        }
        
        // strip array
        while (typeName.endsWith("[]")) {
            typeName = typeName.substring(0, typeName.length() - 2);
        }
        
        return typeName;
    }
}
