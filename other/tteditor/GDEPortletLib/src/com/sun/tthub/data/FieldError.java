
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

/**
 *
 * @author choonyin
 */
public class FieldError {
    
    /** Creates a new instance of FieldError */
    public FieldError(String fieldName, String errorMessage) {
    }
    
    private String fieldName;
    
    private String errorMessage;
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String toString(){
        StringBuffer buffer=new StringBuffer("[FieldError]");
        buffer.append("\nfieldName-").append(this.fieldName);
        buffer.append("\nerrorMessage-").append(this.errorMessage);
        return buffer.toString();
    }
}
