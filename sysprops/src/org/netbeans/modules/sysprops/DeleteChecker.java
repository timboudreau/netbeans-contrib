/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Michael Ruflin
 */
package org.netbeans.modules.sysprops;

import java.util.Properties;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

/** 
 * Checks if a Property in the System Properties is deletable or not.
 * Note: Current method does only check, if the Property is one of the 
 * default Properties listed in System.getProperties() in javadoc (1.3).
 * Maybe it would be better trying to delete each, to see if it's recreated.
 *
 * @author Michael Ruflin
 * @version 0.3
 */
public class DeleteChecker extends Object {
    
    /** Array of all default SystemProperties (taken from System.getProperties()
     * in the javadoc of jdk1.3.
     */
    public final static String[] defaultPropertyNames = 
    {                                 "java.version",
                                      "java.vendor",
                                      "java.vendor.url",
                                      "java.home",
                                      "java.vm.specification.version",
                                      "java.vm.specification.vendor",
                                      "java.vm.specification.name",
                                      "java.vm.version",
                                      "java.vm.vendor",
                                      "java.vm.name",
                                      "java.specification.version",
                                      "java.specification.vendor",
                                      "java.specification.name",
                                      "java.class.version",
                                      "java.class.path",
                                      "java.ext.dirs",
                                      "os.name",
                                      "os.arch",
                                      "os.version",
                                      "file.separator",
                                      "path.separator",
                                      "line.separator",
                                      "user.name",
                                      "user.home",
                                      "user.dir"
    };

    
    /** Checks if a Property is a Java-Default Property, if yes it cannot be deleted.
     *
     * Note: It would be better try deleting it and if it still is in the SystemProperties
     * it isn't deletable, because some other properties aren't deletable too.
     * 
     * @param name the full name of the property.
     * @return true if it is not a defaultProperty and so can be deleted.
     */
    public final static boolean isDeletable(String name) {
        for (int x=0; x < defaultPropertyNames.length; x++) {
            if (name.equals(defaultPropertyNames[x])) {
                return false;
            }
        }
        return true;
    }

    
    
    /* public static Set defaultProperties = new TreeSet(); */
    
    /**
     * Initialize the properties Set.
     */
    /* static{
        updateDefaultProperties();
    }
    */
    
    
    /*
    public static void updateDefaultProperties() {
        Properties defaultProps = System.getProperties();
        System.setProperties(new Properties());
        Properties p2 = System.getProperties();
        Enumeration e = p2.propertyNames();
        defaultProperties = new TreeSet();
        while(e.hasMoreElements()) {
            defaultProperties.add(e.nextElement());
        }
        System.setProperties(defaultProps);
        System.out.println("Properties count: " + defaultProps.size());
        System.out.println(defaultProps);
        System.out.println("Nondeletable: " + defaultProperties.size());
    }*/
    
    
    /*
    public final static boolean isDeletable(String name) {
        return ! defaultProperties.contains(name);
        //return true;
    }*/
}