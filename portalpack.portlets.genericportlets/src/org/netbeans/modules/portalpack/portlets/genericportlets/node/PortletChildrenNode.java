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
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

import org.openide.util.RequestProcessor;

/**
 *
 * @author Satyaranjan
 */
public class PortletChildrenNode extends Children.Keys{
    
    private PortletXMLDataObject dbObj;
    private PortletApp portletApp;
    /** Creates a new instance of PortletChildrenNode */
    public PortletChildrenNode(PortletXMLDataObject dbObj) {
        try {
            this.dbObj = dbObj;
            this.portletApp = dbObj.getPortletApp();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized void updateKeys(){
        TreeSet ts = new TreeSet();
        
        //ts.add(WAIT_NODE);
        
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                List list =new  ArrayList();
                if(portletApp == null)
                    return;
                PortletType[] portletType = portletApp.getPortlet();
                for(int i=0;i<portletType.length;i++)
                    list.add(portletType[i]);
                setKeys(list);
                return;
                
            }}, 0);
            
    }
    protected Node[] createNodes(Object key) {
        if(key == null) return new Node[]{};
        
        if(key instanceof PortletType)
            return new Node[]{new PortletNode((PortletType)key,dbObj)};
        else    
            return new Node[]{};
    }
    
    protected void addNotify() {
        updateKeys();
    }
    
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }
    
    public void refreshPortletDOB()
    {
        dbObj.refreshMe();
    }
    
}
