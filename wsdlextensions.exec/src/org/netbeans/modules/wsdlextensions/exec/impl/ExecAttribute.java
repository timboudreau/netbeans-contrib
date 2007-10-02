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

import org.netbeans.modules.xml.xam.dom.Attribute;

import org.netbeans.modules.wsdlextensions.exec.ExecAddress;
import org.netbeans.modules.wsdlextensions.exec.ExecMessage;
import org.netbeans.modules.wsdlextensions.exec.ExecOperation;

/**
 * ExecAttribute
 */
public enum ExecAttribute implements Attribute {
    EXEC_ADDRESS_HOST_NAME(ExecAddress.ATTR_HOST_NAME),
    EXEC_ADDRESS_USER_NAME(ExecAddress.ATTR_USER_NAME),
    EXEC_ADDRESS_PASSWORD(ExecAddress.ATTR_PASSWORD),
    
    EXEC_OPERATION_COMMAND(ExecOperation.ATTR_COMMAND),
    EXEC_OPERATION_POLLING_INTERVAL(ExecOperation.ATTR_POLLING_INTERVAL),
    EXEC_OPERATION_POLLING_PATTERN(ExecOperation.ATTR_POLLING_PATTERN),
    
    EXEC_MESSAGE_USE(ExecMessage.ATTR_USE),
    EXEC_MESSAGE_ENCODING_STYLE(ExecMessage.ATTR_ENCODING_STYLE),
    EXEC_MESSAGE_RECORDS_TO_BE_SKIPPED(ExecMessage.ATTR_RECORDS_TO_BE_SKIPPED),
    EXEC_MESSAGE_DELIMITERS_OF_RECORD(ExecMessage.ATTR_DELIMITERS_OF_RECORD),
    EXEC_MESSAGE_INJECT_CONTEXT_INFO(ExecMessage.ATTR_INJECT_CONTEXT_INFO);
    
    private String name;

    public ExecAttribute getEXEC_ADDRESS_HOST_NAME() {
        return EXEC_ADDRESS_HOST_NAME;
    }
    private Class type;
    private Class subtype;
    
    ExecAttribute(String name) {
        this(name, String.class);
    }
    
    ExecAttribute(String name, Class type) {
        this(name, type, null);
    }
    
    ExecAttribute(String name, Class type, Class subtype) {
        this.name = name;
        this.type = type;
        this.subtype = subtype;
    }
    
    public String toString() { return name; }
    
    public Class getType() {
        return type;
    }
    
    public String getName() { return name; }
    
    public Class getMemberType() { return subtype; }
}
