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

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.openide.filesystems.*;
import org.netbeans.modules.vcscore.VcsFileSystem;
import java.beans.*;

/**
 * The folder node, which contains RuntimeFolderNodes nodes.
 *
 * @author  Milos Kleint
 */
public class RuntimeMainNode extends AbstractNode {
    
 
    public static final String VCS_RUNTIME_NODE_NAME = "VcsRuntime";
    
    /** Creates new RuntimeFolderNode */
    public RuntimeMainNode(Children children) {
        super(children);
        setName(VCS_RUNTIME_NODE_NAME);
        setDisplayName(g("CTL_VcsRuntime"));
        setIconBase("/org/netbeans/modules/vcscore/runtime/commandIcon");
        Repository repos = org.openide.TopManager.getDefault().getRepository();
        java.util.Enumeration enum = repos.getFileSystems(); 
        while (enum.hasMoreElements()) {
            FileSystem fs = (FileSystem)enum.nextElement();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(fs, bd);
            if (is) {
                initFsInRuntime(fs, bd);
            }
        }
        repos.addRepositoryListener(new RuntimeRepositoryListener());
    }
    
    public RuntimeMainNode() {
        this(new Children.Array());
    }
    

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeMainNode.class).getString(name);
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
    
    private RuntimeFolderNode initFsInRuntime(FileSystem fs, BeanDescriptor bd) {
        RuntimeFolderNode folder;
        if (fs instanceof VcsFileSystem) {
            VcsFileSystem vcsfs = (VcsFileSystem)fs;
            vcsfs.getCommandsPool().setupRuntime();
            folder = RuntimeSupport.findRuntime(fs.getSystemName());
        } else {
            folder = RuntimeSupport.initRuntime(fs);
        }
        return folder;
    }
    
    public class RuntimeRepositoryListener extends  RepositoryAdapter {
        
        public void fileSystemAdded(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            BeanDescriptor bd = getFsBeanDescriptor(fs);
            boolean is = checkFileSystem(fs, bd);
            if (is) {
                initFsInRuntime(fs, bd);
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
                RuntimeFolderNode folder = RuntimeSupport.findRuntime(fs.getSystemName());
                if (folder != null) {
                try {
                    folder.destroy();
                } catch (java.io.IOException exc) {}
                }
            }
            
        }
        
    }

}
