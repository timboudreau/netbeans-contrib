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
        return HelpCtx.DEFAULT_HELP;
    }
  
    public SystemAction[] createActions(){
        return new SystemAction[] {
            SystemAction.get(org.netbeans.modules.corba.browser.ir.actions.RemoveRepository.class)
        };
    }
    
  
  
}
