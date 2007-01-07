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

package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.usertasks.util.AWTThreadAnnotation;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Registry for all open UserTaskViews.
 *
 * @author tl
 */
public class UserTaskViewRegistry {
    private static UserTaskViewRegistry instance =
            new UserTaskViewRegistry();
    
    /**
     * Returns the only instance of this class.
     *
     * @return the instance.
     */
    public static UserTaskViewRegistry getInstance() {
        return instance;
    }
    
    private UserTaskView defview = null;
    
    /** 
     * Keeps track of all UserTaskViews. Access should be synchronized on
     * UserTaskView.class
     */
    private List<WeakReference<UserTaskView>> views = 
            new ArrayList<WeakReference<UserTaskView>>();
    
    private WeakReference<UserTaskView> lastActivated = null;
    private EventListenerList listenerList = new EventListenerList();
            
    /** 
     * Returns the view with the default task list. The view will be opened if
     * it was not.
     *
     * @return the default view or null if an error occured
     */
    @AWTThreadAnnotation
    public UserTaskView getDefault() {
	if (defview == null) {
            try {
                defview = new UserTaskView(
                        UserTaskView.getDefaultFile(), true);
                defview.showInMode();
            } catch (IOException ioe) {
                DialogDisplayer.getDefault().notify(new Message(
                    ioe, NotifyDescriptor.ERROR_MESSAGE));
            }
	}
	return defview;
    }

    /**
     * Returns all opened views.
     *
     * @return array of all opened views
     */
    public UserTaskView[] getAll() {
        synchronized(UserTaskView.class) {
            WeakReference[] r = this.views.toArray(
                new WeakReference[this.views.size()]);
            List<UserTaskView> views = new ArrayList<UserTaskView>();
            for (int i = 0; i < r.length; i++) {
                UserTaskView v = (UserTaskView) r[i].get();
                if (v != null)
                    views.add(v);
            }
            return views.toArray(new UserTaskView[views.size()]);
        }
    }
    
    /** 
     * Return the currently active user task view, or null
     *
     * @return current view
     */
    public UserTaskView getCurrent() {
        TopComponent activated = WindowManager.getDefault().
            getRegistry().getActivated();
        if (activated instanceof UserTaskView)
            return (UserTaskView) activated;
        else 
            return null;
    }    
    
    /**
     * Returns the last activated view.
     *
     * @return the view that was activated as the last one or null
     */
    public UserTaskView getLastActivated() {
        if (lastActivated == null)
            return null;
        UserTaskView v = (UserTaskView) lastActivated.get();
        if (v.isOpened())
            return v;
        else
            return null;
    }

    /** 
     * Locate a particular view showing the given list 
     * @return found view or null
     */
    public UserTaskView findView(FileObject file) {
 	Iterator it = views.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
	    UserTaskView tlv = (UserTaskView) wr.get();
            if (tlv != null && tlv.getFile() == file) 
                return tlv;
        }
        return null;
    }
    
    /**
     * Sets the last activated view.
     *
     * @param v the last activated view
     */
    public void setLastActivated(UserTaskView v) {
        lastActivated = new WeakReference<UserTaskView>(v);
    }

    /**
     * Registers a new view.
     *
     * @param v the view
     */
    public void viewOpened(UserTaskView v) {
        synchronized (UserTaskView.class) {
            views.add(new WeakReference<UserTaskView>(v));
        }
        fireChange();
    }
    
    /**
     * Sets default view.
     *
     * @param v the default view
     */
    public void setDefaultView(UserTaskView v) {
        defview = v;
    }
    
    /**
     * A view was closed.
     *
     * @param v the view
     */
    public void viewClosed(UserTaskView v) {
        if (defview == v)
            defview = null;
        
 	Iterator it = views.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
	    UserTaskView tlv = (UserTaskView) wr.get();
            if (tlv == v) {
                it.remove();
                break;
            }
        }
        fireChange();
    }
    
    /**
     * Adds a listener that will be modified if a view was closed or a new view
     * was opened.
     *
     * @param l a listener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a listener registered with addChangeListener.
     *
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Fires a change.
     */
    private void fireChange() {
        ChangeEvent event = null;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (event == null)
                    event = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }
    
    /** 
     * Creates a new instance of UserTaskViewRegistry 
     */
    private UserTaskViewRegistry() {
    }
}
