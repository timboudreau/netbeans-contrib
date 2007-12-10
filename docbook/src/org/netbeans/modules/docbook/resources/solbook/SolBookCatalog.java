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

package org.netbeans.modules.docbook.resources.solbook;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.modules.xml.catalog.spi.CatalogDescriptor;
import org.netbeans.modules.xml.catalog.spi.CatalogListener;
import org.netbeans.modules.xml.catalog.spi.CatalogProvider;
import org.netbeans.modules.xml.catalog.spi.CatalogReader;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* Specifies the various DTD and schemas
 * 
 */
public class SolBookCatalog implements CatalogProvider {
    
    //maps are package private for unit tests
    
    static final Map<String,String> PUBLIC_2_SYSTEM = new HashMap<String,String>();
    static {
/*        PUBLIC_2_SYSTEM.put("-//Norman Walsh//DTD Slides XML V" + Config.SLIDES_VERSION + "//EN",
                            "nbres:/org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/schema/dtd/slides.dtd");
        PUBLIC_2_SYSTEM.put("-//OASIS//DTD DocBook XML V" + Config.DOCBOOK_XML_VERSION + "//EN",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/docbookx.dtd");
        PUBLIC_2_SYSTEM.put("-//OASIS//DTD DocBook XML V4.4//EN",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/docbookx.dtd");
*/        PUBLIC_2_SYSTEM.put("-//Sun Microsystems//DTD XML-SolBook 3.5 //EN",
                            "nbres:/org/netbeans/modules/docbook/lib/solbook-xml-" + Config.SOLBOOK_XML_VERSION + "/solbookx.dtd--public2system");
        // XXX slides-full.dtd
    }
    
    static final Map<String,String> SYSTEM_2_SYSTEM = new HashMap<String,String>();
    static {
/*        SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/xsl/current/",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xsl-" + Config.DOCBOOK_XSL_VERSION + "/");
        SYSTEM_2_SYSTEM.put("http://www.oasis-open.org/docbook/xml/" + Config.DOCBOOK_XML_VERSION + "/",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/");
        SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl",
                            "nbres:/org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/");
*/        SYSTEM_2_SYSTEM.put("http://www.sun.com/solbook/xml/" + Config.SOLBOOK_XML_VERSION + "/",
                            "nbres:/org/netbeans/modules/docbook/lib/solbook-xml-" + Config.SOLBOOK_XML_VERSION + "/--system2system");
    }
    
    public Class provideClass() throws IOException, ClassNotFoundException {
        return Reader.class;
    }
    
    public static final class Reader implements CatalogReader, CatalogDescriptor, EntityResolver, Serializable {
        
        private static final long serialVersionUID = 1L;

        public Iterator getPublicIDs() {
            return PUBLIC_2_SYSTEM.keySet().iterator();
        }

        public String getSystemID(String publicId) {
            return (String)PUBLIC_2_SYSTEM.get(publicId);
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (publicId != null) {
                String systemId2 = getSystemID(publicId);
                if (systemId2 != null) {
                    return new InputSource(systemId2);
                }
            }
            Iterator it = SYSTEM_2_SYSTEM.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();
                String prefix = (String)e.getKey();
                if (systemId.startsWith(prefix)) {
                    String suffix = (String)e.getValue();
                    return new InputSource(suffix + systemId.substring(prefix.length()));
                }
            }
            return null;
        }
        
        public String getDisplayName() {
            return NbBundle.getMessage(SolBookCatalog.class, "LBL_sb_catalog");
        }
        
        public String getShortDescription() {
            return NbBundle.getMessage(SolBookCatalog.class, "HINT_sb_catalog");
        }
        
        public Image getIcon(int type) {
            if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
                return Utilities.loadImage("org/netbeans/modules/docbook/resources/solbook/templates/solbook.png", true);
            } else {
                return null;
            }
        }
        
        public void refresh() {}

        public void addCatalogListener(CatalogListener l) {}

        public void removeCatalogListener(CatalogListener l) {}
        
        public void addPropertyChangeListener(PropertyChangeListener l) {}
        
        public void removePropertyChangeListener(PropertyChangeListener l) {}

        public String resolveURI(String name) {
            return null;
        }

        public String resolvePublic(String publicId) {
            return null;
        }
        
    }
    
}
