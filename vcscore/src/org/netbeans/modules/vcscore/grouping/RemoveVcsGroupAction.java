/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.JMenuItem;
import org.openide.awt.JMenuPlus;
import javax.swing.JMenu;
import javax.swing.event.*;
import java.io.*;

import org.openide.awt.Actions;
import org.openide.awt.JInlineMenu;
import org.openide.util.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.*;
import org.openide.util.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;


/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class RemoveVcsGroupAction extends NodeAction {

    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        // ...
    }


    public String getName () {
        return NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction");//NOI18N
    }

    protected String iconResource () {
        return "RemoveVcsGroupActionIcon.gif";//NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (RemoveVcsGroupAction.class);
    }

    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        return getPresenter(true);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        return getPresenter(false);
    }
    
    public JMenuItem getPresenter(boolean isMenu){
        JInlineMenu inlineMenu = new JInlineMenu();
        //JMenu menu=new JMenuPlus(g("CvsClientAction.displayName")); // NOI18N
        JMenu menu=new JMenuPlus(NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction")); // NOI18N
        Actions.setMenuText (menu, NbBundle.getMessage(RemoveVcsGroupAction.class, "LBL_RemoveVcsGroupAction"), isMenu);// NOI18N
        if (isMenu) {
            menu.setIcon(getIcon());
        }
        HelpCtx.setHelpIDString (menu, RemoveVcsGroupAction.class.getName ());

        JMenuItem item=null;
        DataFolder folder = GroupUtils.getMainVcsGroupFolder();
        FileObject foFolder = folder.getPrimaryFile();
        Enumeration children = foFolder.getData(false);
        while (children.hasMoreElements()) {
            FileObject fo = (FileObject)children.nextElement();
            if (fo.getExt().equals(VcsGroupNode.PROPFILE_EXT)) {
                try {
                    ResourceBundle bundle = new PropertyResourceBundle(fo.getInputStream());
                    Enumeration en = bundle.getKeys();
                    String dispName = bundle.getString(VcsGroupNode.PROP_NAME);
                    FileObject f = foFolder.getFileObject(fo.getName());
                    if (f != null && f.isFolder()) {
                        menu.add(createItem(fo.getName(), dispName));
                    }
                } catch (Exception exc) {
                    // just ignore missing resource or error while reading the props..
                    System.out.println("remove from group exc=" + exc.getClass());
                }
            }
        }
        JMenuItem[] menus = new JMenuItem[1];
        menus[0] = menu;
        inlineMenu.setMenuItems(menus);
        return inlineMenu;
    }

    //-------------------------------------------
    private JMenuItem createItem(String name, String dispName){
        JMenuItem item=null ;
        
        //item=new JMenuItem(g(name));
        item = new JMenuItem ();
        Actions.setMenuText (item, dispName, false);
        item.setActionCommand(dispName);
        item.addActionListener(this);
        return item;
    }    

    protected boolean enable(org.openide.nodes.Node[] node) {
        FileObject folder = GroupUtils.getMainVcsGroupFolder().getPrimaryFile();
        Enumeration children = folder.getFolders(false);
        if (children.hasMoreElements()) {
            return true;
        } else {
            return false;
        }
    }    

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        String groupName = actionEvent.getActionCommand();
        Node grFolder = GroupUtils.getMainVcsGroupNodeInstance();
        Node[] dobjs = grFolder.getChildren().getNodes();
        DataFolder group = null;
        if (dobjs == null) return;
        for (int i = 0; i < dobjs.length; i++) {
            if (dobjs[i].getName().equals(groupName)) {
                DataFolder fold = (DataFolder)dobjs[i].getCookie(DataObject.class);
                group = fold;
                break;
            }
        }
        if (group == null) return;
        NotifyDescriptor.Confirmation conf = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(RemoveVcsGroupAction.class, "RemoveVcsGroupAction.removeGroupQuestion", groupName), //NOI18N

            NotifyDescriptor.YES_NO_OPTION);
        Object retValue = TopManager.getDefault().notify(conf);
        if (retValue.equals(NotifyDescriptor.NO_OPTION)) {
            return;
        }
        if (retValue.equals(NotifyDescriptor.YES_OPTION)) {
            try {
                group.delete();
            } catch (IOException exc) {
                NotifyDescriptor excMess = new NotifyDescriptor.Message(
                   NbBundle.getBundle(RemoveVcsGroupAction.class).getString("RemoveVcsGroupAction.removingError"), //NOI18N
                   NotifyDescriptor.ERROR_MESSAGE);
                TopManager.getDefault().notify(excMess);
            }
        }
    }
    
}
