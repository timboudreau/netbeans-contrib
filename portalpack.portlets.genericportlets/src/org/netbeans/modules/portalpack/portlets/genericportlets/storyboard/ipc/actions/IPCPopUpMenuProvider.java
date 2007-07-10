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
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.IPCGraphScene;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author Satyaranjan
 */
public class IPCPopUpMenuProvider implements PopupMenuProvider, ActionListener {
    private JPopupMenu menu;
    private static final String ACTION_CONNECT = "connect"; // NOI18N
    private static final String ACTION_SELECT = "selection"; // NOI18N
    private static final String ACTION_RESET = "Reset";
    private IPCGraphScene scene;
     public IPCPopUpMenuProvider(IPCGraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem("Connection Mode");
        item.setActionCommand(ACTION_CONNECT);
        item.addActionListener(this);
       // menu.add(item);

        item = new JMenuItem("Selection Mode");
        item.setActionCommand(ACTION_SELECT);
        item.addActionListener(this);
    //    menu.add(item);
        
        item = new JMenuItem("Reset");
        item.setActionCommand(ACTION_RESET);
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        menu.add(item);
    }
    public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
       return menu;
    }
 
    public void actionPerformed(ActionEvent e) {
      //  scene.setActiveTool(e.getActionCommand());
        if (e.getActionCommand().equals("Create_Events")){
            StatusDisplayer.getDefault().setStatusText("Selection mode");
        } else if(e.getActionCommand().equals(ACTION_CONNECT)){
           // scene.doConnect();
        }else if(e.getActionCommand().equals(ACTION_RESET)){
            scene.resetScene();
        }
    }

     
}
