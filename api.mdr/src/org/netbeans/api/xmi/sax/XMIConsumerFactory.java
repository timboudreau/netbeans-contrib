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

import org.netbeans.api.xmi.XMIInputConfig;
import org.openide.util.Lookup;
import java.util.Collection;

/** Factory for XMIConsumer instances.
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIConsumerFactory {
    /** Returns the default instance of XMIConsumerFactory.
     * @return Default XMI consumer factory.
     */    
    public static XMIConsumerFactory getDefault() {
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(XMIConsumerFactory.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (XMIConsumerFactory) result.allInstances().iterator().next() : null);
    }
    
    /** Creates new instance of XMIConsumer.
     * @return New instance of XMIConsumer.
     */
    public abstract XMIConsumer createXMIConsumer();
    
    /** Creates new instance of XMIConsumer configured using the passed configuration object.
     * The configuration object should not be used by XMIConsumer directly 
     * (i.e. the real configuration of the returned XMIConsumer should be a copy of the passed 
     * configuraion object).
     * @param configuration Configuration of the XMIConsumer instance to be created.
     */
    public abstract XMIConsumer createXMIConsumer(XMIInputConfig configuration);
}
