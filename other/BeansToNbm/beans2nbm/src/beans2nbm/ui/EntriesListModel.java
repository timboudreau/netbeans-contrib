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
import java.util.jar.JarEntry;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Tim Boudreau
 */
public class EntriesListModel implements ListModel {
    private final JarInfo info;
    /** Creates a new instance of EntriesListModel */
    public EntriesListModel(JarInfo info) {
        this.info = info;
    }

    public int getSize() {
        return info.getEntries().size();
    }

    public Object getElementAt(int index) {
        return new BeanItem(((JarEntry) info.getEntries().get(index)).toString());
    }

    public void addListDataListener(ListDataListener l) {
    }

    public void removeListDataListener(ListDataListener l) {
    }
}
