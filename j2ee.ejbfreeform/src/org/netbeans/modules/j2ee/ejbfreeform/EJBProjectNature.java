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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.ant.freeform.spi.ProjectNature;
import org.netbeans.modules.ant.freeform.spi.TargetDescriptor;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.spi.ejbjar.support.J2eeProjectView;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.NbBundle;

/**
 * @author David Konecny
 */
public class EJBProjectNature implements ProjectNature {

    public static final String NS_EJB = "http://www.netbeans.org/ns/freeform-project-ejb/1"; // NOI18N
    public static final String NS_EJB_2 = "http://www.netbeans.org/ns/freeform-project-ejb/2"; // NOI18N
    private static final String SCHEMA = "nbres:/org/netbeans/modules/j2ee/ejbfreeform/resources/freeform-project-ejb.xsd"; // NOI18N
    private static final String SCHEMA_2 = "nbres:/org/netbeans/modules/j2ee/ejbfreeform/resources/freeform-project-ejb-2.xsd"; // NOI18N
    
    public static final String EL_EJB = "ejb-data"; // NOI18N
    public static final String STYLE_CONFIG_FILES = "configFiles"; // NOI18N
    public static final String STYLE_EJBS = "ejbs"; // NOI18N
    
    public EJBProjectNature() {}

   
    
    public List getExtraTargets(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        ArrayList l = new ArrayList();
        if (!isMyProject(aux)) {
            return l;
        }
        l.add(getExtraTarget());
        return l;
    }
    
    public Set/*<String>*/ getSchemas() {
        return new HashSet(Arrays.asList(new String[] { SCHEMA, SCHEMA_2 }));
    }

    public Set/*<String>*/ getSourceFolderViewStyles() {
        Set resultSet = new HashSet();
        resultSet.add(STYLE_CONFIG_FILES);
        resultSet.add(STYLE_EJBS);
        return resultSet;
    }
    
    public Node createSourceFolderView(Project project, FileObject folder, String includes, String excludes, String style, String name, String displayName) throws IllegalArgumentException {
        // XXX consider using includes/excludes
        if (style.equals(STYLE_CONFIG_FILES)) {
            EjbJar ejbJar = EjbJar.getEjbJar(folder);
            assert ejbJar != null;
            return J2eeProjectView.createConfigFilesView(ejbJar.getMetaInf());
        } else if (style.equals(STYLE_EJBS)) {
            EjbJar ejbJar = EjbJar.getEjbJar(folder);
            assert ejbJar != null;
            return J2eeProjectView.createEjbsView(ejbJar, project);
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

    public static boolean isMyProject(AuxiliaryConfiguration aux) {
        return (aux.getConfigurationFragment(EL_EJB, NS_EJB, true) != null) ||
               (aux.getConfigurationFragment(EL_EJB, NS_EJB_2, true) != null);
    }
    
    public static TargetDescriptor getExtraTarget() {
        return new TargetDescriptor("deploy", Arrays.asList(new String[]{"deploy", ".*deploy.*"}),  // NOI18N
            NbBundle.getMessage(EJBProjectNature.class, "LBL_TargetMappingPanel_Deploy"), // NOI18N
            NbBundle.getMessage(EJBProjectNature.class, "ACSD_TargetMappingPanel_Deploy")); // NOI18N
    }
    
    
}
