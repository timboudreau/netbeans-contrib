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

import java.io.OutputStream;
import javax.jmi.reflect.RefPackage;
import org.openide.util.Lookup;
import java.util.Collection;

/** DTD generation utility. Provides a way to generate
 * DTDs for a given metamodel. Use {@link #getDefault} method to get the default instance.
 *
 * @author Martin Matula
 */
public abstract class DTDProducer {
    /** Generates a DTD for a given extent into a provided output stream.
     * @param stream Output stream to generate the DTD into.
     * @param extent Extent that the DTD should be generated from.
     */    
    public abstract void generate(OutputStream stream, RefPackage extent);
    
    /** Returns a default DTDProducer instance.
     * @return Default DTD producer.
     */    
    public static DTDProducer getDefault() {
        // [PENDING] simple lookup should be used once the lookup is fixed (currently it does not preserve order)
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(DTDProducer.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (DTDProducer) result.allInstances().iterator().next() : null);
    }
}

