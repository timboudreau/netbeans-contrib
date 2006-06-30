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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.modules.corba.nodes;

import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.corba.actions.AddInterfaceRepositoryAction;
import org.netbeans.jellytools.modules.corba.actions.FromInitialReferencesAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class CORBAInterfaceRepositoryNode extends Node {

    public static final String NAME = "CORBA Interface Repository";

    public CORBAInterfaceRepositoryNode(JTreeOperator tree) {
       super(tree, NAME);
    }

    static final AddInterfaceRepositoryAction addInterfaceRepositoryAction = new AddInterfaceRepositoryAction();
    static final FromInitialReferencesAction fromInitialReferencesAction = new FromInitialReferencesAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void addInterfaceRepository() {
        addInterfaceRepositoryAction.perform(this);
    }
    
    public void fromInitialReferences () {
        fromInitialReferencesAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
