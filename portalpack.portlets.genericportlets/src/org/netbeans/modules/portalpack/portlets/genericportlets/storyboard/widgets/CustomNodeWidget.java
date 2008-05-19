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
package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PublicRenderParameterNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.IPCGraphScene;

/**
 *
 * @author Satyaranjan
 */
public class CustomNodeWidget extends VMDNodeWidget {
    
    private String nodeKey;
    private String portletName;
    /** Creates a new instance of CustomNodeWidget */
   public CustomNodeWidget(Scene scene)
   {
       super(scene);
       createAcceptAction();
   }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getPortletName() {
        return portletName;
    }

    public void setPortletName(String portletName) {
        this.portletName = portletName;
    }
    
    private void createAcceptAction()
    {
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {

          public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                Object obj = null;
                try {

                    obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd")); //NOI18N
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (obj == null) {
                    return ConnectorState.REJECT;
                } else if (obj instanceof PublicRenderParameterNode) {
                    return ConnectorState.ACCEPT;
                }
                return ConnectorState.REJECT;
            }

            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    Object obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd")); //NOI18N
                    if(obj instanceof PublicRenderParameterNode){
                        PublicRenderParameterNode node = (PublicRenderParameterNode) obj;
                        addPublicRenderParameterPin(node);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
        
    }
    
    private void addPublicRenderParameterPin(PublicRenderParameterNode node)
    {
        EventObject evt = node.getCoordinationObject();
        IPCGraphScene scene = (IPCGraphScene)this.getScene();
        scene.addPublicRenderParameter(nodeKey, evt);
    }
}
