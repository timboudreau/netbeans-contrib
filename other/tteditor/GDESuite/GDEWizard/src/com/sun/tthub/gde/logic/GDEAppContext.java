
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

import com.sun.tthub.gde.ui.GDEWizardUI;
import java.awt.Dialog;

/**
 * This is a singleton class that is initialized at the beginning of the GDE
 * application. It stores references to the global objects that can be used 
 * in the application. It is the responsibility of the application to initialize
 * this object with required data, during the application initialization itself.
 *
 * @author Hareesh Ravindran
 *
 */
public final class GDEAppContext {
    
    private static GDEAppContext appContext;
    
    private GDEPreferencesController prefsController;
    private GDEWizardUI wizardUI;
    private ClassLoader gdeClassLoader;
    private GDEClassesManager gdeClassesMngr;
    
    /** Creates a new instance of GDEAppContext */
    private GDEAppContext() {}
    
    public void setGdePrefsController(GDEPreferencesController controller) {
        this.prefsController = controller;
    }
    
    public GDEPreferencesController getGdePrefsController() {
        return this.prefsController;
    }
    
    public ClassLoader getClassLoader() { return this.gdeClassLoader; }
    public void setClasssLoader(ClassLoader loader) 
            { this.gdeClassLoader = loader; }
    
    // gets and sets the dialog. This has to be done on initialization of the
    // wizard, so that all the components have access to the dialog.
    public void setWizardUI(GDEWizardUI ui) { this.wizardUI = ui; }
    public GDEWizardUI getWizardUI() { return this.wizardUI; }
    
    public GDEClassesManager getClassesManager() { return this.gdeClassesMngr; }
    public void setClassesManager(GDEClassesManager gdeClassesMngr) 
            { this.gdeClassesMngr = gdeClassesMngr; }
    
    
    public static synchronized GDEAppContext getInstance() {
        if(appContext == null)
            appContext = new GDEAppContext();
        return appContext;
    }
}

