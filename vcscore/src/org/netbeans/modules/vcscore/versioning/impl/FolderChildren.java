/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.versioning.VcsFileObject;

/**
 *
 * @author Martin Entlicher
 */
final class FolderChildren extends Children.Keys {

    private AbstractVcsFolder folder;
    
    /** Creates new FolderChildren */
    public FolderChildren(AbstractVcsFolder folder) {
        this.folder = folder;
    }

    protected Node[] createNodes(Object key) {
        VcsFileObject fo = (VcsFileObject) key;
        return new Node[] { fo.getNodeDelegate().cloneNode() };
    }
    
    /** Initializes the children.
     */
    protected void addNotify() {
        initialize(true);
    }
    
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }
    
    private void initialize(boolean force) {
        VcsFileObject[] ch = folder.getChildren();
        setKeys(ch);
    }
    
}
