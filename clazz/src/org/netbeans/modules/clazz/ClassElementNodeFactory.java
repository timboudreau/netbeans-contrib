/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import java.beans.*;

import com.netbeans.ide.actions.PropertiesAction;
import com.netbeans.ide.nodes.Node;
import com.netbeans.ide.nodes.AbstractNode;
import com.netbeans.ide.nodes.Children;
import com.netbeans.ide.src.*;
import com.netbeans.ide.src.nodes.*;
import com.netbeans.ide.util.actions.SystemAction;

/** The implementation of hierarchy nodes factory for the class loader.
*
* @author Petr Hamernik, Dafe Simonek
*/
final class ClassElementNodeFactory extends DefaultFactory {
  /** Default instance of this factory. */
  private static DefaultFactory instance;

  /** Array of the actions for element nodes */
  private static SystemAction[] defaultActions;

  /** Creates new factory. */
  public ClassElementNodeFactory() {
    super(false);
  }

  /** Returns the node asociated with specified element.
  * @return ElementNode
  */
  public Node createMethodNode(final MethodElement element) {
    MethodElementNode n = new MethodElementNode(element, false);
    n.setDefaultAction(SystemAction.get(PropertiesAction.class));
    n.setActions(getDefaultActions());
    return n;
  }

  /** Returns the node asociated with specified element.
  * @return ElementNode
  */
  public Node createConstructorNode(ConstructorElement element) {
    ConstructorElementNode n = new ConstructorElementNode(element, false);
    n.setDefaultAction(SystemAction.get(PropertiesAction.class));
    n.setActions(getDefaultActions());
    return n;
  }

  /** Returns the node asociated with specified element.
  * @return ElementNode
  */
  public Node createFieldNode(FieldElement element) {
    FieldElementNode n = new FieldElementNode(element, false);
    n.setDefaultAction(SystemAction.get(PropertiesAction.class));
    n.setActions(getDefaultActions());
    return n;
  }

  /** Returns the node asociated with specified element.
  * @return ElementNode
  */
  public Node createClassNode(ClassElement element) {
    ClassElementNode n =
      new ClassElementNode(element, createClassChildren(element), false);
    n.setActions(getDefaultActions());
    return n;
  }

  /** Method which creates children for class node.
  * @param element class element
  * @return children for the class element
  */
  protected Children createClassChildren(ClassElement element) {
    return new ClassChildren(getInstance(), element);
  }

  /** Convenience method for obtaining default actions of nodes */
  SystemAction[] getDefaultActions () {
    if (defaultActions == null) {
      defaultActions = new SystemAction[] {
        SystemAction.get(PropertiesAction.class)
      };
    }
    return defaultActions;
  }

  /** Returns instance of this element node factory */
  static DefaultFactory getInstance () {
    if (instance == null)
      instance = new ClassElementNodeFactory();
    return instance;
  }

}

/*
* Log
*  3    src-jtulach1.2         4/1/99   Ian Formanek    Rollback to make it 
*       compilable
*  2    src-jtulach1.1         4/1/99   Jan Jancura     Object browser support
*  1    src-jtulach1.0         3/26/99  David Simonek   
* $
*/
