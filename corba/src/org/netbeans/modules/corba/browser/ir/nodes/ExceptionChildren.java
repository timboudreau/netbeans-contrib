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
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRTypeCodeKey;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

public class ExceptionChildren extends Children implements Refreshable {


    private ExceptionDef exception;
    private boolean container;

    /** Creates new ExceptionChildren */
    public ExceptionChildren(Contained contained) {
        super();
        try {
            this.container = (contained._is_a("IDL:omg.org/CORBA/Container:1.0") || contained._is_a("IDL:omg.org/CORBA/Container:2.3"));
	}catch (Exception e) {
            this.container = false;
	}
        this.exception = ExceptionDefHelper.narrow(contained);
    }
    
    public ExceptionDef getExceptionStub () {
        return this.exception;
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
            StructMember [] members = this.exception.members();
            Contained[] contained = null;
            
            if (this.container) {
                contained = this.exception.contents (DefinitionKind.dk_all, false);
            }
            else {
                contained = new Contained [0];
            }
        
            java.lang.Object[] keys = new java.lang.Object[members.length + contained.length];
            for (int i =0; i< contained.length; i++)
                keys[i] = new IRContainedKey (contained[i]);
            for (int i =0; i< members.length; i++)
                keys[contained.length + i] = new IRTypeCodeKey (members[i].name, members[i].type_def);
            setKeys(keys);
        }catch (final SystemException e) {
            setKeys (new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify(new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }


    public Node[] createNodes (java.lang.Object key){
        if (key != null){
            if (key instanceof IRTypeCodeKey) {
                return new Node[]{new IRPrimitiveNode(((IRTypeCodeKey)key).type, ((IRTypeCodeKey)key).name)};
            }
            else if (key instanceof IRContainedKey) {
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
        return new Node[]{new IRUnknownTypeNode()};
    }

}
