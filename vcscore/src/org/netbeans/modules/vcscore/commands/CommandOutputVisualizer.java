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
import java.util.Hashtable;
import javax.swing.SwingUtilities;

import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;

import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;

/**
 * The default visualizer of command output.
 * @author  Martin Entlicher
 */
public class CommandOutputVisualizer extends VcsCommandVisualizer {

    public static final String MODE_NAME = "Default VCS Command Output";
    
    //private static final int MAX_NUM_LINES_TO_KEEP = 1000;
    /** Maximum number of characters to keep in the buffer */
    private static final int MAX_BUFFER_SIZE = 3000*80;
    /** When both the buffer and the text area are full, replace only this part
     * of the buffer */
    private static final int FAST_APPEND_SIZE = 100*80;
    /** The maximum number of characters to keep in the text area */
    private static final int MAX_AREA_SIZE = MAX_BUFFER_SIZE - FAST_APPEND_SIZE;
    
    private static RequestProcessor outputDisplayRequestProcessor;
    private static Hashtable outputDisplayStuff;

    private CommandOutputPanel outputPanel;
    private ArrayList closeListeners = new ArrayList();
    private CommandTask task;
    private VcsCommandExecutor vce;
    private java.awt.event.ActionListener killListener = null;
    
    
    private static final long serialVersionUID = -8901790341334731237L;
    
    protected CommandOutputVisualizer() {
        outputPanel = new CommandOutputPanel();
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));
        putClientProperty("PersistenceType", "Never");

        // http://www.netbeans.org/issues/show_bug.cgi?id=24199
        // the TabPolicy property's value makes sure that the tab is not shown 
        // for the topcomponent when it is alone in the mode.
        putClientProperty("TabPolicy", "HideWhenAlone");
        
        initComponents();
        synchronized (CommandOutputVisualizer.class) {
            if (outputDisplayRequestProcessor == null) {
                outputDisplayRequestProcessor = new RequestProcessor("Output Display Request Processor");
                outputDisplayRequestProcessor.post(new OutputDisplayer());
            }
        }
    }
    
    /** Creates new CommandOutputVisualizer */
    public CommandOutputVisualizer(VcsDescribedTask task) {
        this.vce = task.getExecutor();
        this.task = (CommandTask) task;
        outputPanel = new CommandOutputPanel();
        killListener = new CommandKillListener();
        outputPanel.addKillActionListener(killListener);
        outputPanel.setExec(vce.getExec());
        String name = vce.getCommand().getDisplayName();
        if (name == null || name.length() == 0) name = vce.getCommand().getName();
        setName(java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"),
                new Object[] { name }));
    }
    
    public CommandOutputVisualizer(java.awt.event.ActionListener killListener, String execString, String name) {
        this();
        this.killListener = killListener;
        outputPanel.addKillActionListener(this.killListener);
        outputPanel.setExec(execString);
        setName(java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.name"),
                new Object[] { name }));
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
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer"));
    }
    
    /*
    synchronized void setCommandsPool(CommandsPool pool) {
        this.pool = pool;
    }
     */
    
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
        requestFocus();
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
    
    private void appendLineToArea(javax.swing.JTextArea area, String line) {
        synchronized (outputDisplayStuff) {
        StringBuffer buffer = (StringBuffer) outputDisplayStuff.get(area);
        if (buffer == null) {
            buffer = new StringBuffer(line + "\n");
            synchronized (outputDisplayStuff) {
                outputDisplayStuff.put(area, buffer);
                if (outputDisplayStuff.size() == 1) {
                    outputDisplayStuff.notify(); // it was empty before!
                }
            }
        } else {
            buffer.append(line + "\n");
            if (buffer.length() > MAX_BUFFER_SIZE) {
                buffer.delete(0, buffer.length() - MAX_AREA_SIZE  - 1);
            }
        }
        }
    }
    
    /**
     * Receive a line of standard output.
     */
    public void stdOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        appendLineToArea(outputPanel.getStdOutputArea(), line);
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        appendLineToArea(outputPanel.getErrOutputArea(), line);
    }

    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        appendLineToArea(outputPanel.getStdDataOutputArea(), VcsUtilities.arrayToString(data));
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        // to prevent deadlocks, append output in the AWT thread
        appendLineToArea(outputPanel.getErrDataOutputArea(), VcsUtilities.arrayToString(data));
    }
    
    public void setExitStatus(int exit) {
        outputPanel.setStatus(CommandProcessor.getExitStatusString(exit));
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
    
    class CommandKillListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            //if (pool != null) {
                String name = vce.getCommand().getDisplayName();
                if (name == null || name.length() == 0) name = vce.getCommand().getName();
                NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation (
                    java.text.MessageFormat.format(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.killCommand"),
                                                   new Object[] { name }),
                    NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
                if (NotifyDescriptor.Confirmation.OK_OPTION.equals (org.openide.TopManager.getDefault ().notify (nd))) {
                    task.stop();
                    //synchronized (this) {
                    //    if (pool != null) pool.kill(vce);
                    //}
                }
            //}
        }
    }
    
    private static class OutputDisplayer extends Object implements Runnable {
        
        private java.util.Random random;
        
        public OutputDisplayer() {
            outputDisplayStuff = new Hashtable();
            random = new java.util.Random();
        }
        
        public void run() {
            if (SwingUtilities.isEventDispatchThread()) {
                int index = random.nextInt(outputDisplayStuff.size());
                java.util.Enumeration keysEnum = outputDisplayStuff.keys();
                javax.swing.JTextArea area;
                do {
                    area = (javax.swing.JTextArea) keysEnum.nextElement();
                } while (--index >= 0);
                String append;
                String replace;
                int start;
                int end = area.getDocument().getLength();
                synchronized (outputDisplayStuff) {
                    StringBuffer buffer = (StringBuffer) outputDisplayStuff.get(area);
                    if (buffer.length() >= MAX_AREA_SIZE) {
                        append = null;
                        replace = buffer.substring(buffer.length() - FAST_APPEND_SIZE, buffer.length()).toString();
                        buffer.delete(0, replace.length());
                        start = end - replace.length();
                        if (start < 0) start = 0;
                    } else {
                        buffer = (StringBuffer) outputDisplayStuff.remove(area);
                        append = buffer.toString();
                        start = 0;
                        end += append.length();
                        if (end < MAX_AREA_SIZE) end = 0;
                        else end = end - MAX_AREA_SIZE + FAST_APPEND_SIZE;
                        replace = null;
                    }
                }
                if (append != null) {
                    area.append(append);
                }
                if (end > 0) {
                    area.replaceRange(replace, start, end);
                }
            } else {
                do {
                    synchronized (outputDisplayStuff) {
                        if (outputDisplayStuff.size() == 0) {
                            try {
                                outputDisplayStuff.wait();
                            } catch (InterruptedException iexc) {
                                break;
                            }
                        }
                    }
                    do {
                        try {
                            SwingUtilities.invokeAndWait(this);
                            // Let the AWT to catch it's breath
                            Thread.currentThread().yield();
                            Thread.currentThread().sleep(250);
                        } catch (InterruptedException iexc) {
                            break;
                        } catch (java.lang.reflect.InvocationTargetException itexc) {
                            org.openide.TopManager.getDefault().notifyException(itexc);
                            break;
                        }
                    } while (outputDisplayStuff.size() > 0);
                } while (true);
            }
        }
    }
}
