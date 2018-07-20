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

package org.netbeans.modules.j2ee.jetty.ide;

import org.netbeans.modules.j2ee.jetty.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;



/**
 * Implementation of J2eePlatformImpl which is used to describe the target
 * environment that J2EE applications are built against and subsequently
 * deployed to. It provides a set of server libraries, supported module types,
 * and J2EE specification versions.
 * @author novakm
 */
public class JetJ2eePlatformImpl extends J2eePlatformImpl {
    
    private JetDeploymentManager dm;
    private LibraryImplementation[] libraries;
    private JetPluginProperties properties;
    
    /**
     * Initializes libraries for given DM
     * @param dm
     */
    public JetJ2eePlatformImpl(JetDeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        initLibraries();
    }

    /**
     * notifies that libraries have changed and initializes them
     */
    public void notifyLibrariesChanged() {
        initLibraries();
        firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
    }
    
    private void initLibraries() {
        LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
        lib.setName(NbBundle.getMessage(JetJ2eePlatformImpl.class, "TITLE_JET_LIBRARY"));
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, properties.getClasses());
        libraries = new LibraryImplementation[] {lib};
    }
    
    public boolean isToolSupported(String toolName) {
        return false;
    }
    
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }

    /**
     * Returns set of supported J2EE versions
     * @return Set containing supported J2EE versions
     */
    @SuppressWarnings("unchecked")    
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_14);
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }

     /**
     * Returns set of supported module types
     * @return Set containing supported module types
     */
    @SuppressWarnings("unchecked")    
    public java.util.Set getSupportedModuleTypes() {
        Set result = new HashSet();
        result.add(J2eeModule.WAR);
        return result;
    }
    
    public java.io.File[] getPlatformRoots() {
        return new File[0];
    }
    
    public LibraryImplementation[] getLibraries() {
        if (libraries==null) {
            initLibraries();
        }
        return libraries;
    }
    
    public java.awt.Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/j2ee/jetty/resources/jicon.gif"); // NOI18N
        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(JetJ2eePlatformImpl.class, "MSG_JetServerPlatform");
    }
    
    @SuppressWarnings("unchecked")    
    public Set getSupportedJavaPlatformVersions() {
        Set versions = new HashSet();
        versions.add("1.4"); // NOI18N
        versions.add("1.5"); // NOI18N
        return versions;
    }
    
    public JavaPlatform getJavaPlatform() {
        return JavaPlatformManager.getDefault().getDefaultPlatform();
    }
}