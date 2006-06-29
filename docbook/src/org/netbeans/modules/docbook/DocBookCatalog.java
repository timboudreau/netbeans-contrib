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

package org.netbeans.modules.docbook;

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

public class DocBookCatalog implements CatalogProvider {
    
    private static final Map/*<String,String>*/ PUBLIC_2_SYSTEM = new HashMap();
    static {
        PUBLIC_2_SYSTEM.put("-//Norman Walsh//DTD Slides XML V" + Config.SLIDES_VERSION + "//EN",
                            "nbres:/org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/schema/dtd/slides.dtd");
        PUBLIC_2_SYSTEM.put("-//OASIS//DTD DocBook XML V" + Config.DOCBOOK_XML_VERSION + "//EN",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/docbookx.dtd");
        // XXX slides-full.dtd
    }
    
    private static final Map/*<String,String>*/ SYSTEM_2_SYSTEM = new HashMap();
    static {
        SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/slides/current/",
                            "nbres:/org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/");
        SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/xsl/current/",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xsl-" + Config.DOCBOOK_XSL_VERSION + "/");
        SYSTEM_2_SYSTEM.put("http://www.oasis-open.org/docbook/xml/" + Config.DOCBOOK_XML_VERSION + "/",
                            "nbres:/org/netbeans/modules/docbook/lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/");
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
            //System.err.println("rE: " + publicId + " ~ " + systemId);
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
            return NbBundle.getMessage(DocBookCatalog.class, "LBL_db_catalog");
        }
        
        public String getShortDescription() {
            return NbBundle.getMessage(DocBookCatalog.class, "HINT_db_catalog");
        }
        
        public Image getIcon(int type) {
            if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
                return Utilities.loadImage("org/netbeans/modules/docbook/docbook.png", true);
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
