/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.api.project.Project;
import org.netbeans.modules.ant.freeform.spi.ProjectNature;
import org.netbeans.modules.ant.freeform.spi.ProjectPropertiesPanel;
import org.netbeans.modules.ant.freeform.spi.TargetDescriptor;
import org.netbeans.modules.j2ee.ejbfreeform.ui.EJBLocationsPanel;
import org.netbeans.modules.j2ee.ejbjarproject.ui.logicalview.LogicalViewChildren;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
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
    
    private AntProjectHelper projectHelper;
    private PropertyEvaluator projectEvaluator;

    private static final WeakHashMap/*<Project,WeakReference<Lookup>>*/ lookupCache = new WeakHashMap();

    private List schemas = new ArrayList();
    
    public EJBProjectNature() {}

    public Lookup getLookup(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        this.projectHelper = projectHelper;
        this.projectEvaluator = projectEvaluator;
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
            return new LogicalViewChildren(project, null, null, null).createNodes(LogicalViewChildren.KEY_DOC_BASE)[0];
        } else if (style.equals(STYLE_EJBS)) {
            return new LogicalViewChildren(project, null, null, null).createNodes(LogicalViewChildren.KEY_EJBS)[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Node findSourceFolderViewPath(Project project, Node root, Object target) {
        return null;
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
            new ProxyEjbJarImplementation(project, projectHelper, projectEvaluator),
            new EJBModules(project, projectHelper, projectEvaluator), // EJBModuleProvider, ClassPathProvider
            new PrivilegedTemplatesImpl(), // List of templates in New action popup
        });
    }
    
    public static final class ProxyEjbJarImplementation implements EjbJarImplementation {
        
        private Project project;
        private AntProjectHelper helper;
        private PropertyEvaluator evaluator;
        private EjbJarImplementation ejbJarImplementation;
        
        public ProxyEjbJarImplementation(Project project, AntProjectHelper helper, PropertyEvaluator evaluator) {
            this.project = project;
            this.helper = helper;
            this.evaluator = evaluator;
        }
        
        private EjbJarImplementation getEjbjarImplementation() {
            if (ejbJarImplementation == null) {
                ejbJarImplementation = new EJBModules(project, helper, evaluator).getEjbJarImplementation();
            }
            return ejbJarImplementation;
        }

        public FileObject getMetaInf() {
            return getEjbjarImplementation().getMetaInf();
        }

        public String getJ2eePlatformVersion() {
            return getEjbjarImplementation().getJ2eePlatformVersion();
        }

        public FileObject getDeploymentDescriptor() {
            return getEjbjarImplementation().getDeploymentDescriptor();
        }
        
        public FileObject[] getJavaSources() {
            return getEjbjarImplementation().getJavaSources();
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

    private static final class PrivilegedTemplatesImpl implements PrivilegedTemplates {
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/J2EE/Session", // NOI18N
            "Templates/J2EE/RelatedCMP", // NOI18N
            "Templates/J2EE/Entity",  // NOI18N
            "Templates/J2EE/Message", //NOI18N
            "Templates/J2EE/WebService", // NOI18N
            "Templates/J2EE/MessageHandler", // NOI18N
            "Templates/J2EE/ServiceLocator.java", // NOI18N
            "Templates/Classes/Class.java" // NOI18N
        };
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
    }
    
}
