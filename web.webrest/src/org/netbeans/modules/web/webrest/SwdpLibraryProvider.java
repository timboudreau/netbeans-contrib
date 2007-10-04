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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.web.webrest;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryProvider;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * This library provider will attempt to create SWDP library supporting
 * REST development.  Each time REST framework is added to Web project
 * the SWDP library is queried, if not exist, the target JavaEE platform
 * is searched for the library files.  Once created, the library will 
 * have to be conciously maintained in case user remove target instance.
 * 
 * Note:  this dynamic approach is active replacement for a ready SWDP library
 * provided by the IDE installation or one from the update center.  
 * 
 * @author Nam Nguyen
 */
public class SwdpLibraryProvider implements LibraryProvider {

    public static final String VOLUME_TYPE_CLASSPATH = "classpath";

    public static final String REST_API_JAR = "restbeans-api.jar";
    
    public static final Set<String> SWDP_JAR_NAMES = new HashSet<String>(Arrays.asList(
        new String[] {
            REST_API_JAR, 
            "restbeans-impl.jar",
            "wadl2java.jar",
            "activation.jar",
            //"jsr250-api.jar",
            "localizer.jar",
            "mail.jar",
            //"jaxb-api.jar",
            //"jaxb-impl.jar",
            //"jsr173_api.jar",
            "webservices-tools.jar",
            "webservices-rt.jar",
            "rome-0.8.jar",
            "jdom-1.0.jar"
    }));

    private static HashMap<String,LibraryImplementation> librariesByServerInstance = 
          new HashMap<String,LibraryImplementation>();

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /** Creates a new instance of SwdpLibraryProvider */
    public SwdpLibraryProvider() {
    }

    public LibraryImplementation[] getLibraries() {
        return librariesByServerInstance.values().toArray(new LibraryImplementation[0]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    protected void fireLibraryAdded(LibraryImplementation newLib) {
        pcs.firePropertyChange(LibraryProvider.PROP_LIBRARIES, null, newLib);
    }
    
    protected void fireLibraryRemoved(LibraryImplementation oldLib) {
        pcs.firePropertyChange(LibraryProvider.PROP_LIBRARIES, oldLib, null);
    }
    
    public synchronized LibraryImplementation getLibrary(String serverInstanceID) {
        LibraryImplementation ret = librariesByServerInstance.get(serverInstanceID);
        if (ret == null) {
            ret = findSwdpLibraryFromServerPlatform(serverInstanceID);
        }
        if (ret != null) {
            librariesByServerInstance.put(serverInstanceID, ret);
            fireLibraryAdded(ret);
        }
        return ret;
    }
    
    private LibraryImplementation findSwdpLibraryFromServerPlatform(String instanceID) {
        J2eePlatform platform = Deployment.getDefault().getJ2eePlatform(instanceID);
        File[] roots = platform.getPlatformRoots();
        Map<String,URL> resultJars = new HashMap<String,URL>();
        for (File root : roots) {
            FileObject rootFO = FileUtil.toFileObject(root);
            if (rootFO == null) {
                continue;
            }
            FileObject libDir = rootFO.getFileObject("lib");
            searchForLibraryJars(libDir, resultJars);
            /*if (resultJars.keySet().containsAll(SWDP_JAR_NAMES)) {
                break;
            }
            searchForLibraryJars(rootFO, resultJars);*/
        }
        if (resultJars.keySet().contains(REST_API_JAR)) {
            return createLibrary(resultJars.values());
        }
        return null;
    }
    
    private void searchForLibraryJars(FileObject dir, Map<String,URL> resultJars) {
        if (dir == null) return;
        Enumeration<? extends FileObject> data = dir.getData(true);
        while (data.hasMoreElements()) {
            FileObject file = data.nextElement();
            String name = file.getNameExt();
            if (SWDP_JAR_NAMES.contains(name)) {
                try {
                    URL url = file.getURL();
                    resultJars.put(name, url);
                } catch (IOException ioe) {
                    Logger.getLogger("global").log(Level.INFO, null, ioe);
                }
            }
        }
    }
    
    private LibraryImplementation createLibrary(Collection<URL> libraryFiles) {
        LibraryImplementation lib = LibrariesSupport.createLibraryImplementation(
                RestSupport.SWDP_LIBRARY, J2eeLibraryTypeProvider.VOLUME_TYPES );
        lib.setName(NbBundle.getMessage(SwdpLibraryProvider.class, "LBL_SWDP_LIBRARY")); 
        lib.setContent(VOLUME_TYPE_CLASSPATH, new ArrayList<URL>(libraryFiles));
        return lib;
    }
}
