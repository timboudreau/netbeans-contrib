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

package org.netbeans.modules.vcscore.actions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

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
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;

import org.netbeans.modules.vcscore.grouping.*;
/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class AddToGroupAction extends NodeAction {
    
    private DataObject newDataObject = null;
    private boolean adding;

    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        if (nodes == null || nodes.length == 0) return;
        // ...
    }


    public String getName () {
        return NbBundle.getMessage(AddToGroupAction.class, "LBL_AddToGroupAction");
    }

    protected String iconResource () {
        return "AddToGroupActionIcon.gif";
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (AddToGroupAction.class);
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
        String label;
        //JMenu menu=new JMenuPlus(g("CvsClientAction.displayName")); // NOI18N
        if (adding) {
            label = NbBundle.getMessage(AddToGroupAction.class, "LBL_AddToGroupAction");
        } else {
            label = NbBundle.getMessage(AddToGroupAction.class, "LBL_MoveToVcsGroupAction");
        }
        JMenu menu=new JMenuPlus(label); // NOI18N
        Actions.setMenuText (menu, label, isMenu);
        if (isMenu) {
            menu.setIcon(getIcon());
        }
        HelpCtx.setHelpIDString (menu, AddToGroupAction.class.getName ());
        JMenuItem item=null;
        DataFolder folder = MainVcsGroupNode.getMainVcsGroupFolder();
        FileObject foFolder = folder.getPrimaryFile();
        Enumeration children = foFolder.getData(false);
        boolean hasAny = false;
        while (children.hasMoreElements()) {
            FileObject fo = (FileObject)children.nextElement();
            if (fo.getExt().equals(VcsGroupNode.PROPFILE_EXT)) {
                try {
                    ResourceBundle bundle = new PropertyResourceBundle(fo.getInputStream());
                    Enumeration en = bundle.getKeys();
                    String dispName = bundle.getString(VcsGroupNode.PROP_NAME);
                    FileObject f = foFolder.getFileObject(fo.getName());
                    if (f != null && f.isFolder()) {
                        hasAny = true;
                        menu.add(createItem(fo.getName(), dispName));
                    }
                } catch (Exception exc) {
                    // just ignore missing resource or error while reading the props..
                    System.out.println("add to group exc=" + exc.getClass());
                }
            }
        }
        if (!hasAny) {
            menu.add(createItem("default", MainVcsGroupNode.getMainVcsGroupNodeInstance().getDefaultGroupInstance().getDisplayName()));
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
        if (node == null || node.length == 0) return false;
        adding = true;
        for (int m = 0; m < node.length; m++) {
            if (node[m] instanceof VcsGroupNode) return false;
            if (node[m] instanceof VcsGroupFileNode) {
                adding = false;
            }
        }
        DataFolder folder = MainVcsGroupNode.getMainVcsGroupFolder();
        DataObject[] children = folder.getChildren();
        if (children == null || children.length == 0) {
            return false;
        }
        if (node != null) {
            for (int i = 0; i < node.length; i++) {
                DataObject dobj = (DataObject)node[i].getCookie(DataObject.class);
                if (dobj != null) {
                    if (!dobj.getPrimaryFile().isData()) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true; // none of the nodes is a folder..
        } else {
            return false;
        }
    }    

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (adding) {
            addPerformed(actionEvent);
        } else {
            movePerformed(actionEvent);
        }
    }
    
    /**
     * Add the array of nodes to the default group..
     */
    public static void addToDefaultGroup(Node[] nodes) {
       VcsGroupNode node = MainVcsGroupNode.getMainVcsGroupNodeInstance().getDefaultGroupInstance(); 
       DataFolder fold = (DataFolder)node.getCookie(DataObject.class);
       if (fold != null) {
           addToGroup(fold, nodes);
       }
    }
    
    private static void addToGroup(DataFolder group, Node[] nodes) {
        List okFiles = new LinkedList();
        List badGroup = new LinkedList();
        for(int i = 0; i < nodes.length; i++) {
            //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null) {
                DataShadow shadow = MainVcsGroupChildren.findDOInGroups(dd);
                if (shadow != null) {
                    if (!group.equals(shadow.getFolder())) {
                    //TODO warning.. some files are already in groups
                        badGroup.add(shadow);
                        System.out.println("already in another group " + shadow.getOriginal().getName());
                    }
                } else {
                    okFiles.add(dd);
                }
                
            }
        }
        if (badGroup.size() > 0) {
            NotifyDescriptor.Confirmation conf = new NotifyDescriptor.Confirmation(
                NbBundle.getBundle(AddToGroupAction.class).getString("AddToGroupAction.moveToGroupQuestion"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION);
            Object retValue = TopManager.getDefault().notify(conf);
            if (retValue.equals(NotifyDescriptor.CANCEL_OPTION)) {
                return;
            }
            if (retValue.equals(NotifyDescriptor.YES_OPTION)) {
                Iterator it = badGroup.iterator();
                while (it.hasNext()) {
                    DataShadow oldShadow = (DataShadow)it.next();
                    DataObject obj = oldShadow.getOriginal();
                    try {
                        oldShadow.delete();
                        obj.createShadow(group);
                    } catch (IOException exc) {
                        //TODO warning..
                        System.out.println("operation could not be completed.");
                    }
                }
            }
        }
        Iterator it = okFiles.iterator();
        while (it.hasNext()) {
            try {
                DataObject obj = (DataObject)it.next();
                DataShadow shadow = obj.createShadow(group);
            } catch (java.io.IOException exc) {
                //TODO warning
                System.out.println("cannot create shadow");
            }
        }
    }
    
    
        /** Fires operation event to data loader pool.
    * @param ev the event
    * @param type OperationEvent.XXXX constant
    */
/*    private static void fireOperationEvent (OperationEvent ev, int type) {
        org.openide.TopManager.getDefault().getLoaderPool ().fireOperationEvent (ev, type);
    }
 */


    private void movePerformed(java.awt.event.ActionEvent actionEvent) {
        String groupName = actionEvent.getActionCommand();
        Node grFolder = MainVcsGroupNode.getMainVcsGroupNodeInstance();
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
        Node[] actNodes = getActivatedNodes();
        if (actNodes == null) return;
        for (int j = 0; j < actNodes.length; j++) {
            if (actNodes[j] instanceof VcsGroupFileNode) {
                VcsGroupFileNode nd = (VcsGroupFileNode)actNodes[j];
                DataShadow shadow = (DataShadow)nd.getCookie(DataShadow.class);
                try {
                    shadow.getOriginal().createShadow(group);
                    shadow.delete();
                } catch (IOException exc) {
                    NotifyDescriptor excMess = new NotifyDescriptor.Message(
                    NbBundle.getBundle(AddToGroupAction.class).getString("MoveToVcsGroupAction.movingError"),
                    NotifyDescriptor.ERROR_MESSAGE);
                    TopManager.getDefault().notify(excMess);
                }
            }
        }
    }
    
    private void addPerformed(java.awt.event.ActionEvent actionEvent) {
        String groupName = actionEvent.getActionCommand();
        Node grFolder = MainVcsGroupNode.getMainVcsGroupNodeInstance();
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
        Node[] nodes = getActivatedNodes();
        addToGroup(group, nodes);
    }

    
}
