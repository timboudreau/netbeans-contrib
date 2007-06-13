
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
 * This class represents the display control attribute for a non-complex entry
 * type. Currently, there are no attributes for this class. This inherits the
 * FieldDisplayInfo class. In future, during analysis, any parameters that are
 * found to be applied  to the non-complex fields, will be added to this class.
 * 
 * @author Hareesh Ravindran
 */
public class SimpleEntryFieldDisplayInfo extends FieldDisplayInfo {
    
    /**
     * Creates a new instance of SimpleEntryFieldDisplayInfo
     */
    public SimpleEntryFieldDisplayInfo() {}

    public SimpleEntryFieldDisplayInfo(String fieldDisplayName, 
                        UIComponentType compType) {
        super(fieldDisplayName, compType);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return new SimpleEntryFieldDisplayInfo(
                fieldDisplayName, displayUIComponentType);
    }
    
    public int getFieldDataEntryNature() {
        return FieldDataEntryNature.TYPE_ENTRY;
    }
    
    public String getDisplayInfoStr() {
        StringBuffer strBuf = new StringBuffer();                
        strBuf.append("Field Display Name: '");
        strBuf.append(this.fieldDisplayName);
        strBuf.append(", UI Control: '");
        strBuf.append(this.displayUIComponentType);
        strBuf.append("'");                
        return strBuf.toString();
    }
}

