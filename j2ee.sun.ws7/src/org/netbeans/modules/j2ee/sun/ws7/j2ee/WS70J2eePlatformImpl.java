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

/*
 * WS70J2eePlatformImpl.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.api.java.platform.JavaPlatform;
/**
 *
 * @author Administrator
 */
public class WS70J2eePlatformImpl extends J2eePlatformImpl{
    private static final Set MODULE_TYPES = new HashSet();
    private static final Set SPEC_VERSIONS = new HashSet();
    
    
    private static final String WS70_JAR =  "lib/pwc.jar"; //NOI18N
    private static final String JWSDP20_JAR =  "lib/webserv-jwsdp.jar"; //NOI18N
    private static final String JSTL_JAR =  "lib/webserv-jstl.jar"; //NOI18N
    private static final String MAIL_JAR =  "lib/mail.jar"; //NOI18N
    private static final String JAXRPC_API_JAR =  "lib/jaxrpc-api.jar"; //NOI18N
    private static final String JAXRPC_IMPL_JAR =  "lib/jaxrpc-impl.jar"; //NOI18N
    private static final String JAXRPC_SPI_JAR =  "lib/jaxrpc-spi.jar"; //NOI18N
    private static final String ACTIVATION =  "lib/activation.jar"; //NOI18N
    
    private List libraries  = new ArrayList();
    private boolean isJwsdp16enabled =false;
    private boolean isJwsdp20enabled =false;
          
    
    
    static {
        MODULE_TYPES.add(J2eeModule.WAR);

        SPEC_VERSIONS.add(J2eeModule.J2EE_13);
        SPEC_VERSIONS.add(J2eeModule.J2EE_14);        
        SPEC_VERSIONS.add(J2eeModule.JAVA_EE_5);        
    }
    
    private File root;
    private String displayName;
    
    /**
     * Creates a new instance of WS70J2eePlatformImpl 
     */
    public WS70J2eePlatformImpl(String rootLocation, String displayName) {
        if(rootLocation!=null){
            root = new File(rootLocation);
            init(root);
        }
        this.displayName = displayName;
    }
    
    private void init(File location){
        try {
            J2eeLibraryTypeProvider lp = new J2eeLibraryTypeProvider();
            lp.createLibrary().setName ("a");
            LibraryImplementation lib = lp.createLibrary();

            lib.setName("webserver70 library"); // NOI18N

            List l = new ArrayList();            

            l.add(fileToUrl(new File(root, WS70_JAR)));
            if(new File(root, JWSDP20_JAR).exists()){
                l.add(fileToUrl(new File(root, JWSDP20_JAR)));
                isJwsdp20enabled = true;
            }
            l.add(fileToUrl(new File(root, MAIL_JAR)));
            l.add(fileToUrl(new File(root, JSTL_JAR)));
            if(new File(root, JAXRPC_API_JAR).exists() &&
                 new File(root, JAXRPC_IMPL_JAR).exists() &&
                 new File(root, JAXRPC_IMPL_JAR).exists() ){
                
                l.add(fileToUrl(new File(root, JAXRPC_API_JAR)));
                l.add(fileToUrl(new File(root, JAXRPC_IMPL_JAR)));
                l.add(fileToUrl(new File(root, JAXRPC_SPI_JAR)));
                isJwsdp16enabled = true;
            }
            l.add(fileToUrl(new File(root, ACTIVATION)));
            lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, l);
            
            File doc = InstalledFileLocator.getDefault().locate("docs/j2eeri-1_4-doc-api.zip", null, false); // NOI18N
            if (doc != null) {
                l = new ArrayList();
                l.add(fileToUrl(doc));
                lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_JAVADOC, l);
            }            

            libraries.add(lib);   

        } catch(Exception e) {
            e.printStackTrace();
        }        
    }
    /**
     * Return a list of supported J2EE specification versions. Use J2EE specification 
     * versions defined in the {@link org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule}
     * class.
     *
     * @return list of supported J2EE specification versions.
     */
    public Set getSupportedSpecVersions() {
        return SPEC_VERSIONS;
    }
    
    /**
     * Return a list of supported J2EE module types. Use module types defined in the 
     * {@link org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule}
     * class.
     *
     * @return list of supported J2EE module types.
     */
    public Set  getSupportedModuleTypes() {
        return MODULE_TYPES;
    }
        /**
     * Specifies whether a tool of the given name is supported by this platform.
     *
     * @param  toolName tool's name e.g. "wscompile".
     * @return <code>true</code> if platform supports tool of the given name, 
     *         <code>false</code> otherwise.
     */
    public boolean isToolSupported(String toolName) {
        if(J2eePlatform.TOOL_WSCOMPILE.equals(toolName)
               && this.isJwsdp16enabled){
            return true;
        }
        if(isJwsdp20enabled){
            if (J2eePlatform.TOOL_WSGEN.equals(toolName)) {
                return true;
            }
            if (J2eePlatform.TOOL_WSIMPORT.equals(toolName)) {
                return true;
            }
            if (J2eePlatform.TOOL_JWSDP.equals(toolName) ){
                return true;
            }
            if (J2eePlatform.TOOL_WSIT.equals(toolName) ){
                return true;
            }            
        }
        if (J2eePlatform.TOOL_JSR109.equals(toolName)) {
            return false;
        }
        return false;
        
    }
    
    /**
     * Return platform's libraries.
     *
     * @return platform's libraries.
     */
    public LibraryImplementation[] getLibraries() {
        return (LibraryImplementation[])libraries.toArray(new LibraryImplementation[libraries.size()]);
    }
    
    public File[] getToolClasspathEntries(String toolName) {
        return new File[] {
            new File(root, JWSDP20_JAR),
            new File(root, JAXRPC_API_JAR),
            new File(root, JAXRPC_IMPL_JAR),
            new File(root, JAXRPC_SPI_JAR),
            new File(root, MAIL_JAR),
            new File(root, ACTIVATION)
            
        };
    }
    
    /**
     * Return platform's display name.
     *
     * @return platform's display name.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Return platform's icon.
     *
     * @return platform's icon.
     */
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/j2ee/sun/ide/resources/ServerInstanceIcon.gif"); // NOI18N;
    }
    
    /**
     * Return platform's root directories. This will be mostly server's installation
     * directory.
     *
     * @return platform's root directories.
     */
    public File[] getPlatformRoots() {
        return new File [] {root};
    }    
    
    public Set/*<String>*/ getSupportedJavaPlatformVersions(){
        Set versions = new HashSet();
        versions.add("1.4"); // NOI18N
        versions.add("1.5"); // NOI18N
        return versions;
    }
    
    /**
     * Return server J2SE platform or null if the platform is unknown, not 
     * registered in the IDE.
     *
     * @return server J2SE platform or null if the platform is unknown, not 
     *         registered in the IDE.
     *
     * @since 1.9
     */
    public JavaPlatform getJavaPlatform(){
        return null;
    }
    private URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        if (FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        return url;
    }
}
