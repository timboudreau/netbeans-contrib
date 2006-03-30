/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.ui.TexCloneableEditor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class RootNode extends AbstractNode {

    private static DataFolder folder;

    private static synchronized DataFolder getPaletteFolder() {
        if (folder == null) {
            FileObject file = Repository.getDefault().getDefaultFileSystem().findResource("LaTeXPalette");

            folder = DataFolder.findFolder(file);
            
            assert folder != null;
        }

        return folder;
    }

    /** Creates a new instance of RootNode */
    public RootNode() {
        super(new RootChildren(getPaletteFolder()), Lookups.singleton(getPaletteFolder()));
    }
    
    private static final class RootChildren extends Children.Keys {
        
        private DataFolder delegateTo;
        
        public RootChildren(DataFolder delegateTo) {
            this.delegateTo = delegateTo;
        }
        
        protected void addNotify() {
            List cath = new ArrayList(IconsStorage.getDefault().getCathegories());
            
            cath.remove("greek");
            cath.add(0, "greek");

            List keys = new ArrayList(cath);

            keys.addAll(Arrays.asList(delegateTo.getChildren()));

            setKeys(keys);
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof DataObject) {
                return new Node[] {((DataObject) key).getNodeDelegate()};
            } else {
                return new Node[] {new CategoryNode((String) key)};
            }
        }
        
    }
    
}
