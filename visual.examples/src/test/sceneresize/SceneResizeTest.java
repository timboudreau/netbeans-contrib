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
package test.sceneresize;

import org.netbeans.api.visual.widget.Scene;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class SceneResizeTest {

    private static int counter = 0;

    public static void main (String[] args) {
        final Scene scene = new Scene ();
        scene.addSceneListener (new Scene.SceneListener() {
            public void sceneRepaint () {
                System.out.println ("Scene Repaint: Scene: " + scene.getBounds () + " - View: " + scene.getView ().getBounds ());
            }
            public void sceneValidating () {
                counter = (counter + 64) & 255;
                scene.setBackground (new Color (counter, counter, counter));
                System.out.println ("Scene Validating: Scene: " + scene.getBounds () + " - View: " + scene.getView ().getBounds ());
                Thread.dumpStack ();
            }
            public void sceneValidated () {
                System.out.println ("Scene Validated: Scene: " + scene.getBounds () + " - View: " + scene.getView ().getBounds ());
            }
        });
        scene.setOpaque (true);
        scene.setBackground (Color.GREEN);

        int width = 800, height = 600;
        JFrame frame = new JFrame ();//new JDialog (), true);
        frame.add (scene.createView (), BorderLayout.CENTER);
        frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit ().getScreenSize ();
        frame.setBounds ((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        frame.setVisible (true);

//        SceneSupport.show (scene);
    }

}
