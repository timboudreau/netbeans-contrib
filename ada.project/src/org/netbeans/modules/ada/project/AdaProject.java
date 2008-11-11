/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.project;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.ada.project.path.ClassPathProviderImplementation;
import org.netbeans.modules.ada.project.ui.Utils;
import org.netbeans.modules.ada.project.ui.properties.AdaCustomizerProvider;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.api.classpath.GlobalPathRegistry;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaProject implements Project {

    protected AntProjectHelper helper;
    protected UpdateHelper updateHelper;
    protected SourceRoots sourceRoots;
    protected SourceRoots testRoots;
    protected Lookup lookup;
    protected PropertyEvaluator evaluator;
    protected ReferenceHelper refHelper;
    protected AuxiliaryConfiguration aux;

    private FileObject sourcesDirectory;

    public AdaProject(final AntProjectHelper helper) {
        assert helper != null;
        this.helper = helper;
        this.updateHelper = new UpdateHelper(UpdateImplementation.NULL, helper);
        this.evaluator = createEvaluator();
        this.aux = helper.createAuxiliaryConfiguration();
        refHelper = new ReferenceHelper(helper, aux, evaluator);
        this.sourceRoots = SourceRoots.create(updateHelper, evaluator, refHelper, false);
        this.testRoots = SourceRoots.create(updateHelper, evaluator, refHelper, true);
        this.lookup = createLookup();
    }

    public AdaProject() {
    }

    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }

    public PropertyEvaluator createEvaluator() {
        PropertyEvaluator privateProps = PropertyUtils.sequentialPropertyEvaluator(
                helper.getStockPropertyPreprovider(),
                helper.getPropertyProvider(AntProjectHelper.PRIVATE_PROPERTIES_PATH));
        return PropertyUtils.sequentialPropertyEvaluator(
                helper.getStockPropertyPreprovider(),
                helper.getPropertyProvider(AntProjectHelper.PRIVATE_PROPERTIES_PATH),
                PropertyUtils.userPropertiesProvider(privateProps,
                "user.properties.file", FileUtil.toFile(getProjectDirectory())), // NOI18N
                helper.getPropertyProvider(AntProjectHelper.PROJECT_PROPERTIES_PATH));
    }

    private Lookup createLookup() {
        return Lookups.fixed(new Object[]{
                    this, //project spec requires a project be in it's own lookup
                    aux, //Auxiliary configuartion to store bookmarks and so on
                    new AdaActionProvider(this), //Provides Standard like build and cleen
                    new ClassPathProviderImplementation(this),
                    new Info(), // Project information Implementation
                    new AdaLogicalViewProvider(this), // Logical view if project implementation
                    new AdaOpenedHook(), //Called by project framework when project is opened (closed)
                    new AdaProjectXmlSavedHook(), //Called when project.xml changes
                    new AdaSources(helper, evaluator, sourceRoots, testRoots), // Ada source grops - used by package view, factories, refactoring, ...
                    new AdaProjectOperations(this), //move, rename, copy of project
                    new RecommendedTemplatesImpl(this.updateHelper), // Recommended Templates
                    new AdaCustomizerProvider(this), //Project custmoizer
                    new AdaProjectFileEncodingQuery(getEvaluator()), //Provides encoding of the project - used by editor, runtime
                    new AdaSharabilityQuery(helper, getEvaluator(), getSourceRoots(), getTestRoots()), //Sharabilit info - used by VCS
                    helper.createCacheDirectoryProvider(), //Cache provider
                    helper.createAuxiliaryProperties(),     // AuxiliaryConfiguraion provider - used by bookmarks, project Preferences, etc
                });
    }

    public Lookup getLookup() {
        return lookup;
    }

    public SourceRoots getSourceRoots() {
        return this.sourceRoots;
    }

    public SourceRoots getTestRoots() {
        return this.testRoots;
    }

    public PropertyEvaluator getEvaluator() {
        return evaluator;
    }

    public AntProjectHelper getHelper() {
        return this.helper;
    }

    public synchronized FileObject getSourcesDirectory() {
        if (sourcesDirectory == null) {
            sourcesDirectory = resolveSourcesDirectory();
        }
        assert sourcesDirectory != null : "Sources directory cannot be null";
        return sourcesDirectory;
    }

    private FileObject resolveSourcesDirectory() {
        // get the first source root
        //  in fact, there should *always* be only 1 source root but see #141200, #141204 or #141229
        FileObject[] sourceObjects = Utils.getSourceObjects(this);
        if (sourceObjects.length > 0) {
            return sourceObjects[0];
        }
        // #144371 - source folder probably deleted => so:
        // #145477 (project sharability):
        //  1. try to restore it - if it fails, then
        //  2. set it to the project directory in *PRIVATE* properties (and save it)
        //      => warn user about impossibility of creating src dir and *remove it in project closed hook*!!!
        String projectName = getName();
        File srcDir = FileUtil.normalizeFile(new File(helper.resolvePath(evaluator.getProperty(AdaProjectProperties.SRC_DIR))));
        if (srcDir.mkdirs()) {
            // original sources restored
            informUser(projectName, NbBundle.getMessage(AdaProject.class, "MSG_SourcesFolderRestored", srcDir.getAbsolutePath()), NotifyDescriptor.INFORMATION_MESSAGE);
            return FileUtil.toFileObject(srcDir);
        }
        // temporary set sources to project directory, do not store it anywhere
        informUser(projectName, NbBundle.getMessage(AdaProject.class, "MSG_SourcesFolderTemporaryToProjectDirectory", srcDir.getAbsolutePath()), NotifyDescriptor.ERROR_MESSAGE);
        return helper.getProjectDirectory();
    }

    private void informUser(String title, String message, int type) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                message,
                title,
                NotifyDescriptor.DEFAULT_OPTION,
                type,
                new Object[] {NotifyDescriptor.OK_OPTION},
                NotifyDescriptor.OK_OPTION));
    }

    public String getName() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<String>() {

            public String run() {
                Element data = getHelper().getPrimaryConfigurationData(true);
                NodeList nl = data.getElementsByTagNameNS(AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                if (nl.getLength() == 1) {
                    nl = nl.item(0).getChildNodes();
                    if (nl.getLength() == 1 && nl.item(0).getNodeType() == Node.TEXT_NODE) {
                        return ((Text) nl.item(0)).getNodeValue();
                    }
                }
                return "???"; // NOI18N
            }
        });
    }

    void setName(final String name) {
        ProjectManager.mutex().writeAccess(new Runnable() {

            public void run() {
                Element data = getHelper().getPrimaryConfigurationData(true);
                NodeList nl = data.getElementsByTagNameNS(AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                Element nameEl;
                if (nl.getLength() == 1) {
                    nameEl = (Element) nl.item(0);
                    NodeList deadKids = nameEl.getChildNodes();
                    while (deadKids.getLength() > 0) {
                        nameEl.removeChild(deadKids.item(0));
                    }
                } else {
                    nameEl = data.getOwnerDocument().createElementNS(
                            AdaProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                    data.insertBefore(nameEl, data.getChildNodes().item(0));
                }
                nameEl.appendChild(data.getOwnerDocument().createTextNode(name));
                getHelper().putPrimaryConfigurationData(data, true);
            }
        });
    }

    private final class Info implements ProjectInformation {

        private final ImageIcon PROJECT_ICON = new ImageIcon(ImageUtilities.loadImage("org/netbeans/modules/ada/project/ui/resources/ada-lovelace-16.png"));
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        public String getDisplayName() {
            return getName();
        }

        public Icon getIcon() {
            return PROJECT_ICON;
        }

        public String getName() {
            return AdaProject.this.getName();
        }

        public Project getProject() {
            return AdaProject.this;
        }

        void firePropertyChange(String prop) {
            propertyChangeSupport.firePropertyChange(prop, null, null);
        }
    }

    public final class AdaOpenedHook extends ProjectOpenedHook {

        protected void projectOpened() {
            // register project's classpaths to GlobalPathRegistry
            final ClassPathProviderImplementation cpProvider = getLookup().lookup(ClassPathProviderImplementation.class);
            assert cpProvider != null;
            GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        }

        protected void projectClosed() {
            // unregister project's classpaths to GlobalPathRegistry
            final ClassPathProviderImplementation cpProvider = getLookup().lookup(ClassPathProviderImplementation.class);
            assert cpProvider != null;
            GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            try {
                ProjectManager.getDefault().saveProject(AdaProject.this);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    public final class AdaProjectXmlSavedHook extends ProjectXmlSavedHook {

        public AdaProjectXmlSavedHook() {
        }

        protected void projectXmlSaved() throws IOException {
            Info info = getLookup().lookup(Info.class);
            assert info != null;
            info.firePropertyChange(ProjectInformation.PROP_NAME);
            info.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
        }
    }

    private static final class RecommendedTemplatesImpl implements RecommendedTemplates, PrivilegedTemplates {

        RecommendedTemplatesImpl(UpdateHelper helper) {
            this.helper = helper;
        }

        private final UpdateHelper helper;

        private static final String[] TYPES = new String[]{
            "Ada", // NOI18N
            "c-types", // NOI18N
            "cpp-types", // NOI18N
            "asm-types", // NOI18N
            "shell-types", // NOI18N
            "simple-files" // NOI18N
        };

        // List of primarily supported templates
        private static final String[] PRIVILEGED_NAMES = new String[]{
            "Templates/Ada/NewAdaMain.adb", //NOI18N
            "Templates/Ada/NewAdaSpec.ads", // NOI18N
            "Templates/Ada/NewAdaBody.adb", // NOI18N
            "Templates/Other/Folder"
        };

        public String[] getRecommendedTypes() {
            return TYPES;
        }

        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
    }
}
