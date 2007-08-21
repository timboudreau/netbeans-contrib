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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node;

import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Satyaranjan
 */
public class PortletNodeFactoryImpl implements NodeFactory {
  
    public PortletNodeFactoryImpl() {
    }
    
 
    public NodeList<?> createNodes(Project p) {
       
            org.netbeans.modules.web.api.webmodule.WebModule wm = org.netbeans.modules.web.api.webmodule.WebModule.getWebModule(p.getProjectDirectory());
            if (wm == null) {
                return null;
            }
            org.openide.filesystems.FileObject webInf = wm.getWebInf();
            org.openide.filesystems.FileObject portletXml = webInf.getFileObject("portlet", "xml");
            if (portletXml == null || !portletXml.existsExt("xml")) {
                return null;
            }
        try {    
            org.openide.loaders.DataObject dob = org.openide.loaders.DataObject.find(portletXml);
            PortletXMLDataNode node = new PortletXMLDataNode((PortletXMLDataObject)dob);
            node.setDisplayName("Portlets");
           // node.setIconBaseWithExtension(null);
            return NodeFactorySupport.fixedNodeList(node);
           // return new PortletNodeList((PortletXMLDataObject)dob);
        } catch (DataObjectNotFoundException ex) {
            
            Exceptions.printStackTrace(ex);
            return null;
        }
        
        
        
    }
}
