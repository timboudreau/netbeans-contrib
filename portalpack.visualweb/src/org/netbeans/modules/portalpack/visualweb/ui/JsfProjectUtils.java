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
package org.netbeans.modules.portalpack.visualweb.ui;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileLock;
import org.openide.util.NbBundle;
import org.openide.util.Mutex;
import org.openide.util.Utilities;
import org.openide.ErrorManager;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;

// XXX WebProjectLibrariesModifier and WebPropertyEvaluator in org.netbeans.modules.web.project
// are not accessible under NetBeans 6.0; need friend-package
// We now access these APIs by org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil
// import org.netbeans.modules.web.project.api.WebProjectLibrariesModifier;
// import org.netbeans.modules.web.project.api.WebPropertyEvaluator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.visualweb.api.JsfPortletSupport;
import org.netbeans.modules.portalpack.visualweb.api.JsfPortletSupportImpl;

/**
 *
 * @author Po-Ting Wu
 */
public class JsfProjectUtils {

    public static boolean isWebProject(Project project) {
        if (project == null) {
            return false;
        }

        WebModule wm = getWebModule(project);
        return wm != null;
    }

    /**
     * Check for Creator project
     * @param project Project to be checked
     */
    public static boolean isJsfProject(Project project) {
        if (project == null) {
            return false;
        }

        String version = getProjectVersion(project);
        return version != null && version.length() > 0;
    }

    public static boolean isJsfFramework(WebFrameworkProvider framework) {
        if (framework == null) {
            return false;
        }

        return JsfProjectConstants.VISUAL_WEB_FRAMEWWORK.equals(framework.getClass().getName());
    }

    public static boolean isJavaEE5Project(Project project) {
        if (project == null) {
            return false;
        }

        return J2eeModule.JAVA_EE_5.equals(getJ2eePlatformVersion(project));
    }

    public static WebModule getWebModule(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());

