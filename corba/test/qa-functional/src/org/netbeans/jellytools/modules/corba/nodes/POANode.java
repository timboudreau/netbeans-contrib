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

package org.netbeans.jellytools.modules.corba.nodes;

import org.netbeans.jellytools.actions.CustomizeAction;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.actions.RenameAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class POANode extends Node {
    
    public POANode(JTreeOperator tree, String path) {
       super(tree, path);
    }

    static final DeleteAction deleteAction = new DeleteAction ();
    static final RenameAction renameAction = new RenameAction ();
    static final CustomizeAction customizeAction = new CustomizeAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void delete() {
        deleteAction.perform (this);
    }
    
    public void rename() {
        renameAction.perform (this);
    }
    
    public void customize () {
        customizeAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
