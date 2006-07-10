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
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.model.ObjectController;
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
    }

    protected NodeController.StringNode attachNodeController (String node) {
        LabelWidget label = new LabelWidget (this, node);
        label.setBorder (new LineBorder (4));
        label.getActions ().addAction (createHoverAction ());
        label.getActions ().addAction (createSelectAction ());
        label.getActions ().addAction (connectAction);
        mainLayer.addChild (label);
        return new NodeController.StringNode (node, label);
    }

    protected EdgeController.StringEdge attachEdgeController (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connection.setEndPointShape (PointShape.SQUARE_FILLED_BIG);
        connection.getActions ().addAction (createHoverAction ());
        connection.getActions ().addAction (createSelectAction ());
        connection.getActions ().addAction (reconnectAction);
        connectionLayer.addChild (connection);
        return new EdgeController.StringEdge (edge, connection);
    }

    protected void attachEdgeSource (EdgeController.StringEdge edgeController, NodeController.StringNode sourceNodeController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setSourceAnchor (sourceNodeController != null ? new RectangularAnchor (sourceNodeController.getMainWidget ()) : null);
    }

    protected void attachEdgeTarget (EdgeController.StringEdge edgeController, NodeController.StringNode targetNodeController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setTargetAnchor (targetNodeController != null ? new RectangularAnchor (targetNodeController.getMainWidget ()) : null);
    }

    private class SceneCreateAction extends WidgetAction.Adapter {

        public State mousePressed (Widget widget, WidgetMouseEvent event) {
            if (event.getClickCount () == 1)
                if (event.getButton () == MouseEvent.BUTTON1 || event.getButton () == MouseEvent.BUTTON2) {

                    addNode (Long.toString (nodeCounter ++)).getMainWidget ().setPreferredLocation (widget.convertLocalToScene (event.getPoint ()));

                    return State.CONSUMED;
                }
            return State.REJECTED;
        }

    }

    private class SceneConnectAction extends ConnectAction {

        private NodeController.StringNode sourceController = null;
        private NodeController.StringNode targetController = null;

        public SceneConnectAction (Widget interractionLayer) {
            super (interractionLayer);
        }

        protected boolean isSourceWidget (Widget sourceWidget) {
            ObjectController objectController = findObjectController (sourceWidget);
            sourceController = objectController instanceof NodeController.StringNode ? (NodeController.StringNode) objectController : null;
            return sourceController != null;
        }

        protected ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            ObjectController objectController = findObjectController (targetWidget);
            if (objectController instanceof NodeController.StringNode) {
                targetController = (NodeController.StringNode) objectController;
                return ! sourceController.equals (targetController) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            }
            targetController = null;
            return objectController != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        protected void createConnection (Widget sourceWidget, Widget targetWidget) {
            EdgeController.StringEdge edge = addEdge (Long.toString (edgeCounter ++));
            setEdgeSource (edge, sourceController);
            setEdgeTarget (edge, targetController);
        }

    }

    private class SceneReconnectAction extends ReconnectAction {

        EdgeController.StringEdge edge;
        NodeController.StringNode originalNode;
        NodeController.StringNode replacementNode;

        public SceneReconnectAction () {
        }

        protected boolean isSourceReconnectable (ConnectionWidget connectionWidget) {
            ObjectController objectController = findObjectController (connectionWidget);
            if (objectController instanceof EdgeController.StringEdge)
                edge = (EdgeController.StringEdge) objectController;
            else
                edge = null;
            originalNode = edge != null ? getEdgeSource (edge) : null;
            return originalNode != null;
        }

        protected boolean isTargetReconnectable (ConnectionWidget connectionWidget) {
            ObjectController objectController = findObjectController (connectionWidget);
            if (objectController instanceof EdgeController.StringEdge)
                edge = (EdgeController.StringEdge) objectController;
            else
                edge = null;
            originalNode = edge != null ? getEdgeTarget (edge) : null;
            return originalNode != null;
        }

        protected ConnectorState isReplacementWidget (ConnectionWidget connectionWidget, Widget replacementWidget, boolean recoonectingSource) {
            ObjectController objectController = findObjectController (replacementWidget);
            if (objectController instanceof NodeController.StringNode) {
                replacementNode = (NodeController.StringNode) objectController;
                return ! originalNode.equals (replacementNode) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            }
            replacementNode = null;
            return objectController != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
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
