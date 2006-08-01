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

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
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

    private LayerWidget mainLayer;
    private SceneLayout.DevolveWidgetLayout devolveLayout;
    private DevolveTest.MyAction action = new DevolveTest.MyAction ();
    private WidgetAction moveAction = new MoveAction ();

    public DevolveTest () {
        LayerWidget backgroundLayer = new LayerWidget (this);
        addChild (backgroundLayer);

        addChild (mainLayer = new LayerWidget (this));

        devolveLayout = new SceneLayout.DevolveWidgetLayout (mainLayer, LayoutFactory.createHorizontalLayout (), true);

        getActions ().addAction (new ZoomAction ());
        getActions ().addAction (new PanAction ());
        getActions ().addAction (action);
        getActions ().addAction (new RectangularSelectAction (this, backgroundLayer));
    }

    protected Widget attachNodeWidget (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (IMAGE);
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
            if (event.getButton () != MouseEvent.BUTTON1) {
                devolveLayout.invokeLayout ();
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
        scene.devolveLayout.invokeLayout ();
        SceneSupport.show (scene);
    }

}
