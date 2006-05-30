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

package javaone.demo2;

import javaone.support.DemoSupport;
import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class ActionDemo {
    
    public static void main (String[] args) {
        Scene scene = new Scene ();

        Widget layer = new Widget (scene);
        scene.addChild(layer);
        
        LabelWidget hello1 = createLabel (scene, "Hello", 100, 100);
        layer.addChild (hello1);
        LabelWidget hello2 = createLabel (scene, "NetBeans", 300, 200);
        layer.addChild (hello2);

        scene.getActions().addAction (new ZoomAction ());
        scene.getActions().addAction (new PanAction ());

        hello1.getActions().addAction (new MoveAction ());
        hello2.getActions().addAction (new MoveAction ());
        
        MyHoverAction hoverAction = new MyHoverAction ();
        scene.getActions().addAction (hoverAction);
        hello1.getActions().addAction (hoverAction);
        hello2.getActions().addAction (hoverAction);
        
        hello1.getActions().addAction(new MyPopupAction ());
        hello2.getActions().addAction(new MyPopupAction ());
        
        DemoSupport.show (scene.createView());
    }
    
    private static LabelWidget createLabel (Scene scene, String text, int x, int y) {
        LabelWidget widget = new LabelWidget (scene, text);
        widget.setFont(scene.getDefaultFont().deriveFont(24.0f));
        widget.setOpaque(true);
        widget.setPreferredLocation (new Point (x, y));
        return widget;
    }
    
    private static class MyHoverAction extends MouseHoverAction.TwoStated {
        
        protected void unsetHovering(Widget widget) {
            if (widget != null) {
                widget.setBackground (Color.WHITE);
                widget.setForeground (Color.BLACK);
            }
        }

        protected void setHovering(Widget widget) {
            if (widget != null) {
                widget.setBackground (new Color (52, 124, 150));
                widget.setForeground (Color.WHITE);
            }
        }
        
    }
    
    private static class MyPopupAction extends PopupMenuAction {

        public JPopupMenu getPopupMenu(Widget widget) {
            JPopupMenu menu = new JPopupMenu ();
            menu.add("Open");
            return menu;
        }
        
    }
    
}
