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

import java.io.OutputStream;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.NamedKey;
import org.netbeans.modules.corba.wizard.nodes.utils.EditCookie;
import org.netbeans.modules.corba.wizard.nodes.utils.MoveableCookie;
/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
abstract public class AbstractMutableIDLNode extends AbstractNode implements EditCookie, MoveableCookie {

    protected SystemAction[] actions;
    protected final static String SPACE = "    ";
    protected final NamedKey key;
  
    /** Creates new AbstractMutableIDLNode */
    public AbstractMutableIDLNode(Children children, NamedKey key) {
        super (children);
        this.key = key;
    }
  
    public boolean canCut () {
        return false;
    }
  
    public boolean canCopy () {
        return false;
    }
  
    public boolean canDestroy () {
        return true;
    }
  
    public boolean canRename () {
        return true;
    }
  
    public SystemAction[] getActions () {
        if (this.actions == null)
        this.actions = this.createActions();
        return this.actions;
    }
    
    public void moveUp () {
        Children cld = this.getParentNode().getChildren();
        ((MutableChildren)cld).moveUp (this);
    }
    
    public void moveDown () {
        Children cld = this.getParentNode().getChildren();
        ((MutableChildren)cld).moveDown (this);
    }
    
    public boolean canMoveUp () {
        try {
            Node parent = this.getParentNode();
            if (parent == null)
                return false;
            Children cld = parent.getChildren();
            if (!(cld instanceof MutableChildren))
                return false;
            Node[] nodes = cld.getNodes();
            if (nodes[0] != this)
                return true;
        }catch (Exception e) {}
        return false;
    }
    
    public boolean canMoveDown () {
        try {
            Node node = this.getParentNode();
            if (node == null)
                return false;
            Children cld = node.getChildren();
            if (!(cld instanceof MutableChildren))
                return false;
            Node[] nodes = cld.getNodes();
            if (nodes[nodes.length-1] != this)
                return true;
        }catch (Exception e) {}
        return false;
    }
  
    public void destroy () {
        Node node = this.getParentNode ();
        if (node == null)
            return;
        Children cld = node.getChildren ();
        if (! (cld instanceof MutableChildren))
            return;
        ((MutableChildren)cld).removeKey (this.key);
    }
  
    public abstract String generateSelf (int indent);
  
    public abstract SystemAction[] createActions ();
}
