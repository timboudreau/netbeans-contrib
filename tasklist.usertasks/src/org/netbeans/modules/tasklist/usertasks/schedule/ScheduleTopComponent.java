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
