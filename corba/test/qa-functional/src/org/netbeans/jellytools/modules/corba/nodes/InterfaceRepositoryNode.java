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

import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.corba.actions.CopyCodeAction;
import org.netbeans.jellytools.modules.corba.actions.RefreshAction;
import org.netbeans.jellytools.modules.corba.actions.RemoveInterfaceRepositoryAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class InterfaceRepositoryNode extends Node {
    
    public InterfaceRepositoryNode(JTreeOperator tree, String path) {
       super(tree, "CORBA Interface Repository" + path);
    }

    static final RemoveInterfaceRepositoryAction removeInterfaceRepositoryAction = new RemoveInterfaceRepositoryAction();
    static final CopyCodeAction copyCodeAction = new CopyCodeAction();
    static final RefreshAction refreshAction = new RefreshAction();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void removeInterfaceRepository() {
        removeInterfaceRepositoryAction.perform(this);
    }
   
    public void copyCode() {
        copyCodeAction.perform(this);
    }
   
    public void refresh() {
        refreshAction.perform(this);
    }
   
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
