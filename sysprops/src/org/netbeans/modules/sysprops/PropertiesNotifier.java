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

package org.netbeans.modules.sysprops;
import java.util.*;
import javax.swing.event.*;
public class PropertiesNotifier {
  private static Set listeners = new HashSet ();
  public static void addChangeListener (ChangeListener listener) {
    listeners.add (listener);
  }
  public static void removeChangeListener (ChangeListener listener) {
    listeners.remove (listener);
  }
  public static void changed () {
    ChangeEvent ev = new ChangeEvent (PropertiesNotifier.class);
    Iterator it = listeners.iterator ();
    while (it.hasNext ())
      ((ChangeListener) it.next ()).stateChanged (ev);
  }
}
