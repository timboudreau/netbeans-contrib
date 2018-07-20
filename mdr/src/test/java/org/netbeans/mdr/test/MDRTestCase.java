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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder.
 */

package org.netbeans.mdr.test;

import java.io.*;
import java.util.*;
import java.net.*;

import junit.extensions.*;
import junit.framework.*;

import org.netbeans.api.mdr.*;
import org.netbeans.junit.*;
import org.openide.util.Lookup;

import org.netbeans.mdr.util.*;
import org.netbeans.mdr.NBMDRepositoryImpl;
import org.netbeans.lib.jmi.mapping.*;

import javax.jmi.reflect.*;
import javax.jmi.model.*;
import javax.jmi.xmi.*;

/**
 * Abstract predecessor of MDR test cases handling set up and providing some "high level"
 * functionality over repository (e.g. load model method).
 */
public abstract class MDRTestCase extends NbTestCase {
    
    // default repository, it is inited by setUp method
    protected MDRepository repository = null;
    // default model package (created on booting repository)
    protected ModelPackage modelPackage = null;
    
    public MDRTestCase(String testName) {
        super (testName);
    }
    
    /**
     * Instantiates Model package and loads MOF model to it.
     *
     * @param docName XMI file name (should reside in 'data' directory)
     * @param pkgName name of Model package extent to be instantiated
     *
     * @return instantiated model package
     */
    public ModelPackage loadMOFModel (String docName, String pkgName) {
        Lookup lookup = Lookup.getDefault ();
        XmiReader reader = (XmiReader) lookup.lookup (XmiReader.class);
        URL url = this.getClass ().getResource ("data//" + docName);
        if (url == null)
            fail ("Resource not found: " + "data//" + docName);
        ModelPackage pkg = (ModelPackage) createExtent (
            findMofPackage (modelPackage, "Model"), pkgName
        );
        try {
            repository.beginTrans (true);
            reader.read (url.toExternalForm(), pkg);
        } catch (Exception e) {
            fail (e.getMessage ());
        } finally {
            repository.endTrans ();
        }
        return pkg;
    }

    /**
     * Finds a package (MofPackage instance) given by a name in ModelPackage extent.
     *
     * @param modelPackage ModelPackage where the queried instance should reside
     * @param name of the MofPackage instance to be found
     *
     * @return the desired MofPackage instance
     */
    public MofPackage findMofPackage (ModelPackage modelPackage, String name) {
        MofPackageClass proxy = modelPackage.getMofPackage ();
        Iterator iter = proxy.refAllOfType ().iterator ();
        MofPackage thePackage = null;
        while (iter.hasNext ()) {
            MofPackage pkg = (MofPackage) iter.next ();
            if (pkg.getName ().equals (name)) {
                thePackage = pkg;
                break;
            } // if                
        } // while
        if (thePackage == null)
            fail ("Cannot find package " + name);
        return thePackage;
    }
 
    /**
     * Creates extent according to given MofPackage and name.
     */
    public RefPackage createExtent (MofPackage pkg, String name) {
        RefPackage extent = getExtent(name);
        if (extent != null) {
            extent.refDelete();
        }
        try {
            return repository.createExtent (name, pkg);
        } catch (CreationFailedException e) {
            e.printStackTrace ();
        }
        fail ("Package instantiation failed: " + name);
        return null;
    }
        
    /**
     * Returns extent in repository given by its name.
     */
    public ModelPackage getExtent (String name) {        
        ModelPackage vmp = null;        
        try {
            vmp = (ModelPackage) repository.getExtent (name);
        } catch (Exception e) {
            fail ("Model not loaded, name not resolved.");
        }
        return vmp;        
    }
   
    protected void setUp() {
        // properties will be set only if running outside of IDE
        if (System.getProperty("org.openide.version") == null) {
            System.setProperty("org.netbeans.mdr.persistence.Dir", System.getProperty("work.dir") + "/test_repository_1");
            System.setProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName",
            "org.netbeans.mdr.persistence.btreeimpl.btreestorage.BtreeFactory");
        }
        
        repository = ((MDRManager) Lookup.getDefault().lookup(MDRManager.class)).getDefaultRepository();
        if (repository == null)
            fail ("Repository manager not found.");
        modelPackage = (ModelPackage) repository.getExtent ("MOF");
        if (modelPackage == null)
            fail ("MOF package not found.");
    }
    
    protected static File getFile(String fileName) {
        return new File(System.getProperty("work.dir") + '/' + fileName);
    }
    
}
