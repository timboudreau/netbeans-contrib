
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
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;
import com.sun.tthub.gdelib.fields.UIComponentType;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.GDEPreferencesController;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This component will get the FeildInfo object for each field in the TTValue 
 * interface using the TTValueDisplayInfo object provided. From this, it will
 * get the type of component used for the field. Depending on the nature of the
 * component, it will generate the code to get the value from the control 
 * parameter in the request. Also, this component will generate the code to
 * convert the gathered value into appropriate data type as mentioned in the
 * FieldInfo object and will gather any errors during this conversion process.
 * It will also apply the validations mentioned in the FieldInfo object, if any
 * (currently, it does not have provisions to specify validation rules) and will
 * gather any validation errors during this process.
 *
 * It will also generate the code to create a new TTValueImplObject and invoke
 * the setter method for each field with the validated value obtained in the
 * above step.
 *
 * @author Hareesh Ravindran
 */

public class PortletReqInterpretorGenerator {
    
    private GDEPreferences preferences;
    private TTValueDisplayInfo displayInfo;
    
    /** Creates a new instance of CreateTTReqInterpretorGenerator */
    public PortletReqInterpretorGenerator() throws GDEException {
        GDEPreferencesController controller = 
                GDEAppContext.getInstance().getGdePrefsController();
        preferences = controller.retrievePreferences();            
    }
    
    public void setTTValueDisplayInfo(TTValueDisplayInfo displayInfo) {
        this.displayInfo = displayInfo;    
    }
        
    /**
     * This method will get the CreateTTReqInterpretor template and will insert
     * the dynamically generated code, based on the type of control for each
     * field. 
     *
     */
    public void generateReqInterpretor(String operationName) throws GDEException {
        
        // load the CreateTTReqInterpretor template.
        File readerFile = new File (preferences.getPortletTemplatesFolder() + 
                        PortletConstants.PORTLET_REQINTERPRETOR_TEMPLATE);
        File writerFile = new File(preferences.getGeneratedFilesFolder() +"/java/"+ 
                        operationName + ".java");
        
        BufferedReader templateReader = null;
        PrintWriter templateWriter = null;
        try {
            templateReader = new BufferedReader(new FileReader(readerFile));
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new GDEException("Failed to read the " + readerFile.getPath()
                   +" template", ex);
        }
        
        try {
            templateWriter = new PrintWriter(new FileWriter(writerFile), true);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new GDEException("Failed to read the " + writerFile.getPath()
                   +" for writing", ex);
        } catch(IOException ex) {
            throw new GDEException("Failed to create the file '" + writerFile.getPath()+
                     "for writing", ex);
        }
        
        while(true) {
            String line = null;
            try {
                line = templateReader.readLine();            
            } catch(IOException ex) {
                throw new GDEException("Failed to read from the " +
                        "template java file.", ex);
            }
            if(line == null)
                break;            
            
            // Define package name
            line=line.replaceFirst(PortletConstants.PACKAGE_STRING,PortletConstants.PACKAGE_NAME);
            
            // Define Classname
            line=line.replaceFirst(PortletConstants.PORTLET_NAME_STRING,operationName);

            // print the line in the template file, as it is.
            templateWriter.println(line);

            // Insert extra dynamic code, if the line position in the template
            // file is encountered.
            /*
            if(line.contains("IMPORT STATEMENT FOR THE TTVALUEIMPLOBJ")) {
                // insert the import statement.
                templateWriter.println("import " + 
                        displayInfo.getExtTTValueImplClass() + ";");
            } else if(line.contains("TTVALIMPLOBJ INITIALIZATION")) {
                // print the variable initialization statement.
                templateWriter.println(displayInfo.getExtTTValueInterface() + 
                        " ttValObj = new " + 
                        displayInfo.getExtTTValueImplClass() + "();");
            } else if(line.contains("DYNAMICALLY GENERATED CODE")) {
                // insert the code for each control here.
                templateWriter.println(getGeneratedCodeForControls());
            }
             */
        }        
    }
    
    
    private String getGeneratedCodeForControls() throws GDEException {
        
        StringBuffer buffer = new StringBuffer();
        
        // print the array declaration in the buffer.
        buffer.append("String[] strValues =  null;\n");
        buffer.append("Collection fieldErrors = new ArrayList();\n");
        
        // Iterate through each of the fields in the TTValue display info. For the
        // time being, the base tt value fields are not considered as this is
        // included in the static template. If an error occurs while generating
        // code for any of the field, return the value.
        Map map = displayInfo.getExtFieldInfoMap();
        Collection coll = map.entrySet();
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = (String) entry.getKey();
            TTValueFieldInfo fieldInfo = (TTValueFieldInfo) entry.getValue();
            // Check if the field Info is to be included in the creatett portlet
            // If not, skip to the next fieldInfo.
            if(!fieldInfo.getIsRequired())
                continue;
            
            FieldDisplayInfo info = fieldInfo.getFieldDisplayInfo();
            FieldMetaData metaData = fieldInfo.getFieldMetaData();

            // Generate the code to get the value of the field from the
            // http request. The code generated will be different for each
            // type of control included.
            UIComponentType compType = info.getUIComponentType();
            String httpReqParamName = 
                        compType.getComponentPrefix() + metaData.getFieldName();
            // Generate the code to get the value from the request.
            buffer.append("strValues = request.getParameterValues(\"");
            buffer.append(httpReqParamName);
            buffer.append("\");\n");            
            
            // Generate the code to convert the string/string array obtained to
            // appropriate data type.            
            FieldValueCodeGenerator generator = new FieldValueCodeGenerator();            
            generator.setFieldMetaData(metaData);
            generator.execute();
            buffer.append(generator.getGeneratedCode());
            buffer.append("\n");
            
        }   
        System.out.println(buffer.toString());
        return buffer.toString();
    }
}
