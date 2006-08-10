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
package test.animator;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class ColorAnimatorTest {

    public static void main (String[] args) {
        Scene scene = new Scene ();
        scene.setLayout (LayoutFactory.createVerticalLayout ());

        for (int a = 0; a < 10; a ++)
            scene.addChild (new MyLabelWidget (scene));

        SceneSupport.show (scene);
    }

    private static final class MyLabelWidget extends LabelWidget {

        public MyLabelWidget (Scene scene) {
            super (scene, "Move mouse over the label to see animation");
            setOpaque (true);
            setBackground (Color.WHITE);
            setForeground (Color.BLACK);
            getActions ().addAction (scene.createWidgetHoverAction ());
        }

        protected void notifyStateChanged (ObjectState previousState, ObjectState state) {
            if (previousState.isHovered ()  == state.isHovered ())
                return;
            getScene ().getSceneAnimator ().animateBackgroundColor (this, state.isHovered () ? Color.BLUE : Color.WHITE);
            getScene ().getSceneAnimator ().animateForegroundColor (this, state.isHovered () ? Color.YELLOW : Color.BLACK);
        }

    }

}
