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

package org.netbeans.modules.corba.idl.src;

public interface IDLParserTreeConstants
{
  public int JJTIDLELEMENT = 0;
  public int JJTVOID = 1;
  public int JJTMODULEELEMENT = 2;
  public int JJTINTERFACEELEMENT = 3;
  public int JJTINTERFACEFORWARDELEMENT = 4;
  public int JJTINTERFACEHEADERELEMENT = 5;
  public int JJTVALUEABSELEMENT = 6;
  public int JJTVALUEELEMENT = 7;
  public int JJTVALUEHEADERELEMENT = 8;
  public int JJTVALUEINHERITANCESPECELEMENT = 9;
  public int JJTSTATEMEMBERELEMENT = 10;
  public int JJTINITDCLELEMENT = 11;
  public int JJTCONSTELEMENT = 12;
  public int JJTTYPEELEMENT = 13;
  public int JJTSIMPLEDECLARATOR = 14;
  public int JJTSTRUCTTYPEELEMENT = 15;
  public int JJTMEMBERELEMENT = 16;
  public int JJTUNIONTYPEELEMENT = 17;
  public int JJTUNIONMEMBERELEMENT = 18;
  public int JJTENUMTYPEELEMENT = 19;
  public int JJTARRAYDECLARATOR = 20;
  public int JJTATTRIBUTEELEMENT = 21;
  public int JJTEXCEPTIONELEMENT = 22;
  public int JJTOPERATIONELEMENT = 23;
  public int JJTPARAMETERELEMENT = 24;
  public int JJTIDENTIFIER = 25;


  public String[] jjtNodeName = {
    "IDLElement",
    "void",
    "ModuleElement",
    "InterfaceElement",
    "InterfaceForwardElement",
    "InterfaceHeaderElement",
    "ValueAbsElement",
    "ValueElement",
    "ValueHeaderElement",
    "ValueInheritanceSpecElement",
    "StateMemberElement",
    "InitDclElement",
    "ConstElement",
    "TypeElement",
    "SimpleDeclarator",
    "StructTypeElement",
    "MemberElement",
    "UnionTypeElement",
    "UnionMemberElement",
    "EnumTypeElement",
    "ArrayDeclarator",
    "AttributeElement",
    "ExceptionElement",
    "OperationElement",
    "ParameterElement",
    "Identifier",
  };
}
