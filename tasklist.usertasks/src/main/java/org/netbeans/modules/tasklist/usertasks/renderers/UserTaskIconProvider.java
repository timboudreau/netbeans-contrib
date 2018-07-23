package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.FastImageIcon;
import org.openide.util.Utilities;

/**
 * Returns an icon for a given UT.
 *
 * @author tl
 */
public class UserTaskIconProvider {
    private static final ImageIcon LIST_IMAGE =
            new FastImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/tasklistfile.gif")); // NOI18N
    private static final ImageIcon IMAGE =
            new FastImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/task.gif")); // NOI18N
    private static final ImageIcon DONE =
            new FastImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/doneItem.gif")); // NOI18N
    private static final ImageIcon UNMATCHED =
            new FastImageIcon(Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/unmatched.gif")); // NOI18N
    
    private static final Image STARTED_BADGE =
            Utilities.loadImage(
            "org/netbeans/modules/tasklist/usertasks/startedBadge.gif"); // NOI18N

    private static final ImageIcon IMAGE_STARTED = 
            new FastImageIcon(Utilities.mergeImages(
            IMAGE.getImage(), STARTED_BADGE, 8, 6));
    private static final ImageIcon DONE_STARTED = 
            new FastImageIcon(
            Utilities.mergeImages(
            DONE.getImage(), STARTED_BADGE, 8, 6));
    private static final ImageIcon UNMATCHED_STARTED = 
            new FastImageIcon(
            Utilities.mergeImages(
            UNMATCHED.getImage(), STARTED_BADGE, 8, 6));
    
    /**
     * Returns the icon for a task list.
     *
     * @return the icon
     */
    public static Image getUserTaskListImage() {
        return LIST_IMAGE.getImage();
    }
    
    /**
     * Returns a 16x16 icon.
     *
     * @param ut a task
     * @param unmatched true for tasks that do not pass through the filter
     * but have children that do
     * @return icon
     */
    public static ImageIcon getUserTaskImage(UserTask ut, boolean unmatched) {
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
