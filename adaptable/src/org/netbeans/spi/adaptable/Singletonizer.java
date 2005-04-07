/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.adaptable;

import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;

/** Allows implementation of adaptors based on singletonizer pattern.
 * @see Adaptors
 */
public interface Singletonizer {
    /** Checks whether the class is right now supported - e.g. whether 
     * its methods should be served.
     *
     * @param c the class its method is being called
     * @return true if call is allowed, false if not
     */
    public boolean isEnabled (Class c);

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
     *     private ChangeListener listener;
     *
     *     public synchronized void addChangeListener (ChangeListener listener) throws TooManyListenersException {
     *         if (this.listener != null) {
     *             throw new TooManyListenersException ();
     *         }
     *         this.listener = listener;
     *     }
     *     public synchronized void removeChangeListener (ChangeListener listener) {
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
    public void addChangeListener (ChangeListener listener) throws TooManyListenersException;
    
    /** Removes registered listener.
     * @see #addChangeListener
     */
    public void removeChangeListener (ChangeListener listener);
}
