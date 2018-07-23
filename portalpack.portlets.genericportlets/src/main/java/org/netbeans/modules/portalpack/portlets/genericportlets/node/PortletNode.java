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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Satyaranjan
 */
public class PortletNode extends AbstractNode{
    
    private PortletType portletType;
    private PortletXMLDataObject dobj;
    private String id;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/portletapp.gif";
    
    /** Creates a new instance of PortletNode */
    public PortletNode(PortletType portletType,PortletXMLDataObject dobj) {
        super(new PortletChildrenNode(dobj,portletType));
        this.portletType = portletType;
        this.dobj = dobj;
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        id = dobj.getApplicationName() +"." + getName();
    }

    @Override
    public String getDisplayName() {
        return portletType.getPortletName();
        
    }

    @Override
    public String getName() {
        return portletType.getPortletName();
    }
    
    
    public Transferable drag() throws IOException{
        return super.drag();
    }

    public PortletXMLDataObject getDataObject()
    {
        return dobj;
    }
    
    public String getID()
    {
        return id;
    }

    @Override
    public PasteType getDropType(final Transferable t, int action, int index) {
        try {
            final Object obj = t.getTransferData(new java.awt.datatransfer.DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd"));
           if(obj instanceof FilterNode)
            {
                if(!(((FilterNode)obj).getDataObject()).getApplicationName().equals(dobj.getApplicationName()))
                        return null;
            }
            else if(obj instanceof PublicRenderParameterNode)
            {
                if(!(((PublicRenderParameterNode)obj).getDataObject()).getApplicationName().equals(dobj.getApplicationName()))
                        return null;
            }
            else{
                return null;
            }
            
            return new PasteType() {

                public Transferable paste() throws IOException {
                    if(obj instanceof FilterNode){
                        FilterNode filter = (FilterNode)obj;
                        getDataObject().getPortletXmlHelper().addFilter(portletType.getPortletName(), filter.getName());
                        
                    }
                    else if(obj instanceof PublicRenderParameterNode){
                        PublicRenderParameterNode prNode = (PublicRenderParameterNode)obj;
                        getDataObject().getPortletXmlHelper().addSupportedPublicRenderParameter(portletType.getPortletName(),prNode.getIdentifier());                      
                    }
                    
                    return t;
                    
                }
            
            };
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
   
    @Override
    protected Sheet createSheet() {
        
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        Map<String,String> propertyMap = new HashMap();
        propertyMap.put("portlet-name", NbBundle.getMessage(PortletNode.class, "PORTLET_NAME"));
        propertyMap.put("portlet-class", NbBundle.getMessage(PortletNode.class, "PORTLET_CLASS"));
        propertyMap.put("display-name", NbBundle.getMessage(PortletNode.class, "DISPLAY-NAME"));
        propertyMap.put("expiration-cache", NbBundle.getMessage(PortletNode.class, "EXPIRATION-CACHE"));
        try{
            Property[] property = NodeHelper.getProperties(propertyMap,(BaseBean)portletType);
            for(int i=0;i<property.length;i++)
            {
                set.put (property[i]);  
            }
            sheet.put(set);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sheet;

    }
    
}
