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

import org.netbeans.jellytools.modules.corba.actions.AddChildPOAAction;
import org.netbeans.jellytools.modules.corba.actions.AddDefaultServantAction;
import org.netbeans.jellytools.modules.corba.actions.AddPOAActivatorAction;
import org.netbeans.jellytools.modules.corba.actions.AddServantAction;
import org.netbeans.jellytools.modules.corba.actions.AddServantManagerAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class POAChildNode extends POANode {
    
    public POAChildNode(JTreeOperator tree, String path) {
       super(tree, path);
    }

    static final AddChildPOAAction addChildPOAAction = new AddChildPOAAction ();
    static final AddServantAction addServantAction = new AddServantAction ();
    static final AddServantManagerAction addServantManagerAction = new AddServantManagerAction ();
    static final AddDefaultServantAction addDefaultServantAction = new AddDefaultServantAction ();
    static final AddPOAActivatorAction addPOAActivatorAction = new AddPOAActivatorAction ();
    
    public void addChildPOA () {
        addChildPOAAction.perform (this);
    }
    
    public void addServant () {
        addServantAction.perform (this);
    }
    
    public void addServantManager () {
        addServantManagerAction.perform (this);
    }
    
    public void addDefaultServant () {
        addDefaultServantAction.perform (this);
    }
    
    public void addPOAActivator () {
        addPOAActivatorAction.perform (this);
    }
    
}
