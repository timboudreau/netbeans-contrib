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

package org.netbeans.modules.javafx.preview;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.execution.NbClassLoader;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

class MemoryClassLoader extends ClassLoader {

    Map<String, byte[]> classBytes;
    ClassLoader compositClassLoader;

    public MemoryClassLoader(ClassPath[] classPaths) {
        classBytes = new HashMap<String, byte[]>();
        int counter = 0;
        for (int i = 0; i < classPaths.length; i++) counter+= classPaths[i].getRoots().length;
        FileObject fos[] = new FileObject[counter];
        counter = 0;
        for (int i = 0; i < classPaths.length; i++) 
            for (int j = 0; j < classPaths[i].getRoots().length; j++)
                fos[counter++] = classPaths[i].getRoots()[j];
        try {
            compositClassLoader = new NbClassLoader(fos, classPaths[0].getClassLoader(false).getParent(), null);
            MFOURLStreamHanfler.setCL(compositClassLoader);
        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void loadMap(Map<String, byte[]> classBytes) throws ClassNotFoundException {
        for (String key : classBytes.keySet()) {
            this.classBytes.put(key, classBytes.get(key));
        }
    }

    static URI toMFOURI(String name) {
        try {
            final StringBuilder newUri = new StringBuilder();
            newUri.append("mfo:///");
            newUri.append(name);
            return URI.create(newUri.toString());
        } catch (Exception exp) {
            return null;
        }
    }
    
    @Override
    protected URL findResource(String name) {
        URL url = compositClassLoader.getResource(name);
        
        if (url == null)
            try {
                url = toMFOURI(name).toURL();
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        return url;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (classBytes.get(name) == null) {
            Class classs = null;
            if (classs != null) return classs;
            try {
                classs = compositClassLoader.loadClass(name);
            } catch (NoClassDefFoundError er) {
            } catch (ClassNotFoundException ex) {
            }
            if (classs != null) return classs;
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        }

        Class result = findClass(name);

        if (resolve) {
            resolveClass(result);
        }

        return result;
    }

    @Override
    protected Class findClass(String className) throws ClassNotFoundException {
        byte[] buf = classBytes.get(className);
        if (buf != null) {
            classBytes.put(className, null);
            return defineClass(className, buf, 0, buf.length);
        } else {
            return super.findClass(className);
        }
    }
}
