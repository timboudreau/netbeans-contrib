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

package org.netbeans.modules.groovy.groovyproject.ui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.CharConversionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.groovy.groovyproject.GroovyProjectType;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.GroovyProjectProperties;
import org.netbeans.spi.java.project.support.ui.BrokenReferencesSupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.util.actions.SystemAction;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.xml.XMLUtil;

/**
 * Support for creating logical views.
 * @author Petr Hrebejk
 */
public class GroovyPhysicalViewProvider implements LogicalViewProvider {
    
    private static final RequestProcessor BROKEN_LINKS_RP = new RequestProcessor("GroovyPhysicalViewProvider.BROKEN_LINKS_RP"); // NOI18N
    
    private final Project project;
    private final AntProjectHelper helper;    
    private final PropertyEvaluator evaluator;
    private final SubprojectProvider spp;
    private final ReferenceHelper resolver;
        
    public GroovyPhysicalViewProvider(Project project, AntProjectHelper helper, PropertyEvaluator evaluator, SubprojectProvider spp, ReferenceHelper resolver) {
        this.project = project;
        assert project != null;
        this.helper = helper;
        assert helper != null;
        this.evaluator = evaluator;
        assert evaluator != null;
        this.spp = spp;
        assert spp != null;
        this.resolver = resolver;
    }
        
    public Node createLogicalView() {
        return new GroovyLogicalViewRootNode();
    }

    public org.openide.nodes.Node findPath( Node root, Object target ) {
                
        Project project = (Project)root.getLookup().lookup( Project.class );
        if ( project == null ) {
            return null;
        }
        
        if ( target instanceof FileObject ) {
            FileObject fo = (FileObject)target;
            Project owner = FileOwnerQuery.getOwner( fo );
            if ( !project.equals( owner ) ) {
                return null; // Don't waste time if project does not own the fo
            }
            
            Node[] nodes = root.getChildren().getNodes( true );
            for ( int i = 0; i < nodes.length; i++ ) {
                TreeRootNode.PathFinder pf2 = (TreeRootNode.PathFinder) nodes[i].getLookup().lookup(TreeRootNode.PathFinder.class);
                if (pf2 != null) {
                    Node n =  pf2.findPath(nodes[i], target);
                    if (n != null) {
                        return n;
                    }
                }
            }
        }

        return null;
    }

    private static Lookup createLookup( Project project ) {
        DataFolder rootFolder = DataFolder.findFolder( project.getProjectDirectory() );
        // XXX Remove root folder after FindAction rewrite
        return Lookups.fixed( new Object[] { project, rootFolder } );
    }

    
    // Private innerclasses ----------------------------------------------------
        
    private static final String[] BREAKABLE_PROPERTIES = new String[] {
        GroovyProjectProperties.JAVAC_CLASSPATH,  
        GroovyProjectProperties.RUN_CLASSPATH, 
        GroovyProjectProperties.SRC_DIR
    };

    public static boolean hasBrokenLinks(AntProjectHelper helper, ReferenceHelper resolver) {
        return BrokenReferencesSupport.isBroken(helper, resolver, BREAKABLE_PROPERTIES, 
            new String[] {GroovyProjectProperties.JAVA_PLATFORM});
    }

    private static Image brokenProjectBadge = Utilities.loadImage( "org/netbeans/modules/groovy/groovyproject/resources/brokenProjectBadge.gif" ); // NOI18N
    
    /** Filter node containin additional features for the J2SE physical
     */
    private final class GroovyLogicalViewRootNode extends AbstractNode {

        private Image icon;
        private Lookup lookup;
        private Action brokenLinksAction;
        private boolean broken;
        
        public GroovyLogicalViewRootNode() {
            super( new LogicalViewChildren( project ), Lookups.singleton(project));
            setIconBase( "org/netbeans/modules/groovy/groovyproject/resources/groovyProject" ); // NOI18N
            setName( ProjectUtils.getInformation( project ).getDisplayName() );            
            if (hasBrokenLinks(helper, resolver)) {
                broken = true;
            }
            brokenLinksAction = new BrokenLinksAction();
        }
        
        public Image getIcon( int type ) {
            Image original = super.getIcon( type );                
            return broken ? Utilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;
        }

        public Image getOpenedIcon( int type ) {
            Image original = super.getOpenedIcon(type);                
            return broken ? Utilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;            
        }            

        public String getHtmlDisplayName() {
            String dispName = super.getDisplayName();
            try {
                dispName = XMLUtil.toElementContent(dispName);
            } catch (CharConversionException ex) {
                // OK, no annotation in this case
                return null;
            }
            return broken ? "<font color=\"#A40000\">" + dispName + "</font>" : null; //NOI18N
        }
        
        public Action[] getActions( boolean context ) {
            return getAdditionalActions();
        }
        
