
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


package com.sun.tthub.data;
import com.sun.tthub.gdelib.fields.UIComponentType;
/**
 *
 * @author choonyin
 */
public class DefaultFieldState extends TempFieldState{
    
    /** Creates a new instance of DefaultFieldState */
    private String fieldName=null;
    private String[] fieldValue=null;
    
    public DefaultFieldState(String fieldName,UIComponentType componentType,String[] fieldValue) {
        super(componentType);
        this. fieldName=fieldName;
        this.fieldValue=fieldValue;
        
    }
    public String[] getFieldValue(){
        return this.fieldValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    public String toString(){
        StringBuffer buffer=new StringBuffer();
        buffer.append("[DefaultFieldState]");
        buffer.append("fieldName: [").append(fieldName).append("], ");
        buffer.append("fieldValue: [");
        if (fieldValue!=null){
            for(int i=0;i<fieldValue.length;i++)
                buffer.append(fieldValue[i]).append(";");
            
        }
        buffer.append("], ");
        buffer.append("componentType: [").append(componentType.toString()).append("]");
        
        return buffer.toString();
        
    }
    
}
