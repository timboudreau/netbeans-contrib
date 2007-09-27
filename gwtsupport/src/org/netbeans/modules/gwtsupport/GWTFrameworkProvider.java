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

package org.netbeans.modules.gwtsupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.gwtsupport.settings.GWTSettings;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * @author Tomas.Zezula@Sun.COM
 * @author Tomasz.Slota@Sun.COM
 */
public class GWTFrameworkProvider extends WebFrameworkProvider {
    private static final String LIBS_FOLDER = "org-netbeans-api-project-libraries/Libraries";               //NOI18N
    private static final String PATTERN_PRJ_NAME = "__PROJECT_NAME__";                                      //NOI18N
    private static final String RESOURCE_BASE = "org/netbeans/modules/gwtsupport/resources/";              //NOI18N
    private static final String GWT_DEV  = "gwt-dev-\\w*.jar";                                              //NOI18N
    private static final String BUILD_GWT   = "build-gwt.xml";                                              //NOI18N
    private static final String PRJ_DIR =  "nbproject";                                                     //NOI18N
    private static final String PUBLIC_FOLDER = "public";                                                   //NOI18N
    private static final String CLIENT_FOLDER = "client";                                                   //NOI18N
    private static final String SERVER_FOLDER = "server";                                                   //NOI18N
    private static final String TEMPLATE_JAVA_CLASS = "Templates/Classes/EntryPoint.java";                  //NOI18N
    private static final String GWT_USER = "gwt-user.jar";                                                  //NOI18N
    private static final String LIB_GWT_NAME = "GWT";                                                       //NOI18N
    private static final String LIB_GWT = "gwt.xml";                                                        //NOI18N
    private GWTConfigPanel pnlConfig = new GWTConfigPanel();
    
    /** Creates a new instance of GWTFrameworkProvider */
    public GWTFrameworkProvider() {
        super("Google Web Toolkit", "Desc");
    }
    
