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
package org.netbeans.modules.scala.editing;

import org.netbeans.modules.scala.editing.nodes.ScalaTypeInferencer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.SourceFileReader;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.modules.gsf.spi.DefaultParseListener;
import org.netbeans.modules.gsf.spi.DefaultParserFile;
import org.netbeans.modules.java.source.usages.VirtualSourceProvider;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.Function;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.modules.scala.editing.nodes.Var;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.scala.util.NbUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Virtual java source
 *
 * @author Caoyuan Deng
 */
public class ScalaVirtualSourceProvider implements VirtualSourceProvider {

    public Set<String> getSupportedExtensions() {
        return Collections.singleton("scala"); // NOI18N

    }

    public void translate(Iterable<File> files, File sourceRoot, Result result) {
        FileObject rootFO = FileUtil.toFileObject(sourceRoot);
        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            File file = it.next();
            /** @Todo */
            List<Template> templates = getTemplates(file);
            if (templates.isEmpty()) {
                // source is probably broken and there is no AST
                // let's generate empty Java stub with simple name equal to file name
                FileObject fo = FileUtil.toFileObject(file);
                String pkg = FileUtil.getRelativePath(rootFO, fo.getParent());
                if (pkg != null) {
                    pkg = pkg.replace('/', '.');
                    StringBuilder sb = new StringBuilder();
                    if (!pkg.equals("")) { // NOI18N
                        sb.append("package " + pkg + ";"); // NOI18N
                    }
                    String name = fo.getName();
                    sb.append("public class ").append(name).append(" implements scala.ScalaObject {}"); // NOI18N
                    result.add(file, pkg, file.getName(), sb.toString());
                }
            } else {
                FileObject fo = FileUtil.toFileObject(file);
                ScalaIndex index = ScalaIndex.get(fo);
                JavaStubGenerator generator = new JavaStubGenerator(index);
                for (Template template : templates) {
                    try {
                        CharSequence javaStub = generator.generateClass(template);
                        Packaging packaging = template.getPackageElement();
                        String pkgName = packaging == null ? "" : packaging.getQualifiedName().toString();
                        result.add(file, pkgName, template.getSimpleName().toString(), javaStub);
                        break;
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Template> getTemplates(File file) {
        List<Template> resultList = new ArrayList<Template>();

        final FileObject fo = FileUtil.toFileObject(file);
        ParserFile parserFile = new DefaultParserFile(fo, null, false);
        if (parserFile != null) {
            /** @Note: do not use CompilationInfo to parse it? which may cause "refershing workspace" */
            List<ParserFile> files = Collections.singletonList(parserFile);
            SourceFileReader reader =
                    new SourceFileReader() {

                        public CharSequence read(ParserFile file) throws IOException {
                            Document doc = NbUtilities.getBaseDocument(fo, true);

                            if (doc == null) {
                                return "";
                            }

                            try {
                                return doc.getText(0, doc.getLength());
                            } catch (BadLocationException ble) {
                                IOException ioe = new IOException();
                                ioe.initCause(ble);
                                throw ioe;
                            }
                        }

                        public int getCaretOffset(ParserFile fileObject) {
                            return -1;
                        }
                    };

            DefaultParseListener listener = new DefaultParseListener();

            TranslatedSource translatedSource = null; // TODO - determine this here?
            Parser.Job job = new Parser.Job(files, listener, reader, translatedSource);
            new ScalaParser().parseFiles(job);

            ScalaParserResult pResult = (ScalaParserResult) listener.getParserResult();
            if (pResult != null) {
                //ScalaIndex index = ScalaIndex.get(fo);
                //pResult.toGlobalPhase(index);
//                AstScope rootScope = pResult.getRootScope();
//                if (rootScope != null) {
//                    List<Template> templates = new ArrayList<Template>();
//                    scan(rootScope, templates);
//
//                    resultList.addAll(templates);
//                }
            } else {
                assert false : "Parse result is null : " + fo.getName();
            }
        }

        return resultList;
    }

    private static void scan(AstScope scope, List<Template> templates) {
        for (AstElement element : scope.getElements()) {
            if (element instanceof Template) {
                templates.add((Template) element);
            }
        }

        for (AstScope _scope : scope.getScopes()) {
            scan(_scope, templates);
        }
    }

    @SuppressWarnings("unchecked")
    private class JavaStubGenerator {

        private boolean java5 = false;
        private boolean requireSuperResolved = false;
        private List toCompile = new ArrayList();
        private ScalaIndex index;

        public JavaStubGenerator(final boolean requireSuperResolved, final boolean java5, ScalaIndex index) {
            this.requireSuperResolved = requireSuperResolved;
            this.java5 = java5;
            this.index = index;
        }

        public JavaStubGenerator(ScalaIndex index) {
            this(false, false, index);
        }

        public CharSequence generateClass(Template template) throws FileNotFoundException {

            String fileName = template.getQualifiedName().toString().replace('.', '/');
            toCompile.add(fileName);

            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);

            try {
                Packaging packaging = template.getPackageElement();
                if (packaging != null) {
                    out.print("package ");
                    out.print(packaging.getQualifiedName());
                    out.println(";");
                }

                //genImports(template, out);

                //out.println("@NetBeansVirtualSource(11, 12)");

                printModifiers(out, template.getModifiers());
                out.print(" class ");
                out.print(template.getSimpleName());
                Type superClass = template.getSuperclass();
                if (superClass != null) {
                    out.print(" extends ");
                    if (Type.isResolved(superClass)) {
                        out.print(Type.qualifiedNameOf(superClass));
                    } else {
                        out.print(Type.simpleNameOf(superClass));
                    }
                }
                out.print(" implements scala.ScalaObject ");

                for (Type trait : template.getInterfaces()) {
                    if (Type.isResolved(trait)) {
                        out.print(Type.qualifiedNameOf(trait));
                    } else {
                        out.print(Type.simpleNameOf(trait));
                    }
                }

                out.println(" {");

                List<? extends AstElement> elements = template.getEnclosedElements();
                for (AstElement element : elements) {
                    if (element instanceof Function) {
                        Function function = (Function) element;
                        printModifiers(out, function.getModifiers());
                        out.print(" ");

                        if (element.getKind() != ElementKind.CONSTRUCTOR) {
                            TypeMirror retType = function.getReturnType();
                            printType(out, retType, index);
                            out.print(" ");
                        }

                        String opName = function.getSimpleName().toString();
                        out.print(JavaScalaMapping.scalaOpNameToJava(opName));
                        out.print("(");

                        Iterator<? extends VariableElement> itr = function.getParameters().iterator();
                        while (itr.hasNext()) {
                            VariableElement param = itr.next();

                            TypeMirror paramType = param.asType();
                            printType(out, paramType, index);
                            out.print(" ");
                            out.print(param.getSimpleName());

                            if (itr.hasNext()) {
                                out.print(",");
                            }
                        }
                        out.println(") {return null;}");
                    } else if (element instanceof Var) {
                        Var var = (Var) element;
                        if (var.getKind() == ElementKind.FIELD) {
                            printModifiers(out, var.getModifiers());
                            out.print(" ");

                            TypeMirror varType = var.asType();
                            printType(out, varType, index);
                            out.print(" ");

                            out.print(var.getSimpleName());
                            out.println(";");
                        }
                    }
                }

                out.println("public int $tag() throws java.rmi.RemoteException {return 0;}");

                out.println("}");
            } finally {
                try {
                    out.close();
                } catch (Exception ex) {
                    // ignore
                }
                try {
                    sw.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
            return sw.toString();
        }

        private void printModifiers(PrintWriter out, Set<Modifier> modifiers) {
            if (modifiers.contains(Modifier.PRIVATE)) {
                out.print("private");
            } else if (modifiers.contains(Modifier.PROTECTED)) {
                out.print("protected");
            } else {
                out.print("public");
            }
        }

        private void printType(PrintWriter out, TypeMirror type, ScalaIndex index) {
            String typeName = null;
            if (type != null) {
                if (Type.isResolved(type)) {
                    typeName = Type.qualifiedNameOf(type);
                } else {
                    if (type instanceof Type) {
                        TypeElement te = ScalaTypeInferencer.resolveType((Type) type, index);
                        if (te != null) {
                            typeName = te.getQualifiedName().toString();
                        }
                    }

                    if (typeName == null) {
                        typeName = Type.simpleNameOf(type);
                    }
                }
            } else {
                typeName = "Object"; //@todo "Unit"?
            }

            out.print(typeName);
        }
    }

}
