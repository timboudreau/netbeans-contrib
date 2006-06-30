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
