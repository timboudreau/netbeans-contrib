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
 */ /*
 * RefactoringUtil.java
 *
 * Created on May 14, 2007, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.saw.palette.items;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;


/**
 *
 * @author Vihang
 */
public class JavaSourceUtil {
    private static Logger logger = Logger.getLogger("SAW_Logger");
    /** Creates a new instance of RefactoringUtil */
    public JavaSourceUtil() {
    }

    public static void resolveImports() {
    }

    public static boolean addMethod(FileObject fObject, final String methodName) {
        boolean couldAddMethod = true;
        try {

            JavaSource js = JavaSource.forFileObject(fObject);
            final FileObject tempFileObject = fObject;
            boolean addFramework = false;
            try {
                FileObject fileObject = WebModule.getWebModule(fObject).getWebInf().getFileObject("ImplementationType", "properties");
                if (fileObject == null) {
                    addFramework = true;
                }
            } catch (Exception e) {
                addFramework = true;
                // e.printStackTrace();
                
            }
            if (addFramework) {

                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getBundle(RefactoringUtil.class).getString("Add_Workflow_Framework"), NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return couldAddMethod;
            }

            ModificationResult result = js.runModificationTask(new CancellableTask<WorkingCopy>() {

                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    boolean alreadyDefined = false;
                    TreeMaker make = workingCopy.getTreeMaker();
                    CompilationUnitTree cut = workingCopy.getCompilationUnit();
                    ClassTree clazz = null;
                    MethodTree methodTree = null;
                    for (Tree typeDecl : cut.getTypeDecls()) {
                        if (Tree.Kind.CLASS == typeDecl.getKind()) {
                            clazz = (ClassTree) typeDecl;
                            for (int i = 0; i < clazz.getMembers().size(); i++) {
                                methodTree = (MethodTree) clazz.getMembers().get(i);
                                logger.log(Level.INFO,this.getClass().getName() + ":", methodTree.getName() + "##################");
                                String tempMethodName = methodTree.getName().toString();
                                if (tempMethodName.equals(methodName)) {
                                   alreadyDefined = true;
                                    NotifyDescriptor d = new NotifyDescriptor.Confirmation(NbBundle.getBundle(RefactoringUtil.class).getString("Add_Duplicate_Method"), NbBundle.getBundle(RefactoringUtil.class).getString("Method_Already_Exists"), NotifyDescriptor.OK_CANCEL_OPTION);
                                    if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                                        alreadyDefined = false;
                                    }
                                    logger.log(Level.INFO,this.getClass().getName() + ":",methodTree.getName());
                                    break;
                                }
                            }
                        } // end if
                        if (!alreadyDefined) {
                            
                            // create method modifier: public and no annotation
                            com.sun.source.tree.ModifiersTree methodModifiers = make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList());
                            //New Code
                            FileObject propFileObject = WebModule.getWebModule(tempFileObject).getWebInf().getFileObject("ImplementationType.properties");
                            InputStream inputStream = propFileObject.getInputStream();
                            Properties prop = new Properties();
                            try {
                                prop.load(inputStream);
                            } catch (IOException ex) {
                                throw new Exception("Unable to load the property file ", ex);
                            }

                            String implementationType = prop.getProperty("ImplementationType"); // this should be of PortalWorkflow type .
                            SAWMethodFactory sawMethodFactory = SAWMethodFactory.getInstance();
                            SAWImplementationType sawImplementationType =  sawMethodFactory.getSAWImplementationTypeInstance(implementationType);
                             SAWMethod sawMethod = new SAWMethod();
                            if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("CheckOut_tasks"))) {
                                sawMethod = sawImplementationType.getCheckOutTasks();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("CheckIn_tasks"))) {
                                sawMethod =  sawImplementationType.getCheckInTasks();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Reassign_tasks"))) {
                                 sawMethod =  sawImplementationType.getReassignTasks();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Delete_tasks"))) {
                                sawMethod =  sawImplementationType.getDeleteTask();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Complete_tasks"))) {
                                sawMethod =  sawImplementationType.getCompleteTasks();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Save_tasks"))) {
                                 sawMethod =  sawImplementationType.getSaveTasks();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Escalate_tasks"))) {
                                 sawMethod =  sawImplementationType.getEscalateTask();
                            } else if (methodName.equals(NbBundle.getBundle(RefactoringUtil.class).getString("Show_AuditHistory"))) {
                                 sawMethod =  sawImplementationType.showAuditHistory();
                            }
                            else {
                                throw new Exception();
                            }
                            //End New Code
                            // make a variable trees - representing parameters
                            Iterator paramIter = sawMethod.getParameters().iterator();
                            List<VariableTree> parList = new ArrayList<VariableTree>(sawMethod.getParameters().size());
                            Tree paramTypeTree = null;
                            while (paramIter.hasNext()) {
                                ParamObject paramObj = (ParamObject) paramIter.next();
                                if (paramObj.getParamType().equals("String")) {
                                    // paramTypeTree = make.PrimitiveType(TypeKind.CHAR);
                                    paramTypeTree = make.Identifier("String");
                                } else if (paramObj.getParamType().equals("java.util.List")) {
                                    paramTypeTree = make.Identifier("java.util.List");
                                }
                                VariableTree par1 = make.Variable(methodModifiers, paramObj.getParamName(), paramTypeTree, null);
                                parList.add(par1);
                            }
                            ArrayList exceptionList = new ArrayList();
                            Iterator exceptionIter = sawMethod.getExceptionList().iterator();
                            while (exceptionIter.hasNext()) {
                                exceptionList.add(make.Identifier((String) exceptionIter.next()));
                            }
                            // now, start the method creation
                            MethodTree newMethod = make.Method(make.Modifiers(Collections.singleton(Modifier.PUBLIC)), sawMethod.getMethodName(), make.Identifier(sawMethod.getReturnType()), Collections.<TypeParameterTree>emptyList(), parList, exceptionList, sawMethod.getMethodBody(), null);
                            ClassTree modifiedClazz = make.addClassMember(clazz, newMethod);
                            workingCopy.rewrite(clazz, modifiedClazz);
                        }
                    } // end for
                }

                public void cancel() {
                }
            });
            result.commit();
        } catch (Exception e) {
            e.printStackTrace();
            couldAddMethod = false;
        }
        return couldAddMethod;
    }

    public static void implementInterface(String interfaceName) {

    }

    public static boolean isMethodPresent(String methodName, HashMap parameters) {
        return false;
    }

    public static void getMethods(FileObject fObject) {
        JavaSource js = JavaSource.forFileObject(fObject);
        try {
            js.runUserActionTask(new CancellableTask<CompilationController>() {

                public void cancel() {
                }

                public void run(CompilationController parameter) throws IOException {
                    parameter.toPhase(Phase.ELEMENTS_RESOLVED);
                    new MemberVisitor(parameter).scan(parameter.getCompilationUnit(), null);
                }
            }, true);
        } catch (IOException e) {
            //Logger.getLogger("global").log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static class MemberVisitor extends TreePathScanner<Void, Void> {

        private CompilationInfo info;

        public MemberVisitor(CompilationInfo info) {
            this.info = info;
        }

        //  @Override
        public Void visitClass(ClassTree t, Void v) {
            Element el = info.getTrees().getElement(getCurrentPath());


            if (el == null) {
                System.err.println("Cannot resolve class!");
            } else {
                TypeElement te = (TypeElement) el;
                System.err.println("Resolved class: " + te.getQualifiedName().toString());
                System.err.println("enclosed methods: " + ElementFilter.methodsIn(te.getEnclosedElements()));
                System.err.println("enclosed types: " + ElementFilter.typesIn(te.getEnclosedElements()));
            }
            return null;
        }
    }
}
