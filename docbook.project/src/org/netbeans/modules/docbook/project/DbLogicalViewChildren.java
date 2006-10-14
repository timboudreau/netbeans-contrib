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
package org.netbeans.modules.docbook.project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
public class DbLogicalViewChildren extends Children.Keys implements FileChangeListener {
    private final DbProject proj;
    final Object lock = new Object();
    public DbLogicalViewChildren(DbProject proj) {
        this.proj = proj;
    }

    RequestProcessor.Task task;
    public void addNotify() {
        setKeys (Collections.singleton("Please wait..."));
        updateChildren();
    }

    private void updateChildren() {
        synchronized (lock) {
            if (task == null) {
                setTask(proj.rp.post(new Updater()));
            } else {
                task.cancel();
                setTask (proj.rp.post(new Updater()));
            }
        }
        proj.getProjectDirectory().addFileChangeListener(this);
    }

    public void removeNotify() {
        synchronized (lock) {
            task.cancel();
            task = null;
        }
        Node[] n = getNodes();
        for (int i = 0; i < n.length; i++) {
            if (n[i] instanceof DbFileFilterNode) {
                ((DbFileFilterNode) n[i]).cancel();
            }
        }
        setKeys (Collections.EMPTY_SET);
        proj.getProjectDirectory().removeFileChangeListener(this);
    }

    private void setTask (RequestProcessor.Task task) {
        synchronized (lock) {
            task = task;
        }
    }

    protected Node[] createNodes(Object key) {
        Node result;
        if (key instanceof String) {
            result = new AbstractNode (Children.LEAF);
            result.setDisplayName(key.toString());
        } else {
            FileObject ob = (FileObject) key;
            try {
                DataObject dob = DataObject.find(ob);
                result = new DbFileFilterNode (dob.getNodeDelegate(), proj);
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
                result = new AbstractNode (Children.LEAF);
                result.setDisplayName(ob.getName());
                ErrorManager.getDefault().notify(ex);
            }
        }
        return new Node[] { result };
    }

    public void fileFolderCreated(FileEvent fe) {
        updateChildren();
    }

    public void fileDataCreated(FileEvent fe) {
        updateChildren();
    }

    public void fileChanged(FileEvent fe) {
        //do nothing
    }

    public void fileDeleted(FileEvent fe) {
        updateChildren();
    }

    public void fileRenamed(FileRenameEvent fe) {
        updateChildren();
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
        //do nothing
    }

    private final class Updater implements Runnable {
        public void run() {
            try {
                Set s = new HashSet();
                try {
                    findFiles (proj.getProjectDirectory(), s);
                    setKeys (s);
                } catch (InterruptedException e) {
                    //normal
                }
            } finally {
                synchronized (lock) {
                    task = null;
                }
            }
        }

        private boolean findFiles (FileObject dir, Set dest) throws InterruptedException {
            int sz = dest.size();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (dir.isFolder()) {
                FileObject[] obs = dir.getChildren();
                for (int i = 0; i < obs.length; i++) {
                    findFiles (obs[i], dest);
                }
            } else if ("text/x-docbook+xml".equals(dir.getMIMEType())) {
                dest.add (dir);
            }
            boolean result = dest.size() > sz;
            if (result) {
                setKeys (dest);
            }
            return result;
        }
    }
}
