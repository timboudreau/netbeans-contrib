/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.nodejs.libraries.LibrariesPanel;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.actions.FileSystemAction;
import org.openide.actions.FindAction;
import org.openide.actions.PasteAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class ProjectRootNode extends AbstractNode {

    private final NodeJSProject project;

    ProjectRootNode(NodeJSProject project) {
        super(NodeFactorySupport.createCompositeChildren(project,
                "Project/NodeJS/Nodes"),
                Lookups.singleton(project));
        this.project = project;
        setIconBaseWithExtension("org/netbeans/modules/nodejs/resources/logo.png");
        super.setName(ProjectUtils.getInformation(project).getDisplayName());
    }

    public Action[] getActions(boolean ignored) {
        final ResourceBundle bundle =
                NbBundle.getBundle(ProjectRootNode.class);

        List<Action> actions = new ArrayList<Action>();

        actions.add(CommonProjectActions.newFileAction());
        actions.add(null);
        actions.add(ProjectSensitiveActions.projectCommandAction(
                ActionProvider.COMMAND_RUN,
                bundle.getString("LBL_RunAction_Name"), null)); // NOI18N
        actions.add(null);
        actions.add(CommonProjectActions.setAsMainProjectAction());
        actions.add(null);
        actions.add(ProjectSensitiveActions.projectCommandAction(
                NodeJSProject.MAIN_FILE_COMMAND,
                bundle.getString("LBL_ChooseMainFile_Name"), null)); // NOI18N
        actions.add(null);
//        actions.add(ProjectSensitiveActions.projectCommandAction(
//                NodeJSProject.LIBRARIES_COMMAND,
//                bundle.getString("LBL_AddLibrary_Name"), null));
        actions.add(new AbstractAction() {

            {
                putValue(NAME, bundle.getString("LBL_AddLibrary_Name"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                LibrariesPanel pn = new LibrariesPanel(project);
                DialogDescriptor dd = new DialogDescriptor(pn, NbBundle.getMessage(NodeJSProject.class, "SEARCH_FOR_LIBRARIES"));
                if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION)) {
                    final Set<String> libraries = new HashSet<String>(pn.getLibraries());
                    if (libraries.size() > 0) {
                        final AtomicInteger jobs = new AtomicInteger();
                        RequestProcessor.getDefault().post(new Runnable() {

                            @Override
                            public void run() {
                                final ProgressHandle h = ProgressHandleFactory.createHandle(NbBundle.getMessage(ProjectRootNode.class,
                                        "MSG_RUNNING_NPM", libraries.size(), project.getDisplayName())); //NOI18N
                                final int totalLibs = libraries.size();
                                try {
                                    h.start(totalLibs * 2);
                                    for (String lib : libraries) {
                                        int job = jobs.incrementAndGet();
                                        ExternalProcessBuilder epb = new ExternalProcessBuilder("npm").addArgument("install").addArgument(lib).workingDirectory(FileUtil.toFile(project.getProjectDirectory()));
                                        final String libraryName = lib;
                                        ExecutionDescriptor des = new ExecutionDescriptor().controllable(true).showProgress(true).showSuspended(true).frontWindow(false).controllable(true).optionsPath("Advanced/Node").postExecution(new Runnable(){
                                            @Override
                                            public void run() {
                                                int ct = jobs.decrementAndGet();
                                                if (ct == 0) {
                                                    h.finish();
                                                    project.getProjectDirectory().refresh();
                                                    FileObject fo = project.getProjectDirectory().getFileObject("node_modules"); //NOI18N
                                                    if (fo != null) {
                                                        fo.refresh();
                                                    }
                                                    Children ch = NodeFactorySupport.createCompositeChildren(project, "Project/NodeJS/Nodes"); //NOI18N
                                                    setChildren(ch);
                                                } else {
                                                    h.progress(NbBundle.getMessage(ProjectRootNode.class, "PROGRESS_LIBS_REMAINING", totalLibs - ct), totalLibs - ct); //NOI18N
                                                    h.setDisplayName(libraryName);
                                                }
                                            }
                                        }).charset(Charset.forName("UTF-8")).frontWindowOnError(true);
                                        ExecutionService service = ExecutionService.newService(epb, des, lib);
                                        service.run();
                                    }
                                } finally {
                                    h.finish();
                                }
                            }
                        });
                    }
                }
            }
        });
        actions.add(null);
        actions.add(CommonProjectActions.setAsMainProjectAction());
        actions.add(CommonProjectActions.closeProjectAction());
        actions.add(null);
        actions.add(CommonProjectActions.renameProjectAction());
        actions.add(CommonProjectActions.moveProjectAction());
        actions.add(CommonProjectActions.copyProjectAction());
        actions.add(CommonProjectActions.deleteProjectAction());
        actions.add(null);
        actions.add(SystemAction.get(FindAction.class));
        actions.add(null);
        actions.add(SystemAction.get(PasteAction.class));
        actions.add(null);
        actions.add(getFilesystemAction());
        actions.add(Lookups.forPath("Project/NodeJS/Actions").lookup(Action.class));
        actions.add(new AbstractAction() {

            {
                putValue(NAME, NbBundle.getMessage(ProjectRootNode.class, "PROPERTIES"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                new PropertiesPanel(project.getLookup().lookup(NodeJSProjectProperties.class)).showDialog();
            }
        });
        return actions.toArray(new Action[actions.size()]);
    }
    
    private Action getFilesystemAction() {
        FileSystemAction a = SystemAction.get(FileSystemAction.class);
        try {
            Node n = DataObject.find(project.getProjectDirectory()).getNodeDelegate();
            return a.createContextAwareInstance(n.getLookup());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return a;
        }
    }

    @Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        try {
            Node n = DataObject.find(project.getProjectDirectory()).getNodeDelegate();
            s.addAll(Arrays.asList(n.getPasteTypes(t)));
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
