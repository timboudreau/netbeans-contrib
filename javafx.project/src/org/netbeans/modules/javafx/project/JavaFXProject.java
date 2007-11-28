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

package org.netbeans.modules.javafx.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.modules.javafx.project.classpath.ClassPathProviderImpl;
import org.netbeans.modules.javafx.project.classpath.JavaFXProjectClassPathExtender;
import org.netbeans.modules.javafx.project.classpath.JavaFXProjectClassPathModifier;
import org.netbeans.modules.javafx.project.queries.BinaryForSourceQueryImpl;
import org.netbeans.modules.javafx.project.queries.CompiledSourceForBinaryQuery;
import org.netbeans.modules.javafx.project.queries.JavaFXProjectEncodingQueryImpl;
import org.netbeans.modules.javafx.project.queries.JavadocForBinaryQueryImpl;
import org.netbeans.modules.javafx.project.queries.SourceLevelQueryImpl;
import org.netbeans.modules.javafx.project.queries.UnitTestForSourceQueryImpl;
import org.netbeans.modules.javafx.project.ui.JavaFXLogicalViewProvider;
import org.netbeans.modules.javafx.project.ui.customizer.CustomizerProviderImpl;
import org.netbeans.modules.javafx.project.ui.customizer.JavaFXProjectProperties;
import org.netbeans.modules.javafx.project.ui.resources.ResourceMarker;
import org.netbeans.spi.java.project.support.ui.BrokenReferencesSupport;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.ant.AntBuildExtenderFactory;
import org.netbeans.spi.project.ant.AntBuildExtenderImplementation;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.FilterPropertyProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Represents one plain JavaFX project.
 * @author ads
 */
public final class JavaFXProject implements Project, AntProjectListener {

    private static final Icon JFX_PROJECT_ICON = new ImageIcon(Utilities
            .loadImage(ResourceMarker.getRosource(ResourceMarker.PROJECT_ICO)));

    private final AuxiliaryConfiguration aux;

    private final AntProjectHelper helper;

    private final PropertyEvaluator eval;

    private final ReferenceHelper refHelper;

    private final GeneratedFilesHelper genFilesHelper;

    private final Lookup lookup;

    private final UpdateHelper updateHelper;

    private MainClassUpdater mainClassUpdater;

    private SourceRoots sourceRoots;

    private SourceRoots testRoots;

    private AntBuildExtender buildExtender;

    JavaFXProject( AntProjectHelper helper ) throws IOException {
        this.helper = helper;
        
        eval = createEvaluator();
        aux = helper.createAuxiliaryConfiguration();
        refHelper = new ReferenceHelper(helper, aux, eval);
        
        buildExtender = AntBuildExtenderFactory.createAntExtender(new JavaFXExtenderImplementation());
        genFilesHelper = new GeneratedFilesHelper(helper, buildExtender);
        
        
        this.updateHelper = new UpdateHelper(this, this.helper, this.aux,
                UpdateHelper.createDefaultNotifier());

        lookup = createLookup(aux);
        helper.addAntProjectListener(this);
    }

