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

package org.netbeans.modules.corba.wizard.nodes;

import java.util.ArrayList;
import java.util.Collection;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.MoveableNode;
/** 
 *
 * @author  root
 * @version 
 */
public class MutableChildren extends Children.Keys implements MoveableNode {

    private ArrayList subNodes;
  
    /** Creates new MutableChildren */
    public MutableChildren() {
        super();
        this.subNodes = new ArrayList ();
    }
  
    public void addNotify () {
        super.addNotify();
        this.prepareKeys();
    }
  
    public void prepareKeys () {
        this.setKeys (subNodes);
    }
  
    public void addKey (MutableKey key) {
        this.subNodes.add (key);
        this.prepareKeys();
    }
  
    public void addKeys (Collection keys) {
        this.subNodes.addAll (keys);
        this.prepareKeys();
    }
  
    public void removeKey (MutableKey key) {
        this.subNodes.remove (key);
        this.prepareKeys();
    }
    
    public void removeAllKeys (boolean notify) {
        this.subNodes.clear();
        if (notify)
            this.prepareKeys();
    }
  
    public Node[] createNodes (Object key){
        if ( key != null && ( key instanceof MutableKey)){
            Node[] nodes = new Node[1];
            switch (((MutableKey)key).kind()){
            case MutableKey.MODULE:
                return new Node[] { new ModuleNode ((NamedKey)key)};
            case MutableKey.CONSTANT:
                return new Node[] { new ConstantNode ((NamedKey)key)};
            case MutableKey.STRUCT:
                return new Node[] { new StructNode ((NamedKey)key)};
            case MutableKey.EXCEPTION:
                return new Node[] { new ExceptionNode ((NamedKey)key)};
            case MutableKey.ALIAS:
                return new Node[] { new AliasNode ((NamedKey)key)};
            case MutableKey.UNION:
                return new Node[] { new UnionNode ((NamedKey)key)}; 
            case MutableKey.ENUM:
                return new Node[] { new EnumNode ((NamedKey)key)};
            case MutableKey.INTERFACE:
                return new Node[]  {new InterfaceNode ((NamedKey) key)};
            case MutableKey.ENUM_MBR:
                return new Node[] { new EnumEntryNode ((NamedKey)key)};
            case MutableKey.STRUCT_MBR:
                return new Node[] { new StructMemberNode ((NamedKey) key)};
            case MutableKey.UNION_MBR:
                return new Node [] { new UnionMemberNode ((NamedKey) key)};
            case MutableKey.OPERATION:
                return new Node [] { new OperationNode ((NamedKey)key)};
            case MutableKey.ATTRIBUTE:
                return new Node[] { new AttributeNode ((NamedKey)key)};
            case MutableKey.FORWARD_DCL:
                return new Node[] { new ForwardDcl ((ForwardDclKey)key)};
            case MutableKey.VALUE_BOX:
                return new Node[] { new ValueBoxNode((AliasKey)key)};
            case MutableKey.VALUETYPE:
                return new Node[] { new ValueTypeNode ((ValueTypeKey)key)};
            case MutableKey.VALUE:
                return new Node[] { new ValueNode ((ValueKey)key)};
            case MutableKey.VALUE_FACTORY:
                return new Node[] { new ValueFactoryNode ((ValueFactoryKey)key)};
            default:
                return new Node[0];
            }
        }
        return new Node[0];
    }
  
    public void moveUp(Node node) {
        NamedKey key = ((AbstractMutableIDLNode)node).key;
        int index = this.subNodes.indexOf (key);
        if (index == -1)
            return; // Uffff
        Object other = this.subNodes.get (index-1);
        this.subNodes.set (index-1, key);
        this.subNodes.set (index, other);
        this.prepareKeys();
    }
    
    public void moveDown(Node node) {
        NamedKey key = ((AbstractMutableIDLNode)node).key;
        int index = this.subNodes.indexOf (key);
        if (index == -1)
            return; // Uffff
        Object other = this.subNodes.get (index+1);
        this.subNodes.set (index+1,key);
        this.subNodes.set (index,other);
        this.prepareKeys();
    }
    
}
