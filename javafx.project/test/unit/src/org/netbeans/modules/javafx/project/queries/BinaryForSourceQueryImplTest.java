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

package org.netbeans.modules.javafx.project.queries;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.javafx.project.JavaFXProjectGenerator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;

/**
 *
 * @author answer
 */
public class BinaryForSourceQueryImplTest extends NbTestCase {
    
    public BinaryForSourceQueryImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFindBinaryRoots() throws IOException{
        String name = "TestJavaFXApp";
        File proj = new File (getWorkDir(), name);
        proj.mkdir();
        JavaFXProjectGenerator.setDefaultSourceLevel(new SpecificationVersion ("1.4"));   //NOI18N
        AntProjectHelper aph = JavaFXProjectGenerator.createProject(proj, name, null, null);
        JavaFXProjectGenerator.setDefaultSourceLevel(null);
        assertNotNull(aph);
        
        File someOut = new File (getWorkDir(), "SomeFolder");
        someOut.mkdir();
        File someIn = new File (proj, "SomeFolderInProject");
        someIn.mkdir();
        File sources = new File (proj, "src");
        sources.mkdir();
        File build = new File (proj, "build");
        build.mkdir();
        File classes = new File (build, "classes");
        classes.mkdir();
        
        BinaryForSourceQuery.Result result = BinaryForSourceQuery.findBinaryRoots(FileUtil.toFileObject(someOut).getURL());
        assertEquals("Non-project folder does not have any source folder", 0, result.getRoots().length);
        
        result = BinaryForSourceQuery.findBinaryRoots(FileUtil.toFileObject(someIn).getURL());
        assertEquals("Project non build folder does not have any source folder", 0, result.getRoots().length);
        result = BinaryForSourceQuery.findBinaryRoots(FileUtil.toFileObject(sources).getURL());        
        assertEquals("Project build folder must have source folder", 1, result.getRoots().length);
        assertEquals("Project build folder must have source folder",FileUtil.toFileObject(classes).getURL(),result.getRoots()[0]);        
        assertEquals(BinaryForSourceQueryImpl.R.class, result.getClass());
        BinaryForSourceQuery.Result result2 = BinaryForSourceQuery.findBinaryRoots(FileUtil.toFileObject(sources).getURL());
        assertTrue (result == result2);
    }    
}
