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
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class SourceForBinaryImplTest extends NbTestCase {

    public SourceForBinaryImplTest(String n) {
        super(n);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
    }

    public void testClassesDir() throws Exception {
        File p = new File(getWorkDir(), "p");
        Cache.put(p.getAbsolutePath() + Cache.PROJECT, "true");
        File pSrc = new File(p, "src");
        pSrc.mkdirs();
        String pSrcPath = pSrc.getAbsolutePath();
        Cache.put(pSrcPath + JavaCacheConstants.SOURCE, pSrcPath);
        File pBin = new File(p, "classes");
        pBin.mkdirs();
        Cache.put(pSrcPath + JavaCacheConstants.BINARY, pBin.getAbsolutePath());
        FileObject pFO = FileUtil.toFileObject(p);
        assertNotNull(pFO);
        assertEquals(Collections.singletonList(pFO.getFileObject("src")),
                Arrays.asList(SourceForBinaryQuery.findSourceRoots(FileUtil.urlForArchiveOrDir(pBin)).getRoots()));
    }

    public void testJar() throws Exception {
        File p = new File(getWorkDir(), "p");
        Cache.put(p.getAbsolutePath() + Cache.PROJECT, "true");
        File pSrc = new File(p, "src");
        pSrc.mkdirs();
        String pSrcPath = pSrc.getAbsolutePath();
        Cache.put(pSrcPath + JavaCacheConstants.SOURCE, pSrcPath);
        File pBin = new File(p, "classes");
        pBin.mkdirs();
        Cache.put(pSrcPath + JavaCacheConstants.BINARY, pBin.getAbsolutePath());
        File pJar = new File(p, "created.jar");
        new FileOutputStream(pJar).close();
        Cache.put(pJar + JavaCacheConstants.JAR, pBin.getAbsolutePath());
        FileObject pFO = FileUtil.toFileObject(p);
        assertNotNull(pFO);
        assertEquals(Collections.singletonList(pFO.getFileObject("src")),
                Arrays.asList(SourceForBinaryQuery.findSourceRoots(FileUtil.urlForArchiveOrDir(pBin)).getRoots()));
        assertEquals(Collections.singletonList(pFO.getFileObject("src")),
                Arrays.asList(SourceForBinaryQuery.findSourceRoots(FileUtil.urlForArchiveOrDir(pJar)).getRoots()));

    }

}
