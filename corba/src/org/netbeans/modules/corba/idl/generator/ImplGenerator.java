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

package com.netbeans.enterprise.modules.corba.idl.generator;

import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;
import java.lang.reflect.*;

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.cookies.*;
import org.openide.src.*;
import org.openide.src.nodes.*;
import org.openide.*;

import com.netbeans.*;


import com.netbeans.enterprise.modules.corba.idl.src.*;
import com.netbeans.enterprise.modules.corba.settings.*;
import com.netbeans.enterprise.modules.corba.*;

/*
 * @author Karel Gardas
 */
 
public class ImplGenerator {

   //public static final boolean DEBUG = true;
   private static final boolean DEBUG = false;


   private IDLElement src;

   private String IMPLBASE_IMPL_PREFIX;
   private String IMPLBASE_IMPL_POSTFIX;
   private String EXT_CLASS_PREFIX;
   private String EXT_CLASS_POSTFIX;
   private String TIE_IMPL_PREFIX;
   private String TIE_IMPL_POSTFIX;
   private String IMPL_INT_PREFIX;
   private String IMPL_INT_POSTFIX;
   private boolean TIE;

   private int IN_MODULE_PACKAGE = 0;
   private int IN_IDL_PACKAGE = 1;

   private int where_generate = IN_IDL_PACKAGE;

   private IDLDataObject ido;

   public ImplGenerator (IDLDataObject _do) {
      
      ido = _do;

      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	 (CORBASupportSettings.class, true);

      IMPLBASE_IMPL_PREFIX = css.getImplBasePrefix ();
      IMPLBASE_IMPL_POSTFIX = css.getImplBasePostfix ();
      EXT_CLASS_PREFIX = css.getExtClassPrefix ();
      EXT_CLASS_POSTFIX = css.getExtClassPostfix ();
      TIE_IMPL_PREFIX = css.getTiePrefix ();
      TIE_IMPL_POSTFIX = css.getTiePostfix ();
      IMPL_INT_PREFIX = css.getImplIntPrefix ();
      IMPL_INT_POSTFIX = css.getImplIntPostfix ();
      TIE = css.isTie ();
   }

   public ImplGenerator () {
      IMPLBASE_IMPL_PREFIX = "";
      IMPLBASE_IMPL_POSTFIX = "Impl";
      EXT_CLASS_PREFIX = "_";
      EXT_CLASS_POSTFIX = "ImplBase";
      TIE_IMPL_PREFIX = "";
      TIE_IMPL_POSTFIX = "ImplTIE";
      IMPL_INT_PREFIX = "";
      IMPL_INT_POSTFIX = "Operations";
      TIE = false;
   }

   public void setSources (IDLElement e) {
      src = e;
   }

   public String simple2java (String idl_type) {
      String type = "";

      if (idl_type.equals ("void"))
	 type = "void";
      if (idl_type.equals ("boolean"))
	 type = "boolean";
      if (idl_type.equals ("char"))
	 type = "char";
      if (idl_type.equals ("wchar"))
	 type = "char";
      if (idl_type.equals ("octet"))
	 type = "byte";
      if (idl_type.equals ("string"))
	 type = "String";
      if (idl_type.equals ("wstring"))
	 type = "String";
      if (idl_type.equals ("short"))
	 type = "short";
      if (idl_type.equals ("unsigned short"))
	 type = "short";
      if (idl_type.equals ("long"))
	 type = "int";
      if (idl_type.equals ("unsigned long"))
	 type = "int";
      if (idl_type.equals ("unsigned long long"))
	 type = "long";
      if (idl_type.equals ("float"))
	 type = "float";
      if (idl_type.equals ("double"))
	 type = "double";

      if (DEBUG)
	 System.out.println ("simple2java: " + type);
      return type;
   }

   public Type type2java (String idl_type) {
      String java_type = simple2java (idl_type);

      if (java_type.equals ("void"))
	 return Type.VOID;
      if (java_type.equals ("boolean"))
	 return Type.BOOLEAN;
      if (java_type.equals ("char"))
	 return Type.CHAR;
      if (java_type.equals ("byte"))
	 return Type.BYTE;
      if (java_type.equals ("String"))
	 return Type.createClass (org.openide.src.Identifier.create ("String"));
      if (java_type.equals ("short"))
	 return Type.SHORT;
      if (java_type.equals ("int"))
	 return Type.INT;
      if (java_type.equals ("long"))
	 return Type.LONG;
      if (java_type.equals ("float"))
	 return Type.FLOAT;
      if (java_type.equals ("double"))
	 return Type.DOUBLE;

      return null;
   }

   public boolean isAbsoluteScopeName (String name) {
      if (DEBUG)
	 System.out.println ("isAbsoluteScopeName (" + name + ");");
      if (name.length () >= 3)
	 if (name.substring (0, 2).equals ("::"))
	    return true;

      return false;
   }

