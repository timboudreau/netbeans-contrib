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

package org.netbeans.modules.portalpack.portlets.genericportlets.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 * Node implementation for children of Portlet Xml Node
 * 
 * @author Satyaranjan
 */
public class PortletXMLChildrenNode extends Children.Keys {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private PortletXMLDataObject dbObj;

    public PortletXMLChildrenNode(PortletXMLDataObject dbObj) {

        this.dbObj = dbObj;
    }

    public synchronized void updateKeys() {
        TreeSet ts = new TreeSet();

        setKeys(ts);

        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                List list = new ArrayList();
                list.add("_portlets");
                if (dbObj.getPortletSpecVersion().equals(PortletApp.VERSION_2_0)) {
                    list.add("_filters");
                    list.add("_public_render_parameters");
                }
                setKeys(list);
                return;
            }
        }, 0);
    }

    protected Node[] createNodes(Object key) {
        if (key == null) {
            return new Node[]{};
        }
        if (key instanceof PortletType) {
            return new Node[]{new PortletNode((PortletType) key, dbObj)};
        } else if (key instanceof String && key.equals("_portlets")) {
            PortletApp portletApp = null;
            try {
                portletApp = dbObj.getPortletApp();
            } catch (IOException e) {
                logger.log(Level.SEVERE,"Error",e);
                return new Node[]{};
            }
            if (portletApp == null) {
                return new Node[]{};
            }
            List list = new ArrayList();
            PortletType[] portletType = portletApp.getPortlet();
            for (int i = 0; i < portletType.length; i++) {
                PortletNode nd = new PortletNode(portletType[i], dbObj);
                list.add(nd);
            }
            return (Node[]) list.toArray(new Node[0]);
        } else if (key instanceof String && key.equals("_filters")) {
            return new Node[]{new FilterHolderNode(dbObj)};
        } else if (key instanceof String && key.equals("_public_render_parameters")) {
            return new Node[]{new PublicRenderParametersHolderNode(dbObj)};
        } else {
            return new Node[]{};
        }
    }

    protected void addNotify() {
        updateKeys();
    }

    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }

    public void refreshPortletDOB() {
        dbObj.refreshMe();
    }
}
