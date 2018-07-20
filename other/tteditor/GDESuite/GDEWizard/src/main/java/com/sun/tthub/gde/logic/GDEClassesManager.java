
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
import com.sun.tthub.gde.util.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is the basic GDEClassesManager. The GDE application will create an instance
 * of the class loader at the start and set this in the GDEAppcontext. The parent
 * of this class loader is the system class loader. The responsiblity of this 
 * class loader is to load all the classes present in the jar files of the
 * GDE Folder. This is done once at the start up, but the classes can be reloaded
 * any time using the loadGdeClasses() method. This class loader will use the
 * JarClassLoader to load all the classes. The JarClassLoader will have this
 * classLoader as the parent.
 * 
 * @author Hareesh Ravindran
 */
public class GDEClassesManager {
    
    private GDEClassLoader gdeClassLoader = null;
    private String gdeFolderName = null;       
    
    private Collection standardTypeLst = new ArrayList();
    private Collection assignableTypeLst = new ArrayList();
    
    /**
     * These are the standard jar files to be loaded. The order of the jar files
     * listed in the array is important as the dependent jar files should 
     * appear after the jars on which they are dependent. If this is not ensured,
     * the class loader will throw a NoClassDefFoundError (since it is not able
     * to resolve the dependent classes). Currently, the list of standard jars
     * is hard coded here. But, this can later be changed to be read from an
     * external configuration file so that the code need not be altered with the
     * change in ossj specification version.
     *
     */
    private static final String[] STANDARD_JARS =  { /*"j2ee.jar",*/ 
            "oss_common_spec-1.0.jar", "oss_qos_spec-1.0.jar", 
            "oss_trouble_ticket_spec-1.0.jar" };
                          
            
    /**
     * Creates a new instance of GDEClassesManager with the class loader used
     * to load the classes from the GDE folder. This class will be instantiated
     * by the GDEController and the loader will be passed to it by the GDE
     * controller.
     */
    public GDEClassesManager() throws GDEException {         
        // create a new class loader instance.
        this.gdeClassLoader = new GDEClassLoader(); 
        gdeFolderName = new GDEFolderManager().getGdeFolderName();        
    }
        
    /**
     * Return the class loader used to load all the classes from the GDE 
     * folder.
     */    
    public ClassLoader getClassLoader() { return this.gdeClassLoader; }
    
    /**
     * This function scans the GDEFolder for jar files and loads the classes from
     * the jar files. First it loads all the standard jar files from the 
     * GDE folder using the protected function loadStandardGdeClasses(). After
     * this it will load classes from all other jar files using the 
     * loadNonStandardGdeClasses().
     * 
     * @throws GDEClassMngmtException if the loadStandardGdeClasses() or the 
     *      loadNonStandardGdeClasses() fails.
     */
    public void loadGdeClasses() throws GDEException {
        // Load the standard classes.
        loadStandardGdeClasses();
        
        // After loading the standard classes, the method will create a cache
        // of the standard interfaces that will be used in the GDEWizard. 
        // (Currently, only the TroubleTicketValue interface will be present
        // in the standard list).
        String[] stdClassesArr = getStandardClasses();
        for(int i = 0; i < stdClassesArr.length; ++i) {
            try {
                Class stdClass = Class.forName(stdClassesArr[i], 
                                    false, gdeClassLoader);
                standardTypeLst.add(stdClass);
            } catch (ClassNotFoundException ex) {
                throw new GDEClassMngmtException("Failed to load the " +
                        "Standard Class '" + stdClassesArr[i] + "'. The required" +
                        "jar file may not be present in the GDE folder.", ex);
            }
        }        
        // Load the non standard classes.
        loadNonStandardGdeClasses();
    }
    
    /**
     * Loads the specified class and checks if it can be assigned to one of the
     * standard GDE classes. If so, it returns true. If the method fails to load
     * the class, using the loadClass method, it throws a GDEException.
     * 
     */
    public boolean isAssignableToStdInterface(
                        String className) throws GDEException {        
        Class cls = loadClass(className);
        return isAssignableToStandardClass(cls);
    }      
    
    /**
     * loads the specified class using the GDE class loader. If the class loader
     * is able to load the class, it returns the class object. Otherwise it
     * throws a GDEException. The GDE Class loader should already have loaded
     * the class before this method is invoked.
     */
    public Class loadClass(String className) throws GDEException {
        try {
            return gdeClassLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new GDEClassMngmtException("The specified class '" + 
                    className + "' is not found in any jars in the" +
                    "GDE Folder", ex);
        } catch (Throwable ex) {
            throw new GDEClassMngmtException("The specified class '" +
                    className + "' cannot be loaded from the GDE Folder", ex);
        }                
    }
            
