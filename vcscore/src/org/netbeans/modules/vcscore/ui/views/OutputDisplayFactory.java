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

package org.netbeans.modules.vcscore.ui.views;

/**
 *
 * @author  Milos Kleint
 */

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.explorer.*;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.util.*;

import org.netbeans.modules.vcscore.versioning.*;
import org.openide.windows.WindowManager;


public class OutputDisplayFactory {
    
    /** Creates a new instance of OutputDisplayFactory */
    public OutputDisplayFactory() {
    }
    
    public static void addCollectedFileInfos(FileVcsInfo root, List results) {
        Iterator it = results.iterator();
        FileVcsInfo parent = root;
//        FileVcsInfo parentVcsInfo = (FileVcsInfo)parent.getCookie(FileVcsInfo.class);
        File rootFile = parent.getFile();
        String path = "";
        while (it.hasNext()) {
            FileVcsInfo info = (FileVcsInfo)it.next();
            File parentFile = info.getFile().getParentFile();
//            parentVcsInfo = (FileVcsInfo)parent.getCookie(FileVcsInfo.class);
//            System.out.println("parentfile=" + parentFile.getAbsolutePath());
//            System.out.println("parentvcsinfofile=" + parentVcsInfo.getFile().getAbsolutePath());
            if (!parentFile.getAbsolutePath().equals(
                      parent.getFile().getAbsolutePath())) {
                // parent not right we need to search for it..
                if (parentFile.getPath().length() == rootFile.getPath().length()) {
//                    System.out.println("parent is root..");
                    path = "";
                    parent = root;
                } else {
                    path = parentFile.getAbsolutePath().substring(rootFile.getAbsolutePath().length() + 1);
                    path = path.replace('\\','/');
//                    System.out.println("path =" + path + "for file=" + info.getFile().getName());
                    parent = createDirPath(root, path);
                }
            }
            info.setAttribute(FileVcsInfo.PROPERTY_NODE_PATH, path);
            if (!parent.getChildren().equals(Children.LEAF)) {
                FileVcsInfoChildren children = (FileVcsInfoChildren)parent.getChildren();
                FileVcsInfo found = children.findKeyByFileName(info.getFile().getName());
                if (found == null) {
                    children.addKey(info);
                } else {
                    found.overwriteAttributesFrom(info);
                    children.refreshThisKey(found);
                }
            }
        }
    }
    
    
    
    public static FileInfoNode createRootNode(FileVcsInfo root, 
                                              boolean createDirStructure) {
       
//        System.out.println("creating fileInfonodestructure...");
        DataObject dobj = FileVcsInfoChildren.findVersioningDO(root);
        if (dobj != null) {
            if (dobj instanceof DataFolder) {
                DataFolder folder = (DataFolder)dobj;
                FileInfoNode node = new FileInfoNode(folder, root);
                if (createDirStructure) {
                    createSubDirStructure(folder, root);
                }
                return node;
            } 
            else {
                FileInfoNode node = new FileInfoNode(dobj, root);
                return node;
            }
        } else {
            Node nd = new AbstractNode(root.getChildren());
            nd.setName(root.getFile().getName());
            FileInfoNode node = new FileInfoNode(nd, root);
            return node;
        }
    }
    
    /**
     * Adds recursively all subdirectories and adds them to the root node.
     * For the directories are created BlankFileInformation objects.
     */
    private static void createSubDirStructure(DataFolder folder, FileVcsInfo info) {
        File rootFile = info.getFile();
        DataObject[] objs = folder.getChildren();
        if (objs != null && objs.length > 0) {
            for (int i = 0; i < objs.length; i++) {
                if (objs[i] instanceof DataFolder) {
                    DataFolder subfolder = (DataFolder)objs[i];
                    File newFile = new File(rootFile, subfolder.getName());
                    FileVcsInfo dirInfo = FileVcsInfoFactory.createBlankFileVcsInfo(info.getType(), newFile);
                    FileVcsInfoChildren childs = (FileVcsInfoChildren)info.getChildren();
                    childs.addKey(dirInfo);
                    createSubDirStructure(subfolder, dirInfo);
                }
            }
        }
    }
    
    /**
     * create a path of nodes from root to the requested node.. 
     * create new nodes on the way if needed, such nodes are Versioning DataObject related only
     * if possible..
     */
    static FileVcsInfo createDirPath(FileVcsInfo rootNode, String path) {
        FileVcsInfo currentParent = rootNode;
        StringTokenizer tokenizer = new StringTokenizer(path, "/", false);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            FileVcsInfoChildren childs = (FileVcsInfoChildren)currentParent.getChildren();
            FileVcsInfo info = childs.findKeyByFileName(token);
            if (info != null) {
                currentParent = info;
            } else {
                // create a new subnode;
                File newFile = new File(currentParent.getFile(), token);
                FileVcsInfo itemInfo = FileVcsInfoFactory.createFileVcsInfo(currentParent.getType(), newFile, false);
                childs.addKey(itemInfo);
                currentParent = itemInfo;
            }
            
        }
        return currentParent;
    }
    
    
    static ExplorerPanel findExistingRootContextPanel(FileVcsInfo info) {
        java.util.Set components = TopComponent.getRegistry().getOpened();
        if (components == null || components.size() == 0) return null;
        Iterator it = components.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof ExplorerPanel) {
                ExplorerPanel panel = (ExplorerPanel)next;
                Node rootContext = panel.getExplorerManager().getRootContext();
                FileVcsInfo infoCookie = (FileVcsInfo)rootContext.getCookie(FileVcsInfo.class);
                if (infoCookie != null) {
                    if    (infoCookie.getType().equals(info.getType()) && 
                           infoCookie.getFile().equals(info.getFile())) {
                       return panel;
                    }
                }
            }
        }
        return null;
    }
    
    
    public static void displayExplorer(FileVcsInfo rootInfo, java.util.List resultsList, VcsViewCreator viewCreator, Mode modeToDockTo) {
        VcsExplorerPanel expanel = (VcsExplorerPanel)OutputDisplayFactory.findExistingRootContextPanel(rootInfo);
        FileInfoNode rootNode = null;
        if (expanel == null) {
            expanel = new VcsExplorerPanel();
            expanel.setName(viewCreator.getTitle());
            expanel.setIcon(viewCreator.getImage());
            org.openide.windows.Workspace workspace = WindowManager.getDefault().getCurrentWorkspace();
            org.openide.windows.Mode javaMode = modeToDockTo;
            javaMode.dockInto(expanel);
            rootNode = OutputDisplayFactory.createRootNode(rootInfo, false);
            expanel.add(viewCreator.createView());
        } else {
            rootNode = (FileInfoNode)expanel.getExplorerManager().getRootContext();
            FileVcsInfo oldRootInfo = (FileVcsInfo)rootNode.getCookie(FileVcsInfo.class);
            oldRootInfo.overwriteAttributesFrom(rootInfo);
        }
        addCollectedFileInfos(rootInfo, resultsList);
        expanel.getExplorerManager().setRootContext(rootNode);
        try {
            expanel.getExplorerManager().setSelectedNodes(new Node[] {rootNode});
        } catch (java.beans.PropertyVetoException exc) {
            exc.printStackTrace();
        }
//        System.out.println("3 po root context");
        //           panel.displayOutputData();
        if (expanel.isOpened()) {
            expanel.requestFocus();
        } else {
            expanel.open();
        }
    }
    
}
