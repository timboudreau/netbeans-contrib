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

//package com.netbeans.enterprise.modules.corba.idl.parser;
package com.netbeans.enterprise.modules.corba.idl.src;

import java.util.Vector;

public class IDLParser/*@bgen(jjtree)*/implements IDLParserTreeConstants, IDLParserConstants {/*@bgen(jjtree)*/
  protected JJTIDLParserState jjtree = new JJTIDLParserState();
  public static void main(String args[]) {
    IDLParser parser = null;
    if (args.length == 0) {
      System.out.println("IDL Parser Version 0.1:  Reading from standard input . . .");
      parser = new IDLParser(System.in);
    } else if (args.length == 1) {
      System.out.println("IDL Parser Version 0.1:  Reading from file " + args[0] + " . . .");
      try {
        parser = new IDLParser(new java.io.FileInputStream(args[0]));
        SimpleNode sn = parser.Start ();
        sn.dump ("|");
        System.out.println ("OK :-))");
      } catch (java.io.FileNotFoundException e) {
        System.out.println("IDL Parser Version 0.1:  File " + args[0] + " not found.");
        return;
      } catch (ParseException e) {
         System.out.println ("IDL parse error !!!");
         e.printStackTrace ();
      }

    } else {
      System.out.println("IDL Parser Version 0.1:  Usage is one of:");
      System.out.println("         java IDLParser < inputfile");
      System.out.println("OR");
      System.out.println("         java IDLParser inputfile");
      return;
    }
  }

/* comment for matching directives    */
/* | < "#" ([" ","\t"])* (["0"-"9"])+ */
/*    (([" ","\t"])* "\"" (~["\""])+ "\"" */
/*           ([" ","\t"])* (["0"-"9"])* ([" ","\t"])* (["0"-"9"])*)? "\n" >  */


/* starting */
  final public SimpleNode Start() throws ParseException {
                                   /*@bgen(jjtree) IDLElement */
  IDLElement jjtn000 = new IDLElement(JJTIDLELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      specification();
     jjtree.closeNodeScope(jjtn000, true);
     jjtc000 = false;
     {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
     if (jjtc000) {
       jjtree.clearNodeScope(jjtn000);
       jjtc000 = false;
     } else {
       jjtree.popNode();
     }
     if (jjte000 instanceof ParseException) {
       {if (true) throw (ParseException)jjte000;}
     }
     if (jjte000 instanceof RuntimeException) {
       {if (true) throw (RuntimeException)jjte000;}
     }
     {if (true) throw (Error)jjte000;}
    } finally {
     if (jjtc000) {
       jjtree.closeNodeScope(jjtn000, true);
     }
    }
    throw new Error("Missing return statement in function");
  }

/* Production 1 */
  final public void specification() throws ParseException {
    label_1:
    while (true) {
      definition();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 10:
      case 13:
      case 17:
      case 34:
      case 44:
      case 45:
      case 49:
      case 58:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
  }

/* Production 2 */
  final public void definition() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 34:
    case 44:
    case 45:
    case 49:
      type_dcl();
      jj_consume_token(9);
      break;
    case 17:
      const_dcl();
      jj_consume_token(9);
      break;
    case 58:
      except_dcl();
      jj_consume_token(9);
      break;
    case 13:
      interfacex();
      jj_consume_token(9);
      break;
    case 10:
      module();
      jj_consume_token(9);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Production 3 */
/*
void module() #ModuleElement :
{}
{
  "module" identifier() "{" ( definition() )+ "}"
}
*/
  final public void module() throws ParseException {
 /*@bgen(jjtree) ModuleElement */
  ModuleElement jjtn000 = new ModuleElement(JJTMODULEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    Identifier id;
      jj_consume_token(10);
      id = identifier();
                               jjtn000.setName (id.getName ());
      jj_consume_token(11);
      label_2:
      while (true) {
        definition();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 10:
        case 13:
        case 17:
        case 34:
        case 44:
        case 45:
        case 49:
        case 58:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_2;
        }
      }
      jj_consume_token(12);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 4 */
  final public void interfacex() throws ParseException {
    if (jj_2_1(3)) {
      interface_dcl();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 13:
        forward_dcl();
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

/* Production 5 */
/*
void interface_dcl() #InterfaceElement :
{
 // Token t;
}
{ 
 // t=<ID>
 // {
 //    jjtThis.setLine (t.beginLine);
 // }
  interface_header() "{" interface_body() "}"
}
*/
  final public void interface_dcl() throws ParseException {
 /*@bgen(jjtree) InterfaceElement */
  InterfaceElement jjtn000 = new InterfaceElement(JJTINTERFACEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      interface_header();
      jj_consume_token(11);
      interface_body();
      jj_consume_token(12);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 6 */
  final public void forward_dcl() throws ParseException {
    jj_consume_token(13);
    identifier();
  }

/* Production 7 */
/*
void interface_header() :
{}
{
  "interface" identifier() [ inheritance_spec() ]
}
*/
  final public Vector interface_header() throws ParseException {
    Vector inter = new Vector ();
    Identifier name;
    Vector inher;
    jj_consume_token(13);
    name = identifier();
    inter.addElement (name);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 14:
      inher = inheritance_spec();
      inter.addElement (inher);
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    {if (true) return inter;}
    throw new Error("Missing return statement in function");
  }

/* Production 8 */
  final public void interface_body() throws ParseException {
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 16:
      case 17:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 49:
      case 53:
      case 56:
      case 57:
      case 58:
      case 59:
      case 60:
      case ID:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_3;
      }
      export();
    }
  }

/* Production 9 */
  final public void export() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 34:
    case 44:
    case 45:
    case 49:
      type_dcl();
      jj_consume_token(9);
      break;
    case 17:
      const_dcl();
      jj_consume_token(9);
      break;
    case 58:
      except_dcl();
      jj_consume_token(9);
      break;
    case 56:
    case 57:
      attr_dcl();
      jj_consume_token(9);
      break;
    case 16:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 53:
    case 59:
    case 60:
    case ID:
      op_dcl();
      jj_consume_token(9);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Production 10 */
/*
void inheritance_spec() :
{}
{
  ":" scoped_name() ( "," scoped_name() )*
}
*/
  final public Vector inheritance_spec() throws ParseException {
    Vector inherited_from = new Vector ();
    String name = "";
    jj_consume_token(14);
    name = scoped_name();
                             inherited_from.addElement (name);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_4;
      }
      jj_consume_token(15);
      name = scoped_name();
                               inherited_from.addElement (name);
    }
    {if (true) return inherited_from;}
    throw new Error("Missing return statement in function");
  }

/* Production 11 */
/*
void scoped_name() :
{}
{
  [ "::" ] identifier() ( "::" identifier() )*
}
*/
  final public String scoped_name() throws ParseException {
    String name = "";
    Identifier id = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
      jj_consume_token(16);
          name = name + "::";
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    id = identifier();
                                                    name = name + id.getName ();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 16:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_5;
      }
      jj_consume_token(16);
          name = name + "::";
      id = identifier();
                                                  name = name + id.getName ();
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 12 */
/*
void const_dcl() #ConstElement :
{}
{
  "const" const_type() identifier() "=" const_exp()
}
*/
  final public void const_dcl() throws ParseException {
 /*@bgen(jjtree) ConstElement */
  ConstElement jjtn000 = new ConstElement(JJTCONSTELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String type, exp, name; Identifier id;
      jj_consume_token(17);
      type = const_type();
      id = identifier();
      jj_consume_token(18);
      exp = const_exp();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setType (type); jjtn000.setName (id.getName ()); jjtn000.setExpression (exp);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 13 */
/*
void const_type() :
{}
{
  integer_type()
|
  char_type()
|
  boolean_type()
|
  floating_pt_type()
|
  string_type()
|
  scoped_name()
}
*/
  final public String const_type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
    case 38:
    case 39:
    String type;
      type = integer_type();
    {if (true) return type;}
      break;
    case 40:
      type = char_type();
    {if (true) return type;}
      break;
    case 41:
      type = boolean_type();
    {if (true) return type;}
      break;
    case 35:
    case 36:
      type = floating_pt_type();
    {if (true) return type;}
      break;
    case 53:
      type = string_type();
    {if (true) return type;}
      break;
    case 16:
    case ID:
      type = scoped_name();
    {if (true) return type;}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 14 */
/*
void const_exp() :
{}
{
  or_expr()
}
*/
  final public String const_exp() throws ParseException {
    String name;
    name = or_expr();
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 15 */
/*
void or_expr() :
{}
{
  xor_expr() ( "|" xor_expr() )*
}
*/
  final public String or_expr() throws ParseException {
    String name, tmp;
    name = xor_expr();
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 19:
        ;
        break;
      default:
        jj_la1[11] = jj_gen;
        break label_6;
      }
      jj_consume_token(19);
      tmp = xor_expr();
                                            name = name + tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 16 */
/*
void xor_expr() :
{}
{
  and_expr() ( "^" and_expr() )*
}
*/
  final public String xor_expr() throws ParseException {
    String name, tmp;
    name = and_expr();
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 20:
        ;
        break;
      default:
        jj_la1[12] = jj_gen;
        break label_7;
      }
      jj_consume_token(20);
      tmp = and_expr();
                                             name = name + "^" + tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 17 */
/*
void and_expr() :
{}
{
  shift_expr() ( "&" shift_expr() )*
}
*/
  final public String and_expr() throws ParseException {
    String name, tmp;
    name = shift_expr();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 21:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_8;
      }
      jj_consume_token(21);
      tmp = shift_expr();
                                                 name = name + "&" + tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 18 */
/*
void shift_expr() :
{}
{
  add_expr() ( ( ">>" | "<<" ) add_expr() )*
}
*/
  final public String shift_expr() throws ParseException {
    String name, tmp;
    name = add_expr();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 22:
      case 23:
        ;
        break;
      default:
        jj_la1[14] = jj_gen;
        break label_9;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 22:
        jj_consume_token(22);
                             name += ">>";
        break;
      case 23:
        jj_consume_token(23);
                                                  name += "<<";
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      tmp = add_expr();
                                                                                    name += tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 19 */
/*
void add_expr() :
{}
{
  mult_expr() ( ( "+" | "-" ) mult_expr() )*
}
*/
  final public String add_expr() throws ParseException {
    String name, tmp;
    name = mult_expr();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 24:
      case 25:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_10;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 24:
        jj_consume_token(24);
                              name += "+";
        break;
      case 25:
        jj_consume_token(25);
                                                  name += "-";
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      tmp = mult_expr();
                                                                                    name += tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 20 */
/*
void mult_expr() :
{}
{
  unary_expr() ( ( "*" | "/" | "%" ) unary_expr() )*
}
*/
  final public String mult_expr() throws ParseException {
    String name, tmp;
    name = unary_expr();
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 26:
      case 27:
      case 28:
        ;
        break;
      default:
        jj_la1[18] = jj_gen;
        break label_11;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 26:
        jj_consume_token(26);
                               name += "*";
        break;
      case 27:
        jj_consume_token(27);
                                                    name += "/";
        break;
      case 28:
        jj_consume_token(28);
                                                                         name += "%";
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      tmp = unary_expr();
                                                                                                             name += tmp;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 21 */
/*
void unary_expr() :
{}
{
  [ unary_operator() ] primary_expr()
}
*/
  final public String unary_expr() throws ParseException {
    String name = "", tmp = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 24:
    case 25:
    case 29:
      tmp = unary_operator();
      break;
    default:
      jj_la1[20] = jj_gen;
      ;
    }
    name = primary_expr();
    {if (true) return name + tmp;}
    throw new Error("Missing return statement in function");
  }

/* Production 22 */
/*
void unary_operator() :
{}
{
  "-"
|
  "+"
|
  "~"
}
*/
  final public String unary_operator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 25:
      jj_consume_token(25);
    {if (true) return "-";}
      break;
    case 24:
      jj_consume_token(24);
    {if (true) return "+";}
      break;
    case 29:
      jj_consume_token(29);
    {if (true) return "~";}
      break;
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 23 */
/*
void primary_expr() :
{}
{
  scoped_name()
|
  literal()
|
  "(" const_exp() ")"
}
*/
  final public String primary_expr() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
    case ID:
    String name;
      name = scoped_name();
    {if (true) return name;}
      break;
    case 32:
    case 33:
    case OCTALINT:
    case DECIMALINT:
    case HEXADECIMALINT:
    case FLOATONE:
    case FLOATTWO:
    case CHARACTER:
    case STRING:
      name = literal();
    {if (true) return name;}
      break;
    case 30:
      jj_consume_token(30);
      name = const_exp();
      jj_consume_token(31);
    {if (true) return "(" + name + ")";}
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 24 */
/*
void literal() :
{}
{
  integer_literal()
|
  string_literal()
|
  character_literal()
|
  floating_pt_literal()
|
  boolean_literal()
}
*/
  final public String literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OCTALINT:
    case DECIMALINT:
    case HEXADECIMALINT:
    String name;
      name = integer_literal();
    {if (true) return name;}
      break;
    case STRING:
      name = string_literal();
    {if (true) return name;}
      break;
    case CHARACTER:
      name = character_literal();
    {if (true) return name;}
      break;
    case FLOATONE:
    case FLOATTWO:
      name = floating_pt_literal();
    {if (true) return name;}
      break;
    case 32:
    case 33:
      name = boolean_literal();
    {if (true) return name;}
      break;
    default:
      jj_la1[23] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 25 */
/*
void boolean_literal() :
{}
{
  "TRUE"
|
  "FALSE"
}
*/
  final public String boolean_literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 32:
      jj_consume_token(32);
    {if (true) return "TRUE";}
      break;
    case 33:
      jj_consume_token(33);
    {if (true) return "FALSE";}
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 26 */
/*
void positive_int_const() :
{}
{
  const_exp()
}
*/
  final public String positive_int_const() throws ParseException {
    String name;
    name = const_exp();
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 27 */
/*
void type_dcl() #TypeElement :
{}
{
  "typedef" type_declarator()
|
  struct_type()
|
  union_type()
|
  enum_type()
}
*/

/* void type_dcl() : */
  final public void type_dcl() throws ParseException {
 /*@bgen(jjtree) TypeElement */
  TypeElement jjtn000 = new TypeElement(JJTTYPEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 34:
    String type;
        jj_consume_token(34);
        type = type_declarator();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setType (type); /* System.out.println ("IDL.jjt " + jjtThis.getType ()); */
        break;
      case 44:
        type = struct_type();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setName (type);
    jjtn000.setType ("struct");
        break;
      case 45:
        type = union_type();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setName (type);
    jjtn000.setType ("union");
        break;
      case 49:
        type = enum_type();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setName (type);
    jjtn000.setType ("enum");
        break;
      default:
        jj_la1[25] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 28 */
/*
void type_declarator() :
{}
{
  type_spec() declarators()
}
*/
  final public String type_declarator() throws ParseException {
    String type;
    type = type_spec();
    declarators();
    {if (true) return type;}
    throw new Error("Missing return statement in function");
  }

/* Production 29 */
/*
void type_spec() :
{}
{
  simple_type_spec()
|
  constr_type_spec()
}
*/
  final public String type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 50:
    case 53:
    case ID:
   String name;
      name = simple_type_spec();
   {if (true) return name;}
      break;
    case 44:
    case 45:
    case 49:
      name = constr_type_spec();
   {if (true) return name;}
      break;
    default:
      jj_la1[26] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 30 */
/*
void simple_type_spec() :
{}
{
  base_type_spec()
|
  template_type_spec()
|
  scoped_name()
}
*/
  final public String simple_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
   String name;
      name = base_type_spec();
   {if (true) return name;}
      break;
    case 50:
    case 53:
      name = template_type_spec();
   {if (true) return name;}
      break;
    case 16:
    case ID:
      name = scoped_name();
   {if (true) return name;}
      break;
    default:
      jj_la1[27] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 31 */
/*
void base_type_spec() :
{}
{
  floating_pt_type()
|
  integer_type()
|
  char_type()
|
  boolean_type()
|
  octet_type()
|
  any_type()
}
*/
  final public String base_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 35:
    case 36:
    String type;
      type = floating_pt_type();
    {if (true) return type;}
      break;
    case 37:
    case 38:
    case 39:
      type = integer_type();
    {if (true) return type;}
      break;
    case 40:
      type = char_type();
    {if (true) return type;}
      break;
    case 41:
      type = boolean_type();
    {if (true) return type;}
      break;
    case 42:
      type = octet_type();
    {if (true) return type;}
      break;
    case 43:
      type = any_type();
    {if (true) return type;}
      break;
    default:
      jj_la1[28] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 32 */
/*
void template_type_spec() :
{}
{
  sequence_type()
|
  string_type()
}
*/
  final public String template_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 50:
    String type;
      type = sequence_type();
    {if (true) return type;}
      break;
    case 53:
      type = string_type();
    {if (true) return type;}
      break;
    default:
      jj_la1[29] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 33 */
/*
void constr_type_spec() :
{}
{
  struct_type()
|
  union_type()
|
  enum_type()
}
*/
  final public String constr_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 44:
    String type;
      type = struct_type();
    {if (true) return type;}
      break;
    case 45:
      type = union_type();
    {if (true) return type;}
      break;
    case 49:
      type = enum_type();
    {if (true) return type;}
      break;
    default:
      jj_la1[30] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 34 */
  final public void declarators() throws ParseException {
    declarator();
    label_12:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        ;
        break;
      default:
        jj_la1[31] = jj_gen;
        break label_12;
      }
      jj_consume_token(15);
      declarator();
    }
  }

/* Production 35 */
/*
void declarator() :
{}
{
  LOOKAHEAD(2)
  complex_declarator()
|
  simple_declarator()
}
*/
  final public void declarator() throws ParseException {
 /*@bgen(jjtree) DeclaratorElement */
  DeclaratorElement jjtn000 = new DeclaratorElement(JJTDECLARATORELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      if (jj_2_2(2)) {
    String dim;
        dim = complex_declarator();
                               jjtree.closeNodeScope(jjtn000, true);
                               jjtc000 = false;
                               jjtn000.setDimension (dim);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ID:
          simple_declarator();
          break;
        default:
          jj_la1[32] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 36 */
/*
void simple_declarator() :
{}
{
  identifier()
}
*/
  final public String simple_declarator() throws ParseException {
    Identifier id;
    id = identifier();
    {if (true) return id.getName ();}
    throw new Error("Missing return statement in function");
  }

/* Production 37 */
/*
void complex_declarator() :
{}
{
  array_declarator()
}
*/
  final public String complex_declarator() throws ParseException {
    String dim;
    dim = array_declarator();
    {if (true) return dim;}
    throw new Error("Missing return statement in function");
  }

/* Production 38 */
/*
void floating_pt_type() :
{}
{
  "float"
|
  "double"
}
*/
  final public String floating_pt_type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 35:
      jj_consume_token(35);
    {if (true) return "float";}
      break;
    case 36:
      jj_consume_token(36);
    {if (true) return "double";}
      break;
    default:
      jj_la1[33] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 39 */
/*
void integer_type() :
{}
{
  signed_int()
|
  unsigned_int()
}
*/
  final public String integer_type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
    case 38:
    String type;
      type = signed_int();
    {if (true) return type;}
      break;
    case 39:
      type = unsigned_int();
    {if (true) return type;}
      break;
    default:
      jj_la1[34] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 40 */
/*
void signed_int() :
{}
{
  signed_long_int()
|
  signed_short_int()
}
*/
  final public String signed_int() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
    String type;
      type = signed_long_int();
    {if (true) return type;}
      break;
    case 38:
      type = signed_short_int();
    {if (true) return type;}
      break;
    default:
      jj_la1[35] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 41 */
/*
void signed_long_int() :
{}
{
  "long"
}
*/
  final public String signed_long_int() throws ParseException {
    jj_consume_token(37);
    {if (true) return "long";}
    throw new Error("Missing return statement in function");
  }

/* Production 42 */
/*
void signed_short_int() :
{}
{
  "short"
}
*/
  final public String signed_short_int() throws ParseException {
    jj_consume_token(38);
    {if (true) return "short";}
    throw new Error("Missing return statement in function");
  }

/* Production 43 */
/*
void unsigned_int() :
{}
{
  LOOKAHEAD(2)
  unsigned_long_int()
|
  unsigned_short_int()
}
*/
  final public String unsigned_int() throws ParseException {
    if (jj_2_3(2)) {
    String type;
      type = unsigned_long_int();
    {if (true) return type;}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 39:
    String type;
        type = unsigned_short_int();
    {if (true) return type;}
        break;
      default:
        jj_la1[36] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

/* Production 44 */
/*
void unsigned_long_int() :
{}
{
  "unsigned" "long"
}
*/
  final public String unsigned_long_int() throws ParseException {
    jj_consume_token(39);
    jj_consume_token(37);
    {if (true) return "unsigned long";}
    throw new Error("Missing return statement in function");
  }

/* Production 45 */
/*
void unsigned_short_int() :
{}
{
  "unsigned" "short"
}
*/
  final public String unsigned_short_int() throws ParseException {
    jj_consume_token(39);
    jj_consume_token(38);
    {if (true) return "unsigned short";}
    throw new Error("Missing return statement in function");
  }

/* Production 46 */
/*
void char_type() :
{}
{
  "char"
}
*/
  final public String char_type() throws ParseException {
    jj_consume_token(40);
    {if (true) return "char";}
    throw new Error("Missing return statement in function");
  }

/* Production 47 */
/*
void boolean_type() :
{}
{
  "boolean"
}
*/
  final public String boolean_type() throws ParseException {
    jj_consume_token(41);
    {if (true) return "boolean";}
    throw new Error("Missing return statement in function");
  }

/* Production 48 */
/*
void octet_type() :
{}
{
  "octet"
}
*/
  final public String octet_type() throws ParseException {
    jj_consume_token(42);
    {if (true) return "octet";}
    throw new Error("Missing return statement in function");
  }

/* Production 49 */
/*
void any_type() :
{}
{
  "any"
}
*/
  final public String any_type() throws ParseException {
    jj_consume_token(43);
    {if (true) return "any";}
    throw new Error("Missing return statement in function");
  }

/* Production 50 */
/*
void struct_type() :
{}
{
  "struct" identifier() "{" member_list() "}"
}
*/
  final public String struct_type() throws ParseException {
 /*@bgen(jjtree) StructTypeElement */
  StructTypeElement jjtn000 = new StructTypeElement(JJTSTRUCTTYPEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String name; Identifier id; Vector vect = new Vector ();
      jj_consume_token(44);
      id = identifier();
                               name = id.getName (); jjtn000.setType ("struct");
      jj_consume_token(11);
      member_list();
      jj_consume_token(12);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    {if (true) return name;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

/* Production 51 */
  final public void member_list() throws ParseException {
    label_13:
    while (true) {
      member();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 16:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 49:
      case 50:
      case 53:
      case ID:
        ;
        break;
      default:
        jj_la1[37] = jj_gen;
        break label_13;
      }
    }
  }

/* Production 52 */
  final public void member() throws ParseException {
 /*@bgen(jjtree) MemberElement */
  MemberElement jjtn000 = new MemberElement(JJTMEMBERELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String type, name = "";
      type = type_spec();
                       jjtn000.setType (type); jjtn000.setName (name);
      declarators();
      jj_consume_token(9);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/*
void member() : 
{}
{
  { String type, name = "";}
  type = type_spec() 
  declarators() ";"
}
*/
/* Production 53 */
/*
void union_type() :
{}
{
  "union" identifier() "switch" "(" switch_type_spec() ")" "{" switch_body() "}"
}
*/
  final public String union_type() throws ParseException {
 /*@bgen(jjtree) UnionTypeElement */
  UnionTypeElement jjtn000 = new UnionTypeElement(JJTUNIONTYPEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String name; Identifier id; Vector vect = new Vector (); String type;
      jj_consume_token(45);
      id = identifier();
                              name = id.getName (); jjtn000.setType ("union");
      jj_consume_token(46);
      jj_consume_token(30);
      type = switch_type_spec();
      jj_consume_token(31);
    jjtn000.setSwitchType (type);
      jj_consume_token(11);
      switch_body();
      jj_consume_token(12);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    {if (true) return name;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

/* Production 54 */
/*
void switch_type_spec() :
{}
{
  integer_type()
|
  char_type()
|
  boolean_type()
|
  enum_type()
|
  scoped_name()
}
*/
  final public String switch_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
    case 38:
    case 39:
    String type;
      type = integer_type();
    {if (true) return type;}
      break;
    case 40:
      type = char_type();
    {if (true) return type;}
      break;
    case 41:
      type = boolean_type();
    {if (true) return type;}
      break;
    case 49:
      type = enum_type();
    {if (true) return type;}
      break;
    case 16:
    case ID:
      type = scoped_name();
    {if (true) return type;}
      break;
    default:
      jj_la1[38] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 55 */
  final public void switch_body() throws ParseException {
    label_14:
    while (true) {
      casex();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 47:
      case 48:
        ;
        break;
      default:
        jj_la1[39] = jj_gen;
        break label_14;
      }
    }
  }

/* Production 56 */
  final public void casex() throws ParseException {
 /*@bgen(jjtree) UnionMemberElement */
  UnionMemberElement jjtn000 = new UnionMemberElement(JJTUNIONMEMBERELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String cases = "", tmp, type;
      label_15:
      while (true) {
        tmp = case_label();
                        cases += tmp + ", ";
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 47:
        case 48:
          ;
          break;
        default:
          jj_la1[40] = jj_gen;
          break label_15;
        }
      }
      type = element_spec();
   jjtn000.setType (type); jjtn000.setCases (cases.substring (0, cases.length () - 2));
      jj_consume_token(9);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 57 */
/*
void case_label() : 
{}
{
  "case" const_exp() ":"
|
  "default" ":"
}
*/
  final public String case_label() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 47:
   String label = "", exp;
      jj_consume_token(47);
      exp = const_exp();
                             label += exp;
      jj_consume_token(14);
    {if (true) return label;}
      break;
    case 48:
      jj_consume_token(48);
      jj_consume_token(14);
    {if (true) return "default";}
      break;
    default:
      jj_la1[41] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 58 */
  final public String element_spec() throws ParseException {
    String type, name = "";
    type = type_spec();
    declarator();
    {if (true) return type;}
    throw new Error("Missing return statement in function");
  }

/* Production 59 */
/*
void enum_type() :
{}
{
  "enum" identifier() "{" enumerator() ( "," enumerator() )* "}"
}
*/
  final public String enum_type() throws ParseException {
 /*@bgen(jjtree) EnumTypeElement */
  EnumTypeElement jjtn000 = new EnumTypeElement(JJTENUMTYPEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String name; Identifier id; Vector vect = new Vector ();
      jj_consume_token(49);
      id = identifier();
                             name = id.getName (); jjtn000.setType ("enum");
      jj_consume_token(11);
      enumerator();
      label_16:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          ;
          break;
        default:
          jj_la1[42] = jj_gen;
          break label_16;
        }
        jj_consume_token(15);
        enumerator();
      }
      jj_consume_token(12);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    {if (true) return name;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

/* Production 60 */
/*
void enumerator() :
{}
{
  identifier()
}
*/
  final public void enumerator() throws ParseException {
 /*@bgen(jjtree) ConstElement */
  ConstElement jjtn000 = new ConstElement(JJTCONSTELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    Identifier id;
      id = identifier();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
     jjtn000.setType ("");
     jjtn000.setExpression ("");
     jjtn000.setName (id.getName ());
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 61 */
/*
void sequence_type() :
{}
{
  "sequence" "<" simple_type_spec() [ "," positive_int_const() ] ">"
}
*/
  final public String sequence_type() throws ParseException {
    String type, num = "", retval = "";
    jj_consume_token(50);
    jj_consume_token(51);
    type = simple_type_spec();
                                            retval = "sequence <" + type;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 15:
      jj_consume_token(15);
      num = positive_int_const();
                                    retval = retval + ", " + num;
      break;
    default:
      jj_la1[43] = jj_gen;
      ;
    }
    jj_consume_token(52);
                                                                          retval += ">";
    {if (true) return retval;}
    throw new Error("Missing return statement in function");
  }

/* Production 62 */
/*
void string_type() :
{}
{
  "string" [ "<" positive_int_const() ">" ]
}
*/
  final public String string_type() throws ParseException {
    String name, tmp;
    jj_consume_token(53);
             name = "string";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 51:
      jj_consume_token(51);
         name = name + "<";
      tmp = positive_int_const();
                                                          name += tmp;
      jj_consume_token(52);
                                                                             name = name + ">";
      break;
    default:
      jj_la1[44] = jj_gen;
      ;
    }
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

/* Production 63 */
/*
void array_declarator() :
{}
{
  identifier() ( fixed_array_size() )+
}
*/
  final public String array_declarator() throws ParseException {
    String tmp, dim = "";
    identifier();
    label_17:
    while (true) {
      tmp = fixed_array_size();
                                            dim += tmp;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 54:
        ;
        break;
      default:
        jj_la1[45] = jj_gen;
        break label_17;
      }
    }
    {if (true) return dim;}
    throw new Error("Missing return statement in function");
  }

/* Production 64 */
/*
void fixed_array_size() :
{}
{
  "[" positive_int_const() "]"
}
*/
  final public String fixed_array_size() throws ParseException {
    String dim;
    jj_consume_token(54);
    dim = positive_int_const();
    jj_consume_token(55);
    {if (true) return ("[" + dim + "]");}
    throw new Error("Missing return statement in function");
  }

/* Production 65 */
  final public void attr_dcl() throws ParseException {
 /*@bgen(jjtree) AttributeElement */
  AttributeElement jjtn000 = new AttributeElement(JJTATTRIBUTEELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String name, other, type;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 56:
        jj_consume_token(56);
                jjtn000.setReadOnly (true);
        break;
      default:
        jj_la1[46] = jj_gen;
        ;
      }
      jj_consume_token(57);
      type = param_type_spec();
                            jjtn000.setType (type);
      name = simple_declarator();
                              jjtn000.setName (name);
      label_18:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          ;
          break;
        default:
          jj_la1[47] = jj_gen;
          break label_18;
        }
        jj_consume_token(15);
        other = simple_declarator();
                                     jjtn000.addOther (other);
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 66 */
  final public void except_dcl() throws ParseException {
 /*@bgen(jjtree) ExceptionElement */
  ExceptionElement jjtn000 = new ExceptionElement(JJTEXCEPTIONELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(58);
      identifier();
      jj_consume_token(11);
      label_19:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 16:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 49:
        case 50:
        case 53:
        case ID:
          ;
          break;
        default:
          jj_la1[48] = jj_gen;
          break label_19;
        }
        member();
      }
      jj_consume_token(12);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 67 */
/*
void op_dcl() #OperationElement :
{}
{
  [ op_attribute() ] op_type_spec() identifier() parameter_dcls() [ raises_expr() ] [ context_expr() ]
}
*/
  final public void op_dcl() throws ParseException {
 /*@bgen(jjtree) OperationElement */
  OperationElement jjtn000 = new OperationElement(JJTOPERATIONELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String attr;
    Vector params, exceptions, contexts;
    Identifier name;
    /* Element returnType; */
    String returnType;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 59:
        attr = op_attribute();
                             jjtn000.setAttribute (attr);
        break;
      default:
        jj_la1[49] = jj_gen;
        ;
      }
      returnType = op_type_spec();
                                   jjtn000.setReturnType (returnType);
      name = identifier();
                           jjtn000.setName (name.getName ());
      parameter_dcls();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 64:
        exceptions = raises_expr();
                                  jjtn000.setExceptions (exceptions);
        break;
      default:
        jj_la1[50] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 65:
        contexts = context_expr();
                                 jjtn000.setContexts (contexts);
        break;
      default:
        jj_la1[51] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 68 */
/*
void op_attribute() :
{}
{
  "oneway"
}
*/
  final public String op_attribute() throws ParseException {
    jj_consume_token(59);
    {if (true) return "oneway";}
    throw new Error("Missing return statement in function");
  }

/* Production 69 */
/*
void op_type_spec() :
{}
{
  param_type_spec()
|
  "void"
}
*/
/*
Identifier op_type_spec() :
{}
{
  {
    String type; 
    Identifier id;
  }
  type = param_type_spec()
  {
    id = new Identifier (-1); 
    id.setName (type);
    return id;
  }
|
  "void"  
  {
     id = new Identifier (-1); 
     id.setName ("void");
     return id;
  }
}
*/
  final public String op_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 53:
    case ID:
    String type;
      type = param_type_spec();
    {if (true) return type;}
      break;
    case 60:
      jj_consume_token(60);
     {if (true) return "void";}
      break;
    default:
      jj_la1[52] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 70 */
  final public void parameter_dcls() throws ParseException {
    jj_consume_token(30);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 61:
    case 62:
    case 63:
      param_dcl();
      label_20:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          ;
          break;
        default:
          jj_la1[53] = jj_gen;
          break label_20;
        }
        jj_consume_token(15);
        param_dcl();
      }
      break;
    default:
      jj_la1[54] = jj_gen;
      ;
    }
    jj_consume_token(31);
  }

/*
String parameter_dcls() :
{}
{
  { 
    String name = "", tmp; 
    Vector params = new Vector ();
  }
  "(" [ name = param_dcl() {params.addElement (name);} 
   ( "," name = param_dcl() { params.addElement (name);} )* ] ")"
  { return params;}
}
*/
/* Production 71 */
/*
void param_dcl() :
{}
{
  param_attribute() param_type_spec() simple_declarator()
}
*/
/*
String param_dcl() :
{}
{
  { String attr, type, name;}
  attr = param_attribute() type = param_type_spec() name = simple_declarator()
  { return attr + " " + type + " " + name;}
}
*/
  final public void param_dcl() throws ParseException {
 /*@bgen(jjtree) ParameterElement */
  ParameterElement jjtn000 = new ParameterElement(JJTPARAMETERELEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
    String type, name; int attr;
      attr = param_attribute();
      type = param_type_spec();
      name = simple_declarator();
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.setAttribute (attr); jjtn000.setType (type); jjtn000.setName (name);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

/* Production 72 */
/*
void param_attribute() :
{}
{
  "in"
|
  "out"
|
  "inout"
}
*/
  final public int param_attribute() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 61:
      jj_consume_token(61);
    {if (true) return 0;}
      break;
    case 62:
      jj_consume_token(62);
    {if (true) return 2;}
      break;
    case 63:
      jj_consume_token(63);
    {if (true) return 1;}
      break;
    default:
      jj_la1[55] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 73 */
/*
void raises_expr() :
{}
{
  "raises" "(" scoped_name() ( "," scoped_name() )* ")"
}
*/
  final public Vector raises_expr() throws ParseException {
    String name; Vector es = new Vector ();
    jj_consume_token(64);
    jj_consume_token(30);
    name = scoped_name();
                                      es.addElement (name);
    label_21:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        ;
        break;
      default:
        jj_la1[56] = jj_gen;
        break label_21;
      }
      jj_consume_token(15);
      name = scoped_name();
                                         es.addElement (name);
    }
    jj_consume_token(31);
    {if (true) return es;}
    throw new Error("Missing return statement in function");
  }

/* Production 74 */
/*
void context_expr() :
{}
{
  "context" "(" string_literal() ( "," string_literal() )* ")"
}
*/
  final public Vector context_expr() throws ParseException {
    String name; Vector cs = new Vector ();
    jj_consume_token(65);
    jj_consume_token(30);
    name = string_literal();
                                          cs.addElement (name);
    label_22:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        ;
        break;
      default:
        jj_la1[57] = jj_gen;
        break label_22;
      }
      jj_consume_token(15);
      name = string_literal();
                                             cs.addElement (name);
    }
    jj_consume_token(31);
    {if (true) return cs;}
    throw new Error("Missing return statement in function");
  }

/* Production 75 */
/*
void param_type_spec() :
{}
{
  base_type_spec()
|
  string_type()
|
  scoped_name()
}
*/
  final public String param_type_spec() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    String type;
      type = base_type_spec();
    {if (true) return type;}
      break;
    case 53:
      type = string_type();
    {if (true) return type;}
      break;
    case 16:
    case ID:
      type = scoped_name();
    {if (true) return type;}
      break;
    default:
      jj_la1[58] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Definitions of complex regular expressions follow */
  final public Identifier identifier() throws ParseException {
 /*@bgen(jjtree) Identifier */
   Identifier jjtn000 = new Identifier(JJTIDENTIFIER);
   boolean jjtc000 = true;
   jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(ID);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
     jjtn000.setName (t.image);
     jjtn000.setLine (t.beginLine);
     {if (true) return jjtn000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

/*
void integer_literal() :
{}
{
  <OCTALINT>
|
  <DECIMALINT>
|
  <HEXADECIMALINT>
}
*/
  final public String integer_literal() throws ParseException {
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OCTALINT:
      t = jj_consume_token(OCTALINT);
    {if (true) return t.image;}
      break;
    case DECIMALINT:
      t = jj_consume_token(DECIMALINT);
    {if (true) return t.image;}
      break;
    case HEXADECIMALINT:
      t = jj_consume_token(HEXADECIMALINT);
    {if (true) return t.image;}
      break;
    default:
      jj_la1[59] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/*
void string_literal() :
{}
{
  <STRING>
}
*/
  final public String string_literal() throws ParseException {
  Token t;
    t = jj_consume_token(STRING);
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

/*
void character_literal() :
{}
{
  <CHARACTER>
}
*/
  final public String character_literal() throws ParseException {
  Token t;
    t = jj_consume_token(CHARACTER);
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

/*
void floating_pt_literal() :
{}
{
  <FLOATONE>
|
  <FLOATTWO>
}
*/
  final public String floating_pt_literal() throws ParseException {
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FLOATONE:
      t = jj_consume_token(FLOATONE);
    {if (true) return t.image;}
      break;
    case FLOATTWO:
      t = jj_consume_token(FLOATTWO);
    {if (true) return t.image;}
      break;
    default:
      jj_la1[60] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_1();
    jj_save(0, xla);
    return retval;
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_2();
    jj_save(1, xla);
    return retval;
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    boolean retval = !jj_3_3();
    jj_save(2, xla);
    return retval;
  }

  final private boolean jj_3R_29() {
    if (jj_3R_31()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_24()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_26() {
    if (jj_scan_token(13)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_28()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_29()) jj_scanpos = xsp;
    else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_25() {
    if (jj_scan_token(39)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(37)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_25()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_23() {
    if (jj_3R_26()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(11)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_32() {
    if (jj_scan_token(54)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_30() {
    if (jj_3R_32()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_31() {
    if (jj_scan_token(14)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_27() {
    if (jj_3R_28()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    if (jj_3R_30()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_30()) { jj_scanpos = xsp; break; }
      if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    }
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_23()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_24() {
    if (jj_3R_27()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_28() {
    if (jj_scan_token(ID)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  public IDLParserTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[61];
  final private int[] jj_la1_0 = {0x22400,0x22400,0x22400,0x2000,0x4000,0x30000,0x30000,0x8000,0x10000,0x10000,0x10000,0x80000,0x100000,0x200000,0xc00000,0xc00000,0x3000000,0x3000000,0x1c000000,0x1c000000,0x23000000,0x23000000,0x40010000,0x0,0x0,0x0,0x10000,0x10000,0x0,0x0,0x0,0x8000,0x0,0x0,0x0,0x0,0x0,0x10000,0x10000,0x0,0x0,0x0,0x8000,0x8000,0x0,0x0,0x0,0x8000,0x10000,0x0,0x0,0x0,0x10000,0x8000,0x0,0x0,0x8000,0x8000,0x10000,0x0,0x0,};
  final private int[] jj_la1_1 = {0x4023004,0x4023004,0x4023004,0x0,0x0,0x1f223ffc,0x1f223ffc,0x0,0x0,0x0,0x2003f8,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3,0x3,0x3,0x23004,0x263ff8,0x240ff8,0xff8,0x240000,0x23000,0x0,0x0,0x18,0xe0,0x60,0x80,0x263ff8,0x203e0,0x18000,0x18000,0x18000,0x0,0x0,0x80000,0x400000,0x1000000,0x0,0x263ff8,0x8000000,0x0,0x0,0x10200ff8,0x0,0xe0000000,0xe0000000,0x0,0x0,0x200ff8,0x0,0x0,};
  final private int[] jj_la1_2 = {0x0,0x0,0x0,0x0,0x0,0x4,0x4,0x0,0x0,0x0,0x4,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3fc,0x3f8,0x0,0x0,0x4,0x4,0x0,0x0,0x0,0x0,0x4,0x0,0x0,0x0,0x0,0x4,0x4,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x4,0x0,0x1,0x2,0x4,0x0,0x0,0x0,0x0,0x0,0x4,0x38,0xc0,};
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public IDLParser(java.io.InputStream stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new IDLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public IDLParser(java.io.Reader stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new IDLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public IDLParser(IDLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(IDLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 61; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    return (jj_scanpos.kind != kind);
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration enum = jj_expentries.elements(); enum.hasMoreElements();) {
        int[] oldentry = (int[])(enum.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[74];
    for (int i = 0; i < 74; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 61; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 74; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 3; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
