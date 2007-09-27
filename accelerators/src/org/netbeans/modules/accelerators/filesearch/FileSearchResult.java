/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.awt.Image;
import java.beans.BeanInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author Andrei Badea
 */
public class FileSearchResult {
    
    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.netbeans.modules.accelerators.filesearch"); // NOI18N
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);
    
    private Set items = new HashSet();
    private List listeners = new ArrayList();
    private boolean dirty;
    
    public void add(FileObject fo) {
        Item item = new Item(fo);
        synchronized (items) {
            items.add(item);
            dirty = true;
        }
    }
    
    public void remove(FileObject fo) {
        Item item = new Item(fo);
        synchronized (items) {
            items.remove(item);
            dirty = true;
        }
    }
    
    public void clear() {
        synchronized (items) {
            dirty = true;
            items.clear();
        }
    }
    
    /**
     * For tests.
     */
    FileObject[] getResult() {
        // XXX maybe should fire the listeners first
        synchronized (items) {
            FileObject[] result = new FileObject[items.size()];
            int j = 0;
            for (Iterator i = items.iterator(); i.hasNext(); j++) {
                result[j] = ((Item)i.next()).getFileObject();
            }
            return result;
        }
    }
    
    public Item[] getItems() {
        synchronized (this) {
            return (Item[])items.toArray(new Item[items.size()]);
        }
    }
    
    public int getSize() {
        synchronized (items) {
            // XXX maybe should fire the listeners first
            return items.size();
        }
    }
    
    public void finish() {
        fireListeners();
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    private void fireListeners() {
        boolean d = false;
        synchronized (items) {
            d = dirty;
        }
        if (!d) {
            return;
        }
        fireListenersReal();
        synchronized (items) {
            dirty = false;
        }
    }
    
    private void fireListenersReal() {
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "Firing listeners at " + System.currentTimeMillis()); // NOI18N
        }
        ArrayList listenersCopy = null;
        synchronized (listeners) {
            listenersCopy = new ArrayList(listeners);
        }
        ChangeEvent e = new ChangeEvent(this);
        for (Iterator i = listenersCopy.iterator(); i.hasNext();) {
            ((ChangeListener)i.next()).stateChanged(e);
        }
    }
    
    public static final class Item {
        
        private final FileObject fileObject;
        private final File file;
        private Image icon;
        
        public Item(FileObject fileObject) {
            this.fileObject = fileObject;
            file = FileUtil.toFile(fileObject);
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof Item)) {
                return false;
            }
            
            Item that = (Item)o;
            return this.file.equals(that.file);
        }
        
        public int hashCode() {
            return file.hashCode();
        }
        
        public FileObject getFileObject() {
            return fileObject;
        }
        
        public File getFile() {
            return file;
        }
        
        public Image getIcon() {
            Image i = null;
            synchronized (this) {
                i = icon;
            }
            if (i == null) {
                try {
                    DataObject dobj = DataObject.find(fileObject);
                    i = dobj.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
                } catch (DataObjectNotFoundException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
                synchronized (this) {
                    if (icon == null) {
                        icon = i;
                    }
                }
            }
            return i;
        }
    }
}