   public boolean isScopeName (String name) {
      if (DEBUG)
         System.out.println ("isScopeName (" + name + ");");
       if (name.indexOf ("::") > -1)
	 return true;
      else
	 return false;
   }

   public String scopedName2javaName (String name, boolean absolute) {
      StringTokenizer st = new StringTokenizer (name, "::");
      String retval = "";
      while (st.hasMoreTokens ()) {
	 retval += st.nextToken () + ".";
      }
      retval = retval.substring (0, retval.length () - 1); 
      // added Package between name of interface and name of data type
      if (!absolute)
	 retval = retval.substring (0, retval.lastIndexOf (".")) + "Package." 
	    + retval.substring (retval.lastIndexOf (".") + 1, retval.length ());
      if (DEBUG) {
	 System.out.println ("scoped name: " + name);
	 System.out.println ("java name: " + retval);
      }
      return retval;
   }

   public boolean isTypeDefinedIn (String idl_type, IDLElement _element) {
      boolean is_in = false;
      if (DEBUG)
	 System.out.println ("isTypeDefinedIn ...");
      for (int i=0; i<_element.getMembers ().size (); i++) {
	 if (_element.getMember (i) instanceof TypeElement)
	    if ((_element.getMember (i).getMember (0) instanceof StructTypeElement)
	     || (_element.getMember (i).getMember (0) instanceof EnumTypeElement)
	     || (_element.getMember (i).getMember (0) instanceof UnionTypeElement))
	       if (((TypeElement)_element.getMember (i).getMember (0)).getName ()
		   .equals (idl_type)) {
	       is_in = true;
	       break;
	    }
      }
      return is_in;
   }

   public boolean isExceptionDefinedIn (InterfaceElement _interface, String exc) {
      boolean is_in = false;
      if (DEBUG)
	 System.out.println ("isExceptionDefinedIn ...");
      for (int i=0; i<_interface.getMembers ().size (); i++) {
	 if (_interface.getMember (i) instanceof ExceptionElement)
	    if (((ExceptionElement)_interface.getMember (i)).getName ()
		   .equals (exc)) {
	       is_in = true;
	       break;
	    }
      }
      return is_in;
   }


   public String modules2package (InterfaceElement _interface) {
      // checking modules
      String modules = "";
      if (_interface.getParent () instanceof ModuleElement) {
	 // has min one Module as Parent
	 IDLElement tmp = _interface;
	 Vector mods = new Vector ();
	 while (tmp.getParent () instanceof ModuleElement) {
	    mods.add (tmp.getParent ().getName ());
	    tmp = tmp.getParent ();
	 }
	 // transform modules names from vector to string in package format
	 for (int i=mods.size () - 1; i>=0; i--) {
	    modules = modules + (String)mods.elementAt (i) + ".";
	 }
      }
      
      return modules;
   }


   public String elements2package (IDLElement _element) {
      // checking modules
      String modules = "";
      if (_element.getParent () instanceof ModuleElement) {
	 // has min one Module as Parent
	 IDLElement tmp = _element;
	 Vector mods = new Vector ();
	 while (tmp.getParent () instanceof ModuleElement) {
	    mods.add (tmp.getParent ().getName ());
	    tmp = tmp.getParent ();
	 }
	 // transform modules names from vector to string in package format
	 for (int i=mods.size () - 1; i>=0; i--) {
	    modules = modules + (String)mods.elementAt (i) + ".";
	 }
      }
      
      return modules;
   }


   public String findType (String name, IDLElement from) {
      Vector mm = from.getMembers ();
      boolean is_in = false;
      for (int i=0; i<mm.size (); i++) {
	 if (from.getMember (i) instanceof TypeElement)
	    if ((from.getMember (i).getMember (0) instanceof StructTypeElement)
		|| (from.getMember (i).getMember (0) instanceof EnumTypeElement)
		|| (from.getMember (i).getMember (0) instanceof UnionTypeElement)) {
	       if (((TypeElement)from.getMember (i).getMember (0)).getName ()
		   .equals (name)) {
		  return ((TypeElement)from.getMember (i).getMember (0)).getType ();
	       }
	    } else {
	       if (((TypeElement)from.getMember (i)).getName ().equals (name))
		  return ((TypeElement)from.getMember (i)).getType ();
	    }
      }
      if (from.getParent () != null)
	 return findType (name, from.getParent ());
      return null;
   }

   public Type hasSimpleParent (String name, IDLElement from) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::hasSimpleParent (" + name + ", " + from + ");");
      Vector mm = from.getMembers ();
      boolean is_in = false;
      
