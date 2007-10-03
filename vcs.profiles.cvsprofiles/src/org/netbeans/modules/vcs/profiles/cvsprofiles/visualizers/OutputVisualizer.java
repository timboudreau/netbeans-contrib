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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.UserCommandTask;
import org.netbeans.modules.vcscore.commands.CommandOutputCollector;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.ProvidedCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedTask;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 * The default visualizer of command output.
 * @author  Richard Gregor
 */

public abstract class OutputVisualizer implements VcsCommandVisualizer {
    
    private Map outputMap;
    private ArrayList closeListeners = new ArrayList();
    private CommandTask task;
    private VcsCommandsProvider cmdProvider;    
    protected Collection files;
    protected File rootDir;
    protected String commonParentStr;
    private java.awt.event.ActionListener killListener = null;
    private int exit;
    private Vector vcsTopComponents;
    private boolean opened = false;
    protected String commandName ;
    private CommandOutputCollector outputCollector;
    /*indicates whether visualizer should be finished imediately or if it should wait while parent task finishes #40113*/
    protected boolean finishVisualizer = true;
    private VcsCommandExecutor vce;
    
    private static final long serialVersionUID = -8901790321334731232L;
    
    public abstract Map getOutputPanels();

    protected String getMode() {
        return "editor";
    }
    
    public void open(Wrapper w) {
        if (w != null) {
            // This will probably not ever happen.
            // w != null is currently used only for EDIT and LOCK commands.
            outputMap = getOutputPanels();
            if (outputMap == null)
                return;
            Iterator it = outputMap.keySet().iterator();
            if (outputMap.size() == 1) {
                String filePath = (String) it.next();
                JComponent component = (JComponent) outputMap.get(filePath);
                component.putClientProperty("wrapper-title", getFileName(filePath)+"["+commandName+"]");
                component.setToolTipText(filePath);
                w.wrap(component, true, true);
            } else {
                JTabbedPane tabbs = new JTabbedPane();
                while(it.hasNext()){
                    String filePath = (String)it.next();
                    JComponent component = (JComponent)outputMap.get(filePath);
                    component.setToolTipText(filePath);
                    tabbs.add(getFileName(filePath)+"["+commandName+"]", component);
                }
                w.wrap(tabbs, true, true);
            }
        } else {
            open();
        }
        opened = true;
    }
    
    public void open() {
        outputMap = getOutputPanels();
        if (outputMap == null)
            return;
        Iterator it = outputMap.keySet().iterator();
        while (it.hasNext()) {
            String filePath = (String)it.next();
            JComponent component = (JComponent)outputMap.get(filePath);
            OutputTopComponent out = new OutputVisualizer.OutputTopComponent();
            out.setOutputPanel(component);
            out.setFileName(getFileName(filePath));
            out.setToolTipText(filePath);
            out.open(WindowManager.getDefault().getCurrentWorkspace());
        }
        opened = true;
    }
    
    private static String getFileName(String filePath) {
        String fileName = filePath;
        int nameIndex = filePath.lastIndexOf('/');
        if (nameIndex < 0) nameIndex = filePath.lastIndexOf(File.separatorChar);
        if (nameIndex >= 0) {
            fileName = filePath.substring(nameIndex + 1);
        }
        return fileName;
    }
    
    
    public void setVcsTask(VcsDescribedTask task) {
        this.task = (CommandTask) task;
        cmdProvider = ((ProvidedCommand) task).getProvider();
        vce = task.getExecutor();
        this.files = vce.getFiles();
        Hashtable vars = vce.getVariables();        
        String rootDirPath = (String)vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        if (module != null && module.length() > 0) {
            rootDirPath = rootDirPath + File.separator + module;
            this.commonParentStr = module;
            String commonParent = (String) vars.get("COMMON_PARENT");
            if (commonParent != null && commonParent.length() > 0) {
                rootDirPath = org.netbeans.modules.vcscore.VcsFileSystem.substractRootDir(rootDirPath, commonParent);
            }
        } else {
            this.commonParentStr = "";
        }
        this.rootDir = new File(rootDirPath);        
        commandName = findDisplayName(this.task);
        //in case there is parent visualizer preferred don't exit visualizer, wait while parent task finishes 
        String useParent;
        if ((useParent = (String) vce.getVariables().get(UserCommandTask.VAR_USE_PARENT_VISUALIZER)) != null && useParent.length() > 0) {
            UserCommandTask parentTask = (UserCommandTask) CommandProcessor.getInstance().getParentTask((CommandTask)task);
            if (parentTask != null && finishVisualizer) {
                finishVisualizer = false;                
                parentTask.addTaskListener(new FinishListener());
            }
        }
    }
        
    public void setOutputCollector(CommandOutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }
    
    protected CommandOutputCollector getOutputCollector() {
        return outputCollector;
    }
    
    public void setPossibleFileStatusInfoMap(java.util.Map infoMap) {
        // Left unimplemented, we do not care about possible statii here, subclasses can override
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
        if(!finishVisualizer)
            return;
        this.exit = exit;
    }
    
    public void addCloseListener(TopComponentCloseListener l) {
        synchronized (closeListeners) {
            closeListeners.add(l);
        }
    }
    
    
    /**
     * Tell, whether the visualizer is currently opened.
     * This method is used to decide whether <code>open()</code>
     * should be called or not.
     */
    public boolean isOpened(){
        return opened;
    }
    
    /**
     * Request the focus for this visualizer. See {@link org.openide.windows.TopComponent#requestFocus}.
     */
    public void requestFocus(){
        
    }
    
    /*
     *Listener responsible for termination i case parent visualizer is used #40113
     */
    final class FinishListener implements TaskListener{
        public void taskFinished(Task task){            
            finishVisualizer = true;
            UserCommandTask utask = (UserCommandTask) task;
            setExitStatus(utask.getExitStatus());
        }
    }
    
    final class OutputTopComponent extends TopComponent{
        
        private JComponent outputPanel;
        private String fileName;
        private static final long serialVersionUID = -7801790121334731232L;
        
        public OutputTopComponent(){        
            setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));                                   
            initAccessibility();
        }        
        
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        } 
        
        protected String preferredID(){
            return "OutputVisualizerOutputTopComponent";    //NOI18N
        }
        
        void setOutputPanel(JComponent outputPanel){
            this.outputPanel = outputPanel;
        }
        
        void setFileName(String fileName){
            setName(fileName+"["+commandName+"]");
            setDisplayName(fileName+"["+commandName+"]");
        }
        
        private void initComponents(){
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
        /**
         * Open the component on the given workspace.
         */
        public void open(Workspace workspace) {    
            if (exit != 0)
                return;
            this.initComponents();
            Mode mode = WindowManager.getDefault().findMode(this);            
            if (mode == null) {
                mode = WindowManager.getDefault().findMode(getMode()); // NOI18N              
                if (mode != null)
                    mode.dockInto(this);
            }
            super.open(workspace);
            super.requestActive();
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
        

    }
}
