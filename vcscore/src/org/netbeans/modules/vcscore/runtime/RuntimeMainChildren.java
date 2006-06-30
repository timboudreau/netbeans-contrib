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
