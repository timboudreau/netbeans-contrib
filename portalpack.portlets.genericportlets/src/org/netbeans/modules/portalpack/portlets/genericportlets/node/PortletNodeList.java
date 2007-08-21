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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Satyaranjan
 */
public class PortletNodeList implements NodeList {

    private PortletXMLDataObject pXmlDObj;

    public PortletNodeList(PortletXMLDataObject portletXmlDataObj) {
        pXmlDObj = portletXmlDataObj;
    }

    public List keys() {
        List list = new ArrayList();
        try {
            org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType[] portletType = ((org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject) pXmlDObj).getPortletApp().getPortlet();
            for (int i = 0; i < portletType.length; i++) {
                list.add(portletType[i]);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return list;
    }

    public void addChangeListener(ChangeListener l) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChangeListener(ChangeListener l) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node node(Object key) {
        return new PortletNode((PortletType)key,pXmlDObj);
    }

    public void addNotify() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeNotify() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
