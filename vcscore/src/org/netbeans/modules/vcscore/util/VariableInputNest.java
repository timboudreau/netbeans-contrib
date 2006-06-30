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
