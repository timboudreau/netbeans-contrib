/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
