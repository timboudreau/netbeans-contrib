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

import java.util.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.TopManager;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;


/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author builder
 */
public class VcsGroupChildren extends Children.Keys implements PropertyChangeListener {

    private GroupFileChange fsListener = new GroupFileChange();
    
    private FileChangeListener wfsListener = WeakListener.fileChange(fsListener, null);
    
    private PropertyChangeListener wpropertyListener = WeakListener.propertyChange(this, null);

    private DataFolder folder;
    
    public VcsGroupChildren(DataFolder dobj) {
        super();
    
        folder = dobj;
        /** add subnodes..
         */
    }
    
    
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getFilesInGroup());
        folder.getPrimaryFile().addFileChangeListener(wfsListener);
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        settings.addPropertyChangeListener(this);
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        folder.getPrimaryFile().removeFileChangeListener(wfsListener);
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        settings.removePropertyChangeListener(this);
    }

    
    private void refreshAll() {
        setKeys(getFilesInGroup());
    }

    private Collection getFilesInGroup() {
        /** add subnodes..
         */
        LinkedList list = new LinkedList();
        Enumeration childs = folder.children(false);
        Set actions = new HashSet();
        while (childs.hasMoreElements()) {
            DataObject dos = (DataObject)childs.nextElement();
            if (dos instanceof DataShadow) {
                 DataShadow shadow = (DataShadow)dos;
                 shadow.getOriginal().addPropertyChangeListener(this);
                 shadow.addPropertyChangeListener(this);
                 list.add(shadow);
            }
            if (dos.getClass().getName().equals("org.openide.loaders.BrokenDataShadow")) { //NOI18N
                list.add(dos);
            }
        }
        return list;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {

        Node newNode;
        if (key instanceof DataShadow) {
            DataShadow shad = (DataShadow)key;
            return new Node[] {new VcsGroupFileNode(shad) };
        }
        if (key.getClass().getName().equals("org.openide.loaders.BrokenDataShadow")) { //NOI18N
            DataObject obj = (DataObject)key;
            obj.addPropertyChangeListener(this);
            return new Node[] {obj.getNodeDelegate()};
        }
        return new Node[0];
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals(VcsGroupSettings.PROP_SHOW_LINKS)) {
            Node[] nods = getNodes();
            for (int i = 0; i < nods.length; i++) {
                if (nods[i] instanceof VcsGroupFileNode) {
                    ((VcsGroupFileNode)nods[i]).checkShowLinks();
                }
            }
            return;
        }
        if (propertyChangeEvent.getPropertyName().equals(DataObject.PROP_VALID)) {
            if (propertyChangeEvent.getNewValue() != null 
                        && propertyChangeEvent.getNewValue() instanceof Boolean) {
                Boolean bool = (Boolean)propertyChangeEvent.getNewValue();
                if (bool.booleanValue() == false) {
                    refreshAll();
                }
            }
        }
    }  


    // Could also write e.g. removeKey to be used by the nodes in this children.
    // Or, could listen to changes in their status (NodeAdapter.nodeDestroyed)
    // and automatically remove them from the keys list here. Etc.

    
    private class GroupFileChange extends FileChangeAdapter {
        
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
            refreshAll();
        }
        
        public void fileDataCreated(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
//            super.fileFolderCreated(fileEvent);
        }
        
        public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fileAttributeEvent) {
//            super.fileAttributeChanged(fileAttributeEvent);
        }
        
    }
}
