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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.javamodel;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.Comment.Style;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;

import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.ErrorManager;
import org.openide.execution.ExecutorTask;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import static org.netbeans.api.java.source.JavaSource.Phase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Satya
 */
public class Utils {

    public static boolean isEqualTo(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        } else {
            return str1.equals(str2);
        }
    }

    public static String getLocalServiceClass(String entityName) {

        return entityName + "LocalServiceImpl";
    }

    public static String getRemoteServiceClass(String entityName) {

        return entityName + "ServiceImpl";
    }

    public static void populateModel(Project project, String entityName) {

        String localClass = getLocalServiceClass(entityName);
        String remoteClass = getRemoteServiceClass(entityName);

        FileObject[] fob = findJavaFileObj(project, localClass);
        if (fob != null && fob.length != 0) {
            // populateModel(implClass, serviceModel);
        }

    }

    public static FileObject[] findJavaFileObj(Project project, String className) {
        className = className.replace(".", File.separator);
        Sources sources = (Sources) project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        List fileObjs = new ArrayList();
        for (int i = 0; i < groups.length; i++) {
            File f = new File(FileUtil.toFile(groups[i].getRootFolder()), className + ".java");
            if (!f.exists()) {
                f = new File(FileUtil.toFile(groups[i].getRootFolder()), className + ".JAVA");
                if (!f.exists()) {
                    continue;
                } else {
                    fileObjs.add(FileUtil.toFileObject(f));
                }
            } else {
                fileObjs.add(FileUtil.toFileObject(f));
            }
        }
        return (FileObject[]) fileObjs.toArray(new FileObject[0]);
    }

    private void getMethods(TypeElement te, List<MethodModel> methods) {
        //Element el = info.getTrees().getElement(getCurrentPath());

        if (te == null) {
            //StatusDisplayer.getDefault().setStatusText("Cannot resolve class!");
            return;
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
                    if (methodElm.getModifiers().contains(Modifier.PUBLIC)) {
                        MethodModel method = new MethodModel();
                        method.setOperationName(methodElm.getSimpleName().toString());

                        List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();
                        List<ParamModel> paramList = new ArrayList();
                        for (int z = 0; z < paramsType.size(); z++) {
                            VariableElement varElm = (VariableElement) paramsType.get(z);
                            ParamModel paramModel = new ParamModel();
                            paramModel.setName(varElm.getSimpleName().toString());
                            paramModel.setParamType(varElm.asType().toString());
                        }

                        method.setParams(paramList);
                    }

                } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                    //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                } else {
                    //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                }
            }

        }
    }

    public static void populateModel(final FileObject implClass, final ServiceModel serviceModel) {
        JavaSource javaSource = JavaSource.forFileObject(implClass);
        if (javaSource != null) {
            CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {

                public void run(CompilationController controller) throws IOException {
                    controller.toPhase(Phase.ELEMENTS_RESOLVED);
                    //CompilationUnitTree cut = controller.getCompilationUnit();

                    TypeElement classEl = SourceUtils.getPublicTopLevelElement(controller);
                    if (classEl != null) {

                        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
                        for (Element member : classEl.getEnclosedElements()) {
                            if (member.getKind() == ElementKind.METHOD) {
                                ExecutableElement methodEl = (ExecutableElement) member;
                                if (methodEl.getModifiers().contains(Modifier.PUBLIC)) {
                                    methods.add(methodEl);
                                }
                            }
                        }
                        // populate methods

                        List<MethodModel> operations = new ArrayList<MethodModel>();
                        if (methods.size() == 0) {
                            serviceModel.operations = operations;
                            serviceModel.status = ServiceModel.STATUS_INCORRECT_SERVICE;
                            return;
                        }
                        
                        // populate methods
                        for (int i = 0; i < methods.size(); i++) {
                            
                            MethodModel operation = new MethodModel();
                            operation.setImplementationClass(implClass);
                            
                            ElementHandle methodHandle = ElementHandle.create(methods.get(i));
                            operation.setMethodHandle(methodHandle);
                            Utils.populateOperation(controller, methods.get(i), methodHandle, operation);
                            operations.add(operation);
                        }
                        serviceModel.operations = operations;
                    } else {
                        serviceModel.status = ServiceModel.STATUS_INCORRECT_SERVICE;
                    }
                }

                public void cancel() {
                }
            };

            try {
                javaSource.runUserActionTask(task, true);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    private static void populateOperation(CompilationController controller, ExecutableElement methodEl, ElementHandle methodHandle, MethodModel methodModel) {
    
        ResultModel resultModel = new ResultModel();
    
        methodModel.javaName = methodEl.getSimpleName().toString();
        methodModel.operationName = methodModel.javaName;
        
        
            TypeMirror returnType = methodEl.getReturnType();
            if (returnType.getKind() == TypeKind.DECLARED) {
                TypeElement element = (TypeElement) ((DeclaredType) returnType).asElement();
                resultModel.setResultType(element.getQualifiedName().toString());
            } else { // for primitive types

                resultModel.setResultType(returnType.toString());
            }
        
        methodModel.setResult(resultModel);


        // populate faults
        List<? extends TypeMirror> faultTypes = methodEl.getThrownTypes();
        List<FaultModel> faults = new ArrayList<FaultModel>();
        for (TypeMirror faultType : faultTypes) {
            FaultModel faultModel = new FaultModel();
            boolean faultFound = false;
            if (faultType.getKind() == TypeKind.DECLARED) {
                TypeElement faultEl = (TypeElement) ((DeclaredType) faultType).asElement();
//                TypeElement faultAnotationEl = controller.getElements().getTypeElement("javax.xml.ws.WebFault"); //NOI18N

                /*List<? extends AnnotationMirror> faultAnnotations = faultEl.getAnnotationMirrors();
                for (AnnotationMirror anMirror : faultAnnotations) {
                    if (controller.getTypes().isSameType(faultAnotationEl.asType(), anMirror.getAnnotationType())) {
                        Map<? extends ExecutableElement, ? extends AnnotationValue> expressions = anMirror.getElementValues();
                        for (ExecutableElement ex : expressions.keySet()) {
                            if (ex.getSimpleName().contentEquals("name")) { //NOI18N

                                faultModel.setName((String) expressions.get(ex).getValue());
                                faultFound = true;
                            } else if (ex.getSimpleName().contentEquals("targetNamespace")) { //NOI18N

                                faultModel.setTargetNamespace((String) expressions.get(ex).getValue());
                            }
                        }
                    }
                }*/
                faultModel.setFaultType(faultEl.getQualifiedName().toString());
            } else {
                faultModel.setFaultType(faultType.toString());
            }
            /*
            if (!faultFound) {
                String fullyQualifiedName = faultModel.getFaultType();
                int index = fullyQualifiedName.lastIndexOf("."); //NOI18N

                faultModel.setName(index >= 0 ? fullyQualifiedName.substring(index + 1) : fullyQualifiedName);
            }*/
            faults.add(faultModel);
        }
        methodModel.setFaults(faults);

        // populate javadoc
        Doc javadoc = controller.getElementUtilities().javaDocFor(methodEl);
        if (javadoc != null) {
            //methodModel.setJavadoc(javadoc.getRawCommentText());
            JavadocModel javadocModel = new JavadocModel(javadoc.getRawCommentText());
            // @param part
            Tag[] paramTags = javadoc.tags("@param"); //NOI18N

            List<String> paramJavadoc = new ArrayList<String>();
            for (Tag paramTag : paramTags) {
                paramJavadoc.add(paramTag.text());
            }
            javadocModel.setParamJavadoc(paramJavadoc);

            // @return part
            Tag[] returnTags = javadoc.tags("@return"); //NOI18N

            if (returnTags.length > 0) {
                javadocModel.setReturnJavadoc(returnTags[0].text());
            }
            // @throws part
            Tag[] throwsTags = javadoc.tags("@throws"); //NOI18N

            List<String> throwsJavadoc = new ArrayList<String>();
            for (Tag throwsTag : throwsTags) {
                throwsJavadoc.add(throwsTag.text());
            }
            javadocModel.setThrowsJavadoc(throwsJavadoc);

            // rest part
            Tag[] inlineTags = javadoc.inlineTags(); //NOI18N

            List<String> inlineJavadoc = new ArrayList<String>();
            for (Tag inlineTag : inlineTags) {
                throwsJavadoc.add(inlineTag.text());
            }
            javadocModel.setInlineJavadoc(inlineJavadoc);
            methodModel.setJavadoc(javadocModel);
        }


        // populate params
        List<? extends VariableElement> paramElements = methodEl.getParameters();
        List<ParamModel> params = new ArrayList<ParamModel>();
        int i = 0;
        for (VariableElement paramEl : paramElements) {
            ParamModel param = new ParamModel("arg" + String.valueOf(i++), paramEl.getSimpleName().toString());
            param.setImplementationClass(methodModel.getImplementationClass());
            param.setMethodHandle(methodHandle);
            populateParam(paramEl, param);
            params.add(param);
        }
        methodModel.setParams(params);

        // set SOAP Request
       // setSoapRequest(methodModel, targetNamespace);

    // set SOAP Response
//TODO        setSoapResponse(methodModel, targetNamespace);
    }

    private static void populateParam(VariableElement paramEl, ParamModel paramModel) {
        TypeMirror type = paramEl.asType();
        if (type.getKind() == TypeKind.DECLARED) {
            TypeElement element = (TypeElement) ((DeclaredType) type).asElement();
            paramModel.setParamType(element.getQualifiedName().toString());
        } else { // for primitive type

            paramModel.setParamType(type.toString());
        }
    }

    private static void setSoapRequest(MethodModel methodModel, String tns) {
        /*  MessageFactory messageFactory = null;
        try {
        // create a sample SOAP request using SAAJ API
        messageFactory = MessageFactory.newInstance();
        } catch (SOAPException ex) {
        Logger.getLogger(Utils.class.getName()).log(Level.FINE, 
        NbBundle.getMessage(Utils.class, "MSG_SAAJ_PROBLEM"), //NOI18N
        ex);
        }
        if (messageFactory != null) {
        try {
        SOAPMessage request = messageFactory.createMessage();
        MimeHeaders headers = request.getMimeHeaders();
        String action = methodModel.getAction();
        headers.addHeader("SOAPAction", action==null? "\"\"":action); //NOI18N
        SOAPPart part = request.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        String prefix = envelope.getPrefix();
        if (!"soap".equals(prefix)) { //NOI18N
        envelope.removeAttribute("xmlns:"+prefix); // NOI18N
        envelope.setPrefix("soap"); //NOI18N
        }
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap"); //NOI18N
        
        // removing soap header
        SOAPHeader header = envelope.getHeader();
        envelope.removeChild(header);
        
        // implementing body
        Name methodName = envelope.createName(methodModel.getOperationName());
        SOAPElement methodElement = body.addBodyElement(methodName);
        methodElement.setPrefix("ns0"); //NOI18N
        methodElement.addNamespaceDeclaration("ns0",tns); //NOI18N
        
        // params
        List<ParamModel> params = methodModel.getParams();
        int i=0;
        for (ParamModel param:params) {
        String paramNs = param.getTargetNamespace();
        Name paramName = null;
        if (paramNs!=null && paramNs.length()>0) {
        String pref = "ns"+String.valueOf(++i); //NOI18N
        paramName = envelope.createName(param.getName(), pref, paramNs);
        methodElement.addNamespaceDeclaration(pref,paramNs);
        } else {
        paramName = envelope.createName(param.getName());
        }
        
        SOAPElement paramElement = methodElement.addChildElement(paramName);
        
        String paramType = param.getParamType();
        if ("javax.xml.namespace.QName".equals(paramType)) {
        paramElement.addNamespaceDeclaration("sampleNs", "http://www.netbeans.org/sampleNamespace");
        paramElement.addTextNode("sampleNs:sampleQName");
        } else {
        paramElement.addTextNode(getSampleValue(paramType));
        }
        }
        
        methodModel.setSoapRequest(request);
        
        } catch (SOAPException ex) {
        ErrorManager.getDefault().notify(ex);
        }
        }*/
    }

    private static String getSampleValue(String paramType) {
        if ("java.lang.String".equals(paramType)) {
            return "sample text"; //NOI18N

        } else if ("int".equals(paramType) || //NOI18N
                "java.lang.Integer".equals(paramType) || //NOI18N
                "java.math.BigInteger".equals(paramType)) { //NOI18N

            return "99"; //NOI18N

        } else if ("double".equals(paramType) || "java.lang.Double".equals(paramType)) { //NOI18N

            return "999.999"; //NOI18N

        } else if ("float".equals(paramType) || //NOI18N
                "java.lang.Float".equals(paramType) || //NOI18N
                "java.math.BigDecimal".equals(paramType)) {//NOI18N

            return "99.99"; //NOI18N

        } else if ("long".equals(paramType) || "java.lang.Long".equals(paramType)) { //NOI18N

            return "999"; //NOI18N

        } else if ("boolean".equals(paramType) || "java.lang.Boolean".equals(paramType)) { //NOI18N

            return "false"; //NOI18N

        } else if ("char".equals(paramType) || //NOI18N
                "java.lang.Char".equals(paramType) || //NOI18N
                "short".equals(paramType) || //NOI18N
                "java.lang.Short".equals(paramType)) { //NOI18N

            return "65"; //NOI18N

        } else if ("byte[]".equals(paramType)) { //NOI18N

            return "73616D706C652074657874"; //NOI18N

        } else if ("javax.xml.datatype.XMLGregorianCalendar".equals(paramType) || //NOI18N
                "java.util.Date".equals(paramType) || //NOI18N
                "java.util.Calendar".equals(paramType) || //NOI18N
                "java.util.GregorianCalendar".equals(paramType)) { //NOI18N

            return "2007-04-19"; //NOI18N

        } else if ("javax.xml.datatype.Duration".equals(paramType)) { //NOI18N

            return "P2007Y4M"; //NOI18N

        } else if ("java.net.URI".equals(paramType) || "java.net.URL".equals(paramType)) { //NOI18N

            return "http://www.netbeans.org/sampleURI"; //NOI18N

        } else {
            return "..."; //NOI18N

        }
    }

    public static void setJavadoc(final FileObject implClass, final MethodModel methodModel, final String text) {
        JavaSource javaSource = JavaSource.forFileObject(implClass);
        CancellableTask<WorkingCopy> modificationTask = new CancellableTask<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();
                ClassTree classTree = null;//TODOSourceUtils.getPublicTopLevelTree(workingCopy);

                List<? extends Tree> members = classTree.getMembers();
                TypeElement methodAnotationEl = workingCopy.getElements().getTypeElement("javax.jws.WebMethod"); //NOI18N

                if (methodAnotationEl == null) {
                    return;
                }
                MethodTree targetMethod = null;
                for (Tree member : members) {
                    if (Tree.Kind.METHOD == member.getKind()) {
                        MethodTree method = (MethodTree) member;
                        TreePath methodPath = workingCopy.getTrees().getPath(workingCopy.getCompilationUnit(), method);
                        ExecutableElement methodEl = (ExecutableElement) workingCopy.getTrees().getElement(methodPath);
                        // browse annotations to find target method
                        List<? extends AnnotationMirror> methodAnnotations = methodEl.getAnnotationMirrors();
                        for (AnnotationMirror anMirror : methodAnnotations) {
                            if (workingCopy.getTypes().isSameType(methodAnotationEl.asType(), anMirror.getAnnotationType())) {
                                Map<? extends ExecutableElement, ? extends AnnotationValue> expressions = anMirror.getElementValues();
                                for (ExecutableElement ex : expressions.keySet()) {
                                    if (ex.getSimpleName().contentEquals("operationName")) { //NOI18N

                                        if (methodModel.getOperationName().equals(expressions.get(ex).getValue())) {
                                            targetMethod = method;
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        // if annotation not found check method name
                        if (targetMethod != null) {
                            break;
                        } else if (method.getName().contentEquals(methodModel.getOperationName())) {
                            targetMethod = method;
                            break;
                        }
                    }

                }
                if (targetMethod != null) {
                    Comment comment = Comment.create(Style.JAVADOC, 0, 0, 0, text);
                // Issue in Retouche (90302) : the following part couldn't be used for now
                // MethodTree newMethod = make.addComment(targetMethod, comment , true);
                // workingCopy.rewrite(targetMethod, newMethod);
                }

            }

            public void cancel() {
            }
        };
        try {
            javaSource.runModificationTask(modificationTask).commit();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    public static void invokeWsImport(Project project, final String serviceName) {
        /*        if (project!=null) {
        JaxWsModel jaxWsModel = project.getLookup().lookup(JaxWsModel.class);
        if (jaxWsModel != null) {
        // call wsimport only for services from wsdl
        Service service = jaxWsModel.findServiceByName(serviceName);
        if (service != null && service.getWsdlUrl() != null) {
        final FileObject buildImplFo = project.getProjectDirectory().getFileObject("nbproject/build-impl.xml"); //NOI18N
        try {
        ProjectManager.mutex().readAccess(new Mutex.ExceptionAction<Boolean>() {
        public Boolean run() throws IOException {
        ExecutorTask wsimportTask =
        ActionUtils.runTarget(buildImplFo,
        new String[]{"wsimport-service-clean-"+serviceName,"wsimport-service-"+serviceName},null); //NOI18N                                       ActionUtils.runTarget(buildImplFo,new String[]{"wsimport-client-"+finalName,"wsimport-client-compile" },null); //NOI18N
        wsimportTask.waitFinished();
        return Boolean.TRUE;
        }
        }).booleanValue();
        } catch (MutexException e) {
        ErrorManager.getDefault().log(e.getLocalizedMessage());
        }
        }
        }
        }*/
    }

    public static String getCurrentJavaName(final MethodModel method) {
        final String[] javaName = new String[1];
        final JavaSource javaSource = JavaSource.forFileObject(method.getImplementationClass());
        final CancellableTask<WorkingCopy> modificationTask = new CancellableTask<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                ElementHandle methodHandle = method.getMethodHandle();
                Element methodEl = methodHandle.resolve(workingCopy);
                javaName[0] = methodEl.getSimpleName().toString();
            }

            public void cancel() {
            }
        };
        try {
            javaSource.runModificationTask(modificationTask).commit();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return javaName[0];
    }

    /**
     * Obtains the value of an annotation's attribute if that attribute is present.
     * @param clazz The Java source to parse
     * @param annotationClass Fully qualified name of the annotation class
     * @param attributeName Name of the attribute whose value is returned
     * @return String Returns the string value of the attribute. Returns empty string if attribute is not found.
     */
    public static String getAttributeValue(FileObject clazz, final String annotationClass, final String attributeName) {
        JavaSource javaSource = JavaSource.forFileObject(clazz);
        final String[] attributeValue = new String[]{""};
        if (javaSource != null) {
            CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {

                public void run(CompilationController controller) throws IOException {
                    controller.toPhase(Phase.ELEMENTS_RESOLVED);
                    TypeElement typeElement = null; //TODO SourceUtils.getPublicTopLevelElement(controller);

                    TypeElement wsElement = controller.getElements().getTypeElement(annotationClass);
                    if (typeElement != null && wsElement != null) {
                        List<? extends AnnotationMirror> annotations = typeElement.getAnnotationMirrors();
                        for (AnnotationMirror anMirror : annotations) {
                            Map<? extends ExecutableElement, ? extends AnnotationValue> expressions = anMirror.getElementValues();
                            for (ExecutableElement ex : expressions.keySet()) {
                                if (ex.getSimpleName().contentEquals(attributeName)) {
                                    String interfaceName = (String) expressions.get(ex).getValue();
                                    if (interfaceName != null) {
                                        attributeValue[0] = URLEncoder.encode(interfaceName, "UTF-8"); //NOI18N

                                        break;
                                    }
                                }
                            }

                        }
                    }
                }

                public void cancel() {
                }
            };
            try {
                javaSource.runUserActionTask(task, true);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return attributeValue[0];
    }
}
