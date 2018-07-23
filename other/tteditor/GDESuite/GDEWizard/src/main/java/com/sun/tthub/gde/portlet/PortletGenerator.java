
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

package com.sun.tthub.gde.portlet;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.GDERuntimeException;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;

import com.sun.tthub.gde.util.FileUtilities;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.GDEPreferencesController;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Hareesh Ravindran
 *
 */
public class PortletGenerator {
    
    private TTValueDisplayInfo displayInfo;
    private GDEPreferences preferences;
    
    /** Creates a new instance of CreateTTPortletGenerator */
    public PortletGenerator(
            TTValueDisplayInfo displayInfo) throws GDEException {
        this.displayInfo = displayInfo;
        GDEPreferencesController controller =
                GDEAppContext.getInstance().getGdePrefsController();
        preferences = controller.retrievePreferences();
        createPortletXMLFile();
    }
    
    
    public void generatePortlet(String operationName) throws GDEException {
        // Create the temporary folder into which the files are to be generated.
        
        String genfiledir=preferences.getGeneratedFilesFolder();
        createGenFilesFolder(genfiledir+"/jsp");
        createGenFilesFolder(genfiledir+"/java");
        
        // Generate the jsp file for the specified Operation, in the gen-files folder.
        // This file will be bundled with the war file.
        PortletJspGenerator jspGenerator = new PortletJspGenerator();
        jspGenerator.setTTValueDisplayInfo(displayInfo);
        jspGenerator.generateJspFile(operationName);
        
        // Generate the request processor java file. This file will contain
        // code to extract the values from the request parameters, conver to
        // appropriate data types and validate these values based on the
        // validation rules specified.
        
        PortletReqInterpretorGenerator reqInterpretorGen =
                new PortletReqInterpretorGenerator();
        reqInterpretorGen.setTTValueDisplayInfo(displayInfo);
        reqInterpretorGen.generateReqInterpretor(operationName);
        
        // Generate the operation portlet file.
        generatePortletConfigFile(operationName);
        
        // Add operation portlet to portlet xml file.
        addPortletConfigtoPortletXML(operationName);
    }
    
    private void createGenFilesFolder(String filedir) throws GDEException {
        File filefolder = new File(filedir);
        try {
            FileUtilities.deleteDir(filefolder);
            boolean created=filefolder.mkdirs();   // Create the sub directory named gen-files
            System.out.println("createGenFilesFolder()-"+filedir+"-"+created);
        } catch(SecurityException ex) {
            throw new GDEException("Failed to create the directory " +filedir+
                    "in the GDE folder.", ex);
        }
        
    }
    private void  generatePortletConfigFile(String operationName) throws GDEException {
        // Read the template portlet file from the folder
        // <GDE Folder>/portlet-templates. Read the file line by line till
        // the comment line containing 'INSERT THE GDE WIZARD GENERATED CODE HERE'.
        // At this point,  is method will insert the controls based on the
        // TTValueDisplayInfo object passed.
        BufferedReader reader = null;
        PrintWriter writer = null;
        
        try {
            reader = new BufferedReader(new FileReader(
                    preferences.getPortletTemplatesFolder() +
                    PortletConstants.PORTLET_CONFIG_TEMPLATE));
            writer = new PrintWriter(
                    new FileWriter(preferences.getGeneratedFilesFolder() +
                    "/java/" + operationName + ".portlet"));
        } catch(FileNotFoundException ex) {
            throw new GDEException("The template portlet file is not " +
                    "found in the GDE folder.", ex);
        } catch(IOException ex) {
            throw new GDEException("Unable to create the file " +
                    "/" + operationName + ".portlet in the GDEFolder for writing.");
        }
        
        // Read the contents from the template file and print it into the
        // new generated file.
        try {
            while(true) {
                String line = reader.readLine();
                if(line == null) // if the end of stream is reached.
                    break;
                // Define package name
                line= line.replaceFirst(PortletConstants.PACKAGE_STRING,PortletConstants.PACKAGE_NAME);
                
                // Define Classname
                line=line.replaceFirst(PortletConstants.PORTLET_NAME_STRING,operationName);
                writer.println(line);
                
            }
        } catch(IOException ex) {
            throw new GDEException("Failed to close the read/write stream.", ex);
        } finally {
            try {
                reader.close();
                writer.close();
            } catch(IOException ex) {
                // log the error and return.
            }
        }
    }
    
    private void  addPortletConfigtoPortletXML(String operationName) throws GDEException {
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document opPortletDoc = docBuilder.parse(preferences.getGeneratedFilesFolder() +
                    "/java/" + operationName + ".portlet");
            Element  opPortletRoot = opPortletDoc.getDocumentElement();
            //-------------------------------

            if (!opPortletRoot.getNodeName().equals("portlet"))
                throw new GDEException("invalid operation portlet");
            
            Node opNode = null;
            Document portletXMLDoc = docBuilder.parse(preferences.getPortletConfigFilesFolder()+"/"+PortletConstants.PORTLET_XML);
            Element  portletXMLRoot = portletXMLDoc.getDocumentElement();
            
            opNode = portletXMLDoc.importNode(opPortletRoot, true);
            portletXMLRoot.appendChild(opNode);
            
            //-------------------------------
            // CreateUsingDom is defined in another example, search for it.
            // We are using 2 static methods of it
            // output to file
            TransformerFactory tfFac = TransformerFactory.newInstance();
            // use null trandformation
            Transformer tf = tfFac.newTransformer();
            tf.transform(new DOMSource(portletXMLDoc), new StreamResult(new FileWriter(preferences.getPortletConfigFilesFolder()+"/"+PortletConstants.PORTLET_XML)));
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch ( TransformerConfigurationException tce){
            tce.printStackTrace();
        } catch ( TransformerException te){
            te.printStackTrace();
        }
    }
    
    private void createPortletXMLFile()throws GDEException {
        try{
            FileUtilities.copy(new File(preferences.getPortletTemplatesFolder()+PortletConstants.PORTLET_XML_TEMPLATE),new File(preferences.getPortletConfigFilesFolder()+"/"+PortletConstants.PORTLET_XML));
        }catch( IOException ioe){
            throw new GDEException("Unable to create portlet.xml",ioe);
        }
    }

}
