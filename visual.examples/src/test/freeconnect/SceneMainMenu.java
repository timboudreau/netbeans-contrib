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
package test.freeconnect;

import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author alex
 */
public class SceneMainMenu implements PopupMenuProvider, ActionListener {
    
    private static final String ADD_NEW_NODE_ACTION = "addNewNodeAction"; // NOI18N

    private GraphScene.StringGraph scene;

    private JPopupMenu menu;
    private Point point;
    
    private int nodeCount=3;
    
    public SceneMainMenu(GraphScene.StringGraph scene) {
        this.scene=scene;
        menu = new JPopupMenu("Scene Menu");
        JMenuItem item;
        
        item = new JMenuItem("Add New Node");
        item.setActionCommand(ADD_NEW_NODE_ACTION);
        item.addActionListener(this);
        menu.add(item);
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point){
        this.point=point;
        return menu;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(ADD_NEW_NODE_ACTION.equals (e.getActionCommand ())) {
            String hm = "Node"+(nodeCount++);
            Widget newNode = scene.addNode(hm);
            scene.getSceneAnimator().animatePreferredLocation(newNode,point);
            scene.validate();
        }
    }
    
}
