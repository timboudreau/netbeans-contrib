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
import org.openide.nodes.*;
import org.openide.*;
import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.browser.ir.*;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;


 
public class ContainerChildren extends Children.Keys implements Refreshable {

  private Container container;

  public static final boolean DEBUG = false;
  //public static final boolean DEBUG = true;
  
  public ContainerChildren (Container contain) {
    super ();
    container = contain;
  }

  public void addNotify () {
    if (DEBUG) 
      System.out.println ("addNotify ()");
    createKeys ();
  }

   
  public void createKeys () {
    Contained[] contained = container.contents (DefinitionKind.dk_all, true);
    java.lang.Object[] keys = new java.lang.Object [contained.length];
    for (int i=0; i<keys.length; i++)
      keys[i] = new IRContainedKey ( contained[i]);
    setKeys (keys);
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
          // Workaround for bug in Jdk 1.2 implementation
          // if MARSHAL exception ocured, try to introspect
          // object in another way.
          try{
            dk = contained.describe().kind;
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
        }catch(Throwable t){
          t.printStackTrace();
          return null;
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
