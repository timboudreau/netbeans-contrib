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
import org.netbeans.api.mdr.MDRepository;

/** Class representing MDR events related to start, commit and
 * rollback of write transactions.
 *
 * @author <a href="mailto:hkrug@rationalizer.com">Holger Krug</a>.
 */
public class TransactionEvent extends MDRChangeEvent {
    
    /** Bitmask representing all event types related to write transactions */
    public static final int EVENTMASK_TRANSACTION = 0x011FFFF;
    
    /** Event type indicating that a write transaction has been started. */
    public static final int EVENT_TRANSACTION_START = 0x0110001;
    /** Event type indicating that a write transaction has been ended. */
    public static final int EVENT_TRANSACTION_END = 0x0110002;

    /** Creates new <code>TransactionEvent</code> instance. 
     * @param source the event source, an instance of <code>MDRepository</code>.
     * @param type the event type.
     */
    public TransactionEvent(MDRepository source, int type) {
        super(source, type);
    }
}