        public boolean canRename() {
            return false;
        }
        
        /*
        public boolean canDestroy() {
            return true;
        }
        
        public void destroy() throws java.io.IOException {
            System.out.println("Destroy " + project.getProjectDirectory() );
            LogicalViews.closeProjectAction().actionPerformed( new ActionEvent( this, 0, "" ) );
            project.getProjectDirectory().delete();
        }        
        */

        // Private methods -------------------------------------------------            

        private Action[] getAdditionalActions() {

            ResourceBundle bundle = NbBundle.getBundle( GroovyPhysicalViewProvider.class );
            
            return new Action[] {
//                CommonProjectActions.newFileAction(),
//                null,                
                ProjectSensitiveActions.projectCommandAction( ActionProvider.COMMAND_BUILD, bundle.getString( "LBL_BuildAction_Name" ), null ), // NOI18N
                ProjectSensitiveActions.projectCommandAction( ActionProvider.COMMAND_REBUILD, bundle.getString( "LBL_RebuildAction_Name" ), null ), // NOI18N
                ProjectSensitiveActions.projectCommandAction( ActionProvider.COMMAND_CLEAN, bundle.getString( "LBL_CleanAction_Name" ), null ), // NOI18N
                null,
                ProjectSensitiveActions.projectCommandAction( ActionProvider.COMMAND_RUN, bundle.getString( "LBL_RunAction_Name" ), null ), // NOI18N
                null,
                CommonProjectActions.setAsMainProjectAction(),
                CommonProjectActions.openSubprojectsAction(),
                CommonProjectActions.closeProjectAction(),
                null,
                SystemAction.get( org.openide.actions.FindAction.class ),
                /*
                null,
                SystemAction.get( org.openide.actions.DeleteAction.class ),
                */
                null,
                SystemAction.get( org.openide.actions.ToolsAction.class ),
                null,
                (broken ? brokenLinksAction : null),
                CommonProjectActions.customizeProjectAction(),
            };

        }
        
        /** This action is created only when project has broken references.
         * Once these are resolved the action is disabled.
         */
        private class BrokenLinksAction extends AbstractAction implements PropertyChangeListener, Runnable {
            
            private RequestProcessor.Task task = null;

            private PropertyChangeListener weakPCL;
            
            public BrokenLinksAction() {
                putValue(Action.NAME, NbBundle.getMessage(GroovyPhysicalViewProvider.class, "LBL_Fix_Broken_Links_Action"));
                setEnabled(broken);
                evaluator.addPropertyChangeListener( this );
                // When evaluator fires changes that platform properties were
                // removed the platform still exists in JavaPlatformManager.
                // That's why I have to listen here also on JPM:
                weakPCL = WeakListeners.propertyChange( this, JavaPlatformManager.getDefault() );                
                JavaPlatformManager.getDefault().addPropertyChangeListener( weakPCL );
            }

            public void actionPerformed(ActionEvent e) {
                BrokenReferencesSupport.showCustomizer(helper, resolver, BREAKABLE_PROPERTIES, new String[]{GroovyProjectProperties.JAVA_PLATFORM});
                run();
            }

            public void propertyChange(PropertyChangeEvent evt) {
                // check project state whenever there was a property change
                // or change in list of platforms.
                // Coalesce changes since they can come quickly:
                if (task == null) {
                    task = BROKEN_LINKS_RP.create(this);
                }
                task.schedule(100);
            }

            public synchronized void run() {
                boolean old = broken;
                broken = hasBrokenLinks(helper, resolver);
                if (old != broken) {
                    setEnabled(broken);
                    fireIconChange();
                    fireOpenedIconChange();
                    fireDisplayNameChange(null, null);
                }
            }

        }

    }
    
    private static final class LogicalViewChildren extends Children.Keys/*<SourceGroup>*/ implements ChangeListener {
        
        private Project project;
        
        public LogicalViewChildren( Project project ) {
            this.project = project;
        }
        
        protected void addNotify() {
            super.addNotify();            
            getSources().addChangeListener( this );            
            setKeys( getKeys() );
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            getSources().removeChangeListener( this );
            super.removeNotify();
        }
        
        protected Node[] createNodes( Object key ) {
            return new Node[] { new TreeRootNode( (SourceGroup)key ) };            
        }            
                
        public void stateChanged( ChangeEvent e ) {
            setKeys( getKeys() );
        }
        
        // Private methods -----------------------------------------------------
        
        private Collection getKeys() {
            Sources sources = getSources();
            SourceGroup[] groups = sources.getSourceGroups( GroovyProjectType.SOURCES_TYPE_GROOVY );
            return new ArrayList( Arrays.asList( groups ) );
        }
        
        private Sources getSources() {
            return ProjectUtils.getSources( project );
        }
    }
}
