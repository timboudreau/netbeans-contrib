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

import java.util.EventObject;

/** Root abstract class for all MDR events.
 * Every MDR event has a type uniquely identified by an integer.
 * The integers representing the event types are chosen in such a way, that
 * they can be uniquely combined into a bitmask without losing information
 * about which event types are represented in the bitmask.
 * This way the event listeners can filter the events using a bitmask.
 *
 * @author Martin Matula
 * @author <a href="mailto:hkrug@rationalizer.com">Holger Krug</a>.
 */
public abstract class MDRChangeEvent extends EventObject {
    /** Bitmask representing all possible repository event types.
     * Can be used for registering listener to receive all kinds of events.
     */
    public static final int EVENTMASK_ALL = 0x0FFFFFFF;
    
    /** Bitmask representing all event types which are initially fired on
     * associations. */
    public static final int EVENTMASK_ON_ASSOCIATION = AssociationEvent.EVENTMASK_ASSOCIATION;
    /** Bitmask representing all event types which are initially fired on
     * instance objects. */
    public static final int EVENTMASK_ON_INSTANCE = InstanceEvent.EVENT_INSTANCE_CREATE | AttributeEvent.EVENTMASK_ATTRIBUTE;
    /** Bitmask representing all event types which are initially fired on
     * class proxies. */
    public static final int EVENTMASK_ON_CLASS = InstanceEvent.EVENT_INSTANCE_DELETE | AttributeEvent.EVENTMASK_CLASSATTR;
    /** Bitmask representing all event types which are initially fired on
     * package proxies.
     *
     * <p><em>Note:</em> This bitmask is empty, because there are not
     * events which originate on packages. Packages receive only events
     * propagated from other objects. As a consequence this bitmask is
     * useless. It is nevertheless part of the API to achieve greater
     * uniformity of the API.</p>
     */
    public static final int EVENTMASK_ON_PACKAGE = 0x0000000;
    /** Bitmask representing all event types which are initially fired on
     * repositories. */
    public static final int EVENTMASK_ON_REPOSITORY = ExtentEvent.EVENTMASK_EXTENT | TransactionEvent.EVENTMASK_TRANSACTION;

    // event type
    private final int eventType;

    /** Creates a new instance of MDR event 
     * @param source Source object for this event.
     * @param type Number indicating type of this event.
     */
    public MDRChangeEvent(Object source, int type) {
        super(source);
        eventType = type;
    }

    /** Returns type of this event.
     * @return Number indicating type of this event.
     */    
    public int getType() {
        return eventType;
    }

    /** Returns <CODE>true</CODE> if the type of this event is contained in the provided 
     * bitmask.
     * @param mask Bitmask.
     * @return <CODE>true</CODE> - the type of this event is contained in the bitmask (i.e. type & mask == type)
     * <CODE>false</CODE> - the type of this event is not contained in the bitmask (i.e. type & mask < type)
     */    
    public boolean isOfType(int mask) {
        return ((eventType & mask) == eventType);
    }
}
