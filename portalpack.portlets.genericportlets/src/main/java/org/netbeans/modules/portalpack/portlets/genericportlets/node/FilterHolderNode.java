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

import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;

/**
 * Node implementation for filter fold node which holds all filter nodes under portlet xml
 * node.
 * @author Satyaranjan
 */
public class FilterHolderNode extends AbstractNode{

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private PortletXMLDataObject dobj;
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/portalpack/portlets/genericportlets/resources/folder.gif";
    /** Creates a new instance of FilterHolderNode */
    public FilterHolderNode(PortletXMLDataObject dobj) {
        super(new FilterChildrenNode(dobj));
       
        this.dobj = dobj;
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(FilterHolderNode.class,"LBL_FILTERS");
        
    }

    @Override
    public String getName() {
        return "Filters"; //NOI18N
    }

    public PortletXMLDataObject getDataObject()
    {
        return dobj;
    }
}
