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

package org.netbeans.modules.portalpack.servers.jnpc.node;

import org.netbeans.modules.j2ee.deployment.devmodules.api.AntDeploymentHelper;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.openide.nodes.Children;

/**
 *
 * @author root
 */
public class CustomNode extends BaseNode{
    
    /** Creates a new instance of CustomNode */
   
    public CustomNode(Children children)
    {
        super(children);
    }
    public String getKey() {
        return "TestNode";
    }

    public String getDn() {
        return "TestDN";
    }

    public String getType() {
        return "TestType";
    }

    public PSDeploymentManager getDeploymentManager() {
        return null;
    }
    

    public String getDisplayName() {
        return "CustomNode";
    }
    
}
