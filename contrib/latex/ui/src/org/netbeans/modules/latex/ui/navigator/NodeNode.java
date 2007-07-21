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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.navigator;

import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.Collections;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.InputNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Jan Lahoda
 */
public class NodeNode extends AbstractNode {
    
    public static org.openide.nodes.Node constructRootNodeFor(DocumentNode dn) throws IntrospectionException {
        return new NodeNode(dn);
    }
    
    private static NodeNode[] getChildren(Node node) throws IntrospectionException {
        if (node instanceof TextNode) {
            TextNode tn = ((TextNode) node);
            int count = tn.getChildrenCount();
            NodeNode[] result = new NodeNode[count];
            
            for (int cntr = 0; cntr < count; cntr++) {
                result[cntr] = new NodeNode(tn.getChild(cntr));
            }
            
            return result;
        }
        
        if (node instanceof CommandNode) {
            CommandNode cnode = ((CommandNode) node);
            
            int count = cnode.getArgumentCount();
            NodeNode[] result = new NodeNode[count + (cnode instanceof InputNode ? 1 : 0)];
            
            for (int cntr = 0; cntr < count; cntr++) {
                result[cntr] = new NodeNode(cnode.getArgument(cntr));
            }
            
            if (cnode instanceof InputNode) {
                result[count] = new NodeNode(((InputNode) cnode).getContent(), "File Content");
            }
            
            return result;
        }
        
        if (node instanceof BlockNode) {
            BlockNode bn = (BlockNode) node;
            NodeNode[] result = new NodeNode[3];
            
            result[0] = new NodeNode(bn.getBeginCommand(), "Begin");
            result[1] = new NodeNode(bn.getContent(), "Content");
            result[2] = new NodeNode(bn.getEndCommand(), "End");
            
            return result;
        }
        
        throw new IllegalArgumentException("Unknown node type: " + node.getClass().getName());
    }
    
    private boolean root;
    
    /** Creates a new instance of NodeNode */
    public NodeNode(Node node) throws IntrospectionException {
        this(node, computeName(node));
    }
    
    private NodeNode(Node node, String displayName) throws IntrospectionException {
        super(new NodeChildren(getChildren(node)), Lookups.singleton(node));
        
        this.root = root;
        setDisplayName(displayName);
    }
    
//    public void addNodeListener(l
    
    private static class NodeChildren extends Children.Keys {
        private NodeNode[] node;
        
        public NodeChildren(NodeNode[] node) {
            this.node = node;
        }
        
        public void addNotify() {
            setKeys(Arrays.asList(node));
        }

        /** Create nodes for a given key.
         * @param key the key
         * @return child nodes for this key or null if there should be no 
         * nodes for this key
         */
        protected org.openide.nodes.Node[] createNodes(Object key) {
            return new org.openide.nodes.Node[] {(NodeNode) key};
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
    }
    
    private static String computeName(Node node) {
        if (node instanceof BlockNode)
            return ((BlockNode) node).getBlockName();

        if (node instanceof InputNode)
            return "Include File"; //TODO.

        if (node instanceof CommandNode)
            return ((CommandNode) node).getCommand().getCommand();

        String className = node.getClass().getName();
        int    lastDot   = className.lastIndexOf('.');

        return lastDot != (-1) ? className.substring(lastDot + 1) : className;
    }
        
    private static class NodeDescription {
        private Node node;
        private String displayName;
        
        
        public NodeDescription(Node node) {
            this(node, computeName(node));
        }
        
        public NodeDescription(Node node, String displayName) {
            this.node = node;
            this.displayName = displayName;
        }
    }
}
