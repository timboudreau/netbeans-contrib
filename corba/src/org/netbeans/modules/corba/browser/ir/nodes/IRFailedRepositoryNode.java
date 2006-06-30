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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.browser.ir.util.Removable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.Util;
/**
 *
 * @author  tzezula
 * @version
 */
public class IRFailedRepositoryNode extends IRLeafNode implements Removable, Node.Cookie {

    private static final String FAILED_ICON_BASE =
        "org/netbeans/modules/corba/browser/ir/resources/failedrep";
  
    /** Creates new IRFailedNode */
    public IRFailedRepositoryNode(String name) {
        super();
        this.getCookieSet().add(this);
        setName(name);
        setIconBase(FAILED_ICON_BASE);
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (IRFailedRepositoryNode.class.getName());
    }
  
    public SystemAction[] createActions(){
        return new SystemAction[] {
            SystemAction.get(org.netbeans.modules.corba.browser.ir.actions.RemoveRepository.class)
        };
    }
    
  
  
}
