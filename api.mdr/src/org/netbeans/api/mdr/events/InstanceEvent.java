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

import java.util.List;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefFeatured;

/** MDR Event used for representing events related to lifecycle of class instances
 * (creation and deletion of instances). As this event contains attribute pointing
 * to the affected (created/deleted) instance, it will need to be subclassed
 * in repository implementation. This is because each implementation has to send
 * the same event object to both pre-change and change events. In case of the
 * instance create event, the actual instance is not known by the time the pre-change
 * event is fired (so the event object is created providing null for this attribute),
 * however the created instance has to be referenced from the event object passed to the
 * change event. As the implementation is not allow to send a different instance of
 * event object to the change event, it needs to be able to set the instance property
 * of the original event object. For this purpose a subclass of this event object
 * containing a package protected setter for the instance attribute should be used.
 * Note that for this purpose the instance property is defined as protected instead
 * of private final.
 *
 * @author Martin Matula
 */
public class InstanceEvent extends MDRChangeEvent {
    /** Bitmask representing all the events related to instance lifecycle. */
    public static final int EVENTMASK_INSTANCE = 0x201FFFF;

    /** Identifier for event type that indicates creation of a new instance of a class. */
    public static final int EVENT_INSTANCE_CREATE = 0x2010001;
    /** Identifier for event type that indicates an instance of a class is to be/was deleted. */
    public static final int EVENT_INSTANCE_DELETE = 0x2010002;

    private final List arguments;
    protected RefObject instance;

    /** Creates new InstanceEvent object.
     * @param source Event source (class proxy in case of instance creation, instance in case of instance deletion).
     * @param type Event type.
     * @param arguments Immutable list of initial attribute values of the new instance (null in case of instance deletion).
     * @param instance The created/deleted instance or null (in case of pre-change event indicating instance creation).
     */
    public InstanceEvent(RefFeatured source, int type, List arguments, RefObject instance) {
        super(source, type);
        this.arguments = arguments;
        this.instance = instance;
    }
    
    /** Returns list of initial values for instance attributes (only applicable for instance creation - otherwise null).
     * @return List of initial attribute values.
     */    
    public List getArguments() {
        return arguments;
    }
    
    /** Returns the created/deleted instance.
     * @return Created/deleted instance.
     */    
    public RefObject getInstance() {
        return instance;
    }
}
