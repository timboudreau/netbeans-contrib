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

import java.io.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.*;
import org.openide.cookies.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.util.actions.*;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.actions.*;
import org.netbeans.modules.vcscore.versioning.*;

/** A customized filter node.
 *
 * @author Milos Kleint
 */
public class FileInfoNode extends FilterNode {

    private Node originalNode;
    private DataObject dataObject = null;
    private FileVcsInfo info;
    
    
    public FileInfoNode(DataObject versioningDO, FileVcsInfo fileInfo) {
        this(versioningDO.getNodeDelegate(), fileInfo);
        dataObject = versioningDO;
        this.disableDelegation(DELEGATE_GET_DISPLAY_NAME);
        this.disableDelegation(DELEGATE_DESTROY);
    }
    
    public FileInfoNode(Node originalNode, FileVcsInfo fileInfo) {
        super(originalNode, fileInfo.getChildren());
        info = fileInfo;
        this.originalNode = originalNode;
    }
    
    
    public String getDisplayName() {
        return getOriginal().getName();
    }

    public Node cloneNode () {
        // Usually you will want to override this if you are subclassing.
        // Otherwise a filter of a filter is created, which works but is not ideal.
        if (dataObject != null) {
            return new FileInfoNode(dataObject, info);
        } else {
            return new FileInfoNode(originalNode, info);
        }
    }

    // Often you will want to override this if you are subclassing.
    // Otherwise the filter node cannot be persisted, so for example
    // Explorer windows rooted at it will not be correctly restored.
/*    public Node.Handle getHandle () {
	Node.Handle origHandle = getOriginal ().getHandle ();
	// Simplest behavior: just store the original node and try to recreate
	// a filter based on that.
	if (origHandle != null)
            return new VcsGroupFileNodeHandle (origHandle);
	else
	    return null; // cannot persist original, do not persist filter
    }
 */

    private org.openide.util.actions.SystemAction[] getAdditionalActions() {
        return info.getAdditionalActions();
    }

    public org.openide.util.actions.SystemAction[] getActions() {
        return getContextActions();
    }
    
    public org.openide.util.actions.SystemAction[] getContextActions() {
        SystemAction[] actions = originalNode.getActions();
        List actionsList = new ArrayList(Arrays.asList(actions));
        if (actionsList.contains(SharedClassObject.findObject(
                                  RefreshRevisionsAction.class, true))) {
            actions = removeAction(actionsList, RefreshRevisionsAction.class);
        }
        if (actionsList.contains(SharedClassObject.findObject(
                         org.openide.actions.OpenLocalExplorerAction.class, true))) {
            actions = removeAction(actionsList, org.openide.actions.OpenLocalExplorerAction.class);
        }
        if (actionsList.contains(SharedClassObject.findObject(
                         org.openide.actions.ToolsAction.class, true))) {
            actions = removeAction(actionsList, org.openide.actions.ToolsAction.class);
        }
        if (getAdditionalActions() != null) {
            actions = SystemAction.linkActions(getAdditionalActions(), actions);
        }
        return actions;
    }

    private SystemAction[] removeAction(List actionsList, Class action) {
//        actionsList = new ArrayList(actionsList);
        int i = actionsList.size() - 1;
        actionsList.remove(SharedClassObject.findObject(action, true));
        SystemAction[] toReturn = new SystemAction[i];
        toReturn = (SystemAction[]) actionsList.toArray(toReturn);
        return toReturn;
    }
    
/*    public org.openide.util.actions.SystemAction[] getActions() {
        SystemAction[] actions = originalNode.getActions();
        List actionsList = Arrays.asList(actions);
        if (actionsList.contains(SystemAction.get(RefreAction.class))) {
            actions = addDelete(actionsList);
        }
        return actions;
    }
 */
    
    
/*    // Note that this class should be static and is serializable:
    private static class VcsGroupFileNodeHandle extends Node.Handle {
	private Node.Handle origHandle; // the only serializable state
        public VcsGroupFileNodeHandle (Node.Handle origHandle) {
	    this.origHandle = origHandle;
	}
	public Node getNode () throws IOException {
	    // Simplest behavior. If you have more configuration present in the filter,
	    // for example additional options to the constructor, you will probably need
	    // to manually store them or otherwise know how to recreate them.
            return new VcsGroupFileNodeNode (origHandle);
	}
    }
*/
    // To override handling of e.g. node destruction, you may:
    public boolean canDestroy () {
	return true;
    }
    public void destroy () throws IOException {
        System.out.println("destroying.." + getName());
	super.destroy (); // if you have disabled DELEGATE_DESTROY, this destroys this node
	// getOriginal ().destroy (); // you might wish to destroy both
	// Can perform other actions here to your liking.
    }

    // To add cookies:
    public Node.Cookie getCookie (Class clazz) {
        if (clazz.equals(RefreshRevisionsCookie.class)) {
            return null;
        }
        if (clazz.equals(FileVcsInfo.class)) {
            return info;
        }
        return originalNode.getCookie (clazz);
    }

    public boolean canRename() {
        return false;
    }
    
    
/*    public DataObject getVersioningDataObject() {
        return dataObject;
    }
  */  
    
}
