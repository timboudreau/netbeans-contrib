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

package com.netbeans.enterprise.modules.corba.idl.src;

import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openide.nodes.Node;
import org.openide.util.Task;
/*
 * @author Karel Gardas
 */

public class SourceElement extends SimpleNode {
   
   static int STATUS_OK = 0;
   static int STATUS_ERROR = 1;
   static int STATUS_PARTIAL = 2;
   static int STATUS_NOT = 3;

   public SourceElement (int i) {
      super (i);
   }

   public SourceElement (IDLParser p, int i) {
      super (p, i);
   }


   public int getStatus () {
      return STATUS_NOT;
   }

   public Task prepare () {
      return null;
   }

   
}

/*
 * <<Log>>
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */



