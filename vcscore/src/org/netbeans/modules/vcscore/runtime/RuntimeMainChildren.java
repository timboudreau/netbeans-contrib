/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
import org.openide.loaders.*;
import org.openide.util.*;
import java.beans.*;

/** 
 *
 * @author builder
 */
public class RuntimeMainChildren extends Children.Keys  {
    
    private static final String NUM_OF_FINISHED_CMDS_TO_COLLECT_CHANGED_METHOD = "numOfFinishedCmdsToCollectChanged"; // NOI18N

    private LinkedList providerList;
    private RuntimeRepositoryListener rrl = new RuntimeRepositoryListener();
    
    public RuntimeMainChildren() {
        super();
    
        /** add subnodes..
         */
        providerList = new LinkedList();
        Repository repos = org.openide.filesystems.Repository.getDefault();
        java.util.Enumeration enum = repos.getFileSystems(); 
        while (enum.hasMoreElements()) {
            FileSystem fs = (FileSystem) enum.nextElement();
            RuntimeCommandsProvider provider = RuntimeCommandsProvider.findProvider(fs);
            if (provider != null && !providerList.contains(provider)) {
                providerList.add(provider);
//                initFsInRuntime(fs);
            }
        }
        repos.addRepositoryListener(WeakListener.repository(rrl, repos));
        RuntimeCommandsProvider[] providers = RuntimeCommandsProvider.getRegistered();
        RuntimeCommandsProvider.addRegisteredListenerWeakly(rrl);
        if (providers != null) providerList.addAll(Arrays.asList(providers));
    }
    

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        setKeys (getProviders());
    }

    /** Called when all children are garbage collected *
    protected void removeNotify() {
        System.out.println(" !!  !! removeNotify(), FS size = "+getProviders().size()+"\n");
    }
     */

    private Collection getProviders() {
        /** add subnodes..
         */
        return providerList;
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        RuntimeCommandsProvider provider = (RuntimeCommandsProvider) key;
        if (providerList.contains(provider)) {
            Node fsRuntime = provider.getNodeDelegate();
            if (fsRuntime != null) return new Node[] { fsRuntime };
        } 
        return null;
    }
    
    
    public class RuntimeRepositoryListener extends  RepositoryAdapter implements PropertyChangeListener {
        
        public void fileSystemAdded(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            RuntimeCommandsProvider provider = RuntimeCommandsProvider.findProvider(fs);
            if (provider != null && !providerList.contains(provider)) {
                providerList.add(provider);
                RuntimeMainChildren.this.setKeys(providerList);
            }
        }
        
        public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
            super.fileSystemPoolReordered(ev);
        }
        
        public void fileSystemRemoved(RepositoryEvent ev) {
            FileSystem fs = ev.getFileSystem();
            RuntimeCommandsProvider provider = RuntimeCommandsProvider.findProvider(fs);
            if (provider != null && !containsProvider(ev.getRepository(), provider)) {
                if (providerList.remove(provider)) {
                    provider.notifyRemoved();
                    RuntimeMainChildren.this.setKeys(providerList);
                }
            }
        }
        
        /** Whether the Repository still contains the provider or not. */
        private boolean containsProvider(Repository repository, RuntimeCommandsProvider provider) {
            for (Enumeration enum = repository.fileSystems(); enum.hasMoreElements(); ) {
                FileSystem fs = (FileSystem) enum.nextElement();
                if (provider.equals(RuntimeCommandsProvider.findProvider(fs))) {
                    return true;
                }
            }
            return false;
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            RuntimeCommandsProvider oldProvider = (RuntimeCommandsProvider) propertyChangeEvent.getOldValue();
            RuntimeCommandsProvider newProvider = (RuntimeCommandsProvider) propertyChangeEvent.getNewValue();
            if (oldProvider != null) providerList.remove(oldProvider);
            if (newProvider != null) {
                providerList.add(newProvider);
                newProvider.notifyRemoved();
            }
            RuntimeMainChildren.this.setKeys(providerList);
        }
        
    }    
}
