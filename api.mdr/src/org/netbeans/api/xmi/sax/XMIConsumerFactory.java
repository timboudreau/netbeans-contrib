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
