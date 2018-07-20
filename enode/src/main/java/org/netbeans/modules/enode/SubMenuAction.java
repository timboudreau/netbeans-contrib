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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import org.openide.awt.Actions;
import org.openide.util.Lookup;

import org.openide.awt.DynamicMenuContent;
import org.openide.util.ContextAwareAction;
import org.openide.util.actions.BooleanStateAction;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;

/**
 * Special action serving as a wrapper for submenus added to the popup
 * menu.
 * @author David Strupl
 */
public class SubMenuAction extends AbstractAction implements 
    org.openide.util.actions.Presenter.Popup, 
    org.openide.util.actions.Presenter.Menu, ContextAwareAction {

    private static final Logger log = Logger.getLogger(SubMenuAction.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);
    
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
            log.fine(entry + " is not under " + myMenu);
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
            if (LOGGABLE) log.fine("addAllCacheItems on menu" + getText());
            SubMenuCache.CacheEntry e;
            Collection entryND = new ArrayList();
            for(Iterator it = newEntries.iterator(); it.hasNext(); ) {
                e = (SubMenuCache.CacheEntry)it.next();
                if(!entryND.contains(e)) {
                    if (LOGGABLE) log.fine("addAllCacheItems adding " + e);
                    entryND.add(e);
                } else { 
                    if (LOGGABLE) log.fine("addAllCacheItems removing duplicate " + e);
                    it.remove();
                }
            }
            e = null;
            for (Iterator it = entryND.iterator(); it.hasNext();) {
                e = (SubMenuCache.CacheEntry)it.next();
                addItemFromCache(e);
            }
        }
        
        /** Converts the items to real elements */
        public void buildMenu() {
            if (LOGGABLE) log.fine("buildMenu() " + getText());
            for (Iterator it = elements.iterator(); it.hasNext();) {
                CacheEntryWithSubmenuPointer cewsp = (CacheEntryWithSubmenuPointer)it.next();
                if (LOGGABLE) log.fine("buildMenu() trying to add: " + cewsp.leaf + " cewsp.topLevel: " + cewsp.topLevel);
                if (cewsp.leaf.equals(cewsp.topLevel)) {
                    if (cewsp.leaf instanceof SubMenuCache.ActionEntry) {
                        SubMenuCache.ActionEntry actionEntry = (SubMenuCache.ActionEntry)cewsp.leaf;
                        Object obj = actionEntry.getActionObject();
                        if (obj instanceof Action) {
                            Action a = (Action)obj;
                            if ((ctx != null) && a instanceof ContextAwareAction) {
                                a = ((ContextAwareAction)a).createContextAwareInstance(ctx);
                            }
                            JMenuItem item;
                            if (a instanceof Presenter.Popup) {
                                item = ((Presenter.Popup) a).getPopupPresenter();
                                
                                if (item == null) {
                                    NullPointerException npe = new NullPointerException(
                                            "buildMenu, getPopupPresenter returning null for " + a
                                            ); // NOI18N
                                    log.log(Level.FINE, "", npe);
                                }
                            } else {
                                item = createPopupPresenter(a);
                            }
                            Component[] comps = convertComponents(item);
                            for (int v = 0; v < comps.length;v++) {
                                add(comps[v]);
                            }
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
                    log.fine("Adding submenu for " + cewsp.leaf);
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
                        log.fine("cewsp.topLevel " + cewsp.topLevel + " is not MenuEntry.");
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
    
    /*
     * Copy from org.netbeans.modules.openide.awt.DefaultAWTBridge
     */
    private static JMenuItem createPopupPresenter(Action action) {
        if (action instanceof BooleanStateAction) {
            BooleanStateAction b = (BooleanStateAction)action;
            return new Actions.CheckboxMenuItem (b, false);
        }
        if (action instanceof SystemAction) {
            SystemAction s = (SystemAction)action;
            return new Actions.MenuItem (s, false);
        }
        return new Actions.MenuItem (action, false);
    }
    
    /*
     * Copy from org.netbeans.modules.openide.awt.DefaultAWTBridge
     */
    private static Component[] convertComponents(Component comp) {
         if (comp instanceof DynamicMenuContent) {
            Component[] toRet = ((DynamicMenuContent)comp).getMenuPresenters();
            boolean atLeastOne = false;
            Collection<Component> col = new ArrayList<Component>();
            for (int i = 0; i < toRet.length; i++) {
                if (toRet[i] instanceof DynamicMenuContent && toRet[i] != comp) {
                    col.addAll(Arrays.asList(convertComponents(toRet[i])));
                    atLeastOne = true;
                } else {
                    if (toRet[i] == null) {
                        toRet[i] = new JSeparator();
                    }
                    col.add(toRet[i]);
                }
            }
            if (atLeastOne) {
                return col.toArray(new Component[col.size()]);
            } else {
                return toRet;
            }
         }
         return new Component[] {comp};
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
