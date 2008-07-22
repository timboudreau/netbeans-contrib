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
package org.netbeans.modules.scala.project.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathProvider;
import org.netbeans.modules.gsfpath.spi.classpath.support.ClassPathSupport;
import org.netbeans.modules.scala.project.classpath.ClassPathProviderImpl;
import org.netbeans.napi.gsfret.source.ClasspathInfo;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.util.WeakListeners;

/**
 * Defines the various class paths for a J2SE project.
 *
 * @author  Caoyuan Deng
 */
public final class GsfClassPathProviderImpl implements ClassPathProvider, PropertyChangeListener {

    private final PropertyEvaluator evaluator;
    private final ClassPath[] cache = new ClassPath[8];
    private final ClassPathProviderImpl javaCpImpl;

    public GsfClassPathProviderImpl(PropertyEvaluator evaluator, ClassPathProviderImpl javaCpImpl) {
        this.evaluator = evaluator;
        this.javaCpImpl = javaCpImpl;
        evaluator.addPropertyChangeListener(WeakListeners.propertyChange(this, evaluator));
    }

    /**
     * Find what a given file represents.
     * @param file a file in the project
     * @return one of: <dl>
     *         <dt>0</dt> <dd>normal source</dd>
     *         <dt>1</dt> <dd>test source</dd>
     *         <dt>2</dt> <dd>built class (unpacked)</dd>
     *         <dt>3</dt> <dd>built test class</dd>
     *         <dt>4</dt> <dd>built class (in dist JAR)</dd>
     *         <dt>-1</dt> <dd>something else</dd>
     *         </dl>
     */
    private int getType(FileObject file) {
        return javaCpImpl.getType(file);
    }

    private ClassPath getCompileTimeClasspath(FileObject file) {
        int type = getType(file);
        return this.getCompileTimeClasspath(file, type);
    }

    private synchronized ClassPath getCompileTimeClasspath(FileObject file, int type) {
        if (type < 0 || type > 1) {
            // Not a source file.
            return null;
        }

        ClassPath cp = cache[2 + type];
        if (cp == null) {
            cp = convert(javaCpImpl.findClassPath(file, ClassPath.COMPILE));
            cache[2 + type] = cp;
        }
        return cp;
    }

    private synchronized ClassPath getRunTimeClasspath(FileObject file) {
        int type = getType(file);
        if (type < 0 || type > 4) {
            // Unregistered file, or in a JAR.
            // For jar:file:$projdir/dist/*.jar!/**/*.class, it is misleading to use
            // run.classpath since that does not actually contain the file!
            // (It contains file:$projdir/build/classes/ instead.)
            return null;
        } else if (type > 1) {
            type -= 2;            //Compiled source transform into source
        }
        ClassPath cp = cache[4 + type];
        if (cp == null) {
            cp = convert(javaCpImpl.findClassPath(file, ClassPath.EXECUTE));
            cache[4 + type] = cp;
        }
        return cp;
    }

    private ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        return this.getSourcepath(file, type);
    }

    private synchronized ClassPath getSourcepath(FileObject file, int type) {
        if (type < 0 || type > 1) {
            return null;
        }
        ClassPath cp = cache[type];
        if (cp == null) {
            cp = convert(javaCpImpl.findClassPath(file, ClassPath.SOURCE));
            cache[type] = cp;
        }
        return cp;
    }

    private synchronized ClassPath getBootClassPath(FileObject file) {
        ClassPath cp = cache[7];
        if (cp == null) {
            cp = convert(javaCpImpl.findClassPath(file, ClassPath.BOOT));
            cache[7] = cp;
        }
        return cp;
    }

    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(ClassPath.COMPILE)) {
            return getCompileTimeClasspath(file);
        } else if (type.equals(ClassPath.EXECUTE)) {
            return getRunTimeClasspath(file);
        } else if (type.equals(ClassPath.SOURCE)) {
            return getSourcepath(file);
        } else if (type.equals(ClassPath.BOOT)) {
            return getBootClassPath(file);
        } else {
            return null;
        }
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
    }
    
    public static ClassPath convert(org.netbeans.api.java.classpath.ClassPath javaCp) {
        List<org.netbeans.api.java.classpath.ClassPath.Entry> entries = javaCp.entries();
        URL[] urls = new URL[javaCp.entries().size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = entries.get(i).getURL();
        }
        return ClassPathSupport.createClassPath(urls);
    }

    public static ClasspathInfo createGsfClassPathInfo(
            org.netbeans.api.java.classpath.ClassPath bootPath,
            org.netbeans.api.java.classpath.ClassPath classPath,
            org.netbeans.api.java.classpath.ClassPath sourcePath) {
        
        return ClasspathInfo.create(convert(bootPath), convert(classPath), convert(sourcePath));
    }
    
}
