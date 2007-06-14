
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

/**
 * This is the final class used to store the details about the GDEPreferences.
 * This class is instantiated in the GDEPreferencesController class which uses
 * this object to store the Global preferences.
 *
 * @author Hareesh Ravindran
 */
public final class GlobalGDEPreferences {
    
    // Create an empty instance of the GDEPreferences object. This will be 
    // filled with values on application initialization. (i.e when the 
    // GDEPreferencesController.retrievePreferences() or 
    // GDEPreferencesController.savePreferences() is called.
    private GDEPreferences gdePrefs = new GDEPreferences();
    
    /** Creates a new instance of GlobalGDEPreferences. The constructor is 
     * of package  scope so that only the package members can instantiate this
     * class.
     */
    protected GlobalGDEPreferences() {}
    
    public GDEPreferences getPreferences() {
        return this.gdePrefs;
    }
    
    public void setPreferences(GDEPreferences gdePrefs) {
        this.gdePrefs = gdePrefs;
    }
}

