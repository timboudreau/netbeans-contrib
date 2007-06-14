
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.gde.logic;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gde.util.NetbeansUtilities;
import com.sun.tthub.gde.portlet.PortletGenerator;
import com.sun.tthub.gde.portlet.PortletConstants;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfoPersistenceDelegate;
import com.sun.tthub.gdelib.fields.UIComponentType;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.beans.XMLEncoder;
/**
 *
 * @author Hareesh Ravindran
 */
public class ExtTTValueInfoProcessorImpl
        implements ExtTTValueInfoProcessor {
    
    /** Creates a new instance of ExtTTValueInfoProcessorImpl */
    public ExtTTValueInfoProcessorImpl() {}
    
    /**
     * This function uses the TTValueDisplayInfo gathered by the GUI
     * and will generate the required war file. This function will first
     * generate the CreateTT portlet and then the GetTT portlet. For generating
     * these portlets it will use the basic CreateTT and GetTT portlet
     * templates. After generating these portlets, it will create the deployment
     * descriptor for the portlet application. Then, it will bundle all these
     * files into a war file. This method uses the GDEFolder to generate all the
     * files.
     */
    public void generateWarFile(TTValueDisplayInfo
            ttValueDisplayInfo) throws GDEException {
        
        generateConfigFiles(ttValueDisplayInfo);
        generatePortletPage(ttValueDisplayInfo);
        assembleToWarFile(ttValueDisplayInfo);
    }
    
    private void generatePortletPage(TTValueDisplayInfo
            ttValueDisplayInfo) throws GDEException {
        PortletGenerator portletGenerator =
                new PortletGenerator(ttValueDisplayInfo);
        portletGenerator.generatePortlet("creatett");
        //portletGenerator.generatePortlet("GetTroubleTicketByKeyResponse");
    }
    
    private void generateConfigFiles(TTValueDisplayInfo
            ttValueDisplayInfo) throws GDEException {
        
        //Test XMLEncoder
        String configFolder=getGDEPreferences().getPortletConfigFilesFolder();
        FileOutputStream fout=null;
        try{
            fout= new FileOutputStream(configFolder+"/"+"ttValueDisplayInfo.xml" );
            
            XMLEncoder encoder= new XMLEncoder(fout);
            encoder.setPersistenceDelegate(UIComponentType.class,new TTValueDisplayInfoPersistenceDelegate());
            encoder.writeObject(ttValueDisplayInfo);
            encoder.close();
            /* 
             //Test decoder
            java.beans.XMLDecoder d = new java.beans.XMLDecoder(
                    new java.io.BufferedInputStream(
                    new java.io.FileInputStream(configFolder+"/"+"ttValueDisplayInfo.xml" )));
            TTValueDisplayInfo result =(TTValueDisplayInfo) d.readObject();
            d.close();
            System.out.println("--------------test decoder----------\n"+result.toString());
            */
            
        }catch(Exception ex){
            
            System.out.println(ex.toString());
            throw new GDEException("Exception occured while executing the" +
                    " ant script to build the generated portlets.", ex);
            
        }finally{
            try{
                fout.close();
            }catch(Exception ex){
                
                System.out.println(ex.toString());
                throw new GDEException("Exception occured while executing the" +
                        " ant script to build the generated portlets.", ex);
            }
        }
        
    }
    
    private void assembleToWarFile(TTValueDisplayInfo
            ttValueDisplayInfo) throws GDEException {
        // invoke the ant script from the gde folder.
        String gdeFolder = getGDEPreferences().getGdeFolder();
        /*
        String antHome = getGDEPreferences().getAntHome();
        if(antHome == null || antHome.trim().equals("")) {
            throw new GDEException("Ant Home is not specified in the " +
                    " GDEPreferences.");
        }
        File file = new File(antHome);
        if(!file.exists() || !file.canRead()) {
            throw new GDEException("The specified ant home '" + antHome + "' " +
                    " is not a valid readable directory. Please check.");
        }
        */
        String antFile = gdeFolder + "/build-gen-files.xml";
        File antFileObj = new File(antFile);
        if(!antFileObj.exists() || !antFileObj.canRead()) {
            throw new GDEException("The ant build file " +
                    "build-gen-files.xml does not exist in the GDE Folder");
        }
        
        try {
            
            
            java.util.Properties properties= new java.util.Properties();
            properties.setProperty( "gde-folder", gdeFolder);
            properties.setProperty( "gde-package", PortletConstants.PACKAGE_FOLDER);
           
            int result = NetbeansUtilities.ExecuteAntTask(antFileObj, properties);
            if (result!=0)
                throw new GDEException("Unable to generate portlets");
            // AntUtil.executeSchemaAntScript(antHome,gdeFolder,"build-schemajar-files.xml",schemafile);
        } catch(Exception ex) {
            
            ex.printStackTrace();
            throw new GDEException("Exception occured while executing the" +
                    " ant script to build the generated portlets.", ex);
            
        }
        
        /*
        String antExe = antHome + "/bin/ant";
        String cmdLine = antExe + " -f " + antFile +
                            " -Dgde-folder=" + gdeFolder;
        try {
            Runtime.getRuntime().exec(cmdLine);
        } catch(IOException ex) {
             System.out.println(ex.toString());
            throw new GDEException("Exception occured while executing the" +
                    " ant script to build the generated portlets.", ex);
         
        }
         */
    }
    
    
    
    /**
     * This function uses the pdeploy command to deploy the war file to the
     * portal server. The deployment output will be shown on the console.
     */
    public void deployToPortalServer(TTValueDisplayInfo
            ttValueDisplayInfo) throws GDEException {
        // First, undeploy any existing war file with the same name, using the
        // pdeploy command (with the undeploy option)
        
        // Then, deploy the war file with the pdeploy command (using the
        // deploy option)
    }
    
    private GDEPreferences getGDEPreferences() throws GDEException {
        GDEPreferencesController controller =
                GDEAppContext.getInstance().getGdePrefsController();
        return controller.retrievePreferences();
    }
}

