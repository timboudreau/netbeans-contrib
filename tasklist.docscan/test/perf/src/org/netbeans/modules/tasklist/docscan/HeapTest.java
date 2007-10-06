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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
