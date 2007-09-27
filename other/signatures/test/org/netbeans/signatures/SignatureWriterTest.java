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

package org.netbeans.signatures;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import junit.framework.TestCase;

/**
 * @author Jesse Glick
 */
public class SignatureWriterTest extends TestCase {
    
    public SignatureWriterTest(String n) {
        super(n);
    }
    
    public void testBasicClass() {
        assertEmitted("package p; class X {}", "p.X", "");
    }
    
    public void testConstructors() {
        assertEmitted("package p; public final class X {private X() {}}",
                "p.X",
                "Class _ = p.X.class;");
        assertEmitted("package p; public class X {private X() {}}",
                "p.X",
                "Class _ = p.X.class;");
        assertEmitted("package p; public final class X {public X() {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X();");
        assertEmitted("package p; public final class X {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X();");
        assertEmitted("package p; public final class X {public X() {} public X(int x, String y, int[] z, java.util.Set<String> w) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X();  " +
                "new p.X(0, \"\", (int[]) null, (java.util.Set<String>) null);");
        assertEmitted("package p; public class S {} package p; public final class X extends S {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "p.S _ = (p.X) null;  " +
                "new p.X();");
        assertEmitted("package p; public class S<T> {} package p; public final class X<T> extends S<T> {public X() {} public X(T x) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "p.S<Object> _ = (p.X<Object>) null;  " +
                "new p.X<Object>();  " +
                "new p.X<Object>((Object) null);");
        assertEmitted("package p; public class S<T> {} package p; public final class X extends S<String> {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "p.S<String> _ = (p.X) null;  " +
                "new p.X();");
        assertEmitted("package p; public abstract class X {}",
                "p.X",
                "Class _ = p.X.class;");
        assertEmitted("package p; class S {} package p; public final class X extends S {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X();");
        assertEmitted("package p; public final class X {public X(char c) throws ClassNotFoundException, java.io.IOException, IllegalArgumentException {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "try {new p.X(' ');} catch (ClassNotFoundException _) {} catch (java.io.IOException _) {}");
        assertEmitted("package p; public final class X {public X(java.lang.reflect.Method m) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X((java.lang.reflect.Method) null);");
        assertEmitted("package p; class Hidden {} package p; public final class X {public X(Hidden h) {}}",
                "p.X",
                "Class _ = p.X.class;");
        assertEmitted("package p; public final class X {public <T> X(T t, Class<T> c) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X((Object) null, (Class<Object>) null);");
        assertEmitted("package p; public final class X {public X(String s1, java.util.List<String> l, String s2, String s3) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X(\"\", (java.util.List<String>) null, \"\", \"\");");
        assertEmitted("package p; public final class X<T> {public X(java.util.List<? extends T> l) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X<Object>((java.util.List<Object>) null);");
        assertEmitted("package p; public final class X<T extends Number> {public X(java.util.List<? extends T> l) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "new p.X<Number>((java.util.List<Number>) null);");
        assertEmitted("package p; public final class X {public X(String s) {} public <T> X(T t) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                // XXX first call is ambiguous, both constructors match!
                // Take example from javax.management.openmbean.OpenMBeanAttributeInfoSupport
                "new p.X(\"\");  " +
                "new p.X((Object) null);");
    }

    private static void assertEmitted(String source, String clazz, String sig) {
        // First compile the test sources.
        //MemoryOutputFileManager mgr = new MemoryOutputFileManager();
        // XXX would be better to compile to memory, but cannot get it to work.
        File dir = new File(System.getProperty("java.io.tmpdir"));
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>();
        for (final String chunk : source.split("(?!^)(?=package [a-z.]+;)")) {
            //System.err.println("Got: " + chunk);
            Matcher m = Pattern.compile("package ([a-z.]+); [a-z ]*(class|enum|interface|@interface) ([a-zA-Z0-9_]+).*").matcher(chunk);
            assertTrue(chunk, m.matches());
            String path = m.group(1).replace('.', '/') + "/" + m.group(3) + ".java";
            //System.err.println("with path: " + path);
            //compunits.add(mgr.store(path, chunk));
            compUnits.add(new SimpleJavaFileObject(URI.create("nowhere:/" + path), JavaFileObject.Kind.SOURCE) {
                public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                    return chunk;
                }
            });
        }
        StringWriter err = new StringWriter();
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                err,
                null,
                null,
                Arrays.asList("-source", "1.6", "-d", dir.getAbsolutePath(), /* exercise JDK bug #6468404 */"-g"),
                null,
                compUnits);
        boolean ok = task.call();
        String errors = err.toString();
        assertTrue(errors, ok);
        assertEquals(errors, 0, errors.length());
        // Now compile a dummy class so we can run the processor.
        task = ToolProvider.getSystemJavaCompiler().getTask(
                err,
                Loader.nullOutputFileManager(),
                null,
                Arrays.asList("-source", "1.6", "-classpath", dir.getAbsolutePath()),
                null,
                Collections.singleton(Loader.dummyCompilationUnit()));
        StringWriter result = new StringWriter();
        task.setProcessors(Collections.singleton(new P(result, clazz)));
        ok = task.call();
        // XXX could now delete the .class files in dir
        errors = err.toString();
        assertTrue(errors, ok);
        assertEquals(errors, 0, errors.length());
        SortedSet<String> lines = new TreeSet<String>(Arrays.asList(sig.split("  ")));
        StringBuilder b = new StringBuilder();
        for (String line : lines) {
            if (b.length() > 0) {
                b.append("  ");
            }
            b.append(line);
        }
        assertEquals(source, b.toString(), result.toString().replaceAll("^\\{", "").replaceAll("\\}\n\n$", "").replaceAll("\\}\n\\{", "  "));
        // XXX check that sig is compilable, too
    }
    
