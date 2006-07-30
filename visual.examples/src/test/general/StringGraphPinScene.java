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

import org.netbeans.api.visual.action.MoveAction;
import org.netbeans.api.visual.action.PopupMenuAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.uml.UMLClassWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;

/**
 * @author David Kaspar
 */
public class StringGraphPinScene extends GraphPinScene.StringGraph {

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    private MoveAction moveAction = new MoveAction ();
    private PopupMenuAction popupMenuAction = new StringGraphPinScene.MyPopupMenuAction ();

    public StringGraphPinScene () {
        mainLayer = new LayerWidget (this);
        connectionLayer = new LayerWidget (this);
        addChild (mainLayer);
        addChild (connectionLayer);
    }

    protected Widget attachNodeWidget (String node) {
        UMLClassWidget widget = new UMLClassWidget (this);
        widget.setClassName ("Class " + node);
        mainLayer.addChild (widget);

        widget.getActions ().addAction (moveAction);
        widget.getActions ().addAction (createObjectHoverAction ());
        widget.getActions ().addAction (popupMenuAction);

        return widget;
    }

    protected Widget attachPinWidget (String node, String pin) {
        UMLClassWidget classWidget = ((UMLClassWidget) findWidget (node));
        if (pin.charAt (0) == '+') {
            Widget member = classWidget.createMember (pin.substring (1));
            classWidget.addMember (member);
            return member;
        } else {
            Widget operation = classWidget.createOperation (pin.substring (1));
            classWidget.addOperation (operation);
            return operation;
        }
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget (this);
        connectionLayer.addChild (connectionWidget);
        return connectionWidget;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourcePin, String sourcePin) {
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createCenterAnchor (findWidget (sourcePin)));
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetPin, String targetPin) {
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createCenterAnchor (findWidget (targetPin)));
    }

    public LayerWidget getMainLayer () {
        return mainLayer;
    }

    public LayerWidget getConnectionLayer () {
        return connectionLayer;
    }

    private static class MyPopupMenuAction extends PopupMenuAction {

        public JPopupMenu getPopupMenu (Widget widget) {
            JPopupMenu popupMenu = new JPopupMenu ();
            popupMenu.add (new JMenuItem ("Open " + ((UMLClassWidget) widget).getClassName ()));
            return popupMenu;
        }

    }

}