    /**
     * Returns the project directory
     * 
     * @return the directory the project is located in
     */
    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }

    @Override
    public String toString() {
        return "JavaFXProject["
                + FileUtil.getFileDisplayName(getProjectDirectory()) + "]"; // NOI18N
    }

    private PropertyEvaluator createEvaluator() {
        // It is currently safe to not use the UpdateHelper for
        // PropertyEvaluator; UH.getProperties() delegates to APH
        // Adapted from APH.getStandardPropertyEvaluator (delegates to
        // ProjectProperties):
        PropertyEvaluator baseEval1 = PropertyUtils
                .sequentialPropertyEvaluator(
                        helper.getStockPropertyPreprovider(),
                        helper
                                .getPropertyProvider(JavaFXConfigurationProvider.CONFIG_PROPS_PATH));
        PropertyEvaluator baseEval2 = PropertyUtils
                .sequentialPropertyEvaluator(
                        helper.getStockPropertyPreprovider(),
                        helper
                                .getPropertyProvider(AntProjectHelper.PRIVATE_PROPERTIES_PATH));
        return PropertyUtils
                .sequentialPropertyEvaluator(
                        helper.getStockPropertyPreprovider(),
                        helper
                                .getPropertyProvider(JavaFXConfigurationProvider.CONFIG_PROPS_PATH),
                        new ConfigPropertyProvider(baseEval1,
                                "nbproject/private/configs", helper), // NOI18N
                        helper
                                .getPropertyProvider(AntProjectHelper.PRIVATE_PROPERTIES_PATH),
                        PropertyUtils.userPropertiesProvider(baseEval2,
                                "user.properties.file", FileUtil
                                        .toFile(getProjectDirectory())), // NOI18N
                        new ConfigPropertyProvider(baseEval1,
                                "nbproject/configs", helper), // NOI18N
                        helper
                                .getPropertyProvider(AntProjectHelper.PROJECT_PROPERTIES_PATH));
    }

    private static final class ConfigPropertyProvider extends
            FilterPropertyProvider implements PropertyChangeListener
    {

        private final PropertyEvaluator baseEval;

        private final String prefix;

        private final AntProjectHelper helper;

        public ConfigPropertyProvider( PropertyEvaluator baseEval,
                String prefix, AntProjectHelper helper )
        {
            super(computeDelegate(baseEval, prefix, helper));
            this.baseEval = baseEval;
            this.prefix = prefix;
            this.helper = helper;
            baseEval.addPropertyChangeListener(this);
        }

        public void propertyChange( PropertyChangeEvent ev ) {
            if (JavaFXConfigurationProvider.PROP_CONFIG.equals(ev
                    .getPropertyName()))
            {
                setDelegate(computeDelegate(baseEval, prefix, helper));
            }
        }

        private static PropertyProvider computeDelegate(
                PropertyEvaluator baseEval, String prefix,
                AntProjectHelper helper )
        {
            String config = baseEval
                    .getProperty(JavaFXConfigurationProvider.PROP_CONFIG);
            if (config != null) {
                return helper.getPropertyProvider(prefix + "/" + config
                        + ".properties"); // NOI18N
            }
            else {
                return PropertyUtils.fixedPropertyProvider(Collections
                        .<String, String> emptyMap());
            }
        }
    }

    public PropertyEvaluator evaluator() {
        return eval;
    }

    ReferenceHelper getReferenceHelper() {
        return this.refHelper;
    }

    public UpdateHelper getUpdateHelper() {
        return this.updateHelper;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public AntProjectHelper getAntProjectHelper() {
        return helper;
    }

    private Lookup createLookup( AuxiliaryConfiguration aux ) {
        SubprojectProvider spp = refHelper.createSubprojectProvider();
        final JavaFXProjectClassPathModifier cpMod = new JavaFXProjectClassPathModifier(
                this, this.updateHelper, eval, refHelper);
        //final JaxWsModel jaxWsModel = getJaxWsModel();
        //assert jaxWsModel != null;
        ClassPathProviderImpl cpProvider = new ClassPathProviderImpl(
                this.helper, evaluator(), getSourceRoots(),
                getTestSourceRoots()); // Does not use APH to get/put
                                        // properties/cfgdata
        Lookup base = Lookups.fixed(new Object[] {
                JavaFXProject.this,
                new Info(),
                aux,
                helper.createCacheDirectoryProvider(),
                spp,
                new JavaFXActionProvider(this, this.updateHelper),
                new JavaFXLogicalViewProvider(this, this.updateHelper,
                        evaluator(), spp, refHelper),
                new CustomizerProviderImpl(this, this.updateHelper,
                        evaluator(), refHelper , genFilesHelper ),
                cpProvider,
                new CompiledSourceForBinaryQuery(this.helper, evaluator(),
                        getSourceRoots(), getTestSourceRoots()), // Does not
                                                                    // use APH
                                                                    // to
                                                                    // get/put
                                                                    // properties/cfgdata
                new JavadocForBinaryQueryImpl(this.helper, evaluator()), // Does
                                                                            // not
                                                                            // use
                                                                            // APH
                                                                            // to
                                                                            // get/put
                                                                            // properties/cfgdata
                new AntArtifactProviderImpl(),
                new ProjectXmlSavedHookImpl(),
                new ProjectOpenedHookImpl(),
                new UnitTestForSourceQueryImpl(getSourceRoots(),
                        getTestSourceRoots()),
                new SourceLevelQueryImpl(evaluator()),
                new JavaFXSources(this.helper, evaluator(), getSourceRoots(),
                        getTestSourceRoots()),
                new JavaFXSharabilityQuery(this.helper, evaluator(),
                        getSourceRoots(), getTestSourceRoots()), // Does not
                                                                    // use APH
                                                                    // to
                                                                    // get/put
                                                                    // properties/cfgdata
                new JavaFxFileBuiltQuery(this.helper, evaluator(),
                        getSourceRoots(), getTestSourceRoots()), // Does not
                                                                    // use APH
                                                                    // to
                                                                    // get/put
                                                                    // properties/cfgdata
                new RecommendedTemplatesImpl(this.updateHelper),
                new JavaFXProjectClassPathExtender(cpMod),
                buildExtender,
                cpMod,
                this, // never cast an externally obtained Project to
                        // JavaFXProject - use lookup instead
                new JavaFXProjectOperations(this),
                new JavaFXConfigurationProvider(this),
                UILookupMergerSupport.createPrivilegedTemplatesMerger(),
                UILookupMergerSupport.createRecommendedTemplatesMerger(),
                LookupProviderSupport.createSourcesMerger(),
                new JavaFXProjectEncodingQueryImpl(evaluator()),
                evaluator(),
                new JavaFXTemplateAttributesProvider(),
                new BinaryForSourceQueryImpl(this.sourceRoots, this.testRoots,
                        this.helper, this.eval) // Does not use APH to get/put
                                                // properties/cfgdata
                });
        return LookupProviderSupport.createCompositeLookup(base,
                "Projects/org-netbeans-modules-javafx-project/Lookup"); // NOI18N
    }

    public void configurationXmlChanged( AntProjectEvent ev ) {
        if (ev.getPath().equals(AntProjectHelper.PROJECT_XML_PATH)) {
            // Could be various kinds of changes, but name & displayName might
            // have changed.
            Info info = (Info) getLookup().lookup(ProjectInformation.class);
            info.firePropertyChange(ProjectInformation.PROP_NAME);
            info.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
        }
    }

    public void propertiesChanged( AntProjectEvent ev ) {
        // currently ignored (probably better to listen to evaluator() if you
        // need to)
    }

    // Package private methods -------------------------------------------------

    /**
     * Returns the source roots of this project
     * 
     * @return project's source roots
     */
    public synchronized SourceRoots getSourceRoots() {
        if (this.sourceRoots == null) { // Local caching, no project metadata
                                        // access
            this.sourceRoots = new SourceRoots(this.updateHelper, evaluator(),
                    getReferenceHelper(), "source-roots", false,
                    "src.{0}{1}.dir"); // NOI18N
        }
        return this.sourceRoots;
    }

    public synchronized SourceRoots getTestSourceRoots() {
        if (this.testRoots == null) { // Local caching, no project metadata
                                        // access
            this.testRoots = new SourceRoots(this.updateHelper, evaluator(),
                    getReferenceHelper(), "test-roots", true, "test.{0}{1}.dir"); // NOI18N
        }
        return this.testRoots;
    }

    File getTestClassesDirectory() {
        String testClassesDir = evaluator().getProperty(
                JavaFXProjectProperties.BUILD_TEST_CLASSES_DIR);
        if (testClassesDir == null) {
            return null;
        }
        return helper.resolveFile(testClassesDir);
    }

    // Currently unused (but see #47230):
    /** Store configured project name. */
    public void setName( final String name ) {
        ProjectManager.mutex().writeAccess(new Mutex.Action<Void>() {

            public Void run() {
                Element data = helper.getPrimaryConfigurationData(true);
                // XXX replace by XMLUtil when that has findElement, findText,
                // etc.
                NodeList nl = data.getElementsByTagNameNS(
                        JavaFXProjectType.PROJECT_CONFIGURATION_NAMESPACE,
                        "name");
                Element nameEl;
                if (nl.getLength() == 1) {
                    nameEl = (Element) nl.item(0);
                    NodeList deadKids = nameEl.getChildNodes();
                    while (deadKids.getLength() > 0) {
                        nameEl.removeChild(deadKids.item(0));
                    }
                }
                else {
                    nameEl = data.getOwnerDocument().createElementNS(
                            JavaFXProjectType.PROJECT_CONFIGURATION_NAMESPACE,
                            "name");
                    data.insertBefore(nameEl, /* OK if null */data
                            .getChildNodes().item(0));
                }
                nameEl
                        .appendChild(data.getOwnerDocument().createTextNode(
                                name));
                helper.putPrimaryConfigurationData(data, true);
                return null;
            }
        });
    }

    // Private innerclasses ----------------------------------------------------

    private final class Info implements ProjectInformation {

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(
                this);

        Info() {
        }

        void firePropertyChange( String prop ) {
            pcs.firePropertyChange(prop, null, null);
        }

        public String getName() {
            return PropertyUtils.getUsablePropertyName(getDisplayName());
        }

        public String getDisplayName() {
            return ProjectManager.mutex().readAccess(new Mutex.Action<String>()
            {

                public String run() {
                    Element data = updateHelper
                            .getPrimaryConfigurationData(true);
                    // XXX replace by XMLUtil when that has findElement,
                    // findText, etc.
                    NodeList nl = data.getElementsByTagNameNS(
                            JavaFXProjectType.PROJECT_CONFIGURATION_NAMESPACE,
                            "name"); // NOI18N
                    if (nl.getLength() == 1) {
                        nl = nl.item(0).getChildNodes();
                        if (nl.getLength() == 1
                                && nl.item(0).getNodeType() == Node.TEXT_NODE)
                        {
                            return ((Text) nl.item(0)).getNodeValue();
                        }
                    }
                    return "???"; // NOI18N
                }
            });
        }

        public Icon getIcon() {
            return JFX_PROJECT_ICON;
        }

        public Project getProject() {
            return JavaFXProject.this;
        }

        public void addPropertyChangeListener( PropertyChangeListener listener )
        {
            pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(
                PropertyChangeListener listener )
        {
            pcs.removePropertyChangeListener(listener);
        }

    }

    private final class ProjectXmlSavedHookImpl extends ProjectXmlSavedHook {

        ProjectXmlSavedHookImpl() {
        }

        protected void projectXmlSaved() throws IOException {
            // May be called by {@link
            // AuxiliaryConfiguration#putConfigurationFragment}
            // which didn't affect the j2seproject
            if (updateHelper.isCurrent()) {
                // Refresh build-impl.xml only for j2seproject/2
                genFilesHelper.refreshBuildScript(
                        GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                        JavaFXProject.class
                                .getResource("resources/build-impl.xsl"),
                         false);
                genFilesHelper.refreshBuildScript(
                        GeneratedFilesHelper.BUILD_XML_PATH,
                        JavaFXProject.class.getResource("resources/build.xsl"),
                        false);
            }
        }

    }

    private final class ProjectOpenedHookImpl extends ProjectOpenedHook {

        ProjectOpenedHookImpl() {
        }

        protected void projectOpened() {
            // Check up on build scripts.
            try {
                if (updateHelper.isCurrent()) {
                    // Refresh build-impl.xml only for j2seproject/2
                    genFilesHelper.refreshBuildScript(
                            GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                            JavaFXProject.class
                                    .getResource("resources/build-impl.xsl"),
                            true);
                    genFilesHelper.refreshBuildScript(
                            GeneratedFilesHelper.BUILD_XML_PATH,
                            JavaFXProject.class
                                    .getResource("resources/build.xsl"),
                            true);
                }
            }
            catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }

            // register project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = lookup
                    .lookup(ClassPathProviderImpl.class);
            GlobalPathRegistry.getDefault().register(ClassPath.BOOT,
                    cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().register(ClassPath.SOURCE,
                    cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            GlobalPathRegistry.getDefault().register(ClassPath.COMPILE,
                    cpProvider.getProjectClassPaths(ClassPath.COMPILE));

            // register updater of main.class
            // the updater is active only on the opened projects
            mainClassUpdater = new MainClassUpdater(JavaFXProject.this, eval,
                    updateHelper, cpProvider
                            .getProjectClassPaths(ClassPath.SOURCE)[0],
                    JavaFXProjectProperties.MAIN_CLASS);

            // Make it easier to run headless builds on the same machine at
            // least.
            ProjectManager.mutex().writeAccess(new Mutex.Action<Void>() {

                public Void run() {
                    EditableProperties ep = updateHelper
                            .getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
                    File buildProperties = new File(System
                            .getProperty("netbeans.user"), "build.properties"); // NOI18N
                    ep.setProperty("user.properties.file", buildProperties
                            .getAbsolutePath()); // NOI18N

                    // set jaxws.endorsed.dir property (for endorsed mechanism
                    // to be used with wsimport, wsgen)
                    //WSUtils.setJaxWsEndorsedDirProperty(ep);

                    updateHelper.putProperties(
                            AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);
                    ep = helper
                            .getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    if (!ep.containsKey(JavaFXProjectProperties.INCLUDES)) {
                        ep.setProperty(JavaFXProjectProperties.INCLUDES, "**"); // NOI18N
                    }
                    if (!ep.containsKey(JavaFXProjectProperties.EXCLUDES)) {
                        ep.setProperty(JavaFXProjectProperties.EXCLUDES, ""); // NOI18N
                    }
                    
                    if (!ep.containsKey(JavaFXProjectProperties.BUILD_CLASSPATH)){
                        ep.setProperty("build.classpath", new String[] { // NOI18N
                            "${javac.classpath}:", // NOI18N
                            "${src.dir}:", // NOI18N
                            "${build.classes.dir}:", // NOI18N
                            "${"+JavaFXProjectProperties.FX_LIBS +"}" // NOI18N
                        });
                    }
                    if (!ep.containsKey(JavaFXProjectProperties.MAIN_FX_BUILD_CLASS)){
                        ep.setProperty(JavaFXProjectProperties.MAIN_FX_BUILD_CLASS, JavaFXProjectProperties.FX_BUILD_CLASS_NAME);
                    }
                    
                    helper.putProperties(
                            AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                    try {
                        ProjectManager.getDefault().saveProject(
                                JavaFXProject.this);
                    }
                    catch (IOException e) {
                        // #91398 provide a better error message in case of
                        // read-only location of project.
                        if (!JavaFXProject.this.getProjectDirectory()
                                .canWrite())
                        {
                            NotifyDescriptor nd = new NotifyDescriptor.Message(
                                    NbBundle.getMessage(JavaFXProject.class,
                                            "ERR_ProjectReadOnly",
                                            JavaFXProject.this
                                                    .getProjectDirectory()
                                                    .getName()));
                            DialogDisplayer.getDefault().notify(nd);
                        }
                        else {
                            ErrorManager.getDefault().notify(e);
                        }
                    }
                    return null;
                }
            });
            JavaFXLogicalViewProvider physicalViewProvider = getLookup().lookup(
                    JavaFXLogicalViewProvider.class);
            if (physicalViewProvider != null
                    && physicalViewProvider.hasBrokenLinks())
            {
                BrokenReferencesSupport.showAlert();
            }
        }

        protected void projectClosed() {
            // Probably unnecessary, but just in case:
            try {
                ProjectManager.getDefault().saveProject(JavaFXProject.this);
            }
            catch (IOException e) {
                if (!JavaFXProject.this.getProjectDirectory().canWrite()) {
                    // #91398 - ignore, we already reported on project open.
                    // not counting with someone setting the ro flag while the
                    // project is opened.
                }
                else {
                    ErrorManager.getDefault().notify(e);
                }
            }

            // unregister project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = lookup
                    .lookup(ClassPathProviderImpl.class);
            GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT,
                    cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE,
                    cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            GlobalPathRegistry.getDefault().unregister(ClassPath.COMPILE,
                    cpProvider.getProjectClassPaths(ClassPath.COMPILE));
            if (mainClassUpdater != null) {
                mainClassUpdater.unregister();
                mainClassUpdater = null;
            }

        }

    }

    /**
     * Exports the main JAR as an official build product for use from other
     * scripts. The type of the artifact will be {@link AntArtifact#TYPE_JAR}.
     */
    private final class AntArtifactProviderImpl implements AntArtifactProvider {

        public AntArtifact[] getBuildArtifacts() {
            return new AntArtifact[] { helper.createSimpleAntArtifact(
                    JavaProjectConstants.ARTIFACT_TYPE_JAR, "dist.jar",
                    evaluator(), "jar", "clean"), // NOI18N
            };
        }

    }

    private static final class RecommendedTemplatesImpl implements
            RecommendedTemplates, PrivilegedTemplates
    {

        RecommendedTemplatesImpl( UpdateHelper helper ) {
            this.helper = helper;
        }

        private UpdateHelper helper;

        // List of primarily supported templates

        private static final String[] APPLICATION_TYPES = new String[] {
                "javafx", // NOI18N
                "java-classes", // NOI18N
                "java-main-class", // NOI18N
                "java-forms", // NOI18N
                "gui-java-application", // NOI18N
                "java-beans", // NOI18N
                "persistence", // NOI18N
                "oasis-XML-catalogs", // NOI18N
                "XML", // NOI18N
                "ant-script", // NOI18N
                "ant-task", // NOI18N
                "web-service-clients", // NOI18N
                "wsdl", // NOI18N
                // "servlet-types", // NOI18N
                // "web-types", // NOI18N
                "junit", // NOI18N
                // "MIDP", // NOI18N
                "simple-files" // NOI18N
        };

        private static final String[] LIBRARY_TYPES = new String[] {
                "javafx", // NOI18N
                "java-classes", // NOI18N
                "java-main-class", // NOI18N
                "java-forms", // NOI18N
                // "gui-java-application", // NOI18N
                "java-beans", // NOI18N
                "persistence", // NOI18N
                "oasis-XML-catalogs", // NOI18N
                "XML", // NOI18N
                "ant-script", // NOI18N
                "ant-task", // NOI18N
                "servlet-types", // NOI18N
                "web-service-clients", // NOI18N
                "wsdl", // NOI18N
                // "web-types", // NOI18N
                "junit", // NOI18N
                // "MIDP", // NOI18N
                "simple-files" // NOI18N
        };

        private static final String[] PRIVILEGED_NAMES = new String[] {
                "Templates/JavaFX/JavaFXEmpty.fx", // NOI18N
                "Templates/JavaFX/JavaFXClass.fx", // NOI18N
                "Templates/Classes/Class.java", // NOI18N
                "Templates/Classes/Package", // NOI18N
                "Templates/Classes/Interface.java", // NOI18N
//                "Templates/GUIForms/JPanel.java", // NOI18N
//                "Templates/GUIForms/JFrame.java", // NOI18N
//                "Templates/Persistence/Entity.java", // NOI18N

//                "Templates/Persistence/RelatedCMP", // NOI18N
//                "Templates/WebServices/WebServiceClient" // NOI18N
        };



        public String[] getRecommendedTypes() {

            EditableProperties ep = helper
                    .getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
            // if the project has no main class, it's not really an application
            boolean isLibrary = ep
                    .getProperty(JavaFXProjectProperties.MAIN_CLASS) == null
                    || "".equals(ep
                            .getProperty(JavaFXProjectProperties.MAIN_CLASS)); // NOI18N
            return isLibrary ? LIBRARY_TYPES : APPLICATION_TYPES;
        }

        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }

    }



    private class JavaFXExtenderImplementation implements 
            AntBuildExtenderImplementation
    {

        //add targets here as required by the external plugins..
        public List<String> getExtensibleTargets() {
            String[] targets = new String[] { "-do-init", "-init-check",
                    "-post-clean", "jar", "-pre-pre-compile", "-do-compile",
                    "-do-compile-single" };
            return Arrays.asList(targets);
        }

        public Project getOwningProject() {
            return JavaFXProject.this;
        }

    }

}
