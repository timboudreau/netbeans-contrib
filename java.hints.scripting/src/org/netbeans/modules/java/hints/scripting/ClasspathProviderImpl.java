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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2008 Sun
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
package org.netbeans.modules.java.hints.scripting;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.regex.Pattern;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class ClasspathProviderImpl implements ClassPathProvider {

    private ClassPath boot;
    private ClassPath compile;
    private ClassPath source;
    
    public synchronized ClassPath findClassPath(FileObject file, String type) {
        if (FileUtil.isParentOf(Utilities.getFolder(), file)) {
            if (ClassPath.BOOT.equals(type)) {
                if (boot == null) {
                    boot = ClassPathSupport.createClassPath(getBootClassPath().toArray(new URL[0]));
                    GlobalPathRegistry.getDefault().register(type, new ClassPath[] {boot});
                }
                
                return boot;
            }
            if (ClassPath.COMPILE.equals(type)) {
                if (compile == null) {
                    List<URL> cp = Utilities.computeCP();
                    URL[] us = new URL[cp.size()];
                    int index = 0;
                    
                    for (URL u : cp) {
                        if (FileUtil.isArchiveFile(u)) {
                            us[index++] = FileUtil.getArchiveRoot(u);
                        } else {
                            us[index++] = u;
                        }
                    }
                    
                    compile = ClassPathSupport.createClassPath(us);
                    GlobalPathRegistry.getDefault().register(type, new ClassPath[] {compile});
                }
                
                return compile;
            }
            if (ClassPath.SOURCE.equals(type)) {
                if (source == null) {
                    source = ClassPathSupport.createClassPath(Utilities.getFolder());
                    GlobalPathRegistry.getDefault().register(type, new ClassPath[] {source});
                }
                
                return source;
            }
        }
        
        return null;
    }

    public static synchronized List<URL> getBootClassPath() {
        try {
            String cp = System.getProperty("sun.boot.class.path");
            List<URL> urls = new ArrayList<URL>();
            String[] paths = cp.split(Pattern.quote(System.getProperty("path.separator")));

            for (String path : paths) {
                File f = new File(path);

                if (!f.canRead()) {
                    continue;
                }

                FileObject fo = FileUtil.toFileObject(f);

                if (FileUtil.isArchiveFile(fo)) {
                    fo = FileUtil.getArchiveRoot(fo);
                }

                if (fo != null) {
                    urls.add(fo.getURL());
                }
            }

            return urls;
        } catch (FileStateInvalidException e) {
            Exceptions.printStackTrace(e);
            return Collections.emptyList();
        }
    }
    
}
