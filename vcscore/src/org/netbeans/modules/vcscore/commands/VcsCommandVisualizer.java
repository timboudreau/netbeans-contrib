/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.awt.event.ActionListener;
import java.util.Map;

import org.netbeans.api.vcs.commands.CommandTask;

/**
 * This class should be used to display the graphical output of the running command.
 * the <code>open</code> method is called to open the visualizer.
 * The implementing class must have a default constructor.
 *
 * @author  Martin Entlicher
 */
//public abstract class VcsCommandVisualizer extends TopComponent {
public interface VcsCommandVisualizer {
    
    /**
     * After instatiation this method is called with the task that should
     * be visualized. Do not attach listeners for the output data here,
     * use the *Output* methods instead. The task can be already finished
     * when this method is called.
     */
    public void setVcsTask(VcsDescribedTask task);
    
    /**
     * After instatiation this method is called with the map of all possible
     * file statuses. The map contains raw file status strings as keys
     * and appropriate {@link org.netbeans.api.vcs.FileStatusInfo} objects
     * as values.
     * @return The file status map.
     */
    public void setPossibleFileStatusInfoMap(Map infoMap);
    
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

    /**
     * Open the visualizer.
     * If there is a GUI wrapper defined, that wrapper should be used to display
     * the visualizer.
     * @param wrapper The GUI wrapper or <code>null</code>
     */
    public abstract void open(Wrapper wrapper);
    
    /**
     * Tell, whether the visualizer is currently opened.
     * This method is used to decide whether <code>open()</code>
     * should be called or not.
     */
    public abstract boolean isOpened();
    
    /**
     * Request the focus for this visualizer. See {@link org.openide.windows.TopComponent#requestFocus}.
     */
    public abstract void requestFocus();
    
    /**
     * The wrapper of the visualizer.
     */
    public static interface Wrapper {
        
        /**
         * After instatiation this method is called with the task that is
         * wrapped. The task can be already finished when this method is called.
         */
        public void setTask(CommandTask task);
    
        /**
         * Wrap a visualizer component.
         * This method is to be used to display the visualizer if wrapper is defined.
         * @param visualizerComponent The component to wrap
         * @param showStatus Whether to show the status of the command (running/finished)
         * @param showCancel Whether to provide a possibility to cancel the command
         * @param showClose Whether to provide a possibility to close the dialog
         * @return The action listener, that is notified when the dialog should be closed.
         */
        public ActionListener wrap(javax.swing.JComponent visualizerComponent,
                                   boolean showStatus, boolean showClose);
        
    }
    
}
