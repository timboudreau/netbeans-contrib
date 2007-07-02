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
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.util.PortletProjectUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
    
    public Set extend(WebModule wm) {
       Set resultSet = new LinkedHashSet();
       final  FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        
        Map data = panel.getData();
        PortletContext context = (PortletContext)data.get("context");
         try{
            FileObject dd = wm.getDeploymentDescriptor();
           // WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
           // if (ddRoot != null){
                //  if (!WebApp.VERSION_2_5.equals(ddRoot.getVersion())) {
                Library bpLibrary = null;
                if(context.getPortletVersion().equals(NetbeanConstants.PORTLET_2_0))
                    bpLibrary = LibraryManager.getDefault().getLibrary("Portlet-2.0-Lib"); //NOI18N
                else
                    bpLibrary = LibraryManager.getDefault().getLibrary("Portlet-1.0-Lib"); //NOI18N
                if (bpLibrary != null) {
                    FileObject[] sources = wm.getJavaSources();
                   for(int i =0;i<sources.length;i++)
                    ProjectClassPathModifier.addLibraries(new Library[]{bpLibrary}, sources[i], ClassPath.COMPILE);
                }
              
         }catch(Exception e){
             e.printStackTrace();
         } 
         
         String pkg = (String)data.get("package");
         FileObject srcFolder = (FileObject)data.get("src_folder");
         if(srcFolder == null)
             srcFolder = wm.getJavaSources()[0];
         if(pkg == null) pkg = "";
         if(((String)data.get("generate_portlet")).equals("true"))
         {
            PortletProjectUtil.createPkgAndClass(srcFolder, project, wm,pkg,context);
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
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void excludePortlet1_0LibrariesFromPackaging()
    {
      /*  
        Project p;
        ProjectUtils.getInformation(p).
         Element data = projectHelper.getPrimaryConfigurationData(true);
                Document doc = data.getOwnerDocument();
                Element nameEl = doc.createElement("name"); // NOI18N
                nameEl.appendChild(doc.createTextNode(prjName));
                data.appendChild(nameEl);
                Element minant = doc.createElement("minimum-ant-version"); // NOI18N
                minant.appendChild(doc.createTextNode(MINIMUM_ANT_VERSION)); // NOI18N
                data.appendChild(minant);            
                
                Element sourceRoots = doc.createElement("source-roots");  //NOI18N
                data.appendChild (sourceRoots);
                Element testRoots = doc.createElement("test-roots");  //NOI18N
                data.appendChild (testRoots);
        
                Element srcDir = doc.createElement("root");
                srcDir.setAttribute("id","src.dir");
                sourceRoots.appendChild(srcDir);
                         
                projectHelper.putPrimaryConfigurationData(data, true);*/
    }

    public boolean isInWebModule(WebModule wm) {
        FileObject webInfObj = wm.getWebInf();
        
        File portletXml = new File(FileUtil.toFile(webInfObj),"portlet.xml");
        return portletXml.exists();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public File[] getConfigurationFiles(WebModule wm) {
        File portletXmlFile = new File(FileUtil.toFile(wm.getWebInf()),"portlet.xml");
        return new File[]{portletXmlFile};
        
    }

    public FrameworkConfigurationPanel getConfigurationPanel(WebModule wm) {
       
       if(wm != null && isInWebModule(wm))
           return null;
       panel = new  PortletApplicationWizardPanel(wm);
        
        return panel;
       
    }

}
