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
import org.openide.util.Lookup;

import javax.swing.event.*;

/**
 * This is the main API class for the backward/forward navigation feature. 
 * The default
 * instance of the service is returned by NavigationService.getDefault().
 * You should use this instance to access service provided by this module.
 * This class contains methods used by the GUI classes of the bookmarks
 * module - so you can call the same functionality programatically.
 * In the very rare case you would like to alter the behaviour of this
 * module you can crate a subclass of this classs and register it in
 * the META-INF/services lookup.
 * @author David Strupl
 */
public abstract class NavigationService {

    /**
     * We register the change listeners in this list.
     */
    private EventListenerList listenerList = new EventListenerList();
    
    /** 
     * Cache for the event - the event is the same every time,
     * no need to create it more times than once.
     */
    private ChangeEvent changeEvent = null;
    
    /**
     * This is the preferred way to access the singleton instance of
     * this service. The implementation calls the default lookup
     * to get an instance. If you want to supply your own version of
     * the service please register the instance in META-INF/services
     * lookup.
     */
    public static NavigationService getDefault() {
        return (NavigationService)Lookup.getDefault().lookup(NavigationService.class);
    }
    
    /** In the very rare case you are providing your own subclass
     * of BookmarkService yo will need this constructor. It should be
     * called only by subclasses - if you need an instance of this class
     * please check the method getDefault().
     */
    protected NavigationService() {
    }
    
    // ---------------------------------------------------
    
    /**
     * BookmarkService is change event source. You can register
     * your listener using this method.
     */
    public final void addChangeListener(ChangeListener cl) {
        listenerList.add(ChangeListener.class, cl);
    }
    
    /**
     * BookmarkService is change event source. You can deregister
     * your listener using this method.
     */
    public final void removeChangeListener(ChangeListener cl) {
        listenerList.remove(ChangeListener.class, cl);
    }
    
    /**
     * This method is usefull only by subclasses of this class. It fires the
     * event to all listeners registered using addChangeListener method.
     */
    protected final void fireChangeEvent() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
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
    public abstract void storeNavigationEvent(NavigationEvent ev);
    
    // ------------------------------------------------------
    
    /**
     * This method is called when the user invokes forward action from
     * the GUI. If canNavigateForward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public abstract void forward();
    
    /**
     * This method is called when the user invokes backward action from
     * the GUI. If canNavigateBackward() returns true this method should
     * bring the activated top component to the state it stored with
     * storeNavigationEvent().
     */
    public abstract void backward();
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public abstract boolean canNavigateForward();
    
    /**
     * This value returned by this method corresponds to the state of
     * the controls for navigation. The returned value depends on
     * the activated TopComponent (see org.openide.windows.WindowManager.getRegistry())
     * and the internal state of the NavigationService with respect
     * to this TopComponent.
     */
    public abstract boolean canNavigateBackward();
    
}
