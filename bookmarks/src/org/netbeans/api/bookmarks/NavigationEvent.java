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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.api.bookmarks;

import org.openide.windows.TopComponent;

/**
 * NavigationEvent is used by the NavigationService to
 * capture the state of a TopComponent. The NavigationService
 * is able to navigate backward/forward using a call to
 * restoreState on individual NavigationEvents. The default
 * implementation of the NavigationService does not persist
 * the events so this class does not have to be persistent.
 * WARNING: the persistence requirement on those classes
 * can be changed in future releases.
 * @author David Strupl
 */
public interface NavigationEvent {
    /**
     * When the user invokes backward/forward navigation this
     * method is called to bring the TopComponents to the remembered
     * state.
     * @returns true if the state of the TopComponent was successfully restored
     *   or false if the state cannot be changed
     */
    public boolean restoreState();
    
    /**
     * Each NavigationEvent is bound to one TopComponent. The
     * value returned by this method should not change between
     * successive invocations of getTopComponent (this property
     * of the event should be immutable). 
     * @returns the top component this event belongs to
     */
    public TopComponent getTopComponent();
}
