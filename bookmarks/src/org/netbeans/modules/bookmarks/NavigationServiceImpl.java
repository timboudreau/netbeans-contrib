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
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;
import javax.naming.*;

import org.openide.ErrorManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import org.netbeans.api.bookmarks.*;

/**
 * Implementation of the NavigationService.
 * @author David Strupl
 */
public class NavigationServiceImpl extends NavigationService implements PropertyChangeListener {
    
    /** 
     * Maps TopComponent --> List (of NavigationEvent)
     * Stores all NavigationEvents in this service.
     * PENDING: the lists should have limited lenght (memory leak)
     */
    private HashMap navigationEvents;
    
    /**
     * Maps TopComponent --> NavigationEvent 
     * Keeps track of current navigation event for the TopComponent.
     */
    private HashMap currentNavigationState;
    
    /**
     * Default constructor adds itself as a listener to
     * the TopComponent.Registry.
     */
    public NavigationServiceImpl() {
        navigationEvents = new HashMap();
        currentNavigationState = new HashMap();
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
    }
    
    /**
     * Navigation events are stored in the NavigationService and allow
     * for the user to use back/forward navigation actions. The navigation
     * events are not stored persistently and are valid only for current
     * session. Each navigation event is bound to a top component so each
     * top component is responsible for creation of navagion events if
     * it wants to use the backward/forward navigation buttons. Please note
     * that the TopComponent has to call this method when it starts and 
     * also always when the top component changes its content. The
     * navigation will use the callback function of NavigationEvent
     * restoreState to call back to the top component after the user
     * invokes the forward/backward buttons.
     * @see NavigationEvent
     */
    public void storeNavigationEvent(NavigationEvent ev) {
        TopComponent tc = ev.getTopComponent();
        List list = (List)navigationEvents.get(tc);
        if (list == null) {
            list = new ArrayList();
            navigationEvents.put(tc, list);
        }
        currentNavigationState.put(tc, ev);
        list.add(ev);
        firePropertyChange(NAVIGATION_PROPERTY, null, null);
    }
    
    /**
     * This method is called when the user invokes forward action from
     * the GUI. If canNavigateForward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public void forward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        NavigationEvent current = (NavigationEvent)currentNavigationState.get(tc);
        if ((list == null) || (current == null)) {
            return;
        }
        int index = list.indexOf(current);
        if (index + 1 >= list.size()) {
            return;
        }
        NavigationEvent ev = (NavigationEvent)list.get(index+1);
        currentNavigationState.put(tc, ev);
        ev.restoreState();
        firePropertyChange(NAVIGATION_PROPERTY, null, null);
    }
    
    /**
     * This method is called when the user invokes backward action from
     * the GUI. If canNavigateBackward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public void backward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        NavigationEvent current = (NavigationEvent)currentNavigationState.get(tc);
        if ((list == null) || (current == null)) {
            return;
        }
        int index = list.indexOf(current);
        if (index - 1 < 0) {
            return;
        }
        NavigationEvent ev = (NavigationEvent)list.get(index-1);
        currentNavigationState.put(tc, ev);
        ev.restoreState();
        firePropertyChange(NAVIGATION_PROPERTY, null, null);
    }
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public boolean canNavigateBackward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        NavigationEvent current = (NavigationEvent)currentNavigationState.get(tc);
        if ((list == null) || (current == null)) {
            return false;
        }
        int index = list.indexOf(current);
        return index > 0;
    }
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public boolean canNavigateForward() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        List list = (List)navigationEvents.get(tc);
        NavigationEvent current = (NavigationEvent)currentNavigationState.get(tc);
        if ((list == null) || (current == null)) {
            return false;
        }
        int index = list.indexOf(current);
        return index+1 < list.size();
    }

    /**
     * We are registered with TopComponent.Registry as propertyChange
     * listener. When the activated top component is changed we
     * recompute the state of our navigation controls.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (NavigationService.getDefault() != this) {
            // this can happen e.g. when the module is uninstalled
            WindowManager.getDefault().getRegistry().removePropertyChangeListener(this);
            return;
        }
        if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
            firePropertyChange(NAVIGATION_PROPERTY, null, null);
        }
    }
}
