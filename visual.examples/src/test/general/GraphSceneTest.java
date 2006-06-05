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
package test.general;

import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.graph.PinController;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class GraphSceneTest {

    public static void main (String[] args) {
        testGraphScene ();
    }

    public static void testLabelWidget () {
        Scene scene = new Scene ();
        scene.setBackground (Color.BLUE);

        LabelWidget label = new LabelWidget (scene);
        label.setOpaque (true);
        label.setBackground (Color.GREEN);
        label.setLabel ("ABCD");
        scene.addChild (label);

        SceneSupport.show (scene);
    }

    public static void testUMLClassWidget () {
        StringGraphScene scene = new StringGraphScene ();

        for (int a = 1; a <= 100; a ++)
            scene.addNode (String.valueOf(a)).getMainWidget ().setPreferredLocation (new Point (a * 10, a * 10));

        SceneSupport.show (scene);
    }

    private static EdgeController.StringEdge createConnection (StringGraphScene scene, String edgeID, NodeController.StringNode node1, NodeController.StringNode node2) {
        EdgeController.StringEdge edge = scene.addEdge (edgeID);
        scene.setEdgeSource (edge, node1);
        scene.setEdgeTarget (edge, node2);
        return edge;
    }

    public static void testConnectionWidget () {
        StringGraphScene scene = new StringGraphScene ();

        NodeController.StringNode node1 = scene.addNode ("1");
        node1.getMainWidget ().setPreferredLocation (new Point (100, 100));
        NodeController.StringNode node2 = scene.addNode ("2");
        node2.getMainWidget ().setPreferredLocation (new Point (400, 400));
        NodeController.StringNode node3 = scene.addNode ("3");
        node3.getMainWidget ().setPreferredLocation (new Point (500, 100));

        createConnection (scene, "A", node1, node2);
        createConnection (scene, "B", node2, node3);
        createConnection (scene, "C", node3, node1);

        SceneSupport.show (scene);
    }

    public static void testConnectionAnimation () {
        StringGraphScene scene = new StringGraphScene ();

        for (int a = 0; a < 3; a ++) {
            NodeController.StringNode aNode = scene.addNode ("A" + a);
            aNode.getMainWidget ().setPreferredLocation (new Point (100, 100));
            NodeController.StringNode bNode = scene.addNode ("B" + a);
            bNode.getMainWidget ().setPreferredLocation (new Point (400, 400));

            createConnection (scene, "C" + a, aNode, bNode);
        }

        SceneSupport.startAnimation (scene, scene.getMainLayer (), 100);
        SceneSupport.show (scene);
    }

    public static void testAnimation () {
        StringGraphScene scene = new StringGraphScene ();

        for (int a = 1; a <= 100; a ++)
            scene.addNode (String.valueOf(a)).getMainWidget ().setPreferredLocation (new Point (a * 10, a * 10));

        SceneSupport.startAnimation (scene, scene.getMainLayer (), 0);
        SceneSupport.show (scene);
    }

    public static void testAddRemove () {
        StringGraphScene scene = new StringGraphScene ();

        SceneSupport.startAddRemove (scene, 500);
        SceneSupport.show (scene);
    }

    public static void testGraphScene () {
        final StringGraphScene scene = new StringGraphScene ();

        NodeController.StringNode previousNodeController = null;
        for (int a = 0; a < 10; a ++) {
            NodeController.StringNode nodeController = scene.addNode (String.valueOf(a));
            nodeController.getMainWidget ().setPreferredLocation (new Point (SceneSupport.randInt (1000), SceneSupport.randInt (1000)));
            if (previousNodeController != null) {
                EdgeController.StringEdge edgeController = scene.addEdge (String.valueOf (a));
                scene.setEdgeSource (edgeController, previousNodeController);
                scene.setEdgeTarget (edgeController, nodeController);
            }
            previousNodeController = nodeController;
        }

        SceneSupport.show (scene);
    }

    public static void testGraphPinScene () {
        final StringGraphPinScene scene = new StringGraphPinScene ();

        NodeController.StringNode rootNode = scene.addNode ("Root");
        rootNode.getMainWidget ().setPreferredLocation (new Point (30, 500));

        for (int a = 0; a < 10; a ++) {
            PinController.StringPin rootPin = scene.addPin (rootNode, "+Pin" + a);

            NodeController.StringNode childNode = scene.addNode ("Child" + a);
            childNode.getMainWidget ().setPreferredLocation (new Point (500, a * 100));
            PinController.StringPin childPin = scene.addPin (childNode, "-Pin" + a);

            EdgeController.StringEdge edgeController = scene.addEdge (String.valueOf (a));
            scene.setEdgeSource (edgeController, rootPin);
            scene.setEdgeTarget (edgeController, childPin);
        }

        SceneSupport.show (scene);
    }

}
