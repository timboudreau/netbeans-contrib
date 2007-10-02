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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.windows.TopComponent;
import org.openide.util.RequestProcessor;

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
     * We hold a reference to the context for preventing
     * the garbage collection.
     */
    private Context bookmarksFolderContext;
    
    /**
     * We hold a reference to the context for preventing
     * the garbage collection.
     */
    private Context bookmarksToolbarContext;
    
    /**
     * Keeps track of the task spawned from the constructor.
     */
    private RequestProcessor.Task initTask = null;
    
    /** This constructor is public for the META-INF/services lookup
     * to be able to instantiate this service
     */
    public BookmarkServiceImpl() {
        
        // we run this in a separate thread because the
        // code checking the bookmarks might use BookmarkServiceImpl
        // but here we are in its constructor. Also we start
        // it later to make sure this constructor is finished
        initTask = RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        checkTopComponentsFolder();
                        checkActionsFolder();
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
        if (b == null) {
            return;
        }
        if (initTask != null) {
            initTask.waitFinished();
        }
        try {
            Context targetFolder = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            Collection childrenNames = targetFolder.getOrderedNames();
            Context targetTcFolder = Context.getDefault().createSubcontext(TOP_COMPONENTS_FOLDER);
            String safeName = findUnusedName(targetFolder, b.getName());
            // and top component to the tc folder
            if ((b instanceof BookmarkImpl) && (! (b instanceof ManageBookmarksBookmarkImpl))) {
                BookmarkImpl bi = (BookmarkImpl)b;
                targetTcFolder.putObject(safeName, bi.getTopComponent());
                bi.setTopComponentFileName(safeName);
            }
            // following line will save the bookmark to the system file system
            targetFolder.putObject(safeName, b);
            // and the order:
            ArrayList al = new ArrayList(childrenNames);
            al.add(safeName);
            targetFolder.orderContext(al);
            // the following will save the bookmark action to the actions folder
            // to be usable for shortcuts
            saveBookmarkActionImpl(targetFolder, safeName);
        } catch (ContextException x) {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", x); 
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
        if ( (bp == null) && (tc instanceof BookmarkProvider) ) {
            bp = (BookmarkProvider)tc;
        }
        if (bp != null) {
            return bp.createBookmark();
        }
        return new BookmarkImpl(tc);
    }
    
    /**
     * Tries to find unused name in the specified context by
     * trying to append a number to the specified name.
     */
    public static String findUnusedName(Context targetFolder, String name) {
        String s = name;
        if (s == null) {
            s = "bookmark"; // NOI18N
        }
        // first, get rid of "strange" characters in the name
        StringBuffer sb = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ( ((ch >= 'a') && (ch <= 'z')) ||
            ((ch >= 'A') && (ch <= 'Z')) ||
            ((ch >= '0') && (ch <= '9')) ||
            (ch == '-') || (ch == '_') ) {
                sb.append(ch);
            }
        }
        if (sb.length() == 0) {
            sb.append(Long.toString(System.currentTimeMillis()));
        }
        
        s = sb.toString();
        String result = s;
        
        // now make it unique
        int i = 0;
        while (targetFolder.getObject(result, null) != null) {
            result = s + i++;
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
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", x); // NOI18N
        }
        return null;
    }
    
    /**
     * This method scans the BOOKMARKS_ACTIONS folder and tries to
     * delete unused actions. The unused actions are those that
     * do not reference the original bookmark in the BOOKMARKS_FOLDER or BOOKMARKS_TOOLBAR folder.
     * On the other hand
     * it calls ensureAllBookmarksAreInActions() to make sure that all
     * bookmarks in the BOOKMARKS_FOLDER and BOOKMARKS_TOOLBAR 
     * have action in BOOKMARKS_ACTIONS.
     */
    private void checkActionsFolder() {
        try {
            bookmarksFolderContext = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            bookmarksToolbarContext = Context.getDefault().createSubcontext(BOOKMARKS_TOOLBAR);
            ArrayList toDelete = new ArrayList();
            ArrayList actionsCache = new ArrayList();
            
            Context con3 = Context.getDefault().createSubcontext(BOOKMARKS_ACTIONS);
            Iterator it = con3.getOrderedNames().iterator();
            while (it.hasNext()) {
                String name = (String)it.next();
                Object obj = con3.getObject(name, null);
                if (obj instanceof BookmarkActionImpl) {
                    if (checkBookmarkAction((BookmarkActionImpl)obj)) {
                        actionsCache.add(obj);
                    } else {
                        toDelete.add(name);
                    }
                }
            }
            // delete unused actions
            for (Iterator i = toDelete.iterator(); i.hasNext();) {
                con3.putObject((String)i.next(), null);
            }
            // create new items in the Actions/Bookmarks folder
            ensureAllBookmarksHaveActions(bookmarksFolderContext, actionsCache);
            ensureAllBookmarksHaveActions(bookmarksToolbarContext, actionsCache);
            // after the action has been updated force the reload
            // of active shortcuts
            refreshShortcutsFolder();
        } catch (ContextException ne) {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", ne); // NOI18N
        }
    }
    
    /**
     * Tries to find given action in the bookmarks folders.
     * @returns true if the given action a is found
     */
    private boolean checkBookmarkAction(BookmarkActionImpl a) {
        String path = a.getPath();
        Object obj = null;
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0) {
            Context c = Context.getDefault().getSubcontext(path.substring(0, lastSlash));
            if (c != null) {
                obj = c.getObject(path.substring(lastSlash+1), null);
            }
        } else {
            obj = Context.getDefault().getObject(path, null);
        }
        return obj instanceof Bookmark;
    }
    
    /**
     * Takes all actions in the given context 
     * (the search is performed recursively) and checks whether the
     * actions are also in BOOKMARKS_ACTIONS folder. If the action
     * is not there it is added (to the BOOKMARKS_ACTIONS folder).
     */
    private void ensureAllBookmarksHaveActions(Context con, Collection actionsCache) {
        Iterator it = con.getBindingNames().iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            Object obj = con.getObject(name, null);
            if (obj instanceof Bookmark) {
                Bookmark b = (Bookmark)obj;
                if (! isThereActionForBookmark(b, actionsCache)) {
                    saveBookmarkActionImpl(con, name);
                }
            }
        }
        
        Iterator it2 = con.getSubcontextNames().iterator();
        while (it2.hasNext()) {
            String name = (String)it2.next();
            Context c = con.getSubcontext(name);
            if (c != null) {
                ensureAllBookmarksHaveActions(c, actionsCache);
            }
        }
    }
    
    private boolean isThereActionForBookmark(Bookmark b, Collection actionsCache) {
        Iterator it = actionsCache.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof BookmarkActionImpl) {
                BookmarkActionImpl bai = (BookmarkActionImpl)obj;
                if (b.equals(bai.getBookmark())) {
                    return true;
                }
            }
        }
        return false;
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
                    Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                        Level.FINE, "File " + name + " in " + 
                        TOP_COMPONENTS_FOLDER + " is not a top component."); //NOI18N
                }
            }

            Context c1 = Context.getDefault().createSubcontext(BOOKMARKS_FOLDER);
            Context c2 = Context.getDefault().createSubcontext(BOOKMARKS_TOOLBAR);
            for (Iterator it = al.iterator(); it.hasNext(); ) {
                String s = (String)it.next();
                if (isTopComponentUsed(c1, s) || isTopComponentUsed(c2 , s)) {
                    it.remove();
                }
            }

            // now perform the deletions
            for (Iterator it = al.iterator(); it.hasNext(); ) {
                String name = (String)it.next();
                targetFolder.putObject(name, null);
            }
        } catch (ContextException ne) {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", ne); // NOI18N
        }
    }
    
    /**
     * 
     * @returns true if the given top component is referenced from
     *        the context c
     */
    private boolean isTopComponentUsed(Context c, String tcFileName) {
        Iterator it = c.getOrderedNames().iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            Object obj = c.getObject(name, null);
            if (obj instanceof BookmarkImpl) {
                BookmarkImpl bimpl = (BookmarkImpl)obj;
                if (tcFileName.equals(bimpl.getTopComponentFileName())) {
                    return true;
                }
            }
            Context c1 = c.getSubcontext(name);
            if (c1 != null) {
                if (isTopComponentUsed(c1, tcFileName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * the following will save the bookmark to the actions folder
     * to be usable for shortcuts.
     * @param name of the bookmark
     * @param con context containing the bookmark
     */
    static void saveBookmarkActionImpl(Context con, String name) {
        try {
            Context targetFolder = Context.getDefault().createSubcontext(BOOKMARKS_ACTIONS);
            String safeName = findUnusedName(targetFolder, name);
            String path1 = con.getAbsoluteContextName() + "/" + name;  // NOI18N
            path1 = path1.substring(1);
            targetFolder.putObject(safeName, new BookmarkActionImpl(path1, safeName));
            refreshShortcutsFolder();
        } catch (ContextException ne) {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", ne); // NOI18N
        }
    }
    
    /**
     * In order for the global shortcuts to be refreshed
     * we need to tell core that something has changed
     * in the shortcuts folder.
     */
    static void refreshShortcutsFolder() {
        try {
            FileObject sfo = Repository.getDefault().getDefaultFileSystem().findResource(SHORTCUTS_FOLDER);
            // This is a hack! It forces the core's impl to refresh the list of shortcuts.
            FileObject dummy = sfo.createData("dummy");  // NOI18N
            // and delete it!
            dummy.delete();
        } catch (IOException ioe)  {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.WARNING, "", ioe); // NOI18N
        }
    }
    
    /**
     * Static utility method for cloning bookmarks. If the cloning process is
     * not successfull it returns the original object.
     */
    static Bookmark cloneBookmark(Bookmark b) {
        try {
            Class bClass = b.getClass();
            Method cloneMethod = bClass.getMethod("clone", new Class[0]);
            return (Bookmark)cloneMethod.invoke(b, new Object[0]);
        } catch (Exception x) {
            Logger.getLogger("org.netbeans.modules.bookmarks").log( // NOI18N
                Level.FINE, "", x); // NOI18N
        }
        return b;
    }
}
