/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
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
     */
    public void restoreState();
    
    /**
     * Each NavigationEvent is bound to one TopComponent. The
     * value returned by this method should not change between
     * successive invocations of getTopComponent (this property
     * of the event should be immutable). 
     * @returns the top component this event belongs to
     */
    public TopComponent getTopComponent();
}
