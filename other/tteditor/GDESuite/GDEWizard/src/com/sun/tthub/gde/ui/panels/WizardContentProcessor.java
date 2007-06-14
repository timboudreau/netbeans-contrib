
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
public interface WizardContentProcessor {
    public void validateContents() throws GDEWizardPageValidationException;         
    public boolean validationFailed(GDEWizardPageValidationException ex);
    
    /**
     * This function is called only after the validation succeeds. The wizard
     * controller should advance to the next page only after invoking this 
     * method.
     * 
     */
    public void processWizardContents(int wizardAction) throws GDEException;
    
    /**
     * This function is called before the validateContents() method, so that
     * any changes that has to be made to the model can be done here before 
     * validation.
     */
    public void preProcessWizardContents(int wizardAction) throws GDEException;
}
