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

import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;


/** This class represents Property of JndiNodeObject
 *
 *  @author Tomas Zezula
 */
public final class JndiProperty extends PropertySupport.ReadOnly{

  /** Value of property */
  String value;
  
  /** Constructor
   *  @param name name of property
   *  @param type class of value
   *  @param pname displayed name of property
   *  @param pvalue value of property
   */
  public JndiProperty(String name, Class type, String pname, String pvalue){
    super(name,type,pname,pvalue);
    this.value=pvalue;
    }

  /** Returns value of property
   *  @return Object value of this property
   */
  public Object getValue(){
    return this.value;
  }
}



/*
 * <<Log>>
 *  1    Gandalf   1.0         7/9/99   Ales Novak      
 * $
 */
