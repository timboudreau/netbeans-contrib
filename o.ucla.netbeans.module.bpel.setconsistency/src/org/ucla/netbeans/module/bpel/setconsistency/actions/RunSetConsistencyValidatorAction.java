/*
 * RunSetConsistencyValidatorAction.java
 * 
 * Created on Oct 4, 2007, 10:56:23 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author radval
 */
public class RunSetConsistencyValidatorAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RunSetConsistencyValidatorAction.class, "RunSetConsistencyValidatorAction_DisplayName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
