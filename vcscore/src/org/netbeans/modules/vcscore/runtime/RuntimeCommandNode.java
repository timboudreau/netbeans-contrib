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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Image;

import org.openide.actions.PropertiesAction;
import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;

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
public class RuntimeCommandNode extends AbstractNode implements PropertyChangeListener {

    static final int STATE_WAITING = 10;
    static final int STATE_RUNNING = 11;
    static final int STATE_DONE = 12;
    static final int STATE_CANCELLED = 13;
    static final int STATE_KILLED_BUT_RUNNING = 14;
    
    private static final int BADGE_ICON_SHIFT_X = 16;
    private static final int BADGE_ICON_SHIFT_Y = 8;

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
        setShortDescription(NbBundle.getMessage(RuntimeCommandNode.class, "RuntimeCommandNode.Description", displayName));
        comm.addPropertyChangeListener(WeakListener.propertyChange(this, comm));
        setState(comm.getState());
        setDefaultAction(CommandOutputViewAction.getInstance());
        getCookieSet().add(comm);
    }
    
    public void setState(int state) {
        this.state = state;
        fireIconChange();
        firePropertyChange("status", null, null);
    }
    
    /* This method should not be used. Get RuntimeCommand.class cookie instead.
    public RuntimeCommand getRuntimeCommand() {
        return command;
    }
     */
    
    public Image getIcon(int type) {
        Image icon = Utilities.loadImage("org/netbeans/modules/vcscore/runtime/commandIcon.gif");
        Image badge = null;
        switch (state) {
            case STATE_RUNNING:
            case STATE_KILLED_BUT_RUNNING:
                badge = Utilities.loadImage("org/netbeans/modules/vcscore/runtime/badgeRunning.gif");
                break;
            case STATE_WAITING:
                badge = Utilities.loadImage("org/netbeans/modules/vcscore/runtime/badgeWaiting.gif");
                break;
            case STATE_CANCELLED:
            case STATE_DONE:
                if (command.getExitStatus() != RuntimeCommand.SUCCEEDED) {
                    badge = Utilities.loadImage("org/netbeans/modules/vcscore/runtime/badgeError.gif");
                }
                break;
        }
        return (badge == null) ? icon : Utilities.mergeImages(icon, badge, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
    }
    
    public SystemAction[] getActions() {
        return command.getActions();
    }

    public Sheet createSheet() {
        return command.createSheet();
    }
    
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String propertyName = propertyChangeEvent.getPropertyName();
        if (RuntimeCommand.PROP_STATE.equals(propertyName)) {
            setState(command.getState());
        } else if (RuntimeCommand.PROP_DISPLAY_NAME.equals(propertyName)) {
            String displayName = command.getDisplayName();
            if (displayName == null || displayName.length() == 0) displayName = command.getName();
            setDisplayName(displayName);
        }
    }


    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeCommandNode.class).getString(name);
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx(this.getClass());
    }
    
}
