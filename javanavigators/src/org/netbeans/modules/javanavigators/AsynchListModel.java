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
 */
package org.netbeans.modules.javanavigators;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataListener;
import org.netbeans.misc.diff.Change;
import org.netbeans.misc.diff.Diff;

/**
 * A thread-safe ListModel which is backed by a typed java.util.List.  To change
 * its contents, call <code>setContents()</code>.  The actual update of the
 * backing storage is asynchronous.
 *
 * Uses the list diffs library to diff lists.
 *
 * @author Tim Boudreau
 */
public final class AsynchListModel <T extends Object> implements GenerifiedListModel <T> {
    //This list will only ever be modified on the AWT Event Thread
    private final List <T> contents = new ArrayList <T> ();
    private Comparator<T> comparator;
    
    public AsynchListModel() {
    }
    
    public AsynchListModel(Comparator <T> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Get the entire contents as an unmodifiable list.
     */
    public List <T> getContents() {
        return Collections.<T>unmodifiableList(contents);
    }
    
    /**
     * Set a comparator used to sort the contents.
     */
    public void setComparator (Comparator <T> c) {
        if (c != this.comparator) {
            this.comparator = c;
            if (!contents.isEmpty()) {
                List <T> nue = new ArrayList <T> (contents);
                if (comparator != null) {
                    Collections.sort (nue, comparator);
                    Diff <T> diff = Diff.<T>create(contents, nue);
                    updateOnEventQueue (diff);
                }
            }
        }
    }
    
    public Comparator <T> getComparator() {
        return comparator;
    }
    
    /**
     * Convenience typed getter since alas javax.swing.ListModel is not
     * genericized.
     *
     * @return the content of the model at the specified index
     */
    public T get (int index) {
        return contents.get(index);
    }
    
    /**
     * Set the contents of this model.  Note that this method 
     * <i>does not immediately update the model</i>.  The actual change to the
     * java.util.List storage that backs this model will be invokeLatered
     * on the event thread.
     */
    public void setContents (final List <T> nue, boolean replace) {
        if (this.contents.isEmpty() != nue.isEmpty()) {
            replace = true;
        }
        if (!replace) {
            //XXX check if defensive copy of contents is really needed.
            Diff <T> diff = Diff.<T>create (this.contents, 
                    nue);
            updateOnEventQueue (diff);
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    int sz = AsynchListModel.this.contents.size();
                    AsynchListModel.this.contents.clear();
                    AsynchListModel.this.contents.addAll (nue);
                    if (comparator != null) {
                        Collections.sort (AsynchListModel.this.contents, comparator);
                    }
                    ListDataEvent evt = new ListDataEvent (AsynchListModel.this,
                            ListDataEvent.CONTENTS_CHANGED,
                            0, Math.max (sz, nue.size()));
                    ListDataListener[] l = (ListDataListener[]) listeners.toArray (
                            new ListDataListener[0]);
                    for (int i = 0; i < l.length; i++) {
                        l[i].contentsChanged(evt);
                    }
                }
            };
            EventQueue.invokeLater (r);
        }
    }
    
    public int getSize() {
        return contents.size();
    }

    public Object getElementAt(int index) {
        return index >= 0 && index < contents.size() ? 
            contents.get(index) : null;
    }

    private List <ListDataListener> listeners = 
            Collections.<ListDataListener>synchronizedList (
            new LinkedList <ListDataListener> ());
    
    public void addListDataListener(ListDataListener l) {
        listeners.add (l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove (l);
    }
    
    public boolean isActive() {
        return !listeners.isEmpty();
    }
    
    public void fire (Diff <T> diff) {
        updateOnEventQueue (diff);
    }
    
    private void updateOnEventQueue (Diff <T> diff) {
        EventQueue.invokeLater (new Firer(diff));
    }
    
    private final class Firer implements Runnable {
        private final Diff <T> diff;
        Firer (Diff <T> diff) {
            this.diff = diff;
        }
        
        public void run() {
            contents.clear();
            contents.addAll(diff.getNew());
            if (comparator != null) {
                Collections.sort (contents, comparator);
            }
            List <Change> changes = (List<Change>) diff.getChanges();
            List <ListDataEvent> events = 
                    new ArrayList <ListDataEvent> (changes.size());
            
            //FIXME - loops here could be optimized
            for (Change change : changes) {
                ListDataEvent evt = new ListDataEvent (AsynchListModel.this, 
                        change.getType(), change.getStart(), change.getEnd());
                events.add (evt);
            }
            if (!events.isEmpty()) {
                fire (events);
            }
            if (Boolean.getBoolean("in.asynchmodel.unit.test")) { //NOI18N
                synchronized (AsynchListModel.this) {
                    AsynchListModel.this.notifyAll();
                }
            }
        }
    }
    
    private void fire (List <ListDataEvent> events) {
        ListDataListener[] l = listeners.toArray(new ListDataListener[0]);
        for (int i = 0; i < l.length; i++) {
            for (ListDataEvent event : events) {
                switch (event.getType()) {
                    case ListDataEvent.CONTENTS_CHANGED :
                        l[i].contentsChanged(event);
                        break;
                    case ListDataEvent.INTERVAL_ADDED :
                        l[i].intervalAdded(event);
                        break;
                    case ListDataEvent.INTERVAL_REMOVED :
                        l[i].intervalRemoved(event);
                        break;
                    default :
                        throw new AssertionError();
                }
            }
        }
    }
}
