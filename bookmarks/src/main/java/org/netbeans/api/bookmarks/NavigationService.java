/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    
    /**
     * For a given top component this method clears its navigation state.
     */
    public abstract void resetNavigation(TopComponent tc);
    
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
