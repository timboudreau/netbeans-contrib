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

package org.netbeans.modules.tasklist.docscan;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.client.Suggestion;


/**
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

}
