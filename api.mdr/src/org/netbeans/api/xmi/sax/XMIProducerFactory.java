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
package org.netbeans.api.xmi.sax;

import org.netbeans.api.xmi.XMIOutputConfig;
import org.openide.util.Lookup;
import java.util.Collection;

/**
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIProducerFactory {
    /** Returns the default instance of XMIProducerFactory.
     * @return Default XMI producer factory.
     */    
    public static XMIProducerFactory getDefault() {
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(XMIProducerFactory.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (XMIProducerFactory) result.allInstances().iterator().next() : null);
    }
    
    /** Creates new instance of XMIProducer.
     * @return New instance of XMIProducer.
     */
    public abstract XMIProducer createXMIProducer();
    
    /** Creates new instance of XMIProducer configured using the passed configuration object.
     * The configuration object should not be used by XMIProducer directly 
     * (i.e. the real configuration of the returned XMIProducer should be a copy of the passed 
     * configuraion object).
     * @param configuration Configuration of the XMIProducer instance to be created.
     */
    public abstract XMIProducer createXMIProducer(XMIOutputConfig configuration);
}
