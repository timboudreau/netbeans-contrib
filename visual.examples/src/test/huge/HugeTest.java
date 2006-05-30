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
package test.huge;

import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.NodeController;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class HugeTest {

    public static void main (String[] args) {
        RadialGraphScene scene = new RadialGraphScene ();
        createStructure (scene, 1, 10, 200, 400, 450);
        createStructure (scene, 2, 100, 1000, 1000, 1000);
        createStructure (scene, 3, 1000, 5000, 7500, 9500);

        SceneSupport.show (scene);
    }

    private static void createStructure (RadialGraphScene scene, int setID, int count, int centerX, int centerY, float maxRadius) {
        NodeController.StringNode rootNode = scene.addNode ("Root of Set no. " + setID);
        rootNode.getMainWidget ().setPreferredLocation (new Point (centerX, centerY));

        for (int index = 0; index < count; index ++) {
            NodeController.StringNode nodeController = scene.addNode ("Set no. " + setID + " - Child " + index);
            double radius = maxRadius * index / count;
            double angle = 2 * Math.PI * index / count;
            int x = (int) (centerX + radius * Math.cos (angle));
            int y = (int) (centerY + radius * Math.sin (angle));
            nodeController.getMainWidget ().setPreferredLocation (new Point (x, y));

            EdgeController.StringEdge edgeController = scene.addEdge ("Set no. " + setID + " - Edge " + index);
            scene.setEdgeSource (edgeController, rootNode);
            scene.setEdgeTarget (edgeController, nodeController);
        }
    }

}
