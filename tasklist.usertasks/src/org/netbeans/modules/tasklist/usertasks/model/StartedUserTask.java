package org.netbeans.modules.tasklist.usertasks.model;

import java.util.TimerTask;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.ActivityListener;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 * Started user task.
 * This code will not work if the value of spent time will be changed for 
 * a task that is currently running. The task should be stopped and
 * then started again.
 *
 * @author tl
 */
public class StartedUserTask {
    private static final java.util.Timer TIMER = new java.util.Timer(true);

    private static final int INACTIVITY_DURATION = 10 * 60 * 1000; // 10 minutes
            
    private static final int STATE_WORKING = 0;
    private static final int STATE_SUSPENDED = 1;
    private static final int STATE_NOTASK = 2;
            
    private static final StartedUserTask INSTANCE =
        new StartedUserTask();
    
    /**
     * Returns the only instance of this class.
     *
     * @return the instance.
     */
    public static StartedUserTask getInstance() {
        return INSTANCE;
    }
    
    /**
     * Currently working on this task 
     */
    private UserTask started = null;
    
    /**
     * Time as returned by System.currentMillis() when the task was started
     */
    private long startedAt;
    
    private int initialSpentTime;
    private int state = STATE_NOTASK;
    private UserTask.WorkPeriod workPeriod;
    private EventListenerList list = new EventListenerList();

    static {
        TIMER.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (SwingUtilities.isEventDispatchThread())
                    getInstance().timer();
                else
                    SwingUtilities.invokeLater(this);
            }
        }, 0, 1000 * 15);
        ActivityListener.init();
    }
    
    /**
     * Creates a new instance of StartingTask
     */
    private StartedUserTask() {
    }

    /**
     * Fires a change event.
     */
    private void fireChange() {
        ChangeEvent event = null;
        Object[] listeners = list.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (event == null)
                    event = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }
    
    /**
     * Adds a listener that will be notified when the started task was
     * changed.
     *
     * @param l a listener
     */
    public void addChangeListener(ChangeListener l) {
        list.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a listener registered with addChangeListener.
     *
     * @param l a listener
     */
    public void removeChangeListener(ChangeListener l) {
        list.remove(ChangeListener.class, l);
    }
    
    /**
     * Executed once per minute
     */
    private void timer() {
        switch (state) {
            case STATE_NOTASK:
                break;
            case STATE_SUSPENDED: {
                long lastActivity = ActivityListener.getLastActivityMillis();
                long cur = System.currentTimeMillis();

                // summer time and similar
                if (lastActivity > cur)
                    lastActivity = cur;                                    
                
                if ((cur - lastActivity) < INACTIVITY_DURATION) {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(StartedUserTask.class,
                            "ActivityDetected"));
                    state = STATE_WORKING;
                    
                    startedAt = lastActivity;
                    int diff = (int) ((cur - startedAt) / (60 * 1000));
                    
                    started.setSpentTime(initialSpentTime + diff);
                    
                    if (Settings.getDefault().getCollectWorkPeriods()) {
                        workPeriod = new UserTask.WorkPeriod(
                            lastActivity, diff);
                        started.getWorkPeriods().add(workPeriod);
                    }
                }
                break;
            }
            case STATE_WORKING: {
                long now = System.currentTimeMillis();

                // summer time and similar
                if (startedAt > now)
                    startedAt = now;
                    
                int diff = (int) ((now - startedAt) / (60 * 1000));

                if ((System.currentTimeMillis() - 
                    ActivityListener.getLastActivityMillis()) > INACTIVITY_DURATION &&
                    Settings.getDefault().getDetectInactivity()) { // NOI18N
                    state = STATE_SUSPENDED;
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(StartedUserTask.class, 
                            "InactivityDetected"));
                }
                
                started.setSpentTime(initialSpentTime + diff);                    
                if (Settings.getDefault().getCollectWorkPeriods()) {
                    // it is possible that workPeriod is null although
                    // getCollectWorkPeriods() returns true if the
                    // value of the option was changed during this
                    // task was running
                    if (workPeriod == null) {
                        workPeriod = new UserTask.WorkPeriod(startedAt, diff);
                        started.getWorkPeriods().add(workPeriod);
                    } else {
                        workPeriod.setDuration(diff);
                    }
                }
                break;
            }
            default:
                throw new InternalError("wrong state"); // NOI18N
        }
    }
    
    /**
     * Starts another task. If a task is currently running it should be 
     * stopped first.
     *
     * @param task currently working on this task. May be null.
     */
    public void start(UserTask task) {
        switch (state) {
            case STATE_WORKING: {
                if (task != null) {
                    throw new InternalError("the task " + started + // NOI18N
                        " should be stopped first"); // NOI18N
                } else {
                    long now = System.currentTimeMillis();

                    // summer time and similar
                    if (startedAt > now)
                        startedAt = now;
                    
                    int diff = (int) ((now - startedAt) / (60 * 1000));
                    
                    updateSpentTime(diff);
                    UserTask ut = started;
                    clear();
                    ut.clearEmptyWorkPeriods();
                    ut.firePropertyChange("started", Boolean.TRUE, 
                        Boolean.FALSE); // NOI18N
                    fireChange();
                }
                break;
            }
            case STATE_SUSPENDED: {
                if (task != null) {
                    throw new InternalError("the task " + started + // NOI18N
                        " should be stopped first"); // NOI18N
                } else {
                    long lastActivity = ActivityListener.getLastActivityMillis();
                    long now = System.currentTimeMillis();

                    // summer time and similar
                    if (lastActivity > now)
                        lastActivity = now;
                    
                    int diff = (int) ((now - lastActivity) / (60 * 1000));
                    
                    workPeriod = null;
                    updateSpentTime(diff);
                    UserTask ut = started;
                    clear();
                    ut.clearEmptyWorkPeriods();
                    ut.firePropertyChange("started", Boolean.TRUE, 
                        Boolean.FALSE); // NOI18N
                    fireChange();
                }
                break;
            }
            case STATE_NOTASK: {
                if (task == null) {
                    throw new InternalError("no task is running"); // NOI18N
                } else {
                    task.setValuesComputed(false);
                    started = task;
                    startedAt = System.currentTimeMillis();
                    initialSpentTime = task.getSpentTime();
                    state = STATE_WORKING;
                    updateSpentTime(0);
                    started.firePropertyChange("started", Boolean.FALSE, 
                        Boolean.TRUE); // NOI18N
                    fireChange();
                }
                break;
            }
            default:
                throw new InternalError("wrong state"); // NOI18N
        }
    }

    /**
     * Updates the spentTime property of the running task.
     *
     * @param diff spent time since startedAt in minutes
     */
    private void updateSpentTime(int diff) {
        started.setSpentTime(initialSpentTime + diff);                    
        if (Settings.getDefault().getCollectWorkPeriods()) {
            // it is possible that workPeriod is null although
            // getCollectWorkPeriods() returns true if the
            // value of the option was changed during this
            // task was running
            if (workPeriod == null) {
                workPeriod = new UserTask.WorkPeriod(startedAt, diff);
                started.getWorkPeriods().add(workPeriod);
            } else {
                workPeriod.setDuration(diff);
            }
        }
    }
    
    /**
     * Clears all internal fields
     * and returns to STATE_NOTASK.
     */
    private void clear() {
        started = null;
        startedAt = 0;
        initialSpentTime = 0;
        state = STATE_NOTASK;
        workPeriod = null;
    }
    
    /**
     * Returns started task
     *
     * @return started task or null
     */
    public UserTask getStarted() {
        return started;
    }
}
