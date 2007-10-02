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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;
import javax.swing.*;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.lookup.*;
import org.openide.windows.*;
import org.openide.loaders.DataObject;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.Context;
import org.netbeans.modules.bookmarks.test.TestBookmark;

/** 
 * Tests for the BookmarkService class. Those tests run
 * in the running application since they need the JNDI naming
 * to be in place and working.
 *
 * @author David Strupl
 */
public class BookmarkServiceTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;
    
    public BookmarkServiceTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(BookmarkServiceTest.class));
    }

    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem();
        String baseFolder = BookmarkServiceImpl.BOOKMARKS_FOLDER; 
        root = dfs.findResource(baseFolder);
        if (root == null) {
            String s1 = baseFolder.substring(0, baseFolder.lastIndexOf('/'));
            FileObject f1 = dfs.findResource(s1);
            if (f1 == null) {
                f1 = dfs.getRoot().createFolder(s1);
            } 
            root = f1.createFolder(baseFolder.substring(baseFolder.lastIndexOf('/')+1));
        }
    }
    
    /**
     * This test should tests the functionality of the method
     * BookmarkService.storeBookmark. It does so by creating a bookmark
     * storing it and checking whether it was stored.
     * The tests performs following steps:
     * <OL><LI> Create a testing bookmark
     *     <LI> Store the bookmark
     *     <LI> Check the presence of the new file on 
     *          the system file system
     * </OL>
     */
    public void testStoreBookmark() throws Exception {
        Bookmark b = new TestBookmark("test1");
        BookmarkService.getDefault().storeBookmark(b);
        
        assertTrue("Stored item did not found", deleteFromBookmarksFolder("test1"));
    }
    
    /**
     * Tries to find and delete file with given name.
     * The name has to only start with the param name.
     */
    private boolean deleteFromBookmarksFolder(String name) throws java.io.IOException {
        java.util.Enumeration en = root.getChildren(false);
        while (en.hasMoreElements()) {
            FileObject fo = (FileObject)en.nextElement();
            if (fo.getName().startsWith(name)) {
                    // using loaders API because the saving process can be running
                try {
                    DataObject dObj = DataObject.find(fo);
                    dObj.delete();
                    return true;
                } catch (java.io.IOException ioe) {
                    Logger.getLogger(BookmarkServiceTest.class.getName()).log(
                        Level.SEVERE, "", ioe);
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * This test should test the API method createDefaultBookmark.
     * It does so by invoking the method with testing TopComponent. 
     * The tests performs following steps:
     * <OL><LI> Creates a testing top component
     *     <LI> The result of createDefaultBookmark should be BookmarkImpl
     *     <LI> creates another top component now supplying a BookmarkProvider
     *          in its lookup
     *     <LI> createDefaultBookmark should call through 
                BookmarkProvider.createBookmark - the resulting bookmark should
     *          be the one from BookmarkProvider.
     * </OL>
     */
    public void testCreateDefaultBookmark() throws Exception {
        TopComponent tc = new TopComponent();
        Bookmark b = BookmarkService.getDefault().createDefaultBookmark(tc);
        assertTrue("Default bookmark should be BookmarkImpl", b instanceof BookmarkImpl);
        BookmarkImpl bi = (BookmarkImpl)b;
        assertEquals("Top component referenced from the BookmarkImpl should be tc.", bi.getTopComponent(), tc);
        
        // -------------------------------------------------
        Bookmark tb = new TestBookmark("test2");
        TopComponent tc1 = new TestTopComponent(tb);
        Bookmark b2 = BookmarkService.getDefault().createDefaultBookmark(tc1);
        assertEquals("Bookmark from the test component must be tb", b2, tb);
    }
    
    /**
     * The testing top component provides a bookmark in its lookup.
     */
    private static class TestTopComponent extends TopComponent {
        private Bookmark b;
        public TestTopComponent() {
        }
        public TestTopComponent(Bookmark b) {
            this.b = b;
        }
        public Lookup getLookup() {
            Lookup orig = super.getLookup();
            return new ProxyLookup(new Lookup[] {
                orig,
                Lookups.singleton(new BookmarkProvider() {
                    public Bookmark createBookmark() {
                        return b;
                    }
                })
            });
        }
    }
    
    /**
     * This test tries to verify that the code in AddBookmarkAction
     * calling createDefaultBookmark and storeBookmark methods works ok.
     * The tests performs following steps:
     * <OL><LI> Create a TestTopComponent with test bookmark
     *     <LI> call createDefaultBookmark and storeBookmark like AddBookmarkAction
     *     <LI> tests whether the testing bookmark bound to the
     *          testing top component is written in the bookmarks folder
     * </OL>
     */
    public void testAddBookmarkAction() throws Exception {
        Bookmark b = new TestBookmark("test3");
        final TopComponent tc = new TestTopComponent(b);

        // these 2 lines compied from AddBookmarkAction:
        BookmarkService bs = BookmarkService.getDefault();
        bs.storeBookmark(bs.createDefaultBookmark(tc));
        
        assertTrue("test3 should be stored by AddBookmarkAction", deleteFromBookmarksFolder("test3"));
        
        // clean up
        tc.close();
    }
    /**
     * Tests whether it is possible to store the deafult
     * bookmark with the same top component twice. 
     * The tests performs following steps:
     * <OL><LI> Create a new TestBookmark
     *     <LI> store the bookmark twice
     *     <LI> checks whether the bookmark is there
     * </OL>
     */
    public void testStoreBookmarkTwice() throws Exception {
        Bookmark ba = new TestBookmark("testA");
        BookmarkService.getDefault().storeBookmark(ba);
        BookmarkService.getDefault().storeBookmark(ba);
        assertTrue("Stored item did not found", deleteFromBookmarksFolder("testA"));
    }
    
    /**
     * Test what is done when the default bookmark is invoked. It
     * should open the saved top component.
     * The tests performs following steps:
     * <OL><LI> Create a special top component that records open
     *          and request focus methods
     *     <LI> createDefaultBookmark method is called with the created
     *         top component
     *     <LI> the resulting bookmark is invoked
     *     <LI> it is verified that the bookmark invocation lead to
     *        openning and selecting the top component
     * </OL>
     */
    public void testInvokeDefaultBookmark() throws Exception {
        final boolean [] res = new boolean[2];
        TopComponent tc = new TopComponent() {
            public void open() {
                super.open();
                res[0] = true;
            }
            public void requestActive() {
                super.requestActive();
                res[1] = true;
            }
        };
        Bookmark b = BookmarkService.getDefault().createDefaultBookmark(tc);
        b.invoke();
        assertTrue("Open has not been called", res[0]);
        assertTrue("requestActive has not been called", res[1]);
        
        // clean up
        tc.close();
    }
    
    /**
     * Test whether it is possible to create bookmark, delete it and
     * create again with the same top component.
     * The tests performs following steps:
     * <OL><LI> Create TestBookmark (with name test1)
     *     <LI> store the bookmark using storeBookmark method of BookmarkService
     *     <LI> check whether the file test1*.* was written and delete it
     *     <LI> Create TestBookmark (with name test1)
     *     <LI> store the bookmark using storeBookmark method of BookmarkService
     *     <LI> check whether the file test1*.* was written again
     * </OL>
     */
    public void testBindAfterDeleting() throws Exception {
        Bookmark b = new TestBookmark("test1");
        BookmarkService.getDefault().storeBookmark(b);
        
        assertTrue("Stored item did not found", deleteFromBookmarksFolder("test1"));
        Bookmark b2 = new TestBookmark("test1"); // this is intentionally the same as above
        BookmarkService.getDefault().storeBookmark(b2);
        
        assertTrue("Stored item did not found", deleteFromBookmarksFolder("test1"));
    }
    
    /**
     * This test should tests the functionality of the method
     * BookmarkService.storeBookmark with respect to the shortcuts folder Actions/Bookmarks.
     * It does so by creating a bookmark, storing it and checking whether Actions/Bookmarks
     * folder was also changed.
     * The tests performs following steps:
     * <OL><LI> Create a testing bookmark
     *     <LI> Store the bookmark
     *     <LI> Check the presence of the new file on 
     *          the system file system in the Actions/Bookmarks folder
     *     <LI> Deletes the bookmark
     *     <LI> Checks whether also corresponding file from Actions/Bookmark was deleted.
     * </OL>
     */
    public void testActionsFolder() throws Exception {
        Bookmark b = new TestBookmark("test1");
        BookmarkService.getDefault().storeBookmark(b);

        Context folder = Context.getDefault().getSubcontext(BookmarkServiceImpl.BOOKMARKS_ACTIONS);
        assertTrue("BOOKMARKS_ACTIONS not found", folder instanceof Context);
        Object obj = folder.getObject("test1", null);
        assertNotNull("action should be there ", obj);

        assertTrue("Stored item could not be deleted", deleteFromBookmarksFolder("test1"));
    }
}
