/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import java.awt.Image;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;

import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The node of command in the runtime tab.
 * The command can be in the three states: waiting (for user data or for other commands),
 * running and done.
 *
 * @author  Martin Entlicher
 */
public class RuntimeCommandNode extends AbstractNode {

    static final int STATE_WAITING = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_DONE = 2;
    static final int STATE_CANCELLED = 3;
    
    private static final int BADGE_ICON_SHIFT_X = 16;
    private static final int BADGE_ICON_SHIFT_Y = 8;

    private static Image icon = null;
    private static Image badgeRunning = null;
    private static Image badgeWaiting = null;
    private static Image badgeError = null;

    private RuntimeCommand command;
    private int state;
    
    
    /** Creates new RuntimeCommandNode */
    RuntimeCommandNode(RuntimeCommand comm) {
        super(Children.LEAF);
        command = comm;
        setName(command.getName());
        String displayName = command.getDisplayName();
        if (displayName == null || displayName.length() == 0) displayName = command.getName();
        setDisplayName(displayName);
        setDefaultAction(CommandOutputViewAction.getInstance());
    }
    
    void setState(int state) {
        this.state = state;
        fireIconChange();
        firePropertyChange("status", null, null);
    }
    
    public RuntimeCommand getRuntimeCommand() {
        return command;
    }
    
    public Image getIcon(int type) {
        if (icon == null) {
            icon = new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vcscore/runtime/commandIcon.gif")).getImage();
        }
        Image badge = null;
        switch (state) {
            case STATE_RUNNING:
                if (badgeRunning == null) {
                    badgeRunning = new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vcscore/runtime/badgeRunning.gif")).getImage();
                }
                badge = badgeRunning;
                break;
            case STATE_WAITING:
                if (badgeWaiting == null) {
                    badgeWaiting = new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vcscore/runtime/badgeWaiting.gif")).getImage();
                }
                badge = badgeWaiting;
                break;
            case STATE_CANCELLED:
            case STATE_DONE:
                if (command.getExitStatus() != RuntimeCommand.SUCCEEDED) {
                    if (badgeError == null) {
                        badgeError = new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vcscore/runtime/badgeError.gif")).getImage();
                    }
                    badge = badgeError;
                }
                break;
        }
        Image badgedIcon;
        if (badge != null) {
            badgedIcon = org.openide.util.Utilities.mergeImages(icon, badge, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
        } else {
            badgedIcon = icon;
        }
        return badgedIcon;
    }
    
    public SystemAction[] getActions() {
        return command.getActions();

    }

    public Sheet createSheet() {
        return command.createSheet();
    }
    


    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeCommandNode.class).getString(name);
    }

}
