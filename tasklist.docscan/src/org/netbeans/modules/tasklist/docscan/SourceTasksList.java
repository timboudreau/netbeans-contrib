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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.api.tasklist.Suggestion;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Lists suggestions for current project or source file.
 * Filters out inproper suggestions not targeted to this list.
 *
 * @author Petr Kuzel
 */
final class SourceTasksList extends SuggestionList {

    SourceTasksList() {
        super(Integer.MAX_VALUE);
    }

    public void addRemove(List addList, List removeList, boolean append,
                          Task parent, Task after) {

        List filtered = new ArrayList(addList);
        Iterator it = filtered.iterator();
        while (it.hasNext()) {
            Suggestion next = (Suggestion) it.next();
            if (next.getSeed() instanceof SourceTaskProvider) {
                continue;
            } else {
                it.remove();
            }
        }

        super.addRemove(filtered, removeList, append, parent, after);
    }

    public void add(Task task) {
        if (task.getSeed() instanceof SourceTaskProvider) {
            super.add(task);
        }
    }

    public void add(Task task, boolean append, boolean show) {
        if (task.getSeed() instanceof SourceTaskProvider) {
            super.add(task, append, show);
        }
    }

    public void add(Task task, Task after, boolean show) {
        if (task.getSeed() instanceof SourceTaskProvider) {
            super.add(task, after, show);
        }
    }
}
