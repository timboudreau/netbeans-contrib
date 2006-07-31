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
package test.component;

import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;
import test.SceneSupport;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class ComponentTest extends Scene {

    private final MoveAction moveAction = new MoveAction ();
    private int pos = 0;

    public ComponentTest () {
        getActions ().addAction (new ZoomAction ());

        LayerWidget layer = new LayerWidget (this);
        addChild (layer);

        layer.addChild (new LabelWidget (this, "Scroll mouse-wheel button to zoom"));

        layer.addChild (createMoveableComponent (new JLabel ("Swing JLabel component integrated")));
        layer.addChild (createMoveableComponent (new JComboBox (new String[] { "First", "Second", "Third" })));
        layer.addChild (createMoveableComponent (new JList (new String[] { "First", "Second", "Third" })));
    }

    private Widget createMoveableComponent (Component component) {
        Widget widget = new Widget (this);
        widget.setLayout (LayoutFactory.createVerticalLayout ());
        widget.setBorder (BorderFactory.createLineBorder ());
        widget.getActions ().addAction (moveAction);

        LabelWidget label = new LabelWidget (this, "Drag this to move widget");
        label.setOpaque (true);
        label.setBackground (Color.LIGHT_GRAY);
        widget.addChild (label);

        widget.addChild (new ComponentWidget (this, component));

        pos += 100;
        widget.setPreferredLocation (new Point (pos, pos));
        return widget;
    }

    public static void main (String[] args) {
        SceneSupport.show (new ComponentTest ());
    }

}
