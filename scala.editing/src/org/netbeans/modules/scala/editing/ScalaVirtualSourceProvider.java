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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.SourceFileReader;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.modules.gsf.spi.DefaultParseListener;
import org.netbeans.modules.gsf.spi.DefaultParserFile;
import org.netbeans.modules.gsf.spi.GsfUtilities;
import org.netbeans.modules.java.preprocessorbridge.spi.JavaSourceProvider;
import org.netbeans.modules.java.preprocessorbridge.spi.VirtualSourceProvider;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstScope;
import org.netbeans.modules.scala.editing.ast.ScalaElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.Type;

/**
 * Virtual java source
 *
 * @author Caoyuan Deng
 */
@org.openide.util.lookup.ServiceProviders({@org.openide.util.lookup.ServiceProvider(service = org.netbeans.modules.java.preprocessorbridge.spi.JavaSourceProvider.class), @org.openide.util.lookup.ServiceProvider(service = org.netbeans.modules.java.preprocessorbridge.spi.VirtualSourceProvider.class)})
public class ScalaVirtualSourceProvider implements VirtualSourceProvider, JavaSourceProvider {

    /** @Todo
     * The only reason to implement JavaSourceProvider is to get a none-null JavaSource#forFileObject,
     * the JavaSource instance is a must currently when eval expression under debugging. see issue #150903
     *
     */
    public PositionTranslatingJavaFileFilterImplementation forFileObject(FileObject fo) {
        if (!"text/x-scala".equals(FileUtil.getMIMEType(fo)) && !"scala".equals(fo.getExt())) {  //NOI18N
            return null;
        }

        return new PositionTranslatingJavaFileFilterImplementation() {

            public int getOriginalPosition(int javaSourcePosition) {
                return -1;
            }

            public int getJavaSourcePosition(int originalPosition) {
                return -1;
            }

            public Reader filterReader(Reader r) {
                return r;
            }

            public CharSequence filterCharSequence(CharSequence charSequence) {
                return "";
            }

            public Writer filterWriter(Writer w) {
                return w;
            }

            public void addChangeListener(ChangeListener listener) {
            }

            public void removeChangeListener(ChangeListener listener) {
            }
        };
    }

    public Set<String> getSupportedExtensions() {
        return Collections.singleton("scala"); // NOI18N

    }

    public boolean index() {
        return false;
    /** @Todo */
    }

