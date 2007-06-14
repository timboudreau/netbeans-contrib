
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

import com.sun.tthub.gde.util.NetbeansUtilities;
import com.sun.tthub.gdelib.GDEException;

/**
 * This class is responsible for the loading, saving and validation of the
 * GDEPreferences object. The GDEPreferences object will be loaded from the
 * preferences property file when the loadGdePreferences() method is invoked.
 * Similarly, it is stored with the changes when the saveGdePreferences() method
 * is invoked. The validateGdePreferences() method does the validation of the
 * GDEPreferences object.
 *
 * @author Hareesh Ravindran
 *
 */
public final class DefGDEPreferencesControllerImpl implements GDEPreferencesController {
    
    // A new instance of the GDEPreferences is created whenever the
    // default GDEPreferencesController is loaded. The whole application
    // will have only one GDEPreferences object as the
    private GlobalGDEPreferences gdePrefs = new GlobalGDEPreferences();
    
    /**
     * Creates a new instance of DefGDEPreferencesControllerImpl. The
     * constructor is of package scope so that only the GDE application can
     * create the instance of the default preferences controller impl.
     */
    protected DefGDEPreferencesControllerImpl() {}
    
    /**
     * This function will load the GDEPreferences from a property file that is
     * assumed to be present in the GDE_HOME directory. It assumes that the
     * user has read permission for the property file.
     *
     * @throws com.sun.tthub.gde.GDEException if it fails to load the
     *      GDEProperties from the properties file in the GDE_HOME directory.
     *
     * @return the GDEPreferences object, if it is successfully loaded.
     */
    public GDEPreferences retrievePreferences() throws GDEException {
        
        // get the gde home directory.
        // check if the property file is present.
        // if so, load the values from the properties file.
        // if not, load the default values for the java home, ant home
        
        
        /*return new GDEPreferences("/usr/java",
                "/opt/apache-ant-1.6.2",
                "/export/home/projects/gdewizard/gdefolder",
                "/opt/SUNWappserver8.1", "/opt/SUNWps",
                "uid=amAdmin,ou=People,dc=India,dc=Sun,dc=COM");
         */
        /*
        return new GDEPreferences("E://Sun/AppServer/jdk/bin",
                "E://Sun/netbeans-5.5/ide7/ant",
                "E://projects/GDESuite/GDEWizard/gdefolder",
                "E://Sun/AppServer", "E://Sun/SUNWps",
                "uid=amAdmin,ou=People,dc=India,dc=Sun,dc=COM");
         */
        //return gdePrefs.getPreferences();
        String gdefolder=NetbeansUtilities.getUserDir()+"/gdefolder";
        return new GDEPreferences("",
                "",
                gdefolder,
                "", "",
                "uid=amAdmin,ou=People,dc=India,dc=Sun,dc=COM");
        
    }
    
    public void saveGdePreferences(GDEPreferences pref) throws GDEException {
        
        // get the gde home directory.
        // check if the property file is present.
        // if so, rewrite the contents of the file using the new values of the
        //      GDEPreferences object.
        // if not create the property file gdeprefs.properties. if it fails to
        //      create the file, return an error.
        // if it succeeds, write the GDEPreferences object content into the
        //      newly created properties file.
        gdePrefs.setPreferences(pref);
        
    }
    
    public void validateGdePreferences(GDEPreferences pref) throws GDEException {
        // check if the java home specified in the GdePreferences is valid.
        // if not throw an exception.
        // if java home is valid, check for the version of java. if it is less
        // than 1.4 throw an exception.
        // check if the ant home specified in the GDEPreferences is valid.
        // if not throw an exception.
        // check if the GDEFolder specified is valid. if not, throw an exception
        // If the GDEFolder is valid, check if the user has read/write
        // permissions on the folder. If not throw an exception.
        
        
        //If GDEFolder is not present, create and copy default gde folder.
        try{
            new NetbeansUtilities().copyDefaultGDEFolder(pref.getGdeFolder());
        }catch(Exception e){
            e.printStackTrace();
            throw new GDEException("Unable to create default gdefolder-"+e.getMessage());
        }
        
    }
    
}
