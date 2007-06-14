
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


package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gdelib.GDEException;

/**
 *
 * @author Hareesh Ravindran
 */
public class GDEWizardPageValidationException extends GDEException {
    
    /** Creates a new instance of GDEWizardPageValidationException */
    public GDEWizardPageValidationException() {}
    
    public GDEWizardPageValidationException(String msg) { super(msg); }
    
    public GDEWizardPageValidationException(Throwable th) { super(th); }
    
    public GDEWizardPageValidationException(String msg, Throwable th)
                            { super(msg, th); }
        
}
