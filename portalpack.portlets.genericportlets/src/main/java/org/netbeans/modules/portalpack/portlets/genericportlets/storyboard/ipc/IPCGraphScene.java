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
package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.ConsumeEventPinMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.util.WidgetUtil;
import org.openide.util.Utilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.AcceptProvider;
import java.awt.datatransfer.DataFlavor;
import java.util.Hashtable;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.WidgetAction.State;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.vmd.VMDGlyphSetWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.impl.PortletXmlEventingHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.PortletXmlHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PublicRenderParameterNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.ui.AddEventPanel;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.ui.AddRenderParameterPanel;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EdgePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EventPinPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.IPCPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.NodePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.PublicRenderParamPinPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satyaranjan
 */
public class IPCGraphScene extends CustomVMDGraphScene {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final Image IMAGE_LIST = Utilities.loadImage("de/eppleton/visualexplorer/resources/list_16.png"); // NOI18N
    private static final Image IMAGE_PORTLET = Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/portletapp.gif"); // NOI18N
    private static final Image IMAGE_PUBLISH_EVENT = Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/storyboard/resources/publishes.png"); // NOI18N
    private static final Image IMAGE_PROCESS_EVENT = Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/storyboard/resources/consumes.png"); // NOI18N
    private static final Image IMAGE_RENDER_PARAM = Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/renderparameter.PNG"); // NOI18N)
    private static Paint PAINT_BACKGROUND;
    

    static {
        Image sourceImage = Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/paper_grid.png"); // NOI18N
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.dispose();
        PAINT_BACKGROUND = new TexturePaint(image, new Rectangle(0, 0, width, height));
    }
    private static int edgeID = 1;
    private static Hashtable nodeMap = new Hashtable();
    private static Hashtable edgeMap = new Hashtable();
    private WidgetAction popupMenuAction;
    // private WidgetAction editorAction = ActionFactory.createInplaceEditorAction(new EventNameTextFieldEditor(this));
    private WidgetAction eventingPopUpMenuProvider;
    private WidgetAction consumeEventPopUpMenuProvider;
    private WidgetAction publicRenderParameterPopUpMenuProvider;
    private WidgetAction connectAction;
    private IPCStoryBoardTopComponent ipcTop;
    private IPCActionsHandler actionsHandler;

