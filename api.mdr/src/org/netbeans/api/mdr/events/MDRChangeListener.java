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

import java.util.EventListener;

/** Listener interface that allows repository clients to be notified of any changes in
 * the repository after they occur. Object needs to implement this interface in order to be able
 * to register for receiving MDR change notifications. The {@link #change} method of this
 * interface is called asynchronously after the change is performed. If you need to get
 * notified of the changes also before they occur, you may rather wish to implement
 * {@link MDRPreChangeListener} interface.
 *
 * @author Martin Matula
 */
public interface MDRChangeListener extends EventListener {
    /** This method gets called after a repository change is performed. This method
     * is called asynchronously.
     * If a listener implements {@link MDRPreChangeListener} which is a descedant
     * of this interface, the event object passed to this method must be the same 
     * instance as the event object previously passed to the corresponding
     * {@link MDRPreChangeListener#plannedChange} method call of the listener.<p>
     * Any run-time exception thrown by the implementation of this method should
     * not affect the events dispatching (i.e. it should be ignored by the event source).
     *
     * @param e Object describing the performed change.
     */    
    public void change(MDRChangeEvent e);
}
