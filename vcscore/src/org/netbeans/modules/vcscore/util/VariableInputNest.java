/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import org.netbeans.modules.vcscore.commands.CommandExecutionContext;

import java.util.Hashtable;

/**
 * Provides services for embedded JCOMPONENT
 * in VariableInputDialog.
 *
 * @author Petr Kuzel
 */
public final class VariableInputNest {

    private final String variable;

    private final VariableInputDialog peer;

    private final NestableInputComponent egg;

    /** Constructed by framework only. */
    VariableInputNest(VariableInputDialog peer, NestableInputComponent egg, String variable) {
        this.peer = peer;
        this.variable = variable;
        this.egg = egg;
    }

    /**
     * Access value of given variable.
     */
    public String getValue(String variable) {
        return peer.getInputComponent(variable).getValue();
    }

    /**
     * Notifies VID validaton framework about value change
     * possibly triggering validity change.
     */
    public void fireValueChanged(String variable, Object newValue) {
        String propName = VariableInputDialog.PROP_VAR_CHANGED + variable;
        peer.firePropertyChange(propName, null, newValue);
    }

    /** If customizing command returns it's CommandExecutionContext */
    public CommandExecutionContext getCommandExecutionContext() {
        return peer.getCommandExecutionContext();
    }

    /** If customizing command returns it's variables */
    public Hashtable getCommandHashtable() {
        return peer.getCommandHashtable();
    }
}
