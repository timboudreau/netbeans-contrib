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

import org.omg.CORBA.*;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;


public class EnumChildren extends Children implements Refreshable {

    private EnumDef enum;

    /** Creates new EnumChildren */
    public EnumChildren(EnumDef def) {
        super();
        this.enum = def;
    }


    public void addNotify(){
        synchronized (this) {
            if (this.state == SYNCHRONOUS) {
                this.createKeys();
                this.state = INITIALIZED;
            }
            else {
                this.state = TRANSIENT;
                this.waitNode = new WaitNode ();
                this.add ( new Node[] { this.waitNode});
                org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
            }
        }
    }


    public void createKeys(){
        try {
            String[] members = this.enum.members();
            setKeys(members);
        }catch (final SystemException e) {
            setKeys (new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }


    public Node[] createNodes(java.lang.Object key){
        if (key != null){
            if (key instanceof String){
                return new Node[] {new EnumEntryNode((String)key)};
            }
            else return new Node[] { new IRUnknownTypeNode()};
        }
        return new Node[0];
    }


}
