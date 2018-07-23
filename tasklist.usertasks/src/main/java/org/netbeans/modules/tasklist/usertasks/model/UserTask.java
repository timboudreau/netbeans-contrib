/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.util.UTListTreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.nodes.Node.Cookie;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.usertasks.annotations.UTAnnotation;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;


/**
 * Class which represents a task in the
 * tasklist.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public final class UserTask implements Cloneable, Cookie,
        PropertyChangeListener {
    /**
     * A period spent working on a task.
     */
    public static class WorkPeriod implements Comparable {
        private long start;
        private int duration;
        
        /**
         * Start
         *
         * @param start start point as returned from System.currentTimeMillis()
         * @param duration duration in minutes
         */
        public WorkPeriod(long start, int duration) {
            this.start = start;
            this.duration = duration;
        }

        public Object clone() throws CloneNotSupportedException {
            return new WorkPeriod(start, duration);
        }
        
        /**
         * Checks whether this period intersects another one.
         * 
         * @param wp a work period
         * @return true = two periods intersect 
         */
        public boolean intersects(WorkPeriod wp) {
            long wpend = wp.start + wp.duration * 60 * 1000;
            long end = start + duration * 60 * 1000;
            return (duration + wp.duration) * 60 * 1000 >
                    Math.max(end, wpend) - Math.min(start, wp.start);
        }
        
        /**
         * Returns the starting point of this period.
         *
         * @return time as returned by System.currentTimeMillis()
         */
        public long getStart() {
            return start;
        }
        
        /**
         * Returns the duration of this period.
         *
         * @return duration in minutes
         */
        public int getDuration() {
            return duration;
        }
        
        /**
         * Changes the duration.
         *
         * @param dur new duration in minutes
         */
        public void setDuration(int dur) {
            this.duration = dur;
        }
        
        /**
         * Tests whether this period was started today.
         *
         * @return true = yes
         */
        public boolean isToday() {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return today.getTimeInMillis() <= start;
        }
    
        public int compareTo(Object obj) {
            if (!(obj instanceof WorkPeriod))
                return -1;
            WorkPeriod wp = (WorkPeriod) obj;
            if (this.start < wp.start)
                return -1;
            else if (this.start > wp.start)
                return 1;
            else
                return 0;
        }
    }
    
    /** Highest priority */
    public static final int HIGH = 1;

    /** Normal/default priority */
    public static final int MEDIUM_HIGH = 2;

    /** Normal/default priority */
    public static final int MEDIUM = 3;

    /** Normal/default priority */
    public static final int MEDIUM_LOW = 4;

    /** Lowest priority */
    public static final int LOW = 5;
    
    /**
     * Priority values that correspond to the values returned by
     * getPriorityNames
     */
    public static final int[] PRIORITY_VALUES = {
        HIGH,
        MEDIUM_HIGH,
        MEDIUM,
        MEDIUM_LOW,
        LOW
    };
    
    /** Keys for the Bundle.properties */
    private static final String[] PRIORITIES_KEYS = {
        "PriorityHigh",  // NOI18N
        "PriorityMediumHigh", // NOI18N
        "PriorityMedium", // NOI18N
        "PriorityMediumLow", // NOI18N
        "PriorityLow" // NOI18N
    };

    /** Names for priorities */
    private static String[] PRIORITIES;

    static {
        PRIORITIES = new String[PRIORITIES_KEYS.length];
        ResourceBundle rb = NbBundle.getBundle(UserTask.class);
        for (int i = 0; i < PRIORITIES_KEYS.length; i++) {
            PRIORITIES[i] = rb.getString(PRIORITIES_KEYS[i]);
        }
    }
    
    /**
     * Returns priority text for the specified value.
     *
     * @param p priority
     */
    public static String getPriorityName(int p) {
        return PRIORITIES[p - 1];
    }    
    
    /**
     * Reduces a list of tasks in such a way that none of tasks is an 
     * ancestor of another.
     *
     * @param tasks user tasks from one task list. None of the element
     * could be included twice.
     * @return reduced array
     */
    public static UserTask[] reduce(UserTask[] tasks) {
        List<UserTask> res = new ArrayList<UserTask>();
        for (int i = 0; i < tasks.length; i++) {
            boolean ok = true;
            for (int j = 0; j < tasks.length; j++) {
                if (j != i) {
                    if (tasks[j].isAncestorOf(tasks[i])) {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok)
                res.add(tasks[i]);
        }
        return res.toArray(new UserTask[res.size()]);
    }
    
    /**
     * Returns a priority for the specified localized text.
     *
     * @param name localized priority text like "high"
     * @return priority value or -1 if not found
     */
    public static int getPriority(String name) {
        for (int i = 0; i < PRIORITIES.length; i++) {
            if (PRIORITIES[i].equals(name))
                return PRIORITY_VALUES[i];
        }
        return -1;
    }
    
    /**
     * Returns localized names for priorities
     *
     * @return [0] - high, [1] - medium-high, ...
     */
    public static String[] getPriorityNames() {
        return PRIORITIES;
    }

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    /** Id of bound summary property. */
    public static final String PROP_SUMMARY = "summary"; // NOI18N

    /** Id of bound icon property. */
    public static final String PROP_ICON = "icon"; // NOI18N

    /** Id of bound details property. */
    public static final String PROP_DETAILS = "details"; // NOI18N

    /** Id of bound priority property. */
    public static final String PROP_PRIORITY = "priority"; // NOI18N

    /** Id of bound "valid" property. */
    public static final String PROP_VALID = "valid"; // NOI18N
    
    /** line number property. Integer */
    public static final String PROP_LINE_NUMBER = "lineNumber"; // NOI18N
    
    /** property name for the associated line object (org.openide.text.Line) */
    public static final String PROP_LINE = "line"; // NOI18N

    public static final String PROP_DUE_DATE = "dueDate"; // NOI18N
    public static final String PROP_CATEGORY = "category"; // NOI18N
    public static final String PROP_PROGRESS = "progress"; // NOI18N
    public static final String PROP_EFFORT = "effort"; // NOI18N
    public static final String PROP_REMAINING_EFFORT = "remainingEffort"; // NOI18N
    public static final String PROP_SPENT_TIME = "spentTime"; // NOI18N
    public static final String PROP_OWNER = "owner"; // NOI18N
    public static final String PROP_COMPLETED_DATE = "completedDate"; // NOI18N
    public static final String PROP_WORK_PERIODS = "workPeriods"; // NOI18N
    public static final String PROP_START = "start"; // NOI18N
    public static final String PROP_SPENT_TIME_TODAY = "spentTimeToday"; // NOI18N
    
    /** will be fired if something in resources has changed. */
    public static final String PROP_RESOURCES = "resources"; // NOI18N
    
    /** last modified. long as returned by System.currentTimeMillis() */
    public static final String PROP_LAST_EDITED_DATE = 
            "lastEditedDate"; // NOI18N
    
    /** see setValuesComputed */
    public static final String PROP_VALUES_COMPUTED = 
            "valuesComputed"; // NOI18N
    
    // ATTENTION: if you add new fields here do not forget to update copyFrom
    
    /**
     * When false, don't update the "last modified" date. 
     * Used by the restore code.
     */
    private boolean updateLastModified = true;

    private UserTask parent;

    /** If this item has subtasks, they are stored in this list */
    private UserTaskObjectList subtasks;

    /** 
     * Used to create uid's. May be expensive to compute, so only do
     * it once. 
     */
    private static String domain = null;
    
    /** Used to create uid's. Assign unique id's for this session. */
    private static int unique = 0; 
    
    /** A summary (one-line description) of the task */
    private String summary = null;

    /** A (possibly) multi-line summary of the task */
    private String details = null;
    
    /** The priority of this suggestion, defaults to SuggestionPriority.MEDIUM */
    private int priority = UserTask.MEDIUM;

    /** 
     * this value is used by ICalImport/ExportFormat to store additional
     * not editable parameters
     */
    public Object userObject;
    
    /** may be null if this task was removed from the list */
    private UserTaskList list;
    
    private String uid;
    
    /** 
     * 0..100 - value in percents 
     */
    private float progress = 0.0f;
    
    /**
     * true means progress will be computed automatically as a weighted average 
     * of the subtasks. If a task has no children it means 0%
     * true means that the effort will be computed automatically as them sum of the
     * subtask efforts. If a task has no children it means 0
     */
    private boolean valuesComputed = false;
    
    private Date dueDate;
    private boolean dueAlarmSent;
    
    private String category;
    private long created;
    private long lastEditedDate;

    /** in minutes */
    private int effort = 60;
    
    /** Time spent on this task */
    private int spentTime = 0;
    
    private PropertyChangeListener lineListener;

    private ObjectList<Dependency> dependencies = new ObjectList<Dependency>();
    private String owner = ""; // NOI18N
    private long completedDate = 0;
    
    /** 
     * Start date/time for the task as returned by System.currentTimeMillis or -1
     * if undefined
     */
    private long start = -1;
    
    // <WorkPeriod>
    private ObjectList<WorkPeriod> workPeriods = new ObjectList<WorkPeriod>();
    
    private ObjectList<UserTaskResource> resources = 
            new ObjectList<UserTaskResource>();
    // ATTENTION: if you add new fields here do not forget to update copyFrom
    
    /**
     * Creates a task with the specified description
     *
     * @param summary description
     * @param list task list that this task belongs to
     */
    public UserTask(String summary, UserTaskList list) {
        this(summary, false, 3, "", "", null, list); // NOI18N
    }
    
    /**
     *
     * Construct a new task with the given parameters.
     *
     * @param summary description
     * @param done true = the task is done
     * @param priority 1..5 (High..Low)
     * @param details details
     * @param category task's category ("" - no category)
     * @param parent parent task
     * @param list task list that this task belongs to
     */    
    public UserTask(String summary, boolean done, int priority, String details, 
    String category, UserTask parent, UserTaskList list) {
    	this.summary = summary;
    	this.parent = parent;
        
        assert priority >= 1 && priority <= 5 : "priority ?"; // NOI18N
        assert summary != null : "desc == null"; // NOI18N
        assert details != null : "details == null"; // NOI18N
        assert category != null : "category == null"; // NOI18N

        workPeriods.addListener(new ObjectListListener() {
            public void listChanged(ObjectListEvent event) {
                firePropertyChange(PROP_WORK_PERIODS, null, null);
            }
        });
        
        resources.addListener(new ObjectListListener() {
            public void listChanged(ObjectListEvent e) {
                firePropertyChange(PROP_RESOURCES, null, null);
            }
        });
        
        lineListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() == Line.PROP_LINE_NUMBER) {
                    firePropertyChange("lineNumber", // NOI18N
                        e.getOldValue(), e.getNewValue());
                }
            }
        };
        
        subtasks = new UserTaskObjectList(this);
        subtasks.addListener(new ObjectListListener() {
            public void listChanged(ObjectListEvent ev) {
                switch (ev.getType()) {
                    case ObjectListEvent.EVENT_ADDED:
                        structureChanged();
                        break;
                    case ObjectListEvent.EVENT_REMOVED: {
                        structureChanged();
                        break;
                    }
                    case ObjectListEvent.EVENT_REORDERED:
                        structureChanged();
                        break;
                    case ObjectListEvent.EVENT_STRUCTURE_CHANGED:
                        structureChanged();
                        break;
                    default:
                        throw new InternalError("unexpected event type"); // NOI18N
                }
            }
            public void structureChanged() {
                if (isValuesComputed()) {
                    setProgress_(computeProgress());
                    setEffort_(computeEffort());
                    setSpentTime_(computeSpentTime());
                }
                if (UserTask.this.list != null) {
                    UserTask.this.list.fireChange();
                }
            }
        });
    
        this.list = list;
        
        if (done)
            setDone(true);
        
        setPriority(priority);

        this.category = category;
        
        setDetails(details);

        created = System.currentTimeMillis();
        lastEditedDate = created;

	if (domain == null) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                domain = address.toString();
            } catch (UnknownHostException e) {
                domain = "unknown"; // NOI18N
            }
	}
        
        // XXX Later, come up with a better time stamp, e.g. use DateFormat
	String timestamp = Long.toString(System.currentTimeMillis());
        
	// uid = "nb" + timestamp + "." + (unique++) + "@" + domain; 
        uid = new StringBuilder(50).append("nb").append(timestamp). // NOI18N
            append('.').append(unique++).append('@').append(domain).toString();

        addPropertyChangeListener(this);
    }

    /**
     * Merges 
     * 
     * @param wps sorted array 
     */
    private void merge(ObjectList<WorkPeriod> wps) {
        for (int i = 0; i < wps.size() - 1; i++) {
            WorkPeriod a = wps.get(i);
            WorkPeriod b = wps.get(i + 1);
            if (a.intersects(b)) {
                
            }
        }
    }
    
    /**
     * Returns work periods for this task.
     *
     * @return <WorkPeriod>
     */
    public ObjectList<WorkPeriod> getWorkPeriods() {
        /*if (isValuesComputed()) {
            ObjectList r = new ObjectList<WorkPeriod>();
            UserTaskObjectList st = getSubtasks();
            for (int i = 0; i < st.size(); i++) {
                r.addAll(st.getUserTask(i).getWorkPeriods());
            }
            Collections.sort(r);
            merge(r);
            return r;
        } else { TODO: future implementation */
            return workPeriods;
        /*}*/
    }

    /**
     * Returns resources associated with this task.
     * 
     * @return associated resources 
     */
    public ObjectList<UserTaskResource> getResources() {
        return resources;
    }
    
    /**
     * Returns the list where this task is contained.
     *
     * @return the list or null if this task is not in a UserTaskList
     */
    public UserTaskObjectList getParentObjectList() {
        if (getParent() != null)
            return getParent().getSubtasks();
        else if (getList() != null)
            return getList().getSubtasks();
        else
            return null;
    }
    
    /**
     * Returns the path to this task starting with the user task list.
     *
     * @return path to this task
     */
    public TreePath getPathTo() {
        List<Object> l = new ArrayList<Object>(10);
        UserTask t = this;
        while (t != null) {
            l.add(0, t);
            t = t.getParent();
        }
        l.add(0, getList());
        return new TreePath(l.toArray());
    }
    
    /**
     * Moves this task up in the parent's list
     */
    public void moveUp() {
    	UserTaskObjectList list;
    	if (getParent() == null)
            list = this.list.getSubtasks();
    	else
            list = this.parent.getSubtasks();
    	int index = list.indexOf(this);
        list.move(index, index - 1);
    }
    
    /**
     * Moves this task down in the parent's list
     */
    public void moveDown() {
    	UserTaskObjectList list;
    	if (getParent() == null)
            list = this.list.getSubtasks();
    	else
            list = this.parent.getSubtasks();
    	int index = list.indexOf(this);
        list.move(index, index + 1);
    }
    
    /**
     * Returns children of this task
     *
     * @return children of this task
     */
    public UserTaskObjectList getSubtasks() {
        return subtasks;
    }
    
    /**
     * Returns the list this task belongs to.
     *
     * @return task list or null if this task was removed from the list.
     */
    public UserTaskList getList() {
        return list;
    }
    
    /**
     * Sets the list this task belongs to.
     *
     * @param list user task list
     */
    void setList(UserTaskList list) {
        this.list = list;
        
        for (UserTask ch: this.getSubtasks()) {
            ch.setList(list);
        }
    }
    
    /**
     * Goes up the hierarchy of tasks and checks whether this task is an
     * ancestor of <code>another</code>. Returns true if 
     * <code>another == this</code>
     *
     * @param another the task that should be tested
     * @return true = ancestor
     */
    public boolean isAncestorOf(UserTask another) {
        while (another != null) {
            if (another == this)
                return true;
            another = another.getParent();
        }
        return false;
    }
    
    /**
     * Returns how long it took to complete this task.
     *
     * @return duration in minutes >= 0
     */
    public int getSpentTime() {
        return spentTime;
    }
    
    /**
     * Changes the duration of this task
     *
     * @param duration new value in minutes
     */
    public void setSpentTime(int spentTime) {
        assert spentTime >= 0;
        if (valuesComputed)
            setValuesComputed(false);
    
        setSpentTime_(spentTime);

        if (isValuesComputed() && getSubtasks().size() == 0)
            setProgress_(computeProgress());
    }
    
    /**
     * Setter for property spentTime. This method does not check the 
     * spentTimeComputed property and so it could be used from setSpentTimeComputed()
     *
     * @param spentTime New value of property spentTime in minutes.
     */
    private void setSpentTime_(int spentTime) {
        int old = this.spentTime;
        
        if (this.spentTime != spentTime) {
            this.spentTime = spentTime;
            firePropertyChange("spentTime",  // NOI18N
                new Integer(old), new Integer(spentTime));
            if (getParent() != null) {
                UserTask p = (UserTask) getParent();
                if (p.isValuesComputed())
                    p.setSpentTime_(p.computeSpentTime());
            }
        }
    }
    
    /**
     * Sets whether the spent time, progress and effort of this task 
     * should be computed
     *
     * @param v true = the spent time, progress and effort will be computed
     */
    public void setValuesComputed(boolean v) {
        if (this.valuesComputed != v) {
            if (isStarted())
                stop();
            this.valuesComputed = v;
            firePropertyChange(PROP_VALUES_COMPUTED, 
                    Boolean.valueOf(!v), Boolean.valueOf(v));
            if (v) {
                setSpentTime_(computeSpentTime());
                setProgress_(computeProgress());
                setEffort_(computeEffort());
                /* TODO: future implementation workPeriods.clear(); */
            }
        }
    }
    
    /**
     * Getter for property valuesComputed.
     *
     * @return true = the spent time, effort and progress 
     * will be computed as the sum of the subtask values
     */
    public boolean isValuesComputed() {
        return valuesComputed;
    }
    
    /**
     * Computes "spentTime" property as the sum of the subtask times.
     * This method should only be called if spentTimeComputed == true
     *
     * This method is used in tests that is why it's package private
     *
     * @return spent time in minutes
     */
    int computeSpentTime() {
        assert valuesComputed;

        int sum = 0;
        Iterator it = getSubtasks().iterator();        
        while (it.hasNext()) {
            UserTask child = (UserTask) it.next();
            sum += child.getSpentTime();
        }
        return sum;
    }

    /**
     * Start to work on this task
     */
    public void start() {
        StartedUserTask.getInstance().start(this);
    }
    
    /**
     * Stops this task
     */
    public void stop() {
        assert isStarted();
        StartedUserTask.getInstance().start(null);
    }
    
    /**
     * Is this task currently running?
     *
     * @return true = this is the currently running task
     */
    public boolean isStarted() {
        return StartedUserTask.getInstance().getStarted() == this;
    }
    
    /**
     * Test whether all tasks that this one depends on are done.
     *
     * @return all dependencies are done
     */
    public boolean areDependenciesDone() {
        List deps = this.getDependencies();
        for (int i = 0; i < deps.size(); i++) {
            Dependency d = (Dependency) deps.get(i);
            if (d.getType() == Dependency.END_BEGIN) {
                if (!d.getDependsOn().isDone())
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Checks whether this task could be started. This method also could
     * return true for a task that is currently running.
     *
     * @return true = this task could be started
     */
    public boolean isStartable() {
        return !isValuesComputed() && !isDone() && areDependenciesDone();
    }
    
    /**
     * Was the due alarm for this task already sent?
     * 
     * @return true = yes
     */
    public boolean isDueAlarmSent() {
        return dueAlarmSent;
    }
    
    /**
     * Sets the "due alarm sent" flag.
     * 
     * @param flag true = due alarm was already sent
     */
    public void setDueAlarmSent(boolean flag) {
        boolean old = this.dueAlarmSent;
        dueAlarmSent = flag;
        firePropertyChange("dueAlarmSent", Boolean.valueOf(old),  // NOI18N
            Boolean.valueOf(dueAlarmSent));
    }
    
    /**
     * Get the "Deadline" for this task
     *
     * @return the "deadline" or null
     */
    public Date getDueDate() {
        return dueDate;
    }
    
    /**
     * Set the "Deadline" for this task
     *
     * @param d the "deadline"
     */
    public void setDueDate(Date d) {      
        Date old = this.dueDate;
        if (d != null) {
            if (!d.equals(dueDate)) {
                dueAlarmSent = false;
            }
        } else {
            if (dueDate != null) {
                dueAlarmSent = false;
            }
        }
        dueDate = d;
        firePropertyChange("dueDate", old, dueDate); // NOI18N
    }
    
    /**
     * get the "deadline" for this task
     *
     * @return "deadline" for this task, Long.MAX_VALUE == no due time
     */
    public long getDueTime() {
        long ret = Long.MAX_VALUE;
        if (dueDate != null) {
            ret = dueDate.getTime(); // getNextDueDate();
        }
        return ret;
    }

    /** 
     * Check if this task is due. A task is considered
     * due if its due time has already passed, or will
     * occur in the next 36 hours (that will usually
     * be roughly "today or tomorrow")
     * <p>
     * @param when Date when we want to know if the date is due.
     *    You'll probably want to pass in a date corresponding
     *    to now.
     */
    boolean isDue(Date when) {
         if (dueDate == null) { 
             return false;
         }
         long due = dueDate.getTime();
         long now = when.getTime();
         return (due < (now + (36 * 60 * 60 * 1000)));
         // number of milliseconds in 36 hours:
         // 36 hours * 60 minutes * 60 * seconds * 1000 milliseconds
    }
    
    /**
     * The UID (Unique Identifier) for this item. See RFC 822 and RFC 2445.
     *
     * @return unique ID
     */
    public String getUID() {
        return uid;
    }
    
    public void setUID(String nuid) {
        String old = this.uid;
        uid = nuid;
        
        firePropertyChange("UID", old, this.uid); // NOI18N
    }
    
    /** 
     * Indicate if the task is done
     *
     * @return true iff percents complete equals 100
     */
    public boolean isDone() {
        return Math.abs(100.0f - getProgress()) < 1e-6;
    }

    /** 
     * Sets the percent complete to either 0 or 100.

     * @param done Whether or not the task is done.
     */    
    public void setDone(boolean done) {
        if (done)
            setPercentComplete(100);
        else
            setPercentComplete(0);
    }

    /**
     * Returns the percentage complete for this task. 
     *
     * @return 0..100 - value in percents
     */
    public int getPercentComplete() {
        int p = Math.round(progress);
        if (p == 100 && Math.abs(100.0f - progress) > 1e-6)
            return 99;
        else
            return p;
    }
    
    /**
     * Returns the percentage complete for this task. 
     *
     * @return 0..100 - value in percents
     */
    public float getProgress() {
        return progress;
    }
    
    /**
     * Computes the "expected" (based on the spent time) progress for this task.
     *
     * @return "expected" progress 0..100 - value in percents
     */
    public float getExpectedProgress() {
        return ((float) getSpentTime()) / getEffort() * 100.0f;
    }
    
    /**
     * Computes "percent complete" property as an average of the subtasks
     * This method should only be called if percentComputed == true
     *
     * @return computed percentage
     */
    private float computeProgress() {
        assert valuesComputed;
        
        if (getSubtasks().size() == 0) {
            if (isValuesComputed())
                return 100.0f;
            
            float p = (((float) getSpentTime()) / getEffort()) * 100.0f;
            if (p > 99.0)
                p = 99;
            return p;
        }
        
        Iterator it = getSubtasks().iterator();
        int sum = 0;
        int full = 0;
        while (it.hasNext()) {
            UserTask child = (UserTask) it.next();
            sum += child.getProgress() * child.getEffort();
            full += 100 * child.getEffort();
        }
        
        if (full == sum)
            return 100;
        
        float p = ((float) sum) / full * 100.0f;
        return p;
    }

    /** 
     * Sets the percentage complete for this task. Will also
     * update the done flag for this task. 
     *
     * @param percent 0..100 - value in percents 
     */
    public void setPercentComplete(int percent) {
        setProgress(percent);
    }
    
    /** 
     * Sets the percentage complete for this task. Will also
     * update the done flag for this task. 
     *
     * @param percent 0..100 - value in percents 
     */
    public void setProgress(float progress) {
        assert progress >= 0 && progress <= 100;
        if (valuesComputed)
            setValuesComputed(false);
        
        setProgress_(progress);
        
        if (isDone() && isStarted())
            stop();
        
        if (isDone())
            setCompletedDate(System.currentTimeMillis());
        else
            setCompletedDate(0);
    
        if (!isDone()) {
            UserTask[] t = findTasksThatDependOnThisOne();
            for (int i = 0; i < t.length; i++) {
                Dependency d = t[i].findDependencyOn(this);
                if (d.getType() == Dependency.END_BEGIN) {
                    t[i].setDone(false);
                    t[i].setSpentTime(0);
                    t[i].getWorkPeriods().clear();
                }
            }
        }
    }

    /** 
     * Sets the percentage complete for this task. This method does not 
     * check the percentComputed flag and could be used from 
     * setProgressComputed()
     *
     * @param percent 0..100 - value in percents 
     */
    private void setProgress_(float progress) {
        float old = this.progress;
        
        if (this.progress != progress) {
            this.progress = progress;
            firePropertyChange("progress",  // NOI18N
                new Float(old), new Float(progress));
            if (getParent() != null) {
                UserTask p = (UserTask) getParent();
                if (p.isValuesComputed())
                    p.setProgress_(p.computeProgress());
            }
        }
    }
    
    /** 
     * Return category of the task, or "" if no category
     * has been selected.
     *
     * @return Category name 
     */    
    public java.lang.String getCategory() {
	if (category == null) {
            return ""; // NOI18N
        }	
        return category;
    }    
    
    /** 
     * Set category associated with the task.
     *
     * @param category New category 
     */    
    public void setCategory(java.lang.String category) {
        String old = this.category;
        this.category = category;
        firePropertyChange("category", old, this.category); // NOI18N
    }
    
    /** 
     * Return the date when the item was created
     *
     * @return Date when the item was created 
     */
    public long getCreatedDate() {
        return created;
    }

    /** 
     * Set the date when the item was created 
     */
    public void setCreatedDate(long cr) {
        long old = this.created;
	created = cr;
        firePropertyChange("createdDate", new Long(old), new Long(this.created)); // NOI18N
    }

    /** 
     * Get the date when the item was last edited.
     * If the item has not been edited since it was created,
     * this returns the date of creation. Note also that
     * adding subtasks or removing subtasks is not considered
     * an edit of this item.
     *
     * @return the date when the item was last edited.
     */
    public long getLastEditedDate() {
	return lastEditedDate;
    }

    /** 
     * Set the date when the item was last edited 
     *
     * @param ed new date as returned by System.currentTimeMillis
     */
    public void setLastEditedDate(long ed) {
        lastEditedDate = ed;
    }
    
    public int hashCode() {
        return summary.hashCode() + details.hashCode() + priority;
    }
    
    /** 
     * Create an identical copy of a task (a deep copy, e.g. the
     * list of subtasks will be cloned as well 
     */
    public Object clone() {
        UserTask t = new UserTask("", null); // NOI18N
        t.copyFrom(this);
        return t;
    }

    /**
     * Returns dependencies of this task. No copy of the dependencies will
     * be made.
     *
     * @return dependencies. List<Dependency>
     */
    public ObjectList<Dependency> getDependencies() {
        return dependencies;
    }
    
    /**
     * Finds a dependency on another task.
     *
     * @param ut another task
     * @return found dependency of this task on <code>ut</code> or null
     */
    public Dependency findDependencyOn(UserTask ut) {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency d = (Dependency) dependencies.get(i);
            if (d.getDependsOn() == ut)
                return d;
        }
        return null;
    }
    
    /**
     * Searches in the whole task list for tasks that depend on this one.
     *
     * @return found tasks
     */
    public UserTask[] findTasksThatDependOnThisOne() {
        List<Object> t = UTUtils.filter(
                new UTListTreeAbstraction(getList()), new UnaryFunction() {
            public Object compute(Object obj) {
                if (obj instanceof UserTask) {
                    UserTask ut = (UserTask) obj;
                    Boolean b = Boolean.valueOf(
                            ut.findDependencyOn(UserTask.this) != null);
                    return b;
                } else {
                    return Boolean.FALSE;
                }
            }
        });
        return (UserTask[]) t.toArray(new UserTask[t.size()]);
    }
    
    /**
     * Copy all the fields in the given task into this object.
     *         Should only be called on an object of the EXACT same type.
     *         Thus, if you're implementing a subclass of Task, say
     *         UserTask, you can implement copy assuming that the passed
     *         in Task parameter is of type UserTask. When overriding,
     *         remember to call super.copyFrom.
     *         <p>
     *         Make a deep copy - except when that doesn't make sense.
     *         For example, you can share the same icon reference.
     *         And in particular, the tasklist reference should be the same.
     *         But the list of subitems should be unique. You get the idea.
     *
     * @param from another task
     */
    protected void copyFrom(UserTask from) {
        setSummary(from.getSummary());
        setPriority(from.getPriority());

        setDetails(from.getDetails());
        setDueDate(from.getDueDate());
        setDueAlarmSent(from.isDueAlarmSent());

        // Copying the parent reference may seem odd, since for children
        // it should be changed - but this only affects the root node.
        // For children nodes, we override the parent reference after
        // cloning the child.
        parent = from.parent;

        // Copy the subtasks reference

	// Please note -- I'm NOT copying the universal id, these have to
	// be unique, even for copies
        Iterator it = from.subtasks.iterator();
        subtasks = new UserTaskObjectList(this);
        while (it.hasNext()) {
            UserTask task = (UserTask)it.next();
            UserTask mycopy = (UserTask)task.clone();
            mycopy.list = list;
            mycopy.parent = this;
            subtasks.add(mycopy);
        }

        progress = from.progress;
        valuesComputed = from.valuesComputed;
        category = from.category;
        created = from.created;
        effort = from.effort;
        spentTime = from.spentTime;
        dependencies.clear();
        dependencies.addAll(from.dependencies);
        owner = from.owner;
        completedDate = from.completedDate;
        start = from.start;
        lastEditedDate = from.lastEditedDate;
        
        workPeriods.clear();
        for (int i = 0; i < from.workPeriods.size(); i++) {
            WorkPeriod wp = (WorkPeriod) from.workPeriods.get(i);
            try {
                workPeriods.add((WorkPeriod) wp.clone());
            } catch (CloneNotSupportedException e) {
                throw new InternalError("unexpected"); // NOI18N
            }
        }
        
        resources.clear();
        for (int i = 0; i < resources.size(); i++) {
            UserTaskResource r = resources.get(i);
            resources.add((UserTaskResource) r.clone());
        }
    }
    
    /**
     * Returns the remaining effort in minutes
     *
     * @return remaining effort in minutes >= 0
     */
    public int getRemainingEffort() {
        return Math.round(getEffort() * (1 - getProgress() / 100));
    }
    
    /**
     * Getter for property effort.
     *
     * @return effort in minutes > 0
     */
    public int getEffort() {
        return effort;
    }
    
    /**
     * Computes "effort" property as the sum of the subtask efforts.
     * This method should only be called if effortComputed == true
     *
     * This method is used in tests that is why it's package private
     *
     * @return effort in minutes
     */
    int computeEffort() {
        assert valuesComputed;
        
        Iterator it = getSubtasks().iterator();
        int sum = 0;
        while (it.hasNext()) {
            UserTask child = (UserTask) it.next();
            sum += child.getEffort();
        }
        return sum;
    }

    /**
     * Setter for property effort.
     *
     * @param effort New value of property effort in minutes.
     */
    public void setEffort(int effort) {
        assert effort >= 0;
        if (valuesComputed)
            setValuesComputed(false);
    
        setEffort_(effort);

        if (isValuesComputed() && getSubtasks().size() == 0)
            setProgress_(computeProgress());
    }

    /**
     * Setter for property effort. This method does not check the 
     * effortComputed property and so it could be used from setEffortComputed()
     *
     * @param effort New value of property effort in minutes.
     */
    private void setEffort_(int effort) {
        int old = this.effort;
        
        if (this.effort != effort) {
            int oldre = getRemainingEffort();
            this.effort = effort;
            firePropertyChange("effort",  // NOI18N
                new Integer(old), new Integer(effort));
            firePropertyChange("remainingEffort",
                    oldre, getRemainingEffort());
            if (getParent() != null) {
                UserTask p = (UserTask) getParent();
                if (p.isValuesComputed())
                    p.setEffort_(p.computeEffort());
                if (p.isValuesComputed())
                    p.setProgress_(p.computeProgress());
            }
        }
    }
    
    /** 
     * Generate a string summary of the task; only used
     * for debugging. DO NOT depend on this format for anything!
     * Use generate() instead.
     *
     * @return summary string 
     */
    public String toString() {
        return "UserTask[" + getSummary() + ", " + 
                getDetails() + "]"; // NOI18N
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (updateLastModified)
            setLastEditedDate(System.currentTimeMillis());
    }

    /**
     * Deletes all completed subtasks of this task (recursively)
     */
    public void purgeCompleted() {
        getSubtasks().purgeCompletedItems();
    }
    
    /**
     * Fires a PropertyChangeEvent. Also fires an event for PROP_DEEP_CHANGE
     *
     * @param propertyName changed property
     * @param oldValue old value (may be null)
     * @param newValue new value (may be null)
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        supp.firePropertyChange(propertyName, oldValue, newValue);
        if (list != null)
            list.fireChange();
    }
    
    /**
     * Searches for a task
     *
     * @param t task to be found
     * @return index of the task or -1
     */
    public int indexOf(UserTask t) {
        return subtasks.indexOf(t);
    }
    
    /**
     * Returns indent level for this task. If parent == null returns 0
     *
     * @return indent level for this task
     */
    public int getLevel() {
        UserTask t = getParent();
        int level = 0;
        while (t != null) {
            level++;
            t = t.getParent();
        }
        return level;
    }

    /**
     * Set the summary of the task. This is a one-line description
     * of the task. The summary should not be null.
     *
     * @param summary The summary of the task.
     */
    public void setSummary(final String summary) {
        if (summary == null) {
            throw new NullPointerException();
        }
        String old = getSummary();
        if (old.equals(summary)) return;
        this.summary = summary;
        firePropertyChange(PROP_SUMMARY, old, summary);
    }

    /**
     * Set the details of the task. This could be multiple lines
     * of description of the task. Can be null. 
     *
     * @param details The details of the task
     */
    public void setDetails(final String details) {
        String old = getDetails();
        if (old.equals(details)) return;
        this.details = details;
        firePropertyChange(PROP_DETAILS, old, details);
    }

    /**
     * Set the priority of the task. 
     * <p>
     *
     * @param priority The priority of the task.
     */
    public void setPriority(int priority) {
        assert priority == HIGH || priority == MEDIUM_HIGH ||
            priority == MEDIUM || priority == MEDIUM_LOW || priority == LOW;
        int old = getPriority();
        if (old == priority) return;
        this.priority = priority;
        firePropertyChange(PROP_PRIORITY, new Integer(old), new Integer(priority));
    }

    /**
     * Returns the parent of this task
     *
     * @return parent or null for top-level tasks
     */
    public final UserTask getParent() {
        return parent;
    }
    
    /**
     * Sets the parent attribute.
     *
     * @param parent new parent or null for top-level tasks
     */
    void setParent(UserTask parent) {
        this.parent = parent;
    }

    /**
     * Write a TodoItem to a text stream. NOT DONE.
     * @param item The task to write out
     * @param w The writer to write the string to
     * @throws IOException Not thrown explicitly by this code, but perhaps
     * by the call it makes to w's write() method
     *
     * @todo Finish the implementation here such that it
     * writes out all the fields, not just the
     * description.
     */
    public static void generate(UserTask item, Writer w) throws IOException {
	w.write(item.getSummary());
    }

    /**
     * Parse a task from a text stream.
     *
     * @param r The reader to read the task from
     * @throws IOException Not thrown directly by this method, but
     * possibly by r's read() method which it calls
     * @return A new task object which represents the
     * data read from the reader
     * @see generate
     */
    public static UserTask[] parse(Reader r) throws IOException {
        UserTaskList utl = new UserTaskList();
        BufferedReader reader = new BufferedReader(r);
        String line;
        List<UserTask> res = new ArrayList<UserTask>();
        while ((line = reader.readLine()) != null) {
            res.add(new UserTask(line, utl));
        }
        return res.toArray(new UserTask[res.size()]);
    }

    /**
     * Counts all subtasks of this task recursively.
     *
     * @return number of subtasks
     */
    public int getSubtaskCountRecursively() {
        int n = 0;
        Iterator it = subtasks.iterator();
        while(it.hasNext()) {
            UserTask t = (UserTask) it.next();
            n += t.getSubtaskCountRecursively() + 1;
        }
        return n;
    }

    /**
     * Clones task's properies without its
     * membership relations (parent).
     */
    public UserTask cloneTask() {
        UserTask clone = (UserTask) clone();
        clone.parent = null;
        return clone;
    }

    /**
     * Get the summary of the task.
     *
     * @return The summary of the task.
     */
    public String getSummary() {
        if (summary == null) {
            summary = ""; // NOI18N
        }
        return summary;
    }


    /**
     * Get the details of the task. Will never be null (but may
     * be an empty string.)
     * <p>
     *
     * @return The details of the task
     */
    public String getDetails() {
        if (details == null) {
            details = ""; // NOI18N
        }
        return details;
    }
    
    /**
     * Get the priority of the task.
     * <p>
     *
     * @return The priority of the task.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Listen to changes in bean properties. Changes will be fired in the 
     * same thread in which different methods of UserTasks were called.
     * 
     * @param l listener to be notified of changes
     */
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
        supp.addPropertyChangeListener(l);
    }

    /**
     * Stop listening to changes in bean properties.
     *
     * @param l listener who will no longer be notified of changes
     */
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }

    /**
     * Will be called from UserTaskList.destroy()
     */
    public void destroy() {
        Iterator it = getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            ut.destroy();
        }
    }
    
    /**
     * Creates an annotation for this object.
     *
     * @return created annotation
     */
    private UTAnnotation createAnnotation() {
        UTAnnotation ann = new UTAnnotation(this, false);
        return ann;
    }

    /**
     * Returns the owner (a person) of this task
     *
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets a new owner of this task
     *
     * @param owner a person
     */
    public void setOwner(String owner) {
        String old = this.owner;
        this.owner = owner;
        firePropertyChange("owner", old, this.owner); // NOI18N
    }

    /**
     * Returns the date when this task was completed.
     *
     * @return date when this task was completed or 0
     */
    public long getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the date when this task was completed.
     *
     * @param completed date when this task was completed or 0
     */
    public void setCompletedDate(long completed) {
        long old = this.completedDate;
        this.completedDate = completed;
        firePropertyChange("completedDate", new Long(old), new Long(completed)); // NOI18N
    }
    
    /**
     * Computes the time spent on this task today.
     *
     * @return time in minutes
     */
    public int getSpentTimeToday() {
        int sum = 0;
        if (isValuesComputed()) {
            for (int i = 0; i < getSubtasks().size(); i++) {
                UserTask ut = (UserTask) getSubtasks().get(i);
                sum += ut.getSpentTimeToday();
            }
        } else {
            for (int i = workPeriods.size() - 1; i >= 0; i--) {
                WorkPeriod wp = (WorkPeriod) workPeriods.get(i);
                if (wp.isToday())
                    sum += wp.getDuration();
                else
                    break;
            }
        }
        return sum;
    }

    /**
     * Returns the start time for this task.
     *
     * @return start time as returned by System.currentTimeMillis or -1 if
     * undefined
     */
    public long getStart() {
        return start;
    }

    /**
     * Returns the start time for this task.
     *
     * @return start time or null if undefined
     */
    public Date getStartDate() {
        if (start == -1)
            return null;
        else
            return new Date(start);
    }
    
    /**
     * Sets the start time for this task.
     *
     * @param start time as returned by System.currentTimeMillis or -1 if
     * undefined
     */
    public void setStart(long start) {
        long old = this.start;
        this.start = start;
        firePropertyChange("start", new Long(old), new Long(start)); // NOI18N
    }

    /**
     * Sets the start time for this task.
     *
     * @param start time or null if undefined
     */
    public void setStartDate(Date start) {
        if (start == null)
            setStart(-1);
        else
            setStart(start.getTime());
    }
    
    /**
     * Clears all work periods with duration = 0.
     */
    public void clearEmptyWorkPeriods() {
        Iterator it = workPeriods.iterator();
        while (it.hasNext()) {
            WorkPeriod wp = (WorkPeriod) it.next();
            if (wp.getDuration() == 0)
                it.remove();
        }
    }

    /**
     * Sets whether the "last modified" date should be updated.
     *
     * @param b true = last modified date will be updated
     */
    public void setUpdateLastModified(boolean b) {
        this.updateLastModified = b;
    }
}
