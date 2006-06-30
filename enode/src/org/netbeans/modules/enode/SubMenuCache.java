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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import org.netbeans.api.registry.AttributeEvent;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextListener;
import org.netbeans.api.registry.SubcontextEvent;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.netbeans.api.enode.ExtensibleNode;
import org.openide.util.WeakListeners;

/**
 * Caches the information contained in the folder /ExtensibleNode/SubMenu/.
 * @author David Strupl
 */
class SubMenuCache {
    
    private static ErrorManager log = ErrorManager.getDefault().getInstance(SubMenuCache.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);

    /** Extension of the files we are interested in (in the SubMenu folder). */
    private static final String SHADOW_EXTENSION = "shadow";
    
    
    /** Singleton instance of this class. */
    private static SubMenuCache instance;
    
    /**
     * For fast finding the entry associated with the path. Maps:
     *   String: path to the original action (in the Actions/ folder
     *      to
     *   SubMenuCacheEntry representing the action in this cache
     */
    private Map/*<String,CacheEntry>*/ pathToEntry;
    
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
    
    /** Creates a new instance of SubMenuCache */
    private SubMenuCache() {
        buildTheCache();
    }
    
    /**
     * Lazy creation of the singleton instance of this class.
     * NEVER keep the resulting reference for long. Always ask this method
     * if you need it.
     */
    public static SubMenuCache getInstance() {
        if (instance == null) {
            instance = new SubMenuCache();
        }
        return instance;
    }

    /**
     * Finds the cached entry for given path. If there is none returns null.
     */
    public CacheEntry getCacheEntry(String originalPath) {
        return (CacheEntry)getPathToEntryMap().get(originalPath);
    }
    
    private FileObject getSubMenusRoot() {
        return Repository.getDefault().getDefaultFileSystem().findResource(
                ExtensibleNode.E_NODE_SUBMENUS);
    }
    
    private void buildTheCache() {
        long startTime = System.currentTimeMillis();
        FileObject root = getSubMenusRoot();
        Context con = Context.getDefault().getSubcontext(root.getPath());
        if (con == null) {
            if (LOGGABLE) log.log("buildTheCache() returning - SubMenu context does not exist.");
            return;
        }
        if (!listenersAttached) {
            ContextListener l1 = getContextListener(con);
            con.addContextListener(l1);
            listenersAttachedTo.add(con);
        }
        MenuEntry rootMenu = scanFolder(root, null);
        getPathToEntryMap().put("", rootMenu);
        long finishTime = System.currentTimeMillis();
        log.log(ErrorManager.USER, "SubMenuCache building has taken " + (finishTime - startTime));
        if (LOGGABLE) log.log(this.toString());
    }
    
