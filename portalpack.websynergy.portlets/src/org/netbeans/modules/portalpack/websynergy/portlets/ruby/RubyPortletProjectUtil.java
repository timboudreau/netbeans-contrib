/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.ruby;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.portalpack.commons.ruby.RubyPlatformUtil;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author PrakashR
 */
public class RubyPortletProjectUtil {

    public static void addRubyLibrary(WebModule wm) {
        
        final FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        Sources sources = project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (groups.length > 0) {
            try {

                ClassPath cp = ClassPath.getClassPath(wm.getDocumentBase(), ClassPath.COMPILE);
                if (cp == null || cp.findResource("org/apache/bsf/BSFManager.class") == null) { //NOI18N

                    Library bpLibrary = LibraryManager.getDefault().getLibrary("Liferay-Ruby-Portlet-Lib");
                    
                    if(bpLibrary == null) {
                        
                        String jrubyLibDir = RubyPlatformUtil.getRubyLibDir();
                        URL bsfJar = FileUtil.toFileObject(new File(jrubyLibDir + File.separator + "bsf.jar")).getURL();
                        URL jrubyJar = FileUtil.toFileObject(new File(jrubyLibDir + File.separator + "jruby.jar")).getURL();
                       
                        
                        List urls = new ArrayList();
                        urls.add(bsfJar);
                        urls.add(jrubyJar);
                        
                        Map content = new HashMap();
                        content.put("classpath", urls);
                        bpLibrary = LibraryManager.getDefault().createLibrary("j2se","Liferay-Ruby-Portlet-Lib" , content);
               
                    }
                    for (int i = 0; i < groups.length; i++) {
                        ProjectClassPathModifier.addLibraries(new Library[]{bpLibrary}, groups[i].getRootFolder(), ClassPath.COMPILE);
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public static String[] createRubyFiles(WebModule wm, FileObject customRubyFolder) {
        
        FileObject webInf = wm.getWebInf();
        TemplateUtil templateUtil = new TemplateUtil(RubyPortletConstants.TEMPLATE_PATH);
        
        try {
            FileObject rubyFolder = webInf.getFileObject("ruby");
            if (rubyFolder == null) {
                rubyFolder = webInf.createFolder("ruby");
            }
            FileObject globalFolder = rubyFolder.getFileObject("global");
            if (globalFolder == null) {
                globalFolder = rubyFolder.createFolder("global");
            // RubyTemplateUtil.createFileFromTemplate("Info.template", rubyFolder, 
            //                                       "info", "rb");
            //RubyTemplateUtil.createFileFromTemplate("Java.template", rubyFolder, 
            //                                        "java", "rb");
            //RubyTemplateUtil.createFileFromTemplate("View.template", rubyFolder, 
            //                                        "view", "rb");
            }
            FileObject customTemplate = globalFolder.getFileObject("custom","rb");
            if (customTemplate == null) {
                templateUtil.createFileFromTemplate("Custom.template", globalFolder,
                        "custom", "rb");
            }
            FileObject liferayPortletTemplate = globalFolder.getFileObject("liferay_portlet","rb");
            if (liferayPortletTemplate == null) {
                templateUtil.createFileFromTemplate("liferay-portlet.template", globalFolder,
                        "liferay_portlet", "rb");
            }

            String globalFolderRelativePath = FileUtil.getRelativePath(wm.getDocumentBase(), globalFolder);
            if (globalFolderRelativePath != null) {
                if (!globalFolderRelativePath.startsWith("/")
                        && !globalFolderRelativePath.startsWith("\\")) {
                    globalFolderRelativePath = "/" + globalFolderRelativePath;
                }
                if (!globalFolderRelativePath.endsWith("/")
                        && !globalFolderRelativePath.endsWith("\\")) {
                    globalFolderRelativePath += "/";
                }
                globalFolderRelativePath = globalFolderRelativePath.replace("\\", "/");
            }
            
            return new String[]{globalFolderRelativePath + "custom.rb", globalFolderRelativePath + "liferay_portlet.rb"};


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }
}
