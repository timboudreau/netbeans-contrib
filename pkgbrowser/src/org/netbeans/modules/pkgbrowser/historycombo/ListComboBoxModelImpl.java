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
