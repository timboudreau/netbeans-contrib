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

package org.netbeans.api.registry;


/** An event from context indicating that binding was modified,
 * added or removed. The event intentionally does not pass value of
 * the object because in some situation it could result in unnecessary
 * creation of the object. The client receiving this event can retrieve
 * the value by regular Context methods. The {@link #getType}
 * can be used to distinguish type of the change.
 *
 * @author  David Konecny
 */
public final class BindingEvent extends ContextEvent {

    /** This event type is for added binding. */
    public static final int BINDING_ADDED = 1;

    /** This event type is for removed binding. */
    public static final int BINDING_REMOVED = 2;
    
    /** This event type is for modified binding. */
    public static final int BINDING_MODIFIED = 3;
    
    private String bindingName;
    private int type;

    BindingEvent(Context source, String bindingName, int type) {
        super(source);
        this.bindingName = bindingName;
        this.type = type;
    }

    /**
     * Name of the binding. It can be null what means
     * that concrete source of the change was not clear and that
     * client should reexamine whole context.
     *
     * @return binding name; can be null
     */
    public String getBindingName() {
        return bindingName;
    }
    
    public int getType() {
        return type;
    }

    public String toString() {
        return "BindingEvent: [bindingName="+bindingName+", type="+type+"] " + super.toString(); // NOI18N
    }
}
