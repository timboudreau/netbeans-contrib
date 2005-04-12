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

import javax.swing.event.ChangeListener;

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
    public <T> T lookup (Class<T> what);
    
    /** Attaches listener to the Adaptable to be notified when a change
     * in the results returned from the lookup method happens.
     */
    public void addChangeListener (ChangeListener l);
    
    /** Removes the listener.
     */
    public void removeChangeListener (ChangeListener l);
}