        if (wm != null) {
            return wm;
        }

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup group : groups) {
            wm = WebModule.getWebModule(group.getRootFolder());
            if (wm != null) {
                return wm;
            }
        }

        return null;
    }

    /**
     * Convenience method to obtain the document root folder.
     * @param project the Project object
     * @return the FileObject of the document root folder
     */
    public static FileObject getDocumentRoot(Project project) {
        if (project == null) {
            return null;
        }

        WebModule wm = getWebModule(project);
        if (wm == null) {
            return null;
        }

        return wm.getDocumentBase();
    }

    /**
     * Convenience method to obtain the WEB-INF folder.
     * @param project the Project object
     * @return the FileObject of the WEB-INF folder
     */
    public static FileObject getWebInf(Project project) {
        if (project == null) {
            return null;
        }

        WebModule wm = getWebModule(project);
        if (wm == null) {
            return null;
        }

        return wm.getWebInf();
    }

    /**
     * Convenience method to obtain the source root folder.
     * @param project the Project object
     * @return the FileObject of the source root folder
     */
    public static FileObject getSourceRoot(Project project) {
        if (project == null) {
            return null;
        }

        // Search the ${src.dir} Source Package Folder first, use the first source group if failed.
        Sources src = ProjectUtils.getSources(project);
        SourceGroup[] grp = src.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < grp.length; i++) {
            if ("${src.dir}".equals(grp[i].getName())) { // NOI18N

                return grp[i].getRootFolder();
            }
        }
        if (grp.length != 0) {
            return grp[0].getRootFolder();
        }

        return null;
    }

    /**
     * Convenience method to obtain the root folder for page beans
     * @param project the Project object
     * @return the FileObject of the page bean root folder
     */
    public static FileObject getPageBeanRoot(Project project) {
        if (project == null) {
            return null;
        }

        if (!isWebProject(project)) {
            return null;
        }

        FileObject srcRoot = getSourceRoot(project);
        if (srcRoot == null) {
            return null;
        }

        String pageBeanPackage = getProjectProperty(project, JsfProjectConstants.PROP_JSF_PAGEBEAN_PACKAGE);
        if (pageBeanPackage == null) {
            return null;
        }

        pageBeanPackage = pageBeanPackage.replace('.', '/');
        FileObject pageBeanFolder = srcRoot.getFileObject(pageBeanPackage);
        if (pageBeanFolder != null) {
            return pageBeanFolder;
        }

        try {
            return FileUtil.createFolder(srcRoot, pageBeanPackage);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            return null;
        }
    }

    /** J2EE platform version - one of the constants {@link #J2EE_13_LEVEL}, {@link #J2EE_14_LEVEL}.
     * @param project
     * @return J2EE platform version
     */
    public static String getJ2eePlatformVersion(Project project) {
        if (project == null) {
            return "";
        }

        WebModule wm = getWebModule(project);
        if (wm == null) {
            return "";
        }

        return wm.getJ2eePlatformVersion();
    }

    public static String getSourceLevel(Project project) {
        if (!isWebProject(project)) {
            return null;
        }

        SourceLevelQueryImplementation slq = (SourceLevelQueryImplementation) project.getLookup().lookup(SourceLevelQueryImplementation.class);
        if (slq == null) {
            return null;
        }

        FileObject srcRoot = getSourceRoot(project);
        if (srcRoot == null) {
            return null;
        }

        return slq.getSourceLevel(srcRoot);
    }

    /**
     * Obtain the portlet support helper object from the project
     * @param project the Project object
     * @return the portlet support object or null if the project is not
     * capable of supporting portlets
     */
    public static JsfPortletSupport getPortletSupport(Project project) {
        FileObject webInf = getWebInf(project);
        if (webInf == null) {
            return null;
        }

        FileObject fo = webInf.getFileObject("portlet.xml");  // NOI18N

        if (fo == null) {
            return null;
        } else {
            return new JsfPortletSupportImpl(project);
        }
    }

    public static boolean supportProjectProperty(Project project) {
        if (isWebProject(project)) {
            AuxiliaryConfiguration ac = ProjectUtils.getAuxiliaryConfiguration(project);
            Element auxElement = ac.getConfigurationFragment(JsfProjectConstants.RAVE_AUX_NAME, JsfProjectConstants.RAVE_AUX_NAMESPACE, true);
            if (auxElement != null) {
                return true;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document xmlDoc = builder.newDocument();
                auxElement = xmlDoc.createElementNS(JsfProjectConstants.RAVE_AUX_NAMESPACE, JsfProjectConstants.RAVE_AUX_NAME);
            } catch (ParserConfigurationException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                return false;
            }

            ac.putConfigurationFragment(auxElement, true);
            auxElement = ac.getConfigurationFragment(JsfProjectConstants.RAVE_AUX_NAME, JsfProjectConstants.RAVE_AUX_NAMESPACE, true);
            if (auxElement != null) {
                return true;
            }
        }

        return false;
    }

    public static String getProjectProperty(Project project, String propName) {
        if (isWebProject(project)) {
            AuxiliaryConfiguration ac = ProjectUtils.getAuxiliaryConfiguration(project);
            Element auxElement = ac.getConfigurationFragment(JsfProjectConstants.RAVE_AUX_NAME, JsfProjectConstants.RAVE_AUX_NAMESPACE, true);
            if (auxElement == null) {  // Creator 2 project

                return getCreatorProperty(project, propName);
            }
            String value = auxElement.getAttribute(propName);
            if (value == null || value.equals("")) {  // Creator 2 project

                return getCreatorProperty(project, propName);
            }
            return value;
        } else {
            return "";
        }
    }

    private static String getCreatorProperty(final Project project, String propName) {
        EditableProperties props;
        /* XXX WebPropertyEvaluator in org.netbeans.modules.web.project is not accessible; need friend-package
        WebPropertyEvaluator wpe = (WebPropertyEvaluator) project.getLookup().lookup(WebPropertyEvaluator.class);
        if (wpe != null) {
        PropertyEvaluator pe = wpe.evaluator();
        props = new EditableProperties(pe.getProperties());
        } else {
         */
        props = NetbeansUtil.getWebProperties(project);
        if (props == null) {
            // Can't find anything, try to read the project.properties file directly. Shouldn't be here.
            try {
                props = (EditableProperties) ProjectManager.mutex().readAccess(new Mutex.ExceptionAction() {

                    public Object run() throws Exception {
                        EditableProperties ep = new EditableProperties();
                        FileObject propFile = project.getProjectDirectory().getFileObject(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                        InputStream is = propFile.getInputStream();

                        ep.load(is);
                        is.close();

                        return ep;
                    }
                });
            } catch (Exception e) {
                return "";
            }
        }

        // Store Creator properties into the new format
        String ret = "";
        boolean isCreator = false;
        for (int i = 0; i < JsfProjectConstants.CreatorProperties.length; i++) {
            String val = props.getProperty(JsfProjectConstants.CreatorProperties[i]);
            if (val != null) {
                isCreator = true;

                putProjectProperty(project, JsfProjectConstants.CreatorProperties[i], val);

                if (propName.equals(JsfProjectConstants.CreatorProperties[i])) {
                    ret = val;
                }
            }
        }

        // Store version into the new format
        String version = props.getProperty("creator"); // NOI18N

        if (isCreator && version == null) {
            version = "2.0"; // NOI18N

        }
        if (version != null) {
            setProjectVersion(project, version);
            if (propName.equals(JsfProjectConstants.PROP_JSF_PROJECT_VERSION)) { // NOI18N

                ret = version;
            }
        }

        return ret;
    }

    public static void createProjectProperty(Project project, String propName, String value) {
        putProjectProperty(project, propName, value, ""); // NOI18N

    }

    public static void putProjectProperty(Project project, String propName, String value) {
        putProjectProperty(project, propName, value, getProjectProperty(project, propName));
    }

    private static void putProjectProperty(Project project, String propName, String value, String oldval) {
        if (isWebProject(project)) {
            AuxiliaryConfiguration ac = ProjectUtils.getAuxiliaryConfiguration(project);
            Element auxElement = ac.getConfigurationFragment(JsfProjectConstants.RAVE_AUX_NAME, JsfProjectConstants.RAVE_AUX_NAMESPACE, true);
            if (auxElement == null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document xmlDoc = builder.newDocument();
                    auxElement = xmlDoc.createElementNS(JsfProjectConstants.RAVE_AUX_NAMESPACE, JsfProjectConstants.RAVE_AUX_NAME);
                } catch (ParserConfigurationException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    return;
                }
            }
            auxElement.setAttribute(propName, value);
            ac.putConfigurationFragment(auxElement, true);
        }
    }

    public static String getProjectVersion(Project project) {
        return getProjectProperty(project, JsfProjectConstants.PROP_JSF_PROJECT_VERSION);
    }

    public static void setProjectVersion(Project project, String version) {
        putProjectProperty(project, JsfProjectConstants.PROP_JSF_PROJECT_VERSION, version);
    }

    /**
     * Add an array of library references to a project, qualified for both the design-time classpath or deployed with the application
     * @param project Project to which the library is to be added
     * @param libraries Library objects from the LibraryManager registry
     * @return Returns true if the library reference was successfully added
     * @throws an IOException if there was a problem adding the reference
     */
    public static boolean addLibraryReferences(Project project, Library[] libraries) throws IOException {
        try {
            return ProjectClassPathModifier.addLibraries(libraries, getSourceRoot(project), ClassPath.COMPILE);
        } catch (IOException e) {
            // Should continue here, many exceptions happened in NetBeans codes are not fatal.
        }

        return false;
    }

    /**
     * Add an array of library references to a web project, qualified by the type parameter.
     * @param project Project to which the library is to be added
     * @param libraries Library objects from the LibraryManager registry
     * @param type Determines whether the library is to be added to the
     *        design-time classpath (ClassPath.COMPILE) or deployed with the application (ClassPath.EXECUTE)
     * @return Returns true if the library reference was successfully added
     * @throws an IOException if there was a problem adding the reference
     */
    public static boolean addLibraryReferences(Project project, Library[] libraries, String type) throws IOException {
        /* XXX WebProjectLibrariesModifier in org.netbeans.modules.web.project is not accessible; need friend-package
        WebProjectLibrariesModifier wplm = (WebProjectLibrariesModifier) project.getLookup().lookup(WebProjectLibrariesModifier.class);
        if (wplm == null) {
        // Something is wrong, shouldn't be here.
        return addLibraryReferences(project, libraries);
        }
        
        if (ClassPath.COMPILE.equals(type)) {
        return wplm.addCompileLibraries(libraries);
        } else if (ClassPath.EXECUTE.equals(type)) {
        return wplm.addPackageLibraries(libraries, PATH_IN_WAR_LIB);
        }
        
        return false;
         */
        if (NetbeansUtil.addLibraryReferences(project, libraries, type)) {
            return true;
        }

        return addLibraryReferences(project, libraries);
    }

    /**
     * Derive an identifier suitable for a java package name or context path
     * @param sourceName Original name from which to derive the name
     * @return An identifier suitable for a java package name or context path
     */
    public static String deriveSafeName(String sourceName) {
        StringBuffer dest = new StringBuffer(sourceName.length());
        int sourceLen = sourceName.length();
        if (sourceLen > 0) {
            int pos = 0;
            while (pos < sourceLen) {
                if (Character.isJavaIdentifierStart(sourceName.charAt(pos))) {
                    dest.append(Character.toLowerCase(sourceName.charAt(pos)));
                    pos++;
                    break;
                }
                pos++;
            }

            for (int i = pos; i < sourceLen; i++) {
                if (Character.isJavaIdentifierPart(sourceName.charAt(i))) {
                    dest.append(Character.toLowerCase(sourceName.charAt(i)));
                }
            }
        }
        if (dest.length() == 0 || !Utilities.isJavaIdentifier(dest.toString())) {
            return "untitled";  // NOI18N

        } else {
            return dest.toString();
        }
    }

    public static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuffer sbuffer = new StringBuffer();
        String lineSep = System.getProperty("line.separator");//NOI18N

        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sbuffer.append(line);
            sbuffer.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sbuffer.toString();
    }

    public static void createFile(FileObject target, String content, String encoding) throws IOException {
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();

        } finally {
            lock.releaseLock();
        }
    }

    /** Reports whether the given name is a valid Java package name.
     * @param name The Java package name to be checked
     * @return true iff the name parameter is a valid Java package name
     */
    public static boolean isValidJavaPackageName(String pkgName) {
        if (pkgName == null) {
            return false;
        }
        String[] pkg = pkgName.split("\\.");
        for (int i = 0; i < pkg.length; i++) {
            if (!Utilities.isJavaIdentifier(pkg[i])) {
                return false;
            }
        }

        return true;
    }

    /** Reports whether the given name is a valid Java file name.
     * @param name The Java file name to be checked
     * @return true iff the name parameter is a valid Java file name
     * @todo Use the passed in project context to make sure that the
     *   name would not conflict with existing files (e.g. check
     *   the webforms and backing file folders for name conflicts).
     */
    public static boolean isValidJavaFileName(String name) {
        if (name == null) {
            return false;
        }
        int n = name.length();
        if (n == 0) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }

        for (int i = 1; i < n; i++) {
            char c = name.charAt(i);
            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }

        if (!Utilities.isJavaIdentifier(name)) {
            return false;
        }

        return true;
    }

    public static String getBackwardsKitMesg(boolean addJSF11, boolean addJAXRPC, boolean addRowset) {
        int count = 0;
        String nbms = "";
        if (addJSF11) {
            count++;
            nbms = NbBundle.getMessage(JsfProjectUtils.class, "LBL_MissingJSF");
        }
        if (addJAXRPC) {
            count++;
            nbms += NbBundle.getMessage(JsfProjectUtils.class, "LBL_MissingJAXRPC");
        }
        if (addRowset) {
            count++;
            nbms += NbBundle.getMessage(JsfProjectUtils.class, "LBL_MissingRowset");
        }

        String RI = NbBundle.getMessage(JsfProjectUtils.class, (count > 1) ? "LBL_MissingMany" : "LBL_MissingOne");

        return NbBundle.getMessage(JsfProjectUtils.class, "LBL_MissingNBM", RI, nbms);
    }

    public static String getRelativePathForJsfPortlet(FileObject docBase,FileObject jsfPortletFolder) {
        String jsfportletFolderRelativePath = FileUtil.getRelativePath(docBase, jsfPortletFolder);
        if (jsfportletFolderRelativePath != null) {
            if (!jsfportletFolderRelativePath.startsWith("/") && !jsfportletFolderRelativePath.startsWith("\\")) {
                jsfportletFolderRelativePath = "/" + jsfportletFolderRelativePath;
            }
            if (!jsfportletFolderRelativePath.endsWith("/") && !jsfportletFolderRelativePath.endsWith("\\")) {
                jsfportletFolderRelativePath += "/";
            }
            jsfportletFolderRelativePath = jsfportletFolderRelativePath.replace("\\", "/");
        }else
            jsfportletFolderRelativePath = "/";
        
        return jsfportletFolderRelativePath;
    }
}
