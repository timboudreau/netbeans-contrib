/*
 * Closure.java
 *
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
 * Contributor(s): Thomas Ball
 */
package org.netbeans.modules.java.closurenavigator;
import org.netbeans.modules.classfile.*;
import java.io.*;
import java.util.*;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Closure:  report all classes which this file references in one
 * way or another.  Note: this utility won't find classes which are
 * dynamically loaded.
 *
 * @author Thomas Ball
 */
public class Closure {
    String thisClass;
    Set closure;
    private final ClassPath classPath;

    Closure(ClassPath classPath, String thisClass) throws IOException {
        this.classPath = classPath;
        this.thisClass = thisClass;
        buildClosure (false);
    }
    
    //TODO:  Exclude class files in library jars from search
    //TODO:  Figure out how to put the whole shebang on the clipboard when
    //we're done, or better, offer to create a new project, extract the
    //class and its closure, update dependencies everywhere, including 
    //libraries...

    public void buildClosure(boolean includeJDK)
      throws IOException {
        if (closure != null)
            return;
        closure = new HashSet();
        Set visited = new HashSet();
        Stack stk = new Stack();
        ClassName thisCN = ClassName.getClassName(thisClass.replace('.', '/'));
        stk.push(thisCN);
        visited.add(thisCN.getExternalName());

        while (!stk.empty()) {
            // Add class to closure.
            ClassName cn = (ClassName)stk.pop();
            InputStream is = findClassStream(cn.getType());
	    if (is == null) {
		System.err.println("couldn't find class: " + 
                                   cn.getExternalName());
		continue;
	    }
            ClassFile cfile = new ClassFile(is);
            if (cfile.getSourceFileName() != null) {
                String srcName = cfile.getName().getPackage().replace('.', '/') + 
                        '/' + cfile.getSourceFileName();
                System.err.println("SFN " + srcName);
                sourceFileNames.add (srcName);
            }
            closure.add(cfile.getName().getExternalName());
            
            ConstantPool pool = cfile.getConstantPool();
            Iterator refs = pool.getAllClassNames().iterator();
            while (refs.hasNext()) {
                ClassName cnRef = (ClassName)refs.next();
                String cname = cnRef.getExternalName();
                if (cname.indexOf('[') != -1) {
                    // skip arrays
                } else if (!includeJDK && 
                           (cname.startsWith("java.") || 
                            cname.startsWith("javax.") ||
                            cname.startsWith("sun.") ||
                            cname.startsWith("com.sun.corba") ||
                            cname.startsWith("com.sun.image") ||
                            cname.startsWith("com.sun.java.swing") ||
                            cname.startsWith("com.sun.naming") ||
                            cname.startsWith("com.sun.security"))) {
                    // if directed, skip JDK references
                } else {
                    boolean isNew = visited.add(cname);
                    if (isNew)
                        stk.push(cnRef);
                }
            }
        }
    }

    InputOutput dumpClosure() {
        InputOutput io = IOProvider.getDefault().getIO("Closure of " + 
                thisClass, false);
        PrintWriter out = io.getOut();
        io.select();
        Iterator iter = new TreeSet(closure).iterator();
        while (iter.hasNext()) {
            out.println((String)iter.next());
        }
        out.close();
        return io;
    }

    Iterator dependencies() {
        return closure.iterator();
    }

    private Set <String> sourceFileNames = new HashSet();
    private Set <String> notFound = null;
//    private Set <FileObject> binaries = new HashSet();
    private InputStream findClassStream(String className) throws IOException {
        String nm = className.replace('.','/') + ".class";
        FileObject ob = classPath.findResource(nm);
        if (ob == null) {
            StatusDisplayer.getDefault().setStatusText("Could not find resource " +
                    "for " + className);
//            IOException e = new IOException ("Could not find resource " + 
//                    className);
//            ErrorManager.getDefault().annotate (e, ErrorManager.USER,
//                    "Failed at " + className, "Could not find resource " +
//                    className, null, null);
            if (notFound == null) {
                notFound = new HashSet();
            }
            notFound.add (nm);
            return null;
//            throw e;
        } else {
//            binaries.add (ob);
            return new BufferedInputStream (ob.getInputStream());
        }
    }
        
    Set <DataObject> sources = null;
    Set getSources(ClassPath srcPath) throws IOException {
        if (sourceFileNames.isEmpty() || (sources != null && sources.isEmpty())) {
            throw new IOException ("Source file names not present.  Recompile "+
                    "with debug info");
        }
        if (sources == null) {
            sources = new HashSet <DataObject> ();
            for (String nm : sourceFileNames) {
                FileObject ob = srcPath.findResource(nm);
                if (ob == null) {
                    if (notFound == null) {
                        notFound = new HashSet();
                    }
                    notFound.add (nm);
//                    sources.clear();
//                    throw new IOException ("Failed to find file for " + nm + 
//                            ". I give up.");
                } else {
                    sources.add (DataObject.find(ob));
                }
            }
        }
        return sources;
    }
    
    String[] getMissingSources () {
        if (notFound == null) {
            return new String[0];
        } else {
            String[] result=(String[]) notFound.toArray(new String[notFound.size()]);
            return result;
        }
    }
}
