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

package org.netbeans.modules.erlang.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import javax.swing.Icon;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.erlang.makeproject.spi.support.FilterPropertyProvider;
import org.netbeans.modules.erlang.makeproject.spi.support.GeneratedFilesHelper;
import org.netbeans.modules.erlang.makeproject.spi.support.PropertyEvaluator;
import org.netbeans.modules.erlang.makeproject.spi.support.PropertyProvider;
import org.netbeans.modules.erlang.makeproject.spi.support.PropertyUtils;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeProjectEvent;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeProjectHelper;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeProjectListener;
import org.netbeans.modules.erlang.makeproject.spi.support.ReferenceHelper;
import org.netbeans.modules.erlang.platform.api.RubyPlatformProvider;
import org.netbeans.modules.erlang.project.classpath.ClassPathProviderImpl;
import org.netbeans.modules.erlang.project.queries.RubyProjectEncodingQueryImpl;
import org.netbeans.modules.erlang.project.ui.RubyLogicalViewProvider;
import org.netbeans.modules.erlang.project.ui.customizer.CustomizerProviderImpl;
import org.netbeans.modules.erlang.project.ui.customizer.RubyProjectProperties;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents one plain Ruby project.
 * @author Jesse Glick, et al.
 */
public final class RubyProject implements Project, RakeProjectListener {

    /** @todo should use ErlangLanguae definitons of these ids: */
    public static final String BOOT    = "erlang/classpath/boot";
    public static final String COMPILE = "erlang/classpath/compile";
    public static final String EXECUTE = "erlang/classpath/execute";
    public static final String SOURCE  = "erlang/classpath/source";
    
    /**
     * Ruby package root sources type.
     * @see org.netbeans.api.project.Sources
     */
    public static final String SOURCES_TYPE_RUBY = "erlang"; // NOI18N

    private static final Icon Ruby_PROJECT_ICON = ImageUtilities.loadImageIcon("org/netbeans/modules/erlang/project/ui/resources/erlang.png", false); // NOI18N
    
    private final AuxiliaryConfiguration aux;
    private final RakeProjectHelper helper;
    private final PropertyEvaluator eval;
    private final ReferenceHelper refHelper;
    private final GeneratedFilesHelper genFilesHelper;
    private final Lookup lookup;
    private final UpdateHelper updateHelper;
    private SourceRoots sourceRoots;
    private SourceRoots includeRoots;
    private SourceRoots testRoots;
    
    RubyProject(RakeProjectHelper helper) throws IOException {
        this.helper = helper;
        eval = createEvaluator();
        aux = helper.createAuxiliaryConfiguration();
        refHelper = new ReferenceHelper(helper, aux, eval);
        genFilesHelper = new GeneratedFilesHelper(helper);
        this.updateHelper = new UpdateHelper (this, this.helper, this.aux, this.genFilesHelper,
            UpdateHelper.createDefaultNotifier());

        lookup = createLookup(aux);
        helper.addRakeProjectListener(this);
    }

