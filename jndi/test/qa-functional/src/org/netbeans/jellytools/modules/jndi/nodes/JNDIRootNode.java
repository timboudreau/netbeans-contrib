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

package org.netbeans.jellytools.modules.jndi.nodes;

import org.netbeans.jellytools.modules.jndi.actions.AddContextAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class JNDIRootNode extends Node {
    
    public static final String NAME = "JNDI";
    
    public JNDIRootNode(JTreeOperator tree) {
       super(tree, NAME);
    }

    static final AddContextAction addContextAction = new AddContextAction();
    
    public void addContext() {
        addContextAction.perform(this);
    }
    
}
