/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.docbook;

import java.io.IOException;
import java.io.StringReader;
import junit.framework.Assert;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.junit.NbTestCase;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test functionality of {@link XMLNavigatorPanel}.
 * @author Jesse Glick
 */
public class DocBookNavigatorPanelTest extends NbTestCase {
    
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
        Assert.assertEquals(Lkp.class, Lookup.getDefault().getClass());
    }
    public static final class Lkp extends ProxyLookup {
        private static Lkp DEFAULT;
        public Lkp() {
            Assert.assertNull(DEFAULT);
            DEFAULT = this;
            setLookup(new Object[0]);
        }
        public static void setLookup(Object[] instances) {
            ClassLoader l = Lkp.class.getClassLoader();
            DEFAULT.setLookups(new Lookup[] {
                Lookups.fixed(instances),
                Lookups.metaInfServices(l),
                Lookups.singleton(l),
            });
        }
    }
    
    public DocBookNavigatorPanelTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        Lkp.setLookup(new Object[] {
            new TestCatalog(),
        });
    }
    
    public void testParseDocBook() throws Exception {
        assertEquals("correct parse of some DocBook sections",
            "1[section/title]First\n" +
            "4[section/title]Second\n" +
            "6[section/title]Notes on Second\n",
            itemsSummary(parse(
                "<article>\n" + // 0
                " <section><title>First</title>\n" + // 1
                "  <para>Stuff...</para>\n" + // 2
                " </section>\n" + // 3
                " <section><title>Second</title>\n" + // 4
                "  <para>Stuff...</para>\n" + // 5
                "  <section><title>Notes on Second</title>\n" + // 6
                "   <para>Stuff...</para>\n" + // 7
                "  </section>\n" + // 8
                " </section>\n" + // 9
                "</article>\n" // 10
                )));
        // XXX should DocBook subsections be marked differently?
    }
    
    private static DocBookNavigatorPanel.Item[] parse(String xml) throws Exception {
        return DocBookNavigatorPanel.parse(new InputSource(new StringReader(xml)), null);
    }
    
    private static String itemsSummary(DocBookNavigatorPanel.Item[] items) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < items.length; i++) {
            b.append(items[i].getLine());
            b.append('[');
            b.append(items[i].getElement());
            b.append('/');
            b.append(items[i].getHeader());
            b.append(']');
            b.append(items[i].getLabel());
            b.append('\n');
        }
        return b.toString();
    }
    
    private static final class TestCatalog extends UserCatalog implements EntityResolver {
        public TestCatalog() {}
        public EntityResolver getEntityResolver() {
            return this;
        }
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    }
    
}
