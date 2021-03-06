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

package org.netbeans.modules.portalpack.servers.core.util;

import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;


public class Status implements DeploymentStatus {
    
    /** Value of action type. */
    private ActionType at;
    
    /** Executed command. */
    private CommandType ct;
    
    /** Status message. */
    private String msg;
    
    /** Current state. */
    private StateType state;
    
    public Status(ActionType at, CommandType ct, String msg, StateType state) {
        this.at = at;
        this.ct = ct;
        this.msg = msg;
        this.state = state;
    }
    
    public ActionType getAction() {
        return at;
    }
    
    public CommandType getCommand() {
        return ct;
    }
    
    public String getMessage() {
        return msg;
    }
    
    public StateType getState() {
        return state;
    }
    
    public boolean isCompleted() {
        return StateType.COMPLETED.equals(state);
    }
    
    public boolean isFailed() {
        return StateType.FAILED.equals(state);
    }
    
    public boolean isRunning() {
        return StateType.RUNNING.equals(state);
    }
    
    public String toString() {
        return "Action= " + getAction() + // NOI18N
            "  Status= " + getState() + "  " + getMessage (); // NOI18N
    }
}

