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

package org.netbeans.modules.debugger.localsviewenhancements.ui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.LocalVariable;
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
public class LocalsViewActionsProviderFilter implements NodeActionsProviderFilter {
    
    public LocalsViewActionsProviderFilter() {
    }
    
    private static final Action GOTO_TYPE_ACTION = Models.createAction(
            NbBundle.getBundle(LocalsViewActionsProviderFilter.class).getString("CTL_GotoTypeAction"),
            new Models.ActionPerformer() {
        public boolean isEnabled(Object node) {
            return true;
        }
        public void perform(Object[] nodes) {
            gotoTypeOf((Variable) nodes [0]);
        }
    },
            Models.MULTISELECTION_TYPE_EXACTLY_ONE
            );
    
    private static final Action GOTO_DECLARED_TYPE_ACTION = Models.createAction(
            NbBundle.getBundle(LocalsViewActionsProviderFilter.class).getString("CTL_GotoDeclaredTypeAction"),
            new Models.ActionPerformer() {
        public boolean isEnabled(Object node) {
            return true;
        }
        public void perform(Object[] nodes) {
            gotoDeclaredTypeOf((Variable) nodes [0]);
        }
    },
            Models.MULTISELECTION_TYPE_EXACTLY_ONE
            );
    
    private static final String[] primitivesArray = new String[] {
        "boolean",
        "byte",
        "char",
        "double",
        "float",
        "int",
        "long",
        "short",
    };
    
    private static final List primitivesList = Arrays.asList(primitivesArray);
    
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        Action [] actions = original.getActions(node);
        List myActions = new ArrayList();
        if (node instanceof Variable) {
            Variable variable = (Variable) node;
            String variableType = variable.getType();
            if (!primitivesList.contains(variableType)) {
                if (variableType != null && !variableType.trim().isEmpty()) {
                    myActions.add(GOTO_TYPE_ACTION);
                }
                myActions.add(GOTO_DECLARED_TYPE_ACTION);
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
    
    private static void gotoTypeOf(Variable v) {
        String typeName = v.getType();
        
        if (typeName == null) {
            return;
        }
        
        showType(typeName);
    }
    
    private static void gotoDeclaredTypeOf(Variable v) {
        String declaredType = null;
        if (v instanceof LocalVariable) {
            declaredType = ((LocalVariable) v).getDeclaredType();
        } else if (v instanceof Field) {
            declaredType = ((Field) v).getDeclaredType();
        }
        
        if (declaredType == null) {
            return;
        }
        
        showType(declaredType);
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
