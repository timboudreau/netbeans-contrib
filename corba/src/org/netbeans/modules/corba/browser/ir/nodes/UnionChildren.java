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
import org.openide.nodes.Node;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRUnionMemberKey;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;

public class UnionChildren extends Children implements Refreshable {

    UnionDef union;
    boolean container;

    /** Creates new UnionChildren */
    public UnionChildren(Contained contained) {
    	try {
    	    this.container = (contained._is_a ("IDL:omg.org/CORBA/Container:1.0") || contained._is_a("IDL:omg.org/CORBA/Container:2.3"));
	}catch (Exception e) {
	    this.container = false;	
	}
        this.union = UnionDefHelper.narrow(contained);
    }
    
    public UnionDef getUnionStub () {
       return this.union;
    }


    public void addNotify() {
        synchronized (this) {
            if (this.state == SYNCHRONOUS) {
                this.createKeys();
                this.state = INITIALIZED;
            }else {
                this.state = TRANSIENT;
                this.waitNode = new WaitNode ();
                this.add ( new Node[] { this.waitNode});
                org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
            }
        }
    }


    public void createKeys(){
        try {
            UnionMember[] members = this.union.members();
            Contained[] contained  = null;
            if (this.container) {
                contained = this.union.contents (DefinitionKind.dk_all, false);
            }
            else {
                contained = new Contained[0];
            }
            java.lang.Object[] keys = new java.lang.Object[members.length + contained.length];
            for (int i = 0; i<contained.length; i++) {
                keys[i] = new IRContainedKey (contained[i]);
            }
            for (int i = 0; i<members.length; i++) {
                keys[contained.length + i] = new IRUnionMemberKey ( members[i]);
            }
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
            if (key instanceof IRUnionMemberKey){
                try {
                    return new Node[] { new IRUnionMemberNode (((IRUnionMemberKey)key).getValue())};
                }catch (final Exception t) {
                    java.awt.EventQueue.invokeLater ( new Runnable () {
                        public void run () {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (t.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                        }});
                    return new Node[0];
                }
            }
            else if ( key instanceof IRContainedKey) {
                Contained contained = ((IRContainedKey)key).contained;
                switch (contained.def_kind().value()) {
                    case DefinitionKind._dk_Struct:
                        return new Node[] { new IRStructDefNode (contained)};
                    case DefinitionKind._dk_Union:
                        return new Node[]{ new IRUnionDefNode(contained)};
                    case DefinitionKind._dk_Enum:
                        return new Node[]{ new IREnumDefNode(contained)};
                    default:
                        return new Node[]{ new IRUnknownTypeNode()};
                }
            }
        }
        return new Node[] {new IRUnknownTypeNode()};
    }

}
