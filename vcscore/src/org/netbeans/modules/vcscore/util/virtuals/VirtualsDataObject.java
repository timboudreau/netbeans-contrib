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

package org.netbeans.modules.vcscore.util.virtuals;

import org.openide.cookies.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author  Martin Entlicher
 */
public class VirtualsDataObject extends MultiDataObject {

    
    /** Creates new VersioningDataObject */
    public VirtualsDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
    }
      
    /** Get the name of the data object.
    * @return the name with the extension
    */
    public String getName () {
        return getPrimaryFile ().getNameExt();
    }

    /** Provide node that should represent this data object.
    * @return the node representation for this data object
    */
    protected Node createNodeDelegate () {
        DataNode node = new DataNode (this, Children.LEAF);
        return node;
    }
    
    
}
