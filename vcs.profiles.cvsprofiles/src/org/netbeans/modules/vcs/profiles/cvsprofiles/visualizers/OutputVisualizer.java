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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openide.windows.Workspace;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;

import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.ProvidedCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedTask;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.openide.DialogDisplayer;
import org.openide.awt.Actions;

/**
 * The default visualizer of command output.
 * @author  Richard Gregor
 */
public abstract class OutputVisualizer extends VcsCommandVisualizer {
    
    //private static RequestProcessor outputDisplayRequestProcessor;        
    private JComponent outputPanel;
    private ArrayList closeListeners = new ArrayList();
    private CommandTask task;
    private VcsCommandsProvider cmdProvider;
    //private VcsCommandExecutor vce;
    protected Collection files;
    protected File rootDir;
    //protected String actFilePath;
    private java.awt.event.ActionListener killListener = null;
    private int exit;
    
    
    private static final long serialVersionUID = -8901790321334731232L;
    
    public OutputVisualizer() {
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));
        putClientProperty("PersistenceType", "Never");
        initAccessibility();
    }
    
    public abstract JComponent getOutputPanel();
    
    private void initComponents() {
        outputPanel = getOutputPanel();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(outputPanel, gridBagConstraints);
        
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(OutputVisualizer.class, "ACSN_CommandOutputVisualizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(OutputVisualizer.class, "ACSD_CommandOutputVisualizer"));
    }
    
    public void setVcsTask(VcsDescribedTask task) {
        this.task = (CommandTask) task;
        cmdProvider = ((ProvidedCommand) task).getProvider();
        VcsCommandExecutor vce = task.getExecutor();
        this.files = vce.getFiles();
        Hashtable vars = vce.getVariables();        
        this.rootDir = new File((String)vars.get("ROOTDIR"));
        //actFilePath = ""+vars.get("WORKDIR")+vars.get("FILE");
        //actFilePath = Variables.expand(vars, actFilePath, false);
        String commandName = findDisplayName(this.task);
        String title;
        if (files.size() == 1) {
            String filePath = (String) files.iterator().next();
            File file = new File(filePath);
            title = java.text.MessageFormat.format(
                NbBundle.getBundle(OutputVisualizer.class).getString("CommandOutputVisualizer.title_one"), // NOI18N
                new Object[] { file.getName(), commandName });
        }
        else title = java.text.MessageFormat.format(
            NbBundle.getBundle(OutputVisualizer.class).getString("CommandOutputVisualizer.title_many"), // NOI18N
            new Object[] { Integer.toString(files.size()), commandName });

        setName(commandName);
        setDisplayName(title);
    }
    
    /**
     *Searchs tasks structure for valid displayName
     */
    private String findDisplayName(CommandTask task){
        String dispName = null;       
        while(dispName == null && task != null){
            dispName = task.getDisplayName();
            task = CommandProcessor.getInstance().getParentTask(task);            
        }
        if(dispName == null)dispName = task.getName();
        return Actions.cutAmpersand(dispName);
    }
        
    protected final CommandTask getVcsTask(){
        return task;
    }
    
    protected final VcsCommandsProvider getCommandsProvider() {
        return cmdProvider;
    }
    
    /** @return false to open immediatelly.
     */
    public boolean openAfterCommandFinish() {
        return true;
    }
    
    /**
     * Open the component on the given workspace.
     */
    public void open(Workspace workspace) {
        if (exit != 0)
            return;
        if (outputPanel == null)
            this.initComponents();
        super.open(workspace);
        requestFocus();
    }
    
    
    /**
     * Disable serialization.
     * @return null
     */
    protected Object writeReplace() throws java.io.ObjectStreamException {
        close();
        return null;
    }
    
    /**
     * Receive a line of standard output.
     */
    public void stdOutputLine(final String line) {
        
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        
    }
    
    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        
    }
    
    public void setExitStatus(int exit) {
        this.exit = exit;
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
        synchronized (closeListeners) {
            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                TopComponentCloseListener l = (TopComponentCloseListener) it.next();
                l.closing();
            }
            closeListeners.clear();
        }
    }    
 
}
