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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Michael Ruflin, Jesse Glick
 */

package org.netbeans.modules.sysprops;

/**
 * Checks if a Property in the System Properties is deletable or not.
 * Note: Current method does only check, if the Property is one of the
 * default Properties listed in System.getProperties() in javadoc (1.3).
 * Maybe it would be better trying to delete each, to see if it's recreated.
 *
 * @author Michael Ruflin
 */
public class DeleteChecker extends Object {

    /**
     * Array of all default SystemProperties (taken from System.getProperties()
     * in the javadoc of jdk1.3).
     */
    public final static String[] defaultPropertyNames = {
        "java.version",
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
    
    /**
     * Checks if a Property is a Java-Default Property, if yes it cannot be deleted.
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
    
}
