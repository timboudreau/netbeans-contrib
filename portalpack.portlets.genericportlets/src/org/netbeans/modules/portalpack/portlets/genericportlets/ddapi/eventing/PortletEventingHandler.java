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
import javax.xml.namespace.QName;

/**
 *
 * @author Satyaranjan
 */
public interface PortletEventingHandler {
    
    public QName[] getPublishEvents(String portletName);
    
    public QName[] getProcessEvents(String portletName);
    
    public boolean  addProcessEvent(String targetPortlet,QName event,Map properties) throws PortletEventException;
    
    public boolean  addPublishEvent(String portlet,QName evt,Map properties) throws PortletEventException;
    
    public boolean  deleteProcessEvent(String portlet,QName evt)throws PortletEventException;
    
    public boolean  deletePublishEvent(String portlet,QName evt)throws PortletEventException;
    
    public boolean  isProcessEventExists(String portlet,QName evt);
    
    public boolean  isPublishEventExists(String portlet,QName evt);
  
    public boolean  renamePublishEvent(String portlet,QName oldEvent,QName newEvent,Map properties)throws PortletEventException;
   
    public boolean  renameProcessEvent(String portlet,QName oldEvent,QName newEvent,Map properties)throws PortletEventException;
    
    public String getPublishEventMethodBody(String portlet,QName eventName, boolean newMethod) throws PortletEventException;
    
    public String getProcessEventMethodBody(String portlet,QName eventName, boolean newMethod) throws PortletEventException;
    
    public boolean generatePublishEventMethod(String portlet,QName eventName)throws PortletEventException;
    
    public boolean generateProcessEventMethod(String portlet,QName eventName)throws PortletEventException;
    
    public boolean isEventingSupported();
    
    public void refresh();
}
