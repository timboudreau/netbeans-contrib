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
import org.netbeans.modules.wsdlextensions.exec.ExecOperation;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.w3c.dom.Element;

/**
 * ExecOperationImpl
 */
public class ExecOperationImpl extends ExecComponentImpl
        implements ExecOperation {

    public ExecOperationImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public ExecOperationImpl(WSDLModel model){
        this(model, createPrefixedElement(ExecQName.OPERATION.getQName(), model));
    }
    
    public String getCommand() {
        return getAttribute(ExecAttribute.EXEC_OPERATION_COMMAND);
    }
    
    public void setCommand(String val) {
        setAttribute(ExecOperation.ATTR_COMMAND, 
                     ExecAttribute.EXEC_OPERATION_COMMAND,
                     val);        
    }

    public int getPollingInterval() {
        String strVal = getAttribute(ExecAttribute.EXEC_OPERATION_POLLING_INTERVAL);
        
        int numVal = ExecConstants.POLLING_INTERVAL_DEFAULT;
        if ( strVal != null ) {
            try {
                numVal = Integer.parseInt(strVal);
            } catch (Exception e) {
                // just ignore
            }
        }
        return numVal;
    }
    
    public void setPollingInterval(int val) {
        setAttribute(ExecOperation.ATTR_POLLING_INTERVAL,
                     ExecAttribute.EXEC_OPERATION_POLLING_INTERVAL,
                     "" + val);
    }

    public String getPollingPattern() {
        return getAttribute(ExecAttribute.EXEC_OPERATION_POLLING_PATTERN);
    }
    
    public void setPollingPattern(String val) {
        setAttribute(ExecOperation.ATTR_POLLING_PATTERN, 
                     ExecAttribute.EXEC_OPERATION_POLLING_PATTERN,
                     val);        
    }
}
