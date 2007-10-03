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

package org.netbeans.modules.vcscore.grouping;

import java.util.*;
import java.io.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.ErrorManager;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;


/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author builder
 */
public class MainVcsGroupChildren extends Children.Keys  {

    private VcsGroupFileChangeList fsListener = new VcsGroupFileChangeList();
    
    private FileChangeListener wfsListener = FileUtil.weakFileChangeListener(fsListener, null);
    private FileObject rootFo;
    
    private final Object defaulGroupFileAccessLock = new Object();
    
    private final static String DEFAULT_FOLDER_NAME = "default";//NOI18N
    
    public MainVcsGroupChildren() {
        super();
    
        /** add subnodes..
         */
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        if (rootFo != null) {
            rootFo.addFileChangeListener(fsListener);
        }
    }
    
    public static DataFolder getMainVcsGroupFolder() {
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        DataFolder fold = null;
        try {
            fold = (DataFolder)DataObject.find(rootFo);
        } catch (DataObjectNotFoundException exc) {
            return null;
        }
        return fold;
        
    }
    
    VcsGroupNode getDefaultGroupNode() {
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dob = (DataObject)nodes[i].getCookie(DataObject.class);
            if (dob != null) {
                FileObject fo = dob.getPrimaryFile();
                if (fo.getName().equalsIgnoreCase(DEFAULT_FOLDER_NAME)) {
                    return (VcsGroupNode)nodes[i];
                }
            }
        }
        // not found, needs to be created..
        synchronized (defaulGroupFileAccessLock) {
            FileLock lock = null;
            PrintWriter writer = null;
            try {
                FileObject props = rootFo.getFileObject(DEFAULT_FOLDER_NAME, VcsGroupNode.PROPFILE_EXT);
                if (props == null) {
                    props = rootFo.createData(DEFAULT_FOLDER_NAME, VcsGroupNode.PROPFILE_EXT);
                }
                lock = props.lock();
                writer = new PrintWriter(props.getOutputStream(lock));
                writer.println(VcsGroupNode.PROP_NAME + "=" + NbBundle.getBundle(MainVcsGroupChildren.class).getString("LBL_DefaultGroupName"));//NOI18N
                writer.close();
                writer = null;
                lock.releaseLock();
                lock = null;
                // We must close the writer before creation of the folder,
                // because we listen on folder creation and refresh the whole thing.
                FileObject group = rootFo.getFileObject(DEFAULT_FOLDER_NAME);
                if (group == null) {
                    rootFo.createFolder(DEFAULT_FOLDER_NAME);
                }
            } catch (IOException exc) {
                ErrorManager manager = ErrorManager.getDefault();
                manager.notify(ErrorManager.WARNING, exc);
                return null;
            } finally {
                // Just to be sure, check if there's something left:
                if (writer != null) {
                    writer.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
        refreshAll();
        return getDefaultGroupNode();
    }
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getGroups());
//        getDefaultGroupNode(); //hack here to create the default group by default..
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }

    
    private void refreshAll() {
        setKeys(getGroups());
    }

    private Collection getGroups() {
        /** add subnodes..
         */
        List grList = new LinkedList();
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        if (rootFo != null) {
            Enumeration en = rootFo.getChildren(false);
            if (en != null) {
                while (en.hasMoreElements()) {
                    FileObject fo = (FileObject)en.nextElement();
                    if (fo.isFolder()) {
                        grList.add(fo);
                    }
                }
            }
        }        
        return grList;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {

        Node newNode;
        DataFolder df = DataFolder.findFolder (((FileObject)key)) ;
        if (df != null) {
            return new Node[] { new VcsGroupNode(df) };
        }

        return new Node[0];
    }
    

    // Could also write e.g. removeKey to be used by the nodes in this children.
    // Or, could listen to changes in their status (NodeAdapter.nodeDestroyed)
    // and automatically remove them from the keys list here. Etc.

    
    private class VcsGroupFileChangeList extends FileChangeAdapter {
        
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
            if (fileRenameEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
            if (fileEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
            if (fileEvent.getFile().isFolder()) {
                refreshAll();
            }
        }
        
        public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
//            refreshAll();

        }
        
    }    
}
