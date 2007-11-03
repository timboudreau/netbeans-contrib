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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * ThemeBuilderViewProvider.java
 *
 * Created on February 12, 2007, 4:57 PM
 */

package org.netbeans.modules.themebuilder.project.view;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.themebuilder.project.ThemeBuilderProject;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Theme Builder Project Logical View
 * @author Winston Prakash
 * @version 1.0
 */
public final class ThemeBuilderViewProvider implements LogicalViewProvider{
    
    ThemeBuilderProject themeBuilderProject;
    
    /**
     *
     * @param project
     */
    public ThemeBuilderViewProvider(Project project) {
        themeBuilderProject = (ThemeBuilderProject)project;
    }
    
    /**
     *
     * @return
     */
    public Node createLogicalView(){
        try     {
            return new ThemeBuilderViewProvider.ThemeBuilderRootNode(themeBuilderProject);
        } catch (DataObjectNotFoundException ex) {
            Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                    ex.getMessage(), ex);
            //Fallback - the directory couldn't be created -
            //read-only filesystem or something evil happened
            return new AbstractNode(Children.LEAF);
        }
    }
    
    /**
     *
     * @param node
     * @param object
     * @return
     */
    public Node findPath(Node root, Object target) {
        Project project = root.getLookup().lookup(Project.class);
        if (project == null) {
            return null;
        }
        
        if (target instanceof FileObject) {
            FileObject fo = (FileObject) target;
            Project owner = FileOwnerQuery.getOwner(fo);
            if (!project.equals(owner)) {
                return null; // Don't waste time if project does not own the fo
            }
        }
        
        return null;
    }
    
    private static final class ThemeBuilderRootNode extends AbstractNode {
        final ThemeBuilderProject project;
        public ThemeBuilderRootNode(ThemeBuilderProject project)
                throws DataObjectNotFoundException {
            // Must set project in the Node Lookups. Needed by ProjectSensitiveActions
            // and CommonProjectActions to find the Action Provider via project lookup
            super(new ThemeBuilderRootChildren(project), Lookups.singleton(project));
            this.project = project;
        }
        
        /**
         *  Return the supported actions.
         *  The actions are invoked via Action Provider
         *  Enabling and disabling are also done via Action Provider
         */
        public Action[] getActions( boolean context ) {
            ResourceBundle bundle = NbBundle.getBundle(ThemeBuilderViewProvider.class);
            
            List<Action> actions = new ArrayList<Action>();
            actions.add(CommonProjectActions.newFileAction());
            actions.add(null);
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_BUILD, bundle.getString("LBL_BuildAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_CLEAN, bundle.getString("LBL_CleanAction_Name"), null)); // NOI18N
            actions.add(null);
            actions.add(CommonProjectActions.setAsMainProjectAction());
            actions.add(CommonProjectActions.openSubprojectsAction());
            actions.add(CommonProjectActions.closeProjectAction());
            actions.add(null);
            actions.add(CommonProjectActions.renameProjectAction());
            actions.add(CommonProjectActions.moveProjectAction());
            actions.add(CommonProjectActions.copyProjectAction());
            actions.add(CommonProjectActions.deleteProjectAction());
            actions.add(null);
            actions.add(SystemAction.get(FindAction.class));
            actions.add(null);
            /*FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Projects/Actions"); // NOI18N
            if (fo != null) {
                for (Object next : new FolderLookup(DataFolder.findFolder(fo)).getLookup().lookupAll(Object.class)) {
                    if (next instanceof Action) {
                        actions.add((Action) next);
                    } else if (next instanceof JSeparator) {
                        actions.add(null);
                    }
                }
            }*/
            actions.add(null);
            actions.add(SystemAction.get(ToolsAction.class));
            actions.add(null);
            
            actions.add(CommonProjectActions.customizeProjectAction());
            return actions.toArray(new Action[actions.size()]);
        }
        
        public Image getIcon(int type) {
            return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/themebuilder.png");
        }
        
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
        
        public String getDisplayName() {
            return project.getProjectDirectory().getName();
        }
        
    }
    
    private static final class ThemeBuilderRootChildren extends Children.Keys {
        
        private ThemeBuilderProject themeProject;
        ThemeBuilderRootChildren(ThemeBuilderProject project){
            List<FileObject> folders = new ArrayList<FileObject>();
            //folders.add(project.getProjectDirectory());
            folders.add(project.getCssFolder());
            folders.add(project.getImagesFolder());
            folders.add(project.getJavaScriptFolder());
            folders.add(project.getMessagesFolder());
            setKeys(folders);
            themeProject = project;
        }
        
        protected Node[] createNodes(Object key) {
            if(key instanceof FileObject) {
                try{
                    FileObject folder = (FileObject) key;
                    
                    //Get the DataObject that represents it
                    DataFolder dataObject = DataFolder.findFolder(folder);
                    
                    //Get its default node - we'll wrap our node around it to change the
                    //display name, icon, etc.
                    Node folderNode = dataObject.getNodeDelegate();
                    
                    //This FilterNode will be our project node
                    return new Node[] {new FolderNode(folderNode, themeProject, folder)};
                } catch (DataObjectNotFoundException donfe) {
                    ErrorManager.getDefault().notify(donfe);
                    //Fallback - the directory couldn't be created -
                    //read-only filesystem or something evil happened
                    return new Node[] {new AbstractNode(Children.LEAF)};
                }
            }
            return new Node[0];
        }
    }
    
    
    /** This is the node you actually see in the project tab for the project */
    private static final class FolderNode extends FilterNode {
        final ThemeBuilderProject project;
        final FileObject fileObject;
        public FolderNode(Node node, ThemeBuilderProject project, FileObject fobj)
                throws DataObjectNotFoundException {
            super(node, new FilterNode.Children(node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup(new Lookup[] { Lookups.singleton(project), node.getLookup() }));
            this.project = project;
            fileObject = fobj;
        }
        
        public Image getIcon(int type) {
            if (fileObject == project.getCssFolder()){
                return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/css.png");
            }else if (fileObject == project.getImagesFolder()){
                return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/images.png");
            }else if (fileObject == project.getJavaScriptFolder()){
                return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/javascript.png");
            }else if (fileObject == project.getMessagesFolder()){
                return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/messages.png");
            }else{
                return Utilities.loadImage("org/netbeans/modules/themebuilder/resources/default.png");
            }
        }
        
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
        
        public String getDisplayName() {
            return fileObject.getName();
        }
    }
    
    
}
