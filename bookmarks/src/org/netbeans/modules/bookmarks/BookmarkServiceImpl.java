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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.openide.ErrorManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListener;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.*;

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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        checkTopComponentsFolder();
                    }
                });
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
            Context targetFolder = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            Context targetTcFolder = Context.getDefault().createSubcontext(TOP_COMPONENTS_FOLDER);
            String safeName = findUnusedName(targetFolder, b.getName());
            // and top component to the tc folder
            if (b instanceof BookmarkImpl) {
                BookmarkImpl bi = (BookmarkImpl)b;
                targetTcFolder.putObject(safeName, bi.getTopComponent());
                bi.setTopComponentFileName(safeName);
            }
            // following line will save the bookmark to the system file system
            targetFolder.putObject(safeName, b);
        } catch (ContextException x) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(x); // NOI18N
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
        int i = 0;
        while (targetFolder.getObject(result, null) != null) {
            result = name + i++;
        }
        return result;
    }
    
    /**
     * Loads top component from the top components folder.
     */
    public TopComponent loadTopComponent(String name) {
        try {
            Context targetFolder = Context.getDefault().createSubcontext(TOP_COMPONENTS_FOLDER);
            TopComponent tc = (TopComponent)targetFolder.getObject(name, null);
            return tc;
        } catch (ContextException x) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(x); // NOI18N
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
            Context con1 = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            Context con2 = Context.getDefault().createSubcontext(BOOKMARKS_TOOLBAR);
            if (!listenersAttached) {
                ContextListener l1 = getContextListener(con1);
                ContextListener l2 = getContextListener(con2);
                con1.addContextListener(l1); 
                con2.addContextListener(l2); // NOI18N
                listenersAttached = true;
            }
            
            ArrayList toDelete = new ArrayList();
            
            Context con3 = Context.getDefault().createSubcontext(BOOKMARKS_ACTIONS);
            Iterator it = con3.getOrderedNames().iterator();
            while (it.hasNext()) {
                String name = (String)it.next();
                Object obj = con3.getObject(name, null);
                if (obj instanceof Action) {
                    
                    if ( ( ! checkBookmarkAction((Action)obj, con1)) && 
                        ( ! checkBookmarkAction((Action)obj, con2)) ) {
                        toDelete.add(name);
                    }
                }
            }
            // delete unused bookmarks
            for (Iterator i = toDelete.iterator(); i.hasNext();) {
                con3.putObject(null, (String)i.next());
            }
            // create new items in the Actions/Bookmarks folder
            ensureAllActionsAreInActions(con1);
            ensureAllActionsAreInActions(con2);
            // after the action has been updated force the reload
            // of active shortcuts
            refreshShortcutsFolder();
        } catch (ContextException ne) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
        }
    }
    
    /**
     * Tries to find given action in the bookmarks folders.
     * @returns true if the given action a is found in the context con
     *      (the search is performed recursively)
     */
    private boolean checkBookmarkAction(Action a, Context con) {
        Iterator it = con.getOrderedNames().iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            Object obj = con.getObject(name, null);
            if (obj != null && obj.equals(a)) {
                return true;
            }
            Context c = con.getSubcontext(name);
            if (c != null) {
                if (checkBookmarkAction(a, c)) {
                    return true;
                }
            }
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
            Iterator it = con.getOrderedNames().iterator();
            while (it.hasNext()) {
                String name = (String)it.next();
                Object obj = con.getObject(name, null);
                if (obj instanceof Action) {
                    Action a = (Action)obj;
                    Context targetFolder = Context.getDefault().createSubcontext(BOOKMARKS_ACTIONS);
                    if (! checkBookmarkAction(a, targetFolder)) {
                        // the following will save the bookmark to the actions folder
                        // to be usable for shortcuts
                        String safeName = findUnusedName(targetFolder, name);
                        targetFolder.putObject(safeName, a);
                    }
                }
                Context c = con.getSubcontext(name);
                if (c != null) {
                    ensureAllActionsAreInActions(c);
                }
            }
        } catch (ContextException ne) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
        }
    }
    
    /**
     * Tries to find and delete all unused top components in
     * the top components folder.
     */
    private void checkTopComponentsFolder() {
        try {
            Context targetFolder = Context.getDefault().createSubcontext(TOP_COMPONENTS_FOLDER);
            Iterator i = targetFolder.getOrderedNames().iterator();
            ArrayList al = new ArrayList();

            while (i.hasNext()) {
                String name = (String)i.next();
                Object obj = targetFolder.getObject(name, null);
                if (obj instanceof TopComponent) {
                    al.add(name);
                } else {
                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, 
                        "File " + name + " in " + TOP_COMPONENTS_FOLDER + " is not a top component."); //NOI18N
                }
            }

            Context c1 = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            Context c2 = Context.getDefault().createSubcontext(BOOKMARKS_TOOLBAR);
            for (Iterator it = al.iterator(); it.hasNext(); ) {
                String s = (String)it.next();
                TopComponent tc = (TopComponent)targetFolder.getObject(s, null);
                if (isTopComponentUsed(c1, tc) || isTopComponentUsed(c2 , tc)) {
                    it.remove();
                }
            }

            // now perform the deletions
            for (Iterator it = al.iterator(); it.hasNext(); ) {
                String name = (String)it.next();
                targetFolder.putObject(name, null);
            }
        } catch (ContextException ne) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
        }
    }
    
    /**
     * 
     * @returns true if the given top component is referenced from
     *        the context c
     */
    private boolean isTopComponentUsed(Context c, TopComponent tc) {
        Iterator it = c.getOrderedNames().iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            Object obj = c.getObject(name, null);
            if (obj instanceof BookmarkImpl) {
                BookmarkImpl bimpl = (BookmarkImpl)obj;
                if (tc.getName().equals(bimpl.getName())) {
                    return true;
                }
            }
            Context c1 = c.getSubcontext(name);
            if (c1 != null) {
                if (isTopComponentUsed(c1, tc)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * In order for the global shortcuts to be refreshed
     * we need to tell core that something has changed
     * in the shortcuts folder.
     */
    private void refreshShortcutsFolder() {
//        try {
//            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(SHORTCUTS_FOLDER);
//            DataFolder dfo = DataFolder.findFolder(fo);
//            DataObject ch[] = dfo.getChildren();
//            // following line is the main heck: it should not do anything but
//            // as a side effect the folder fires property change that is caught
//            // in the FolderInstance subclass in core (ShortcutsFolder) and causes
//            // the refresh of the active shortcuts
//            dfo.setOrder(ch);
//        } catch (java.io.IOException ioe) {
//            ErrorManager.getDefault().notify(ioe);
//        }
    }
    
    /**
     * Lazy initialization of the listener variable. 
     * @param source Object source object
     */
    private ContextListener getContextListener(Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        return (ContextListener)WeakListener.create(ContextListener.class, listener, source);
    }
    
    /**
     * Listener for updating bookmarks in the BOOKMARKS_ACTIONS
     * folder.
     */
    private class Listener implements ContextListener {
        
        public void attributeChanged(AttributeEvent evt) {
            checkActionsFolder();
        }
        
        public void bindingChanged(BindingEvent evt) {
            checkActionsFolder();
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            checkActionsFolder();
        }
    }
}
