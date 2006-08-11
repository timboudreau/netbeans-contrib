/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.modules.ant.freeform.spi.HelpIDFragmentProvider;
import org.netbeans.modules.ant.freeform.spi.ProjectNature;
import org.netbeans.modules.ant.freeform.spi.ProjectPropertiesPanel;
import org.netbeans.modules.ant.freeform.spi.TargetDescriptor;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.ejbfreeform.ui.EJBLocationsPanel;
import org.netbeans.modules.j2ee.spi.ejbjar.support.J2eeProjectView;
import org.netbeans.modules.j2ee.spi.ejbjar.support.EjbEnterpriseReferenceContainerSupport;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;


/**
 * @author David Konecny
 */
public class EJBProjectNature implements ProjectNature {

    public static final String NS_EJB = "http://www.netbeans.org/ns/freeform-project-ejb/1"; // NOI18N
    private static final String SCHEMA = "nbres:/org/netbeans/modules/j2ee/ejbfreeform/resources/freeform-project-ejb.xsd"; // NOI18N
    public static final String STYLE_CONFIG_FILES = "configFiles"; // NOI18N
    public static final String STYLE_EJBS = "ejbs"; // NOI18N

    private static final String HELP_ID_FRAGMENT = "ejb"; // NOI18N
    
    private static final WeakHashMap/*<Project,WeakReference<Lookup>>*/ lookupCache = new WeakHashMap();

    private List schemas = new ArrayList();
    
    public EJBProjectNature() {}

    public Lookup getLookup(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        WeakReference wr = (WeakReference)lookupCache.get(project);
        Lookup lookup = wr != null ? (Lookup)wr.get() : null;
        if (lookup == null) {
            lookup = new ProjectLookup(project, projectHelper, projectEvaluator, aux);
            lookupCache.put(project, new WeakReference(lookup));
        }
        return lookup;
    }
    
    public Set getCustomizerPanels(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        HashSet l = new HashSet();
        if (!isMyProject(aux)) {
            return l;
        }
        ProjectPropertiesPanel ejb = new EJBLocationsPanel.Panel(project, projectHelper, projectEvaluator, aux);
        l.add(ejb);
        return l;
    }
    
    public List getExtraTargets(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        ArrayList l = new ArrayList();
        if (!isMyProject(aux)) {
            return l;
        }
        l.add(getExtraTarget());
        return l;
    }
    
    public Set/*<String>*/ getSchemas() {
        return Collections.singleton(SCHEMA);
    }

    public Set/*<String>*/ getSourceFolderViewStyles() {
        Set resultSet = new HashSet();
        resultSet.add(STYLE_CONFIG_FILES);
        resultSet.add(STYLE_EJBS);
        return resultSet;
    }
    
