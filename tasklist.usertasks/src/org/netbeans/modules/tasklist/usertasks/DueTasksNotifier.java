/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.tasklist.usertasks.model.*;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList.UserTaskProcessor;
import org.openide.DialogDisplayer;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Notifies the user about due tasks.
 *
 * @author tl
 */
public class DueTasksNotifier implements Timeout {
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

                    String title = NbBundle.getMessage(UserTaskList.class, 
                            "TaskDueLabel"); // NOI18N
                    DialogDescriptor d = new DialogDescriptor(panel, title);                
                    d.setModal(true);
                    d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
                    d.setOptions(new Object[] {DialogDescriptor.OK_OPTION});
                    java.awt.Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
                    dlg.pack();
                    dlg.setVisible(true);
                }
            });
        }
    }
    
    /** The current timeout */
    private long currentTimeout;    
    
    private UserTaskList utl;
    
    /**
     * Creates a new instance of DueTasksNotifier
     *
     * @param utl a task list
     */
    public DueTasksNotifier(UserTaskList utl) {
        this.utl = utl;
        currentTimeout = Long.MAX_VALUE;
        utl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                orderNextTimeout();
            }
        });
    }

    /**
     * Order a timeout for the next due date
     */
    public void orderNextTimeout() {
        ShowExpiredUserTaskProcessor se =
            new ShowExpiredUserTaskProcessor();
        utl.processDepthFirst(se, utl.getSubtasks());
        
        FindNextTimeoutUserTaskProcessor p = 
            new FindNextTimeoutUserTaskProcessor();
        utl.processDepthFirst(p, utl.getSubtasks());
        
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

}
