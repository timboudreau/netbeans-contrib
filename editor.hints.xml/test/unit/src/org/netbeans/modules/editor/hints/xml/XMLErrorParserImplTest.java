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

package org.netbeans.modules.editor.hints.xml;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class XMLErrorParserImplTest extends NbTestCase {
    
    private static final String PUBLIC_ID = "-//NetBeans//DTD Test 1.0//EN";
    
    static {
        System.setProperty("org.openide.util.Lookup", "org.netbeans.modules.editor.hints.xml.XMLErrorParserImplTest$Lkp");
    }
    
    public XMLErrorParserImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        assertTrue(Lookup.getDefault() instanceof Lkp);
        
        URL u = getClass().getResource("/org/netbeans/modules/editor/hints/xml/layer.xml");
        XMLFileSystem xfs = new XMLFileSystem(u);
        
        Repository r = new Repository(xfs);
        
        ((Lkp) Lookup.getDefault()).setLookupsInt(new Lookup[] {
            Lookups.singleton(r),
            Lookups.singleton(new UserCatalogImpl()),
        });
    }

    /**
     * Test of parseForErrors method, of class org.netbeans.modules.editor.hints.xml.XMLErrorParserImpl.
     */
    public void testNoDTD() throws Exception {
        Document doc = new PlainDocument();
        
        doc.insertString(0, "<root></root>", null);
        
        List errors = new XMLErrorParserImpl().parseForErrors(doc);
        
        assertEquals(0, errors.size());
        
        doc.remove(0, doc.getLength());
        doc.insertString(0, "<root></ro", null);
        
        errors = new XMLErrorParserImpl().parseForErrors(doc);
        
        assertEquals(1, errors.size());
    }

    public void testDTD() throws Exception {
        Document doc = new PlainDocument();
        
        doc.insertString(0, "<!DOCTYPE root PUBLIC \"-//NetBeans//DTD Test 1.0//EN\" \"\"><root name=''><el></el></root>", null);
        
        List errors = new XMLErrorParserImpl().parseForErrors(doc);
        
        assertEquals(errors.toString(), 0, errors.size());
        
        doc.remove(0, doc.getLength());
        doc.insertString(0, "<!DOCTYPE root PUBLIC \"-//NetBeans//DTD Test 1.0//EN\" \"\"><root name=''><el></el></ro", null);
        
        errors = new XMLErrorParserImpl().parseForErrors(doc);
        
        assertEquals(errors.toString(), 1, errors.size());
        
        doc.remove(0, doc.getLength());
        doc.insertString(0, "<!DOCTYPE root PUBLIC \"-//NetBeans//DTD Test 1.0//EN\" \"\"><root></root>", null);
        
        errors = new XMLErrorParserImpl().parseForErrors(doc);
        
        assertEquals(errors.toString(), 2, errors.size());
    }
    
    public static class UserCatalogImpl extends UserCatalog implements EntityResolver {
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            System.err.println("publicId=" + publicId);
            if (PUBLIC_ID.equals(publicId)) {
                URL dtd = getClass().getResource("/org/netbeans/modules/editor/hints/xml/test.dtd");
                
                assert dtd != null;
                
                return new InputSource(dtd.toExternalForm());
            }
            
            return null;
        }
        
        public EntityResolver getEntityResolver() {
            return this;
        }
        
    }
    
    public static class Lkp extends ProxyLookup {
        
        public void setLookupsInt(Lookup[] l) {
            setLookups(l);
        }
    }
    
}
