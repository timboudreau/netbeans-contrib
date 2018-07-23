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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.InvocationEvent;
import java.util.Iterator;

import org.netbeans.api.vcs.commands.CommandTask;

import org.openide.awt.Actions;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 * Dialog wrapper of a command visualizer.<p>
 * This dialog is to be used when it is necessary to wait for a command task
 * in AWT thread. This dialog is able to wrap any GUI that can be presented
 * by the command so that it will not collide with the blocked AWT event queue.
 * <p>
 * This dialog is modal, therefore its presentation is blocking, but another
 * event pump is started so that the system stays responsive.
 *
 * @author  Martin Entlicher
 */
public class DialogVisualizerWrapper extends javax.swing.JDialog implements VcsCommandVisualizer.Wrapper, CommandProcessListener {
    
    private CommandTask task;
    private String msgRunning;
    private String msgFinished;
    private ActionListener killActionListener;
    
    /** Creates new form DialogVisualizerWrapper */
    public DialogVisualizerWrapper() {
        super(WindowManager.getDefault().getMainWindow(), true);
        this.msgRunning = org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.msgCmdRunning");
        this.msgFinished = org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.msgCmdFinished");
        initComponents();
        commandProgressBar.setIndeterminate(true);
        statusLabel.setText(msgRunning);
        setBounds(Utilities.findCenterBounds(getSize()));
    }
    
    public void setTask(CommandTask task) {
        this.task = task;
        String cmdName = task.getDisplayName();
        if (cmdName == null) {
            cmdName = task.getName();
        } else {
            cmdName = Actions.cutAmpersand(cmdName);
        }
        this.msgRunning = org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.msgCmdRunning", cmdName);
        this.msgFinished = org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.msgCmdFinished", cmdName);
        killActionListener = new CommandOutputVisualizer.CommandKillListener(task, this);
        cancelButton.addActionListener(killActionListener);
        statusLabel.setText(msgRunning);
        CommandProcessor.getInstance().addCommandProcessListener(this);
        if (task.isFinished()) {
            commandHasFinished();
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                pack();
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        statusLabel = new javax.swing.JLabel();
        visualizerPanel = new javax.swing.JPanel();
        commandProgressBar = new javax.swing.JProgressBar();
        buttonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        statusLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 6);
        getContentPane().add(statusLabel, gridBagConstraints);

        visualizerPanel.add(commandProgressBar);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 6);
        getContentPane().add(visualizerPanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setLabel(org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.btnCancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        buttonPanel.add(cancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 6, 6);
        getContentPane().add(buttonPanel, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        if (task.isFinished()) {
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        if (task.isFinished()) {
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_closeDialog
    
    public ActionListener wrap(javax.swing.JComponent visualizerComponent,
                               boolean showStatus, boolean showClose) {
        if (!showStatus && !showClose) {
            getContentPane().removeAll();
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            getContentPane().add(visualizerPanel, gridBagConstraints);
        } else {
            if (!showStatus) {
                remove(statusLabel);
            }
            if (!showClose) {
                remove(buttonPanel);
            }
        }
        visualizerPanel.removeAll();
        visualizerPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        visualizerPanel.add(visualizerComponent, gridBagConstraints);
        String title = (String) visualizerComponent.getClientProperty("wrapper-title");
        if (title != null) {
            setTitle(title);
        } else {
            if (task != null) {
                String cmdName = task.getDisplayName();
                if (cmdName == null) {
                    cmdName = task.getName();
                } else {
                    cmdName = Actions.cutAmpersand(cmdName);
                }
                setTitle(cmdName);
            }
        }
        pack();
        setBounds(Utilities.findCenterBounds(getSize()));
        return new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisible(false);
                dispose();
            }
        };
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JProgressBar commandProgressBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel visualizerPanel;
    // End of variables declaration//GEN-END:variables
    
    private void commandHasFinished() {
        CommandProcessor.getInstance().removeCommandProcessListener(this);
        javax.swing.SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                cancelButton.removeActionListener(killActionListener);
                statusLabel.setText(msgFinished);
                cancelButton.setText(org.openide.util.NbBundle.getMessage(DialogVisualizerWrapper.class, "DialogVisualizerWrapper.btnClose"));
                javax.swing.JRootPane root = getRootPane();
                if (root != null) {
                    root.setDefaultButton(cancelButton);
                }
                commandProgressBar.setValue(commandProgressBar.getMaximum());
                commandProgressBar.setIndeterminate(false);
            }
        });
    }
    
    public void commandDone(CommandTaskInfo info) {
        if (task.equals(info.getTask())) {
            commandHasFinished();
        }
    }
    
    public void commandPreprocessed(org.netbeans.api.vcs.commands.Command cmd, boolean status) {
    }
    
    public void commandPreprocessing(org.netbeans.api.vcs.commands.Command cmd) {
    }
    
    public void commandStarting(CommandTaskInfo info) {
    }
    
    public org.netbeans.spi.vcs.VcsCommandsProvider getProvider() {
        return null;
    }
    
    private PumpControl pumpControl;
    
    public void show() {
        pumpControl = new PumpControl();
        super.show();
    }
    
    public void dispose() {
        super.dispose();
        pumpControl.release();
    }
    
    /**
     * Control the event pump.
     * Do not let invocation events from outside of VCS and JDK to process.
     * This is a protection of our modal dialog, so that scheduled events do not
     * create GUI upon us.
     */
    private class PumpControl implements Runnable {
        
        private java.util.List ignoredEvents = new java.util.ArrayList();
        private EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        private CleanUpEvent cleanUpEvent = new CleanUpEvent(Toolkit.getDefaultToolkit(), this);
        
        public PumpControl() {
            cleanUp();
            queue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this));
        }
        
