
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
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;

/**
 * A field is represented by a UI component. The value of the field is obtained
 * in a string array. Once the value is obtained, it is required to generate the
 * code to convert the string to the required data type. This class is 
 * responsible for  generating the code to convert the String value obtained to
 * appropriate data type and return the variable representing the value.
 *
 * @author Hareesh Ravindran
 */
public class FieldValueCodeGenerator {
    
    private FieldMetaData metaData;
    
    private StringBuffer buffer = new StringBuffer();        
    private String generatedVarName = null;
    
    /** Creates a new instance of FieldValueCodeGenerator */
    public FieldValueCodeGenerator() {}
    
    /**
     * This method will assume the following: 
     * - The variables 'strValues' array and 'fieldErrors' collection are 
     * already declared in the generated code.
     *
     * This method will check the data type in the FieldMetaData and will 
     * generate the code to convert the value stored in the 'strValues' array
     * to the appropriate data type. Any conversion errors will be  captured and
     * stored in the 'fieldErrors' collection.
     *
     */
    public void execute() throws GDEException {
       
        // Generate the code for complex data type.
        
        String dataType = metaData.getFieldDataType();
        String fieldName = metaData.getFieldName();
        boolean isArray = metaData.getIsArray();
        
        // Generate the code for integer, long, float, boolean, double.
        if(SimpleDataTypes.TYPE_INTEGER.equals(dataType) ||
                SimpleDataTypes.TYPE_LONG.equals(dataType) ||
                SimpleDataTypes.TYPE_FLOAT.equals(dataType) ||
                SimpleDataTypes.TYPE_DOUBLE.equals(dataType) ||
                SimpleDataTypes.TYPE_BOOLEAN.equals(dataType)) {
            if(!isArray)
                getDataTypeConversionCode(dataType, fieldName, false);            
            else
                getDataTypeArrConversionCode(dataType, fieldName, false);
        } else if(SimpleDataTypes.TYPE_INTEGER_OBJ.equals(dataType) ||
                SimpleDataTypes.TYPE_LONG_OBJ.equals(dataType) ||
                SimpleDataTypes.TYPE_FLOAT_OBJ.equals(dataType) ||
                SimpleDataTypes.TYPE_DOUBLE_OBJ.equals(dataType) ||
                SimpleDataTypes.TYPE_BOOLEAN_OBJ.equals(dataType)) {
            if(!isArray)
                getDataTypeConversionCode(dataType, fieldName, true);            
            else
                getDataTypeArrConversionCode(dataType, fieldName, true);
        } else if(SimpleDataTypes.TYPE_DATE.equals(dataType)) {
            if(!isArray)
                getDateConversionCode(fieldName);
            else
                getDateArrConversionCode(fieldName);
        } else if(SimpleDataTypes.TYPE_STRING.equals(dataType)) {
            
        }
        
    }
    
    // Getter methods for the generated code.    
    public String getGeneratedCode() {
        return buffer.toString();
    }

    // return the name of the generated variable, so that it can be used
    // furthur in the code.
    public String getGeneratedVarName() { return this.generatedVarName; }
    
    // Setter methods to initialize the instance.
    public void setFieldMetaData(FieldMetaData metaData) 
                { this.metaData = metaData; }
    
