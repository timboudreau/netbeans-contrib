/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Component Generator action class
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class GeneratorAction extends NodeAction {
    
    private static final long serialVersionUID = 2491417043823675616L;
    
    /** method performing the action
     * @param nodes selected nodes
     */    
    protected void performAction(Node[] nodes) {
        ComponentGeneratorPanel.showDialog ();
    }
    
    /** action is enabled for any selected node
     * @param node selected nodes
     * @return boolean true
     */    
    public boolean enable (Node[] node) {
        try {
            Class.forName("org.netbeans.jemmy.operators.ComponentOperator");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /** method returning name of the action
     * @return String name of the action
     */    
    public String getName() {
        return "Jemmy Component Generator";
    }

    /** method returning icon for the action
     * @return String path to action icon
     */    
    protected String iconResource() {
       return "org/netbeans/modules/jemmysupport/generator/GeneratorAction.gif"; 
    }
    
    /** method returning action Help Context
     * @return action Help Context
     */    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(GeneratorAction.class);
    }
    
}

