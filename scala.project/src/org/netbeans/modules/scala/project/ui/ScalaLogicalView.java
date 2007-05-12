package org.netbeans.modules.scala.project.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.scala.project.ScalaActions;
import org.netbeans.modules.scala.project.ScalaProject;
import org.netbeans.modules.scala.project.Util;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 * @author Martin Krauskopf
 */
public final class ScalaLogicalView implements LogicalViewProvider {
    
    private final ScalaProject project;
    
    public ScalaLogicalView(final ScalaProject project) {
        this.project = project;
    }
    
    public Node createLogicalView() {
        return new ScalaRootNode(project);
    }
    
    public Node findPath(final Node root, final Object target) {
        Node result = null;
        if (root.getLookup().lookup(ScalaProject.class) != project) {
            // Not intended for this project. Should not normally happen anyway.
            return null;
        }
        
        if (target instanceof FileObject) {
            FileObject fo = (FileObject) target;
            Project owner = FileOwnerQuery.getOwner(fo);
            if (project.equals(owner)) {
                Node[] nodes = root.getChildren().getNodes(true);
                for (int i = 0; i < nodes.length; i++) {
                    result = PackageView.findPath(nodes[i], target);
                    if (result != null) {
                        break;
                    }
                }
            } // else don't waste time if project does not own the fo
        }
        
        return result;
    }
    
    private static final class ScalaRootNode extends AbstractNode {
        
        private final ScalaProject project;
        
        ScalaRootNode(final ScalaProject project) {
            super(new RootChildren(project), Lookups.singleton(project));
            this.project = project;
            setIconBaseWithExtension(ScalaProject.SCALA_PROJECT_ICON_PATH);
            setDisplayName(ProjectUtils.getInformation(project).getDisplayName());
            setShortDescription(NbBundle.getMessage(ScalaLogicalView.class, "HINT_project_root_node",
                    FileUtil.getFileDisplayName(project.getProjectDirectory())));
        }
        
        public Action[] getActions(boolean context) {
            return ScalaActions.getProjectActions(project);
        }
        
    }
    
    private static final class RootChildren extends Children.Keys<Object> implements ChangeListener {
        
        private final ScalaProject project;
        
        private static final String BUILD_SCRIPT = "build.xml"; // NOI18N
        
        RootChildren(ScalaProject project) {
            this.project = project;
        }
        
        protected void addNotify() {
            super.addNotify();
            refreshKeys();
            ProjectUtils.getSources(project).addChangeListener(this);
        }
        
        private void refreshKeys() {
            List<Object> keys = new ArrayList<Object>();
            SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            keys.addAll(Arrays.asList(groups));
            FileObject buildScript = project.getProjectDirectory().getFileObject(BUILD_SCRIPT);
            if (buildScript != null) {
                keys.add(buildScript);
            }
            setKeys(keys);
        }
        
        protected void removeNotify() {
            ProjectUtils.getSources(project).removeChangeListener(this);
            setKeys(Collections.<Object>emptySet());
            super.removeNotify();
        }
        
        protected Node[] createNodes(final Object key) {
            Node n;
            if (key instanceof SourceGroup) {
                n = PackageView.createPackageView((SourceGroup) key);
            } else if (key instanceof FileObject) {
                try {
                    FileObject fo = (FileObject) key;
                    Node orig = DataObject.find(fo).getNodeDelegate();
                    String displayName = fo.getNameExt().equals(BUILD_SCRIPT) ?
                        NbBundle.getMessage(ScalaLogicalView.class, "LBL_build.xml") : null;
                    return new Node[] { new SpecialFileNode(orig, displayName) };
                } catch (DataObjectNotFoundException e) {
                    throw new AssertionError(e);
                }
            } else {
                throw new AssertionError("Unknown key: " + key);
            }
            return new Node[] { n };
        }
        
        public void stateChanged(final ChangeEvent e) {
            refreshKeys();
        }
        
    }
    
    /**
     * Node to represent some special file in a project. Mostly just a wrapper
     * around the normal data node.
     */
    static final class SpecialFileNode extends FilterNode {
        
        private final String displayName;
        
        public SpecialFileNode(final Node orig, final String displayName) {
            super(orig);
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return (displayName == null) ? super.getDisplayName() : displayName;
        }
        
        public boolean canRename() {
            return false;
        }
        
        public boolean canDestroy() {
            return false;
        }
        
        public boolean canCut() {
            return false;
        }
        
        public String getHtmlDisplayName() {
            String result = null;
            DataObject dob = (DataObject) getLookup().lookup(DataObject.class);
            if (dob != null) {
                Set<FileObject> files = dob.files();
                result = computeAnnotatedHtmlDisplayName(getDisplayName(), files);
            }
            return result;
        }
        
    }
    
    /**
     * Annotates <code>htmlDisplayName</code>, if it is needed, and returns the
     * result; <code>null</code> otherwise.
     */
    private static String computeAnnotatedHtmlDisplayName(
            final String htmlDisplayName, final Set<FileObject> files) {
        
        String result = null;
        if (files != null && files.iterator().hasNext()) {
            try {
                FileObject fo = files.iterator().next();
                FileSystem.Status stat = fo.getFileSystem().getStatus();
                if (stat instanceof FileSystem.HtmlStatus) {
                    FileSystem.HtmlStatus hstat = (FileSystem.HtmlStatus) stat;
                    
                    String annotated = hstat.annotateNameHtml(htmlDisplayName, files);
                    
                    // Make sure the super string was really modified (XXX why?)
                    if (!htmlDisplayName.equals(annotated)) {
                        result = annotated;
                    }
                }
            } catch (FileStateInvalidException e) {
                Util.LOG.log(Level.FINE, "Problem with annotating display name", e);
            }
        }
        return result;
    }
    
}
