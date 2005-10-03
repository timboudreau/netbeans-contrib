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

package org.netbeans.modules.jemmysupport.runinternally;

import org.netbeans.jemmy.explorer.GUIBrowser;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/** Used to run Jemmy GUI Browser internally in the same JVM as IDE.
 * @author Jiri.Skrivanek@sun.com
 */
public class RunGUIBrowserAction extends NodeAction {

    /** Not to show icon in main menu. */
    public RunGUIBrowserAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    /** method performing the action
     * @param nodes selected nodes
     */
    protected void performAction(Node[] nodes) {
        GUIBrowser.showBrowser();
    }
    
    /** Action is enabled everytime.
     * @param node selected nodes
     * @return true
     */
    public boolean enable(Node[] node) {
        return true;
    }
    
    /** method returning name of the action
     * @return String name of the action
     */
    public String getName() {
        return NbBundle.getMessage(RunGUIBrowserAction.class, "LBL_RunGUIBrowserAction"); // NOI18N
    }
    
    /** method returning icon for the action
     * @return String path to action icon
     */
    protected String iconResource() {
        return "org/netbeans/modules/jemmysupport/runinternally/RunGUIBrowserAction.gif"; // NOI18N
    }
    
    /** method returning action Help Context
     * @return action Help Context
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RunGUIBrowserAction.class);
    }
    
    /** Always return false - no need to run asynchronously. */
    protected boolean asynchronous() {
        return false;
    }
}

