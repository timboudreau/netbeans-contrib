
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
import com.sun.tthub.gde.ui.GDEWizardUI;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Hareesh Ravindran
 */

public class GDEController {
    
    /** Creates a new instance of GDEController */
    public GDEController() {}
    
    /**
     * Initialize the singleton instance of GDEAppContext so that all the
     * application objects can have access to the GDEPreferencesController.
     */
    public void initializeAppContext(Map initParams) throws GDEException {
        //  Get access to the singleton instance of the GDEAppContext object.        
        GDEAppContext context = GDEAppContext.getInstance();
        
        // Get the value of the preferences controller passed as the 
        // initialization parameter. If nothing is passed, use the ]
        // DefaultGDEPreferencesControllerImpl class. Otherwise instantiate the
        // specified class and throw an exception if any instantiation exception
        // occurs
        GDEPreferencesController prefsController = 
                    new DefGDEPreferencesControllerImpl();
     
        // call the retrievePreferences() method of the GdePreferencesController
        // method.
        GDEPreferences prefs = prefsController.retrievePreferences();        
        
        // validate teh GDEPreferences object that is loaded so that we can
        // make sure that the java home, ant home and the gde folder are 
        // valid directories and the current OS user has read/write permissions
        // to the gde folder.
        prefsController.validateGdePreferences(prefs);
        // The preferences controller should be set before any other process 
        // as it may be required to retrieve the GDE folder.
        context.setGdePrefsController(prefsController);
        
        
        // Use the GDE Classes Manager to load all the classes from the 
        // GDE folder. The Classes manager will use the GDEClassLoader to
        // load the classes internally.
        
        GDEClassesManager mngr = new GDEClassesManager();
        mngr.loadGdeClasses();
        
        // Set the GDEClassesManager and the ClassLoader in the app context so
        // that other components can access these objects.
        context.setClassesManager(mngr);
        context.setClasssLoader(mngr.getClassLoader());       
        
        // retrieve the main UI of the wizard. If it is not set, throw an 
        // exception to inform the user that the wizard UI should be set 
        // before proceeding.
        GDEWizardUI wizardUI = (GDEWizardUI) 
                        initParams.get(GDEInitParamKeys.GDE_WIZARD_UI);
        // Set the main wizard user interface in the application context so 
        // that other UI components can get access to the main wizard UI.
        context.setWizardUI(wizardUI);
    }    
     
}

