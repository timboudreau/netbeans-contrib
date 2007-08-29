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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.DeletePublicRenderParameterAction;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/**
 * Node implementation for Supported Public Render Parameter shown under Portlet Node
 * @author Satyaranjan
 */
public class SupportedPublicRenderParameterNode extends AbstractNode{

    private PortletXMLDataObject dobj;
    private PortletType portletType;
    private String publicrenderParameterName;
    private String id;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/renderparameter.PNG";
    
    public SupportedPublicRenderParameterNode(PortletXMLDataObject dobj,PortletType portletType,String publicRenderParameter) {
        super(Children.LEAF);
        this.dobj = dobj;
        this.portletType = portletType;
        this.publicrenderParameterName = publicRenderParameter;
        id = dobj.getApplicationName()+"."+portletType.getPortletName()+":" + publicRenderParameter + "_publicrenderparameter";
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
     @Override
    public String getDisplayName() {
        return publicrenderParameterName;
        
    }

    @Override
    public String getName() {
        return publicrenderParameterName + "_supportedPublicRenderParameter";
    }
    
    public String getPortletName()
    {
        return portletType.getPortletName();
    }
    
    public PortletXMLDataObject getDataObject()
    {
        return dobj;
    }
    
    public String getSupportedPublicRenderParameterName()
    {
        return publicrenderParameterName;
    }

    private Object getID() {
        return id;
    }
    
     @Override
    public Action[] getActions(boolean context) {
        List list = new ArrayList();
        list.add(SystemAction.get(DeletePublicRenderParameterAction.class));     
        return (Action [])list.toArray(new Action[0]);
    }

}
