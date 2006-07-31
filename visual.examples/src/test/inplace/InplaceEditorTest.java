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
package test.inplace;

import org.netbeans.api.visual.action.*;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author David Kaspar
 */
public class InplaceEditorTest extends GraphScene.StringGraph {

    private static final Image IMAGE = Utilities.loadImage ("test/resources/displayable_64.png"); // NOI18N

    private LayerWidget mainLayer;
    private WidgetAction editorAction;
    private boolean forceLayout;
    private WidgetAction moveAction = new MoveAction ();

    public InplaceEditorTest () {
        addChild (mainLayer = new LayerWidget (this));

        getActions ().addAction (new ZoomAction ());
        getActions ().addAction (new PanAction ());

        editorAction = new MyInplaceEditorAction ();

        forceLayout = true;
    }

    protected Widget attachNodeWidget (String node) {
        IconNodeWidget widget = new IconNodeWidget (this);
        widget.setImage (IMAGE);
        widget.setLabel (node);
        mainLayer.addChild (widget);

        widget.getActions ().addAction (createSelectAction ());
        widget.getActions ().addAction (createObjectHoverAction ());
        widget.getActions ().addAction (moveAction);
        widget.getLabelWidget ().getActions ().addAction (editorAction);

        return widget;
    }

    protected Widget attachEdgeWidget (String edge) {
        return null;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
    }

    protected void notifyValidated () {
        if (forceLayout) {
            SwingUtilities.invokeLater (new Runnable() {
                public void run () {
                    mainLayer.reevaluateLayout (LayoutFactory.createHorizontalLayout (), false);
                }
            });
            forceLayout = false;
        }
    }

    private class MyInplaceEditorAction extends InplaceEditorAction.TextFieldEditor {

        public State keyPressed (Widget widget, WidgetKeyEvent event) {
            if (! isEditorVisible ()) {
                if (event.getKeyCode () == KeyEvent.VK_F2) {
                    Object object = findObject (widget);
                    if (object != null  &&  getSelectedObjects ().contains (object))
                        if (openEditor (widget))
                            return State.createLocked (widget, this);
                }
            }

            return super.keyPressed (widget, event);
        }

        protected String getText (Widget widget) {
            return ((LabelWidget) widget).getLabel ();
        }

        protected void setText (Widget widget, String text) {
            ((LabelWidget) widget).setLabel (text);
        }

    }

    public static void main (String[] args) {
        InplaceEditorTest scene = new InplaceEditorTest ();
        scene.addNode ("double");
        scene.addNode ("click");
        scene.addNode ("on");
        scene.addNode ("a label");
        scene.addNode ("to edit");
        scene.addNode ("it");

        SceneSupport.show (scene);
    }

}
