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
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Satyaranjan
 */
public class PortletChildrenNode extends Children.Keys {

   private PortletXMLDataObject dbObj;
   private PortletType portletType;
   private PortletApp portletApp;
  
   public PortletChildrenNode(PortletXMLDataObject dbObj,PortletType portletType) {
       this.portletType = portletType;
       
        try {
            this.dbObj = dbObj;
            this.portletApp = dbObj.getPortletApp();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized void updateKeys(){
        TreeSet ts = new TreeSet();
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                List list =new  ArrayList();
                if(portletType == null)
                    return;
                if(dbObj.getPortletSpecVersion().equals(PortletApp.VERSION_1_0))
                    return;
                FilterMappingType[] filterMappingType = portletApp.getFilterMapping();
                for(int i=0;i<filterMappingType.length;i++)
                {
                    String[] portlets = filterMappingType[i].getPortletName();
                    boolean isAssigned = false;
                    for(String portletName:portlets)
                    {
                        if(portletName.equals(portletType.getPortletName())
                                || portletName.equals("*"))
                        {
                            isAssigned = true;
                            break;
                        }
                    }
                    if(isAssigned)
                       list.add(filterMappingType[i]);
                }
              
                list.add("supported-public-render-parameters");
                
                setKeys(list);
                return;
                
            }}, 0);
            
    }
    protected Node[] createNodes(Object key) {
        if(key == null) return new Node[]{};
        
        if(key instanceof FilterMappingType)
            return new Node[]{new FilterMappingNode(dbObj,portletType,(FilterMappingType)key)};
       
        else if(key.equals("supported-public-render-parameters"))
        {
               String[] values = portletType.getSupportedPublicRenderParameter();
                if(values == null || values.length == 0)
                    return new Node[]{};
                Node[] nds = new Node[values.length];
                for(int i=0;i<values.length;i++)
                {
                    nds[i] = new SupportedPublicRenderParameterNode(dbObj,portletType,values[i]);
                    
                }
                return nds;
         
        }
        else    
            return new Node[]{};
    }
    
    protected void addNotify() {
        updateKeys();
    }
    
    @Override
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }
    
    public void refreshPortletDOB()
    {
        dbObj.refreshMe();
    }


}
