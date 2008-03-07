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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Satya
 */
public class JavaCodeUtil {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

    public JavaCodeUtil() {
    }

    public static void resolveImports() {
    }

    public static boolean addPublishEventCode(FileObject fObject, final String className, final MethodInfo methodInfo, final String methodName, final EventObject event) {

        try {
            JavaSource js = JavaSource.forFileObject(fObject);
            ModificationResult result = js.runModificationTask(new CancellableTask<WorkingCopy>() {

                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    TreeMaker make = workingCopy.getTreeMaker();
                    CompilationUnitTree cut = workingCopy.getCompilationUnit();

                    //ClassTree clazz = null;

                    MethodTree methodTree = null;
                    //   for (Tree typeDecl : cut.getTypeDecls()) {

                    ClassTree clazz = findClassTree(cut, className);

                    if (methodInfo != null) { //add to a existing method

                        String amethodName = methodInfo.getMethodName();


                        for (int i = 0; i < clazz.getMembers().size(); i++) {
                            Tree member = clazz.getMembers().get(i);
                            if (member.getKind() != Tree.Kind.METHOD) {
                                continue;
                            }
                            methodTree = (MethodTree) member;

                            Name methodName = methodTree.getName();
                            if (methodName.toString().equals(amethodName)) {

                                isSameSignature(methodInfo, methodTree);
                                List<VariableTree> params = (List<VariableTree>) methodTree.getParameters();
                                Name varName = ((VariableTree) params.get(1)).getName();

                                String responseVariableName = null;
                                if (varName != null) {
                                    responseVariableName = varName.toString();
                                }
                                BlockTree blockTree = methodTree.getBody();
                                String stmt = getPublishEventSrc(event, responseVariableName);

                                StatementTree statement = workingCopy.getTreeUtilities().parseStatement(stmt, new SourcePositions[1]);
                                BlockTree newBlock = workingCopy.getTreeMaker().addBlockStatement(blockTree, statement);
                                BlockTree modBlockWithImport = GeneratorUtilities.get(workingCopy).importFQNs(newBlock);
                                workingCopy.rewrite(blockTree, modBlockWithImport);

                            }
                        }
                    } else {

                        // create method modifier: public and no annotation
                        com.sun.source.tree.ModifiersTree methodModifiers = make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList());
                        //New Code


                        List<Tree> paramType = new ArrayList();
                        paramType.add(make.Identifier("javax.portlet.ActionRequest"));
                        paramType.add(make.Identifier("javax.portlet.ActionResponse"));

                        List<VariableTree> parList = new ArrayList();
                        parList.add(make.Variable(methodModifiers, "request", (Tree) paramType.get(0), null));
                        parList.add(make.Variable(methodModifiers, "response", (Tree) paramType.get(1), null));

                        String stmt = "{" + getPublishEventSrc(event, null) + "}";

                        ModifiersTree mods = make.Modifiers(Collections.singleton(Modifier.PUBLIC));
                        MethodTree newMethod = make.Method(mods, methodName, make.Identifier("void"),
                                Collections.<TypeParameterTree>emptyList(),
                                parList,
                                //Collections.singletonList(make.Variable(mods, "i", make.Identifier("int"), null)),
                                Collections.<ExpressionTree>emptyList(), stmt, null);
                        // MethodTree newMethod = make.Method(make.Modifiers(Collections.singleton(Modifier.PUBLIC)), methodName , make.Identifier("void"), Collections.<TypeParameterTree>emptyList(), parList, Collections.emptyList(), "return;", null);
                        ClassTree modifiedClazz = make.addClassMember(clazz, newMethod);
                        workingCopy.rewrite(clazz, modifiedClazz);
                    }
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

    public static boolean addProcessEventCode(FileObject fileObject, final String portletClassName, final MethodInfo methodInfo, final String suggestedMethodName, final EventObject event) {

        try {
            JavaSource js = JavaSource.forFileObject(fileObject);
            ModificationResult result = js.runModificationTask(new CancellableTask<WorkingCopy>() {

                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    TreeMaker make = workingCopy.getTreeMaker();
                    CompilationUnitTree cut = workingCopy.getCompilationUnit();

                    MethodTree methodTree = null;
                    //   for (Tree typeDecl : cut.getTypeDecls()) {

                    ClassTree clazz = findClassTree(cut, portletClassName);

                    if (methodInfo != null) { //add to a existing method
                        //Show warning that method is aready there
                    }

                    String annotationAttrValue = "";
                    if (event.isQName()) {
                        QName qName = event.getQName();
                        annotationAttrValue = "qname = \"" + qName.toString() + "\"";
                    } else {
                        annotationAttrValue = "name = \"" + event.getName() + "\"";
                    }
                    // create method modifier: public and no annotation
                    com.sun.source.tree.ModifiersTree methodModifiers = make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList());
                    //New Code

                    List<Tree> paramType = new ArrayList();
                    paramType.add(make.Identifier("javax.portlet.EventRequest"));
                    paramType.add(make.Identifier("javax.portlet.EventResponse"));

                    List<VariableTree> parList = new ArrayList();
                    parList.add(make.Variable(methodModifiers, "request", (Tree) paramType.get(0), null));
                    parList.add(make.Variable(methodModifiers, "response", (Tree) paramType.get(1), null));

                    String stmt = getProcessEventMethodBody(event, null);
                    //add Annotation
                    Tree annType = make.Identifier("javax.portlet.ProcessEvent");
                    List<ExpressionTree> expList = new ArrayList();
                    ExpressionTree eTree = make.Identifier(annotationAttrValue);
                    expList.add(eTree);
                    AnnotationTree annTree = make.Annotation(annType, expList);
                    List<AnnotationTree> annTreeList = new ArrayList();
                    annTreeList.add(annTree);

                    List<ExpressionTree> exceptionList = new ArrayList();
                    exceptionList.add(make.Identifier("javax.portlet.PortletException"));
                    exceptionList.add(make.Identifier("java.io.IOException"));
                    ModifiersTree mods = make.Modifiers(Collections.singleton(Modifier.PUBLIC), annTreeList);
                    MethodTree newMethod = make.Method(mods, suggestedMethodName, make.Identifier("void"),
                            Collections.<TypeParameterTree>emptyList(),
                            parList,
                            //Collections.singletonList(make.Variable(mods, "i", make.Identifier("int"), null)),
                            exceptionList, stmt, null);

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

    private static boolean isSameSignature(MethodInfo method1, MethodTree method2) {
        MethodInfo.ParameterInfo[] paramInfo1 = method1.getParameterInfo();
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

    private static String getPublishEventSrc(EventObject event, String responseVariableName) {
        try {

            HashMap map = new HashMap();
            if (event.getQName() != null) {
                map.put("qname", event.getQName());
                QName qName = event.getQName();

                if (qName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
                    map.put("NAMESPACE", "");
                } else {
                    map.put("NAMESPACE", qName.getNamespaceURI());
                }

                if (qName.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                    map.put("PREFIX", "");
                } else {
                    map.put("PREFIX", qName.getPrefix());
                }
                map.put("LOCALPART", qName.getLocalPart());
            } else {
                map.put("qname", "");
                map.put("EVENT_NAME", event.getName());
            }
            if (responseVariableName != null) {
                map.put("RESPONSE", responseVariableName);
            } else {
                map.put("RESPONSE", "response");
            }
            if (event.getValueType() != null) {
                map.put("VALUE_TYPE", event.getValueType());
            } else {
                map.put("VALUE_TYPE", "");
            }

            FileObject template = TemplateHelper.getTemplateFile("publishevent.template");
            StringWriter writer = new StringWriter();
            TemplateHelper.mergeTemplateToWriter(template, writer, map);
            return writer.toString();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error getting publish method body", ex);
        }
        return "";
    }

    public static String getProcessEventMethodBody(EventObject event, String calledMethodName) {
        try {

            HashMap map = new HashMap();
            if (event.getQName() != null) {
                map.put("qname", event.getQName());
                QName qName = event.getQName();

                if (qName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
                    map.put("NAMESPACE", "");
                } else {
                    map.put("NAMESPACE", qName.getNamespaceURI());
                }

                if (qName.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                    map.put("PREFIX", "");
                } else {
                    map.put("PREFIX", qName.getPrefix());
                }
                map.put("LOCALPART", qName.getLocalPart());
            } else {
                map.put("qname", "");
                map.put("EVENT_NAME", event.getName());
            }

            map.put("NEW_PROCESS_EVENT", "true");
            map.put("CUSTOM_METHOD", "");
            map.put("VALUE_TYPE", event.getValueType());

            FileObject template = TemplateHelper.getTemplateFile("processevent.template");
            StringWriter writer = new StringWriter();
            TemplateHelper.mergeTemplateToWriter(template, writer, map);
            return writer.toString();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    public String getCustomProcessEventMethod(EventObject event) {
        return null;
    }

    public static void implementInterface(String interfaceName) {
    }

    public static boolean isMethodPresent(String methodName, HashMap parameters) {
        return false;
    }

    public static List<MethodInfo> getMethods(final String className, FileObject fObject) {
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
                    TypeElement clazz = findClass(parameter, className);
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

    private static ClassTree findClassTree(CompilationUnitTree ctrl, String className) {
        for (Tree decl : ctrl.getTypeDecls()) {
            if (Tree.Kind.CLASS != decl.getKind()) {
                continue;
            }
            String actualClazzName = ((ClassTree) decl).getSimpleName().toString();
            if (className.equals(actualClazzName) || className.endsWith("." + actualClazzName)) {
                return (ClassTree) decl;
            }
        }
        return null;
    }

    private static void getMethods(TypeElement te, List<MethodInfo> methods) {
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
                    MethodInfo methodInfo = new MethodInfo(methodElm.getSimpleName().toString());

                    List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();
                    MethodInfo.ParameterInfo[] paramTable = new MethodInfo.ParameterInfo[paramsType.size()];
                    for (int z = 0; z < paramTable.length; z++) {
                        VariableElement varElm = (VariableElement) paramsType.get(z);
                        paramTable[z] = new MethodInfo.ParameterInfo(varElm.getSimpleName().toString(), varElm.asType().toString(), null);
                    }

                    methodInfo.setParameterInfo(paramTable);
                    methods.add(methodInfo);

                } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                    //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                } else {
                    //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                }
            }

        }
    }

    public static List getMethodsForPublishEvent(List<MethodInfo> enclosedElements) {
        List methods = new ArrayList();
        for (MethodInfo enclosedElement : enclosedElements) {
           // String methodName = enclosedElement.getMethodName();//enclosedElement.getSimpleName().toString();
          /*  if (!methodName.equals("processAction") && !methodName.equals("processEvent")) //NOI18N
            {
                continue;
            }*/
            //ExecutableElement methodElm = (ExecutableElement) enclosedElement;
            //List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();

            /*if (paramsType.size() != 2) {
            continue;
            }*/

            MethodInfo.ParameterInfo[] paramTable = enclosedElement.getParameterInfo();
            if (paramTable == null || paramTable.length != 2) {
                continue;

            //VariableElement v = (VariableElement) paramsType.get(0);
            //String type1 = v.asType().toString();
            }
            String type1 = paramTable[0].getType();
            //String type2 = ((VariableElement) paramsType.get(1)).asType().toString();
            String type2 = paramTable[1].getType();
            if (((type1.equals("javax.portlet.ActionRequest") || type1.equals("ActionRequest")) //NOI18N
                    && (type2.equals("javax.portlet.ActionResponse") || type2.equals("ActionResponse"))) || //NOI18N
                    ((type1.equals("javax.portlet.EventRequest") || type1.equals("EventRequest")) //NOI18N
                    && (type2.equals("javax.portlet.EventResponse") || type2.equals("EventResponse")))) //NOI18N
            {
                methods.add(enclosedElement);
            }
        }
        return methods;

    }

    public static MethodInfo getHandleProcessEventMethod(List<MethodInfo> methods) {

        for (MethodInfo method : methods) {
            String methodName = method.getMethodName();
            if (!methodName.equals("processEvent")) //NOI18N
            {
                continue;
            }

            MethodInfo.ParameterInfo[] paramTable = method.getParameterInfo();
            if (paramTable == null || paramTable.length != 2) {
                continue;

            //VariableElement v = (VariableElement) paramsType.get(0);
            //String type1 = v.asType().toString();
            }
            String type1 = paramTable[0].getType();
            //String type2 = ((VariableElement) paramsType.get(1)).asType().toString();
            String type2 = paramTable[1].getType();
            if (((type1.equals("javax.portlet.EventRequest") || type1.equals("EventRequest")) //NOI18N
                    && (type2.equals("javax.portlet.EventResponse") || type2.equals("EventResponse")))) //NOI18N
            {
                return method;
            }
        }
        return null;
    }

    private static class MemberVisitor extends TreePathScanner<Void, Void> {

        private CompilationInfo info;
        private List methods;

        public MemberVisitor(CompilationInfo info, List methods) {
            this.info = info;
            this.methods = methods;
        }

        //  @Override
        public Void visitClass(ClassTree t, Void v) {
            Element el = info.getTrees().getElement(getCurrentPath());

            if (el == null) {
                StatusDisplayer.getDefault().setStatusText("Cannot resolve class!");
            } else {
                TypeElement te = (TypeElement) el;
                List enclosedElements = te.getEnclosedElements();
                InputOutput io = IOProvider.getDefault().getIO("Analysis of " + info.getFileObject().getName(), true);
                for (int i = 0; i < enclosedElements.size(); i++) {
                    Element enclosedElement = (Element) enclosedElements.get(i);
                    if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                        //    io.getOut().println("Constructor: " + enclosedElement.getSimpleName());
                    } else if (enclosedElement.getKind() == ElementKind.METHOD) {
                        //  io.getOut().println("Method: " + enclosedElement.getSimpleName());
                        methods.add(enclosedElement.getSimpleName());
                    } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                        //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                    } else {
                        //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                    }
                }
                io.getOut().close();
            }
            return null;
        }

        public List getMethods() {
            return methods;
        }
    }
}
