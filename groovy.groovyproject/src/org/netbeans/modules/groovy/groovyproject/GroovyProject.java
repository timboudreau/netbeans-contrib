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

package org.netbeans.modules.groovy.groovyproject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.modules.groovy.groovyproject.classpath.ClassPathProviderImpl;
import org.netbeans.modules.groovy.groovyproject.classpath.GroovyProjectClassPathExtender;
import org.netbeans.modules.groovy.groovyproject.queries.CompiledSourceForBinaryQuery;
import org.netbeans.modules.groovy.groovyproject.queries.SourceLevelQueryImpl;
import org.netbeans.modules.groovy.groovyproject.ui.GroovyCustomizerProvider;
import org.netbeans.modules.groovy.groovyproject.ui.GroovyPhysicalViewProvider;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.GroovyProjectProperties;
import org.netbeans.spi.java.project.support.ui.BrokenReferencesSupport;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.netbeans.spi.queries.FileBuiltQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
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
 * Represents one plain J2SE project.
 * @author Jesse Glick, et al.
 */
final class GroovyProject implements Project, AntProjectListener {
    
    private static final Icon GROOVY_PROJECT_ICON = new ImageIcon(Utilities.loadImage("org/netbeans/modules/groovy/groovyproject/resources/groovyProject.gif")); // NOI18N
        
    private final AntProjectHelper helper;
    private final PropertyEvaluator eval;
    private final ReferenceHelper refHelper;
    private final GeneratedFilesHelper genFilesHelper;
    private final Lookup lookup;

