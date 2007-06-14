
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

package com.sun.tthub.gde.portletcontrol;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;

/**
 *
 * @author Hareesh Ravindran
 */
        
public class BooleanComboBoxControl extends TTValueControl {
    
    /** Creates a new instance of BooleanComboBoxControl */
    public BooleanComboBoxControl(FieldInfo fieldInfo) {      
        super(fieldInfo);
    }
    
    public String getFieldInfoJspString() throws GDEException {
        StringBuffer buffer = new StringBuffer();
        String fieldName = "cmbBool" + 
                    fieldInfo.getFieldMetaData().getFieldName();  
        buffer.append("<select name=\"");
        buffer.append(fieldName);
        buffer.append("\">");        
        buffer.append("<option value=\"Y\">Yes</option>");
        buffer.append("<option value=\"N\">No</option>");        
        buffer.append("</select>");  // close the select tag.
        return buffer.toString();
    }
    
    public String getFieldInfoDeclarationString() throws GDEException {
        return null;
    }
    
    public String getFieldInfoInitializationString() throws GDEException {
        return null;
    }    
}
