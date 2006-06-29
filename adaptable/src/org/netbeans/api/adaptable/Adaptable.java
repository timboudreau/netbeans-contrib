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

package org.netbeans.api.adaptable;

/** Generic adaptable interface that represents object that can be
 * seen in various ways. For example by calling
 * <pre>
 * String s = adatable.lookup(String.class);
 * </pre>
 * one can get view of the object as a string
 *
 * @author Jaroslav Tulach
 */
public interface Adaptable {
    /** Tries to look at the adatable object as an instance of what.
     * @param what the class one requests
     * @return instance of the class or null if such view is not possible
     */
    public <T> T lookup (Class<T>what);
    
    /** Attaches listener to the Adaptable to be notified when a change
     * in the results returned from the lookup method happens.
     */
    public void addAdaptableListener (AdaptableListener l);
    
    /** Removes the listener.
     */
    public void removeAdaptableListener (AdaptableListener l);
}
