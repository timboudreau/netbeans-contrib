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

package org.netbeans.spi.jackpot;

import org.netbeans.api.jackpot.Query;
import org.openide.filesystems.FileObject;

/**
 * The interface Jackpot uses to request Query instances from script
 * engines.  
*
 * @author Tom Ball
 */
public interface QueryProvider {
    
    /**
     * Returns whether this QueryProvider can return a Query instance which
     * is associated with this script.  This method only determines whether 
     * creating a script is possible, such as that it recognizes the file
     * type, but does not guarantee that no exceptions are thrown creating
     * the Query.  
     * 
     * @param script the file to test
     * @return true if a Query can be created from this script.
     */
    boolean hasQuery(FileObject script);
    
    /**
     * Return the Jackpot Query associated with a specified script.  
     * 
     * @param script the script from which a Query class is defined.
     * @param queryDescription a short description of the query, as shown in the 
     *                         Jackpot UI.  This string is useful when reporting
     *                         any errors creating or executing the query.
     * @return a Query instance associated with this script.
     * @throws Exception if there are any problems creating the query.
     */
    Query getQuery(FileObject script, String queryDescription) throws Exception;
}
