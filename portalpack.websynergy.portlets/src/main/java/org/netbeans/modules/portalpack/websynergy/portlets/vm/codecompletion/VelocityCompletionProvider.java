/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.vm.codecompletion;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities.ElementAcceptor;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TypeUtilities;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.servers.websynergy.common.WebSpacePropertiesUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class VelocityCompletionProvider implements CompletionProvider {

    private static HashMap map;


    static {
        map = new HashMap();
        
        map.put("$request","javax.servlet.HttpServletRequest");
        map.put("$portletConfig","javax.portlet.PortletConfig");
        map.put("$renderRequest","javax.portlet.RenderRequest");
        map.put("$renderResponse","javax.portlet.RenderResponse");
        map.put("$xmlRequest","");
        map.put("$themeDisplay","com.liferay.portal.theme.ThemeDisplay");
        map.put("$company","com.liferay.portal.model.Company");
        map.put("$permissionChecker","com.liferay.portal.security.permission.PermissionChecker");
        map.put("$user","com.liferay.portal.model.User");
        map.put("$realUser","com.liferay.portal.model.User");
        map.put("$colorScheme","com.liferay.portal.model.ColorScheme");
        map.put("$portletDisplay","com.liferay.portal.theme.PortletDisplay");
        map.put("$navItems","java.util.List");
        map.put("$fullCssPath","");
        map.put("$fullTemplatesPath","");
        map.put("$portletGroupId","");
        map.put("$pageTitle","");
        map.put("$pageSubtitle","");
        map.put("$layout","com.liferay.portal.model.Layout");
        map.put("$layouts","java.util.List");
        map.put("$plid","");
        map.put("$layoutTypePortlet","com.liferay.portal.model.LayoutTypePortlet");
        map.put("$scopeGroupId","");
        map.put("$locale","java.util.Locale");
        map.put("$timeZone","java.util.TimeZone");
        map.put("$theme","com.liferay.portal.model.Theme");

    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }

        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {

                String varName = null;
                int startOffset = caretOffset - 1;
                try{
                    FileObject fob = NbEditorUtilities.getFileObject(document);
                    if(!fob.getExt().equalsIgnoreCase("vm")) {
                        completionResultSet.finish();
                        return;
                    }
                    Project project = FileOwnerQuery.getOwner(fob);

                    if(!WebSpacePropertiesUtil.isWebSynergyServer(project)) {
                        completionResultSet.finish();
                        return;
                    }
                }catch(Throwable t) {
                    completionResultSet.finish();
                    return;
                }
                try {
                    final StyledDocument bDoc = (StyledDocument) document;
                    final int lineStartOffset = getRowFirstNonWhite(bDoc, caretOffset);
                    final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                    final int whiteOffset = indexOfWhite(line);
                    varName = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                    if (whiteOffset > 0) {
                        startOffset = lineStartOffset + whiteOffset + 1;
                    } else {
                        startOffset = lineStartOffset;
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }


                //Iterate through the available locales
                //and assign each country display name
                //to a CompletionResultSet:
//                Locale[] locales = Locale.getAvailableLocales();
//                for (int i = 0; i < locales.length; i++) {
//                    final Locale locale = locales[i];
//                    final String country = locale.getDisplayCountry();
//                    if (!country.equals("") && filter.startsWith("$request.")) {
//                        completionResultSet.addItem(new VelocityCompletionItem(country, startOffset,caretOffset));
//                    }
//                }

                int i = varName.lastIndexOf("$");
                if(i != -1) {
                    varName = varName.substring(i);
                }
                int bi = varName.indexOf('(');
                int doti = varName.lastIndexOf(".");
                if (i != -1 && bi == -1 && doti != -1) {

                    String filter = "";
                    if(varName.endsWith(".")) {
                        varName = varName.substring(0, varName.length() - 1);
                    } else {
                        if(varName.length() > doti + 1)
                            filter = varName.substring(doti + 1);
                        //if(varName.length() > i + 1) {
                            varName = varName.substring(0,doti);
                        //}

                    }

                    System.out.println("Filter::: "+filter);
                    System.out.println("VarName::: "+varName);

                    // System.out.println("filteris ::::::::::::::::::: " + filter);
                    final String implClass = (String) map.get(varName);
                    //if(filter.startsWith("$eventResponse")) {
                    if (implClass != null && implClass.trim().length() != 0) {
                        FileObject fObject = NbEditorUtilities.getFileObject(document);
                        Project project = FileOwnerQuery.getOwner(fObject);
                        WebModule wm = PortletProjectUtils.getWebModule(project);
                        if (wm != null) {

                            ClassPath cp = ClassPath.getClassPath(wm.getDocumentBase(), ClassPath.COMPILE);

                            ClasspathInfo info = ClasspathInfo.create(wm.getDocumentBase());
                            JavaSource source = JavaSource.create(info);
                            final List<MethodInfo> methods = new ArrayList();
                            final List fields = new ArrayList();
                            try {
                                source.runUserActionTask(new Task<CompilationController>() {

                                    public void run(CompilationController info) throws Exception {
                                        Elements elements = info.getElements();
                                        TypeElement te = elements.getTypeElement(implClass);
                                        if(te == null)
                                            return;
                                        getMethodsAndFields(info, te, methods,fields);

                                        if (te.getKind() == javax.lang.model.element.ElementKind.INTERFACE) {
                                            List<? extends TypeMirror> intTypeMirror = te.getInterfaces();

                                            for (TypeMirror intf : intTypeMirror) {

                                                TypeElement intfElm = elements.getTypeElement(intf.toString());
                                                if(intfElm == null)
                                                    continue;
                                                if(intfElm.getKind() == javax.lang.model.element.ElementKind.INTERFACE)
                                                    getMethodsAndFields(info, intfElm, methods,fields);

                                            }
                                        }
                                        //methods from super classes
                                        TypeMirror mirror = te.getSuperclass();

                                        if(mirror.getKind() != TypeKind.NONE) {
                                            TypeElement superElm = elements.getTypeElement(mirror.toString());
                                            if(superElm != null) {
                                                getMethodsAndFields(info, superElm, methods,fields);
                                            }
                                        }
                                    }
                                }, true);
                            } catch (IOException ex) {
                               ex.printStackTrace();
                            }


                            for (Object f : fields) {
                                completionResultSet.addItem(new VelocityCompletionItem((String)f,(String)f, null, startOffset, caretOffset));
                            }

                            for (MethodInfo method : methods) {
                                if(method.getMethodName().startsWith(filter)) {
                                    completionResultSet.addItem(new VelocityCompletionItem(method.toString(),method.getSignature(), method.getReturnType(), startOffset, caretOffset));


                                }
                            }
                        }
                    }
                } else {

                    Set keys = map.keySet();
                    for (Object key : keys) {
                        completionResultSet.addItem(new VelocityCompletionItem((String) key,(String) key, null,startOffset, caretOffset));
                    }
                }

                completionResultSet.finish();

            }
        }, component);

    }

    private static void getMethods(CompilationInfo info, javax.lang.model.element.Element te, List<MethodInfo> methods) {
        getMethodsAndFields(info, te, methods, null);
    }

    private static void getMethodsAndFields(CompilationInfo info, javax.lang.model.element.Element te, List<MethodInfo> methods, List fields) {
        //Element el = info.getTrees().getElement(getCurrentPath());

        if (te == null) {
            //       StatusDisplayer.getDefault().setStatusText("Cannot resolve class!");
        } else {
            //    TypeElement te = (TypeElement) el;
            List enclosedElements = te.getEnclosedElements();
            //InputOutput io = IOProvider.getDefault().getIO("Analysis of " + info.getFileObject().getName(), true);
            for (int i = 0; i < enclosedElements.size(); i++) {
                javax.lang.model.element.Element enclosedElement = (javax.lang.model.element.Element) enclosedElements.get(i);

                if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                    //    io.getOut().println("Constructor: " + enclosedElement.getSimpleName());
                } else if (enclosedElement.getKind() == ElementKind.METHOD) {
                    //  io.getOut().println("Method: " + enclosedElement.getSimpleName());
                    //String methodName = enclosedElement.getSimpleName().toString();
                    ///methods.add((ExecutableElement) enclosedElement);
                    try{
                    ExecutableElement methodElm = (ExecutableElement) enclosedElement;
                    MethodInfo methodInfo = new MethodInfo(methodElm.getSimpleName().toString());

                    methodInfo.setReturnType(methodElm.getReturnType().toString());
                    List<VariableElement> paramsType = (List<VariableElement>) methodElm.getParameters();
                    MethodInfo.ParameterInfo[] paramTable = new MethodInfo.ParameterInfo[paramsType.size()];
                    for (int z = 0; z < paramTable.length; z++) {
                        VariableElement varElm = (VariableElement) paramsType.get(z);
                        
                        paramTable[z] = new MethodInfo.ParameterInfo(varElm.getSimpleName().toString(), varElm.asType().toString(), null);
                    }

                    methodInfo.setParameterInfo(paramTable);
                    methods.add(methodInfo);
                    }catch(java.lang.AssertionError e) {
                        e.printStackTrace();
                    }

                } else if (enclosedElement.getKind() == ElementKind.FIELD) {
                    try{
                        if(fields != null) {
                            Set<Modifier> modifiers = enclosedElement.getModifiers();
                            
                            for(Modifier modifier:modifiers) {
                                if(modifier == Modifier.PUBLIC) {
                                    fields.add(enclosedElement.getSimpleName().toString());
                                    break;
                                }
                            }
                            
                        }
                    }catch(java.lang.AssertionError e) {
                        e.printStackTrace();
                    }
                    //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                } else {
                    //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                }
            }



        }
    }


    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }

    static int getRowFirstNonWhite(StyledDocument doc, int offset)
            throws BadLocationException {
        if (doc == null) {
            return 0;
        }
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    static int indexOfWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }

    static int indexOf$(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (c == '$') {
                return i;
            }
        }
        return -1;
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
            javax.lang.model.element.Element el = info.getTrees().getElement(getCurrentPath());

            if (el == null) {
                //  StatusDisplayer.getDefault().setStatusText("Cannot resolve class!");
            } else {
                TypeElement te = (TypeElement) el;
                List enclosedElements = te.getEnclosedElements();
                //InputOutput io = IOProvider.getDefault().getIO("Analysis of " + info.getFileObject().getName(), true);
                for (int i = 0; i < enclosedElements.size(); i++) {
                    javax.lang.model.element.Element enclosedElement = (javax.lang.model.element.Element) enclosedElements.get(i);
                    if (enclosedElement.getKind() == javax.lang.model.element.ElementKind.CONSTRUCTOR) {
                        //    io.getOut().println("Constructor: " + enclosedElement.getSimpleName());
                    } else if (enclosedElement.getKind() == javax.lang.model.element.ElementKind.METHOD) {
                        //  io.getOut().println("Method: " + enclosedElement.getSimpleName());
                        methods.add(enclosedElement.getSimpleName());
                    } else if (enclosedElement.getKind() == javax.lang.model.element.ElementKind.FIELD) {
                        //io.getOut().println("Field: " + enclosedElement.getSimpleName());
                    } else {
                        //io.getOut().println("Other: " + enclosedElement.getSimpleName());
                    }
                }
            }
            return null;
        }

        public List getMethods() {
            return methods;
        }
    }
}
