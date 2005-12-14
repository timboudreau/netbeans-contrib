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

package org.netbeans.modules.tasklist.usertasks.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ValidationException;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
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

    protected void setUp () throws Exception {
    }

    protected void tearDown () throws Exception {
    }

    /**
     * Save to/restore from .ics.
     *
     * @param utl this task list will be saved
     * @return restored from .ics task list
     */
    private UserTaskList saveAndLoad(UserTaskList utl) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ICalExportFormat exp = new ICalExportFormat();
        exp.writeList(utl, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
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
        
        ut.setProgressComputed(true);
        ut.setEffortComputed(true);
        ut.setSpentTimeComputed(true);
        ut3.setDone(true);
        assertEquals(15, ut2.getLastEditedDate());
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
    
    /** This is just a dummy place holder test */
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


   /** Test the import/export feature */
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

        ICalExportFormat ef = new ICalExportFormat();
        try {
            ef.writeList(list, out);
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
        UserTaskList list = UserTaskList.readDocument(fo);
        return list;
    }

}
