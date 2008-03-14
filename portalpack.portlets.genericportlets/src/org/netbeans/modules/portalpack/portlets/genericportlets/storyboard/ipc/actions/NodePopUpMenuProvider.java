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
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.IPCGraphScene;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class NodePopUpMenuProvider implements PopupMenuProvider, ActionListener{
     private IPCGraphScene scene;
     private static final String ACTION_REMOVE = "Remove"; //NOI18N
     private static final String ACTION_ADD = "Add"; //NOI18N
     private static final String ACTION_ADD_PROCESS_EVENT = "Add_Process_Event"; //NOI18N
     private String nodeKey="";
     private JPopupMenu menu;
     /** Creates a new instance of NodePopUpMenuProvider */
    public NodePopUpMenuProvider(IPCGraphScene scene,String nodeKey) {
        this.nodeKey = nodeKey;
        this.scene = scene;
        menu = new JPopupMenu(NbBundle.getMessage(NodePopUpMenuProvider.class, "MENU_POP_UP"));
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(NodePopUpMenuProvider.class, "MENU_REMOVE_PORTLET_FROM_STORYBOARD"));
        item.setActionCommand(ACTION_REMOVE);
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        menu.add(item);
        
        item = new JMenuItem(NbBundle.getMessage(NodePopUpMenuProvider.class, "MENU_ADD_PUBLISH_EVENT"));
        item.setActionCommand(ACTION_ADD);
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        menu.add(item);
        
        JMenuItem item1 = new JMenuItem(NbBundle.getMessage(NodePopUpMenuProvider.class, "MENU_ADD_PROCESS_EVENT"));
        item1.setActionCommand(ACTION_ADD_PROCESS_EVENT);
        item1.addActionListener(this);
        item1.setBackground(Color.WHITE);
        item.setToolTipText(NbBundle.getMessage(NodePopUpMenuProvider.class, "MENU_ADD_PROCESS_EVENT_TOOLTIP"));
        menu.add(item1);
        
        menu.setBackground(Color.white);
        
    }

    public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
       return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ACTION_REMOVE)){
            scene.deletePortletNodeFromScene(nodeKey, true);
        }else if(e.getActionCommand().equals(ACTION_ADD)){
            scene.addEvent(nodeKey);
        }else if(e.getActionCommand().equals(ACTION_ADD_PROCESS_EVENT)){
            scene.addNewProcessEvent(nodeKey);
        }
    }
    
}
