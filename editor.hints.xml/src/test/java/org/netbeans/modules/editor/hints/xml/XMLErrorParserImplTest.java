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
