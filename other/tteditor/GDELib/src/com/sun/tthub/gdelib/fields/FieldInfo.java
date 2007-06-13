
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

package com.sun.tthub.gdelib.fields;

import com.sun.tthub.gdelib.InvalidArgumentException;

/**
 *
 * @author Hareesh Ravindran
 */
public class FieldInfo implements Cloneable {
    
    protected FieldMetaData fieldMetaData;         
    protected FieldDisplayInfo fieldDisplayInfo;
    
    /**
     * Creates a new instance of FieldInfo
     */
    public FieldInfo() {}
    
    public FieldInfo(FieldMetaData fieldMetaData, 
                            FieldDisplayInfo fieldDisplayInfo) {
        if(fieldMetaData == null || fieldDisplayInfo == null) {
            throw new InvalidArgumentException("The field meta data and the " +
                    "display control attribute cannot be null.");
        }
        this.fieldMetaData = fieldMetaData;
        this.fieldDisplayInfo = fieldDisplayInfo;
    }    
    
    public FieldMetaData getFieldMetaData() { return this.fieldMetaData; }
    public void setFieldMetaData(FieldMetaData fieldInfo) {
        if(fieldInfo == null) {
            throw new InvalidArgumentException("The field meta data for the " +
                    "TTValue field cannot be null.");
        }
        this.fieldMetaData = fieldInfo;
    }
    
    public FieldDisplayInfo getFieldDisplayInfo() { 
        return this.fieldDisplayInfo;
    } 
    public void setFieldDisplayInfo(FieldDisplayInfo attr) {
        if(attr == null) {
            throw new InvalidArgumentException("The display control attribute " +
                    "for the TTValue field cannot be null.");
        }
        this.fieldDisplayInfo = attr;
    }
        
    public Object clone() throws CloneNotSupportedException {
        FieldMetaData data = (fieldMetaData == null) ? null : 
                (FieldMetaData) fieldMetaData.clone();
        FieldDisplayInfo attr = (this.fieldDisplayInfo ==  null) ?
                null : (FieldDisplayInfo) fieldDisplayInfo.clone();
        return new FieldInfo(data, attr);
    }
    
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof FieldInfo))
            return false;
        FieldInfo info = (FieldInfo) obj;
        boolean isMetaDataEqual = (fieldMetaData == null) ? 
            (info.getFieldMetaData() == null) : 
            fieldMetaData.equals(info.getFieldMetaData());
        if(!isMetaDataEqual)
            return false;        
        boolean isDisplayControlAttrEqual = (fieldDisplayInfo == null) ?
            (info.getFieldDisplayInfo() == null) : 
            fieldDisplayInfo.equals(info.getFieldDisplayInfo());        
        return isDisplayControlAttrEqual;
    }    
    
    public String toString() {
        return "FieldMetaData: [" + fieldMetaData + "], " +
                "FieldDisplayInfo: [" + fieldDisplayInfo + "]";
    }
}
