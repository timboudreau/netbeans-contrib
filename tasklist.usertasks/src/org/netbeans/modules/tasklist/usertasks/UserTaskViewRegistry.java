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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.usertasks.util.AWTThread;
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
    @AWTThread
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
