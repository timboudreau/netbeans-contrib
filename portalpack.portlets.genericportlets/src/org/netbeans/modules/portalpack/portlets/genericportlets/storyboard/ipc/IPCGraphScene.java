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

import java.awt.Color;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomPinWidget;
import java.awt.Image;
import java.awt.Paint;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.util.WidgetUtil;
import org.openide.util.Utilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.AcceptProvider;
import java.awt.datatransfer.DataFlavor;
import java.util.Hashtable;
import javax.swing.JTextField;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EdgePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.EventPinPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.IPCPopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions.NodePopUpMenuProvider;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets.CustomNodeWidget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Satyaranjan
 */
public class IPCGraphScene extends CustomVMDGraphScene {
    
    private static final Image IMAGE_LIST = Utilities.loadImage("de/eppleton/visualexplorer/resources/list_16.png"); // NOI18N
    private static final Image IMAGE_CANVAS = Utilities.loadImage("de/eppleton/visualexplorer/resources/custom_displayable_16.png"); // NOI18N
    private static final Image IMAGE_COMMAND = Utilities.loadImage("de/eppleton/visualexplorer/resources/command_16.png"); // NOI18N
    private static final Image IMAGE_ITEM = Utilities.loadImage("de/eppleton/visualexplorer/resources/item_16.png"); // NOI18N
    private static final Image GLYPH_PRE_CODE = Utilities.loadImage("de/eppleton/visualexplorer/resources/preCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_POST_CODE = Utilities.loadImage("de/eppleton/visualexplorer/resources/postCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_CANCEL = Utilities.loadImage("de/eppleton/visualexplorer/resources/cancelGlyph.png"); // NOI18N
  
 
    private static int nodeID = 1;
    private static int edgeID = 1;
    
    private static  Hashtable nodeMap = new Hashtable();
    private static  Hashtable edgeMap = new Hashtable();
   
    private WidgetAction popupMenuAction;
    private WidgetAction editorAction = ActionFactory.createInplaceEditorAction(new EventNameTextFieldEditor(this));
    private WidgetAction eventingPopUpMenuProvider;
    
    //private LayerWidget mainLayer;
   // private LayerWidget connectionLayer;
   // private LayerWidget interractionLayer;
    
   // private WidgetAction createAction;
    private WidgetAction connectAction;
    private WidgetAction reconnectAction;
    private IPCStoryBoardTopComponent ipcTop;
    private IPCActionsHandler actionsHandler;
    
    /** Creates a new instance of DBGraphScene */
    
    public IPCGraphScene(IPCStoryBoardTopComponent ipcTop) {
        super();
        this.ipcTop = ipcTop;
        this.actionsHandler = new IPCActionsHandler(this);
       //  mainLayer = new LayerWidget (this);
       //  connectionLayer = new LayerWidget (this);
       //  interractionLayer = new LayerWidget (this);
        
       // createAction = new SceneCreateAction ();
       connectAction = ActionFactory.createConnectAction (connectionLayer, new SceneConnectProvider (this));
       popupMenuAction = ActionFactory.createPopupMenuAction (new IPCPopUpMenuProvider (this));
       eventingPopUpMenuProvider = ActionFactory.createPopupMenuAction(new EventPinPopUpMenuProvider(this));
       setBorder(BorderFactory.createBevelBorder(true));
       setForeground(Color.PINK);
       
       JTextField jt = new JTextField(50);
       addChild(new ComponentWidget(this,jt));
      // reconnectAction = ActionFactory.createReconnectAction (new SceneReconnectProvider ());
    //   Paint p;
   //   setBackground()
       //  addChild(mainLayer);
       //  addChild(connectionLayer);
       //  addChild(interractionLayer);
        
    //    getActions().addAction(ActionFactory.createPopupMenuAction(new IPCPopUpMenuProvider(this)));
      //  getActions().addAction(ActionFactory.createZoomAction());
      //  getActions().addAction(connectAction);
       // getActions().addAction(reconnectAction);
        getActions().addAction(popupMenuAction);
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {  
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                Object obj = null;
                try{
                  
                    obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node","application/x-java-openide-nodednd"));
               
                 //DataFlavor[] d = (DataFlavor[]) transferable.getTransferDataFlavors();
                 //for(int i=-0;i<d.length;i++)
                 //   System.out.println(d[i] + "************** "+d[i].getHumanPresentableName());
                 //Object obj1 = transferable.getTransferData(d[0]);
                 System.out.println("^^^^^^^^^^^^^^^"+obj);
                }catch(Exception e){
                    e.printStackTrace();
                }
                 
                 if(obj == null)
                 {
                     System.out.println("Obj is null............");
                     return ConnectorState.REJECT;
                     
                 }else if(obj instanceof PortletNode)
                 {
                     return ConnectorState.ACCEPT;
                 }
                 return ConnectorState.REJECT;
               
            }
            
            public void accept(Widget widget, Point point, Transferable transferable) {
                try{
                    Object obj = transferable.getTransferData(new DataFlavor("application/x-java-openide-nodednd; class=org.openide.nodes.Node","application/x-java-openide-nodednd"));
                    PortletNode node = (PortletNode)obj;
                    addPortletNode(node,point);
                }catch(Exception e){
                    e.printStackTrace();
                }    
                
             //   Image image = getImageFromTransferable(transferable);
              //  Widget w = GraphSceneImpl.this.addNode(new MyNode(image));
              //  w.setPreferredLocation(widget.convertLocalToScene(point));
            }
       
        }));
        
    }
    
    public IPCActionsHandler getTaskHandler()
    {
        return actionsHandler;
    }
    
    //end
   /* public  void addPortletNode(String name) {
        if(checkIfNodePresent(name))
            return;
        VMDNodeWidget mobileWidget = WidgetUtil.createNode(this, 100, 100, IMAGE_LIST, name, "List", null);
        
        mobileWidget.getActions().addAction(connectAction);
        String mobile = name;
        VMDPinWidget pin1 = WidgetUtil.createPin(this, mobile, name + "evt1", IMAGE_ITEM, name + "evt1", "Element");
        VMDPinWidget consumePin = WidgetUtil.createPin(this, mobile, "consume_" + name, IMAGE_ITEM, "consume_" + name, "Element");
        
        
        pin1.getActions().addAction(connectAction);
        consumePin.getActions().addAction(connectAction);
      ///  WidgetUtil.createPin(this, mobile, name + "evt2", IMAGE_ITEM, name + "evt2", "Element");
      ///  WidgetUtil.createPin(this, mobile, name + "evt3", IMAGE_ITEM, name + "evt3", "Element");
        
        //createPin (this, mobile, "evt1", IMAGE_ITEM, "evt1", "Element");
        nodeMap.put(mobile, new Object());
        validate();
    }*/
    
    public  void addPortletNode(PortletNode node,Point point) {
        
        String name = node.getName();
        String key = node.getID();
        if(checkIfNodePresent(name))
            return;
        CustomNodeWidget mobileWidget = (CustomNodeWidget)WidgetUtil.createNode(this, point.x, point.y, IMAGE_LIST, key, name, "List", null);
        mobileWidget.getActions().addAction(connectAction);
        mobileWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new NodePopUpMenuProvider(this,key)));
        //mobileWidget.getActions().addAction(ActionFactory.createResizeAction());
        String nodeID = key;
        PortletXMLDataObject dobj = node.getDataObject();
        String[] events = dobj.getPortletEventingHandler().getPublishEvents(name);
        if(events != null)
        {
             for(int i=0;i<events.length;i++)
             {    
                 
                 addEventPinToNode(nodeID, events[i]);
                /* VMDPinWidget pin1 = WidgetUtil.createPin(this,nodeID, key+"_"+events[i], IMAGE_ITEM, events[i], "Element");
                 ((CustomPinWidget)pin1).setEventName(events[i]);
                 pin1.getActions().addAction(connectAction);
                 pin1.getActions().addAction(editorAction);*/
             }
        }
       // VMDPinWidget consumePin = WidgetUtil.createPin(this, mobile, "consume_" + name, IMAGE_ITEM, "consume_" + name, "Element");
       
       // consumePin.getActions().addAction(connectAction);
      ///  WidgetUtil.createPin(this, mobile, name +"_"+ "evt2", IMAGE_ITEM, name +"_"+ "evt2", "Element");
      //  WidgetUtil.createPin(this, mobile, name +"_"+ "evt3", IMAGE_ITEM, name +"_"+ "evt3", "Element");     
        //createPin (this, mobile, "evt1", IMAGE_ITEM, "evt1", "Element");
        nodeMap.put(nodeID, node);
        checkAndPerformNodeDependency((CustomNodeWidget)mobileWidget);
     //   mobileWidget.setVisible(true);
        validate();
    }
    
   
    
    public boolean checkIfNodePresent(String nodeID)
    {
        if(nodeMap.get(nodeID) != null)
            return true;
        else
            return false;
    }
    
    private CustomPinWidget addEventPinToNode(String nodeID,String eventName)
    {
         VMDPinWidget pin1 = WidgetUtil.createPin(this,nodeID, nodeID+"_"+eventName, IMAGE_ITEM, eventName, "Element");
         ((CustomPinWidget)pin1).setEventName(eventName);
         pin1.getActions().addAction(connectAction);
         pin1.getActions().addAction(editorAction);
         pin1.getActions().addAction(eventingPopUpMenuProvider);
         return (CustomPinWidget)pin1;
    }
    
    public void deletePortletNodeFromScene(String nodeID,boolean removeRef) {
        Object obj = nodeMap.get(nodeID);
        if(obj != null) {
           // nodeMap.remove(nodeID);
           // if(removeRef)
           this.removeNodeWithEdges(nodeID);
           if(removeRef)
               nodeMap.remove(nodeID);
            
        }
    }
    
    public void addEvent(String nodeKey)
    {
        // findWidget(nodeKey).revalidate();
          PortletNode node = getPortletNode(nodeKey);
          if(node == null)
              return;
          String evtName = resolveNewEventName(node);
          addEventPinToNode(nodeKey, evtName);
    /*     VMDPinWidget pin1 = WidgetUtil.createPin(this, nodeKey, nodeKey+"_"+System.currentTimeMillis(), IMAGE_ITEM, evtName, "Element");
          ((CustomPinWidget)pin1).setEventName(evtName);
          pin1.getActions().addAction(connectAction);
          pin1.getActions().addAction(editorAction);*/
          try{
            node.getDataObject().getPortletEventingHandler().addPublishEvent(node.getName(), evtName,null);
          }catch(Exception e){
              e.printStackTrace();
          }
          validate();
          //revalidate();
    }
    
    private String resolveNewEventName(PortletNode node)
    {
        String prefix = "New_Event";
        String evtName = prefix;
        int i=1;
        while(node.getDataObject().getPortletEventingHandler().isPublishEventExists(node.getName(),evtName))
        {
            evtName =  prefix + "_"+i;
            i++;
        }
        return evtName;
    }
    
    private void deleteEdgeFromScene(String edgeID,boolean removeRef) {
        Object obj = edgeMap.get(edgeID);
        if(obj != null) {
           // nodeMap.remove(nodeID);
          //  if(removeRef)
            try{
                this.removeEdge(edgeID);
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }
    
    private void checkAndPerformNodeDependency(CustomNodeWidget nodeWidget)
    {
        String orgNodeKey = nodeWidget.getNodeKey();   //getNodeName();
        PortletNode orgPortletNode = (PortletNode)nodeMap.get(orgNodeKey);
        String orgNodename = orgPortletNode.getName();
        String[] consumeEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getProcessEvents(orgNodename);
        String[] sourceEvts = orgPortletNode.getDataObject().getPortletEventingHandler().getPublishEvents(orgNodename);
        Set s = nodeMap.keySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            String ndKey = (String)it.next();
         ////   if(ndName.equals(orgNodename))
         /////       continue;
            PortletNode portletNode = (PortletNode)nodeMap.get(ndKey);
            if(portletNode == null) continue;
            String[] evts = portletNode.getDataObject().getPortletEventingHandler().getPublishEvents(portletNode.getName());
            
            for(int i=0;i<consumeEvts.length;i++)
            {
                if(hasString(consumeEvts[i], evts))
                {
                  /*  VMDNodeWidget srcNodeWidget = (VMDNodeWidget)findWidget(ndName);
                    if(srcNodeWidget == null) continue;
                    Collection col = getNodePins(ndName);
                    Iterator ndPins = col.iterator();*/
                    Object ob = findWidget(ndKey+"_"+consumeEvts[i]);
                    if(ob instanceof CustomPinWidget)
                    {
                       CustomPinWidget pin = (CustomPinWidget)ob;
                       if(pin == null)
                       {
                            continue;
                       }
                       
                       connectBothPortletNodes(pin,nodeWidget);
                    }
                }
            }
            
            String[] targetConsumeEvts = portletNode.getDataObject().getPortletEventingHandler().getProcessEvents(portletNode.getName());
            for(int i=0;i<sourceEvts.length;i++)
            {
                if(hasString(sourceEvts[i], targetConsumeEvts))
                {
                    Object ob = findWidget(orgNodeKey+"_"+sourceEvts[i]);
                    if(ob instanceof CustomPinWidget)
                    {
                       CustomPinWidget pin = (CustomPinWidget)ob;
                       if(pin == null)
                       {
                            continue;
                       }
                       VMDNodeWidget targetNodeWidget = (VMDNodeWidget)findWidget(ndKey);
                       if(targetNodeWidget != null)
                             connectBothPortletNodes(pin,(CustomNodeWidget)targetNodeWidget);
                    }
                }
            }
        }
    }
    
    private void connectBothPortletNodes(CustomPinWidget sourceWidget,CustomNodeWidget targetNode)
    {
            String eventName = ((CustomPinWidget)sourceWidget).getEventName();
            CustomPinWidget consumePin = (CustomPinWidget)findWidget(targetNode.getNodeKey()+"_"+"consume_"+eventName);
            
            //a create a cosumer pin
            if(consumePin == null){
                 consumePin = (CustomPinWidget)WidgetUtil.createPin(this, targetNode.getNodeKey(), targetNode.getNodeKey()+"_"+"consume_"+eventName, IMAGE_ITEM, "consume_"+eventName, "Element");
                 consumePin.setEventName(eventName);
            }
            else
                System.out.println("Pin Exist..............");
            String edge = "edge" + edgeID ++;
            Widget edgeWidget = addEdge (edge);
            edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction (new EdgePopUpMenuProvider(edge,this,consumePin,sourceWidget)));
            setEdgeSource(edge,((CustomPinWidget)sourceWidget).getKey());
            setEdgeTarget(edge,consumePin.getKey());
            edgeMap.put(edge, new Object());
    }
    
    private boolean hasString(String org,String[] arr)
    {
        for(int i=0;i<arr.length;i++)
        {
            if(org.equals(arr[i]))
                return true;
        }
        return false;
    }
    
    public void resetScene() {
       
       
        
        Set edgekeys = edgeMap.keySet();
        Iterator edgeit = edgekeys.iterator();
        while(edgeit.hasNext()) {
            String edgeId = (String)edgeit.next();
     ///       deleteEdgeFromScene(edgeId,false);
        }
        
        Set keys = nodeMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            String nodeId = (String)it.next();
            deletePortletNodeFromScene(nodeId,false);
        }
        //this.validate();
        nodeMap.clear();
        
        nodeMap.clear();
        edgeMap.clear();
        this.removeChildren();
        revalidate(true);
        ipcTop.reset();
        //this.
        System.out.println("*********************"+this.getActions().getActions());
        System.out.println("Reset is called...................");
        
        //this.resetScene();
    }
    
   
    
    //inner class started
      private class SceneCreateAction extends WidgetAction.Adapter {

        public State mousePressed (Widget widget, WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {

                    //addNode ("node" + nodeCounter ++).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));

                    return State.CONSUMED;
                }
            return State.REJECTED;
        }

    }

    private class SceneConnectProvider implements ConnectProvider {

        private Widget source = null;
    ////    private VMDPinWidget target = null;
        private CustomNodeWidget targetNode = null;
        private IPCGraphScene scene;
        public SceneConnectProvider(IPCGraphScene scene)
        {
            this.scene = scene;
        }

        public boolean isSourceWidget (Widget sourceWidget) {
            System.out.println("Connect.............................111");
          ///  Object object = findObject (sourceWidget);
         /*   if(sourceWidget instanceof VMDPinWidget)
            {
                source = (VMDPinWidget)sourceWidget;
                return true;
            } else
                return false;*/
            if(sourceWidget instanceof VMDPinWidget)
            {
                VMDPinWidget pinWidget = (VMDPinWidget)sourceWidget;
                
                source = (VMDPinWidget)sourceWidget;
                return true;
                
                /*
                VMDPinWidget pinWidget = (VMDPinWidget)sourceWidget;
                if(pinWidget.getPinName().startsWith("consume"))
                {
                    source = (VMDPinWidget)sourceWidget;
                    return true;
                }else
                    return false;*/
            }
            else
                return false;
           // source = isNode (object) ?  (Widget)object : null;
            //return source != null;
        }

        public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            System.out.println("Connect.............................222");
            if(targetWidget != null && targetWidget instanceof CustomNodeWidget)
            {
                targetNode = (CustomNodeWidget)targetWidget;
                return ConnectorState.ACCEPT;
            }else{
                Widget widget = targetWidget.getParentWidget();
                if(widget instanceof CustomNodeWidget)
                {
                   
                    targetNode = (CustomNodeWidget)widget;
                    return ConnectorState.ACCEPT;
                }
                else
                return ConnectorState.REJECT_AND_STOP;
            }
           
            //Object object = findObject (targetWidget);
            /********************************
            if(targetWidget != null && targetWidget instanceof  VMDPinWidget)
            {
                target = (VMDPinWidget)targetWidget;
                if(((VMDPinWidget)targetWidget).getPinName().startsWith("consume"))
                {
                    target = null;
                    return ConnectorState.REJECT_AND_STOP;
                }
                return ConnectorState.ACCEPT;
            }
            else
                return ConnectorState.REJECT_AND_STOP;
             * **********************/
            /*if((targetWidget!= null && targetWidget instanceof VMDNodeWidget))
            {
                if(!sourceWidget.equals(targetWidget))
                {
                     //VMDPinWidget pin1 = WidgetUtil.createPin(((CustomVMDGraphScene)scene), ((VMDNodeWidget)targetWidget).getNodeName(), ((VMDPinWidget)sourceWidget).getPinName(), IMAGE_ITEM, ((VMDPinWidget)sourceWidget).getPinName(), "Element");
                     target = (VMDNodeWidget)targetWidget;
                  
                     return ConnectorState.ACCEPT;
                }
                else
                {
                    target = null;
                    return ConnectorState.REJECT_AND_STOP;
                }
            }else if(targetWidget != null && targetWidget.getParentWidget() instanceof VMDNodeWidget)
            {
                VMDNodeWidget twid =(VMDNodeWidget) targetWidget.getParentWidget();
                if(!sourceWidget.equals(twid))
                {
                    target = twid;
                    return ConnectorState.ACCEPT;
                }else
                {
                    target = null;
                    return ConnectorState.REJECT_AND_STOP;
                }
            }
            else{
                target = null;
                return ConnectorState.REJECT_AND_STOP;
            }*/
                    
           // target = isNode (object) ? (Widget) object : null;
           // if (target != null)
             //   return ! source.equals (target) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            //return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomTargetWidgetResolver (Scene scene) {
            System.out.println("Connect.............................3333");
            return false;
        }

        public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
            System.out.println("Connect.............................4444");
            return null;
        }

        public void createConnection (Widget sourceWidget, Widget targetWidget) {
            /*************
            System.out.println("Connect.............................55555");
            if(source == null || target == null)
                return;
            if(sourceWidget == null || targetWidget == null)
                return;
            if(((VMDPinWidget)targetWidget).getPinName() == null || ((VMDPinWidget)targetWidget).getPinName().startsWith("consume"))
                 return;
            String edge = "edge" + edgeID ++;
            addEdge (edge);
            setEdgeSource(edge,((VMDPinWidget)sourceWidget).getPinName());
            setEdgeTarget(edge,((VMDPinWidget)targetWidget).getPinName());
            edgeMap.put(edge, new Object());
            
            //added code to put the data in map
            Widget parentSourceWidget = sourceWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
                System.out.println("Parent Node name is--------------------"+ ((VMDNodeWidget)parentSourceWidget).getNodeName());
                
            }
            parentSourceWidget = targetWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
                System.out.println("Target Parent Node name is--------------------"+ ((VMDNodeWidget)parentSourceWidget).getNodeName());
                
            }
             *****************/
            
            
            System.out.println("Connect.............................55555");
            if(source == null || targetNode == null)
                return;
            if(sourceWidget == null || targetWidget == null)
                return;
            
            String eventName = ((CustomPinWidget)sourceWidget).getEventName();
            CustomPinWidget consumePin = (CustomPinWidget)findWidget(targetNode.getNodeKey()+"_"+"consume_"+eventName);
            
            //a create a cosumer pin
            if(consumePin == null){
                 consumePin = (CustomPinWidget)WidgetUtil.createPin(scene, targetNode.getNodeKey(), targetNode.getNodeKey()+"_"+"consume_"+eventName, IMAGE_ITEM, "consume_"+eventName, "Element");
                 consumePin.setEventName(eventName);
            }
            else
                System.out.println("Pin Exist..............");
            String edge = "edge" + edgeID ++;
            Widget edgeWidget = addEdge (edge);
            edgeWidget.getActions().addAction(ActionFactory.createPopupMenuAction (new EdgePopUpMenuProvider(edge,scene,consumePin,sourceWidget)));
            setEdgeSource(edge,((CustomPinWidget)sourceWidget).getKey());
            setEdgeTarget(edge,consumePin.getKey());
            edgeMap.put(edge, new Object());
            
            PortletNode targetPortletNode = (PortletNode)nodeMap.get(targetNode.getNodeKey());
            if(targetPortletNode != null)
            {
                try{
                     targetPortletNode.getDataObject().getPortletEventingHandler().addProcessEvent(targetPortletNode.getName(), eventName,null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //added code to put the data in map
            Widget parentSourceWidget = sourceWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
                System.out.println("Parent Node name is--------------------"+ ((CustomNodeWidget)parentSourceWidget).getNodeKey());
                
            }
            parentSourceWidget = targetWidget.getParentWidget();
            if(parentSourceWidget instanceof VMDNodeWidget)
            {
                System.out.println("Target Parent Node name is--------------------"+ ((CustomNodeWidget)parentSourceWidget).getNodeKey());
                
            }
              
           // setEdgeSource (edge, source);
           // setEdgeTarget (edge, target);
        }

    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        String originalNode;
        String replacementNode;

        public void reconnectingStarted (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public void reconnectingFinished (ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        public boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        public boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        public ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            Object object = findObject (replacementWidget);
            replacementNode = isNode (object) ? (String) object : null;
            if (replacementNode != null)
                return ConnectorState.ACCEPT;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        public boolean hasCustomReplacementWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveReplacementWidget (Scene scene, Point sceneLocation) {
            return null;
        }
        
        public void reconnect (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null)
                removeEdge (edge);
            else if (reconnectingSource)
                setEdgeSource (edge, replacementNode);
            else
                setEdgeTarget (edge, replacementNode);
        }

    }  
    
    public PortletNode getPortletNode(String nodeKey)
    {
        return (PortletNode)nodeMap.get(nodeKey);
    }
    
}

class EventNameTextFieldEditor implements TextFieldInplaceEditor {
    private IPCGraphScene scene;
    public EventNameTextFieldEditor(IPCGraphScene scene)
    {
        this.scene = scene;
    }
    public boolean isEnabled(Widget widget) {
        return true;
    }

    public String getText(Widget widget) {
        
        return ((CustomPinWidget) widget).getPinName();
    }

    public void setText(Widget widget, String text) {
        if(!CoreUtil.validateString(text, false))
        {
            NotifyDescriptor nd =new  NotifyDescriptor.Message("Invalid Event Name",NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        String oldEventName = ((CustomPinWidget) widget).getEventName();
        ((CustomPinWidget) widget).setPinName(text);
        ((CustomPinWidget) widget).setEventName(text);
        String nodeKey = ((CustomPinWidget) widget).getNodeKey();
        PortletNode pNode = scene.getPortletNode(nodeKey);
        try{
          pNode.getDataObject().getPortletEventingHandler().renamePublishEvent(pNode.getName() , oldEventName,text,null);
        }catch(PortletEventException e){
            e.printStackTrace();
        }
    }

}
