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
