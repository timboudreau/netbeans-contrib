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
