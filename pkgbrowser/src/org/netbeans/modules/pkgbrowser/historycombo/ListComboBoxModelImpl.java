/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.pkgbrowser.historycombo;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.util.Mutex;

/**
 *
 * @author Timothy Boudreau
 */
public class ListComboBoxModelImpl implements ListComboBoxModel {
    private final List contents = Collections.synchronizedList(new LinkedList());
    private String key;
    public ListComboBoxModelImpl (String key) {
        this (Histories.get(key));
        this.key = key;
    }

    protected String getKey() {
        return key;
    }
    
    public ListComboBoxModelImpl (List data) {
        contents.addAll(data);
    }
    
    public List getData() {
        return Collections.unmodifiableList(contents);
    }

    public void setSelectedItem(Object anItem) {
        throw new UnsupportedOperationException();
    }

    public Object getSelectedItem() {
        throw new UnsupportedOperationException();
    }

    public int getSize() {
        return contents.size();
    }

    public Object getElementAt(int index) {
        return contents.get(index);
    }

    private List listeners = new LinkedList();
    public void addListDataListener(ListDataListener l) {
        listeners.add (l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    private String lastAdded = null;
    private static final int MAX = 20;
    public void add (String val) {
        if (lastAdded != null && lastAdded.startsWith (val)) {
            int ix = contents.indexOf (lastAdded);
            if (ix != -1) {
                contents.remove(ix);
                fire (new ListDataEvent (this, ListDataEvent.INTERVAL_REMOVED, ix, ix));
            }
            lastAdded = val;
        }
        int oldIndex = contents.indexOf(val);
        if (oldIndex != -1 && oldIndex != 0) {
            fire (new ListDataEvent (this, ListDataEvent.INTERVAL_REMOVED, oldIndex, oldIndex));
        }
        contents.add (0, val);
        if (oldIndex == 0) {
            fire (new ListDataEvent (this, ListDataEvent.INTERVAL_ADDED, 0, 0));
        } else if (oldIndex == 0) {
            fire (new ListDataEvent (this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
        }
        if (contents.size() > MAX) {
            contents.remove (MAX-1);
            fire (new ListDataEvent (this, ListDataEvent.INTERVAL_REMOVED, MAX-1, MAX-1));
        }
    }
    
    private void fire (final ListDataEvent e) {
        Mutex.EVENT.readAccess(new Runnable() {
           public void run() {
                for (Iterator i = listeners.iterator(); i.hasNext();) {
                    ListDataListener l = (ListDataListener) i.next();
                    ListDataEvent nue = new ListDataEvent (this, e.getType(), e.getIndex0(), e.getIndex1());
                    switch (e.getType()) {
                        case ListDataEvent.INTERVAL_ADDED :
                            l.intervalAdded(nue);
                            break;
                        case ListDataEvent.INTERVAL_REMOVED :
                            l.intervalRemoved(nue);
                            break;
                        case ListDataEvent.CONTENTS_CHANGED :
                            l.contentsChanged(nue);
                            break;
                        default :
                            throw new IllegalArgumentException (Integer.toString(e.getType()));
                    }
                }
           } 
        });
    }
}
