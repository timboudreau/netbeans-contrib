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

package org.netbeans.modules.vcscore.runtime;

import java.lang.ref.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.*;

import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.TopManager;
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;

import org.netbeans.modules.vcscore.VcsFileSystem;

/** 
 *
 * @author builder
 */
public class RuntimeMainChildren extends Children.Keys  {
    
    private static final String NUM_OF_FINISHED_CMDS_TO_COLLECT_CHANGED_METHOD = "numOfFinishedCmdsToCollectChanged"; // NOI18N

    private LinkedList fsList;
    
    public RuntimeMainChildren() {
        super();
    
        /** add subnodes..
         */
        fsList = new LinkedList();
        Repository repos = org.openide.TopManager.getDefault().getRepository();
        java.util.Enumeration enum = repos.getFileSystems(); 
        while (enum.hasMoreElements()) {
            FileSystem fs = (FileSystem)enum.nextElement();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(fs, bd);
            if (is) {
                fsList.add(fs);
//                initFsInRuntime(fs);
            }
        }
        repos.addRepositoryListener(new RuntimeRepositoryListener());

    }
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getFileSystems());
        RuntimeSupport.getInstance().setMainChildren(this);
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        RuntimeSupport.getInstance().setMainChildren(null);
    }

    
    private void refreshAll() {
        setKeys(getFileSystems());
    }

    private Collection getFileSystems() {
        /** add subnodes..
         */
        return fsList;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        FileSystem fs = (FileSystem)key;
        if (fsList.contains(fs)) {
            RuntimeFolderNode fsRuntime = createFolderNode(fs);
            return new Node[] { fsRuntime };
        } 
        return new Node[0];
    }
    
    private RuntimeFolderNode createFolderNode(org.openide.filesystems.FileSystem fs) {
        Children fsCh = new Index.ArrayChildren();
        RuntimeFolderNode fsRuntime = new RuntimeFolderNode(fsCh);
        fsRuntime.setName(fs.getSystemName());
        fsRuntime.setDisplayName(fs.getDisplayName());
        propagateProperty(fsRuntime, fs);
        java.beans.BeanDescriptor bd = RuntimeMainChildren.getFsBeanDescriptor(fs);
        if (bd != null) {
            String str = (String)bd.getValue(org.netbeans.modules.vcscore.VcsFileSystem.VCS_FILESYSTEM_ICON_BASE);
            if (str != null) {
                fsRuntime.setIconBase(str);
            }
        }
        RuntimeSupport.getInstance().addScheduledRuntimeNodeCommands(fsRuntime, fs.getSystemName());
        return fsRuntime;
    }
    
    private void propagateProperty(RuntimeFolderNode folder, org.openide.filesystems.FileSystem fs) {
        Method method = null;
        try {
            method = fs.getClass().getMethod(NUM_OF_FINISHED_CMDS_TO_COLLECT_CHANGED_METHOD, new Class[0]);
        } catch (NoSuchMethodException nsmExc) {
            method = null;
        } catch (SecurityException sExc) {
            method = null;
        };
        final Method numOfCmdsChangedMethod = method;
        if (numOfCmdsChangedMethod == null) return ;
        final Reference ref = new WeakReference(fs);
        final Reference folderRef = new WeakReference(folder);
        final PropertyChangeListener folderPropertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT.equals(evt.getPropertyName())) {
                    Object chFS = ref.get();
                    if (chFS != null) {
                        try {
                            numOfCmdsChangedMethod.invoke(chFS, new Object[0]);
                        } catch (IllegalAccessException iaExc) {
                        } catch (IllegalArgumentException iarExc) {
                        } catch (InvocationTargetException itExc) {
                        }
                    } else {
                        RuntimeFolderNode theFolder = (RuntimeFolderNode) folderRef.get();
                        if (theFolder != null) {
                            theFolder.removePropertyChangeListener(this);
                        }
                    }
                }
            }
        };
        folder.addPropertyChangeListener(folderPropertyChangeListener);
        folder.addNodeListener(new NodeListener() {
            public void propertyChange(PropertyChangeEvent evt) {}
            public void childrenAdded(NodeMemberEvent ev) {}
            public void childrenRemoved(NodeMemberEvent ev) {}
            public void childrenReordered(NodeReorderEvent ev) {}
            public void nodeDestroyed(NodeEvent evt) {
                RuntimeFolderNode theFolder = (RuntimeFolderNode) folderRef.get();
                if (theFolder != null) {
                    theFolder.removeNodeListener(this);
                    theFolder.removePropertyChangeListener(folderPropertyChangeListener);
                }
            }
        });
    }
    
    private boolean checkFileSystem(FileSystem fs, BeanDescriptor bd) {
        if (bd == null) return false;
        Boolean is;
        // hack to fix #17088 - multifilesystems don't get included into the runtime tab nodes..
        if (fs instanceof MultiFileSystem) {
           return false;
        }
        Object vcs = bd.getValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE);
        if (vcs instanceof Boolean) is = (Boolean) vcs;
        else is = Boolean.FALSE;
        if (is.booleanValue()) {
            if (fs instanceof VcsFileSystem) {
                VcsFileSystem vcsfs = (VcsFileSystem)fs;
                if (!vcsfs.isCreateRuntimeCommands()) {
                    is = Boolean.FALSE;
                }
            }
        }
        return is.booleanValue();
    }
    
    static BeanDescriptor getFsBeanDescriptor(FileSystem fs) {
        BeanInfo info;
        try {
            info = org.openide.util.Utilities.getBeanInfo(fs.getClass());
        } catch (java.beans.IntrospectionException intrExc) {
            return null;
        }
        if (info != null) {
            return info.getBeanDescriptor();
        }
        return null;
    }
    
    public class RuntimeRepositoryListener extends  RepositoryAdapter {
        
        public void fileSystemAdded(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(fs, bd);
            if (is) {
                fsList.add(fs);
                RuntimeMainChildren.this.setKeys(fsList);
//                RuntimeMainChildren.this.add(new Node[] {RuntimeSupport.createFolderNode(fs)});
                //RuntimeMainChildren.this.refreshKey(fs);
//                initFsInRuntime(fs);
            }
        }
        
        public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
            super.fileSystemPoolReordered(ev);
        }
        
        public void fileSystemRemoved(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(ev.getFileSystem(), bd);
            if (is) {
                fsList.remove(fs);
                RuntimeSupport.getInstance().runtimeFolderRemoved(fs.getSystemName());
                RuntimeMainChildren.this.setKeys(fsList);
                //RuntimeMainChildren.this.refreshKey(fs);
            }
            
        }
        
    }    
}
