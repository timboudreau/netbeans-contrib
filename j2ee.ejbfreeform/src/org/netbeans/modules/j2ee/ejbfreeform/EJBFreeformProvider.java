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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.File;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;


/**
 * An ejb module implementation on top of project.
 *
 * @author Martin Adamek
 */
public class EJBFreeformProvider extends J2eeModuleProvider implements ModuleChangeReporter, 
        EjbChangeDescriptor {
    
    private Project project;
    private AntProjectHelper helper;
    private PropertyEvaluator evaluator;
    private J2eeModule j2eeModule;
    
    /** Creates a new instance of EJBFreeformProvider */
    public EJBFreeformProvider(Project project, AntProjectHelper helper, PropertyEvaluator evaluator, EJBFreeformModule ejbFreeMod) {
        this.project = project;
        this.helper = helper;
        this.evaluator = evaluator;
        j2eeModule = J2eeModuleFactory.createJ2eeModule(ejbFreeMod);
    }
    
    // from J2eeModuleProvider
    
    public ModuleChangeReporter getModuleChangeReporter() {
        return this;
    }
    
    public J2eeModule getJ2eeModule() {
        return j2eeModule;
    }
    
    //  from ModuleChangeReporter
    
    public boolean isManifestChanged(long timestamp) {
        return false;
    }
    
    public EjbChangeDescriptor getEjbChanges(long timestamp) {
        return this;
    }
    
    // from EjbChangeDescriptor
    
    public String[] getChangedEjbs() {
        return new String[] {};
    }
    
    public boolean ejbsChanged() {
        return false;
    }
    
    public boolean useDefaultServer() {
        return false;
    }
    
    public FileObject[] getSourceRoots() {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        FileObject[] roots = new FileObject[groups.length+1];
        roots[0] = EjbJar.getEjbJars(project)[0].getMetaInf();
        for (int i=0; i < groups.length; i++) {
            roots[i+1] = groups[i].getRootFolder();
        }
        
        return roots;
    }
    
    public void setServerInstanceID(String severInstanceID) {
        // TODO implement when needed
    }
    
    public String getServerInstanceID() {
        return evaluator.getProperty(EjbFreeformProperties.J2EE_SERVER_INSTANCE);
    }
    
    public String getServerID() {
        return evaluator.getProperty(EjbFreeformProperties.J2EE_SERVER_TYPE);
    }
    
    // private methods
    
    private File getFile(String propname) {
        String prop = evaluator.getProperty(propname);
        if (prop != null) {
            return helper.resolveFile(prop);
        }
        return null;
    }
}
