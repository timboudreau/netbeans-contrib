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

package org.netbeans.modules.j2ee.oc4j.ide;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.api.J2eePlatformImpl;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author pblaha
 */
public class OC4JJ2eePlatformImpl extends J2eePlatformImpl {
    
    private OC4JDeploymentManager dm;
    private LibraryImplementation[] libraries;
    private OC4JPluginProperties properties;
    
    public OC4JJ2eePlatformImpl(OC4JDeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        initLibraries();
    }
    
    public boolean isToolSupported(String toolName) {
        if ("oracle.toplink.essentials.ejb.cmp3.EntityManagerFactoryProvider".equals(toolName)) {
            return true;
        }
        return false;
    }
    
    
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }
    
    public Set getSupportedSpecVersions() {
        Set <String> result = new HashSet<String>();
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    
    public java.util.Set getSupportedModuleTypes() {
        Set <Object> result = new HashSet<Object>();
        result.add(J2eeModule.EAR);
        result.add(J2eeModule.WAR);
        result.add(J2eeModule.EJB);
        return result;
    }
    
    public java.io.File[] getPlatformRoots() {
        return new File[0];
    }
    
    public LibraryImplementation[] getLibraries() {
        return libraries;
    }
    
    public java.awt.Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/j2ee/myserver/resources/server.gif"); // NOI18N
        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(OC4JJ2eePlatformImpl.class, "MSG_OC4JServerPlatform");
    }
    
    public Set getSupportedJavaPlatformVersions() {
        return null;
    }
    
    public org.netbeans.api.java.platform.JavaPlatform getJavaPlatform() {
        return null;
    }
    
    public void notifyLibrariesChanged() {
        initLibraries();
        firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
    }
    
    private void initLibraries() {
        // create library
        LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
        lib.setName(NbBundle.getMessage(OC4JJ2eePlatformImpl.class, "TITLE_OC4J_LIBRARY"));
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, properties.getClasses());
        libraries = new LibraryImplementation[] {lib};
    }
}