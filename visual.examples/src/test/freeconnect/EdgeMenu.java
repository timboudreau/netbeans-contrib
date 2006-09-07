/*
 * MainMenu.java
 *
 * Created on August 30, 2006, 2:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test.freeconnect;

import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.FreeConnectionWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.graph.GraphScene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author alex
 */
public class EdgeMenu implements PopupMenuProvider, ActionListener {
    
    private static final String ADD_REMOVE_CP_ACTION = "addRemoveCPAction"; // NOI18N
//    private static final String DELETE_ALL_CP_ACTION = "deleteAllCPAction"; // NOI18N
    private static final String DELETE_TRANSITION = "deleteTransition"; // NOI18N

    private GraphScene.StringGraph scene;

    private JPopupMenu menu;
    private FreeConnectionWidget edge;
    private Point point;

    public EdgeMenu(GraphScene.StringGraph scene) {
        this.scene = scene;
        menu = new JPopupMenu("Transition Menu");
        JMenuItem item;

        item = new JMenuItem("Add/Delete Control Point");
        item.setActionCommand(ADD_REMOVE_CP_ACTION);
        item.addActionListener(this);
        menu.add(item);

        menu.addSeparator();

//        item = new JMenuItem("Delete All Control Points");
//        item.setActionCommand(DELETE_ALL_CP_ACTION);
//        item.addActionListener(this);
//        item.setEnabled(false);
//        menu.add(item);

        item = new JMenuItem("Delete Transition");
        item.setActionCommand(DELETE_TRANSITION);
        item.addActionListener(this);
        menu.add(item);

    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point){
        if (widget instanceof FreeConnectionWidget) {
            this.edge = (FreeConnectionWidget) widget;
            this.point=point;
            return menu;
        }
        return null;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD_REMOVE_CP_ACTION)) {
            edge.addRemoveControlPoint(point);
        } else if(e.getActionCommand().equals(DELETE_TRANSITION)) {
            scene.removeEdge ((String) scene.findObject (edge));
        }
    }
    
}
