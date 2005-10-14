/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.spi.enode.LookupContentFactory;
import org.netbeans.api.registry.*;

/**
 * Special lookup capable of reading its content from the system
 * file system.
 * @author David Strupl
 */
public class ExtensibleLookupImpl extends ProxyLookup {

    private static ErrorManager log = ErrorManager.getDefault().getInstance(SubMenuCache.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);

    /** enode we (lookup) belong to */
    private ExtensibleNode enode;
    
    /** Content of the lookup containing all the intances (not lookups) 
      * from the layer. */
    private InstanceContent content;
    
    /** Candidates for including in this lookup (found by Registry). This
     * cache is null at the beginning, than filled with candidates and
     * finally the candidates are removed as they are examined.
     */
    private List candidates;
    
    /** lock for candidates modifications */
    private Object lock = new Object();
    
    /** To avoid recursion */
    private boolean settingLookups = false;
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    /**
     * Prevent the listeners to be attached more than once.
     */
    private boolean listenersAttached = false;
    
    /**
     * setExtensibleNode has to be called shortly after using this
     * constructor otherwise this class will not work properly
     */
    public ExtensibleLookupImpl() {
        super(new Lookup[0]);
    }

    /** Sets the enode variable and finishes the initialization.
     * This method has to be called shortly after the constructor -
     * before the enode variable is needed.
     */
    public final void setExtensibleNode(ExtensibleNode en) {
        this.enode = en;
        Lookup instances = new AbstractLookup(content = new InstanceContent());
        setLookups(new Lookup[] { instances });
    }
    
    /** Notifies subclasses that a query is about to be processed.
     * @param template the template 
     */
    protected void beforeLookup (Template template) {
        super.beforeLookup(template);
        
        if (settingLookups) {
            if (LOGGABLE) {
                log.log(this + " beforeLookup exiting because settingLookups == true"); // NOI18N
                Thread.dumpStack();
            }
            return;
        }
        
        if (LOGGABLE) log.log("beforeLookup " + template.getType().getName());
        Iterator c = getCandidates();
        
        while (c.hasNext()) {
            Object o = c.next();
            if (LOGGABLE) log.log("candidate " + o);
            if (o instanceof LookupContentFactory) {
                LookupContentFactory lcf = (LookupContentFactory)o;
                if (lcf instanceof FactoryWrapper) {
                    // in this case we avoid initialization of the objects
                    // that we know not to be returned anyway
                    FactoryWrapper impl = (FactoryWrapper)lcf;
                    if (! impl.matches(template)) {
                        if (LOGGABLE) log.log("continue");
                        continue;
                    }
                }
                // remove object o (lcf, impl) from the list of candidates
                synchronized (lock) {
                    // we can do this here even if there are more threads in this
                    // method since we are iterating through a copy (getCandidates returned a copy
                    // of candidates)
                    if (candidates != null) {
                        candidates.remove(o);
                    }
                }

                Object resObject = lcf.create(enode); 
                Lookup resLookup = lcf.createLookup(enode);
                if (resLookup != null) {
                    if (LOGGABLE) log.log("adding lookup " + resLookup);
                    addLookup(resLookup);
                }
                if (resObject != null) {
                    if (LOGGABLE) log.log("adding " + resObject);
                    content.add(resObject);
                }
            } else {
                if (! (o instanceof Context)) { // Context is directory -- not interested in warning
                    ErrorManager.getDefault().log(ErrorManager.WARNING,
                        "For " + enode + // NOI18N
                        " ExtensibleNodeLookup found an object that is not LookupContentFactory : " + o); // NOI18N
                }
            }
        }
    }

    /**
     * Adds additional lookup to this proxy lookup.
     */
    private void addLookup(Lookup l) {
        Lookup []old = getLookups();
        for (int i = 0; i < old.length; i++) {
            if (old[i] == l) {
                // it is already there - do nothing
                return;
            }
        }
        Lookup []newL = new Lookup[old.length + 1];
        System.arraycopy(old, 0, newL, 0, old.length);
        newL[old.length] = l;
        try {
            settingLookups = true;
            setLookups(newL);
        } finally {
            settingLookups = false;
        }
    }
    
    /**
     * Check the cache of candidates and return an iterator.
     * !!! The iterator can change between successive calls to this
     * method due to removing candidates !!!
     * This method returns an iterator from a copy of the actual candidates list
     */
    private Iterator getCandidates() {
        List c = null;
        if (candidates == null) {
            // we are called for the first time
            c = new LinkedList();
            String[] whereToSearch = enode.getPaths();
            for (int i = 0; i < whereToSearch.length; i++) {
                computeCandidates(whereToSearch[i], c);                
            }
            listenersAttached = true;
        }
        
        synchronized (lock) {
            if (candidates == null) {
                candidates = c;
            }
            // return a copy to make sure two threads have distinct copies
            List result = new ArrayList(candidates);
            return result.iterator();
        }
    }
    
    /**
     * Consults Registry to get the list of applicable objects.
     */
    private void computeCandidates(String name, List result) {
        String path = ExtensibleNode.E_NODE_LOOKUP + name;
        try {
            boolean exists = true;
            Context con = Context.getDefault().getSubcontext(path);
            if (con == null) {
                con = Context.getDefault();
                exists = false;
            }
            if (!listenersAttached) {
                ContextListener l1 = getContextListener(con);
                con.addContextListener(l1);
            }
            if (exists) {
                List objects = con.getOrderedObjects();
                result.addAll(objects);
            }
        } catch (Exception ce) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.enode").notify(ErrorManager.INFORMATIONAL, ce); // NOI18N
        }
    }
    
    /**
     * Lazy initialization of the listener variable. This method
     * will return a weak listener.
     * The weak listener references the object hold
     * by the <code> listener </code> variable.
     */
    private ContextListener getContextListener(Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        return (ContextListener)WeakListeners.create(ContextListener.class, listener, source);
    }
    
    /**
     * Change content of the lookup after the listener detected a change
     * on the system file system.
     */
    private void changeContent() {
        synchronized (lock) {
            candidates = null;
        }
        Lookup instances = new AbstractLookup(content = new InstanceContent());
        setLookups(new Lookup[] { instances });
    }
    
    /**
     * Whatever happens in the selected context this listener only calls
     * changeContent. 
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            changeContent();
        }
        
        public void bindingChanged(BindingEvent evt) {
            changeContent();
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            changeContent();
        }
    }
}
