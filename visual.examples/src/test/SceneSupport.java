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
package test;

import org.netbeans.api.visual.widget.Scene;
import org.openide.util.RequestProcessor;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class SceneSupport {

    public static void show (final Scene scene) {
        JComponent sceneView = scene.getComponent ();
        if (sceneView == null)
            sceneView = scene.createView ();
        show (sceneView);
    }

    public static void show (final JComponent sceneView) {
        int width=800,height=600;
        JFrame frame = new JFrame ();//new JDialog (), true);
        JScrollPane panel = new JScrollPane (sceneView);
        panel.getHorizontalScrollBar ().setUnitIncrement (32);
        panel.getHorizontalScrollBar ().setBlockIncrement (256);
        panel.getVerticalScrollBar ().setUnitIncrement (32);
        panel.getVerticalScrollBar ().setBlockIncrement (256);
        frame.add (panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width-width)/2, (screenSize.height-height)/2, width, height);
        frame.setVisible (true);
    }

    public static int randInt (int max) {
        return (int) (Math.random () * max);
    }

    public static void invokeLater (final Runnable runnable, int delay) {
        RequestProcessor.getDefault ().post (new Runnable() {
            public void run () {
                SwingUtilities.invokeLater (runnable);
            }
        }, delay);
    }

}
