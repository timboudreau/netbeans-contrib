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

import javax.jmi.reflect.RefAssociation;
import javax.jmi.reflect.RefObject;

/** MDR Event used for representing association-related events.
 * Any association event is described the way that there is one association end
 * taken as "fixed". This means that the change will be described from the perspective
 * of this association end. For changes to the association caused by modifying
 * a reference or live collection returned from a getter for an association end this behavior
 * is very intuitive - the fixed association end will be the exposed end of
 * the reference (or the fixed element will be the element provided to getter for
 * association end). For direct association proxy operations (i.e. remove or add),
 * the event object looks as follows:<ul>
 * <li> remove - the fixed element is any of the two linked elements, old element
 * represents the element on the other side of the link, new element is null</li>
 * <li> add - the fixed element is any of the two linked elements, old element
 * is null, the element on the other side of the link is represented as the new 
 * element.</li>
 *</ul>
 *
 * @author Martin Matula
 */
public class AssociationEvent extends MDRChangeEvent {
    /** Value indicating unspecified position */
    public static final int POSITION_NONE = -1;
    
    /** Bitmask for event types related to operations with associations.
     * Can be used for registering a listener for listening to all association-related events.
     */
    public static final int EVENTMASK_ASSOCIATION = 0x401FFFF;

    /** Identifier of the event type indicating that one end of an existing
     * association link is to be/was modified.
     */
    public static final int EVENT_ASSOCIATION_SET = 0x4010001;
    /** Identifier of the event type indicating that a new link is to be/was added.
     */
    public static final int EVENT_ASSOCIATION_ADD = 0x4010002;
    /** Identifier of the event type indicating that an existing link is to be/was removed.
     */
    public static final int EVENT_ASSOCIATION_REMOVE = 0x4010004;
    
    // element on fixed association end
    private final RefObject fixedElement;
    // name of fixed association end
    private final String endName;
    // original element on the non-fixed end
    private final RefObject oldElement;
    // new element on the non-fixed end
    private final RefObject newElement;
    // position of the affected element on the non-fixed end
    private final int position;
    
    /** Creates new AssociationEvent object.
     * @param source Event source (association proxy object).
     * @param type Event type.
     * @param fixedElement Element of the affected link on the side of fixed association end.
     * @param endName Name of association end on the fixed side of the link.
     * @param oldElement Original element of the affected link on the non-fixed side of the link or null.
     * @param newElement New element of the affected link on the non-fixed side of the link or null.
     * @param position Position of the element on the non-fixed side of the affected link or {@link #POSITION_NONE} if not applicable.
     */
    public AssociationEvent(RefAssociation source, int type, RefObject fixedElement, String endName, RefObject oldElement, RefObject newElement, int position) {
        super(source, type);
        this.fixedElement = fixedElement;
        this.endName = endName;
        this.oldElement = oldElement;
        this.newElement = newElement;
        this.position = position;
    }

    /** Returns the element on the fixed side of the affected link.
     * @return Link element.
     */
    public RefObject getFixedElement() {
        return fixedElement;
    }
    
    /** Returns name of the fixed association end.
     * @return Association end name.
     */
    public String getEndName() {
        return endName;
    }
    
    /** Returns the original element on the non-fixed side of the affected link.
     * @return Link element or null.
     */
    public RefObject getOldElement() {
        return oldElement;
    }
    
    /** Returns the new element on the non-fixed side of the affected link.
     * @return Link element or null.
     */
    public RefObject getNewElement() {
        return newElement;
    }
    
    /** Returns position of the non-fixed element of the affected link.
     * @return Position of element or {@link #POSITION_NONE}.
     */
    public int getPosition() {
        return position;
    }
}
