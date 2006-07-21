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
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.RectangularAnchor;
import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * @author David Kaspar
 */
public class DemoGraphScene extends GraphScene.StringGraph {

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    private WidgetAction moveAction = new MoveAction ();

    public DemoGraphScene() {
        mainLayer = new LayerWidget (this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget (this);
        addChild(connectionLayer);
    }

    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this, node);
        label.setBorder (new LineBorder (4));
        label.getActions ().addAction (moveAction);
        mainLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
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

}
