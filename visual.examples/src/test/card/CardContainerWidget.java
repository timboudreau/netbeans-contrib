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

import org.netbeans.api.visual.border.LineBorder;
import org.netbeans.api.visual.border.SwingBorder;
import org.netbeans.api.visual.layout.CardLayout;
import org.netbeans.api.visual.layout.SerialLayout;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.netbeans.api.visual.action.SwitchCardAction;
import org.netbeans.api.visual.action.MoveAction;
import org.openide.util.Utilities;
import test.SceneSupport;

import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class CardContainerWidget extends Widget {

    private Widget container;
    private CardLayout cardLayout;

    public CardContainerWidget (Scene scene) {
        super (scene);

        setLayout (new SerialLayout (SerialLayout.Orientation.VERTICAL));

        LabelWidget switchButton = new LabelWidget (scene, "Click me to switch card.");
        switchButton.setOpaque (true);
        switchButton.setBackground (Color.LIGHT_GRAY);
        switchButton.setBorder (new SwingBorder (scene, new BevelBorder (BevelBorder.RAISED)));
        addChild (switchButton);

        container = new Widget (scene);
        container.setBorder (new LineBorder (1));
        addChild (container);

        cardLayout = new CardLayout (container);
        container.setLayout (cardLayout);

        switchButton.getActions ().addAction (new SwitchCardAction (container));
    }

    public void addCard (Widget widget) {
        container.addChild (widget);
        if (cardLayout.getActiveChildWidget () == null)
            cardLayout.setActiveChildWidget (widget);
    }

    public static void main (String[] args) {
        Scene scene  = new Scene();

        LayerWidget layer = new LayerWidget (scene);
        scene.addChild (layer);

        CardContainerWidget container = new CardContainerWidget (scene);
        container.getActions ().addAction (new MoveAction ());
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
