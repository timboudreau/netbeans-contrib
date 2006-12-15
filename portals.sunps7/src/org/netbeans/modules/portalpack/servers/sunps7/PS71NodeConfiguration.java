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

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSNodeConfiguration;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Satya
 */
public class PS71NodeConfiguration extends DefaultPSNodeConfiguration{

    private static PS71NodeConfiguration instance;
    protected PS71NodeConfiguration()
    {

    }
    public static PSNodeConfiguration getInstance()
    {
        if(instance == null)
        {
            synchronized(PS71NodeConfiguration.class)
            {
                 if(instance == null)
                    instance = new PS71NodeConfiguration();
            }
        }
        return instance;
    }

}
