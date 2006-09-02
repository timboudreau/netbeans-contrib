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

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.modules.debugger.callstackviewenhancements.ui.ResizablePanel;
import org.netbeans.modules.debugger.callstackviewenhancements.ui.TableSorter;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
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
    
    private static Rectangle bounds = new Rectangle(100, 100, 900, 700);
    
    private static class ShowClassesAction implements Models.ActionPerformer {
        private VirtualMachine virtualMachine;
        private ThreadReference threadReference;
        
        ShowClassesAction(VirtualMachine virtualMachine, ThreadReference threadReference) {
            this.virtualMachine = virtualMachine;
            this.threadReference = threadReference ;
        }
        
        public boolean isEnabled(Object node) {
            return true;
        }
        
        public void perform(Object[] nodes) {
            TableSorter tableSorter = new TableSorter(new ClassesTableModel(virtualMachine.allClasses(), threadReference));
            tableSorter.setColumnComparator(String.class, tableSorter.COMPARABLE_COMAPRATOR);
            tableSorter.setSortingStatus(0, TableSorter.ASCENDING);
            JTable classesTable = new JTable(tableSorter);
            tableSorter.setTableHeader(classesTable.getTableHeader());
            final JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(), "", false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setUndecorated(true);
            dialog.setContentPane(new ResizablePanel(new JScrollPane(classesTable), "Classes"));
            dialog.setBounds(bounds);
            dialog.setVisible(true);
            
            dialog.addWindowListener(new WindowAdapter() {
                public void windowDeactivated(WindowEvent e) {
                    hide(dialog);
                }
            });
            
            dialog.getRootPane().registerKeyboardAction(
                    new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    hide(dialog);
                }
            },
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }
        
        private void hide(JDialog dialog) {
            bounds = dialog.getBounds();
            dialog.setVisible(false);
        }
    }
    
    private static class ClassesTableModel extends AbstractTableModel {
        private List classes = new ArrayList();
        private ThreadReference theThreadReference;
        
        ClassesTableModel(List classesList, ThreadReference threadReference) {
            this.theThreadReference = threadReference;
            
            JPDADebugger jpdaDebugger = null;
            DebuggerEngine debuggerEngine = DebuggerManager.getDebuggerManager().getCurrentEngine();
            if (debuggerEngine != null) {
                jpdaDebugger = (JPDADebugger) debuggerEngine.lookupFirst(null, JPDADebugger.class);
            }
            
            for (Iterator it = classesList.iterator(); it.hasNext();) {
                ReferenceType referenceType = (ReferenceType) it.next();
                if (referenceType instanceof ArrayType) {
                    continue;
                } else {
                    String fqn = referenceType.name();
                    String className = getClassName(fqn);
                    String packageName = getPackageName(fqn);
                    String classLoader = null;
                    ClassLoaderReference classLoaderReference = referenceType.classLoader();
                    if (classLoaderReference != null) {
                        if (jpdaDebugger != null && theThreadReference != null) {
                            ClassType classLoaderClassType = (ClassType) classLoaderReference.referenceType();
                            com.sun.jdi.Method toStringMethod = classLoaderClassType.
                                    concreteMethodByName("toString", "()Ljava/lang/String;");
                            
//                            try {
                            StringReference sr = invokeMethod(jpdaDebugger, classLoaderReference, toStringMethod);
//                                        (StringReference) classLoaderReference.invokeMethod(
//                                        threadReference,
//                                        toStringMethod,
//                                        Collections.EMPTY_LIST,
//                                        ObjectReference.INVOKE_SINGLE_THREADED);
                            if (sr == null) {
                                
                                classLoader = String.valueOf(classLoaderReference);
                            } else {
                                classLoader = sr.value();
                            }
//                            } catch (InvalidTypeException ex) {
//                            } catch (InvocationException ex) {
//                            } catch (IncompatibleThreadStateException ex) {
//                                classLoader = String.valueOf(classLoaderReference);
//                            } catch (ClassNotLoadedException ex) {
//                            }
                        } else {
                            classLoader = String.valueOf(classLoaderReference);
                        }
                    }
                    classes.add(
                            className +
                            "#" +
                            ( packageName == null ? " " : packageName) +
                            "#" +
                            (classLoader == null ? " " : classLoader)
                            );
                }
            }
            Collections.sort(classes);
        }
        
        private StringReference invokeMethod(JPDADebugger jpdaDebugger, ObjectReference classLoaderReference, com.sun.jdi.Method toStringMethod) {
            try {
                Method method =
                        jpdaDebugger.getClass().getMethod("invokeMethod",
                        new Class[] {ObjectReference.class, com.sun.jdi.Method.class, Value[].class});
                StringReference stringReference =
                        (StringReference) method.invoke(jpdaDebugger, new Object[] {classLoaderReference, toStringMethod, new Value[0]});
                return stringReference;
            } catch (IllegalArgumentException ex) {
            } catch (IllegalAccessException ex) {
            } catch (InvocationTargetException ex) {
            } catch (SecurityException ex) {
            } catch (NoSuchMethodException ex) {
            }
            return null;
            
        }
        
        public int getRowCount() {
            return classes.size();
        }
        
        private String[] columnNames = {
            "Class",
            "Package",
            "ClassLoader",
        };
        
        public Class getColumnClass(int columnIndex) {
            return String.class;
        }
        
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex] + (columnIndex == 0 ? " (" + classes.size() + ")" : "");
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            //return classes.get(rowIndex);
            String[] colValues =  ((String) classes.get(rowIndex)).split("#");
            return colValues[columnIndex];
        }
    }
    
    private static String getClassName(String fqn) {
        int dotIndex = fqn.lastIndexOf(".");
        if (dotIndex != -1) {
            return fqn.substring(dotIndex+1);
        }
        return fqn;
    }
    
    private static String getPackageName(String fqn) {
        int dotIndex = fqn.lastIndexOf(".");
        if (dotIndex != -1) {
            return fqn.substring(0, dotIndex);
        }
        return null;
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
            
            JPDAThread jpdaThread = callStackFrame.getThread();
            if (jpdaThread != null) {
                ThreadReference threadReference = getThreadReference(jpdaThread);
                if (threadReference != null) {
                    myActions.add(
                            Models.createAction(
                            NbBundle.getBundle(CallStackViewNodeActionsProviderFilter.class).getString("CTL_ShowClassesAction"),
                            new ShowClassesAction(threadReference.virtualMachine(), threadReference),
                            Models.MULTISELECTION_TYPE_EXACTLY_ONE
                            ));
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
    
    
    private ThreadReference getThreadReference(JPDAThread jpdaThread) {
        try {
            Method method = jpdaThread.getClass().getMethod("getThreadReference", new Class[0]);
            ThreadReference threadReference = (ThreadReference) method.invoke(jpdaThread, new Object[0]);
            return threadReference;
        } catch (IllegalArgumentException ex) {
        } catch (IllegalAccessException ex) {
        } catch (InvocationTargetException ex) {
        } catch (SecurityException ex) {
        } catch (NoSuchMethodException ex) {
        }
        return null;
    }
    
    protected void finalize() throws Throwable {
    }
}
