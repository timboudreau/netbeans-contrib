/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import javax.swing.event.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.*;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class RevisionChildren extends Children.Keys implements ChangeListener, java.io.Serializable {

    private String acceptField = "";
    private int numAcceptDots = 1;
    private RevisionList list = null;
    private RevisionNode parentNode = null;
    
    /** Creates new RevisionChildren */
    public RevisionChildren(RevisionList list) {
        list.addChangeListener(WeakListener.change (this, this));
        this.list = list;
        stateChanged (null);
    }
    
    public RevisionList getList() {
        return list;
    }
    
    /*
    public void setNode(RevisionNode node) {
        parentNode = node;
    }
     */
    
    public RevisionNode getParentNode() {
        return parentNode;
    }
    
    private void setAcceptField(String acceptField) {
        this.acceptField = acceptField;
    }
    
    private void setNumAcceptDots(int dots) {
        numAcceptDots = dots;
    }
    
    protected abstract boolean accept(RevisionItem item);
    
    protected abstract RevisionChildren getChildrenFor(RevisionItem item);
    
    protected Node[] createNodes(Object key) {
        Node[] nodes = new Node[0]; //new Node[] { Node.EMPTY };
        RevisionItem item = (RevisionItem) key;
        //System.out.println("createNodes("+item.getRevision()+")");
        if (accept(item)) {
            //System.out.println("isRevision = "+(item.isRevision() && !item.isBranch()));
            //if (item.isRevision() && !item.isBranch()) {
            if (!list.containsSubRevisions(item.getRevision()) && !item.isBranch()) {
                RevisionNode newNode = new RevisionNode(list, item);
                nodes = new Node[] { newNode };
            } else {
                RevisionChildren children = getChildrenFor(item);
                RevisionNode node = new RevisionNode(children);
                node.setName(item.getDisplayName());
                node.setItem(item);
                nodes = new Node[1]; // { node };
                nodes[0] = node;
                //list.fireChange();
            }
        }
        //if (nodes.length > 0) System.out.println("return node = "+nodes[0]);
        return nodes;
    }
    
    public void stateChanged(ChangeEvent evt) {
        //System.out.println("RevisionChildren.stateChanged("+evt+")");
        setKeys(list /*(Collection) evt.getSource()*/);
    }

}