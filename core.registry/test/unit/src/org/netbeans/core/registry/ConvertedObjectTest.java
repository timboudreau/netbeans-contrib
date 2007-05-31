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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.core.registry.convertors.TestBean;
import org.netbeans.core.registry.convertors.TestBeanConvertor;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.*;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test usage of Convertors API with object bindings.
 * @author Jesse Glick
 */
public class ConvertedObjectTest extends NbTestCase {
    
    public ConvertedObjectTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ConvertedObjectTest.class));
    }
    
    private Context c;
    private FileSystem fs;
    
    protected void setUp() throws Exception {
        super.setUp();
        URL u = ConvertedObjectTest.class.getResource("data/object-layer.xml");
        FileSystem xfs = new XMLFileSystem(u);
        LocalFileSystem lfs = new LocalFileSystem();
        clearWorkDir();
        lfs.setRootDirectory(getWorkDir());
        fs = new MultiFileSystem(new FileSystem[] {lfs, xfs});
        BasicContext rootCtx = FileSystemContextFactory.createContext(fs.getRoot());
        Context root = SpiUtils.createContext(rootCtx);
        c = root.createSubcontext("test-context");
    }
    /*
    protected void tearDown() throws Exception {
    }
     */

    /** Check that read operations work. */
    public void testRead() throws Exception {
        TestBean t1 = (TestBean)c.getObject("binding1", null);
        assertNotNull(t1);
        assertEquals("val1", t1.getProp1());
        assertEquals("val2", t1.getProp2());
        TestBean t2 = (TestBean)c.getObject("binding2", null);
        assertNotNull(t1);
        assertEquals("val3", t2.getProp1());
        assertEquals("val4", t2.getProp2());
        List l = c.getOrderedNames();
        Collections.sort(l);
        assertEquals(Arrays.asList(new String[] {"binding1", "binding2"}), l);
    }
    
    /** Check that writing new values works. */
    public void testWriteNew() throws Exception {
        TestBean t = new TestBean();
        t.setProp1("gen1");
        t.setProp2("gen2");
        c.putObject("binding3", t);
        FileObject fo = fs.findResource("test-context/binding3.xml");
        assertNotNull(fo);
        InputStream is = fo.getInputStream();
        try {
            Document d = XMLUtil.parse(new InputSource(is), false, true, null, null);
            Element e = d.getDocumentElement();
            assertEquals(TestBeanConvertor.NS, e.getNamespaceURI());
            assertEquals("test-bean", e.getLocalName());
            assertEquals("gen1", e.getAttribute("prop1"));
            assertEquals("gen2", e.getAttribute("prop2"));
        } finally {
            is.close();
        }
    }
    
    /** Test that overwriting old values works too. */
    public void testOverwrite() throws Exception {
        TestBean t = (TestBean)c.getObject("binding1", null);
        assertNotNull(t);
        assertEquals("val1", t.getProp1());
        assertEquals("val2", t.getProp2());
        t.setProp1("val1-modified");
        c.putObject("binding1", t);
        assertEquals(t, c.getObject("binding1", null));
        FileObject fo = fs.findResource("test-context/binding1.xml");
        assertNotNull(fo);
        InputStream is = fo.getInputStream();
        try {
            Document d = XMLUtil.parse(new InputSource(is), false, true, null, null);
            Element e = d.getDocumentElement();
            assertEquals(TestBeanConvertor.NS, e.getNamespaceURI());
            assertEquals("test-bean", e.getLocalName());
            assertEquals("val1-modified", e.getAttribute("prop1"));
            assertEquals("val2", e.getAttribute("prop2"));
        } finally {
            is.close();
        }
    }
    
}
