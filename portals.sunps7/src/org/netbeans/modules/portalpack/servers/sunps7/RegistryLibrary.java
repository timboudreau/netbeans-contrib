/*
 * RegistryLibrary.java
 *
 * Created on May 15, 2007, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.servers.sunps7;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Satyaranjan
 */
public class RegistryLibrary {
    public static final String SUNPS_COMPONENT_FOLDER = "ps71";
    public static final String SUNPS_LIB_DIR = NetbeanConstants.CONFIG_DIR + File.separator + SUNPS_COMPONENT_FOLDER;
    public String[] libs = {"portletappengine-1.0.jar"};
    private static RegistryLibrary registryLibrary;
    /** Creates a new instance of RegistryLibrary */
    private RegistryLibrary() {
    }

    public static RegistryLibrary getDefault()
    {
        if(registryLibrary == null)
            registryLibrary = new RegistryLibrary();
        return registryLibrary;

    }
    
     public File getLibraryFolder() {
                
        File userDir = new File(SUNPS_LIB_DIR);
        if(!userDir.exists())
            userDir.mkdirs();
        
        return userDir;
    }

    public void copyLibs(boolean overwrite)
    {
        File libDir = getLibraryFolder();
        for(int i=0;i<libs.length;i++)
        {
            InputStream ins = null;
            OutputStream outs = null;
            try{
               File outputfile = new File(libDir,libs[i]);
               if(outputfile.exists() && !overwrite)
                 continue;
               ins = getClass().getResourceAsStream("/libs/"+libs[i]);
               outs = new FileOutputStream(outputfile);
               FileUtil.copy(ins,outs);
               outs.flush();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
              try{
                if(ins != null) ins.close();
                if(outs != null) outs.close();
              }catch(Exception e){
                e.printStackTrace();
              }
            }
        }
    }
}
