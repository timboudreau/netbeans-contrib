/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.ui;

import java.io.IOException;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author eu155513
 */
public class ProfilerActions {
    public static Action profileMainProject() {
        final Action a = MainProjectSensitiveActions.mainProjectSensitiveAction(new ProjectActionPerformer() {
                public boolean enable(final Project project) {

                    // No projects opened => disable action
                    if (OpenProjects.getDefault().getOpenProjects().length == 0) {
                        return false;
                    }

                    // No main project set => enable action (see Issue 116619)
                    if (project == null) {
                        return true;
                    }

                    // Check if project type is supported, eventually return null
                    return true;//isProjectTypeSupported(project);
                }

                public void perform(final Project project) {
                    profileProject(project);
                }
            }, "Profile Main Project", // NOI18N
                                                                                null);
        a.putValue("iconBase", // NOI18N
                   "org/netbeans/modules/cnd/profiler/resources/leaf.png" // NOI18N
        );
        a.putValue(Action.SMALL_ICON,
                   new ImageIcon(Utilities.loadImage("org/netbeans/modules/cnd/profiler/resources/leaf.png")) //NOI18N
        );

        return a;
    }
    
    private static final String PROFILING_FOLDER_NAME = "profiling";
    
    private static void profileProject(Project project) {
        // gprof provider implementation
        
        // 1) build with -pg
        //ConfigurationSupport.getProjectDescriptor(project).getConfs().getActive();
        
        // 2) run the project
        ActionProvider ap = project.getLookup().lookup(ActionProvider.class);
        if (ap == null) {
            return; // fail early
        }
        ap.invokeAction("run", Lookup.EMPTY);
        
        // 3) wait for completion and prepare/open gprof results
        FileObject projectDir = project.getProjectDirectory();
        try {
            // create profiling folder if needed
            FileObject profilingDir = projectDir.getFileObject(PROFILING_FOLDER_NAME);
            if (profilingDir == null) {
                profilingDir = projectDir.createFolder(PROFILING_FOLDER_NAME);
            }
            
            // execute gprof on gmon.out
            FileObject gmon = projectDir.getFileObject("gmon.out");
            if (gmon == null) {
                return;
            }
            Runtime rt = Runtime.getRuntime();
            try {
                FileObject resFile = profilingDir.createData(String.valueOf(System.currentTimeMillis()));
                Process proc = rt.exec("ggprof -b " + gmon.getPath() + " > " + resFile.getPath());
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