        private void cleanUp() {
            java.util.List eventsToReturn = new java.util.ArrayList();
            while (queue.peekEvent() != null) {
                try {
                    AWTEvent event = queue.getNextEvent();
                    if (ignore(event)) {
                        ignoredEvents.add(event);
                    } else {
                        eventsToReturn.add(event);
                    }
                } catch (InterruptedException iex) {}
            }
            for (Iterator it = eventsToReturn.iterator(); it.hasNext(); ) {
                AWTEvent event = (AWTEvent) it.next();
                queue.postEvent(event);
            }
        }
        
        public void run() {
            if (queue != null) {
                try {
                    Thread.currentThread().sleep(20);
                } catch (InterruptedException iex) {}
                cleanUp();
                queue.postEvent(cleanUpEvent);
            }
        }
        
        public void release() {
            // Post all the ignored events now
            for (Iterator it = ignoredEvents.iterator(); it.hasNext(); ) {
                AWTEvent event = (AWTEvent) it.next();
                queue.postEvent(event);
                //System.out.println("POST Event: "+event);
            }
            // Stop cleaning up.
            queue = null;
        }
        
        private boolean ignore(AWTEvent event) {
            //System.out.println("HAVE Event: "+event);
            if (event instanceof InvocationEvent) {
                InvocationEvent ie = (InvocationEvent) event;
                String ids;
                try {
                    java.lang.reflect.Field runnableField = ie.getClass().getDeclaredField("runnable");
                    runnableField.setAccessible(true);
                    Object runnable = runnableField.get(ie);
                    ids = runnable.getClass().getName();
                } catch (Exception ex) {
                    ids = ie.paramString();
                }
                if (ids.indexOf("javax.swing") >= 0 || ids.indexOf("java.awt") >= 0 ||
                        ids.indexOf("sun.awt.") >= 0 ||
                        ids.indexOf("vcscore") >= 0) {
                    return false;
                } else {
                    //System.out.println("IGNORED Event: "+event);
                    return true;
                }
            }
            return false;
        }
        
        
        private class CleanUpEvent extends InvocationEvent {
            public CleanUpEvent(Object source, Runnable run) {
                // ID = PaintEvent.PAINT, to have the lowest priority.
                super(source, java.awt.event.PaintEvent.PAINT, run, null, false);
            }
        }
        
    }
    
}