    /** Creates a new instance of IPCGraphScene */
    public IPCGraphScene(IPCStoryBoardTopComponent ipcTop) {
        super();
        this.ipcTop = ipcTop;
        this.actionsHandler = new IPCActionsHandler(this);

        connectAction = ActionFactory.createConnectAction(connectionLayer, new SceneConnectProvider(this));
        popupMenuAction = ActionFactory.createPopupMenuAction(new IPCPopUpMenuProvider(this));
        eventingPopUpMenuProvider = ActionFactory.createPopupMenuAction(new EventPinPopUpMenuProvider(this));
        consumeEventPopUpMenuProvider = ActionFactory.createPopupMenuAction(new ConsumeEventPinMenuProvider(this));
        publicRenderParameterPopUpMenuProvider = ActionFactory.createPopupMenuAction(new PublicRenderParamPinPopUpMenuProvider(this));
        setBorder(BorderFactory.createBevelBorder(true));
        setBackground(PAINT_BACKGROUND);
        getActions().addAction(popupMenuAction);
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
                } else if (obj instanceof PortletNode) {
                    return ConnectorState.ACCEPT;
                } else if (obj instanceof PublicRenderParameterNode) {
                    return ConnectorState.ACCEPT;
                }
                return ConnectorState.REJECT;
            }

            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    Object obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node", "application/x-java-openide-nodednd")); //NOI18N
                    if(obj instanceof PortletNode)
                    {
                         PortletNode node = (PortletNode) obj;
                         addPortletNode(node, point);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public IPCActionsHandler getTaskHandler() {
        return actionsHandler;
    }

    public void addPortletNode(PortletNode node, Point point) {

        String name = node.getName();
        String key = node.getID();
        if (checkIfNodePresent(key)) {
            return;
        }
        //check if eventing is supported for this portlet
        if (!node.getDataObject().getPortletEventingHandler().isEventingSupported()) {
            return;
        }
        List glyphs = new ArrayList();
        glyphs.add(IMAGE_PORTLET);
        CustomNodeWidget mobileWidget = (CustomNodeWidget) WidgetUtil.createNode(this, point.x, point.y, IMAGE_LIST, key, name, "List", glyphs); //NOI18N
        mobileWidget.getActions().addAction(connectAction);
        mobileWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new NodePopUpMenuProvider(this, key)));
        String nodeID = key;
        PortletXMLDataObject dobj = node.getDataObject();
        EventObject[] events = dobj.getPortletEventingHandler().getPublishEvents(name);
        if (events != null) {
            for (int i = 0; i < events.length; i++) {
                addEventPinToNode(nodeID, events[i]);
            }
        }

        EventObject[] processEvents = dobj.getPortletEventingHandler().getProcessEvents(name);
        if (processEvents != null) {
            for (int i = 0; i < processEvents.length; i++) {
                addProcessEventPinToNode(nodeID, processEvents[i]);
            }
        }

        nodeMap.put(nodeID, node);
        checkAndPerformNodeDependency((CustomNodeWidget) mobileWidget);
        
        addPublicRenderParameters(node);
        validate();
    }
    
    private void addPublicRenderParameters(PortletNode node)
    {
        String name = node.getName();
        List<EventObject> list = node.getDataObject().getPortletXmlHelper().getSupportedPublicRenderParameters(name);
        
        for(EventObject evt:list)
        {
            addPublicRenderParameterPinToNode(node.getID(), evt);
        }
    }
    public boolean checkIfNodePresent(String nodeID) {
        if (nodeMap.get(nodeID) != null) {
            return true;
        } else {
            return false;
        }
    }

    CustomPinWidget addEventPinToNode(String nodeID, EventObject event) {
        String eventName = null;
        if (event.isQName()) {
            eventName = event.getQName().toString();
        } else {
            eventName = event.getName();
        
        }
        VMDPinWidget pin1 = WidgetUtil.createPin(this, nodeID, nodeID + "_" + eventName, IMAGE_PUBLISH_EVENT, eventName, "Element"); //NOI18N
        ((CustomPinWidget) pin1).setEventName(eventName);
        ((CustomPinWidget) pin1).setEvent(event);
        ((CustomPinWidget) pin1).setToolTipText();
        pin1.getActions().addAction(connectAction);
        
        pin1.getActions().addAction(eventingPopUpMenuProvider);
        return (CustomPinWidget) pin1;
    }

    CustomPinWidget addProcessEventPinToNode(String nodeID, EventObject event) {
        String eventName = null;
        if (event.isQName()) {
            eventName = event.getQName().toString();
        } else {
            eventName = event.getName();
        }
        VMDPinWidget consumePin = WidgetUtil.createPin(this, nodeID, nodeID + "_" + "consume_" + eventName, IMAGE_PROCESS_EVENT, "consume_" + eventName, "Element"); //NOI18N
        ((CustomPinWidget) consumePin).setEventName(eventName);
        ((CustomPinWidget) consumePin).setEvent(event);
        ((CustomPinWidget) consumePin).setToolTipText();
        ((CustomPinWidget) consumePin).getActions().addAction(consumeEventPopUpMenuProvider);
        ((CustomPinWidget) consumePin).setType(CustomPinWidget.PROCESS_EVENT_TYPE);
        return (CustomPinWidget) consumePin;
    }
    
    public CustomPinWidget addPublicRenderParameterPinToNode(String nodeID, EventObject event) {
        String eventName = null;
        if (event.isQName()) {
            eventName = event.getQName().toString();
        } else {
            eventName = event.getName();
        }
        eventName = event.getPublicRenderParamId() + " [" + eventName + "]";
        VMDPinWidget pin1 = WidgetUtil.createPin(this, nodeID, getRenderParameterPinKey(nodeID, event), IMAGE_RENDER_PARAM, eventName, "Element"); //NOI18N
        ((CustomPinWidget) pin1).setEventName(eventName);
        ((CustomPinWidget) pin1).setEvent(event);
        ((CustomPinWidget) pin1).setToolTipText();
        ((CustomPinWidget) pin1).setType(CustomPinWidget.RENDERPARAM_TYPE);
        pin1.getActions().addAction(connectAction);
        
        pin1.getActions().addAction(publicRenderParameterPopUpMenuProvider);
        checkAndPerformNodeDependecyForRenderParameter((CustomPinWidget)pin1);
        validate();
        return (CustomPinWidget) pin1;
    }
    
    private void checkAndPerformNodeDependecyForRenderParameter(CustomPinWidget rpWidget)
    {
        String orgNodeKey = rpWidget.getNodeKey(); //getNodeName();
        PortletNode orgPortletNode = (PortletNode) nodeMap.get(orgNodeKey);
        //String orgNodename = orgPortletNode.getName();
        EventObject prpObject = rpWidget.getEvent();
        Set s = nodeMap.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String ndKey = (String) it.next();
            
            if(orgNodeKey.equals(ndKey))
                continue;
            PortletNode portletNode = (PortletNode) nodeMap.get(ndKey);
            
            if (portletNode == null) {
                continue;
            }
            List<EventObject> prpList = portletNode.getDataObject().getPortletXmlHelper().getSupportedPublicRenderParameters(portletNode.getName());
            EventObject[] prps = (EventObject [])prpList.toArray(new EventObject[0]);
            EventObject[] equalPrps = hasEvent(prpObject,prps);
            
            if(equalPrps != null)
            {
                for(EventObject prp:equalPrps)
                {
                    //String prpId = getEventName(prp);
                    Object ob = findWidget(getRenderParameterPinKey(ndKey, prp));
                    if(ob != null || ob instanceof CustomPinWidget){
                         CustomPinWidget targetPin = (CustomPinWidget)ob;
                         connectBothRenderParametersPins(rpWidget,targetPin);
                    }
                }
            }
        }
    }
    
    private void connectBothRenderParametersPins(CustomPinWidget sourcePin,CustomPinWidget targetPin)
    {
        String edge = IPCStoryBoardConstants.RENDER_PARAM_NODE_PREFIX +"edge" + edgeID++; //NOI18N
        Widget edgeWidget = addEdge(edge);
       // edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new EdgePopUpMenuProvider(edge, this, consumePin, sourceWidget)));
        setEdgeSource(edge, sourcePin.getKey());
        setEdgeTarget(edge, targetPin.getKey());
        edgeMap.put(edge, new Object());
    }

    public void deletePortletNodeFromScene(String nodeID, boolean removeRef) {
        Object obj = nodeMap.get(nodeID);
        if (obj != null) {
            this.removeNodeWithEdges(nodeID);
            if (removeRef) {
                nodeMap.remove(nodeID);
            }
        }
    }

    public void addEvent(final String nodeKey) {
        
        final PortletNode node = getPortletNode(nodeKey);
        if (node == null) {
            return;
        }
        
        try {
        
            AddEventPanel panel = new AddEventPanel(WindowManager.getDefault().getMainWindow(),
                    NbBundle.getMessage(IPCGraphScene.class, "TL_ADD_PUBLISH_EVENT"));
   
            EventObject evtQName = panel.getEvent();

            if (evtQName == null) {
                return;
            }
            addEventPinToNode(nodeKey, evtQName);
            try {
                if (!node.getDataObject().getPortletEventingHandler().addPublishEvent(node.getName(), evtQName, null)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            //check dependency..
            Widget nodeWidget = findWidget(nodeKey);
            if (nodeWidget != null && nodeWidget instanceof CustomNodeWidget) {
                removeEdgesOfNode(nodeKey);
                checkAndPerformNodeDependency((CustomNodeWidget) nodeWidget);
            }

            validate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding new event.", e);
        }
    }

    //This method is called from the "Add Process Event" menu
    public void addNewProcessEvent(final String nodeKey) {
        final PortletNode node = getPortletNode(nodeKey);
        if (node == null) {
            return;
        }
        
        try {
            AddEventPanel panel = new AddEventPanel(WindowManager.getDefault().getMainWindow(),
                    NbBundle.getMessage(IPCGraphScene.class, "TL_ADD_PROCESS_EVENT"));
            EventObject evtQName = panel.getEvent();

            if (evtQName == null) {
                return;
            }
            try {
                if (!node.getDataObject().getPortletEventingHandler().addProcessEvent(node.getName(), evtQName, null)) {
                    return;
                }
                addProcessEventPinToNode(nodeKey, evtQName);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            //check dependency..
            Widget nodeWidget = findWidget(nodeKey);
            if (nodeWidget != null && nodeWidget instanceof CustomNodeWidget) {
                removeEdgesOfNode(nodeKey);
                checkAndPerformNodeDependency((CustomNodeWidget) nodeWidget);
            }

            validate();
        
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding new event.", e); //NOI18N
        }

    }
    
    public CustomPinWidget addPublicRenderParameter(String nodeKey,EventObject evt)
    {
        final PortletNode node = getPortletNode(nodeKey);
        if (node == null) {
            return null;
        }
        EventObject newEvent = null;
        if(evt == null){
            AddRenderParameterPanel panel = new AddRenderParameterPanel(WindowManager.getDefault().getMainWindow(),node.getDataObject(),node.getName());
            newEvent = panel.getCoordinationObject();
        } else {
            if((newEvent = node.getDataObject().getPortletXmlHelper().addSupportedPublicRenderParameter(node.getName(),evt)) == null) {
                return null;
            }
        }
        
        if(newEvent == null) return null;
        //node.getDataObject().getPortletXmlHelper().addSupportedPublicRenderParameter(node.getName(), evt.getPublicRenderParamId());
        
        return addPublicRenderParameterPinToNode(nodeKey, newEvent);
    }
    void checkAndPerformNodeDependency(CustomNodeWidget nodeWidget) {
        String orgNodeKey = nodeWidget.getNodeKey(); //getNodeName();
        PortletNode orgPortletNode = (PortletNode) nodeMap.get(orgNodeKey);
        String orgNodename = orgPortletNode.getName();
        EventObject[] consumeEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getProcessEvents(orgNodename);
        EventObject[] sourceEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getPublishEvents(orgNodename);
        Set s = nodeMap.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String ndKey = (String) it.next();
            PortletNode portletNode = (PortletNode) nodeMap.get(ndKey);
            if (portletNode == null) {
                continue;
            }
            EventObject[] evts = portletNode.getDataObject().getPortletEventingHandler().getPublishEvents(portletNode.getName());

            for (int i = 0; i < consumeEvts.length; i++) {
    
                EventObject[] consumeEvent = hasEvent(consumeEvts[i], evts);
                if (consumeEvent != null) {
                    for (int k = 0; k < consumeEvent.length; k++) {
                        String consumeEventName = getEventName(consumeEvent[k]);
                        Object ob = findWidget(ndKey + "_" + consumeEventName);
                      
                        if (ob != null && ob instanceof CustomPinWidget) {
                            CustomPinWidget pin = (CustomPinWidget) ob;
                            if (pin == null) {
                                continue;
                            }

                            connectBothPortletNodes(pin, nodeWidget, consumeEvts[i]);
                        }
                    }
                }
            }

            EventObject[] targetConsumeEvts = portletNode.getDataObject().getPortletEventingHandler().getProcessEvents(portletNode.getName());
            for (int i = 0; i < sourceEvts.length; i++) {
                //ignore circular dependency
                if (orgNodeKey.equals(ndKey))// {
                {
                    continue;

                }
                EventObject[] consumeEvent = hasEvent(sourceEvts[i], targetConsumeEvts);
                if (consumeEvent != null) {
                    for (int k = 0; k < consumeEvent.length; k++) {
                        String sourceEventName = getEventName(sourceEvts[i]);
                        Object ob = findWidget(orgNodeKey + "_" + sourceEventName);
                        if (ob instanceof CustomPinWidget) {
                            CustomPinWidget pin = (CustomPinWidget) ob;
                            if (pin == null) {
                                continue;
                            }
                            VMDNodeWidget targetNodeWidget = (VMDNodeWidget) findWidget(ndKey);
                            if (targetNodeWidget != null) {
                                connectBothPortletNodes(pin, (CustomNodeWidget) targetNodeWidget, consumeEvent[k]);
                            }
                        }
                    }
                }

            }
        }
    }

    private String getEventName(EventObject evt) {
        String evtName = null;
        if (evt.isQName()) {
            evtName = evt.getQName().toString();
        } else {
            evtName = evt.getName();
        }
        return evtName;
    }

    private void connectBothPortletNodes(CustomPinWidget sourceWidget, CustomNodeWidget targetNode, EventObject consumeEvent) {
        EventObject event = ((CustomPinWidget) sourceWidget).getEvent();
        String eventName = getEventName(event);

        String consumeEventName = getEventName(consumeEvent);
        CustomPinWidget consumePin = (CustomPinWidget) findWidget(targetNode.getNodeKey() + "_" + "consume_" + consumeEventName); //NOI18N

        //a create a cosumer pin
        if (consumePin == null) {
            consumePin = (CustomPinWidget) WidgetUtil.createPin(this, targetNode.getNodeKey(), targetNode.getNodeKey() + "_" + "consume_" + consumeEventName, IMAGE_PROCESS_EVENT, "consume_" + consumeEventName, "Element"); //NOI18N
            consumePin.setEventName(eventName);
            consumePin.setEvent(consumeEvent);
            consumePin.setToolTipText();
            consumePin.getActions().addAction(consumeEventPopUpMenuProvider);
            consumePin.setType(CustomPinWidget.PROCESS_EVENT_TYPE);
        }
        String edge = "edge" + edgeID++; //NOI18N
        Widget edgeWidget = addEdge(edge);
        edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new EdgePopUpMenuProvider(edge, this, consumePin, sourceWidget)));
        setEdgeSource(edge, ((CustomPinWidget) sourceWidget).getKey());
        setEdgeTarget(edge, consumePin.getKey());
        edgeMap.put(edge, new Object());
    }

    private EventObject[] hasEvent(EventObject evt, EventObject[] evts) {

        List eventList = new ArrayList();
        for (int i = 0; i < evts.length; i++) {
            if (PortletXmlEventingHelper.checkEventsNameForEqual(evt, evts[i], true))
            {
                eventList.add(evts[i]);
            }
        }
        if (eventList.size() == 0) {
            return null;
        }
        return (EventObject[]) eventList.toArray(new EventObject[0]);
    }

    public void resetScene() {
        Set keys = nodeMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String nodeId = (String) it.next();
            deletePortletNodeFromScene(nodeId, false);
        }
        
        nodeMap.clear();

        nodeMap.clear();
        edgeMap.clear();
        this.removeChildren();
        revalidate(true);
        ipcTop.reset();
    }

