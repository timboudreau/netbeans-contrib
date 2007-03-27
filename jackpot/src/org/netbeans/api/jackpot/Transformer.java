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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.jackpot;

import org.netbeans.api.java.source.WorkingCopy;

/**
 * A Transformer is applied to a a set of source files (defined by a <code>
 * JavaSource</code> instance) to transform query matches into new source code.  
 *
 * @author Tom Ball
 */
public interface Transformer extends Query {
    
    /**
     * Returns the WorkingCopy attached to this transformation.
     * 
     * @return the WorkingCopy instance, or null if not attached.
     */
    WorkingCopy getWorkingCopy();

    /**
     * Called by a transformer when it discovers code it cannot transform, such
     * as an unmapped method when converting one class to another.  This is
     * different than QueryException, which is thrown when the query or 
     * transformer cannot continue.
     * 
     * @param message a description of the translation failure.
     */
    void transformationFailure(String message);
}
