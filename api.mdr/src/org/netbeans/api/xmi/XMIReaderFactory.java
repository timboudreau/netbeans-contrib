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
package org.netbeans.api.xmi;

import org.openide.util.*;
import java.util.*;

/** Factory class for creating instances of XMI reader objects.
 *
 * @author  Martin Matula
 */
public abstract class XMIReaderFactory {
    /** Returns the default instance of XMIReaderFactory.
     * @return Default XMI reader factory.
     */    
    public static XMIReaderFactory getDefault() {
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(XMIReaderFactory.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (XMIReaderFactory) result.allInstances().iterator().next() : null);
    }
    
    /** Creates new instance of XMIReader.
     * @return New instance of XMIReader.
     */
    public abstract XMIReader createXMIReader();
    
    /** Creates new instance of XMIReader configured using the passed configuration object.
     * The configuration object should not be used by XMIReader directly 
     * (i.e. the real configuration of the returned XMIReader should be a copy of the passed 
     * configuraion object).
     * @param configuration Configuration of the XMIReader instance to be created.
     */
    public abstract XMIReader createXMIReader(XMIInputConfig configuration);
}
