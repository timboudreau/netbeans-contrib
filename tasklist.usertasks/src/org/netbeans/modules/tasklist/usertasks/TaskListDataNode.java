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


package org.netbeans.modules.tasklist.usertasks;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/** A node to represent this object.
 *
 * @author Tor Norbye
 */
public class TaskListDataNode extends DataNode {

    public TaskListDataNode(TaskListDataObject obj) {
	super(obj, Children.LEAF);
        setIconBase(icon);
    }

    private static final String icon =
        "org/netbeans/modules/tasklist/usertasks/tasklistfile";  // NOI18N
}
