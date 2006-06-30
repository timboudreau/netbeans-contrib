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
