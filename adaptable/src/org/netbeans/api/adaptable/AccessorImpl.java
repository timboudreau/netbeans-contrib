/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.adaptable;

import java.util.Set;
import org.netbeans.spi.adaptable.SingletonizerEvent;

/** Implementation of accessor for friend access to hidden features
 * of the API.
 *
 * @author Jaroslav Tulach
 */
final class AccessorImpl extends org.netbeans.modules.adaptable.Accessor {
    
    protected Adaptor createAspectProvider(org.netbeans.modules.adaptable.ProviderImpl impl, Object data) {
        return new Adaptor (impl, data);
    }

    protected Object getData(Adaptor adaptor) {
        return adaptor.data;
    }

    protected org.netbeans.modules.adaptable.ProviderImpl getProviderImpl(Adaptor adaptor) {
        return adaptor.impl;
    }

    protected AdaptableEvent createEvent(Adaptable source, Set<Class> affected) {
        return new AdaptableEvent(source, affected);
    }

    protected Object getAffectedObject(SingletonizerEvent ev) {
        assert false;
        return null;
    }

    protected Set<Class> getAffectedClasses(SingletonizerEvent ev) {
        assert false;
        return null;
    }
}
