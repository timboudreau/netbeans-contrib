/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.ui;

import java.io.File;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.cnd.profiler.providers.GprofFactory;
import org.netbeans.modules.cnd.profiler.providers.ProfilerProvider;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

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
        ProfilerProvider profilerProvider = new GprofFactory().createProvider(Lookups.singleton(project));
        profilerProvider.prepare();
        profilerProvider.run();
        
        // (on finish) notify ui and open results
        PresentationTopComponent tc = PresentationTopComponent.findInstance();
        tc.open();
        tc.requestActive();
        tc.showResults(new File("/" + project.getProjectDirectory().getPath(), "gp_res"));
    }
}
