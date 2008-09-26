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

package org.netbeans.modules.portalpack.websynergy.servicebuilder.catalog;

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
public class RegisterDTDCatalog implements CatalogReader, CatalogDescriptor, EntityResolver {

    private static final String LR_SERVICE_BUILDER_5_1_0_DTD = "liferay-service-builder_5_1_0.dtd";
    private static final String LR_SERVICE_BUILDER_510 = "http://www.liferay.com/dtd/liferay-service-builder_5_1_0.dtd";
    private static final String LR_SERVICE_BUILDER_510_URL = "nbres:/org/netbeans/modules/portalpack/websynergy/servicebuilder/resources/liferay-service-builder_5_1_0.dtd";
    private static final String LR_SERVICE_BUILDER_510_ID = "SCHEMA:" + LR_SERVICE_BUILDER_510;
    
    
    public RegisterDTDCatalog()
    {
        
    }
    public Iterator getPublicIDs() {
        List<String> list = new ArrayList<String>();
        list.add(LR_SERVICE_BUILDER_510_ID);
       
        return list.listIterator();
    }

    public void refresh() {
        
    }

    public String getSystemID(String publicId) {
        if(publicId.equals(LR_SERVICE_BUILDER_510_ID)) {
            return LR_SERVICE_BUILDER_510_URL;
        }else
            return null;
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
        return "Liferay Service Builder Catalog";
    }

    public String getShortDescription() {
        return "XML Catalog for Liferay Service Builder";
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
       
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        
        if (LR_SERVICE_BUILDER_510.equals(systemId)){
            return new org.xml.sax.InputSource(LR_SERVICE_BUILDER_510_URL);
        }
        if (systemId != null && systemId.endsWith(LR_SERVICE_BUILDER_5_1_0_DTD)){
            return new org.xml.sax.InputSource(LR_SERVICE_BUILDER_510_URL);
        }

        return null;
    }

}
