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
