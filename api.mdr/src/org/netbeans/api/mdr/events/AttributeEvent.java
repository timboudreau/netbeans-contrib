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

import javax.jmi.reflect.RefFeatured;

/** Class represeting MDR events related to changes
 * of repository object (class/instance) attributes.
 *
 * @author Martin Matula
 */
public class AttributeEvent extends MDRChangeEvent {
    /** Value indicating unspecified position */
    public static final int POSITION_NONE = -1;
    
    /** Bitmask representing all event types related to instance attribute changes */
    public static final int EVENTMASK_ATTRIBUTE = 0x101FFFF;
    /** Bitmask representing all event types related to classifier attribute changes */
    public static final int EVENTMASK_CLASSATTR = 0x102FFFF;
    
    /** Event type indicating that a value of a single-valued instance attribute is to be/was changed or
     * a value element of a multi-valued instance attribute is to be/was changed.
     */
    public static final int EVENT_ATTRIBUTE_SET = 0x1010001;
    /** Event type indicating that a value element of a multi-valued instance attribute is to be/was added */
    public static final int EVENT_ATTRIBUTE_ADD = 0x1010002;
    /** Event type indicating that a value element of a multi-valued instance attribute is to be/was removed */
    public static final int EVENT_ATTRIBUTE_REMOVE = 0x1010004;

    /** Event type indicating that a value of a single-valued classifier attribute is to be/was changed or
     * a value element of a multi-valued classifier attribute is to be/was changed.
     */    
    public static final int EVENT_CLASSATTR_SET = 0x1020001;
    /** Event type indicating that a value element of a multi-valued classifier attribute is to be/was added
     */    
    public static final int EVENT_CLASSATTR_ADD = 0x1020002;
    /** Event type indicating that a value element of a multi-valued classifier attribute is to be/was removed
     */    
    public static final int EVENT_CLASSATTR_REMOVE = 0x1020004;

    private final String attrName;
    private final Object oldElement;
    private final Object newElement;
    private final int position;

    /** Creates new AttributeEvent instance. 
     * @param source Event source (class proxy or instance).
     * @param type Event type.
     * @param attrName Name of the attribute that was changed.
     * @param oldElement Original attribute value (for single-valued) or value element (for multi-valued attribute) or null if not applicable (e.g. in case of ADD event).
     * @param newElement New attribute value (for single-valued) or value element (for multi-valued attribute) or null if not applicable (e.g. in case of REMOVE event).
     * @param position Position of the affected element or POSITION_NONE if not applicable (e.g. in case of unordered multi-valued attributes).
     */
    public AttributeEvent(RefFeatured source, int type, String attrName, Object oldElement, Object newElement, int position) {
        super(source, type);
        this.attrName = attrName;
        this.oldElement = oldElement;
        this.newElement = newElement;
        this.position = position;
    }

    /** Returns name of an attribute affected by this event.
     * @return Attribute name.
     */    
    public String getAttributeName() {
        return attrName;
    }

    /** Returns original value of the element affected by this event or null if not applicable.
     * @return Original value of element.
     */    
    public Object getOldElement() {
        return oldElement;
    }

    /** Returns a new value of the element affected by this event or null if not applicable.
     * @return New element value.
     */    
    public Object getNewElement() {
        return newElement;
    }

    /** Returns original position of element affected by this event or {@link #POSITION_NONE} if not applicable.
     * @return Original position of affected element.
     */    
    public int getPosition() {
        return position;
    }
}
