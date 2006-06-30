/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
