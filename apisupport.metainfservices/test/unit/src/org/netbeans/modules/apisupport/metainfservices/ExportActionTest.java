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
package org.netbeans.modules.apisupport.metainfservices;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefException;
import javax.jmi.reflect.RefFeatured;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.ElementPartKind;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.JavaDoc;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.MultipartId;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jarda
 */
public class ExportActionTest extends NbTestCase {
    
    public ExportActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        clearWorkDir();
    }

    protected void tearDown() throws Exception {
    }

    public void testGenerateFiles() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());

        FileSystem fs = lfs;
        FileObject src = FileUtil.createFolder(fs.getRoot(), "src");

        ArrayList<String> files = new ArrayList<String>();
        files.add("META-INF/services/java.lang.Object");
        files.add("META-INF/services/java.lang.Runnable");

        ExportAction.createFiles(R.class.getName(), files, src);

        URLClassLoader loader = new URLClassLoader(new URL[] { src.getURL() }, getClass().getClassLoader());
        Lookup l = Lookups.metaInfServices(loader);

        Runnable r = l.lookup(Runnable.class);

        assertNotNull("Runnable found", r);
        assertEquals("It is my class", R.class, r.getClass());


        ExportAction.createFiles(Q.class.getName(), files, src);

        l = Lookups.metaInfServices(loader);
        Set<Class<? extends Runnable>> all = l.lookupResult(Runnable.class).allClasses();

        assertEquals(2, all.size());
        assertTrue("Q is there", all.contains(Q.class));
        assertTrue("R is there", all.contains(R.class));
    }

    public void testRemovesAnnotations() throws Exception {
        JavaClassImpl impl = new JavaClassImpl();
        impl.setName("org.tst.Test");
        impl.setSimpleName("Test");

        JavaClassImpl par = new JavaClassImpl();
        par.setName("org.par.Parent <X,Y>");
        par.setSimpleName("Parent <X,Y>");

        impl.setSuperClass(par);

        JavaClassImpl obj = new JavaClassImpl();
        obj.setName("java.lang.Object");
        obj.setSimpleName("Object");

        par.setSuperClass(obj);

        ArrayList<String> names = new ArrayList<String>();
        ExportAction.findInterfaces(impl, names);

        assertEquals("Three", 3, names.size());

        for (String n : names) {
            if (n.indexOf("<") >= 0) {
                fail("Contains wrong char: " + n);
            }
            if (n.endsWith(" ")) {
                fail("Ends with space:[" + n + "]");
            }
        }
    }

    public static class R extends Object implements Runnable {
        public void run() {
        }
    }

    public static class Q implements Runnable {
        public void run() {
        }
    }

    private static final class JavaClassImpl implements JavaClass {
        private String simpleName;
        private String name;
        private JavaClass superClass;
        public List<JavaClass> interfaces = Collections.emptyList();

        public Collection findSubTypes(boolean recursively) {
            fail("Not implemented");
            return null;
        }

        public boolean isInterface() {
            fail("Not implemented");
            return false;
        }

        public void setInterface(boolean newValue) {
            fail("Not implemented");
        }

        public String getSimpleName() {
            return simpleName;
        }

        public void setSimpleName(String newValue) {
            simpleName = newValue;
        }

        public boolean isInner() {
            return false;
        }

        public Collection getSubClasses() {
            fail("Not implemented");
            return null;
        }

        public Collection getImplementors() {
            fail("Not implemented");
            return null;
        }

        public int getStartOffset() {
            return 0;
        }

        public int getEndOffset() {
            return 0;
        }

        public Resource getResource() {
            fail("Not implemented");
            return null;
        }

        public int getPartStartOffset(ElementPartKind part) {
            fail("Not implemented");
            return 0;
        }

        public int getPartEndOffset(ElementPartKind part) {
            fail("Not implemented");
            return 0;
        }

        public void replaceChild(Element oldChild, Element newChild) {
        }

        public List getChildren() {
            fail("Not implemented");
            return null;
        }

        public boolean isValid() {
            return true;
        }

        public Element duplicate() {
            try {
                return (JavaClass)clone();
            } catch (CloneNotSupportedException ex) {
                throw new IllegalStateException(ex);
            }
        }

        public boolean refIsInstanceOf(RefObject refObject, boolean b) {
            fail("Not implemented");
            return false;
        }

        public RefClass refClass() {
            fail("No");
            return null;
        }

        public RefFeatured refImmediateComposite() {
            fail("No");
            return null;
        }

        public RefFeatured refOutermostComposite() {
            fail("No");
            return null;
        }

        public void refDelete() {
        }

        public void refSetValue(RefObject refObject, Object object) {
        }

        public void refSetValue(String string, Object object) {
        }

        public Object refGetValue(RefObject refObject) {
            fail("No");
            return null;
        }

        public Object refGetValue(String string) {
            fail("No");
            return null;
        }

        public Object refInvokeOperation(RefObject refObject, List list) throws RefException {
            fail("No");
            return null;
        }

        public Object refInvokeOperation(String string, List list) throws RefException {
            fail("No");
            return null;
        }

        public RefObject refMetaObject() {
            fail("No");
            return null;
        }

        public RefPackage refImmediatePackage() {
            fail("No");
            return null;
        }

        public RefPackage refOutermostPackage() {
            fail("No");
            return null;
        }

        public String refMofId() {
            fail("No");
            return null;
        }

        public Collection refVerifyConstraints(boolean b) {
            fail("No");
            return null;
        }

        public boolean isDeprecated() {
            fail("No");
            return false;
        }

        public void setDeprecated(boolean newValue) {
        }

        public ClassDefinition getDeclaringClass() {
            fail("No");
            return null;
        }

        public int getModifiers() {
            fail("No");
            return 0;
        }

        public void setModifiers(int newValue) {
        }

        public String getJavadocText() {
            fail("No");
            return null;
        }

        public void setJavadocText(String newValue) {
        }

        public JavaDoc getJavadoc() {
            fail("No");
            return null;
        }

        public void setJavadoc(JavaDoc newValue) {
        }

        public List getAnnotations() {
            fail("No");
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String newValue) {
            this.name = newValue;
        }

        public Collection getReferences() {
            fail("No");
            return null;
        }

        public Field getField(String name, boolean includeSupertypes) {
            fail("No");
            return null;
        }

        public Method getMethod(String name, List parameters, boolean includeSupertypes) {
            fail("No");
            return null;
        }

        public JavaClass getInnerClass(String simpleName, boolean includeSupertypes) {
            fail("No");
            return null;
        }

        public Constructor getConstructor(List parameters, boolean includeSupertypes) {
            fail("No");
            return null;
        }

        public boolean isSubTypeOf(ClassDefinition javaClass) {
            fail("No");
            return false;
        }

        public List getContents() {
            fail("No");
            return null;
        }

        public MultipartId getSuperClassName() {
            fail("No");
            return null;
        }

        public void setSuperClassName(MultipartId newValue) {
        }

        public List getInterfaceNames() {
            fail("No");
            return null;
        }

        public List getFeatures() {
            fail("No");
            return null;
        }

        public List getInterfaces() {
            return interfaces;
        }

        public JavaClass getSuperClass() {
            return superClass;
        }

        public void setSuperClass(JavaClass newValue) {
            this.superClass = newValue;
        }

        public List getTypeParameters() {
            fail("No");
            return null;
        }

    }
}
