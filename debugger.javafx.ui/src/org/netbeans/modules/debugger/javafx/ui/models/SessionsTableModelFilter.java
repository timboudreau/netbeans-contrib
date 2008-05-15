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

package org.netbeans.modules.debugger.javafx.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.javafx.JavaFXDebugger;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TableModelFilter;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.NbBundle;


/**
 *
 * @author   Jan Jancura
 */
public class SessionsTableModelFilter implements TableModelFilter, Constants,
PropertyChangeListener {
    
    private static String loc(String key) {
        return NbBundle.getBundle (SessionsTableModelFilter.class).getString (key);
    }

    private Vector listeners = new Vector ();
    private boolean addedAsListener;

    
    public SessionsTableModelFilter () {
    }

    public Object getValueAt (TableModel original, Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof Session && isJavaFXSession((Session) row)) {
            if (SESSION_STATE_COLUMN_ID.equals (columnID))
                return getSessionState ((Session) row);
            else
            if (SESSION_LANGUAGE_COLUMN_ID.equals (columnID))
                return row;
            else
            if (SESSION_HOST_NAME_COLUMN_ID.equals (columnID))
                return ((Session) row).getLocationName ();
            else
                throw new UnknownTypeException (row);
        }
        return original.getValueAt(row, columnID);
    }
    
    public boolean isReadOnly (TableModel original, Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof Session && isJavaFXSession((Session) row)) {
            if (SESSION_STATE_COLUMN_ID.equals (columnID))
                return true;
            else
            if (SESSION_LANGUAGE_COLUMN_ID.equals (columnID))
                return false;
            else
            if (SESSION_HOST_NAME_COLUMN_ID.equals (columnID))
                return true;
            else
                throw new UnknownTypeException (row);
        }
        return original.isReadOnly(row, columnID);
    }
    
    public void setValueAt (TableModel original, Object row, String columnID, Object value) 
    throws UnknownTypeException {
        original.setValueAt(row, columnID, value);
    }

    
    // other methods ...........................................................

    static boolean isJavaFXSession(Session s) {
        DebuggerEngine e = s.getCurrentEngine ();
        if (e == null) {
            return false;
        }
        JavaFXDebugger d = e.lookupFirst(null, JavaFXDebugger.class);
        return d != null;
    }
    
    private String getSessionState (Session s) {
        DebuggerEngine e = s.getCurrentEngine ();
        if (e == null)
            return loc ("MSG_Session_State_Starting");
        JavaFXDebugger d = e.lookupFirst(null, JavaFXDebugger.class);
        synchronized (this) {
            if (!addedAsListener) {
                d.addPropertyChangeListener (JavaFXDebugger.PROP_STATE, this);
            }
        }
        switch (d.getState ()) {
            case JavaFXDebugger.STATE_DISCONNECTED:
                return loc ("MSG_Session_State_Disconnected");
            case JavaFXDebugger.STATE_RUNNING:
                return loc ("MSG_Session_State_Running");
            case JavaFXDebugger.STATE_STARTING:
                return loc ("MSG_Session_State_Starting");
            case JavaFXDebugger.STATE_STOPPED:
                return loc ("MSG_Session_State_Stopped");
        }
        return null;
    }
    
    /** 
     * Registers given listener.
     * 
     * @param l the listener to add
     */
    public void addModelListener (ModelListener l) {
        listeners.add (l);
    }

    /** 
     * Unregisters given listener.
     *
     * @param l the listener to remove
     */
    public void removeModelListener (ModelListener l) {
        listeners.remove (l);
    }
    
    private void fireTreeChanged () {
        Vector v = (Vector) listeners.clone ();
        int i, k = v.size ();
        for (i = 0; i < k; i++)
            ((ModelListener) v.get (i)).modelChanged (null);
    }
    
    private static final Integer SD = new Integer 
        (JavaFXDebugger.STATE_DISCONNECTED);
    
    public void propertyChange (PropertyChangeEvent e) {
        fireTreeChanged ();
        if (e.getNewValue ().equals (SD))
            ((JavaFXDebugger) e.getSource ()).removePropertyChangeListener (
                JavaFXDebugger.PROP_STATE, this
            );
    }
}
