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

package org.netbeans.modules.tasklist.usertasks.model;

/**
 * Dependency for a task
 *
 * @author tl
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
    
    /**
     * Constructor.
     *
     * @param dependsOn the task we depend on.
     * @type one of END_BEGIN and BEGIN_BEGIN
     */
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
     * @return one of END_BEGIN and BEGIN_BEGIN
     */
    public int getType() {
        return type;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
