
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


package com.sun.tthub.gde.util;

import com.sun.tthub.gdelib.GDEException;

/**
 *
 * @author Hareesh Ravindran
 */
public class GDEFileMngmtException extends GDEException {
    
    /**
     * Creates a new instance of GDEFileMngmtException
     */
    public GDEFileMngmtException() {}
    
    public GDEFileMngmtException(String msg) { super(msg); }

    public GDEFileMngmtException(Throwable t) { super(t); }    
    
    public GDEFileMngmtException(String msg, Throwable t) { super(msg, t); }
    
}
