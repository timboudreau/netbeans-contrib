/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.codecompletion.catalog;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.xml.catalog.spi.CatalogDescriptor;
import org.netbeans.modules.xml.catalog.spi.CatalogListener;
import org.netbeans.modules.xml.catalog.spi.CatalogReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Satyaranjan
 */
public class RegisterXSDCatalog implements CatalogReader, CatalogDescriptor, EntityResolver {

    private static final String PORTLET_20_XSD = "portlet-app_2_0.xsd";
    private static final String PORTLET_20 = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd";
    private static final String PORTLET_20_URL = "nbres:/org/netbeans/modules/portalpack/portlets/genericportlets/portlet-app_2_0.xsd";
    private static final String PORTLET_20_ID = "SCHEMA:" + PORTLET_20;
    
    private static final String PORTLET_10_XSD = "portlet-app_1_0.xsd";
    private static final String PORTLET_10 = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    private static final String PORTLET_10_URL = "nbres:/org/netbeans/modules/portalpack/portlets/genericportlets/portlet-app_1_0.xsd";
    private static final String PORTLET_10_ID = "SCHEMA:" + PORTLET_10;
    
    public RegisterXSDCatalog()
    {
        
    }
    public Iterator getPublicIDs() {
        List<String> list = new ArrayList<String>();
        list.add(PORTLET_20_ID);
        list.add(PORTLET_10_ID);
       
        return list.listIterator();
    }

    public void refresh() {
        
    }

    public String getSystemID(String publicId) {
        if(publicId.equals(PORTLET_20_ID)) {
            return PORTLET_20_URL;
        } else if(publicId.equals(PORTLET_10_ID)) {
            return PORTLET_10_URL;
        } else {
            return null;
        }
    }

    public String resolveURI(String name) {
        return null;
    }

    public String resolvePublic(String publicId) {
        return null;
    }

    public void addCatalogListener(CatalogListener l) {
        
    }

    public void removeCatalogListener(CatalogListener l) {
        
    }

    public Image getIcon(int type) {
        return null;
    }

    public String getDisplayName() {
        return "Portlet XML Catalog";
    }

    public String getShortDescription() {
        return "XML Catalog for Portlet Development";
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (PORTLET_20.equals(systemId)){
            return new org.xml.sax.InputSource(PORTLET_20_URL);
        }
        if (systemId != null && systemId.endsWith(PORTLET_20_XSD)){
            return new org.xml.sax.InputSource(PORTLET_20_URL);
        }
        
        if (PORTLET_10.equals(systemId)){
            return new org.xml.sax.InputSource(PORTLET_10_URL);
        }
        if (systemId != null && systemId.endsWith(PORTLET_10_XSD)){
            return new org.xml.sax.InputSource(PORTLET_10_URL);
        }

        return null;
    }

}
