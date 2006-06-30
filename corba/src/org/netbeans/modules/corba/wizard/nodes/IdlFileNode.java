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
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;

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
            SystemAction.get (CreateFwdDclAction.class),
            SystemAction.get (CreateInterfaceAction.class),
            SystemAction.get (CreateModuleAction.class),
            SystemAction.get (CreateStructAction.class),
            SystemAction.get (CreateUnionAction.class),
            SystemAction.get (CreateValueBoxAction.class),
            SystemAction.get (CreateValueTypeAction.class)
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
    
    public String generate () {
        return this.generateSelf(0);
    }
  
    public boolean canDestroy () {
        return false;
    }
  
    public boolean canRename () {
        return false;
    }
    
    public ExPanel getEditPanel() {
        return null;
    }
    
    public void reInit (ExPanel p) {
    }
  
}
