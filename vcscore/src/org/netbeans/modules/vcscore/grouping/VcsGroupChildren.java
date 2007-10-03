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

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;


/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author builder
 */
public class VcsGroupChildren extends Children.Keys implements PropertyChangeListener {

    private GroupFileChange fsListener = new GroupFileChange();

    private FileChangeListener wfsListener = FileUtil.weakFileChangeListener(fsListener, null);
    
    private PropertyChangeListener wpropertyListener = WeakListeners.propertyChange(this, null);

    private DataFolder folder;
    
    private ShadowPropertyChangeListener shPropChange = new ShadowPropertyChangeListener();
    
    private DOPropertyChangeListener doPropChange = new DOPropertyChangeListener();
    
    private VcsGroupChildren.RefreshAllTask refreshRunnable;
    private RequestProcessor.Task refreshTask;
    
    public VcsGroupChildren(DataFolder dobj) {
        super();
    
        folder = dobj;
        refreshRunnable = new VcsGroupChildren.RefreshAllTask();
        refreshTask = null;
    }
    
    
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys(getFilesInGroup());
        folder.getPrimaryFile().addFileChangeListener(wfsListener);
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        settings.addPropertyChangeListener(this);
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        folder.getPrimaryFile().removeFileChangeListener(wfsListener);
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        settings.removePropertyChangeListener(this);
    }

    /**
     * Schedules a request processor task that refreshes the group.
     * If the task is scheduled and another request arrives, the task is
     * rescheduled to later time to avoid to many refreshes.
     */
    
    private synchronized void refreshAll() {
        if (refreshTask != null) {
//            System.out.println("delay=" + refreshTask.getDelay());
            if (refreshTask.getDelay() > 500) {
                return;
            }
            if (refreshTask.getDelay() > 0 && refreshTask.getDelay() < 500) {
//                System.out.println("rescheduling..");
                refreshTask.schedule(1000);
                return;
            }
        }
        refreshTask = RequestProcessor.postRequest(VcsGroupChildren.this.refreshRunnable, 1000);
//        System.out.println("schedulling");
    }
    

    private Collection getFilesInGroup() {
        /** add subnodes..
         */
        LinkedList list = new LinkedList();
        Enumeration childs = folder.children(false);
        Set actions = new HashSet();
        while (childs.hasMoreElements()) {
            DataObject dos = (DataObject)childs.nextElement();
            if (dos instanceof DataShadow) {
                 DataShadow shadow = (DataShadow)dos;
                 shadow.getOriginal().removePropertyChangeListener(doPropChange);
                 shadow.getOriginal().addPropertyChangeListener(doPropChange);
                 shadow.removePropertyChangeListener(shPropChange);
                 shadow.addPropertyChangeListener(shPropChange);
                 list.add(shadow.getOriginal());
            }
            if (dos.getClass().getName().equals("org.openide.loaders.BrokenDataShadow")) { //NOI18N
                list.add(dos);
            }
        }
        return list;
    }
    
    private DataObject findShadowForDO(DataObject orig) {
        Enumeration childs = folder.children(false);
        while (childs.hasMoreElements()) {
            DataObject dos = (DataObject)childs.nextElement();
            if (dos instanceof DataShadow) {
                 DataShadow shadow = (DataShadow)dos;
                 if (orig.equals(shadow.getOriginal())) {
                     return shadow;
                 }
            }
        }
        return null;
        
    }

    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        Node newNode;
        DataObject dobj = (DataObject)key;
        if (!dobj.isValid()) {
            return new Node[0];
        }
        if (key.getClass().getName().equals("org.openide.loaders.BrokenDataShadow")) { //NOI18N
            DataObject obj = (DataObject)key;
            if (!obj.isValid()) {
                return new Node[0];
            }
            obj.addPropertyChangeListener(shPropChange);
            return new Node[] {obj.getNodeDelegate().cloneNode()};
        } else {
            DataObject obj = (DataObject)key;
            DataShadow shad = (DataShadow)findShadowForDO(obj);
            if (!obj.isValid()) {
                if (shad != null) {
                    shad.getOriginal().removePropertyChangeListener(doPropChange);
                    shad.removePropertyChangeListener(shPropChange);
                }
                return new Node[0];
            }
//            System.out.println("creating new node..");
            if (shad == null) {
                return new Node[0];
            }
            return new Node[] {new VcsGroupFileNode(shad, obj.getNodeDelegate().cloneNode()) };
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent != null && VcsGroupSettings.PROP_SHOW_LINKS.equals(propertyChangeEvent.getPropertyName())) {
            Node[] nods = getNodes();
            for (int i = 0; i < nods.length; i++) {
                if (nods[i] instanceof VcsGroupFileNode) {
                    ((VcsGroupFileNode)nods[i]).checkShowLinks();
                }
            }
            return;
        }
    }  


    // Could also write e.g. removeKey to be used by the nodes in this children.
    // Or, could listen to changes in their status (NodeAdapter.nodeDestroyed)
    // and automatically remove them from the keys list here. Etc.

    
    private class GroupFileChange extends FileChangeAdapter {
        
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fileRenameEvent) {
            refreshAll();
        }
        
        public void fileDataCreated(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileFolderCreated(org.openide.filesystems.FileEvent fileEvent) {
//            super.fileFolderCreated(fileEvent);
        }
        
        public void fileDeleted(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileChanged(org.openide.filesystems.FileEvent fileEvent) {
            refreshAll();
        }
        
        public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fileAttributeEvent) {
//            super.fileAttributeChanged(fileAttributeEvent);
        }
        
    }
    
    private class ShadowPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals(DataObject.PROP_VALID)) {
                if (propertyChangeEvent.getNewValue() != null
                && propertyChangeEvent.getNewValue() instanceof Boolean) {
                    Boolean bool = (Boolean)propertyChangeEvent.getNewValue();
                    if (bool.booleanValue() == false) {
//                    System.out.println("shadow invalid");
                        DataObject obj = (DataObject)propertyChangeEvent.getSource();
                        obj.removePropertyChangeListener(shPropChange);
                        refreshAll();
                    }
                }
            }
        }
    }
    
    private class DOPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals(DataObject.PROP_VALID)) {
                if (propertyChangeEvent.getNewValue() != null
                && propertyChangeEvent.getNewValue() instanceof Boolean) {
                    Boolean bool = (Boolean)propertyChangeEvent.getNewValue();
                    if (bool.booleanValue() == false) {
//                        System.out.println("dataobject invalied..");
                        refreshAll();
                    }
                }
            }
        }
    }

    /**
     * A runnable that is to be put into the requestprocessor, when any
     * refresh of the grouo children is requested.
     */
    public class RefreshAllTask implements Runnable {
        
        public void run() {
           setKeys(getFilesInGroup());
        }        
    }
    
}
