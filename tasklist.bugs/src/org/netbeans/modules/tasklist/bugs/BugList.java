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

import java.util.List;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.openide.util.NbBundle;





/**
 * This class represents the tasklist itself
 * @author Tor Norbye
 */
public class BugList extends TaskList { // XXX remove the publicness

    // List category
    final static String USER_CATEGORY = "bugs"; // NOI18N


    BugEngine[] engines = null;
    
    
    /** Creates a new instance of TaskList */
    public BugList() {
	IZBugEngine issuezilla = new IZBugEngine(this);
	// Later, allow these puppies to be registered via Lookup
	engines = new BugEngine[] { issuezilla };
    }

    private static BugList tasklist = null;

    public static BugList getDefault() {
        if (tasklist == null) {
            tasklist = new BugList();

            // First time - try to fetch the contents from the web
            // tasklist.refresh();
        }
        return tasklist;
    }

    

    
    /** Returns the root of the tree. When called on the BugList,
        it always returns a Bug, even though the return type
        is the parent class, Task. */
    public Task getRoot() {
        if (root == null) {
            // Just use the name "Description" since for some reason,
            // when we have no items the TreeView puts the root node
            // description as the header for the leftmost column...
            root = new Bug(NbBundle.getMessage(BugList.class, 
                                               "Summary"), "", 0, // NOI18N
			   "", "", "", null, "", "", "", "", "", 0); // NOI18N
	    root.setList(this);
	}
        return root;
    }

    /** Write todo items out to disk */
    public void save() {
    }

    public void refresh() {
        // Do in the background
	for (int i = 0; i < engines.length; i++) {
	    engines[i].refresh();
	}
    }

    /** Update the contents to show the given list */
    public void setBugs(List issues) {
        Bug parent = (Bug)getRoot();
        parent.dropSubtasks();

        addRemove(issues, null, false, null, null);
    }

    /** View a particular bug. */
    public void viewBug(Bug bug) {
        // Do in the background
	BugEngine engine = bug.getEngine();
	engine.viewBug(bug);
    }

    BugEngine getDefaultEngine() {
        return engines[0];
    }
}