    GroovyProject(AntProjectHelper helper) throws IOException {
        this.helper = helper;
        eval = createEvaluator();
        AuxiliaryConfiguration aux = helper.createAuxiliaryConfiguration();
        refHelper = new ReferenceHelper(helper, aux, eval);
        genFilesHelper = new GeneratedFilesHelper(helper);
        lookup = createLookup(aux);
        helper.addAntProjectListener(this);
    }

    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }

    public String toString() {
        return "GroovyProject[" + getProjectDirectory() + "]"; // NOI18N
    }
    
    private PropertyEvaluator createEvaluator() {
        // XXX might need to use a custom evaluator to handle active platform substitutions... TBD
        return helper.getStandardPropertyEvaluator();
    }
    
    PropertyEvaluator evaluator() {
        return eval;
    }

    ReferenceHelper getReferenceHelper () {
        return this.refHelper;
    }

    public Lookup getLookup() {
        return lookup;
    }

    private Lookup createLookup(AuxiliaryConfiguration aux) {
        SubprojectProvider spp = refHelper.createSubprojectProvider();
        FileBuiltQueryImplementation fileBuilt = helper.createGlobFileBuiltQuery(evaluator(), new String[] {
            "${" + GroovyProjectProperties.SRC_DIR + "}/*.groovy", // NOI18N
        }, new String[] {
            "${" + GroovyProjectProperties.BUILD_CLASSES_DIR + "}/*.class", // NOI18N
        });
        final SourcesHelper sourcesHelper = new SourcesHelper(helper, evaluator());
        sourcesHelper.addPrincipalSourceRoot("${" + GroovyProjectProperties.SRC_DIR + "}", NbBundle.getMessage(GroovyProject.class, "NAME_src.dir"), /*XXX*/null, null);
        // XXX add build dir too?
        sourcesHelper.addTypedSourceRoot("${" + GroovyProjectProperties.SRC_DIR + "}", GroovyProjectType.SOURCES_TYPE_GROOVY, NbBundle.getMessage(GroovyProject.class, "NAME_src.dir"), /*XXX*/null, null);
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {
                sourcesHelper.registerExternalRoots(FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
            }
        });
        return Lookups.fixed(new Object[] {
            new Info(),
            aux,
            helper.createCacheDirectoryProvider(),
            spp,
            new GroovyActionProvider( this, helper ),
            new GroovyPhysicalViewProvider(this, helper, evaluator(), spp, refHelper),
            new GroovyCustomizerProvider(this, helper, evaluator(), refHelper),
            new ClassPathProviderImpl(helper, evaluator()),
            new CompiledSourceForBinaryQuery(helper, evaluator()),
            new AntArtifactProviderImpl(),
            new ProjectXmlSavedHookImpl(),
            new ProjectOpenedHookImpl(),
            new SourceLevelQueryImpl(helper, evaluator()),
            sourcesHelper.createSources(),
            helper.createSharabilityQuery(evaluator(), new String[] {
                "${" + GroovyProjectProperties.SRC_DIR + "}", // NOI18N
            }, new String[] {
                "${" + GroovyProjectProperties.DIST_DIR + "}", // NOI18N
                "${" + GroovyProjectProperties.BUILD_DIR + "}", // NOI18N
            }),
            fileBuilt,
            new RecommendedTemplatesImpl (helper),
            new GroovyProjectClassPathExtender(this, helper, eval,refHelper),
            this, // never cast an externally obtained Project to J2SEProject - use lookup instead
        });
    }

    public void configurationXmlChanged(AntProjectEvent ev) {
        if (ev.getPath().equals(AntProjectHelper.PROJECT_XML_PATH)) {
            // Could be various kinds of changes, but name & displayName might have changed.
            Info info = (Info)getLookup().lookup(ProjectInformation.class);
            info.firePropertyChange(ProjectInformation.PROP_NAME);
            info.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
        }
    }

    public void propertiesChanged(AntProjectEvent ev) {
        // currently ignored (probably better to listen to evaluator() if you need to)
    }
    
    // Package private methods -------------------------------------------------
    
    FileObject getSourceDirectory() {
        String srcDir = evaluator().getProperty(GroovyProjectProperties.SRC_DIR);
        if (srcDir == null) {
            return null;
        }
        return helper.resolveFileObject(srcDir);
    }
    
    // Currently unused (but see #47230):
    /** Store configured project name. * /
    public void setName(final String name) {
        ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                Element data = helper.getPrimaryConfigurationData(true);
                // XXX replace by XMLUtil when that has findElement, findText, etc.
                NodeList nl = data.getElementsByTagNameNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name");
                Element nameEl;
                if (nl.getLength() == 1) {
                    nameEl = (Element) nl.item(0);
                    NodeList deadKids = nameEl.getChildNodes();
                    while (deadKids.getLength() > 0) {
                        nameEl.removeChild(deadKids.item(0));
                    }
                } else {
                    nameEl = data.getOwnerDocument().createElementNS(J2SEProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name");
                    data.insertBefore(nameEl, / * OK if null * /data.getChildNodes().item(0));
                }
                nameEl.appendChild(data.getOwnerDocument().createTextNode(name));
                helper.putPrimaryConfigurationData(data, true);
                return null;
            }
        });
    }
     */
    
    // Private innerclasses ----------------------------------------------------
    
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
            return (String) ProjectManager.mutex().readAccess(new Mutex.Action() {
                public Object run() {
                    Element data = helper.getPrimaryConfigurationData(true);
                    // XXX replace by XMLUtil when that has findElement, findText, etc.
                    NodeList nl = data.getElementsByTagNameNS(GroovyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
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
            return GROOVY_PROJECT_ICON;
        }
        
        public Project getProject() {
            return GroovyProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
    }
    
    private final class ProjectXmlSavedHookImpl extends ProjectXmlSavedHook {
        
        ProjectXmlSavedHookImpl() {}
        
        protected void projectXmlSaved() throws IOException {
            genFilesHelper.refreshBuildScript(
                GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                GroovyProject.class.getResource("resources/build-impl.xsl"),
                false);
            genFilesHelper.refreshBuildScript(
                GeneratedFilesHelper.BUILD_XML_PATH,
                GroovyProject.class.getResource("resources/build.xsl"),
                false);
        }
        
    }
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook {
        
        ProjectOpenedHookImpl() {}
        
        protected void projectOpened() {
            // Check up on build scripts.
            try {
                genFilesHelper.refreshBuildScript(
                    GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                    GroovyProject.class.getResource("resources/build-impl.xsl"),
                    true);
                genFilesHelper.refreshBuildScript(
                    GeneratedFilesHelper.BUILD_XML_PATH,
                    GroovyProject.class.getResource("resources/build.xsl"),
                    true);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            
            // register project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)lookup.lookup(ClassPathProviderImpl.class);
            GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));

            //register updater of main.script
            //the updater is active only on the opened projects
//            mainScriptUpdater = new MainScriptUpdater (J2SEProject.this, eval, helper,
//                    cpProvider.getProjectClassPaths(ClassPath.SOURCE)[0], J2SEProjectProperties.MAIN_CLASS);

            // Make it easier to run headless builds on the same machine at least.
            ProjectManager.mutex().writeAccess(new Mutex.Action() {
                public Object run() {
                    EditableProperties ep = helper.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
                    File buildProperties = new File(System.getProperty("netbeans.user"), "build.properties"); // NOI18N
                    ep.setProperty("user.properties.file", buildProperties.getAbsolutePath()); //NOI18N
                    helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);
                    try {
                        ProjectManager.getDefault().saveProject(GroovyProject.this);
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                    return null;
                }
            });
            if (GroovyPhysicalViewProvider.hasBrokenLinks(helper, refHelper)) {
                BrokenReferencesSupport.showAlert();
            }
        }
        
        protected void projectClosed() {
            // Probably unnecessary, but just in case:
            try {
                ProjectManager.getDefault().saveProject(GroovyProject.this);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
            
            // unregister project's classpaths to GlobalPathRegistry
            ClassPathProviderImpl cpProvider = (ClassPathProviderImpl)lookup.lookup(ClassPathProviderImpl.class);
            GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
            GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            GlobalPathRegistry.getDefault().unregister(ClassPath.COMPILE, cpProvider.getProjectClassPaths(ClassPath.COMPILE));
        }
        
    }
    
    /**
     * Exports the main JAR as an official build product for use from other scripts.
     * The type of the artifact will be {@link AntArtifact#TYPE_JAR}.
     */
    private final class AntArtifactProviderImpl implements AntArtifactProvider {

        public AntArtifact[] getBuildArtifacts() {
            return new AntArtifact[] {
                helper.createSimpleAntArtifact(JavaProjectConstants.ARTIFACT_TYPE_JAR, "dist.jar", evaluator(), "jar", "clean"), // NOI18N
            };
        }

    }
    
    private static final class RecommendedTemplatesImpl implements RecommendedTemplates, PrivilegedTemplates {
        RecommendedTemplatesImpl (AntProjectHelper helper) {
            this.helper = helper;
        }
        
        private AntProjectHelper helper;
        
        // List of primarily supported templates
        
        private static final String[] APPLICATION_TYPES = new String[] { 
            "groovy",         // NOI18N
            // "MIDP",              // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] LIBRARY_TYPES = new String[] { 
            "groovy",         // NOI18N
            // "MIDP",              // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Scripting/groovy.groovy", // NOI18N
        };
        
        public String[] getRecommendedTypes() {
            
            EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
            // if the project has no main script, it's not really an application
            boolean isLibrary = ep.getProperty (GroovyProjectProperties.MAIN_SCRIPT) == null || "".equals (ep.getProperty (GroovyProjectProperties.MAIN_SCRIPT)); // NOI18N
            return isLibrary ? LIBRARY_TYPES : APPLICATION_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
    }
    
}
