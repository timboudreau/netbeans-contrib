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

package javaone.demo4;

import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.NodeController;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class GraphDemo {

    public static void main (String[] args) {
        DemoGraphScene scene = new DemoGraphScene ();

        NodeController.StringNode hello = scene.addNode ("Hello");
        NodeController.StringNode world = scene.addNode ("World");

        EdgeController.StringEdge edge = scene.addEdge ("edge");

        scene.setEdgeSource(edge, hello);
        scene.setEdgeTarget(edge, world);

        hello.getMainWidget().setPreferredLocation(new Point (100, 100));
        world.getMainWidget().setPreferredLocation(new Point (400, 200));

        SceneSupport.show(scene.createView());
    }

}
