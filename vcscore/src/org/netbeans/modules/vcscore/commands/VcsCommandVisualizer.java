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

package org.netbeans.modules.vcscore.commands;

import java.util.Map;

import org.openide.windows.TopComponent;

/**
 * This class should be used to display the graphical output of the running command.
 * the <code>open</code> method is called to open the visualizer.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsCommandVisualizer extends TopComponent {
    
    /**
     * After instatiation this method is called with the task that should
     * be visualized. Do not attach listeners for the output data here,
     * use the *Output* methods instead. The task can be already finished
     * when this method is called.
     */
    public void setVcsTask(VcsDescribedTask task) {
        // subclasses can implement this
    }
    
    /**
     * After instatiation this method is called with the map of all possible
     * file statuses. The map contains raw file status strings as keys
     * and appropriate {@link org.netbeans.api.vcs.FileStatusInfo} objects
     * as values.
     * @return The file status map.
     */
    public void setPossibleFileStatusInfoMap(Map infoMap) {
        // subclasses can implement this
    }
    
    /**
     * This method is called when the command finishes.
     * @param exit the exit status of the command.
     */
    public abstract void setExitStatus(int exit);
    
    /**
     * Tells when the <code>open</code> method should be called.
     * @return true -- this component will be opened after the command finish its execution,
     *         false -- this component will be opened just before the command is started.
     */
    public abstract boolean openAfterCommandFinish();
    
    /**
     * Receive a line of standard output of the visualized command.
     */
    public abstract void stdOutputLine(String line);
    
    /**
     * Receive a line of error output of the visualized command.
     */
    public abstract void errOutputLine(String line);

    /**
     * Receive the data output of the visualized command.
     */
    public abstract void stdOutputData(String[] data);
    
    /**
     * Receive the error data output of the visualized command.
     */
    public abstract void errOutputData(String[] data);

}
