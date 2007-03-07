/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
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
