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
