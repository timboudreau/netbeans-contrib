
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

import java.lang.reflect.*;
/**
 *
 * @author choonyin
 */
public class NonComplxFieldConverter extends FieldConverter{
    
    /** Creates a new instance of NonComplxFieldConverter */
    public NonComplxFieldConverter() {
    }
    
    public static Object convert(String fieldValue,String fieldType) throws ConversionException{
        Class fieldDefinition;
        Class[] intArgsClass = new Class[] {String.class};
        Object[] intArgs = new Object[] {fieldValue};
        Constructor intArgsConstructor;
        
        Object fieldObj=null;
        try {
            
            fieldDefinition = Class.forName(fieldType);
            intArgsConstructor =
                    fieldDefinition.getConstructor(intArgsClass);
            
            fieldObj = intArgsConstructor.newInstance(intArgs);
            System.out.println("fieldObj: " + fieldObj.toString());
            return fieldObj;
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
        
    }
    
}
