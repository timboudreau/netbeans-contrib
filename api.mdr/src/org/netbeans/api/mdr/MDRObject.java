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
package org.netbeans.api.mdr;

import org.netbeans.api.mdr.events.MDRChangeSource;
import javax.jmi.reflect.RefBaseObject;

/** Interface implemented by each repository object (besides the standard JMI interfaces).
 * Adds a method for navigating to the parent repository and methods for registering event
 * listeners.
 *
 * @author Martin Matula
 */
public interface MDRObject extends MDRChangeSource, RefBaseObject {
    /** Returns a reference to the home repository of this object.
     * @return home repository of this object.
     */    
    public MDRepository repository();
}
