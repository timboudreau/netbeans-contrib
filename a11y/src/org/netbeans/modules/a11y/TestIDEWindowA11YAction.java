/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.a11y;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import org.netbeans.a11y.tester.UIAccessibilityTester;

/**
 * Accessibility test action. Testing windows from IDE.
 * @author  Marian.Mirilovic@Sun.Com
 */
public class TestIDEWindowA11YAction extends NodeAction {
    
    //private static final long serialVersionUID = ;
    
    protected void performAction(Node[] nodes) {
        UIAccessibilityTester at = UIAccessibilityTester.getInstance();
        at.show();
        at.requestFocus();
    }
    
    public boolean enable(Node[] node) {
        if(Boolean.getBoolean("a11ytest.IDE"))
            return true;
        else
            return false;
    }
    
    /**
     *  Human presentable name of the action. This should be
     *  presented as an item in a menu.
     *  @return the name of the action */
    public String getName() {
        //return NbBundle.getBundle(TestIDEWindowA11YAction.class).getString("ACT_A11Y_IDE_Test"); // NOI18N
        return "UI Accessibility Tester - testing IDE";
    }
    
    
    /** @return resource for the action icon */
    protected String iconResource() {
        return "org/netbeans/modules/a11y/resources/disabled.gif";
    }
    
    
    /** Help context where to find more about the action.
     * @return the help context for this action */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(TestIDEWindowA11YAction.class);
    }
    
    /** Fix issue 45833
     * Warning - org.netbeans.modules.a11y.TestIDEWindowA11YAction should override CallableSystemAction.asynchronous() to return false
     */
    protected boolean asynchronous() {
        return false;
    }
}
    
