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

package org.netbeans.modules.tasklist.usertasks.model;

import org.netbeans.modules.tasklist.usertasks.*;


/**
 * Dependency for a task
 */
public class Dependency implements Cloneable {
    /**
     * If task A depends on task B with this type of dependency it 
     * means that B should be completed before A could be started.
     */
    public static final int END_BEGIN = 0;
    
    /**
     * If task A depends on task B with this type of dependency it 
     * means that B should be started simultaneosely with A.
     */
    public static final int BEGIN_BEGIN = 1;

    /** one of END_BEGIN and BEGIN_BEGIN */
    private int type = END_BEGIN;
    
    private UserTask dependsOn;
    
    public Dependency(UserTask dependsOn, int type) {
        this.type = type;
        this.dependsOn = dependsOn;
    }
    
    /**
     * Returns the task we depend on.
     *
     * @return dependency
     */
    public UserTask getDependsOn() {
        return dependsOn;
    }
    
    /**
     * Returns type of this dependency
     *
     * @return one of END_BEGINN and BEGIN_BEGIN
     */
    public int getType() {
        return type;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
