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

package org.netbeans.modules.docbook;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.docbook.DocBookNavigatorPanel.Handler;
import org.netbeans.modules.docbook.DocBookNavigatorPanel.Item;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Test functionality of {@link XMLNavigatorPanel}.
 * @author Jesse Glick
 */
public class DocBookNavigatorPanelTest extends NbTestCase {

    public DocBookNavigatorPanelTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
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
        Handler handler = new DocBookNavigatorPanel.Handler(null);
        SAXParserFactory f = SAXParserFactory.newInstance();
        SAXParser parser = f.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        
        reader.setContentHandler(handler);
        EntityResolver resolver = new TestCatalog();
        reader.setEntityResolver(resolver);
        reader.parse(new InputSource(new StringReader(xml)));
        List <Item> l = handler.items;
        return (Item[]) l.toArray(new Item[0]);
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
    
    public static final class TestCatalog extends UserCatalog implements EntityResolver {
        public TestCatalog() {}
        public EntityResolver getEntityResolver() {
            return this;
        }
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    }
    
}
