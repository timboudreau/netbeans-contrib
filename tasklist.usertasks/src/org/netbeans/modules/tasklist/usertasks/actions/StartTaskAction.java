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
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.util.ObjectListEvent;
import org.netbeans.modules.tasklist.core.util.ObjectListListener;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Starts a task
 *
 * @author tl
 */
public class StartTaskAction extends UTViewAction implements 
        PropertyChangeListener, ObjectListListener {
    private UserTask ut;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public StartTaskAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(StartTaskAction.class, 
                "StartTask")); // NOI18N
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/actions/" + // NOI18N
                "startTask.gif"))); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        if (StartedUserTask.getInstance().getStarted() != null)
            StartedUserTask.getInstance().start(null);
        ut.start();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (ut != null) {
            ut.removePropertyChangeListener(this);
            ut.getDependencies().removeListener(this);
            ut = null;
        }
        
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        if (paths.length == 1) {
            Object last = paths[0].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                UserTask ut = ((UserTaskTreeTableNode) last).getUserTask();
                this.ut = ut;
                this.ut.addPropertyChangeListener(this);
                this.ut.getDependencies().addListener(this);
            }
        }
        updateEnabled();
    }

    private void updateEnabled() {
        setEnabled(ut != null && ut.isStartable() && !ut.isStarted());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "started" || 
                evt.getPropertyName() == "spentTimeComputed" ||
                evt.getPropertyName() == "progress") {
            setEnabled(ut.isStartable() && !ut.isStarted());
        }
    }

    public void listChanged(ObjectListEvent e) {
        updateEnabled();
    }
}
