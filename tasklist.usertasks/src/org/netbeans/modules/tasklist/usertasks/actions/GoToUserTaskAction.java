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
package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskResource;
import org.openide.awt.HtmlBrowser;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/**
 * Go to the source code / associated file for a particular
 * task.
 *
 * @author Tor Norbye
 * @author tl
 */
public class GoToUserTaskAction extends UTViewAction 
        implements PropertyChangeListener {
    private UserTask last;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public GoToUserTaskAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(GoToUserTaskAction.class, 
                "LBL_Goto")); // NOI18N
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/actions/" + // NOI18N
                "gotosource.png"))); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        UserTask ut = getSingleSelectedTask();
        for (UserTaskResource r: ut.getResources()) {
            r.open();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (last != null)
            last.removePropertyChangeListener(this);
        UserTask ut = getSingleSelectedTask();
        setEnabled(ut != null && ut.getResources().size() > 0);
        last = ut;
        if (last != null)
            last.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == UserTask.PROP_RESOURCES) {
            UserTask ut = getSingleSelectedTask();
            setEnabled(ut != null && ut.getResources().size() > 0);
        }
    }
}
