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

import java.io.File;
import java.util.Collections;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.TestFileUtils;

public class SubprojectProviderImplTest extends NbTestCase {

    public SubprojectProviderImplTest(String n) {
        super(n);
    }

    public void testSubprojects() throws Exception {
        clearWorkDir();
        Cache.clear();
        File p1 = new File(getWorkDir(), "p1");
        File p2 = new File(getWorkDir(), "p2");
        TestFileUtils.writeFile(new File(p1, "build.xml"), "<project name='p1'/>");
        TestFileUtils.writeFile(new File(p2, "build.xml"), "<project name='p2'/>");
        Cache.put(p1.getAbsolutePath() + Cache.PROJECT, "true");
        File p1Src = new File(p1, "src");
        p1Src.mkdirs();
        String p1SrcPath = p1Src.getAbsolutePath();
        Cache.put(p1SrcPath + JavaCacheConstants.SOURCE, p1SrcPath);
        String p2BinPath = new File(p2, "build/p2.jar").getAbsolutePath();
        Cache.put(p1SrcPath + JavaCacheConstants.CLASSPATH, p2BinPath);
        Cache.put(p2.getAbsolutePath() + Cache.PROJECT, "true");
        File p2Src = new File(p2, "src");
        p2Src.mkdirs();
        String p2SrcPath = p2Src.getAbsolutePath();
        Cache.put(p2SrcPath + JavaCacheConstants.SOURCE, p2SrcPath);
        Cache.put(p2SrcPath + JavaCacheConstants.BINARY, p2BinPath);
        FileObject p1FO = FileUtil.toFileObject(p1);
        assertNotNull(p1FO);
        Project p1P = ProjectManager.getDefault().findProject(p1FO);
        assertNotNull(p1P);
        FileObject p2FO = FileUtil.toFileObject(p2);
        assertNotNull(p2FO);
        Project p2P = ProjectManager.getDefault().findProject(p2FO);
        assertNotNull(p2P);
        SubprojectProvider spp = p1P.getLookup().lookup(SubprojectProvider.class);
        assertNotNull(spp);
        assertEquals(Collections.singleton(p2P), spp.getSubprojects());
    }

}
