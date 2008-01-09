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
    public String[] libs = {"javaee-api-5.jar"};
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
