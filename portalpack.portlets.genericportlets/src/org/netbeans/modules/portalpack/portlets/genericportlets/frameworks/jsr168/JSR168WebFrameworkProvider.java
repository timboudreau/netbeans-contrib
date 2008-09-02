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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.jsr168;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.util.PortletFrameworkUtil;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.project.api.WebProjectLibrariesModifier;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class JSR168WebFrameworkProvider extends WebFrameworkProvider{
    private PortletApplicationWizardPanel panel;
    /**
     * Default constructor for JSR168WebFramework (Portlet 1.0 Support)
     */
    public JSR168WebFrameworkProvider()
    {
        super(NbBundle.getMessage(JSR168WebFrameworkProvider.class, "LBL_PORTLET_FRAMEWORK"),NbBundle.getMessage(JSR168WebFrameworkProvider.class, "LBL_PORTLET_FRAMEWORK_DESC"));
    }
    
    @Override
    public WebModuleExtender createWebModuleExtender(WebModule wm, ExtenderController controller) {
        
        boolean customizer = (wm != null && isInWebModule(wm));
        panel = new  PortletApplicationWizardPanel(this,wm,controller,customizer);
        
        return panel;
    }
    
    public Set extendImpl(WebModule wm) {
       Set resultSet = new LinkedHashSet();
       final  FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        
        Map data = panel.getData();
        PortletContext context = (PortletContext)data.get("context");
         try{
            FileObject dd = wm.getDeploymentDescriptor();
          
                Library bpLibrary = null;
                if(context.getPortletVersion().equals(NetbeanConstants.PORTLET_2_0))
                    bpLibrary = LibraryManager.getDefault().getLibrary("Portlet-2.0-Lib"); //NOI18N
                else
                    bpLibrary = LibraryManager.getDefault().getLibrary("Portlet-1.0-Lib"); //NOI18N
                
                Lookup lookup = project.getLookup();
                Object modifierObj = lookup.lookup(WebProjectLibrariesModifier.class);
                if(modifierObj != null && (modifierObj instanceof WebProjectLibrariesModifier))
                {
                    ((WebProjectLibrariesModifier)modifierObj).addCompileLibraries(new Library[]{bpLibrary});
                }
                
              
         }catch(Exception e){
             e.printStackTrace();
         } 
         
         String pkg = (String)data.get("package");
         FileObject srcFolder = (FileObject)data.get("src_folder");
         if(srcFolder == null)
         {
            Sources sources = (Sources)project.getLookup().lookup(Sources.class);
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if(groups.length > 0)
                srcFolder = groups[0].getRootFolder();
         }
         if(pkg == null) pkg = "";
         if(((String)data.get("generate_portlet")).equals("true"))
         {
            PortletFrameworkUtil.createPkgAndClass(srcFolder, project, wm,pkg,context);
            resultSet.add(srcFolder);
         }
         else
         {
            try{
                WebDescriptorGenerator webDescGen = new org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator();
                File portletXml = webDescGen.createPortletXml(org.openide.filesystems.FileUtil.toFile(wm.getWebInf()).getAbsolutePath(),context,new java.util.HashMap());
                resultSet.add(FileUtil.toFileObject(portletXml));                                                                                                                 
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
        
    }

    public boolean isInWebModule(WebModule wm) {
        FileObject webInfObj = wm.getWebInf();
        
        File portletXml = new File(FileUtil.toFile(webInfObj),"portlet.xml");
        
        ClassPath cp = ClassPath.getClassPath(wm.getDocumentBase(), ClassPath.COMPILE);
        
        if (cp == null || cp.findResource("javax/portlet/GenericPortlet.class") == null) { //NOI18N
            return false;
        }
        if(!portletXml.exists())
            return false;
        
        return true;
    }

    public File[] getConfigurationFiles(WebModule wm) {
        File portletXmlFile = new File(FileUtil.toFile(wm.getWebInf()),"portlet.xml");
        return new File[]{portletXmlFile};
        
    }

}
