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
package test.justify;

import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.action.InplaceEditorAction;
import test.SceneSupport;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class JustifyTest extends Scene {

    public JustifyTest () {
        Widget r = creeateVerticalBox ();
        r.setMinimumBounds (new Rectangle (200, 200));
        addChild (r);

        Widget h1 = creeateHorizontalBox ();
        r.addChild (h1);
        Widget h2 = creeateHorizontalBox ();
        r.addChild (h2);

        Widget v11 = creeateVerticalBox ();
        h1.addChild (v11);
        Widget v12 = creeateVerticalBox ();
        h1.addChild (v12);

        Widget v21 = creeateVerticalBox ();
        h2.addChild (v21);
        Widget v22 = creeateVerticalBox ();
        h2.addChild (v22);

        v11.addChild (createLabel ("Hi"));
        v11.addChild (createLabel ("Privet"));
        v12.addChild (createLabel ("Cau"));
        v12.addChild (createLabel ("Caio"));
        v21.addChild (createLabel ("Good morning"));
        v21.addChild (createLabel ("Dobry den"));
        v22.addChild (createLabel ("Welcome"));
        v22.addChild (createLabel ("Vitejte"));
    }

    private LabelWidget createLabel (String text) {
        final LabelWidget label = new LabelWidget (this, text);
        label.setBorder (BorderFactory.createLineBorder (1, Color.RED));
        label.getActions ().addAction (new InplaceEditorAction.TextFieldEditor () {
            public String getText (Widget widget) {
                return label.getLabel ();
            }
            public void setText (Widget widget, String text) {
                label.setLabel (text);
            }
        });
        return label;
    }

    private Widget creeateVerticalBox () {
        Widget vbox = new Widget (this);
        vbox.setBorder (BorderFactory.createLineBorder (1, Color.GREEN));
        vbox.setLayout (LayoutFactory.createVerticalLayout ());
        return vbox;
    }

    private Widget creeateHorizontalBox () {
        Widget hbox = new Widget (this);
        hbox.setBorder (BorderFactory.createLineBorder (1, Color.BLUE));
        hbox.setLayout (LayoutFactory.createHorizontalLayout ());
        return hbox;
    }

    public static void main (String[] args) {
        SceneSupport.show (new JustifyTest ());
    }

}
