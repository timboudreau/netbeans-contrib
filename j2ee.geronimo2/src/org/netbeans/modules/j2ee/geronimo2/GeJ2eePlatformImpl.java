/*
 * GeJ2eePlatformImpl.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;

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
 * Geronimo Implementation of J2eePlatformImpl
 * Describes target environment.
 * @author Max Sauer
 */
public class GeJ2eePlatformImpl extends J2eePlatformImpl {
    
    private GeDeploymentManager dm;
    private LibraryImplementation[] libraries;
    private GePluginProperties properties;
    
    public GeJ2eePlatformImpl(GeDeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        initLibraries();
    }
    
    public void notifyLibrariesChanged() {
        initLibraries();
        firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
    }
    
    private void initLibraries() {
        // create library
        LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
        lib.setName(NbBundle.getMessage(GeJ2eePlatformImpl.class, "TITLE_GE_LIBRARY"));
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
     * For geronimo 2+, EE5
     * @return supported specifications
     */
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_14);
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    
    public java.util.Set getSupportedModuleTypes() {
        Set result = new HashSet();
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
        return Utilities.loadImage("org/netbeans/modules/j2ee/geronimo/resources/server.gif"); // NOI18N
        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(GeJ2eePlatformImpl.class, "MSG_GeServerPlatform");
    }
    
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