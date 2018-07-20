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
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.debugger.threadviewenhancement.ui.models;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ThreadsViewNodeActionsProviderFilter  implements NodeActionsProviderFilter {
    
    public ThreadsViewNodeActionsProviderFilter() {
    }
    
    private final Action MAKE_CURRENT_ACTION = Models.createAction (
        NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_CallstackAction_MakeCurrent_Label"),
        new Models.ActionPerformer () {
            public boolean isEnabled (Object node) {
                // TODO: Check whether is not current - API change necessary
                return true;
            }
            public void perform (Object[] nodes) {
                if (nodes[0] instanceof CallStackFrame) {
                    makeCurrent ((CallStackFrame) nodes [0]);
                }
            }
        },
        Models.MULTISELECTION_TYPE_EXACTLY_ONE
    );
        
    private static final Action POP_TO_HERE_ACTION = Models.createAction (
        NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_CallstackAction_PopToHere_Label"),
        new Models.ActionPerformer () {
            public boolean isEnabled (Object node) {
                // TODO: Check whether this frame is deeper then the top-most
                return true;
            }
            public void perform (final Object[] nodes) {
                // Do not do expensive actions in AWT,
                // It can also block if it can not procceed for some reason
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        popToHere((CallStackFrame) nodes [0]);
                    }
                });
            }
        },
        Models.MULTISELECTION_TYPE_EXACTLY_ONE
    );
       
    private final Action COPY_TO_CLBD_ACTION = Models.createAction (
        NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_CallstackAction_Copy2CLBD_Label"),
        new Models.ActionPerformer () {
            public boolean isEnabled (Object node) {
                // TODO: Check whether is not current - API change necessary
                return true;
            }
            public void perform (Object[] nodes) {
                if (nodes[0] instanceof CallStackFrame) {
                    stackToCLBD( (CallStackFrame) nodes[0]);
                }
            }
        },
        Models.MULTISELECTION_TYPE_EXACTLY_ONE
    );

    private static void makeCurrent (final CallStackFrame frame) {
        JPDADebugger debugger = Utils.getDebugger(frame);
        if (!Utils.isCurrentStackFrame(frame)) {
            frame.makeCurrent ();
        } else {
            showSource(frame);
        }
    }
    
    private static void showSource (final CallStackFrame frame) {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                final DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
                String language = debuggerManager.getCurrentSession ().getCurrentLanguage ();
                String url = Utils.getLocation(frame);
                if (url != null) {
                    EditorContext editorContext = (EditorContext) debuggerManager.lookupFirst(null, EditorContext.class);
                    if (editorContext != null) {
                        editorContext.showSource(url, frame.getLineNumber(language), null);
                    }
                }
            }
        });
    }
    
    private static void popToHere (final CallStackFrame frame) {
        try {
            JPDAThread t = frame.getThread ();
            CallStackFrame[] stack = t.getCallStack ();
            int i, k = stack.length;
            if (k < 2) return ;
            for (i = 0; i < k; i++)
                if (stack [i].equals (frame)) {
                    if (i > 0) {
                        stack [i - 1].popFrame ();
                    }
                    return;
                }
        } catch (AbsentInformationException ex) {
        }
    }
        
    private void stackToCLBD(final CallStackFrame frame) {
        JPDADebugger debugger = Utils.getDebugger(frame);
        JPDAThread thread = frame.getThread();
        if (!thread.isSuspended()) {
            return;
        }
        StringBuffer frameStr = new StringBuffer(50);
        CallStackFrame[] stack;
        try {
            stack = thread.getCallStack ();
        } catch (AbsentInformationException ex) {
            frameStr.append(NbBundle.getMessage(ThreadsViewNodeActionsProviderFilter.class, "MSG_NoSourceInfo"));
            stack = null;
        }
        if (stack != null) {
            int i, k = stack.length;

            for (i = 0; i < k; i++) {
                frameStr.append(Utils.getDisplayName(stack[i]));
                try {
                    String sourceName = stack[i].getSourceName(null);
                    frameStr.append("(");
                    frameStr.append(sourceName);
                    frameStr.append(")");
                } catch (AbsentInformationException ex) {
                    //frameStr.append(NbBundle.getMessage(CallStackActionsProvider.class, "MSG_NoSourceInfo"));
                    // Ignore, do not provide source name.
                }
                if (i != k - 1) frameStr.append('\n');
            }
        }
        Clipboard systemClipboard = getClipboard();
        Transferable transferableText =
                new StringSelection(frameStr.toString());
        systemClipboard.setContents(
                transferableText,
                null);
    }
    
    private static Clipboard getClipboard() {
        Clipboard clipboard = (Clipboard) org.openide.util.Lookup.getDefault().lookup(Clipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        return clipboard;
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
        private Thread thread;
        private boolean stopShowClasses;
        
        ShowClassesAction(VirtualMachine virtualMachine, ThreadReference threadReference) {
            this.virtualMachine = virtualMachine;
            this.threadReference = threadReference ;
        }
        
        public boolean isEnabled(Object node) {
            return true;
        }
        
        public void perform(Object[] nodes) {
            final JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(), "", true); // NOI18N
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setUndecorated(true);
            
            final JTable classesTable = new JTable(new PleaseWaitTableModel());
            dialog.setContentPane(new ResizablePanel(new JScrollPane(classesTable),
                    NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("TITLE_Classes"))); // NOI18N
            
            dialog.addWindowListener(new WindowAdapter() {
                public void windowDeactivated(WindowEvent e) {
                    //hide(dialog);
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
            
            thread = new Thread(new Runnable() {
                public void run() {
                    final TableSorter tableSorter = new TableSorter(new ClassesTableModel(virtualMachine.allClasses(), threadReference));
                    if (!isStopShowClasses()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                tableSorter.setColumnComparator(String.class, tableSorter.COMPARABLE_COMAPRATOR);
                                tableSorter.setSortingStatus(0, TableSorter.ASCENDING);
                                tableSorter.setTableHeader(classesTable.getTableHeader());
                                classesTable.setModel(tableSorter);
                            }
                        });
                    }
                    synchronized(ShowClassesAction.this) {
                        thread = null;
                    }
                }
            }, "Show Classes"); // NOI18N
            setStopShowClasses(false);
            thread.start();
            
            dialog.setBounds(bounds);
            dialog.setVisible(true);
        }
        
        private void hide(JDialog dialog) {
            synchronized(this) {
                if (thread != null && thread.isAlive()) {
                    setStopShowClasses(true);
                    thread = null;
                }
            }
            
            bounds = dialog.getBounds();
            dialog.setVisible(false);
        }
        
        public synchronized boolean isStopShowClasses() {
            return stopShowClasses;
        }
        
        public synchronized void setStopShowClasses(boolean stopShowClasses) {
            this.stopShowClasses = stopShowClasses;
        }
        
        private class ClassesTableModel extends AbstractTableModel {
            private List classes = new ArrayList();
            private ThreadReference theThreadReference;
            
            ClassesTableModel(List unmodifyableClassesList, ThreadReference threadReference) {
                this.theThreadReference = threadReference;
                
                List classesList = new ArrayList(unmodifyableClassesList);
                IdentityHashMap initiatingClassLoadersMap = new IdentityHashMap();
                {
                    Set initiatingClassLoaders = new HashSet();
                    for (Iterator it = classesList.iterator(); it.hasNext();) {
                        if (isStopShowClasses()) {
                            return;
                        }
                        ReferenceType referenceType = (ReferenceType) it.next();
                        // filter out ArrayType
                        if (referenceType instanceof ArrayType) {
                            it.remove();
                            continue;
                        }
                        ClassLoaderReference classLoaderReference = referenceType.classLoader();
                        if (classLoaderReference != null) {
                            // have we scanned this ClassLoader already?
                            if (!initiatingClassLoaders.contains(classLoaderReference)) {
                                List visibleClasess = new ArrayList(classLoaderReference.visibleClasses());
                                visibleClasess.removeAll(classLoaderReference.definedClasses());
                                for (Iterator iter = visibleClasess.iterator(); iter.hasNext();) {
                                    if (isStopShowClasses()) {
                                        return;
                                    }
                                    ReferenceType visibleReferenceType = (ReferenceType) iter.next();
                                    initiatingClassLoadersMap.put(visibleReferenceType, classLoaderReference);
                                }
                                initiatingClassLoaders.add(classLoaderReference);
                            }
                        }
                        Thread.yield();
                    }
                    initiatingClassLoaders = null;
                }
                
                JPDADebugger jpdaDebugger = null;
                DebuggerEngine debuggerEngine = DebuggerManager.getDebuggerManager().getCurrentEngine();
                if (debuggerEngine != null) {
                    jpdaDebugger = (JPDADebugger) debuggerEngine.lookupFirst(null, JPDADebugger.class);
                }
                
                Map classLoaderReferenceToStringMap = new HashMap();
                Map initiatingClassLoaderReferenceToStringMap = new HashMap();
                
                for (Iterator it = classesList.iterator(); it.hasNext();) {
                    if (isStopShowClasses()) {
                        return;
                    }
                    ReferenceType referenceType = (ReferenceType) it.next();
                    if (referenceType instanceof ArrayType) {
                        continue;
                    } else {
                        String fqn = referenceType.name();
                        String className = getClassName(fqn)+
                                " (id=" + referenceType.classObject().uniqueID() + ")"; // NOI18N
                        String packageName = getPackageName(fqn);
                        String classLoader = null;
                        String initiatingClassLoader = null;
                        
                        ClassLoaderReference classLoaderReference = referenceType.classLoader();
                        
                        if (classLoaderReference != null) {
                            classLoader = (String) classLoaderReferenceToStringMap.get(classLoaderReference);
                            if (classLoader == null) {
                                classLoader = String.valueOf(classLoaderReference);
                                if (jpdaDebugger != null && theThreadReference != null) {
                                    ClassType classLoaderClassType = (ClassType) classLoaderReference.referenceType();
                                    com.sun.jdi.Method toStringMethod = classLoaderClassType.
                                            concreteMethodByName("toString", "()Ljava/lang/String;"); // NOI18N
                                    StringReference sr = invokeMethod(jpdaDebugger, classLoaderReference, toStringMethod);
                                    if (sr != null) {
                                        classLoader = sr.value() +
                                                " (id=" + classLoaderReference.uniqueID() + ")"; // NOI18N
                                    }
                                    if (initiatingClassLoader != null) {
                                        classLoaderReferenceToStringMap.put(classLoaderReference, classLoader);
                                    }
                                }
                            }
                        }
                        
                        ClassLoaderReference initiatingClassLoaderReference = (ClassLoaderReference) initiatingClassLoadersMap.get(referenceType);
                        if (initiatingClassLoaderReference != null) {
                            initiatingClassLoader = (String) initiatingClassLoaderReferenceToStringMap.get(initiatingClassLoaderReference);
                            if (initiatingClassLoader == null) {
                                initiatingClassLoader = String.valueOf(initiatingClassLoaderReference);
                                if (jpdaDebugger != null && theThreadReference != null) {
                                    ClassType classLoaderClassType = (ClassType) initiatingClassLoaderReference.referenceType();
                                    com.sun.jdi.Method toStringMethod = classLoaderClassType.
                                            concreteMethodByName("toString", "()Ljava/lang/String;"); // NOI18N
                                    StringReference sr = invokeMethod(jpdaDebugger, initiatingClassLoaderReference, toStringMethod);
                                    if (sr != null) {
                                        initiatingClassLoader = sr.value() +
                                                " (id=" + initiatingClassLoaderReference.uniqueID() + ")"; // NOI18N
                                    }
                                    if (initiatingClassLoader != null) {
                                        initiatingClassLoaderReferenceToStringMap.put(initiatingClassLoaderReference, initiatingClassLoader);
                                    }
                                }
                            }
                        }
                        classes.add(
                                className +
                                "#" + // NOI18N
                                ( packageName == null ? " " : packageName) + // NOI18N
                                "#" + // NOI18N
                                (classLoader == null ? " " : classLoader) + // NOI18N
                                "#" + // NOI18N
                                (initiatingClassLoader == null ? " " : initiatingClassLoader) // NOI18N
                                );
                    }
                }
                Collections.sort(classes);
            }
            
            private StringReference invokeMethod(JPDADebugger jpdaDebugger, ObjectReference classLoaderReference, com.sun.jdi.Method toStringMethod) {
                try {
                    Method method =
                            jpdaDebugger.getClass().getMethod("invokeMethod",  // NOI18N
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
                NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("HEADER_Class"), // NOI18N
                NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("HEADER_Package"), // NOI18N
                NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("HEADER_ClassLoader"), // NOI18N
                NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("HEADER_InitiatingClassLoader"), // NOI18N
            };
            
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
            
            public String getColumnName(int columnIndex) {
                return columnNames[columnIndex] + (columnIndex == 0 ? " (" + classes.size() + ")" : ""); // NOI18N
            }
            
            public int getColumnCount() {
                return columnNames.length;
            }
            
            public Object getValueAt(int rowIndex, int columnIndex) {
                //return classes.get(rowIndex);
                String[] colValues =  ((String) classes.get(rowIndex)).split("#"); // NOI18N
                return colValues[columnIndex];
            }
        }
    }
    
    private static class PleaseWaitTableModel extends AbstractTableModel {
        public int getRowCount() {
            return 1;
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public String getColumnName(int columnIndex) {
            return "";
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            return NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("LBL_PleaseWait");
        }
    }
    
    private static String getClassName(String fqn) {
        int dotIndex = fqn.lastIndexOf("."); // NOI18N
        if (dotIndex != -1) {
            return fqn.substring(dotIndex+1);
        }
        return fqn;
    }
    
    private static String getPackageName(String fqn) {
        int dotIndex = fqn.lastIndexOf("."); // NOI18N
        if (dotIndex != -1) {
            return fqn.substring(0, dotIndex);
        }
        return null;
    }
    
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {        
        List myActions = new ArrayList();
        if (node instanceof CallStackFrame) {
            myActions.add(MAKE_CURRENT_ACTION);
            myActions.add(POP_TO_HERE_ACTION);            
            myActions.add(COPY_TO_CLBD_ACTION);
            CallStackFrame callStackFrame = (CallStackFrame) node;
            List alreadyAddedTypeName = new ArrayList();
            This thisOfCallStackFrame = callStackFrame.getThisVariable();
            if (thisOfCallStackFrame != null) {
                final String thisTypeNameFinal = thisOfCallStackFrame.getType();
                if (!callStackFrame.getClassName().equals(thisTypeNameFinal)) {
                    myActions.add(
                            Models.createAction(
                            NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + thisTypeNameFinal + " (this)", // NOI18N
                            new GotoLocalVariableTypeAction(thisTypeNameFinal),
                            Models.MULTISELECTION_TYPE_EXACTLY_ONE));
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
                                    NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + typeNameFinal, // NOI18N
                                    new GotoLocalVariableTypeAction(typeNameFinal),
                                    Models.MULTISELECTION_TYPE_EXACTLY_ONE));
                            alreadyAddedTypeName.add(typeNameFinal);
                        }
                    }
                    String declaredTypeName = localVariable.getDeclaredType();
                    if (declaredTypeName != null) {
                        final String declaredTypeNameFinal = Utils.stripArray(declaredTypeName);
                        if (!Utils.primitivesList.contains(declaredTypeNameFinal) && !alreadyAddedTypeName.contains(declaredTypeNameFinal)) {
                            myActions.add(
                                    Models.createAction(
                                    NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_GotoTypeAction") + " " + declaredTypeNameFinal, // NOI18N
                                    new GotoLocalVariableTypeAction(declaredTypeNameFinal),
                                    Models.MULTISELECTION_TYPE_EXACTLY_ONE));
                            alreadyAddedTypeName.add(declaredTypeNameFinal);
                        }
                    }
                }
            } catch (AbsentInformationException e) {
                
            }
            
            try {
                JPDAThread jpdaThread = callStackFrame.getThread();
                if (jpdaThread != null) {
                    ThreadReference threadReference = getThreadReference(jpdaThread);
                    if (threadReference != null) {
                        myActions.add(
                                Models.createAction(
                                NbBundle.getBundle(ThreadsViewNodeActionsProviderFilter.class).getString("CTL_ShowClassesAction"),
                                new ShowClassesAction(threadReference.virtualMachine(), threadReference),
                                Models.MULTISELECTION_TYPE_EXACTLY_ONE));
                    }
                }
            } catch (InvalidStackFrameException invalidStackFrameException) {
                // Thread has been resumed.
            }
        } else {
            return original.getActions(node);
        }
        List actionToAdd = new ArrayList();
        actionToAdd.addAll(myActions);
        return (Action[]) actionToAdd.toArray(new Action [actionToAdd.size()]);
    }
    
    private ThreadReference getThreadReference(JPDAThread jpdaThread) {
        try {
            Method method = jpdaThread.getClass().getMethod("getThreadReference", new Class[0]); // NOI18N
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
    
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
        if (node instanceof CallStackFrame) {
            makeCurrent((CallStackFrame) node);
            return;
        }
        original.performDefaultAction(node);
    }
    
    public void addModelListener(ModelListener l) {
    }
    
    public void removeModelListener(ModelListener l) {
    }
    
    protected void finalize() throws Throwable {
    }

}
