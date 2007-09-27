
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
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
        portletGenerator.generatePortlet("createTroubleTicketByValueRequest");
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

