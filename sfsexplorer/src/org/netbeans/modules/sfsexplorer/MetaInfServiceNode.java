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
package org.netbeans.modules.sfsexplorer;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import org.netbeans.modules.sfsexplorer.ShowURLActionFactory.ShowURLAction;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;

/**
 * Node representing one service under META-INF/services.
 * Sandip V. Chitale (Sandip.Chitale@Sun.Com), David Strupl
 */
class MetaInfServiceNode extends AbstractNode {
    private String platform;
    private String service;
    private Action[] actions;
    
    /**
     * Maps: Class name --> JavaDoc documentation of that class.
     */
    private static Map<String, String> metaInfServicesAPI = new HashMap<String, String>();
    
    static {
        metaInfServicesAPI.put("org.openide.filesystems.Repository", "org-openide-filesystems/org/openide/filesystems/Repository.html");
        metaInfServicesAPI.put("org.openide.filesystems.URLMapper", "org-openide-filesystems/org/openide/filesystems/URLMapper.html");
        metaInfServicesAPI.put("org.openide.modules.InstalledFileLocator", "org-openide-modules/org/openide/modules/InstalledFileLocator.html");
        metaInfServicesAPI.put("org.openide.util.Lookup", "org-openide-util/org/openide/util/Lookup.html");
        metaInfServicesAPI.put("org.openide.nodes.NodeOperation", "org-openide-nodes/org/openide/nodes/NodeOperation.html");
        metaInfServicesAPI.put("org.openide.util.ContextGlobalProvider", "org-openide-util/org/openide/util/ContextGlobalProvider.html");
        metaInfServicesAPI.put("org.openide.xml.EntityCatalog", "org-openide-util/org/openide/xml/EntityCatalog.html");
        metaInfServicesAPI.put("org.netbeans.spi.editor.mimelookup.MimeLookupInitializer", "org-netbeans-modules-editor-mimelookup/org/netbeans/spi/editor/mimelookup/MimeLookupInitializer.html");
        metaInfServicesAPI.put("org.netbeans.spi.editor.mimelookup.Class2LayerFolder", "org-netbeans-modules-editor-mimelookup/org/netbeans/spi/editor/mimelookup/Class2LayerFolder.html");
        metaInfServicesAPI.put("org.openide.awt.StatusLineElementProvider", "org-openide-awt/org/openide/awt/StatusLineElementProvider.html");
        metaInfServicesAPI.put("org.openide.ErrorManager", "org-openide-util/org/openide/ErrorManager.html");
        metaInfServicesAPI.put("org.openide.LifecycleManager", "org-openide-util/org/openide/LifecycleManager.html");
        metaInfServicesAPI.put("org.openide.actions.ActionManager", "org-openide-actions/org/openide/actions/ActionManager.html");
        metaInfServicesAPI.put("org.openide.awt.StatusDisplayer", "org-openide-awt/org/openide/awt/StatusDisplayer.html");
        metaInfServicesAPI.put("org.openide.loaders.DataLoaderPool", "org-openide-loaders/org/openide/loaders/DataLoaderPool.html");
        metaInfServicesAPI.put("org.openide.loaders.RepositoryNodeFactory", "org-openide-loaders/org/openide/loaders/RepositoryNodeFactory.html");
        metaInfServicesAPI.put("org.openide.util.datatransfer.ExClipboard", "org-openide-util/org/openide/util/datatransfer/ExClipboard.html");
        metaInfServicesAPI.put("org.openide.windows.IOProvider", "org-openide-io/org/openide/windows/IOProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.CollocationQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/CollocationQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.FileOwnerQueryImplementation", "org-netbeans-modules-projectapi/org/netbeans/spi/project/FileOwnerQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.FileBuiltQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/FileBuiltQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.SharabilityQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/SharabilityQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.classpath.ClassPathProvider", "org-netbeans-api-java/org/netbeans/spi/java/classpath/ClassPathProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/SourceForBinaryQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.ProjectFactory", "org-netbeans-modules-projectapi/org/netbeans/spi/project/ProjectFactory.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.ant.AntArtifactQueryImplementation", "org-netbeans-modules-project-ant/org/netbeans/spi/project/ant/AntArtifactQueryImplementation.html");
        metaInfServicesAPI.put("org.openide.execution.ExecutionEngine", "org-openide-execution/org/openide/execution/ExecutionEngine.html");
        metaInfServicesAPI.put("org.netbeans.spi.queries.VisibilityQueryImplementation", "org-netbeans-modules-queries/org/netbeans/spi/queries/VisibilityQueryImplementation.html");
        metaInfServicesAPI.put("org.apache.tools.ant.module.spi.AntLogger", "org-apache-tools-ant-module/org/apache/tools/ant/module/spi/AntLogger.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.libraries.LibraryProvider", "org-netbeans-modules-project-libraries/org/netbeans/spi/project/libraries/LibraryProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.AccessibilityQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/AccessibilityQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/JavadocForBinaryQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.MultipleRootsUnitTestForSourceQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/MultipleRootsUnitTestForSourceQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.SourceLevelQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/SourceLevelQueryImplementation.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation", "org-netbeans-api-java/org/netbeans/spi/java/queries/UnitTestForSourceQueryImplementation.html");
        metaInfServicesAPI.put("org.apache.tools.ant.module.spi.AutomaticExtraClasspathProvider", "org-apache-tools-ant-module/org/apache/tools/ant/module/spi/AutomaticExtraClasspathProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.java.project.support.ui.PackageRenameHandler", "org-netbeans-modules-java-project/org/netbeans/spi/java/project/support/ui/PackageRenameHandler.html");
        metaInfServicesAPI.put("org.openide.loaders.FolderRenameHandler", "org-openide-loaders/org/openide/loaders/FolderRenameHandler.html");
        metaInfServicesAPI.put("org.openide.text.AnnotationProvider", "org-openide-text/org/openide/text/AnnotationProvider.html");
        metaInfServicesAPI.put("org.netbeans.spi.project.support.ant.AntBasedProjectType", "org-netbeans-modules-project-ant/org/netbeans/spi/project/support/ant/AntBasedProjectType.html");
        metaInfServicesAPI.put("org.netbeans.modules.masterfs.providers.AnnotationProvider", "org-netbeans-modules-masterfs/org/netbeans/modules/masterfs/providers/AnnotationProvider.html");
        metaInfServicesAPI.put("org.openide.DialogDisplayer", "org-openide-dialogs/org/openide/DialogDisplayer.html");
        metaInfServicesAPI.put("org.openide.windows.WindowManager", "org-openide-windows/org/openide/windows/WindowManager.html");
    }

