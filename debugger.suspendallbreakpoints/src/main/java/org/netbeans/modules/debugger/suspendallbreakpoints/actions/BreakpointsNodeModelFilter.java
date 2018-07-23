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

import java.awt.SystemColor;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.NodeModelFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class BreakpointsNodeModelFilter implements NodeModelFilter {
    private static int inactiveRed = SystemColor.textInactiveText.getRed();
    private static int inactiveGreen = SystemColor.textInactiveText.getGreen();
    private static int inactiveBlue = SystemColor.textInactiveText.getBlue();
    /**
     * This method returns.
     *
     * @param original
     * @param node
     * @return String
     */
    public String getDisplayName(NodeModel original, Object node) throws UnknownTypeException {
        String displayName = original.getDisplayName(node);
        if (node instanceof Breakpoint) {
            if (BreakpointsStateMementoManager.getDefault() != null) {
                if (BreakpointsStateMementoManager.getDefault().isAllSuspended()) {
                    if (displayName.startsWith("<html>")) {
                        return //NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Suspended") +
                                "<html>" +
                                "<i>" +
                                "<font color='#444444'>" +
                                displayName.substring(6) +
                                "</font>" +
                                "</i>";
                    } else {
                        return //NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Suspended") +
                                "<html>" +
                                "<i>" +
                                "<font color='#444444'>" +
                                displayName +
                                "</font>" +
                                "</i>";
                    }
                }
            }
        }
        return displayName;
    }
    
    /**
     * This method returns.
     *
     * @param original
     * @param node
     * @return String
     */
    public String getIconBase(NodeModel original, Object node) throws UnknownTypeException {
        return original.getIconBase(node);
    }
    
    /**
     * This method returns.
     *
     * @param original
     * @param node
     * @return String
     */
    public String getShortDescription(NodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof Breakpoint) {
            Breakpoint breakpoint = (Breakpoint) node;
            BreakpointsStateMementoManager breakpointsStateMementoManager = BreakpointsStateMementoManager.getDefault();
            if (breakpointsStateMementoManager != null && breakpointsStateMementoManager.isAllSuspended()) {
                Boolean breakpointState = breakpointsStateMementoManager.getBreakpointState(breakpoint);
                return NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_SuspendedTooltip",
                        original.getShortDescription(node),
                        ((breakpointState == null) ?
                            NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Unknown") :
                            (Boolean.TRUE.equals(breakpointState) ?
                                NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Enabled") :
                                NbBundle.getMessage(ToggleSuspendAllBreakpoints.class, "CTL_Disabled")))
                                );
            }
        }
        return original.getShortDescription(node);
    }
    
    /**
     * This method.
     *
     * @param l
     */
    public void addModelListener(ModelListener l) {
    }
    
    /**
     * This method.
     *
     * @param l
     */
    public void removeModelListener(ModelListener l) {
    }
    
}
