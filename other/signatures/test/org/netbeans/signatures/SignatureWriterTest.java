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
 */

package org.netbeans.signatures;

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
        assertEmitted("package p; public final class X {private X() {}}",
                "p.X",
                "Class _ = p.X.class;");
        assertEmitted("package p; public class X {private X() {}}",
                "p.X",
                "Class _ = p.X.class;");
    }
    
    public void testConstructors() {
        assertEmitted("package p; public final class X {public X() {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X();  " +
                "p.X _ = new p.X();");
        assertEmitted("package p; public final class X {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X();  " +
                "p.X _ = new p.X();");
        assertEmitted("package p; public final class X {public X() {} public X(int x, String y, int[] z, java.util.Set<String> w) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X();  " +
                "p.X _ = new p.X();  " +
                "p.X _ = new p.X(0, \"\", (int[]) null, (java.util.Set<String>) null);");
        assertEmitted("package p; public class S {} package p; public final class X extends S {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X();  " +
                "p.S _ = new p.X();  " +
                "p.X _ = new p.X();");
        assertEmitted("package p; public class S<T> {} package p; public final class X<T> extends S<T> {public X() {} public X(T x) {}}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X<Object>();  " +
                "p.S<Object> _ = new p.X<Object>();  " +
                "p.X<Object> _ = new p.X<Object>();  " +
                "p.X<Object> _ = new p.X<Object>((Object) null);");
        assertEmitted("package p; public class S<T> {} package p; public final class X extends S<String> {}",
                "p.X",
                "Class _ = p.X.class;  " +
                "Object _ = new p.X();  " +
                "p.S<String> _ = new p.X();  " +
                "p.X _ = new p.X();");
    }

    private static void assertEmitted(String source, String clazz, String sig) {
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>();
        for (final String chunk : source.split("(?!^)(?=package [a-z.]+;)")) {
            //System.err.println("Got: " + chunk);
            Matcher m = Pattern.compile("package ([a-z.]+); [a-z ]*(class|enum|interface|@interface) ([a-zA-Z0-9_]+).*").matcher(chunk);
            assertTrue(chunk, m.matches());
            String path = m.group(1).replace('.', '/') + "/" + m.group(3) + ".java";
            //System.err.println("with path: " + path);
            compUnits.add(new SimpleJavaFileObject(URI.create("nowhere:/" + path), JavaFileObject.Kind.SOURCE) {
                public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                    return chunk;
                }
            });
        }
        StringWriter err = new StringWriter();
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                err,
                Loader.nullOutputFileManager(),
                null,
                Arrays.asList("-source", "1.6"),
                null,
                compUnits);
        StringWriter result = new StringWriter();
        task.setProcessors(Collections.singleton(new P(result, clazz)));
        boolean ok = task.call();
        String errors = err.toString();
        assertTrue(errors, ok);
        assertEquals(errors, 0, errors.length());
        assertEquals(source, sig, result.toString().replaceAll("^\\{", "").replaceAll("\\}\n$", "").replaceAll("\\}\n\\{", "  "));
    }
    
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
            new SignatureWriter(new PrintWriter(w), "", processingEnv.getElementUtils(), processingEnv.getTypeUtils()).process(clazz);
            return true;
        }
        
    }
    
}
