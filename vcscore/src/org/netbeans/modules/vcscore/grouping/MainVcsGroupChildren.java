/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import java.util.*;
import java.io.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.ErrorManager;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;


/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author builder
 */
public class MainVcsGroupChildren extends Children.Keys  {

    private VcsGroupFileChangeList fsListener = new VcsGroupFileChangeList();
    
    private FileChangeListener wfsListener = WeakListener.fileChange(fsListener, null);
    private FileObject rootFo;
    
    private final Object defaulGroupFileAccessLock = new Object();
    
    private final static String DEFAULT_FOLDER_NAME = "default";//NOI18N
    
    public MainVcsGroupChildren() {
        super();
    
        /** add subnodes..
         */
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        if (rootFo != null) {
            rootFo.addFileChangeListener(fsListener);
        }
    }
    
    public static DataFolder getMainVcsGroupFolder() {
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        DataFolder fold = null;
        try {
            fold = (DataFolder)DataObject.find(rootFo);
        } catch (DataObjectNotFoundException exc) {
            return null;
        }
        return fold;
        
    }
    
    VcsGroupNode getDefaultGroupNode() {
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dob = (DataObject)nodes[i].getCookie(DataObject.class);
            if (dob != null) {
                FileObject fo = dob.getPrimaryFile();
                if (fo.getName().equalsIgnoreCase(DEFAULT_FOLDER_NAME)) {
                    return (VcsGroupNode)nodes[i];
                }
            }
        }
        // not found, needs to be created..
        synchronized (defaulGroupFileAccessLock) {
            FileLock lock = null;
            PrintWriter writer = null;
            try {
                FileObject props = rootFo.getFileObject(DEFAULT_FOLDER_NAME, VcsGroupNode.PROPFILE_EXT);
                if (props == null) {
                    props = rootFo.createData(DEFAULT_FOLDER_NAME, VcsGroupNode.PROPFILE_EXT);
                }
                lock = props.lock();
                writer = new PrintWriter(props.getOutputStream(lock));
                writer.println(VcsGroupNode.PROP_NAME + "=" + NbBundle.getBundle(MainVcsGroupChildren.class).getString("LBL_DefaultGroupName"));//NOI18N
                writer.close();
                writer = null;
                lock.releaseLock();
                lock = null;
                // We must close the writer before creation of the folder,
                // because we listen on folder creation and refresh the whole thing.
                FileObject group = rootFo.getFileObject(DEFAULT_FOLDER_NAME);
                if (group == null) {
                    rootFo.createFolder(DEFAULT_FOLDER_NAME);
                }
            } catch (IOException exc) {
                ErrorManager manager = ErrorManager.getDefault();
                manager.notify(ErrorManager.WARNING, exc);
                return null;
            } finally {
                // Just to be sure, check if there's something left:
                if (writer != null) {
                    writer.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
        refreshAll();
        return getDefaultGroupNode();
    }
    

    
    private Node createVcsGroupNode(FileObject fo) {
        DataFolder fold = null;
        try {
            fold = (DataFolder)DataObject.find(fo);
        } catch (DataObjectNotFoundException exc) {
            return null;
        }
        VcsGroupNode node = new VcsGroupNode(fold);
        return node;
        
    }    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getGroups());
//        getDefaultGroupNode(); //hack here to create the default group by default..
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }

    
    private void refreshAll() {
        setKeys(getGroups());
    }

    private Collection getGroups() {
        /** add subnodes..
         */
        List grList = new LinkedList();
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        if (rootFo != null) {
            Enumeration enum = rootFo.getChildren(false);
            if (enum != null) {
                while (enum.hasMoreElements()) {
                    FileObject fo = (FileObject)enum.nextElement();
                    if (fo.isFolder()) {
                        grList.add(fo);
                    }
                }
            }
        }        
        return grList;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {

        Node newNode;
        DataFolder df = DataFolder.findFolder (((FileObject)key)) ;
        if (df != null) {
            return new Node[] { new VcsGroupNode(df) };
        }

        return new Node[0];
    }
    

    // Could also write e.g. removeKey to be used by the nodes in this children.
    // Or, could listen to changes in their status (NodeAdapter.nodeDestroyed)
    // and automatically remove them from the keys list here. Etc.

    
    private class VcsGroupFileChangeList extends FileChangeAdapter {
        
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
            if (fileRenameEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
            if (fileEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
            if (fileEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
//            refreshAll();

        }
        
    }    
}
