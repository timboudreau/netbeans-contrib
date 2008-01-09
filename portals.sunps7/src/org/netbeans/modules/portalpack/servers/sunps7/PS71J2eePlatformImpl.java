/*
 * PS71J2eePlatformImpl.java
 *
 * Created on May 15, 2007, 3:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.servers.sunps7;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.Util;

/**
 *
 * @author Satyaranjan
 */
public class PS71J2eePlatformImpl extends PSJ2eePlatformImpl{

    public PS71J2eePlatformImpl(PSConfigObject psconfig) {
        super(psconfig);
    }

     protected List getCustomLibraries() {
         List classPath = new ArrayList();
         
         String[] libFiles = {"javaee-api-5.jar"};
         //PSConfigObject psconfig = psconfig.getPSConfig();
         
         for(int i=0;i<libFiles.length;i++)
         {
            String portletJarUri = RegistryLibrary.SUNPS_LIB_DIR + File.separator + libFiles[i];
            File portletJar = new File(portletJarUri);
            if(portletJar.exists())
            {
                try {
                    classPath.add(fileToUrl(portletJar));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
         }
         

         String[] encClassPaths = Util.decodeClassPath(psconfig.getClassPath());
         for(int i=0;i<encClassPaths.length;i++)
         {
             File classpathJar = new File(encClassPaths[i]);
             if(classpathJar.exists())
             {
                try {
                    classPath.add(fileToUrl(classpathJar));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
             }
         }
         return classPath;     
    }

}
