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

package javaone.demo6;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

/**
 * @author David Kaspar
 */
public class LODDemo {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions().addAction(ActionFactory.createZoomAction (1.2, false));
        scene.getActions().addAction(ActionFactory.createPanAction ());

        scene.setLayout (LayoutFactory.createVerticalLayout (LayoutFactory.SerialAlignment.LEFT_TOP, 0));
        scene.addChild(new LabelWidget (scene, "Zoom inside the rectangle"));

        scene.addChild (new LODDemoWidget (scene, 5, 0.5));

        SceneSupport.show(scene.createView());
    }

}
