/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.registry;

import java.rmi.server.*;
import java.text.MessageFormat;

import org.openide.nodes.*;

/**
 *
 * @author  mryzl
 */

public class ServiceItem extends Object implements Comparable, Node.Cookie {

    /** A format for toString() method. */
    public static final String FMT_NAME = "{0}[class={1}]"; // NOI18N

    /** Name of the service. */
    private String name;

    /** Class of the service. */
    private Class clazz;

    /** Creates new ServiceItem. */
    public ServiceItem(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /** Getter for name.
    * @return name
    */
    public String getName() {
        return name;
    }

    /** Getter for class.
    * @return class
    */
    public Class getServiceClass() {
        return clazz;
    }

    /** Get class annotation - codebase where the class was downloaded from.
    * @return annotation
    */
    public String getClassAnnotation() {
        Class clazz;
        if ((clazz = getServiceClass()) != null) {
            String ca = RMIClassLoader.getClassAnnotation(clazz);
            if (ca == null) {
                try {
                    java.security.ProtectionDomain pd = clazz.getProtectionDomain();
                    ca = pd.getCodeSource().getLocation().toString();
                } catch (SecurityException ex) {
                    // prohibited by SM
                }
            }
            return ca;
        }
        return null;
    }

    /** Equals.
    * @return true if names and classes are equal.
    */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof ServiceItem)) {
            ServiceItem item = (ServiceItem) obj;
            if (item.getName().equals(name)) {
                return (clazz == null) ? item.getServiceClass() == null : clazz.equals(item.getServiceClass());
            }
        }
        return false;
    }

    /** toString
    */
    public String toString() {
        return MessageFormat.format(FMT_NAME, new Object[] { getName(), getServiceClass()});
    }

    public int compareTo(final java.lang.Object p1) {
        return ((ServiceItem)p1).getName().compareTo(getName());
    }
}

/*
* <<Log>>
*  5    Gandalf-post-FCS1.3.1.0     3/20/00  Martin Ryzl     localization
*  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  3    Gandalf   1.2         8/30/99  Martin Ryzl     saving corrected
*  2    Gandalf   1.1         8/27/99  Martin Ryzl     equals changed
*  1    Gandalf   1.0         8/27/99  Martin Ryzl     
* $ 
*/ 
