/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
    private RuntimeProviderListener rpl = new RuntimeProviderListener();
    
    public RuntimeMainChildren() {
        super();
    
        /** add subnodes..
         */
        providerList = new LinkedList();
        RuntimeCommandsProvider[] providers = RuntimeCommandsProvider.getRegistered();
        RuntimeCommandsProvider.addRegisteredListenerWeakly(rpl);
        if (providers != null) providerList.addAll(Arrays.asList(providers));
    }
    
    private void refreshKeys(final Collection collection) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setKeys(collection);
            }
        });
    }

    /** Called when the preparetion of nodes is needed
     */
    protected void addNotify() {
        refreshKeys (getProviders());
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
    
    
    public class RuntimeProviderListener extends Object implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            RuntimeCommandsProvider oldProvider = (RuntimeCommandsProvider) propertyChangeEvent.getOldValue();
            RuntimeCommandsProvider newProvider = (RuntimeCommandsProvider) propertyChangeEvent.getNewValue();
            if (oldProvider != null) {
                if (providerList.remove(oldProvider) && newProvider != null) {
                    newProvider.notifyRemoved();
                }
            }
            if (newProvider != null) {
                providerList.add(newProvider);
            }
            RuntimeMainChildren.this.refreshKeys(providerList);
        }
        
    }    
}
