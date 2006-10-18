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
package test.controlpoint;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.router.RouterFactory;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class AddRemoveControlPointTest extends Scene {

    private LayerWidget mainLayer;

    public AddRemoveControlPointTest () {
        mainLayer = new LayerWidget (this);
        addChild (mainLayer);
        LayerWidget connLayer = new LayerWidget (this);
        addChild (connLayer);

        addLabel ("Double-click on the connection to create a control point", 10, 30);
        addLabel ("Drag a control point to move it", 10, 60);
        addLabel ("Double-click on a control point to delete it", 10, 90);

        LabelWidget hello1 = addLabel ("Hello", 100, 150);
        LabelWidget hello2 = addLabel ("NetBeans", 300, 250);

        ConnectionWidget conn = new ConnectionWidget (this);
        conn.setPaintControlPoints (true);
        conn.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
        conn.setRouter (RouterFactory.createFreeRouter ());
        conn.setSourceAnchor (AnchorFactory.createFreeRectangularAnchor (hello1, true));
        conn.setTargetAnchor (AnchorFactory.createFreeRectangularAnchor (hello2, true));
        connLayer.addChild (conn);

        conn.getActions ().addAction (ActionFactory.createAddRemoveControlPointAction ());
        conn.getActions ().addAction (ActionFactory.createFreeMoveControlPointAction ());
    }

    private LabelWidget addLabel (String text, int x, int y) {
        LabelWidget widget = new LabelWidget (this, text);

        widget.setFont(getDefaultFont().deriveFont(24.0f));
        widget.setOpaque(true);
        widget.setPreferredLocation (new Point (x, y));

        widget.getActions ().addAction (ActionFactory.createMoveAction ());

        mainLayer.addChild (widget);

        return widget;
    }

    public static void main (String[] args) {
        SceneSupport.show (new AddRemoveControlPointTest ().createView ());
    }

}
