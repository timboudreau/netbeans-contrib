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

/**
 *
 * @author  Milos Kleint
 */

import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.cookies.InstanceCookie;
import org.openide.*;
import org.openide.util.NbBundle;
import org.openide.filesystems.*;
import java.util.*;
import java.io.*;

public class GroupUtils {

    /** Creates new GroupUtils */
    private GroupUtils() {
    }
    
    public static MainVcsGroupNode getMainVcsGroupNodeInstance() {
        MainVcsGroupNode root = null;
        FileSystem defFs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject fo = defFs.findResource(MainVcsGroupNode.GROUPS_PATH + "/org-netbeans-modules-vcscore-grouping-MainVcsGroupNode.instance");//NOI18N
        if (fo != null) {
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                dobj = null;
            }
            if (dobj != null && dobj instanceof InstanceDataObject) {
                InstanceDataObject ido = (InstanceDataObject)dobj;
                InstanceCookie cook = (InstanceCookie)ido.getCookie(InstanceCookie.class);
                root = (MainVcsGroupNode)ido.getNodeDelegate();
            }
        }
        return root;
    }

    public static DataFolder getMainVcsGroupFolder() {
        FileSystem fs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        DataFolder fold = null;
        try {
            fold = (DataFolder)DataObject.find(rootFo);
        } catch (DataObjectNotFoundException exc) {
            return null;
        }
        return fold;
        
    }    

    
    public static VcsGroupNode getDefaultGroupInstance() {
        MainVcsGroupNode node = getMainVcsGroupNodeInstance();
        MainVcsGroupChildren child = (MainVcsGroupChildren)node.getChildren();
        return child.getDefaultGroupNode();
    }  
    
    /**
     * Add the array of nodes to the default group..
     */
    public static void addToDefaultGroup(Node[] nodes) {
       VcsGroupNode node = GroupUtils.getDefaultGroupInstance(); 
       DataFolder fold = (DataFolder)node.getCookie(DataObject.class);
       if (fold != null) {
           addToGroup(fold, nodes);
       }
    }
    
    public static void addToGroup(DataFolder group, Node[] nodes) {
        List okFiles = new LinkedList();
        List badGroup = new LinkedList();
        for(int i = 0; i < nodes.length; i++) {
            //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null) {
                DataShadow shadow = findDOInGroups(dd);
                if (shadow != null) {
                    if (!group.equals(shadow.getFolder())) {
                    //TODO warning.. some files are already in groups
                        badGroup.add(shadow);
//                        System.out.println("already in another group " + shadow.getOriginal().getName());
                    }
                } else {
                    okFiles.add(dd);
                }
                
            }
        }
        if (badGroup.size() > 0) {
            NotifyDescriptor.Confirmation conf = new NotifyDescriptor.Confirmation(
                NbBundle.getBundle(GroupUtils.class).getString("AddToGroupAction.moveToGroupQuestion"),
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
    
    /**
     * the method checks final the specified dataobject is already 
     * in any of the groups. if so, returns the shadow data object.
     * Otherwise returns null
     */
    
    public static DataShadow findDOInGroups(DataObject dataObj) {
        FileSystem fs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        Enumeration enum = rootFo.getData(true);
        while (enum.hasMoreElements()) {
            FileObject fo = (FileObject)enum.nextElement();
            try {
                DataObject dobj = DataObject.find(fo);
                if (dobj.getClass().equals(DataShadow.class)) {
                    DataShadow shadow = (DataShadow)dobj;
                    if (shadow.getOriginal().equals(dataObj)) {
                        return shadow;
                    }
                }
            } catch (DataObjectNotFoundException exc) {
            }
        }
        return null;
    }    
}
