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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
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
/*
 * RemoteProjectWizardIteratorTest.java
 * JUnit based test
 *
 * Created on March 5, 2007, 2:57 PM
 */

package org.netbeans.modules.remoteproject;

import java.io.File;
import java.util.Collection;
import junit.framework.TestCase;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Lookup;
/**
 *
 * @author Tim Boudreau
 */
public class RemoteProjectWizardIteratorTest extends TestCase {
    
    public RemoteProjectWizardIteratorTest(String testName) {
        super(testName);
    }
    
    FileObject template;
    protected void setUp() throws Exception {
        super.setUp();
        File tempfile = new File (System.getProperty("java.io.tmpdir"));
        String nm = "" + System.currentTimeMillis();
        String ext = "test";
        File target = new File (tempfile, nm + "." + ext);
        target.deleteOnExit();
        if (target.exists() && !target.delete()) {
            fail ("Could not delete " + target.getPath());
        }
        target.createNewFile();
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(tempfile);
        template = lfs.getRoot().getFileObject(nm, ext);
        assertNotNull (template);
        template.setAttribute("vcs", 
                "vcs");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testLookupIsSane() {
        System.out.println("testLookupIsSane");
        Collection <? extends CheckoutHandler> c = 
                Lookup.getDefault().lookupAll (CheckoutHandler.class);
        assertFalse (c.isEmpty());
        assertTrue (c.iterator().next() instanceof DummyCheckoutHandler);
        assertTrue (c.iterator().next().canCheckout(template));
    }
    
    public void testIterator() throws Exception {
        System.out.println("testIterator");
        Collection <? extends CheckoutHandler> c = 
                Lookup.getDefault().lookupAll (CheckoutHandler.class);
        DummyCheckoutHandler handler = (DummyCheckoutHandler) c.iterator().next();
        RemoteProjectWizardIterator iter = RemoteProjectWizardIterator.createIterator();
        WizardDescriptor wiz = new WizardDescriptor(iter);
        wiz.putProperty("projdir", new File (
                System.getProperty("java.io.tmpdir")));
        wiz.putProperty("targetTemplate", template);
        iter.initialize(wiz);
        assertFalse (handler.wasCheckoutCalled());
        iter.instantiate(null);
        assertTrue (handler.wasCheckoutCalled());
    }
}
