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

package org.netbeans.modules.javafx.project.templates;

import java.io.File;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author answer
 */
public class JavaFXTargetChooserPanelTest extends NbTestCase {
    
    FileObject root = null;
    
    public JavaFXTargetChooserPanelTest(String testName) {
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
/*
    public void testGetComponent() {
        System.out.println("getComponent");
        JavaFXTargetChooserPanel instance = null;
        Component expResult = null;
        Component result = instance.getComponent();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 

    public void testGetHelp() {
        System.out.println("getHelp");
        JavaFXTargetChooserPanel instance = null;
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelp();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 

    public void testIsValid() {
        System.out.println("isValid");
        JavaFXTargetChooserPanel instance = null;
        boolean expResult = false;
        boolean result = instance.isValid();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 

    public void testAddChangeListener() {
        System.out.println("addChangeListener");
        ChangeListener l = null;
        JavaFXTargetChooserPanel instance = null;
        instance.addChangeListener(l);
        fail("The test case is a prototype.");
    } 

    public void testRemoveChangeListener() {
        System.out.println("removeChangeListener");
        ChangeListener l = null;
        JavaFXTargetChooserPanel instance = null;
        instance.removeChangeListener(l);
        fail("The test case is a prototype.");
    } 

    public void testReadSettings() {
        System.out.println("readSettings");
        Object settings = null;
        JavaFXTargetChooserPanel instance = null;
        instance.readSettings(settings);
        fail("The test case is a prototype.");
    } 

    public void testStoreSettings() {
        System.out.println("storeSettings");
        Object settings = null;
        JavaFXTargetChooserPanel instance = null;
        instance.storeSettings(settings);
        fail("The test case is a prototype.");
    } 

    public void testStateChanged() {
        System.out.println("stateChanged");
        ChangeEvent e = null;
        JavaFXTargetChooserPanel instance = null;
        instance.stateChanged(e);
        fail("The test case is a prototype.");
    } 

    public void testIsValidPackageName() {
        System.out.println("isValidPackageName");
        String str = "";
        boolean expResult = false;
        boolean result = JavaFXTargetChooserPanel.isValidPackageName(str);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 

    public void testIsValidTypeIdentifier() {
        System.out.println("isValidTypeIdentifier");
        String ident = "";
        boolean expResult = false;
        boolean result = JavaFXTargetChooserPanel.isValidTypeIdentifier(ident);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 

    public void testIsValidFileName() {
        System.out.println("isValidFileName");
        String ident = "";
        boolean expResult = false;
        boolean result = JavaFXTargetChooserPanel.isValidFileName(ident);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    } 
*/
    public void testCanUseFileName() throws Exception{
        File rootFile = getWorkDir ();
        assertNotNull ("WorkDir exists.", rootFile);
        root = FileUtil.toFileObject (rootFile);
        if (!root.canWrite ()) {
            fail ("Cannot create test folder.");
        }

        root = root.createFolder ("testCanUseFileName");

        assertNotNull (root + " exists.", FileUtil.toFile (root));
        assertTrue ("Package aaa.bbb.ccc can be created.", JavaFXTargetChooserPanel.canUseFileName (root, "", "aaa.bbb.ccc", "") == null);

        assertNotNull ("Package aaa.bbb.ccc was created.", root.createFolder ("aaa").createFolder ("bbb").createFolder ("ccc"));
        assertTrue ("Package aaa cannot be created.", JavaFXTargetChooserPanel.canUseFileName (root, "", "aaa", "") != null);
        assertTrue ("Package aaa.bbb cannot be created.", JavaFXTargetChooserPanel.canUseFileName (root, "", "aaa.bbb", "") != null);
        assertTrue ("Package aaa.bbb.ccc cannot be created.", JavaFXTargetChooserPanel.canUseFileName (root, "", "aaa.bbb.ccc", "") != null);
        assertTrue ("Package ddd can be created.", JavaFXTargetChooserPanel.canUseFileName (root, "", "ddd", "") == null);
    } 
    
}
