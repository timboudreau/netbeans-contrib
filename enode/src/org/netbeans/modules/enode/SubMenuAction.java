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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.openide.ErrorManager;
import org.openide.awt.Actions;
import org.openide.util.Lookup;

import org.netbeans.modules.enode.*;
import org.openide.util.ContextAwareAction;

/**
 * Special action serving as a wrapper for submenus added to the popup
 * menu.
 * @author David Strupl
 */
public class SubMenuAction extends AbstractAction implements 
    org.openide.util.actions.Presenter.Popup, 
    org.openide.util.actions.Presenter.Menu, ContextAwareAction {

    private static ErrorManager log = ErrorManager.getDefault().getInstance(SubMenuAction.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);
    
    /** Name used for the display of this context */
    private String name;
    /** Lookup */
    private Lookup ctx;
    /** */
    private List/*<SubMenuCache.CacheEntry>*/ entries = new ArrayList();
    
    /** This is the entry representing this menu*/
    private SubMenuCache.MenuEntry currentEntry;
    
    /** Constructor used for testing */
    public SubMenuAction() {
        this("Test"); // NOI18N
    }
    
    /** Creates a new instance of SubMenuAction.
     */
    public SubMenuAction(String name) {
        this(name, null, null, null);
    }
    
    /** Creates a new instance of SubMenuAction.
     */
    public SubMenuAction(SubMenuCache.MenuEntry current) {
        this(current.getDisplayName(), null, current, null);
    }
    
    /** Creates a new instance of SubMenuAction and remembers
     * the parameters in private variables.
     */
    private SubMenuAction(String name, Lookup ctx, SubMenuCache.MenuEntry currentEntry, List/*<SubMenuCache.CacheEntry>*/ entries) {
        this.name = name;
        this.ctx = ctx;
        this.currentEntry = currentEntry;
        if (entries != null) {
            this.entries = entries;
        }
    }
    
    /**
     * This action creates a submenu. So invoking directly this
     * action does not make any sense. This method throws
     * an IllegalStateException.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        throw new IllegalStateException("SubMenuAction should not be performed as action."); // NOI18N
    }

    /**
     * Implementing the only method from ContextAwareAction.
     */
    public Action createContextAwareInstance(Lookup actionContext) {
        return new SubMenuAction(name, actionContext, currentEntry, entries);
    }
    
    /**
     * Method implementing interface Presenter.Menu.
     */
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }
    
    /**
     * Method implementing interface Presenter.Popup.
     */
    public JMenuItem getPopupPresenter() {
        return createMenu(name);
    }

    /**
     *
     */
    private JMenuItem createMenu(String name) {
        ENodePopupSubMenu menu = new ENodePopupSubMenu(name, currentEntry);
        menu.addAllCacheItems(entries);
        menu.buildMenu();
        return menu;
    }
    
    /**
     * Overriden from Object. Uses only the name field for the comparison.
     */
    public boolean equals(Object another) {
        if ((another == null) || (! (another instanceof SubMenuAction) ) ){
            return false;
        }
        SubMenuAction other = (SubMenuAction)another;
        if (name == null) {
            return other.name == null;
        }
        return name.equals(other.name);
    }
    
    /**
     * Overriden to be in sync with equals.
     */
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }

    /** 
     * True if this submenu has zero elements.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    /**
     * Registers new item to this menu.
     */
    public void addItemFromCache(SubMenuCache.CacheEntry entry) {
        entries.add(entry);
    }
    
    public void addAllCacheItems(Collection/*<SubMenuCache.CacheEntry>*/ newEntries) {
        entries.addAll(newEntries);
    }
    
    /**
     *
     */
    public Collection getCacheItries() {
        return Collections.unmodifiableList(entries);
    }
    
    /**
     *
     */
    private static CacheEntryWithSubmenuPointer insertEntry(
            List/*<CacheEntryWithSubmenuPointer>*/ list,
            SubMenuCache.CacheEntry entry,
            SubMenuCache.MenuEntry myMenu) {
        SubMenuCache.CacheEntry e = entry;
        while ((e.getParent() != null) && (!e.getParent().equals(myMenu))) {
            e = e.getParent();
        }
        if (e.getParent() == null) {
            log.log(entry + " is not under " + myMenu);
            return null;
        }
        
        CacheEntryWithSubmenuPointer fresh = new CacheEntryWithSubmenuPointer();
        fresh.topLevel = e;
        fresh.leaf = entry;
        CacheEntryWithSubmenuPointer existing = null;
        ListIterator it = list.listIterator();
        while (it.hasNext()) {
            existing = (CacheEntryWithSubmenuPointer)it.next();
            if (! e.getParent().equals(existing.topLevel.getParent())) {
                throw new IllegalStateException(existing.topLevel + " doesn't have same parent as " + e);
            }
            int entryIndex = e.getIndex();
            int existingIndex = existing.topLevel.getIndex();
            if (entryIndex < existingIndex) {
                if (it.hasPrevious()) {
                    it.previous();
                }
                it.add(fresh);
                return fresh;
            }
        }
        // if this will be the last element in the list:
        it.add(fresh);
        return fresh;
    }
    
    private class ENodePopupSubMenu extends JMenu {
        private List/*<CacheEntryWithSubmenuPointer>*/ elements = new LinkedList();
        private SubMenuCache.MenuEntry menuEntry;
        public ENodePopupSubMenu(String name, SubMenuCache.MenuEntry menuEntry) {
            super(name);
            this.menuEntry = menuEntry;
        }
        /**
         * Registers new item to this menu.
         */
        public void addItemFromCache(SubMenuCache.CacheEntry entry) {
            CacheEntryWithSubmenuPointer e = insertEntry(elements, entry, menuEntry);
        }

        public void addAllCacheItems(Collection/*<SubMenuCache.CacheEntry>*/ newEntries) {
            if (LOGGABLE) log.log("addAllCacheItems on menu" + getText());
            for (Iterator it = newEntries.iterator(); it.hasNext();) {
                SubMenuCache.CacheEntry e = (SubMenuCache.CacheEntry)it.next();
                if (LOGGABLE) log.log("addAllCacheItems adding " + e);
                addItemFromCache(e);
            }
        }
        /** Converts the items to real elements */
        public void buildMenu() {
            if (LOGGABLE) log.log("buildMenu() " + getText());
            for (Iterator it = elements.iterator(); it.hasNext();) {
                CacheEntryWithSubmenuPointer cewsp = (CacheEntryWithSubmenuPointer)it.next();
                if (LOGGABLE) log.log("buildMenu() trying to add: " + cewsp.leaf + " cewsp.topLevel: " + cewsp.topLevel);
                if (cewsp.leaf.equals(cewsp.topLevel)) {
                    if (cewsp.leaf instanceof SubMenuCache.ActionEntry) {
                        SubMenuCache.ActionEntry actionEntry = (SubMenuCache.ActionEntry)cewsp.leaf;
                        Object obj = actionEntry.getActionObject();
                        if (obj instanceof Action) {
                            Action a = (Action)obj;
                            if ((ctx != null) && a instanceof ContextAwareAction) {
                                a = ((ContextAwareAction)a).createContextAwareInstance(ctx);
                            }
                            JMenuItem menuItem = new JMenuItem();
                            Actions.connect(menuItem, a, true);
                            add(menuItem);
                        }
                        if (obj instanceof JSeparator) {
                            // create a "clone" of the separator
                            add(new JSeparator());
                        } else {
                            if (obj instanceof JComponent) {
                                add((JComponent)obj);
                            }
                        }
                    }
                } else {
                    log.log("Adding submenu for " + cewsp.leaf);
                    if (cewsp.topLevel instanceof SubMenuCache.MenuEntry) {
                        SubMenuCache.MenuEntry subMenu = (SubMenuCache.MenuEntry)cewsp.topLevel;
                        String subMenuDisplayName = subMenu.getDisplayName();
                        ENodePopupSubMenu sub = findSubMenu(subMenuDisplayName);
                        if (sub != null) {
                            sub.addItemFromCache(cewsp.leaf);
                        } else {
                            // create new one
                            ENodePopupSubMenu newSub = new ENodePopupSubMenu(subMenu.getDisplayName(), subMenu);
                            newSub.addItemFromCache(cewsp.leaf);
                            add(newSub);
                        }
                    } else {
                        log.log("cewsp.topLevel " + cewsp.topLevel + " is not MenuEntry.");
                    }
                }
            }
            Component[] subMenus = getMenuComponents();
            for (int i = 0; i < subMenus.length; i++) {
                if (subMenus[i] instanceof ENodePopupSubMenu) {
                    ENodePopupSubMenu sub = (ENodePopupSubMenu)subMenus[i];
                    sub.buildMenu();
                }
            }

        }
        
        public ENodePopupSubMenu findSubMenu(String displayName) {
            Component[] subMenus = getMenuComponents();
            for (int i = 0; i < subMenus.length; i++) {
                if (subMenus[i] instanceof ENodePopupSubMenu) {
                    ENodePopupSubMenu sub = (ENodePopupSubMenu)subMenus[i];
                    if (displayName.equals(sub.getText())) {
                        return sub;
                    }
                }
            }
            return null;
        }
    }
    
    /**
     * Invariant: 
     *     leaf == topLevel or 
     *     leaf.getParent() == topLevel or
     *     leaf.getParent().getParent() == topLevel or 
     *     ...
     */
    private static class CacheEntryWithSubmenuPointer {
        /** This is the sub menu pointer pointing
         * to the submenu in current menu that
         * is being built for leaf
         */
        public SubMenuCache.CacheEntry topLevel;
        public SubMenuCache.CacheEntry leaf;
    }
}
