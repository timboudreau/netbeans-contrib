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

package org.netbeans.modules.portalpack.servers.core.api;

import java.util.*;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
/**
 *
 * @author Satya
 */
public abstract class PSStartServerInf {
    
    private List listeners;
    public final void startServer() throws Exception
    {
        fireStartStopEvent(StartStopEvent.BEFORE_START);
        doStartServer();
        fireStartStopEvent(StartStopEvent.AFTER_START);
    }
    public final void stopServer() throws Exception
    {
        fireStartStopEvent(StartStopEvent.BEFORE_STOP);
        doStopServer();
        fireStartStopEvent(StartStopEvent.AFTER_STOP);
    }
    
    public final void startDebug() throws Exception
    {
        fireStartStopEvent(StartStopEvent.BEFORE_START);
        doStartDebug();
        fireStartStopEvent(StartStopEvent.AFTER_START);
    }

    public final void stopDebug() throws Exception
    {
        fireStartStopEvent(StartStopEvent.BEFORE_START);
        doStopDebug();
        fireStartStopEvent(StartStopEvent.AFTER_START);
    }
    public void addListener(ServerStartStopListener listener)
    {
        if(listeners == null)
            listeners = new ArrayList();
        
        listeners.add(listener);
    }
    
    public void removeListener(ServerStartStopListener listener)
    {
        if(listeners == null)
            return;
        listeners.remove(listener);
    }
    
    
    private void fireStartStopEvent(String eventType)throws Exception{
        if(listeners == null)
            return;
        
        StartStopEvent event = new StartStopEvent(eventType);
        for(int i =0; i < listeners.size(); i++)
        {
           ServerStartStopListener listener = (ServerStartStopListener)listeners.get(i);
           if(listener != null)
           {
               listener.performAction(event);
           }
        }
    }
    
    public abstract void doStartServer() throws Exception;
    public abstract void doStopServer() throws Exception;
    public abstract void doStartDebug() throws Exception;
    public abstract void doStopDebug() throws Exception;
    public abstract int  getDebugPort(); 
    //Implement this menthod to make debugger find the generated jsps
    public abstract FindJSPServlet getFindJSPServlet(PSDeploymentManager dm);  
       
}