    public Node createSourceFolderView(Project project, FileObject folder, String style, String name, String displayName) throws IllegalArgumentException {
        if (style.equals(STYLE_CONFIG_FILES)) {
            EjbJar ejbJar = EjbJar.getEjbJar(folder);
            assert ejbJar != null;
            return J2eeProjectView.createConfigFilesView(ejbJar.getMetaInf());
        } else if (style.equals(STYLE_EJBS)) {
            EjbJar ejbJar = EjbJar.getEjbJar(folder);
            assert ejbJar != null;
            FileObject ddFile = ejbJar.getDeploymentDescriptor();
            org.netbeans.modules.j2ee.dd.api.ejb.EjbJar model;
            try {
                model = DDProvider.getDefault().getDDRoot(ddFile);
                ClassPath cp = org.netbeans.spi.java.classpath.support.ClassPathSupport.createClassPath(ejbJar.getJavaSources());
                return J2eeProjectView.createEjbsView(model, cp, ddFile, project);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        throw new IllegalArgumentException();
    }

    public Node findSourceFolderViewPath(Project project, Node root, Object target) {
        // handle the Configuration Files node
        
        DataObject rootDO = (DataObject)root.getLookup().lookup(DataObject.class);
        if (rootDO == null) {
            return null;
        }
        
        DataObject targetDO = null;
        
        if (target instanceof DataObject) {
            targetDO = (DataObject)target;
        } else if (target instanceof FileObject) {
            try {
                targetDO = DataObject.find((FileObject)target);
            } catch (DataObjectNotFoundException ex) {
                return null;
            }
        } else {
            return null;
        }
        
        if (!FileUtil.isParentOf(rootDO.getPrimaryFile(), targetDO.getPrimaryFile())) {
            // target is not under root
            return null;
        }
        
        try {
            return NodeOp.findPath(root, new String[] { targetDO.getNodeDelegate().getName() });
        } catch (NodeNotFoundException ex) {
            return null;
        }
    }

    private static boolean isMyProject(AuxiliaryConfiguration aux) {
        return aux.getConfigurationFragment("ejb-data", NS_EJB, true) != null; // NOI18N
    }
    
    public static TargetDescriptor getExtraTarget() {
        return new TargetDescriptor("deploy", Arrays.asList(new String[]{"deploy", ".*deploy.*"}),  // NOI18N
            NbBundle.getMessage(EJBProjectNature.class, "LBL_TargetMappingPanel_Deploy"), // NOI18N
            NbBundle.getMessage(EJBProjectNature.class, "ACSD_TargetMappingPanel_Deploy")); // NOI18N
    }
    
    private static Lookup initLookup(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        
        return Lookups.fixed(new Object[] {
            new EJBFreeformProvider(project, projectHelper, projectEvaluator),
            new EJBModules(project, projectHelper, projectEvaluator), // EJBModuleProvider, ClassPathProvider
            new PrivilegedTemplatesImpl(), // List of templates in New action popup
            EjbEnterpriseReferenceContainerSupport.createEnterpriseReferenceContainer(project, projectHelper),
            new EjbFreeFormActionProvider(project, projectHelper, aux),
            new ProjectOpenedHookImpl(project),
            new HelpIDFragmentProviderImpl(),
        });
    }
    
    private static final class HelpIDFragmentProviderImpl implements HelpIDFragmentProvider {
        public String getHelpIDFragment() {
            return HELP_ID_FRAGMENT;
        }
    }
    
    private static final class ProjectLookup extends ProxyLookup implements AntProjectListener {

        private AntProjectHelper helper;
        private PropertyEvaluator evaluator;
        private Project project;
        private AuxiliaryConfiguration aux;
        private boolean isMyProject;
        
        public ProjectLookup(Project project, AntProjectHelper helper, PropertyEvaluator evaluator, AuxiliaryConfiguration aux) {
            super(new Lookup[0]);
            this.project = project;
            this.helper = helper;
            this.evaluator = evaluator;
            this.aux = aux;
            this.isMyProject = isMyProject(aux);
            updateLookup();
            helper.addAntProjectListener(this);
        }
        
        private void updateLookup() {
            Lookup l = Lookup.EMPTY;
            if (isMyProject) {
                l = initLookup(project, helper, evaluator, aux);
            }
            setLookups(new Lookup[]{l});
        }
        
        public void configurationXmlChanged(AntProjectEvent ev) {
            if (isMyProject(aux) != isMyProject) {
                isMyProject = !isMyProject;
                updateLookup();
            }
        }
        
        public void propertiesChanged(AntProjectEvent ev) {
            // ignore
        }
        
    }
    
    private static final class ProjectOpenedHookImpl extends ProjectOpenedHook {
        
        private Project project;
        
        public ProjectOpenedHookImpl(Project project) {
            this.project = project;
        }
        
        protected void projectOpened() {
            // initialize the server configuration
            J2eeModuleProvider j2eeModule = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
            j2eeModule.getConfigSupport().ensureConfigurationReady();
        }

        protected void projectClosed() {
        }
    }

    private static final class PrivilegedTemplatesImpl implements PrivilegedTemplates, RecommendedTemplates {
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/J2EE/Session", // NOI18N
            "Templates/J2EE/RelatedCMP", // NOI18N
            "Templates/J2EE/Entity",  // NOI18N
            "Templates/J2EE/Message", //NOI18N
            "Templates/J2EE/ServiceLocator.java", // NOI18N
            "Templates/Classes/Class.java" // NOI18N
        };
        
        private static final String[] RECOMENDED_TYPES = new String[] {
            "java-classes",         // NOI18N
            "ejb-types",            // NOI18N
            "j2ee-types",           // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "wsdl",                 // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "junit",                // NOI18N
            "simple-files"          // NOI18N
        };
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }

        public String[] getRecommendedTypes() {
            return RECOMENDED_TYPES;
        }
        
        
        
    }
    
}
