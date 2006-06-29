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

package org.netbeans.modules.adaptable;

import java.util.Set;
import org.netbeans.api.adaptable.*;
import org.netbeans.spi.adaptable.SingletonizerEvent;

/** Class that allows "friend" calls to api package.
 *
 * @author Jaroslav Tulach
 */
public abstract class Accessor {
    /** instance to make calls to api package */
    public static Accessor API;
    /** spi part of the accessor */
    public static Accessor SPI;

    static {
        // forces initialization of class Adaptor that initializes
        // field API
        Class c = Adaptor.class;
        try {
            Class.forName (c.getName (), true, c.getClassLoader ());
        } catch (Exception ex) {
            // swallow
        }
        //org.netbeans.api.adaptable.Adaptor.init ();
        assert API != null : "We have to initilialize the API field"; // NOI18N

        c = SingletonizerEvent.class;
        try {
            Class.forName (c.getName (), true, c.getClassLoader ());
        } catch (Exception ex) {
            // swallow
        }
        //org.netbeans.api.adaptable.Adaptor.init ();
        assert SPI != null : "We have to initilialize the SPI field"; // NOI18N
    }
 
    /**
     * Creates new instance of Adaptor
     * @param impl the impl to pass to the provider
     */
    public abstract Adaptor createAspectProvider (ProviderImpl impl, Object data);

    /** creates the AdaptableEvent.
     */
    public abstract AdaptableEvent createEvent(Adaptable source, Set<Class> affected);
    
    /** Gets the associated data */
    public abstract Object getData (Adaptor adaptor);
    /** Gets associated provider */
    public abstract ProviderImpl getProviderImpl (Adaptor adaptor);

    /** Gets affected object from an event.
     */
    public abstract Object getAffectedObject(SingletonizerEvent ev);

    /** Gets set of affected classes from the event.
     */
    public abstract Set<Class> getAffectedClasses(SingletonizerEvent ev);
}
