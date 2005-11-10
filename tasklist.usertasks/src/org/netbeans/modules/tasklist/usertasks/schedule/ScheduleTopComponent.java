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

package org.netbeans.modules.tasklist.usertasks.schedule;

import java.awt.BorderLayout;
import org.openide.windows.TopComponent;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;


/**
 * Scheduling view
 */
public class ScheduleTopComponent extends TopComponent {
    private ScheduleView view;
    
    /** 
     * Creates a new instance of ScheduleTopComponent 
     *
     * @param title title for the view
     * @param utl user task list that should be shown
     */
    public ScheduleTopComponent(String title, UserTaskList utl) {
        setName(title); // NOI18N
        setLayout(new BorderLayout());
        view = new ScheduleView();
        add(view, BorderLayout.CENTER);
        
        view.setUserTaskList(utl);
    }

    protected String preferredID() {
        return "org.netbeans.modules.tasklist.usertasks.Schedule"; // NOI18N
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
}
