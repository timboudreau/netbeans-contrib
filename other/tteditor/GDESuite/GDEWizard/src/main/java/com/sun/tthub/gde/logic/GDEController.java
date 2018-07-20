
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
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

