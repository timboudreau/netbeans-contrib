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
//                initFsInRuntime(fs, bd);
            }
        }
        repos.addRepositoryListener(new RuntimeRepositoryListener());

    }
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getFileSystems());
//        getDefaultGroupNode(); //hack here to create the default group by default..
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
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
            RuntimeFolderNode fsRuntime = RuntimeSupport.createFolderNode(fs);
            return new Node[] { fsRuntime };
        } 
        return new Node[0];
    }
    
    private boolean checkFileSystem(FileSystem fs, BeanDescriptor bd) {
        if (bd == null) return false;
        Boolean is;
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
    
    
    private void initFsInRuntime(FileSystem fs) {
        if (fs instanceof VcsFileSystem) {
            VcsFileSystem vcsfs = (VcsFileSystem)fs;
            vcsfs.getCommandsPool().setupRuntime();
        }
    }    
    
    public class RuntimeRepositoryListener extends  RepositoryAdapter {
        
        public void fileSystemAdded(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(fs, bd);
            if (is) {
                fsList.add(fs);
                RuntimeMainChildren.this.refreshKey(fs);
                initFsInRuntime(fs);
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
                RuntimeMainChildren.this.refreshKey(fs);
            }
            
        }
        
    }    
}
