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
package test.list;

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.graph.PinController;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.ListItemWidget;
import org.netbeans.api.visual.widget.general.ListWidget;
import org.netbeans.api.visual.model.ObjectState;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ListTest extends GraphPinScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/custom_displayable_32.png"); // NOI18N

    private Widget layer;

    private WidgetAction hoverAction = new MyHover ();
    private WidgetAction moveAction = new MoveAction ();

    public ListTest () {
        layer = new Widget (this);
        addChild (layer);
        getActions ().addAction (new ZoomAction ());
        getActions ().addAction (new PanAction ());
        getActions ().addAction (hoverAction);
    }

    protected NodeController.StringNode attachNodeController (String node) {
        ListWidget list = new ListWidget (this);
        list.setImage (IMAGE);
        list.setLabel (node);

        list.getActions ().addAction (moveAction);
        list.getActions ().addAction (hoverAction);
        addChild (list);

        return new NodeController.StringNode (node, list);
    }

    protected EdgeController.StringEdge attachEdgeController (String edge) {
        return null;
    }

    protected PinController.StringPin attachPinController (NodeController.StringNode nodeController, String pin) {
        ListItemWidget item = new ListItemWidget (this);
        item.setLabel (pin);

        item.getActions ().addAction (hoverAction);
        nodeController.getMainWidget ().addChild (item);

        return new PinController.StringPin (pin, item);
    }

    protected void attachEdgeSource (EdgeController.StringEdge edgeController, PinController.StringPin sourcePinController) {
    }

    protected void attachEdgeTarget (EdgeController.StringEdge edgeController, PinController.StringPin targetPinController) {
    }

    public class MyHover extends MouseHoverAction.TwoStated {

        protected void unsetHovering (Widget widget) {
            widget.setState (ObjectState.NORMAL);
        }

        protected void setHovering (Widget widget) {
            widget.setState (new ObjectState (false, false, false, true));
        }
    }

    public static void main (String[] args) {
        ListTest scene = new ListTest ();

        NodeController.StringNode node1 = scene.addNode ("Displayables");
        scene.addPin (node1, "Alert");
        scene.addPin (node1, "Form");
        scene.addPin (node1, "List");
        scene.addPin (node1, "TextBox");

        NodeController.StringNode node2 = scene.addNode ("Commands");
        scene.addPin (node2, "Back");
        scene.addPin (node2, "Cancel");
        scene.addPin (node2, "Exit");
        scene.addPin (node2, "Help");
        scene.addPin (node2, "Ok");
        scene.addPin (node2, "Stop");

        SceneSupport.show (scene);
    }

}
