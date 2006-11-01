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

package org.netbeans.modules.jemmysupport;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * Utilities methods.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class Utils {

    /** Returns NetBeans SystemClassLoader from threads hierarchy. */
    public static ClassLoader getSystemClassLoader() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ClassLoader systemClassloader = Thread.currentThread().getContextClassLoader();
        while(!systemClassloader.getClass().getName().endsWith("SystemClassLoader")) { // NOI18N
            tg = tg.getParent();
            if(tg == null) {
                ErrorManager.getDefault().notify(new Exception("NetBeans SystemClassLoader not found!")); // NOI18N
            }
            Thread[] list = new Thread[tg.activeCount()];
            tg.enumerate(list);
            systemClassloader = list[0].getContextClassLoader();
        }
        return systemClassloader;
    }
    
    /** Classloder with overriden getPermissions method because it doesn't
     * have sufficient permissions when run from IDE.
     */
    public static class TestClassLoader extends URLClassLoader {
        
        public TestClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        protected PermissionCollection getPermissions(CodeSource codesource) {
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            permissions.setReadOnly();
            return permissions;
        }
        
        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.netbeans.jemmy") || name.startsWith("org.netbeans.jellytools")) { // NOI18N
                //System.out.println("CLASSNAME="+name);
                // Do not proxy to parent!
                Class c = findLoadedClass(name);
                if (c != null) return c;
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } else {
                return super.loadClass(name, resolve);
            }
        }
        
        /** Just to make it public. Used in BundleLookupAction. */
        public Package[] getPackages() {
            return super.getPackages();
        }
    }

    /** Returns fully qualified class name if given file object is a java source
     * and it contains main method.
     * @param fo file object
     * @return fully qualified class name if file object has main method, null otherwise
     */
    public static String hasMainMethod(FileObject fo) {
        // Later revise in org.netbeans.modules.java.j2seproject.J2SEProjectUtil.hasMainMethod()
        JavaSource js = JavaSource.forFileObject(fo);
        if(js == null) {
            // not a java source
            return null;
        }
        MemberVisitor.hasMainMethod = false;
        try {
            js.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws IOException {
                    parameter.toPhase(Phase.ELEMENTS_RESOLVED);
                    new MemberVisitor(parameter).scan(parameter.getCompilationUnit(), null);
                }
            }, true);
        } catch (IOException e) {
            Logger.getLogger("").log(Level.SEVERE, e.getMessage(), e);
        }
        if(MemberVisitor.hasMainMethod) {
            return MemberVisitor.qualifiedName;
        } else {
            return null;
        }
    }
    
    private static class MemberVisitor extends TreePathScanner<Void, Void> {
        
        public static boolean hasMainMethod = false;
        public static String qualifiedName = null;
        private CompilationInfo info;
        
        public MemberVisitor(CompilationInfo info) {
            this.info = info;
        }
        
        @Override
        public Void visitClass(ClassTree t, Void v) {
            hasMainMethod = false;
            Element el = info.getTrees().getElement(getCurrentPath());
            if (el != null) {
                TypeElement te = (TypeElement) el;
                qualifiedName = te.getQualifiedName().toString();
                List<ExecutableElement> methods = ElementFilter.methodsIn(te.getEnclosedElements());
                for (int i = 0; i < methods.size(); i++) {
                    ExecutableElement method = methods.get(i);
                    hasMainMethod = isMainMethod(method);
                    if(hasMainMethod) {
                        // find it -> return
                        return null;
                    }
                }
            }
            return null;
        }
        
        private static boolean isMainMethod(ExecutableElement method) {
            // name should be "main"
            if(!"main".equals(method.getSimpleName().toString())) { //NOI18N
                return false;
            }
            // return type should be void
            if(!method.getReturnType().getKind().equals(TypeKind.VOID)) {
                return false;
            }
            // check String[] parameter
            /* it seems not to work now.
            List<? extends TypeParameterElement> paramTypes = method.getTypeParameters();
            for (int j = 0; j < paramTypes.size(); j++) {
                Object object = paramTypes.get(j);
            }
             */
            // modifiers should be public static
            Set<Modifier> modifiers = method.getModifiers();
            if(!modifiers.contains(Modifier.PUBLIC)) {
                return false;
            }
            if(!modifiers.contains(Modifier.STATIC)) {
                return false;
            }
            return modifiers.size() == 2;
        }
    }
}
