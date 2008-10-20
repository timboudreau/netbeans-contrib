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

package org.netbeans.modules.javahints;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.TreeRuleTestBase;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Jan Lahoda
 */
public class NoDOGetCookieTest extends TreeRuleTestBase {

    public NoDOGetCookieTest(String testName) {
        super(testName);
    }

    public void testGetCookie() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "import org.openide.loaders.DataObject;\n" +
                       "import org.openide.cookies.EditorCookie;\n" +
                       "public class Test {\n" +
                       "    private void test(DataObject o) {\n" +
                       "        o.get|Cookie(EditorCookie.class);\n" +
                       "    }\n" +
                       "}\n",
                       "5:8-5:39:error:Use of DO.getCookie",
                       "FixImpl",
                       "package test; import org.openide.loaders.DataObject; import org.openide.cookies.EditorCookie; public class Test { private void test(DataObject o) { o.getLookup().lookup(EditorCookie.class); } } ");
    }

    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        return "FixImpl";
    }

    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path, int offset) {
        NoDOGetCookie c = new NoDOGetCookie();
        while (path != null && path.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
            path = path.getParentPath();
        }

        if (path == null) {
            return null;
        }
        return c.run(info, path);
    }

    @Override
    protected FileObject[] extraClassPath() {
        List<FileObject> result = new LinkedList<FileObject>();

        for (Class c : new Class[] { Utilities.class, FileObject.class, Node.class, DataObject.class, EditorCookie.class}) {
            FileObject api = URLMapper.findFileObject(c.getProtectionDomain().getCodeSource().getLocation());

            assertNotNull(api);

            result.add(FileUtil.getArchiveRoot(api));
        }

        return result.toArray(new FileObject[0]);
    }

    static {
        NbBundle.setBranding("test");
    }

}
