/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.bugs;

import java.util.Date;
import org.netbeans.modules.tasklist.core.Task;
import org.openide.nodes.Node;


// XXX todo: fire property change whenever anything changes in the node...

/** Class which represents a task in the
 * tasklist.
 * @author Tor Norbye */
public final class Bug extends Task {
    private BugEngine engine = null;

    // IMPORTANT: If you add additional fields, update copyFrom() as well
    private String id = "";
    private String synopsis = "";
    private String summary = "";
    private int priority;
    private String type = "";
    private String component = "";
    private String subcomponent = "";
    private Date created = null;
    private String keywords = "";
    private String assignedto = "";
    private String reportedby = "";
    private String status = "";
    private String target = "";
    private int votes = 0;
    // IMPORTANT: If you add additional fields, update copyFrom() as well


    public Bug() {
    }

    public Bug(String id,
	       String synopsis,
	       int priority,
	       String type,
	       String component,
	       String subcomponent,
	       Date created,
	       String keywords,
	       String assignedto,
	       String reportedby,
	       String status,
	       String target,
	       int votes
	       ) {
        super(id, null);
        this.id = id;
        this.synopsis = synopsis;
        this.priority = priority;
	this.type = type;
	this.component = component;
	this.subcomponent = subcomponent;
	this.created = created;
	this.keywords = keywords;
	this.assignedto = assignedto;
	this.reportedby = reportedby;
	this.status = status;
	this.target = target;
	this.votes = votes;
    }

    /** Return the priority of the task.
     * @return The priority of the task. "0" is considered
     * "not prioritized". Lower number is considered
     * a higher priority, by convention. The default
     * priority of tasks is "3". */    
    public int getPriorityNumber() {
        return priority;
    }

    public void setPriorityNumber(int priority) {
        this.priority = priority;
    }
    
    /** Return the bug number
     * @return The bug number
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    /** Return the description for the bug
     * @return Bug description.
     */
    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /** Get the "summary" for the bug. This is just the
     * combination of the id and the synopsis.
     */
    public String getIdAndSynopsis() {
        if ((summary == null) || (summary == "")) {
            summary = id + ": " + synopsis; // NOI18N
        }
        return summary;
    }

    // No setter - not really an attribute on its own; it's derived
    // from id and synopsis
    
    
    /** Return the bug's component/category */
    public String getComponent() {
	return component;
    }
    public void setComponent(String component) {
	this.component = component;
    }

    
    /** Return the bug's subcomponent/subcategory */
    public String getSubComponent() {
	return subcomponent;
    }
    public void setSubComponent(String subcomponent) {
	this.subcomponent = subcomponent;
    }

    
    /** Return the bug's creation date */
    public Date getCreated() {
	return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }

    
    /** Return keywords associated with the bug */
    public String getKeywords() {
	return keywords;
    }
    public void setKeywords(String keywords) {
	this.keywords = keywords;
    }
    
    /** Return the name of the person assigned to the bug */
    public String getAssignedTo() {
	return assignedto;
    }
    public void setAssignedTo(String assignedto) {
	this.assignedto = assignedto;
    }

    /** Return the name of the person who filed the bug */
    public String getReportedBy() {
	return reportedby;
    }
    public void setReportedBy(String reportedby) {
	this.reportedby = reportedby;
    }
    
    /** Return the current status of the bug */
    public String getStatus() {
	return status;
    }
    public void setStatus(String status) {
	this.status = status;
    }
    
    /** Return the target milestone for the bug */
    public String getTarget() {
	return target;
    }
    public void setTarget(String target) {
	this.target = target;
    }
    
    /** Return the type of bug: enhancement, bug, ... */
    public String getType() {
	return type;
    }
    public void setType(String type) {
	this.type = type;
    }
    
    /** Return the number of votes for the bug */
    public int getVotes() {
	return votes;
    }
    public void setVotes(int votes) {
	this.votes = votes;
    }

    // TODO: Issuezilla also provides: getResolution, getDescriptions,
    // getObservedBy, getBlocks, getDependsOn -- do I care about these
    
    /** Generate a string summary of the task; only used
     * for debugging. DO NOT depend on this format for anything!
     * Use generate() instead.
     * @return summary string */    
    public String toString() {
        return "Bug[\"" + id + "\", " + synopsis + ":" + priority + "]"; // NOI18N
    }

    /** Create a node for this item */
    protected Node[] createNode() {
        // PENDING Do I allow subnodes for bugs? IssueZilla depends on
        // seems like something you could consider a "subtask", although
        // not quite
        if (hasSubtasks()) {
            return new Node[] { new BugNode(this, getSubtasks())};
        } else {
            return new Node[] { new BugNode(this)};
        }
    }

    /** Create an identical copy of a task (a deep copy, e.g. the
        list of subtasks will be cloned as well */
    protected Object clone() throws CloneNotSupportedException {
        Bug t = new Bug();
        t.copyFrom(this);
        return t;
    }

    /** Copy all the fields in the given task into this object.
        Should only be called on an object of the EXACT same type.
        Thus, if you're implementing a subclass of Task, say
        UserTask, you can implement copy assuming that the passed
        in Task parameter is of type UserTask. When overriding,
        remember to call super.copyFrom.
        <p>
        Make a deep copy - except when that doesn't make sense.
        For example, you can share the same icon reference.
        And in particular, the tasklist reference should be the same.
        But the list of subitems should be unique. You get the idea.
    */
    protected void copyFrom(Bug from) {
        super.copyFrom(from);

	engine = from.engine;

        id = from.id;
        summary = from.summary;
        synopsis = from.synopsis;
        priority = from.priority;
	type = from.type;
	component = from.component;
	subcomponent = from.subcomponent;
	created = from.created;
	keywords = from.keywords;
	assignedto = from.assignedto;
	reportedby = from.reportedby;
	status = from.status;
	target = from.target;
	votes = from.votes;
    }

    /** Return the display name of the task, which is identical
     * to the summary.
     * @return The description
     * @todo Decide if this method is necessary/used or not. */    
    public String getDisplayName() {
        return getIdAndSynopsis();
    }

    /** Get rid of the children/subtasks associated with this task */
    void dropSubtasks() {
        subtasks = null;
    }

    /** View the particular bug */
    void view() {
	((BugList)list).viewBug(this);
    }

    /** Return the bug engine associated with this bug */
    public void setEngine(BugEngine engine) {
	this.engine = engine;
    }

    /** Set the bug engine associated with this bug */
    public BugEngine getEngine() {
	return engine;
    }
}



