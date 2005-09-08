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

package org.netbeans.modules.tasklist.usertasks.filter;

import java.net.URL;
import java.util.Date;

import org.netbeans.modules.tasklist.core.TaskProperties;
import org.netbeans.modules.tasklist.core.filter.SuggestionProperty;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

public class UserTaskProperties extends TaskProperties {
    public static final String PROPID_PRIORITY = "priority"; // NOI18N
    public static final String PROPID_SUMMARY = "summary"; // NOI18N
    public static final String PROPID_DETAILS = "details"; // NOI18N
    public static final String PROPID_CATEGORY = "category"; // NOI18N
    public static final String PROPID_FILENAME = "filename"; // NOI18N
    public static final String PROPID_LINE_NUMBER = "line"; // NOI18N
    public static final String PROPID_CREATED_DATE = "created"; // NOI18N
    public static final String PROPID_LAST_EDITED_DATE = "edited"; // NOI18N
    public static final String PROPID_DUE_DATE = "dueDate"; // NOI18N
    public static final String PROPID_DONE = "done"; // NOI18N
    public static final String PROPID_PERCENT_COMPLETE = "percentComplete"; // NOI18N
    public static final String PROPID_EFFORT = "effort"; // NOI18N
    public static final String PROPID_REMAINING_EFFORT = "remainingEffort"; // NOI18N
    public static final String PROPID_SPENT_TIME = "spentTime"; // NOI18N
    public static final String PROPID_OWNER = "owner"; // NOI18N
    public static final String PROPID_COMPLETED_DATE = "completedDate"; // NOI18N
    public static final String PROPID_START = "start"; // NOI18N
    public static final String PROPID_SPENT_TIME_TODAY = "spentTimeToday"; // NOI18N

  public static SuggestionProperty PROP_SUMMARY = 
    new SuggestionProperty(PROPID_SUMMARY, String.class) { 
      public Object getValue(Object obj) {return ((UserTask) obj).getSummary(); }
    };

  public static SuggestionProperty PROP_PRIORITY = 
    new SuggestionProperty(PROPID_PRIORITY, String.class) {   
      public Object getValue(Object obj) {
          return new Integer(((UserTask) obj).getPriority()); 
      }
    };

  public static SuggestionProperty PROP_DETAILS = 
    new SuggestionProperty(PROPID_DETAILS, String.class) {   
      public Object getValue(Object obj) {return ((UserTask) obj).getDetails(); }
    };

    public static final SuggestionProperty PROP_CATEGORY =
        new SuggestionProperty(PROPID_CATEGORY, String.class) {
            public Object getValue(Object obj) {
                return ((UserTask) obj).getCategory();
            }
        };

    public static final SuggestionProperty PROP_FILENAME =
        new SuggestionProperty(PROPID_FILENAME, String.class) {
            public Object getValue(Object obj) {
                URL url = ((UserTask) obj).getUrl();
                if (url == null)
                    return ""; // NOI18N
                else
                    return url.toExternalForm();
            }
        };

