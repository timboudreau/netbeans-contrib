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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.List;
import junit.framework.Test;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.TreeRuleTestBase;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Jaroslav Tulach
 */
public class ImageUtilitiesTest extends TreeRuleTestBase {
    
    public ImageUtilitiesTest(String testName) {
        super(testName);
    }

    public void testLoadImage() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "import org.openide.util.Utilities;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        Utilities.lo|adImage(\"Ahoj\");\n" +
                       "    }\n" +
                       "}\n",
                       "4:18-4:27:verifier:Use of Utilities.loadImage",
                       "FixImpl",
                       "package test; import org.openide.util.ImageUtilities; import org.openide.util.Utilities; public class Test { private void test(Object o) { ImageUtilities.loadImage(\"Ahoj\"); } } ");
    }

    public void testLoadImageLocalized() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "import org.openide.util.Utilities;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        Utilities.lo|adImage(\"Ahoj\", true);\n" +
                       "    }\n" +
                       "}\n",
                       "4:18-4:27:verifier:Use of Utilities.loadImage",
                       "FixImpl",
                       "package test; import org.openide.util.ImageUtilities; import org.openide.util.Utilities; public class Test { private void test(Object o) { ImageUtilities.loadImage(\"Ahoj\", true); } } ");
    }

    public void testIcon2Image() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "import org.openide.util.Utilities;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        Utilities.icon2|Image(null);\n" +
                       "    }\n" +
                       "}\n",
                       "4:18-4:28:verifier:Use of Utilities.icon2Image",
                       "FixImpl",
                       "package test; import org.openide.util.ImageUtilities; import org.openide.util.Utilities; public class Test { private void test(Object o) { ImageUtilities.icon2Image(null); } } ");
    }

    public void testMergeImages() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "import org.openide.util.Utilities;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        Utilities.merge|Images(null, null);\n" +
                       "    }\n" +
                       "}\n",
                       "4:18-4:29:verifier:Use of Utilities.mergeImages",
                       "FixImpl",
                       "package test; import org.openide.util.ImageUtilities; import org.openide.util.Utilities; public class Test { private void test(Object o) { ImageUtilities.mergeImages(null, null); } } ");
    }
        
    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        if (f instanceof ImageUtilitiesDeprecation.FixImpl) {
            return "FixImpl";
        }
        
        return super.toDebugString(info, f);
    }

    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path, int offset) {
        ImageUtilitiesDeprecation c = new ImageUtilitiesDeprecation();
        while (path != null && path.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
            path = path.getParentPath();
        }

        if (path == null) {
            return null;
        }
        return c.run(info, path, offset);
    }

    @Override
    protected FileObject[] extraClassPath() {
        FileObject api = URLMapper.findFileObject(Utilities.class.getProtectionDomain().getCodeSource().getLocation());

        assertNotNull(api);

        return new FileObject[] {FileUtil.getArchiveRoot(api)};
    }
    
    static {
        NbBundle.setBranding("test");
    }
    
}
