/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ProjectFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Borrowed from javascript.editor
 * 
 * @author vita, Tim Boudreau
 */
@ServiceProvider(service = ClassPathProvider.class)
public final class NodeJsClassPathProvider implements ClassPathProvider {

    public static final String BOOT_CP = "NodeJsBootClassPath"; //NOI18N
    private static FileObject jsStubsFO;
    private static FileObject nodeStubsFO;
    private static ClassPath bootClassPath;

    @Override
    public ClassPath findClassPath(FileObject file, String type) {
        NodeJSProjectFactory pf = new NodeJSProjectFactory();
        if (type.equals(BOOT_CP) || ClassPath.SOURCE.equals(type)) {
            try {
                if (pf.findOwner(file) != null) {
                    return getBootClassPath();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    public static synchronized ClassPath getBootClassPath() {
        if (bootClassPath == null) {
            FileObject jsstubs = getJsStubs();
            if (jsstubs != null) {
                bootClassPath = ClassPathSupport.createClassPath(getJsStubs(), getNodeJsSources());
            }
        }
        return bootClassPath;
    }

    private static FileObject getNodeJsSources() {
        if (nodeStubsFO == null) {
            String loc = new DefaultExectable().getSourcesLocation();
            if (loc != null) {
                File dir = new File(loc);
                return nodeStubsFO = FileUtil.toFileObject(dir);
            }
        }
        return nodeStubsFO;

    }

    // TODO - add classpath recognizer for these ? No, don't need go to declaration inside these files...
    private static FileObject getJsStubs() {
        if (jsStubsFO == null) {
            // Core classes: Stubs generated for the "builtin" Ruby libraries.
            File allstubs = InstalledFileLocator.getDefault().locate("jsstubs/allstubs.zip", "org.netbeans.modules.javascript.editing", false);
            if (allstubs == null) {
                // Probably inside unit test.
                try {
                    File moduleJar = new File(NodeJsClassPathProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                    allstubs = new File(moduleJar.getParentFile().getParentFile(), "jsstubs/allstubs.zip");
                } catch (URISyntaxException x) {
                    assert false : x;
                    return null;
                }
            }
            assert allstubs.isFile() : allstubs;
            jsStubsFO = FileUtil.getArchiveRoot(FileUtil.toFileObject(allstubs));
        }
        return jsStubsFO;
    }
}
