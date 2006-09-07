/*
 * MainMenu.java
 *
 * Created on August 30, 2006, 2:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test.freeconnect;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;

/**
 *
 * @author alex
 */
public class NodeMenu implements PopupMenuProvider, ActionListener {
    
    private static final String DELETE_NODE_ACTION = "deleteNodeAction"; // NOI18N
    
    private JPopupMenu menu;
    private IconNodeWidget node;

    private Point point;
    private GraphScene.StringGraph scene;
    
    public NodeMenu(GraphScene.StringGraph scene) {
        this.scene=scene;
        menu = new JPopupMenu("Node Menu");
        JMenuItem item;
        
        item = new JMenuItem("Delete Node");
        item.setActionCommand(DELETE_NODE_ACTION);
        item.addActionListener(this);
        menu.add(item);
    }
    
    public JPopupMenu getPopupMenu(Widget widget,Point point){
        this.point=point;
        this.node=(IconNodeWidget)widget;
        return menu;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(DELETE_NODE_ACTION)){
            scene.removeNodeWithEdges((String)scene.findObject (node));
        }
    }

    
}
