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

package org.netbeans.core.naming;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;

import org.openide.cookies.InstanceCookie;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;


/**
 * Support for <code>EventDOContext</code>. Manages listeners
 * registered at the context.
 * <b>Note: This class is thread-safe.</b>.
 *
 * @author Peter Zavadsky
 */
class EventDOContextSupport {

    /** Maps target name->list of listeners on that target. */
    private final Map targets2listeners = new HashMap(30);
    
    /** Set of <code>NamespaceChangeListener</code>S. */
    private final List namespaceListeners = new ArrayList(20);
    /** Set of <code>ObjectChangeListener</code>S. */
    private final List objectListeners = new ArrayList(20);
    
    /** Set of listeners with <code>EventContext.OBJECT_SCOPE</code>. */
    private final List scopeObjectListeners = new ArrayList(20);
    /** Set of listeners with <code>EventContext.ONELEVEL_SCOPE</code>. */
    private final List scopeLevelListeners = new ArrayList(20);
    /** Set of listeners with <code>EventContext.SUBTREE_SCOPE</code>. */
    private final List scopeTreeListeners = new ArrayList(20);
    
    
    /** Creates a new instance of ContextListeners */
    public EventDOContextSupport() {
    }

    
    public synchronized void addNamingListener(Name name, int scope,
    NamingListener l) {
        List list = (List)targets2listeners.get(name);
        if(list == null) {
            list = new ArrayList(7);
            targets2listeners.put(name, list);
        }
        
        list.add(l);

        boolean added = false;
        if(l instanceof NamespaceChangeListener) {
            namespaceListeners.add(l);
            added = true;
        } 
        if(l instanceof ObjectChangeListener) {
            objectListeners.add(l);
            added = true;
        } 
        if(!added) {
            throw new IllegalArgumentException(
                    "Not supported listener type " + l); // NOI18N
        }
        
        if(scope == EventContext.OBJECT_SCOPE) {
            scopeObjectListeners.add(l);
        } else if(scope == EventContext.ONELEVEL_SCOPE) {
            scopeLevelListeners.add(l);
        } else if(scope == EventContext.SUBTREE_SCOPE) {
            scopeTreeListeners.add(l);
        } else {
            throw new IllegalArgumentException(
                    "Not supported scope " + scope); // NOI18N
        }
        
    }

    public synchronized void removeNamingListener(NamingListener l) {
        for(Iterator it = targets2listeners.values().iterator(); it.hasNext(); ) {
            List list = (List)it.next();
            list.remove(l);
            
            if(list.isEmpty()) {
                it.remove();
            }
        }
        
        namespaceListeners.remove(l);
        objectListeners.remove(l);
        
        scopeObjectListeners.remove(l);
        scopeLevelListeners.remove(l);
        scopeTreeListeners.remove(l);
    }

    
    public void fireTargetAdded(Name target, EventContext source, Hashtable env,
    FileObject file, Object info) {
        List listeners = findListenersForTarget(target, true);
        
        // Fire events.
        if(!listeners.isEmpty()) {
            NamingEvent evt = new NamingEvent(
                source,
                NamingEvent.OBJECT_ADDED,
                new FileBinding(target, env, file),
                null,
                info);

            for(Iterator it = listeners.iterator(); it.hasNext(); ) {
                NamespaceChangeListener nl = (NamespaceChangeListener)it.next();
                nl.objectAdded(evt);
            }
        }

    }
    
    public void fireTargetRemoved(Name target, EventContext source,
    Hashtable env, FileObject file, Object info) {
        List listeners = findListenersForTarget(target, true);
        
        // Fire event.
        if(!listeners.isEmpty()) {
            NamingEvent evt = new NamingEvent(
                source,
                NamingEvent.OBJECT_REMOVED,
                null,
                new FileBinding(target, env, file),
                info);

            for(Iterator it = listeners.iterator(); it.hasNext(); ) {
                NamespaceChangeListener nl = (NamespaceChangeListener)it.next();
                nl.objectRemoved(evt);
            }
        }
    }
    
    public void fireTargetRenamed(Name target, EventContext source,
    Hashtable env, FileObject file, Object info, Name oldName) {
        List listeners = findListenersForTarget(oldName, true);
        
        // Fire event.
        if(!listeners.isEmpty()) {
            NamingEvent evt = new NamingEvent(
                source,
                NamingEvent.OBJECT_RENAMED,
                new FileBinding(target, env, file),
                new FileBinding(oldName, env, file),
                info);

            for(Iterator it = listeners.iterator(); it.hasNext(); ) {
                NamespaceChangeListener nl = (NamespaceChangeListener)it.next();
                nl.objectRenamed(evt);
            }
        }
    }
    
