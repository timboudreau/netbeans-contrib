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

package org.netbeans.modules.corba.idl.generator;

import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Iterator;

import java.text.MessageFormat;

import java.io.*;
import java.lang.reflect.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.cookies.*;
import org.openide.src.*;
import org.openide.src.nodes.*;
import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.java.JavaConnections;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.*;


import org.netbeans.modules.corba.idl.src.*;
import org.netbeans.modules.corba.settings.*;
import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class ImplGenerator implements PropertyChangeListener {

    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;


    private boolean showMessage; // Fix of showing message when the sync is disabled
    private boolean _M_exception_occured;
    private IDLElement _M_src;

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

    private IDLDataObject _M_ido;

    private List _M_generated_impls;

    private boolean WAS_TEMPLATE = false;
    // this variable indicate if in calling of hasTemplateParent is template type or not
    // => it must be setuped to

    private boolean _M_open = true;

    CORBASupportSettings css;

    private boolean _M_listen = false;

    public ImplGenerator (IDLDataObject _do) {

        _M_ido = _do;

        css = (CORBASupportSettings) CORBASupportSettings.findObject
              (CORBASupportSettings.class, true);

	IMPLBASE_IMPL_PREFIX = css.getActiveSetting ().getImplBasePrefix ();
	IMPLBASE_IMPL_POSTFIX = css.getActiveSetting ().getImplBasePostfix ();
	EXT_CLASS_PREFIX = css.getActiveSetting ().getExtClassPrefix ();
	EXT_CLASS_POSTFIX = css.getActiveSetting ().getExtClassPostfix ();
	TIE_IMPL_PREFIX = css.getActiveSetting ().getTiePrefix ();
	TIE_IMPL_POSTFIX = css.getActiveSetting ().getTiePostfix ();
	IMPL_INT_PREFIX = css.getActiveSetting ().getImplIntPrefix ();
	IMPL_INT_POSTFIX = css.getActiveSetting ().getImplIntPostfix ();
	TIE = css.getActiveSetting ().isTie ();

	/*
	  IMPLBASE_IMPL_PREFIX = "";
	  IMPLBASE_IMPL_POSTFIX = "Impl";
	  EXT_CLASS_PREFIX = "_";
	  EXT_CLASS_POSTFIX = "ImplBase";
	  TIE_IMPL_PREFIX = "";
	  TIE_IMPL_POSTFIX = "ImplTIE";
	  IMPL_INT_PREFIX = "";
	  IMPL_INT_POSTFIX = "Operations";
	  TIE = false;
	*/
    }

    public ImplGenerator () {
        IMPLBASE_IMPL_PREFIX = ""; // NOI18N
        IMPLBASE_IMPL_POSTFIX = "Impl"; // NOI18N
        EXT_CLASS_PREFIX = "_"; // NOI18N
        EXT_CLASS_POSTFIX = "ImplBase"; // NOI18N
        TIE_IMPL_PREFIX = ""; // NOI18N
        TIE_IMPL_POSTFIX = "ImplTIE"; // NOI18N
        IMPL_INT_PREFIX = ""; // NOI18N
        IMPL_INT_POSTFIX = "Operations"; // NOI18N
        TIE = false;
    }

    public void setSources (IDLElement e) {
        _M_src = e;
    }

    public void setOpen (boolean __value) {
	_M_open = __value;
    }

    public boolean getOpen () {
	return _M_open;
    }

    public String simple2java (IDLType idl_type) {
        String type = ""; // NOI18N

        if (idl_type.getType () == IDLType.VOID)
            type = "void"; // NOI18N
        if (idl_type.getType () == IDLType.BOOLEAN)
            type = "boolean"; // NOI18N
        if (idl_type.getType () == IDLType.CHAR)
            type = "char"; // NOI18N
        if (idl_type.getType () == IDLType.WCHAR)
            type = "char"; // NOI18N
        if (idl_type.getType () == IDLType.OCTET)
            type = "byte"; // NOI18N
        if (idl_type.getType () == IDLType.STRING)
            type = "String"; // NOI18N
        if (idl_type.getType () == IDLType.WSTRING)
            type = "String"; // NOI18N
        if (idl_type.getType () == IDLType.SHORT)
            type = "short"; // NOI18N
        if (idl_type.getType () == IDLType.USHORT)
            type = "short"; // NOI18N
        if (idl_type.getType () == IDLType.LONG)
            type = "int"; // NOI18N
        if (idl_type.getType () == IDLType.ULONG)
            type = "int"; // NOI18N
        if (idl_type.getType () == IDLType.ULONGLONG)
            type = "long"; // NOI18N
        if (idl_type.getType () == IDLType.FLOAT)
            type = "float"; // NOI18N
        if (idl_type.getType () == IDLType.DOUBLE)
            type = "double"; // NOI18N

        if (idl_type.getType () == IDLType.OBJECT)
            type = "org.omg.CORBA.Object"; // NOI18N

        if (idl_type.getType () == IDLType.ANY)
            type = "org.omg.CORBA.Any"; // NOI18N

        if (DEBUG)
            System.out.println ("simple2java: " + type); // NOI18N
        return type;
    }

    public Type type2java (IDLType idl_type) {
        if (DEBUG)
            System.out.println ("ImplGenerator::type2java (" + idl_type + ");"); // NOI18N
        String java_type = simple2java (idl_type);

        if (DEBUG) {
            System.out.println ("java_type: " + java_type); // NOI18N
        }

        if (java_type.equals ("void")) // NOI18N
            return Type.VOID;
        if (java_type.equals ("boolean")) // NOI18N
            return Type.BOOLEAN;
        if (java_type.equals ("char")) // NOI18N
            return Type.CHAR;
        if (java_type.equals ("byte")) // NOI18N
            return Type.BYTE;
        if (java_type.equals ("String")) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("java.lang.String", "String")); // NOI18N
        if (java_type.equals ("short")) // NOI18N
            return Type.SHORT;
        if (java_type.equals ("int")) // NOI18N
            return Type.INT;
        if (java_type.equals ("long")) // NOI18N
            return Type.LONG;
        if (java_type.equals ("float")) // NOI18N
            return Type.FLOAT;
        if (java_type.equals ("double")) // NOI18N
            return Type.DOUBLE;

        if (java_type.equals ("org.omg.CORBA.Object")) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.Object")); // NOI18N

        if (java_type.equals ("org.omg.CORBA.Any")) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.Any")); // NOI18N

        return null;
    }


    public Type JavaTypeToHolder (Type type) throws UnknownTypeException {

        if (DEBUG)
            System.out.println ("ImplGenerator::JavaTypeToHolder (" + type + ");"); // NOI18N
        //if (java_type.equals ("void")) // NOI18N
        //  return Type.VOID;
        if (type.equals (Type.BOOLEAN))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.BooleanHolder")); // NOI18N
        if (type.equals (Type.CHAR))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.CharHolder")); // NOI18N
        if (type.equals (Type.CHAR))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.ByteHolder")); // NOI18N
        if (type.equals (Type.createClass (org.openide.src.Identifier.create("java.lang.String", // NOI18N
                                           "String")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.StringHolder")); // NOI18N
        if (type.equals (Type.SHORT))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.ShortHolder")); // NOI18N
        if (type.equals (Type.INT))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.IntHolder")); // NOI18N
        //if (type.equals ("long")) // NOI18N
        //  return Type.LONG;
        //if (type.equals ("float")) // NOI18N
        //  return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.FloatHolder")); // NOI18N
        if (type.equals (Type.FLOAT))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.FloatHolder")); // NOI18N
        if (type.equals (Type.DOUBLE))
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.DoubleHolder")); // NOI18N

        if (type.equals (Type.createClass (org.openide.src.Identifier.create
                                           ("org.omg.CORBA.Object")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.ObjectHolder")); // NOI18N

        if (type.equals (Type.createClass (org.openide.src.Identifier.create
                                           ("org.omg.CORBA.Any")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create ("org.omg.CORBA.AnyHolder")); // NOI18N

        if (DEBUG)
            System.out.println ("error unknown type!!!"); // NOI18N
        throw new UnknownTypeException (type.getSourceString ());
    }

    public boolean isAbsoluteScopeType (IDLType type) {
        if (type.getType () != IDLType.SEQUENCE)
            return isAbsoluteScopeName (type.getName ());
        else
            return isAbsoluteScopeName (type.ofType ().getName ());
    }

    public static boolean isAbsoluteScopeName (String name) {
        if (DEBUG)
            System.out.println ("isAbsoluteScopeName (" + name + ");"); // NOI18N
        if (name.length () >= 3)
            if (name.substring (0, 2).equals ("::")) { // NOI18N
                if (DEBUG)
                    System.out.println ("YES"); // NOI18N
                return true;
            }
        if (DEBUG)
            System.out.println ("NO"); // NOI18N
        return false;
    }

    public boolean isScopeType (IDLType type) {
        /*
          if (DEBUG)
          System.out.println ("isScopeType (" + type.getName () + ");");
          if (type.getType () == IDLType.SCOPED) {
          if (DEBUG)
          System.out.println ("YES");
          return true;
          }
          else {
          if (DEBUG)
          System.out.println ("NO");    
          return false;
          }
        */
        if (type.getType () != IDLType.SEQUENCE)
            return isScopeName (type.getName ());
        else
            return isScopeName (type.ofType ().getName ());
    }

    public static boolean isScopeName (String name) {
        if (DEBUG)
            System.out.println ("isScopeName (" + name + ");"); // NOI18N
        if (name.indexOf ("::") > -1) { // NOI18N
            if (DEBUG)
                System.out.println ("YES"); // NOI18N
            return true;
        }
        else {
            if (DEBUG)
                System.out.println ("NO"); // NOI18N
            return false;
        }
    }


    public static String getSimpleName (String name) {
        String retval = name.substring (name.lastIndexOf ("::") + 2, name.length ()); // NOI18N
        if (DEBUG)
            System.out.println ("getSimpleName (" + name + "); => " + retval); // NOI18N
        return retval;
    }

    public String scopedName2javaName (String name, boolean absolute) {
        StringTokenizer st = new StringTokenizer (name, "::"); // NOI18N
        String retval = ""; // NOI18N
        while (st.hasMoreTokens ()) {
            retval += st.nextToken () + "."; // NOI18N
        }
        retval = retval.substring (0, retval.length () - 1);
        // added Package between name of interface and name of data type
        if (!absolute)
            retval = retval.substring (0, retval.lastIndexOf (".")) + "Package." // NOI18N
                     + retval.substring (retval.lastIndexOf (".") + 1, retval.length ()); // NOI18N
        if (DEBUG) {
            System.out.println ("scoped name: " + name); // NOI18N
            System.out.println ("java name: " + retval); // NOI18N
        }
        return retval;
    }

    /*
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
    */

    public boolean isExceptionDefinedIn (InterfaceElement _interface, String exc) {
        boolean is_in = false;
        if (DEBUG)
            System.out.println ("isExceptionDefinedIn ..."); // NOI18N
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
        String modules = ""; // NOI18N
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
                modules = modules + (String)mods.elementAt (i) + "."; // NOI18N
            }
        }

        return modules;
    }


    public String elements2package (IDLElement _element) {
        // checking modules
        String modules = ""; // NOI18N
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
                modules = modules + (String)mods.elementAt (i) + "."; // NOI18N
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
                        return ((TypeElement)from.getMember (i).getMember (0)).getType ().getName ();
                    }
                } else {
                    if (((TypeElement)from.getMember (i)).getName ().equals (name))
                        return ((TypeElement)from.getMember (i)).getType ().getName ();
                }
        }
        if (from.getParent () != null)
            return findType (name, from.getParent ());
        return null;
    }

    /*
      public Type hasSimpleParent (IDLType type, IDLElement from) {
      
      if (from == null)
      return null;
      
      if (DEBUG)
      System.out.println ("ImplGenerator::hasSimpleParent (" + type.getName () 
      + ", " + from + ");");
      Vector mm = from.getMembers ();
      boolean is_in = false;
        
      if (isAbsoluteScopeType (type)) {
        // is absolute scoped name
        return hasSimpleParent 
    (new IDLType (type.getType (), 
          type.getName ().substring 
          (type.getName ().lastIndexOf ("::") + 2, 
           type.getName ().length ())), 
          findTopLevelModuleForType (type.getName (), from));
      }
      if (isScopeType (type)) {
        // is scoped name
        return hasSimpleParent 
    (new IDLType (type.getType (), 
          type.getName ().substring (type.getName ().lastIndexOf ("::") + 2, 
    				 type.getName ().length ())), 
    findModuleForScopeType (type.getName (), from));
      }
        
      for (int i=0; i<mm.size (); i++) {
        if (from.getMember (i) instanceof TypeElement) {
    if ((from.getMember (i).getMember (0) instanceof StructTypeElement)
       || (from.getMember (i).getMember (0) instanceof EnumTypeElement)
       || (from.getMember (i).getMember (0) instanceof UnionTypeElement)) {
     if (DEBUG)
       System.out.println ("constructed type");
     if (((TypeElement)from.getMember (i).getMember (0)).getName ()
         .equals (type)) {
       String name = ((TypeElement)from.getMember (i).getMember (0)).getType ().getName ();
       if (isScopeType (type)) {
         // is scoped type
         return hasSimpleParent 
    (new IDLType (type.getType (), 
    	      name.substring (name.lastIndexOf ("::") + 2, name.length ())), 
    	      findModuleForScopeType (name, from));
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
     if (DEBUG) {
       System.out.println ("first declarator element");
       System.out.println ("declarator: " + ((TypeElement)from.getMember (i).getMember (0))
    		.getName ());
       System.out.println ("type: " + type);
       System.out.println ("type: " + type.getName ());
       System.out.println ("type: " + type.getType ());
       System.out.println ("type of parent: " + ((TypeElement)from.getMember (i)).getType ());
     }
     //if (((TypeElement)from.getMember (i).getMember (0)).getName ()
     //    .equals (type)) {
     if (((TypeElement)from.getMember (i).getMember (0)).getName ()
         .equals (type.getName ())) {
       String name = ((TypeElement)from.getMember (i).getMember (0)).getType ().getName ();
       IDLType type_of_parent = ((TypeElement)from.getMember (i)).getType ();
       
         // nothing todo because in this case we can't have scoped name
       //  if (isScopeName (type)) {
         // is scoped type
       //  return hasSimpleParent 
       //  (type.substring (type.lastIndexOf ("::") + 2, type.length ()), 
       //  findModuleForScopeType (type, from));
       //  }
       
       if (type2java (type) != null)
         return type2java (type);
       else
         if (type2java (type_of_parent) != null)
    return type2java (type_of_parent);
         else {
    return hasSimpleParent (type, from);
         }
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
         .equals (type)) {
       String name = ((TypeElement)from.getMember (i).getMember (last)).getType ().getName ();
       if (DEBUG)
         System.out.println ("name: " + name + " is type: " + type);
       if (isAbsoluteScopeType (type)) {
         // is absolute scope type
         return hasSimpleParent 
    (new IDLType (type.getType (),
    	      name.substring (name.lastIndexOf ("::") + 2, name.length ())),
     findTopLevelModuleForType (name, from));
       }
       if (isScopeType (type)) {
         // is scope type
         return hasSimpleParent 
    (new IDLType (type.getType (),
    	      name.substring (name.lastIndexOf ("::") + 2, name.length ())), 
    	      findModuleForScopeType (name, from));
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
        return hasSimpleParent (type, from.getParent ());
        return null;
      }
    */

    public Type hasSimpleParent (IDLType type, IDLElement from) {

        if (from == null)
            return null;
        if (DEBUG)
            System.out.println ("ImplGenerator::hasSimpleParent (" + type.getName () // NOI18N
                                + ", " + from + ");"); // NOI18N

        Type java_type;

        if (type2java (type) != null)
            return type2java (type);

        // if type is template type it hasn't simple parent
        if (type.getType () == IDLType.SEQUENCE) {
            if (DEBUG)
                System.out.println ("hasn't simple parent -- is template type"); // NOI18N
            return null;
        }
        if (type.getType () == IDLType.STRUCT
                || type.getType () == IDLType.UNION
                || type.getType () == IDLType.ENUM) {
            if (DEBUG)
                System.out.println ("hasn't simple parent -- is constructed type"); // NOI18N
            return null;
        }

        if (isAbsoluteScopeType (type)) {
            // is absolute scoped name
            IDLType tmp_type = createChildFromType (type);
            return hasSimpleParent (tmp_type, findTopLevelModuleForType (type, from));
        }
        if (isScopeType (type)) {
            // is scoped name
            IDLType tmp_type = createChildFromType (type);
            return hasSimpleParent (tmp_type, findModuleForScopeType (type, from));
        }

        IDLElement tmp_element = findElementByName (type.getName (), from);
        if (DEBUG)
            System.out.println ("dimension of element: " + type.ofDimension ()); // NOI18N
        if (tmp_element instanceof TypeElement) {
            TypeElement result = (TypeElement)tmp_element;
            if (result == null) {
                if (DEBUG) {
                    System.out.println ("can't find type: " + type.getName ()); // NOI18N
                }
                return null;
            }

            IDLType res_type = result.getType ();
            Vector dim = result.getType ().ofDimension ();
            if ((java_type = type2java (res_type)) == null) {
                java_type = hasSimpleParent (res_type, from);
                if (java_type == null)  // we are at the top of type tree
                    return null;
            }
            for (int i=0; i<dim.size (); i++) {
                if (DEBUG)
                    System.out.println ("!!!!!!!!!!!!! " + i + "  !!!!!!!!!!!!!!!!!!!"); // NOI18N
                java_type = Type.createArray (java_type);
            }
            return java_type;
        }
        else {
            return null;
        }
    }


    public IDLElement findTypeByName (IDLType type, IDLElement from) {

        if (from == null)
            return null;

        IDLElement element = null;

        if ((element = findElementInElement (type.getName (), from)) == null) {
            return findTypeByName (type, from.getParent ());
        }
        else {
            return element;
        }
    }


    public IDLType createChildFromType (IDLType type) {
        if (DEBUG)
            System.out.println ("ImplGenerator::createChildFromType (" + type + ");"); // NOI18N
        if (type.getType () == IDLType.SEQUENCE) {
            if (DEBUG)
                System.out.println ("-- for sequences"); // NOI18N
            return new IDLType (type.ofType ().getType (),
                                type.ofType ().getName ().substring
                                (type.ofType ().getName ().lastIndexOf ("::") + 2, // NOI18N
                                 type.ofType ().getName ().length ()),
                                type.ofType (),
                                type.ofDimension ());
        }
        else {
            if (DEBUG)
                System.out.println ("-- for simple types"); // NOI18N
            //return new IDLType (type.getType (), type.getName ());

            return new IDLType (type.getType (),
                                type.getName ().substring (type.getName ().lastIndexOf ("::") + 2, // NOI18N
                                                           type.getName ().length ()));

            //type);
        }
    }

    /*
      public Type hasTemplateParent (IDLType type, int mode, String _package, IDLElement from) {
      
      if (from == null)
      return null;
      
      if (DEBUG)
      System.out.println ("ImplGenerator::hasTemplateParent (" + type.getName () + ", " + mode 
      + ", " + from + ");");
      Vector mm = from.getMembers ();
      boolean is_in = false;
      TypeElement type_element = null;
      Type java_type = null;

      if (isAbsoluteScopeType (type)) {
      // is absolute scoped name
      IDLType tmp_type = createChildFromType (type);
      return hasTemplateParent (tmp_type, mode, _package,
      findTopLevelModuleForType (type, from));
      }
      if (isScopeType (type)) {
      // is scoped name
      if (DEBUG) {
      System.out.println (type.getType ());
      System.out.println (type.getName ().substring (type.getName ().lastIndexOf ("::") + 2, 
      type.getName ().length ()));
      System.out.println (type.ofType ());
      System.out.println (type.ofDimension ());
      }

      IDLType tmp_type = createChildFromType (type);
      return hasTemplateParent (tmp_type, mode, _package,
      findModuleForScopeType (type, from));

      }

      TypeElement el_for_type;


      if (type.getType () == IDLType.SEQUENCE) {
      if (DEBUG)
      System.out.println ("has simple parent??");
      if ((java_type = hasSimpleParent (type.ofType (), from)) != null) {
      if (DEBUG)
      System.out.println ("yes");
      if (mode == Parameter.IN)
      return java_type; 
      else
      return JavaTypeToHolder (java_type);
      }
      else {
      IDLElement tmp_element = findElementByName (type.ofType ().getName (), from);
      if (tmp_element instanceof TypeElement)
      el_for_type = (TypeElement)tmp_element;
      else
      return null;
      }
      }
      else {
      if (DEBUG)
      System.out.println ("has simple parent??");
      if (hasSimpleParent (type, from) != null) {
      if (DEBUG)
      System.out.println ("yes");
      return hasSimpleParent (type, from); 
      }
      else
      el_for_type = (TypeElement)findElementByName (type.getName (), from);
      }
      if (el_for_type == null) {
      if (DEBUG)
      System.out.println ("can't find type!!!");
      //return null;
      //Thread.dumpStack ();
      }
      if (DEBUG) {
      if (el_for_type != null)
      System.out.println (el_for_type.getType ().toString ());
      }

      if (el_for_type.getType ().getType () == IDLType.SEQUENCE) {
      if (el_for_type.getType ().getType () == IDLType.SEQUENCE) {
      if (mode == Parameter.IN)
      return Type.createArray (hasTemplateParent (el_for_type.getType (),
      mode, _package, from));
      else
      return hasTemplateParent (el_for_type.getType (), mode, _package, from);
      }
      else {
      if (mode == Parameter.IN) {
      if ((java_type = type2java (el_for_type.getType ().ofType ())) != null) {
      // haha
      if (DEBUG)
      System.out.println ("return array of type");
      return Type.createArray (java_type);
      }
      if ((java_type = hasSimpleParent (el_for_type.getType ().ofType (), from)) != null) {
      // haha
      if (DEBUG)
      System.out.println ("return array of type2");
      return Type.createArray (java_type);
      }
      }
      else {
      if (DEBUG)
      System.out.println ("return simple type");
        
      IDLElement element_for_type = findElementByName (type.getName (), from);
      if (DEBUG)
      System.out.println ("element_for_type: " + element_for_type.getName () + " : " 
      + element_for_type);
      String full_name = _package + "." + ctype2package (element_for_type);
      full_name = full_name + "Holder";
      return Type.createClass (org.openide.src.Identifier.create (full_name));
      }
      }
      }
      
      
      
      if (el_for_type.getType ().getType () == IDLType.STRING) {
      return null;
      } 
      
      return null;
      }
    */


    public Type hasTemplateParent (IDLType type, int mode, String _package, IDLElement from) {

        if (from == null)
            return null;

        if (DEBUG)
            System.out.println ("ImplGenerator::hasTemplateParent (" + type.getName () + ", " + mode // NOI18N
                                + ", " + from + ");"); // NOI18N
        Vector mm = from.getMembers ();
        boolean is_in = false;
        TypeElement type_element = null;
        Type java_type = null;

        if (isAbsoluteScopeType (type)) {
            // is absolute scoped name
            IDLType tmp_type = createChildFromType (type);
            return hasTemplateParent (tmp_type, mode, _package,
                                      findTopLevelModuleForType (type, from));
        }
        if (isScopeType (type)) {
            // is scoped name
            if (DEBUG) {
                System.out.println (type.getType ());
                System.out.println (type.getName ().substring (type.getName ().lastIndexOf ("::") + 2, // NOI18N
                                    type.getName ().length ()));
                System.out.println (type.ofType ());
                System.out.println (type.ofDimension ());
            }

            IDLType tmp_type = createChildFromType (type);
            return hasTemplateParent (tmp_type, mode, _package,
                                      findModuleForScopeType (type, from));

        }

        if (type.getType () == IDLType.STRUCT
                || type.getType () == IDLType.UNION
                || type.getType () == IDLType.ENUM) {
            if (DEBUG)
                System.out.println ("hasn't template parent -- is constructed type"); // NOI18N
            return null;
        }


        TypeElement el_for_type;

        if (type.getType () == IDLType.SEQUENCE) {

            WAS_TEMPLATE = true;

            if (mode == Parameter.IN) {
                // variable of this type is "in" // NOI18N
                if (DEBUG)
                    System.out.println ("\"IN\""); // NOI18N
                if (DEBUG)
                    System.out.println ("has simple parent??"); // NOI18N
                if ((java_type = hasSimpleParent (type.ofType (), from)) != null) {
                    if (DEBUG)
                        System.out.println (type + "has simple parent2"); // NOI18N
                    return java_type;
                }
            }
            else {
                // variable of this type is "inout" or "out" // NOI18N
                if (DEBUG)
                    System.out.println ("\"INOUT\" || \"OUT\" => return null"); // NOI18N
                return null;
            }
        }
        else {
            // we have DeclaratorElement of some type e.g. sequence<long> seqlong;
            // so we have seqlong now
            if (mode == Parameter.IN) {
                if ((java_type = hasSimpleParent (type, from)) != null) {
                    if (DEBUG)
                        System.out.println (type + "has simple parent"); // NOI18N
                    return java_type;
                }

                IDLElement tmp_element  = findElementByName (type.getName (), from);
                if (tmp_element instanceof TypeElement) {
                    type_element = (TypeElement)tmp_element;
                    //if (type_element.getType ().ofType () != null) {
                    if (type_element.getType ().getType () == IDLType.SEQUENCE) {

                        WAS_TEMPLATE = true;

                        if ((java_type = hasTemplateParent (type_element.getType ().ofType (),
                                                            mode, _package, from)) != null) {
                            return Type.createArray (java_type);
                        }
                        else {
                            if (WAS_TEMPLATE) {
                                if (DEBUG)
                                    System.out.println ("::id1 " + type_element.getName ()); // NOI18N
                                return Type.createClass (org.openide.src.Identifier.create
                                                         (type_element.getName ()));
                            }
                            else
                                return null;
                        }

                        //throw new NullPointerException ();
                    }
                    else {
                        type_element = (TypeElement)tmp_element;
                        if (DEBUG)
                            System.out.println ("Chcip 1:" + type_element.getType () + " : " // NOI18N
                                                + type_element.getName ());


                        if (type_element.getType ().getType () == IDLType.STRUCT
                                || type_element.getType ().getType () == IDLType.UNION
                                || type_element.getType ().getType () == IDLType.ENUM) {

                            if (WAS_TEMPLATE) {
                                if (DEBUG)
                                    System.out.println ("hasn't template parent -- is constructed type but" // NOI18N
                                                        + " WAS_TEMPLATE"); // NOI18N
                                if (DEBUG)
                                    System.out.println ("::id2 " + type_element.getName ()); // NOI18N
                                return Type.createClass (org.openide.src.Identifier.create
                                                         (type_element.getName ()));
                            }
                            else {
                                if (DEBUG)
                                    System.out.println ("hasn't template parent -- is constructed type but" // NOI18N
                                                        + " WASN'T_TEMPLATE"); // NOI18N
                                return null;
                            }
                        }
                        if ((java_type = hasTemplateParent (type_element.getType (), mode, _package, from))
                                != null) {
                            return java_type;
                        }
                        else {
                            if (WAS_TEMPLATE) {
                                if (DEBUG)
                                    System.out.println ("::id3 " + type_element.getName ()); // NOI18N
                                return Type.createClass (org.openide.src.Identifier.create
                                                         (type_element.getName ()));
                            }
                            else
                                return null;
                        }
                    }
                }
                if (tmp_element instanceof InterfaceElement) {
                    if (WAS_TEMPLATE) {
                        if (DEBUG)
                            System.out.println ("::id4 " + type.getName ()); // NOI18N
                        return Type.createClass (org.openide.src.Identifier.create (type.getName ()));
                    }
                    else
                        return null;
                }
            }
            else {
                IDLElement tmp_element = findElementByName (type.getName (), from);
                if (DEBUG)
                    System.out.println ("tmp_element: " + tmp_element.getName () + " : " + tmp_element); // NOI18N
                //System.out.println ("956: Package: "+_package); // NOI18N
                String full_name = _package + "." + ctype2package (tmp_element); // NOI18N
                full_name = full_name + "Holder"; // NOI18N

                if (WAS_TEMPLATE) {
                    if (DEBUG)
                        System.out.println ("::id5 " + full_name); // NOI18N
                    return Type.createClass (org.openide.src.Identifier.create (full_name));
                }
                else
                    return null;
            }
        }


        /*
          if (type.getType () == IDLType.SEQUENCE) {
          if (DEBUG)
          System.out.println ("has simple parent??");
          if ((java_type = hasSimpleParent (type.ofType (), from)) != null) {
          if (DEBUG)
          System.out.println ("yes");
          if (mode == Parameter.IN)
          return java_type; 
          else
          return JavaTypeToHolder (java_type);
          }
          else {
          IDLElement tmp_element = findElementByName (type.ofType ().getName (), from);
          if (tmp_element instanceof TypeElement)
          el_for_type = (TypeElement)tmp_element;
          else
          return null;
          }
          }
          else {
          if (DEBUG)
          System.out.println ("has simple parent??");
          if (hasSimpleParent (type, from) != null) {
          if (DEBUG)
          System.out.println ("yes");
          return hasSimpleParent (type, from); 
          }
          else
          el_for_type = (TypeElement)findElementByName (type.getName (), from);
          }
          if (el_for_type == null) {
          if (DEBUG)
          System.out.println ("can't find type!!!");
          //return null;
          //Thread.dumpStack ();
          }
          if (DEBUG) {
          if (el_for_type != null)
          System.out.println (el_for_type.getType ().toString ());
          }
          
          if (el_for_type.getType ().getType () == IDLType.SEQUENCE) {
          if (el_for_type.getType ().getType () == IDLType.SEQUENCE) {
          if (mode == Parameter.IN)
          return Type.createArray (hasTemplateParent (el_for_type.getType (),
          mode, _package, from));
          else
          return hasTemplateParent (el_for_type.getType (), mode, _package, from);
          }
          else {
          if (mode == Parameter.IN) {
          if ((java_type = type2java (el_for_type.getType ().ofType ())) != null) {
          // haha
          if (DEBUG)
          System.out.println ("return array of type");
          return Type.createArray (java_type);
          }
          if ((java_type = hasSimpleParent (el_for_type.getType ().ofType (), from)) != null) {
          // haha
          if (DEBUG)
          System.out.println ("return array of type2");
          return Type.createArray (java_type);
          }
          }
          else {
          if (DEBUG)
          System.out.println ("return simple type");
          
          IDLElement element_for_type = findElementByName (type.getName (), from);
          if (DEBUG)
          System.out.println ("element_for_type: " + element_for_type.getName () + " : " 
          + element_for_type);
          String full_name = _package + "." + ctype2package (element_for_type);
          full_name = full_name + "Holder";
          return Type.createClass (org.openide.src.Identifier.create (full_name));
          }
          }
          }
          
          
          if (el_for_type.getType ().getType () == IDLType.STRING) {
          return null;
          } 
        */

        return null;
    }


    public IDLElement getTopParentTypeElement (IDLElement type, IDLElement from) {
        if (from == null || type == null)
            return null;
        if (DEBUG)
            System.out.println ("ImplGenerator::getTopParentType (" + type.getName () + ", " // NOI18N
                                + from + ");"); // NOI18N

        if (type instanceof TypeElement) {
            TypeElement type_element = (TypeElement)type;
            if (type_element.getType ().getType() == IDLType.STRUCT
                    || type_element.getType ().getType() == IDLType.UNION
                    || type_element.getType ().getType() == IDLType.ENUM) {
                if (DEBUG)
                    System.out.println ("-- top parent type found"); // NOI18N
                return type;
            }
        }


        if (type instanceof InterfaceElement) {
            if (DEBUG)
                System.out.println ("-- top parent interface found"); // NOI18N

            return type;
        }

        IDLElement parent = findElementByName (((TypeElement)type).getType ().getName (), from);
        IDLElement tmp_element;
        if (parent != null) {
            if (parent instanceof TypeElement || parent instanceof InterfaceElement) {
                if ((tmp_element = getTopParentTypeElement (parent, from)) != null)
                    return tmp_element;
                else
                    return parent;
            }
            else
                return type;
        }
        return null;
    }


    public int getArrayDimensionOfType (IDLElement type, IDLElement from, int counter) {
        if (from == null || type == null)
            return 0;
        if (DEBUG)
            System.out.println ("ImplGenerator::getArrayDimensionOfType (" + type.getName () + ", " // NOI18N
                                + from + ", " + counter + ");"); // NOI18N

        if (type instanceof TypeElement) {
            TypeElement type_element = (TypeElement)type;
            if (type_element.getType ().getType() == IDLType.STRUCT
                    || type_element.getType ().getType() == IDLType.UNION
                    || type_element.getType ().getType() == IDLType.ENUM) {
                if (DEBUG)
                    System.out.println ("-- top parent type found"); // NOI18N
                return counter;
            }
        }

        if (type instanceof InterfaceElement) {
            if (DEBUG)
                System.out.println ("-- top parent interface found"); // NOI18N

            return counter;
        }

        if (type instanceof DeclaratorElement) {
            if (DEBUG)
                System.out.println ("declarator element!"); // NOI18N
            DeclaratorElement de = (DeclaratorElement)type;
            counter = counter + de.getDimension ().size ();
            if (DEBUG)
                System.out.println ("counter: " + counter); // NOI18N

        }

        TypeElement type_element = (TypeElement)type;
        IDLElement parent;
        if (type_element.getType ().getType () != IDLType.SEQUENCE) {
            parent = findElementByName (type_element.getType ().getName (), from);
        }
        else {
            // sequence
            parent = findElementByName (type_element.getType ().ofType ().getName (), from);
        }
        //IDLElement tmp_element;
        if (parent != null) {
            if (parent instanceof TypeElement)
                return getArrayDimensionOfType (parent, from, counter);
            if (parent instanceof InterfaceElement)
                return getArrayDimensionOfType (parent, from, counter);

        }
        //return 0;
        return counter;
    }


    public static IDLElement findModule (String name, IDLElement from) {
        if (DEBUG)
            System.out.println ("ImplGenerator::findModule (" + name + ", " + from.getName () + ":" // NOI18N
                                + from + ");"); // NOI18N
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


    public static IDLElement findModuleIn (String name, IDLElement from) {

        if (from == null)
            return null;

        if (DEBUG)
            System.out.println ("ImplGenerator::findModuleIn (" + name + ", " + from.getName () // NOI18N
                                + ":" + from + ");"); // NOI18N

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


    public static IDLElement findElementByName (String name, IDLElement from) {
        // constructed type by name
        if (DEBUG)
            System.out.println ("ImplGenerator::findElementByName (" + name + ", " + from.getName () // NOI18N
                                + ":" + from + ");"); // NOI18N
        //from = findType (name, from);
        Vector mm = from.getMembers ();
        IDLElement result = null;
        // for absolute sope names
        if (isAbsoluteScopeName (name)) {
            IDLElement tmp_from = findTopLevelModuleForName (name, from);
            String tmp_name = getSimpleName (name);
            result = findElementInElement (name, tmp_from);

            return result;
        }

        if (isScopeName (name)) {
            IDLElement tmp_from = findModuleForScopeName (name, from);
            String tmp_name = getSimpleName (name);
            result = findElementInElement (name, tmp_from);

            return result;
        }
        result = findElementInElement (name, from);
        if (result != null)
            return result;
        else
            if (from.getParent () != null)
                return findElementByName (name, from.getParent ());
        return null;
    }


    public static IDLElement findModuleForScopeName (String name, IDLElement from) {
        if (DEBUG)
            System.out.println ("ImplGenerator::findModuleForScopeName (" + name + ", " + // NOI18N
                                from.getName () + ":" + from + ");"); // NOI18N
        StringTokenizer st = new StringTokenizer (name, "::"); // NOI18N
        String retval = ""; // NOI18N
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


    public IDLElement findModuleForScopeType (IDLType type, IDLElement from) {
        if (DEBUG)
            System.out.println ("ImplGenerator::findModuleForScopeType (" + type + ", " // NOI18N
                                + from.getName () + ":" + from + ");"); // NOI18N
        if (type.getType () == IDLType.SEQUENCE)
            return findModuleForScopeName (type.ofType ().getName (), from);
        else
            return findModuleForScopeName (type.getName (), from);

        /*
          StringTokenizer st = new StringTokenizer (type.getName (), "::");
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
        */
    }

    public static IDLElement findTopLevelModuleForName (String name, IDLElement from) {
        if (DEBUG)
            System.out.println ("ImplGenerator::findTopLevelModuleForName (" + name + ", " // NOI18N
                                + from.getName () + ":" + from + ");"); // NOI18N
        StringTokenizer st = new StringTokenizer (name, "::"); // NOI18N
        String retval = ""; // NOI18N
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

    public IDLElement findTopLevelModuleForType (IDLType type, IDLElement from) {
        if (DEBUG)
            System.out.println ("ImplGenerator::findTopLevelModuleForType (" + type + ", " // NOI18N
                                + from.getName () + ":" + from + ");"); // NOI18N
        if (type.getType () == IDLType.SEQUENCE)
            return findTopLevelModuleForName (type.ofType ().getName (), from);
        else
            return findTopLevelModuleForName (type.getName (), from);

    }


    public String ctype2package (IDLElement type) {
        // checking modules
        if (DEBUG)
            System.out.println ("ImplGenerator::ctype2package (" + type + ");"); // NOI18N
        String modules = ""; // NOI18N
        if (type != null) {
            Vector mods = new Vector ();
            mods.add (type.getName ());
            while (type.getParent () != null) {
                type = type.getParent ();
                if (type instanceof ModuleElement)
                    mods.add (type.getName ());
                if (type instanceof InterfaceElement)
                    mods.add (type.getName () + "Package"); // NOI18N

            }
            // transform modules names from vector to string in package format
            for (int i=mods.size () - 1; i>=0; i--) {
                if (DEBUG)
                    System.out.println ("transfrom: " + (String)mods.elementAt (i)); // NOI18N
                modules = modules + (String)mods.elementAt (i) + "."; // NOI18N
            }
            // without last dot
            modules = modules.substring (0, modules.length () - 1);
            if (DEBUG)
                System.out.println ("result: >" + modules + "<"); // NOI18N
        }
        return modules;
    }


    public Type isSimpleType (IDLType type, IDLElement _element) {
        if (DEBUG)
            System.out.println ("ImplGenerator::isSimpleType (" + type + ", " + _element.getName () // NOI18N
                                + ");"); // NOI18N
        return type2java (type);
    }


    public static String nameFromScopeName (String name) {
        if (name != null) {
            if (name.lastIndexOf ("::") != -1) { // NOI18N
                return name.substring (name.lastIndexOf ("::") + 2, name.length ()); // NOI18N
            }
        }
        return name;
    }


    public static IDLElement findElementInElement (String name, IDLElement element) {
        name = nameFromScopeName (name);
        if (element == null)
            return null;

        if (DEBUG)
            System.out.println ("ImplGenerator::findElementInElement (" + name + ", " // NOI18N
                                + element.getName () + ":" + element + ");"); // NOI18N
        Vector mm = element.getMembers ();
        IDLElement tmp_element = null;

        for (int i=0; i<mm.size (); i++) {
            if (DEBUG)
                System.out.println ("i = " + i); // NOI18N
            if (element.getMember (i) instanceof TypeElement) {
                if (DEBUG)
                    System.out.println ("type element"); // NOI18N
                if ((element.getMember (i).getMember (0) instanceof StructTypeElement)
                        || (element.getMember (i).getMember (0) instanceof EnumTypeElement)
                        || (element.getMember (i).getMember (0) instanceof UnionTypeElement)) {
                    if (DEBUG)
                        System.out.println ("constructed type"); // NOI18N
                    if (((TypeElement)element.getMember (i).getMember (0)).getName ().equals (name)) {
                        tmp_element = element.getMember (i).getMember (0);
                        if (DEBUG)
                            System.out.println ("element: " + tmp_element+ " : " + tmp_element.getName ()); // NOI18N
                        return tmp_element;
                    }
                }
                if (element.getMember (i).getMember (0) instanceof DeclaratorElement) {
                    if (DEBUG) {
                        System.out.println ("declarator element: " + // NOI18N
                                            ((TypeElement)element.getMember (i).getMember (0)).getName ()
                                            + ":" + ((DeclaratorElement)element.getMember (i).getMember (0)).getType ().ofDimension ()); // NOI18N
                    }

                    if (((TypeElement)element.getMember (i).getMember (0)).getName ().equals (name)) {
                        tmp_element = element.getMember (i).getMember (0);
                        if (DEBUG)
                            System.out.println ("element: " + tmp_element+ " : " + tmp_element.getName ()); // NOI18N
                        return tmp_element;
                    }
                }
                if ((element.getMember (i).getMembers ().size () > 1)
                        && (element.getMember (i).getMember (element.getMember (i).getMembers ().size () - 1)
                            instanceof DeclaratorElement)) {
                    int last = element.getMember (i).getMembers ().size () - 1;
                    if (DEBUG) {
                        System.out.println ("last declarator element: " + // NOI18N
                                            ((TypeElement)element.getMember (i).getMember (last)).getName ());
                        System.out.println ("name: " + name); // NOI18N
                    }
                    if (((TypeElement)element.getMember (i).getMember (last)).getName ().equals (name)) {
                        tmp_element = element.getMember (i).getMember (last);
                        if (DEBUG)
                            System.out.println ("element: " + tmp_element+ " : " + tmp_element.getName ()); // NOI18N
                        return tmp_element;
                    }
                }
            }
            if (element.getMember (i) instanceof ExceptionElement) {
                if (DEBUG)
                    System.out.println ("exception"); // NOI18N
                if (((IDLElement)element.getMember (i)).getName ().equals (name)) {
                    tmp_element = element.getMember (i);
                    if (DEBUG)
                        System.out.println ("element: " + tmp_element + " : " + tmp_element.getName ()); // NOI18N
                    return (IDLElement)tmp_element;

                }
            }
            if (element.getMember (i) instanceof InterfaceElement) {
                if (DEBUG)
                    System.out.println ("interface element"); // NOI18N
                if (((IDLElement)element.getMember (i)).getName ().equals (name)) {
                    tmp_element = element.getMember (i);
                    if (DEBUG)
                        System.out.println ("element: " + tmp_element + " : " + tmp_element.getName ()); // NOI18N
                    return (IDLElement)tmp_element;

                }
            }
        }
        return null;
    }


    public Type type2java (IDLType idl_type, int mode, String _package,
                           InterfaceElement _interface) {
        if (DEBUG)
            System.out.println ("ImplGenerator::type2java (" + idl_type + ", " + mode + ", " // NOI18N
                                + _package + ", " + _interface.getName () + ");"); // NOI18N
        String name_of_interface = _interface.getName ();
        String type_name;
        Type type = null;
        IDLType tmp_type;
        int dim = 0;

        if (DEBUG)
            System.out.println ("-- is simple type?"); // NOI18N
        if ((type = isSimpleType (idl_type, _interface)) != null) {
            // idl_type is mapped to java simple type
            if (mode == Parameter.IN)
                return type;
            else {
                //	if (type.isPrimitive ())
                try {
                    return JavaTypeToHolder (type);
                } catch (UnknownTypeException e) {
                    TopManager.getDefault ().notifyException (e);
		    _M_exception_occured = true;
		    //e.printStackTrace ();
                }
                //else
                //return JavaTypeToHolder (Type.createClass (org.openide.src.Identifier.create (idl_type.getName ())));
            }
        }
        if (DEBUG)
            System.out.println ("-- is type with simple parent?"); // NOI18N
        if ((type = hasSimpleParent (idl_type, _interface)) != null) {
            // idl_type is mapped throw other idl type(s) to simple java type
            if (mode == Parameter.IN) {
                // is array ???
                IDLElement tmp_type2 = findElementInElement (idl_type.getName (), _interface);
                if (DEBUG)
                    System.out.println (tmp_type2);
                return type;
            }
            else {
                // is array ???
                //if (type.isPrimitive ())
                try {
                    return JavaTypeToHolder (type);
                } catch (UnknownTypeException e) {
                }
                //else
                //  return JavaTypeToHolder (Type.createClass (org.openide.src.Identifier.create
                //					     (idl_type.getName ())));
            }
        }
        if (DEBUG)
            System.out.println ("-- is type with simple parent? NOOOO"); // NOI18N

        if (DEBUG)
            System.out.println ("-- is type with template parent?"); // NOI18N

        WAS_TEMPLATE = false;
        if ((type = hasTemplateParent (idl_type, mode, _package, _interface)) != null) {
            // idl_type is template type
            if (DEBUG)
                System.out.println ("-- is type with template parent? YEEES"); // NOI18N

            if (DEBUG)
                System.out.println ("-- has this template type array parent?"); // NOI18N
            IDLElement tmp_element_for_type = findElementByName (idl_type.getName (), _interface);
            IDLElement element_for_type = null;
            // here we must find top parent type of type
            if (mode == Parameter.IN) {
                //element_for_type = getTopParentTypeElement (tmp_element_for_type, _interface);
                dim = getArrayDimensionOfType (tmp_element_for_type, _interface, 0);

                if (DEBUG)
                    System.out.println ("dim: " + dim); // NOI18N
                for (int i=0; i<dim; i++) {
                    type = Type.createArray (type);
                }
            }

            return type;
        }
        if (DEBUG)
            System.out.println ("-- is type with template parent? NOOOO"); // NOI18N

        //if (DEBUG)
        //  System.out.println ("cons: " + ctype2package (findElementByName (idl_type.getName (), // NOI18N
        //								       _interface)));

        if (DEBUG)
            System.out.println ("-- is type with absolute scope name"); // NOI18N
        if (isAbsoluteScopeType (idl_type)) {
            // is absolute scoped name

            //if ((tmp_type = hasSimpleParent (idl_type, _interface)) != null)
            //   return tmp_type;
            IDLElement tmp = findTopLevelModuleForType (idl_type, _interface);
            //
            IDLElement element_for_type = findElementInElement (idl_type.getName (), tmp);
            String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_type);
            if (mode != Parameter.IN)
                full_name = full_name + "Holder"; // NOI18N
            if (DEBUG)
                System.out.println ("::id6 " + full_name); // NOI18N
            type = Type.createClass (org.openide.src.Identifier.create (full_name));

            return type;
        }
        if (DEBUG)
            System.out.println ("-- is type with scope name"); // NOI18N
        if (isScopeType (idl_type)) {
            // find first module of this scoped type

            // is in module second module of this scoped type?

            // etc to simple type => is this simple type?
            //Type tmp_type;
            IDLElement tmp = findModuleForScopeType (idl_type, _interface);
            IDLElement element_for_type = findElementInElement (idl_type.getName (), tmp);
            String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_type);
            if (mode != Parameter.IN)
                full_name = full_name + "Holder"; // NOI18N
            if (DEBUG)
                System.out.println ("::id7 " + full_name); // NOI18N
            type = Type.createClass (org.openide.src.Identifier.create (full_name));

            return type;

        }

        if (DEBUG)
            System.out.println ("-- is type normal name"); // NOI18N
        //Type tmp_type;
        IDLElement tmp_element_for_type = findElementByName (idl_type.getName (), _interface);
        IDLElement element_for_type = null;
        dim = getArrayDimensionOfType (tmp_element_for_type, _interface, 0);
        if (DEBUG)
            System.out.println ("dim: " + dim); // NOI18N
        // here we must find top parent type of type
        if (mode == Parameter.IN || dim == 0) {
            element_for_type = getTopParentTypeElement (tmp_element_for_type, _interface);
            if (element_for_type == null) {
                // we don't find top level type
                element_for_type = tmp_element_for_type;
            }
        }
        else {
            element_for_type = tmp_element_for_type;
        }
        if (DEBUG)
            System.out.println ("element_for_type: " + element_for_type.getName () + " : " // NOI18N
                                + element_for_type);
        String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_type);
        if (mode != Parameter.IN)
            full_name = full_name + "Holder"; // NOI18N
        if (DEBUG)
            System.out.println ("::id8 " + full_name); // NOI18N
        type = Type.createClass (org.openide.src.Identifier.create (full_name));
        if (mode == Parameter.IN) {
            for (int i=0; i<dim; i++) {
                // creating array from type
                type = Type.createArray (type);
            }
        }

        return type;

    }


    public String exception2java (String ex, String _package, InterfaceElement _interface) {
        if (DEBUG)
            System.out.println ("-- is exception with absolute scope name"); // NOI18N

        if (isAbsoluteScopeName (ex)) {
            // is absolute scope name
            IDLElement tmp = findTopLevelModuleForName (ex, _interface);
            IDLElement element_for_exception = findElementInElement (ex, tmp);
            String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_exception);

            return full_name;
        }
        if (DEBUG)
            System.out.println ("-- is exception with scope name"); // NOI18N
        if (isScopeName (ex)) {
            IDLElement tmp = findModuleForScopeName (ex, _interface);
            IDLElement element_for_exception = findElementInElement (ex, tmp);
            String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_exception);

            return full_name;

        }
        if (DEBUG)
            System.out.println ("-- is exception with normal name"); // NOI18N
        IDLElement element_for_exception = findElementByName (ex, _interface);
        if (DEBUG)
            System.out.println ("element_for_exception: " + element_for_exception.getName () + " : " // NOI18N
                                + element_for_exception);
        String full_name =""; // NOI18N
            if (_package.length() >0) 
                full_name = _package + "."; // NOI18N
            full_name = full_name + ctype2package (element_for_exception);

        return full_name;
    }

    /*
    public boolean existMethodInClass (ClassElement clazz, MethodElement method) {
      if (DEBUG) {
        System.out.println ("ImplGenerator::existMethodInClass (" + clazz + ", " + method + ");");
        System.out.println ("find id
      MethodParameter[] mps = method.getParameters ();
      org.openide.src.Identifier id = method.getName ();
      Type[] types = new Type [mps.length];
      for (int i=0; i<mps.length; i++)
        types[i] = mps[i].getType ();

      if (DEBUG) {
        System.out.println ("methods: ");
        MethodElement[] methods = clazz.getMethods ();
        for (int i=0; i<methods.length; i++) {
    System.out.println (i + ". " + methods[i]);
    System.out.println ("id: " + methods[i].getName ());
    MethodParameter[] params = methods[i].getParameters ();
    for (int j=0; j<params.length; j++)
     System.out.println ("param[" + j + "]: " + params[j]); 
        }
      }
      if (clazz.getMethod (id, types) != null) {
        if (DEBUG)
    System.out.println ("\n------------- YES!!!");
        return true;
      }
      else {
        if (DEBUG)
    System.out.println ("\n------------- NO!!!");
        return false;
      }
}
    */

    public boolean existMethodInClass (ClassElement clazz, MethodElement method) {
        if (DEBUG) {
            System.out.println ("ImplGenerator::existMethodInClass (" + clazz + ", " + method + ");"); // NOI18N
        }
        MethodParameter[] mps = method.getParameters ();
        org.openide.src.Identifier id = method.getName ();
        Type[] types = new Type [mps.length];
        for (int i=0; i<mps.length; i++)
            types[i] = mps[i].getType ();

        boolean exist_id = false;
        boolean exist_params = false;
        //System.out.println ("methods: "); // NOI18N
        MethodElement[] methods = clazz.getMethods ();
        for (int i=0; i<methods.length; i++) {
            if (DEBUG) {
		System.out.println (i + ". " + methods[i]); // NOI18N
		System.out.println ("id: " + methods[i].getName ()); // NOI18N
	    }
            if (id.equals (methods[i].getName ())) {
                exist_id = true;
                MethodParameter[] params = methods[i].getParameters ();
                if (params.length == 0 && mps.length == 0)
                    exist_params = true;
                for (int j=0; j<params.length; j++) {
                    try {
                        if (!params[j].equals (mps[j])) {
                            exist_params = false;
                            break;
                        }
                        else {
                            exist_params = true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        exist_params = false;
                        break;
                    }
                }
                if (exist_id && exist_params) {
                    if (DEBUG)
                        System.out.println ("\n------------ YES!!!"); // NOI18N
                    return true;
                }
                else {
                    exist_params = false;
                    exist_id = false;
                }
            }
        }
        if (DEBUG)
            System.out.println ("\n------------ NOOO"); // NOI18N
        return false;
    }

    public void attribute2java (AttributeElement attr, ClassElement clazz) {
        String _package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        InterfaceElement _interface = (InterfaceElement)attr.getParent ();
        Type attr_type = type2java (attr.getType (), Parameter.IN, _package, _interface);
        if (DEBUG)
            System.out.println ("attribute2java"); // NOI18N
        if (DEBUG) {
            System.out.println ("attribute: " + attr.getName ()); // NOI18N
            System.out.println ("type: " + attr.getType ()); // NOI18N
            System.out.println ("java: " + attr_type); // NOI18N
            System.out.println ("package: " + _package); // NOI18N
        }
        try {
            MethodElement geter = new MethodElement ();
            if (DEBUG)
                System.out.println ("::id9 " + attr.getName ()); // NOI18N
            geter.setName (org.openide.src.Identifier.create (attr.getName ()));
            geter.setModifiers (Modifier.PUBLIC);
            geter.setReturn (attr_type);
            //geter.setBody ("\n  return null;\n"); // NOI18N
            setBodyOfMethod (geter);
            //if (!existMethodInClass (clazz, geter))
            clazz.addMethod (geter); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
        }

        if (!attr.getReadOnly ()) {
            try {
                MethodElement seter = new MethodElement ();
                if (DEBUG)
                    System.out.println ("::id10 " + attr.getName ()); // NOI18N
                seter.setName (org.openide.src.Identifier.create (attr.getName ()));
                seter.setModifiers (Modifier.PUBLIC);
                seter.setReturn (Type.VOID);
                //seter.setBody ("\n"); // NOI18N
                setBodyOfMethod (seter);
                seter.setParameters (new MethodParameter[] {
                                         new MethodParameter ("value", attr_type, false) }); // NOI18N
                //if (!existMethodInClass (clazz, seter))
                clazz.addMethod (seter); // now addMethod throws SourceExcetion
            } catch (SourceException e) {
                //e.printStackTrace ();
            }
        }

    }

    public void operation2java (OperationElement operation, ClassElement clazz) {
        if (DEBUG)
            System.out.println ("operation2java"); // NOI18N
        String _package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        InterfaceElement _interface = (InterfaceElement)operation.getParent ();
        Type rettype = type2java (operation.getReturnType (), Parameter.IN, _package, _interface);
        if (DEBUG) {
            System.out.println ("operation: " + operation.getName ()); // NOI18N
            System.out.println ("operation rettype:" + operation.getReturnType () + ":"); // NOI18N
            System.out.println ("return type: " + rettype); // NOI18N
        }
        try {
            MethodElement oper = new MethodElement ();
            if (DEBUG)
                System.out.println ("::id11 " + operation.getName ()); // NOI18N
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
                                            ("ctx", Type.createClass (org.openide.src.Identifier.create // NOI18N
                                                                      ("org.omg.CORBA.Context")), false); // NOI18N
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
            //if (oper.getReturn () != Type.VOID)
            //oper.setBody ("\n  return null;\n"); // NOI18N
            setBodyOfMethod (oper);

            //if (!existMethodInClass (clazz, oper))
            clazz.addMethod (oper); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
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


    /*
      public void synchronize (InterfaceElement element, String name) {
      System.out.println ("Emacs2");
      }
      
      public void generate (InterfaceElement element, String name) {
      System.out.println ("Emacs");
      }
    */

    public void interface2java (ClassElement clazz, InterfaceElement element)
    throws SymbolNotFoundException {
        //if (element == null)
        // throw new SymbolNotFound ();

        if (DEBUG)
            System.out.println ("ImplGenerator::interface2java (clazz, " + element.getName () + ");"); // NOI18N
        // parents...

        Vector parents = element.getParents ();
        for (int i=0; i<parents.size (); i++) {
            /*
            String name_of_parent = ((org.netbeans.modules.corba.idl.src.Identifier)
            parents.elementAt (i)).getName ();
            */
            String name_of_parent = (String)parents.elementAt (i);
            IDLElement parent
		= findElementByName (name_of_parent, element);
            //InterfaceElement parent = (InterfaceElement)id.getParent ();
            if (parent == null) {
                //throw new SymbolNotFound (name_of_parent);
                //new SymbolNotFound (name_of_parent);
                throw new SymbolNotFoundException (name_of_parent);
            }
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

    /** generate ClassElement from InterfaceElement
    */

    public void interface2java (InterfaceElement element)
	throws SymbolNotFoundException, RecursiveInheritanceException, java.io.IOException {
        if (DEBUG)
            System.out.println ("interface2java: " + element.getName ()); // NOI18N
        if (DEBUG)
            System.out.println ("name: " + _M_ido.getPrimaryFile ().getName ()); // NOI18N

	RecursiveInheritanceChecker.check (element);

        String impl_name = ""; // NOI18N
        String super_name = ""; // NOI18N
        String modules = modules2package (element);
        String _package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
	
        if (DEBUG) {
            System.out.println ("modules:>" + modules + "<"); // NOI18N
            System.out.println ("package:>" + _package + "<"); // NOI18N
        }


        if (!TIE) {
            impl_name = IMPLBASE_IMPL_PREFIX + element.getName () + IMPLBASE_IMPL_POSTFIX;
            if (where_generate == IN_IDL_PACKAGE) {
                if (_package.length() >0)
                    super_name = _package + "."; // NOI18N
                super_name = super_name + modules + EXT_CLASS_PREFIX + element.getName ()
                             + EXT_CLASS_POSTFIX;
            } else
                super_name = EXT_CLASS_PREFIX + element.getName () + EXT_CLASS_POSTFIX;
        }
        else {
            impl_name = TIE_IMPL_PREFIX + element.getName () + TIE_IMPL_POSTFIX;
            if (where_generate == IN_IDL_PACKAGE) {
                if (_package.length() >0)
                    super_name = _package + "."; // NOI18N
                super_name = super_name + modules + IMPL_INT_PREFIX + element.getName ()
                             + IMPL_INT_POSTFIX;
            } else
                super_name = IMPL_INT_PREFIX + element.getName () + IMPL_INT_POSTFIX;
        }

        // print to status line
        String status_package = ""; // NOI18N
        StringTokenizer st = new StringTokenizer (_package, "."); // NOI18N
        while (st.hasMoreTokens ()) {
            status_package += st.nextToken () + "/"; // NOI18N
        }

	String __status_package = status_package;
	String __impl_name = impl_name;
	//System.out.println ("Generate " + __status_package + __impl_name + " ..."); // NOI18N
	java.lang.Object[] __arr = new Object[] {__status_package + __impl_name};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

        Vector members = element.getMembers ();

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

            final ClassElement clazz = new ClassElement ();
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

            final FileObject folder = _M_ido.getPrimaryFile ().getParent ();
            final FileObject impl;

            if ((impl = folder.getFileObject (impl_name, "java")) != null) { // NOI18N
                if (DEBUG)
                    System.out.println ("file exists"); // NOI18N
                String full_name = ""; // NOI18N
                if (_package.length() > 0) 
                    full_name = full_name + _package + "."; // NOI18N
                full_name = full_name + impl_name;
                if (DEBUG)
		    System.out.println ("full name: " + full_name); // NOI18N
                ClassElement dest = ClassElement.forName (full_name);
                if (DEBUG) {
                    System.out.println ("orig class: " + dest.toString ()); // NOI18N
                    System.out.println ("new class: " + clazz.toString ()); // NOI18N
                }

		if (css.getActiveSetting ().getSynchro () != ORBSettingsBundle.SYNCHRO_DISABLED) {
		    /*
		      javax.swing.SwingUtilities.invokeLater (new Runnable () {
		      public void run () {
		    */
		    List changes = new LinkedList ();
		    JavaConnections.compareMethods 
			(dest, clazz, changes, CORBASupport.ADD_METHOD, CORBASupport.UPDATE_METHOD);
		    if (changes.size () > 0)
			JavaConnections.showChangesDialog 
			    (changes, (byte)JavaConnections.TYPE_ALL);
		    /*
		      }
		      });
		    */
                }
		else {
		    this.showMessage = false;
		}
		
            }
            else {
                if (DEBUG)
                    System.out.println ("file don't exists"); // NOI18N
                //try {
		//final FileObject __final_impl = impl;
		//final FileObject __final_impl;// = impl;
		final String __final_package = _package;
		final String __final_impl_name = impl_name;
		folder.getFileSystem ().runAtomicAction 
		    (new org.openide.filesystems.FileSystem.AtomicAction () {
			    public void run () throws java.io.IOException {
				final FileObject __final_impl = folder.createData (__final_impl_name, "java"); // NOI18N
				//} catch (IOException e) {
				//e.printStackTrace ();
				//}
				
				FileLock lock = __final_impl.lock ();
				//	 if (source != null)
				//   source.addClass (clazz);
				PrintStream printer = new PrintStream (__final_impl.getOutputStream (lock));
				
				// add comment
				printer.println ("/*\n * This file was generated from "
						 + _M_ido.getPrimaryFile ().getName () + ".idl\n"
						 + " */"); // NOI18N
				
				if (__final_package.length() > 0) // If it isn't in file system root
				    printer.println ("\npackage " + __final_package + ";\n"); // NOI18N
				printer.println (clazz.toString ());
				_M_generated_impls.add (__final_impl);
				lock.releaseLock ();
			    }
			});
	    }
		     
	    //JavaDataObject __jdo = (JavaDataObject)DataObject.find (impl);
	    //OpenCookie __cookie = (OpenCookie)__jdo.getCookie (OpenCookie.class);
	    //__cookie.open ();
	    //_M_generated_impls.add (impl);

	} catch (org.openide.src.SourceException e) {
	    //e.printStackTrace ();
	    //} catch (java.io.IOException e) {
	    //e.printStackTrace ();
	    //throw e;
	}
    }
	
	
    public void generate () {
	this.showMessage = true;  // We suppose that we generate or synchronization is not disabled
        if (DEBUG) {
            System.out.println ("generate :-))"); // NOI18N
            try {
                _M_src.dump (""); // NOI18N
            } catch (NullPointerException ex) {
                ex.printStackTrace ();
            }
        }
	
	_M_generated_impls = new LinkedList ();

        //if (_M_src == null) {
	if (DEBUG)
	    System.out.println ("status = " + _M_ido.getStatus ()); // NOI18N
	if (_M_ido.getStatus () == IDLDataObject.STATUS_ERROR) {
	    java.lang.Object[] __arr = new Object[] {_M_ido.getPrimaryFile ().getName ()};
            TopManager.getDefault ().setStatusText 
		(MessageFormat.format (CORBASupport.PARSE_ERROR, __arr));
		//("Parse Error in " + _M_ido.getPrimaryFile ().getName () + "."); // NOI18N
	    if (_M_listen) {
		_M_ido.removePropertyChangeListener (this);
		_M_listen = false;
	    }
            return;
        }
	if (_M_ido.getStatus () == IDLDataObject.STATUS_NOT_PARSED) {
            TopManager.getDefault ().setStatusText 
		(CORBASupport.WAITING_FOR_PARSER);
		//("Waiting For Idl Parser..."); // NOI18N
	    if (!_M_listen) {
		_M_ido.addPropertyChangeListener (this);
		_M_listen = true;
	    }
            return;
        }
	if (_M_ido.getStatus () == IDLDataObject.STATUS_PARSING) {
	    java.lang.Object[] __arr = new Object[] {_M_ido.getPrimaryFile ().getName ()};
            TopManager.getDefault ().setStatusText
		(MessageFormat.format (CORBASupport.PARSING, __arr));
		//("Parsing " + _M_ido.getPrimaryFile ().getName () + "..."); // NOI18N
	    if (!_M_listen) {
		_M_ido.addPropertyChangeListener (this);
		_M_listen = true;
	    }
            return;
        }

        //Vector members = _M_src.getMembers ();     // update for working with modules :-))
        Vector members = this.getInterfaces (_M_src.getMembers ());
        for (int i=0; i<members.size (); i++) {
            if (members.elementAt (i) instanceof InterfaceElement)
                try {
                    interface2java ((InterfaceElement)members.elementAt (i));
                    //} catch (SymbolNotFound e) {
                } catch (SymbolNotFoundException __ex) {
		    java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		    //String __msg = MessageFormat.format ("can't find symbol: {0}", __arr);
		    //System.out.println ("msg: " + __msg);
		    TopManager.getDefault ().notify 
			(new NotifyDescriptor.Exception 
			    (__ex, MessageFormat.format (CORBASupport.CANT_FIND_SYMBOL, __arr)));
			    //(__ex, MessageFormat.format ("Can't find symbol: {0}", __arr)));
		    //(__ex, "can't find symbol: " + __ex.getSymbolName ())); // NOI18N
                    //System.err.println ("can't find symbol: " + __ex.getSymbolName ());
                    //System.err.println (e);
		    _M_exception_occured = true;
		} catch (RecursiveInheritanceException __ex) {
		    java.lang.Object[] __arr 
			= new java.lang.Object[] {__ex.getName (), new Integer (__ex.getLine ())};
		    String __msg = MessageFormat.format 
			(CORBASupport.RECURSIVE_INHERITANCE, __arr);
		    TopManager.getDefault ().notify 
			(new NotifyDescriptor.Exception 
			    (__ex, __msg));
		    _M_exception_occured = true;
		} catch (Exception __ex) {
		    TopManager.getDefault ().notify (new NotifyDescriptor.Exception (__ex));
		    __ex.printStackTrace ();
		    _M_exception_occured = true;
		}
        }

	if (this.getOpen ()) {
	    // open all generated classes in IDE Editor
	    Iterator __iterator = _M_generated_impls.iterator ();
	    while (__iterator.hasNext ()) {
		FileObject __fo = null;
		try {
		    __fo = (FileObject)__iterator.next ();
		    JavaDataObject __jdo = (JavaDataObject)DataObject.find (__fo);
		    OpenCookie __cookie = (OpenCookie)__jdo.getCookie (OpenCookie.class);
		    __cookie.open ();
		} catch (DataObjectNotFoundException __ex) {
		    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
			System.out.println ("can't find " + __fo.toString ()); // NOI18N
		}
	    }
	}
	//System.out.println ("showMessage: " + this.showMessage);
	//System.out.println ("_M_exception_occured: " + this._M_exception_occured);
	if (this.showMessage && (!this._M_exception_occured)) { 
	    // Bug Fix, when sync is disabled, don't show the message
	    //javax.swing.SwingUtilities.invokeLater (new Runnable () {
	    //public void run () {
	    java.lang.Object[] __arr = new Object[] {_M_ido.getPrimaryFile ().getName ()};
	    TopManager.getDefault ().setStatusText
		(MessageFormat.format (CORBASupport.SUCESS_GENERATED, __arr));
		//("Successfully Generated Implementation Classes for " // NOI18N
		// + _M_ido.getPrimaryFile ().getName () + "."); // NOI18N
	    //}
	    //});
	}
	if (!this.showMessage) {
	    TopManager.getDefault ().setStatusText 
		(ORBSettingsBundle.SYNCHRO_DISABLED);
		//("Idl synchronization for this project is disabled."); // NOI18N
	}

	if (_M_listen) {
	    _M_ido.removePropertyChangeListener (this);
	    _M_listen = false;
	}

    }


    public void setBodyOfMethod (MethodElement method) throws SourceException {
        if (DEBUG) {
	    System.out.println ("setBodyOfMethod (" + method + ");"); // NOI18N
            //System.out.println ("css.getGeneration () : " + css.getGeneration ()); // NOI18N
        }

	if (css.getActiveSetting ().getGeneration ().equals (ORBSettingsBundle.GEN_NOTHING)) {
	    //System.out.println ("CORBASupport.GEN_NOTHING"); // NOI18N
	    method.setBody ("\n"); // NOI18N
	    return;
	}
	if (css.getActiveSetting ().getGeneration ().equals (ORBSettingsBundle.GEN_EXCEPTION)) {
	    //System.out.println ("CORBASupport.GEN_EXCEPTION"); // NOI18N
	    method.setBody ("\n  throw new UnsupportedOperationException ();\n"); // NOI18N
	    return;
	}
	if (css.getActiveSetting ().getGeneration ().equals (ORBSettingsBundle.GEN_RETURN_NULL)) {
            //System.out.println ("CORBASupport.GEN_RETURN_NULL"); // NOI18N
            method.setBody ("\n  return null;\n"); // NOI18N
            return;
	}

    }

    
    public void propertyChange (PropertyChangeEvent __event) {
	if (DEBUG)
	    System.out.println ("property change: " + __event.getPropertyName ());
	if (__event.getPropertyName ().equals ("_M_status")) {
	    _M_src = _M_ido.getSources ();
	    RequestProcessor __processor = _M_ido.getGeneratorProcessor ();
	    __processor.post (new Runnable () {
		    public void run () {
			ImplGenerator.this.generate ();
		    }
		});
	}
    }

}
