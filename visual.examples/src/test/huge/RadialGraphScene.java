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
package test.huge;

import org.netbeans.api.visual.action.MouseHoverAction;
import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.PanAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.anchor.RectangularAnchor;
import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class RadialGraphScene extends GraphScene<String, String, NodeController.StringNode, EdgeController.StringEdge> {

    private Widget nodesLayer = new Widget (this);
    private Widget edgesLayer = new Widget (this);

    private LineBorder lineBorder = new LineBorder (1, Color.BLACK);
    private MyHoverAction hoverAction = new MyHoverAction ();
    private MoveAction moveAction = new MoveAction ();
    private ZoomAction zoomAction = new ZoomAction ();
    private PanAction panAction = new PanAction ();

    public RadialGraphScene () {
        addChild (nodesLayer);
        addChild (edgesLayer);
        getActions ().addAction (hoverAction);
        getActions ().addAction (moveAction);
        getActions ().addAction (zoomAction);
        getActions ().addAction (panAction);
    }

    protected NodeController.StringNode attachNodeController (String node) {
        LabelWidget label = new LabelWidget (this);
        label.setBorder (lineBorder);
        label.setCheckClipping (true);
        label.setText (node);
        label.setOpaque (true);
        label.getActions ().addAction (hoverAction);
        label.getActions ().addAction (moveAction);
        nodesLayer.addChild (label);
        return new NodeController.StringNode (node, label);
    }

    protected EdgeController.StringEdge attachEdgeController (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setCheckClipping (true);
        edgesLayer.addChild (connection);
        return new EdgeController.StringEdge (edge, connection);
    }

    protected void attachEdgeSource (EdgeController.StringEdge edgeController, NodeController.StringNode sourceNodeController) {
        ConnectionWidget widget = ((ConnectionWidget) edgeController.getMainWidget ());
        widget.setSourceAnchor (new RectangularAnchor (sourceNodeController.getMainWidget ()));
    }

    protected void attachEdgeTarget (EdgeController.StringEdge edgeController, NodeController.StringNode targetNodeController) {
        ConnectionWidget widget = ((ConnectionWidget) edgeController.getMainWidget ());
        widget.setTargetAnchor (new RectangularAnchor (targetNodeController.getMainWidget ()));
    }

    private static class MyHoverAction extends MouseHoverAction.TwoStated {

        protected void unsetHovering (Widget widget) {
            widget.setBackground (Color.WHITE);
        }

        protected void setHovering (Widget widget) {
            widget.setBackground (Color.GREEN);
        }

    }

}
