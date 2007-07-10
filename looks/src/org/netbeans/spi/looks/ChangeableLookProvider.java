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
import javax.swing.event.ChangeListener;

/** Interface for finding a Looks for given represented object. To create
 * a {@link LookSelector} implement this interface and call
 * {@link Selectors#selector( ChangeableLookProvider )}. LookSelectors created
 * from this interface are allowed to change the content. To do so you have
 * to implement the {@link #addChangeListener} method and fire the changes
 * on the registered listener. Typical implementation of the method
 * would look like:<P>
 * <CODE>
 *  if ( this.listener != null ) {
        throw new TooManyListenersException();
    }
    else {
        this.listener = listener;
        }
 * </CODE>
 * <P>
 * and fire method would look like:<P>
 * <CODE>
 * listener.stateChanged( new ChangeEvent( this ) );
 * </CODE>
 * <P>
 * Notice that it is usually not necessary to create a list of listeners and
 * rather throw TooManyListeners exception if there would be more than one
 * listener registered. The reason is that reusing one instance of
 * ChangeableLookProvider would result in two LookSelectors with the same
 * behavior i.e. you may rather want to reuse the LookSelector than the
 * Provider.
 *
 *
 * @see LookProvider for creating LooksSelectors which have fixed content
 *
 * @author Petr Hrebejk
 */
public interface ChangeableLookProvider {
    
    /** Finds all suitable Looks for given key. The key for a representedObject
     * is obtained by call to the (@link #getKey} method.
     * @param key The key we want to find available looks for.
     * @return Enumeration of available Looks
     */
    public Enumeration getLooksForKey( Object key );
    
    /** Returns key for given object. Make sure you return the same key for
     * given object during whole lifecycle of the LookSelector. This means
     * that changes in the content are changes in the enumerations of Looks
     * for given key not a change in a key which is returned for given object.
     * (It also means that it is not good idea to base the implementation
     * of this method on an attribute of an object which can change. Changes
     * in representation of such attributes should generally be handled by Looks
     * rather than LookSelectors/Providers)
     * @param representedObject The represented object we want to find key for.
     * @return Key for given represented object. Returning <CODE>null</CODE>
     *         instead of a key will result in returning empty Enumeration 
     *         from the LookSelector.
     */
    public Object getKeyForObject( Object representedObject );
    
    /** Registers listener for changes in the Provider's content. Call
     * stateChanged on the registered listener to notify it about change in
     * the Look enumerations returned for keys.
     *
     * @see #getKeyForObject for info about correct behavior of the Provider
     * @param listener The listenet which should be notified when change in
     *        the Provider's content occurs.
     * @throws TooManyListenersException May be thrown when more than one
     *         listener is registered.
     */
    public void addChangeListener( ChangeListener listener ) throws TooManyListenersException;
            
            
}