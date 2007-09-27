
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


package com.sun.tthub.gdelib.fields;

import com.sun.tthub.gdelib.GDERuntimeException;
import com.sun.tthub.gdelib.InvalidArgumentException;

/**
 * This class maintains the meta data for a field. The field name and the
 * data type of the field are passed to it when it is initialized. The nature
 * of the field data type (i.e. SIMPLE or COMPLEX) can be determined at any time
 * using the getFieldDataTypeNature() method.
 *
 * @author Hareesh Ravindran
 */
public final class FieldMetaData implements Cloneable,java.io.Serializable {
    
    /**
     * This is the name of the field (i.e. the variable) as defined in the
     * class.
     */
    private String fieldName;
    
    /**
     * String representing the fully qualified name of the data type of the
     * field. For int, long, float, double and boolean the respective object
     * types in the java.lang package are used.
     *
     */
    private String fieldDataType;
    private int nestingLevel = 0; // applicable for arrays.
    
    /** Creates a new instance of FieldMetaData */
    public FieldMetaData() {}
    
    /**
     * Ease of use constructor.
     */
    public FieldMetaData(String fieldName, String fieldDataType) {
        if(fieldName == null || fieldDataType == null) {
            throw new InvalidArgumentException("The fieldName and fieldDataType " +
                    "cannot be null");
        }
        this.fieldName = fieldName;
        this.fieldDataType = fieldDataType;
    }
    
    public String getFieldName() { return this.fieldName; }
    
    /**
     * This function will set the name of the field.
     *
     * @param fieldName The name of the field (i.e. the variable) as specified
     *      in the class.
     *
     * @throws InvalidArgumentException if the fieldName passed is null.
     */
    public void setFieldName(String fieldName) {
        if(fieldName == null) {
            throw new InvalidArgumentException("The fieldName cannot be null");
        }
        this.fieldName = fieldName;
    }
    
    public String getFieldDataType() { return this.fieldDataType; }
    /**
     * This function will set the data type of the field.
     *
     * @param dataType The name of the data type. This should either be one
     *      among those listed in the SimpleDataTypes class or should be the
     *      fully qualified name of the java object used as the type in the
     *      class.
     *
     *  @throws InvalidArgumentException if the data type passed to the function
     *      is null.
     */
    public void setFieldDataType(String dataType) {
        if(dataType == null) {
            throw new InvalidArgumentException("The dataType cannot be null");
        }
        this.fieldDataType = dataType;
    }
    
    public boolean getIsArray() { return this.nestingLevel > 0; }
    
    public int getNestingLevel() { return this.nestingLevel; }
    public void setNestingLevel(int nestingLevel) {
        this.nestingLevel = nestingLevel; }
    
    /**
     * This function returns whether the field is of type 'SIMPLE' or of type
     * 'COMPLEX'. The field said to be simple if the data type is one among those
     * listed in SimpleDataTypes. If the fieldDataType is null, it will return
     * a the type as 'SIMPLE'. There is no setter method corresponding to this
     * function.
     *
     */
    public int getFieldDataTypeNature() {
        
        if(fieldName == null || fieldDataType == null) {
            throw new GDERuntimeException("The function getFieldDataTypeNature" +
                    "cannot be used before setting a valid fieldName and a " +
                    "fieldDataType in the object. The fieldDataType and the" +
                    "fieldName cannot be null when this function is used.");
        }
        
        return (SimpleDataTypes.TYPE_BOOLEAN.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_CHAR.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_DATE.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_DOUBLE.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_FLOAT.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_INTEGER.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_LONG.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_SHORT.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_STRING.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_BYTE.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_DOUBLE_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_FLOAT_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_INTEGER_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_LONG_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_BOOLEAN_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_CHAR_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_SHORT_OBJ.equals(fieldDataType) ||
                SimpleDataTypes.TYPE_BYTE_OBJ.equals(fieldDataType)) ?
                    DataTypeNature.NATURE_SIMPLE : DataTypeNature.NATURE_COMPLEX;
    }
    
    /**
     * Override the clone method and expose it as a public method. It creates
     * a new FieldMetaData object from the existing variables and returns it.
     *
     */
    public Object clone() throws CloneNotSupportedException {
        return new FieldMetaData(
                this.fieldName, this.fieldDataType);
    }
    
    /**
     * Overrides the equals method of the object.
     */
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof FieldMetaData))
            return false;
        FieldMetaData data = (FieldMetaData) obj;
        
        boolean isFieldNameEqual = (this.fieldName == null) ?
            (data.getFieldName() == null) : fieldName.equals(data.getFieldName());
        boolean isFieldDataTypeEqual = (this.fieldDataType == null) ?
            (data.getFieldDataType() == null) :
            fieldDataType.equals(data.getFieldDataType());
        return (isFieldNameEqual && isFieldDataTypeEqual);
    }
    
    /**
     * Note that if this function is called before initalizing the fieldDataType
     * and the fieldName values with a non null value, the function will throw
     * a GDERuntimeException.
     */
    /*
    public String toString() {
        int dtNature = getFieldDataTypeNature();
        StringBuffer buffer = new StringBuffer();
        buffer.append(fieldName);
        buffer.append("    {");
        buffer.append(fieldDataType);
        buffer.append(" : ");
        buffer.append((dtNature ==
                    DataTypeNature.NATURE_SIMPLE) ? "Simple" : "Complex");
        buffer.append("}");
        return buffer.toString();
    }
     */
    public String toString(){
        StringBuffer strBuf=new StringBuffer();
        strBuf.append("<meta-data>\r\n");
        strBuf.append("<field-name>");
        strBuf.append(this.fieldName);
        strBuf.append("</field-name>\r\n");
        strBuf.append("<data-type>");
        strBuf.append(this.getFieldDataType());
        strBuf.append("</data-type>\r\n");
        strBuf.append("<nesting-level>");
        strBuf.append(this.nestingLevel);
        strBuf.append("</nesting-level>\r\n");
        strBuf.append("<data-type-nature>");
        strBuf.append((this.getFieldDataTypeNature()==DataTypeNature.NATURE_SIMPLE)?"simple":"complex");
        strBuf.append("</data-type-nature\r\n");
        strBuf.append("</meta-data>\r\n");
        
        return strBuf.toString();
    }
}

