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

/**
 * Provides services for embedded JCOMPONENT
 * in VariableInputDialog.
 *
 * @author Petr Kuzel
 */
public final class VariableInputNest {

    private final String variable;

    private final VariableInputDialog peer;

    /** Constructed by framework only. */
    VariableInputNest(VariableInputDialog peer, String variable) {
        this.peer = peer;
        this.variable = variable;
    }

    /**
     * Access value of given variable.
     */
    public String getValue(String variable) {
        return peer.getInputComponent(variable).getValue();
    }

    /**
     * Notifies VID validaton framework about value
     * validity change.
     */
    public void fireVerificationMessage(String msg) {
        // TODO how to kick up the VID validation framework?
        // I basicaly need to disable OK button and possibly
        // set error message
    }
}
