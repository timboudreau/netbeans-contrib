/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ByteListModel.java
 *
 * Created on April 27, 2004, 1:14 AM
 */

package org.netbeans.modules.hexedit;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple list model that proxies the arrays of bytes for columns in a HexTableModel
 *
 * @author  Tim Boudreau
 */
class ByteListModel implements ListModel, TableModelListener {
    private HexTableModel mdl;
    /** Creates a new instance of ByteListModel */
    public ByteListModel(HexTableModel mdl) {
        this.mdl = mdl;
    }
    
    public synchronized void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }    
    
    public Object getElementAt(int row) {
        byte[] b = mdl.getBytesForRow(row);
        char[] c = new char[b.length];
        for (int i=0; i < b.length; i++) {
            if (b[i] < 128) {
                if ('\n' != (char) c[i]) {
                    c[i] = (char) b[i];
                } else {
                    c[i] = ' ';
                }
            } else {
                c[i] = '.';
            }
        }
        return new String (c);
    }    
    
    public int getSize() {
        return mdl.getRowCount();
    }
    
    public synchronized void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
    public void tableChanged (TableModelEvent tme) {
        fire();
    }
    
    private void fire() {
        ListDataEvent lme = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
        List lis = null;
        synchronized (this) {
            lis = new ArrayList(listeners);
        }
        for (Iterator i=lis.iterator(); i.hasNext();) {
            ListDataListener l = (ListDataListener) i.next();
            l.contentsChanged(lme);
        }
    }
    
    private List listeners = new ArrayList();
    
}
