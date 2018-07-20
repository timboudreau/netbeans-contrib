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
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.FilterMappingDeleteAction;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/**
 * Node implementation for FilterMappingNode shown under PortletNode.
 * @author Satyaranjan
 */
public class FilterMappingNode extends AbstractNode {

    private FilterMappingType filterMappingType;
    private PortletXMLDataObject dobj;
    private PortletType portletType;
    private String id;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/filter.PNG"; //NOI18N
    /** Creates a new instance of FilterMappingNode */
    public FilterMappingNode(PortletXMLDataObject dobj,PortletType portletType,FilterMappingType filterMappingType){
        super(Children.LEAF);
        this.filterMappingType = filterMappingType;
        this.dobj = dobj;
        this.portletType = portletType;
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        id = new StringBuffer().append(dobj.getApplicationName())
                               .append(".") //NOI18N
                               .append(getName())
                               .append(":") //NOI18N
                               .append(portletType.getPortletName())
                               .append("_filtermapping").toString(); //NOI18N
    }

    @Override
    public String getDisplayName() {
        return filterMappingType.getFilterName();
        
    }

    @Override
    public String getName() {
        return filterMappingType.getFilterName() + "_mapping"; //NOI18N
    }
    
    public String getPortletName()
    {
        return portletType.getPortletName();
    }
    
    public PortletXMLDataObject getDataObject()
    {
        return dobj;
    }
    
    public String getFilterName()
    {
        return filterMappingType.getFilterName();
    }

     @Override
    public Action[] getActions(boolean context) {
        
        List list = new ArrayList();
        list.add(SystemAction.get(FilterMappingDeleteAction.class));
        return (Action [])list.toArray(new Action[0]);
    }
     
    public String getID()
    {
        return id;
    }
     
}
