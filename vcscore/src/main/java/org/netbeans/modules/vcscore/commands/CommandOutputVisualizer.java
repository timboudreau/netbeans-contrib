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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;


import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcscore.ui.OutputPanel;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.openide.windows.Mode;

/**
 * The default visualizer of command output.
 * @author  Martin Entlicher
 */
public class CommandOutputVisualizer extends TopComponent implements VcsCommandVisualizer {

    private OutputPanel outputPanel;
    private ArrayList closeListeners = new ArrayList();
    private CommandTask task;
    private VcsCommandExecutor vce;
    private java.awt.event.ActionListener killListener = null;
       
    private java.awt.event.ActionListener closeWrapperListener = null;
    
    private String displayName;
    
    private CommandOutputTextProcessor.TextOutput stdOutput;
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    private static final long serialVersionUID = -8901790341334731237L;
    
    public CommandOutputVisualizer() {
        outputPanel = createOutputPanel();      
        initComponents();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    public CommandOutputVisualizer(java.awt.event.ActionListener killListener, String execString, String name) {
        this();
        this.killListener = killListener;
        outputPanel.addKillActionListener(this.killListener);        
        setName(java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"),
                new Object[] { name }));
       
    }
    
    protected OutputPanel createOutputPanel(){
        return new OutputPanel();
    }
    
    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(outputPanel, gridBagConstraints);
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer"));
    }
    
    public void setVcsTask(VcsDescribedTask task) {
        this.task = (CommandTask) task;
        this.vce = task.getExecutor();
        killListener = new CommandKillListener(this.task);
        outputPanel.addKillActionListener(killListener);
        outputPanel.setIgnoreFailure(VcsCommandIO.getBooleanPropertyAssumeDefault(task.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL));
        final String title;
        String commandName = findDisplayName(this.task);
        java.util.Collection files = vce.getFiles();
        if (files.size() == 1) {
            String filePath = (String) files.iterator().next();
            java.io.File file = new java.io.File(filePath);
            title = java.text.MessageFormat.format(
                NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.title_one"), // NOI18N
                new Object[] { file.getName(), commandName });
        }
        else if (files.size() > 1) {
            title = java.text.MessageFormat.format(
                NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.title_many"), // NOI18N
                new Object[] { Integer.toString(files.size()), commandName }); 
        }
        else title = java.text.MessageFormat.format(
            NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"), // NOI18N
            new Object[] { commandName });

        final String finalName = commandName;
        this.displayName = title;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setName(finalName);
                setDisplayName(title);
            }
        });
    }
    
    /**
     *Searchs tasks structure for valid displayName
     */
    private String findDisplayName(CommandTask task) {
        String dispName = null;
        while(dispName == null && task != null) {
            dispName = task.getDisplayName();
            task = CommandProcessor.getInstance().getParentTask(task);
        }
        if (dispName == null) dispName = this.task.getName();
        return Actions.cutAmpersand(dispName);
    }
    
    public void setOutputCollector(CommandOutputCollector outputCollector) {
        outputPanel.setOutputCollector(outputCollector);
    }
    
    public void setPossibleFileStatusInfoMap(java.util.Map infoMap) {
        // Left unimplemented, we do not care about possible statii here.
    }
    
    /*
    synchronized void setCommandsPool(CommandsPool pool) {
        this.pool = pool;
    }
     */
    
    public boolean doesDisplayError() {
        return true;
    }
    
    /** @return false to open immediatelly.
     */
    public boolean openAfterCommandFinish() {
        return false;
    }
    
    public void open(Wrapper wrapper) {
        if (wrapper == null) {
            open();
        } else {
            outputPanel.putClientProperty("wrapper-title", getDisplayName());
            closeWrapperListener = wrapper.wrap(outputPanel, false, true);
            Component window = outputPanel.getParent();
            while (window != null && !(window instanceof java.awt.Window)) {
                window = window.getParent();
            }
            if (window != null) {
                outputPanel.removeKillActionListener(killListener);
                killListener = new CommandKillListener(this.task, window);
                outputPanel.addKillActionListener(killListener);
            }
        }
	
    }

    /**
     * Open the component on the given workspace.
     */
    public void open() {
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/vcs_output.png"));
        CommandOutputTopComponent out = CommandOutputTopComponent.getInstance();
        out.addVisualizer(displayName, outputPanel, true);
        out.open();
    }

    protected void componentActivated() {
        super.componentActivated();
   //     outputPanel.componentActivated();
    }
    
    protected void componentDeactivated() {
        super.componentDeactivated();
  //      outputPanel.componentDeactivated();
    }
    
    /**
     * Disable serialization.
     * @return null
     */
    protected Object writeReplace () throws java.io.ObjectStreamException {
        return null;
    }
    
    /**
     * Receive a line of standard output.
     */
    public void stdOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        if (stdOutput == null) {
            stdOutput = CommandOutputTextProcessor.getDefault().createOutput();
            stdOutput.setTextArea(outputPanel.getStdOutputArea());
        }
        stdOutput.addText(line+'\n');
        //appendLineToArea(outputPanel.getStdOutputArea(), line);
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        if (errOutput == null) {
            errOutput = CommandOutputTextProcessor.getDefault().createOutput();
            errOutput.setTextArea(outputPanel.getErrOutputArea());
        }
        errOutput.addText(line+'\n');
        //appendLineToArea(outputPanel.getErrOutputArea(), line);
    }

    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        if (Boolean.getBoolean("netbeans.vcs.dev")) {
            if (stdDataOutput == null) {
                stdDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                stdDataOutput.setTextArea(outputPanel.getDataStdOutputArea());
            }
            stdDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
            //appendLineToArea(outputPanel.getDataStdOutputArea(), VcsUtilities.arrayToString(data));
        }
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        if (Boolean.getBoolean("netbeans.vcs.dev")) {
            if (errDataOutput == null) {
                errDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                errDataOutput.setTextArea(outputPanel.getDataErrOutputArea());
            }
            errDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
            //appendLineToArea(outputPanel.getDataErrOutputArea(), VcsUtilities.arrayToString(data));
        }
    }
    
    public void setExitStatus(int exit) {
   //     outputPanel.setStatus(CommandProcessor.getExitStatusString(exit));
        outputPanel.commandFinished(exit);
        if (closeWrapperListener != null) {
            outputPanel.addKillActionListener(closeWrapperListener);
        }
    }

    public void addCloseListener(TopComponentCloseListener l) {
        synchronized (closeListeners) {
            closeListeners.add(l);
        }
    }
    
    /**
     * Override for clean up reasons.
     * Will be moved to the appropriate method when will be made.
     */
    public boolean canClose(Workspace workspace, boolean last) {
        boolean can = super.canClose(workspace, last);
        if (last && can) {
            closing();
        }
        return can;
    }
    
    /**
     * Called when the TopComponent is being to close.
     */
    private void closing() {
        outputPanel.removeKillActionListener(killListener);
        //synchronized (this) {
        //    pool = null;
        //}
        synchronized (closeListeners) {
            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                TopComponentCloseListener l = (TopComponentCloseListener) it.next();
                l.closing();
            }
            closeListeners.clear();
        }
    }
    
    public static class CommandKillListener implements java.awt.event.ActionListener {
        
        private CommandTask task;
        private Component owner;
        
        public CommandKillListener(CommandTask task) {
            this(task, null);
        }
        
        public CommandKillListener(CommandTask task, Component owner) {
            this.task = task;
            this.owner = owner;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            String name = task.getDisplayName();//vce.getCommand().getDisplayName();
            if (name == null || name.length() == 0) name = task.getName();//vce.getCommand().getName();
            else name = Actions.cutAmpersand(name);
            String message = java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.killCommand"),
                                               new Object[] { name });
            if (owner == null) {
                NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation (
                    message, NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
                if (NotifyDescriptor.Confirmation.OK_OPTION.equals (DialogDisplayer.getDefault ().notify (nd))) {
                    task.stop();
                }
            } else {
                int confirmed = JOptionPane.showConfirmDialog(owner, message,
                    NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.QuestionTitle"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirmed == JOptionPane.YES_OPTION) {
                    task.stop();
                }
            }
        }
        
    }
    
}
