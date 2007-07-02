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

import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        GenerateEventDialog eventDialog = new GenerateEventDialog(WindowManager.getDefault().getMainWindow(),portlet,eventName);
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
        //    for(int i=0;i<fileObjs.length;i++)
            {
                 methods = JavaSourceUtil.getMethodNames(portletClassName, fileObjs[0]);
                // if(methods != null)
              //       break;
                
            }
            logger.fine("Portlet Class:::::::::::: " + portletClassName);
            
            eventDialog.setMethodNames(methods);
            eventDialog.setVisible(true);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean generateProcessEventMethod(String portlet, String eventName) throws PortletEventException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    
}
