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

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.rmi.activation.settings.RMIActivationSettings;

/** List of children of a containing node.
 * Remember to document what your permitted keys are!
 *
 * @author mryzl, Jan Pokorsky
 */
public class ActivationBrowserChildren extends Children.Keys implements PropertyChangeListener {

    /** Refresh task. */
    private RequestProcessor.Task task = null;
    /** Refresh time */
    private int timeout;
    
    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    
    /**
    */
    public ActivationBrowserChildren() {
    }
    
    /** Schedule refresh task.
    */
    protected void scheduleRefreshTask(int millis) {
        if (task == null) {
            updateTimeout();
            task = RequestProcessor.getDefault().create(new RefreshTask());
            task.setPriority(Thread.MIN_PRIORITY);
        }
        task.schedule(millis);
        if (debug) System.err.println("ABChildren: Task resheduled: " + millis + " ms."); // NOI18N
    }
    
    /**
    */
    protected void addNotify () {
        scheduleRefreshTask(0);
        RMIActivationSettings.getDefault().addPropertyChangeListener(this);
    }
    
    /**
    */
    protected void removeNotify () {
        setKeys (Collections.EMPTY_SET);
        RMIActivationSettings.getDefault().removePropertyChangeListener(this);
    }
    
    /** Creates nodes. */
    protected Node[] createNodes (Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        ActivationSystemItem item = (ActivationSystemItem) key;
        return new Node[] { new ActivationSystemNode(item, new ActivationSystemChildren(item)) };
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (RMIActivationSettings.PROP_ACTIVATION_SYSTEM_ITEMS.equals(p1.getPropertyName())) {
//            scheduleRefreshTask(0);
            setKeys(RMIActivationSettings.getDefault().getActivationSystemItems());
        } else if (RMIActivationSettings.PROP_REFRESH_TIME.equals(p1.getPropertyName())) {
            updateTimeout();
            if (timeout > 0) scheduleRefreshTask(timeout);
            else if (task != null && task.isFinished()) {
                boolean cancel = task.cancel();
                if (debug) System.err.println("ABChildren: task canceled: " + cancel); // NOI18N
            }
        }
    }
    
    /** Sets refresh time for browser from system options.
     */
    private void updateTimeout() {
        timeout = RMIActivationSettings.getDefault().getRefreshTime();
    }
    
    /** Automatic refresh of all connected activation system browsers. */
    private class RefreshTask implements Runnable {

        public void run() {
            ActivationSystemItem[] items = RMIActivationSettings.getDefault().getActivationSystemItems();
            setKeys(items);
            for(int i = 0; i < items.length; i++) {
                items[i].updateActivationItems();
            }
            if (timeout > 0) scheduleRefreshTask(timeout);
        }
        
    }
    
}