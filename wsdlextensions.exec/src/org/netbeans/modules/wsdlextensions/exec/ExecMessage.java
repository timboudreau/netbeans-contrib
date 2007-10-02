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
package org.netbeans.modules.wsdlextensions.exec;

/**
 * ExecMessage
 */
public interface ExecMessage extends ExecComponent {
    
    public static final String ATTR_USE = "use";
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";
    public static final String ATTR_RECORDS_TO_BE_SKIPPED = "recordsToBeSkipped";
    public static final String ATTR_DELIMITERS_OF_RECORD = "delimitersOfRecord";
    public static final String ATTR_INJECT_CONTEXT_INFO = "injectContextInfo";

    public static final String ATTR_USE_TYPE_LITERAL = "literal";
    public static final String ATTR_USE_TYPE_ENCODED = "encoded";

    public String getUse();
    public void setUse(String val);
    
    public String getEncodingStyle();
    public void setEncodingStyle(String val);

    public int getRecordsToBeSkipped();
    public void setRecordsToBeSkipped(int val);

    public String getDelimitersOfRecord();
    public void setDelimitersOfRecord(String val);

    public boolean getInjectContextInfo();
    public void setInjectContextInfo(boolean val);
}
