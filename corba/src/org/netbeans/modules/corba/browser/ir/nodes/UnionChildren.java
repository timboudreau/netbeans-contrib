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
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRTypeCodeKey;


public class UnionChildren extends Children.Keys implements Refreshable {

    UnionDef union;

    /** Creates new UnionChildren */
    public UnionChildren(UnionDef union) {
        this.union = union;
    }


    public void addNotify(){
        createKeys();
    }


    public void createKeys(){
        try {
            UnionMember[] members = this.union.members();
            java.lang.Object[] keys = new java.lang.Object[members.length];
            for (int i = 0; i<members.length; i++)
                keys[i] = new IRTypeCodeKey ( members[i].name, members[i].type, members[i].label);
            setKeys(keys);
        }catch (final SystemException e) {
            setKeys ( new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater (new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }


    public Node[] createNodes(java.lang.Object key){
        if (key != null){
            if (key instanceof IRTypeCodeKey){
                try {
                    return new Node[] { new IRPrimitiveNode(((IRTypeCodeKey)key).type,((IRTypeCodeKey)key).name)};
                }catch (final Throwable t) {
                    java.awt.EventQueue.invokeLater ( new Runnable () {
                        public void run () {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (t.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                        }});
                    return new Node[0];
                }
            }
        }
        return new Node[] {new IRUnknownTypeNode()};
    }

}
