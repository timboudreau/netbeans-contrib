/*
 * IPCActionsHandler.java
 *
 * Created on May 13, 2007, 1:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.ui.AddAliasPanel;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satyaranjan
 */
public class IPCActionsHandler {

    private IPCGraphScene scene;
    private Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

    /** Creates a new instance of IPCActionsHandler */
    public IPCActionsHandler(IPCGraphScene scene) {
        this.scene = scene;
    }

    public void removeEventPinFromNode(CustomPinWidget pin) {
        String nodeKey = pin.getNodeKey();
        PortletNode node = (PortletNode) scene.getPortletNode(nodeKey);
        EventObject event = pin.getEvent();
        if (node != null) {
            try {
                if (event != null) {
                    node.getDataObject().getPortletEventingHandler().deleteProcessEvent(node.getName(), event);
                    scene.removePin(pin.getKey());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,"MSG_ERROR_EVENT_REMOVE",e);
            }
        }
    }

    public void removePublishEventPinFromNode(CustomPinWidget pin) {
        String nodeKey = pin.getNodeKey();
        PortletNode node = (PortletNode) scene.getPortletNode(nodeKey);
        EventObject event = pin.getEvent();
        if (node != null) {
            try {
                if (event != null) {
                    node.getDataObject().getPortletEventingHandler().deletePublishEvent(node.getName(), event);
                    scene.removePin(pin.getKey());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Could not remove event properly",e);
            }
        }
    }
    
    public void removePublicRenderParameterPinFromNode(CustomPinWidget pin) {
        String nodeKey = pin.getNodeKey();
        PortletNode node = (PortletNode) scene.getPortletNode(nodeKey);
        EventObject event = pin.getEvent();
        String identifier = event.getPublicRenderParamId();
        if (node != null) {
            try {
                if (event != null) {
                    node.getDataObject().getPortletXmlHelper().removeSupportedPublicRenderParameter(node.getName(), identifier);
                    scene.removePin(pin.getKey());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Could not remove event properly",e);
            }
        }
    }

    public void generatePublishEventSource(String nodeKey, EventObject evtObject) {
        PortletNode portletNode = scene.getPortletNode(nodeKey);
        try {
            portletNode.getDataObject().getPortletEventingHandler().generatePublishEventMethod(portletNode.getName(), evtObject);
        } catch (PortletEventException e) {
           logger.log(Level.SEVERE,"Could not generate source for publish event",e);
        }
    }

    public void generateProcessEventSource(String nodeKey, EventObject event) {
        PortletNode portletNode = scene.getPortletNode(nodeKey);
        try {
            portletNode.getDataObject().getPortletEventingHandler().generateProcessEventMethod(portletNode.getName(), event);
        } catch (PortletEventException e) {
            logger.log(Level.SEVERE,"Could not generate source for process event",e);
        //NotifyDescriptor.Message notifyd = new NotifyDescriptor.Message(NbBundle.getMessage(IPCActionsHandler.class,
        //        "MSG_ERROR_GENERATING_PROCESS_EVENT_CODE"), NotifyDescriptor.WARNING_MESSAGE);
        // DialogDisplayer.getDefault().notify(notifyd);
        }
    }

    public void addAliasForEvent(CustomPinWidget widget,QName... qName) {
        
        String nodeKey = widget.getNodeKey();
        PortletNode node = (PortletNode) scene.getPortletNode(nodeKey);
        EventObject event = widget.getEvent();
    
        QName aliasQName = null;
        if(qName.length == 0)
        {
             AddAliasPanel panel = new AddAliasPanel(WindowManager.getDefault().getMainWindow());        
             aliasQName = panel.getAlias();
        } else
            aliasQName = qName[0];
        
        if(aliasQName == null) return;
        if (node != null) {
            try {
                if (event != null) {
                    node.getDataObject().getPortletEventingHandler().addAlias(event, aliasQName);
                    widget.setToolTipText();
                    //perform dependency check   
                    Widget nodeWidget = scene.findWidget(nodeKey);
                    if (nodeWidget != null && nodeWidget instanceof CustomNodeWidget) {
                        scene.removeEdgesOfNode(nodeKey);
                        scene.checkAndPerformNodeDependency((CustomNodeWidget) nodeWidget);
                        scene.validate();
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Alias Could not be added",e);
            }
        }
    }
}
