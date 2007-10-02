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
 * ExecOperation
 */
public interface ExecOperation extends ExecComponent {

    public static final String ATTR_COMMAND = "command";
    public static final String ATTR_POLLING_INTERVAL = "pollingInterval";
    public static final String ATTR_POLLING_PATTERN = "pollingPattern";

    public static final String REPETITIVE_INVOKE = "RepetitiveInvokeAndReceive";
    public static final String INVOKE_ONCE_AND_RECEIVE = "InvokeOnceAndKeepReceiving";
    
    public String getCommand();
    public void setCommand(String val);
    
    public int getPollingInterval();
    public void setPollingInterval(int val);

    public String getPollingPattern();
    public void setPollingPattern(String pattern);
}
