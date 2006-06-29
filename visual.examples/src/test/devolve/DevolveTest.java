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
package test.devolve;

import org.netbeans.api.visual.action.PanAction;
import org.netbeans.api.visual.action.RectangularSelectAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.layout.SerialLayout;
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
public class DevolveTest extends GraphScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/displayable_64.png"); // NOI18N

    private LayerWidget backgroundLayer;
    private LayerWidget mainLayer;

    private DevolveTest.MyAction action = new DevolveTest.MyAction ();

    public DevolveTest () {
        addChild (backgroundLayer = new LayerWidget (this));
        addChild (mainLayer = new LayerWidget (this));

        mainLayer.setDevolveLayout (new SerialLayout (SerialLayout.Orientation.HORIZONTAL));

        getActions ().addAction (new ZoomAction ());
        getActions ().addAction (new PanAction ());
        getActions ().addAction (action);
        getActions ().addAction (new RectangularSelectAction (this, backgroundLayer));
    }

    protected NodeController.StringNode attachNodeController (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (IMAGE);
        widget.setLabel (node);
        mainLayer.addChild (widget);

        widget.getActions ().addAction (createSelectAction ());
        widget.getActions ().addAction (createHoverAction ());
        widget.getActions ().addAction (createMoveAction ());

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
            if (event.getButton () != MouseEvent.BUTTON1) {
                mainLayer.reevaluateLayout (true);
                return State.CONSUMED;
            }
            return State.REJECTED;
        }

    }

    public static void main (String[] args) {
        DevolveTest scene = new DevolveTest ();
        scene.addNode ("form [Form]");
        scene.addNode ("list [List]");
        scene.addNode ("canvas [Canvas]");
        scene.addNode ("alert [Alert]");
        // scene.mainLayer.reevaluateLayout (true); // WARNING: Initial reevaluateLayout will not work because the scene is not initialized completely.
        SceneSupport.show (scene);
    }

}
