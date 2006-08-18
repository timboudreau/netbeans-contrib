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
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.ListItemWidget;
import org.netbeans.api.visual.widget.general.ListWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ListTest extends GraphPinScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/custom_displayable_32.png"); // NOI18N

    private LayerWidget layer;

    private WidgetAction moveAction = ActionFactory.createMoveAction ();

    public ListTest () {
        layer = new LayerWidget (this);
        addChild (layer);
        getActions ().addAction (ActionFactory.createZoomAction ());
        getActions ().addAction (ActionFactory.createPanAction ());
    }

    protected Widget attachNodeWidget (String node) {
        ListWidget list = new ListWidget (this);
        list.setImage (IMAGE);
        list.setLabel (node);

        list.getActions ().addAction (moveAction);
        list.getActions ().addAction (createObjectHoverAction ());
        layer.addChild (list);

        return list;
    }

    protected Widget attachPinWidget (String node, String pin) {
        ListItemWidget item = new ListItemWidget (this);
        item.setLabel (pin);

        item.getActions ().addAction (createObjectHoverAction ());
        findWidget (node).addChild (item);

        return item;
    }

    protected Widget attachEdgeWidget (String edge) {
        return null;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourcePin, String sourcePin) {
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetPin, String targetPin) {
    }

    public static void main (String[] args) {
        ListTest scene = new ListTest ();

        String node1 = "Displayables";
        scene.addNode (node1);
        scene.addPin (node1, "Alert");
        scene.addPin (node1, "Form");
        scene.addPin (node1, "List");
        scene.addPin (node1, "TextBox");

        String node2 = "Commands";
        scene.addNode (node2);
        scene.addPin (node2, "Back");
        scene.addPin (node2, "Cancel");
        scene.addPin (node2, "Exit");
        scene.addPin (node2, "Help");
        scene.addPin (node2, "Ok");
        scene.addPin (node2, "Stop");

        SceneSupport.show (scene);
    }

}
