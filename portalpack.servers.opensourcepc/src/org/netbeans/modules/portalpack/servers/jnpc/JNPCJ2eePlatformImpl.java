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

package org.netbeans.modules.portalpack.servers.jnpc;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;

/**
 *
 * @author satya
 */
public class JNPCJ2eePlatformImpl extends PSJ2eePlatformImpl{
    
    /** Creates a new instance of JNPCJ2eePlatformImpl */
    public JNPCJ2eePlatformImpl(PSDeploymentManager dm) {
        super(dm);
        
    }
    
    protected List getCustomLibraries() {
         List classPath = new ArrayList();
         
         String[] libFiles = {"portlet-api-1.0.jar","portlettaglib-1.0.jar"};
         PSConfigObject psconfig = dm.getPSConfig();
         
         for(int i=0;i<libFiles.length;i++)
         {
            String portletJarUri = psconfig.getPSHome() + File.separator + "lib" + File.separator + libFiles[i];
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
         return classPath;     
    }
    
}
