
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


package com.sun.tthub.processor;

import javax.portlet.ActionRequest;
import com.sun.tthub.data.AppRequest;
/**
 *
 * @author choonyin
 */
public class RequestRouter {
    public static final String SaveEditPathRequest="Save";
    public static final String SaveElementRequest="saveelement.do";
    public static final String SelectElementRequest="saveelement.do";
    public static final String RemoveElementsRequest="removeelements.do";
    public static final String AbortRequest="Abort";
    public static final String DrillDownRequest="drilldown.do";
    
    /** Creates a new instance of RequestRouter */
    public RequestRouter() {
    }
    
    public String route(AppRequest appRequest,ActionRequest request){
        String requestId=(String)appRequest.getRequestId();
        System.out.println("-----------RequestRouter-------------");
        if (requestId!=null){
            if (requestId.equalsIgnoreCase(this.SaveEditPathRequest)){
                return new SaveEditPathReqProcessor().processRequest(appRequest,request);
            }else if ( requestId.equalsIgnoreCase(this.AbortRequest)){
                return new AbortReqProcessor().processRequest(appRequest,request);
            }else if ( requestId.equalsIgnoreCase(this.DrillDownRequest)){
                return new DrillDownReqProcessor().processRequest(appRequest,request);
            }else{
                return null;
            }
            
        }else{
            return null;
        }
    }
    
    
}
