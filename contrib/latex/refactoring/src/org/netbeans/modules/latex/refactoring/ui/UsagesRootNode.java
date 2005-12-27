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
package org.netbeans.modules.latex.refactoring.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class UsagesRootNode extends AbstractNode {
    
    /** Creates a new instance of UsagesRootNode */
    public UsagesRootNode(Map<FileObject, List<org.netbeans.modules.latex.model.command.Node>> file2Node, String name) {
        super(new UsagesChildren(file2Node));
        
        setDisplayName("Usages of " + name);
    }
    
    private static final class UsagesChildren extends Children.Keys {
        
        private java.util.Map<FileObject, List<org.netbeans.modules.latex.model.command.Node>> file2Node;
        
        public UsagesChildren(java.util.Map<FileObject, List<org.netbeans.modules.latex.model.command.Node>> file2Node) {
            this.file2Node = file2Node;
        }
        
        @Override
        public void addNotify() {
            setKeys(file2Node.keySet());
        }
        
        @Override
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }

        protected Node[] createNodes(Object key) {
            try {
                FileObject file = (FileObject) key;
                DataObject od   = DataObject.find(file);
                
                return new Node[] {new UsagesFileNode(od.getNodeDelegate(), file2Node.get(key))};
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
                return new Node[0];
            }
        }
    }
}
