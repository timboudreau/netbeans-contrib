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
package test.custom;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class CustomWidgetTest extends Widget {

    private double radius;

    public CustomWidgetTest (Scene scene, double radius) {
        super (scene);
        this.radius = radius;
    }

    protected Rectangle calculateClientArea () {
        int r = (int) Math.ceil (radius);
        return new Rectangle (- r, - r, 2 * r + 1, 2 * r + 1);
    }

    protected void paintWidget () {
        int r = (int) Math.ceil (radius);
        Graphics2D g = getGraphics ();
        g.setColor (getForeground ());
        g.drawOval (- r, - r, 2 * r, 2 * r);
    }

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.addChild (new CustomWidgetTest (scene, 10));
        SceneSupport.show (scene);
    }

}
