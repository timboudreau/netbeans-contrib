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

package org.netbeans.modules.vcscore.versioning;

import java.util.*;
import javax.swing.event.*;

import org.openide.nodes.Node;

/**
 *
 * @author  Martin Entlicher
 * RevisionList is a sorted set of RevisionItem objects.
 */
public abstract class RevisionList extends TreeSet implements Node.Cookie {

    private transient VcsFileObject fo = null; // The current File Object
    private transient Vector listeners;
    private transient WeakHashMap nodeDelegates;

    static final long serialVersionUID = -8578787400541124223L;
    
    /** Creates new RevisionList */
    public RevisionList() {
        listeners = new Vector();
        nodeDelegates = new WeakHashMap();
    }
    
    public void setFileObject(VcsFileObject fo) {
        this.fo = fo;
    }
    
    public VcsFileObject getFileObject() {
        return fo;
    }
    
    public boolean add(Object obj) {
        boolean status = super.add(obj);
        //System.out.println("RevisionList.add("+((RevisionItem) obj).getRevision()+")");
        fireChanged();
        return status;
    }
    
    public boolean addAll(Collection c) {
        boolean status = super.addAll(c);
        //System.out.println("RevisionList.addAll("+c+"): c.size() = "+c.size());
        fireChanged();
        return status;
    }
    
    public boolean remove(Object obj) {
        boolean status = super.remove(obj);
        //System.out.println("RevisionList.remove("+((RevisionItem) obj).getRevision()+")");
        fireChanged();
        return status;
    }
    
    public boolean removeAll(Collection c) {
        boolean status = super.removeAll(c);
        //System.out.println("RevisionList.removeAll("+c+"): c.size() = "+c.size());
        fireChanged();
        return status;
    }
    
    public boolean removeRevision(String revision) {
        Iterator it = this.iterator();
        while(it.hasNext()) {
            RevisionItem rev = (RevisionItem) it.next();
            String revision2 = rev.getRevision();
            if (revision2.equals(revision)) {
                this.remove(rev);
                return true;
            }
        }
        return false;
    }
    
    public void fireChanged() {
        //System.out.println("RevisionList.fireChange()");
        for(Enumeration enum = listeners.elements(); enum.hasMoreElements(); ) {
            ChangeListener listener = (ChangeListener) enum.nextElement();
            listener.stateChanged(new ChangeEvent(this));
        }
    }
    
    public abstract boolean containsSubRevisions(String revision);

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public boolean removeChangeListener(ChangeListener listener) {
        return listeners.remove(listener);
    }
    
    public Node getNodeDelegate(RevisionItem item, RevisionChildren children) {
        Node node = (Node) nodeDelegates.get(item);
        if (node == null) {
            node = createNodeDelegate(item, children);
            nodeDelegates.put(item, node);
        }
        return node;
    }
    
    protected Node createNodeDelegate(RevisionItem item, RevisionChildren children) {
        RevisionNode node;
        if (children == null || org.openide.nodes.Children.LEAF.equals(children)) {
            node = new RevisionNode(this, item);
        } else {
            node = new RevisionNode(children);
            node.setItem(item);
        }
        node.setDisplayName(item.getDisplayName());
        return node;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, java.io.IOException, java.io.NotActiveException {
        in.defaultReadObject();
        listeners = new Vector();
        nodeDelegates = new WeakHashMap();
    }
    
    /*
    private class SubRevisionComparator extends Object implements Comparator {
        
        private int length;
        
        public SubRevisionComparator(int length) {
            this.length = length;
        }
        
        public boolean equals(Object obj) {
            return 
        }
        
    }
    */
}
