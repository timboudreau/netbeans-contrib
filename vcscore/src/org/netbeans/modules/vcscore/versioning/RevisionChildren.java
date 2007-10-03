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

import javax.swing.event.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.*;

/**
 *
 * @author  Martin Entlicher
 */
public class RevisionChildren extends Children.Keys implements ChangeListener, java.io.Serializable {

    protected static final Object WAIT_KEY = new Object();

    private RevisionList list = null;
    private RevisionItem item = null;
    private ChangeListener changeListenerToList;
    private ArrayList notificationListeners = new ArrayList(2);
    private boolean added; // true after addNotify() is called.
    
    /** Creates new RevisionChildren
     * @param list The RevisionList, can be null
     */
    public RevisionChildren(RevisionList list) {
        this(list, null);
    }
    
    /** Creates new RevisionChildren
     * @param list The RevisionList, can be null
     * @param item The RevisionItem where this children are rooted.
     */
    public RevisionChildren(RevisionList list, RevisionItem item) {
        changeListenerToList = WeakListeners.change (this, this);
        if (list != null) list.addChangeListener(changeListenerToList);
        this.list = list;
        this.item = item;
        stateChanged (null);
    }
    
    public RevisionList getList() {
        return list;
    }
    
    /**
     * Set a new revision list. Can be called only for the root children.
     */
    public void setList(RevisionList newList) {
        if (list != null) list.removeChangeListener(changeListenerToList);
        if (newList != null) newList.addChangeListener(changeListenerToList);
        this.list = newList;
        stateChanged (null);
    }
    
    public void addNotificationListener(NotificationListener l) {
        synchronized (notificationListeners) {
            notificationListeners.add(l);
        }
    }
    
    public void removeNotificationListener(NotificationListener l) {
        synchronized (notificationListeners) {
            notificationListeners.remove(l);
        }
    }
    
    protected void addNotify() {
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                ArrayList notifList;
                synchronized (notificationListeners) {
                    notifList = new ArrayList(notificationListeners);
                }
                for (int i = notifList.size() - 1; i >= 0; i--) {
                    NotificationListener l = (NotificationListener) notifList.get(i);
                    l.notifyAdded();
                }
            }
        });
        super.addNotify();
        added = true;
    }
    
    protected void removeNotify() {
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                ArrayList notifList;
                synchronized (notificationListeners) {
                    notifList = new ArrayList(notificationListeners);
                }
                for (int i = notifList.size() - 1; i >= 0; i--) {
                    NotificationListener l = (NotificationListener) notifList.get(i);
                    l.notifyRemoved();
                }
            }
        });
        super.removeNotify();
    }
    
    protected boolean accept(RevisionItem item) {
        return item.isDirectSubItemOf(this.item);
    }
    
    //protected abstract RevisionChildren getChildrenFor(RevisionItem item);
    
    protected Node createWaitingNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(RevisionChildren.class, "WaitNodeTooltip"));
        n.setIconBase("org/netbeans/modules/vcscore/versioning/wait");
        return n;
    }
    
    protected Node[] createNodes(Object key) {

        if (WAIT_KEY.equals(key)) {
            return new Node[] { createWaitingNode() };
        }

        // XXX workaround for synchronization problems
        // list is asynchonously nulled
        if (list == null) return new Node[0];

        Node[] nodes; //new Node[] { Node.EMPTY };
        RevisionItem item = (RevisionItem) key;
        //System.out.println("createNodes("+item.getRevision()+")");
        if (accept(item)) {
            //System.out.println("isRevision = "+(item.isRevision() && !item.isBranch()));
            //if (item.isRevision() && !item.isBranch()) {
            Node newNode;
            if (!list.containsSubRevisions(item) && !item.isBranch()) {
                newNode = list.getNodeDelegate(item, null);
            } else {
                RevisionChildren children = list.getChildrenFor(item);
                newNode = list.getNodeDelegate(item, children);
            }
            nodes = new Node[] { newNode };
        } else {
            nodes = new Node[0];
        }
        //if (nodes.length > 0) System.out.println("return node = "+nodes[0]);
        return nodes;
    }
    
    public synchronized void stateChanged(ChangeEvent evt) {
        //System.out.println("RevisionChildren.stateChanged("+evt+")");
        if (list == null) {
            setKeys(Collections.singleton(WAIT_KEY));
        } else {
            // #55399: Iterate all existing nodes and re-create the node delegates
            // (list.getNodeDelegate() is smart enough to re-create the node
            //  only when it's children changed (from EMPTY to something))
            if (added) {
                Node[] nodes = getNodes();
                for (int i = 0; i < nodes.length; i++) {
                    if (!(nodes[i] instanceof RevisionNode)) {
                        continue; // Unsupported
                    }
                    RevisionItem item = ((RevisionNode) nodes[i]).getItem();
                    if (!list.contains(item)) {
                        continue;
                    }
                    refreshKey(item); // To re-create the node if necessary
                }
            }
            setKeys(list); // To add/remove nodes
        }
        
    }
    
    public static interface NotificationListener {
        public void notifyAdded();
        public void notifyRemoved();
    }

}