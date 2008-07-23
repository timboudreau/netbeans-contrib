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

package org.netbeans.modules.autoproject.java;

import org.netbeans.modules.autoproject.spi.Cache;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.project.support.JavadocAndSourceRootDetection;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 * Manages detected classpath information for the project.
 */
class ClassPathProviderImpl implements ClassPathProvider {

    private static final Logger LOG = Logger.getLogger(ClassPathProviderImpl.class.getName());

    private final Project prj;
    private final Map<File,Map<String,ClassPathImpl>> classpaths = new WeakHashMap<File,Map<String,ClassPathImpl>>();
    private final Map<String,List<ClassPath>> registeredPaths = new HashMap<String,List<ClassPath>>();

    public ClassPathProviderImpl(Project p) {
        prj = p;
    }

    public ClassPath findClassPath(FileObject file, String type) {
        File f = FileUtil.toFile(file);
        if (f == null) {
            return null;
        }
        // Look for a defined sourcepath of some ancestor.
        File root = f;
        while (root != null) {
            if (Cache.get(root + JavaCacheConstants.SOURCE) != null) {
                break;
            }
            root = root.getParentFile();
        }
        if (root == null && f.isFile() && f.getName().endsWith(".java")) {
            FileObject packageRoot = JavadocAndSourceRootDetection.findPackageRoot(file);
            if (packageRoot != null) {
                root = FileUtil.toFile(packageRoot);
                LOG.log(Level.FINE, "Inferring root {0} for {1}", new Object[] {root, f});
                Cache.put(root + JavaCacheConstants.SOURCE, root.getAbsolutePath());
            }
        }
        if (root == null) {
            LOG.log(Level.FINE, "Found no classpath definition for {0} in {1}", new Object[] {type, f});
            return null;
        }
        Map<String,ClassPathImpl> m = classpaths.get(root);
        if (m == null) {
            m = new HashMap<String,ClassPathImpl>();
            classpaths.put(root, m);
        }
        ClassPathImpl impl = m.get(type);
        if (impl == null) {
            impl = new ClassPathImpl(root.getAbsolutePath(), type);
            m.put(type, impl);
            LOG.log(Level.FINE, "Found classpath definition for {0} in {1}", new Object[] {type, root});
            synchronized (registeredPaths) {
                List<ClassPath> cps = registeredPaths.get(type);
                if (cps != null) {
                    cps.add(impl.cp);
                    GlobalPathRegistry.getDefault().register(type, new ClassPath[] {impl.cp});
                }
            }
        }
        return impl.cp;
    }

    private final class ClassPathImpl implements ClassPathImplementation, PropertyChangeListener {

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        private List<URL> urls = null;
        private final String root;
        private final String type;
        final ClassPath cp;

        ClassPathImpl(String root, String type) {
            this.root = root;
            this.type = type;
            cp = ClassPathFactory.createClassPath(this);
            Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
        }

        public List<? extends PathResourceImplementation> getResources() {
            boolean fire;
            List<URL> newurls = new ArrayList<URL>();
            synchronized (this) {
                String path = null;
                if (type.equals(ClassPath.SOURCE)) {
                    path = Cache.get(root + JavaCacheConstants.SOURCE);
                } else if (type.equals(ClassPath.COMPILE)) {
                    path = Cache.get(root + JavaCacheConstants.CLASSPATH);
                } else if (type.equals(ClassPath.EXECUTE)) {
                    String classpath = Cache.get(root + JavaCacheConstants.CLASSPATH);
                    String binary = Cache.get(root + JavaCacheConstants.BINARY);
                    path = classpath != null ?
                        (binary != null ? classpath + File.pathSeparator + binary : classpath) :
                            (binary != null ? binary : null);
                } else if (type.equals(ClassPath.BOOT)) {
                    path = Cache.get(root + JavaCacheConstants.BOOTCLASSPATH);
                }
                if (path != null) {
                    for (String piece : path.split("[:;]")) {
                        if (piece.length() == 0) {
                            continue;
                        }
                        URL u = FileUtil.urlForArchiveOrDir(new File(piece));
                        if (u != null) {
                            newurls.add(u);
                        }
                    }
                } else if (type.equals(ClassPath.BOOT)) {
                    // Fall back to default platform since no other info is available.
                    for (ClassPath.Entry e : JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries().entries()) {
                        newurls.add(e.getURL());
                    }
                }
                fire = urls != null && !newurls.equals(urls);
                urls = newurls;
            }
            if (fire) {
                pcs.firePropertyChange(PROP_RESOURCES, null, null);
            }
            LOG.log(Level.FINE, "calculated {0} for {1}: {2}", new Object[] {type, root, newurls});
            List<PathResourceImplementation> resources = new ArrayList<PathResourceImplementation>(newurls.size());
            for (URL u : newurls) {
                resources.add(ClassPathSupport.createResource(u));
            }
            return resources;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().contains(root)) {
                getResources();
            }
        }

    }

    void open() {
        synchronized (registeredPaths) {
            String[] TYPES = {ClassPath.SOURCE, ClassPath.COMPILE, ClassPath.EXECUTE, ClassPath.BOOT};
            for (String type : TYPES) {
                registeredPaths.put(type, new ArrayList<ClassPath>());
            }
            for (SourceGroup g : ProjectUtils.getSources(prj).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
                FileObject root = g.getRootFolder();
                for (String type : TYPES) {
                    ClassPath cp = ClassPath.getClassPath(root, type);
                    if (cp != null) {
                        registeredPaths.get(type).add(cp);
                    }
                }
            }
            for (Map.Entry<String, List<ClassPath>> entry : registeredPaths.entrySet()) {
                GlobalPathRegistry.getDefault().register(entry.getKey(), entry.getValue().toArray(new ClassPath[0]));
            }
        }
    }

    void close() throws IllegalArgumentException {
        synchronized (registeredPaths) {
            for (Map.Entry<String, List<ClassPath>> entry : registeredPaths.entrySet()) {
                GlobalPathRegistry.getDefault().unregister(entry.getKey(), entry.getValue().toArray(new ClassPath[0]));
            }
            registeredPaths.clear();
        }
    }

}
