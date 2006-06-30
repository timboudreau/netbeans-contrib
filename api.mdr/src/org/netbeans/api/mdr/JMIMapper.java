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
 */
package org.netbeans.api.mdr;

import org.openide.util.Lookup;
import javax.jmi.reflect.RefBaseObject;
import java.io.IOException;
import java.util.Collection;

/** JMI mapping utility. Generates JMI interfaces for a given metamodel.
 * Use {@link #getDefault} method to obtain the default instance.
 *
 * @author Martin Matula
 */
public abstract class JMIMapper {

    /** Generates JMI interfaces for the specified object
     * and the objects contained in it.
     * @param sf Implementation of {@link JMIStreamFactory} interface.
     * @param object Top-level object for interface generation. There are two possible kinds of objects that can be passed:
     * <ul>
     *    <li>RefObject (instance) - interfaces for this instance together with interfaces for all transitively contained instances are generated.</li>
     *    <li>RefPackage (package extent) - interfaces for all instances contained transitively in this package extent are generated.</li>
     * </ul>
     * @throws IOException I/O error during interfaces generation.
     */    
    public abstract void generate(JMIStreamFactory sf, RefBaseObject object) throws IOException;
    
    /** Returns the default JMI mapping utility in the system
     * @return default JMI mapping utility
     */
    public static synchronized JMIMapper getDefault() {
        // [PENDING] simple lookup should be used once the lookup is fixed (currently it does not preserve order)
        Lookup.Result result = Lookup.getDefault().lookup(
            new Lookup.Template(JMIMapper.class)
        );
        Collection instances = result.allInstances();
        return (instances.size() > 0 ? (JMIMapper) result.allInstances().iterator().next() : null);
    }
}

