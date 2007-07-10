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

/** Interface for finding a Looks for given represented object. To create
 * a {@link LookSelector} implement this interface and call
 * {@link Selectors#selector( LookProvider )}. LookSelectors created
 * from this interface are fixed (i.e. changing content of the selector
 * is impossible.
 *
 * @see ChangeableLookProvider for creating LooksSelectors which may change
 *      it's content.
 *
 * @author Petr Hrebejk
 */
public interface LookProvider {

    /** Finds all suitable Looks given represented object
     * @param representedObject The object we want to find available looks for.
     * @return Enumeration of available Looks
     */
    public abstract Enumeration getLooksForObject( Object representedObject );
    
}
