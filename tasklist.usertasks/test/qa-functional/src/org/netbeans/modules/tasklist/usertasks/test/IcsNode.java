/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 2002-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.test;

import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

/**
 * Ics file.
 */
public class IcsNode extends Node {
    /** Creates a new instance of AntTargetNode */
    public IcsNode(String treePath) {
        super(new FilesTabOperator().tree(), treePath);
    }
    
    /** Creates a new instance of AntTargetNode */
    public IcsNode(JTreeOperator treeOperator, String treePath) {
        super(treeOperator, treePath);
    }
    
    /** Creates a new instance of AntTargetNode */
    public IcsNode(Node parent, String trg) {
        super(parent, trg);
    }
    
    public void open() {
        new OpenAction().perform(this);
    }
}
