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
import java.util.ArrayList;
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
    //private boolean _M_open = false;

    //CORBASupportSettings css;
    ORBSettings _M_settings;

    private boolean _M_listen = false;

    public ImplGenerator (IDLDataObject _do) {

        _M_ido = _do;

        CORBASupportSettings __css = (CORBASupportSettings) CORBASupportSettings.findObject
	    (CORBASupportSettings.class, true);

	if (_M_ido.getOrbForCompilation () != null) {
	    // user setuped ORB for compilation on this DO
	    _M_settings = __css.getSettingByName (_M_ido.getOrbForCompilation ());
	} else {
	    _M_settings = __css.getActiveSetting ();
	}

	IMPLBASE_IMPL_PREFIX = _M_settings.getImplBasePrefix ();
	IMPLBASE_IMPL_POSTFIX = _M_settings.getImplBasePostfix ();
	EXT_CLASS_PREFIX = _M_settings.getExtClassPrefix ();
	EXT_CLASS_POSTFIX = _M_settings.getExtClassPostfix ();
	TIE_IMPL_PREFIX = _M_settings.getTiePrefix ();
	TIE_IMPL_POSTFIX = _M_settings.getTiePostfix ();
	IMPL_INT_PREFIX = _M_settings.getImplIntPrefix ();
	IMPL_INT_POSTFIX = _M_settings.getImplIntPostfix ();
	TIE = _M_settings.isTie ();
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


    public void setSources (IDLElement __src) {
        _M_src = __src;
    }


    public void setOpen (boolean __value) {
	_M_open = __value;
    }


    public boolean getOpen () {
	return _M_open;
    }


    public Type type2java (IDLType __idl_type) {
	int __type = __idl_type.getType ();

	switch (__type) {

	case IDLType.VOID: 
	    return Type.VOID;

	case IDLType.BOOLEAN: 
	    return Type.BOOLEAN;

	case IDLType.CHAR:
	case IDLType.WCHAR:
	    return Type.CHAR;

	case IDLType.OCTET:
	    return Type.BYTE;

	case IDLType.STRING:
	case IDLType.WSTRING:
	    return Type.createClass (org.openide.src.Identifier.create
				     ("java.lang.String", "String"));

	case IDLType.SHORT:
	case IDLType.USHORT:
	    return Type.SHORT;
	    
	case IDLType.LONG:
	case IDLType.ULONG:
	    return Type.INT;
	    
	case IDLType.LONGLONG:
	case IDLType.ULONGLONG:
	    return Type.LONG;

	case IDLType.FLOAT:
	    return Type.FLOAT;

	case IDLType.DOUBLE:
	    return Type.DOUBLE;
	    
	    // not primitive types
	case IDLType.OBJECT:
	    return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.Object"));
	    
	case IDLType.ANY:
	    return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.Any"));

	case IDLType.FIXED:
	    return Type.createClass (org.openide.src.Identifier.create
				     ("java.math.BigDecimal"));

	default:
	    return null;
	}
    }


    public Type JavaTypeToHolder (Type __type) throws UnknownTypeException {
        if (DEBUG)
            System.out.println ("ImplGenerator::JavaTypeToHolder (" 
				+ __type + ");"); // NOI18N

        if (__type.equals (Type.BOOLEAN))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.BooleanHolder")); // NOI18N
        if (__type.equals (Type.CHAR))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.CharHolder")); // NOI18N
        if (__type.equals (Type.BYTE))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.ByteHolder")); // NOI18N
        if (__type.equals (Type.createClass (org.openide.src.Identifier.create
					     ("java.lang.String", "String")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.StringHolder")); // NOI18N
        if (__type.equals (Type.SHORT))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.ShortHolder")); // NOI18N
        if (__type.equals (Type.INT))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.IntHolder")); // NOI18N
        if (__type.equals (Type.LONG))
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.LongHolder")); // NOI18N
        if (__type.equals (Type.FLOAT))
            return Type.createClass (org.openide.src.Identifier.create
				     ("org.omg.CORBA.FloatHolder")); // NOI18N
        if (__type.equals (Type.DOUBLE))
            return Type.createClass (org.openide.src.Identifier.create
				     ("org.omg.CORBA.DoubleHolder")); // NOI18N

        if (__type.equals (Type.createClass (org.openide.src.Identifier.create
					     ("org.omg.CORBA.Object")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.ObjectHolder")); // NOI18N

        if (__type.equals (Type.createClass (org.openide.src.Identifier.create
					     ("org.omg.CORBA.Any")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.AnyHolder")); // NOI18N

        if (__type.equals (Type.createClass (org.openide.src.Identifier.create
					     ("java.math.BigDecimal")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.FixedHolder")); // NOI18N

        if (DEBUG)
            System.out.println ("error unknown type!!!"); // NOI18N
        throw new UnknownTypeException (__type.getSourceString ());
    }


    private boolean is_absolute_scope_type (IDLType __type) {
        if (__type.getType () == IDLType.SEQUENCE)
            return this.isAbsoluteScopeName (__type.ofType ().getName ());
        else
            return this.isAbsoluteScopeName (__type.getName ());
    }


    public static boolean isAbsoluteScopeName (String __name) {
        if (DEBUG)
            System.out.println ("isAbsoluteScopeName (" + __name + ");"); // NOI18N
        if (__name.length () >= 3)
            if (__name.substring (0, 2).equals ("::")) { // NOI18N
                if (DEBUG)
                    System.out.println ("YES"); // NOI18N
                return true;
            }
        if (DEBUG)
            System.out.println ("NO"); // NOI18N
        return false;
    }


    private boolean is_scope_type (IDLType __type) {
	if (DEBUG)
	    System.out.println ("isScopeType (" + __type.getName () + ");");
        if (__type.getType () == IDLType.SEQUENCE)
            return this.isScopeName (__type.ofType ().getName ());
        else
            return this.isScopeName (__type.getName ());
    }


    public static boolean isScopeName (String __name) {
        if (DEBUG)
            System.out.println ("isScopeName (" + __name + ");"); // NOI18N
        if (__name.indexOf ("::") > -1) { // NOI18N
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


    public static String getSimpleName (String __name) {
        String __retval = __name.substring
	    (__name.lastIndexOf ("::") + 2, __name.length ()); // NOI18N
        if (DEBUG)
            System.out.println ("getSimpleName (" + __name + "); => " + __retval); // NOI18N
        return __retval;
    }


    public String modules2package (InterfaceElement __interface) {
        // checking modules
        String __modules = ""; // NOI18N
        if (__interface.getParent () instanceof ModuleElement) {
            // has min one Module as Parent
            IDLElement __tmp = __interface;
            ArrayList __mods = new ArrayList ();
            while (__tmp.getParent () instanceof ModuleElement) {
                __mods.add (__tmp.getParent ().getName ());
                __tmp = __tmp.getParent ();
            }
            // transform modules names from vector to string in package format
            for (int __i=__mods.size () - 1; __i>=0; __i--) {
                __modules = __modules + (String)__mods.get (__i) + "."; // NOI18N
            }
        }

        return __modules;
    }


    private IDLType create_child_from_type (IDLType type) {
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
            return this.findTopLevelModuleForName (type.ofType ().getName (), from);
        else
            return this.findTopLevelModuleForName (type.getName (), from);

    }


    public String ctype2package (IDLElement __type) {
        // checking modules
        if (DEBUG)
            System.out.println ("ImplGenerator::ctype2package (" + __type + ");"); // NOI18N
        String __modules = ""; // NOI18N
        if (__type != null) {
            ArrayList __mods = new ArrayList ();
            __mods.add (__type.getName ());
            while (__type.getParent () != null) {
                __type = __type.getParent ();
                if (__type instanceof ModuleElement)
                    __mods.add (__type.getName ());
                if (__type instanceof InterfaceElement)
                    __mods.add (__type.getName () + "Package"); // NOI18N

            }
            // transform modules names from vector to string in package format
            for (int __i = __mods.size () - 1; __i>=0; __i--) {
                if (DEBUG)
                    System.out.println ("transfrom: " + (String)__mods.get (__i)); // NOI18N
                __modules = __modules + (String)__mods.get (__i) + "."; // NOI18N
            }
            // without last dot
            __modules = __modules.substring (0, __modules.length () - 1);
            if (DEBUG)
                System.out.println ("result: >" + __modules + "<"); // NOI18N
        }
        return __modules;
    }

    
    private String ctype2package (String __package, IDLElement __element) {
	return __package + "." + this.ctype2package (__element); // NOI18N
    }


    public static String nameFromScopeName (String name) {
        if (name != null) {
            if (name.lastIndexOf ("::") != -1) { // NOI18N
                return name.substring (name.lastIndexOf ("::") + 2, name.length ()); // NOI18N
            }
        }
        return name;
    }


    public static IDLElement findElementInElement (String __name, IDLElement __element) {
        __name = ImplGenerator.nameFromScopeName (__name);
        if (__element == null)
            return null;

        if (DEBUG)
            System.out.println ("ImplGenerator::findElementInElement (" + __name + ", " // NOI18N
                                + __element.getName () + ":" + __element + ");"); // NOI18N
        Vector __mm = __element.getMembers ();
        IDLElement __tmp_element = null;

        for (int __i=0; __i<__mm.size (); __i++) {
            if (DEBUG)
                System.out.println ("i = " + __i); // NOI18N
	    __tmp_element = __element.getMember (__i);
            if (__tmp_element instanceof TypeElement) {
                if (DEBUG)
                    System.out.println ("type element"); // NOI18N
                if (ImplGenerator.is_constructed_type (__tmp_element.getMember (0))) {
                    if (DEBUG)
                        System.out.println ("constructed type"); // NOI18N
                    if (__tmp_element.getMember (0).getName ().equals (__name)) {
                        __tmp_element = __tmp_element.getMember (0);
                        if (DEBUG)
                            System.out.println ("element: " + __tmp_element+ " : " + __tmp_element.getName ()); // NOI18N
                        return __tmp_element;
                    }
		    // we need to check all children
		    // e.g.
		    // typedef struct A {
		    //   long value;
		    // } Achild1, Achild2;
		    //
		    if (DEBUG)
			System.out.println ("children checking...");
		    int __last = __tmp_element.getMembers ().size () - 1;
		    for (int __j = 1; __j <= __last; __j++) { 
			if (DEBUG) {
			    System.out.println ("declarator element: " + // NOI18N
						(__tmp_element.getMember (__j).getName ()));
			    System.out.println ("name: " + __name); // NOI18N
			}
			if (__tmp_element.getMember (__j).getName ().equals (__name)) {
			    __tmp_element = __tmp_element.getMember (__j);
			    if (DEBUG)
				System.out.println ("element: " + __tmp_element // NOI18N
						    + " : " // NOI18N
						    + __tmp_element.getName ()); // NOI18N
			    return __tmp_element;
			}
		    }
		    // end of children checks
                }
                if (__tmp_element.getMember (0) instanceof DeclaratorElement) {
		    DeclaratorElement __tmp_del 
			= (DeclaratorElement)__tmp_element.getMember (0);
                    if (DEBUG) {
			int __dim = __tmp_del.getType ().ofDimension ().size ();
			System.out.println 
			    ("declarator element: " + // NOI18N
			     __tmp_del + ", dim:" + __dim); // NOI18N
		    }
		    
                    if (__tmp_del.getName ().equals (__name)) {
                        //__tmp_element = __element.getMember (__i).getMember (0);
                        //if (DEBUG)
                        //    System.out.println ("element: " + __tmp_element+ " : " + __tmp_element.getName ()); // NOI18N
                        //return __tmp_element;
			return __tmp_del;
                    }
                }
		if ((__tmp_element.getMembers ().size () > 1)
		    && (__tmp_element.getMember (__tmp_element.getMembers ().size () - 1)
			instanceof DeclaratorElement)) {
		    int last = __tmp_element.getMembers ().size () - 1;
		    if (DEBUG) {
			System.out.println ("last declarator element: " + // NOI18N
					    __tmp_element.getMember (last).getName ());
			System.out.println ("name: " + __name); // NOI18N
		    }
		    if (__tmp_element.getMember (last).getName ().equals (__name)) {
			__tmp_element = __tmp_element.getMember (last);
			if (DEBUG)
			    System.out.println ("element: " + __tmp_element+ " : " // NOI18N
						+ __tmp_element.getName ());
			return __tmp_element;
		    }
		}
            }
            if (__tmp_element instanceof ExceptionElement) {
                if (DEBUG)
                    System.out.println ("exception"); // NOI18N
                if (__tmp_element.getName ().equals (__name)) {
                    if (DEBUG)
                        System.out.println ("element: " + __tmp_element + " : " // NOI18N
					    + __tmp_element.getName ());
                    return __tmp_element;

                }
            }
            if (ImplGenerator.is_interface (__tmp_element)) {
                if (DEBUG)
                    System.out.println ("interface element"); // NOI18N
                if (__tmp_element.getName ().equals (__name)) {
                    if (DEBUG)
                        System.out.println ("element: " + __tmp_element + " : " // NOI18N
					    + __tmp_element.getName ());
                    return __tmp_element;

                }
            }
            if (ImplGenerator.is_value (__tmp_element)) {
                if (DEBUG)
                    System.out.println ("value element"); // NOI18N
                if (__tmp_element.getName ().equals (__name)) {
                    if (DEBUG)
                        System.out.println ("element: " + __tmp_element + " : " // NOI18N
					    + __tmp_element.getName ());
                    return __tmp_element;

		}
	    }
	    if (ImplGenerator.is_valuebox (__tmp_element)) {
                if (DEBUG)
		    System.out.println ("value box element"); // NOI18N
                if (__tmp_element.getName ().equals (__name)) {
                    if (DEBUG)
			System.out.println ("element: " + __tmp_element + " : " // NOI18N
					    + __tmp_element.getName ());
                    return __tmp_element;
		}
		if (__tmp_element.getMembers ().size () > 1
		    && ImplGenerator.is_constructed_type (__tmp_element.getMember (1))) {
		    if (DEBUG) {
			System.out.println ("constructed type inside of value box");
			System.out.println ("lookup for " + __name);
		    }
		    IDLElement __constr_type = __tmp_element.getMember (1);
		    if (DEBUG)
			System.out.println ("constr: " + __constr_type.getName ());
		    if (__constr_type.getName ().equals (__name)) {
			if (DEBUG)
			    System.out.println ("element: " + __constr_type // NOI18N
						+ " : " // NOI18N
						+ __constr_type.getName ());		    
			return __constr_type;
		    }
		}
            }
        }
        return null;
    }

    
    public static boolean is_constructed_type (IDLElement __element) {
	if (__element instanceof TypeElement) {
	    int __type = ((TypeElement)__element).getType ().getType ();
	    if (__type == IDLType.STRUCT
		|| __type == IDLType.UNION
		|| __type == IDLType.ENUM)
		return true;
	}
	return false;
    }


    private static boolean is_interface (IDLElement __element) {
	if (__element instanceof InterfaceElement)
	    return true;
	return false;
    }


    private static boolean is_value (IDLElement __element) {
	if (__element instanceof ValueElement 
	    || __element instanceof ValueAbsElement)
	    return true;
	return false;
    }


    private static boolean is_valuebox (IDLElement __element) {
	if (__element instanceof ValueBoxElement)
	    return true;
	return false;
    }


    private static boolean is_declarator (IDLElement __element) {
	if (__element instanceof DeclaratorElement)
	    return true;
	return false;
    }

    
    private static boolean is_sequence (IDLElement __element) {
	IDLType __idl_type = ((TypeElement)__element).getType ();
	if (__idl_type.getType () == IDLType.SEQUENCE)
	    return true;
	else
	    return false;
    }

    
    private static boolean is_native (IDLElement __element) {
	IDLType __idl_type = ((TypeElement)__element).getType ();
	if (__idl_type.getType () == IDLType.NATIVE)
	    return true;
	else
	    return false;
    }    
    private Type create_type_from_name (String __name, int __array_dimension) {
	Type __type = Type.createClass (org.openide.src.Identifier.create (__name));
	return this.create_type_from_type (__type, __array_dimension);
    }


    private Type create_type_from_type (Type __type, int __array_dimension) {
	for (int __i=0; __i<__array_dimension; __i++)
	    __type = Type.createArray (__type);
	return __type;
    }


    private Type type2java (IDLType __idl_type, IDLType __orig_type, int __mode, 
			    String __package, IDLElement __start_element, 
			    int __array_counter, IDLElement __orig_type_element) {
	//boolean DEBUG = true;
	Type __java_type = null;
	if (DEBUG) {
	    System.out.println ("__idl_type: " + __idl_type);
	    System.out.println ("__start_element: " + __start_element);
	}
	if ((__java_type = this.type2java (__idl_type)) != null) {
	    if (__mode == Parameter.IN) {
		//System.out.println ("__idl_type: " + __idl_type + " -> __java_type: " 
		//+ __java_type);
		return this.create_type_from_type (__java_type, __array_counter);
	    }
	    else {
		if (__array_counter > 0) {
		    // simple type does not map to org.omg.CORBA.<SimpleType>Holder
		    // but to Holder of its array
		    String __name = this.ctype2package (__package, __orig_type_element) 
			+ "Holder"; // NOI18N
		    return this.create_type_from_name (__name, 0);
		}
		else {
		    try {
			return this.JavaTypeToHolder (__java_type);
		    } catch (UnknownTypeException __e) {
		    }
		}
	    }
	}
	if (this.is_absolute_scope_type (__idl_type) 
	    || this.is_scope_type (__idl_type)) {
	    IDLElement __top_level_module = null;
	    if (this.is_absolute_scope_type (__idl_type))
		__top_level_module = this.findTopLevelModuleForType 
		    (__idl_type, __start_element);
	    if (this.is_scope_type (__idl_type))
		__top_level_module = this.findModuleForScopeType 
		    (__idl_type, __start_element);
            IDLElement __element_for_type = this.findElementInElement 
		(__idl_type.getName (), __top_level_module);
	    if (__element_for_type != null) {
		if (__orig_type_element == null)
		    __orig_type_element = __element_for_type;
		IDLType __new_type = this.create_child_from_type (__idl_type);
		return this.type2java (__new_type, __orig_type, __mode, __package, 
				       __top_level_module, __array_counter, 
				       __orig_type_element);
	    }
	}

	IDLElement __element_for_type = this.findElementInElement 
	    (__idl_type.getName (), __start_element);
	if (__element_for_type != null) {
	    if (DEBUG)
		System.out.println ("found element: " + __element_for_type.getName ());
	    if (__orig_type_element == null)
		__orig_type_element = __element_for_type;
	    if (this.is_declarator (__element_for_type)) {
		if (DEBUG)
		    System.out.println ("found declarator");
		IDLType __new_type = null;
		TypeElement __parent = (TypeElement)__element_for_type.getParent ();
		IDLType __parent_type = __parent.getType ();
		DeclaratorElement __declarator = (DeclaratorElement)__element_for_type;
		if (this.is_sequence (__parent)) {
		    //__new_type = null; // new IDLType (...);
		    __new_type = new IDLType (__parent_type.ofType ().getType (),
					      __parent_type.ofType ().getName (),
					      __parent_type.ofType (),
					      __parent_type.ofDimension ());
		    __array_counter += 1;
		}
		else {
		    if (DEBUG)
			System.out.println ("isn't seq");
		    __new_type = new IDLType (__parent_type.getType (), 
					      __parent_type.getName ());
		    __array_counter += __declarator.getDimension ().size ();
		    //System.out.println ("__new_type: " + __new_type);
		    //System.out.println ("__array_counter: " + __array_counter);
		    if (ImplGenerator.is_native (__declarator)) {
			if (DEBUG)
			    System.out.println ("standard mapping for native types");
			if (__mode == Parameter.IN) { 
			    String __name = this.ctype2package 
				(__package, __declarator);
			    return this.create_type_from_name (__name, __array_counter);
			}
			else if (__array_counter == 0) {
			    return this.create_type_from_name ("org.omg.PortableServer.ServantLocatorPackage.CookieHolder", 0); // NOI18N
			} 
			else {
			    String __name = this.ctype2package 
				(__package, __orig_type_element) + "Holder"; // NOI18N
			    return this.create_type_from_name (__name, 0);
			}
		    }
		}
		return this.type2java (__new_type, __orig_type, __mode, __package,
				       __start_element, __array_counter, 
				       __orig_type_element);
	    }
	    if (this.is_constructed_type (__element_for_type)
		|| this.is_interface (__element_for_type)
		|| this.is_value (__element_for_type)
		|| this.is_valuebox (__element_for_type)) {
		if (DEBUG)
		    System.out.println ("constructed type|interface|value[box]: " 
					+ __idl_type.getName ());
		if (__mode == Parameter.IN) {
		    if (this.is_valuebox (__element_for_type)) {
			// special handling for value boxes for in mode
			ValueBoxElement __box = (ValueBoxElement)__element_for_type;
			IDLType __type_of_box = __box.getType ();
			if (DEBUG)
			    System.out.println ("__type_of_box: " + __type_of_box);
			if (!(this.type2java (__type_of_box) != null
			      && this.type2java (__type_of_box).isPrimitive ())) {
			    if (DEBUG)
				System.out.println ("special case in value box mapping");
			    if (__box.getMembers ().size () > 1) {
				if (ImplGenerator.is_constructed_type 
				    (__box.getMember (1))) {
				    if (DEBUG)
					System.out.println ("constr in value box");
				    TypeElement __constr_element 
					= (TypeElement)__box.getMember (1);
				    String __name = this.ctype2package 
					(__package, __constr_element);
				    return this.create_type_from_name 
					(__name, __array_counter);
				}
			    }
			    if (__type_of_box.getType () == IDLType.SEQUENCE) {
				if (DEBUG)
				    System.out.println ("seq");
				__type_of_box = __type_of_box.ofType ();
				__array_counter++;
			    }
			    return this.type2java (__type_of_box, __orig_type, __mode, 
						   __package, __start_element, 
						   __array_counter, __orig_type_element);
			}
			// standard mapping for primitive valuebox types as for normal
			// value types
		    }
		    if (DEBUG)
			System.out.println ("standard mapping for primitive valuebox");
		    String __name = this.ctype2package (__package, __element_for_type);
		    return this.create_type_from_name (__name, __array_counter);
		}
		else {
		    // inout || out parameter
		    String __name = "";
		    if (__array_counter > 0)
			__name = this.ctype2package (__package, __orig_type_element) 
			    + "Holder"; // NOI18N
		    else
			__name = this.ctype2package (__package, __element_for_type) 
			    + "Holder"; // NOI18N
		    return this.create_type_from_name (__name, 0);
		}
	    }
	}
	if (__start_element.getParent () != null) {
	    return this.type2java (__idl_type, __orig_type, __mode, __package, 
				   __start_element.getParent (), __array_counter,
				   __orig_type_element);
	}

	// found element is null
	return Type.createClass (org.openide.src.Identifier.create ("unknown_type"));
    }


    public Type type2java (IDLType __idl_type, int __mode, String __package,
                           InterfaceElement __interface) {
	return this.type2java (__idl_type, __idl_type, __mode, __package, __interface, 0, 
			       null);
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


    public void attribute2java (AttributeElement __attr, ClassElement __clazz) {
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        InterfaceElement __interface = (InterfaceElement)__attr.getParent ();
        Type __attr_type = this.type2java (__attr.getType (), Parameter.IN, 
					   __package, __interface);
        if (DEBUG)
            System.out.println ("attribute2java"); // NOI18N
        if (DEBUG) {
            System.out.println ("attribute: " + __attr.getName ()); // NOI18N
            System.out.println ("type: " + __attr.getType ()); // NOI18N
            System.out.println ("java: " + __attr_type); // NOI18N
            System.out.println ("package: " + __package); // NOI18N
        }
        try {
            MethodElement __geter = new MethodElement ();
            if (DEBUG)
                System.out.println ("::id9 " + __attr.getName ()); // NOI18N
            __geter.setName (org.openide.src.Identifier.create (__attr.getName ()));
            __geter.setModifiers (Modifier.PUBLIC);
            __geter.setReturn (__attr_type);
            //geter.setBody ("\n  return null;\n"); // NOI18N
            this.setBodyOfMethod (__geter);
            __clazz.addMethod (__geter); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
        }

        if (!__attr.getReadOnly ()) {
            try {
                MethodElement __seter = new MethodElement ();
                if (DEBUG)
                    System.out.println ("::id10 " + __attr.getName ()); // NOI18N
                __seter.setName (org.openide.src.Identifier.create (__attr.getName ()));
                __seter.setModifiers (Modifier.PUBLIC);
                __seter.setReturn (Type.VOID);
                //seter.setBody ("\n"); // NOI18N
		this.setBodyOfMethod (__seter);
                __seter.setParameters (new MethodParameter[] {
		    new MethodParameter ("value", __attr_type, false) }); // NOI18N
                __clazz.addMethod (__seter); // now addMethod throws SourceExcetion
            } catch (SourceException e) {
                //e.printStackTrace ();
            }
        }

    }


    public void operation2java (OperationElement __operation, ClassElement __clazz) {
        if (DEBUG)
            System.out.println ("operation2java"); // NOI18N
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        InterfaceElement __interface = (InterfaceElement)__operation.getParent ();
        Type __rettype = this.type2java (__operation.getReturnType (), Parameter.IN, 
					 __package, __interface);
        if (DEBUG) {
            System.out.println ("operation: " + __operation.getName ()); // NOI18N
            System.out.println ("operation rettype:" + __operation.getReturnType () + ":"); // NOI18N
            System.out.println ("return type: " + __rettype); // NOI18N
        }
        try {
            MethodElement __oper = new MethodElement ();
            if (DEBUG)
                System.out.println ("::id11 " + __operation.getName ()); // NOI18N
            __oper.setName (org.openide.src.Identifier.create (__operation.getName ()));
            __oper.setModifiers (Modifier.PUBLIC);
            __oper.setReturn (__rettype);
            // parameters and context!!!
            MethodParameter[] __params;
            if (__operation.getContexts ().size () != 0)
                __params = new MethodParameter[__operation.getParameters ().size () + 1];
            else
                __params = new MethodParameter[__operation.getParameters ().size ()];

            for (int __i=0; __i<__operation.getParameters ().size (); __i++) {
                ParameterElement __p 
		    = (ParameterElement)__operation.getParameters ().elementAt (__i);
                Type __ptype = this.type2java (__p.getType (), __p.getAttribute (), 
					       __package, __interface);
                __params[__i] = new MethodParameter (__p.getName (), __ptype, false);
            }
            if (__operation.getContexts ().size () != 0)
                __params[__params.length - 1] = new MethodParameter
		    ("ctx", Type.createClass (org.openide.src.Identifier.create // NOI18N
					      ("org.omg.CORBA.Context")), false); // NOI18N
            __oper.setParameters (__params);

            // exceptions
            org.openide.src.Identifier[] __excs
		= new org.openide.src.Identifier[__operation.getExceptions ().size ()];
            for (int __i=0; __i<__operation.getExceptions ().size (); __i++) {
                __excs[__i] = org.openide.src.Identifier.create
		    (this.exception2java 
		     ((String)__operation.getExceptions ().elementAt (__i),
		      __package, __interface));
            }
            __oper.setExceptions (__excs);

            this.setBodyOfMethod (__oper);
            __clazz.addMethod (__oper); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
        }

    }


    protected List get_interfaces (Vector __elements) {
        ArrayList __retval = new ArrayList ();

        for (int __i=0; __i<__elements.size (); __i++) {
            if (__elements.elementAt (__i) instanceof ModuleElement)
                __retval.addAll 
		    (this.get_interfaces 
		     (((ModuleElement)__elements.elementAt (__i)).getMembers ()));
            if (__elements.elementAt (__i) instanceof InterfaceElement)
                __retval.add ((InterfaceElement)__elements.elementAt (__i));
        }

        return __retval;
    }


    public void interface2java (ClassElement __clazz, InterfaceElement __element)
	throws SymbolNotFoundException {

        if (DEBUG)
            System.out.println ("ImplGenerator::interface2java (__clazz, " 
				+ __element.getName () + ");"); // NOI18N
        // parents...

        Vector __parents = __element.getParents ();
        for (int __i=0; __i<__parents.size (); __i++) {
            String __name_of_parent = (String)__parents.elementAt (__i);
            IDLElement __parent
		= findElementByName (__name_of_parent, __element);
            if (__parent == null) {
                throw new SymbolNotFoundException (__name_of_parent);
            }
            this.interface2java (__clazz, (InterfaceElement)__parent);
        }

        Vector __members = __element.getMembers ();

        for (int __i=0; __i<__members.size (); __i++) {
            if (__members.elementAt (__i) instanceof AttributeElement) {
                this.attribute2java ((AttributeElement)__members.elementAt (__i), __clazz);
            }
            if (__members.elementAt (__i) instanceof OperationElement) {
                this.operation2java ((OperationElement)__members.elementAt (__i), __clazz);
            }
        }

    }

    public void interface2java (InterfaceElement __element)
	throws SymbolNotFoundException, RecursiveInheritanceException, java.io.IOException {
        if (DEBUG) {
            System.out.println ("interface2java: " + __element.getName ()); // NOI18N
            System.out.println ("name: " + _M_ido.getPrimaryFile ().getName ()); // NOI18N
	}
	if (__element.isAbstract ()) {
	    if (DEBUG)
		System.out.println ("abstract interface " + __element.getName ());
	    return;
	}

	RecursiveInheritanceChecker.check (__element);

        String __impl_name = ""; // NOI18N
        String __super_name = ""; // NOI18N
        String __modules = this.modules2package (__element);
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
	
        if (DEBUG) {
            System.out.println ("modules:>" + __modules + "<"); // NOI18N
            System.out.println ("package:>" + __package + "<"); // NOI18N
        }


        if (!TIE) {
            __impl_name = IMPLBASE_IMPL_PREFIX + __element.getName () + IMPLBASE_IMPL_POSTFIX;
            if (where_generate == IN_IDL_PACKAGE) {
                if (__package.length() >0)
                    __super_name = __package + "."; // NOI18N
                __super_name += __modules + EXT_CLASS_PREFIX + __element.getName ()
		    + EXT_CLASS_POSTFIX;
            } else
                __super_name = EXT_CLASS_PREFIX + __element.getName () + EXT_CLASS_POSTFIX;
        }
        else {
            __impl_name = TIE_IMPL_PREFIX + __element.getName () + TIE_IMPL_POSTFIX;
            if (where_generate == IN_IDL_PACKAGE) {
                if (__package.length() >0)
                    __super_name = __package + "."; // NOI18N
                __super_name += __modules + IMPL_INT_PREFIX + __element.getName ()
		    + IMPL_INT_POSTFIX;
            } else
                __super_name = IMPL_INT_PREFIX + __element.getName () + IMPL_INT_POSTFIX;
        }

        // print to status line
        String __status_package = ""; // NOI18N
        StringTokenizer __st = new StringTokenizer (__package, "."); // NOI18N
        while (__st.hasMoreTokens ()) {
            __status_package += __st.nextToken () + "/"; // NOI18N
        }

	//String __status_package = status_package;
	//String __impl_name = impl_name;
	//System.out.println ("Generate " + __status_package + __impl_name + " ..."); // NOI18N
	java.lang.Object[] __arr = new Object[] {__status_package + __impl_name};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

        Vector __members = __element.getMembers ();

        try {
            final ClassElement clazz = new ClassElement ();
            clazz.setName (org.openide.src.Identifier.create (__impl_name));
            if (!TIE)
                clazz.setSuperclass (org.openide.src.Identifier.create (__super_name));
            else
                clazz.setInterfaces (new org.openide.src.Identifier[]
		    {org.openide.src.Identifier.create (__super_name)} );

            interface2java (clazz, __element);

            final FileObject folder = _M_ido.getPrimaryFile ().getParent ();
            final FileObject impl;

            if ((impl = folder.getFileObject (__impl_name, "java")) != null) { // NOI18N
                if (DEBUG)
                    System.out.println ("file exists"); // NOI18N
                String full_name = ""; // NOI18N
                if (__package.length() > 0) 
                    full_name = full_name + __package + "."; // NOI18N
                full_name = full_name + __impl_name;
                if (DEBUG)
		    System.out.println ("full name: " + full_name); // NOI18N
                ClassElement dest = ClassElement.forName (full_name);
                if (DEBUG) {
                    System.out.println ("orig class: " + dest.toString ()); // NOI18N
                    System.out.println ("new class: " + clazz.toString ()); // NOI18N
                }

		if (_M_settings.getSynchro () != ORBSettingsBundle.SYNCHRO_DISABLED) {
		    List changes = new LinkedList ();
		    JavaConnections.compareMethods 
			(dest, clazz, changes, CORBASupport.ADD_METHOD, CORBASupport.UPDATE_METHOD);
		    if (changes.size () > 0)
			JavaConnections.showChangesDialog 
			    (changes, (byte)JavaConnections.TYPE_ALL);
                }
		else {
		    this.showMessage = false;
		}
		
            }
            else {
                if (DEBUG)
                    System.out.println ("file don't exists"); // NOI18N
		final String __final_package = __package;
		final String __final_impl_name = __impl_name;
		folder.getFileSystem ().runAtomicAction 
		    (new org.openide.filesystems.FileSystem.AtomicAction () {
			    public void run () throws java.io.IOException {
				final FileObject __final_impl = folder.createData (__final_impl_name, "java"); // NOI18N
				
				FileLock lock = __final_impl.lock ();
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
		     
	} catch (org.openide.src.SourceException e) {
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

	if (DEBUG)
	    System.out.println ("status = " + _M_ido.getStatus ()); // NOI18N
	if (_M_ido.getStatus () == IDLDataObject.STATUS_ERROR) {
	    java.lang.Object[] __arr = new Object[] {_M_ido.getPrimaryFile ().getName ()};
            TopManager.getDefault ().setStatusText 
		(MessageFormat.format (CORBASupport.PARSE_ERROR, __arr));
	    if (_M_listen) {
		_M_ido.removePropertyChangeListener (this);
		_M_listen = false;
	    }
            return;
        }
	if (_M_ido.getStatus () == IDLDataObject.STATUS_NOT_PARSED) {
            TopManager.getDefault ().setStatusText 
		(CORBASupport.WAITING_FOR_PARSER);
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
	    if (!_M_listen) {
		_M_ido.addPropertyChangeListener (this);
		_M_listen = true;
	    }
            return;
        }

	// status of _M_ido is equal to IDLDataObject.STATUS_OK
	_M_src = _M_ido.getSources ();
        //Vector members = _M_src.getMembers ();     // update for working with modules :-))
        List members = this.get_interfaces (_M_src.getMembers ());
        for (int i=0; i<members.size (); i++) {
            if (members.get (i) instanceof InterfaceElement)
                try {
                    interface2java ((InterfaceElement)members.get (i));
                } catch (SymbolNotFoundException __ex) {
		    java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		    TopManager.getDefault ().notify 
			(new NotifyDescriptor.Exception 
			    (__ex, MessageFormat.format (CORBASupport.CANT_FIND_SYMBOL, 
							 __arr)));
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
	if (this.showMessage && (!this._M_exception_occured)) { 
	    // Bug Fix, when sync is disabled, don't show the message
	    java.lang.Object[] __arr = new Object[] {_M_ido.getPrimaryFile ().getName ()};
	    TopManager.getDefault ().setStatusText
		(MessageFormat.format (CORBASupport.SUCESS_GENERATED, __arr));
	}
	if (!this.showMessage) {
	    TopManager.getDefault ().setStatusText 
		(ORBSettingsBundle.SYNCHRO_DISABLED);
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

	if (_M_settings.getGeneration ().equals (ORBSettingsBundle.GEN_NOTHING)) {
	    //System.out.println ("CORBASupport.GEN_NOTHING"); // NOI18N
	    method.setBody ("\n"); // NOI18N
	    return;
	}
	if (_M_settings.getGeneration ().equals (ORBSettingsBundle.GEN_EXCEPTION)) {
	    //System.out.println ("CORBASupport.GEN_EXCEPTION"); // NOI18N
	    method.setBody ("\n  throw new UnsupportedOperationException ();\n"); // NOI18N
	    return;
	}
	if (_M_settings.getGeneration ().equals (ORBSettingsBundle.GEN_RETURN_NULL)) {
            //System.out.println ("CORBASupport.GEN_RETURN_NULL"); // NOI18N
            method.setBody ("\n  return null;\n"); // NOI18N
            return;
	}

    }

    
    public void propertyChange (PropertyChangeEvent __event) {
	if (DEBUG)
	    System.out.println ("property change: " + __event.getPropertyName ());
	if (__event.getPropertyName ().equals ("_M_status")) {
	    RequestProcessor __processor = _M_ido.getGeneratorProcessor ();
	    __processor.post (new Runnable () {
		    public void run () {
			ImplGenerator.this.generate ();
		    }
		});
	}
    }

}
