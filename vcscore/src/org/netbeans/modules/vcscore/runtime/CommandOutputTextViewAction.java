/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import org.openide.util.actions.NodeAction;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;

/**
 * The action that opens the command output in the Command Output Visualizer.
 *
 * @author  Martin Entlicher
 */
public class CommandOutputTextViewAction extends NodeAction {

    private static final long serialVersionUID = 5124642672039783673L;
    
    /** Creates new CommandOutputViewAction */
    private CommandOutputTextViewAction() {
    }
    
    public static CommandOutputTextViewAction getInstance() {
        return (CommandOutputTextViewAction)
            org.openide.util.actions.SystemAction.get(CommandOutputTextViewAction.class);
    }

    public String getName() {
        return g("CTL_Command_Output_Text_View_Action"); // NOI18N
    }

    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    public void performAction(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            RuntimeCommand comm = (RuntimeCommand) nodes[i].getCookie(RuntimeCommand.class);
            if (comm != null) {
                comm.openCommandOutputDisplay(false);
            }
        }
    }

    public boolean enable(Node[] nodes){
        return nodes.length > 0;
    }

    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandOutputTextViewAction.class).getString(s);
    }
    
}