    /**
     * This function will generate the code to convert the field value obtaine
     * in the string array 'strValues'. It assumes that this is not null. The 
     * code to check whether 'strValues' is null, should be generated separately.
     * This function can be used for the following data types : int, long, 
     * float, double, boolean, Integer, Long, Float, Double, Boolean. 
     *
     */
    private void getDataTypeConversionCode(
            String dataType, String fieldName, boolean isObj) {        
        // print the following code to the string buffer.
        String[] prefixArr = getPrefixArr(dataType);        
        
        buffer.append("try {\n");
        if(!isObj) {
            
            // Set the generated variable name string.
            this.generatedVarName = prefixArr[0] + "Val" + fieldName;
            
            //  prepare the buffer
            buffer.append("\t");
            buffer.append(prefixArr[0]);             
            buffer.append(" ");
            buffer.append(prefixArr[0]);
            buffer.append("Val");            
            buffer.append(fieldName);            
            buffer.append(" = ");
            buffer.append(prefixArr[1]);
            buffer.append(".");
            buffer.append(prefixArr[2]);
            buffer.append("(strValues[0]);\n");
        } else {
            // Set the generated variable name string.
            this.generatedVarName = prefixArr[0] + "Obj" + fieldName;
            
            // prepare the buffer
            buffer.append("\t");
            buffer.append(prefixArr[1]);
            buffer.append(" ");
            buffer.append(prefixArr[0]);
            buffer.append("Obj");                        
            buffer.append(fieldName);            
            buffer.append(" = new ");
            buffer.append(prefixArr[1]);
            buffer.append("(strValues[0]);\n");
        }
        
        // Generate the code to invoke the setter method on the 
        // TTValueObjImpl object with the converted parameter.
        buffer.append("\tttValObj.set");
        buffer.append(fieldName);
        buffer.append("(");
        buffer.append(this.generatedVarName);
        buffer.append(");\n");                    
        
        buffer.append("} catch(NumberFormatException ex) {\n");
        buffer.append("\tfieldErrors.add(new FieldError(\"");
        buffer.append(fieldName);
        buffer.append("\", \"Error while converting the number\"));\n");        
        buffer.append("}\n");
    }    
    
    private void getDateConversionCode(String fieldName) {
        
        // Set the generated variable name string.
        this.generatedVarName = "dateVal" + fieldName;        
        
        // prepare the buffer.
        buffer.append("try {\n");
        buffer.append("\tSimpleDateFormat format = new " +
                "                   SimpleDateFormat(\"dd/MM/yyyy\");\n");
        buffer.append("\tDate dateVal");
        buffer.append(fieldName);
        buffer.append(" = format.parse(strValues[0]);");
        // Generate the code to invoke the setter method on the 
        // TTValueObjImpl object with the converted parameter.
        buffer.append("\tttValObj.set");
        buffer.append(fieldName);
        buffer.append("(");
        buffer.append(this.generatedVarName);
        buffer.append(");\n");                            
        
        buffer.append("} catch(ParseException ex) {\n");
        buffer.append("\tfieldErrors.add(new FieldError(\"");
        buffer.append(fieldName);
        buffer.append("\", \"Error while parsing the date " +
                "in 'dd/MM/yyyy' format \"));\n");
        buffer.append("}\n");
                
    }
    
    
    
    private void getDateArrConversionCode(String fieldName) {
        
        // Set the generated variable name string.
        this.generatedVarName = "dateValArr" + fieldName;        
        
        // prepare the buffer.
        
        buffer.append("Date[] dateValArr");
        buffer.append(fieldName);
        buffer.append(" = new Date[strValues.length];\n");
        buffer.append("try {\n");
        buffer.append("\tSimpleDateFormat format = new " +
                "                   SimpleDateFormat(\"dd/MM/yyyy\");\n");
        buffer.append("\tfor(int i = 0; i < strValues.length; ++i) {\n");
        buffer.append("\t\tdateValArr");
        buffer.append(fieldName);
        buffer.append("[i] = format.parse(strValues[i]);\n");
        buffer.append("\t}\n"); // End the for loop for parsing the array.        
        
        // Generate the code to invoke the setter method on the 
        // TTValueObjImpl object with the converted parameter.
        buffer.append("\tttValObj.set");
        buffer.append(fieldName);
        buffer.append("(");
        buffer.append(this.generatedVarName);
        buffer.append(");\n");                    
        
        buffer.append("} catch(ParseException ex) {\n");
        buffer.append("\tfieldErrors.add(new FieldError(\"");
        buffer.append(fieldName);
        buffer.append("\", \"Conversion error in the date array\"));\n");
        buffer.append("}\n");                        
    }
    
