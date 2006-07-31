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

package javaone.demo1;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;
import test.SceneSupport;

/**
 * @author David Kaspar
 */
public class IconNodeWidget extends Widget {

    public IconNodeWidget(Scene scene, String icon, String label) {
        super (scene);

        setOpaque (true);
        setLayout (LayoutFactory.createVerticalLayout (LayoutFactory.SerialAlignment.CENTER, 4)); // use vertical layout

        addChild (new ImageWidget (scene, Utilities.loadImage (icon))); // add image sub-widget
        addChild (new LabelWidget (scene, label)); // add label sub-widget
    }

    public static void main (String[] args) {
        Scene scene = new Scene (); // create a scene

        IconNodeWidget iconNode = new IconNodeWidget (scene, "javaone/resources/netbeans_logo.gif", "Visual Library"); // create our icon node
        scene.addChild (iconNode); // add the icon node into scene

        SceneSupport.show (scene.createView ()); // create and show the view in JFrame
    }

}
