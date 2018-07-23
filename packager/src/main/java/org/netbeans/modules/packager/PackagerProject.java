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

package org.netbeans.modules.packager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A project type which takes the build products of other projects and
 * assembles a platform-specific installation structure from them.
 *
 * @author  Tim Boudreau
 */
public class PackagerProject implements Project {
    public static final String KEY_MAIN_CLASS="main.class"; //NOI18N
    public static final String KEY_MAIN_CLASS_JAR="main.class.jar"; //NOI18N
    
    public static final String KEY_NAME = "name"; //NOI18N
    public static final String KEY_DIR = "projdir"; //NOI18N
    public static final String KEY_PROJECTS = "childProjects"; //NOI18N
    public static final String KEY_INDIRECT_DEPENDENCIES = "indirectDependencies"; //NOI18N
    public static final String KEY_MAC = "platform.mac"; //NOI18N
    public static final String KEY_UNIX = "platform.unix"; //NOI18N
    public static final String KEY_WINDOWS = "platform.windows"; //NOI18N
    public static final String KEY_WEBSTART = "platform.webstart"; //NOI18N
    
    public static final String KEY_JNLP_CODEBASE = "jnlp.codebase"; //NOI18N
    public static final String KEY_JNLP_DESCRIPTION = "jnlp.description"; //NOI18N
    public static final String KEY_JNLP_HOMEPAGE = "jnlp.homepage"; //NOI18N
    public static final String KEY_JNLP_ICON = "jnlp.icon"; //NOI18N
    public static final String KEY_JNLP_SHORT_DESCRIPTION="jnlp.longdescription";  //NOI18N [sic]
    public static final String KEY_JNLP_VENDOR = "jnlp.vendor"; //NOI18N
    public static final String KEY_JNLP_ALLOW_OFFLINE = "jnlp.offline"; //NOI18N
    
    public static final String KEY_JNLP_PERMISSIONS = "jnlp.permissions"; //NOI18N
    
    public static final String PERMISSION_JNLP_SANDBOX = "jnlp.permissions.none"; //NOI18N
    public static final String PERMISSION_JNLP_J2EE = "jnlp.permissions.j2ee"; //NOI18N
    public static final String PERMISSION_JNLP_FULL = "jnlp.permissions.full"; //NOI18N
    
    public static final String KEY_MAC_APPVERSION = "mac.appversion"; //NOI18N
    public static final String KEY_MAC_VERSIONSTRING = "mac.versionstring"; //NOI18N
    public static final String KEY_MAC_ICONFILE = "mac.iconfile"; //NOI18N

    public static final String[] ALL_KEYS = new String[] {
        KEY_MAIN_CLASS,
        KEY_MAIN_CLASS_JAR,
        KEY_NAME,
        KEY_DIR,
        KEY_PROJECTS,
        KEY_INDIRECT_DEPENDENCIES,
        KEY_MAC,
        KEY_UNIX ,
        KEY_WINDOWS,
        KEY_WEBSTART,    
        KEY_JNLP_CODEBASE,
        KEY_JNLP_DESCRIPTION,
        KEY_JNLP_HOMEPAGE,
        KEY_JNLP_ICON,
        KEY_JNLP_SHORT_DESCRIPTION,
        KEY_JNLP_VENDOR,
        KEY_JNLP_PERMISSIONS,
        KEY_JNLP_ALLOW_OFFLINE,
        KEY_MAC_APPVERSION,
        KEY_MAC_VERSIONSTRING ,
        KEY_MAC_ICONFILE,
    };
    
    
    private final AntProjectHelper helper;
    private final PropertyEvaluator eval;
    private final ReferenceHelper refHelper;
    private final GeneratedFilesHelper genFilesHelper;
    private final Lookup lookup;
    private final SubprojectProvider subprojects;
    

    public PackagerProject(AntProjectHelper helper) {
        this.helper = helper;
        eval = helper.getStandardPropertyEvaluator();
        AuxiliaryConfiguration aux = helper.createAuxiliaryConfiguration();
        refHelper = new ReferenceHelper(helper, aux, eval);
        genFilesHelper = new GeneratedFilesHelper(helper);
        subprojects = refHelper.createSubprojectProvider(); 
        
        lookup = Lookups.fixed(new Object[] {
          new PackagerCustomizerProvider(this),
          subprojects,
          new PackagerActionProvider (this, helper),
          new ProjectOpenedHookImpl(this),
        });    
        
    }
    
