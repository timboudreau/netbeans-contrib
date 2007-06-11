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

package org.netbeans.modules.wsdlextensions.msmq.impl;

import java.util.Collection;

import org.netbeans.modules.xml.xam.dom.Attribute;

public enum MSMQAttribute implements Attribute {
    MSMQ_HOST("hostName"),
    MSMQ_CONNECTION_MODE("connectionMode"),
    MSMQ_DESTINATION("destination"),
    MSMQ_SHARE_MODE("shareMode"),
    MSMQ_ACCESS_MODE("accessMode"),
    MSMQ_MSG_LOOKUP_ID("msgLookupID"),
    MSMQ_RECEIVE_INTERVAL("receiveInterval"),
    MSMQ_MESSAGE_PRIORITY("messagePriority"),
    MSMQ_ACKNOWLEDGEMENT("acknowledgement"),
    MSMQ_MESSAGE_TYPE("messageType"),
    MSMQ_USE("use"),
    MSMQ_ENCODING_STYLE("encodingStyle"),
    MSMQ_PART("part"),
    MSMQ_TRANSACTION("transaction");
    
    private String name;
    private Class type;
    private Class subtype;
    
    MSMQAttribute(String name) {
        this(name, String.class);
    }
    
    MSMQAttribute(String name, Class type) {
        this(name, type, null);
    }
    
    MSMQAttribute(String name, Class type, Class subtype) {
        this.name = name;
        this.type = type;
        this.subtype = subtype;
    }
    
    public String toString() {
		return name; 
	}
    
    public Class getType() {
		return type;
    }
    
    public String getName() { 
		return name; 
	}
    
    public Class getMemberType() { 
		return subtype; 
	}
}
