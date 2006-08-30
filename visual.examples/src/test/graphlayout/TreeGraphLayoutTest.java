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

import test.general.StringGraphScene;
import test.SceneSupport;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.graph.layout.TreeGraphLayout;

/**
 * @author David Kaspar
 */
public class TreeGraphLayoutTest extends StringGraphScene {

    public TreeGraphLayoutTest () {
        getActions ().addAction (new WidgetAction.Adapter () {
            public State mouseClicked (Widget widget, WidgetMouseEvent event) {
                if (event.getClickCount () == 2) {
                    new TreeGraphLayout<String,String> (TreeGraphLayoutTest.this, 100, 100, 50, 50, true).layout ("root");
                    return State.CONSUMED;
                }
                return super.mouseClicked (widget, event);
            }
        });
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
