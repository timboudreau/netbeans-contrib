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

package com.netbeans.enterprise.modules.jndi;

import java.util.Collection;
import java.util.ArrayList;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.directory.DirContext;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Children class for Directories in JNDI tree.
*    It's responsible for lazy initialization as well
*    it is an holder of Context and actual offset of node in Contxt
*/
final class JndiChildren extends Children.Keys {
  
  public final static String CONTEXT_CLASS_NAME = "javax.naming.Context";

  /** Class object for javax.naming.Context */
  private static Class ctxClass;

  private final DirContext parentContext;	// Initial Directory context
  private final CompositeName offset;	// Offset in Initial Directory context
  
  
  //Constructor takes the initial context as its parameter
  public JndiChildren(DirContext parentContext, CompositeName offset) throws NamingException {
    this.parentContext = parentContext;
    this.offset = offset;
    prepareKeys();
  }
  
  // Returns actual offset
  public CompositeName getOffset() {
    return offset;
  }
  
  // Returns context
  public DirContext getContext() {
    return parentContext;
  }
    
  // this method creates keys and set them
  public void prepareKeys() throws NamingException {

    NamingEnumeration ne = parentContext.list(offset);
    if (ne == null) return;
    ArrayList v = new ArrayList();
    while (ne.hasMore()) {
      v.add(ne.next());
    }
    this.setKeys(v);
  }
  
  // creates Node for key
  public Node[] createNodes(Object key) {
    try {
      if (key == null) {
        return null;
      }
      if (! (key instanceof NameClassPair)) {
        return null;
      }

      NameClassPair np = (NameClassPair) key;
      String objName = np.getName();
      CompositeName newName = (CompositeName) ((CompositeName) offset.clone()).add(objName);
      
      if (isContext(np.getClassName())) {
        return new Node[] {new JndiNode(parentContext, newName, objName)};
      } else {
        return new Node[] {new JndiLeafNode(parentContext, newName, objName, np.getClassName())};
      }
    } catch (NamingException ne) {
      return new Node[0];
    }
  }

  /** Heuristicaly decides whether specified class is a Context or not. */
  static boolean isContext(String className) {
    if (className.equals(CONTEXT_CLASS_NAME)) {
      return true;
    } else if (isPrimitive(className)) {
      return false;
    } else {
      try {
        Class clazz = Class.forName(className);
        if (getCtxClass().isAssignableFrom(clazz)) {
          return true;
        }
      } catch (ClassNotFoundException e) {
        JndiRootNode.notifyForeignException(e);
      }
    }
    return false;
  }

  /** @return <tt>true</tt> iff <tt>s</tt> is one of int, long, char, boolean, float, byte, double */
  private static boolean isPrimitive(String s) {
    if (s.indexOf('.') >= 0) {
      return false;
    }

    return s.equals("int") ||
      s.equals("short") ||
      s.equals("long") ||
      s.equals("byte") ||
      s.equals("char") ||
      s.equals("float") ||
      s.equals("double") ||
      s.equals("boolean");
  }

  /** @return Class object for javax.naming.Context */
  static Class getCtxClass() throws ClassNotFoundException {
    if (ctxClass == null) {
      ctxClass = Class.forName(CONTEXT_CLASS_NAME);
    }
    return ctxClass;
  }
}








