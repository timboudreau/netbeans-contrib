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
package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletChildrenNode;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class PortletXMLDataNode extends DataNode {
    
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/portlet-xml.gif";
    
    public PortletXMLDataNode(PortletXMLDataObject obj) {
        super(obj, new PortletChildrenNode(obj));
        getCookieSet().add(new RefreshChannelChildren((PortletChildrenNode)getChildren()));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    PortletXMLDataNode(PortletXMLDataObject obj, Lookup lookup) {
        super(obj, new PortletChildrenNode(obj), lookup);
        
        Lookups.singleton(new RefreshChannelChildren((PortletChildrenNode)getChildren()));
       // getCookieSet().add(new RefreshChannelChildren((PortletChildrenNode)getChildren()));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
    public javax.swing.Action[] getActions(boolean context) {
            javax.swing.Action[] actions = super.getActions(context);
            javax.swing.Action[] finalActions = new javax.swing.Action[actions.length + 1];
            List actionList = new ArrayList();
            for(int i=0;i<actions.length;i++)
            {
                actionList.add(actions[i]);
            }
            
            actionList.add(SystemAction.get(RefreshPortletXMLAction.class));
            return (javax.swing.Action [])actionList.toArray(new javax.swing.Action[0]);
            
    }
    
    //    /** Creates a property sheet. */
    //    protected Sheet createSheet() {
    //        Sheet s = super.createSheet();
    //        Sheet.Set ss = s.get(Sheet.PROPERTIES);
    //        if (ss == null) {
    //            ss = Sheet.createPropertiesSet();
    //            s.put(ss);
    //        }
    //        // TODO add some relevant properties: ss.put(...)
    //        return s;
    //    }
    
}

class RefreshChannelChildren implements RefreshCookie{ //RefreshCookie {
    PortletChildrenNode children;
    
    RefreshChannelChildren(PortletChildrenNode children){
        this.children = children;
        
    }
    
    public void refresh() {
        children.refreshPortletDOB();
        children.updateKeys();
    }
}
