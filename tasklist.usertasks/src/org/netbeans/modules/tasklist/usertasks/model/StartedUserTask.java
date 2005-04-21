package org.netbeans.modules.tasklist.usertasks.model;

import java.util.TimerTask;
import org.netbeans.modules.tasklist.core.util.ActivityListener;
import org.netbeans.modules.tasklist.usertasks.Settings;

/**
 * Started user task.
 */
public class StartedUserTask {
    private static final java.util.Timer TIMER = new java.util.Timer(true);

    /**
     * Currently working on this task 
     */
    private static UserTask started = null;
    
    /**
     * The running task was suspended. It means that after the user
     * touched the mouse it will be running again.
     */
    private static boolean suspended = false;
    
    /**
     * Time as returned by System.currentMillis() when the task was started
     */
    private static long startedAt;
    
    private static int initialDuration;
    
    /**
     * Old value of the duration attribute of the current task
     */
    private static int lastDuration;

    static {
        TIMER.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timer();
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
     * Executed once per minute
     */
    private static void timer() {
        if (started == null)
            return;
        
        long now = System.currentTimeMillis();
        if (lastDuration == started.getSpentTime()) {
            if ((System.currentTimeMillis() - 
                ActivityListener.getLastActivityMillis()) > 10 * 60 * 1000 &&
                System.getProperty("org.netbeans.modules.tasklist.usertasks.DontDetectInactivity") == null) { // NOI18N
                if (Settings.getDefault().getCollectWorkPeriods()) {
                    int m = (int) ((System.currentTimeMillis() - startedAt) / 
                        (1000 * 60));
                    if (m != 0)
                        started.getWorkPeriods().add(new UserTask.WorkPeriod(
                            startedAt, m));
                }
                suspended = true;
            } else {
                if (suspended) {
                    startedAt = ActivityListener.getLastActivityMillis();
                    initialDuration = started.getSpentTime();
                    suspended = false;
                }
                int diff = (int) ((now - startedAt) / (60 * 1000));
                started.setSpentTime(initialDuration + diff);
                lastDuration = started.getSpentTime();
            }
        } else {
            if (Settings.getDefault().getCollectWorkPeriods()) {
                int m = (int) ((System.currentTimeMillis() - startedAt) / 
                    (1000 * 60));
                if (m != 0)
                    started.getWorkPeriods().add(new UserTask.WorkPeriod(
                        startedAt, m));
            }
            
            startedAt = System.currentTimeMillis();
            lastDuration = initialDuration = started.getSpentTime();
        }
    }
    
    /**
     * Starts another task
     *
     * @param task currently working on this task. May be null.
     */
    public static void start(UserTask task) {
        assert started != task;
        if (started == task)
            return;
        
        if (started != null) {
            if (Settings.getDefault().getCollectWorkPeriods()) {
                int m = (int) ((System.currentTimeMillis() - startedAt) / 
                    (1000 * 60));
                if (m != 0)
                    started.getWorkPeriods().add(new UserTask.WorkPeriod(
                        startedAt, m));
            }
            timer();
        }
        
        UserTask oldStarted = started;
        started = task;
        
        if (oldStarted != null)
            oldStarted.firePropertyChange("started", Boolean.TRUE, Boolean.FALSE); // NOI18N
        
        if (started != null) {
            started.setSpentTimeComputed(false);
            startedAt = System.currentTimeMillis();
            lastDuration = task.getSpentTime();
            initialDuration = task.getSpentTime();
            started.firePropertyChange("started", Boolean.FALSE, Boolean.TRUE); // NOI18N
        }
    }
    
    /**
     * Returns started task
     *
     * @return started task or null
     */
    public static UserTask getStarted() {
        return started;
    }
}
