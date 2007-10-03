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

package org.netbeans.modules.vcscore.versioning;

import java.util.*;
import javax.swing.event.*;

import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

/**
 * RevisionList is a sorted set of RevisionItem objects.
 * <p>
 * Can be overriden to provide different Node delegates
 * {@link #createNodeDelegate}, or different children impl.
 * {@link #getChildrenFor}, or both.
 *
 * @author  Martin Entlicher
 */
public class RevisionList extends TreeSet implements Node.Cookie {

    private transient FileObject fo = null; // The current File Object
    private transient Vector listeners;
    private transient WeakHashMap nodeDelegatesWithoutChildren;
    private transient WeakHashMap nodeDelegatesWithChildren;
    private transient WeakHashMap existingChildren;
    private transient RevisionChildren rootChildren;

    static final long serialVersionUID = -8578787400541124223L;
    
    /** Creates new RevisionList */
    public RevisionList() {
        listeners = new Vector();
        nodeDelegatesWithoutChildren = new WeakHashMap();
        nodeDelegatesWithChildren = new WeakHashMap();
        existingChildren = new WeakHashMap();
    }
    
    public final void setFileObject(FileObject fo) {
        this.fo = fo;
    }
    
    public final FileObject getFileObject() {
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
    
    public void clear() {
        super.clear();
        fireChanged();
    }
    
    public boolean remove(Object obj) {
        boolean status = super.remove(obj);
        synchronized (this) {
            nodeDelegatesWithoutChildren.remove(obj);
            nodeDelegatesWithChildren.remove(obj);
            existingChildren.remove(obj);
        }
        //System.out.println("RevisionList.remove("+((RevisionItem) obj).getRevision()+")");
        fireChanged();
        return status;
    }
    
    public boolean removeAll(Collection c) {
        boolean status = super.removeAll(c);
        synchronized (this) {
            for (Iterator it = c.iterator(); it.hasNext(); nodeDelegatesWithoutChildren.remove(it.next()));
            for (Iterator it = c.iterator(); it.hasNext(); nodeDelegatesWithChildren.remove(it.next()));
            for (Iterator it = c.iterator(); it.hasNext(); existingChildren.remove(it.next()));
        }
        //System.out.println("RevisionList.removeAll("+c+"): c.size() = "+c.size());
        fireChanged();
        return status;
    }
    
    public final boolean removeRevision(String revision) {
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
    
    /**
     * Called when the content of this list changes.
     */
    private final void fireChanged() {
        //System.out.println("RevisionList.fireChange()");
        for(Enumeration en = listeners.elements(); en.hasMoreElements(); ) {
            ChangeListener listener = (ChangeListener) en.nextElement();
            listener.stateChanged(new ChangeEvent(this));
        }
    }
    
    /**
     * Add a change listener to listen on changes of the content of this list.
     */
    public final void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public final  boolean removeChangeListener(ChangeListener listener) {
        return listeners.remove(listener);
    }
    
    /**
     * Test whether this list contains any sub-revisions to the given revision.
     * This method is called to decide whether the given revision have any children
     * or not. Uses {@link RevisionItem#isDirectSubItemOf} method to decide whether
     * there is some sub-revision or not.
     */
    public final boolean containsSubRevisions(RevisionItem revision) {
        Iterator it = this.iterator();
        while(it.hasNext()) {
            RevisionItem testItem = (RevisionItem) it.next();
            if (testItem.isDirectSubItemOf(revision)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the children implementation for the given revision item.
     * {@link #createChildrenFor} method is called to create the children
     * if necessary.
     * @param item The revision item to get the children for or
     *             <code>null</code> when the root children are requested.
     */
    public final synchronized RevisionChildren getChildrenFor(RevisionItem item) {
        if (item == null) {
            if (rootChildren == null) {
                rootChildren = createChildrenFor(null);
            }
            return rootChildren;
        } else {
            RevisionChildren children = (RevisionChildren) existingChildren.get(item);
            if (children == null) {
                children = createChildrenFor(item);
                existingChildren.put(item, children);
            }
            return children;
        }
    }
    
    /**
     * Create the children implementation for the given revision item.
     * This method is called for revision items that are not branches
     * and {@link #containsSubRevisions} returns true.
     * The initial children should be returned when <code>null</code>
     * argument is provided. <p>
     * Called under a lock on <code>this</code>.
     * @param item The revision item to get the children for or
     *             <code>null</code> when the root children are requested.
     */
    protected RevisionChildren createChildrenFor(RevisionItem item) {
        return new RevisionChildren(this, item);
    }
    
    /**
     * Get the node delegate of the given revision item with given children.
     * The children are obtained by {@link #getChildrenFor} method.
     * Uses {@link #createNodeDelegate} method to create the node when it
     * does not exist.
     */
    public final synchronized Node getNodeDelegate(RevisionItem item, RevisionChildren children) {
        Node node;
        if (children == null) {
            node = (Node) nodeDelegatesWithoutChildren.get(item);
        } else {
            node = (Node) nodeDelegatesWithChildren.get(item);
        }
        if (node == null) {
            node = createNodeDelegate(item, children);
            if (children == null) {
                nodeDelegatesWithoutChildren.put(item, node);
            } else {
                nodeDelegatesWithChildren.put(item, node);
            }
        }
        return node;
    }
    
    /**
     * Get the node delegate for RevisionItem. Override to provide custom Node implementation.
     * <p>
     * Called under a lock on <code>this</code>.
     * @param item The revision item
     * @param children The children obtained from {@getChildrenFor} method.
     */
    protected Node createNodeDelegate(RevisionItem item, RevisionChildren children) {
        RevisionNode node;
        if (children == null || org.openide.nodes.Children.LEAF.equals(children)) {
            node = new RevisionNode(this, item);
        } else {
            node = new RevisionNode(children);
            node.setItem(item);
        }
        return node;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, java.io.IOException, java.io.NotActiveException {
        in.defaultReadObject();
        listeners = new Vector();
        nodeDelegatesWithoutChildren = new WeakHashMap();
        nodeDelegatesWithChildren = new WeakHashMap();
        existingChildren = new WeakHashMap();
    }
    
}
