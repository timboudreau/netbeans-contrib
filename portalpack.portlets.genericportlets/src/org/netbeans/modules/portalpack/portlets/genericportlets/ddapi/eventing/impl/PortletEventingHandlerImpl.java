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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.lang.model.element.ExecutableElement;
import javax.xml.namespace.QName;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.JavaCodeUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.JavaSourceUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventingHandler;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun.ui.GenerateEventDialog;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.util.PortletXMLUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satyaranjan
 */
public class PortletEventingHandlerImpl implements PortletEventingHandler {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PortletXMLDataObject dbObj;
    private Project project;
    private PortletXmlEventingHelper helper;

    public PortletEventingHandlerImpl(FileObject webInfDir, PortletXMLDataObject dbObj) {
        try {
            // initSunPortletXmlHandler(webInfDir);
            project = FileOwnerQuery.getOwner(webInfDir);
        } catch (Error e) {
            e.printStackTrace();
        //do nothing
        }
        this.dbObj = dbObj;
        helper = new PortletXmlEventingHelper(this.dbObj);
    }

    private FileObject[] findJavaFileObj(String className) {
        className = className.replace(".", File.separator);
        Sources sources = (Sources) project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        List fileObjs = new ArrayList();
        for (int i = 0; i < groups.length; i++) {
            File f = new File(FileUtil.toFile(groups[i].getRootFolder()), className + ".java");
            if (!f.exists()) {
                f = new File(FileUtil.toFile(groups[i].getRootFolder()), className + ".JAVA");
                if (!f.exists()) {
                    continue;
                } else {
                    fileObjs.add(FileUtil.toFileObject(f));
                }
            } else {
                fileObjs.add(FileUtil.toFileObject(f));
            }
        }
        return (FileObject[]) fileObjs.toArray(new FileObject[0]);
    }

    public EventObject[] getPublishEvents(String portletName) {
        return helper.getPublishEvents(portletName);
    }

    public EventObject[] getProcessEvents(String portletName) {
        return helper.getProcessEvents(portletName);
    }

    public boolean addProcessEvent(String targetPortlet, EventObject event, Map properties) throws PortletEventException {
        return helper.addProcessEvent(targetPortlet, event, properties);
    }

    public boolean addPublishEvent(String portlet, EventObject evt, Map properties) throws PortletEventException {
        return helper.addPublishEvent(portlet, evt, properties);
    }

    public boolean deleteProcessEvent(String portlet, EventObject evt) throws PortletEventException {
        return helper.deleteProcessEvent(portlet, evt);
    }

    public boolean deletePublishEvent(String portlet, EventObject evt) throws PortletEventException {
        return helper.deletePublishEvent(portlet, evt);
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
        String portletClassName = null;
        GenerateEventDialog eventDialog = new GenerateEventDialog(WindowManager.getDefault().getMainWindow(),portlet,eventName);
        try {
            portletClassName = PortletXMLUtil.getPortletClassName(dbObj.getPortletApp(), portlet);
            String[] methods = null;
            FileObject[] fileObjs = findJavaFileObj(portletClassName);
            if (fileObjs.length == 0) {
                logger.fine("No Java class file found for ::: " + portletClassName);
                return false;
            }
            List<ExecutableElement> list = JavaCodeUtil.getMethods(portletClassName, fileObjs[0]);
            List publishMethods = JavaCodeUtil.getMethodsForPublishEvent(list);
           
            eventDialog.setMethods(publishMethods);
            eventDialog.setVisible(true);
            
            if(eventDialog.addToExistingMethod())
            {
                /*org.openide.src.MethodElement methodElm = (org.openide.src.MethodElement) eventDialog.getExistingMethodName();
                List parameterNames = JavaSourceUtil.getParameterName(methodElm,"ActionRequest","javax.portlet.ActionRequest");
                StringWriter writer = new StringWriter();
                HashMap map = new HashMap();
                map.put("PORTLET_EVENT_NAME",eventName);
                map.put("NEW_METHOD","false");
                if(parameterNames.size() > 0)
                    map.put("REQUEST",parameterNames.get(0));
                JavaSourceUtil.mergeTemplate(JavaSourceUtil.IPC_GENERATE_EVENT_TEMPLATE,writer,map);
                JavaSourceUtil.addToMethodBody(methodElm,writer.toString());
                String[] imports = {"com.sun.portal.portletappengine.ipc.PortletEvent",  
                                            "com.sun.portal.portletappengine.ipc.PortletEventBroker"};
                JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);*/
                ExecutableElement methodElm = (ExecutableElement)eventDialog.getExistingMethodName();
                JavaCodeUtil.addPublishEventCode(fileObjs[0], portletClassName, methodElm, null,eventName);
            }else{
                String newMethodName = eventDialog.getSuggestedMethodName();
                for(int i=0;i<publishMethods.size();i++)
                {
                    ExecutableElement methodElm = (ExecutableElement)publishMethods.get(i);
                    if(methodElm.getSimpleName().toString().equals(newMethodName))
                    {
                        NotifyDescriptor.Message msg = new NotifyDescriptor.Message("A method with same name "+newMethodName+" already exists.");
                        DialogDisplayer.getDefault().notify(msg);
                        return false;
                    }
                }
                
               
                JavaCodeUtil.addPublishEventCode(fileObjs[0], portletClassName, null , newMethodName,eventName);
             /*   StringWriter writer = new StringWriter();
                HashMap map = new HashMap();
                map.put("PORTLET_EVENT_NAME",eventName);
                map.put("NEW_METHOD","true");
                map.put("REQUEST","actionRequest");
                JavaSourceUtil.mergeTemplate(JavaSourceUtil.IPC_GENERATE_EVENT_TEMPLATE,writer,map);
                String body = writer.toString();
                
                ArrayList parameters = new ArrayList();
                parameters.add("ActionRequest actionRequest");
                parameters.add("ActionResponse actionResponse");
                parameters.add("String eventData");
                String[] imports = {"com.sun.portal.portletappengine.ipc.PortletEvent",  
                                            "com.sun.portal.portletappengine.ipc.PortletEventBroker"};
                JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);
                JavaSourceUtil.addNewMethod(portletClassName,newMethodName,Modifier.PRIVATE,org.openide.src.Type.VOID,parameters,body,fileObjs[0]);*/
              

            }

             DataObject dob = DataObject.find(fileObjs[0]);
             OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
             if (oc != null) { 
                 oc.open();
              }
       
            
            return true;
        } catch (IOException ex) {
            StatusDisplayer.getDefault().setStatusText("Could not add code to publish event");
            logger.fine("Could not add code to publish event");
        }
        return false;
    }

public boolean generateProcessEventMethod(String portlet, EventObject eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

public boolean isEventingSupported() {
        return helper.isEventingSupported();
    }

public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
