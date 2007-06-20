
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

import java.util.Collection;
import java.util.Map;
import java.lang.StringBuffer;
/**
 *
 * @author choonyin
 */
public class ReqProcessingResult {
    
    private Map fieldErrors;
    private Collection globalErrors;
    private String statusMessage;
    private boolean clearTTValueFlag=false;
    
    /** Creates a new instance of ReqProcessingResult */
    public ReqProcessingResult() {
    }

    public Map getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Collection getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(Collection globalErrors) {
        this.globalErrors = globalErrors;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isClearTTValueFlag() {
        return clearTTValueFlag;
    }

    public void setClearTTValueFlag(boolean clearTTValueFlag) {
        this.clearTTValueFlag = clearTTValueFlag;
    }
    
    public String toString(){
      StringBuffer buffer=new StringBuffer("[ReqProcessingResult]");
      buffer.append("\nstatusMessage-").append(this.statusMessage);
      buffer.append("\nfieldErrors-").append(this.fieldErrors);
      buffer.append("\nglobalErrors-").append(this.globalErrors);
      buffer.append("\nisClearTTValueFlag-").append(this.isClearTTValueFlag());
      return buffer.toString();
    }
    
}