    public void translate(Iterable<File> files, File sourceRoot, Result result) {
        FileObject rootFO = FileUtil.toFileObject(sourceRoot);
        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            File file = it.next();
            /** @Todo */
            List<AstDef> tmpls = getTemplates(file);
            if (tmpls.isEmpty()) {
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
                    sb.append("public class ").append(name).append(" implements scala.ScalaObject {public int $tag() throws java.rmi.RemoteException {return 0;}}"); // NOI18N
                    result.add(file, pkg, file.getName(), sb.toString());
                }
            } else {
                FileObject fo = FileUtil.toFileObject(file);
                ScalaIndex index = ScalaIndex.get(fo);
                JavaStubGenerator generator = new JavaStubGenerator(index);
                for (AstDef tmpl : tmpls) {
                    try {
                        CharSequence javaStub = generator.generateClass(tmpl);
                        Symbol packaging = tmpl.getSymbol().enclosingPackage();
                        String pkgName = packaging == null ? "" : packaging.fullNameString();
                        if (pkgName.equals("<empty>")) {
                            pkgName = "";
                        }
                        result.add(file, pkgName, tmpl.getSymbol().nameString(), javaStub);
                        break;
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<AstDef> getTemplates(File file) {
        List<AstDef> resultList = new ArrayList<AstDef>();

        final FileObject fo = FileUtil.toFileObject(file);
        ParserFile parserFile = new DefaultParserFile(fo, null, false);
        if (parserFile != null) {
            /** @Note: do not use CompilationInfo to parse it? which may cause "refershing workspace" */
            List<ParserFile> files = Collections.singletonList(parserFile);
            SourceFileReader reader =
                    new SourceFileReader() {

                        public CharSequence read(ParserFile file) throws IOException {
                            Document doc = GsfUtilities.getDocument(fo, true);

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
                AstRootScope rootScope = pResult.getRootScope();
                if (rootScope != null) {
                    List<AstDef> tmpls = new ArrayList<AstDef>();
                    scan(rootScope, tmpls);

                    resultList.addAll(tmpls);
                }
            } else {
                assert false : "Parse result is null : " + fo.getName();
            }
        }

        return resultList;
    }

    private static void scan(AstScope scope, List<AstDef> tmpls) {
        for (AstDef def : scope.getDefs()) {
            if (def.getKind() == ElementKind.CLASS || def.getKind() == ElementKind.MODULE) {
                tmpls.add(def);
            }
        }

        for (AstScope _scope : scope.getSubScopes()) {
            scan(_scope, tmpls);
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

        public CharSequence generateClass(AstDef tmpl) throws FileNotFoundException {
            Symbol symbol = tmpl.getSymbol();
            String fileName = toJavaName(ScalaElement.symbolQualifiedName(symbol)).replace('.', '/');
            toCompile.add(fileName);

            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);

            try {
                Symbol packaging = symbol.enclosingPackage();
                if (packaging != null) {
                    String pkgName = packaging.fullNameString();
                    if (!pkgName.equals("") && !pkgName.equals("<empty>")) {
                        out.print("package ");
                        out.print(packaging.fullNameString());
                        out.println(";");
                    }
                }

                //out.println("@NetBeansVirtualSource(11, 12)");

                printModifiers(out, tmpl.getSymbol());
                if (symbol.isClass()) {
                    out.print(" class ");
                } else if (symbol.isModule()) {
                    // @Todo has two classes;
                    out.print(" class");
                } else if (symbol.isTrait()) {
                    // @Todo has two classes;
                    out.print(" interface");
                }

                // class name
                String clzName = toJavaName(symbol.nameString());
                out.print(clzName);

                Symbol superClass = symbol.superClass();
                if (superClass != null) {
                    String superQName = ScalaElement.symbolQualifiedName(superClass);
                    out.print(" extends ");
                    out.print(superQName);
                }

                scala.List parents = symbol.tpe().parents();
                int n = 0;
                for (int i = 0; i < parents.size(); i++) {
                    Type parent = (Type) parents.apply(i);
                    if (ScalaElement.typeQualifiedName(parent, false).equals("java.lang.Object")) {
                        continue;
                    }

                    if (n == 0) {
                        out.print(" implements ");
                    } else {
                        out.print(",");
                    }
                    printType(out, parent);
                    n++;
                }

                out.println(" {");

/*
                scala.List members = null;
                try {
                    // scalac will throw exceptions here, we have to catch it
                    members = symbol.tpe().members();
                } catch (Throwable e) {
                    ScalaGlobal.reset();
                }
                if (members != null) {
                    int size = members.size();
                    for (int i = 0; i < size; i++) {
                        Symbol member = (Symbol) members.apply(i);

                        if (member.isPublic() || member.isProtectedLocal()) {
                            if (ScalaElement.isInherited(symbol, member)) {
                                continue;
                            }

                            if (member.isMethod()) {
                                if (member.nameString().equals("$init$") || member.nameString().equals("synchronized")) {
                                    continue;
                                }

                                printModifiers(out, member);
                                out.print(" ");
                                if (member.isConstructor()) {
                                    out.print(toJavaName(symbol.nameString()));
                                    // parameters
                                    printParams(out, member.tpe().paramTypes());
                                    out.print(" ");
                                    out.println("{}");
                                } else {
                                    Type resType = null;
                                    try {
                                        resType = member.tpe().resultType();
                                    } catch (Throwable ex) {
                                        ScalaGlobal.reset();
                                    }
                                    if (resType != null) {
                                        String resQName = toJavaType(ScalaElement.typeQualifiedName(resType, false));
                                        out.print(resQName);
                                        out.print(" ");
                                        // method name
                                        out.print(toJavaName(member.nameString()));
                                        // method parameters
                                        printParams(out, member.tpe().paramTypes());
                                        out.print(" ");

                                        // method body
                                        out.print("{");
                                        printReturn(out, resQName);
                                        out.println("}");
                                    }
                                }
                            } else if (member.isVariable()) {
                                // do nothing
                            } else if (member.isValue()) {
                                printModifiers(out, member);
                                out.print(" ");
                                Type resType = member.tpe().resultType();
                                String resQName = toJavaType(ScalaElement.typeQualifiedName(resType, false));
                                out.print(resQName);
                                out.print(" ");
                                out.print(member.nameString());
                                out.println(";");
                            }
                        }

                        // implements scala.ScalaObject
                        out.println("public int $tag() throws java.rmi.RemoteException {return 0;}");
                    }
                }
*/
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

        private void printModifiers(PrintWriter out, Symbol symbol) {
            if (symbol.isPublic()) {
                out.print("public");
            } else if (symbol.isProtectedLocal()) {
                out.print("protected");
            } else {
                out.print("private");
            }
        }

        private void printType(PrintWriter out, Type type) {
            out.print(JavaScalaMapping.toJavaType(ScalaElement.typeQualifiedName(type, false)));
        }

        private void printParams(PrintWriter out, scala.List params) {
            out.print("(");
            int size = params.size();
            for (int i = 0; i < size; i++) {
                Type type = (Type) params.apply(i);
                printType(out, type);
                out.print(" ");
                out.print("a");
                out.print(i);
                if (i != size - 1) {
                    out.print(",");
                }
            }
            out.print(")");
        }

        private void printReturn(PrintWriter out, String typeName) {
            String returnStr = TypeToReturn.get(typeName);
            out.print(returnStr == null ? "return null;" : returnStr);

        }

        private String toJavaName(String scalaName) {
            return JavaScalaMapping.toJavaOpName(scalaName);
        }

        private String toJavaType(String scalaTypeName) {
            return JavaScalaMapping.toJavaType(scalaTypeName);
        }
    }
    private static Map<String, String> TypeToReturn = new HashMap<String, String>();


    {
        TypeToReturn.put("void", "");
        TypeToReturn.put("double", "return 0.0;");
        TypeToReturn.put("float", "return 0.0f;");
        TypeToReturn.put("long", "return 0L;");
        TypeToReturn.put("int", "return 0;");
        TypeToReturn.put("short", "return 0;");
        TypeToReturn.put("byte", "return 0;");
        TypeToReturn.put("boolean", "return false;");
        TypeToReturn.put("char", "return 0;");
    }
}
