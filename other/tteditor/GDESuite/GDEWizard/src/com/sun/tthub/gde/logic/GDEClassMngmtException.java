
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


package com.sun.tthub.gde.logic;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gde.util.*;

/**
 *
 * @author hr157577
 */
public class GDEClassMngmtException extends GDEException {
    
    /**
     * Creates a new instance of GDEClassMngmtException
     */
    public GDEClassMngmtException() {}
    
    public GDEClassMngmtException(String msg) { super(msg); }

    public GDEClassMngmtException(Throwable t) { super(t); }    
    
    public GDEClassMngmtException(String msg, Throwable t) { super(msg, t); }
    
}
