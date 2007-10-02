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

package org.netbeans.modules.groovy.groovyproject.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.HashMap;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 * Defines the various class paths for a J2SE project.
 */
public final class ClassPathProviderImpl implements ClassPathProvider, PropertyChangeListener {
    
    private static final String SRC_DIR = "src.dir"; // NOI18N
    private static final String BUILD_CLASSES_DIR = "build.classes.dir"; // NOI18N
    private static final String DIST_JAR = "dist.jar"; // NOI18N
    
    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private final ClassPath[] cache = new ClassPath[8];

    private final Map/*<String,FileObject>*/ dirCache = new HashMap();

    public ClassPathProviderImpl(AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.helper = helper;
        this.evaluator = evaluator;
        evaluator.addPropertyChangeListener(WeakListeners.propertyChange(this, evaluator));
    }

    private synchronized FileObject getDir(String propname) {
        FileObject fo = (FileObject) this.dirCache.get (propname);
        if (fo == null ||  !fo.isValid()) {
            String prop = evaluator.getProperty(propname);
            if (prop != null) {
                fo = helper.resolveFileObject(prop);
                this.dirCache.put (propname, fo);
            }
        }
        return fo;
    }
    
    private FileObject getPrimarySrcDir() {
        return getDir(SRC_DIR);
    }
    
    private FileObject getBuildClassesDir() {
        return getDir(BUILD_CLASSES_DIR);
    }
    
    private FileObject getDistJar() {
        return getDir(DIST_JAR);
    }
    
    /**
     * Find what a given file represents.
     * @param file a file in the project
     * @return one of: <dl>
     *         <dt>0</dt> <dd>normal source</dd>
     *         <dt>2</dt> <dd>built class (unpacked)</dd>
     *         <dt>4</dt> <dd>built class (in dist JAR)</dd>
     *         <dt>-1</dt> <dd>something else</dd>
     *         </dl>
     */
    private int getType(FileObject file) {
        FileObject dir = getPrimarySrcDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return 0;
        }
        dir = getBuildClassesDir();
        if (dir != null && (dir.equals(file) || FileUtil.isParentOf(dir, file))) {
            return 2;
        }
        dir = getDistJar(); // not really a dir at all, of course
        if (dir != null && dir.equals(FileUtil.getArchiveFile(file))) {
            // XXX check whether this is really the root
            return 4;
        }
        return -1;
    }
    
    private ClassPath getCompileTimeClasspath(FileObject file) {
        int type = getType(file);
        return this.getCompileTimeClasspath(type);
    }
    
    private ClassPath getCompileTimeClasspath(int type) {        
        if (type < 0 || type > 1) {
            // Not a source file.
            return null;
        }
        ClassPath cp = cache[2+type];
        if ( cp == null) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "javac.classpath", evaluator)); // NOI18N
            }
            cache[2+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getRunTimeClasspath(FileObject file) {
        int type = getType(file);
        if (type < 0 || type > 4) {
            // Unregistered file, or in a JAR.
            // For jar:file:$projdir/dist/*.jar!/**/*.class, it is misleading to use
            // run.classpath since that does not actually contain the file!
            // (It contains file:$projdir/build/classes/ instead.)
            return null;
        } else if (type > 1) {
            type-=2;            //Compiled source transform into source
        }
        ClassPath cp = cache[4+type];
        if ( cp == null) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, "run.classpath", evaluator)); // NOI18N
            }
            else if (type == 2) {
                //Only to make the CompiledDataNode hapy
                //Todo: Strictly it should return ${run.classpath} - ${build.classes.dir} + ${dist.jar}
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, DIST_JAR, evaluator)); // NOI18N
            }
            cache[4+type] = cp;
        }
        return cp;
    }
    
    private ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        return this.getSourcepath(type);
    }
    
    private ClassPath getSourcepath(int type) {
        if (type < 0 || type > 1) {
            return null;
        }
        ClassPath cp = cache[type];
        if ( cp == null ) {
            if (type == 0) {
                cp = ClassPathFactory.createClassPath(
                new ProjectClassPathImplementation(helper, SRC_DIR, evaluator)); // NOI18N
            }
            cache[type] = cp;
        }
        return cp;
    }
    
    private ClassPath getBootClassPath() {
        ClassPath cp = cache[7];
        if ( cp== null ) {
            cp = ClassPathFactory.createClassPath(new BootClassPathImplementation(helper, evaluator));
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
            return getBootClassPath();
        } else {
            return null;
        }
    }
    
    /**
     * Returns array of all classpaths of the given type in the project.
     * The result is used for example for GlobalPathRegistry registrations.
     */
    public ClassPath[] getProjectClassPaths(String type) {
        if (ClassPath.BOOT.equals(type)) {
            return new ClassPath[]{getBootClassPath()};
        }
        if (ClassPath.COMPILE.equals(type)) {
            ClassPath[] l = new ClassPath[1];
            l[0] = getCompileTimeClasspath(0);
            return l;
        }
        if (ClassPath.SOURCE.equals(type)) {
            ClassPath[] l = new ClassPath[1];
            l[0] = getSourcepath(0);
            return l;
        }
        assert false;
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        dirCache.remove(evt.getPropertyName());
    }
    
}

