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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.validation.adapters.DialogBuilder;
import org.netbeans.api.validation.adapters.DialogBuilder.DialogType;
import org.netbeans.modules.nodejs.libraries.LibrariesPanel;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOrRenameOperationImplementation;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationListener;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.CreateFromTemplateAttributesProvider;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class NodeJSProject implements Project, ProjectConfiguration, ActionProvider, Comparable<NodeJSProject>, LogicalViewProvider, ProjectInformation, PrivilegedTemplates, RecommendedTemplates, CreateFromTemplateAttributesProvider, PropertyChangeListener, MoveOrRenameOperationImplementation, DeleteOperationImplementation, CopyOperationImplementation {

    private final FileObject dir;
    private final ProjectState state;
    static final String MAIN_FILE_COMMAND = "set_main_file"; //NOI18N
    static final String PROPERTIES_COMMAND = "project_properties"; //NOI18N
    static final String LIBRARIES_COMMAND = "libs";
    static final String CLOSE_COMMAND = "close"; //NOI18N
    static final Logger LOGGER = Logger.getLogger(NodeJSProject.class.getName());
    private final ProjectMetadataImpl metadata = new ProjectMetadataImpl(this);
    private final NodeJsClassPathProvider classpath = new NodeJsClassPathProvider();
    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);
    private final Sources sources = new NodeJSProjectSources(this);
    private final Lookup lookup = Lookups.fixed(this, new NodeJSProjectProperties(this), classpath, sources);

    @SuppressWarnings("LeakingThisInConstructor")
    NodeJSProject(FileObject dir, ProjectState state) {
        this.dir = dir;
        this.state = state;
        metadata.addPropertyChangeListener(this);
    }

    ProjectState state() {
        return state;
    }

    ProjectMetadataImpl metadata() {
        return metadata;
    }

    @Override
    public FileObject getProjectDirectory() {
        return dir;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public String getDisplayName() {
        String result = getLookup().lookup(NodeJSProjectProperties.class).getDisplayName();
        if (result == null || "".equals(result)) {
            result = getProjectDirectory().getName();
        }
        return result;
    }

    @Override
    public String[] getSupportedActions() {
        return ALWAYS_ENABLED.toArray(new String[ALWAYS_ENABLED.size()]);
    }

    @Override
    public void invokeAction(String string, Lookup lkp) throws IllegalArgumentException {
        if (COMMAND_RUN.equals(string)) {
            final NodeJSExecutable exe = NodeJSExecutable.getDefault();
            FileObject main = getLookup().lookup(NodeJSProjectProperties.class).getMainFile();
            if (main == null) {
                main = showSelectMainFileDialog();
                if (main != null) {
                    NodeJSProjectProperties props = getLookup().lookup(NodeJSProjectProperties.class);
                    props.setMainFile(main);
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(NodeJSProject.class,
                            "MSG_MAIN_FILE_SET", getName(), main.getName()));
                } else {
                    return;
                }
            }
            final FileObject toRun = main;
            if (toRun != null) {
                RequestProcessor.getDefault().post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            exe.run(toRun, getLookup().lookup(NodeJSProjectProperties.class).getRunArguments());
                        } catch (IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    }
                });
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else if (MAIN_FILE_COMMAND.equals(string)) {
            FileObject main = showSelectMainFileDialog();
            if (main != null) {
                NodeJSProjectProperties props = getLookup().lookup(NodeJSProjectProperties.class);
                props.setMainFile(main);
            }
        } else if (PROPERTIES_COMMAND.equals(string)) {
        } else if (CLOSE_COMMAND.equals(string)) {
            OpenProjects.getDefault().close(new Project[]{this});
        } else if (LIBRARIES_COMMAND.equals(string)) {
            LibrariesPanel pn = new LibrariesPanel(this);
            DialogDescriptor dd = new DialogDescriptor(pn, NbBundle.getMessage(NodeJSProject.class, "SEARCH_FOR_LIBRARIES")); //NOI18N
            DialogDisplayer.getDefault().notify(dd);
        } else if (COMMAND_DELETE.equals(string)) {
            DefaultProjectOperations.performDefaultDeleteOperation(this);
        } else if (COMMAND_MOVE.equals(string)) {
            DefaultProjectOperations.performDefaultMoveOperation(this);
        } else if (COMMAND_RENAME.equals(string)) {
            String label = NbBundle.getMessage(NodeJSProject.class, "LBL_PROJECT_RENAME"); //NOI18N
            NotifyDescriptor.InputLine l = new NotifyDescriptor.InputLine(label, NbBundle.getMessage(NodeJSProject.class, "TTL_PROJECT_RENAME")); //NOI18N
            if (DialogDisplayer.getDefault().notify(l).equals(NotifyDescriptor.OK_OPTION)) {
                String txt = l.getInputText();
                Validator<String> v = Validators.merge(Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_FILENAME);
                Problems p = new Problems();
                if (!v.validate(p, label, txt)) {
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message(p.getLeadProblem().getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(msg);
                    return;
                }
                DefaultProjectOperations.performDefaultRenameOperation(this, l.getInputText());
            }
        } else if (COMMAND_COPY.equals(string)) {
            DefaultProjectOperations.performDefaultCopyOperation(this);
        } else {
            throw new AssertionError(string);
        }
    }

    private static final Set<String> ALWAYS_ENABLED = new HashSet<String>(Arrays.asList(
            LIBRARIES_COMMAND, COMMAND_DELETE, COMMAND_MOVE, COMMAND_COPY, 
            COMMAND_RENAME, MAIN_FILE_COMMAND, CLOSE_COMMAND, COMMAND_RUN));

    @Override
    public boolean isActionEnabled(String string, Lookup lkp) throws IllegalArgumentException {
        if (COMMAND_RUN.equals(string)) {
            return getLookup().lookup(NodeJSProjectProperties.class).getMainFile() != null;
        }
        boolean result = ALWAYS_ENABLED.contains(string);
        return result;
    }

    @Override
    public int compareTo(NodeJSProject t) {
        int myPathLength = getProjectDirectory().getPath().length();
        int otherPathLength = t.getProjectDirectory().getPath().length();
        return myPathLength > otherPathLength ? -1 : myPathLength < otherPathLength ? 0 : 1;
    }
    private Reference<ProjectRootNode> rn = null;

    @Override
    public Node createLogicalView() {
        ProjectRootNode n = new ProjectRootNode(this);
        synchronized (this) {
            rn = new WeakReference<ProjectRootNode>(n);
        }
        return n;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectMetadata.PROP_NAME.equals(evt.getPropertyName())) {
            ProjectRootNode n = null;
            synchronized (this) {
                if (rn != null) {
                    n = rn.get();
                }
            }
            if (n != null) {
                n.setDisplayName(evt.getNewValue() + "");
            }
        }
    }

    private static final class AllJSFiles extends ChildFactory<FileObject> implements Comparator<FileObject> {

        private final FileObject root;

        public AllJSFiles(FileObject root) {
            this.root = root;
        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            createKeys(root, toPopulate);
            Collections.sort(toPopulate, this);
            return true;
        }

        private void createKeys(FileObject fo, List<FileObject> toPopulate) {
            for (FileObject file : fo.getChildren()) {
                if (file.getExt().equals("js") && file.isData()) { //NOI18N
                    toPopulate.add(file);
                } else if (file.isFolder()) {
                    createKeys(file, toPopulate);
                }
            }
        }

        @Override
        protected Node createNodeForKey(FileObject key) {
            try {
                DataObject dob = DataObject.find(key);
                return new JSFileFilterNode(dob.getNodeDelegate());
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return Node.EMPTY;
            }
        }

        @Override
        public int compare(FileObject o1, FileObject o2) {
            if (o1.getParent() != o2.getParent()) {
                if (o1.getParent().equals(root)) {
                    return -1;
                } else if (o2.getParent().equals(root)) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                FileObject p1 = o1.getParent();
                FileObject p2 = o2.getParent();
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        }

        private final class JSFileFilterNode extends FilterNode {

            public JSFileFilterNode(Node original) {
                super(original, Children.LEAF);
            }

            public Action[] getActions(boolean ignored) {
                return new Action[0];
            }

            private String getRelativePath() {
                FileObject fo = getLookup().lookup(DataObject.class).getPrimaryFile();
                if (fo != null && !fo.getParent().equals(root)) {
                    String s = FileUtil.getRelativePath(root, fo);
                    int ix = s.lastIndexOf('/'); //NOI18N
                    if (ix > 0 && ix < s.length() - 1) {
                        s = s.substring(0, ix);
                    }
                    return s;
                }
                return null;
            }

            @Override
            public Image getIcon(int type) {
                if (getRelativePath() != null) {
                    return ImageUtilities.createDisabledImage(super.getIcon(type));
                }
                return super.getIcon(type);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getHtmlDisplayName() {
                String rp = getRelativePath();
                if (rp != null) {
                    return super.getDisplayName() + " <font color='!controlShadow'>(" + rp + ")"; //NOI18N
                }
                return super.getHtmlDisplayName();
            }
        }
    }

    FileObject showSelectMainFileDialog() {
        ExplorerPanel ep = new ExplorerPanel();
        final ExplorerManager mgr = ep.getExplorerManager();
        ChildFactory<?> kids = new AllJSFiles(getProjectDirectory());
        Children ch = Children.create(kids, true);
        Node root = new AbstractNode(ch);
        mgr.setRootContext(root);
        ValidationGroup grp = ValidationGroup.create();
        ValidationPanel pnl = new ValidationPanel(grp);
        pnl.setInnerComponent(ep);
        class X extends ValidationListener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                super.validate();
            }

            @Override
            protected boolean validate(Problems prblms) {
                Node[] selection = mgr.getSelectedNodes();
                if (selection != null && selection.length == 1) {
                    return true;
                }
                prblms.add(new Problem(NbBundle.getMessage(NodeJSProject.class, "PROBLEM_NO_MAIN_FILE"), Severity.FATAL)); //NOI18N
                return false;
            }
        }
        X x = new X();
        mgr.addPropertyChangeListener(x);
        grp.add(x);
        DialogBuilder b = new DialogBuilder(NodeJSProject.class).setModal(true).setContent(ep).setValidationGroup(grp).setTitle(NbBundle.getMessage(NodeJSProject.class, "CHOOSE_NO_MAIN_FILE")).setDialogType(DialogType.QUESTION);
        if (b.showDialog(NotifyDescriptor.OK_OPTION) && mgr.getSelectedNodes().length == 1) {
            Node n = mgr.getSelectedNodes()[0];
            FileObject fo = n.getLookup().lookup(DataObject.class).getPrimaryFile();
            return fo;
        }
        return null;
    }

    @Override
    public String[] getPrivilegedTemplates() {
        return new String[]{
                    "Templates/javascript/Empty.js", //NOI18N
                    "Templates/javascript/Module.js", //NOI18N
                    "Templates/javascript/HelloWorld.js", //NOI18N
                    "Templates/Other/javascript.js", //NOI18N
                    "Templates/Web/Xhtml.html", //NOI18N
                    "Templates/Web/Html.html", //NOI18N
                    "Templates/Web/CascadingStyleSheet.css", //NOI18N
                    "Templates/Other/json.json", //NOI18N
                    "Templates/Other/Folder", //NOI18N
                    "Templates/javscript/package.json" //NOI18N
                };
    }

    @Override
    public String[] getRecommendedTypes() {
        return new String[]{"javascript", "Other", "Web"}; //NOI18N
    }

    @Override
    public Map<String, ?> attributesFor(DataObject template, DataFolder target, String name) {
        String license = getLookup().lookup(NodeJSProjectProperties.class).getLicenseType();
        Map<String, Object> result = new HashMap<String, Object>();
        if (license != null) {
            result.put("project.license", license); //NOI18N
            result.put("license", license); //NOI18N
        }
        result.put("port", "" + DefaultExectable.get().getDefaultPort()); //NOI18N
        return result;
    }

    @Override
    public Node findPath(Node root, Object target) {
        System.out.println("FindPath " + target);
        //XXX this does not work - but what will?
        FileObject fo;
        if (target instanceof DataObject) {
            fo = ((DataObject) target).getPrimaryFile();
        } else if (target instanceof FileObject) {
            fo = (FileObject) target;
        } else {
            return null;
        }
        if (FileUtil.isParentOf(getProjectDirectory(), fo)) {
            Node n = rn == null ? null : rn.get();
            if (n != null) {
                String relPath = FileUtil.getRelativePath(getProjectDirectory(), fo);
                String[] paths = relPath.split("/"); //NOI18N
                String nm = fo.getName();
                paths[paths.length - 1] = nm;
                Node curr = n;
                for (int i=0; i < paths.length; i++) {
                    String path = paths[i];
                    curr = n.getChildren().findChild(path);
                    System.out.println("Find child " + path + " found? " + curr);
                    if (curr != null && i == paths.length - 1) {
                        return curr;
                    }
                }
                return curr;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return getProjectDirectory().getNameExt();
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon(NodeJSProject.class.getPackage().getName().replace('.', '/') + "/project.png", true);
    }

    @Override
    public Project getProject() {
        return this;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supp.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supp.removePropertyChangeListener(listener);
    }
    
    @Override
    public void notifyRenaming() throws IOException {
        DefaultExectable.get().stopRunningProcesses(this);
    }

    @Override
    public void notifyRenamed(String nueName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void notifyMoving() throws IOException {
        DefaultExectable.get().stopRunningProcesses(this);
    }

    @Override
    public void notifyMoved(Project original, File originalPath, String nueName) throws IOException {
        getLookup().lookup(NodeJSProjectProperties.class).setDisplayName(nueName);
    }

    @Override
    public List<FileObject> getMetadataFiles() {
        List<FileObject> result = new ArrayList<FileObject>();
        FileObject projectProps = getProjectDirectory().getFileObject("package.json");
        if (projectProps != null) {
            result.add(projectProps);
        }
        FileObject runProps = getProjectDirectory().getFileObject(".nbrun");
        if (runProps != null) {
            result.add(runProps);
        }
        return result;
    }

    @Override
    public List<FileObject> getDataFiles() {
        return Arrays.asList(getProjectDirectory().getChildren());
    }

    @Override
    public void notifyDeleting() throws IOException {
        DefaultExectable.get().stopRunningProcesses(this);
    }

    @Override
    public void notifyDeleted() throws IOException {
        //wipe project props?
    }

    @Override
    public void notifyCopying() throws IOException {
        //do nothing
    }

    @Override
    public void notifyCopied(Project original, File originalPath, String nueName) throws IOException {
        //do nothing
    }
}
