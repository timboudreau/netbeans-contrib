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


/** Interface which represents a bug engine which can be queried.
 *  @author Tor Norbye
 *  @todo Add support for multiple queries (that the user can create),
 *        caching support, etc.
 */
public interface BugEngine {
    /** Fetch the buglist, then call BugList.setList() with the
	results (preferably asynchronously) */
    void refresh();

    /** View a particular bug. */
    void viewBug(Bug bug);

    /** Return the user name of the engine */
    String getName();

    /** Return a query customizer */
    
}
