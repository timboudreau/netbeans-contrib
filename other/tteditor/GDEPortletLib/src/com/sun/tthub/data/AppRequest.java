
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

/**
 *
 * @author choonyin
 */
// contains usecaseid, requestid, cureditpath 
public class AppRequest {
    private String usecaseId=null;
    private String requestId=null;
    private String curEditPath=null;
    
    /** Creates a new instance of AppRequest */
    public AppRequest() {
    }
    public AppRequest(String usecaseid,String requestid,String cureditpath) {
        this.usecaseId=usecaseid;
        this.requestId=requestid;
        this.curEditPath=cureditpath;
    }
    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCurEditPath() {
        return curEditPath;
    }

    public void setCurEditPath(String curEditPath) {
        this.curEditPath = curEditPath;
    }
    
}