      if (isAbsoluteScopeName (name)) {
	 // is absolute scoped name
	 return hasSimpleParent (name.substring (name.lastIndexOf ("::") + 2, name.length ()), 
				 findTopLevelModuleForType (name, from));
      }
      if (isScopeName (name)) {
	 // is scoped name
	 return hasSimpleParent (name.substring (name.lastIndexOf ("::") + 2, name.length ()), 
				 findModuleForScopeType (name, from));
      }
      
      for (int i=0; i<mm.size (); i++) {
	 if (from.getMember (i) instanceof TypeElement) {
	    if ((from.getMember (i).getMember (0) instanceof StructTypeElement)
		|| (from.getMember (i).getMember (0) instanceof EnumTypeElement)
		|| (from.getMember (i).getMember (0) instanceof UnionTypeElement)) {
	       if (DEBUG)
		  System.out.println ("constructed type");
	       if (((TypeElement)from.getMember (i).getMember (0)).getName ()
		   .equals (name)) {
		  String type = ((TypeElement)from.getMember (i).getMember (0)).getType ();
		  if (isScopeName (type)) {
		     // is scoped type
		     return hasSimpleParent 
			(type.substring (type.lastIndexOf ("::") + 2, type.length ()), 
			 findModuleForScopeType (type, from));
		  }
		  if (type2java (type) != null)
		     return type2java (type);
		  else 
		     return hasSimpleParent (type, from);
	       }
	    }
	    if (from.getMember (i).getMember (0) instanceof DeclaratorElement) {
	       //
	       // first member is DeclaratorElement e.g.  typedef long x;
	       //
	       if (DEBUG)
		  System.out.println ("first declarator element");
	       if (((TypeElement)from.getMember (i).getMember (0)).getName ()
                   .equals (name)) {
		  String type = ((TypeElement)from.getMember (i).getMember (0)).getType ();
		  /*
		    // nothing todo because in this case we can't have scoped name
		  if (isScopeName (type)) {
		     // is scoped type
		     return hasSimpleParent 
			(type.substring (type.lastIndexOf ("::") + 2, type.length ()), 
			 findModuleForScopeType (type, from));
		  }
		  */
		  if (type2java (type) != null)
		     return type2java (type);
		  else 
		     return hasSimpleParent (type, from);
	       }
	    }
	    if (from.getMember (i).getMembers ().size () > 1 
		&& from.getMember (i).getMember (from.getMember (i).getMembers ().size () - 1) 
		instanceof DeclaratorElement) {
	       //
	       // last member is DeclaratorElement e.g.  typedef haha x; or typedef mx::haha x;
	       //                                     or typedef ::m1::m2::haha x; 
	       //
	       int last = from.getMember (i).getMembers ().size () - 1;
	       if (DEBUG) {
		  System.out.println ("last declarator element");
		  System.out.println 
		     ("name: " + ((TypeElement)from.getMember (i).getMember (last)).getName ());
		  System.out.println
		     ("type: " + ((TypeElement)from.getMember (i).getMember (last)).getType ());
	       }
	       if (((TypeElement)from.getMember (i).getMember (last)).getName ()
                   .equals (name)) {
		  String type = ((TypeElement)from.getMember (i).getMember (last)).getType ();
		  if (DEBUG)
		     System.out.println ("name: " + name + " is type: " + type);
		  if (isAbsoluteScopeName (type)) {
		     // is absolute scope type
		     return hasSimpleParent 
			(type.substring (type.lastIndexOf ("::") + 2, type.length ()),
			 findTopLevelModuleForType (type, from));
		  }
		  if (isScopeName (type)) {
		     // is scope type
		     return hasSimpleParent 
			(type.substring (type.lastIndexOf ("::") + 2, type.length ()), 
			 findModuleForScopeType (type, from));
		  }
		  if (type2java (type) != null)
		     return type2java (type);
		  else 
		     return hasSimpleParent (type, from);
	       }
	    }
	 }
      }
      if (from.getParent () != null)
	 return hasSimpleParent (name, from.getParent ());
      return null;
   }

   
   public IDLElement findModule (String name, IDLElement from) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::findModule (" + name + ", " + from + ");");
      if (from.getName ().equals (name)) {
	 return from;
      }
      else {
	 if (findModuleIn (name, from) != null)
	    return findModuleIn (name, from);
	 else
	    if (from.getParent () != null) {
	       return findModule (name, from.getParent ());
	    }
	 return null;
      }
   }


   public IDLElement findModuleIn (String name, IDLElement from) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::findModuleIn (" + name + ", " + from + ");");
      Vector mm = from.getMembers ();
      IDLElement retval = null;
      for (int i=0; i<mm.size (); i++) {
	 if (from.getMember (i) instanceof ModuleElement 
	     || from.getMember (i) instanceof InterfaceElement) {
	    if (((IDLElement)from.getMember (i)).getName ().equals (name)) {
	       retval = from.getMember (i);
	       break;
	    }
	 }
      }

      return retval;
   }


   public IDLElement findElementByName (String name, IDLElement from) {
      // constructed type by name
      if (DEBUG)
	 System.out.println ("ImplGenerator::findElementByName (" + name + ", " + from + " : "
			     + from.getName () + ");");
      //from = findType (name, from);
      Vector mm = from.getMembers ();
      IDLElement result = null;
      result = findElementInElement (name, from);
      if (result != null)
	 return result;
      else
	 if (from.getParent () != null)
	    return findElementByName (name, from.getParent ());
      return null;
   }


   public IDLElement findModuleForScopeType (String name, IDLElement from) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::findModuleForScopeType (" + name + ", " + from + ");");
      StringTokenizer st = new StringTokenizer (name, "::");
      String retval = "";
      String s1;
      IDLElement fm = findModule (st.nextToken (), from);
      while (st.hasMoreTokens ()) {
	 s1 = st.nextToken ();
	 if (st.hasMoreTokens ()) {
	    // in s1 in module name
	    fm = findModuleIn (s1, fm);
	 } else {
	    // in s1 is name of type 
	    break;
	 }
      }
	 
      return fm;
   }
	 

   public IDLElement findTopLevelModuleForType (String name, IDLElement from) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::findTopLevelModuleForType (" + name + ", " 
			     + from + ");");
      StringTokenizer st = new StringTokenizer (name, "::");
      String retval = "";
      String s1;
      IDLElement fm = from;
      // find top level IDLElement
      while (fm.getParent () != null) {
	 fm = fm.getParent ();
      }
      //fm = findModuleIn (st.nextToken (), from);
      while (st.hasMoreTokens ()) {
	 s1 = st.nextToken ();
	 if (st.hasMoreTokens ()) {
	    // in s1 in module name
	    fm = findModuleIn (s1, fm);
	 } else {
	    // in s1 is name of type 
	    break;
	 }
      }
	 
      return fm;
   }
	 

   public String ctype2package (IDLElement type) {
      // checking modules
      if (DEBUG)
	 System.out.println ("ImplGenerator::ctype2package (" + type + ");");
      String modules = "";
      if (type != null) {
	 Vector mods = new Vector ();
	 mods.add (type.getName ());
	 while (type.getParent () != null) {
	    type = type.getParent ();
	    if (type instanceof ModuleElement)
	       mods.add (type.getName ());
	    if (type instanceof InterfaceElement)
	       mods.add (type.getName () + "Package");
	    
	 }
	 // transform modules names from vector to string in package format
	 for (int i=mods.size () - 1; i>=0; i--) {
	    if (DEBUG)
	       System.out.println ("transfrom: " + (String)mods.elementAt (i));
	    modules = modules + (String)mods.elementAt (i) + ".";
	 }
	 // without last dot
	 modules = modules.substring (0, modules.length () - 1);
	 if (DEBUG)
	    System.out.println ("result: >" + modules + "<");
      }
      return modules;
   }


   public Type isSimpleType (String type, IDLElement _element) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::isSimpleType (" + type + ", " + _element.getName () 
			     + ");");
      return type2java (type);
   }


   public String nameFromScopeName (String name) {
      if (name != null) {
	 if (name.lastIndexOf ("::") != -1) {
	    return name.substring (name.lastIndexOf ("::") + 2, name.length ());
	 }
      }
      return name;
   }


   public IDLElement findElementInElement (String name, IDLElement element) {
      name = nameFromScopeName (name);
      if (element == null)
	 return null;

      if (DEBUG)
	 System.out.println ("ImplGenerator::findElementInElement (" + name + ", " + element + " : "
			     + element.getName () + ");");
      Vector mm = element.getMembers ();
      IDLElement tmp_element = null;

      for (int i=0; i<mm.size (); i++) {
	 if (element.getMember (i) instanceof TypeElement) {
	    if ((element.getMember (i).getMember (0) instanceof StructTypeElement)
		|| (element.getMember (i).getMember (0) instanceof EnumTypeElement)
		|| (element.getMember (i).getMember (0) instanceof UnionTypeElement)) {
	       if (DEBUG)
		  System.out.println ("constructed type");
	       if (((TypeElement)element.getMember (i).getMember (0)).getName ().equals (name)) {
		  tmp_element = element.getMember (i).getMember (0);
		  if (DEBUG)
		     System.out.println ("element: " + tmp_element+ " : " + tmp_element.getName ());
		  return tmp_element;
	       }
	    }
	 }
	 if (element.getMember (i) instanceof ExceptionElement) {
	    if (DEBUG)
	       System.out.println ("exception");
	    if (((IDLElement)element.getMember (i)).getName ().equals (name)) {
	       tmp_element = element.getMember (i);
	       if (DEBUG)
		  System.out.println ("element: " + tmp_element + " : " + tmp_element.getName ());
	       return (IDLElement)tmp_element;
	    
	    }
	 }
	 if (element.getMember (i) instanceof InterfaceElement) {
	    if (DEBUG)
	       System.out.println ("interface");
	    if (((IDLElement)element.getMember (i)).getName ().equals (name)) {
	       tmp_element = element.getMember (i);
	       if (DEBUG)
		  System.out.println ("element: " + tmp_element + " : " + tmp_element.getName ());
	       return (IDLElement)tmp_element;
	    
	    }
	 }
      }
      return null;
   }


   public Type type2java (String idl_type, int mode, String _package, InterfaceElement _interface) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::type2java (" + idl_type + ", " + mode + ", " 
			     + _package + ", " + _interface.getName () + ");");
      String name_of_interface = _interface.getName ();
      String type_name;
      Type type = null;
      if (DEBUG)
	 System.out.println ("-- is simple type?");
      if ((type = isSimpleType (idl_type, _interface)) != null) {
	 // idl_type is mapped to java simple type
	 return type;
      }
      if (DEBUG)
	 System.out.println ("-- is type with simple parent?");
      if ((type = hasSimpleParent (idl_type, _interface)) != null) {
	 // idl_type is mapped throw other idl type(s) to simple java type
	 return type;
      }
      if (DEBUG)
	 System.out.println ("cons: " + ctype2package (findElementByName (idl_type, _interface)));
      
      if (DEBUG)
	 System.out.println ("-- is type with absolute scope name");
      if (isAbsoluteScopeName (idl_type)) {
	 // is absolute scoped name
	 Type tmp_type;
	 //if ((tmp_type = hasSimpleParent (idl_type, _interface)) != null)
	 //   return tmp_type;
	 IDLElement tmp = findTopLevelModuleForType (idl_type, _interface);
	 //
	 IDLElement element_for_type = findElementInElement (idl_type, tmp);
	 String full_name = _package + "." + ctype2package (element_for_type);
	 if (mode != Parameter.IN)
	    full_name = full_name + "Holder";
	 type = Type.createClass (org.openide.src.Identifier.create (full_name));
				  
	 /*
	 type = Type.createClass (org.openide.src.Identifier.create 
				  (scopedName2javaName (idl_type.substring 
				  (2, idl_type.length ()), true)));	    
	 */
	 return type;
      }
      if (DEBUG)
	 System.out.println ("-- is type with scope name");
      if (isScopeName (idl_type)) {
	 // find first module of this scoped type
	 
	 // is in module second module of this scoped type?
	 
	 // etc to simple type => is this simple type?
	 Type tmp_type;
	 IDLElement tmp = findModuleForScopeType (idl_type, _interface);
	 IDLElement element_for_type = findElementInElement (idl_type, tmp);
	 String full_name = _package + "." + ctype2package (element_for_type);
	 if (mode != Parameter.IN)
	    full_name = full_name + "Holder";
	 type = Type.createClass (org.openide.src.Identifier.create (full_name));

	 return type;
	 
      }
      if (DEBUG)
	 System.out.println ("-- is type normal name");
      Type tmp_type;
      IDLElement element_for_type = findElementByName (idl_type, _interface);
      if (DEBUG)
	 System.out.println ("element_for_type: " + element_for_type.getName () + " : " 
			     + element_for_type);
      String full_name = _package + "." + ctype2package (element_for_type);
      if (mode != Parameter.IN)
	 full_name = full_name + "Holder";
      type = Type.createClass (org.openide.src.Identifier.create (full_name));
      
      return type;
      
      /*
      if (DEBUG)
	 System.out.println ("type is constructed or not simple");
      String modules = modules2package (_interface);
      if (DEBUG)
	 System.out.println ("type is defined in modules: " + modules);
      Type tmp_type;
      if ((tmp_type = hasSimpleParent (idl_type, _interface)) != null) {
	 return tmp_type;
      }
      if (isTypeDefinedIn (idl_type, _interface)) {
	 if (DEBUG)
	    System.out.println ("type is defined in interface");
	 //type_name = _package + "." + _interface.getName () + "Package" + "." + idl_type;
	 // for module support we must do
	 type_name = _package + "." + modules + _interface.getName () + "Package" + "." 
	    + idl_type;
	 if ((mode == Parameter.INOUT) || (mode == Parameter.OUT))
	    type_name = type_name + "Holder";
      }
      else {
	 if (DEBUG)
	    System.out.println ("type isn't defined in interface");
	 //type_name = _package + "." + idl_type;
	 // for module support we must do
	 type_name = _package + "." + modules + idl_type;
	 
	 if ((mode == Parameter.INOUT) || (mode == Parameter.OUT))
	    type_name = type_name + "Holder";
      }
      type = Type.createClass (org.openide.src.Identifier.create (type_name));
     }
     else {
     // idl_type is simple type :-)
     type = type2java (idl_type);
     }
      */     
      //return null;
   }



   /*	 
   public Type type2java (String idl_type, int mode, String _package, InterfaceElement _interface) {
      String name_of_interface = _interface.getName ();
      String type_name;
      Type type = null;
      if (type2java (idl_type) == null) {
	 // idl_type isn't simple
	 if (isAbsoluteScopeName (idl_type)) {
	    type = Type.createClass (org.openide.src.Identifier.create 
				     (scopedName2javaName (idl_type.substring 
							   (2, idl_type.length ()), true)));	    
	 }
	 else {
	    if (isScopeName (idl_type)) {
	       idl_type = scopedName2javaName (idl_type, false);
	    }
	    if (DEBUG)
               System.out.println ("type is constructed or not simple");
	    String modules = modules2package (_interface);
	    if (DEBUG)
	       System.out.println ("type is defined in modules: " + modules);
	    if (isTypeDefinedIn (_interface, idl_type)) {
	       if (DEBUG)
		  System.out.println ("type is defined in interface");
	       //type_name = _package + "." + _interface.getName () + "Package" + "." + idl_type;
	       // for module support we must do
	       type_name = _package + "." + modules + _interface.getName () + "Package" + "." 
		  + idl_type;
	       if ((mode == Parameter.INOUT) || (mode == Parameter.OUT))
		  type_name = type_name + "Holder";
	    }
	    else {
	       if (DEBUG)
		  System.out.println ("type isn't defined in interface");
	       //type_name = _package + "." + idl_type;
 	       // for module support we must do
	       type_name = _package + "." + modules + idl_type;

	       if ((mode == Parameter.INOUT) || (mode == Parameter.OUT))
		  type_name = type_name + "Holder";
	    }
	    type = Type.createClass (org.openide.src.Identifier.create (type_name));
	 }
      }
      else {
	 // idl_type is simple type :-)
	 type = type2java (idl_type);
      }
      return type;
   }

   */

   public String exception2java (String ex, String _package, InterfaceElement _interface) {
      if (DEBUG)
	 System.out.println ("-- is exception with absolute scope name");
      
      if (isAbsoluteScopeName (ex)) {
	 // is absolute scope name
	 IDLElement tmp = findTopLevelModuleForType (ex, _interface);
	 IDLElement element_for_exception = findElementInElement (ex, tmp);
	 String full_name = _package + "." + ctype2package (element_for_exception);

	 return full_name;
      }
      if (DEBUG)
	 System.out.println ("-- is exception with scope name");
      if (isScopeName (ex)) {
	 IDLElement tmp = findModuleForScopeType (ex, _interface);
	 IDLElement element_for_exception = findElementInElement (ex, tmp);
	 String full_name = _package + "." + ctype2package (element_for_exception);

	 return full_name;
	 
      }
      if (DEBUG)
	 System.out.println ("-- is exception with normal name");
      IDLElement element_for_exception = findElementByName (ex, _interface);
      if (DEBUG)
	 System.out.println ("element_for_exception: " + element_for_exception.getName () + " : " 
			     + element_for_exception);
      String full_name = _package + "." + ctype2package (element_for_exception);
      
      return full_name;
   }
   
   public boolean existMethodInClass (ClassElement clazz, MethodElement method) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::existMethodInClass (" + clazz + ", " + method + ");");
      MethodParameter[] mps = method.getParameters ();
      org.openide.src.Identifier id = method.getName ();
      Type[] types = new Type [mps.length];
      for (int i=0; i<mps.length; i++)
	 types[i] = mps[i].getType ();
      if (clazz.getMethod (id, types) != null)
	 return true;
      else
	 return false;
   }

   public void attribute2java (AttributeElement attr, ClassElement clazz) {
      String _package = ido.getPrimaryFile ().getParent ().getPackageName ('.');
      InterfaceElement _interface = (InterfaceElement)attr.getParent ();
      Type attr_type = type2java (attr.getType (), Parameter.IN, _package, _interface);
      if (DEBUG)
	 System.out.println ("attribute2java");
      if (DEBUG) {
	 System.out.println ("attribute: " + attr.getName ());
	 System.out.println ("type: " + attr.getType ());
	 System.out.println ("java: " + attr_type);
	 System.out.println ("package: " + _package);
      }
      try {
	 MethodElement geter = new MethodElement ();
	 geter.setName (org.openide.src.Identifier.create (attr.getName ()));
	 geter.setModifiers (Modifier.PUBLIC);
	 geter.setReturn (attr_type);
	 geter.setBody ("\n  return null;\n");
	 if (!existMethodInClass (clazz, geter))
	    clazz.addMethod (geter);
      } catch (SourceException e) {
	 e.printStackTrace ();
      }
      
      if (!attr.getReadOnly ()) {
	 try {
	    MethodElement seter = new MethodElement ();
	    seter.setName (org.openide.src.Identifier.create (attr.getName ()));
	    seter.setModifiers (Modifier.PUBLIC);
	    seter.setReturn (Type.VOID);
	    seter.setParameters (new MethodParameter[] { 
	    new MethodParameter ("value", attr_type, false) });
	    if (!existMethodInClass (clazz, seter))
	       clazz.addMethod (seter);
	 } catch (SourceException e) {
	    e.printStackTrace ();
	 }
      }
      
   }

   public void operation2java (OperationElement operation, ClassElement clazz) {
      if (DEBUG)
	 System.out.println ("operation2java");
      String _package = ido.getPrimaryFile ().getParent ().getPackageName ('.');
      InterfaceElement _interface = (InterfaceElement)operation.getParent ();
      Type rettype = type2java (operation.getReturnType (), Parameter.IN, _package, _interface);
      if (DEBUG) {
	 System.out.println ("operation: " + operation.getName ());
	 System.out.println ("operation rettype:" + operation.getReturnType () + ":"); 
	 System.out.println ("return type: " + rettype);
      }
      try {
	 MethodElement oper = new MethodElement ();
	 oper.setName (org.openide.src.Identifier.create (operation.getName ()));
	 oper.setModifiers (Modifier.PUBLIC);
	 oper.setReturn (rettype);
	 // parameters and context!!!
	 MethodParameter[] params;
	 if (operation.getContexts ().size () != 0)
	    params = new MethodParameter[operation.getParameters ().size () + 1];
	 else
	    params = new MethodParameter[operation.getParameters ().size ()];

	 for (int i=0; i<operation.getParameters ().size (); i++) {
	    ParameterElement p = (ParameterElement)operation.getParameters ().elementAt (i);
	    Type ptype = type2java (p.getType (), p.getAttribute (), _package, _interface);
	    params[i] = new MethodParameter (p.getName (), ptype, false);
	 }
	 if (operation.getContexts ().size () != 0)
	    params[params.length - 1] = new MethodParameter 
	       ("ctx", Type.createClass (org.openide.src.Identifier.create 
					 ("org.omg.CORBA.Context")), false);
	 oper.setParameters (params);

	 // exceptions
	 org.openide.src.Identifier[] excs 
	    = new org.openide.src.Identifier[operation.getExceptions ().size ()];
	 for (int i=0; i<operation.getExceptions ().size (); i++) {
	    excs[i] = org.openide.src.Identifier.create 
	       (exception2java ((String)operation.getExceptions ().elementAt (i), 
				_package, _interface));
	 }
	 oper.setExceptions (excs);

	 // set body to return null if rettype != void;
	 if (oper.getReturn () != Type.VOID)
	    oper.setBody ("\n  return null;\n");

	 if (!existMethodInClass (clazz, oper)) 
	    clazz.addMethod (oper);
      } catch (SourceException e) {
            e.printStackTrace ();
      }

   }

   
   public Vector getInterfaces (Vector elements) {
      Vector retval = new Vector ();

      for (int i=0; i<elements.size (); i++) {
	 if (elements.elementAt (i) instanceof ModuleElement)
	    retval.addAll (getInterfaces (((ModuleElement)elements.elementAt (i)).getMembers ()));
	 if (elements.elementAt (i) instanceof InterfaceElement)
	    retval.add ((InterfaceElement)elements.elementAt (i));
      }
      
      return retval;
   }


   public void generate (InterfaceElement element, String name) {
   }

   public void synchronize (InterfaceElement element, String name) {
   }


   public void interface2java (ClassElement clazz, InterfaceElement element) {
      if (DEBUG)
	 System.out.println ("ImplGenerator::interface2java (clazz, " + element.getName () + ");");
      // parents...

      Vector parents = element.getParents ();
      for (int i=0; i<parents.size (); i++) {
	 IDLElement parent 
	    = findElementByName (((com.netbeans.enterprise.modules.corba.idl.src.Identifier)
				  parents.elementAt (i)).getName (), element);
	 //InterfaceElement parent = (InterfaceElement)id.getParent ();
	 interface2java (clazz, (InterfaceElement)parent);
      }

      Vector members = element.getMembers ();
      
      for (int i=0; i<members.size (); i++) {
	 if (members.elementAt (i) instanceof AttributeElement) {
	    attribute2java ((AttributeElement)members.elementAt (i), clazz);
	 }
	 if (members.elementAt (i) instanceof OperationElement) {
	    operation2java ((OperationElement)members.elementAt (i), clazz);
	 }
      }
      
   }

   public void interface2java (InterfaceElement element) {
      if (DEBUG)
	 System.out.println ("interface2java: " + element.getName ());
      if (DEBUG)
         System.out.println ("name: " + ido.getPrimaryFile ().getName ());


      String impl_name = "";
      String super_name = "";
      String modules = modules2package (element);
      String _package = ido.getPrimaryFile ().getParent ().getPackageName ('.'); 
      
      if (DEBUG) {
	 System.out.println ("modules:>" + modules + "<");
	 System.out.println ("package:>" + _package + "<");
      }


      if (!TIE) {
	 impl_name = IMPLBASE_IMPL_PREFIX + element.getName () + IMPLBASE_IMPL_POSTFIX;
	 if (where_generate == IN_IDL_PACKAGE)
	    super_name = _package + "." + modules + EXT_CLASS_PREFIX + element.getName () 
	       + EXT_CLASS_POSTFIX;
	 else
	    super_name = EXT_CLASS_PREFIX + element.getName () + EXT_CLASS_POSTFIX;
      }
      else {
	 impl_name = TIE_IMPL_PREFIX + element.getName () + TIE_IMPL_POSTFIX;
	 if (where_generate == IN_IDL_PACKAGE)
	    super_name = _package + "." + modules + IMPL_INT_PREFIX + element.getName () 
	       + IMPL_INT_POSTFIX;
	 else
	    super_name = IMPL_INT_PREFIX + element.getName () + IMPL_INT_POSTFIX;
      }

      // print to status line
      String status_package = "";
      StringTokenizer st = new StringTokenizer (_package, ".");
      while (st.hasMoreTokens ()) {
	 status_package += st.nextToken () + "/";
      }

      TopManager.getDefault ().setStatusText ("Generate " + status_package + impl_name + " ...");

      Vector members = element.getMembers ();

      FileObject folder = ido.getPrimaryFile ().getParent ();
      FileObject impl;

      if ((impl = folder.getFileObject (impl_name, "java")) != null) {
	 if (DEBUG)
	    System.out.println ("file exists");
      }
      else {
	 if (DEBUG)
	    System.out.println ("file don't exists");
	 try {
	    impl = folder.createData (impl_name, "java");
	 } catch (IOException e) {
	    e.printStackTrace ();
	 }
      }
      /*
      org.openide.src.SourceElement java_src = null;
      try {
	 java_src = ((SourceCookie)(DataObject.find (impl)).getCookie
		     (SourceCookie.class)).getSource ();
	 //DataObject tmp_do = DataObject.find (impl);
	 //SourceCookie src_cookie = (SourceCookie)tmp_do.getCookie (SourceCookie.class);
	 //java_src = src_cookie.getSource ();
      } catch (DataObjectNotFoundException e) {
	 e.printStackTrace ();
      }
      */
      try {
	 //org.openide.src.SourceElement source = new SourceElement ();

	 ClassElement clazz = new ClassElement ();
	 clazz.setName (org.openide.src.Identifier.create (impl_name));
	 if (!TIE)
	    clazz.setSuperclass (org.openide.src.Identifier.create (super_name));
	 else
	    clazz.setInterfaces (new org.openide.src.Identifier[] 
				 {org.openide.src.Identifier.create (super_name)} );
	 //java_src.addClass (clazz);
	 //java_src.addClasses (new ClassElement[] { clazz });

	 interface2java (clazz, element); 
	 /*

	 for (int i=0; i<members.size (); i++) {
	    if (members.elementAt (i) instanceof AttributeElement) {
	       attribute2java ((AttributeElement)members.elementAt (i), clazz);
	    }
	    if (members.elementAt (i) instanceof OperationElement) {
	       operation2java ((OperationElement)members.elementAt (i), clazz);
	    }
	 }
	 */

	 FileLock lock = impl.lock ();
	 //	 if (source != null)
	 //   source.addClass (clazz);
	 PrintStream printer = new PrintStream (impl.getOutputStream (lock));
	 printer.println ("\npackage " + _package + ";\n");
	 printer.println (clazz.toString ());
	 lock.releaseLock ();
      } catch (Exception e) {
	 e.printStackTrace ();
      }
   }

   public void generate () {
      if (DEBUG) {
	 System.out.println ("generate :-))");
	 src.dump ("");
      }
      //Vector members = src.getMembers ();     // update for working with modules :-))
      Vector members = getInterfaces (src.getMembers ());
      for (int i=0; i<members.size (); i++) {
	 if (members.elementAt (i) instanceof InterfaceElement)
	    interface2java ((InterfaceElement)members.elementAt (i));
      }

      TopManager.getDefault ().setStatusText ("Successfully Generated Implementation Classes for "
					      + ido.getPrimaryFile ().getName () + ".");

   }

   
   public static void main (String[] args) {
      try {
	 IDLParser parser = new IDLParser (new FileInputStream (args[0]));

	 IDLElement src = (IDLElement)parser.Start ();

	 ImplGenerator generator = new ImplGenerator ();
	 generator.setSources (src);
	 generator.generate ();

      } catch (Exception e) {
	 e.printStackTrace ();
      }

      
   }

}


/*
 * $Log
 * $
 */











