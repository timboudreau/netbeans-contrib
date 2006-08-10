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
package test.scroll;

import org.netbeans.api.visual.action.ResizeAction;
import org.netbeans.api.visual.action.ZoomAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class SwingScrollTest extends Scene {

    public SwingScrollTest () {
        getActions ().addAction (new ZoomAction ());
        LayerWidget layer = new LayerWidget (this);
        addChild (layer);

        SwingScrollWidget scroll = new SwingScrollWidget (this);
        scroll.setBorder (BorderFactory.createResizeBorder (8, Color.BLUE, false));
        scroll.setPreferredLocation (new Point (50, 50));
        scroll.setMinimalSize (new Dimension (100, 200));
        scroll.setMaximalSize (new Dimension (500, 500));
        layer.addChild (scroll);

        Widget view = new Widget (this);
        view.setLayout (LayoutFactory.createVerticalLayout ());

        view.addChild (new LabelWidget (this, "Shrink the area for showing scroll bars"));
        view.addChild (new LabelWidget (this, "Drag scroll bars to move the view"));
        view.addChild (new LabelWidget (this, "Click on arrow and slider to perform unit and block scroll of the view"));
        view.addChild (new SeparatorWidget (this, SeparatorWidget.Orientation.HORIZONTAL));
        view.addChild (new ImageWidget (this, Utilities.loadImage ("test/resources/displayable_64.png")));
        view.addChild (new LabelWidget (this, "Long Long Long Long Long Long Label 1"));
        view.addChild (new LabelWidget (this, "Label 1"));
        view.addChild (new LabelWidget (this, "Label 2"));
        view.addChild (new LabelWidget (this, "Label 3"));
        view.addChild (new LabelWidget (this, "Label 4"));
        view.addChild (new LabelWidget (this, "Label 5"));
        view.addChild (new LabelWidget (this, "Long Long Long Long Long Long Label 5"));
        view.addChild (new LabelWidget (this, "Label 6"));
        view.addChild (new LabelWidget (this, "Label 7"));
        view.addChild (new LabelWidget (this, "Label 8"));
        view.addChild (new LabelWidget (this, "Label 9"));
        view.addChild (new LabelWidget (this, "Label 0"));
        view.addChild (new LabelWidget (this, "Long Long Long Long Long Long Label 0"));

        scroll.setView (view);

        scroll.getActions ().addAction (new ResizeAction ());
    }

    public static void main (String[] args) {
        SceneSupport.show (new SwingScrollTest ());
    }

}
