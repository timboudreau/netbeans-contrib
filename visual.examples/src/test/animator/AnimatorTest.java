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
package test.animator;

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author David Kaspar
 */
public class AnimatorTest extends GraphScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/displayable_64.png"); // NOI18N

    private LayerWidget layer;

    private WidgetAction moveAction = ActionFactory.createMoveAction ();

    public AnimatorTest () {
        layer = new LayerWidget (this);
        addChild (layer);
        getActions ().addAction (ActionFactory.createZoomAction ());
        getActions ().addAction (ActionFactory.createPanAction ());
        getActions ().addAction (new MyAction ());
    }

    protected Widget attachNodeWidget (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (IMAGE);
        widget.setLabel (node);
        layer.addChild (widget);

        widget.getActions ().addAction (createObjectHoverAction ());
        widget.getActions ().addAction (moveAction);

        return widget;
    }

    protected Widget attachEdgeWidget (String edge) {
        return null;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
    }

    public class MyAction extends WidgetAction.Adapter {

        public State mousePressed (Widget widget, WidgetMouseEvent event) {
            moveTo (event.getButton () == MouseEvent.BUTTON1 ? event.getPoint () : null);
            return State.CONSUMED;
        }

        public State mouseDragged (Widget widget, WidgetMouseEvent event) {
            moveTo (event.getPoint ());
            return State.CONSUMED;
        }

    }

    private void moveTo (Point point) {
        int index = 0;
        for (String node : getNodes ())
            getSceneAnimator ().animatePreferredLocation (findWidget (node), point != null ? point : new Point (++ index * 100, index * 100));
//            findWidget (node).setPreferredLocation (point != null ? point : new Point (++ index * 100, index * 100));
    }

    public static void main (String[] args) {
        AnimatorTest scene = new AnimatorTest ();
        scene.addNode ("form [Form]");
        scene.addNode ("list [List]");
        scene.addNode ("canvas [Canvas]");
        scene.addNode ("alert [Alert]");
        scene.moveTo (null);
        SceneSupport.show (scene);
    }

}
