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
import org.netbeans.jellytools.modules.corba.actions.BindNewObjectAction;
import org.netbeans.jellytools.modules.corba.actions.CopyServerBindingCodeAction;
import org.netbeans.jellytools.modules.corba.actions.CreateNewContextAction;
import org.netbeans.jellytools.modules.corba.actions.RefreshAction;
import org.netbeans.jellytools.modules.corba.actions.UnbindContextAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class NamingContextNode extends Node {
    
    public NamingContextNode(JTreeOperator tree, String path) {
       super(tree, "CORBA Naming Service" + path);
    }

    static final CreateNewContextAction createNewContextAction = new CreateNewContextAction();
    static final BindNewContextAction bindNewContextAction = new BindNewContextAction();
    static final UnbindContextAction unbindContextAction = new UnbindContextAction ();
    static final CopyServerBindingCodeAction copyServerBindingCodeAction = new CopyServerBindingCodeAction ();
    static final BindNewObjectAction bindNewObjectAction = new BindNewObjectAction ();
    static final RefreshAction refreshAction = new RefreshAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void createNewContext() {
        createNewContextAction.perform(this);
    }
    
    public void bindNewContext() {
        bindNewContextAction.perform(this);
    }
    
    public void unbindContext () {
        unbindContextAction.perform (this);
    }
    
    public void copyServerBindingCode () {
        copyServerBindingCodeAction.perform (this);
    }
    
    public void bindNewObject () {
        bindNewObjectAction.perform (this);
    }
    
    public void refresh () {
        refreshAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
