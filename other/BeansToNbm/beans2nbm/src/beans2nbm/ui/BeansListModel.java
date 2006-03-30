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
package beans2nbm.ui;

import beans2nbm.gen.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Tim Boudreau
 */
public class BeansListModel implements ListModel {
    private final JarInfo info;
    /** Creates a new instance of BeansListModel */
    public BeansListModel(JarInfo info) {
        this.info = info;
    }

    public int getSize() {
        return info.getBeans().size();
    }

    public Object getElementAt(int index) {
        return new BeanItem(info.getBeans().get(index).toString());
    }
    
    private final List listeners = Collections.synchronizedList(new LinkedList ());
    public void addListDataListener(ListDataListener l) {
        listeners.add (l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
    public String getLikelyCodeName() {
        if (getSize() > 0) {
            BeanItem item = (BeanItem) getElementAt (0);
            return item.getPackageName();
        } else {
            return "com.foo.mycom";
        }
    }
    
    public void add (BeanItem item) {
        List l = info.getBeans();
        if (!l.contains(item.getPath())) {
            info.getBeans().add (item.getPath());
            ListDataEvent lde = new ListDataEvent (this, ListDataEvent.INTERVAL_ADDED, l.size(), l.size());
            ListDataListener[] ll = (ListDataListener[]) listeners.toArray (new ListDataListener[listeners.size()]);
            for (int i=0; i < ll.length; i++) {
                ll[i].intervalAdded(lde);
            }
        }
    }
    
    public void remove (BeanItem item) {
        List l = info.getBeans();
        int ix = l.indexOf(item.getPath());
        if (ix >= 0) {
            l.remove(ix);
            ListDataEvent lde = new ListDataEvent (this, ListDataEvent.INTERVAL_ADDED, ix, ix);
            ListDataListener[] ll = (ListDataListener[]) listeners.toArray (new ListDataListener[listeners.size()]);
            for (int i=0; i < ll.length; i++) {
                ll[i].intervalRemoved(lde);
            }
        }
    }
    
}
