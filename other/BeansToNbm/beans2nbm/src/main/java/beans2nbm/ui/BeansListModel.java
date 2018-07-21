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
