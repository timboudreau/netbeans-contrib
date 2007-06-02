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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;

/**
 *
 * @author Tim Boudreau
 */
public class DbLogicalViewChildren extends ChildFactory<FileObject> implements FileChangeListener {
    private final DbProject proj;
    private static final Logger log = Logger.getLogger(DbLogicalViewChildren.class.getName());
    final Object lock = new Object();
    public DbLogicalViewChildren(DbProject proj) {
        this.proj = proj;
    }
    
    public void fileFolderCreated(FileEvent fe) {
        refresh(false);
    }

    public void fileDataCreated(FileEvent fe) {
        refresh(false);
    }

    public void fileChanged(FileEvent fe) {
        //do nothing
    }

    public void fileDeleted(FileEvent fe) {
        refresh(false);
    }

    public void fileRenamed(FileRenameEvent fe) {
        refresh(false);
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
        //do nothing
    }

    WeakSet <DbFileFilterNode> nodes = new WeakSet <DbFileFilterNode> (); //temporary fix
    @Override
    protected Node createNodeForKey(FileObject key) {
        try {
            DataObject dob = DataObject.find(key);
            DbFileFilterNode result = new DbFileFilterNode (dob.getNodeDelegate(), proj, key.getParent());
            return result;
        } catch (IOException ioe) {
            AbstractNode result = new AbstractNode(Children.LEAF);
            result.setDisplayName (ioe.getMessage());
            Exceptions.printStackTrace (ioe);
            return result;
        }
    }

    private boolean initialized;
    protected boolean createKeys(List <FileObject> toPopulate) {
        try {
            FileObject projdir = proj.getProjectDirectory();
            if (!initialized) {
                projdir.addFileChangeListener(WeakListeners.create(FileChangeListener.class, this, projdir));
                initialized = true;
            }
            boolean result = findFiles (projdir, toPopulate);
            return result;
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace( ex );
            return true;
        }
    }
    
    private boolean findFiles (FileObject dir, Collection <FileObject> dest) throws InterruptedException {
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
        return result;
    }
}
