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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;
import javax.naming.*;
import javax.naming.event.*;

import javax.swing.Action;

import org.openide.ErrorManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.RequestProcessor;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;

import org.netbeans.api.bookmarks.*;

/**
 * Implementation of the BookmarkService. Stores the bookmarks in
 * folder BOOKMARKS_FOLDER and the top components in TOP_COMPONENTS_FOLDER.
 * @author David Strupl
 */
public class BookmarkServiceImpl extends BookmarkService {
    
    /** Folder name on the system file system where the
     * user bookmarks are stored
     */
    public static final String BOOKMARKS_FOLDER = "Bookmarks/Bookmarks";
    
    /**
     * Folder for storing the TopComponents referenced from the bookmarks
     */
    public static final String TOP_COMPONENTS_FOLDER = "Bookmarks/TopComponents";
    
    /** Folder name on the system file system where the
     * user bookmarks are stored
     */
    public static final String BOOKMARKS_TOOLBAR = "Toolbars/Bookmarks";
    
    /** 
     * Folder name on the system file system where the bookmarks are
     * registered.
     */
    public static final String BOOKMARKS_ACTIONS = "Actions/Bookmarks";
    
    /**
     * Folder name on the system file system where the shortcuts
     * are stored. This folder is used by the core.
     */
    private static final String SHORTCUTS_FOLDER = "Shortcuts";
    
    /** Cache of the initial context */
    private static Context incon;
    
    /** String denoting current JNDI context */
    private static final String CURRENT = ""; // NOI18N
    
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
     * Keeps track of the task spawned from the constructor.
     */
    private RequestProcessor.Task initTask = null;
    
