
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

import javax.portlet.ActionRequest;
import com.sun.tthub.gdelib.fields.UIComponentType;

/**
 * The NonComplexFieldStateHandler will extract the value of the field using the
 * getRequestParameterValues() function of the HttpRequest object. This value will
 * be stored in the TemporaryFieldState object. The value can be null, String or
 * a String array depending on the type of the control from which the value is
 * retrieved. But the TemporaryFieldState object will maintain a String array to
 * store the value. Later, the UI tag should be able to interpret the value stored
 * in the TemporaryFieldState object and regenerate the state of the object.
 *
 * @author choonyin
 */

public class NonCmplxFieldStateHandler extends FieldStateHandler{
    
    /** Creates a new instance of NonCmplxFieldStateHandler */
    public NonCmplxFieldStateHandler(ActionRequest request) {
        super(request);
    }
    
    public TempFieldState extractTempFieldState(UIComponentType uiComponent,String fieldName) {
       System.out.println("[NonCmplxFieldStateHandler.extractTempFieldState-request]-"+request);
        
       String[] fieldValue= request.getParameterValues(fieldName);
       
       System.out.println("[NonCmplxFieldStateHandler.extractTempFieldState-fieldName]-"+fieldName);
       System.out.println("[NonCmplxFieldStateHandler.extractTempFieldState-fieldValue]-"+fieldValue);
       
       return new DefaultFieldState(fieldName,uiComponent,fieldValue);
    }
}
