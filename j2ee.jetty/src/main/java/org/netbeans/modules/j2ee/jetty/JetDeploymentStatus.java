/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.j2ee.jetty;

import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;

/**
 *
 * @author novakm
 */
public class JetDeploymentStatus implements DeploymentStatus {
    private final ActionType action;
    private final CommandType command;
    private final StateType state;
    private final String message;
    
    /** 
     * Creates a new instance of JetDeploymentStatus
     * @param action - type of the action it represents
     * @param command - command that is being executed
     * @param state - state of the action
     * @param message - message for the status
     */
    public JetDeploymentStatus(ActionType action, CommandType command, StateType state, String message) {
        this.action = action;
        this.command = command;
        this.state = state;
        this.message = message;
    }
    /**
     * Returns message of the status
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns state of the status
     * @return state
     */
    public StateType getState() {
        return state;
    }

    /**
     * Returns command of the status
     * @return command
     */
    public CommandType getCommand() {
        return command;
    }
    
    /**
     * Returns action of the status
     * @return action
     */
    public ActionType getAction() {
        return action;
    }
    
    /**
     * Checks whether the status is in running state
     * @return true if it is, false otherwise
     */
    public boolean isRunning() {
        return StateType.RUNNING.equals(state);
    }

    /**
     * Checks whether the status is in failed state
     * @return true if it is, false otherwise
     */
    public boolean isFailed() {
        return StateType.FAILED.equals(state);
    }

     
    /**
     * Checks whether the status is in completed state
     * @return true if it is, false otherwise
     */
    public boolean isCompleted() {
        return StateType.COMPLETED.equals(state);
    }

}