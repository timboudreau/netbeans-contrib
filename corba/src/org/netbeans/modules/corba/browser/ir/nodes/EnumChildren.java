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
import org.openide.nodes.Children;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;


public class EnumChildren extends Children.Keys implements Refreshable {

    private EnumDef enum;

    /** Creates new EnumChildren */
    public EnumChildren(EnumDef def) {
        super();
        this.enum = def;
    }


    public void addNotify(){
        createKeys();
    }


    public void createKeys(){
        try {
            String[] members = this.enum.members();
            setKeys(members);
        }catch (final SystemException e) {
            setKeys (new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Exception (e));
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
