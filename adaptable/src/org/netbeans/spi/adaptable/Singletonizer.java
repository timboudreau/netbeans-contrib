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

package org.netbeans.spi.adaptable;

import java.util.TooManyListenersException;

/** Allows implementation of adaptors based on singletonizer pattern.
 * @see Adaptors
 */
public interface Singletonizer {
    /** Checks whether the class is right now supported - e.g. whether
     * its methods should be served.
     *
     * @param obj the represented object that the query applies to
     * @param c the class its method is being called
     * @return true if call is allowed, false if not
     */
    public boolean isEnabled (Object obj, Class c);

    /** Invokes a method of given class on provided represented object.
     * @param obj the represented object that is making the call
     * @param method method that was called on that object
     * @param args the arguments to the method
     */
    public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) throws Throwable;

    /** Allows the infrastructure to register a change listener on this
     * singletonizer. Whenever the set of supported classes changes,
     * the singletonizer is supposed to call the listener to notify all
     * Adaptables backed by this Singletonizer to update its state.
     * <p>
     * Usual implementation is:
     * <pre>
     * class MySingletonizer implements Singletonizer {
     *     private SingletonizerListener listener;
     *
     *     public synchronized void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException {
     *         if (this.listener != null) {
     *             throw new TooManyListenersException ();
     *         }
     *         this.listener = listener;
     *     }
     *     public synchronized void removeSingletonizerListener (SingletonizerListener listener) {
     *         if (this.listener == listener) {
     *             this.listener = null;
     *         }
     *     }
     * }
     * </pre>
     *
     * @param listener the listener to attache
     * @exception TooManyListenersException if there already is listener (no need to support more than one)
     */
    public void addSingletonizerListener (SingletonizerListener listener) throws TooManyListenersException;
    
    /** Removes registered listener.
     * @see #addSingletonizerListener
     */
    public void removeSingletonizerListener (SingletonizerListener listener);
}
