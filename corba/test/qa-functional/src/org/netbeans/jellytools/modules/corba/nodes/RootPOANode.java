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
import org.netbeans.jellytools.modules.corba.actions.AddChildPOAAction;
import org.netbeans.jellytools.modules.corba.actions.AddServantAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class RootPOANode extends Node {
    
    public RootPOANode(JTreeOperator tree, String path) {
       super(tree, path);
    }

    static final AddChildPOAAction addChildPOAAction = new AddChildPOAAction ();
    static final AddServantAction addServantAction = new AddServantAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void addChildPOA () {
        addChildPOAAction.perform (this);
    }
    
    public void addServant () {
        addServantAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
