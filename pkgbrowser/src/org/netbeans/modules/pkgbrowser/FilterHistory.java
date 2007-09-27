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
