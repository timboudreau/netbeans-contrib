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
package org.netbeans.modules.pkgbrowser.historycombo;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.misc.diff.Change;
import org.netbeans.misc.diff.ListDiff;

/**
 *
 * @author Timothy Boudreau
 */
public class ProxyComboBoxModel implements ComboBoxModel {
    private final List listeners = new LinkedList();
    private final LDL monitor = new LDL();
  private Object selected = null;
    private ListComboBoxModel model = null;
    
    /** Creates a new instance of ProxyComboBoxModel */
    public ProxyComboBoxModel() {
    }
    
    public ProxyComboBoxModel(ListComboBoxModel model) {
        this.model = model;
    }

    public void setSelectedItem(Object anItem) {
        String old = selected == null ? "" : (String) selected;
        String nue = anItem == null ? "" : (String) anItem;
        selected = anItem;
        if (!old.equals(nue)) {
            selected = nue;
            if (!listeners.isEmpty()) {
                ListDataEvent e = new ListDataEvent (this, 
                        ListDataEvent.CONTENTS_CHANGED, -1, -1);
                monitor.fire(e);
            }
        }
    }

    public Object getSelectedItem() {
        return selected;
    }

    public int getSize() {
        return model == null ? 0 : model.getSize();
    }

    public Object getElementAt(int index) {
        if (model == null) {
            throw new ArrayIndexOutOfBoundsException (Integer.toString(index));
        }
        return model.getElementAt (index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add (l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove (l);
    }
    
    private ListComboBoxModel getModel() {
        return model;
    }
    
    public void setUnderlyingModel (ListComboBoxModel mdl) {
        ListComboBoxModel old = (ListComboBoxModel) model;
        this.model = mdl;
        if (old != null) {
            old.removeListDataListener(monitor);
        }
        if (mdl != null) {
            model.addListDataListener(monitor);
        }
        if (!listeners.isEmpty()) {
            List oldContents = old == null ? Collections.EMPTY_LIST : old.getData();
            List newContents = model == null ? Collections.EMPTY_LIST : model.getData();
            List /* <Change> */ changes = ListDiff.createDiff (oldContents, newContents).getChanges();
            if (!changes.isEmpty()) {
                for (Iterator i = changes.iterator(); i.hasNext();) {
                    Change change = (Change) i.next();
                    ListDataEvent lde = new ListDataEvent (this, change.getType(), change.getStart(), change.getEnd());
                    monitor.fire(lde);
                }
            }
        }
    }
    
    private class LDL implements ListDataListener {
        public void intervalAdded(ListDataEvent e) {
            fire (e);
        }

        public void intervalRemoved(ListDataEvent e) {
            fire (e);
        }

        public void contentsChanged(ListDataEvent e) {
            fire (e);
        }
        
        private void fire (ListDataEvent e) {
            for (Iterator i = listeners.iterator(); i.hasNext();) {
                ListDataListener l = (ListDataListener) i.next();
                ListDataEvent nue = new ListDataEvent (ProxyComboBoxModel.this,
                        e.getType(), e.getIndex0(), e.getIndex1());

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
    }
}
