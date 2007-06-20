
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
/**
 * The ComplexArrayFieldStateHandler will not store anything as the control is
 * non-editable (i.e. the user cannot edit/change the state of the control inline, 
 * without sending a request to the server). So, this handler assumes that the 
 * user has not edited the value in the control and it will mark the value as 
 * 'Not Edited'.
 *
 * @author choonyin
 */
public class CmplxArrayFieldStateHandler extends FieldStateHandler {
    
    /** Creates a new instance of CmplxArrayFieldStateHandler */
    public CmplxArrayFieldStateHandler(ActionRequest request) {
        super(request);
    }
    
}
