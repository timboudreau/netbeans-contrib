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

package org.netbeans.modules.corba.wizard.nodes;

import java.io.PrintWriter;
import java.io.IOException;
import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;

/** 
 *
 * @author  root
 * @version 
 */
public class IdlFileNode extends FMNode implements Node.Cookie {

    private static final String ICON_BASE = "org/netbeans/modules/corba/settings/idl";
  
    /** Creates new IdlFileNode */
    public IdlFileNode () {
        super (null);
        this.getCookieSet().add (this);
        this.setName ("Idl Node");
        this.setIconBase(ICON_BASE);
    }
  
    public IdlFileNode(String name) {
        super (null);
        this.getCookieSet().add (this);
        this.setName (name);
        this.setIconBase(ICON_BASE);
    }
  
  
    public SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (CreateAliasAction.class),
            SystemAction.get (CreateConstantAction.class),
            SystemAction.get (CreateEnumAction.class),
            SystemAction.get (CreateExceptionAction.class),
            SystemAction.get (CreateInterfaceAction.class),
            SystemAction.get (CreateModuleAction.class),
            SystemAction.get (CreateStructAction.class),
            SystemAction.get (CreateUnionAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = new String();
        Node[] nodes = this.getChildren().getNodes();
        for (int i=0; i<nodes.length; i++) {
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (0);
            code = code + "\n";
        }
        return code;
    }
  
    public void generate (PrintWriter out) throws IOException {
        out.println (generateSelf (0));
    }
  
    public boolean canDestroy () {
        return false;
    }
  
    public boolean canRename () {
        return false;
    }
  
}
