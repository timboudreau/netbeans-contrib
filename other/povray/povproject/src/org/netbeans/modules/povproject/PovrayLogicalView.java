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
/*
 * PovrayLogicalView.java
 *
 * Created on February 16, 2005, 5:57 PM
 */

package org.netbeans.modules.povproject;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.povproject.RendererService;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Logical view of a Povray project for the project tab in Explorer.
 * Provides a FilterNode wraps the default DataNode
 * for the project, replacing the icon.
 *
 * @author Timothy Boudreau
 */
class PovrayLogicalView implements LogicalViewProvider {
    private PovProject project;
    
    /** Creates a new instance of PovrayLogicalView */
    public PovrayLogicalView(PovProject project) {
        this.project = project;
    }

    public org.openide.nodes.Node createLogicalView() {
        try {
            //Get the scenes directory, creating if deleted
            FileObject scenes = project.getScenesFolder(true);
            
            //Get the DataObject that represents it
            DataObject scenesDataObject = 
                    DataObject.find (scenes);
            
            //Get its default node - we'll wrap our node around it to change the
            //display name, icon, etc.
            Node scenesNode = scenesDataObject.getNodeDelegate();
            
            //This FilterNode will be our project node
            return new ScenesNode (scenesNode, project);
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(donfe);
            return new AbstractNode (Children.LEAF);
        }
    }

    public org.openide.nodes.Node findPath(org.openide.nodes.Node root, Object target) {
        if (target instanceof DataObject) {
            return null; //XXX not implemented yet
        } else {
            return null;
        }
    }

    /** This is the node you actually see in the project tab for the project */
    private static final class ScenesNode extends FilterNode {
        final PovProject project;
        public ScenesNode (Node node, PovProject project) throws DataObjectNotFoundException {
            super (node, new FilterNode.Children (node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup (new Lookup[] { Lookups.singleton(project), 
                    node.getLookup() }));
            this.project = project;
        }
        
        public Image getIcon(int type) {
            return Utilities.loadImage ("org/netbeans/modules/povproject/resources/scenes.gif");
        }
        
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }      
        
        public String getDisplayName() {
            return project.getProjectDirectory().getName();
        }
        
        public Action[] getActions(boolean context) {
            Action[] orig = super.getActions (context);
            Action[] result = new Action[orig.length+1];
            System.arraycopy(orig, 0, result, 1, orig.length);
            result[0] = new RenderAction(project);
            return result;
        }
    }
    
    /**
     * An action which will appear on POV-Ray project nodes, which does two
     * things:  Provides a submenu of all the available renderer settings,
     * and shows a checkmark on the last-used setting.
     */
    private static final class RenderAction extends AbstractAction implements Presenter.Popup {
        private final PovProject project;
        
        public RenderAction (PovProject project) {
            this.project = project;
            //Set the display name of this action
            putValue (Action.NAME, NbBundle.getMessage(PovrayLogicalView.class,
                    "LBL_RenderProject"));
        }
        
        public JMenuItem getPopupPresenter() {
            JMenu result = new JMenu (this);
            RendererService service = (RendererService) 
                project.getLookup().lookup (RendererService.class);
            
            //Get the names of the renderer settings available (localized
            //names of e.g. 320x200.properties in the system filesystem - see
            //layer.xml)
            String[] settings = service.getAvailableRendererSettings();
            
            //Get the name of the one the user last rendered with
            String preferred = service.getPreferredConfigurationName();
            
            for (int i=0; i < settings.length; i++) {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem (this);
                item.setText (settings[i]);
                if (preferred.equals(settings[i])) {
                    item.setSelected (true);
                }
                result.add (item);
            }
            return result;
        }
        
        public void actionPerformed (ActionEvent ae) {
            //Find the menu item
            JMenuItem item = (JMenuItem) ae.getSource();
            
            //Locate the main file for the project
            MainFileProvider provider = (MainFileProvider) 
                project.getLookup().lookup (MainFileProvider.class);
            
            //Convert FileObject -> File
            File scene = FileUtil.toFile (provider.getMainFile());
            
            //Find the renderer service
            RendererService renderer = (RendererService) 
                project.getLookup().lookup (RendererService.class);
            
            //The item's text came from getAvailableRendererSettings()
            String target = item.getText();
            
            renderer.render(scene, renderer.getRendererSettings(target));
        }
        
        public boolean isEnabled() {
            //Just lookup whether Build is enabled - if it isn't, we don't have
            //a main file set, so we shouldn't be enabled either
            ActionProvider actions = 
                (ActionProvider) project.getLookup().lookup (
                ActionProvider.class);
            
            return actions.isActionEnabled(
                    ActionProvider.COMMAND_BUILD,  project.getLookup());
        }
    }
}
