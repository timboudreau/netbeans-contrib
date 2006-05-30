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

import javaone.support.DemoSupport;
import org.netbeans.api.visual.action.PanAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.layout.SerialLayout;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * @author David Kaspar
 */
public class LODDemo {
    
    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions().addAction(new ZoomAction (1.1));
        scene.getActions().addAction(new PanAction ());
        
        scene.setLayout (new SerialLayout (SerialLayout.Orientation.VERTICAL));
        scene.addChild(new LabelWidget (scene, "Zoom inside the rectangle"));
        
        scene.addChild (new LODDemoWidget (scene, 5, 0.5));
        
        DemoSupport.show(scene.createView());
    }
    
}
