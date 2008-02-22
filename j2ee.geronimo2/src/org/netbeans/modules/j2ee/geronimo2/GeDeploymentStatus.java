/*
 * GeDeploymentStatus.java
 *
 * Created on February 21, 2007, 3:20 PM
 *
 */
package org.netbeans.modules.j2ee.geronimo2;


import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;

/**
 * Tracks down startup/shutdown process
 * @author Max Sauer
 */
public class GeDeploymentStatus implements DeploymentStatus {

    private ActionType action;
    private CommandType command;
    private StateType state;
    private String message;
    
    /** Creates a new instance of GeDeploymentStatus */
    public GeDeploymentStatus(ActionType action, CommandType command,
	    StateType state, String message) {
	this.action = action;
        this.command = command;
        this.state = state;
        this.message = message;
    }
    
    public StateType getState() {
	return state;
    }
    
    public CommandType getCommand() {
	return command;
    }
    
    public ActionType getAction() {
	return action;
    }
    
    public String getMessage() {
	return message;
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
    
}
