/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package test.freeconnect;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.FreeConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;

import java.awt.*;
import java.util.Collections;


/**
 * @author Alex
 */
public class DemoGraphScene extends GraphScene.StringGraph {
    
    private static final Image IMAGE = Utilities.loadImage("test/resources/node.png"); // NOI18N
    
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private LayerWidget interractionLayer = new LayerWidget(this);
    private LayerWidget backgroundLayer = new LayerWidget(this);
    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private Router router = RouterFactory.createFreeRouter();
    
    private WidgetAction connectAction = ActionFactory.createExtendedConnectAction(interractionLayer, new SceneConnectProvider(this));
    private WidgetAction reconnectAction = ActionFactory.createReconnectAction(new SceneReconnectProvider(this));
    private WidgetAction moveControlPointAction = ActionFactory.createFreeMoveControlPointAction();
    private WidgetAction selectAction = ActionFactory.createSelectAction(new ObjectSelectProvider());

    private NodeMenu nodeMenu=new NodeMenu(this);
    private EdgeMenu edgeMenu=new EdgeMenu(this);
    
    
    public DemoGraphScene() {
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);
        
        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        addChild(interractionLayer);
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));
        getActions().addAction(ActionFactory.createPopupMenuAction(new SceneMainMenu(this)));
    }
    
    protected Widget attachNodeWidget(String node) {
        IconNodeWidget label = new IconNodeWidget(this);
        label.setLabel(node);
        label.setImage(IMAGE);
        label.getActions().addAction(connectAction);
        label.getActions().addAction(moveAction);
        mainLayer.addChild(label);
        label.getActions().addAction(ActionFactory.createPopupMenuAction(nodeMenu));
        return label;
    }
    
    protected Widget attachEdgeWidget(String edge) {
        FreeConnectionWidget connection = new FreeConnectionWidget(this);
        connection.setRouter(router);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setControlPointShape(PointShape.SQUARE_FILLED_BIG);
        connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connectionLayer.addChild(connection);
        connection.getActions().addAction(reconnectAction);
        connection.getActions().addAction(createSelectAction());
        connection.getActions().addAction(ActionFactory.createAddRemoveControlPointAction());
        connection.getActions().addAction(moveControlPointAction);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(edgeMenu));
        return connection;
    }
    
    protected void attachEdgeSourceAnchor(String edge, String oldSourceNode, String sourceNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget sourceNodeWidget = findWidget (sourceNode);
        widget.setSourceAnchor(sourceNodeWidget != null ? AnchorFactory.createFreeRectangularAnchor(sourceNodeWidget, true) : null);
    }
    
    protected void attachEdgeTargetAnchor(String edge, String oldTargetNode, String targetNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget(edge);
        Widget targetNodeWidget = findWidget (targetNode);
        widget.setTargetAnchor(targetNodeWidget != null ? AnchorFactory.createFreeRectangularAnchor(targetNodeWidget, true) : null);
    }
    
    private class ObjectSelectProvider implements SelectProvider {
        
        public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return false;
        }
        
        public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }
        
        public void select(Widget widget, Point localLocation, boolean invertSelection) {
            Object object = findObject(widget);
            
            if (object != null) {
                if (getSelectedObjects().contains(object))return;
                userSelectionSuggested(Collections.singleton(object), invertSelection);
            } else
                userSelectionSuggested(Collections.emptySet(), invertSelection);
        }
    }
    
}
