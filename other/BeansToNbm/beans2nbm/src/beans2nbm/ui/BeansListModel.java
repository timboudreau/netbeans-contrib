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
