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
package test.graphlayout;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.graph.layout.TreeGraphLayout;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;
import test.general.StringGraphScene;

/**
 * @author David Kaspar
 */
public class TreeGraphLayoutTest extends StringGraphScene {

    public TreeGraphLayoutTest () {
        getActions ().addAction (ActionFactory.createEditAction (new EditProvider() {
            public void edit (Widget widget) {
                new TreeGraphLayout<String, String> (TreeGraphLayoutTest.this, 100, 100, 50, 50, true).layout ("root");
            }
        }));
    }

    public static void main (String[] args) {
        TreeGraphLayoutTest scene = new TreeGraphLayoutTest ();

        scene.addNode ("root");
        scene.addNode ("n1");
        scene.addNode ("n2");

        scene.addEdge ("e1");
        scene.setEdgeSource ("e1", "root");
        scene.setEdgeTarget ("e1", "n1");

        scene.addEdge ("e2");
        scene.setEdgeSource ("e2", "root");
        scene.setEdgeTarget ("e2", "n2");

        SceneSupport.show (scene);
    }

}
