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

package com.netbeans.enterprise.modules.rmi;

import java.util.*;
import org.openide.src.*;

/** 
 *
 * @author  mryzl
 */

public class RMIHelper extends Object {

  public static final String REMOTE = "java.rmi.Remote";
  
  /** Creates new RMIHelper. */
  public RMIHelper() {
  }
  
  /** Test if ce1 implements given class or if interface ce1 extends given class.
  */
  public static boolean implementsClass(ClassElement ce1, String classname) {
    Identifier cn = Identifier.create(classname);
    if (ce1.getName().equals(cn)) return true;

    ArrayList list = new ArrayList();
    Set done = new HashSet();

    Identifier[] ids = ce1.getInterfaces();
    putInterfaces(list, done, ids);
    while (!list.isEmpty()) {
      int len = list.size();
      Identifier id = (Identifier) list.get(len - 1);
      list.remove(len - 1);
      if (id.equals(cn)) return true;
      ClassElement ce2 = ClassElement.forName(id.getFullName());
      if (ce2 != null) {
        Identifier[] ids2 = ce2.getInterfaces();
        putInterfaces(list, done, ids2);
      }
    }
    return false;
  }

  /** Add interfaces to the FIFO.
  */
  private static void putInterfaces(List list, Set done, Identifier[] ids) {
    for(int i = 0; i < ids.length; i++) {
      if (done.add(ids[i])) {
        list.add(ids[i]);
      }
    }
  }  
}

/* 
* <<Log>>
*  2    Gandalf   1.1         1/28/00  Martin Ryzl     
*  1    Gandalf   1.0         1/24/00  Martin Ryzl     
* $ 
*/ 
  