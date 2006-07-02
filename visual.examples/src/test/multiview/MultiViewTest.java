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
package test.multiview;

import test.object.ObjectTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author David Kaspar
 */
public class MultiViewTest {

    public static void main (String[] args) {
        final ObjectTest scene = new ObjectTest ();
        scene.addNode ("form [Form]");
        scene.addNode ("list [List]");
        scene.addNode ("canvas [Canvas]");
        scene.addNode ("alert [Alert]");
        scene.moveTo (null);

        int width = 800, height = 600;
        JFrame frame = new JFrame ();//new JDialog (), true);
        Container contentPane = frame.getContentPane ();
        contentPane.setLayout (new BorderLayout ());

        JComponent sceneView = scene.createView ();

        JScrollPane panel = new JScrollPane (sceneView);
        panel.getHorizontalScrollBar ().setUnitIncrement (32);
        panel.getHorizontalScrollBar ().setBlockIncrement (256);
        panel.getVerticalScrollBar ().setUnitIncrement (32);
        panel.getVerticalScrollBar ().setBlockIncrement (256);
        contentPane.add (panel, BorderLayout.CENTER);

//        contentPane.add (scene.createSateliteView (), BorderLayout.NORTH);
//        contentPane.add (scene.createSateliteView (), BorderLayout.SOUTH);
        contentPane.add (scene.createSateliteView (), BorderLayout.WEST);
//        contentPane.add (scene.createSateliteView (), BorderLayout.EAST);

        final JButton button = new JButton ("Preview");
        button.addActionListener (new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                JPopupMenu popup = new JPopupMenu ();
                popup.setLayout (new BorderLayout ());
                JComponent sateliteView = scene.createSateliteView ();
                popup.add (sateliteView, BorderLayout.CENTER);
                popup.show (button, (button.getSize ().width - sateliteView.getPreferredSize ().width) / 2, button.getSize ().height);
            }
        });
        contentPane.add (button, BorderLayout.NORTH);


        frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit ().getScreenSize ();
        frame.setBounds ((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        frame.setVisible (true);
    }

}
