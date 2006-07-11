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
package test.connectionlabels;

import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.MouseHoverAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.DirectionalAnchor;
import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.layout.ConnectionWidgetLayout;
import org.netbeans.api.visual.router.OrthogonalSearchRouter;
import org.netbeans.api.visual.widget.*;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ConnectionLabelsTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        LayerWidget mainLayer = new LayerWidget (scene);
        scene.addChild (mainLayer);
        LayerWidget connectionLayer = new LayerWidget (scene);
        scene.addChild (connectionLayer);
        WidgetAction action = new MouseHoverAction.SceneLookFeel (scene);
        scene.getActions ().addAction (action);

        LabelWidget sourceNode = new LabelWidget (scene, "Source");
        sourceNode.setBorder (new LineBorder (1));
        sourceNode.setOpaque (true);
        mainLayer.addChild (sourceNode);
        sourceNode.getActions ().addAction (action);
        sourceNode.getActions ().addAction (new MoveAction ());
        sourceNode.setPreferredLocation (new Point (50, 100));

        LabelWidget targetNode = new LabelWidget (scene, "Target");
        targetNode.setBorder (new LineBorder (1));
        targetNode.setOpaque (true);
        mainLayer.addChild (targetNode);
        targetNode.getActions ().addAction (action);
        targetNode.getActions ().addAction (new MoveAction ());
        targetNode.setPreferredLocation (new Point (350, 200));

        ConnectionWidget edge = new ConnectionWidget (scene);
        edge.setSourceAnchor (new DirectionalAnchor (sourceNode, DirectionalAnchor.Kind.HORIZONTAL));
        edge.setTargetAnchor (new DirectionalAnchor (targetNode, DirectionalAnchor.Kind.HORIZONTAL));
        edge.setRouter (new OrthogonalSearchRouter (new OrthogonalSearchRouter.WidgetsCollisionCollector (mainLayer)));
        connectionLayer.addChild (edge);

        LabelWidget label1 = new LabelWidget (scene, "Source Top Label");
        label1.setOpaque (true);
        edge.addChild (label1);
        edge.setConstraint (label1, ConnectionWidgetLayout.Alignment.TOP_RIGHT, 10);
        label1.getActions ().addAction (action);

        LabelWidget label2 = new LabelWidget (scene, "Movable Edge Center Label");
        label2.setOpaque (true);
        label2.getActions ().addAction (new MoveAction ());
        edge.addChild (label2);
        edge.setConstraint (label2, ConnectionWidgetLayout.Alignment.CENTER_RIGHT, 0.5f);
        label2.getActions ().addAction (action);

        LabelWidget label3 = new LabelWidget (scene, "Target Bottom Label");
        label3.setOpaque (true);
        edge.addChild (label3);
        edge.setConstraint (label3, ConnectionWidgetLayout.Alignment.BOTTOM_LEFT, -10);
        label3.getActions ().addAction (action);

        SceneSupport.show (scene);
    }

}
