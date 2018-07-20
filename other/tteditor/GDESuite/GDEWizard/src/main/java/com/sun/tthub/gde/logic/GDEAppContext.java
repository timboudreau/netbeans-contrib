
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