    @Override public Set extend(WebModule webModule) {
        EnableGWTAction enableGWTAction = new EnableGWTAction(webModule);
        
        try {
            webModule.getDocumentBase().getFileSystem().runAtomicAction(enableGWTAction);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        
        return enableGWTAction.getObjectsToOpen();
    }
    
    private class EnableGWTAction implements AtomicAction{
        private Set<FileObject> toOpen = new LinkedHashSet<FileObject>();
        private WebModule webModule;
        
        EnableGWTAction(WebModule webModule){
            this.webModule = webModule;
        }
        
        public void run() throws IOException {
            Project project = FileOwnerQuery.getOwner(webModule.getDocumentBase());
            assert project != null;
            
            
            ProjectInformation projectInfo = ProjectUtils.getInformation(project);
            String projectName = projectInfo.getName();
            try{
                FileObject nbprj = project.getProjectDirectory().getFileObject(PRJ_DIR);
                // COPY build-gwt.xml
                FileObject buildGwt = nbprj.createData(BUILD_GWT);
                copyResource(RESOURCE_BASE + BUILD_GWT, buildGwt, new String[] {PATTERN_PRJ_NAME}, new String[] {projectName});
                FileObject foBuildXML = nbprj.getParent().getFileObject("build.xml");
                patchBuildXML(foBuildXML);
                
                // ADD LIBRARY
                ProjectClassPathExtender cpe = (ProjectClassPathExtender) project.getLookup().lookup(ProjectClassPathExtender.class);
                File gwtFolder = pnlConfig.getGwtFolder();
                Library libUser = createGWTUserLibrary(gwtFolder);
                
                if (libUser != null) {
                    GWTSettings gwtSettings = GWTSettings.getDefault();
                    gwtSettings.setGWTLocation(gwtFolder);
                    cpe.addLibrary(libUser);
                } else{
                    ErrorManager.getDefault().log("Could not add GWT library");
                }
                
                FileObject gwtRtLib = getGWTDevArchive(gwtFolder);
                assert gwtRtLib != null;
                cpe.addArchiveFile(gwtRtLib);
                
                FileObject gwtServletLib = FileUtil.toFileObject(
                        FileUtil.normalizeFile(new File(gwtFolder, "gwt-servlet.jar")));
                
                cpe.addArchiveFile(gwtServletLib);
                
                // CREATE ENTRY POINT CLASS
                FileObject src = nbprj.getParent().getFileObject("src/java");//properties.getProperty("src.dir"));
                
                String [] epNames = getEntryPointModuleName(pnlConfig.getGWTModule());
                if (epNames != null) {
                    assert epNames.length == 2 && epNames[0] != null && epNames[1] != null;
                    createEntryPoint(src, epNames[0], epNames[1], toOpen);
                }
                
                // CREATE gwt.properties
                createGWTProperties(nbprj, pnlConfig.getGWTModule());
                
                patchIndexJSP(webModule.getDocumentBase().getFileObject("index.jsp"), pnlConfig.getGWTModule());
                
            } catch (IOException e){
                ErrorManager.getDefault().notify(e);
            }
        }
        
        public Set<FileObject> getObjectsToOpen(){
            return toOpen;
        }
    }
    
    @Override public boolean isInWebModule(WebModule webModule) {
        Project project = FileOwnerQuery.getOwner(webModule.getDocumentBase());
        FileObject buildGWT = project.getProjectDirectory().getFileObject(PRJ_DIR + "/" + BUILD_GWT);
        return buildGWT != null;
    }
    
    public File[] getConfigurationFiles(WebModule webModule) {
        return null;
    }
    
    public FrameworkConfigurationPanel getConfigurationPanel(WebModule webModule) {
        return pnlConfig;
    }
    
    /**
     *XXX: Replace with apache velocity?
     */
    private static void copyResource(final String res, final FileObject to, final String[] mapFrom, final String[] mapTo) throws IOException {
        assert res != null;
        assert to != null;
        assert mapFrom != null;
        assert mapTo != null;
        assert mapFrom.length == mapTo.length;
        InputStream _in = GWTFrameworkProvider.class.getClassLoader().getResourceAsStream(res);
        assert _in != null;
        BufferedReader in = new BufferedReader(new InputStreamReader(_in));
        try {
            FileLock lock = to.lock();
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(to.getOutputStream(lock)));
                try {
                    copyStream(in,out,mapFrom,mapTo);
                } finally {
                    out.close();
                }
            } finally {
                lock.releaseLock();
            }
        } finally {
            in.close();
        }
    }
    
    private static void copyStream(final BufferedReader in , final PrintWriter out, final String[] mapFrom, String[] mapTo) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            for (int i=0; i<mapFrom.length; i++) {
                line = line.replaceAll(mapFrom[i],mapTo[i]);
            }
            out.println(line);
        }
    }
    
    private static void createGWTProperties(FileObject projectDir, String gwtModule) throws IOException{
        FileObject propFile = projectDir.createData("gwt.properties");
        FileLock lock = propFile.lock();
        PrintWriter writer = new PrintWriter(propFile.getOutputStream(lock));
        writer.println("gwt.module=" + gwtModule);
        writer.close();
        lock.releaseLock();
    }
    
    private static FileObject getGWTDevArchive(final File gwtRoot) {
        assert gwtRoot != null;
        assert gwtRoot.exists() && gwtRoot.isDirectory();
        File[] files = gwtRoot.listFiles();
        Pattern pattern = Pattern.compile(GWT_DEV,Pattern.CASE_INSENSITIVE);
        for (File file : files) {
            if (pattern.matcher(file.getName()).matches()) {
                return FileUtil.toFileObject(FileUtil.normalizeFile(file));
            }
        }
        return null;
    }
    
    private static String[] getEntryPointModuleName(String entryPointClass) {
        if (entryPointClass == null) {
            return null;
        }
        int endIndex = entryPointClass.lastIndexOf('.');
        assert endIndex > 0;
        return new String[] {
            entryPointClass.substring(0,endIndex),
            entryPointClass.substring(endIndex+1)
        };
    }
    
    private static void createEntryPoint(final FileObject srcRoot, final String pkg, final String cls, final Set<FileObject> toOpen) throws IOException {
        assert srcRoot != null;
        assert pkg != null;
        assert cls != null;
        assert toOpen != null;
        FileObject folder;
        if (pkg.length()>0) {
            folder = FileUtil.createFolder(srcRoot,pkg.replace('.','/'));    //NOI18N
        } else {
            folder = srcRoot;
        }
        assert folder != null;
        FileObject pf = folder.createFolder(PUBLIC_FOLDER);
        FileObject cf = folder.createFolder(CLIENT_FOLDER);
        FileObject sf = folder.createFolder(SERVER_FOLDER);
        toOpen.add(createModule(folder,pkg, cls));
        createHtml(pf,pkg,cls);
        toOpen.add(createJava(cf, cls ));
    }
    
    private static String getEntryPointClassName(String moduleName){
        return moduleName + "EntryPoint";
    }
    
    
    private static void patchBuildXML(FileObject foBuildXML){
        //TODO: provide a better implementation
        replaceInFile(foBuildXML, "<import file=\"nbproject/build-impl.xml\"/>",
                "<import file=\"nbproject/build-gwt.xml\"/>"
                + System.getProperty("line.separator")
                + "\t<import file=\"nbproject/build-impl.xml\"/>");
    }
    
    /** Changes the index.jsp file. Only when there is <h1>JSP Page</h1> string.
     */
    private void patchIndexJSP(FileObject indexjsp, String gwtModule) throws IOException {
        String nl = System.getProperty("line.separator");
        replaceInFile(indexjsp, "<h1>JSP Page</h1>", "<h1>JSP Page</h1>" + nl
                + "\t<h3><a href=\"" + gwtModule + "/index.html\">GWT page</a></h3>" + nl);
    }
    
    private static void replaceInFile(FileObject file, String searchedString, String replacement){
        //TODO: provide a better implementation
        String orgFileContent = null;
        
        InputStream is = null;
        OutputStream os = null;
        try     {
            is = file.getInputStream();
            byte rawContent[] = new byte[is.available()];
            is.read(rawContent);
            orgFileContent = new String(rawContent);
            
            is.close();
            
            String alteredContent = orgFileContent.replace(searchedString, replacement);
            FileLock lock = file.lock();
            PrintWriter writer = new PrintWriter(file.getOutputStream(lock));
            writer.print(alteredContent);
            writer.close();
            lock.releaseLock();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        } finally {
            try {
                if (is != null){
                    is.close();
                }
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }
    
    private static FileObject createHtml(final FileObject folder, final String pkg, final String cls) throws IOException {
        assert folder != null;
        assert folder.isFolder();
        assert pkg != null;
        assert cls != null;
        FileObject fo = folder.createData("index.html");  //NOI18N
        FileLock lock = fo.lock();
        try {
            PrintWriter out = new PrintWriter( new OutputStreamWriter(fo.getOutputStream(lock)));
            try {
                out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
                out.println("<html>");
                out.println("    <head>");
                out.println("        <meta name='gwt:module' content='"+pkg+'.'+cls+"'>");
                out.println("        <title>"+cls+"</title>");
                out.println("    </head>");
                out.println("    <body>");
                out.println("        <script language=\"javascript\" src=\"gwt.js\"></script>");
                out.println("    </body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        } finally {
            lock.releaseLock();
        }
        return fo;
    }
    
    private static FileObject createModule(final FileObject folder, final String pkg, final String cls) throws IOException {
        assert folder != null;
        assert folder.isFolder();
        assert cls != null;
        FileObject fo = folder.createData(cls+".gwt.xml");  //NOI18N
        FileLock lock = fo.lock();
        try {
            PrintWriter out = new PrintWriter( new OutputStreamWriter(fo.getOutputStream(lock)));
            try {
                String className = getEntryPointClassName(cls);
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");          //NOI18N
                out.println("<module>");                                            //NOI18N
                out.println("\t<inherits name=\"com.google.gwt.user.User\"/>");     //NOI18N
                out.println("\t<entry-point class=\"" + (pkg != null ? (pkg+'.'): "") + CLIENT_FOLDER + '.' + className + "\"/>"); //NOI18N
                out.println("</module>");                                           //NOI18N
            } finally {
                out.close();
            }
        } finally {
            lock.releaseLock();
        }
        return fo;
    }
    
    @SuppressWarnings("unchecked")  //NOI18N
    private static FileObject createJava(final FileObject folder, final String cls) throws IOException {
        assert folder != null;
        assert folder.isFolder();
        assert cls != null;
        final FileObject jc = Repository.getDefault().getDefaultFileSystem().findResource(TEMPLATE_JAVA_CLASS);
        assert jc != null;
        final DataObject template = DataObject.find(jc);
        assert template != null;
        String className = getEntryPointClassName(cls);
        DataObject jdo = template.createFromTemplate(DataFolder.findFolder(folder), className);
        return jdo.getPrimaryFile();
    }
    
    //XXX: Replace this when API for managing libraries is available
    public static Library createGWTUserLibrary(File gwtFolder) throws IOException {
        assert gwtFolder != null;
        final File userJar = new File(gwtFolder,GWT_USER);
        if (!userJar.exists()) {
            return null;
        }
        
        Library lib = LibraryManager.getDefault().getLibrary(LIB_GWT_NAME);
        final List<URL> src = lib == null ? Collections.<URL>emptyList() : (List<URL>) lib.getContent("src");
        List<URL> _javadoc = lib == null ? Collections.<URL>emptyList() : (List<URL>) lib.getContent("javadoc");
        final File gwtJavadoc = FileUtil.normalizeFile(new File(new File(gwtFolder, "doc"), "javadoc"));
        if (_javadoc.isEmpty() && gwtJavadoc.exists() && gwtJavadoc.isDirectory() && gwtJavadoc.canRead()) {
            _javadoc = Collections.singletonList(gwtJavadoc.toURI().toURL());
        }
        final List<URL> javadoc = _javadoc;
        final FileSystem sysFs = Repository.getDefault().getDefaultFileSystem();
        assert sysFs != null;
        final FileObject libsFolder = sysFs.findResource(LIBS_FOLDER);
        assert libsFolder != null && libsFolder.isFolder();
        FileObject gwt = libsFolder.getFileObject(LIB_GWT);
        if (gwt == null) {
            gwt = libsFolder.createData(LIB_GWT);
        }
        FileLock lock = gwt.lock();
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(gwt.getOutputStream(lock)));
            try {
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");  //NOI18N
                out.println("<!DOCTYPE library PUBLIC \"-//NetBeans//DTD Library Declaration 1.0//EN\" \"http://www.netbeans.org/dtds/library-declaration-1_0.dtd\">");  //NOI18N
                out.println("<library version=\"1.0\">");           //NOI18N
                out.println("\t<name>"+LIB_GWT_NAME+"</name>");                  //NOI18N
                out.println("<localizing-bundle>org.netbeans.modules.gwtsupport.Bundle</localizing-bundle>");
                out.println("\t<type>j2se</type>");                 //NOI18N
                out.println("\t<volume>");                          //NOI18N
                out.println("\t\t<type>classpath</type>");          //NOI18N
                out.println("\t\t<resource>"+FileUtil.getArchiveRoot(userJar.toURI().toURL()).toString()+"</resource>");    //NOI18N
                out.println("\t</volume>");                         //NOI18N
                out.println("\t<volume>");                          //NOI18N
                out.println("\t\t<type>src</type>");                //NOI18N
                out.println("\t</volume>");                         //NOI18N
                for (URL root : src) {
                    out.println("\t\t<resource>"+root.toExternalForm()+"</resource>");    //NOI18N
                }
                out.println("\t<volume>");                          //NOI18N
                out.println("\t\t<type>javadoc</type>");            //NOI18N
                for (URL root : javadoc) {
                    out.println("\t\t<resource>"+root.toExternalForm()+"</resource>");    //NOI18N
                }
                out.println("\t</volume>");                         //NOI18N
                out.println("</library>");                          //NOI18N
            } finally {
                out.close();
            }
        } finally {
            lock.releaseLock();
        }
        
        if (lib == null){
            lib = LibraryManager.getDefault().getLibrary(LIB_GWT_NAME);
        }
        
        return lib;
    }
    
}