    /**
     * Returns the project directory
     * @return the directory the project is located in
     */
    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }
    
    @Override
    public String toString() {
        return "ErlangProject[" + FileUtil.getFileDisplayName(getProjectDirectory()) + "]"; // NOI18N
    }

    private PropertyEvaluator createEvaluator() {
        // It is currently safe to not use the UpdateHelper for PropertyEvaluator; UH.getProperties() delegates to APH
        // Adapted from APH.getStandardPropertyEvaluator (delegates to ProjectProperties):
        PropertyEvaluator baseEval1 = PropertyUtils.sequentialPropertyEvaluator(
                helper.getStockPropertyPreprovider(),
                helper.getPropertyProvider(RubyConfigurationProvider.CONFIG_PROPS_PATH));
        PropertyEvaluator baseEval2 = PropertyUtils.sequentialPropertyEvaluator(
                helper.getStockPropertyPreprovider(),
                helper.getPropertyProvider(RakeProjectHelper.PRIVATE_PROPERTIES_PATH));
        return PropertyUtils.sequentialPropertyEvaluator(
                helper.getStockPropertyPreprovider(),
                helper.getPropertyProvider(RubyConfigurationProvider.CONFIG_PROPS_PATH),
                new ConfigPropertyProvider(baseEval1, "nbproject/private/configs", helper), // NOI18N
                helper.getPropertyProvider(RakeProjectHelper.PRIVATE_PROPERTIES_PATH),
                PropertyUtils.userPropertiesProvider(baseEval2,
                    "user.properties.file", FileUtil.toFile(getProjectDirectory())), // NOI18N
                new ConfigPropertyProvider(baseEval1, "nbproject/configs", helper), // NOI18N
                helper.getPropertyProvider(RakeProjectHelper.PROJECT_PROPERTIES_PATH));
    }
    
    private static final class ConfigPropertyProvider extends FilterPropertyProvider implements PropertyChangeListener {
        private final PropertyEvaluator baseEval;
        private final String prefix;
        private final RakeProjectHelper helper;
        public ConfigPropertyProvider(PropertyEvaluator baseEval, String prefix, RakeProjectHelper helper) {
            super(computeDelegate(baseEval, prefix, helper));
            this.baseEval = baseEval;
            this.prefix = prefix;
            this.helper = helper;
            baseEval.addPropertyChangeListener(this);
        }
        public void propertyChange(PropertyChangeEvent ev) {
            if (RubyConfigurationProvider.PROP_CONFIG.equals(ev.getPropertyName())) {
                setDelegate(computeDelegate(baseEval, prefix, helper));
            }
        }
        private static PropertyProvider computeDelegate(PropertyEvaluator baseEval, String prefix, RakeProjectHelper helper) {
            String config = baseEval.getProperty(RubyConfigurationProvider.PROP_CONFIG);
            if (config != null) {
                return helper.getPropertyProvider(prefix + "/" + config + ".properties"); // NOI18N
            } else {
                return PropertyUtils.fixedPropertyProvider(Collections.<String,String>emptyMap());
            }
        }
    }
    
    public PropertyEvaluator evaluator() {
        return eval;
    }

    ReferenceHelper getReferenceHelper () {
        return this.refHelper;
    }

    public UpdateHelper getUpdateHelper() {
        return this.updateHelper;
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public RakeProjectHelper getRakeProjectHelper() {
        return helper;
    }

    private Lookup createLookup(AuxiliaryConfiguration aux) {
        SubprojectProvider spp = refHelper.createSubprojectProvider();
        Lookup base = Lookups.fixed(new Object[] {
            new Info(),
            aux,
            helper.createCacheDirectoryProvider(),
            spp,
            new RubyActionProvider( this, this.updateHelper ),
            new RubyLogicalViewProvider(this, this.updateHelper, evaluator(), spp, refHelper),
            new ClassPathProviderImpl(this.helper, evaluator(), getSourceRoots(), getIncludeRoots(), getTestSourceRoots()), //Does not use APH to get/put properties/cfgdata
            // new RubyCustomizerProvider(this, this.updateHelper, evaluator(), refHelper),
            new CustomizerProviderImpl(this, this.updateHelper, evaluator(), refHelper, this.genFilesHelper),        
            new ProjectOpenedHookImpl(),
            new RubySources (this.helper, evaluator(), getSourceRoots(), getIncludeRoots() ,getTestSourceRoots()),
            new RubySharabilityQuery (this.helper, evaluator(), getSourceRoots(), getIncludeRoots(), getTestSourceRoots()), //Does not use APH to get/put properties/cfgdata
            new RubyFileBuiltQuery (this.helper, evaluator(),getSourceRoots(), getIncludeRoots(), getTestSourceRoots()), //Does not use APH to get/put properties/cfgdata
            new RecommendedTemplatesImpl (this.updateHelper),
            this, // never cast an externally obtained Project to RubyProject - use lookup instead
            new RubyProjectOperations(this),
            new RubyConfigurationProvider(this),
            UILookupMergerSupport.createPrivilegedTemplatesMerger(),
            UILookupMergerSupport.createRecommendedTemplatesMerger(),
            LookupProviderSupport.createSourcesMerger(),            
            new RubyProjectEncodingQueryImpl(evaluator()),
            evaluator(),
            new RubyFileLocator(null, this),
            new RubyPlatformProvider(evaluator())
        });
        return LookupProviderSupport.createCompositeLookup(base, "Projects/org-netbeans-modules-erlang-project/Lookup"); //NOI18N
    }
    
    public void configurationXmlChanged(RakeProjectEvent ev) {
        if (ev.getPath().equals(RakeProjectHelper.PROJECT_XML_PATH)) {
            // Could be various kinds of changes, but name & displayName might have changed.
            Info info = (Info)getLookup().lookup(ProjectInformation.class);
            info.firePropertyChange(ProjectInformation.PROP_NAME);
            info.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
        }
    }

    public void propertiesChanged(RakeProjectEvent ev) {
        // currently ignored (probably better to listen to evaluator() if you need to)
    }
    
    // Package private methods -------------------------------------------------------------

    /**
     * Returns the source roots of this project
     * @return project's source roots
     */
    public synchronized SourceRoots getSourceRoots() {        
        if (this.sourceRoots == null) { //Local caching, no project metadata access
            this.sourceRoots = new SourceRoots(this.updateHelper, evaluator(), getReferenceHelper(), "source-roots", false, "src.{0}{1}.dir"); //NOI18N
        }
        return this.sourceRoots;
    }
    
    public synchronized SourceRoots getIncludeRoots() {        
        if (this.includeRoots == null) { //Local caching, no project metadata access
            this.includeRoots = new SourceRoots(this.updateHelper, evaluator(), getReferenceHelper(), "include-roots", false, "include.{0}{1}.dir"); //NOI18N
        }
        return this.includeRoots;
    }
    
    public synchronized SourceRoots getTestSourceRoots() {
        if (this.testRoots == null) { //Local caching, no project metadata access
            this.testRoots = new SourceRoots(this.updateHelper, evaluator(), getReferenceHelper(), "test-roots", true, "test.{0}{1}.dir"); //NOI18N
        }
        return this.testRoots;
    }
    
    File getTestClassesDirectory() {
        String testClassesDir = evaluator().getProperty(RubyProjectProperties.BUILD_TEST_CLASSES_DIR);
        if (testClassesDir == null) {
            return null;
        }
        return helper.resolveFile(testClassesDir);
    }
    
    // Currently unused (but see #47230):
    /** Store configured project name. */
    public void setName(final String name) {
        ProjectManager.mutex().writeAccess(new Mutex.Action<Void>() {
            public Void run() {
                Element data = helper.getPrimaryConfigurationData(true);
                // XXX replace by XMLUtil when that has findElement, findText, etc.
                NodeList nl = data.getElementsByTagNameNS(RubyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                Element nameEl;
                if (nl.getLength() == 1) {
                    nameEl = (Element) nl.item(0);
                    NodeList deadKids = nameEl.getChildNodes();
                    while (deadKids.getLength() > 0) {
                        nameEl.removeChild(deadKids.item(0));
                    }
                } else {
                    nameEl = data.getOwnerDocument().createElementNS(RubyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                    data.insertBefore(nameEl, /* OK if null */data.getChildNodes().item(0));
                }
                nameEl.appendChild(data.getOwnerDocument().createTextNode(name));
                helper.putPrimaryConfigurationData(data, true);
                return null;
            }
        });
    }




    // Private innerclasses ----------------------------------------------------------------
    
    private final class Info implements ProjectInformation {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        
        Info() {}
        
        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }
        
        public String getName() {
            return PropertyUtils.getUsablePropertyName(getDisplayName());
        }
        
        public String getDisplayName() {
            return ProjectManager.mutex().readAccess(new Mutex.Action<String>() {
                public String run() {
                    Element data = updateHelper.getPrimaryConfigurationData(true);
                    // XXX replace by XMLUtil when that has findElement, findText, etc.
                    NodeList nl = data.getElementsByTagNameNS(RubyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
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
        
        public Icon getIcon() {
            return Ruby_PROJECT_ICON;
        }
        
        public Project getProject() {
            return RubyProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
    }
    
    static boolean bootRegistered = false;
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook {
        
        ProjectOpenedHookImpl() {}
        
        protected void projectOpened() {
            // register project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = lookup.lookup(ClassPathProviderImpl.class);
            GlobalPathRegistry.getDefault().register(RubyProject.BOOT, cpProvider.getProjectClassPaths(RubyProject.BOOT));
            GlobalPathRegistry.getDefault().register(RubyProject.SOURCE, cpProvider.getProjectClassPaths(RubyProject.SOURCE));
            //GlobalPathRegistry.getDefault().register(RubyProject.COMPILE, cpProvider.getProjectClassPaths(RubyProject.COMPILE));
        }
        
        protected void projectClosed() {
            // unregister project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = lookup.lookup(ClassPathProviderImpl.class);
            //GlobalPathRegistry.getDefault().unregister(RubyProject.BOOT, cpProvider.getProjectClassPaths(RubyProject.BOOT));
            GlobalPathRegistry.getDefault().unregister(RubyProject.SOURCE, cpProvider.getProjectClassPaths(RubyProject.SOURCE));
            //GlobalPathRegistry.getDefault().unregister(RubyProject.COMPILE, cpProvider.getProjectClassPaths(RubyProject.COMPILE));
        }
        
    }
        
    private static final class RecommendedTemplatesImpl implements RecommendedTemplates, PrivilegedTemplates {
        RecommendedTemplatesImpl (UpdateHelper helper) {
            this.helper = helper;
        }
        
        private UpdateHelper helper;
        
        // List of primarily supported templates
        
        private static final String[] APPLICATION_TYPES = new String[] { 
            "erlang",         // NOI18N
            "XML",                  // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Erlang/main.erl", // NOI18N
            "Templates/Erlang/module.erl", // NOI18N
            "Templates/Erlang/test.erl", // NOI18N
            "Templates/Erlang/suite.erl", // NOI18N
            "Templates/Erlang/Makefile", // NOI18N
            "Templates/Erlang/EMakefile", // NOI18N
        };
        
        public String[] getRecommendedTypes() {
            return APPLICATION_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
    }
}
