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

import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;


/**
 * Node implementation for filters under Portlet Xml Node
 * @author Satyaranjan
 */
public class FilterNode extends AbstractNode {
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private FilterType filterType;
    private PortletXMLDataObject dobj;
    private String id;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/filter.PNG"; //NOI18N
    /** Creates a new instance of FilterNode */
    public FilterNode(FilterType filterType,PortletXMLDataObject dobj) {
        super(Children.LEAF);
        this.filterType = filterType;
        this.dobj = dobj;
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        id = dobj.getApplicationName() +"." + getName();
    }

    @Override
    public String getDisplayName() {
        return filterType.getFilterName();
        
    }

    @Override
    public String getName() {
        return filterType.getFilterName();
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
    protected Sheet createSheet() {
        
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        Map<String,String> propertyMap = new HashMap();
        propertyMap.put("filter-name", NbBundle.getMessage(FilterNode.class, "FILTER_NAME"));
        propertyMap.put("filter-class", NbBundle.getMessage(FilterNode.class, "FILTER_CLASS"));
        propertyMap.put("lifecycle", NbBundle.getMessage(FilterNode.class, "LIFE_CYCLE"));
        try{
            Property[] property = NodeHelper.getProperties(propertyMap,(BaseBean)filterType);
            for(int i=0;i<property.length;i++)
            {
                set.put (property[i]);  
            }
            sheet.put(set);
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error in createSheet",e);
        }
        return sheet;

    }
    
}
