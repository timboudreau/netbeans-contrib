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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.WeakListeners;

import org.netbeans.api.enode.*;
import org.netbeans.api.registry.*;

/**
 * Object that helps the ExtensibleNode to keep track of
 * the actions. The actions are read from the layer using Registry API.
 * @author David Strupl
 */
public class ExtensibleActionsImpl extends ExtensibleActions {

    private static final Logger log = Logger.getLogger(ExtensibleActionsImpl.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);
    
    /**
     * Our paths.
     */
    private String[] paths;
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    
    /** 
     * Cache for the actions
     */
    private Action[] actions;
    
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
     * 
     */
    public ExtensibleActionsImpl(String[] paths) {
        this.paths = paths;
    }
    
    /** Reads actions from a Regisry context. Also adds a listener
     * to the context.
     * @param name of the context.
     * @return array of actions
     */
    public Action[] getActions() {
        if (LOGGABLE) log.fine("getActions() called on " + this);
        if (actions != null) {
            if (LOGGABLE) log.fine("returning cached actions: ");
            return actions;
        }
        
        // array for gathering the result
        List arr = new ArrayList ();
        // for each object from arr the location will contain String location on the SFS
        List/*<String>*/ location = new ArrayList();
        
        String cpaths[] = getCookiePaths();
        try {
            for (int i = 0; i < paths.length; i++) {
                String path = ExtensibleNode.E_NODE_ACTIONS + paths[i];
                scanContext(path, arr, location);
            }
            for (int i = 0; i < cpaths.length; i++) {
                String path = ExtensibleNode.E_NODE_ACTIONS + cpaths[i];
                scanContext(path, arr, location);
            }
            if (LOGGABLE) log("arr after scanContext", arr);
            List result = scanSubFolderContext(arr, location);
            if (LOGGABLE) log("result after scanSubFolderContext", result);
            Map m = new HashMap(); // for removing duplicate menu entries
            for (Iterator it = result.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj instanceof SubMenuAction) {
                    if (m.containsKey(obj)) {
                        // remove the duplicate
                        it.remove();
                        if (LOGGABLE) log.fine("removed " + obj);
                        // move all the elements from the removed action to its brother
                        SubMenuAction removed = (SubMenuAction)obj;
                        SubMenuAction sma = (SubMenuAction)m.get(obj);
                        if (removed != sma) { // they are equal but not the same object:
                                              // we have to pass the content of the other
                                              // one to sma.
                            if (LOGGABLE) log.fine("adding all cache entries to " + sma);
                            sma.addAllCacheItems(removed.getCacheItries());
                        }
                    } else {
                        m.put(obj, obj);
                    }
                }
            }
            if (LOGGABLE) log("result after removing duplicates", result);
            List rest = new ArrayList(arr);
            if (LOGGABLE) log("rest", rest);
            // add actions that were not configured in subfolders
            result.addAll(rest); 
            arr = result;
            
        } catch (Exception ce) {
            log.log(Level.FINE, "", ce); // NOI18N
        }
        // replace separators with nulls
        for (int i = 0; i < arr.size(); i++) {
            Object element = arr.get(i);
            if (element instanceof JSeparator) {
                arr.set(i, null);
            }
        }
        if (LOGGABLE) log("arr before returning", arr);
        listenersAttached = true; // after successfull iteration
        actions = (Action[])arr.toArray(new Action[arr.size()]);
        return actions;
    }

    /**
     * Helper method dumping the given colletion to the log.
     */
    private static void log(String name, Collection c) {
        log.fine("Dumping " + name);
        for (Iterator it = c.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj != null) {
                log.fine(obj.toString());
            } else {
                log.fine("null");
            }
        }
        log.fine("End of " + name);
    }
    
    /**
     *
     */
    private String subFoldersLocation() {
        return ExtensibleNode.E_NODE_SUBMENUS;
    }
    
    /**
     * Adds objects from the context with path to the list arr.
     */
    private void scanContext(String path, List arr, List location) {
        if (LOGGABLE) log.fine("scanContext(" + path + ",arr) called on " + this);
        boolean exists = true;
        Context con = Context.getDefault().getSubcontext(path);
        if (con == null) {
            exists = false;
            con = ExtensibleLookupImpl.findExistingContext(path);
        }
        if (!listenersAttached) {
            ContextListener l1 = getContextListener(con);
            con.addContextListener(l1);
            listenersAttachedTo.add(con);
        }

        if (! exists) {
            if (LOGGABLE) log.fine("scanContext(" + path + ",arr) returning - context does not exist.");
            return;
        }
        
        List names = con.getOrderedNames();
        Iterator it = names.iterator();
        while (it.hasNext()) {
            String objName = (String)it.next();
            Object obj = con.getObject(objName, null);
            String absName = con.getAbsoluteContextName()+"/"+objName;
            if (absName.startsWith("/")) {
                absName = absName.substring(1);
            }
            if (LOGGABLE) log.fine("scanContext(...) checking " + obj + " with absName " + absName);
            if (obj instanceof Action) {
                arr.add(obj);
                location.add(absName);
                if (LOGGABLE) log.fine("scanContext(...) adding " + obj + " with name " + absName);
                continue;
            }
            // special handling of separators since they are represented
            // as nulls in the actions array
            if (obj instanceof JSeparator) {
                arr.add(obj);
                location.add(absName);
                if (LOGGABLE) log.fine("scanContext(...) adding " + obj + " with name " + absName);
                continue;
            }
            // general JComponents are wrapped in special actions
            if (obj instanceof JComponent) {
                Action a = new ComponentAction((JComponent)obj);
                arr.add(a);
                location.add(absName);
                if (LOGGABLE) log.fine("scanContext(...) adding " + a + " with name " + absName);
                continue;
            }
        }
    }

    /**
     * Returns list of actions configured via the SubMenu
     * folder.
     */
    private List/*<Action>*/ scanSubFolderContext(List/*<Object>*/objects, List location) {
        String path = subFoldersLocation();
        if (LOGGABLE) log.fine("scanSubFolderContext(...) with " + path + ") called on " + this);
        List/*<ActionCacheEntryPair>*/ linkedList = new LinkedList();
        boolean exists = true;
        Context con = Context.getDefault().getSubcontext(path);
        if (con == null) {
            exists = false;
            con = ExtensibleLookupImpl.findExistingContext(path);
        }
        if (!listenersAttached) {
            ContextListener l1 = getContextListener(con);
            con.addContextListener(l1);
            listenersAttachedTo.add(con);
        }

        if (! exists) {
            if (LOGGABLE) log.fine("scanSubFolderContext(...) returning empty list because " + path + " does not exist.");
            return new ArrayList();
        }
        
        Iterator locationIt = location.iterator();
        for (Iterator it = objects.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            String objLocation = (String)locationIt.next();
            if (LOGGABLE) log.fine("scanSubFolderContext(...) checking " + obj + " with location " + objLocation);
            if (objLocation == null) {
                throw new IllegalStateException("Did not find location for " + obj);
            }
            SubMenuCache.CacheEntry entry = SubMenuCache.getInstance().getCacheEntry(objLocation);
            if (entry == null) {
                String origLocation = tryToResolveShadow(objLocation);
                entry = SubMenuCache.getInstance().getCacheEntry(origLocation);
                if (entry == null) {
                    if (LOGGABLE) log.fine(obj + " with location " + objLocation + " was not found in cache");
                    continue;
                }
            }
            ActionCacheEntryPair pair = new ActionCacheEntryPair();
            if (entry.getParent().getParent() == null) {
                pair.entry = entry;
                if (obj instanceof Action) {
                    pair.action = (Action)obj;
                    // successfully added to result --> remove from the original list
                    it.remove();
                    locationIt.remove();
                }
                // general JComponents are wrapped in special actions
                if ((obj instanceof JComponent) && (! (obj instanceof JSeparator))) {
                    pair.action = new ComponentAction((JComponent)obj);
                    // successfully added to result --> remove from the original list
                    it.remove();
                    locationIt.remove();
                }
                // special handling of separators since they are represented
                // as nulls in the actions array
                if (obj instanceof JSeparator) {
                    // we remove the separator from the original collection
                    // because it is now represented as pair in the linkedList
                    it.remove();
                    locationIt.remove();
                }
            } else {
                SubMenuCache.CacheEntry origEntry = entry;
                // it is in some subfolder
                while (entry.getParent().getParent() != null) {
                    entry = entry.getParent();
                }
                pair.entry = entry;
                if (entry instanceof SubMenuCache.MenuEntry) {
                    SubMenuAction sma = new SubMenuAction((SubMenuCache.MenuEntry)entry);
                    pair.action = sma;
                    sma.addItemFromCache(origEntry);
                    // successfully added to result --> remove from the original list
                    it.remove();
                    locationIt.remove();
                } else {
                    log.fine(entry + " is not MenuEntry! adding null instead of " + obj);
                }
            }
            insertPair(linkedList, pair);
        }
        ArrayList arr = new ArrayList();
        for (Iterator it = linkedList.iterator(); it.hasNext();) {
            ActionCacheEntryPair acep = (ActionCacheEntryPair)it.next();
            arr.add(acep.action);
        }
        return arr;
    }

    private String tryToResolveShadow(String shadowLocation) {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(shadowLocation + ".shadow");
        if (fo != null) {
            String origPathAttr = (String)fo.getAttribute("originalFile");
            if (origPathAttr == null) {
                log.fine("Shadow file " + fo.getPath() + " is missing the originalFile attribute");
                return null;
            }
            FileObject origAction = Repository.getDefault().getDefaultFileSystem().findResource(origPathAttr);
            if (origAction == null) {
                log.fine("originalFile attribute (" + origPathAttr + ") of " + fo.getPath() + " does not reference existing action.");
                return null;
            }
            int lastDotIndex = origPathAttr.lastIndexOf('.');
            String pathWithoutExt = origPathAttr.substring(0, lastDotIndex);
            return pathWithoutExt;
        }
        log.fine("tryToResolveShadow returning null for " + shadowLocation);
        return null;
    }
    
    /**
     *
     */
    private static void insertPair(List/*<ActionCacheEntryPair>*/ list, ActionCacheEntryPair pair) {
        ActionCacheEntryPair existing = null;
        ListIterator it = list.listIterator();
        while (it.hasNext()) {
            existing = (ActionCacheEntryPair)it.next();
            if (! pair.entry.getParent().equals(existing.entry.getParent())) {
                throw new IllegalStateException(pair.entry + " and " + existing.entry + " don't have the same parent");
            }
            int pairIndex = pair.entry.getIndex();
            int existingIndex = existing.entry.getIndex();
            if (pairIndex < existingIndex) {
                if (it.hasPrevious()) {
                    ActionCacheEntryPair previous = (ActionCacheEntryPair)it.previous();
                }
                it.add(pair);
                return;
            }
        }
        // if this will be the last element in the list:
        it.add(pair);
    }
    
    /**
     * Computes additional paths for actions configured
     * via adding an action to a cookie instance.
     */
    private String[] getCookiePaths() {
        if (LOGGABLE) log.fine("getCookiePaths() called on " + this);
        ArrayList names = new ArrayList();
        for (int i = 0; i < paths.length; i++) {
            String path = ExtensibleNode.E_NODE_LOOKUP + paths[i];
            if (LOGGABLE) log.fine("getCookiePaths() checking " + path);
            try {
                boolean exists = true;
                Context con = Context.getDefault().getSubcontext(path);
                if (con == null) {
                    con = ExtensibleLookupImpl.findExistingContext(path);
                    exists = false;
                }
                if (!listenersAttached) {
                    ContextListener l1 = getContextListener(con);
                    con.addContextListener(l1);
                    listenersAttachedTo.add(con);
                }
                if (exists) {
                    Collection objects = con.getBindingNames();
                    if (LOGGABLE) {
                        log.fine("getCookiePaths() adding names:");
                        for (Iterator it = objects.iterator(); it.hasNext();) {
                            Object tmp = it.next();
                            if (tmp != null) {
                                log.fine( tmp.toString());
                            }
                        }
                    }
                    names.addAll(objects);
                }
            } catch (Exception ce) {
                log.log(Level.FINE,"", ce); // NOI18N
            }
        }
        if (LOGGABLE) log.fine("getCookiePaths() returning " + names.size() + " names.");
        return (String[])names.toArray(new String[names.size()]);
    }

    public String toString() {
        String res = "ExtensibleActionsImpl[";
        if (paths != null) {
            res += "path=";
            for (int i = 0; i < paths.length; i++) {
                res += paths[i] + ",";
            }
        }
        if (actions != null) {
            res += "actions=";
            for (int i = 0; i < actions.length; i++) {
                res += actions[i] + ",";
            }
        }
        return res + "]";
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
        if (LOGGABLE) log.fine(this + " adding context listener to " + source);
        return (ContextListener)WeakListeners.create(ContextListener.class, listener, source);
    }
    
    /**
     * Whatever happens in the selected context this listener only clears
     * the actions reference. This cause the list of actions to
     * be computed next time someone asks for them.
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            if (LOGGABLE) log.fine("attributeChanged("+evt+") called on listener from " + ExtensibleActionsImpl.this);
            actions = null;
        }
        
        public void bindingChanged(BindingEvent evt) {
            if (LOGGABLE) log.fine("bindingChanged("+evt+") called on listener from " + ExtensibleActionsImpl.this);
            actions = null;
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            if (LOGGABLE) log.fine("subcontextChanged("+evt+") called on listener from " + ExtensibleActionsImpl.this);
            actions = null;
        }
    }
    
    private static class ActionCacheEntryPair {
        public Action action;
        public SubMenuCache.CacheEntry entry;
    }
}
