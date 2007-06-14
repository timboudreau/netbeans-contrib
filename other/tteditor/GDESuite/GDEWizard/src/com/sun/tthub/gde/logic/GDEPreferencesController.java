
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

/**
 * This interface contains the methods for loading, saving and validating the
 * GDEPreferences object. An implementation of this interface is required to
 * perform these functions, as GDEPreferences is the core object of the 
 * application. A default implementation of this interface is provided with
 * the gde package - DefGDEPreferencsControllerImpl. If the user runs the GDE
 * application without configuring any GDEPreferencesController implementation,
 * the application uses the default implementation provided.
 *
 * @author Hareesh Ravindran
 */
public interface GDEPreferencesController {
    

    /**
     * This function loads the GDEPreferences object using the implemntation 
     *      technique. The preferences may be loaded from a database, property
     *      file etc. and is specific to the implementation class.
     *
     * @throws com.sun.tthub.gde.GDEException if the implementation fails to 
     *      load the GDEPreferences object due to internal exceptions like
     *      file input output exceptions, connection exceptions etc.
     * 
     * @return GDEPreferences object if the function successfully loads the
     *      GDEPreferences object.
     */
    public GDEPreferences retrievePreferences() throws GDEException;    
    
    /**
     * This method is used by the GDE application to store the modified values
     * of the GDEPreferences. The preferences may be stored anywhere depending
     * on the implementation - like database, flat file etc.
     *
     * @param pref the modified GDEPreferences object instance which will be 
     *      used to alter the preferences stored in the persistent storage.
     *
     * @throws com.sun.tthub.gde.GDEException if the implementation fails to
     *      store the modified GDEPreferences object due to internal exceptions
     *      like fine input output exceptions, connection exceptions etc.
     *
     * @throws NullPointerException if the GDEPreferences object passed to the
     *      function is null.
     *
     */
    public void saveGdePreferences(GDEPreferences pref) throws GDEException;
    
    
    /**
     * This method is used by the GDE application to validate the GDEPreferences
     * after loading from the persistent store and before saving the modified
     * values to the persistent store.
     *
     * @throws com.sun.tthub.gde.GDEException if the GDEPreferences object holds
     *      a state which is not allowed according to the rules of the 
     *      GDE application. For example, an invalid java home or an invalid
     *      ant home or an invalid GDE Folder or a GDE folder without read or
     *      write permissions to the current OS user, will be the cause of this
     *      exception. The default implementation of this function will throw
     *      a GDEValidationException.
     * 
     * @throws NullPointerException if the GDEPreferences object passed to the
     *      method is null.
     *
     */
    public void validateGdePreferences(GDEPreferences pref) throws GDEException;

}

