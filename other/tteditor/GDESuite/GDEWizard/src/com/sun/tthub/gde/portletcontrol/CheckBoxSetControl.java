
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
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;

/**
 *
 * @author Hareesh Ravindran
 */

public class CheckBoxSetControl extends TTValueControl {
    
    /** Creates a new instance of CheckBoxSetControl */
    public CheckBoxSetControl(FieldInfo fieldInfo) {
        super(fieldInfo);
    }
        
    public String getFieldInfoJspString() throws GDEException {
        StringBuffer buffer = new StringBuffer();
        String fieldName = "chkLst" + 
                    fieldInfo.getFieldMetaData().getFieldName(); 
        
          buffer.append("<table border=\"0\" width=\"100%\">");
          
        SelectionFieldDisplayInfo displayInfo = 
                (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
        Object[] selRange = displayInfo.getSelectionRange();
        Object[] defList = displayInfo.getDefaultSelection();
        for(int i = 0; i < selRange.length; ++i) {
            buffer.append("<tr><td><input type=\"checkbox\" name=\"");
            buffer.append(fieldName);
            buffer.append("\" value=\""); 
            buffer.append(selRange[i].toString());
            buffer.append("\"");
            buffer.append(isInDefValueList(selRange[i], defList) ?
                            "checked />" : "/>");
            buffer.append(selRange[i].toString());
            buffer.append("</td></tr>");
        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private boolean isInDefValueList(Object obj, Object[] defList) {
        for(int i = 0; i < defList.length; ++i) {
            if(obj.equals(defList[i]))
                return true;
        }
        return false;
    }
    
    public String getFieldInfoDeclarationString() throws GDEException {
        return null;
    }
    
    
    public String getFieldInfoInitializationString() throws GDEException {
        return null;
    }

}
