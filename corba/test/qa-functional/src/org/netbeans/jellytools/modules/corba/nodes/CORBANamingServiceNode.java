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
import org.netbeans.jellytools.modules.corba.actions.BindNewContextAction;
import org.netbeans.jellytools.modules.corba.actions.FromInitialReferencesAction;
import org.netbeans.jellytools.modules.corba.actions.StartLocalAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class CORBANamingServiceNode extends Node {
    
    public CORBANamingServiceNode(JTreeOperator tree) {
       super(tree, "CORBA Naming Service");
    }

    static final BindNewContextAction bindNewContextAction = new BindNewContextAction();
    static final StartLocalAction startLocalAction = new StartLocalAction();
    static final FromInitialReferencesAction fromInitialReferencesAction = new FromInitialReferencesAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void bindNewContext() {
        bindNewContextAction.perform(this);
    }
    
    public void startLocal () {
        startLocalAction.perform (this);
    }

    public void fromInitialReferences () {
        fromInitialReferencesAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
