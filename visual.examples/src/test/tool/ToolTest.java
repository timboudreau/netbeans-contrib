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
package test.tool;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.action.*;
import test.SceneSupport;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class ToolTest extends Scene {

    private static final String ACTION_SCENE = "scene"; // NOI18N
    private static final String ACTION_MOVE = "move"; // NOI18N

    public ToolTest () {
        LayerWidget layer = new LayerWidget (this);
        addChild (layer);

        LabelWidget label = new LabelWidget (this, "Right-click to open popup menu and select an active tool.");
        label.setPreferredLocation (new Point (100, 100));
        layer.addChild (label);

        WidgetAction popup = ActionFactory.createPopupMenuAction (new MyPopupProvider ());

        getActions ().addAction (popup);

        createActions (ACTION_SCENE).addAction (popup);
        createActions (ACTION_SCENE).addAction (ActionFactory.createZoomAction ());
        createActions (ACTION_SCENE).addAction (ActionFactory.createPanAction ());

        createActions (ACTION_MOVE).addAction (popup);
        label.createActions (ACTION_MOVE).addAction (ActionFactory.createMoveAction ());
    }

    private final class MyPopupProvider implements PopupMenuProvider, ActionListener {

        private JPopupMenu menu;

        public MyPopupProvider () {
            menu = new JPopupMenu ("Popup menu");
            JMenuItem item;

            item = new JMenuItem ("Activate Scene tool - zoom/pan the scene");
            item.setActionCommand (ACTION_SCENE);
            item.addActionListener (this);
            menu.add (item);

            item = new JMenuItem ("Activate Move tool - move the label");
            item.setActionCommand (ACTION_MOVE);
            item.addActionListener (this);
            menu.add (item);

        }

        public JPopupMenu getPopupMenu (Widget widget, Point localLocation) {
            return menu;
        }

        public void actionPerformed (ActionEvent e) {
            setActiveTool (e.getActionCommand ());
        }

    }

    public static void main (String[] args) {
        SceneSupport.show (new ToolTest ());
    }

}
