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