//inner class started
    private class SceneCreateAction extends WidgetAction.Adapter {

        public State mousePressed(Widget widget, WidgetMouseEvent event) {
            if (event.getClickCount() == 1) {
                if (event.getButton() == MouseEvent.BUTTON1 || event.getButton() == MouseEvent.BUTTON2) {

                    //addNode ("node" + nodeCounter ++).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));
                    return State.CONSUMED;
                }
            }
            return State.REJECTED;
        }
    }

    private class SceneConnectProvider implements ConnectProvider {

        private Widget source = null;
        private CustomNodeWidget targetNode = null;
        private CustomPinWidget targetPinWidget = null;
        private IPCGraphScene scene;

        public SceneConnectProvider(IPCGraphScene scene) {
            this.scene = scene;
        }

        public boolean isSourceWidget(Widget sourceWidget) {


            if (sourceWidget instanceof VMDPinWidget) {
                
                source = (VMDPinWidget) sourceWidget;
                return true;
            } else {
                return false;
            }
        }

        public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {

            if (targetWidget != null && targetWidget instanceof CustomNodeWidget) {
                targetNode = (CustomNodeWidget) targetWidget;
                return ConnectorState.ACCEPT;
            } else {
                Widget widget = targetWidget.getParentWidget();
                if (widget instanceof CustomNodeWidget) {

                    targetNode = (CustomNodeWidget) widget;
                    return ConnectorState.ACCEPT;
                } else if (widget instanceof VMDGlyphSetWidget) {
                    Widget parent = widget.getParentWidget();
                    if(parent == null) 
                        return ConnectorState.REJECT_AND_STOP;
                    
                    if(!(parent instanceof CustomPinWidget))
                        return ConnectorState.REJECT_AND_STOP;
                    
                    if (((CustomPinWidget) parent).getType() == CustomPinWidget.PUBLISH_EVENT_TYPE) {
                        return ConnectorState.REJECT_AND_STOP;
                    } else if(((CustomPinWidget) parent).getType() == CustomPinWidget.PROCESS_EVENT_TYPE){
                        targetPinWidget = (CustomPinWidget) parent;
                        return ConnectorState.ACCEPT;
                    } else {
                        return ConnectorState.REJECT_AND_STOP;
                    }
                
                } else {
                    return ConnectorState.REJECT_AND_STOP;
                }
            }
        }

        public boolean hasCustomTargetWidgetResolver(Scene scene) {
            return false;
        }

        public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {

            return null;
        }

        public void createConnection(Widget sourceWidget, Widget targetWidget) {


            if (source == null || targetNode == null) {
                return;
            }
            if (sourceWidget == null || targetWidget == null) {
                return;
            }

            if (targetPinWidget != null) {
                doAlias(sourceWidget, targetPinWidget);
                targetPinWidget = null;
                return;
            }
            EventObject event = ((CustomPinWidget) sourceWidget).getEvent();
            
            if(((CustomPinWidget)sourceWidget).getType() == CustomPinWidget.RENDERPARAM_TYPE)
            {
                createConnectionForRenderParameter((CustomPinWidget)sourceWidget,targetNode);
                return;
            }
            String eventName = getEventName(event);
            CustomPinWidget consumePin = (CustomPinWidget) findWidget(targetNode.getNodeKey() + "_" + "consume_" + eventName); //NOI18N

            //a create a cosumer pin
            if (consumePin == null) {
                consumePin = (CustomPinWidget) WidgetUtil.createPin(scene, targetNode.getNodeKey(), targetNode.getNodeKey() + "_" + "consume_" + eventName, IMAGE_PROCESS_EVENT, "consume_" + eventName, "Element"); //NOI18N
                consumePin.setEventName(eventName);
                consumePin.setEvent(event);
                consumePin.setToolTipText();
                consumePin.getActions().addAction(consumeEventPopUpMenuProvider);
                consumePin.setType(CustomPinWidget.PROCESS_EVENT_TYPE);
            }
            String edge = "edge" + edgeID++; //NOI18N
            Widget edgeWidget = addEdge(edge);
            edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new EdgePopUpMenuProvider(edge, scene, consumePin, sourceWidget)));
            setEdgeSource(edge, ((CustomPinWidget) sourceWidget).getKey());
            setEdgeTarget(edge, consumePin.getKey());
            edgeMap.put(edge, new Object());

            PortletNode targetPortletNode = (PortletNode) nodeMap.get(targetNode.getNodeKey());
            if (targetPortletNode != null) {
                try {
                    targetPortletNode.getDataObject().getPortletEventingHandler().addProcessEvent(targetPortletNode.getName(), event, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //added code to put the data in map
            Widget parentSourceWidget = sourceWidget.getParentWidget();
            if (parentSourceWidget instanceof VMDNodeWidget) {
                
            }
            parentSourceWidget = targetWidget.getParentWidget();
            if (parentSourceWidget instanceof VMDNodeWidget) {
               
            }
        }
    }

    private void doAlias(Widget sourceWidget, CustomPinWidget targetWidget) {
        if (!(sourceWidget instanceof CustomPinWidget)) {
            return;
        }
        EventObject srcEvt = ((CustomPinWidget) sourceWidget).getEvent();
        EventObject targetEvt = targetWidget.getEvent();
        
        PortletNode targetPortletNode = getPortletNode(targetWidget.getNodeKey());
        if (targetPortletNode != null) {
            try {
                
                if(PortletXmlEventingHelper.checkEventsNameForEqual(srcEvt, targetEvt, true))
                    return;
                Object[] params = new Object[2];
                params[0] = srcEvt.isQName()? srcEvt.getQName() : srcEvt.getName();
                params[1] = targetEvt.isQName() ? targetEvt.getQName() : targetEvt.getName();
                
                NotifyDescriptor.Confirmation nd =
                        new NotifyDescriptor.Confirmation(NbBundle.getMessage(IPCGraphScene.class,
                        "MSG_WANT_TO_ADD_ALIAS",params),NotifyDescriptor.YES_NO_OPTION);
                
                Object retVal = DialogDisplayer.getDefault().notify(nd);
                if(retVal == NotifyDescriptor.NO_OPTION)
                    return;
                
                QName qName = srcEvt.isQName() ? srcEvt.getQName() : new QName(srcEvt.getName());
                actionsHandler.addAliasForEvent(targetWidget,qName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    
    private void createConnectionForRenderParameter(CustomPinWidget sourceWidget,CustomNodeWidget targetNode){
        if(sourceWidget.getType() != CustomPinWidget.RENDERPARAM_TYPE)
            return;
        EventObject prp = sourceWidget.getEvent();
        String pinKey = getRenderParameterPinKey(targetNode.getNodeKey(), prp);
        Object ob = findWidget(pinKey);
        CustomPinWidget targetPin = null;
        if(ob != null || ob instanceof CustomPinWidget){
            targetPin = (CustomPinWidget)ob;
            connectBothRenderParametersPins(sourceWidget,targetPin);
        }
        
        if(ob == null) {
            
            targetPin = addPublicRenderParameter(targetNode.getNodeKey(), prp);
            
        }
        
    }
    
    private String getRenderParameterPinKey(String ndKey,EventObject evt)
    {
        String id = getEventName(evt);
        return ndKey + "_" + IPCStoryBoardConstants.RENDER_PARAM_NODE_PREFIX + id;
    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        String originalNode;
        String replacementNode;

        public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource(edge) : null;
            return originalNode != null;
        }

        public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget(edge) : null;
            return originalNode != null;
        }

        public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            Object object = findObject(replacementWidget);
            replacementNode = isNode(object) ? (String) object : null;
            if (replacementNode != null) {
                return ConnectorState.ACCEPT;
            }
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomReplacementWidgetResolver(Scene scene) {
            return false;
        }

        public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
            return null;
        }

        public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null) {
                removeEdge(edge);
            } else if (reconnectingSource) {
                setEdgeSource(edge, replacementNode);
            } else {
                setEdgeTarget(edge, replacementNode);
            }
        }
    }

    public PortletNode getPortletNode(String nodeKey) {
        return (PortletNode) nodeMap.get(nodeKey);
    }

    public void removeEdgesOfNode(String nodeKey) {
        Collection pins = getNodePins(nodeKey);
        Iterator pinsIt = pins.iterator();
        while (pinsIt.hasNext()) {
            String pin = (String) pinsIt.next();
            Collection edges = findPinEdges(pin, true, true); //((CustomPinWidget)widget).getKey(),true,true);
            Iterator it = edges.iterator();
            while (it.hasNext()) {
                removeEdge((String) it.next());
            }
        }
    }
}

