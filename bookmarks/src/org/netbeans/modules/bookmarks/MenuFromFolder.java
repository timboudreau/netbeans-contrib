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

package org.netbeans.modules.bookmarks;

import java.util.*;
import javax.swing.*;

import org.openide.ErrorManager;
import org.openide.util.actions.Presenter;
import org.openide.util.WeakListener;
import org.openide.filesystems.Repository;

import org.netbeans.api.registry.*;

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
    public MenuFromFolder(String path) {
        this.path = path;
    }
    
    /** Reads actions from a JNDI context. Also adds a listener
     * to the context.
     * @param name of the context.
     * @return array of actions
     */
    public JMenu getMenu() {
        String s = path;
        StringTokenizer tok = new StringTokenizer(path, "/"); // NOI18N
        while (tok.hasMoreTokens()) {
            s = tok.nextToken();
        }
        // now there is last token from the path in variable s --> name of the folder
        if (menu == null) {
            menu = new MyMenu(s);
        } else {
            menu.removeAll();
        }
        if (fixedItems != null) {
            for (int i = 0; i < fixedItems.length; i++) {
                menu.add(fixedItems[i]);
            }
            menu.addSeparator();
        }
        ArrayList arr = new ArrayList ();
        try {
            scanContext(path, arr);
            listenersAttached = true; // after successfull scan
            for (Iterator i = arr.iterator(); i.hasNext();) {
                menu.add((JMenuItem)i.next());
            }
        } catch (ContextException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return menu;
    }

    /**
     * Adds objects from the context with path to the list arr.
     */
    private void scanContext(String path, ArrayList arr) throws ContextException {
        context = Context.getDefault().createSubcontext(path);
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
                MenuFromFolder mff = new MenuFromFolder(path + "/" + n);
                arr.add(mff.getMenu());
                continue;
            }
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
        return (ContextListener)WeakListener.create(ContextListener.class, listener, source);
    }
    
    private void refreshMenuContents() {
        if (menu == null) {
            return;
        }
        // run in special thread - we don't care when this happens
        //   and if invoked directly caused neverending loops
        //   from core (probably due to the fact that we
        //   possibly retrigger refreshing from the listener
       java.awt.EventQueue.invokeLater(this);
    }
    
    /*
     * Implementing Runnable. Used from refreshMenuContents to
     * replan for later.
     */
    public void run() {
        menu = getMenu();
    }
    
    /**
     * Whatever happens in the selected context this listener calls
     * refreshMenuContents.
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            refreshMenuContents();
        }
        
        public void bindingChanged(BindingEvent evt) {
            refreshMenuContents();
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            refreshMenuContents();
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
    }
}
