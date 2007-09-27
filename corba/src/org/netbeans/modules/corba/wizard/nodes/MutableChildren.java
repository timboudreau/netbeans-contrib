/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    
    public void addKey (int index, MutableKey key) {
        if (index < 0 || index > this.subNodes.size())
            return;
        this.subNodes.add (index, key);
        this.prepareKeys ();
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
    
    public ArrayList getKeys () {
        return this.subNodes;
    }
    
    public ArrayList getKeysSafe() {
        return (ArrayList)this.subNodes.clone();
    }
    
    public int getKeysCount () {
        return this.subNodes.size();
    }
    
    public MutableKey getKey (int index) {
        if (index < 0 || index>= this.subNodes.size())
            return null;
        return (MutableKey) this.subNodes.get (index);
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
