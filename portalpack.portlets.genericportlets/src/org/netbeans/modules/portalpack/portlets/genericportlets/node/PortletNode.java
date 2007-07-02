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
import java.io.IOException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.loaders.DataNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

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
        super(Children.LEAF);
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
    
}
