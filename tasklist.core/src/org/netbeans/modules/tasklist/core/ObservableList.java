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

/**
 * Readonly live tasklist interface.
 *
 * @author Petr Kuzel
 */
public interface ObservableList {

    /**
     * Return top level task.
     *
     * XXX based on current impl where
     * root task is a client provided
     * top level tasks holder.
     *
     * Proper impl shoudl be that toplevel
     * holder is impl issue and clients
     * use mutation methods using writeable
     * tasklist interface
     */
    Task getRoot();

    /**
     * The listener is notifiead about list modifications
     */
    void addListener(TaskListener l);

    /**
     * The listener is detached
     */
    void removeListener(TaskListener l);

    /**
     * Dispatch structureChanged event.
     */
    void notifyStructureChanged(Task task);
}
