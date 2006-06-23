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

package javaone.demo4;

import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.RectangularAnchor;
import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;

/**
 * @author David Kaspar
 */
public class DemoGraphScene extends GraphScene.StringGraph {

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    public DemoGraphScene() {
        mainLayer = new LayerWidget (this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget (this);
        addChild(connectionLayer);
    }

    protected NodeController.StringNode attachNodeController(String node) {
        LabelWidget label = new LabelWidget (this, node);
        label.setBorder (new LineBorder (4));
        label.getActions().addAction(new MoveAction ());
        mainLayer.addChild (label);
        return new NodeController.StringNode (node, label);
    }

    protected EdgeController.StringEdge attachEdgeController(String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionLayer.addChild(connection);
        return new EdgeController.StringEdge (edge, connection);
    }

    protected void attachEdgeSource(EdgeController.StringEdge edgeController, NodeController.StringNode sourceNodeController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setSourceAnchor(sourceNodeController != null ? new RectangularAnchor (sourceNodeController.getMainWidget()) : null);
    }

    protected void attachEdgeTarget(EdgeController.StringEdge edgeController, NodeController.StringNode targetNodeController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setTargetAnchor(targetNodeController != null ? new RectangularAnchor (targetNodeController.getMainWidget()) : null);
    }

}