    public static final SuggestionProperty PROP_LINE_NUMBER =
        new SuggestionProperty(PROPID_LINE_NUMBER, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getLineNumber() + 1);
            }
        };

    public static final SuggestionProperty PROP_CREATED_DATE =
        new SuggestionProperty(PROPID_CREATED_DATE, Date.class) {
            public Object getValue(Object obj) {
                return new Date(((UserTask) obj).getCreatedDate());
            }
        };

    public static final SuggestionProperty PROP_LAST_EDITED_DATE =
        new SuggestionProperty(PROPID_LAST_EDITED_DATE, Date.class) {
            public Object getValue(Object obj) {
                return new Date(((UserTask) obj).getLastEditedDate());
            }
        };

    public static final SuggestionProperty PROP_COMPLETED_DATE =
        new SuggestionProperty(PROPID_COMPLETED_DATE, Date.class) {
        public Object getValue(Object obj) {
            return new Date(((UserTask) obj).getCompletedDate());
        }
    };
        
    public static final SuggestionProperty PROP_DUE_DATE =
        new SuggestionProperty(PROPID_DUE_DATE, Date.class) {
            public Object getValue(Object obj) {
                return ((UserTask) obj).getDueDate();
            }
        };

    public static final SuggestionProperty PROP_DONE =
        new SuggestionProperty(PROPID_DONE, Boolean.class) {
            public Object getValue(Object obj) {
                return Boolean.valueOf(((UserTask) obj).isDone());
            }
        };

    public static final SuggestionProperty PROP_PERCENT_COMPLETE =
        new SuggestionProperty(PROPID_PERCENT_COMPLETE, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getPercentComplete());
            }
        };

    public static final SuggestionProperty PROP_EFFORT =
        new SuggestionProperty(PROPID_EFFORT, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getEffort());
            }
        };

    public static final SuggestionProperty PROP_REMAINING_EFFORT =
        new SuggestionProperty(PROPID_REMAINING_EFFORT, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getRemainingEffort());
            }
        };

    public static final SuggestionProperty PROP_SPENT_TIME =
        new SuggestionProperty(PROPID_SPENT_TIME, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getSpentTime());
            }
        };

    public static final SuggestionProperty PROP_SPENT_TIME_TODAY =
        new SuggestionProperty(PROPID_SPENT_TIME_TODAY, Integer.class) {
            public Object getValue(Object obj) {
                return new Integer(((UserTask) obj).getSpentTimeToday());
            }
        };

    public static final SuggestionProperty PROP_OWNER =
        new SuggestionProperty(PROPID_OWNER, String.class) {
            public Object getValue(Object obj) {
                return ((UserTask) obj).getOwner();
            }
        };

    public static final SuggestionProperty PROP_START =
        new SuggestionProperty(PROPID_START, Date.class) {
            public Object getValue(Object obj) {
                long start = ((UserTask) obj).getStart();
                if (start == -1)
                    return null;
                else
                    return new Date(start);
            }
        };

    public static SuggestionProperty getProperty(String propID) {
        if (propID.equals(PROPID_CATEGORY)) {
            return PROP_CATEGORY;
        } else if (propID.equals(PROPID_FILENAME)) {
            return PROP_FILENAME;
        } else if (propID.equals(PROPID_LINE_NUMBER)) {
            return PROP_LINE_NUMBER;
        } else if (propID.equals(PROPID_CREATED_DATE)) {
            return PROP_CREATED_DATE;
        } else if (propID.equals(PROPID_LAST_EDITED_DATE)) {
            return PROP_LAST_EDITED_DATE;
        } else if (propID.equals(PROPID_COMPLETED_DATE)) {
            return PROP_COMPLETED_DATE;
        } else if (propID.equals(PROPID_DUE_DATE)) {
            return PROP_DUE_DATE;
        } else if (propID.equals(PROPID_DONE)) {
            return PROP_DONE;
        } else if (propID.equals(PROPID_PERCENT_COMPLETE)) {
            return PROP_PERCENT_COMPLETE;
        } else if (propID.equals(PROPID_EFFORT)) {
            return PROP_EFFORT;
        } else if (propID.equals(PROPID_REMAINING_EFFORT)) {
            return PROP_REMAINING_EFFORT;
        } else if (propID.equals(PROPID_SPENT_TIME)) {
            return PROP_SPENT_TIME;
        } else if (propID.equals(PROPID_DETAILS)) {
            return PROP_DETAILS;
        } else if (propID.equals(PROPID_OWNER)) {
            return PROP_OWNER;
        } else if (propID.equals(PROPID_PRIORITY)) { 
            return PROP_PRIORITY;
        } else if (propID.equals(PROPID_SUMMARY)) { 
            return PROP_SUMMARY;
        } else if (propID.equals(PROPID_DETAILS)) { 
            return PROP_DETAILS;
        } else if (propID.equals(PROPID_START)) { 
            return PROP_START;
        } else if (propID.equals(PROPID_SPENT_TIME_TODAY)) {
            return PROP_SPENT_TIME_TODAY;
        } else {
            throw new IllegalArgumentException("Unresolved property id " + propID); // NOI18N
        }
    }
}
