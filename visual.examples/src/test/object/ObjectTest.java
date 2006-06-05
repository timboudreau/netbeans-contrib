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
package test.object;

import org.netbeans.api.visual.action.PanAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * @author David Kaspar
 */
public class ObjectTest extends GraphScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/displayable_64.png"); // NOI18N

    private Widget layer;

    private MyAction action = new MyAction ();

    public ObjectTest () {
        layer = new Widget (this);
        addChild (layer);
        getActions ().addAction (new ZoomAction ());
        getActions ().addAction (new PanAction ());
        getActions ().addAction (action);
    }

    protected NodeController.StringNode attachNodeController (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (test.object.ObjectTest.IMAGE);
        widget.setLabel (node);
        layer.addChild (widget);

        widget.getActions ().addAction (createSelectAction ());
        widget.getActions ().addAction (createHoverAction ());
        widget.getActions ().addAction (new MoveAction ());

        return new NodeController.StringNode (node, widget);
    }

    protected EdgeController.StringEdge attachEdgeController (String edge) {
        return null;
    }

    protected void attachEdgeSource (EdgeController.StringEdge edgeController, NodeController.StringNode sourceNodeController) {
    }

    protected void attachEdgeTarget (EdgeController.StringEdge edgeController, NodeController.StringNode targetNodeController) {
    }

    public class MyAction extends WidgetAction.Adapter {

        public State mouseClicked (Widget widget, WidgetMouseEvent event) {
            if (event.getButton () == MouseEvent.BUTTON3) {
                moveTo (event.getPoint ());
                return State.CONSUMED;
            } else if (event.getButton () == MouseEvent.BUTTON2) {
                moveTo (null);
                return State.CONSUMED;
            }
            return State.REJECTED;
        }

    }

    private void moveTo (Point point) {
        Collection<NodeController.StringNode> nodes = getNodes ();
        int index = 0;
        for (NodeController.StringNode node : nodes)
            getSceneAnimator ().getPreferredLocationAnimator ().setPreferredLocation (node.getMainWidget (), point != null ? point : new Point (++ index * 100, index * 100));
    }

    public static void main (String[] args) {
        ObjectTest scene = new ObjectTest ();
        scene.addNode ("form [Form]");
        scene.addNode ("list [List]");
        scene.addNode ("canvas [Canvas]");
        scene.addNode ("alert [Alert]");
        scene.moveTo (null);
        SceneSupport.show (scene);
    }

}
