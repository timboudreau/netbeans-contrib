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

import org.netbeans.modules.vcscore.ui.OutputPanel;

/**
 * The interactive visualizer of command output with an ability to provide input.
 *
 * @author  Martin Entlicher
 */
public class InteractiveCommandOutputVisualizer extends CommandOutputVisualizer {
    
    private VcsCommandExecutor executor;
    private InteractiveCommandOutputPanel interactivePanel;
    private TextOutputListener immediateOut;
    private TextOutputListener immediateErr;
    
    /** Creates a new instance of InteractiveCommandOutputVisualizer */
    public InteractiveCommandOutputVisualizer() {
        this.immediateOut = new ImmediateOutput();
        this.immediateErr = new ImmediateError();
    }
    
    protected OutputPanel createOutputPanel() {
        this.interactivePanel = new InteractiveCommandOutputPanel();
        return interactivePanel;
    }
    
    public void setVcsTask(VcsDescribedTask task) {
        super.setVcsTask(task);
        executor = task.getExecutor();
        executor.addImmediateTextOutputListener(immediateOut);
        executor.addImmediateTextErrorListener(immediateErr);
        interactivePanel.setInput(new TextInput() {
            public void sendInput(String text) {
                executor.sendInput(text);
                immediateOut.outputLine(text);
                //javax.swing.JTextArea area = interactivePanel.getStdOutputArea();
                //appendTextToArea(area, text);
            }
        });
    }
    
    /**
     * Receive a line of standard output.
     * Unimplemented, we use immediate output listeners.
     */
    public void stdOutputLine(final String line) {
        // Unimplemented.
    }
    
    /**
     * Receive a line of error output.
     * Unimplemented, we use immediate output listeners.
     */
    public void errOutputLine(final String line) {
        // Unimplemented.
    }

    
    private final class ImmediateOutput extends Object implements TextOutputListener {
        
        private CommandOutputTextProcessor.TextOutput stdOutput;
        
        public void outputLine(String text) {
            if (stdOutput == null) {
                stdOutput = CommandOutputTextProcessor.getDefault().createOutput();
                stdOutput.setTextArea(interactivePanel.getStdOutputArea());
            }
            stdOutput.addText(text);
        }
        
    }
    
    private final class ImmediateError extends Object implements TextOutputListener {
        
        private CommandOutputTextProcessor.TextOutput errOutput;
        
        public void outputLine(String text) {
            if (errOutput == null) {
                errOutput = CommandOutputTextProcessor.getDefault().createOutput();
                errOutput.setTextArea(interactivePanel.getErrOutputArea());
            }
            errOutput.addText(text);
        }
        
    }
    
}
