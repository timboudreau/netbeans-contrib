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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList;

import org.openide.filesystems.FileSystem;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.WeakListener;

/**
 * The provider of commands for the representation on the Runtime Tab.
 *
 * @author  Martin Entlicher
 */
public abstract class RuntimeCommandsProvider {
    
    /**
     * This property is fired, when the children commands changed.
     */
    public static final String PROP_CHILDREN = "children"; // NOI18N
    
    /**
     * The name of FileObject attribute, that contains instance of RuntimeCommandsProvider
     * on VCS filesystems.
     */
    private static final String FO_ATTRIBUTE = "org.netbeans.modules.vcscore.runtime.RuntimeCommandsProvider"; // NOI18N
    
    private static List registeredProviders;
    private static List registeredListenersWeak;

    private Reference nodeDelegate = new WeakReference(null);
    private PropertyChangeSupport listenerSupport = new PropertyChangeSupport(this);
    
    /**
     * Find the runtime commands provider for a FileSystem.
     */
    public static RuntimeCommandsProvider findProvider(FileSystem fs) {
        try {
        return (RuntimeCommandsProvider) fs.getRoot().getAttribute(FO_ATTRIBUTE);
        } catch (NullPointerException npe) {
            throw (NullPointerException) org.openide.ErrorManager.getDefault().annotate(npe, "fs = "+fs+"\n"+
                "fs.getRoot() = "+((fs != null) ? ""+fs.getRoot() : "null"));
        }
    }
    
    public void register() {
        synchronized (RuntimeCommandsProvider.class) {
            if (registeredProviders == null) {
                registeredProviders = new ArrayList();
            }
            registeredProviders.add(this);
        }
        fireRegisteredListeners(null, this);
    }
    
    public void unregister() {
        boolean fire = false;
        synchronized (RuntimeCommandsProvider.class) {
            if (registeredProviders != null) {
                registeredProviders.remove(this);
                if (registeredProviders.size() == 0) registeredProviders = null;
                fire = true;
            }
        }
        if (fire) fireRegisteredListeners(this, null);
    }
    
    static synchronized RuntimeCommandsProvider[] getRegistered() {
        if (registeredProviders == null) return null;
        else return (RuntimeCommandsProvider[])
            registeredProviders.toArray(new RuntimeCommandsProvider[registeredProviders.size()]);
    }
    
    static synchronized void addRegisteredListenerWeakly(PropertyChangeListener l) {
        if (registeredListenersWeak == null) {
            registeredListenersWeak = new ArrayList();
        }
        registeredListenersWeak.add(new WeakReference(l));
    }
    
    private static synchronized void fireRegisteredListeners(Object oldValue, Object newValue) {
        if (registeredListenersWeak != null) {
            for (int i = 0; i < registeredListenersWeak.size(); i++) {
                WeakReference ref = (WeakReference) registeredListenersWeak.get(i);
                PropertyChangeListener l = (PropertyChangeListener) ref.get();
                if (l != null) {
                    l.propertyChange(new PropertyChangeEvent(RuntimeCommandsProvider.class, "", oldValue, newValue));
                } else {
                    registeredListenersWeak.remove(i--);
                }
            }
        }
    }
    
    public final synchronized Node getNodeDelegate() {
        Node node = (Node) nodeDelegate.get();
        if (node == null) {
            node = createNodeDelegate();
            nodeDelegate = new WeakReference(node);
        }
        return node;
    }
    
    protected abstract Node createNodeDelegate();
    
    /**
     * Get the node delegate if exists.
     * @return The node delegate or <code>null</code> if the node delegate is not created.
     */
    protected final Node getExistingNodeDelegate() {
        return (Node) nodeDelegate.get();
    }
    
    public abstract RuntimeCommand[] children();
    
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        listenerSupport.addPropertyChangeListener(l);
    }
    
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        listenerSupport.removePropertyChangeListener(l);
    }
    
    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        listenerSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Notify, that this runtime commands provider was added into the Runtime tab.
     * Subclasses can use this method to do various inicialization here.
     */
    protected void notifyAdded() {
    }
    
    /**
     * Notify, that this runtime commands provider was removed from the Runtime tab.
     * Subclasses can use this method to do various cleanup here.
     */
    protected void notifyRemoved() {
    }
    
    public static final class RuntimeFolderChildren extends Children.Keys implements PropertyChangeListener {
        
        private RuntimeCommandsProvider provider;
        
        public RuntimeFolderChildren(RuntimeCommandsProvider provider) {
            this.provider = provider;
            setKeys(provider.children());
            provider.addPropertyChangeListener(WeakListener.propertyChange(this, provider));
        }
        
        protected Node[] createNodes(Object obj) {
            RuntimeCommand cmd = (RuntimeCommand) obj;
            return new Node[] { cmd.getNodeDelegate() };
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (PROP_CHILDREN.equals(propertyChangeEvent.getPropertyName())) {
                setKeys(provider.children());
            }
        }
        
    }

}
