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

package com.netbeans.enterprise.modules.corba.browser.ns;

import java.io.*;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import com.netbeans.enterprise.modules.corba.*;

/*
 * @author Karel Gardas
 */
 
public class NamingServiceChild implements Serializable {

   public static final boolean DEBUG = false;
   //public static final boolean DEBUG = true;

   public String name;
   public String kind;
   public String url;
   public String ior;

   public NamingServiceChild () {
   }

   
   //public Object writeReplace () {
   //   System.out.println ("serialization of " + this);
   //   return "NamingServiceChild";
   //}

   public NamingServiceChild (String n, String k, String u, String i) {
      if (DEBUG)
	 System.out.println ("NamingServiceChild (" + n + ", " + k + ", " + u + ", " + i + ");");
      name = n;
      kind = k;
      url = u;
      ior = i;
   }

   public String getName () {
      return name;
   }

   public String getKind () {
      return kind;
   }

   public String getURL () {
      return url;
   }

   public String getIOR () {
      return ior;
   }
   
}


/*
 * $Log
 * $
 */
