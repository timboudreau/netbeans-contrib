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


import java.lang.reflect.InvocationTargetException;
import javax.naming.Context;
import org.openide.nodes.PropertySupport;


/** This class represents a property for ProviderNode
 */
public class ProviderProperty extends PropertySupport {

  /** data holder */
  private ProviderProperties repository;
  
  /** Creates new ProviderProperty 
   *  @param String name of property (hash key of property in Properties)
   *  @param Class type
   *  @param String name to be displayed
   *  @param String short description
   *  @param Object repository (should be java.util.Properties)
   *  @param boollean writeable, can be changed
   */
  public ProviderProperty (String name, Class type, String displayName, String shortDescription, Object repository, boolean writable) {
    super (name, type, displayName,  shortDescription, true, writable);
    this.repository = (ProviderProperties) repository;
  }
  
  /** Returns value of the property
   *  @return Object value of property
   *  @exception IllegalAccessException, IllegalArgumentException, InvocationTargetException
   */
  public Object getValue () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    String key = getName();
    return this.repository.getProperty(key);
  }
  
  /** Sets value of the property
   *  @param Object value of property
   *  @exception IllegalAccessException, IllegalArgumentException, InvocationTargetException
   */
  public void setValue (Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    String key = getName();
    this.repository.setProperty (key, value);
  }
  
}

/*
 * <<Log>>
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         10/6/99  Tomas Zezula    
 * $
 */
