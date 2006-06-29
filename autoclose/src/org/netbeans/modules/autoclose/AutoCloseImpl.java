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
package org.netbeans.modules.autoclose;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.openide.ErrorManager;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
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
    
    private static ErrorManager ERR = ErrorManager.getDefault().getInstance("org.netbeans.modules.autoclose");
    
    private int maxOpenedFiles;
    private boolean isEnabled;
    
    private Map timestamps;
    
    private static AutoCloseImpl INSTANCE;
    
    public static synchronized AutoCloseImpl getDefault() {
        if (INSTANCE == null) {
            INSTANCE = new AutoCloseImpl();
        }
        
        return INSTANCE;
    }
    
    /** Creates a new instance of AutoCloseImpl */
    private AutoCloseImpl() {
        AutoCloseOptions.getDefault().addPropertyChangeListener(this);
        timestamps = new WeakHashMap();
        timestamps.putAll(AutoCloseOptions.getDefault().getTimestampMap());
        loadSettings();
    }
    
    private void loadSettings() {
        maxOpenedFiles = AutoCloseOptions.getDefault().getMaxOpenedFiles();
        isEnabled = AutoCloseOptions.getDefault().isAutoCloseEnabled();
    }
    
    public void close() {
        AutoCloseOptions.getDefault().setTimestampMap(new HashMap(timestamps));
    }

    private void closeIfNecessary() {
        if (!isEnabled)
            return ;
        
        long currentLast = Long.MAX_VALUE;
        TopComponent found = null;
        int count = 0;
        
        for (Iterator i = TopComponent.getRegistry().getOpened().iterator(); i.hasNext(); ) {
            TopComponent tc = (TopComponent) i.next();
            
            Long l = (Long) timestamps.get(tc);
            
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
        
        timestamps.put(tc, new Long(System.currentTimeMillis()));
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
