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
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class PublicRenderParameterNode extends AbstractNode{
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private PublicRenderParameterType publicRenderParameterType;
    private PortletXMLDataObject dobj;
    private String id;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/renderparameter.PNG"; //NOI18N
    
    /** Creates a new instance of PublicRenderParameterNode */
    public PublicRenderParameterNode(PublicRenderParameterType publicRenderParameter,PortletXMLDataObject dobj) {
        super(Children.LEAF);
        this.publicRenderParameterType = publicRenderParameter;
        this.dobj = dobj;
        setIconBaseWithExtension(IMAGE_ICON_BASE);
        id = dobj.getApplicationName() +"." + getName()+"_renderparameters"; //NOI18N
    }

    @Override
    public String getDisplayName() {
        return publicRenderParameterType.getIdentifier();
        
    }

    @Override
    public String getName() {
        return publicRenderParameterType.getIdentifier();
    }
    
    public String getIdentifier()
    {
        return publicRenderParameterType.getIdentifier();
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
    
    public EventObject getCoordinationObject()
    {
            EventObject evt = new EventObject();
            evt.setPublicRenderParamId(publicRenderParameterType.getIdentifier());
            evt.setType(EventObject.PUBLIC_RENDER_PARAMETER_TYPE);
            if(publicRenderParameterType.getQname() != null)
                evt.setQName(publicRenderParameterType.getQname());
            else
                evt.setName(publicRenderParameterType.getName());
            try {

                evt.setDefaultNameSpace(dobj.getPortletApp().getPortletDefaultNamespace());
            } catch (IOException ex) {
                logger.info(ex.getMessage());
                //don't do anything
            }
            evt.setAlias(publicRenderParameterType.getAlias());
            return evt;
    }

    @Override
    protected Sheet createSheet() {
      
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        Map<String,String> propertyMap = new HashMap();
        propertyMap.put("identifier", NbBundle.getMessage(PublicRenderParameterNode.class, "IDENTIFIER"));
        propertyMap.put("qname", NbBundle.getMessage(FilterNode.class, "QNAME"));
        propertyMap.put("name", NbBundle.getMessage(FilterNode.class, "NAME"));
       
        try{
            Property[] property = NodeHelper.getProperties(propertyMap,(BaseBean)publicRenderParameterType);
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
