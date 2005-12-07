/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.autoclose;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ final class AutoCloseImpl implements PropertyChangeListener {
    
    private static final String PROP_DATA = "autoclose-auxiliary-data";
    
    private static ErrorManager ERR = ErrorManager.getDefault().getInstance("org.netbeans.modules.autoclose");
    
    private int maxOpenedFiles;
    private boolean isEnabled;
    
    /** Creates a new instance of AutoCloseImpl */
    public AutoCloseImpl() {
        AutoCloseOptions.getDefault().addPropertyChangeListener(this);
        loadSettings();
    }
    
    private void loadSettings() {
        maxOpenedFiles = AutoCloseOptions.getDefault().getMaxOpenedFiles();
        isEnabled = AutoCloseOptions.getDefault().isAutoCloseEnabled();
    }

    private void closeIfNecessary() {
        if (!isEnabled)
            return ;
        
        long currentLast = Long.MAX_VALUE;
        TopComponent found = null;
        int count = 0;
        
        for (Iterator i = TopComponent.getRegistry().getOpened().iterator(); i.hasNext(); ) {
            TopComponent tc = (TopComponent) i.next();
            
            Long l = (Long) tc.getClientProperty(PROP_DATA);
            
            if (l != null) {
                //if l == null, it is not augmented, ignore.
                count++;
                
                if (tc.getLookup().lookup(SaveCookie.class) == null) {
                    //only non-modified files.
                    long value = l.longValue();
                    
                    if (value < currentLast) {
                        currentLast = value;
                        found = tc;
                    }
                }
            }
        }
        
        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
            ERR.log("found = " + found );
            ERR.log("count = " + count );
            ERR.log("currentLast = " + currentLast );
        }
        
        if (count > (maxOpenedFiles + 1) && found != null) {
            found.close();
        }
    }
    
    private void update(TopComponent tc) {
        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
            ERR.log("augmenting: " + tc);
        }
        tc.putClientProperty(PROP_DATA, new Long(System.currentTimeMillis()));
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == TopComponent.getRegistry()) {
            propertyChangeRegistry(evt);
        } else {
            if (evt.getSource() == AutoCloseOptions.getDefault()) {
                loadSettings();
            }
        }
    }
    
    private boolean isEditor(TopComponent tc) {
        if (tc == null) {
            return false;
        }
        // #57621: check if the closed top component isn't instance of ExplorerManager.Provider e.g. Projects/Files tab, if yes then do skip this loop
        if (tc instanceof ExplorerManager.Provider) {
            return false;
        }
        // #68677: closing only documents in the editor, not eg. navigator window:
        Mode m = WindowManager.getDefault().findMode(tc);
        if (m == null || !CloneableEditorSupport.EDITOR_MODE.equals(m.getName())) { // NOI18N
            return false;
        }
        DataObject dobj = (DataObject) tc.getLookup ().lookup (DataObject.class);
        
        return dobj != null;
    }
    
    private void propertyChangeRegistry(PropertyChangeEvent evt) {
        if (TopComponent.Registry.PROP_OPENED.equals(evt.getPropertyName())) {
            Set newlyOpened = new HashSet((Collection) evt.getNewValue());
            
            newlyOpened.removeAll((Collection) evt.getOldValue());
            
            for (Iterator i = ((Collection) newlyOpened).iterator(); i.hasNext(); ) {
                TopComponent tc = (TopComponent) i.next();
                
                if (isEditor(tc)) {
                    //probably editor:
                    update(tc);
                }
            }
            
            closeIfNecessary();
        }
        
        if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            TopComponent tc = (TopComponent) evt.getNewValue();
            
            if (isEditor(tc)) {
                //probably editor:
                update(tc);
            }
        }
    }
    
}
