/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.xmi;

import org.openide.util.*;
import java.util.*;

/** Factory for XMI writers.
 *
 * @author Martin Matula
 */
public abstract class XMIWriterFactory {
    /** Returns the default instance of XMIWriterFactory.
     * @return Default XMI writer factory.
     */    
    public static XMIWriterFactory getDefault() {
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(XMIWriterFactory.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (XMIWriterFactory) result.allInstances().iterator().next() : null);
    }
    
    /** Creates new instance of XMIWriter.
     * @return New instance of XMIWriter.
     */
    public abstract XMIWriter createXMIWriter();

    /** Creates new instance of XMIWriter configured using the passed configuration object.
     * The configuration object should not be used by XMIWriter directly 
     * (i.e. the real configuration of the returned XMIWriter should be a copy of the passed 
     * configuraion object).
     * @param configuration Configuration of the XMIWriter instance to be created.
     */
    public abstract XMIWriter createXMIWriter(XMIOutputConfig configuration);
}
