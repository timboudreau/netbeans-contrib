/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.experimental.categories.Categories;
import org.netbeans.junit.NbTestCase;
import static org.netbeans.modules.licensechanger.TestUtils.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Nils Hoffmann
 */
public class LicenseHeaderTest extends NbTestCase {

    private LicenseHeader golden;

    public LicenseHeaderTest(String name) {
        super(name);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.out.println("Creating golden license header");
        File f;
        try {
            f = new File(getWorkDir(), "license-test.txt");
            writeFile(readFile("license-test.txt"), f);
            Assert.assertTrue(f.exists());
            golden = LicenseHeader.fromFile(f);
            System.out.println("Golden: " + golden);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test that file object may not be null, of class LicenseHeader.
     */
    @Test
    public void testNullArgs1() {
        boolean npeCaught = false;
        try {
            LicenseHeader actual = LicenseHeader.fromFileObject(null, golden.getName(), false);
        } catch (NullPointerException npe) {
            npeCaught = true;
        }
        Assert.assertTrue(npeCaught);
    }
    
    /**
     * Test that file object may not be null, of class LicenseHeader.
     */
    @Test
    public void testNullArgs2() {
        boolean npeCaught = false;
        try {
            LicenseHeader actual = LicenseHeader.fromFileObject(null, golden.getName());
        } catch (NullPointerException npe) {
            npeCaught = true;
        }
        Assert.assertTrue(npeCaught);
    }
    
    /**
     * Test that file object may not be null, of class LicenseHeader.
     */
    @Test
    public void testNullArgs3() {
        boolean npeCaught = false;
        try {
            LicenseHeader actual = LicenseHeader.fromFile(null);
        } catch (NullPointerException npe) {
            npeCaught = true;
        }
        Assert.assertTrue(npeCaught);
    }
    
    /**
     * Test retrieval of license header name from file object, of class LicenseHeader.
     */
    @Test
    public void testNullArgs4() {
        LicenseHeader actual = LicenseHeader.fromFileObject(golden.getFileObject(), null, false);
    }

    /**
     * Test of fromFileObject method, of class LicenseHeader.
     */
    @Test
    public void testFromFileObject_3args() {
        LicenseHeader actual = LicenseHeader.fromFileObject(golden.getFileObject(), golden.getName(), false);
        Assert.assertEquals(golden, actual);
    }

    /**
     * Test of fromFileObject method, of class LicenseHeader.
     */
    @Test
    public void testFromFileObject_FileObject_String() {
        LicenseHeader actual = LicenseHeader.fromFileObject(golden.getFileObject(), golden.getName());
        Assert.assertEquals(golden, actual);
    }

    /**
     * Test of fromFile method, of class LicenseHeader.
     */
    @Test
    public void testFromFile() {
        LicenseHeader actual = LicenseHeader.fromFile(FileUtil.toFile(golden.getFileObject()));
        Assert.assertEquals(golden, actual);
    }

    /**
     * Test of addAsNetBeansTemplate and fromTemplates methods, of class
     * LicenseHeader.
     */
    @Test
    public void testAddAsNetBeansTemplateFromTemplates() throws Exception {
        FileObject configRoot = FileUtil.getConfigRoot();
        FileObject licenseTemplates = configRoot.getFileObject("Templates").createFolder("Licenses");
        LicenseHeader goldenTemplate = LicenseHeader.addAsNetBeansTemplate(golden);
        Assert.assertFalse(golden.isNetBeansTemplate());
        Assert.assertTrue(goldenTemplate.isNetBeansTemplate());
        Assert.assertFalse(goldenTemplate.getLicenseHeader().isEmpty());
        Collection<? extends LicenseHeader> licenses = LicenseHeader.fromTemplates();
        Assert.assertTrue(licenses.contains(goldenTemplate));
    }
}
