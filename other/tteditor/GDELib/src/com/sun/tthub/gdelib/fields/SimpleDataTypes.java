
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

/**
 * This interface represents the data types that are known to the wizard. The
 * wizard does not need any separate pop up window to accept the value for these
 * data types.
 *
 * @author Hareesh Ravindran
 */
public interface SimpleDataTypes {
    
    public static final String TYPE_STRING = "java.lang.String";
    public static final String TYPE_DATE = "java.util.Date";
    
    public static final String TYPE_INTEGER_OBJ = "java.lang.Integer";
    public static final String TYPE_LONG_OBJ = "java.lang.Long";
    public static final String TYPE_FLOAT_OBJ = "java.lang.Float";
    public static final String TYPE_DOUBLE_OBJ = "java.lang.Double";
    public static final String TYPE_BOOLEAN_OBJ = "java.lang.Boolean";
    public static final String TYPE_SHORT_OBJ = "java.lang.Short"; 
    public static final String TYPE_BYTE_OBJ = "java.lang.Byte";
    public static final String TYPE_CHAR_OBJ = "java.lang.Character";
        
    public static final String TYPE_INTEGER = "int";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_SHORT = "short";    
    public static final String TYPE_FLOAT = "float";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_BYTE = "byte";    
    public static final String TYPE_CHAR = "char";
}
