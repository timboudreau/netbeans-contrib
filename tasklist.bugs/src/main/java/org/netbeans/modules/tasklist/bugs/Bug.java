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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.tasklist.bugs;

import java.util.Date;

import org.netbeans.modules.tasklist.core.Task;
import org.openide.nodes.Node;


// XXX todo: fire property change whenever anything changes in the node...

/**
 * Class which represents a task in the
 * tasklist.
 *
 * @author Tor Norbye
 */
public final class Bug extends Task {
    private BugEngine engine = null;

    // IMPORTANT: If you add additional fields, update copyFrom() as well
    private String id = "";
    private String synopsis = "";
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
               int votes) {
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

        setSummary(id + ": " + synopsis); // NOI18N
    }

    /**
     * Return the priority of the task.
     *
     * @return The priority of the task. "0" is considered
     *         "not prioritized". Lower number is considered
     *         a higher priority, by convention. The default
     *         priority of tasks is "3".
     */
    public int getPriorityNumber() {
        return priority;
    }

    public void setPriorityNumber(int priority) {
        this.priority = priority;
    }

    /**
     * Return the bug number
     *
     * @return The bug number
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * Return the description for the bug
     *
     * @return Bug description.
     */
    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Return the bug's component/category
     */
    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }


    /**
     * Return the bug's subcomponent/subcategory
     */
    public String getSubComponent() {
        return subcomponent;
    }

    public void setSubComponent(String subcomponent) {
        this.subcomponent = subcomponent;
    }


    /**
     * Return the bug's creation date
     */
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    /**
     * Return keywords associated with the bug
     */
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Return the name of the person assigned to the bug
     */
    public String getAssignedTo() {
        return assignedto;
    }

    public void setAssignedTo(String assignedto) {
        this.assignedto = assignedto;
    }

    /**
     * Return the name of the person who filed the bug
     */
    public String getReportedBy() {
        return reportedby;
    }

    public void setReportedBy(String reportedby) {
        this.reportedby = reportedby;
    }

    /**
     * Return the current status of the bug
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return the target milestone for the bug
     */
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Return the type of bug: enhancement, bug, ...
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return the number of votes for the bug
     */
    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    // TODO: Issuezilla also provides: getResolution, getDescriptions,
    // getObservedBy, getBlocks, getDependsOn -- do I care about these
    
    /**
     * Generate a string summary of the task; only used
     * for debugging. DO NOT depend on this format for anything!
     * Use generate() instead.
     *
     * @return summary string
     */
    public String toString() {
        return "Bug[\"" + id + "\", " + synopsis + ":" + priority + "]"; // NOI18N
    }

    /**
     * Create a node for this item
     */
    public Node[] createNode() {
        // PENDING Do I allow subnodes for bugs? IssueZilla depends on
        // seems like something you could consider a "subtask", although
        // not quite
        // A: Exactly, I would use it for IZ depends mapping, in reality
        // it can cause identity problems as the same IZ task is
        // nodeled by multiple TL tasks
        if (hasSubtasks()) {
            return new Node[]{ new BugNode(this, new BugNode.BugChildren(this))};
        } else {
            return new Node[]{ new BugNode(this)};
        }
    }

    /**
     * Create an identical copy of a task (a deep copy, e.g. the
     * list of subtasks will be cloned as well
     */
    protected Object clone() {
        Bug t = new Bug();
        t.copyFrom(this);
        return t;
    }

    /**
     * Copy all the fields in the given task into this object.
     * Should only be called on an object of the EXACT same type.
     * Thus, if you're implementing a subclass of Task, say
     * UserTask, you can implement copy assuming that the passed
     * in Task parameter is of type UserTask. When overriding,
     * remember to call super.copyFrom.
     * <p/>
     * Make a deep copy - except when that doesn't make sense.
     * For example, you can share the same icon reference.
     * And in particular, the tasklist reference should be the same.
     * But the list of subitems should be unique. You get the idea.
     */
    protected void copyFrom(Bug from) {
        super.copyFrom(from);

        engine = from.engine;

        id = from.id;
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

    /**
     * View the particular bug in current view
     */
    void view() {
        BugList list = (BugList) BugsView.getCurrent().getList();
        list.viewBug(this);
    }

    /**
     * Return the bug engine associated with this bug
     */
    public void setEngine(BugEngine engine) {
        this.engine = engine;
    }

    /**
     * Set the bug engine associated with this bug
     */
    public BugEngine getEngine() {
        return engine;
    }
}



