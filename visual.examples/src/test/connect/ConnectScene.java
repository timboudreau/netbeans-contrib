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
package test.connect;

import org.netbeans.api.visual.action.ConnectAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.ReconnectAction;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.RectangularAnchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LayerWidget;
import test.SceneSupport;

import java.awt.event.MouseEvent;

/**
 * @author David Kaspar
 */
public class ConnectScene extends GraphScene.StringGraph {

    private LayerWidget mainLayer = new LayerWidget (this);
    private LayerWidget connectionLayer = new LayerWidget (this);
    private LayerWidget interractionLayer = new LayerWidget (this);

    private WidgetAction createAction = new SceneCreateAction ();
    private WidgetAction connectAction = new SceneConnectAction (interractionLayer);
    private WidgetAction reconnectAction = new SceneReconnectAction ();

    private long nodeCounter = 0;
    private long edgeCounter = 0;

    public ConnectScene () {
        addChild (mainLayer);
        addChild (connectionLayer);
        addChild (interractionLayer);

        getActions ().addAction (createAction);

        mainLayer.addChild (new LabelWidget (this, "Click on background to create a node. Drag a node to create a connection."));
    }

    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this, node);
        label.setBorder (new LineBorder (4));
        label.getActions ().addAction (createObjectHoverAction ());
        label.getActions ().addAction (createSelectAction ());
        label.getActions ().addAction (connectAction);
        mainLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape (PointShape.SQUARE_FILLED_BIG);
        connection.getActions ().addAction (createObjectHoverAction ());
        connection.getActions ().addAction (createSelectAction ());
        connection.getActions ().addAction (reconnectAction);
        connectionLayer.addChild (connection);
        return connection;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (w != null ? new RectangularAnchor (w) : null);
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (w != null ? new RectangularAnchor (w) : null);
    }

    private class SceneCreateAction extends WidgetAction.Adapter {

        public State mousePressed (Widget widget, WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {

                    addNode ("node" + nodeCounter ++).setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));

                    return State.CONSUMED;
                }
            return State.REJECTED;
        }

    }

    private class SceneConnectAction extends ConnectAction {

        private String source = null;
        private String target = null;

        public SceneConnectAction (Widget interractionLayer) {
            super (interractionLayer);
        }

        protected boolean isSourceWidget (Widget sourceWidget) {
            Object object = findObject (sourceWidget);
            source = isNode (object) ? (String) object : null;
            return source != null;
        }

        protected ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            Object object = findObject (targetWidget);
            target = isNode (object) ? (String) object : null;
            if (target != null)
                return ! source.equals (target) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        protected void createConnection (Widget sourceWidget, Widget targetWidget) {
            String edge = "edge" + edgeCounter ++;
            addEdge (edge);
            setEdgeSource (edge, source);
            setEdgeTarget (edge, target);
        }

    }

    private class SceneReconnectAction extends ReconnectAction {

        String edge;
        String originalNode;
        String replacementNode;

        public SceneReconnectAction () {
        }

        protected boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        protected boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            Object object = findObject (connectionWidget);
            edge = isEdge (object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        protected ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean recoonectingSource) {
            Object object = findObject (replacementWidget);
            replacementNode = isNode (object) ? (String) object : null;
            if (replacementNode != null)
                return ! originalNode.equals (replacementNode) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        protected void setConnectionAnchor (ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            if (replacementWidget == null)
                removeEdge (edge);
            else if (reconnectingSource)
                setEdgeSource (edge, replacementNode);
            else
                setEdgeTarget (edge, replacementNode);
        }

    }

    public static void main (String[] args) {
        SceneSupport.show (new ConnectScene ());
    }

}
