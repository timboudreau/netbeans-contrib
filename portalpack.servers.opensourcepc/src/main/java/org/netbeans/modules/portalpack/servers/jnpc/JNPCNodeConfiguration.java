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

import javax.swing.Action;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.jnpc.node.action.ShowMultiplePortletsAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author root
 */
public class JNPCNodeConfiguration extends DefaultPSNodeConfiguration{
    
    private static JNPCNodeConfiguration instance;
    /** Creates a new instance of JNPCNodeConfiguration */
    private JNPCNodeConfiguration() {
    }
    
    public static PSNodeConfiguration getInstance(){
        
        if(instance == null)
        {
            synchronized(JNPCNodeConfiguration.class)
            {
                if(instance == null)
                    instance = new JNPCNodeConfiguration();
                
            }
            
        }
        return instance;
    }
    
    public boolean showContainerNodes() {
        return false;
    }
    
    public boolean showTopChannelsNode() {
        return false;
    }  
    
    public Action[] getDnActions() {
        
        javax.swing.Action[]  newActions = new javax.swing.Action[1] ;
        newActions[0]=(null);  
        //newActions[1] = SystemAction.get(ShowMultiplePortletsAction.class);
        return newActions;
    }
}
