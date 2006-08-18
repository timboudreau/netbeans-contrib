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
package test.card;

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class CardContainerWidget extends Widget {

    private Widget container;

    public CardContainerWidget (Scene scene) {
        super (scene);

        setLayout (LayoutFactory.createVerticalLayout ());

        LabelWidget switchButton = new LabelWidget (scene, "Click me to switch card.");
        switchButton.setOpaque (true);
        switchButton.setBackground (Color.LIGHT_GRAY);
        switchButton.setBorder (BorderFactory.createBevelBorder (true));
        addChild (switchButton);

        container = new Widget (scene);
        container.setBorder (BorderFactory.createLineBorder ());
        addChild (container);

        container.setLayout (LayoutFactory.createCardLayout (container));

        switchButton.getActions ().addAction (ActionFactory.createSwitchCardAction (container));
    }

    public void addCard (Widget widget) {
        container.addChild (widget);
        if (LayoutFactory.getActiveCard (container) == null)
            LayoutFactory.setActiveCard (container, widget);
    }

    public static void main (String[] args) {
        Scene scene  = new Scene();
        scene.getActions ().addAction (ActionFactory.createZoomAction ());
        scene.getActions ().addAction (ActionFactory.createPanAction ());

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        CardContainerWidget container = new CardContainerWidget (scene);
        container.getActions ().addAction (ActionFactory.createMoveAction ());
        container.setPreferredLocation (new Point (100, 100));
        layer.addChild (container);

        Widget card1 = new LabelWidget (scene, "This is the first card. Drag me to to move me.");
        container.addCard (card1);

        IconNodeWidget card2 = new IconNodeWidget (scene);
        card2.setLabel ("This is the second card. Drag me to to move me.");
        card2.setImage (Utilities.loadImage ("test/resources/displayable_64.png"));
        container.addCard (card2);

        SceneSupport.show (scene);
    }

}
