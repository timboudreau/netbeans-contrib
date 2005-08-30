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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import junit.framework.Test;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

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
    

   /** Test the import/export feature */
    public void testComplexICalImportExport() throws Exception {
	    // based on testcase.ics .
        String contents = "BEGIN:VCALENDAR\r\nPRODID:-//NetBeans tasklist/" + // NOI18N
            "/NONSGML 1.0//EN\r\nVERSION:2.0\r\n\r\nBEGIN:VTODO\r\nUID:nb1" + // NOI18N
            "032191274156.3@proto/192.129.100.100\r\nCREATED:20020916T1547" + // NOI18N
            "54Z\r\nSUMMARY:This is a second test task\r\nDESCRIPTION:More" + // NOI18N
            " details \\n\\n\\n\\n\\nhere\r\nPRIORITY:5\r\nPERCENT-COMPLETE:0\r\n" + // NOI18N
            "CATEGORIES:cat 1\\, cat 2\r\nEND:VTODO\r\n\r\nBEGIN:VTODO\r\n" + // NOI18N
            "UID:nb1032191304749.6@proto/192.129.100.100\r\nCREATED:200209" + // NOI18N
            "16T154824Z\r\nSUMMARY:subtask 2\r\nPERCENT-COMPLETE:0\r\nRELA" + // NOI18N
            "TED-TO:nb1032191274156.3@proto/192.129.100.100\r\nEND:VTODO\r" + // NOI18N
            "\n\r\nBEGIN:VTODO\r\nUID:nb1032191288704.4@proto/192.129.100." + // NOI18N
            "100\r\nCREATED:20020916T154808Z\r\nSUMMARY:subtask 1\r\nPERCE" + // NOI18N
            "NT-COMPLETE:0\r\nRELATED-TO:nb1032191274156.3@proto/192.129.1" + // NOI18N
            "00.100\r\nEND:VTODO\r\n\r\nBEGIN:VTODO\r\nUID:nb1032191297655" + // NOI18N
            ".5@proto/192.129.100.100\r\nCREATED:20020916T154817Z\r\nSUMMA" + // NOI18N
            "RY:subtask1a\r\nPERCENT-COMPLETE:0\r\nRELATED-TO:nb1032191288" + // NOI18N
            "704.4@proto/192.129.100.100\r\nEND:VTODO\r\n\r\nBEGIN:VTODO\r" + // NOI18N
            "\nUID:nb1032191234814.2@proto/192.129.100.100\r\nCREATED:2002" + // NOI18N
            "0916T154714Z\r\nSUMMARY:Test Task\r\nDESCRIPTION:This is a mu" + // NOI18N
            "ltiline\\ndescription\\ncontaining all kinds of \"evil\r\n \"\\nch" + // NOI18N
            "aracters that may trip up the import/export:\\n!" + // NOI18N
            "\r\n \r\nPRIORITY:1\r\nPERCENT-COMPLETE:55\r\nCATEGORI" + // NOI18N
            "ES:My category\r\nLAST-MODIFIED:20020916T154730Z\r\nX-NETBEAN" + // NOI18N
            "S-FILENAME:/tmp/testfile\r\nX-NETBEANS-LINE:21\r\nX-IGNORE-ME" + // NOI18N
            ":whatever\r\nEND:VTODO\r\n\r\nEND:VCALENDAR\r\n"; // NOI18N
        byte[] byteContents = contents.getBytes("utf8"); // NOI18N
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
                   subtasks.size() == 2);
        ListIterator it = subtasks.listIterator();
        int count = 0;
        while (it.hasNext()) {
            UserTask task = (UserTask)it.next();
            switch (count) {
            case 0: {
                assertTrue("Wrong description: " + task.getSummary(), // NOI18N
                           task.getSummary().equals("This is a second test task")); // NOI18N
                assertTrue("Wrong isDone", // NOI18N
                           !task.isDone());
                assertEquals("Wrong Percent Complete", 0, task.getPercentComplete()); // NOI18N
                assertTrue("Wrong hasSubtasks", // NOI18N
                           !task.getSubtasks().isEmpty());

                assertEquals("Wrong Description", "More details \n\n\n\n\nhere", task.getDetails()); // NOI18N
                assertEquals("Wrong uid", "nb1032191274156.3@proto/192.129.100.100", task.getUID()); // NOI18N
                assertEquals("Wrong priority", 5, task.getPriority()); // NOI18N
                assertEquals("Wrong category", "cat 1, cat 2", task.getCategory()); // NOI18N

                // Do date comparison
                long created = task.getCreatedDate();
                TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles"); // NOI18N
                GregorianCalendar cal = new GregorianCalendar(tz);
                cal.setTime(new Date(created));
                assertEquals("Wrong created day", cal.get(Calendar.DAY_OF_MONTH), // NOI18N
                             16);
                assertEquals("Wrong created day", cal.get(Calendar.MONTH), 8); // NOI18N
                assertEquals("Wrong created year", cal.get(Calendar.YEAR), 2002); // NOI18N
                assertEquals("Wrong created hour", cal.get(Calendar.HOUR_OF_DAY), 8); // NOI18N
                assertEquals("Wrong created minute", cal.get(Calendar.MINUTE), 47); // NOI18N
                assertEquals("Wrong created second", cal.get(Calendar.SECOND), 54); // NOI18N

                // Do subtask check
                List subtasks2 = task.getSubtasks();
                assertEquals("Wrong # of subtasks.", 2, subtasks2.size()); // NOI18N
                ListIterator it2 = subtasks2.listIterator();
                if (it2.hasNext()) {
                    UserTask subtask = (UserTask)it2.next();
                    assertEquals("Wrong subtask 2 description", // NOI18N
                                 "subtask 2", subtask.getSummary()); // NOI18N
                } else {
                    fail("Missing subtask 2"); // NOI18N
                }
                if (it2.hasNext()) {
                    UserTask subtask = (UserTask)it2.next();
                    assertEquals("Wrong subtask1 description", // NOI18N
                                 "subtask 1", subtask.getSummary()); // NOI18N
                    assertTrue("Wrong hasSubtasks for subtask 1", // NOI18N
                    	!subtask.getSubtasks().isEmpty());
                    List subtasks3 = subtask.getSubtasks();
                    assertEquals("Wrong # of subtasks.", 1, subtasks3.size()); // NOI18N
                    ListIterator it3 = subtasks3.listIterator();
                    while (it3.hasNext()) {
                        UserTask subtask2 = (UserTask)it3.next();
                        assertEquals("Wrong subtask1 description", // NOI18N
                                     "subtask1a", subtask2.getSummary()); // NOI18N
                        assertTrue(!it3.hasNext()); // Only one subtask
                    }
                } else {
                    fail("Missing subtask 1"); // NOI18N
                }
                break;
            }
            case 1: {
                assertTrue("Wrong description: " + task.getSummary(), // NOI18N
                           task.getSummary().equals("Test Task")); // NOI18N
                assertTrue("Wrong isDone", // NOI18N
                           !task.isDone());
                assertEquals("Wrong Percent Complete", 55, task.getPercentComplete()); // NOI18N
                assertEquals("Wrong Description",  // NOI18N
                    "This is a multiline\ndescription\ncontaining all kinds " +  // NOI18N
                    "of \"evil\"\ncharacters that may trip up the " +  // NOI18N
                    "import/export:\n!",  // NOI18N
                    task.getDetails());

                assertEquals("Wrong uid", "nb1032191234814.2@proto/192.129.100.100", task.getUID()); // NOI18N
                assertEquals("Wrong priority", 1, task.getPriority()); // NOI18N
                assertEquals("Wrong category", "My category", task.getCategory()); // NOI18N
                //assertEquals("Wrong filename", "/tmp/testfile", task.getFilename());
                //assertEquals("Wrong line number", 21, task.getLineNumber());
                break;
            }
            }
            count++;
        }

        // Write the list back out
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);

        ICalExportFormat ef = new ICalExportFormat();
        try {
            // XXX I do not know waht encgoging was used
            ef.writeList(list, out);
        } catch (Exception e) {
            throw e;
        }
        
        byte[] result = out.toByteArray();

        /* Uncomment to log the two strings if you want to diff them etc.
        try {
            FileWriter w = new FileWriter("/tmp/contents");
            w.write(contents);
            w.close();
        } catch (Exception e) {
            fail("Exception");
        }
        
        try {
            FileWriter w = new FileWriter("/tmp/result");
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
