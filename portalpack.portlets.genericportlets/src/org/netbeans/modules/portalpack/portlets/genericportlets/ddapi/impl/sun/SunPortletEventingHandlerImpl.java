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

package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun;

import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun.ui.ConsumeEventDialog;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.util.PortletXMLUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.JavaSourceUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.util.PortletXMLUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventingHandler;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun.ui.GenerateEventDialog;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satyaranjan
 */
public class SunPortletEventingHandlerImpl implements PortletEventingHandler{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private SunPortletXmlHandler sunPortletXmlHandler;
    private PortletXMLDataObject dbObj;
    private Project project;
   
    /** Creates a new instance of SunPortletEventingHandlerImpl */
    public SunPortletEventingHandlerImpl(FileObject webInfDir,PortletXMLDataObject dbObj) {
       try{
            initSunPortletXmlHandler(webInfDir);
            project = FileOwnerQuery.getOwner(webInfDir);
       }catch(Error e){
           e.printStackTrace();
           //do nothing
       }
       this.dbObj = dbObj;
    }
    
     public void  initSunPortletXmlHandler(FileObject webInf) {
        File webInfFile = FileUtil.toFile(webInf);
        File sunPortlet = new File(webInfFile,"sun-portlet.xml");
            try{
                //portletAppExt = PortletAppExtension.createGraph(sunPortlet);
                sunPortletXmlHandler = new SunPortletXmlHandler(sunPortlet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    
    }
    
    public FileObject[] findJavaFileObj(String className)
    {
        className = className.replace(".", File.separator);
        Sources sources = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        List fileObjs = new ArrayList();
        for(int i=0;i<groups.length;i++)
        {
            File f = new File(FileUtil.toFile(groups[i].getRootFolder()),className+".java");
            if(!f.exists()){
                f = new File(FileUtil.toFile(groups[i].getRootFolder()),className+".JAVA");
                if(!f.exists())
                    continue;
                else
                    fileObjs.add(FileUtil.toFileObject(f));
            }else
                fileObjs.add(FileUtil.toFileObject(f));
        }
        return (FileObject [])fileObjs.toArray(new FileObject[0]);
    }
    public void refresh()
    {
        try{
            sunPortletXmlHandler.reload();
        }catch(Exception e){}
    }
    
    public Object getSunPortletXmlObj()
    {
        return sunPortletXmlHandler.getDocument();
    }
    
    public String[] getPublishEvents(String portletName)
    {
        return sunPortletXmlHandler.getEvents(portletName);
    }
    
    public String[] getProcessEvents(String portletName)
    {
        return sunPortletXmlHandler.getConsumeEvents(portletName);
    }
    
    public boolean addProcessEvent(String targetPortlet,String event,Map properties)
    {
        return sunPortletXmlHandler.addConsumeEvent(targetPortlet, event);
    }
    
    public boolean deleteProcessEvent(String portlet,String evt)
    {
        return sunPortletXmlHandler.deleteConsumeEvent(portlet,evt);
    }
    
    public boolean isProcessEventExists(String portlet,String evt)
    {
        return sunPortletXmlHandler.isConsumesEventExists(portlet, evt);
    }
    
    public boolean isPublishEventExists(String portlet,String evt)
    {
        return sunPortletXmlHandler.isGeneratesEventExists(portlet, evt);
    }
    
    public boolean addPublishEvent(String portlet,String evt,Map properties)
    {
        return sunPortletXmlHandler.addGeneratesEvent(portlet, evt);
    }
    
    public boolean renamePublishEvent(String portlet,String oldEvent,String newEvent,Map properties)
    {
        return sunPortletXmlHandler.renameGeneratesEvent(portlet,oldEvent,newEvent);
    }
    
    public boolean renameProcessEvent(String portlet,String oldEvent,String newEvent,Map properties)
    {
        return sunPortletXmlHandler.renameConsumesEvent(portlet,oldEvent,newEvent);
    }

    public boolean deletePublishEvent(String portlet, String evt) {
        return sunPortletXmlHandler.deleteGeneratesEvent(portlet,evt);
    }

    public String getPublishEventMethodBody(String portlet, String eventName,
                                            boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProcessEventMethodBody(String portlet, String eventName,
                                            boolean newMethod) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generatePublishEventMethod(String portlet, String eventName) throws PortletEventException {
       /* GenerateEventDialog eventDialog = new GenerateEventDialog(WindowManager.getDefault().getMainWindow(),portlet,eventName);
        try{
            String portletClassName = null;
            portletClassName = PortletXMLUtil.getPortletClassName(dbObj.getPortletApp(), portlet);
            String[] methods = null;
            FileObject[] fileObjs = findJavaFileObj(portletClassName);
            if(fileObjs.length == 0)
            {
                logger.fine("No Java class file found for ::: "+portletClassName);
                return false;
            }
       
             methods = JavaSourceUtil.getMethodNames(portletClassName, fileObjs[0]);
              
          
            logger.fine("Portlet Class:::::::::::: " + portletClassName);
            org.openide.src.MethodElement[] methodElms = JavaSourceUtil.getMethods(portletClassName,fileObjs[0]);
            List allowableMethods = new ArrayList();
            for(int i=0;i<methodElms.length;i++)
            {
                List parameterNames = JavaSourceUtil.getParameterName(methodElms[i],"ActionRequest","javax.portlet.ActionRequest");
                if(parameterNames.size() > 0)
                    allowableMethods.add(methodElms[i]);
            }
            eventDialog.setMethods((org.openide.src.MethodElement [])allowableMethods.toArray(new org.openide.src.MethodElement[0]));
            eventDialog.setSuggestedMethodName(resolveNewGenerateEventMethodName(eventName,methods));
            eventDialog.setVisible(true);

            if(eventDialog.isCancelled()) return false;

            //create body

            if(eventDialog.addToExistingMethod())
            {
                org.openide.src.MethodElement methodElm = (org.openide.src.MethodElement) eventDialog.getExistingMethodName();
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
                JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);
            }else{
                String newMethodName = eventDialog.getSuggestedMethodName();
                StringWriter writer = new StringWriter();
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
                JavaSourceUtil.addNewMethod(portletClassName,newMethodName,Modifier.PRIVATE,org.openide.src.Type.VOID,parameters,body,fileObjs[0]);
              

            }

             DataObject dob = DataObject.find(fileObjs[0]);
             OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
             if (oc != null) { 
                 oc.open();
              }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    public boolean generateProcessEventMethod(String portlet, String eventName) throws PortletEventException {
        /*ConsumeEventDialog eventDialog = new ConsumeEventDialog(WindowManager.getDefault().getMainWindow());
        try{
            eventDialog.setEventName(eventName);
            eventDialog.setPortletName(portlet);
            
            String portletClassName = null;
            portletClassName = PortletXMLUtil.getPortletClassName(dbObj.getPortletApp(), portlet);
            String[] methods = null;
            FileObject[] fileObjs = findJavaFileObj(portletClassName);
            if(fileObjs.length == 0)
            {
                logger.fine("No Java class file found for ::: "+portletClassName);
                return false;
            }
            eventDialog.setJavaSourceName(FileUtil.toFile(fileObjs[0]).getAbsolutePath());
            methods = JavaSourceUtil.getMethodNames(portletClassName, fileObjs[0]);
              
            eventDialog.setSuggestedMethodName(resolveNewConsumeEventMethodName(eventName,methods));
            eventDialog.setVisible(true);

            if(eventDialog.isCancelled()) return false;

            ArrayList parameters = new ArrayList();
            parameters.add("EventRequest");
            parameters.add("EventResponse");
            //create body
            org.openide.src.MethodElement handleEventMethod = JavaSourceUtil.getMethod(portletClassName,"handleEvent",org.openide.src.Type.VOID,parameters,fileObjs[0]);

            if(handleEventMethod == null)
            {
                //Create handleEvent method ... It's not there
                
                ArrayList handleparameters = new ArrayList();
                handleparameters.add("EventRequest eventRequest");
                handleparameters.add("EventResponse eventResponse");
                String body = "";
                String[] imports = {"com.sun.portal.portletappengine.ipc.EventRequest",  
                                            "com.sun.portal.portletappengine.ipc.EventResponse","com.sun.portal.portletappengine.ipc.PortletEventListener"};
                JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);
                JavaSourceUtil.addNewMethod(portletClassName,"handleEvent",Modifier.PUBLIC,org.openide.src.Type.VOID,handleparameters,body,fileObjs[0]);
                handleEventMethod = JavaSourceUtil.getMethod(portletClassName,"handleEvent",org.openide.src.Type.VOID,parameters,fileObjs[0]);
            }

            JavaSourceUtil.addInterface("com.sun.portal.portletappengine.ipc.PortletEventListener","PortletEventListener",portletClassName,fileObjs[0]);
            //create new ConsumeEvent method
            String newConsumeMethodName = eventDialog.getSuggestedMethodName();
            ArrayList consumeparameters = new ArrayList();
            consumeparameters.add("EventRequest eventRequest");
            consumeparameters.add("EventResponse eventResponse");
            HashMap consumemap = new HashMap();
            consumemap.put("CONSUME_EVENT_NAME",eventName);
            StringWriter consumeEventWriter = new StringWriter();

            String[] imports = {"javax.portlet.PortletSession"};
            JavaSourceUtil.mergeTemplate(JavaSourceUtil.IPC_CONSUME_EVENT_METHOD_TEMPLATE,consumeEventWriter,consumemap);

            JavaSourceUtil.addNewMethod(portletClassName,newConsumeMethodName,Modifier.PRIVATE,org.openide.src.Type.VOID,consumeparameters,consumeEventWriter.toString(),fileObjs[0]);
            JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);   
            if(handleEventMethod != null)
            {
                
                List eventRequestParameterNames = JavaSourceUtil.getParameterName(handleEventMethod,"EventRequest","com.sun.portal.portletappengine.ipc.EventRequest");
                List eventResponseParameterNames = JavaSourceUtil.getParameterName(handleEventMethod,"EventResponse","com.sun.portal.portletappengine.ipc.EventResponse");
                StringWriter writer = new StringWriter();
                HashMap map = new HashMap();
                map.put("CONSUME_EVENT_NAME",eventName);
                map.put("METHOD_NAME",newConsumeMethodName);
                if(eventRequestParameterNames.size() > 0)
                    map.put("EVENT_REQUEST_PARAMETER",eventRequestParameterNames.get(0));
                if(eventResponseParameterNames.size() > 0)
                    map.put("EVENT_RESPONSE_PARAMETER",eventResponseParameterNames.get(0));

                JavaSourceUtil.mergeTemplate(JavaSourceUtil.IPC_CONSUME_EVENT_TEMPLATE,writer,map);
                JavaSourceUtil.addToMethodBody(handleEventMethod,writer.toString());
             //   String[] imports = {"com.sun.portal.portletappengine.ipc.PortletEvent",  
              //                              "com.sun.portal.portletappengine.ipc.PortletEventBroker"};
               // JavaSourceUtil.addImports(imports,portletClassName,fileObjs[0]);
            }

             DataObject dob = DataObject.find(fileObjs[0]);
             OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
             if (oc != null) { 
                 oc.open();
              }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }*/
        return true;
    }
    
    private String resolveNewGenerateEventMethodName(String eventName,String[] existingMethods)
    {
        String prefix = "generate"+eventName+"Event";
        
        List list = new ArrayList();
        for(int i=0;i<existingMethods.length;i++)
        {
            list.add(existingMethods[i]);
        }
        
        int i = 0;
        String methodName = prefix;
        while(list.contains(methodName))
        {
            methodName = prefix + "_" + i;
            i++;
        }
        return methodName;
    }

    private String resolveNewConsumeEventMethodName(String eventName,String[] existingMethods)
    {
        String prefix = "handleConsume"+eventName+"Event";
        
        List list = new ArrayList();
        for(int i=0;i<existingMethods.length;i++)
        {
            list.add(existingMethods[i]);
        }
        
        int i = 0;
        String methodName = prefix;
        while(list.contains(methodName))
        {
            methodName = prefix + "_" + i;
            i++;
        }
        return methodName;
    }
    
    public boolean isEventingSupported()
    {
        if(sunPortletXmlHandler.notExistsSunPortletXml())
        {
            NotifyDescriptor nd = new NotifyDescriptor.Message("No proper Portal Server/Portlet Container runtime is selected for this portlet application\n or sun-portlet.xml does not exist",NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);     
            return false;
        }
        return true;
    }

    
}
