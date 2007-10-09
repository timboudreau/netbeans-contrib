/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package test.swingborder;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;
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

    private static org.netbeans.api.visual.border.Border BORDER_SHADOW_NORMAL = BorderFactory.createImageBorder (new Insets (6, 6, 6, 6), Utilities.loadImage ("test/resources/shadow_normal.png")); // NOI18N

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.getActions ().addAction (ActionFactory.createZoomAction ());
        scene.setBackground (Color.LIGHT_GRAY);
        scene.setLayout (LayoutFactory.createVerticalFlowLayout (LayoutFactory.SerialAlignment.LEFT_TOP, 4));
        addLabel (scene, new EtchedBorder (EtchedBorder.LOWERED));
        addLabel (scene, new BevelBorder (BevelBorder.LOWERED));
        addLabel (scene, new TitledBorder ("Titled"));
        addLabel (scene, new TitledBorder (new EtchedBorder (EtchedBorder.LOWERED), "Titled with border"));

        // using SwingBorder together with CompositeBorder
        // scene.createView has to be called because JComponent has to be created before using SwingBorder inside CompositeBorder
        // otherwise the SwingBorder insets would not be resolved correctly
        scene.createView ();
        Widget label = new LabelWidget (scene, "Composite");
        label.setBorder (BorderFactory.createCompositeBorder (BORDER_SHADOW_NORMAL, BorderFactory.createSwingBorder (scene, new TitledBorder (new EtchedBorder (EtchedBorder.LOWERED), "Composite")), BORDER_SHADOW_NORMAL));
        scene.addChild (label);

        SceneSupport.show (scene);
    }

    private static void addLabel (Scene scene, Border border) {
        Widget label = new LabelWidget (scene, border.getClass ().getName ());
        label.setBorder (border);
        scene.addChild (label);
    }

}
