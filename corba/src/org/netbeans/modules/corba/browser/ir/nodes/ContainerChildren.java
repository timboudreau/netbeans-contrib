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
import java.util.Vector;
import java.io.*;
import org.openide.nodes.Node;
import org.openide.*;
import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.browser.ir.*;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;



public class ContainerChildren extends Children {
    
    
    private Container container;
    
    //public static final boolean DEBUG = false;
    public static final boolean DEBUG = true;
    
    
    
    
    public ContainerChildren (Container contain) {
        super ();
        this.container = contain;
    }
    
    public void addNotify () {
        if (DEBUG)
            System.out.println ("addNotify ()");
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
    
    
    public void createKeys () {
        try {
            Contained[] contained = container.contents (DefinitionKind.dk_all, true);
            java.lang.Object[] keys = new java.lang.Object [contained.length];
            for (int i=0; i<keys.length; i++)
                keys[i] = new IRContainedKey ( contained[i]);
            setKeys (keys);
        }catch (final SystemException e) {
            if (DEBUG)
                e.printStackTrace();
            setKeys ( new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }
    
    /*
      public void setRootNode (IRRootNode node) {
      _root_node = node;
      }
     
      public IRRootNode getRootNode () {
      return _root_node;
      }
     */
    
    public org.openide.nodes.Node[] createNodes (java.lang.Object key) {
        boolean operation = false;
        DefinitionKind dk = null;
        if (key != null){
            if (key instanceof IRContainedKey) {
                try{
                    Node[] nodes = new Node[1];
                    Contained contained = ((IRContainedKey)key).contained;
                   // Workaround for bug in ORBacus 4.x implementation
                    // instead of operation is_abstract special type 
                    // AbstractInterfaceDef exists.
                    // Has to be tested before def_kind, because it returns
                    // value over the limit.
                   if (contained._is_a ("IDL:omg.org/CORBA/AbstractInterfaceDef:1.0")) {
                        nodes[0] = new IRInterfaceDefNode(ContainerHelper.narrow (contained), true);
                        return nodes;
                    }
                     // Workaround for bug in Jdk 1.2 implementation
                    // if MARSHAL exception ocured, try to introspect
                    // object in another way.
                    try{
                        dk = contained.def_kind();
                    }catch (org.omg.CORBA.MARSHAL marshalException){
                        if (contained._is_a("IDL:omg.org/CORBA/OperationDef:1.0")){
                            operation = true;
                        }
                        else
                            throw new RuntimeException("Inner exception is: " + marshalException);
                    }
                    
                    if (dk == DefinitionKind.dk_Module) {
                        Container container = ContainerHelper.narrow (contained);
                        nodes[0] = new IRModuleDefNode (container);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Interface) {
                        Container container = ContainerHelper.narrow (contained);
                        nodes[0] = new IRInterfaceDefNode (container);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Exception){
                        nodes[0] = new IRExceptionDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Struct){
                        nodes[0] = new IRStructDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Union){
                        nodes[0] = new IRUnionDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Constant) {
                        nodes[0] = new IRConstantDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Attribute){
                        nodes[0] = new IRAttributeDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Operation || operation){
                        nodes[0] = new IROperationDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Alias){
                        nodes[0] = new IRAliasDefNode(contained);
                        return nodes;
                    }
                    else if (dk== DefinitionKind.dk_Enum){
                        nodes[0] = new IREnumDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_ValueBox) {
                        nodes[0] = new IRValueBoxDefNode(contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Value) {
                        nodes[0] = new IRValueDefNode (contained);
                        return nodes;
                    }
                    else if (dk == DefinitionKind.dk_Native){
                        nodes[0] = new IRNativeDefNode(contained);
                        return nodes;
                    }
                }catch(final Exception t){
                    if (DEBUG)
                        t.printStackTrace();
                    java.awt.EventQueue.invokeLater ( new Runnable () {
                        public void run () {
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (t.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                        }});
                        return new Node[0];
                }
            }
        }
        return new Node[]{new IRUnknownTypeNode()};
    }
    
    public void setContainer (Container c) {
        container = c;
    }
    
}


/*
 * $Log
 * $
 */
