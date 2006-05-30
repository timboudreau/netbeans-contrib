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

import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.layout.SerialLayout;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LevelOfDetailsWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * @author David Kaspar
 */
public class LODDemoWidget extends LevelOfDetailsWidget {

    private static final double ZOOM_MULT = 2.0;

    /** Creates a new instance of LODDemoWidget */
    public LODDemoWidget(Scene scene, int level, double zoom) {
        super (scene, zoom, zoom * ZOOM_MULT, Double.MAX_VALUE, Double.MAX_VALUE);

        setBorder (new LineBorder (2));

        if (level > 1) {
            Widget vbox = new Widget (scene);
            vbox.setLayout(new SerialLayout (SerialLayout.Orientation.VERTICAL, SerialLayout.Alignment.JUSTIFY, 1));
            addChild (vbox);

            Widget hbox1 = new Widget (scene);
            hbox1.setLayout(new SerialLayout (SerialLayout.Orientation.HORIZONTAL, SerialLayout.Alignment.JUSTIFY, 1));
            vbox.addChild(hbox1);

            Widget hbox2 = new Widget (scene);
            hbox2.setLayout(new SerialLayout (SerialLayout.Orientation.HORIZONTAL, SerialLayout.Alignment.JUSTIFY, 1));
            vbox.addChild(hbox2);

            hbox1.addChild(new LODDemoWidget (scene, level - 1, zoom * ZOOM_MULT));
            hbox1.addChild(new LODDemoWidget (scene, level - 1, zoom * ZOOM_MULT));

            hbox2.addChild(new LODDemoWidget (scene, level - 1, zoom * ZOOM_MULT));
            hbox2.addChild(new LODDemoWidget (scene, level - 1, zoom * ZOOM_MULT));
        } else {
            LabelWidget label = new LabelWidget (scene, "Item");
            label.setFont(scene.getDefaultFont().deriveFont (8.0f));
            addChild (label);
        }

    }

}
