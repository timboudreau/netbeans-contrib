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

public interface IDLParserTreeConstants
{
  public int JJTIDLELEMENT = 0;
  public int JJTVOID = 1;
  public int JJTMODULEELEMENT = 2;
  public int JJTINTERFACEELEMENT = 3;
  public int JJTCONSTELEMENT = 4;
  public int JJTTYPEELEMENT = 5;
  public int JJTDECLARATORELEMENT = 6;
  public int JJTSTRUCTTYPEELEMENT = 7;
  public int JJTMEMBERELEMENT = 8;
  public int JJTUNIONTYPEELEMENT = 9;
  public int JJTUNIONMEMBERELEMENT = 10;
  public int JJTENUMTYPEELEMENT = 11;
  public int JJTATTRIBUTEELEMENT = 12;
  public int JJTEXCEPTIONELEMENT = 13;
  public int JJTOPERATIONELEMENT = 14;
  public int JJTPARAMETERELEMENT = 15;
  public int JJTIDENTIFIER = 16;


  public String[] jjtNodeName = {
    "IDLElement",
    "void",
    "ModuleElement",
    "InterfaceElement",
    "ConstElement",
    "TypeElement",
    "DeclaratorElement",
    "StructTypeElement",
    "MemberElement",
    "UnionTypeElement",
    "UnionMemberElement",
    "EnumTypeElement",
    "AttributeElement",
    "ExceptionElement",
    "OperationElement",
    "ParameterElement",
    "Identifier",
  };
}

/*
 * <<Log>>
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */
