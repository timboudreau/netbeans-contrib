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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
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
        PropertyChangeListener {
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
            ut = null;
        }
        
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        boolean en = false;
        if (paths.length == 1) {
            Object last = paths[0].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                UserTask ut = ((UserTaskTreeTableNode) last).getUserTask();
                // TODO: ut.getDependencies().addListener()...
                en = ut.isStartable() && !ut.isStarted();
                if (en) {
                    this.ut = ut;
                    this.ut.addPropertyChangeListener(this);
                }
            }
        }
        setEnabled(en);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "started" || 
                evt.getPropertyName() == "spentTimeComputed" ||
                evt.getPropertyName() == "progress") {
            setEnabled(ut.isStartable() && !ut.isStarted());
        }
    }
}
