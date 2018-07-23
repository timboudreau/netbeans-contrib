/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.commons.palette.java.util;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.portalpack.commons.palette.java.JavaMethod;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;

/**
 *
 * @author satyaranjan
 */
public class JavaUtil {

    private static Logger logger = Logger.getLogger("portalpack.commons");

    public static boolean addMethod(FileObject fileObject, final JavaMethod method) {

        try {
            JavaSource js = JavaSource.forFileObject(fileObject);
            ModificationResult result = js.runModificationTask(new CancellableTask<WorkingCopy>() {

                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    TreeMaker make = workingCopy.getTreeMaker();
                    CompilationUnitTree cut = workingCopy.getCompilationUnit();

                    MethodTree methodTree = null;
                    //   for (Tree typeDecl : cut.getTypeDecls()) {
                    
                    TypeElement clazzType = findMainClassName(workingCopy);
                    ClassTree clazz = workingCopy.getTrees().getTree(clazzType);

                   /* String annotationAttrValue = "";
                    if (event.isQName()) {
                        QName qName = event.getQName();
                        annotationAttrValue = "qname = \"" + qName.toString() + "\"";
                    } else {
                        annotationAttrValue = "name = \"" + event.getName() + "\"";
                    }*/
                    // create method modifier: public and no annotation
                    com.sun.source.tree.ModifiersTree methodModifiers =
                        make.Modifiers(Collections.<Modifier>emptySet(), 
                        Collections.<AnnotationTree>emptyList());
                    //New Code

                    List<VariableTree> varList = new ArrayList();
                    JavaMethod.ParameterInfo[] paramInfos = method.getParameters();
                    for(int z=0;z< paramInfos.length;z++) {
                        
                        JavaMethod.ParameterInfo paramInfo = paramInfos[z];
                        
                        varList.add(make.Variable(methodModifiers, paramInfo.getName(),
                                                    make.Identifier(paramInfo.getType()), 
                                                    null));
                    }
                   
                    
                    //add Annotation
                  /*  Tree annType = make.Identifier("javax.portlet.ProcessEvent");
                    List<ExpressionTree> expList = new ArrayList();
                    ExpressionTree eTree = make.Identifier(annotationAttrValue);
                    expList.add(eTree);
                    AnnotationTree annTree = make.AAnnotation(annType, expList);
                    List<AnnotationTree> annTreeList = new ArrayList();
                    annTreeList.add(annTree);*/

                    List<ExpressionTree> exceptionList = new ArrayList();
                    
                    for(int i=0;i<method.getExceptionList().size();i++) {
                        exceptionList.add(make.Identifier((String)method.getExceptionList().get(i)));
                    }
                    
                    List modifiersList = method.getModifier();
                    Set set = new HashSet();
                    for(int i=0;i<modifiersList.size();i++) {
                        
                        if(modifiersList.get(i).equals("public"))
                            set.add(Modifier.PUBLIC);
                        else if(modifiersList.get(i).equals("private"))
                            set.add(Modifier.PRIVATE);
                        else if(modifiersList.get(i).equals("protected"))
                            set.add(Modifier.PROTECTED);
                        else if(modifiersList.get(i).equals("static"))
                            set.add(Modifier.STATIC);
                        else if(modifiersList.get(i).equals("synchronized"))
                            set.add(Modifier.SYNCHRONIZED);
                        else if(modifiersList.get(i).equals("final"))
                            set.add(Modifier.FINAL);
                    }
                    
                    ModifiersTree mods = make.Modifiers(set);//, annTreeList);
                    MethodTree newMethod = make.Method(mods, method.getMethodName(), make.Identifier(method.getReturnType()),
                        Collections.<TypeParameterTree>emptyList(),
                        varList,
                        //Collections.singletonList(make.Variable(mods, "i", make.Identifier("int"), null)),
                        exceptionList, method.getMethodBody(), null);

                    ClassTree modifiedClazz = make.addClassMember(clazz, newMethod);
                    workingCopy.rewrite(clazz, modifiedClazz);

                }

                public void cancel() {
                }
            });
            result.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    public static List<JavaMethod> getMethods(FileObject fObject, final String className) {
        final List l = new ArrayList();
        JavaSource js = JavaSource.forFileObject(fObject);

        try {

            js.runUserActionTask(new CancellableTask<CompilationController>() {

                public void cancel() {
                }

                public void run(CompilationController parameter) throws IOException {

                    parameter.toPhase(Phase.ELEMENTS_RESOLVED);
                    //MemberVisitor m = new MemberVisitor(parameter,l);
                    //m.scan(parameter.getCompilationUnit(), null);
                    TypeElement clazz = null;

                    if (className == null) {
                        clazz = findMainClassName(parameter);
                    } else {
                        findClass(parameter, className);
                    }
                    getMethods(clazz, l);
                    if (clazz == null) {
                        return;
                    }
                }
            }, true);


        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return l;
    }


    private static void getMethods(TypeElement te, List<JavaMethod> methods) {
        //Element el = info.getTrees().getElement(getCurrentPath());

        if (te == null) {
            StatusDisplayer.getDefault().setStatusText("Cannot resolve class!");
        } else {
            //    TypeElement te = (TypeElement) el;
            List enclosedElements = te.getEnclosedElements();
            //InputOutput io = IOProvider.getDefault().getIO("Analysis of " + info.getFileObject().getName(), true);
            for (int i = 0; i < enclosedElements.size(); i++) {
                Element enclosedElement = (Element) enclosedElements.get(i);
                if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                    //    io.getOut().println("Constructor: " + enclosedElement.getSimpleName());
                } else if (enclosedElement.getKind() == ElementKind.METHOD) {
                    //  io.getOut().println("Method: " + enclosedElement.getSimpleName());
                    //String methodName = enclosedElement.getSimpleName().toString();
                    ///methods.add((ExecutableElement) enclosedElement);
                    ExecutableElement methodElm = (ExecutableElement) enclosedElement;
                    JavaMethod methodInfo = new JavaMethod(methodElm.getSimpleName().toString());

                    List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();
                    JavaMethod.ParameterInfo[] paramTable = new JavaMethod.ParameterInfo[paramsType.size()];
                    for (int z = 0; z < paramTable.length; z++) {
                        VariableElement varElm = (VariableElement) paramsType.get(z);
                        paramTable[z] = new JavaMethod.ParameterInfo(varElm.getSimpleName().toString(), varElm.asType().toString(), null);
                    }

                    methodInfo.setParameters(paramTable);
                    methods.add(methodInfo);

                } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                    //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                } else {
                    //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                }
            }

        }
    }

    private static TypeElement findClass(CompilationController ctrl, String className) {

        for (Tree decl : ctrl.getCompilationUnit().getTypeDecls()) {
            if (Tree.Kind.CLASS != decl.getKind()) {
                continue;
            }

            String actualClazzName = ((ClassTree) decl).getSimpleName().toString();
            if (className.equals(actualClazzName) || className.endsWith("." + actualClazzName)) {
                TreePath path = ctrl.getTrees().getPath(ctrl.getCompilationUnit(), decl);
                TypeElement clazz = (TypeElement) ctrl.getTrees().getElement(path);
                return clazz;
            }
        }
        return null;
    }  

    private static TypeElement findMainClassName(CompilationController ctrl) {
        
        for (Tree decl : ctrl.getCompilationUnit().getTypeDecls()) {
            if (Tree.Kind.CLASS != decl.getKind()) {
                continue;
            }

            String actualClazzName = ((ClassTree) decl).getSimpleName().toString();
            //TreePath path = ctrl.getTrees().getPath(ctrl.getCompilationUnit(), decl);
            TreePath path = ctrl.getTrees().getPath(ctrl.getCompilationUnit(), decl);
            TypeElement clazz = (TypeElement) ctrl.getTrees().getElement(path);
            return clazz;
            

        }
        return null;
    }
    
    public static boolean isMethodPresent(JavaMethod method, JavaMethod[] methods) {
        
        for(int i=0;i<methods.length; i++) {
            
            if(!method.getMethodName().equals(methods[i].getMethodName()))
                return false;
            
            if(method.getParameters().length != methods[i].getParameters().length)
                return false;
            
            JavaMethod.ParameterInfo[] paramInfo1 = method.getParameters();
            JavaMethod.ParameterInfo[] paramInfo2 = methods[i].getParameters();
            
            for(int z=0;i<paramInfo1.length; z++) {
                
                if(!paramInfo1[z].getType().equals(paramInfo2[z].getType())
                        && !paramInfo1[z].getType().endsWith("." + paramInfo2[z].getType())
                        && !paramInfo2[z].getType().endsWith("."+paramInfo1[z].getType()))
                    return false;                
            }
            
            return true;
            
        }
        
        return false;
    }

    private static boolean isSameSignature(JavaMethod method1, MethodTree method2) {

        JavaMethod.ParameterInfo[] paramInfo1 = method1.getParameters();
        List<VariableTree> paramList2 = (List<VariableTree>) method2.getParameters();

        if (!method1.getMethodName().equals(method2.getName().toString())) {
            return false;
        }
        if (paramInfo1.length != paramList2.size()) {
            return false;
        }
        for (int i = 0; i < paramInfo1.length; i++) {

            VariableTree param = (VariableTree) paramList2.get(i);
            String type = "";
            if (param.getType().getKind() == Kind.IDENTIFIER) {
                IdentifierTree idTree = (IdentifierTree) param.getType();
                type = idTree.getName().toString();
            } else {
                type = param.getType().toString();
            }

            if (!paramInfo1[i].getType().equals(type) && !(paramInfo1[i].getType().endsWith("." + type)) && !(type.endsWith("." + paramInfo1[i].getType()))) {
                return false;
            }
        }
        return true;
    }
}
