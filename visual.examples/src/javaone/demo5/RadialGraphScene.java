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
package javaone.demo5;

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.graph.GraphScene;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class RadialGraphScene extends GraphScene.StringGraph {

    private LayerWidget nodesLayer = new LayerWidget (this);
    private LayerWidget edgesLayer = new LayerWidget (this);

    private Border lineBorder = BorderFactory.createLineBorder (1, Color.BLACK);
    private WidgetAction hoverAction = ActionFactory.createHoverAction (new MyHoverProvider ());
    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    private WidgetAction zoomAction = ActionFactory.createZoomAction ();
    private WidgetAction panAction = ActionFactory.createPanAction ();

    public RadialGraphScene () {
        addChild (nodesLayer);
        addChild (edgesLayer);
        getActions ().addAction (hoverAction);
        getActions ().addAction (moveAction);
        getActions ().addAction (zoomAction);
        getActions ().addAction (panAction);
    }

    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this);
        label.setBorder (lineBorder);
        label.setBackground (Color.WHITE);
        label.setOpaque (true);
        label.setCheckClipping (true);
        label.setLabel (node);
        label.getActions ().addAction (hoverAction);
        label.getActions ().addAction (moveAction);
        nodesLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setCheckClipping (true);
        edgesLayer.addChild (connection);
        return connection;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget (edge);
        widget.setSourceAnchor (AnchorFactory.createRectangularAnchor (findWidget (sourceNode)));
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        ConnectionWidget widget = (ConnectionWidget) findWidget (edge);
        widget.setTargetAnchor (AnchorFactory.createRectangularAnchor (findWidget (targetNode)));
    }

    private static class MyHoverProvider implements TwoStateHoverProvider {

        public void unsetHovering (Widget widget) {
            widget.setBackground (Color.WHITE);
            widget.setForeground (Color.BLACK);
        }

        public void setHovering (Widget widget) {
            widget.setBackground (new Color (52, 124, 150));
            widget.setForeground (Color.WHITE);
        }

    }

}
