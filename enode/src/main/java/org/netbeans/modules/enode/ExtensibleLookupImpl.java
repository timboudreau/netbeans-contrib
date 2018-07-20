/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger log = Logger.getLogger(ExtensibleLookupImpl.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);
    /**
     * Maps List<String> --> List. The key is list of
     * folders passed as paths parameter or computed by
     * ExtensibleNode.computeHierarchicalPaths().
     */
    private static Map cache = new HashMap();

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
     * To prevent garbage collection of context where we attached
     * listeners. We just add items to the set and never do anything
     * with them. But that is the reason why it is here - to hold
     * strong references to the Context objects.
     */
    private Set listenersAttachedTo = new HashSet();
    
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
    @Override protected void beforeLookup (Template template) {
        super.beforeLookup(template);
        
        if (settingLookups) {
            if (LOGGABLE) {
                log.fine(this + " beforeLookup exiting because settingLookups == true"); // NOI18N
                Thread.dumpStack();
            }
            return;
        }
        
        if (LOGGABLE) {
            log.fine(this + " beforeLookup " + template.getType().getName() + " thread: " + Thread.currentThread());
        }
        Iterator c = getCandidates();
        
        while (c.hasNext()) {
            Object o = c.next();
            if (LOGGABLE) {
                log.fine(this + " candidate " + o + " thread: " + Thread.currentThread());
            }
            if (o instanceof LookupContentFactory) {
                LookupContentFactory lcf = (LookupContentFactory)o;
                if (lcf instanceof FactoryWrapper) {
                    // in this case we avoid initialization of the objects
                    // that we know not to be returned anyway
                    FactoryWrapper impl = (FactoryWrapper)lcf;
                    if (! impl.matches(template)) {
                        if (LOGGABLE) {
                            log.fine(this + " continue " + " thread: " + Thread.currentThread());
                            log.fine(impl + " did not match " + template);
                        }
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
                    if (LOGGABLE) {
                        log.fine(this + " adding lookup " + resLookup + " thread: " + Thread.currentThread());
                    }
                    addLookup(resLookup);
                }
                if (resObject != null) {
                    if (LOGGABLE) {
                        log.fine(this + " adding " + resObject + " thread: " + Thread.currentThread());
                    }
                    content.add(resObject);
                }
            } else {
                if (! (o instanceof Context)) { // Context is directory -- not interested in warning
                    log.info(
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
            String[] whereToSearch = enode.getPaths();
            c = getCandidatesCachedInstance(whereToSearch);
            listenersAttached = true;
        }
        
        synchronized (lock) {
            if (candidates == null) {
                // use a copy of the static candidates cache:
                candidates = new ArrayList(c);
            }
            // return a copy to make sure two threads have distinct copies
            List result = new LinkedList(candidates);
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
                con = findExistingContext(path);
                exists = false;
            }
            if (!listenersAttached) {
                ContextListener l1 = getContextListener(con);
                con.addContextListener(l1);
                listenersAttachedTo.add(con);
            }
            if (exists) {
                List objects = con.getOrderedObjects();
                result.addAll(objects);
            }
        } catch (Exception ce) {
            log.log(Level.FINE, "", ce); // NOI18N
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
        return WeakListeners.create(ContextListener.class, listener, source);
    }
    
    /**
     * Change content of the lookup after the listener detected a change
     * on the system file system.
     */
    private void changeContent() {
        synchronized (lock) {
            candidates = null;
            cache = new HashMap();
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
    
    /**
     * Tries to find an existing context that is created from given path.
     * If context with given path does not exist this method tries to walk
     * up to the parent until an existing one is found.
     */
    static Context findExistingContext(String path) {
        String result = path;
        Context con = Context.getDefault().getSubcontext(result);
        while (con == null) {
            int slash = result.lastIndexOf('/');
            if (slash < 0) {
                if (LOGGABLE) {
                    log.fine("Cound not find proper context for " + path); // NOI18N
                } // NOI18N
                return Context.getDefault();
            }
            result = result.substring(0, slash);
            con = Context.getDefault().getSubcontext(result);
        }
        return con;
    }
    
    /**
     * Caches the candidates list in the static cache.
     */
    public List getCandidatesCachedInstance(String[] paths) {
        // We use list as the key. It ensures that the hashCode will
        // be same for the arrays of equals String instances.
        Object key = Arrays.asList(paths);
        
        TimedSoftReference ref = null;
        synchronized (cache) {
            ref = (TimedSoftReference)cache.get(key);
        }
        List instance = null;
        if (ref != null) {
            instance = (List)ref.get();
        }
        if (instance == null) {
            instance = new LinkedList();
            for (int i = 0; i < paths.length; i++) {
                computeCandidates(paths[i], instance);                
            }
            synchronized (cache) {
                cache.put(key, new TimedSoftReference(instance, cache, key));
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        String res = "ExtensibleLookupImpl[";
        res += "enode=" + enode;
        res += ",candidates="+ candidates;
        return res + "]";
    }
    
}
