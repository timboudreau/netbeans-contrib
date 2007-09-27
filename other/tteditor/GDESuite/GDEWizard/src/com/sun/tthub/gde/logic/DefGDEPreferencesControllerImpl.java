
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
