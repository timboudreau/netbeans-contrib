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

package org.netbeans.modules.loaderswitcher;

import java.io.IOException;
import java.util.logging.Level;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.*;

/** Checks the functional behaviour of the Object Type Switcher.
 *
 * @author Jaroslav Tulach
 */
public class ObjectTypeTest extends NbTestCase
implements DataLoader.RecognizedFiles {

    public ObjectTypeTest(String testName) {
        super(testName);
    }

    protected Level logLevel() {
        return Level.ALL;
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testFindAndSwitch() throws Exception {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fnd = fs.getRoot().createFolder("testFindAndSwitch");
        
        DataObject obj = InstanceDataObject.create(DataFolder.findFolder(fnd), "ahoj", java.awt.FlowLayout.class);
        
        DataLoader[] x = ObjectType.findPossibleLoaders(obj, this);
        
        assertEquals("Two loaders shall be interested", 2, x.length);
        assertEquals("First one is the actual loader", obj.getLoader(), x[0]);

        assertEquals("Name", "ahoj", obj.getName());

        ObjectType.convertTo(obj, x[1]);

        assertFalse("Old object is invalidated", obj.isValid());

        DataObject n = DataObject.find(obj.getPrimaryFile());

        if (n == obj) {
            fail("They should be different: " + n);
        }

        assertEquals("The right loader", x[1], n.getLoader());


        assertEquals("Name with extension", "ahoj.instance", n.getName());

        n.rename("kuk.unknown");

        DataLoader[] arr = ObjectType.findPossibleLoaders(n, this);
        assertEquals("Just one loader now", 1, arr.length);
        assertEquals("and it is the default loader", n.getLoader(), arr[0]);

    }

    public void markRecognized(FileObject fo) {
    }
}
