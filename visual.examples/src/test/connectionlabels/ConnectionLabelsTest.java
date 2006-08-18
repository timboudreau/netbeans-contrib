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

import org.netbeans.api.visual.action.MouseHoverAction;
import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.router.RouterFactory;
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
        WidgetAction action = new MyHoverAction (scene);
        scene.getActions ().addAction (action);

        LabelWidget sourceNode = new LabelWidget (scene, "Source");
        sourceNode.setBorder (BorderFactory.createLineBorder ());
        sourceNode.setOpaque (true);
        mainLayer.addChild (sourceNode);
        sourceNode.getActions ().addAction (action);
        sourceNode.getActions ().addAction (new MoveAction ());
        sourceNode.setPreferredLocation (new Point (50, 100));

        LabelWidget targetNode = new LabelWidget (scene, "Target");
        targetNode.setBorder (BorderFactory.createLineBorder ());
        targetNode.setOpaque (true);
        mainLayer.addChild (targetNode);
        targetNode.getActions ().addAction (action);
        targetNode.getActions ().addAction (new MoveAction ());
        targetNode.setPreferredLocation (new Point (350, 200));

        ConnectionWidget edge = new ConnectionWidget (scene);
        edge.setSourceAnchor (AnchorFactory.createDirectionalAnchor (sourceNode, AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
        edge.setTargetAnchor (AnchorFactory.createDirectionalAnchor (targetNode, AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
        edge.setRouter (RouterFactory.createOrthogonalSearchRouter (mainLayer));
        connectionLayer.addChild (edge);

        LabelWidget label1 = new LabelWidget (scene, "Source Top Label");
        label1.setOpaque (true);
        edge.addChild (label1);
        edge.setConstraint (label1, LayoutFactory.ConnectionWidgetLayoutAlignment.TOP_RIGHT, 10);
        label1.getActions ().addAction (action);

        LabelWidget label2 = new LabelWidget (scene, "Movable Edge Center Label");
        label2.setOpaque (true);
        label2.getActions ().addAction (new MoveAction ());
        edge.addChild (label2);
        edge.setConstraint (label2, LayoutFactory.ConnectionWidgetLayoutAlignment.CENTER_RIGHT, 0.5f);
        label2.getActions ().addAction (action);

        LabelWidget label3 = new LabelWidget (scene, "Target Bottom Label");
        label3.setOpaque (true);
        edge.addChild (label3);
        edge.setConstraint (label3, LayoutFactory.ConnectionWidgetLayoutAlignment.BOTTOM_LEFT, -10);
        label3.getActions ().addAction (action);

        SceneSupport.show (scene);
    }

    private static class MyHoverAction extends MouseHoverAction.TwoStated {

        private Scene scene;

        public MyHoverAction (Scene scene) {
            this.scene = scene;
        }

        protected void unsetHovering (Widget widget) {
            if (widget != null) {
                widget.setBackground (scene.getLookFeel ().getBackground (ObjectState.NORMAL));
                widget.setForeground (scene.getLookFeel ().getForeground (ObjectState.NORMAL));
            }
        }

        protected void setHovering (Widget widget) {
            if (widget != null) {
                ObjectState state = ObjectState.NORMAL.deriveSelected (true);
                widget.setBackground (scene.getLookFeel ().getBackground (state));
                widget.setForeground (scene.getLookFeel ().getForeground (state));
            }
        }
    }

}
