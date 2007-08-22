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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
        
        final RerunAction rerunActionFin = rerunAction;
        final InputOutput inoutFin = inout;
        
        try {
            class Exec implements Runnable {
                public void run() {
                    LifecycleManager.getDefault().saveAll();
                    doBuild();
                }

                private void doBuild() {
                    if (COMMAND_CLEAN.equals(command) || COMMAND_REBUILD.equals(command)) {
                        BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());
                        
                        rerunActionFin.runAndRemember(project, new CleanBuilder(conf), inoutFin);
                        return ;
                    }
                    
                    if (COMMAND_BUILD.equals(command) || COMMAND_REBUILD.equals(command) || LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
                        BuildConfiguration conf = Utilities.getBuildConfigurationProvider(project).getBuildConfiguration(ProjectSettings.getDefault(project).getBuildConfigurationName());
                        
                        rerunActionFin.runAndRemember(project, conf, inoutFin);
                        return ;
                    }
                    
                    if (LaTeXGUIProject.COMMAND_SHOW.equals(command)) {
                        ShowConfiguration conf = Utilities.getBuildConfigurationProvider(project).getShowConfiguration(ProjectSettings.getDefault(project).getShowConfigurationName());
                        
                        rerunActionFin.runAndRemember(project, conf, inoutFin);
                        return ;
                    }
                }
            }
            
            ExecutionEngine.getDefault().execute(name, new Exec(), inout);
        } finally {
            synchronized (ActionsFactory.class) {
                inout.getOut().close();
                inout.getErr().close();

                freeTabs.put(inout, name);
            }
        }
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
        private Builder toRepeat;
        private InputOutput inout;
        
        public RerunAction() {
            putValue(SMALL_ICON, new ImageIcon(ActionsFactory.class.getResource("/org/netbeans/modules/latex/guiproject/resources/rerun.png")));
            putValue(SHORT_DESCRIPTION, "Rerun build");
        }

        public void actionPerformed(ActionEvent e) {
            try {
                inout.getOut().reset();
                inout.select();

                if (toRepeat.build(project, inout)) {
                    inout.getOut().println("Build passed.");
                } else {
                    inout.getOut().println("Build failed, more info should be provided above.");
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        void runAndRemember(LaTeXGUIProject project, Builder toRepeat, InputOutput inout) {
            this.project = project;
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
