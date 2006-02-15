/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ValidationException;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.core.util.ObjectList;

import org.netbeans.modules.tasklist.usertasks.*;


/**
 * This class represents the tasklist itself
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class UserTaskList implements Timeout, ObjectList.Owner {    
    /**
     * Callback for the UserTaskList.process method
     */
    public static interface UserTaskProcessor {
        /**
         * This method will be called for each user task.
         *
         * @param ut reference to the task
         */
        public void process(UserTask ut);
    }

    /**
     * Callback for finding the next timeout
     */
    private static class FindNextTimeoutUserTaskProcessor implements
    UserTaskProcessor {
        private long nextTimeout = Long.MAX_VALUE;
        
        /** Task for the next timeout */
        public UserTask ref = null;
        
        public void process(UserTask t) {
            long n = t.getDueTime();
            if (n != Long.MAX_VALUE && !t.isDueAlarmSent() && !t.isDone() &&
                n > System.currentTimeMillis() && n < nextTimeout) {
                nextTimeout = n;
                ref = t;
            }
        }
    }
    
    private static class ShowExpiredUserTaskProcessor implements
        UserTaskProcessor {
        public void process(UserTask t) {
            long n = t.getDueTime();
            if (n != Long.MAX_VALUE && !t.isDueAlarmSent() &&
                !t.isDone() && 
                n <= System.currentTimeMillis()) {
                showExpiredTask(t);
            }
        }

        /**
         * Present the user with a dialog that shows information of the task that
         * expired... 
         *
         * @param task the task to show
         */
        private void showExpiredTask(UserTask task) {
            task.setDueAlarmSent(true);

            final UserTask t = task;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UserTaskDuePanel panel = new UserTaskDuePanel(t);

                    String title = NbBundle.getMessage(UserTaskList.class, "TaskDueLabel"); // NOI18N
                    DialogDescriptor d = new DialogDescriptor(panel, title);                
                    d.setModal(true);
                    d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
                    d.setOptions(new Object[] {DialogDescriptor.OK_OPTION});
                    java.awt.Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
                    dlg.pack();
                    dlg.show();
                }
            });
        }
    }
    
    private static UserTaskList tasklist = null;
    
    /**
     * Copies the content of one stream to another.
     *
     * @param is input stream
     * @param os output stream
     */
    private static void copyStream(InputStream is, OutputStream os) 
    throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }
    
    /**
     * Returns the default task list
     *
     * @return default task list
     */
    public static UserTaskList getDefault() throws IOException {
        if (tasklist != null) 
            return tasklist;
                
        File f = getDefaultFile();
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) 
            return readDocument(fo);

        File dir = f.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "CannotCreateDir", dir.getAbsolutePath())); // NOI18N
        }
        OutputStream os = new FileOutputStream(f);
        try {
            InputStream is = UserTaskList.class.getResourceAsStream(
                    "/org/netbeans/modules/tasklist/usertasks/tasklist.ics"); // NOI18N
            try {
                copyStream(is, os);
            } finally {
                is.close();
            }
        } finally {
            os.close();
        }

        tasklist = readDocument(FileUtil.toFileObject(f));
        return tasklist;
    }
    
    private UserTaskObjectList tasks;

    /** Has the options set changed such that we need to save */
    protected boolean needSave = false;
    protected boolean dontSave = false;
    
    /** 
     * this value is used by ICalImport/ExportFormat to store additional
     * not editable parameters
     */
    public Object userObject;
    
    /** File being shown in this tasklist */
    private FileObject file = null;
    
    /** The current timeout */
    private long currentTimeout;
    
    /**
     * During loading of a tasklist I may encounter items that have expired
     * while the IDE was shut down. Since the load-function turns off the 
     * effect of markChanged, I need to store this information in another
     * variable, and save the tasklist when the load is finished..
     */
    private boolean expiredTask;
    
    /** 
     * Timer which keeps track of outstanding save requests - that way
     * deleting multiple items for example will not cause multiple saves. 
     */
    private Timer runTimer = null;


    // User can work on one task at time (simplification) ~~~~~~~~~~~~~~~

    /**
     * Creates a new instance of TaskList
     */
    public UserTaskList() {
        tasks = new UserTaskObjectList(this);
        tasks.addListener(new ObjectList.Listener() {
            public void listChanged(ObjectList.Event ev) {
                markChanged();
            }
        });
        
        expiredTask = false;
        currentTimeout = Long.MAX_VALUE;
    }
    
    /** 
     * Location of the tasklist 
     */
    public FileObject getFile() {
        return file;
    }
    
    /**
     * Searches for owners through all tasks.
     *
     * @return all found categories
     */
    public String[] getOwners() {
        Iterator it = this.getSubtasks().iterator();
        Set cat = new java.util.HashSet();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            findOwners(ut, cat);
        }
        return (String[]) cat.toArray(new String[cat.size()]);
    }
    
    /**
     * Searches for owners
     *
     * @param task search for owners in this task and all of it's subtasks
     * recursively
     * @param cat container for found owners. <String>
     */
    private static void findOwners(UserTask task, Set cat) {
        if (task.getOwner().length() != 0)
            cat.add(task.getOwner());
        
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            findOwners((UserTask) it.next(), cat);
        }
    }
    
    /**
     * Searches for categories through all tasks.
     *
     * @return all found categories
     */
    public String[] getCategories() {
        Iterator it = this.getSubtasks().iterator();
        Set cat = new java.util.HashSet();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            findCategories(ut, cat);
        }
        return (String[]) cat.toArray(new String[cat.size()]);
    }
    
    /**
     * Searches for categories
     *
     * @param task search for categories in this task and all of it's subtasks
     * recursively
     * @param cat container for found categories. String[]
     */
    private static void findCategories(UserTask task, Set cat) {
        if (task.getCategory().length() != 0)
            cat.add(task.getCategory());
        
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            findCategories((UserTask) it.next(), cat);
        }
    }
    
    /** Write items out to disk */
    public void save() {
        if (!needSave || dontSave) {
            return;
        }

        // Write out items to disk
        scheduleWrite();
    }
    
    /**
     * Returns the default .ics file.
     *
     * @return default ics file
     */
    private static File getDefaultFile() {
        String name = Settings.getDefault().getExpandedFilename();
        File fname = FileUtil.normalizeFile(new File(name));
        return fname;
    }
    
    /**
     * Reads an ics file
     *
     * @param is an .ics file
     */
    private static UserTaskList readDocument(InputStream is) throws IOException {
        ICalImportFormat io = new ICalImportFormat();

        UserTaskList ret = new UserTaskList();
        ret.dontSave = true;
        try {
            io.read(ret, is);
        } catch (ParserException e) {
            // NOTE the exception text should be localized!
            DialogDisplayer.getDefault().notify(new Message(e.getMessage(),
               NotifyDescriptor.ERROR_MESSAGE));
        } catch (IOException e) {
            // NOTE the exception text should be localized!
            DialogDisplayer.getDefault().notify(new Message(e.getMessage(),
               NotifyDescriptor.ERROR_MESSAGE));
        }

        ret.needSave = false;
        ret.dontSave = false;        

        ret.orderNextTimeout();
        
        if (ret.expiredTask) {
            // One (or more) tasks expired while the IDE was closed...
            // save the list as soon as possible...
            ret.expiredTask = true;
            ret.markChanged();
        }
        return ret;
    }
    
    /**
     * Reads an ics file
     *
     * @param fo an .ics file
     */
    public static UserTaskList readDocument(FileObject fo) throws IOException {
        if (fo.isValid()) {
            InputStream is = fo.getInputStream();
            UserTaskList ret = null;
            try {
                long m = System.currentTimeMillis();
                ret = readDocument(is);
                UTUtils.LOGGER.fine("File " + fo + " read in " + // NOI18N
                    (System.currentTimeMillis() - m) + "ms"); // NOI18N
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            ret.file = fo;
            return ret;
        } else {
            throw new IOException(
                NbBundle.getMessage(UserTaskList.class,
                    "FileNotValid", FileUtil.getFileDisplayName(fo))); // NOI18N
        }
    }

    // Look up a particular item by uid
    public UserTask findItem(Iterator tasks, String uid) {
        while (tasks.hasNext()) {
            UserTask task = (UserTask)tasks.next();
            if (task.getUID().equals(uid)) {
                return task;
            }
            if (!task.getSubtasks().isEmpty()) {
                UserTask f = findItem(task.getSubtasks().iterator(), uid);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Process all tasks including subtasks in the depth-first order.
     *
     * @param p a callback that will be called for each task
     * @param list a list of user tasks
     */
    public static void processDepthFirst(UserTaskProcessor p, UserTaskObjectList list) {
        for (int i = 0; i < list.size(); i++) {
            UserTask ut = list.getUserTask(i);
            processDepthFirst(p, ut.getSubtasks());
            p.process(ut);
        }
    }
        
    /** 
     * Schedule a document save 
     */
    private void scheduleWrite() {
        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
	if (runTimer != null) {
	    runTimer.stop();
	    runTimer = null;
	}
	runTimer = new Timer(300, // 0.3 second delay
		     new ActionListener() {
			 public void actionPerformed(ActionEvent evt) {
                             runTimer = null;
                             
                             // Write out items to disk
                             try {
                                 writeDocument();
                             } catch (IOException ioe) {
                                 ioe.printStackTrace();
                                 DialogDisplayer.getDefault().notify(new Message(
                                    ioe, NotifyDescriptor.ERROR_MESSAGE));
                             }
                             needSave = false;
			 }
		     });
	runTimer.setRepeats(false);
	runTimer.setCoalesce(true);
	runTimer.start();
    }

    /** 
     * Write the list to iCal.
     */
    private void writeDocument() throws IOException {
        if (this.file == null)
            return;
        
        ICalExportFormat io = new ICalExportFormat();
        
        FileLock lock = this.file.lock();
        try {
            OutputStream fos = file.getOutputStream(lock);
            try {
                io.writeList(this, fos);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            } catch (ValidationException e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        } finally {
            lock.releaseLock();
        }

        // Remove permissions for others on the file when on Unix
        // varieties
        if (new File("/bin/chmod").exists()) { // NOI18N
            try {
                Runtime.getRuntime().exec(
                     new String[] {"/bin/chmod", "go-rwx",  // NOI18N
                         FileUtil.toFile(this.file).getAbsolutePath()});
            } catch (Exception e) {
                // Silently accept
                ErrorManager.getDefault().notify(
                     ErrorManager.INFORMATIONAL, e);
            }
        }

        needSave = false;
    }
    
    /**
     * Order a timeout for the next due date
     */
    void orderNextTimeout() {
        ShowExpiredUserTaskProcessor se =
            new ShowExpiredUserTaskProcessor();
        processDepthFirst(se, getSubtasks());
        
        FindNextTimeoutUserTaskProcessor p = 
            new FindNextTimeoutUserTaskProcessor();
        processDepthFirst(p, getSubtasks());
        
        if (p.ref != null && p.ref.getDueTime() != Long.MAX_VALUE && 
            !p.ref.isDueAlarmSent() && !p.ref.isDone() &&
            p.ref.getDueTime() != currentTimeout) {
            // cancel the previous ordered timeout, and add the new one
            if (currentTimeout != Long.MAX_VALUE) {
                TimeoutProvider.getInstance().cancel(this, null);
            }
            TimeoutProvider.getInstance().add(this, p.ref, p.ref.getDueTime());
            currentTimeout = p.ref.getDueTime();
        }
    }
    
    public String toString() {
        return "UserTaskList(" + file + ")"; // NOI18N
    }
    
    /**
     * Callback function for the TimeoutProvider to call when the timeout
     * expired. This function will block the TimeoutProviders thread, so
     * it should be used for a timeconsuming task (one should probably
     * reschedule oneself with the SwingUtilities.invokeLater() ???)
     * @param o the object provided as a user reference
     */
    public void timeoutExpired(Object o) {
        // order the next timeout for this list
        orderNextTimeout();
    }

    protected void setNeedSave(boolean b) {
        needSave = b;
    }

    protected void setDontSave(boolean b) {
        dontSave = b;
    }

    /** 
     * Returns top-level tasks holded by this list. 
     *
     * @return list of top-level tasks
     */
    public final UserTaskObjectList getSubtasks() {
        return tasks;
    }

    /**
     * Notify the task list that some aspect of it has been changed, so
     * it should save itself soon. Eventually calls save 
     */
    public void markChanged() {
        if (dontSave)
            return;
        orderNextTimeout();
        needSave = true;
        save();
    }

    public ObjectList getObjectList() {
        return tasks;
    }
    
    /**
     * Should be called after closing a view. Removes all annotations.
     */
    public void destroy() {
        Iterator it = getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            ut.destroy();
        }
    }
    
    /**
     * Returns all subtasks (searches recursively).
     *
     * @return list of UserTask
     */
    public List getAllSubtasks() {
        List ret = new ArrayList();
        collectAllSubtasks(ret, getSubtasks());
        return ret;
    }
    
    /**
     * Collects all tasks recursively.
     *
     * @param ret output 
     * @param tasks a list of UserTasks
     */
    private void collectAllSubtasks(List ret, UserTaskObjectList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            ret.add(tasks.getUserTask(i));
            collectAllSubtasks(ret, tasks.getUserTask(i).getSubtasks());
        }
    }
    
    /** For debugging purposes, only. Writes directly to serr. 
    public void print() {
        System.err.println("\nTask List:\n-------------");
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task next = (Task) it.next();
            recursivePrint(next, 0);
        }

        System.err.println("\n\n");
    }

    private void recursivePrint(Task node, int depth) {
        if (depth > 20) { // probably invalid list
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
        System.err.println(node);
        if (node.getSubtasks() != null) {
            List l = node.getSubtasks();
            ListIterator it = l.listIterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                recursivePrint(task, depth + 1);
            }
        }
    }*/

    // TaskListener impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
