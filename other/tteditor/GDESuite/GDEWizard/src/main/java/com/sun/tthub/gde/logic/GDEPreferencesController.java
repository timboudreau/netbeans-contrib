
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

