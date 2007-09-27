/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Micro//S ystems, Inc. Portions Copyright 1997-2001 Sun
 * Micro//S ystems, Inc. All Rights Reserved.

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
 */

package org.netbeans.modules.debugger.suspendallbreakpoints.actions;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class BreakpointsStateMementoManager extends DebuggerManagerAdapter {
    
    private static BreakpointsStateMementoManager breakpointsStateMementoManager;
    
    public BreakpointsStateMementoManager() {
        breakpointsStateMementoManager = this;
        SystemAction.get(ToggleSuspendAllBreakpoints.class).setEnabled(true);
    }
    
    public static BreakpointsStateMementoManager getDefault() {
        return breakpointsStateMementoManager;
    }
    
    private static String[] properties = {
        DebuggerManager.PROP_BREAKPOINTS,
    };
    
    private static Map breakpointsToState = new HashMap();
    
    private boolean allSuspended = false;
    
    public boolean isAllSuspended() {
        return allSuspended;
    }
    
    private boolean ignoreBreakpointEnabledPropertyChange = false;
    
    public void setAllSuspended(boolean allSuspended) {
        this.allSuspended = allSuspended;
        try {
            ignoreBreakpointEnabledPropertyChange = true;
            if (allSuspended) {
                // suspend all
                synchronized (breakpointsToState) {
                    Set breakpoints = breakpointsToState.keySet();
                    for (Iterator it = breakpoints.iterator(); it.hasNext();) {
                        Breakpoint breakpoint = (Breakpoint) it.next();
                        breakpoint.enable();
                        breakpoint.disable();
                    }
                }
            } else {
                synchronized (breakpointsToState) {
                    // restore enabled/disabled state of all break points before suspension
                    Set breakpoints = breakpointsToState.keySet();
                    for (Iterator it = breakpoints.iterator(); it.hasNext();) {
                        Breakpoint breakpoint = (Breakpoint) it.next();
                        Boolean enabled = (Boolean) breakpointsToState.get(breakpoint);
                        if (enabled.equals(Boolean.TRUE)) {
                            // was enabled before suspension so enable it now
                            breakpoint.disable();
                            breakpoint.enable();
                        } else {
                            // was disabled before suspension so disnable it now
                            breakpoint.enable();
                            breakpoint.disable();
                        }
                    }
                }
            }
        } finally {
            ignoreBreakpointEnabledPropertyChange = false;
        }
    }
    
    Boolean getBreakpointState(Breakpoint breakpoint) {
        if (breakpoint == null) {
            return null;            
        }
        return (Boolean) breakpointsToState.get(breakpoint);
    }
    
    public String[] getProperties() {
        // show interest in DebuggerManager.PROP_BREAKPOINTS properties
        return properties;
    }
    
    public void breakpointAdded(Breakpoint breakpoint) {
        synchronized (breakpointsToState) {
            // record it's initial state
            breakpointsToState.put(breakpoint, Boolean.valueOf(breakpoint.isEnabled()));
            
            // if all breakpoints are in suspension then disable this one also
            if (isAllSuspended()) {
                breakpoint.disable();
            }
            
            // now monitor the breakpoint's enabled/disabled state
            breakpoint.addPropertyChangeListener(Breakpoint.PROP_ENABLED, this);
        }
    }
    
    public void breakpointRemoved(Breakpoint breakpoint) {
        // remove listener
        breakpoint.removePropertyChangeListener(Breakpoint.PROP_ENABLED, this);
        
        // remove from map
        synchronized (breakpointsToState) {
            breakpointsToState.remove(breakpoint);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (!ignoreBreakpointEnabledPropertyChange) {
            if (evt.getPropertyName().equals(Breakpoint.PROP_ENABLED)) {
                Object source = evt.getSource();
                if (source instanceof Breakpoint) {
                    Breakpoint breakpoint = (Breakpoint) source;
                    // store new state
                    breakpointsToState.put(breakpoint, Boolean.valueOf(breakpoint.isEnabled()));
                    if (isAllSuspended()) {
                        try {
                            ignoreBreakpointEnabledPropertyChange = true;
                            
                            // if all breakpoints are in suspension then disable this one also
                            breakpoint.disable();
                        } finally {
                            ignoreBreakpointEnabledPropertyChange = false;
                        }
                        
                    }
                }
            }
        }
    }
}
