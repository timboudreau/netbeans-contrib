
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

import com.sun.tthub.util.DataConstants;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import com.sun.tthub.data.AppRequest;

/**
 *
 * @author choonyin
 */
public class AbortReqProcessor {
    
    /** Creates a new instance of AbortReqProcessor */
    public AbortReqProcessor() {
    }
    
   public String processRequest(AppRequest appRequest,ActionRequest request){
       System.out.println("[AbortReqProcessor.processRequest Entry]");
        PortletSession session= request.getPortletSession();
        session.removeAttribute(DataConstants.TTVALUEDISPLAYINFO);
        session.removeAttribute(DataConstants.TTVALUEIMPLOBJECT);
        session.removeAttribute(DataConstants.REQPROCESSINGRESULT);
        session.removeAttribute(DataConstants.TTVALUEIMPLXML);
                
        
       //Specify the returnpath for forwarding to the initial page
       String returnpath="/";
       return returnpath;
   }
    
}