    /** This constructor is public for the META-INF/services lookup
     * to be able to instantiate this service
     */
    public BookmarkServiceImpl() {
        checkActionsFolder();
        
        // we run this in a separate thread because the
        // code checking the bookmarks might use BookmarkServiceImpl
        // but here we are in its constructor. Also we start
        // it later to make sure this constructor is finished
        initTask = RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                checkTopComponentsFolder();
            }
        }, 100);
    }
    
    /**
     * By invoking this method you are asking to store the bookmark
     * in the storage maintained by the service. It means that after
     * calling this method your bookmark will appear in the bookmarks menu
     * (or in whatever visual presentation the bookmarks module uses).
     * After the bookmark is stored the user will be able to invoke
     * your bookmark - the method invoke will be called on it
     * after the user clicks the menu or toolbar representation of
     * the bookmark. <p> Example: you want to create and store a bookmark
     * for your TopComponent tc you can use: <pre>
     * BookmarkService.getDefault().storeBookmark(b);
     * </pre>. Where b is either your own implementation of the interface
     * Bookmark or result of calling method createDefaultBookmark. 
     * This or similar code is used in the action provided by
     * the module in the main menu bar called "Add Bookmark".
     * @see Bookmark
     */
    public void storeBookmark(Bookmark b) {
        if (initTask != null) {
            initTask.waitFinished();
        }
        try {
            Context c = getInitialContext();
            Object folder = c.lookup(BOOKMARKS_FOLDER);
            Object tcFolder = c.lookup(TOP_COMPONENTS_FOLDER);
            if ((folder instanceof Context) && (tcFolder instanceof Context)){
                Context targetFolder = (Context)folder;
                Context targetTcFolder = (Context)tcFolder;
                String safeName = findUnusedName(targetFolder, b.getName());
                // and top component to the tc folder
                if (b instanceof BookmarkImpl) {
                    BookmarkImpl bi = (BookmarkImpl)b;
                    targetTcFolder.rebind(safeName, bi.getTopComponent());
                    bi.setTopComponentFileName(safeName);
                }
                // following line will save the bookmark to the system file system
                targetFolder.bind(safeName, b);
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
    }
    
    /**
     * This method creates a Bookmark object saving the state of the
     * supplied TopComponent. Invoking such a bookmark means to open
     * a clone of the TopComponent.
     * @returns default implementation of the Bookmark interface
     */
    public Bookmark createDefaultBookmark(TopComponent tc) {
        BookmarkProvider bp = (BookmarkProvider)tc.getLookup().lookup(BookmarkProvider.class);
        Bookmark b = null;
        if (bp != null) {
            b = bp.createBookmark();
        }
        if (b == null) {
            b = new BookmarkImpl(tc);
        }
        return b;
    }
    
    /**
     * Tries to find unused name in the specified context by
     * trying to append a number to the specified name.
     */
    public static String findUnusedName(Context targetFolder, String name) {
        String result = name;
        try {
            int i = 0;
            while (true) {
                targetFolder.lookup(result);
                result = name + i++;
            }
        } catch (NamingException ne) {
            // if the name is not found --> fine, we can return it
            return result;
        }
    }
    
    /**
     * Lazy initialization of the initial context
     */
    public static Context getInitialContext() throws NamingException {
        if (incon == null) {
            incon = (Context)new InitialContext().lookup("nbres:/");
        }
        return incon;
    }

    /**
     * Loads top component from the top components folder.
     */
    public TopComponent loadTopComponent(String name) {
        try {
            Context c = getInitialContext();
            Object folder = c.lookup(TOP_COMPONENTS_FOLDER);
            if (folder instanceof Context) {
                Context targetFolder = (Context)folder;
                TopComponent tc = (TopComponent)targetFolder.lookup(name);
                return tc;
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
        }
        return null;
    }
    
    /**
     * This method scans the BOOKMARKS_ACTIONS folder and tries to
     * delete unused bookmarks. The unused bookmarks are those that
     * are not present in the BOOKMARKS_FOLDER or BOOKMARKS_TOOLBAR folder.
     * On the other hand
     * it calls ensureAllActionsAreInActions() to make sure that all
     * actions in the BOOKMARKS_FOLDER and BOOKMARKS_TOOLBAR 
     * are also in BOOKMARKS_ACTIONS.
     */
    private void checkActionsFolder() {
        try {
            Context con = (Context)getInitialContext().lookup(CURRENT);
            if (!listenersAttached && con instanceof EventContext) {
                EventContext ec = (EventContext)con;
                NamingListener l2 = getNamingListener(ec);
                ec.addNamingListener(BOOKMARKS_FOLDER, EventContext.SUBTREE_SCOPE, l2); // NOI18N
                ec.addNamingListener(BOOKMARKS_TOOLBAR, EventContext.SUBTREE_SCOPE, l2); // NOI18N
                listenersAttached = true;
            }
            
            ArrayList toDelete = new ArrayList();
            Context c1 = (Context)con.lookup(BOOKMARKS_FOLDER);
            Context c2 = (Context)con.lookup(BOOKMARKS_TOOLBAR);
            
            NamingEnumeration en = con.listBindings(BOOKMARKS_ACTIONS);
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj instanceof Action) {
                    
                    if ( ( ! checkBookmarkAction((Action)obj, c1)) && 
                        ( ! checkBookmarkAction((Action)obj, c2)) ) {
                        toDelete.add(b);
                    }
                }
            }
            // delete unused bookmarks
            for (Iterator i = toDelete.iterator(); i.hasNext();) {
                Binding b = (Binding)i.next();
                b.setRelative(false);
                con.unbind(BOOKMARKS_ACTIONS+ "/" + b.getName());
            }
            // create new items in the Actions/Bookmarks folder
            ensureAllActionsAreInActions(c1);
            ensureAllActionsAreInActions(c2);
            // after the action has been updated force the reload
            // of active shortcuts
            refreshShortcutsFolder();
        } catch (NamingException ne) {
            ErrorManager.getDefault().notify(ne);
        }
    }
    
    /**
     * Tries to find given action in the bookmarks folders.
     * @returns true if the given action a is found in the context con
     *      (the search is performed recursively)
     */
    private boolean checkBookmarkAction(Action a, Context con) {
        try {
            NamingEnumeration en = con.listBindings(CURRENT);
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj.equals(a)) {
                    return true;
                }
                if (obj instanceof Context) {
                    if (checkBookmarkAction(a, (Context)obj)) {
                        return true;
                    }
                }
            }
        } catch (NamingException ne) {
            ErrorManager.getDefault().notify(ne);
        }
        return false;
    }
    
    /**
     * Takes all actions in the given context 
     * (the search is performed recursively) and checks whether the
     * actions are also in BOOKMARKS_ACTIONS folder. If the action
     * is not there it is added (to the BOOKMARKS_ACTIONS folder).
     */
    private void ensureAllActionsAreInActions(Context con) {
        try {
            NamingEnumeration en = con.listBindings(CURRENT);
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj instanceof Action) {
                    Action a = (Action)obj;
                    Context c = (Context)getInitialContext().lookup(CURRENT);
                    Object aFolder = c.lookup(BOOKMARKS_ACTIONS);
                    if (aFolder instanceof Context) {
                        Context targetFolder = (Context)aFolder;
                        if (! checkBookmarkAction(a, targetFolder)) {
                            // the following will save the bookmark to the actions folder
                            // to be usable for shortcuts
                            String safeName = findUnusedName(targetFolder, b.getName());
                            targetFolder.bind(safeName, a);
                        }
                    } else {
                        System.err.println("WARNING: " + BOOKMARKS_ACTIONS + " not found!");
                    }
                }
                if (obj instanceof Context) {
                    ensureAllActionsAreInActions((Context)obj);
                }
            }
        } catch (NamingException ne) {
            ErrorManager.getDefault().notify(ne);
        }
    }
    
    /**
     * Tries to find and delete all unused top components in
     * the top components folder.
     */
    private void checkTopComponentsFolder() {
        try {
            Context c = getInitialContext();
            Object folder = c.lookup(TOP_COMPONENTS_FOLDER);
            if (folder instanceof Context) {
                Context targetFolder = (Context)folder;
                NamingEnumeration en = targetFolder.listBindings(CURRENT);
                ArrayList al = new ArrayList();
                
                while (en.hasMoreElements()) {
                    Binding b = (Binding)en.nextElement();
                    Object obj = b.getObject();
                    if (obj instanceof TopComponent) {
                        al.add(b);
                    } else {
                        ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, 
                            "File " + b.getName() + " in " + TOP_COMPONENTS_FOLDER + " is not a top component."); //NOI18N
                    }
                }
                
                Context c1 = (Context)c.lookup(BOOKMARKS_FOLDER);
                Context c2 = (Context)c.lookup(BOOKMARKS_TOOLBAR);
                for (Iterator it = al.iterator(); it.hasNext(); ) {
                    Binding b = (Binding)it.next();
                    TopComponent tc = (TopComponent)b.getObject();
                    if (isTopComponentUsed(c1, tc) || isTopComponentUsed(c2 , tc)) {
                        it.remove();
                    }
                }

                // now perform the deletions
                for (Iterator it = al.iterator(); it.hasNext(); ) {
                    Binding b = (Binding)it.next();
                    try {
                        targetFolder.unbind(b.getName());
                    } catch (NamingException ne) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ne);
                    }
                }
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
    }
    
    /**
     * 
     * @returns true if the given top component is referenced from
     *        the context c
     */
    private boolean isTopComponentUsed(Context c, TopComponent tc) {
        try {
            NamingEnumeration en = c.listBindings(CURRENT);
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj instanceof BookmarkImpl) {
                    BookmarkImpl bimpl = (BookmarkImpl)obj;
                    if (tc.getName().equals(bimpl.getName())) {
                        return true;
                    }
                } else if (obj instanceof Context) {
                    if (isTopComponentUsed((Context)obj, tc)) {
                        return true;
                    }
                }
            }
        } catch (NamingException ne) {
            ErrorManager.getDefault().notify(ne);
        }
        return false;
    }

    /**
     * In order for the global shortcuts to be refreshed
     * we need to tell core that something has changed
     * in the shortcuts folder.
     */
    private void refreshShortcutsFolder() {
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(SHORTCUTS_FOLDER);
            DataFolder dfo = DataFolder.findFolder(fo);
            DataObject ch[] = dfo.getChildren();
            // following line is the main heck: it should not do anything but
            // as a side effect the folder fires property change that is caught
            // in the FolderInstance subclass in core (ShortcutsFolder) and causes
            // the refresh of the active shortcuts
            dfo.setOrder(ch);
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        }
    }
    
    /**
     * Lazy initialization of the listener variable. 
     * @param source Object source object
     */
    private NamingListener getNamingListener(Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        // Used to be:
        //return (NamingListener)WeakListener.create(type, listener, source);
        // but because of the remove method name we have to create the weak
        // listeners here:
        return new NamespaceChangeWeakListener(listener, source);
    }
    
    /**
     * Listener for updating bookmarks in the BOOKMARKS_ACTIONS
     * folder.
     */
    private class Listener implements NamespaceChangeListener {
        public void namingExceptionThrown(NamingExceptionEvent evt) {
        }
        
        public void objectAdded(NamingEvent evt) {
            checkActionsFolder();
        }
        
        public void objectRemoved(NamingEvent evt) {
            checkActionsFolder();
        }
        
        public void objectRenamed(NamingEvent evt) {
        }
    }
}
