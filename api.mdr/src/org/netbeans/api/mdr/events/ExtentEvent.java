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

import java.util.Collection;
import javax.jmi.reflect.RefObject;

/** MDR event used for representing events related to lifecycle of package extents in a repository
 * (creation and deletion of extents).
 *
 * @author Martin Matula
 */
public class ExtentEvent extends MDRChangeEvent {
    /** Bitmask representing all the events related to extent lifecycle. */
    public static final int EVENTMASK_EXTENT = 0x801FFFF;

    /** Identifier for event type that indicates creation of a new extent in a repository. */
    public static final int EVENT_EXTENT_CREATE = 0x8010001;
    /** Identifier for event type that indicates an extent is to be/was deleted. */
    public static final int EVENT_EXTENT_DELETE = 0x8010002;

    private final String extentName;
    private final RefObject metaObject;
    private final Collection existingExtents;
    private final boolean empty;
    
    /** Creates new ExtentEvent object.
     * @param source Event source (MDRepository in case of extent creation, RefPackage in case of extent deletion).
     * @param type Event type.
     * @param extentName Name of created/deleted extent.
     * @param metaObject Metaobject of created/deleted extent or null.
     * @param existingExtents Immutable collection of existing extents that were provided to be used for clustering or null.
     */    
    public ExtentEvent (Object source, int type, String extentName, RefObject metaObject, Collection existingExtents) {
        this (source,  type, extentName, metaObject, existingExtents, true);
    }
    
    public ExtentEvent(Object source, int type, String extentName, RefObject metaObject, Collection existingExtents, boolean empty) {
        super(source, type);
        this.extentName = extentName;
        this.metaObject = metaObject;
        this.existingExtents = existingExtents;
        this.empty = empty;
    }
    
    /** Returns name of deleted/created extent.
     * @return Name of created/deleted extent.
     */    
    public String getExtentName() {
        return extentName;
    }
    
    /** Returns metaobject of created/deleted extent.
     * @return metaobject of created/deleted extent or null.
     */    
    public RefObject getMetaObject() {
        return metaObject;
    }
    
    /** Returns collection of existing extents provided to be used for package clustering.
     * @return Collection of extents or null.
     */    
    public Collection getExistingExtents() {
        return existingExtents;
    }
    
    /** Returns true in the case that the created package is empty.
     *  The false value means that the extent was created by partition mounting.
     *  @return boolean
     */
    public boolean isEmpty () {
        return this.empty;
    }
}
