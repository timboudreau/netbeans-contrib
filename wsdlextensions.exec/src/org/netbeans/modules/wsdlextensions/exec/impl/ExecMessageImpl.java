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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.wsdlextensions.exec.impl;

import org.netbeans.modules.wsdlextensions.exec.ExecConstants;
import org.netbeans.modules.wsdlextensions.exec.ExecQName;
import org.netbeans.modules.wsdlextensions.exec.ExecMessage;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.w3c.dom.Element;

/**
 * ExecMessageImpl
 */
public class ExecMessageImpl extends ExecComponentImpl implements ExecMessage {

    public ExecMessageImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public ExecMessageImpl(WSDLModel model){
        this(model, createPrefixedElement(ExecQName.MESSAGE.getQName(), model));
    }
        
    public String getUse() {
        return getAttribute(ExecAttribute.EXEC_MESSAGE_USE);        
    }
    
    public void setUse(String val) {
        setAttribute(ExecMessage.ATTR_USE, 
                     ExecAttribute.EXEC_MESSAGE_USE,
                     val);        
    }
    
    public void setEncodingStyle(String val) {
        setAttribute(ExecMessage.ATTR_ENCODING_STYLE, 
                     ExecAttribute.EXEC_MESSAGE_ENCODING_STYLE,
                     val);        
    }
    
    public String getEncodingStyle() {
        return getAttribute(ExecAttribute.EXEC_MESSAGE_ENCODING_STYLE);        
    }

    public void setRecordsToBeSkipped(int val) {
        setAttribute(ExecMessage.ATTR_RECORDS_TO_BE_SKIPPED,
                     ExecAttribute.EXEC_MESSAGE_RECORDS_TO_BE_SKIPPED,
                     "" + val);
    }
    
    public int getRecordsToBeSkipped() {
        String strVal = getAttribute(ExecAttribute.EXEC_MESSAGE_RECORDS_TO_BE_SKIPPED);
        
        int numVal = ExecConstants.RECORDS_TO_BE_SKIPPED_DEFAULT;
        if ( strVal != null ) {
            try {
                numVal = Integer.parseInt(strVal);
            } catch (Exception e) {
                // just ignore
            }
        }
        return numVal;
    }
    
    public void setDelimitersOfRecord(String val) {
        setAttribute(ExecMessage.ATTR_DELIMITERS_OF_RECORD,
                     ExecAttribute.EXEC_MESSAGE_DELIMITERS_OF_RECORD,
                     val);        
    }
    
    public String getDelimitersOfRecord() {
        return getAttribute(ExecAttribute.EXEC_MESSAGE_DELIMITERS_OF_RECORD);
    }


    public void setInjectContextInfo(boolean val) {
        setAttribute(ExecMessage.ATTR_INJECT_CONTEXT_INFO,
                     ExecAttribute.EXEC_MESSAGE_INJECT_CONTEXT_INFO,
                     val ? "true" : "false");
    }
    
    public boolean getInjectContextInfo() {
        String strVal = getAttribute(ExecAttribute.EXEC_MESSAGE_INJECT_CONTEXT_INFO);
        
        boolean numVal = true;
        if ( strVal != null ) {
            try {
                numVal = Boolean.parseBoolean(strVal);
            } catch (Exception e) {
                // just ignore
            }
        }
        return numVal;
    }
}
