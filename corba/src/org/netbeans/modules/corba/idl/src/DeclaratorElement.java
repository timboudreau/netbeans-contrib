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

/*
 * NAME_SUBSTITUTION.java -- synopsis.
 *
 *
 * Date: 15.6.1998 12:22:29$
 * <<Revision>>
 *
 * SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 *
 * Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.netbeans.enterprise.modules.corba.idl.src;

import java.util.Vector;

public class DeclaratorElement extends TypeElement {

  private Vector dim;

  public DeclaratorElement (int id) {
    super(id);
    dim = new Vector ();
  }

  public DeclaratorElement (IDLParser p, int id) {
    super(p, id);
    dim = new Vector ();
  }

  public void setDimension (Vector s) {
    dim = s;
  }

  public Vector getDimension () {
    return dim;
  }

  /*
    public void setType (String s) {
    System.out.println (getType () + " -> " + s);
    super.setType (s);
    Thread.dumpStack ();
    }
  */

  public IDLType getType () {
    if (super.getType ().ofDimension () != null) {
      if (!super.getType ().ofDimension ().equals (getDimension ())) {
	//System.out.println ("setting right dimension for IDLType");
	super.getType ().setDimension (getDimension ());
      }
    }
    else {
      super.getType ().setDimension (new Vector ());
    }
    return super.getType ();
  }

  public void jjtClose () {
    super.jjtClose ();
    //System.out.println ("DeclaratorElement.jjtClose ();");
    setName (((Identifier)getMember (0)).getName ());
    //getType ().setDimension (getDimension ());
  }
}


/*
 * <<Log>>
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         10/8/99  Karel Gardas    
 *  4    Gandalf   1.3         10/5/99  Karel Gardas    
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */
