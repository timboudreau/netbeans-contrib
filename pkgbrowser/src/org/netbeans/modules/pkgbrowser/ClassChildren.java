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
package org.netbeans.modules.pkgbrowser;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.Type;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
importorg.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Displays children of a ClassDefinition object by looking up its
 * corresponding DataObject.
 *
 * @author Timothy Boudreau
 */
class ClassChildren extends Children.Keys implements Runnable, NodeListener {
    private final String pkg;
    private final RequestProcessor rp;
    private final String clazz;
    private final Object lock = new Object();
    private RequestProcessor.Task task = null;
    
    /** Creates a new instance of ClassChildren */
    public ClassChildren(String pkg, String clazz, RequestProcessor rp) {
        this.pkg = pkg;
        this.rp = rp;
        this.clazz = clazz;
    }

    boolean alive = false;
    public void addNotify() {
        setKeys (new String[] { NbBundle.getMessage(PackageChildren.class, 
                "LBL_WAIT") }); //NOI18N
        synchronized (lock) {
            if (task == null) {
                task = rp.post(this);
            }
        }
        alive = true;
    }
    
    public void removeNotify() {
        synchronized (lock) {
            if (task != null) {
                task.cancel();
                task = null;
            }
            if (node != null) {
                alive = false;
                node.removePropertyChangeListener(this);
                node = null;
            }
            setKeys (new Object[0]);
        }
    }
    
    protected Node[] createNodes(Object key) {
        if (key instanceof String) {
            Node result = new AbstractNode (Children.LEAF);
            result.setDisplayName(key.toString());
            return new Node[] { result };
        } else {
            return new Node[] { new FilterNode ((Node) key) };
        }
    }

    public void run() {
        assert !EventQueue.isDispatchThread();
        synchronized (lock) {
            task = null;
        }
        String complexName = this.pkg + '.' + clazz;
        FileObject fob = null;
        boolean set = false;
        
        JavaMetamodel.getManager().waitScanFinished();
        JavaMetamodel.getDefaultRepository().beginTrans(false); 
        try {
            Type t = JavaModel.getDefaultExtent().getType().resolve(complexName);
            Resource r = t.getResource();
            fob = JavaModel.getFileObject(r);
            if (Thread.interrupted()) {
                setKeys (Collections.EMPTY_LIST);
            }
        } finally {
            JavaMetamodel.getDefaultRepository().endTrans();
        }
        if (fob != null) {
            DataObject ob;
            try {
                ob = DataObject.find(fob);
                setNode (ob.getNodeDelegate());
                set = true;
            } catch (DataObjectNotFoundException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        if (!set) {
            setKeys (new String[] { NbBundle.getMessage(ClassChildren.class, 
                    "LBL_UNRESOLVABLE") }); //NOI18N
        }
    }
    
    private Node node = null;
    private void setNode(Node n) {
        synchronized (lock) {
            if (this.node != null) {
                this.node.removePropertyChangeListener(this);
            }
            this.node = n;
        }
        if (n != null) {
            n.addPropertyChangeListener(this);
            setKeys (n.getChildren().getNodes(true));
        }
    }
    
    private void refreshFromNode() {
        Node n;
        synchronized (lock) {
            n = this.node;
        }
        if (n != null) {
            setKeys (n.getChildren().getNodes(true));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //do nothing
    }

    public void childrenAdded(NodeMemberEvent ev) {
        refreshFromNode();
    }

    public void childrenRemoved(NodeMemberEvent ev) {
        refreshFromNode();
    }

    public void childrenReordered(NodeReorderEvent ev) {
        refreshFromNode();
    }

    public void nodeDestroyed(NodeEvent ev) {
        setKeys (new String[] { NbBundle.getMessage(ClassChildren.class, 
                "LBL_UNRESOLVABLE") }); //NOI18N
    }
    
}
