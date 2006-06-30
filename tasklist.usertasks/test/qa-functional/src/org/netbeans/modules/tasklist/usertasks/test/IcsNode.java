/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2002-2003 Sun
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
