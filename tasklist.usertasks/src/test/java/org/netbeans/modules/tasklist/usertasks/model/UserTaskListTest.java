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

package org.netbeans.modules.tasklist.usertasks.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;

/**
 * Test the usertask list functionality
 *
 * @author  Tor Norbye
 */
public class UserTaskListTest extends NbTestCase {

    public UserTaskListTest (String name) {
        super (name);
    }
    
    public static void main (String args []) throws Exception {
        openList("C:\\Dokumente und Einstellungen\\tim\\Desktop\\drmtasks.ics");
        //junit.textui.TestRunner.run (UserTaskListTest.class);
    }
    
    public static Test suite () {
        return new NbTestSuite(UserTaskListTest.class);
    }

    /**
     * Save to/restore from .ics.
     *
     * @param utl this task list will be saved
     * @return restored from .ics task list
     */
    private UserTaskList saveAndLoad(UserTaskList utl) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(out, "UTF-8");
        ICalExportFormat exp = new ICalExportFormat();
        exp.writeList(utl, w, true);
        byte[] bytes = out.toByteArray();
        String s = new String(bytes);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ICalImportFormat imp = new ICalImportFormat();

        utl = new UserTaskList();
        imp.read(utl, in);
        return utl;
    }
    
    /**
     * Task list should preserve order of tasks.
     */
    public void testPreserveOrder() throws Exception {
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("1", utl);
        UserTask ut2 = new UserTask("2", utl);
        UserTask ut3 = new UserTask("3", utl);
        utl.getSubtasks().add(ut);
        utl.getSubtasks().add(ut2);
        utl.getSubtasks().add(ut3);
        
        utl = saveAndLoad(utl);
        
        assertEquals("1", utl.getSubtasks().getUserTask(0).getSummary());
        assertEquals("2", utl.getSubtasks().getUserTask(1).getSummary());
        assertEquals("3", utl.getSubtasks().getUserTask(2).getSummary());
        
        utl.getSubtasks().getUserTask(1).moveUp();
        
        utl = saveAndLoad(utl);
        
        assertEquals("2", utl.getSubtasks().getUserTask(0).getSummary());
        assertEquals("1", utl.getSubtasks().getUserTask(1).getSummary());
        assertEquals("3", utl.getSubtasks().getUserTask(2).getSummary());
    }
    
    /**
     * Changing one task should not trigger the "last-modified" value of 
     * other tasks in a list.
     */
    public void testLastModified() {
        // utl 
        //  |-ut
        //    |-ut3
        //  |-ut2
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("test", utl);
        UserTask ut2 = new UserTask("test2", utl);
        UserTask ut3 = new UserTask("test2", utl);
        utl.getSubtasks().add(ut);
        utl.getSubtasks().add(ut2);
        ut.getSubtasks().add(ut3);
        
        ut2.setLastEditedDate(15);
        
        ut.setDone(true);
        assertEquals(15, ut2.getLastEditedDate());
        
        ut.setValuesComputed(true);
        ut3.setDone(true);
        assertEquals(15, ut2.getLastEditedDate());
    }
    
    /**
     * ValuesComputed should be saved properly.
     */
    public void testValuesComputed() throws Exception {
        // utl 
        //  |-ut
        //    |-ut2
        //      |-ut3
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("test", utl);
        UserTask ut2 = new UserTask("test2", utl);
        UserTask ut3 = new UserTask("test2", utl);
        utl.getSubtasks().add(ut);
        ut.getSubtasks().add(ut2);
        ut2.getSubtasks().add(ut3);
        ut.setValuesComputed(true);
        
        utl = saveAndLoad(utl);
        ut = utl.getSubtasks().get(0);
        ut2 = ut.getSubtasks().get(0);
        ut3 = ut2.getSubtasks().get(0);
        
        assertTrue(ut.isValuesComputed());
        assertFalse(ut2.isValuesComputed());
        assertFalse(ut3.isValuesComputed());
    }
    
    /**
     * Opening a task list should not trigger the last modified date.
     */
    public void testLastModified2() throws Exception {
        UTUtils.LOGGER.fine("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        // utl 
        //  |-ut
        //    |-ut2
        //    |-ut3
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("test", utl);
        UserTask ut2 = new UserTask("test2", utl);
        UserTask ut3 = new UserTask("test2", utl);
        utl.getSubtasks().add(ut);
        ut.getSubtasks().add(ut2);
        ut.getSubtasks().add(ut3);
        ut3.setDone(true);
        ut.setValuesComputed(true);
        
        ut.setLastEditedDate(15000);
        ut2.setLastEditedDate(15000);
        ut3.setLastEditedDate(15000);
        
        utl = saveAndLoad(utl);
        ut = utl.getSubtasks().get(0);
        ut2 = ut.getSubtasks().get(0);
        ut3 = ut.getSubtasks().get(1);
        
        assertEquals(15000, ut.getLastEditedDate());
        assertEquals(15000, ut2.getLastEditedDate());
        assertEquals(15000, ut3.getLastEditedDate());
    }
    
    /**
     * Stores and loads working periods.
     */
    public void testWorkPeriods() throws Exception {
        // utl 
        //  |-ut
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("test", utl);
        utl.getSubtasks().add(ut);
        
        long time = System.currentTimeMillis();
        UserTask.WorkPeriod wp = new UserTask.WorkPeriod(time, 2);
        ut.getWorkPeriods().add(wp);
        
        utl = saveAndLoad(utl);
        ut = utl.getSubtasks().get(0);
        wp = ut.getWorkPeriods().get(0);
        
        long delta = Math.abs(wp.getStart() - time);
        assertTrue(Long.toString(delta), delta < 1000);
        assertEquals(wp.getDuration(), 2);
    }
    
    /**
     * Deleting completed tasks.
     */
    public void testPurge() {
        UserTaskList utl = new UserTaskList();
        UserTask ut = new UserTask("test", utl);
        utl.getSubtasks().add(ut);
        ut.setDone(true);
        utl.getSubtasks().purgeCompletedItems();
        assertTrue(utl.getSubtasks().size() == 0);
    }
    
    public void testUserTaskList() throws Exception {
        UserTaskList list = (UserTaskList)openList("tasklist.ics"); // NOI18N
        List subtasks = list.getSubtasks();
        assertTrue("Not all tasks in the list found: found " + subtasks.size() + // NOI18N
                   " elements", // NOI18N
                   subtasks.size() == 1);
        ListIterator it = subtasks.listIterator();
        while (it.hasNext()) {
            UserTask task = (UserTask)it.next();
            assertTrue("Wrong description: " + task.getSummary(), // NOI18N
                       task.getSummary().equals("This is a test task")); // NOI18N
            assertTrue("Wrong isDone", // NOI18N
                       !task.isDone());
        }
    }


    /** 
     * Test the import/export feature 
     */
    public void testSimpleICalImportExport() throws Exception {
        String contents = "BEGIN:VCALENDAR\r\nPRODID:-//NetBeans tasklist//NONSGML 1.0//EN\r\nVERSION:2.0\r\n\r\nBEGIN:VTODO\r\nUID:nb1031618664570.1@proto/192.129.100.100\r\nCREATED:20020910T004424Z\r\nSUMMARY:This is a test task\r\nPERCENT-COMPLETE:0\r\nEND:VTODO\r\n\r\nEND:VCALENDAR\r\n"; // NOI18N
        ByteArrayInputStream reader = new ByteArrayInputStream(contents.getBytes()); 

        ICalImportFormat io = new ICalImportFormat();
        UserTaskList list = new UserTaskList();

        try {
            io.read(list, reader);
        } catch (Exception e) {
            throw e;
        }

        // Check that the list we read in is indeed correct
        List subtasks = list.getSubtasks();
        assertTrue("Not all tasks in the list found: found " + subtasks.size() + // NOI18N
                   " elements", // NOI18N
                   subtasks.size() == 1);        
        ListIterator it = subtasks.listIterator();
        while (it.hasNext()) {
            UserTask task = (UserTask)it.next();
            assertTrue("Wrong description: " + task.getSummary(), // NOI18N
                       task.getSummary().equals("This is a test task")); // NOI18N
            assertTrue("Wrong isDone", // NOI18N
                       !task.isDone());
            assertEquals("Wrong Percent Complete", 0, task.getPercentComplete()); // NOI18N
        }

        // Write the list back out
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        Writer w = new OutputStreamWriter(out, "UTF-8");

        ICalExportFormat ef = new ICalExportFormat();
        try {
            ef.writeList(list, w, true);
        } catch (Exception e) {
            throw e;
        }
        
        String result =new String(out.toByteArray(), "utf8");  // NOI18N XXX we do not know the encoding

        /* Uncomment to log the two strings if you want to diff them etc.
        try {
            FileWriter w = new FileWriter("contents.txt");
            w.write(contents);
            w.close();
        } catch (Exception e) {
            fail("Exception");
        }
        
        try {
            FileWriter w = new FileWriter("result.txt");
            w.write(result);
            w.close();
        } catch (Exception e) {
            fail("Exception");
        }
        */
    }

    // TODO: xCal test - run output out and back in through xCal, then
    // do a second check
    
    private static UserTaskList openList(String name) throws Exception {
        File data = new File(UserTaskListTest.class.getResource("data").getFile()); // NOI18N
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(data);
        Repository.getDefault().addFileSystem(lfs);

        FileObject fo = null;
        try {
            DataObject dao = DataObject.find(lfs.findResource(name));
            if (dao == null) {
                fail(name + " not found"); // NOI18N
            }
            fo = dao.getPrimaryFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        InputStream is = fo.getInputStream();
        UserTaskList list = new UserTaskList();
        ICalImportFormat.read(list, fo.getInputStream());
        is.close();
        return list;
    }
}
