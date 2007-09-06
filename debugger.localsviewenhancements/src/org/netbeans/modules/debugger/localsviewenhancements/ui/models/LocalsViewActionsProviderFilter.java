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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.localsviewenhancements.ui.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.swing.*;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.UiUtils;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
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
                if (variableType != null && variableType.trim().length() > 0) {
                    myActions.add(GOTO_TYPE_ACTION);
                }
                if (variable instanceof This) {
                    // actual type is same as declared type
                } else if (variable instanceof LocalVariable) {
                    String declaredType = ((LocalVariable) variable).getDeclaredType();
                    if (!declaredType.equals(variableType)) {
                        myActions.add(GOTO_DECLARED_TYPE_ACTION);
                    }
                } else if (variable instanceof Field) {
                    String declaredType = ((Field) variable).getDeclaredType();
                    if (!declaredType.equals(variableType)) {
                        myActions.add(GOTO_DECLARED_TYPE_ACTION);
                    }
                }
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
        
        final String finalTypeName = typeName.replace('$', '.');
        
        typeName = stripInner(typeName);
        
        typeName = typeName.replace('.', '/') + ".java";
        
        DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
        DebuggerEngine debuggerEngine = debuggerManager.getCurrentEngine();
        List sourcePathProviders = debuggerEngine.lookup(null, SourcePathProvider.class);
        
        String url = null;
        int count = sourcePathProviders.size();
        for (int i = 0; i < count; i++) {
            SourcePathProvider sourcePathProvider = (SourcePathProvider) sourcePathProviders.get(i);
            url = sourcePathProvider.getURL(typeName, false);
            if (url == null) {
                url = sourcePathProvider.getURL(typeName, true);
            }
            if (url != null) {
                break;
            }
        }
        
        if (url != null) {
            try         {
                FileObject fileObject = URLMapper.findFileObject(new java.net.URL(url));
                if (fileObject != null) {
                    JavaSource javaSource = JavaSource.forFileObject(fileObject);
                    if (javaSource != null) {
                         try {
                            javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                                public void cancel() {
                                }

                                public void run(CompilationController compilationController)
                                    throws Exception {
                                    compilationController.toPhase(Phase.RESOLVED);
                                    TypeElement typeElement = compilationController.getElements().getTypeElement(finalTypeName);
                                    if (typeElement != null) {
                                        UiUtils.open(compilationController.getClasspathInfo(), typeElement);
                                    }
                                }
                            }, true);                                                    
                        } catch (IOException ex) {
                        }
                        return;
                    }
                }
            }
            catch (MalformedURLException ex) {
                
            }
            EditorContext editorContext = (EditorContext) debuggerManager.lookupFirst(null, EditorContext.class);

            if (editorContext != null) {
                editorContext.showSource(url, 1, null);
            }
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
