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


   private com.netbeans.enterprise.modules.corba.idl.src.Element src;

   private String IMPL_PREFIX;
   private String IMPL_POSTFIX;
   private String EXT_CLASS_PREFIX;
   private String EXT_CLASS_POSTFIX;
   
   private IDLDataObject ido;

   public ImplGenerator (IDLDataObject _do) {
      
      ido = _do;

      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	 (CORBASupportSettings.class, true);

      IMPL_PREFIX = css.getImplPrefix ();
      IMPL_POSTFIX = css.getImplPostfix ();
      EXT_CLASS_PREFIX = css.getExtClassPrefix ();
      EXT_CLASS_POSTFIX = css.getExtClassPostfix ();
   }

   public void setSources (com.netbeans.enterprise.modules.corba.idl.src.Element e) {
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

   public boolean isAbsoluteScopedName (String name) {
      if (DEBUG)
	 System.out.println ("isAbsoluteScopedName (" + name + ");");
      if (name.substring (0, 2).equals ("::"))
	 return true;
      else
	 return false;
   }

   public boolean isScopedName (String name) {
      if (DEBUG)
         System.out.println ("isScopedName (" + name + ");");
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

   public boolean isTypeDefinedIn (InterfaceElement _interface, String idl_type) {
      boolean is_in = false;
      if (DEBUG)
	 System.out.println ("isTypeDefinedIn ...");
      for (int i=0; i<_interface.getMembers ().size (); i++) {
	 if (_interface.getMember (i) instanceof TypeElement)
	    if ((_interface.getMember (i).getMember (0) instanceof StructTypeElement)
	     || (_interface.getMember (i).getMember (0) instanceof EnumTypeElement)
	     || (_interface.getMember (i).getMember (0) instanceof UnionTypeElement))
	       if (((TypeElement)_interface.getMember (i).getMember (0)).getName ()
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
	 
   public Type type2java (String idl_type, int mode, String _package, InterfaceElement _interface) {
      String name_of_interface = _interface.getName ();
      String type_name;
      Type type = null;
      if (type2java (idl_type) == null) {
	 // idl_type isn't simple
	 if (isAbsoluteScopedName (idl_type)) {
	    type = Type.createClass (org.openide.src.Identifier.create 
				     (scopedName2javaName (idl_type.substring 
							   (2, idl_type.length ()), true)));	    
	 }
	 else {
	    if (isScopedName (idl_type)) {
	       idl_type = scopedName2javaName (idl_type, false);
	    }
	    if (DEBUG)
               System.out.println ("type is constructed or not simple");
	    if (isTypeDefinedIn (_interface, idl_type)) {
	       if (DEBUG)
		  System.out.println ("type is defined in interface");
	       type_name = _package + "." + _interface.getName () + "Package" + "." + idl_type;
	       if ((mode == Parameter.INOUT) || (mode == Parameter.OUT))
		  type_name = type_name + "Holder";
	    }
	    else {
	       if (DEBUG)
		  System.out.println ("type isn't defined in interface");
	       type_name = _package + "." + idl_type;
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

   public String exception2java (String ex, String _package, InterfaceElement _interface) {
      String name_of_interface = _interface.getName ();
      String exc_name = "";
      String exc_type = null;
      if (isAbsoluteScopedName (ex)) {
	 exc_type = scopedName2javaName (ex.substring (2, ex.length ()), true);    
      }
      else {
	 if (isScopedName (ex)) {
	    exc_name = _package + "." + scopedName2javaName (ex, false);
	 }
	 else {
	    if (isExceptionDefinedIn (_interface, ex)) {
	       if (DEBUG)
		  System.out.println ("exception is defined in interface");
	       exc_name = _package + "." + _interface.getName () + "Package" + "." + ex;
	    }
	    else {
	       if (DEBUG)
		  System.out.println ("type isn't defined in interface");
	       exc_name = _package + "." + ex;
	    }
	 }
      }
      return exc_name;
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
	 System.out.println ("opeartion: " + operation.getName ());
	 System.out.println ("opeartion rettype:" + operation.getReturnType () + ":"); 
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

	 // set body to return null;
	 oper.setBody ("\n  return null;\n");

	 clazz.addMethod (oper);
      } catch (SourceException e) {
            e.printStackTrace ();
      }

   }

   public void interface2java (InterfaceElement element) {
      if (DEBUG)
	 System.out.println ("interface2java: " + element.getName ());
      if (DEBUG)
         System.out.println ("name: " + ido.getPrimaryFile ().getName ());

      String impl_name = IMPL_PREFIX + element.getName () + IMPL_POSTFIX;
      String super_name = EXT_CLASS_PREFIX + element.getName () + EXT_CLASS_POSTFIX;
      String _package = ido.getPrimaryFile ().getParent ().getPackageName ('.'); 

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
	 clazz.setSuperclass (org.openide.src.Identifier.create (super_name));
	 //java_src.addClass (clazz);
	 //java_src.addClasses (new ClassElement[] { clazz });

	 for (int i=0; i<members.size (); i++) {
	    if (members.elementAt (i) instanceof AttributeElement) {
	       attribute2java ((AttributeElement)members.elementAt (i), clazz);
	    }
	    if (members.elementAt (i) instanceof OperationElement) {
	       operation2java ((OperationElement)members.elementAt (i), clazz);
	    }
	 }

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
      if (DEBUG)
	 System.out.println ("generate :-))");
      Vector members = src.getMembers ();
      for (int i=0; i<members.size (); i++) {
	 if (members.elementAt (i) instanceof InterfaceElement)
	    interface2java ((InterfaceElement)members.elementAt (i));
      }
   }

   
   public static void main (String[] args) {
   }

}


/*
 * $Log
 * $
 */