    /**
     * Constructs new node.
     * @param metaInfService 
     * @param platform 
     */
    MetaInfServiceNode(MetaInfService metaInfService, String platform) {
        super(new MetaInfServiceNodeChildren(metaInfService.getProviders()));
        this.platform = platform;
        this.service = metaInfService.getService();
        setDisplayName(service);
        setIconBaseWithExtension("org/netbeans/modules/sfsexplorer/service.gif");
    }

    /**
     * Computes the actions for this node.
     * @param context 
     * @return 
     */
    public Action[] getActions(boolean context) {
        if (metaInfServicesAPI.containsKey(service)) {
            if (!context) {
                if (actions == null) {
                    List<Action> actionsList = new LinkedList<Action>();
                    String urlString = (String) metaInfServicesAPI.get(service);
                    if (platform.equals("platform6")) {
                        urlString = NbBundle.getMessage(MetaInfServiceNode.class, "javadoc55")+ urlString;
                    } else if (platform.equals("platform7")) {
                        urlString = NbBundle.getMessage(MetaInfServiceNode.class, "javadoc60")+ urlString;
                    }
                    URL url = SFSBrowserTopComponent.getURL(urlString);
                    if (url != null) {
                        actionsList.add(new ShowURLAction(service + " API", url));
                    }
                    actions = actionsList.toArray(SFSBrowserTopComponent.EMPTY_ACTIONS);
                }
                return actions;
            }
        }
        return SFSBrowserTopComponent.EMPTY_ACTIONS;
    }
}