    /**
     * This method returns the collection containing the list of Class objects
     * that represent the standard classes/ interfaces of the GDE application.
     * This method will return a valid value only after the loadGdeClasses() 
     * method is invoked.
     */
    public Collection getStandardTypeList() { return this.standardTypeLst; }
    
    /**
     * This method returns the collection containing the list of class objects
     * that are assignable to any of the standard classes/interfaces. This 
     * method will return a valid value only after the loadGdeClasses() method
     * is invoked.
     */
    public Collection getAssignableTypeList() { return this.assignableTypeLst; }
    
    /**
     * Currently, this function returns an array containing the class names
     * which are required for the GDE processing. This can be obtained from 
     * a configuration file instead of being hardcoded here. 
     *
     */
    protected String[] getStandardClasses() {
        return new String[] { "javax.oss.trouble.TroubleTicketValue" };
    }
    
    /**
     * loads the standard GDE Classes. Currently, the gde classes are loaded
     * from the jars - oss_common_spec_1.0.jar, oss_qos_spec-1.0.jar and
     * oss_trouble_ticket_spec-1.0.jar. These are the core jar files defined
     * by the OSS/J specification for trouble ticketing and QOS. It is based on
     * these classes that the class loader will load the remaining classes from
     * other jar files in the gde folder.
     * 
     * @throws com.sun.tthub.gde.util.GDEClassMngmtException if an error occurs
     *      while loading the classes from the jars specified above. This 
     *      exception will be thrown even if these jar files are not present 
     *      in the GDEFolder.
     */
    protected void loadStandardGdeClasses() throws GDEException {        
        for(int i = 0; i < STANDARD_JARS.length; ++i) {
            loadClassesFromJarFile(STANDARD_JARS[i], true);
        }
    }
    
    protected void loadNonStandardGdeClasses()  throws GDEException {
        GDEFolderManager mngr = new GDEFolderManager();
        Collection coll = mngr.loadJarFilesFromGdeFolder();
        // eliminate the standard jar files, as the classes from the
        // standard jar files will be loaded before this call, using the
        // loadStandardGdeClasses method of this class.
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            String jarFileName = ((File) it.next()).getName();
            if(isStandardJar(jarFileName))
                continue;
            loadClassesFromJarFile(jarFileName, false);
        }
    }
    
    public void loadClassesFromJarFile(String jarName) throws GDEException {
        loadClassesFromJarFile(jarName, false);
    }
    
    /**
     * This function can be used to load the classes from a jar file, when a 
     * new jar is added to the GDE Folder. In other cases, clients should not
     * use this method.
     */
    public void loadClassesFromJarFile(String jarName, 
                                boolean isStandardJar) throws GDEException {
        String jarFileName = gdeFolderName + "/lib/" + jarName;         
        
        // Initialize the JarFileResources class of the GDEClassLoader with
        // the name of the standard jar file. This will load the classes of
        // the jar file from the GDE folder.
        JarResources jarFileResources = gdeClassLoader.getJarResources();
        jarFileResources.setJarFileName(jarFileName);
        jarFileResources.init();

        JarFileManager mngr = new JarFileManager(new File(jarFileName));
        Collection coll = mngr.loadClassesForJar();

        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            String className = (String) it.next();
            try {
                Class cls = gdeClassLoader.loadClass(className);
                if(isStandardJar)
                    continue;
                if(isAssignableToStandardClass(cls))
                    assignableTypeLst.add(cls);
            } catch (ClassNotFoundException ex) {
            } catch (NoClassDefFoundError ex) {
                // ignore this error as it may be caused since the dependent
                // classes may not be available
                // can be handled later.
            } catch (Throwable ex) {
                // ignore any other exception.
            }
        }                    
    }
    
    private boolean isAssignableToStandardClass(Class cls) {
        for(Iterator it = standardTypeLst.iterator(); it.hasNext(); ) {
            Class stdClass = (Class) it.next();
            if(stdClass.isAssignableFrom(cls))
                return true;
        }
        return false;
    }
    
    private boolean isStandardJar(String fileName) {
        for(int i = 0; i < STANDARD_JARS.length; ++i) {
            if(STANDARD_JARS[i].equals(fileName))
                return true;            
        }
        return false;
    }    
}