    /**
     * This method will generate code to convert the value in the 'strValues'
     * string array to an array of appropriate data type. It assumes that the
     * 'strValues' variable is not null. 
     * This function can be used for the following data types : int, long, 
     * float, double, boolean, Integer, Long, Float, Double, Boolean. 
     */
    private void getDataTypeArrConversionCode(String dataType,
                String fieldName, boolean isObj) {
        String[] prefixArr = getPrefixArr(dataType);
        // Declare the variable.
        if(!isObj) {            
            // Set the generated variable name string.
            this.generatedVarName = prefixArr[0] + "ValArr" + fieldName;
            
            //  prepare the buffer            
            buffer.append(prefixArr[0]);            
            buffer.append("[] ");
            buffer.append(prefixArr[0]);
            buffer.append("ValArr");
            buffer.append(fieldName);
            buffer.append(" = new ");
            buffer.append(prefixArr[0]);
            buffer.append("[strValues.length];\n");
        } else {
            // Set the generated variable name string.
            this.generatedVarName = prefixArr[0] + "ObjArr" + fieldName;
            
            //  prepare the buffer                        
            buffer.append(prefixArr[1]);
            buffer.append("[] ");
            buffer.append(prefixArr[0]);
            buffer.append("ObjArr");
            buffer.append(fieldName);
            buffer.append(" = new ");
            buffer.append(prefixArr[1]);
            buffer.append("[strValues.length];\n");            
        }
        
        buffer.append("try {\n");        
        buffer.append("\tfor(int i = 0; i < strValues.length; ++i) {\n"); 
        if(!isObj) {
            buffer.append("\t\t");
            buffer.append(prefixArr[0]);
            buffer.append("ValArr");            
            buffer.append(fieldName);
            buffer.append("[i] = ");
            buffer.append(prefixArr[1]);
            buffer.append(".");
            buffer.append(prefixArr[2]);
            buffer.append("(strValues[0]);\n");
        } else {
            buffer.append("\t\t");
            buffer.append(prefixArr[0]);
            buffer.append("ObjArr");            
            buffer.append(fieldName);
            buffer.append("[i] = ");
            buffer.append("new ");
            buffer.append(prefixArr[1]);
            buffer.append("(strValues[0]);\n");            
        }
        buffer.append("\t}\n"); // End the for loop for parsing the array.
        
        // Generate the code to invoke the setter method on the 
        // TTValueObjImpl object with the converted parameter.
        buffer.append("\tttValObj.set");
        buffer.append(fieldName);
        buffer.append("(");
        buffer.append(this.generatedVarName);
        buffer.append(");\n");                            
        
        buffer.append("} catch(NumberFormatException ex) {\n");
        buffer.append("\tfieldErrors.add(new FieldError(\"");
        buffer.append(fieldName);
        buffer.append("\", \"Conversion error in the array\"));\n");
        buffer.append("}\n");                
    }
    
    // The datatypes can be int, long, float, double or boolean
    private String[] getPrefixArr(String dataType) {
        if(dataType.equals(SimpleDataTypes.TYPE_INTEGER) ||
                    dataType.equals(SimpleDataTypes.TYPE_INTEGER_OBJ)) {
            return new String[] {"int", "Integer", "parseInt" };
        } else if(dataType.equals(SimpleDataTypes.TYPE_FLOAT) ||
                    dataType.equals(SimpleDataTypes.TYPE_FLOAT_OBJ)) {
            return new String[] {"float", "Float", "parseFloat" };            
        } else if(dataType.equals(SimpleDataTypes.TYPE_LONG) ||
                    dataType.equals(SimpleDataTypes.TYPE_LONG_OBJ)) {
            return new String[] {"long", "Long", "parseLong" };            
        } else if(dataType.equals(SimpleDataTypes.TYPE_DOUBLE) ||
                    dataType.equals(SimpleDataTypes.TYPE_DOUBLE_OBJ)) {
            return new String[] {"double", "Double", "parseDouble" };                        
        } else if(dataType.equals(SimpleDataTypes.TYPE_BOOLEAN) ||
                    dataType.equals(SimpleDataTypes.TYPE_BOOLEAN_OBJ)) {
            return new String[] {"boolean", "Boolean", "parseBoolean" };            
        }
        return null;        
    }    
}
