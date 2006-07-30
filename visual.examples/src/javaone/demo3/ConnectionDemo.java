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

package javaone.demo3;

import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ConnectionDemo {

    public static void main (String[] args) {
        Scene scene = new Scene ();

        LayerWidget mainLayer = new LayerWidget (scene);
        scene.addChild(mainLayer);

        ImageWidget first = new ImageWidget (scene);
        first.setImage (Utilities.loadImage ("javaone/resources/a.png"));
        first.setPreferredLocation(new Point (100, 100));
        first.getActions().addAction(new MoveAction ());
        mainLayer.addChild(first);

        ImageWidget second = new ImageWidget (scene);
        second.setImage (Utilities.loadImage ("javaone/resources/b.png"));
        second.setPreferredLocation(new Point (300, 200));
        second.getActions().addAction(new MoveAction ());
        mainLayer.addChild(second);

        LayerWidget connectionLayer = new LayerWidget (scene);
        scene.addChild(connectionLayer);

        ConnectionWidget connection = new ConnectionWidget (scene);
        connection.setSourceAnchor(AnchorFactory.createCircularAnchor (first, 32));
        connection.setTargetAnchor(AnchorFactory.createCircularAnchor (second, 32));
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionLayer.addChild(connection);

        SceneSupport.show (scene.createView ());
    }

}