    ReferenceHelper getReferenceHelper() {
        return refHelper;
    }
    
    SubprojectProvider getSubprojectProvider() {
        return subprojects;
    }
    
    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }    
    
    public Lookup getLookup() {
        return lookup;
    }
    
    public static AntProjectHelper createProject (final File dir, final String name, final Project[] projects, final Map data) throws IOException {
        assert dir != null : "Source folder must be given";   //NOI18N
        
        final FileObject dirFO = createProjectDir (dir);
        final AntProjectHelper h = ProjectGenerator.createProject(dirFO, PackagerProjectType.TYPE);
        
        final PackagerProject p = (PackagerProject) ProjectManager.getDefault().findProject(dirFO);
        try {
            ProjectManager.mutex().writeAccess( new Mutex.ExceptionAction () {
                public Object run() throws Exception {
                    Element el = h.getPrimaryConfigurationData(true);
                    Document doc = el.getOwnerDocument();
                    Element nameEl = doc.createElementNS(PackagerProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                    nameEl.appendChild(doc.createTextNode(name));
                    el.appendChild(nameEl);
                    h.putPrimaryConfigurationData(el, true);
                    
                    //Write out all of the properties that were set in the wizard
                    EditableProperties ep = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    for (Iterator i=data.keySet().iterator(); i.hasNext();) {
                        String key = (String) i.next();
                        Object value = data.get(key);
                        System.err.println("Writing: " + key + " = " + value);
                        if (value instanceof String) {
                            ep.setProperty (key, (String) value);
                        } else if (value instanceof Boolean) {
                            ep.setProperty (key, value.toString());
                        }
                    }
                    h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                    return null;
                }
            });
        } catch (MutexException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        p.setPackagedProjects(projects);
                
        return h;
    } 
    
    public void setPackagedProjects (final Project[] projects) {
        final ReferenceHelper refHelper = getReferenceHelper();
        Set current = subprojects.getSubprojects();
        
        final HashSet subs = new HashSet (Arrays.asList(projects));
        if (current.equals(subs)) {
            return;
        } 
        
        final HashSet removed = new HashSet();
        if (!subs.isEmpty()) {
            removed.addAll(subs);
            removed.removeAll(Arrays.asList(projects));
        }
        
        try {
        ProjectManager.mutex().writeAccess( new Mutex.ExceptionAction () {
            public Object run() throws Exception {
                String mainClass = null;
                String mainClassJar = null;
                AntArtifact mainClassArtifact = null;
                for (int i=0; i < projects.length; i++) {
                    boolean isMainClassProject = false;
                    
                    if (mainClass == null) {
                        mainClass = findMainClass(projects[i]);
                        if (mainClass != null) {
                            isMainClassProject = true;
                        }
                    }
                    AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(
                        projects[i], JavaProjectConstants.ARTIFACT_TYPE_JAR);
                    for (int j=0; j < artifacts.length; j++) {
                        URI[] locations = artifacts[j].getArtifactLocations();
                        for (int k = 0; k < locations.length; k++) {
                            refHelper.addReference(artifacts[j], locations[k]);
                            if (isMainClassProject && mainClassJar == null) {
                                mainClassJar = findJarFor(projects[i], artifacts[j], mainClass, locations[k]);
                            }
                        }
                    }
                }
                
                if (mainClass != null) {
                    EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    ep.setProperty (KEY_MAIN_CLASS, mainClass);
                    if (mainClassJar != null) {
                        ep.setProperty (KEY_MAIN_CLASS_JAR, mainClassJar);
                    }
                    helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                }
                
                for (Iterator i=removed.iterator(); i.hasNext();) {
                    Project rem = (Project) i.next();
                    
                    String projname = ProjectUtils.getInformation(rem).getName();
                    
                    AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(
                        rem, JavaProjectConstants.ARTIFACT_TYPE_JAR);
                    for (int j=0; j < artifacts.length; j++) {
                        FileObject fo = artifacts[j].getArtifactFile();
                        refHelper.removeReference(fo.getPath()); //XXX nothing seems to work right here
//                        refHelper.removeReference(projname, artifacts[j].getID());
                    }
                }
                
                ProjectManager.getDefault().saveProject (PackagerProject.this);
                return null;
            }
        });
        } catch (MutexException me ) {
            ErrorManager.getDefault().notify (me);
        }
    }
    
    /**
     * Locate the jar file which will contain the main class, for generating
     * the jnlp descriptor.  XXX needs some work.
     */
    private static String findJarFor (Project proj, AntArtifact art, String className, URI location) {
        Sources sources = ProjectUtils.getSources(proj);
        String toFind = Utilities.replaceString(className, ".", "/");
        System.err.println("Scanning " + sources + " from " + proj.getProjectDirectory().getPath() + " for " + toFind);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i=0; i < groups.length; i++) {
            FileObject fob = groups[i].getRootFolder().getFileObject(toFind);
            File f = new File(File.separator + groups[i].getRootFolder().getPath() + File.separator + toFind + ".java");
            System.err.println("Check exists " + f.getPath());
            if (f.exists()) {
                String jarname = location.toString(); //XXX probably wrong
                System.err.println("Found " + f.getPath() + " jar is " + jarname);
                return jarname;
            }
        }
        return null;
    }
    
    public static String findMainClass (Project p) {
        FileObject fo = p.getProjectDirectory().getFileObject(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String result = null;
        if (fo != null) {
            Properties props = new Properties();
            try {
                InputStream is = fo.getInputStream();
                try {
                    props.load(is);
                    result = props.getProperty("main.class"); //NOI18N
                    if (result != null && result.trim().length() == 0) {
                        result = null;
                    }
                } finally {
                    is.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        }
        return result;
    }
  
    
    private static FileObject createProjectDir (File dir) throws IOException {
        dir.mkdirs();
        // XXX clumsy way to refresh, but otherwise it doesn't work for new folders
        File rootF = dir;
        while (rootF.getParentFile() != null) {
            rootF = rootF.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(rootF);
        assert dirFO != null : "At least disk roots must be mounted! " + rootF;
        dirFO.getFileSystem().refresh(false);
        dirFO = FileUtil.toFileObject(dir);
        assert dirFO != null : "No such dir on disk: " + dir;
        assert dirFO.isFolder() : "Not really a dir: " + dir;
        assert dirFO.getChildren().length == 0 : "Dir must have been empty: " + dir;
        return dirFO;
    }    
    
    private final class ProjectOpenedHookImpl extends ProjectOpenedHook {
        private PackagerProject prj;
        
        public ProjectOpenedHookImpl (PackagerProject prj) {
            this.prj = prj;
        }
        
        protected void projectClosed() {
        }
        
        protected void projectOpened() {
            URL buildXsl = PackagerProject.class.getResource("build.xsl"); //NOI18N
            URL buildImplXsl = PackagerProject.class.getResource("build-impl.xsl"); //NOI18N
            URL startShXsl = PackagerProject.class.getResource("resources/mac/start.sh.xsl"); //NOI18N
            URL infoPlistXsl = PackagerProject.class.getResource("resources/mac/info.plist.xsl"); //NOI18N

            URL jnlpXsl = PackagerProject.class.getResource("resources/jnlp/jnlp.xsl"); //NOI18N
            
            //PENDING:  Check the dependencies of the subprojects to make sure there's nothing 
            //new we need to include
            
            try {
                genFilesHelper.refreshBuildScript(GeneratedFilesHelper.BUILD_XML_PATH, buildXsl, true);
                genFilesHelper.refreshBuildScript(GeneratedFilesHelper.BUILD_IMPL_XML_PATH, buildImplXsl, true);
                genFilesHelper.refreshBuildScript("Configurations/Macintosh/start.sh", startShXsl, true); //NOI18N
                genFilesHelper.refreshBuildScript("Configurations/Macintosh/Info.plist", infoPlistXsl, true); //NOI18N
                
                genFilesHelper.refreshBuildScript("Configurations/WebStart/app.jnlp", jnlpXsl, true); //NOI18N
                
                maybeRefreshIcon();
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        private void maybeRefreshIcon() throws IOException, URISyntaxException {
            FileObject root = prj.getProjectDirectory();
            String expectedName = "Configurations/Macintosh/" + ProjectUtils.getInformation(prj).getDisplayName() + ".icns"; //NOI18N
            if (root.getFileObject(expectedName) == null) {
                URL macIconFile = PackagerProject.class.getResource("resources/mac/generic.icns"); //NOI18N
                
                InputStream in = macIconFile.openStream();
                try {
                    FileObject iconFile = FileUtil.createData(root, expectedName);
                    FileLock lock = iconFile.lock();
                    try {
                        OutputStream out = iconFile.getOutputStream(lock);
                        FileUtil.copy(in, out);
                    } finally {
                        lock.releaseLock();
                    }
                } finally {
                    in.close();
                }
            }
        }
    }    
     
    
}
