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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.impl;

import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventingHandler;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Satyaranjan
 */
public class PortletEventingHandlerImpl implements PortletEventingHandler{

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PortletXMLDataObject dbObj;
    private Project project;
    private PortletXmlEventingHelper helper;
    public PortletEventingHandlerImpl(FileObject webInfDir,PortletXMLDataObject dbObj) {
         try{
           // initSunPortletXmlHandler(webInfDir);
            project = FileOwnerQuery.getOwner(webInfDir);
       }catch(Error e){
           e.printStackTrace();
           //do nothing
       }
       this.dbObj = dbObj;
       helper = new PortletXmlEventingHelper(this.dbObj);
    }

    public QName[] getPublishEvents(String portletName) {
        return helper.getPublishEvents(portletName);
    }

    public QName[] getProcessEvents(String portletName) {
        return helper.getProcessEvents(portletName);
    }

    public boolean addProcessEvent(String targetPortlet, QName event, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addPublishEvent(String portlet, QName evt, Map properties) throws PortletEventException {
        return helper.addPublishEvent(portlet, evt, properties);
    }

    public boolean deleteProcessEvent(String portlet, QName evt) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deletePublishEvent(String portlet, QName evt) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isProcessEventExists(String portlet, QName evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isPublishEventExists(String portlet, QName evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean renamePublishEvent(String portlet, QName oldEvent, QName newEvent, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean renameProcessEvent(String portlet, QName oldEvent, QName newEvent, Map properties) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPublishEventMethodBody(String portlet, QName eventName, boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProcessEventMethodBody(String portlet, QName eventName, boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generatePublishEventMethod(String portlet, QName eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateProcessEventMethod(String portlet, QName eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEventingSupported() {
        return helper.isEventingSupported();
    }

    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
