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


package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.openide.options.SystemOption;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;


/** Settings for the tasklist module.
 */

public final class Settings extends SystemOption {

    /** serial uid */
    static final long serialVersionUID = -29424677132370773L;

    // Option labels
    public static final String PROP_SCAN_SKIP = "skipComments";	//NOI18N
    public static final String PROP_SCAN_TAGS = "taskTags";		//NOI18N
    static final String PROP_MODIFICATION_TIME = "modificationTime";  // NOI18N

    /** Defines how many suggestions make sence. */
    public static final String PROP_USABILITY_LIMIT = "usabilityLimit";  // NOI18N
    private final static int DEFAULT_USABILITY_LIMIT = 300;


    /** Return the signleton */
    public static Settings getDefault() {
        return (Settings) findObject(Settings.class, true);
    }


    /**
     * Get the display name.
     *
     *  @return value of OPTION_TASK_SETTINGS_NAME
     */
    public String displayName() {
        return NbBundle.getMessage(Settings.class,
                "OPTION_TASK_SETTINGS_NAME"); //NOI18N
    }

    public HelpCtx getHelpCtx () {
	return new HelpCtx (org.netbeans.modules.tasklist.docscan.Settings.class);	        //NOI18N
    }


    /**
     * @return true iff the user wants to skip all tasks tokens
     * appear outside of comment sections.  The default value
     * is true.
     */
    public boolean getSkipComments() {
        // XXX I did a spectacularly poor job naming this method.
        // I never skip comments, I skip non-comments.
        Boolean b = (Boolean) getProperty(PROP_SCAN_SKIP);

        /*
	// Default to on
	return (b != Boolean.FALSE);
        */

        // Default to off (null != Boolean.TRUE)
        return (b == Boolean.TRUE);
    }

    /** Sets the skip-outside-of-comments property
     * @param doSkip True iff you want to skip tasks outside of comments
     */
    public void setSkipComments(boolean doSkip) {
        Boolean b = doSkip ? Boolean.TRUE : Boolean.FALSE;
        putProperty(PROP_SCAN_SKIP, b, true);
        modified();
        //firePropertyChange(PROP_SCAN_SKIP, null, b);
    }


    public void setUsabilityLimit(int limit) {
        if (limit > 1000) limit = 1000;
        if (limit <=0) limit = DEFAULT_USABILITY_LIMIT;
        putProperty(PROP_USABILITY_LIMIT, new Integer(limit));
    }

    public int getUsabilityLimit() {
        Integer limit = (Integer) getProperty(PROP_USABILITY_LIMIT);
        if (limit == null) {
            return DEFAULT_USABILITY_LIMIT;
        } else {
            return limit.intValue();
        }
    }

    public TaskTags getTaskTags() {
        if (tags == null) {
            TaskTags d = (TaskTags) getProperty(PROP_SCAN_TAGS);
            if (d != null) {
                tags = d;
            } else {
                tags = new TaskTags();
                tags.setTags(new TaskTag[]{
                    new TaskTag("@todo", SuggestionPriority.MEDIUM),
                    new TaskTag("TODO", SuggestionPriority.MEDIUM),
                    new TaskTag("FIXME", SuggestionPriority.MEDIUM),
                    new TaskTag("XXX", SuggestionPriority.MEDIUM),
                    new TaskTag("PENDING", SuggestionPriority.MEDIUM),
                    // XXX CVS merge conflict: overlaps with skipNonComments settings
                    new TaskTag("<<<<<<<", SuggestionPriority.HIGH),

                    // Additional candidates: HACK, WORKAROUND, REMOVE, OLD
                });
                ;
            }
        }
        return tags;
    }

    private TaskTags tags = null;

    /** Sets the skip-outside-of-comments property
     * @param doSkip True iff you want to skip tasks outside of comments
     */
    public void setTaskTags(TaskTags scanTasks) {
        tags = scanTasks;
        putProperty(PROP_SCAN_TAGS, tags, true);
        modified();
        //firePropertyChange(PROP_SCAN_TAGS, null, b);
    }


    /**
     * Last modification time is stored as hidden property.
     */
    public long getModificationTime() {
        Long time = (Long) getProperty(PROP_MODIFICATION_TIME);
        if (time == null) {
            return 0;
        } else {
            return time.longValue();
        }
    }

    /** for deserialization purposes only */
    public void setModificationTime(long time) {
        putProperty(PROP_MODIFICATION_TIME, new Long(time));
    }

    // update modification time
    private void modified() {
        if (this.isReadExternal() == false) {
            putProperty(PROP_MODIFICATION_TIME, new Long(System.currentTimeMillis()));
        }
    }
}
