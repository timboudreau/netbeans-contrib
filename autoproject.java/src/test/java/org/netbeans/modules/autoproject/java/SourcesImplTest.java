/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.io.File;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.MockPropertyChangeListener;

public class SourcesImplTest extends NbTestCase {

    public SourcesImplTest(String n) {
        super(n);
    }

    private FileObject pFO;
    private String pSrcPath;
    private FileObject pSrcFO;

    protected @Override void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
        File p = new File(getWorkDir(), "p");
        Cache.put(p.getAbsolutePath() + Cache.PROJECT, "true");
        File pSrc = new File(p, "src");
        pSrc.mkdirs();
        pSrcFO = FileUtil.toFileObject(pSrc);
        assertNotNull(pSrcFO);
        pSrcPath = pSrc.getAbsolutePath();
        Cache.put(pSrcPath + JavaCacheConstants.SOURCE, pSrcPath);
        pFO = FileUtil.toFileObject(p);
        assertNotNull(pFO);
    }

    public void testBasicSources() throws Exception {
        Sources s = ProjectUtils.getSources(ProjectManager.getDefault().findProject(pFO));
        SourceGroup[] gs = s.getSourceGroups(Sources.TYPE_GENERIC);
        assertEquals(1, gs.length);
        assertEquals(pFO, gs[0].getRootFolder());
        gs = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        assertEquals(1, gs.length);
        assertEquals(pSrcFO, gs[0].getRootFolder());
        assertEquals(0, s.getSourceGroups("whatever").length);
        // XXX add a new source root, check that change is fired and root returned
        // XXX check that deletion of source root results in group being removed
    }

    public void testIncludesExcludes() throws Exception {
        Cache.put(pSrcPath + JavaCacheConstants.INCLUDES, "com/");
        Cache.put(pSrcPath + JavaCacheConstants.EXCLUDES, "com/foreign1/,com/foreign2/");
        FileObject j1 = FileUtil.createData(pSrcFO, "com/domestic/Class.java");
        FileObject j2 = FileUtil.createData(pSrcFO, "com/foreign1/Class.java");
        FileObject j3 = FileUtil.createData(pSrcFO, "com/foreign2/Class.java");
        Sources s = ProjectUtils.getSources(ProjectManager.getDefault().findProject(pFO));
        SourceGroup[] gs = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        assertEquals(1, gs.length);
        assertEquals(pSrcFO, gs[0].getRootFolder());
        assertTrue(gs[0].contains(j1));
        assertFalse(gs[0].contains(j2));
        assertFalse(gs[0].contains(j3));
        MockPropertyChangeListener pcl = new MockPropertyChangeListener(SourceGroup.PROP_CONTAINERSHIP);
        gs[0].addPropertyChangeListener(pcl);
        Cache.put(pSrcPath + JavaCacheConstants.EXCLUDES, "com/foreign1/");
        pcl.assertEvents(SourceGroup.PROP_CONTAINERSHIP);
        assertTrue(gs[0].contains(j1));
        assertFalse(gs[0].contains(j2));
        assertTrue(gs[0].contains(j3));
    }

}
