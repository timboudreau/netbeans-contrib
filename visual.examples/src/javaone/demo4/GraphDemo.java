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

import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class GraphDemo {

    public static void main (String[] args) {
        DemoGraphScene scene = new DemoGraphScene ();

        String helloNodeID = "Hello";
        String worldNodeID = "World";
        String edge = "edge";

        Widget hello = scene.addNode (helloNodeID);
        Widget world = scene.addNode (worldNodeID);

        scene.addEdge (edge);

        scene.setEdgeSource(edge, helloNodeID);
        scene.setEdgeTarget(edge, worldNodeID);

        hello.setPreferredLocation (new Point (100, 100));
        world.setPreferredLocation (new Point (400, 200));

        SceneSupport.show(scene.createView());
    }

}