    /**
     * Creates part of the cache by traversing the given folder
     * and creating SubMenuCacheEntries.
     */
    private MenuEntry scanFolder(FileObject folder, MenuEntry parent) {
        if (LOGGABLE) log.log("scanFolder(" + folder.getPath() + ") START");
        String displayName = folder.getName();
        try {
            displayName = folder.getFileSystem ().getStatus ().annotateName(folder.getName(), Collections.singleton(folder));
        } catch (Exception x) {
            log.notify(ErrorManager.EXCEPTION, x);
        }
        MenuEntry result = new MenuEntry(displayName, parent);
        // in order to get the order we need Registry API:
        Context con = Context.getDefault().getSubcontext(folder.getPath());
        List orderedNames = con.getOrderedNames();
        for (Iterator it = orderedNames.iterator(); it.hasNext();) {
            String name = (String) it.next();
            if (LOGGABLE) log.log("scanFolder checking " + name);
            if (name.endsWith("/")) {
                name = name.substring(0, name.length()-1);
            }
            FileObject child = folder.getFileObject(name);
            if (child == null) {
                // try with extension:
                child = folder.getFileObject(name, SHADOW_EXTENSION);
            }
            if (child == null) {
                log.log("child == null: Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (! child.isValid()) {
                log.log("!child.isValid(): Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (child.isData()) {
                String ext = child.getExt();
                if (!SHADOW_EXTENSION.equals(ext)) {
                    log.log("Only .shadows files are allowed in SubMenu folder. Illegal file: " + child.getPath());
                    continue;
                }
                String origPathAttr = (String)child.getAttribute("originalFile");
                if (origPathAttr == null) {
                    log.log("Shadow file " + child.getPath() + " is missing the originalFile attribute");
                    continue;
                }
                FileObject origAction = Repository.getDefault().getDefaultFileSystem().findResource(origPathAttr);
                if (origAction == null) {
                    log.log("originalFile attribute (" + origPathAttr + ") of " + child.getPath() + " does not reference existing action.");
                    continue;
                }
                int lastDotIndex = origPathAttr.lastIndexOf('.');
                String pathWithoutExt = origPathAttr.substring(0, lastDotIndex);
                if (LOGGABLE) log.log("adding result " + result + " with path " + pathWithoutExt);
                ActionEntry ae = new ActionEntry(pathWithoutExt, result);
                result.addChild(ae);
                getPathToEntryMap().put(pathWithoutExt, ae);
            }
            if (child.isFolder()) {
                scanFolder(child, result);
            }
        }
        if (parent != null) {
            parent.addChild(result);
        }
        return result;
    }
    
    /** Lazy init of pathToEntry */
    private Map getPathToEntryMap() {
        if (pathToEntry == null) {
            pathToEntry = new HashMap();
        }
        return pathToEntry;
    }

    /** Debugging output */
    public String toString() {
        String result =  "SubMenuCache[";
        CacheEntry root = getCacheEntry("");
        result += convertToString(0, root);
        return result + "]";
    }
    
    /**
     * Called from toString(). Calls toString() on all cached elements.
     */
    private String convertToString(int indent, CacheEntry entry) {
        StringBuffer sb = new StringBuffer(150);
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
        sb.append(entry.toString() + "\n");
        if (entry instanceof MenuEntry) {
            MenuEntry me = (MenuEntry)entry;
            for (int i = 0; i < me.getChildrenCount(); i++) {
                sb.append(convertToString(indent + 4, me.getChild(i)));
            }
        }
        return sb.toString();
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
     * Entry for one action or folder.
     */
    public static class CacheEntry {
        /** Parent in our cache and also in the storage under SubMenu folder. */
        private MenuEntry parent;
        
        public CacheEntry(MenuEntry parent) {
            this.parent = parent;
        }
        
        /** returns index in the parent menu */
        public int getIndex() {
            if (parent == null) {
                return 0;
            }
            return parent.getChildIndex(this);
        }
        
        public MenuEntry getParent() {
            return parent;
        }
    }
    
    /**
     * Entry for one folder.
     */
    public static class MenuEntry extends CacheEntry {
        /**
         * Children of the menu are actions or menus
         */
        private List/*<CacheEntry>*/ children;
        
        /** Text for the menu item:*/
        private String displayName;
        
        public MenuEntry(String displayName, MenuEntry parent) {
            super(parent);
            this.displayName = displayName;
            children = new ArrayList();
        }
        
        public CacheEntry getChild(int index) {
            return (CacheEntry) children.get(index);
        }
        
        public void addChild(CacheEntry child) {
            children.add(child);
        }
        
        public int getChildIndex(CacheEntry child) {
            return children.indexOf(child);
        }

        public int getChildrenCount() {
            return children.size();
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String toString() {
            return "MenuEntry[" + displayName +",childrenCount=" + children.size() + "]";
        }
    }
    
    /**
     * Entry for one action.
     */
    public static class ActionEntry extends CacheEntry {
        private String originalActionPath;
        public ActionEntry(String originalActionPath, MenuEntry parent) {
            super(parent);
            this.originalActionPath = originalActionPath;
        }
        public String toString() {
            return "ActionEntry["+originalActionPath+",parent="+getParent()+"]";
        }
        /**
         * Returns one of the following:
         *      Action
         *      JSeparator
         *      JComponent
         */
        public Object getActionObject() {
            int lastSlash = originalActionPath.lastIndexOf('/');
            String contextName = originalActionPath.substring(0, lastSlash);
            Context ctx = Context.getDefault().getSubcontext(contextName);
            if (ctx == null) {
                throw new IllegalStateException("Context " + contextName + " was not found.");
            }
            Object obj = ctx.getObject(originalActionPath.substring(lastSlash+1), null);
            if (obj instanceof Action) {
                return obj;
            }
            if (obj instanceof JSeparator) {
                return obj;
            }
            if (obj instanceof JComponent) {
                return obj;
            }
            throw new IllegalStateException("Path " + originalActionPath + " cannot be converted to Action.\n" +
                    "Object with name " + originalActionPath.substring(lastSlash+1) + " was not found in " + ctx);
        }
    }
    
    /**
     * Whatever happens in the selected context this listener only clears
     * the actions reference. This cause the list of actions to
     * be computed next time someone asks for them.
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            if (LOGGABLE) log.log("attributeChanged("+evt+") called on listener from " + SubMenuCache.this);
            instance = null;
        }
        
        public void bindingChanged(BindingEvent evt) {
            if (LOGGABLE) log.log("bindingChanged("+evt+") called on listener from " + SubMenuCache.this);
            instance = null;
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            if (LOGGABLE) log.log("subcontextChanged("+evt+") called on listener from " + SubMenuCache.this);
            instance = null;
        }
    }

}
