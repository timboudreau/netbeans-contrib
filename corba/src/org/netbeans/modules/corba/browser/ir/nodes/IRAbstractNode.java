/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;



public abstract class IRAbstractNode extends AbstractNode implements GenerateSupportFactory {

    /** Creates new IRAbstractNode
     *  @param Children children
     */
    protected GenerateSupport generator;

    public IRAbstractNode(Children children) {
        super(children);
    }
}
