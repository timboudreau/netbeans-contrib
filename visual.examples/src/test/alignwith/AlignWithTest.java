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
package test.alignwith;

import org.netbeans.api.visual.action.AlignWithMoveAction;
import org.netbeans.api.visual.action.InplaceEditorAction;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.SelectAction;
import org.netbeans.api.visual.border.SwingBorder;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import test.SceneSupport;

import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class AlignWithTest extends Scene {

    private LayerWidget mainLayer;
    private WidgetAction moveAction;
    private WidgetAction renameAction;

    public AlignWithTest () {
        setBackground (Color.WHITE);

        mainLayer = new LayerWidget (this);
        addChild (mainLayer);

        LayerWidget interractionLayer = new LayerWidget (this);
        addChild (interractionLayer);

        moveAction = new AlignWithMoveAction (mainLayer, interractionLayer);
        renameAction = new RenameAction ();

        getActions ().addAction (new CreateAction ());

        createLabel ("Click on the scene to create a new label", new Point (10, 10));
        createLabel ("Drag a label to move it and try to align it with other ones", new Point (10, 30));
    }

    private void createLabel (String label, Point location) {
        Scene scene = mainLayer.getScene ();
        Widget widget = new LabelWidget (scene, label);

        widget.setOpaque (true);
        widget.setBackground (Color.LIGHT_GRAY);
        widget.setBorder (new SwingBorder (scene, new BevelBorder (BevelBorder.RAISED)));
        widget.setPreferredLocation (location);

        widget.getActions ().addAction (moveAction);
        widget.getActions ().addAction (renameAction);

        mainLayer.addChild (widget);
    }

    private class CreateAction extends SelectAction {

        public void doSelect (Widget widget, Point localLocation) {
            createLabel ("Double-click to rename me", localLocation);
        }

    }

    private static class RenameAction extends InplaceEditorAction.TextFieldEditor {

        protected String getText (Widget widget) {
            return ((LabelWidget) widget).getLabel ();
        }

        protected void setText (Widget widget, String text) {
            ((LabelWidget) widget).setLabel (text);
        }

    }

    public static void main (String[] args) {
        SceneSupport.show (new AlignWithTest ());
    }

}
