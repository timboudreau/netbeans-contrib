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

import org.openide.windows.Workspace;
import org.openide.windows.Mode;

import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The default visualizer of command output.
 * @author  Martin Entlicher
 */
public class CommandOutputVisualizer extends VcsCommandVisualizer {

    private static final String MODE_NAME = "Default VCS Command Output";

    private CommandOutputPanel outputPanel;
    /** Creates new CommandOutputVisualizer */
    public CommandOutputVisualizer(VcsCommandExecutor vce) {
        outputPanel = new CommandOutputPanel();
        initComponents();
        outputPanel.setExec(vce.getExec());
        setName(java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"),
                new Object[] { vce.getCommand().getDisplayName() }));
        /*
        vce.addOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                stdOutputLine(line);
                //outputPanel.getStdOutputArea().append(line + "\n");
            }
        });
        vce.addErrorOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                errOutputLine(line);
                //outputPanel.getErrOutputArea().append(line + "\n");
            }
        });
        vce.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                stdOutputData(data);
                //outputPanel.getStdDataOutputArea().append(VcsUtilities.arrayToString(data));
            }
        });
        vce.addDataErrorOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                errOutputData(data);
                //outputPanel.getErrDataOutputArea().append(VcsUtilities.arrayToString(data));
            }
        });
         */
    }
    
    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(outputPanel, gridBagConstraints);
        outputPanel.setStatus(org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandExitStatus.running"));
    }
    
    /** @return false to open immediatelly.
     */
    public boolean openAfterCommandFinish() {
        return false;
    }
    
    /**
     * Open the component on the given workspace.
     */
    public void open(Workspace workspace) {
        if (workspace == null) workspace = org.openide.TopManager.getDefault().getWindowManager().getCurrentWorkspace();
        Mode myMode = workspace.findMode(this);
        if (myMode == null) {
            // create new mode for CI and set the bounds properly
            String modeName = org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.modeName");
            myMode = workspace.createMode(MODE_NAME, modeName, null); //NOI18N
            myMode.dockInto(this);
        }
        super.open(workspace);
    }
    
    /**
     * Disable serialization.
     * @return null
     */
    protected Object writeReplace () throws java.io.ObjectStreamException {
        close();
        return null;
    }
    
    /**
     * Receive a line of standard output.
     */
    public void stdOutputLine(String line) {
        outputPanel.getStdOutputArea().append(line + "\n");
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(String line) {
        outputPanel.getErrOutputArea().append(line + "\n");
    }

    /**
     * Receive the data output.
     */
    public void stdOutputData(String[] data) {
        outputPanel.getStdDataOutputArea().append(VcsUtilities.arrayToString(data) + "\n");
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(String[] data) {
        outputPanel.getErrDataOutputArea().append(VcsUtilities.arrayToString(data) + "\n");
    }
    
    public void setExitStatus(int exit) {
        String status;
        if (VcsCommandExecutor.SUCCEEDED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandExitStatus.success");
        } else if (VcsCommandExecutor.FAILED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandExitStatus.failed");
        } else if (VcsCommandExecutor.INTERRUPTED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandExitStatus.interrupted");
        } else {
            status = org.openide.util.NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandExitStatus.unknown");
        }
        outputPanel.setStatus(status);
        outputPanel.commandFinished(true);
    }

}
