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


/** Abstract ancestor of all context events. It adds {@link #getContext}
 * method returning source context of the event.
 *
 * @author  David Konecny
 */
public abstract class ContextEvent extends java.util.EventObject {

    ContextEvent(Context source) {
        super(source);
    }

    /**
     * Context on which this event happened.
     *
     * @return context on which the modification was done
     */
    public Context getContext() {
        return (Context)source;
    }

    public String toString() {
        return "[context="+getContext()+"] " + super.toString(); // NOI18N
    }

}
