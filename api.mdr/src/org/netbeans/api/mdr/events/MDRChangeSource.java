/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.mdr.events;

/** Event source interface implemented by all the repository
 * objects and MDRepository. Enables other objects
 * to register for listening to any repository events.
 * The objects that have to be registered for event
 * notifications need to implement {@link MDRChangeListener}
 * or {@link MDRPreChangeListener} interface.<p>
 *
 * <p>The repository distributes all the events recursively in the following way:
 * <ul>
 * <li>Events fired on an instance are also fired on the corresponding class proxy for this instance.
 * <li>Events fired on a class proxy are propagated to its immediate package proxy.
 * <li>Events fired on an association proxy are fired on all the instances affected by this event 
 * (e.g. instances added/removed from a link) and also it is fired on the immediate package proxy.
 * <li>Events fired on a package proxy are propagated to its immediate package proxy.
 * If the package proxy is outermost, the event is propagated to the MDRepository that contains
 * the proxy.
 * </ul>
 * Figure below shows where the
 * events are initially fired and how they propagate.
 * <img src="doc-files/eventForwardingChain.png" alt="Event sources and propagation"/>
 * </p>
 * 
 * <p>All the events are propagated recursively till they reach the repository object (e.g. each
 * instance event is as a result of propagation always fired on the instance itself,
 * on its class proxy, on its immediate package proxy, on all other package proxies containing
 * the immediate package proxy to the outermost package proxy and at the end on the repository containing
 * instance).</p>
 *
 * <p>In addition, any event is fired only once for each listener (so no matter how many objects 
 * on the event's propagation path is a listener registered on - e.g. on both
 * class proxy and its instances - it receives each notification only once per event).</p>
 *
 * <p>Listeners may be added specifically for certain events only by calling
 * {@link #addListener(MDRChangeListener, int) addListener(MDRChangeListener, int)}.
 * If instead {@link #addListener(MDRChangeListener) addListener(MDRChangeListener)}
 * is used for listener addition and, hence, no event mask is speficied, the listener
 * receives all events which are propagated to the source it is registered for.
 *
 * @author Martin Matula
 * @author <a href="mailto:hkrug@rationalizer.com">Holger Krug</a>.
 */
public interface MDRChangeSource {
    
    /** Registers a listener for receiving all event notifications.
     *
     *
     * @param listener Object that implements {@link MDRChangeListener} interface.
     */    
    public void addListener(MDRChangeListener listener);
    /** Registers a listener for receiving notifications about events the type
     *  of which matches <code>mask</code>.
     * @param listener Object that implements {@link MDRChangeListener} interface.
     * @param mask bitmask to be used in a call to {@link MDRChangeEvent#isOfType(int)}
     *    to filter events based on the mask. The masks are defined in {@link MDRChangeEvent}
     *    and the interfaces that extend it.
     * @see <a href="../../../../../constant-values.html">Constant Field Values</a>
     */    
    public void addListener(MDRChangeListener listener, int mask);
    /** Removes listener from the list of objects registered for events notifications.
     * @param listener Object that implements {@link MDRChangeListener} interface.
     */    
    public void removeListener(MDRChangeListener listener);
    /** Removes listener only for events of types matching <code>mask</code>.
     * If a listener is additionally registered for events of other types, it
     * proceeds listening on the remaining event types.
     * @param listener Object that implements {@link MDRChangeListener} interface.
     * @param mask determines the types of events the listener shall stop
     *   to listen on. The masks are defined in {@link MDRChangeEvent}
     *   and the interfaces that extend it.
     * @see <a href="../../../../../constant-values.html">Constant Field Values</a>
     */    
    public void removeListener(MDRChangeListener listener, int mask);
}
