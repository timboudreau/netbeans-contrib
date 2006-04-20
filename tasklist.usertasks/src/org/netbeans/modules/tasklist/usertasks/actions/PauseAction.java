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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Stops a task.
 *
 * @author tl
 */
public class PauseAction extends AbstractAction implements ChangeListener {
    private static PauseAction INSTANCE = new PauseAction();
    
    /**
     * Returns the only instance of this class.
     *
     * @return the instance.
     */
    public static PauseAction getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    private PauseAction() {
        putValue(Action.NAME, 
                NbBundle.getMessage(PauseAction.class, "Pause")); // NOI18N
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/" + // NOI18N
                "actions/pause.gif"))); // NOI18N
        StartedUserTask.getInstance().addChangeListener(this);
        stateChanged(null);
    }
    
    public void stateChanged(ChangeEvent e) {
        setEnabled(StartedUserTask.getInstance().getStarted() != null);
    }

    public void actionPerformed(ActionEvent e) {
        StartedUserTask.getInstance().start(null);
    }
}
