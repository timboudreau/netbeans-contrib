/*
 * SceneMainMenu.java
 *
 * Created on August 31, 2006, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
        }
    }
    
}
