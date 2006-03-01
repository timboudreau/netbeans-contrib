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

package org.netbeans.modules.pkgbrowser;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Timothy Boudreau
 */
public class FilterHistory implements ComboBoxModel {
    private final List history = new ArrayList();
    private static final String SEP = "#&%^"; //NOI18N

    private final String name;
    private FilterHistory(String name, String contents) {
        this.name = name;
        parseContents(contents);
    }
    
    String getName() {
        return name;
    }
    
    private static final int MAX = 15;
    private String lastAdded = null;
    public void add (String s) {
        if (s == null || "".equals(s)) {
            return;
        }
        s = s.trim();
        if (lastAdded.equals(s)) {
            return;
        } else if (lastAdded != null && s.startsWith(lastAdded) && lastAdded.length() == s.length() - 1) {
            history.remove(lastAdded);
        }
        history.remove(s);
        history.add (0, s);
        if (history.size() > MAX) {
            history.remove(history.size() - 1);
        }
        fire();
        lastAdded = s;
    }
    
    public void remove (String s) {
        if (lastAdded != null && lastAdded.equals(s)) {
            history.remove(s);
            lastAdded = null;
        }
    }
    
    public List match (String filter) {
        ArrayList result = new ArrayList (history.size());
        boolean tailFilter = filter.startsWith ("*"); //NOI18N
        if (tailFilter) {
            StringBuffer sb = new StringBuffer(filter);
            while (sb.length() > 0 && sb.charAt(0) == '*') { //NOI18N
                sb.deleteCharAt(0);
            }
            if (sb.length() == 0) {
                result.addAll (history);
                return result;
            }
        }
        for (Iterator i = history.iterator(); i.hasNext();) {
            String check = (String) i.next();
            boolean match = tailFilter ? check.endsWith(filter) : check.startsWith(filter);
            if (match && !check.equals(filter)) {
                result.add(check);
            }
        }
        return result;
    }
    
    private static Map filters = new HashMap();
    public static FilterHistory getFilterHistory(String name) {
        FilterHistory result = null;
        Reference r = (Reference) filters.get(name);
        if (r != null) {
            result = (FilterHistory) r.get();
        }
        if (result == null) {
            Preferences prefs = Preferences.userNodeForPackage(FilterHistory.class);
            String prefString = prefs.get(name, "");
            result = new FilterHistory (name, prefString);
            filters.put (name, new WeakReference(result));
        }
        return result;
    }

    private void parseContents(String contents) {
        if (contents.length() > 0) {
            for (StringTokenizer tok = new StringTokenizer(contents, SEP); tok.hasMoreTokens();) {
                history.add (tok.nextToken());
            }
        }
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (Iterator i = history.iterator(); i.hasNext();) {
            String item = (String) i.next();
            result.append (item);
            result.append (SEP);
        }
        return result.toString();
    }
    
    public int hashCode() {
        return name.hashCode() * 37;
    }
    
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FilterHistory) {
            return ((FilterHistory)o).getName().equals(getName());
        } else {
            return false;
        }
    }
    
    void save() {
        Preferences prefs = Preferences.userNodeForPackage(FilterHistory.class);
        prefs.put(getName(), toString());
    }

    private String selItem = null;
    public void setSelectedItem(Object anItem) {
//        add (anItem.toString());
        selItem = (String) anItem;
    }

    public Object getSelectedItem() {
        return selItem == null ? history.size() > 0 ? history.get(0) : "" : selItem;
    }
    
    public void commit() {
        if (selItem != null && !"".equals(selItem.trim())) {
            add (selItem);
            save();
        }
    }

    public int getSize() {
        return history.size();
    }

    public Object getElementAt(int index) {
        return history.get(index);
    }

    private List listeners = new ArrayList();
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove (l);
    }
    
    private void fire() {
        if (listeners.size() > 0) {
            ListDataEvent evt = new ListDataEvent (this, 
                    ListDataEvent.CONTENTS_CHANGED, 0, history.size());
            for (Iterator i = listeners.iterator(); i.hasNext();) {
                ListDataListener l = (ListDataListener) i.next();
                l.contentsChanged(evt);
            }
        }
    }
}
