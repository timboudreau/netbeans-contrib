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
