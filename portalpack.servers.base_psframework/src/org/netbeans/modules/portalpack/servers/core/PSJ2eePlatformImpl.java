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

package org.netbeans.modules.portalpack.servers.core;

import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.filesystems.FileUtil;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;



/**
 *
 * @author Satya
 */
public class PSJ2eePlatformImpl extends J2eePlatformImpl {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private List/*<LibraryImpl>*/ libraries  = new ArrayList();
    protected PSConfigObject psconfig;
    public PSJ2eePlatformImpl(PSConfigObject psconfig)
    {
        //this.dm =(PSDeploymentManager)dm;
        this.psconfig = psconfig;
        J2eeLibraryTypeProvider libProvider = new J2eeLibraryTypeProvider();
        LibraryImplementation lib = libProvider.createLibrary();
        lib.setName("Portlet Plugin");
        loadLibraries(lib);
        libraries.add(lib);
    }
    public boolean isToolSupported(String toolName) {
        return false;
    }
    
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }
    
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_13);
        result.add(J2eeModule.J2EE_14);
        //result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    
    public java.util.Set getSupportedModuleTypes() {
        Set result = new HashSet();
        result.add(J2eeModule.EAR);
        result.add(J2eeModule.WAR);
      //result.add(J2eeModule.EJB);
        return result;
    }
    
    public java.io.File[] getPlatformRoots() {
        return new File[]{new File(psconfig.getPSHome())};
    }
    
   public void notifyLibrariesChanged() {
        //System.out.println("************************* Firing event for Library has been change *******************");
        LibraryImplementation lib = (LibraryImplementation)libraries.get(0);
        loadLibraries(lib);
        firePropertyChange(PROP_LIBRARIES, null, libraries);
    }
    
    public LibraryImplementation[] getLibraries() {
       return (LibraryImplementation[])libraries.toArray(new LibraryImplementation[libraries.size()]);
    }
    
    public java.awt.Image getIcon() {
        return Utilities.loadImage("com/sun/portlet/netbeans/server/ps/resources/server.gif"); // NOI18N
        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(PSJ2eePlatformImpl.class, "MSG_MyServerPlatform");
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
    
    private void loadLibraries(LibraryImplementation lib) {
        
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, getCustomLibraries());
        //lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_JAVADOC, tp.getJavadocs());
        //lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_SRC, tp.getSources());
    }
    
    
    protected List getCustomLibraries()
    {
         return Collections.EMPTY_LIST;
    }
    
       // copied from appserv plugin
        protected URL fileToUrl(File file) throws MalformedURLException {
            URL url = file.toURI().toURL();
            if (FileUtil.isArchiveFile(url)) {
                url = FileUtil.getArchiveRoot(url);
            }
            return url;
        }
}