class EventNameTextFieldEditor implements TextFieldInplaceEditor {

    private final IPCGraphScene scene;

    public EventNameTextFieldEditor(IPCGraphScene scene) {
        this.scene = scene;
    }

    public boolean isEnabled(Widget widget) {
        return true;
    }

    public String getText(Widget widget) {

        return ((CustomPinWidget) widget).getPinName();
    }

    public void setText(final Widget widget, String text) {
        if (!CoreUtil.validateString(text, false)) {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Invalid Event Name", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        final String nodeKey = ((CustomPinWidget) widget).getNodeKey();
        PortletNode pNode = scene.getPortletNode(nodeKey);

        if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you sure to rename the Event ? ", "Rename", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }
        //TODO QName[] existingPublishEvents = pNode.getDataObject().getPortletEventingHandler().getPublishEvents(pNode.getName());
        //TODO       if(WidgetUtil.hasString(text,existingPublishEvents))
        if (1 != 0) {

            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "An Event with same name already exists.", "Rename", JOptionPane.ERROR_MESSAGE);

            return;
        }

        String oldEventName = ((CustomPinWidget) widget).getEventName();
        ((CustomPinWidget) widget).setPinName(text);
        ((CustomPinWidget) widget).setEventName(text);



        //      try{
////TODO          pNode.getDataObject().getPortletEventingHandler().renamePublishEvent(pNode.getName() , oldEventName,text,null);
        //     }catch(PortletEventException e){
        //        e.printStackTrace();
        //   }
        //remove edges
        Collection pins = scene.getNodePins(nodeKey);
        Iterator pinsIt = pins.iterator();
        while (pinsIt.hasNext()) {
            String pin = (String) pinsIt.next();
            Collection edges = scene.findPinEdges(pin, true, true); //((CustomPinWidget)widget).getKey(),true,true);
            Iterator it = edges.iterator();
            while (it.hasNext()) {
                scene.removeEdge((String) it.next());
            }
        }
        scene.removePin(((CustomPinWidget) widget).getKey());
//TODO        scene.addEventPinToNode(nodeKey,text);
        Widget nodeWidget = scene.findWidget(nodeKey);
        Object ob = scene.findWidget(nodeKey + "_" + text);
        if (nodeWidget != null && nodeWidget instanceof CustomNodeWidget) {
            scene.checkAndPerformNodeDependency((CustomNodeWidget) nodeWidget);
        }
        scene.revalidate();
    }
}
