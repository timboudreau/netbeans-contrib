/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import java.util.List;

/**
 * Readonly live tasklist interface.
 *
 * @author Petr Kuzel
 */
public interface ObservableList {
    
    /**
     * Access top level tasks in the list.
     *
     * @return List&lt;Task> never <code>null</code>
     */
    List getTasks();

    /**
     * The listener is notifiead about list modifications
     */
    void addTaskListener(TaskListener l);

    /**
     * The listener is detached
     */
    void removeTaskListener(TaskListener l);

}
