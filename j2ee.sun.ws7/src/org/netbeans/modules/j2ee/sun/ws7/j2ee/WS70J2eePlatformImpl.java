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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
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
    
    
    private static final String WS70_JAR =  "pwc.jar"; //NOI18N
    private static final String JWSDP20_JAR =  "webserv-jwsdp.jar"; //NOI18N
    private static final String JSTL_JAR =  "webserv-jstl.jar"; //NOI18N
    private static final String MAIL_JAR =  "mail.jar"; //NOI18N
    private static final String JAXRPC_API_JAR =  "jaxrpc-api.jar"; //NOI18N
    private static final String JAXRPC_IMPL_JAR =  "jaxrpc-impl.jar"; //NOI18N
    private static final String JAXRPC_SPI_JAR =  "jaxrpc-spi.jar"; //NOI18N
    private static final String ACTIVATION =  "activation.jar"; //NOI18N
    private static final String JSF_IMPL =  "jsf-impl.jar"; //NOI18N
    private static final String JSF_API =  "jsf-api.jar"; //NOI18N

    private List libraries  = new ArrayList();
    private List classPathEntries = new ArrayList();
    private boolean isJwsdp16enabled =false;
    private boolean isJwsdp20enabled =false;
          
    static {
        MODULE_TYPES.add(J2eeModule.WAR);

        SPEC_VERSIONS.add(J2eeModule.J2EE_13);
        SPEC_VERSIONS.add(J2eeModule.J2EE_14);        
        SPEC_VERSIONS.add(J2eeModule.JAVA_EE_5);        
    }
    
    private File root;
    private File instance;
    private String displayName;
    
    /**
     * Creates a new instance of WS70J2eePlatformImpl 
     */
    public WS70J2eePlatformImpl(String serverLocation, String instanceLocation, String displayName) {
        if(serverLocation!=null && instanceLocation!=null){
            root = new File(serverLocation);
            instance = new File(instanceLocation);
            init(root, instance);
        }
        this.displayName = displayName;
    }
    
    private void init(File server, File instance){
        try {
            J2eeLibraryTypeProvider lp = new J2eeLibraryTypeProvider();
            lp.createLibrary().setName ("a");
            LibraryImplementation lib = lp.createLibrary();

            lib.setName("webserver70 library"); // NOI18N

            List l = new ArrayList();            
            if (server.equals(instance)) {
                l.add(fileToUrl(new File(root, libJar(WS70_JAR))));
                if(new File(root, libJar(JWSDP20_JAR)).exists()){
                    l.add(fileToUrl(new File(root, libJar(JWSDP20_JAR))));
                    isJwsdp20enabled = true;
                }
                l.add(fileToUrl(new File(root, libJar(MAIL_JAR))));
                l.add(fileToUrl(new File(root, libJar(JSTL_JAR))));
                if(new File(root, libJar(JAXRPC_API_JAR)).exists() &&
                     new File(root, libJar(JAXRPC_IMPL_JAR)).exists() &&
                     new File(root, libJar(JAXRPC_IMPL_JAR)).exists() ){
                
                    l.add(fileToUrl(new File(root, libJar(JAXRPC_API_JAR))));
                    l.add(fileToUrl(new File(root, libJar(JAXRPC_IMPL_JAR))));
                    l.add(fileToUrl(new File(root, libJar(JAXRPC_SPI_JAR))));
                    isJwsdp16enabled = true;
                }
                l.add(fileToUrl(new File(root, libJar(ACTIVATION))));
                l.add(fileToUrl(new File(root, libJar(JSF_IMPL))));
                l.add(fileToUrl(new File(root, libJar(JSF_API))));
            } else { 
                // In case of JES installation, parse the serverxml file to get the classpath jars
                // Check these jars against listed jars (listJars()) before adding them to the library
                String instanceName = getInstanceName(instance);
                String serverXml = instanceName + File.separator +  "config" + File.separator + "server.xml";
                String[] classPathJars = getServerClassPath(serverXml);
                String[] jars = listJars();
                for(String cjar:classPathJars) {
                    String[] tokens = cjar.split(File.separator);
                    for(String jar:jars) {
                        if(jar.equals(tokens[tokens.length-1])) {
                            //classPathEntries will hold all these jars to pass them to getToolClasspathEntries
                            classPathEntries.add(new File(cjar));
                            l.add(fileToUrl(new File(cjar)));
                        }
                    }
                }
            }

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

    // for JES installation
    private String[] listJars() {
        String[] jars = { "jaxws-api.jar", "jaxws-rt.jar", "jaxws-tools.jar", "jsr181-api.jar", "jsr250-api.jar", "jaxb-api.jar", "jaxb-impl.jar", "jaxb-xjc.jar", "sjsxp.jar", "jsr173_api.jar", "saaj-api.jar", "saaj-impl.jar", "xmldsig.jar", "xmlsec.jar", "xws-security.jar", "xws-security_jaxrpc.jar", "wss-provider-update.jar", "security-plugin.jar", "FastInfoset.jar", "relaxngDatatype.jar", "resolver.jar", WS70_JAR, JSTL_JAR, MAIL_JAR, JAXRPC_API_JAR, JAXRPC_IMPL_JAR, JAXRPC_SPI_JAR, ACTIVATION, JSF_IMPL, JSF_API };

        return jars;
    }

    private static String getInstanceName(File instanceLocation) {
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().startsWith("https-"); //NO I18N
            }
        };

        return instanceLocation.listFiles( filter )[0].toString();
    }

    private static String[] getServerClassPath(String fileLocation) {
        String line = new String();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileLocation));
            boolean flag = true;
            Pattern p = Pattern.compile("<server-class-path>(.*)</server-class-path>");
            while(flag) {
                Matcher m = p.matcher(br.readLine());
                if (m.find()) {
                    flag = false;
                    line = m.group(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return line.split(File.pathSeparator);
    }

    private String libJar(String jar) {
        return "lib" + File.separator + jar;
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
        if (root.equals(instance)) {
            return new File[] {
                new File(root, libJar(JWSDP20_JAR)),
                new File(root, libJar(JAXRPC_API_JAR)),
                new File(root, libJar(JAXRPC_IMPL_JAR)),
                new File(root, libJar(JAXRPC_SPI_JAR)),
                new File(root, libJar(MAIL_JAR)),
                new File(root, libJar(ACTIVATION)),
                new File(root, libJar(JSF_IMPL)),
                new File(root, libJar(JSF_API))
            };
        } else {
            return (File[])classPathEntries.toArray();
        }
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
