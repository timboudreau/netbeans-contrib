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

import javax.swing.*;


/** Interface which represents a bug engine which can be queried.
 *  @author Tor Norbye
 *  @todo Add support for multiple queries (that the user can create),
 *        caching support, etc.
 */
public interface BugEngine {
    /** Fetch the buglist, then call BugList.setList() with the
	results (preferably asynchronously) */
    void refresh(BugQuery query, BugList list);

    /** View a particular bug. */
    void viewBug(Bug bug, String serverURL);

    /** Return the user name of the engine */
    String getName();

    /** Return a query customizer */
    JComponent getQueryCustomizer(BugQuery query, boolean edit);

}
