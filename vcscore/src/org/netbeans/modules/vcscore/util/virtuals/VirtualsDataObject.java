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

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

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
        DataNode node = new VirtualsDataNode (this, Children.LEAF);
        return node;
    }

    public class VirtualsDataNode extends DataNode {
        
        public VirtualsDataNode(MultiDataObject obj, Children childs) {
            super(obj, childs);
            //            setName(obj.getPrimaryFile().getNameExt());
            //            setIconBase(
            updateDisplayName();
            setShortDescription(NbBundle.getMessage(VirtualsDataNode.class, "VirtualsDataNode.Description"));
        }
        
        /** Changes the name of the node and may also rename the data object.
         * If the object is renamed and file extensions are to be shown,
         * the display name is also updated accordingly.
         *
         * @param name new name for the object
         * @param rename rename the data object?
         * @exception IllegalArgumentException if the rename failed
         */
        public void setName(String name, boolean rename) {
            super.setName(name, rename);
            if (rename) updateDisplayName();
        }
        
        private void updateDisplayName() {
            FileObject prim = getDataObject().getPrimaryFile();
            String newDisplayName = null;
            
            
            String ext = prim.getExt();
            newDisplayName = ext == null || ext.equals("") ? // NOI18N
                             prim.getName() : prim.getName() + '.' + ext; // NOI18N
            if (displayFormat != null)
                setDisplayName(displayFormat.format(new Object[] { newDisplayName }));
            else
                setDisplayName(newDisplayName);
        }
        
        
    }
    
}
