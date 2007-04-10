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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.j2ee.hk2;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.netbeans.spi.project.libraries.LibraryImplementation;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;



/**
 *
 * @author Ludo
 */
public class Hk2J2eePlatformImpl extends J2eePlatformImpl {
    
    private Hk2PluginProperties properties;
    private Hk2DeploymentManager dm;
    private LibraryImplementation[] libraries;
    
    /**
     * 
     * @param dm 
     */
    public Hk2J2eePlatformImpl(Hk2DeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        initLibraries();
    }
    /**
     * 
     * @param toolName 
     * @return 
     */
    public boolean isToolSupported(String toolName) {
        return false;
    }
    
    /**
     * 
     * @param toolName 
     * @return 
     */
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }
    
    /**
     * 
     * @return 
     */
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_14);
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public java.util.Set getSupportedModuleTypes() {
        Set result = new HashSet();

        result.add(J2eeModule.WAR);

        return result;
    }
    
    /**
     * 
     * @return 
     */
    public java.io.File[] getPlatformRoots() {
        return new File[0];
    }
    
    /**
     * 
     * @return 
     */
    public LibraryImplementation[] getLibraries() {
        return libraries;
    }
    
    /**
     * 
     * @return 
     */
    public java.awt.Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/j2ee/hk2/resources/server.gif"); // NOI18N
        
    }
    
    /**
     * 
     * @return 
     */
    public String getDisplayName() {
        return NbBundle.getMessage(Hk2J2eePlatformImpl.class, "MSG_MyServerPlatform");
    }
    
    /**
     * 
     * @return 
     */
    public Set getSupportedJavaPlatformVersions() {
        Set versions = new HashSet();
        versions.add("1.4"); // NOI18N
        versions.add("1.5"); // NOI18N
        return versions;
    }
    
    /**
     * 
     * @return 
     */
    public JavaPlatform getJavaPlatform() {
        return JavaPlatformManager.getDefault().getDefaultPlatform();
    }
    
    /**
     * 
     */
    public void notifyLibrariesChanged() {
        initLibraries();
        firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
    }
    
    private void initLibraries() {

        LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
        lib.setName(NbBundle.getMessage(Hk2J2eePlatformImpl.class, "LBL_LIBRARY"));
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, properties.getClasses());
        libraries = new LibraryImplementation[] {lib};
    }
}