    public void fireTargetChanged(Name target, EventContext source,
    Hashtable env, FileObject file, Object info) {
        List listeners = findListenersForTarget(target, false);

        // Fire event.
        if(!listeners.isEmpty()) {
            NamingEvent evt = new NamingEvent(
                source,
                NamingEvent.OBJECT_CHANGED,
                new FileBinding(target, env, file),
                new Binding(target.toString(), null),
                info);

            for(Iterator it = listeners.iterator(); it.hasNext(); ) {
                ObjectChangeListener ol = (ObjectChangeListener)it.next();
                ol.objectChanged(evt);
            }
        }

    }

    private synchronized List findListenersForTarget(
    Name target, boolean namespaceType) {
        // Object scope.
        List listeners = findObjectScopeListenersForTarget(
                target, namespaceType);
        // One level scope.
        Name parentName = getParentName(target);
        if(parentName != null) {
            listeners.addAll(findLevelScopeListenersForTarget(
                    parentName, namespaceType));
        }
        // Tree scope.
        Name name = target;
        while(name != null) {
            listeners.addAll(findTreeScopeListenersForTarget(
                    name, namespaceType));
            name = getParentName(name);
        }
        
        return listeners;
    }
    
    private List findObjectScopeListenersForTarget(Name target,
    boolean namespaceType) {
        return findScopeListenersForTarget(
                target, EventContext.OBJECT_SCOPE, namespaceType);
    }

    private List findLevelScopeListenersForTarget(Name target,
    boolean namespaceType) {
        return findScopeListenersForTarget(
                target, EventContext.ONELEVEL_SCOPE, namespaceType);
    }
    
    private List findTreeScopeListenersForTarget(Name target,
    boolean namespaceType) {
        return findScopeListenersForTarget(
                target, EventContext.SUBTREE_SCOPE, namespaceType);
    }

    private List findScopeListenersForTarget(Name target, int scope,
    boolean namespaceType) {
        List listeners = new ArrayList();
        
        List targetListeners = (List)targets2listeners.get(target);
        if(targetListeners != null) {
            listeners.addAll(targetListeners);
            // Has listeners.
            if(scope == EventContext.OBJECT_SCOPE) {
                listeners.retainAll(scopeObjectListeners);
            } else if(scope == EventContext.ONELEVEL_SCOPE) {
                listeners.retainAll(scopeLevelListeners);
            } else if(scope == EventContext.SUBTREE_SCOPE) {
                listeners.retainAll(scopeTreeListeners);
            }

            if(namespaceType) {
                listeners.retainAll(namespaceListeners);
            } else {
                listeners.retainAll(objectListeners);
            }
        }
        
        return listeners;
    }
    
    
    /** Gets parent name, from specified name.
     * @return parent name or <code>null</code> if no exists. */
    private static Name getParentName(Name name) {
        // When jdk1.4 use this:
        // List components = Collections.list(name.getAll());
        List components = new ArrayList();
        Enumeration cs = name.getAll();
        while(cs.hasMoreElements()) {
            components.add(cs.nextElement());
        }
        
        if(!components.isEmpty()) {
            // Check if it is already a root name.
            if(components.isEmpty() 
            || (components.size() == 1
                    && components.get(0).equals("")) ) { // NOI18N
                return null;
            }
            
            // Remove the last element.
            components.remove(components.size() - 1);
            
            try {
                if(components.isEmpty()) {
                    return new CompositeName(""); // NOI18N
                }
                
                Name cn = new CompositeName();
                for(Iterator it = components.iterator(); it.hasNext(); ) {
                    cn.add((String)it.next());
                }
                return cn;
            } catch(InvalidNameException ine) {
                ErrorManager.getDefault().notify(ine);
            }
        }
        
        return null;
    }
    
    
    /** Binding for a file object. */
    private static final class FileBinding extends Binding {
        private FileObject fo;
        private Hashtable env;
        
        public FileBinding(Name target, Hashtable env, FileObject fo) {
            super(target.toString(), null);
            this.fo = fo;
            this.env = env;
        }
        
        public String getClassName() {
            DataObject obj;
            try {
                obj = DataObject.find(fo);
            } catch(DataObjectNotFoundException dnfe) {
                ErrorManager.getDefault().notify(dnfe);
                return null;
            }
            
            if (obj instanceof DataFolder) {
                return DOContext.class.getName ();
            } else {
                try {
                    InstanceCookie ic = (InstanceCookie)obj.getCookie (InstanceCookie.class);
                    if (ic != null) {
                        return ic.instanceClass().getName ();
                    }
                } catch(IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                } catch (ClassNotFoundException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
                
                return null;
            }
        }
        
        public Object getObject() {
            DataObject obj;
            try {
                obj = DataObject.find(fo);
            } catch(DataObjectNotFoundException dnfe) {
                ErrorManager.getDefault().notify(dnfe);
                return null;
            }
            
            if (obj instanceof DataFolder) {
                try {
                    return DOContext.find (env, obj.getPrimaryFile ());
                } catch (NamingException ex) {
                    ErrorManager.getDefault ().notify (ex);
                    return null;
                }
            } else {
                try {
                    return Utils.instanceCreate (obj, env, false);
                } catch (NamingException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
                
                return null;
            }
        }
    }
    
}

