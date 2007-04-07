package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Image;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.Utilities;

/**
 * Returns an icon for a given UT.
 *
 * @author tl
 */
public class UserTaskIconProvider {
    private static final Image LIST_IMAGE =
        Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/tasklistfile.gif"); // NOI18N
    private static final Image IMAGE =
        Utilities.loadImage(
            "org/netbeans/modules/tasklist/core/task.gif"); // NOI18N
    private static final Image DONE =
        Utilities.loadImage(
            "org/netbeans/modules/tasklist/core/doneItem.gif"); // NOI18N
    private static final Image UNMATCHED =
        Utilities.loadImage(
            "org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
    
    private static final Image STARTED_BADGE =
        Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/startedBadge.gif"); // NOI18N

    private static final Image IMAGE_STARTED = 
        Utilities.mergeImages(IMAGE, STARTED_BADGE, 8, 6);
    private static final Image DONE_STARTED = 
        Utilities.mergeImages(DONE, STARTED_BADGE, 8, 6);
    private static final Image UNMATCHED_STARTED = 
        Utilities.mergeImages(UNMATCHED, STARTED_BADGE, 8, 6);
    
    /**
     * Returns the icon for a task list.
     *
     * @return the icon
     */
    public static Image getUserTaskListImage() {
        return LIST_IMAGE;
    }
    
    /**
     * Returns a 16x16 icon.
     *
     * @param ut a task
     * @param unmatched true for tasks that do not pass through the filter
     * but have children that do
     * @return icon
     */
    public static Image getUserTaskImage(UserTask ut, boolean unmatched) {
        if (ut.isStarted()) {
            if (unmatched)
                return UNMATCHED_STARTED;
            else if (ut.isDone())
                return DONE_STARTED;
            else
                return IMAGE_STARTED;
        } else {
            if (unmatched)
                return UNMATCHED;
            else if (ut.isDone())
                return DONE;
            else
                return IMAGE;
        }
    }
    
    /** 
     * Creates a new instance of UserTaskIconProvider 
     */
    private UserTaskIconProvider() {
    }
}
