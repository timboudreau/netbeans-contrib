/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * PackagerProject.java
 *
 * Created on May 26, 2004, 3:05 AM
 */

package org.netbeans.modules.packager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileLock;

/**
 * A project type which takes the build products of other projects and
 * assembles a platform-specific installation structure from them.
 *
 * @author  Tim Boudreau
 */
public class PackagerProject implements Project {
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
    
    public static final String PROP_MAIN_CLASS="main.class";
    public static AntProjectHelper createProject (final File dir, final String name, final Project[] projects) throws IOException {
        assert dir != null : "Source folder must be given";   //NOI18N
        
        final FileObject dirFO = createProjectDir (dir);
        final AntProjectHelper h = ProjectGenerator.createProject(dirFO, PackagerProjectType.TYPE, name);
        
        final PackagerProject p = (PackagerProject) ProjectManager.getDefault().findProject(dirFO);
        
        p.setPackagedProjects(projects);
                
        return h;
    } 
    
    public void setPackagedProjects (final Project[] projects) {
        final ReferenceHelper refHelper = getReferenceHelper();
        Set current = subprojects.getSubProjects();
        
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
                for (int i=0; i < projects.length; i++) {
                    if (mainClass == null) {
                        mainClass = findMainClass(projects[i]);
                    }
                    AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(
                        projects[i], JavaProjectConstants.ARTIFACT_TYPE_JAR);
                    for (int j=0; j < artifacts.length; j++) {
                        refHelper.addReference(artifacts[j]);
                    }
                }
                
                if (mainClass != null) {
                    EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    ep.setProperty (PROP_MAIN_CLASS, mainClass);
                    helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                }
                
                for (Iterator i=removed.iterator(); i.hasNext();) {
                    Project rem = (Project) i.next();
                    
                    String projname = ProjectUtils.getInformation(rem).getName();
                    
                    AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(
                        rem, JavaProjectConstants.ARTIFACT_TYPE_JAR);
                    for (int j=0; j < artifacts.length; j++) {
                        FileObject fo = artifacts[j].getArtifactFile();
                        refHelper.removeReference(fo.getPath());
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
            
            try {
                genFilesHelper.refreshBuildScript(GeneratedFilesHelper.BUILD_XML_PATH, buildXsl, true);
                genFilesHelper.refreshBuildScript(GeneratedFilesHelper.BUILD_IMPL_XML_PATH, buildImplXsl, true);
                genFilesHelper.refreshBuildScript("Configurations/Macintosh/start.sh", startShXsl, true); //NOI18N
                genFilesHelper.refreshBuildScript("Configurations/Macintosh/Info.plist", infoPlistXsl, true); //NOI18N
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
