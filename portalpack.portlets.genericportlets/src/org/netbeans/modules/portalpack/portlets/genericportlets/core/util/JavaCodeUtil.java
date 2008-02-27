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
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    public static boolean addPublishEventCode(FileObject fObject, final String className, final ExecutableElement methodElm, final String methodName, final EventObject event) {

        try {
            JavaSource js = JavaSource.forFileObject(fObject);
            ModificationResult result = js.runModificationTask(new CancellableTask<WorkingCopy>() {

                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    boolean alreadyDefined = false;
                    TreeMaker make = workingCopy.getTreeMaker();
                    CompilationUnitTree cut = workingCopy.getCompilationUnit();
                    //ClassTree clazz = null;
                    MethodTree methodTree = null;
                    //   for (Tree typeDecl : cut.getTypeDecls()) {

                    ClassTree clazz = findClassTree(cut, className);
                    if (methodElm != null) { //add to a existing method

                        Name amethodName = methodElm.getSimpleName();
                        TypeMirror areturnType = methodElm.getReturnType();
                        List<VariableElement> parameters = (List<VariableElement>) methodElm.getParameters();

                        for (int i = 0; i < clazz.getMembers().size(); i++) {
                            Tree member = clazz.getMembers().get(i);
                            if (member.getKind() != Tree.Kind.METHOD) {
                                continue;
                            }
                            methodTree = (MethodTree) member;

                            Name methodName = methodTree.getName();
                            if (methodName.equals(amethodName)) {
                                BlockTree blockTree = methodTree.getBody();
                                String stmt = getPublishEventSrc(event, null);
                               
                                
                                System.out.println(stmt);
                               // stmt = "(" + stmt + "}";
                                StatementTree statement = workingCopy.getTreeUtilities().parseStatement(stmt, new SourcePositions[1]);           
                                BlockTree newBlock = workingCopy.getTreeMaker().addBlockStatement(blockTree, statement);
                                workingCopy.rewrite(blockTree, newBlock);

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
                /*
                methodTree.getBody().
                .get logger.log(Level  .INFO, this.getClass().getName() + ":", methodTree.getName() + "##################");
                String tempMethodName = methodTree.getName().toString();
                if (tempMethodName.equals(methodName)) {
                alreadyDefined = true;
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(NbBundle.getBundle(RefactoringUtil.class).getString("Add_Duplicate_Method"), NbBundle.getBundle(RefactoringUtil.class).getString("Method_Already_Exists"), NotifyDescriptor.OK_CANCEL_OPTION);
                if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                alreadyDefined = false;
                }
                logger.log(Level.INFO, this.getClass().getName() + ":", methodTree.getName());
                break;
                }
                }
                if (!alreadyDefined) {
                // create method modifier: public and no annotation
                com.sun.source.tree.ModifiersTree methodModifiers = make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList());
                //New Code
                FileObject propFileObject = null;
                // FileObject propFileObject = WebModule.getWebModule(tempFileObject).getWebInf().getFileObject("ImplementationType.properties");
                // now, start the method creation
                MethodTree newMethod = make.Method(make.Modifiers(Collections.singleton(Modifier.PUBLIC)), sawMethod.getMethodName(), make.Identifier(sawMethod.getReturnType()), Collections.<TypeParameterTree>emptyList(), parList, exceptionList, sawMethod.getMethodBody(), null);
                ClassTree modifiedClazz = make.addClassMember(clazz, newMethod);
                workingCopy.rewrite(clazz, modifiedClazz);
                }
                } // end for
                // }
                 */
            });
            result.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    /*return couldAddMethod;
    return 
    true;*/
    }

    private static String getPublishEventSrc(EventObject event, String responseObjectName) {
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
            map.put("RESPONSE", "response");
            if (event.getValueType() != null) {
                map.put("VALUE_TYPE", event.getValueType());
            } else {
                map.put("VALUE_TYPE", "");
            }

            FileObject template = TemplateHelper.getTemplateFile("ipcgenerateevent.template");
            StringWriter writer = new StringWriter();
            TemplateHelper.mergeTemplateToWriter(template, writer, map);
            return writer.toString();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    private static String getPublishEventSrc1(EventObject event, String responseObjectName) {
        String src = "";
        if (event.getQName() != null) {
            QName q = event.getQName();

            if (q.getNamespaceURI().equals(XMLConstants.NULL_NS_URI) && q.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                src = "QName qname = new QName(\"" + q.getLocalPart() + "\");";
            } else if (q.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
                src = "QName qname = new QName(null,\"" + q.getLocalPart() + "\",\"" + q.getPrefix() + "\");";
            } else if (q.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                src = "QName qname = new QName(\"" + q.getNamespaceURI() + "\",\"" + q.getLocalPart() + "\",null);";
            } else {
                src = "QName qname = new QName(\"" + q.getNamespaceURI() + "\",\"" + q.getLocalPart() + "\"," + "\"" + q.getPrefix() + "\");";
            }

            src = src + /*event.getValueType() +*/ " Object " + q.getLocalPart() + "Data = null;";
            src += "response.setEvent(qname," + q.getLocalPart() + "Data);";

        } else {
            src = "String " + event.getName() + "Event = \"" + event.getName() + "\";";
            src = src /*+ event.getValueType() */ + "  Object" + event.getName() + "Data = null;";
            src += "response.setEvent(" + event.getName() + "Event," + event.getName() + "Data);";
        }


        System.out.println(src);


        return src;
    }

    public static void implementInterface(String interfaceName) {
    }

    public static boolean isMethodPresent(String methodName, HashMap parameters) {
        return false;
    }

    public static List<ExecutableElement> getMethods(final String className, FileObject fObject) {
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

            System.out.println("Hahahah-----------" + l);
        } catch (IOException e) {
        //Logger.getLogger("global").log(Level.SEVERE, e.getMessage(), e);

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

    private static List<ExecutableElement> getMethods(TypeElement te, List<ExecutableElement> methods) {
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
                    methods.add((ExecutableElement) enclosedElement);


                } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                } else {
                //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                }
            }

        }
        return methods;
    }

    public static List getMethodsForPublishEvent(List<ExecutableElement> enclosedElements) {
        List methods = new ArrayList();
        for (ExecutableElement enclosedElement : enclosedElements) {
            String methodName = enclosedElement.getSimpleName().toString();
            if (!methodName.equals("processAction") && !methodName.equals("processEvent")) //NOI18N
            {
                continue;
            }
            ExecutableElement methodElm = (ExecutableElement) enclosedElement;
            List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();
            if (paramsType.size() != 2) {
                continue;
            }

            VariableElement v = (VariableElement) paramsType.get(0);
            String type1 = v.asType().toString();
            String type2 = ((VariableElement) paramsType.get(1)).asType().toString();
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
