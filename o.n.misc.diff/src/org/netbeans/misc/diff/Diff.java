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
/*
 * Diff.java
 *
 * Created on September 18, 2004, 6:33 PM
 */

package org.netbeans.misc.diff;

import java.util.*;

/**
 * Represents the difference between two lists.  The <code>getChanges()</code> method returns a list of
 * <code>Change</code> objects, which, if applied to the old list in the order they are returned, will result in the new
 * list. Example code for such a transformation:
 * <pre>
 * List list = new ArrayList (diff.getOld());
 * List target = diff.getNew();
 * List changes = diff.getChanges();
 * for (Iterator iter=changes.iterator(); iter.hasNext();) {
 * Change change = (Change) iter.next();
 * int start = change.getStart();
 * int end = change.getEnd();
 * switch (change.getType()) {
 * case Change.CHANGE :
 * for (int i=start; i <= end; i++) {
 * list.set (i, target.get(i));
 * }
 * break;
 * case Change.INSERT :
 * int ct = 0;
 * for (int i=end; i >= start; i--) {
 * Object o = target.get(i);
 * list.add(start, o);
 * }
 * break;
 * case Change.DELETE :
 * for (int i=end; i >= start; i--) {
 * list.remove(i);
 * }
 * break;
 * }
 * }
 * </pre>
 * Primarily this is used for tranlating ListModelEvents into TreeModelEvents, where all the details of the data which
 * was changed are required.
 *
 * @author Tim Boudreau
 * @see Change
 * @see ListDiff#createDiff
 * @see ListDiff#createFixed
 */
public interface Diff {
    /**
     * Get a list of Change objects in the order they need to be applied to construct the result of
     * <code>getNew()</code> by applying these changes to <code>getOld()</code>.
     */
    public List getChanges ();

    /**
     * Get the former contents.
     */
    public List getOld ();

    /**
     * Get the current contents.
     */
    public List getNew ();
}
