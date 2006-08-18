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
public class ObjectTest extends GraphScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/displayable_64.png"); // NOI18N

    private LayerWidget backgroundLayer;
    private LayerWidget mainLayer;

    private WidgetAction moveAction = ActionFactory.createMoveAction ();
    private MyAction action = new MyAction ();

    public ObjectTest () {
        addChild (backgroundLayer = new LayerWidget (this));
        addChild (mainLayer = new LayerWidget (this));

        getActions ().addAction (ActionFactory.createZoomAction ());
        getActions ().addAction (ActionFactory.createPanAction ());
        getActions ().addAction (action);
        getActions ().addAction (ActionFactory.createRectangularSelectAction (this, backgroundLayer));
    }

    protected Widget attachNodeWidget (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (test.object.ObjectTest.IMAGE);
        widget.setLabel (node);
        mainLayer.addChild (widget);

        widget.getActions ().addAction (createSelectAction ());
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

    public void moveTo (Point point) {
        int index = 0;
        for (String node : getNodes ())
            getSceneAnimator ().animatePreferredLocation (findWidget (node), point != null ? point : new Point (++ index * 100, index * 100));
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
