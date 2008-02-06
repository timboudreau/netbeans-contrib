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
package org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileChangeListener;
//import org.netbeans.modules.xml.multiview.DesignMultiViewDesc;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
//import org.netbeans.api.xml.cookies.CheckXMLCookie;
//import org.netbeans.api.xml.cookies.ValidateXMLCookie;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.PortletEventingHandler;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.impl.PortletEventingHandlerImpl;
//import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.PortletXmlHelper;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class PortletXMLDataObject extends XMLDataObject//XmlMultiViewDataObject
        implements Lookup.Provider {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    //private ModelSynchronizer modelSynchronizer;
    private PortletApp portletApp;
   // private SunPortletXmlHandler sunPortletXmlHandler;
    private PortletEventingHandler portletEventingHandler;
    private PortletXmlHelper portletXmlHelper;
    private FileObject portletXmlFobj;
    private static final int TYPE_TOOLBAR = 0;
    private String applicationName;
    
    public PortletXMLDataObject(FileObject pf, PortletXMLDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
      //  CookieSet cookies = getCookieSet();
      ///  cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        this.portletXmlFobj = pf;
        /*CookieSet cookies = getCookieSet();
        org.xml.sax.InputSource in = DataObjectAdapters.inputSource(this);
        CheckXMLCookie checkCookie = new CheckXMLSupport(in);
        cookies.add(checkCookie)
        
        ValidateXMLCookie validateCookie = new ValidateXMLSupport(in);
        cookies.add(validateCookie);*/
        
        Project p = FileOwnerQuery.getOwner(this.getPrimaryFile());
        if(p != null)
        {
            
        }
        try{
            this.applicationName = ProjectUtils.getInformation(FileOwnerQuery.getOwner(this.getPrimaryFile())).getName();
        }catch(Exception e){
            applicationName = "";
        }
        
        try {
            parseDocument();
        } catch (IOException ex) {
            logger.log(Level.INFO,"Parse Error",ex);
        }catch(Exception e){
             logger.log(Level.INFO,"Parse Error",e);
        }catch(Error e){
            logger.log(Level.INFO,"",e);
        }
        
        //Initialize portlet eventing handler
        try{
            portletEventingHandler = new PortletEventingHandlerImpl(pf.getParent(),this);
            //parseSunPortletXml(pf.getParent());
        }catch(Throwable e){
            e.printStackTrace();
           /// portletAppExt = null;
        }
        
        portletXmlHelper = new PortletXmlHelper(this);
        
        
       // cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }
    
    protected Node createNodeDelegate() {
        return new PortletXMLDataNode(this);//, getLookup());
    }
    
    public void parseDocument() throws IOException {
        if (portletApp==null) {
            portletApp = getPortletApp();
        } else {
            //java.io.InputStream is = getEditorSupport().getInputStream();
            PortletApp newPortletApp = null;
            try {
                //TODO remove this line
                portletApp = PortletXMLFactory.createGraph(FileUtil.toFile(portletXmlFobj));
               // newPortletApp = PortletXMLFactory.createGraph(FileUtil.toFile(portletXmlFobj));
               ///newPortletApp = PortletXMLFactory.createGraph(FileUtil.toFile(portletXmlFobj));
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Parse Error",ex);
                NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(PortletXMLDataObject.class, 
                                            "INVAILD_PORTLET_XML"),NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } 
            if (newPortletApp!=null) {
                //portletApp.merge(newPortletApp, org.netbeans.modules.schema2beans.BaseBean.MERGE_UPDATE);
               //// PortletXMLFactory.merge(portletApp,newPortletApp, org.netbeans.modules.schema2beans.BaseBean.MERGE_UPDATE);
                
            }
        }
    }
    
    public PortletEventingHandler getPortletEventingHandler()
    {
        return portletEventingHandler;
    }
    
    public PortletXmlHelper getPortletXmlHelper()
    {
        return portletXmlHelper;
    }
    
    public String getApplicationName()
    {
        return applicationName;
    }
   
    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public PortletApp getPortletApp() throws IOException {
        if (portletApp==null) {
            if(FileUtil.toFile(portletXmlFobj).exists()){
                try{
                    portletApp = PortletXMLFactory.createGraph(FileUtil.toFile(portletXmlFobj));
                }catch(Exception e){
                    logger.log(Level.SEVERE,"Error in creatingGraph for portlet.xml",e);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(PortletXMLDataObject.class, 
                                            "INVAILD_PORTLET_XML"),NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
            }
        }
        return portletApp;
    }
    
    public void refreshMe() {
        try{
            parseDocument();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error",e);
            //do nothing/
        }
        try{
            portletEventingHandler.refresh();
        }catch(Exception e){
            //do nothing
        }
        //  if(FileUtil.toFile(getPrimaryFile()).exists())
        //          portletApp = PortletApp.createGraph(FileUtil.toFile(getPrimaryFile()));
    }
    
    public Lookup getLookup() {
        //return null;        
        return getCookieSet().getLookup();
    }
 /*   
    protected DesignMultiViewDesc[] getMultiViewDesc() {
        return new DesignMultiViewDesc[]{new DesignView(this,TYPE_TOOLBAR)};
    }*/
    
    protected String getPrefixMark() {
        return null;
    }
    
    public void addFileChangeListener(FileChangeListener fileChangeListener)
    {
        portletXmlFobj.addFileChangeListener(fileChangeListener);
    }
    
    public void removeFileChangeListener(FileChangeListener fcl)
    {
        portletXmlFobj.removeFileChangeListener(fcl);
    }

    public String getPortletSpecVersion()
    {
        if(portletApp == null) return "";
       // String version = ((BaseBean)portletApp).getAttributeValue("version");
        String version = portletApp.getVersion();
        if(version.equals(PortletApp.VERSION_2_0))
            return PortletApp.VERSION_2_0;
        else
            return PortletApp.VERSION_1_0;

    }
    
/*
    private static class DesignView extends DesignMultiViewDesc {
        private int type;
        DesignView(PortletXMLDataObject dObj, int type) {
            //super(dObj, "Design"+String.valueOf(type));
            super(dObj, "Design");
            this.type=type;
        }
        
        public org.netbeans.core.spi.multiview.MultiViewElement createElement() {
            PortletXMLDataObject dObj = (PortletXMLDataObject)getDataObject();
            //            if (type==TYPE_TOOLBAR) return new BookToolBarMVElement(dObj);
            //            else return new BookTreePanelMVElement(dObj);
            return new PortletAppToolBarMVElement(dObj);
        }
        
        public java.awt.Image getIcon() {
            return org.openide.util.Utilities.loadImage("org/netbeans/modules/portalpack/portlets/genericportlets/resources/portlet-xml.gif"); //NOI18N
        }
        
        public String preferredID() {
            return "portlet_xml_multiview_"+String.valueOf(type);
        }
    }
  */  

    
    
}
