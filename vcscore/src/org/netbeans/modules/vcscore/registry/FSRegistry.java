/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Registry of recognized filesystem types.
 *
 * @author  Martin Entlicher
 */
public class FSRegistry {
    
    private static FSRegistry registry;
    
    private List registryListeners = new LinkedList();
    private List fsInfos = new LinkedList();
    
    /** Creates a new instance of FSRegistry */
    private FSRegistry() {
        fsInfos.addAll(RecognizedFS.getDefault().getManuallyRecognized());
    }
    
    /**
     * Get the default filesystem registry.
     */
    public static synchronized FSRegistry getDefault() {
        if (registry == null) {
            registry = new FSRegistry();
        }
        return registry;
    }
    
    /**
     * Register a filesystem information.
     */
    public void register(FSInfo fsInfo) {
        register(fsInfo, null, false);
    }
    
    /**
     * Register a filesystem information.
     */
    void register(FSInfo fsInfo, Object propagationId, boolean autoRecognized) {
        synchronized (fsInfos) {
            if (fsInfos.contains(fsInfo)) {
                // Already registered
                return ;
            }
            fsInfos.add(fsInfo);
        }
        if (!autoRecognized) {
            RecognizedFS.getDefault().addManuallyRecognized(fsInfo);
        }
        fireFSInfoChanged(fsInfo, true, propagationId);
    }
    
    /**
     * Unregister a filesystem information.
     */
    public void unregister(FSInfo fsInfo) {
        unregister(fsInfo, null);
    }
    
    /**
     * Unregister a filesystem information.
     */
    void unregister(FSInfo fsInfo, Object propagationId) {
        synchronized (fsInfos) {
            fsInfos.remove(fsInfo);
        }
        RecognizedFS.getDefault().removeRecognized(fsInfo);
        fireFSInfoChanged(fsInfo, false, propagationId);
        fsInfo.destroy();
    }
    
    /**
     * Get all registered filesystem infos.
     */
    public FSInfo[] getRegistered() {
        synchronized (fsInfos) {
            return (FSInfo[]) fsInfos.toArray(new FSInfo[fsInfos.size()]);
        }
    }
    
    /**
     * Find out whether a given filesystem info is already registered or not.
     * @param info The filesystem info to test
     * @return true When the filesystem info is already registered, false otherwise.
     */
    public boolean isRegistered(FSInfo info) {
        synchronized (fsInfos) {
            return fsInfos.contains(info);
        }
    }
    
    /**
     * Add a filesystem registry listener.
     */
    public void addFSRegistryListener(FSRegistryListener fsrl) {
        synchronized (registryListeners) {
            registryListeners.add(fsrl);
        }
    }
    
    /**
     * Remove a filesystem registry listener.
     */
    public void removeFSRegistryListener(FSRegistryListener fsrl) {
        synchronized (registryListeners) {
            registryListeners.remove(fsrl);
        }
    }
    
    protected void fireFSInfoChanged(FSInfo fsInfo, boolean added, Object propagationId) {
        FSRegistryEvent evt = new FSRegistryEvent(this, fsInfo, added);
        evt.setPropagationId(propagationId);
        List listeners;
        synchronized (registryListeners) {
            listeners = new ArrayList(registryListeners);
        }
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            FSRegistryListener l = (FSRegistryListener) it.next();
            if (added) l.fsAdded(evt);
            else l.fsRemoved(evt);
        }
    }
}
