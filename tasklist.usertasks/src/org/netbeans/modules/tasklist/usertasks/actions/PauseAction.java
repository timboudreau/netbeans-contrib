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