    /* Just does not work, not obvious why. Never writes out a .class file.
    private static final class MemoryOutputFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        
        final Map<String,String> data = new HashMap<String,String>();
        
        public MemoryOutputFileManager() {
            super(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));
        }
        
        public JavaFileObject store(String path, String contents) {
            data.put(path, contents);
            return new FO(path, null);
        }

        public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {
            String path = className.replace('.', '/') + ".java";
            if (data.containsKey(path)) {
                return new FO(path, kind);
            } else {
                return super.getJavaFileForInput(location, className, kind);
            }
        }

        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return new FO(className.replace('.', '/') + ".java", kind);
        }

        public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName) throws IOException {
            String path = packageName.replace('.', '/') + "/" + relativeName;
            if (data.containsKey(path)) {
                return new FO(path, null);
            } else {
                return super.getFileForInput(location, packageName, relativeName);
            }
        }

        public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
            return new FO(packageName.replace('.', '/') + "/" + relativeName, null);
        }

        public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
            List<JavaFileObject> files = new ArrayList<JavaFileObject>();
            for (JavaFileObject f : super.list(location, packageName, kinds, recurse)) {
                files.add(f);
            }
            for (Map.Entry<String,String> entry : data.entrySet()) {
                String path = entry.getKey();
                if (path.startsWith(packageName.replace('.', '/') + "/") && kinds.contains(kindFor(path))) {
                    files.add(new FO(path, null));
                }
            }
            System.err.println("XXX list: " + location + " " + packageName + " " + kinds + " " + recurse + " -> " + files);
            return files;
        }

        public boolean hasLocation(JavaFileManager.Location location) {
            return true;
            / *
            boolean retValue;
            
            retValue = super.hasLocation(location);
            System.err.println("XXX hasLocation " + location + " -> " + retValue);
            return retValue;
             * /
        }
        
        private JavaFileObject.Kind kindFor(String path) {
            if (path.endsWith(".java")) {
                return JavaFileObject.Kind.SOURCE;
            } else if (path.endsWith(".class")) {
                return JavaFileObject.Kind.CLASS;
            } else {
                return JavaFileObject.Kind.OTHER;
            }
        }

        public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file) {
            if (file instanceof FO) {
                return file.getName().replaceFirst("\\.[^/.]+$", "").replace('/', '.');
            } else {
                return super.inferBinaryName(location, file);
            }
        }

        private class FO extends SimpleJavaFileObject {
            
            public FO(String path, JavaFileObject.Kind kind) {
                super(URI.create("mem:/" + path), kind != null ? kind : kindFor(path));
                System.err.println("XXX made FO for " + uri + " " + kind);
            }

            public InputStream openInputStream() throws IOException {
                return new ByteArrayInputStream(data.get(getName()).getBytes("UTF-8"));
            }

            public OutputStream openOutputStream() throws IOException {
                return new ByteArrayOutputStream() {
                    public void close() throws IOException {
                        super.close();
                        data.put(getName(), toString("UTF-8"));
                    }
                };
            }

            public Writer openWriter() throws IOException {
                return new StringWriter() {
                    public void close() throws IOException {
                        super.close();
                        data.put(getName(), toString());
                    }
                };
            }

            public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                return data.get(getName());
            }
            
        }
        
    }
     */
    
    @SupportedAnnotationTypes("*")
    @SupportedSourceVersion(SourceVersion.RELEASE_6)
    private static final class P extends AbstractProcessor {
        
        private final Writer w;
        private final String clazz;
        private boolean ran = false;
        
        P(Writer w, String clazz) {
            this.w = w;
            this.clazz = clazz;
        }
        
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (!ran) {
                ran = true;
            } else {
                return true;
            }
            new SignatureWriter(new PrintWriter(w), processingEnv.getElementUtils(), processingEnv.getTypeUtils()).process(clazz);
            return true;
        }
        
    }

}
