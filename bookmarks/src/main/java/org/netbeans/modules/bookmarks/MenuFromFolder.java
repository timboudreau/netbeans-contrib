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

package org.netbeans.modules.bookmarks;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import org.openide.util.actions.Presenter;
import org.openide.util.WeakListeners;

import org.netbeans.api.registry.*;
import org.openide.util.RequestProcessor;

/**
 * Creates a menu with submenus from a content of folder
 * on the system file system. The path to the folder is given
 * in the constructor. The resulting menu is obtained by calling
 * getMenu().
 * @author David Strupl
 */
public class MenuFromFolder implements Runnable {
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    
    /**
     * We hold a reference to the context for preventing
     * the garbage collection.
     */
    private Context context;
    
    /** 
     * Cache for the created menu.
     */
    private JMenu menu;
    
    /**
     * Items at the beginning of the menu that are not
     * from the underlying folder.
     */
    private JMenuItem[] fixedItems;

    /**
     * Path of the folder with the contents of this menu.
     */
    private String path;
    
    /**
     * Prevent the listeners to be attached more than once.
     */
    private boolean listenersAttached = false;
    
    /**
     * Keeps track of the task spawned from refreshMenuContents.
     */
    private RequestProcessor.Task refreshTask = null;
    
    /**
     * Should we read the values from the storage?
     */
    private boolean refreshNeeded = true;
    
    /**
     * Map: path (String) --> WeakReference(MenuFromFolder)
     */
    private static Map instanceCache = new HashMap();
    
    /**
     * Parameter path is path in the JNDI root context.
     * fixedItems are items that are not changing with the content
     * of the folder. They are put at the top of the menu.
     */
    public MenuFromFolder(String path, JMenuItem[] fixedItems) {
        this(path);
        this.fixedItems = fixedItems;
    }
    
    /**
     * Parameter path is path in the JNDI root context.
     */
    private MenuFromFolder(String path) {
        this.path = path;
    }
    
    /**
     * Reads actions from a context. Also adds a listener
     * to the context.
     */
    public JMenu getMenu() {
        if ((! refreshNeeded) && (menu != null)) {
            return menu;
        }
        if ((path.length() > 0) && (path.charAt(0) == '/')) {
            path = path.substring(1);
        }
        String s = path.substring(path.lastIndexOf('/')+1);
        Context ctx = Context.getDefault().getSubcontext(path);
        if (ctx != null) {
            s = ctx.getAttribute(null, org.openide.nodes.Node.PROP_DISPLAY_NAME, s);
        }
        if (menu == null) {
            menu = new MyMenu(s);
        } else {
            menu.removeAll();
            menu.setText(s);
        }
        if (fixedItems != null) {
            for (int i = 0; i < fixedItems.length; i++) {
                menu.add(fixedItems[i]);
            }
            menu.addSeparator();
        }
        ArrayList arr = new ArrayList ();
        if (scanContext(path, arr)) {
            listenersAttached = true; // after successfull scan
            for (Iterator i = arr.iterator(); i.hasNext();) {
                menu.add((JMenuItem)i.next());
            }
            refreshNeeded = false;
        }
        return menu;
    }

    /**
     * Adds objects from the context with path to the list arr.
     */
    private boolean scanContext(String path, ArrayList arr) {
        if (listenersAttached) {
            context = Context.getDefault().getSubcontext(path);
        } else {
            try {
                context = Context.getDefault().createSubcontext(path);
            } catch (ContextException ce) {
                Logger.getLogger(MenuFromFolder.class.getName()).log(
                    Level.WARNING, path, ce);
            }
        }
        if (context == null) {
            return false;
        }
        if (!listenersAttached) {
            ContextListener l1 = getContextListener(context);
            context.addContextListener(l1);
        }
        Iterator it = context.getOrderedNames().iterator();
        while (it.hasNext()) {
            String n = (String)it.next();
            Object obj = context.getObject(n, null);
            if (obj instanceof Presenter.Menu) {
                Presenter.Menu m = (Presenter.Menu)obj;
                arr.add(m.getMenuPresenter());
                continue;
            }
            //
            Context c = context.getSubcontext(n);
            if (c != null) {
                MenuFromFolder mff = instance(path + "/" + n);
                arr.add(mff.getMenu());
                continue;
            }
        }
        return true;
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
    
    private void refreshMenuContents(int time) {
        if (menu == null) {
            return;
        }
        // run in special thread - we don't care when this happens
        //   and if invoked directly caused neverending loops
        //   from core (probably due to the fact that we
        //   possibly retrigger refreshing from the listener
        if (refreshTask == null) {
            refreshTask = RequestProcessor.getDefault().create(
                new Runnable() {
                    public void run() {
                        java.awt.EventQueue.invokeLater(MenuFromFolder.this);
                    }
                }
            );
        }
        refreshTask.schedule(time);
    }
    
    /*
     * Implementing Runnable. Used from refreshMenuContents to
     * replan for later.
     */
    public void run() {
        refreshNeeded = true;
        getMenu();
    }
    
    /**
     * Factory method preventing excessive creation of instances of this
     * class.
     */
    public static MenuFromFolder instance(String path) {
        WeakReference wr = (WeakReference)instanceCache.get(path);
        if (wr != null) {
            MenuFromFolder cachedValue = (MenuFromFolder)wr.get();
            if (cachedValue != null) {
                return cachedValue;
            }
        }
        MenuFromFolder newValue = new MenuFromFolder(path);
        instanceCache.put(path, new WeakReference(newValue));
        return newValue;
    }
    
    /**
     * Whatever happens in the selected context this listener calls
     * refreshMenuContents.
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            refreshMenuContents(200);
        }
        
        public void bindingChanged(BindingEvent evt) {
            refreshMenuContents(200);
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            refreshMenuContents(200);
        }
        
    }
    
    /** This class is here to keep a reference to the enclosing class.
     * It is not static so the this$0 reference is the one that keeps
     * the instance of MenuFromFolder alive as long as someone keeps
     * a reference to the MyMenu.
     */
    private class MyMenu extends JMenu {
        public MyMenu() {}
        public MyMenu(String s) { super(s); }
        public String toString() {
            return "MyMenu: " + this.hashCode(); // NOI18N
        }
    }
}
