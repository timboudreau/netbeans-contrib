/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.bookmarks;

import java.util.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.naming.*;
import javax.naming.event.*;
import javax.swing.*;

import org.openide.ErrorManager;
import org.openide.util.actions.Presenter;
import org.openide.util.WeakListener;
import org.openide.filesystems.Repository;

/**
 * Creates a menu with submenus from a content of folder
 * on the system file system. The path to the folder is given
 * in the constructor. The resulting menu is obtained by calling
 * getMenu().
 * @author David Strupl
 */
public class MenuFromFolder implements Runnable {
    
    /** String denoting current JNDI context */
    private static final String CURRENT = ""; // NOI18N
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    
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
        } catch (NamingException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return menu;
    }

    /**
     * Adds objects from the context with path to the list arr.
     */
    private void scanContext(String path, ArrayList arr) throws NamingException {
        try {
            Context con = (Context)BookmarkServiceImpl.getInitialContext().lookup(CURRENT);
            if (!listenersAttached && con instanceof EventContext) {
                EventContext ec = (EventContext)con;
                NamingListener l1 = getNamingListener(ObjectChangeListener.class, ec);
                NamingListener l2 = getNamingListener(NamespaceChangeListener.class, ec);
                ec.addNamingListener(path, EventContext.ONELEVEL_SCOPE, l1);
                ec.addNamingListener(path, EventContext.OBJECT_SCOPE, l1);
                ec.addNamingListener(path, EventContext.SUBTREE_SCOPE, l2);
            }
            NamingEnumeration en = con.listBindings(path);
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj instanceof Presenter.Menu) {
                    Presenter.Menu m = (Presenter.Menu)obj;
                    arr.add(m.getMenuPresenter());
                    continue;
                }
                //
                if (obj instanceof Context) {
                    b.setRelative(true);
                    String n = b.getName();
                    if (n != null) {
                        MenuFromFolder mff = new MenuFromFolder(path+"/"+n);
                        arr.add(mff.getMenu());
                        continue;
                    }
                }
            }
        } catch (NameNotFoundException nnfe) {
            // no problem
            ErrorManager.getDefault().notify(nnfe);
        }
    }
    
    /**
     * Lazy initialization of the listener variable. This method
     * will return a weak listener according to the type argument.
     * In both cases the weak listener references the object hold
     * by the <code> listener </code> variable.
     * @param type ObjectChangeListener or NamespaceChangeListener
     */
    private NamingListener getNamingListener(Class type, Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        // Used to be:
        //return (NamingListener)WeakListener.create(type, listener, source);
        // but because of the remove method name we have to create the weak
        // listeners here:
        if (type.equals(ObjectChangeListener.class)) {
            return new ObjectChangeWeakListener(listener, source);
        }
        if (type.equals(NamespaceChangeListener.class)) {
            return new NamespaceChangeWeakListener(listener, source);
        }
        throw new IllegalStateException();
    }
    
    private void refreshMenuContents() {
        if (menu == null) {
            return;
        }
        // run in special thread - we don't care when this happens
        //   and if invoked directly caused neverending loops
        //   from core/naming (probably due to the fact that we
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
    private class Listener implements ObjectChangeListener, NamespaceChangeListener {
        public void namingExceptionThrown(NamingExceptionEvent evt) {
            refreshMenuContents();
        }
        
        public void objectAdded(NamingEvent evt) {
            refreshMenuContents();
        }
        
        public void objectChanged(NamingEvent evt) {
            refreshMenuContents();
        }
        
        public void objectRemoved(NamingEvent evt) {
            refreshMenuContents();
        }
        
        public void objectRenamed(NamingEvent evt) {
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
