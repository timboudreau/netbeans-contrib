/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.MemoryFilter;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;

import javax.swing.*;
import java.net.URL;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

/**
 * Set of tests running GUI and hunting for memoty leaks.
 * <P>
 * Depends On: http://xml.netbeans.org/issues/show_bug.cgi?id=40633
 *
 * @author Petr Kuzel
 */
public final class HeapTest extends NbTestCase {

    public HeapTest(String s) {
        super(s);
    }


    /** NB filesytem initialized during setUp. */
    private FileSystem dataFS;
    private Filter filter = new Filter();

    protected void setUp () throws Exception {
        URL url = this.getClass().getResource("data");
        String resString = NbTestCase.convertNBFSURL(url);
        LocalFileSystem fs = new LocalFileSystem();
        fs.setRootDirectory(new File(resString));
        Repository.getDefault().addFileSystem(fs);
        dataFS = fs;
    }

    public void testBug40565() {

        if (SourceTasksAction.class.getClassLoader() != getClass().getClassLoader()) {
            throw new IllegalStateException("Test must be laoded by tested module classloader");
        }

        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    // it requires GUI mode
                    SourceTasksAction action = (SourceTasksAction) SystemAction.get(SourceTasksAction.class);
                    action.performAction();

                }
            });
        } catch (InterruptedException e) {

        } catch (InvocationTargetException e) {

        }

        SourceTasksView view = (SourceTasksView) SourceTasksView.getCurrent();



        view.selectedFolder = dataFS.findResource("leaks");
        view.getAllFiles().doClick(1);

        // wait till all threads done (otherwise we get randam numbers)
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
        }
        // XXX we still sometimes get random numbers

        view.getRefresh().doClick(1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        // max estimates sbased on 20040301 build
        int TL_SIZE = 6650336;
        //int TABLE_SIZE = 6604320 + 20000;  // the table size is very random (I've seen 7308880 (with all submodules), too)
                                             // XXX why submodule spresence affects it >500kB
        int TABLE_SIZE = 7308880 + 40000;
        int TABLE_LEAK = 400; // tolerated leak

//        int initial_tl_size = assertSize("Tasklist actual size too big", Collections.singleton(view.discloseModel()), TL_SIZE, filter);
        int initial_table_size = assertSize("Table actual size too big", Collections.singleton(view.discloseTable()), TABLE_SIZE, filter);
        int initial_node_size = assertSize("Node actual size too big", Collections.singleton(view.discloseNode()), Integer.MAX_VALUE, filter);
        int initial_view_size = assertSize("View actual size too big", Collections.singleton(view), Integer.MAX_VALUE, filter);

        for (int i = 0 ; i <5; i++) {
            view.getRefresh().doClick(1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

//            assertSize("Tasklist after refresh #" + i, Collections.singleton(view.discloseModel()), initial_tl_size, filter); // 0B leak
            int current_table_size = assertSize("Table after refresh #" + i, Collections.singleton(view.discloseTable()), initial_table_size + TABLE_LEAK, filter);  // 20-400B leak
//            int current_node_size = assertSize("Node after refresh #" + i, Collections.singleton(view.discloseNode()), initial_node_size, filter);  // 968b leak
//
//            int knownLeak = (current_table_size - initial_table_size) + (current_node_size - initial_node_size);
//            assertSize("View after refresh #" + i, Collections.singleton(view), initial_view_size + knownLeak, filter);  // 2Kb leak
        }
        assertSize("View after refresh test", Collections.singleton(view), initial_view_size, filter);  // 2Kb leak

    }

    private class Filter implements MemoryFilter {
        public boolean reject (Object obj) {
            return obj instanceof java.lang.ref.Reference;
        }
    }
}
