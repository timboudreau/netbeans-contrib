/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.guiproject;

import junit.framework.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.UserQuestionException;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Mostly copied from ant/project's AntProjectHelperTest and modified for LaTeX project by Jan Lahoda.
 *
 */
public class LaTeXAuxiliaryConfigurationImplTest extends ProjectTestCase {
    
    public LaTeXAuxiliaryConfigurationImplTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();//LaTeXAuxiliaryConfigurationImplTest.class);
        
        suite.addTest(TestSuite.createTest(LaTeXAuxiliaryConfigurationImplTest.class, "testExtensibleMetadataProviderImpl"));
        
        return suite;
    }

    /**
     * Test of getConfigurationFragment method, of class org.netbeans.modules.latex.guiproject.LaTeXAuxiliaryConfigurationImpl.
     */
    public void testGetConfigurationFragment() {
        System.out.println("testGetConfigurationFragment");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of putConfigurationFragment method, of class org.netbeans.modules.latex.guiproject.LaTeXAuxiliaryConfigurationImpl.
     */
    public void testPutConfigurationFragment() {
        System.out.println("testPutConfigurationFragment");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of removeConfigurationFragment method, of class org.netbeans.modules.latex.guiproject.LaTeXAuxiliaryConfigurationImpl.
     */
    public void testRemoveConfigurationFragment() {
        System.out.println("testRemoveConfigurationFragment");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of findElement method, of class org.netbeans.modules.latex.guiproject.LaTeXAuxiliaryConfigurationImpl.
     */
    public void testFindElement() {
        System.out.println("testFindElement");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }
    
    /**
     * Test that it is possible for external code to store custom data in project.xml and private.xml.
     * @throws Exception if anything unexpected happens
     */
    public void testExtensibleMetadataProviderImpl() throws Exception {
        AuxiliaryConfiguration aux = (AuxiliaryConfiguration)ProjectManager.getDefault().findProject(prj1Impl).getLookup().lookup(AuxiliaryConfiguration.class);
        assertNotNull("AuxiliaryConfiguration present", aux);

        // Check read of shared data.
        Element data = aux.getConfigurationFragment("data", "urn:test:shared-aux", true);
        assertNotNull("found shared <data>", data);
        assertEquals("correct name", "data", data.getLocalName());
        assertEquals("correct namespace", "urn:test:shared-aux", data.getNamespaceURI());
        Element stuff = LaTeXAuxiliaryConfigurationImpl.findElement(data, "aux-shared-stuff", "urn:test:shared-aux");
        assertNotNull("found <aux-shared-stuff/>", stuff);
        
//        assertEquals("gCF fires no changes", 0, l.events().length);
        
        // Check write of shared data.
        stuff.setAttribute("attr", "val");
        
//        assertFalse("project not modified by local change", pm.isModified(p));
        
        aux.putConfigurationFragment(data, true);
        
//        assertTrue("now project is modified", pm.isModified(p));
//        AntProjectEvent[] evs = l.events();
//        assertEquals("pCF fires one event", 1, evs.length);
//        assertEquals("correct helper", h, evs[0].getHelper());
//        assertEquals("correct path", AntProjectHelper.PROJECT_XML_PATH, evs[0].getPath());
//        assertTrue("expected change", evs[0].isExpected());
//        pm.saveProject(p);
//        assertEquals("saving project fires no new changes", 0, l.events().length);
//        Document doc = AntBasedTestUtil.slurpXml(h, AntProjectHelper.PROJECT_XML_PATH);
//        Element config = Util.findElement(doc.getDocumentElement(), "configuration", AntProjectHelper.PROJECT_NS);
//        assertNotNull("<configuration> still exists", config);
//        data = Util.findElement(config, "data", "urn:test:shared-aux");
//        assertNotNull("<data> still exists", data);
//        stuff = Util.findElement(data, "aux-shared-stuff", "urn:test:shared-aux");
//        assertNotNull("still have <aux-shared-stuff/>", stuff);
//        assertEquals("attr written correctly", "val", stuff.getAttribute("attr"));
        
        // Check read of private data.
        data = aux.getConfigurationFragment("data", "urn:test:private-aux", false);
        assertNotNull("found shared <data>", data);
        assertEquals("correct name", "data", data.getLocalName());
        assertEquals("correct namespace", "urn:test:private-aux", data.getNamespaceURI());
        stuff = LaTeXAuxiliaryConfigurationImpl.findElement(data, "aux-private-stuff", "urn:test:private-aux");
        assertNotNull("found <aux-private-stuff/>", stuff);
        
//        assertEquals("gCF fires no changes", 0, l.events().length);
        
        // Check write of private data.
        stuff.setAttribute("attr", "val");
//        assertFalse("project not modified by local change", pm.isModified(p));
        aux.putConfigurationFragment(data, false);
        
//        assertTrue("now project is modified", pm.isModified(p));
//        evs = l.events();
//        assertEquals("pCF fires one event", 1, evs.length);
//        assertEquals("correct helper", h, evs[0].getHelper());
//        assertEquals("correct path", AntProjectHelper.PRIVATE_XML_PATH, evs[0].getPath());
//        assertTrue("expected change", evs[0].isExpected());
//        pm.saveProject(p);
//        assertEquals("saving project fires no new changes", 0, l.events().length);
//        doc = AntBasedTestUtil.slurpXml(h, AntProjectHelper.PRIVATE_XML_PATH);
//        config = doc.getDocumentElement();
//        data = Util.findElement(config, "data", "urn:test:private-aux");
//        assertNotNull("<data> still exists", data);
//        stuff = Util.findElement(data, "aux-private-stuff", "urn:test:private-aux");
//        assertNotNull("still have <aux-private-stuff/>", stuff);
//        assertEquals("attr written correctly", "val", stuff.getAttribute("attr"));
        
        // Check that missing fragments are not returned.
        Element bogus = aux.getConfigurationFragment("doesn't exist", "bogus", true);
        assertNull("no such fragment - wrong name/ns", bogus);
        bogus = aux.getConfigurationFragment("data", "bogus", true);
        assertNull("no such fragment - wrong ns", bogus);
        bogus = aux.getConfigurationFragment("doesn't exist", "urn:test:shared-aux", true);
        assertNull("no such fragment - wrong name", bogus);
        bogus = aux.getConfigurationFragment("data", "urn:test:shared-aux", false);
        assertNull("no such fragment - wrong file", bogus);
        
        // Try adding a new fragment.
        Document temp = XMLUtil.createDocument("whatever", null, null, null);
        data = temp.createElementNS("urn:test:whatever", "hello");
        data.appendChild(temp.createTextNode("stuff"));
//        assertFalse("project currently unmodified", pm.isModified(p));
        aux.putConfigurationFragment(data, true);
//        assertTrue("adding frag modified project", pm.isModified(p));
//        evs = l.events();
//        assertEquals("pCF fires one event", 1, evs.length);
//        assertEquals("correct path", AntProjectHelper.PROJECT_XML_PATH, evs[0].getPath());
//        pm.saveProject(p);
//        assertEquals("saving project fires no new changes", 0, l.events().length);
        data = aux.getConfigurationFragment("hello", "urn:test:whatever", true);
        assertNotNull("can retrieve new frag", data);
//        doc = AntBasedTestUtil.slurpXml(h, AntProjectHelper.PROJECT_XML_PATH);
//        config = Util.findElement(doc.getDocumentElement(), "configuration", AntProjectHelper.PROJECT_NS);
//        assertNotNull("<configuration> still exists", config);
//        data = Util.findElement(config, "hello", "urn:test:whatever");
//        assertNotNull("<hello> still exists", data);
//        assertEquals("correct nested contents too", "stuff", Util.findText(data));
        
        // Try removing a fragment.
//        assertFalse("project is unmodified", pm.isModified(p));
        assertTrue("can remove new frag", aux.removeConfigurationFragment("hello", "urn:test:whatever", true));
//        assertTrue("project is now modified", pm.isModified(p));
        assertNull("now frag is gone", aux.getConfigurationFragment("hello", "urn:test:whatever", true));
//        pm.saveProject(p);
//        doc = AntBasedTestUtil.slurpXml(h, AntProjectHelper.PROJECT_XML_PATH);
//        config = Util.findElement(doc.getDocumentElement(), "configuration", AntProjectHelper.PROJECT_NS);
//        assertNotNull("<configuration> still exists", config);
//        data = Util.findElement(config, "hello", "urn:test:whatever");
//        assertNull("now <hello> is gone", data);
//        assertFalse("cannot remove a frag that is not there", aux.removeConfigurationFragment("hello", "urn:test:whatever", true));
//        assertFalse("trying to remove a nonexistent frag does not modify project", pm.isModified(p));
        
        // check that created elements are ordered
//        data = temp.createElementNS("namespace", "ccc");
//        aux.putConfigurationFragment(data, true);
//        data = temp.createElementNS("namespace", "bbb");
//        aux.putConfigurationFragment(data, true);
//        data = temp.createElementNS("namespace", "aaa");
//        aux.putConfigurationFragment(data, true);
//        data = temp.createElementNS("namespace-1", "bbb");
//        aux.putConfigurationFragment(data, true);
//        data = temp.createElementNS("name-sp", "bbb");
//        aux.putConfigurationFragment(data, true);
//        data = temp.createElementNS("namespace", "aaaa");
//        aux.putConfigurationFragment(data, true);
//        pm.saveProject(p);
//        doc = AntBasedTestUtil.slurpXml(h, AntProjectHelper.PROJECT_XML_PATH);
//        config = Util.findElement(doc.getDocumentElement(), "configuration", AntProjectHelper.PROJECT_NS);
//        String[] names = new String[]{"aaa-namespace", "aaaa-namespace", "bbb-name-sp", "bbb-namespace", "bbb-namespace-1", "ccc-namespace", "data-urn:test:shared", "data-urn:test:shared-aux"};
//        int count = 0;
//        NodeList list = config.getChildNodes();
//        for (int i=0; i<list.getLength(); i++) {
//            Node n = list.item(i);
//            if (n.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
//            assertEquals(names[count], n.getNodeName()+"-"+n.getNamespaceURI());
//            count++;
//        }
//        assertEquals("Elements count does not match", names.length, count);
        
        // XXX check that it cannot be used to load or store primary configuration data
        // or other general fixed metadata
        // XXX try overwriting data
    }
}
