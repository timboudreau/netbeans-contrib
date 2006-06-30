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

package org.netbeans.modules.tasklist.bugs;

import java.util.List;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.bugs.bugzilla.BZBugEngine;
import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.openide.util.NbBundle;





/**
 * This class represents the tasklist itself
 * @author Tor Norbye
 */
public class BugList extends TaskList { // XXX remove the publicness
    /** The associated query with this list */
    private BugQuery mQuery;
    
    // List category
    final static String USER_CATEGORY = "bugs"; // NOI18N

    /** Creates a new instance of TaskList */
    public BugList(BugQuery inQuery) {
        mQuery = inQuery;
    }

    private static BugList tasklist = null;

    public static BugList getDefault(BugQuery inQuery) {
//        if (tasklist == null) {
            tasklist = new BugList(inQuery);

            // First time - try to fetch the contents from the web
            tasklist.refresh();
//        }
        return tasklist;
    }

    
    /** Write todo items out to disk */
    public void save() {
    }

    public void refresh() {
        if (mQuery != null) {
            BugEngine engine = BugEngines.get(mQuery.getBugEngine());
            engine.refresh(mQuery, this);
        }
    }

    /** Update the contents to show the given list */
    public void setBugs(List issues) {
        addRemove(issues, getTasks(), false, null, null);
    }
    
//    public void addBug(List issue) {
//        Bug parent = (Bug)getRoot();
//        parent.dropSubtasks();
//
//        addRemove(issue, null, true, null, null);
//    }

    /** View a particular bug. */
    public void viewBug(Bug bug) {
        // Do in the background
	    BugEngine engine = bug.getEngine();
	    engine.viewBug(bug, mQuery.getBaseUrl());
    }

}
