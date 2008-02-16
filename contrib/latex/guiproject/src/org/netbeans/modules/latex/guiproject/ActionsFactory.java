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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.build.BuildConfiguration;
import org.netbeans.modules.latex.guiproject.build.Builder;
import org.netbeans.modules.latex.guiproject.build.ShowConfiguration;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.LifecycleManager;
import org.openide.awt.StatusDisplayer;
import org.openide.execution.ExecutionEngine;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Jan Lahoda
 */
public class ActionsFactory implements ActionProvider {
    
    private LaTeXGUIProject project;
    
    /** Creates a new instance of ActionsFactory */
    public ActionsFactory(LaTeXGUIProject project) {
        this.project = project;
    }
    
    public String[] getSupportedActions() {
        return new String[] {
            COMMAND_BUILD,
            COMMAND_CLEAN,
            COMMAND_REBUILD,
            LaTeXGUIProject.COMMAND_SHOW
        };
    }

    private static Map<String, String> command2DisplayName;

    static {
        command2DisplayName = new HashMap<String, String>();

        command2DisplayName.put(COMMAND_CLEAN, "clean");
        command2DisplayName.put(COMMAND_REBUILD, "rebuild");
        command2DisplayName.put(COMMAND_BUILD, "build");
        command2DisplayName.put(LaTeXGUIProject.COMMAND_SHOW, "show");
    }
    
    private static final Map<InputOutput, RerunAction> rerunActions = new WeakHashMap<InputOutput, RerunAction>();
    private static final Map<InputOutput, String> freeTabs = new WeakHashMap<InputOutput, String>();
    
    public void invokeAction(final String command, Lookup context) throws IllegalArgumentException {
        String name = ProjectUtils.getInformation(project).getDisplayName() + "(" + command2DisplayName.get(command) + ")";
        RerunAction rerunAction = null;
        InputOutput inout = null;
    
        synchronized (ActionsFactory.class) {
            for (Entry<InputOutput, String> tab : freeTabs.entrySet()) {
                if (name.equals(tab.getValue())) {
                    inout = tab.getKey();
                    rerunAction = rerunActions.get(inout);
                    rerunAction.setEnabled(false);
                    freeTabs.remove(inout);
                    break;
                }
            }
        
            if (inout == null) {
                rerunAction = new RerunAction();
                inout = IOProvider.getDefault().getIO(name, new Action[]{rerunAction});
                rerunActions.put(inout, rerunAction);
            }
        }
        
        List<Builder> builders = new LinkedList<Builder>();
        
        if (COMMAND_CLEAN.equals(command) || COMMAND_REBUILD.equals(command)) {
            BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());

            builders.add(new CleanBuilder(conf));
        }

        if (COMMAND_BUILD.equals(command) || COMMAND_REBUILD.equals(command) || LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
            BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());

            builders.add(conf);
        }

        if (LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
            ShowConfiguration conf = Utilities.getBuildConfigurationProvider(project).getShowConfiguration(ProjectSettings.getDefault(project).getShowConfigurationName());

            builders.add(conf);
        }

        rerunAction.runAndRemember(project, name, builders, inout);
    }
    
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (COMMAND_CLEAN.equals(command) || COMMAND_REBUILD.equals(command)) {
            BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());
            
            if (conf == null || !conf.isSupported(project))
                return false;
        }
        
        if (COMMAND_BUILD.equals(command) || COMMAND_REBUILD.equals(command) || LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
            BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());
            
            if (conf == null || !conf.isSupported(project))
                return false;
        }
        
        if (LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
            ShowConfiguration conf = Utilities.getBuildConfigurationProvider(project).getShowConfiguration(ProjectSettings.getDefault(project).getShowConfigurationName());
            
            if (conf == null || !conf.isSupported(project))
                return false;
        }
        return true;
    }

    private static File findLideClient() {
        return InstalledFileLocator.getDefault().locate("modules/bin/lide-editor-client", null, false);
    }
    
    public static Action createShowAction() {
        return ProjectSensitiveActions.projectCommandAction(LaTeXGUIProject.COMMAND_SHOW, "Show Project Resulting Document", null);
    }
    
    public static Action createMainProjectShowAction() {
        return MainProjectSensitiveActions.mainProjectCommandAction(LaTeXGUIProject.COMMAND_SHOW, "Show Main Project Resulting Document", null);
    }

    private static final class RerunAction extends AbstractAction {

        private LaTeXGUIProject project;
        private List<Builder> toRepeat;
        private InputOutput inout;
        private String name;
        
        public RerunAction() {
            putValue(SMALL_ICON, new ImageIcon(ActionsFactory.class.getResource("/org/netbeans/modules/latex/guiproject/resources/rerun.png")));
            putValue(SHORT_DESCRIPTION, "Rerun build");
        }

        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            class Exec implements Runnable {
                public void run() {
                    InputOutput inout;
                    synchronized (RerunAction.this) {
                        inout = RerunAction.this.inout;
                    }
                    LifecycleManager.getDefault().saveAll();

                    try {
                        
                        inout.getOut().reset();
                        inout.select();

                        boolean succeeded = false;

                        for (Builder b : toRepeat) {
                            succeeded = b.build(project, inout);

                            if (!succeeded) {
                                break;
                            }
                        }

                        if (succeeded) {
                            inout.getOut().println("Build passed.");
                            StatusDisplayer.getDefault().setStatusText("Build passed.");
                        } else {
                            inout.getOut().println("Build failed, more info should be provided above.");
                            StatusDisplayer.getDefault().setStatusText("Build failed.");
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        inout.getOut().close();
                        inout.getErr().close();
                        synchronized (ActionsFactory.class) {
                            freeTabs.put(inout, name);
                        }
                        
                        setEnabled(true);
                    }
                }
            }
            
            ExecutionEngine.getDefault().execute(name, new Exec(), inout);
        }
        
        synchronized void runAndRemember(LaTeXGUIProject project, String name, List<Builder> toRepeat, InputOutput inout) {
            this.project = project;
            this.name = name;
            this.toRepeat = toRepeat;
            this.inout = inout;
            
            actionPerformed(null);
        }
    }
    
    private static final class CleanBuilder implements Builder {

        private BuildConfiguration bc;

        public CleanBuilder(BuildConfiguration bc) {
            this.bc = bc;
        }
        
        public boolean build(LaTeXGUIProject p, InputOutput inout) {
            return bc.clean(p, inout);
        }
        
    }
    
}
