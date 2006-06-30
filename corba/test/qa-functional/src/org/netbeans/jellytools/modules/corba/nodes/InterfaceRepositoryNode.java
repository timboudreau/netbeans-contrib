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
