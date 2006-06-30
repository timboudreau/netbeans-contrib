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
package test.swingborder;

import org.netbeans.api.visual.layout.SerialLayout;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.action.ZoomAction;
import test.SceneSupport;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class SwingBorderTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions ().addAction (new ZoomAction ());
        scene.setBackground (Color.LIGHT_GRAY);
        scene.setLayout (new SerialLayout (SerialLayout.Orientation.VERTICAL, SerialLayout.Alignment.JUSTIFY, 4));
        addLabel (scene, new EtchedBorder (EtchedBorder.LOWERED));
        addLabel (scene, new BevelBorder (BevelBorder.LOWERED));
        addLabel (scene, new TitledBorder ("Titled"));
        addLabel (scene, new TitledBorder (new EtchedBorder (EtchedBorder.LOWERED), "Titled with border"));
        SceneSupport.show (scene);
    }

    private static void addLabel (Scene scene, Border border) {
        Widget label = new LabelWidget (scene, border.getClass ().getName ());
        label.setBorder (border);
        scene.addChild (label);
    }

}
