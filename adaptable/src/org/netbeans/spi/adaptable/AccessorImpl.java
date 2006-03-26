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

package org.netbeans.spi.adaptable;

import java.util.Set;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.AdaptableEvent;
import org.netbeans.api.adaptable.Adaptor;

/** Implementation of accessor for friend access to hidden features
 * of the API.
 *
 * @author Jaroslav Tulach
 */
final class AccessorImpl extends org.netbeans.modules.adaptable.Accessor {
    
    protected Adaptor createAspectProvider(org.netbeans.modules.adaptable.ProviderImpl impl, Object data) {
        assert false;
        return null;
    }

    protected Object getData(Adaptor adaptor) {
        assert false;
        return null;
    }

    protected org.netbeans.modules.adaptable.ProviderImpl getProviderImpl(Adaptor adaptor) {
        assert false;
        return null;
    }

    protected AdaptableEvent createEvent(Adaptable source, Set<Class> affected) {
        assert false;
        return null;
    }

    protected Object getAffectedObject(SingletonizerEvent ev) {
        return ev.obj;
    }

    protected Set<Class> getAffectedClasses(SingletonizerEvent ev) {
        return ev.affected;
    }
}
