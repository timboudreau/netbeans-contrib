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
public class DefaultPortletEventingHandlerImpl implements PortletEventingHandler {

    public EventObject[] getPublishEvents(String portletName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EventObject[] getProcessEvents(String portletName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    public boolean isEventingSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addProcessEvent(String targetPortlet, EventObject event, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addPublishEvent(String portlet, EventObject evt, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deleteProcessEvent(String portlet, EventObject evt) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deletePublishEvent(String portlet, EventObject evt) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isProcessEventExists(String portlet, EventObject evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isPublishEventExists(String portlet, EventObject evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean renamePublishEvent(String portlet, EventObject oldEvent, EventObject newEvent, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean renameProcessEvent(String portlet, EventObject oldEvent, EventObject newEvent, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPublishEventMethodBody(String portlet, EventObject eventName, boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProcessEventMethodBody(String portlet, EventObject eventName, boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generatePublishEventMethod(String portlet, EventObject eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateProcessEventMethod(String portlet, EventObject eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAlias(EventObject event, QName alias) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /** Creates a new instance of DefaultPorletEventingHandlerImpl */
   
}
