
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

import java.util.Map;
import java.lang.StringBuffer;
/**
 *
 * @author choonyin
 */
public class StandardTempPageState extends TempPageState  {
    
    /** Creates a new instance of StandardTempPageState */
    public StandardTempPageState(String editPath,Map tempFieldsList) {
        super(editPath,tempFieldsList);
    }
    public TempFieldState getTempFieldState(String fieldName) {
        return (TempFieldState)this.tempFieldsList.get(fieldName);
                
    }
    
   

}
