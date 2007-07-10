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

package org.netbeans.spi.looks;

import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.netbeans.modules.looks.SelectorImpl;

/** Interface for finding a Look for given object.
 *
 * @author Petr Hrebejk,  Jaroslav Tulach
 */
public final class LookSelector {

    /** Looks provider */
    SelectorImpl impl;

    /** Package private, all selectors are created using factory methods
     */
    LookSelector( SelectorImpl impl ) {
        this.impl = impl;
        try {
            impl.setLookSelector( this );
        }
        catch ( TooManyListenersException e ) {            
            throw new IllegalStateException( "SelectorImpl " + impl + " used for more than one selector" );
        }
    }
    
    /** Finds all suitable Looks for given object
     * @param representedObject The object we want to find available looks for.
     * @return Enumeration of available Looks
     */
    public Enumeration getLooks( Object representedObject ) {
        return impl.getLooks( representedObject );
    }

    // Package private methods -------------------------------------------------
    
    /** Returns the implementation from the modules package. This method is
     * used by the Accessor when registering listeners for given LookSelector
     */    
    SelectorImpl getImpl() {
        return impl;
    }
            
}
