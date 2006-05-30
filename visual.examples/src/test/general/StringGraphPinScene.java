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
package test.general;

import org.netbeans.api.visual.action.MouseHoverAction;
import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.PopupMenuAction;
import org.netbeans.api.visual.anchor.CenterAnchor;
import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.graph.NodeController;
import org.netbeans.api.visual.graph.PinController;
import org.netbeans.api.visual.uml.UMLClassWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public class StringGraphPinScene extends GraphPinScene<String, String, String, NodeController.StringNode, EdgeController.StringEdge, PinController.StringPin> {

    private Widget mainLayer;
    private Widget connectionLayer;

    private MoveAction moveAction = new MoveAction ();
    private MouseHoverAction mouseHoverAction = new StringGraphPinScene.MyMouseHoverAction ();
    private PopupMenuAction popupMenuAction = new StringGraphPinScene.MyPopupMenuAction ();

    public StringGraphPinScene () {
        mainLayer = new Widget (this);
        connectionLayer = new Widget (this);
        addChild (mainLayer);
        addChild (connectionLayer);

        getActions ().addAction (mouseHoverAction);
    }

    public Widget getMainLayer () {
        return mainLayer;
    }

    public Widget getConnectionLayer () {
        return connectionLayer;
    }

    protected NodeController.StringNode attachNodeController (String node) {
        UMLClassWidget widget = new UMLClassWidget (this);
        widget.setClassName ("Class" + node);
        mainLayer.addChild (widget);

        widget.getActions ().addAction (moveAction);
        widget.getActions ().addAction (mouseHoverAction);
        widget.getActions ().addAction (popupMenuAction);

        return new NodeController.StringNode (node, widget);
    }

    protected EdgeController.StringEdge attachEdgeController (String edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget (this);
        connectionLayer.addChild (connectionWidget);
        return new EdgeController.StringEdge (edge, connectionWidget);
    }

    protected PinController.StringPin attachPinController (NodeController.StringNode nodeController, String pin) {
        UMLClassWidget classWidget = ((UMLClassWidget) nodeController.getMainWidget ());
        if (pin.charAt (0) == '+') {
            Widget member = classWidget.createMember (pin.substring (1));
            classWidget.addMember (member);
            return new PinController.StringPin (pin, member);
        } else {
            Widget operation = classWidget.createOperation (pin.substring (1));
            classWidget.addOperation (operation);
            return new PinController.StringPin (pin, operation);
        }
    }

    protected void attachEdgeSource (EdgeController.StringEdge edgeController, PinController.StringPin sourcePinController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setSourceAnchor (new CenterAnchor (sourcePinController.getMainWidget ()));
    }

    protected void attachEdgeTarget (EdgeController.StringEdge edgeController, PinController.StringPin targetPinController) {
        ((ConnectionWidget) edgeController.getMainWidget ()).setTargetAnchor (new CenterAnchor (targetPinController.getMainWidget ()));
    }

    private static class MyMouseHoverAction extends MouseHoverAction.TwoStated {

        protected void unsetHovering (Widget widget) {
            widget.setBackground (Color.WHITE);
        }

        protected void setHovering (Widget widget) {
            widget.setBackground (Color.CYAN);
        }

    }

    private static class MyPopupMenuAction extends PopupMenuAction {

        public JPopupMenu getPopupMenu (Widget widget) {
            JPopupMenu popupMenu = new JPopupMenu ();
            popupMenu.add (new JMenuItem ("Open " + ((UMLClassWidget) widget).getClassName ()));
            return popupMenu;
        }

    }

}
