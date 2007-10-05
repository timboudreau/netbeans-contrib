/*
 * EditSetConsistencyRule.java
 * 
 * Created on Oct 4, 2007, 10:56:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.actions;

import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author radval
 */
public class EditSetConsistencyRuleAction extends CookieAction {

    @Override
    protected int mode() {
        return CookieAction.MODE_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[] {DataObject.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(EditSetConsistencyRuleAction.class, "EditSetConsistencyRuleAction_DisplayName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
