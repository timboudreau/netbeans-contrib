
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
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.UIComponentType;
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.GDEPreferencesController;

import com.sun.tthub.gde.portletcontrol.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Hareesh Ravindran
 */
public class PortletJspGenerator {
    
    private TTValueDisplayInfo displayInfo;
    private GDEPreferences preferences = null;
    
    /** Creates a new instance of CreateTTJspGenerator */
    public PortletJspGenerator() throws GDEException {
        GDEPreferencesController controller =
                GDEAppContext.getInstance().getGdePrefsController();
        preferences = controller.retrievePreferences();
    }
    
    public void setTTValueDisplayInfo(TTValueDisplayInfo displayInfo) {
        this.displayInfo = displayInfo;
        
    }
    
    private StringBuffer generateDynamicContent(Map fieldInfoMap,String operationName,String curEditPath) throws GDEException {
        
        Collection coll =fieldInfoMap.entrySet();
        
        // This is the buffer to which the dynamically generated contents are
        // placed.
        StringBuffer jspBuffer = new StringBuffer();
        int i=0;
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            //String str = getTagString((FieldInfo) entry.getValue());
            FieldInfo fieldInfo= (FieldInfo)entry.getValue();
            
            jspBuffer.append("<tr><td class=\"labelStyle\">\n");
            
            jspBuffer.append(fieldInfo.getFieldDisplayInfo().getFieldDisplayName());
            jspBuffer.append(" :</td><td colspan=\"2\">\n");
            
            String str = getTagString(fieldInfo,operationName,curEditPath);
            
            jspBuffer.append(str);
            jspBuffer.append("</td><tr>\n");
            

        }
        return jspBuffer;
    }
    
    public void generateJspFile(String operationName) throws GDEException {
        
        
        String curEditPath="/";
        Map fieldInfoMap=displayInfo.getExtFieldInfoMap();
        
        generateJSPFilesForOperation(fieldInfoMap,operationName,curEditPath);
    }
    private void generateJSPFilesForOperation(Map fieldInfoMap, String operationName,String curEditPath) throws GDEException {
        
        //Loop through the fieldInfoMap to create PortletView.jsp for each complex field.
        
        Collection coll = fieldInfoMap.entrySet();
        
        //Generete JSP File for FieldMap
        generateJSPFileForEditPath(fieldInfoMap,operationName,curEditPath);
        
        // Loop for complex member and generate JSP file.
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            FieldInfo info =
                    (FieldInfo) entry.getValue();
            UIComponentType uiComponentType= info.getFieldDisplayInfo().getUIComponentType();
            
            if (uiComponentType.equals(uiComponentType.CONTROL_COMPLEX_ENTRY)){
                //to add control for complex object
                String fieldName=info.getFieldMetaData().getFieldName();
                ComplexEntryFieldDisplayInfo fieldInfo= (ComplexEntryFieldDisplayInfo)info.getFieldDisplayInfo();
                Map complxFieldInfoMap=fieldInfo.getFieldInfoMap();
                String editPath=curEditPath;
                if (editPath.equals("/")){
                    editPath=editPath+ fieldName;
                }else{
                    editPath=editPath+"/"+fieldName;
                }
                System.out.println("curEditPath---"+curEditPath);
                
                System.out.println("editPath---"+editPath);
                generateJSPFilesForOperation(complxFieldInfoMap, operationName,editPath);
                
            }
        }
    }
    
    private void generateJSPFileForEditPath(Map fieldInfoMap, String operationName, String curEditPath) throws GDEException {
        // Read the template jsp file from the folder
        // <GDE Folder>/portlet-templates. Read the file line by line till
        // the comment line containing 'INSERT THE GDE WIZARD GENERATED CODE HERE'.
        // At this point,  is method will insert the controls based on the
        // TTValueDisplayInfo object passed.
        
        System.out.println("-----generateJSPFileForEditPath-------");
        System.out.println("-OperatioName-"+operationName);
        System.out.println("-curEditPath-"+curEditPath);
        
        BufferedReader reader = null;
        PrintWriter writer = null;
        StringBuffer fileName=new StringBuffer();
        
        try {
            
            reader = new BufferedReader(new FileReader(
                    preferences.getPortletTemplatesFolder() +
                    PortletConstants.PORTLET_JSP_TEMPLATE));
            
            // JSP file in /operationname/editpath/PortletView.jsp
            fileName.append(preferences.getGeneratedFilesFolder()).append("/jsp");
            if (curEditPath.equals("/"))
                fileName=fileName.append("/").append(operationName);
            else
                fileName=fileName.append("/").append(operationName).append(curEditPath);
            
            //Create parent/child directories
            File filedir=new File(fileName.toString());
            filedir.mkdirs();
            
            fileName.append(PortletConstants.PORTLET_JSP_FILENAME);
            writer = new PrintWriter(fileName.toString());
            System.out.println("-fileName-"+fileName);    
        } catch(FileNotFoundException ex) {
            throw new GDEException("The template jsp file is not " +
                    "found in the GDE folder.", ex);
        } catch(IOException ex) {
            throw new GDEException("Unable to create the file " +
                    "/" + fileName.toString() + " in the GDEFolder for writing.");
        }
        StringBuffer jspBuffer=generateDynamicContent(fieldInfoMap,operationName,curEditPath);
        // Read the contents from the template file and print it into the
        // new generated file.
        try {
            while(true) {
                String line = reader.readLine();
                if(line == null) // if the end of stream is reached.
                    break;
                
                // Define FieldName display as header
                line=line.replaceFirst(PortletConstants.JSP_FIELDNAME_STRING,curEditPath);
                
                // Define operationname as hidden field
                line=line.replaceFirst(PortletConstants.JSP_OPERATIONNAME_STRING,operationName);
                
                
                // Define currentEditPath as hidden field
                line=line.replaceFirst(PortletConstants.JSP_CUREDITPATH_STRING,curEditPath);
                
                // print the line in the template file, as it is.
                
                writer.println(line);
                if(line.contains("INSERT THE GDE WIZARD GENERATED CODE HERE"))
                    writer.println(jspBuffer.toString());
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
    private String getScriptString(FieldInfo fieldInfo) throws GDEException{
        FieldDisplayInfo dispInfo = fieldInfo.getFieldDisplayInfo();
        UIComponentType compType = dispInfo.getUIComponentType();
        
        if(UIComponentType.CONTROL_BOOLEAN_CHECKBOX == compType) {
            return new BooleanCheckBoxControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_BOOLEAN_COMBO == compType) {
            return new BooleanComboBoxControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_BOOLEAN_RADIOBUTTONS == compType) {
            return new BooleanRadioButtonControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_CHECKBOX_SET == compType) {
            return new CheckBoxSetControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_COMBOBOX == compType) {
            return new ComboBoxControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_MULTISELECT == compType) {
            return new MultiSelectListControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_RADIOBUTTON_SET == compType) {
            return new RadioButtonSetControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_SINGLESELECT == compType) {
            return new SingleSelectListControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_TEXTBOX == compType) {
            return new TextBoxControl(fieldInfo).getFieldInfoJspString();
        }
        
        if(UIComponentType.CONTROL_COMPLEX_ENTRY == compType) {
            return "<!-- tthub:ComplexEntryControlTag not available-->";
        }
        
        throw new GDEException("Unknown Control Type encountered. " +
                "Failed to generate the control in CreateTTPortletGenerator");
        
    }
    private String getTagString(FieldInfo fieldInfo,String operationName,String curEditPath) throws GDEException {
        
        FieldDisplayInfo dispInfo = fieldInfo.getFieldDisplayInfo();
        UIComponentType compType = dispInfo.getUIComponentType();
        String fieldName = fieldInfo.getFieldMetaData().getFieldName();
        
        if(UIComponentType.CONTROL_BOOLEAN_CHECKBOX == compType) {
            return "<tthub:BoolCheckBoxTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_BOOLEAN_COMBO == compType) {
            return "<tthub:BoolComboBoxTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_BOOLEAN_RADIOBUTTONS == compType) {
            return "<tthub:BoolRadioButtonSetTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_CHECKBOX_SET == compType) {
            return "<tthub:CheckBoxSetTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_COMBOBOX == compType) {
            return "<tthub:ComboBoxTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_MULTISELECT == compType) {
            return "<tthub:MultiSelectListTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_RADIOBUTTON_SET == compType) {
            return "<tthub:RadioButtonSetTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_SINGLESELECT == compType) {
            return "<tthub:SingleSelectListTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_TEXTBOX == compType) {
            return "<tthub:TextBoxTag fieldName=\"" + fieldName + "\"/>";
        }
        
        if(UIComponentType.CONTROL_COMPLEX_ENTRY == compType) {
            StringBuffer buffer=new StringBuffer();
            buffer.append("<tthub:ComplexEntryControlTag ");
            buffer.append("fieldName=\"").append(fieldName).append("\" ");
            buffer.append("usecaseId=\"").append(operationName).append("\" ");
            buffer.append("curEditPath=\"").append(curEditPath).append("\"/>");
            
            return  buffer.toString();
        }
        
        throw new GDEException("Unknown Control Type encountered. " +
                "Failed to generate the control in CreateTTPortletGenerator");
    }
}
