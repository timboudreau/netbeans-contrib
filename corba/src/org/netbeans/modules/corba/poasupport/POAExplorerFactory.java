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

package org.netbeans.modules.corba.poasupport;


import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.src.nodes.FilterFactory;
import org.openide.text.CloneableEditorSupport;
import org.openide.cookies.EditorCookie;
import org.netbeans.modules.corba.poasupport.nodes.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/*
/** Adds Root POA node to CORBA servers' Java class nodes

 @author Dusan Balek
 */

public class POAExplorerFactory extends FilterFactory {
    
    public POAExplorerFactory() {
        super();
    }
    
    public Node createClassNode (ClassElement element) {
        Node node = super.createClassNode( element );
        if (element.getSource().getStatus() == SourceElement.STATUS_NOT)
            element.getSource().prepare(); 
        POASourceMaker maker = new POASourceMaker(element);
        if (maker.checkForPOA()) {
            POAChildren poaChildren = new POAChildren(maker);
            POANode pn = new POANode(poaChildren);
            Children children = node.getChildren();
            children.add(new Node[] {
                pn
            });
        }
        return node;
    }
    
}
