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
package test.lod;

import org.netbeans.api.visual.action.MouseHoverAction;
import org.netbeans.api.visual.action.PanAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.layout.SerialLayout;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LevelOfDetailsWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.border.BorderFactory;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class LevelOfDetailsTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.setZoomFactor(0.2);
        scene.setLayout (new SerialLayout (SerialLayout.Orientation.VERTICAL));
        scene.getActions().addAction(new ZoomAction (1.1));
        scene.getActions().addAction(new PanAction ());
        MouseHoverAction hover = new MouseHoverAction.TwoStated () {
            protected void setHovering (Widget widget) { widget.setOpaque(true); widget.setBackground (Color.GREEN); }
            protected void unsetHovering (Widget widget) { widget.setOpaque(false); widget.setBackground (Color.WHITE); }
        };
        scene.getActions().addAction(hover);

        scene.addChild(createLabel (scene, "Use mouse-wheel for zooming, use middle button for panning.", 72));
        scene.addChild(createLabel (scene, "For more details zoom into the rectangle below.", 72));
        
        Widget root = new LevelOfDetailsWidget (scene, 0.21, 0.3, Double.MAX_VALUE, Double.MAX_VALUE);
        root.setBorder (BorderFactory.createLineBorder (10));
        root.setLayout (new SerialLayout (SerialLayout.Orientation.VERTICAL, SerialLayout.Alignment.JUSTIFY, 4));
        scene.addChild (root);
        
        for (int a = 0; a < 10; a ++) {
            root.addChild(createLabel (scene, "Row: " + a, 36));
            
            Widget row = new LevelOfDetailsWidget (scene, 0.3, 0.5, Double.MAX_VALUE, Double.MAX_VALUE);
            row.setBorder(BorderFactory.createLineBorder (4));
            row.setLayout (new SerialLayout (SerialLayout.Orientation.HORIZONTAL, SerialLayout.Alignment.JUSTIFY, 4));
            row.getActions().addAction(hover);
            root.addChild (row);
            
            for (int b = 0; b < 20; b ++) {
                Widget item = new LevelOfDetailsWidget (scene, 0.5, 1.0, Double.MAX_VALUE, Double.MAX_VALUE);
                item.setBorder (BorderFactory.createLineBorder (2));
                item.addChild (createLabel (scene, "Item-" + a + "," + b, 18));
                item.getActions().addAction(hover);
                row.addChild(item);
            }
        }
        
        SceneSupport.show (scene);
    }
    
    private static Widget createLabel (Scene scene, String text, int size) {
        LabelWidget label = new LabelWidget (scene, text);
        label.setFont(scene.getDefaultFont().deriveFont((float) size));
        return label;
    }
    
}
