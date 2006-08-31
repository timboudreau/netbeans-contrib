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
package test.convolve;

import org.netbeans.api.visual.widget.*;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * @author David Kaspar
 */
public class ConvolveTest extends Scene {

    Kernel blurKernel = new Kernel (5, 5, new float[]{
            0.00f, 0.00f, 0.05f, 0.00f, 0.00f,
            0.00f, 0.05f, 0.10f, 0.05f, 0.00f,
            0.05f, 0.10f, 0.20f, 0.10f, 0.05f,
            0.00f, 0.15f, 0.10f, 0.05f, 0.00f,
            0.00f, 0.00f, 0.05f, 0.00f, 0.00f,
    });

    Kernel dropShadowKernel = new Kernel (5, 5, new float[]{
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 1.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.30f,
    });

    private LayerWidget layer;

    public ConvolveTest () {
        layer = new LayerWidget (this);
        addChild (layer);

        createNormalWidget (50, 50, "This is normal Widget - no effect");
        createConvolveWidget (blurKernel, 100, 150, "This is ConvolveWidget - the image with label has to be blurred");
        createConvolveWidget (dropShadowKernel, 150, 250, "This is ConvolveWidget - the image with label has to be blurred");
    }

    private void createNormalWidget (int x, int y, String text) {
        Widget widget = new Widget (this);
        widget.setLayout (LayoutFactory.createVerticalLayout ());
        widget.setPreferredLocation (new Point (x, y));

        widget.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (widget);

        widget.addChild (new ImageWidget (this, Utilities.loadImage ("test/resources/displayable_64.png"))); // NOI18N
        widget.addChild (new LabelWidget (this, text));
    }

    private void createConvolveWidget (Kernel kernel, int x, int y, String text) {
        ConvolveWidget convolve = new ConvolveWidget (this, new ConvolveOp (kernel));
        convolve.setLayout (LayoutFactory.createVerticalLayout ());
        convolve.setPreferredLocation (new Point (x, y));

        convolve.getActions ().addAction (ActionFactory.createMoveAction ());
        layer.addChild (convolve);

        convolve.addChild (new ImageWidget (this, Utilities.loadImage ("test/resources/displayable_64.png"))); // NOI18N
        convolve.addChild (new LabelWidget (this, text));
    }

    public static void main (String[] args) {
        SceneSupport.show (new ConvolveTest ());
    }

}
