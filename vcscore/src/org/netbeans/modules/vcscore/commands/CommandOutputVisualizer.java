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

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.SwingUtilities;

import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;

/**
 * The default visualizer of command output.
 * @author  Martin Entlicher
 */
public class CommandOutputVisualizer extends VcsCommandVisualizer {

    public static final String MODE_NAME = "Default VCS Command Output";
    
    private static final int MAX_NUM_LINES_TO_KEEP = 5000;

    private CommandOutputPanel outputPanel;
    private ArrayList closeListeners = new ArrayList();
    private VcsCommandExecutor vce;
    private CommandsPool pool = null;
    private CommandKillListener killListener = null;
    
    private static final long serialVersionUID = -8901790341334731237L;
    
    /** Creates new CommandOutputVisualizer */
    public CommandOutputVisualizer(VcsCommandExecutor vce) {
        this.vce = vce;
        outputPanel = new CommandOutputPanel();
        killListener = new CommandKillListener();
        outputPanel.addKillActionListener(killListener);
        initComponents();
        outputPanel.setExec(vce.getExec());
        String name = vce.getCommand().getDisplayName();
        if (name == null || name.length() == 0) name = vce.getCommand().getName();
        setName(java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"),
                new Object[] { name }));
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));
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
    
    synchronized void setCommandsPool(CommandsPool pool) {
        this.pool = pool;
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

    protected void componentActivated() {
        super.componentActivated();
        outputPanel.componentActivated();
    }
    
    protected void componentDeactivated() {
        super.componentDeactivated();
        outputPanel.componentDeactivated();
    }
    
    /**
     * Disable serialization.
     * @return null
     */
    protected Object writeReplace () throws java.io.ObjectStreamException {
        close();
        return null;
    }
    
    private void keepReasonableNumOfLines(javax.swing.JTextArea area) {
        if (area.getLineCount() > MAX_NUM_LINES_TO_KEEP) {
            try {
                area.replaceRange(null, area.getLineStartOffset(0), area.getLineStartOffset(10));
            } catch (javax.swing.text.BadLocationException exc) {
            }
        }
    }
    
    /**
     * Receive a line of standard output.
     */
    public void stdOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                javax.swing.JTextArea area = outputPanel.getStdOutputArea();
                area.append(line + "\n");
                keepReasonableNumOfLines(area);
            }
        });
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                javax.swing.JTextArea area = outputPanel.getErrOutputArea();
                area.append(line + "\n");
                keepReasonableNumOfLines(area);
            }
        });
    }

    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                outputPanel.getStdDataOutputArea().append(VcsUtilities.arrayToString(data) + "\n");
            }
        });
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                outputPanel.getErrDataOutputArea().append(VcsUtilities.arrayToString(data) + "\n");
            }
        });
    }
    
    public void setExitStatus(int exit) {
        outputPanel.setStatus(CommandsPool.getExitStatusString(exit));
        outputPanel.commandFinished(true);
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
        synchronized (this) {
            pool = null;
        }
        synchronized (closeListeners) {
            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                TopComponentCloseListener l = (TopComponentCloseListener) it.next();
                l.closing();
            }
            closeListeners.clear();
        }
    }
    
    class CommandKillListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (pool != null) {
                String name = vce.getCommand().getDisplayName();
                if (name == null || name.length() == 0) name = vce.getCommand().getName();
                NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation (
                    java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.killCommand"),
                                                   new Object[] { name }),
                    NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
                if (NotifyDescriptor.Confirmation.OK_OPTION.equals (org.openide.TopManager.getDefault ().notify (nd))) {
                    synchronized (this) {
                        if (pool != null) pool.kill(vce);
                    }
                }
            }
        }
    }
}
