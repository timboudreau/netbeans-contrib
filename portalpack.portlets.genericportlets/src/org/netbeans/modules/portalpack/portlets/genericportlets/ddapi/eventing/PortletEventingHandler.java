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

package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing;

import java.util.Map;

/**
 *
 * @author Satyaranjan
 */
public interface PortletEventingHandler {
    
    public String[] getPublishEvents(String portletName);
    
    public String[] getProcessEvents(String portletName);
    
    public boolean  addProcessEvent(String targetPortlet,String event,Map properties) throws PortletEventException;
    
    public boolean  addPublishEvent(String portlet,String evt,Map properties) throws PortletEventException;
    
    public boolean  deleteProcessEvent(String portlet,String evt)throws PortletEventException;
    
    public boolean  deletePublishEvent(String portlet,String evt)throws PortletEventException;
    
    public boolean  isProcessEventExists(String portlet,String evt);
    
    public boolean  isPublishEventExists(String portlet,String evt);
  
    public boolean  renamePublishEvent(String portlet,String oldEvent,String newEvent,Map properties)throws PortletEventException;
   
    public boolean  renameProcessEvent(String portlet,String oldEvent,String newEvent,Map properties)throws PortletEventException;
    
    public String getPublishEventMethodBody(String portlet,String eventName, boolean newMethod) throws PortletEventException;
    
    public String getProcessEventMethodBody(String portlet,String eventName, boolean newMethod) throws PortletEventException;
    
    public boolean generatePublishEventMethod(String portlet,String eventName)throws PortletEventException;
    
    public boolean generateProcessEventMethod(String portlet,String eventName)throws PortletEventException;
    
    public void refresh();
}
