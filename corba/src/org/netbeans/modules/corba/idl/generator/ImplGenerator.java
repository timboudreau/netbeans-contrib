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
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Stack;
import java.util.Enumeration;
//import java.util.Comparator;

import java.text.MessageFormat;

import java.io.PrintStream;
import java.io.File;

import java.lang.reflect.Modifier;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileStateInvalidException;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.cookies.OpenCookie;
import org.openide.cookies.SourceCookie;

import org.openide.src.Type;
import org.openide.src.SourceElement;
import org.openide.src.ClassElement;
import org.openide.src.MethodElement;
import org.openide.src.MethodParameter;
import org.openide.src.FieldElement;
import org.openide.src.MemberElement;
import org.openide.src.ConstructorElement;
import org.openide.src.SourceException;
import org.openide.src.Element;

import org.openide.text.PositionRef;
import org.openide.text.PositionBounds;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;

import org.netbeans.modules.java.JavaConnections;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.java.JavaEditor;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.IDLType;
import org.netbeans.modules.corba.idl.src.ModuleElement;
import org.netbeans.modules.corba.idl.src.InterfaceElement;
import org.netbeans.modules.corba.idl.src.OperationElement;
import org.netbeans.modules.corba.idl.src.AttributeElement;
import org.netbeans.modules.corba.idl.src.TypeElement;
import org.netbeans.modules.corba.idl.src.StructTypeElement;
import org.netbeans.modules.corba.idl.src.UnionTypeElement;
import org.netbeans.modules.corba.idl.src.EnumTypeElement;
import org.netbeans.modules.corba.idl.src.DeclaratorElement;
import org.netbeans.modules.corba.idl.src.ExceptionElement;
import org.netbeans.modules.corba.idl.src.ParameterElement;
import org.netbeans.modules.corba.idl.src.ValueElement;
import org.netbeans.modules.corba.idl.src.ValueAbsElement;
import org.netbeans.modules.corba.idl.src.ValueBoxElement;
import org.netbeans.modules.corba.idl.src.InitDclElement;
import org.netbeans.modules.corba.idl.src.InitParamDeclElement;
import org.netbeans.modules.corba.idl.src.InterfaceForwardElement;
import org.netbeans.modules.corba.idl.src.ValueForwardElement;
import org.netbeans.modules.corba.idl.src.ConstElement;
import org.netbeans.modules.corba.idl.src.StateMemberElement;

import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;

import org.netbeans.modules.corba.utils.AssertionException;
import org.netbeans.modules.corba.utils.Assertion;
import org.netbeans.modules.corba.utils.Pair;
import org.netbeans.modules.corba.utils.ObjectFilter;
import org.netbeans.modules.corba.utils.ParentsExecutor;

import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.CORBASupport;

/*
 * @author Karel Gardas
 */

public class ImplGenerator implements PropertyChangeListener {

    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    private static final boolean DEBUG_EXCEPT = false;
    //private static final boolean DEBUG_EXCEPT = true;
    
    private static final int GUARDING_FAILED = -1;
    private static final int GUARDING_RUNNING = 0;
    private static final int GUARDING_OK = 1;

    private boolean showMessage; // Fix of showing message when the sync is disabled
    private boolean _M_exception_occured;
    private static IDLElement _M_src;

    private String IMPLBASE_IMPL_PREFIX;
    private String IMPLBASE_IMPL_POSTFIX;
    private String EXT_CLASS_PREFIX;
    private String EXT_CLASS_POSTFIX;
    private String TIE_IMPL_PREFIX;
    private String TIE_IMPL_POSTFIX;
    private String IMPL_INT_PREFIX;
    private String IMPL_INT_POSTFIX;
    private boolean TIE;

    private String VALUE_IMPL_PREFIX;
    private String VALUE_IMPL_POSTFIX;
    private String VALUE_FACTORY_IMPL_PREFIX;
    private String VALUE_FACTORY_IMPL_POSTFIX;
    
    private int IN_MODULE_PACKAGE = 0;
    private int IN_IDL_PACKAGE = 1;
    private int _M_guarding_status;

    private int where_generate = IN_MODULE_PACKAGE;

    private IDLDataObject _M_ido;

    private List _M_generated_impls;

    private List _M_elements_for_guard_blocks;

    private boolean WAS_TEMPLATE = false;
    // this variable indicate if in calling of hasTemplateParent is template type or not
    // => it must be setuped to

    private boolean _M_open = true;
    //private boolean _M_open = false;

    //private boolean _M_run_testsuite = true;
    private static boolean _M_run_testsuite = false;

    CORBASupportSettings _M_css;
    ORBSettings _M_settings;

    private boolean _M_listen = false;

    private boolean _M_use_guarded_blocks;

    private String _M_delegation;

    private static int CREATE_SECTION = 1;
    private static int REMOVE_SECTION = -1;

    private static String INITIALIZE_INHERITANCE_TREE = "_initialize_inheritance_tree";
    private static String PREFIX_OF_FIELD_NAME = "_M_variable_of_type_";
    private static String POSTFIX_OF_FIELD_NAME = "";
    private static String DELEGATION_COMMENT 
	= "\n// Do not edit! This is a delegation method.\n";
    private static String SET_PARENT_METHOD_PREFIX = "_set_parent_of_type_";
    private static String SET_PARENT_METHOD_POSTFIX = "";
    private static String SET_PARENT_METHOD_COMMENT
	= "\n// Do not edit! This is a method which is necessary for using delegation.\n";


    private static HashMap _S_java_keywords;
    private static HashMap _S_idl_mapping_names;

    private static SymbolTable _S_symbol_table;

    private ClassElement _M_working_class;

    private String _M_file_name;

    static {
	ImplGenerator.initKeywordsMaps ();
	_S_symbol_table = new SymbolTable ();
    }

    public ImplGenerator (IDLDataObject _do) {
	try {
        _M_ido = _do;
	//_M_symbol_table = new SymbolTable ();
	this.initKeywordsMaps ();

        CORBASupportSettings __css = (CORBASupportSettings) CORBASupportSettings.findObject
	    (CORBASupportSettings.class, true);

	_M_css = __css;

	if (_M_ido.getOrbForCompilation () != null) {
	    // user setuped ORB for compilation on this DO
	    _M_settings = __css.getSettingByName (_M_ido.getOrbForCompilation ());
	} else {
	    _M_settings = __css.getActiveSetting ();
	}

	IMPLBASE_IMPL_PREFIX = _M_settings.getImplBaseImplPrefix ();
	IMPLBASE_IMPL_POSTFIX = _M_settings.getImplBaseImplPostfix ();
	EXT_CLASS_PREFIX = _M_settings.getExtClassPrefix ();
	EXT_CLASS_POSTFIX = _M_settings.getExtClassPostfix ();
	TIE_IMPL_PREFIX = _M_settings.getTieImplPrefix ();
	TIE_IMPL_POSTFIX = _M_settings.getTieImplPostfix ();
	IMPL_INT_PREFIX = _M_settings.getImplIntPrefix ();
	IMPL_INT_POSTFIX = _M_settings.getImplIntPostfix ();
	TIE = _M_settings.isTie ();

	VALUE_IMPL_PREFIX = _M_settings.getValueImplPrefix ();
	VALUE_IMPL_POSTFIX = _M_settings.getValueImplPostfix ();
	VALUE_FACTORY_IMPL_PREFIX = _M_settings.getValueFactoryImplPrefix ();
	VALUE_FACTORY_IMPL_POSTFIX = _M_settings.getValueFactoryImplPostfix ();

	_M_use_guarded_blocks = _M_settings.getUseGuardedBlocks ();
	_M_delegation = _M_settings.getDelegation ();
	} catch (Exception __e) {
	    __e.printStackTrace ();
	}
    }


    public ImplGenerator () {
	
	//_M_symbol_table = new SymbolTable ();
	
        IMPLBASE_IMPL_PREFIX = ""; // NOI18N
        IMPLBASE_IMPL_POSTFIX = "Impl"; // NOI18N
        EXT_CLASS_PREFIX = "_"; // NOI18N
        EXT_CLASS_POSTFIX = "ImplBase"; // NOI18N
        TIE_IMPL_PREFIX = ""; // NOI18N
        TIE_IMPL_POSTFIX = "Impl"; // NOI18N
        IMPL_INT_PREFIX = ""; // NOI18N
        IMPL_INT_POSTFIX = "Operations"; // NOI18N
        TIE = false;

	VALUE_IMPL_PREFIX = "";
	VALUE_IMPL_POSTFIX = "Impl";
	VALUE_FACTORY_IMPL_PREFIX = "";
	VALUE_FACTORY_IMPL_POSTFIX = "Impl";
	
    }


    public void setSources (IDLElement __src) {
        _M_src = __src;
    }

    private static IDLElement getSources () {
	return _M_src;
    }

    public void setOpen (boolean __value) {
	_M_open = __value;
    }


    public boolean getOpen () {
	return _M_open;
    }

    
    public static void initKeywordsMaps () {
	_S_java_keywords = new HashMap ();
	_S_idl_mapping_names = new HashMap ();

	java.lang.Object __object = new java.lang.Object ();
	_S_java_keywords.put ("abstract", __object);
	_S_java_keywords.put ("boolean", __object);
	_S_java_keywords.put ("break", __object);
	_S_java_keywords.put ("byte", __object);
	_S_java_keywords.put ("case", __object);
	_S_java_keywords.put ("catch", __object);
	_S_java_keywords.put ("char", __object);
	_S_java_keywords.put ("class", __object);
	_S_java_keywords.put ("const", __object);
	_S_java_keywords.put ("continue", __object);
	_S_java_keywords.put ("default", __object);
	_S_java_keywords.put ("do", __object);
	_S_java_keywords.put ("double", __object);
	_S_java_keywords.put ("else", __object);
	_S_java_keywords.put ("extends", __object);
	_S_java_keywords.put ("final", __object);
	_S_java_keywords.put ("finally", __object);
	_S_java_keywords.put ("float", __object);
	_S_java_keywords.put ("for", __object);
	_S_java_keywords.put ("goto", __object);
	_S_java_keywords.put ("if", __object);
	_S_java_keywords.put ("implements", __object);
	_S_java_keywords.put ("import", __object);
	_S_java_keywords.put ("instanceof", __object);
	_S_java_keywords.put ("int", __object);
	_S_java_keywords.put ("interface", __object);
	_S_java_keywords.put ("long", __object);
	_S_java_keywords.put ("native", __object);
	_S_java_keywords.put ("new", __object);
	_S_java_keywords.put ("package", __object);
	_S_java_keywords.put ("private", __object);
	_S_java_keywords.put ("protected", __object);
	_S_java_keywords.put ("public", __object);
	_S_java_keywords.put ("return", __object);
	_S_java_keywords.put ("short", __object);
	_S_java_keywords.put ("static", __object);
	_S_java_keywords.put ("super", __object);
	_S_java_keywords.put ("switch", __object);
	_S_java_keywords.put ("synchronized", __object);
	_S_java_keywords.put ("this", __object);
	_S_java_keywords.put ("throw", __object);
	_S_java_keywords.put ("throws", __object);
	_S_java_keywords.put ("transient", __object);
	_S_java_keywords.put ("try", __object);
	_S_java_keywords.put ("void", __object);
	_S_java_keywords.put ("volatile", __object);
	_S_java_keywords.put ("while", __object);
	// additonal constants
	_S_java_keywords.put ("true", __object);
	_S_java_keywords.put ("false", __object);
	_S_java_keywords.put ("null", __object);
	// names which colide with java.lang.Object methods' names
	_S_java_keywords.put ("clone", __object);
	_S_java_keywords.put ("equals", __object);
	_S_java_keywords.put ("finalize", __object);
	_S_java_keywords.put ("getClass", __object);
	_S_java_keywords.put ("hashCode", __object);
	_S_java_keywords.put ("notify", __object);
	_S_java_keywords.put ("notifyAll", __object);
	_S_java_keywords.put ("toString", __object);
	_S_java_keywords.put ("wait", __object);

	// idl->java mapping reserved names
	_S_idl_mapping_names.put ("Helper", __object);
	_S_idl_mapping_names.put ("Holder", __object);
	_S_idl_mapping_names.put ("Operations", __object);
	_S_idl_mapping_names.put ("POA", __object);
	_S_idl_mapping_names.put ("POATie", __object);
	_S_idl_mapping_names.put ("Package", __object);
    }


    public static boolean is_java_keyword (String __name) {
	if (_S_java_keywords.get (__name) != null)
	    return true;
	else
	    return false;
    }

    
    public static boolean is_idl_mapping_name (String __name) {
	Iterator __iterator = _S_idl_mapping_names.keySet ().iterator ();
	while (__iterator.hasNext ()) {
	    String __key = (String)__iterator.next ();
	    if (__name.endsWith (__key) && (__name.length () > __key.length ()))
		return true;
	}
	return false;
    }


    public static String idl_name2java_name (String __name, boolean __operation_name) {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("idl_name2java_name () <- " + __name);
	String __result;
	if (__name.startsWith ("_")) { // NOI18N
	    __result = __name.substring (1, __name.length ());
	    if (!ImplGenerator.is_java_keyword (__result)) {
		if (DEBUG)
		    System.out.println ("1: idl_name2java_name () -> " + __result);
		return __result;
	    }
	}
	if (!__operation_name) {
	    if (ImplGenerator.is_idl_mapping_name (__name)
		||ImplGenerator.is_java_keyword (__name)) {
		__result = "_" + __name;
		if (DEBUG)
		    System.out.println ("2: idl_name2java_name () -> " + __result);
		return __result;
	    }
	    else {
		__result = __name;
		if (DEBUG)
		    System.out.println ("3: idl_name2java_name () -> " + __result);
		return __result;
	    }
	}
	else {
	    // operation name
	    if (ImplGenerator.is_java_keyword (__name)) {
		__result = "_" + __name;
	    }
	    else {
		__result = __name;
	    }
	    if (DEBUG)
		System.out.println ("4: idl_name2java_name () -> " + __result);
	    return __result;
	}
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

	case IDLType.VALUEBASE:
	    return Type.createClass (org.openide.src.Identifier.create 
				     ("java.io.Serializable"));

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

        if (__type.equals (Type.createClass (org.openide.src.Identifier.create
					     ("java.io.Serializable")))) // NOI18N
            return Type.createClass (org.openide.src.Identifier.create 
				     ("org.omg.CORBA.ValueBaseHolder")); // NOI18N

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


    private static boolean is_absolute_scope_name (String __name) {
	if (__name.indexOf ("::") == 0)
	    return true;
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


    public static String modules2package (IDLElement __element) {
	String __modules = ""; // NOI18N
	LinkedList __list_of_modules = ImplGenerator.modules2list (__element);
	// transform modules names from vector to string in package format
	if (__list_of_modules.size () > 0) {
	    for (int __i=0; __i<__list_of_modules.size (); __i++) {
		__modules = __modules + (String)__list_of_modules.get (__i) + "."; // NOI18N
	    }
	}
        return __modules;
    }


    public static LinkedList modules2list (IDLElement __element) {
	LinkedList __modules = new LinkedList ();
        if (__element.getParent () instanceof ModuleElement) {
            // has min one module as parent
            IDLElement __tmp = __element;
            while (__tmp.getParent () instanceof ModuleElement) {
                __modules.addFirst 
		    (ImplGenerator.idl_name2java_name (__tmp.getParent ().getName (),
						       false));
                __tmp = __tmp.getParent ();
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

    
    public String ctype2package (IDLElement __type) {
        // checking modules
	//boolean DEBUG=true;
        if (DEBUG)
            System.out.println ("ImplGenerator::ctype2package (" + __type + ");"); // NOI18N
        String __modules = ""; // NOI18N
        if (__type != null) {
            ArrayList __mods = new ArrayList ();
            __mods.add (this.idl_name2java_name (__type.getName (), false));
            while (__type.getParent () != null) {
                __type = __type.getParent ();
                if (ImplGenerator.is_module (__type))
                    __mods.add (this.idl_name2java_name (__type.getName (), false));
                if (ImplGenerator.is_container (__type)
		    && (!ImplGenerator.is_module (__type)))
                    __mods.add (this.idl_name2java_name (__type.getName (), false)
				+ "Package"); // NOI18N

            }
            // transform modules names from list to string in package format
	    // added trsnformating with idl_name2java_name
            for (int __i = __mods.size () - 1; __i>=0; __i--) {
                if (DEBUG)
                    System.out.println ("transfrom: " + (String)__mods.get (__i)); // NOI18N
                //__modules = __modules + this.idl_name2java_name ((String)__mods.get (__i))
                __modules = __modules + (String)__mods.get (__i)
		    + "."; // NOI18N
            }
            // without last dot
            __modules = __modules.substring (0, __modules.length () - 1);
            if (DEBUG)
                System.out.println ("result: >" + __modules + "<"); // NOI18N
        }
        return __modules;
    }

    
    private String ctype2package (String __package, IDLElement __element) {
	if (__package != null && (!__package.equals (""))) // NOI18N
	    return __package + "." + this.ctype2package (__element); // NOI18N
	else
	    return this.ctype2package (__element); // NOI18N
    }


    public static String nameFromScopeName (String name) {
        if (name != null) {
            if (name.lastIndexOf ("::") != -1) { // NOI18N
                return name.substring (name.lastIndexOf ("::") + 2, name.length ()); // NOI18N
            }
        }
        return name;
    }

    
    private static List elementParents (IDLElement __element) {
	if (__element.getParent () == null
	    || __element.getParent ().getParent () == null) {
	    return new ArrayList ();
	}
	List __result = new ArrayList ();
	__result.add (__element.getParent ());
	__result.addAll (ImplGenerator.elementParents (__element.getParent ()));
	return __result;
    }

    private static List makeLexicalCut (IDLElement __element) {
	List __result = ImplGenerator.elementParents (__element);
	__result.add (0, __element);
	return __result;
    }

    private static List scope_name2list (String __name) {
	if (DEBUG)
	    System.out.println ("scope_name2list (" + __name + ");");
        StringTokenizer __st = new StringTokenizer (__name, "::"); // NOI18N
	String __s = null;
	List __result = new ArrayList ();
        while (__st.hasMoreTokens ()) {
            __s = __st.nextToken ();
	    __result.add (__s);
        }
	if (DEBUG)
	    System.out.println ("scope_name2list () -> " + __result);
	return __result;
    }

    private static String list2absolute_scope_name (List __name) {
	Assertion.assert (__name != null);
	StringBuffer __buf = new StringBuffer ();
	// the name is in form [A, B, xxx] where A is module with module B which is module
	// with interface xxx
	Iterator __iter = __name.iterator ();
	while (__iter.hasNext ()) {
	    __buf.append ("::");
	    __buf.append ((String)__iter.next ());
	}
	return __buf.toString ();
    }

    private static IDLElement find_element_by_type (IDLType __type, IDLElement __from) {
	if (DEBUG)
            System.out.println ("ImplGenerator::find_element_by_type ("
				+ __type + ", " // NOI18N
                                + __from.getName () + ":" + __from + ");"); // NOI18N
        if (__type.getType () == IDLType.SEQUENCE) {
            //return ImplGenerator.find_element_by_name (__type.ofType ().getName (), __from);
	    return ImplGenerator.find_element_by_type (__type.ofType (), __from);
	}
        else
            return ImplGenerator.find_element_by_name (__type.getName (), __from);
    }

    private static IDLElement find_element_by_name (String __name, IDLElement __from) {
	Assertion.assert (__name != null && __from != null);
	//boolean DEBUG=true;
	if (DEBUG) {
	    System.out.println ("find_element_by_name (" + __name + ", " + __from + ");");
	    System.out.println ("_S_symbol_table: " + _S_symbol_table);
	}
	List __name_of_scope = ImplGenerator.element2list_name (__from.getParent ());
	List __searched_name = scope_name2list (__name);
	//Collections.reverse (__name_of_scope);
	if (ImplGenerator.is_absolute_scope_name (__name)) {
	    // absolute scope name
	    IDLElement __result = (IDLElement)_S_symbol_table.get_element (__searched_name);
	    return __result;
	}
	else {
	    // simple or scope name
	    List __possible_names = new ArrayList ();
	    for (int __i = 0; __i < __name_of_scope.size (); __i++) {
		List __t_name = new ArrayList ();
		for (int __j = __i; __j < __name_of_scope.size (); __j++) {
		    __t_name.add (0, __name_of_scope.get (__j));
		}
		__t_name.addAll (__searched_name);
		__possible_names.add (__t_name);
	    }
	    __possible_names.add (__searched_name);
	    //List __result = _S_symbol_table.get (__name);
	    if (DEBUG) {
		System.out.println ("__name_of_scope: " + __name_of_scope);
		System.out.println ("__possible_names: " + __possible_names);
	    }
	    List __t_results = new ArrayList ();
	    Iterator __i_names = __possible_names.iterator ();
	    while (__i_names.hasNext ()) {
		IDLElement __tmp = _S_symbol_table.get_element ((List)__i_names.next ());
		if (__tmp != null)
		    __t_results.add (__tmp);
	    }
	    if (DEBUG)
		System.out.println ("__t_results: " + __t_results);
	    Collections.sort (__t_results, new LexicalComparator ());
	    if (DEBUG)
		System.out.println ("sorted __t_results: " + __t_results);
	    if (__t_results.size () > 0)
		return (IDLElement)__t_results.get (__t_results.size () - 1);
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


    private static boolean is_struct (IDLElement __element) {
	if (__element instanceof StructTypeElement) {
	    return true;
	}
	return false;
    }


    private static boolean is_type (IDLElement __element) {
	if (__element instanceof TypeElement)
	    return true;
	return false;
    }


    private static boolean is_interface (IDLElement __element) {
	if (__element instanceof InterfaceElement)
	    return true;
	return false;
    }

    
    private static boolean is_exception (IDLElement __element) {
	if (__element instanceof ExceptionElement)
	    return true;
	return false;
    }


    private static boolean is_module (IDLElement __element) {
	if (__element instanceof ModuleElement)
	    return true;
	return false;
    }


    private static boolean is_container (IDLElement __element) {
	return (ImplGenerator.is_module (__element) 
		|| ImplGenerator.is_interface (__element)
		|| ImplGenerator.is_value (__element)
		|| ImplGenerator.is_struct (__element));
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


    private static boolean is_array (IDLElement __element) {
	if (ImplGenerator.is_declarator (__element)) {
	    DeclaratorElement __declarator = (DeclaratorElement)__element;
	    if (__declarator.getDimension ().size () > 0)
		return true;
	}
	return false;
    }

    
    private static boolean is_native (IDLElement __element) {
	IDLType __idl_type = ((TypeElement)__element).getType ();
	if (__idl_type.getType () == IDLType.NATIVE)
	    return true;
	else
	    return false;
    }    


    private static boolean is_operation (IDLElement __element) {
	if (__element instanceof OperationElement)
	    return true;
	return false;
    }


    private static boolean is_attribute (IDLElement __element) {
	if (__element instanceof AttributeElement)
	    return true;
	return false;
    }

    private static boolean is_identifier (IDLElement __element) {
	if (__element instanceof org.netbeans.modules.corba.idl.src.Identifier)
	    return true;
	return false;
    }


    private static boolean is_forward_interface (IDLElement __element) {
	if (__element instanceof InterfaceForwardElement)
	    return true;
	return false;
    }


    private static boolean is_forward_value (IDLElement __element) {
	if (__element instanceof ValueForwardElement)
	    return true;
	return false;
    }


    private static boolean is_abstract_value (IDLElement __element) {
	if (__element instanceof ValueAbsElement)
	    return true;
	return false;
    }


    private static boolean is_concrete_value (IDLElement __element) {
	if (__element instanceof ValueElement)
	    return true;
	return false;
    }


    private static boolean is_const (IDLElement __element) {
	if (__element instanceof ConstElement)
	    return true;
	return false;
    }


    private static boolean is_state (IDLElement __element) {
	if (__element instanceof StateMemberElement)
	    return true;
	return false;
    }


    private static boolean is_init (IDLElement __element) {
	if (__element instanceof InitDclElement)
	    return true;
	return false;
    }


    private static boolean is_member (IDLElement __element) {
	if (__element instanceof org.netbeans.modules.corba.idl.src.MemberElement)
	    return true;
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
			    int __array_counter, IDLElement __orig_type_element)
	throws SymbolNotFoundException {
	//boolean DEBUG = true;
	Type __java_type = null;
	if (DEBUG) {
	    System.out.println ("*type2java*");
	    System.out.println ("__idl_type: " + __idl_type);
	    System.out.println ("__orig_type: " + __orig_type);
	    System.out.println ("__mode: " + __mode);
	    System.out.println ("__start_element: " + __start_element);
	    System.out.println ("__array_counter: " + __array_counter);
	    System.out.println ("__orig_type_element: " + __orig_type_element);
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
	IDLElement __element_for_type = null;
	__element_for_type = ImplGenerator.find_element_by_type
	    (__idl_type, __start_element);

	if (__element_for_type != null) {
	    if (DEBUG)
		System.out.println ("found element: " + __element_for_type.getName ());
	    if (__orig_type_element == null)
		__orig_type_element = __element_for_type;
	    if (this.is_declarator (__element_for_type)) {
		if (DEBUG)
		    System.out.println ("found declarator");
		IDLType __new_type = null;
		TypeElement __parent = null;
		if (ImplGenerator.is_type (__element_for_type.getParent ()))
		    __parent = (TypeElement)__element_for_type.getParent ();
		else {
		    // The parent is sometimes MemberElement which has 
		    // TypeElement as its first member
		    __parent = (TypeElement)__element_for_type.getParent ().getMember (0);
		    __start_element = __parent;
		    __new_type = ((TypeElement)__start_element).getType ();
		    return this.type2java (__new_type, __orig_type, __mode, __package,
					   __start_element, __array_counter, 
					   __orig_type_element);		    
		}
		if (DEBUG)
		    System.out.println ("__parent: " + __parent);
		IDLType __parent_type = __parent.getType ();
		DeclaratorElement __declarator = (DeclaratorElement)__element_for_type;
		if (__mode != Parameter.IN && (this.is_sequence (__parent) 
					       || this.is_array (__declarator))) {
		    if (DEBUG)
			System.out.println ("->spec handling for inout|out declarators");
		    // handling for cases
		    // typedef sequence<long, 10> A; || typedef long A[10];
		    // typedef A B;
		    // interface test { void op (inout B p);
		    //                           ^ it has to be mapped to:
		    // AHolder
		    String __name = this.ctype2package 
			(__package, __element_for_type);
		    return this.create_type_from_name (__name + "Holder", 0);
		}
		
		if (this.is_sequence (__parent)) {
		    //__new_type = null; // new IDLType (...);
		    __new_type = __parent_type;
		    if (DEBUG)
			System.out.println ("sequence...");
		    while (__new_type.ofType ().getType () == IDLType.SEQUENCE) {
			if (DEBUG) {
			    System.out.println ("seq...");
			    System.out.println ("__new_type.ofType (): "
						+ __new_type.ofType ());
			    System.out.println ("__new_type.ofDimension (): "
						+ __new_type.ofDimension ());
			}
			__array_counter += 1;
			__new_type = __new_type.ofType ();
			if (DEBUG) {
			    System.out.println ("__array_counter: " + __array_counter);
			    System.out.println ("__new_type: " + __new_type);
			}
		    }
		    __array_counter += 1;
		    __new_type = new IDLType (__new_type.ofType ().getType (),
					      __new_type.ofType ().getName (),
					      __new_type.ofType (),
					      __new_type.ofDimension ());
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
		/*
		  return this.type2java (__new_type, __orig_type, __mode, __package,
		  __start_element, __array_counter, 
		  __orig_type_element);
		*/
		return this.type2java (__new_type, __orig_type, __mode, __package,
				       __declarator, __array_counter, 
				       __orig_type_element);
	    }
	    if (this.is_constructed_type (__element_for_type)
		|| this.is_interface (__element_for_type)
		|| this.is_forward_interface (__element_for_type)
		|| this.is_value (__element_for_type)
		|| this.is_valuebox (__element_for_type)
		|| this.is_forward_value (__element_for_type)
		|| this.is_struct (__element_for_type)) {
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
		    if (DEBUG)
			System.out.println ("inout|out of constructed type|interface"
					    + "|value[box]: " + __idl_type.getName ());
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
	/*
	  if (__start_element.getParent () != null) {
	  //System.out.println ("end :-)");
	  return this.type2java (__idl_type, __orig_type, __mode, __package, 
	  __start_element.getParent (), __array_counter,
	  __orig_type_element);
	  }
	*/
	// found element is null
	//return Type.createClass (org.openide.src.Identifier.create ("unknown_type"));
	throw new SymbolNotFoundException (__idl_type.getName ());
    }


    public Type type2java (IDLType __idl_type, int __mode, String __package,
                           IDLElement __element)
	throws SymbolNotFoundException {
	return this.type2java (__idl_type, __idl_type, __mode, __package, __element, 0, 
			       null);
    }


    private static String element2repo_id (IDLElement __element, String __prefix,
					   String __delim, String __postfix) {
	Stack __stack = new Stack ();
	IDLElement __tmp_element = __element;
	while (__tmp_element.getParent () != null) {
	    __stack.push (__tmp_element.getName ());
	    __tmp_element = __tmp_element.getParent ();
	}
	String __id = __prefix;
	while (!__stack.isEmpty ()) {
	    __id += (String)__stack.pop ();
	    if (!__stack.isEmpty ())
		__id += __delim;
	}
	__id += __postfix;
	return __id;
    }


    public static String element2repo_id (IDLElement __element) {
	return ImplGenerator.element2repo_id (__element, "IDL:", "/", ":1.0");
    }


    public static String element2absolute_scope_name (IDLElement __element) {
	return ImplGenerator.element2repo_id (__element, "", "::", "");
    }


    public String exception2java (String __ex, String __package, IDLElement __element) {
	IDLElement __element_for_exception = ImplGenerator.find_element_by_name
	    (__ex, __element);
        if (DEBUG)
            System.out.println ("element_for_exception: " + __element_for_exception.getName () + " : " // NOI18N
                                + __element_for_exception);
        String __full_name =""; // NOI18N
	if (__package.length() >0) 
	    __full_name = __package + "."; // NOI18N
	__full_name += this.ctype2package (__element_for_exception);

        return __full_name;
    }


    public MethodElement[] attribute2java (AttributeElement __attr)
	throws SymbolNotFoundException {
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        IDLElement __parent_element = __attr.getParent ();
	/*
	  Type __attr_type = this.type2java (__attr.getType (), Parameter.IN, 
	  __package, __parent_element);
	*/
	Type __attr_type = this.type2java (__attr.getType (), Parameter.IN, 
					   __package, __attr);
	MethodElement __geter = new MethodElement ();
	MethodElement __seter = new MethodElement ();
	int __attr_length = 1;
        if (DEBUG)
	    System.out.println ("attribute2java"); // NOI18N
        if (DEBUG) {
            System.out.println ("attribute: " + __attr.getName ()); // NOI18N
            System.out.println ("type: " + __attr.getType ()); // NOI18N
            System.out.println ("java: " + __attr_type); // NOI18N
            System.out.println ("package: " + __package); // NOI18N
	}
        try {
	    if (DEBUG)
		System.out.println ("::id9 " + __attr.getName ()); // NOI18N
	    __geter.setName (org.openide.src.Identifier.create 
			     (this.idl_name2java_name (__attr.getName (), true)));
	    __geter.setModifiers (Modifier.PUBLIC);
	    __geter.setReturn (__attr_type);
	    //geter.setBody ("\n  return null;\n"); // NOI18N
	    this.setBodyOfMethod (__geter);
	    //__clazz.addMethod (__geter); // now addMethod throws SourceExcetion
	    } catch (SourceException e) {
		//e.printStackTrace ();
	    }

	if (!__attr.getReadOnly ()) {
            try {
		__attr_length++;
		if (DEBUG)
		    System.out.println ("::id10 " + __attr.getName ()); // NOI18N
		__seter.setName (org.openide.src.Identifier.create
				 (this.idl_name2java_name (__attr.getName (), true)));
		__seter.setModifiers (Modifier.PUBLIC);
		__seter.setReturn (Type.VOID);
		//seter.setBody ("\n"); // NOI18N
		this.setBodyOfMethod (__seter);
		__seter.setParameters (new MethodParameter[] {
		    new MethodParameter ("value", __attr_type, false) }); // NOI18N
		//__clazz.addMethod (__seter); // now addMethod throws SourceExcetion
	    } catch (SourceException e) {
		//e.printStackTrace ();
	    }
        }
	
	MethodElement[] __attr_methods = new MethodElement[__attr_length];
	__attr_methods[0] = __geter;
	if (!__attr.getReadOnly ()) {
	    __attr_methods[1] = __seter;
	}

	return __attr_methods;
    }


    public MethodElement operation2java (OperationElement __operation)
	throws DuplicateExceptionException, SymbolNotFoundException {
	//boolean DEBUG=true;
        if (DEBUG)
            System.out.println ("operation2java"); // NOI18N
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        IDLElement __parent_element = __operation.getParent ();
	/*
	  Type __rettype = this.type2java (__operation.getReturnType (), Parameter.IN, 
	  __package, __parent_element);
	*/
	Type __rettype = this.type2java (__operation.getReturnType (), Parameter.IN, 
					 __package, __operation);
	
	MethodElement __oper = new MethodElement ();
        if (DEBUG) {
            System.out.println ("operation: " + __operation.getName ()); // NOI18N
            System.out.println ("operation rettype:" + __operation.getReturnType () + ":"); // NOI18N
            System.out.println ("return type: " + __rettype); // NOI18N
        }
        try {
            if (DEBUG)
                System.out.println ("::id11 " + __operation.getName ()); // NOI18N
            __oper.setName (org.openide.src.Identifier.create
			    (this.idl_name2java_name (__operation.getName (), true)));
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
		/*
		  Type __ptype = this.type2java (__p.getType (), __p.getAttribute (), 
		  __package, __parent_element);
		*/
		Type __ptype = this.type2java (__p.getType (), __p.getAttribute (), 
					       __package, __operation);
		if (DEBUG) {
		    System.out.println ("param type: " + __ptype);
		    System.out.println ("param name: " + this.idl_name2java_name
					(__p.getName (), false));
		}
                __params[__i] = new MethodParameter
		    (this.idl_name2java_name (__p.getName (), false), __ptype, false);
            }
            if (__operation.getContexts ().size () != 0)
                __params[__params.length - 1] = new MethodParameter
		    ("ctx", Type.createClass (org.openide.src.Identifier.create // NOI18N
					      ("org.omg.CORBA.Context")), false); // NOI18N
            __oper.setParameters (__params);

            // exceptions
	    HashSet __exc_set = new HashSet ();
	    int __exc_set_size = __exc_set.size ();
            org.openide.src.Identifier[] __excs
		= new org.openide.src.Identifier[__operation.getExceptions ().size ()];
            for (int __i=0; __i<__operation.getExceptions ().size (); __i++) {
		String __name = (String)__operation.getExceptions ().elementAt (__i);
		IDLElement __e = ImplGenerator.find_element_by_name (__name, __operation);
		if (__e == null)
		    throw new SymbolNotFoundException (__name);
                __excs[__i] = org.openide.src.Identifier.create
		    (this.exception2java (__name, __package, __operation));
		__exc_set_size = __exc_set.size ();
		__exc_set.add (__excs[__i]);
		if (__exc_set.size () == __exc_set_size)
		    throw new DuplicateExceptionException (__excs[__i].getName ());
            }
            __oper.setExceptions (__excs);

            this.setBodyOfMethod (__oper);
            //__clazz.addMethod (__oper); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
        }

	return __oper;

    }


    private FileObject create_folders (List __folders, FileObject __currect_folder)
	throws java.io.IOException {
	Iterator __iterator = __folders.iterator ();
	while (__iterator.hasNext ()) {
	    Enumeration __tmp_folders = __currect_folder.getFolders (false);
	    String __name_of_folder = (String)__iterator.next ();
	    boolean __folder_exist = false;
	    while (__tmp_folders.hasMoreElements ()) {
		FileObject __folder = (FileObject)__tmp_folders.nextElement ();
		if (__folder.getName ().equals (__name_of_folder)) {
		    __folder_exist = true;
		    __currect_folder = __folder;
		    if (DEBUG)
			System.out.println ("found folder: " + __currect_folder);
		    break;
		}
	    }
	    if (!__folder_exist) {
		__currect_folder = __currect_folder.createFolder (__name_of_folder);
		if (DEBUG)
		    System.out.println ("create folder: " + __currect_folder);
	    }
	}
	return __currect_folder;
    }


    public static List get_all_interfaces (IDLElement __element, ObjectFilter __filter) {
	//return this.get_interfaces (this._M_src);
	return ImplGenerator.get_elements_from_element (__element, __filter, true);
    }


    public static List get_all_interfaces (IDLElement __element) {
	return ImplGenerator.get_all_interfaces (__element, new InterfaceFilter ());
    }
    

    public List get_all_interfaces (ObjectFilter __filter) {
	return ImplGenerator.get_all_interfaces (this._M_src, __filter);
    }


    public static List get_all_values (IDLElement __element, ObjectFilter __filter) {
	return ImplGenerator.get_elements_from_element (__element, __filter, true);
    }


    private List get_all_values (ObjectFilter __filter) {
	return ImplGenerator.get_all_values (this._M_src, __filter);
    }


    public static List get_all_factories (IDLElement __element) {
	return ImplGenerator.get_elements_from_element (__element,
							new ValueFactoryFilter (),
							true);
    }


    private List get_all_factories () {
	return ImplGenerator.get_all_factories (this._M_src);
    }


    private IDLElement resolve_typedef (IDLElement __element)
	throws SymbolNotFoundException {
	//String __name_of_parent = (String)__tmp_parents.get (__j);
	if (__element == null)
	    return null;
	String __name_of_parent = null;
	IDLElement __parent = __element;
	for (;;) {
	    //__parent = this.find_element_by_name (__name_of_parent, __element);
	    if (ImplGenerator.is_declarator (__parent)) {
		// this is a typedef
		if (DEBUG) {
		    System.out.println ("typedefed parent");
		    System.out.println ("found: " + __parent);
		    System.out.println ("searching for: "
					+ __parent.getParent ().getName ());
		}
		__element = __parent;
		__name_of_parent = __parent.getParent ().getName ();
		__parent = this.find_element_by_name (__name_of_parent, __element);
		if (__parent == null)
		    throw new SymbolNotFoundException (__name_of_parent);
	    }
	    else {
		break;
	    }
	}
	return __parent;
    }

    private ArrayList generic_parents (IDLElement __element,
				       ObjectFilter[] __filter,
				       ParentsExecutor[] __executor,
				       boolean __recursive)
	throws SymbolNotFoundException, CannotInheritFromException {
	//boolean DEBUG=true;
	Assertion.assert (__element != null && __filter.length == __executor.length);
	if (DEBUG)
	    System.out.println ("generic_parents for: " + __element);
	ArrayList __result = new ArrayList ();
	ArrayList __parents_for_recursion = new ArrayList ();
	for (int __i=0; __i<__executor.length;__i++) {
	    if (DEBUG)
		System.out.println ("__executor[" + __i + "]: " + __executor[__i]);
	    //System.out.println ("__element: " + __element);
	    List __tmp_parents = __executor[__i].getParents (__element);
	    if (DEBUG)
		System.out.println ("__tmp_parents: " + __tmp_parents);
	    for (int __j=0; __j<__tmp_parents.size (); __j++) {
		String __name_of_parent = (String)__tmp_parents.get (__j);
		IDLElement __parent = null;
		__parent = this.find_element_by_name (__name_of_parent, __element);
		if (__parent == null) {
		    throw new SymbolNotFoundException (__name_of_parent);
		}
		IDLElement __tmp = __parent;
		__parent = this.resolve_typedef (__tmp);
		__name_of_parent = __parent.getName ();
                if (__parent == __element)
                    throw new CannotInheritFromException (__name_of_parent);
		if (DEBUG)
		    System.out.println ("parent: " + __parent);
		if (__filter[__i].is (__parent)) {
		    if (DEBUG)
			System.out.println ("adding parent into result: " + __parent);
		    __result.add (__parent);
		}
	    }
	}
	if (__recursive) {
	    if (DEBUG)
		System.out.println ("parents for recursion: " + __result);
	    Iterator __r_iter = __result.iterator ();
	    ArrayList __final_result = new ArrayList ();
	    HashSet __set = new HashSet ();
	    __set.addAll (__result);
	    while (__r_iter.hasNext ()) {
		IDLElement __t_element = (IDLElement)__r_iter.next ();
		if (DEBUG)
		    System.out.println ("__t_element: " + __t_element);
		List __t_result = this.generic_parents
		    (__t_element, __filter, __executor, true);
		if (DEBUG)
		    System.out.println ("__t_result: " + __t_result);
		__set.addAll (__t_result);
	    }
	    __final_result.addAll (__set);
	    if (DEBUG)
		System.out.println ("generic_parents (recursive) -> " + __final_result);
	    return __final_result;
	}
	if (DEBUG)
	    System.out.println ("generic_parents (non-recursive) -> " + __result);
	return __result;
    }


    private ArrayList generic_parents (IDLElement __element, ObjectFilter __filter,
				       ParentsExecutor __executor, boolean __recursive)
	throws SymbolNotFoundException, CannotInheritFromException {
	return this.generic_parents (__element, new ObjectFilter[] {__filter},
				     new ParentsExecutor[] {__executor}, __recursive);
    }


    private ArrayList all_concrete_parents (InterfaceElement __interface)
	throws SymbolNotFoundException, CannotInheritFromException {
	return this.generic_parents (__interface, new ConcreteInterfaceFilter (),
				     new InterfaceParentsExecutor (), true);
    }


    private ArrayList all_abstract_parents (InterfaceElement __interface) 
	throws SymbolNotFoundException, CannotInheritFromException {
	return this.generic_parents (__interface, new AbstractInterfaceFilter (),
				     new InterfaceParentsExecutor (), true);
    }


    private ArrayList abstract_parents (InterfaceElement __interface) 
	throws SymbolNotFoundException, CannotInheritFromException {
	return this.generic_parents (__interface, new AbstractInterfaceFilter (),
				     new InterfaceParentsExecutor (), false);
    }

    
    private ArrayList all_parents_and_supported_interfaces (ValueElement __value) 
	throws SymbolNotFoundException, CannotInheritFromException {
	return this.generic_parents (__value, 
				     new ObjectFilter[] {
					 new ValueFilter (), 
					 new InterfaceFilter ()},
				     new ParentsExecutor[] {
					 new ValueParentsExecutor (),
					 new SupportedInterfacesExecutor ()},
				     true);
    }


    private ArrayList directly_implemented_interfaces (InterfaceElement __interface)
	throws SymbolNotFoundException, CannotInheritFromException {
	ArrayList __abstract_parents = this.abstract_parents (__interface);
	Iterator __iterator = __abstract_parents.iterator ();
	ArrayList __result = new ArrayList ();
	__result.addAll (__abstract_parents);
	while (__iterator.hasNext ()) {
	    InterfaceElement __tmp = (InterfaceElement)__iterator.next ();
	    ArrayList __tmp_list = this.all_abstract_parents (__tmp);
	    Iterator __tmp_iterator = __tmp_list.iterator ();
	    while (__tmp_iterator.hasNext ()) {
		Object __object = __tmp_iterator.next ();
		if (!__result.contains (__object))
		    __result.add (__object);
	    }
	}
	return __result;
    }

    
    private ArrayList all_implemented_interfaces (InterfaceElement __interface)
	throws SymbolNotFoundException, CannotInheritFromException {
	ArrayList __abstract_parents = this.all_abstract_parents (__interface);
	ArrayList __concrete_parents = this.all_concrete_parents (__interface);
	ArrayList __result = new ArrayList ();
	Iterator __iterator = __concrete_parents.iterator ();
	while (__iterator.hasNext ()) {
	    InterfaceElement __parent = (InterfaceElement)__iterator.next ();
	    ArrayList __tmp_abstract_parents = this.all_abstract_parents (__parent);
	    Iterator __ap_iterator = __tmp_abstract_parents.iterator ();
	    while (__ap_iterator.hasNext ()) {
		Object __object = __ap_iterator.next ();
		if (!__abstract_parents.contains (__object))
		    __abstract_parents.add (__object);
	    }
	}
	__result.addAll (__abstract_parents);
	__result.addAll (__concrete_parents);
	return __result;
    }


    private static List get_elements_from_element (IDLElement __element,
						   ObjectFilter __filter, 
						   boolean __recursive) {
	Vector __members = __element.getMembers ();
	List __result = new LinkedList ();
	for (int __i=0; __i<__members.size (); __i++) {
	    if (__filter.is (__members.elementAt (__i)))
		__result.add (__members.elementAt (__i));
	}
	if (__recursive) {
	    for (int __i=0; __i<__members.size (); __i++) {
		__result.addAll 
		    (ImplGenerator.get_elements_from_element 
		     ((IDLElement)__members.elementAt (__i), __filter, __recursive));
	    }
	}
	return __result;
    }


    private ArrayList directly_implemented_methods (InterfaceElement __interface) 
	throws SymbolNotFoundException, CannotInheritFromException {
	ArrayList __result = new ArrayList ();
	
	if (__interface.isAbstract ())
	    return __result;

	__result.addAll (this.get_elements_from_element 
			 (__interface, new OperationFilter (), false));
	ArrayList __directly_implemented_interfaces 
	    = this.directly_implemented_interfaces (__interface);
	Iterator __iterator = __directly_implemented_interfaces.iterator ();
	while (__iterator.hasNext ()) {
	    InterfaceElement __tmp_interface = (InterfaceElement)__iterator.next ();
	    List __tmp = this.get_elements_from_element 
		(__tmp_interface, new OperationFilter (), false);
	    __result.addAll (__tmp);
	}
	return __result;
    }


    private ArrayList all_implemented_methods (InterfaceElement __interface) 
	throws SymbolNotFoundException, CannotInheritFromException {
	
	if (__interface.isAbstract ())
	    return new ArrayList ();

	ArrayList __all_implemented_interfaces
	    = this.all_implemented_interfaces (__interface);
	ArrayList __tmp_result = new ArrayList ();
	ArrayList __result = new ArrayList ();
	__result.addAll (this.get_elements_from_element 
			 (__interface, new OperationFilter (), false));
	Iterator __iterator = __all_implemented_interfaces.iterator ();
	while (__iterator.hasNext ()) {
	    InterfaceElement __tmp_interface = (InterfaceElement)__iterator.next ();
	    List __tmp = this.get_elements_from_element 
		(__tmp_interface, new OperationFilter (), false);
	    __tmp_result.addAll (__tmp);	    
	}
	__result.addAll (__tmp_result);
	return __result;
    }


    private static String element2java_name (IDLDataObject __ido, IDLElement __element,
					     String __prefix, String __posfix) {
        String __default_package 
	    = __ido.getPrimaryFile ().getParent ().getPackageName ('.');
	if (!__default_package.equals (""))
	    __default_package += ".";
	String __package = ImplGenerator.modules2package (__element);
	String __name = "";
	//String __interface_name = this.element2repo_id (__interface, "", "_", "");
	String __element_name = ImplGenerator.idl_name2java_name
	    (__element.getName (), false);
	if (__package.equals ("")) {
	    return __default_package + __prefix + __element_name + __posfix;
	}
	else {
	    return __default_package + __package + __prefix + __element_name + __posfix;
	}
    }


    private String element2java_name (IDLElement __element, String __prefix,
				      String __posfix) {
	return ImplGenerator.element2java_name (_M_ido, __element, __prefix, __posfix);
    }


    private String element2java_name (IDLElement __element) {
	return this.element2java_name (__element, "", "");
    }


    private String interface2java_name (InterfaceElement __interface) {
	return this.element2java_name (__interface);
    }


    private static String interface2java_name (IDLDataObject __ido, 
					       InterfaceElement __interface) {
	return ImplGenerator.element2java_name (__ido, __interface, "", "");
    }
    

    public String interface2java_impl_name (InterfaceElement __interface) {
	String __prefix = "";
	String __postfix = "";
	if (!TIE) {
	    __prefix = this.IMPLBASE_IMPL_PREFIX;
	    __postfix = this.IMPLBASE_IMPL_POSTFIX;
	}
	else {
	    __prefix = this.TIE_IMPL_PREFIX;
	    __postfix = this.TIE_IMPL_POSTFIX;
	}
	return this.element2java_name (__interface, __prefix, __postfix);
    }

    public String interface2partial_java_impl_name (InterfaceElement __interface) {
	String __full_name = this.interface2java_impl_name (__interface);
	int __index = __full_name.lastIndexOf ('.');
	return __full_name.substring (__index + 1, __full_name.length ());
    }

    public String interface2java_impl_super_name (InterfaceElement __interface) {
	String __prefix = "";
	String __postfix = "";
	if (!TIE) {
	    __prefix = this.EXT_CLASS_PREFIX;
	    __postfix = this.EXT_CLASS_POSTFIX;
	}
	else {
	    __prefix = this.IMPL_INT_PREFIX;
	    __postfix = this.IMPL_INT_POSTFIX;
	}
	return this.element2java_name (__interface, __prefix, __postfix);
    }


    public static String interface2java_impl_name (IDLDataObject __ido,
						   InterfaceElement __interface) {
        CORBASupportSettings __css = (CORBASupportSettings) CORBASupportSettings.findObject
	    (CORBASupportSettings.class, true);
	ORBSettings __settings = null;
	if (__ido.getOrbForCompilation () != null) {
	    __settings = __css.getSettingByName (__ido.getOrbForCompilation ());
	} else {
	    __settings = __css.getActiveSetting ();
	}
	String __prefix = "";
	String __postfix = "";
	if (!__settings.isTie ()) {
	    __prefix = __settings.getImplBaseImplPrefix ();
	    __postfix = __settings.getImplBaseImplPostfix ();
	}
	else {
	    __prefix = __settings.getTieImplPrefix ();
	    __postfix = __settings.getTieImplPostfix ();
	}
	return ImplGenerator.element2java_name (__ido, __interface, __prefix, __postfix);
    }


    public String value2java_impl_name (ValueAbsElement __value) {
	String __prefix = this.VALUE_IMPL_PREFIX;
	String __postfix = this.VALUE_IMPL_POSTFIX;
	return this.element2java_name (__value, __prefix, __postfix);
    }


    public String value_factory2java_impl_name (InitDclElement __factory) {
	ValueAbsElement __value = (ValueAbsElement)__factory.getParent ();
	String __prefix = this.VALUE_FACTORY_IMPL_PREFIX;
	String __postfix = this.VALUE_FACTORY_IMPL_POSTFIX;
	return this.element2java_name (__value, __prefix, __postfix);
    }


    private String create_field_name_for_parent (InterfaceElement __interface,
						 String __prefix, String __postfix) {
	String __interface_name = this.element2repo_id (__interface, "", "_", "");
	String __identifier = __prefix + __interface_name + __postfix;
	return __identifier;
    }

    
    private String create_field_name_for_parent (InterfaceElement __interface) {
	//String __prefix = "_M_variable_of_type_";
	//String __postfix = "";
	//return this.create_field_name_for_parent (__interface, __prefix, __postfix);
	return this.create_field_name_for_parent (__interface,
						  ImplGenerator.PREFIX_OF_FIELD_NAME,
						  ImplGenerator.POSTFIX_OF_FIELD_NAME);
    }


    private FieldElement create_field_for_parent (InterfaceElement __interface)
	throws SourceException {
	String __type = this.interface2java_impl_name (__interface);
	FieldElement __field = new FieldElement ();
	String __identifier = this.create_field_name_for_parent (__interface);
	__field.setName (org.openide.src.Identifier.create (__identifier));
	__field.setType (Type.createClass (org.openide.src.Identifier.create (__type)));
	__field.setModifiers (Modifier.PRIVATE);
	return __field;
    }


    private String create_set_method_name_for_parent (InterfaceElement __interface,
						      String __prefix, String __postfix) {
	String __interface_name = this.element2repo_id (__interface, "", "_", "");
	String __identifier = __prefix + __interface_name + __postfix;
	return __identifier;
    }


    private String create_set_method_name_for_parent (InterfaceElement __interface) {
	//String __prefix = "_set_parent_of_type_";
	//String __postfix = "";
	return this.create_set_method_name_for_parent
	    (__interface, ImplGenerator.SET_PARENT_METHOD_PREFIX,
	     ImplGenerator.SET_PARENT_METHOD_POSTFIX);
    }


    private MethodElement create_set_method_for_parent (InterfaceElement __interface,
							FieldElement __field) 
	throws SourceException {
	String __identifier = this.create_set_method_name_for_parent (__interface);
	String __type = this.interface2java_impl_name (__interface);
	Type __java_type = Type.createClass (org.openide.src.Identifier.create (__type));
	String __parameter_name = "__value";
	MethodElement __method = new MethodElement ();
	__method.setName (org.openide.src.Identifier.create (__identifier));
	__method.setReturn (Type.VOID);
	__method.setParameters (new MethodParameter[] {
	    new MethodParameter (__parameter_name, __java_type, false) });
	__method.setModifiers (Modifier.PUBLIC);
	__method.setBody (ImplGenerator.SET_PARENT_METHOD_COMMENT + __field.getName ()
			  + " = " + __parameter_name + ";\n");
	return __method;
    }

    private void add_attribute (ClassElement __clazz, MemberElement[] __members) throws OperationAlreadyDefinedException {
        //Preconditions check
        Assertion.assert (__members.length>0 && __members.length <=2);
        if (__members[0] instanceof MethodElement) {
            MethodElement __method = (MethodElement) __members[0];
            MethodElement[] methods = __clazz.getMethods();
            for (int i=0; i< methods.length; i++) {
                if (methods[i].getName().getName().equals(__method.getName().getName())) {
                    // No valid IDL
                    // No two operations can have the same name
                    throw new OperationAlreadyDefinedException (__method.getName().getName());
                }
            }
        }
        // Action
        for (int i=0; i<__members.length; i++) {
            this.add_element (__clazz, __members[i]);
        }
        // Postconditions check
    }
    
    private void add_operation (ClassElement __clazz, MemberElement __member) throws OperationAlreadyDefinedException {
        // Preconditions check 
        if (__member instanceof MethodElement) {
            MethodElement __method = (MethodElement) __member;
            MethodElement[] methods = __clazz.getMethods();
            for (int i=0; i< methods.length; i++) {
                if (methods[i].getName().getName().equals(__method.getName().getName())) {
                    // No valid IDL
                    // No two operations can have the same name
                    throw new OperationAlreadyDefinedException (__method.getName().getName());
                }
            }
        }
        //Action
        this.add_element (__clazz, __member);
        //Postconditions check
    }

    private void add_element (ClassElement __clazz, MemberElement __element) {
	if (DEBUG)
	    System.out.println ("add_element (" + __clazz.getName () + ", "
				+ __element + ");");
	if (_M_use_guarded_blocks)
	    _M_elements_for_guard_blocks.add (__element);
	try {
	    if (__element instanceof FieldElement) {
		FieldElement __field = (FieldElement)__element;
		__clazz.addField (__field);
	    } else if (__element instanceof MethodElement) {
		MethodElement __method = (MethodElement)__element;
		__clazz.addMethod (__method);
	    } else if (__element instanceof ConstructorElement) {
		ConstructorElement __constr = (ConstructorElement)__element;
		__clazz.addConstructor (__constr);
		Thread.dumpStack ();
	    } else {
		Assertion.assert (false);
	    }
	} catch (SourceException __ex) {
	    __ex.printStackTrace ();
	    //throw new AssertionException ();
	    Assertion.assert (false);
	}
    }


    private ArrayList elements2operations (ArrayList __elements) {
	ArrayList __operations = new ArrayList ();
	Iterator __iterator = __elements.iterator ();
	while (__iterator.hasNext ()) {
	    IDLElement __element = (IDLElement)__iterator.next ();
	    __operations.addAll 
		(this.get_elements_from_element (__element, new OperationFilter (), false));
	}
	return __operations;
    }


    private Type[] method_parameters2types (MethodParameter[] __params) {
	Type[] __types = new Type[__params.length];
	for (int __i=0; __i<__params.length; __i++) {
	    __types[__i] = __params[__i].getType ();
	}
	return __types;
    }

    private boolean compare_methods_parameters (MethodParameter[] __p1,
						MethodParameter[] __p2) {
	if (__p1.length == __p2.length) {
	    for (int __i=0; __i<__p1.length; __i++) {
		if (!__p1[__i].compareTo (__p2[__i], false, true))
		    return false;
	    }
	    return true;
	}
	return false;
    }

    private String create_block_name_from_parameters (MethodParameter[] __params) {
	String __name = "";
	Type[] __types = this.method_parameters2types (__params);
	for (int __i=0; __i<__types.length; __i++) {
	    __name += __types[__i].toString () + "_";
	}
	return __name;
    }


    private String create_block_name_from_element (MemberElement __element) {
	String __name = "";
	if (__element instanceof FieldElement) {
	    __name += "F_" + __element.getName ();
	    return __name;
	}
	if (__element instanceof MethodElement) {
	    MethodElement __method = (MethodElement)__element;
	    __name += "M_" + __method.getReturn () + "_" + __method.getName () + "_";
	    __name += this.create_block_name_from_parameters (__method.getParameters ());
	    return __name;
	}
	if (__element instanceof ConstructorElement) {
	    ConstructorElement __constr = (ConstructorElement)__element;
	    __name += "C_" + __constr.getName () + "_";
	    __name += this.create_block_name_from_parameters (__constr.getParameters ());
	    return __name;
	}
	//throw new AssertionException ();
	//System.out.println (__element);
	if (DEBUG)
	    System.out.println ("element: " + __element);
	Assertion.assert (false);
	return null;
    }


    private void work_with_guarded (final int __action, final MemberElement __guarded_element,
				    final String __name_of_block, final JavaEditor __editor) {
	try {
	    if (DEBUG)
		System.out.println ("name of guarded block: " + __name_of_block);
	    Assertion.assert (__guarded_element != null);
	    Assertion.assert (__guarded_element.getDeclaringClass () != null);
	    if (DEBUG) {
		System.out.println ("__guarded_element: " + __guarded_element);
		System.out.println ("class: " + __guarded_element.getDeclaringClass ());
	    }
	    Assertion.assert (__editor != null);
	    //Assertion.assert (__editor.sourceToText 
	    //(__guarded_element.getDeclaringClass ()) != null);
            SourceCookie.Editor __src_editor = (SourceCookie.Editor)
                        __guarded_element.getDeclaringClass ().getCookie (SourceCookie.Editor.class);
                    Assertion.assert (__src_editor != null);
            final javax.swing.text.StyledDocument __root_document = (javax.swing.text.StyledDocument) __src_editor.sourceToText
		(__guarded_element.getDeclaringClass ()).getDocument ();
	    Assertion.assert (__root_document != null);
            synchronized (this) {
                _M_guarding_status = GUARDING_RUNNING;
            }
            __src_editor.getSource().prepare().waitFinished();
            org.openide.text.NbDocument.runAtomic (__root_document, new Runnable () {
                public void run () 
            {
                    int __status=ImplGenerator.GUARDING_RUNNING;
                    try {
                    String __root_text = __root_document.getText (0, __root_document.getLength ());
                    if (DEBUG)
                        System.out.println ("__root_text: " + __root_text);
                    SourceCookie.Editor __src_editor2 = (SourceCookie.Editor)__guarded_element.getCookie
                        (SourceCookie.Editor.class);
                    javax.swing.text.Element __element = __src_editor2.sourceToText
                        (__guarded_element);
                    Assertion.assert (__element != null);
                    List __l_bounds = new ArrayList ();
                    if ((__guarded_element instanceof MethodElement)
                        || (__guarded_element instanceof FieldElement)) {
                        if (DEBUG)
                            System.out.println ("not constructor: " + __guarded_element);
                        int __tmp_start_offset = __element.getStartOffset();
                        int __start_offset = -1;
                        for (int __i=__tmp_start_offset; __i>0; __i--) {
                            // finding "\n"
                            //System.out.println (__i + ". `" + __root_text.charAt (__i) + "'");
                            if (__root_text.charAt (__i) == '\n') {
                                __start_offset = __i+1;
        		        break;
                	    }
                        }
			
                        int __tmp_end_offset = __element.getEndOffset();
                        int __end_offset = __root_text.indexOf ('\n', __tmp_end_offset) + 1;
                        Assertion.assert (__element.getDocument () != null);
                        PositionRef __start_pos_ref = __editor.createPositionRef 
                            (__start_offset, null);
                        PositionRef __end_pos_ref = __editor.createPositionRef
                            (__end_offset, null);
                        //System.out.println("__start_pos_ref: " + __start_pos_ref);
                        PositionBounds __bounds = new PositionBounds (__start_pos_ref, __end_pos_ref);
                        if (DEBUG)
                            System.out.println("bounds: " + __bounds);
                        __l_bounds.add (new Pair (__name_of_block, __bounds));
                    }
                    else {
                        // __element is ConstructorElement so we need to create/remove two
                        // guarded blocks
                        if (DEBUG)
                            System.out.println ("constructor: " + __guarded_element);
                        System.out.println ("_M_elements_for_guard_blocks: "
				    + _M_elements_for_guard_blocks);
                        // Thread.dumpStack ();
                        int __tmp_start_offset = __element.getStartOffset();
                        int __start_offset = -1;
                        for (int __i=__tmp_start_offset; __i>0; __i--) {
                            // finding "\n"
                            //System.out.println (__i + ". `" + __root_text.charAt (__i) + "'");
                            if (__root_text.charAt (__i) == '\n') {
                                __start_offset = __i+1;
                                break;
                            }
                        }
                        int __tmp_end_offset = __element.getEndOffset();
                        int __semicolon_offset = __root_text.indexOf (';', __start_offset) + 1;
                        int __end_offset = __semicolon_offset;
                        for (int __i=__semicolon_offset; __i<__tmp_end_offset; __i++) {
                            //System.out.println (__i + ":0char `"+__root_text.charAt (__i) + "'");
                            // finding "\n"
                            if (__root_text.charAt (__i) == '\n') {
                                if (Character.isWhitespace (__root_text.charAt (__i+1))) {
                                    __end_offset = __i+1;
                                    break;
                                }
                                else {
                                    __end_offset = __i;
                                    break;
                                }
                            }
                            if (!Character.isWhitespace (__root_text.charAt (__i))) {
                                //System.out.println (__i + ":1char `" + __root_text.charAt (__i)
                                //+ "' is not whitespace");
                                __end_offset = __i-1;
                                break;
                            };
                        }
                        Assertion.assert (__element.getDocument () != null);
                        PositionRef __start_pos_ref = __editor.createPositionRef 
                            (__start_offset, null);
                        PositionRef __end_pos_ref = __editor.createPositionRef
                            (__end_offset, null);
                        //System.out.println("__start_pos_ref: " + __start_pos_ref);
                        //System.out.println ("__start_offset: " + __start_offset);
                        //System.out.println ("__end_offset: " + __end_offset);
                        PositionBounds __bounds = new PositionBounds
                            (__start_pos_ref, __end_pos_ref);
                        //System.out.println("bounds: " + __bounds);
                        __l_bounds.add (new Pair (__name_of_block + "_begin", __bounds));
                        //int __tmp_end_offset = __element.getEndOffset();
                        int __tmp_bracket_offset = __root_text.indexOf
                            ('}', __tmp_start_offset) - 1;
                        __start_offset = __end_offset + 1;
                        for (int __i=__tmp_bracket_offset; __i>=__end_offset; __i--) {
                            //System.out.println (__i + ":3char `"+__root_text.charAt (__i) + "'");
                            // finding "\n"
                            //System.out.println (__i + ". `" + __root_text.charAt (__i) + "'");
                            if (__root_text.charAt (__i) == '\n') {
                                __start_offset = __i+1;
                                break;
                            }
                            if (!Character.isWhitespace (__root_text.charAt (__i))) {
                                //System.out.println (__i + ":2char `" + __root_text.charAt (__i)
                                //+ "' is not whitespace");
                                __start_offset = __i+1;
                                break;
                            }
	    
                        }
                        __end_offset = __root_text.indexOf ('\n', __start_offset) + 1;
                        __start_pos_ref = __editor.createPositionRef (__start_offset, null);
                        __end_pos_ref = __editor.createPositionRef (__end_offset, null);
                        __bounds = new PositionBounds (__start_pos_ref, __end_pos_ref);
                        __l_bounds.add (new Pair (__name_of_block + "_end", __bounds));
                        //System.out.println ("__start_offset: " + __start_offset);
                        //System.out.println ("__end_offset: " + __end_offset);
                        //System.out.println ("__bounds: " + __bounds);
                    }
                    Iterator __iter = __l_bounds.iterator ();
                    while (__iter.hasNext ()) {
                        Pair __pair = (Pair)__iter.next ();
                        String __name = (String)__pair.first;
                        PositionBounds __bounds = (PositionBounds)__pair.second;
                        JavaEditor.SimpleSection __guarded = __editor.findSimpleSection
                            (__name);
                        if (__guarded != null) {
                            if (__action == ImplGenerator.REMOVE_SECTION) {
                                if (!__guarded.removeSection ())
                                    Assertion.assert (false);
                            }
                        }
                        else {
                            // can't find guard block with name __name_of_block
                            // we will create one
                            if (__action == ImplGenerator.CREATE_SECTION) {
                                JavaEditor.SimpleSection __section = __editor.createSimpleSection 
                                    (__bounds, __name);
                            }
                        }
                    }
                    __status = GUARDING_OK;
                    }catch (Exception __ex) {
                        __status = GUARDING_FAILED;
                    }
                    finally {
                        synchronized (ImplGenerator.this) {
                            ImplGenerator.this._M_guarding_status = __status;
                            ImplGenerator.this.notify ();
                        }
                    }
                }
            });
            synchronized (this) {
                while (this._M_guarding_status == GUARDING_RUNNING)
                    try {
                        this.wait ();
                    }catch (InterruptedException ie) {}
                if (__editor.isModified())
                    __editor.saveDocument();
                if (this._M_guarding_status == GUARDING_FAILED)
                    Assertion.assert (false);
            }
	} catch (AssertionException __ex) {
	    throw __ex;
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	    //throw new AssertionException ();
	    Assertion.assert (false);
	}
    }


    private void work_with_guarded (int __action, ClassElement __clazz,
				    MemberElement __element, JavaEditor __editor) {
	String __name = "";
	if (__element instanceof FieldElement) {
	    FieldElement __field  = __clazz.getField (__element.getName ());
	    Assertion.assert (__field != null);
	    //if (__field == null)
	    //System.out.println ("can't find: " + __element.getName () + " in: " + __clazz);
	    __name = this.create_block_name_from_element (__field);
	    this.work_with_guarded (__action, __field, __name, __editor);
	    return;
	}
	if (__element instanceof MethodElement) {	    
	    MethodElement __tmp = (MethodElement)__element;
	    MethodElement __method = __clazz.getMethod 
		(__tmp.getName (), this.method_parameters2types (__tmp.getParameters ()));
	    Assertion.assert (__method != null);
	    //if (__method == null) {
	    //System.out.println ("can't find __tmp: " + __tmp);
	    //System.out.println ("in __clazz: " + __clazz);
	    //}
	    __name = this.create_block_name_from_element (__method);
	    this.work_with_guarded (__action, __method, __name, __editor);
	    return;
	}
	if (__element instanceof ConstructorElement) {
	    ConstructorElement __tmp = (ConstructorElement)__element;
	    ConstructorElement __constr = __clazz.getConstructor 
		(this.method_parameters2types (__tmp.getParameters ()));
	    Assertion.assert (__constr != null);
	    __name = this.create_block_name_from_element (__constr);
	    this.work_with_guarded (__action, __constr, __name, __editor);
	    return;
	}
	//throw new AssertionException ();
	Assertion.assert (false);
    }


    private void generate_methods_for_parent (ClassElement __clazz, 
					      InterfaceElement __interface,
					      List __operations, List __methods_map,
					      List __all_methods_map,
					      boolean __virtual_delegation) 
	throws DuplicateExceptionException, SymbolNotFoundException, OperationAlreadyDefinedException {
	String __direct_parent_variable_name;
	MethodElement __method;
	MethodElement[] __methods;
	try {
	    for (int __i=0; __i<__operations.size (); __i++) {
		IDLElement __element = (IDLElement)__operations.get (__i);
		InterfaceElement __interface_for_delegation = null;
		if (!__virtual_delegation) {
		    //__interface_for_delegation = (InterfaceElement)__element.getParent ();
		    //ArrayList __reverse_methods = new ArrayList (__methods_map);
		    //Collections.reverse (__reverse_methods);
		    //Iterator __iter = __reverse_methods.iterator ();
		    Iterator __iter = __methods_map.iterator ();
		    //System.out.println ("reverse methods map: " + __reverse_methods);
		    while (__iter.hasNext ()) {
			Pair __pair = (Pair)__iter.next ();
			InterfaceElement __tmp_interface = (InterfaceElement)__pair.first;
			List __tmp_methods = (List)__pair.second;
			//System.out.println ("comparing: " + __tmp_methods + " of type " + __tmp_interface + " with " + __element);
			if (__tmp_methods.contains (__element)) {
			    if (DEBUG)
				System.out.println ("FOUND: parent for static delegation");
			    __interface_for_delegation = __tmp_interface;
			    break;
			}
		    }
		}
		else {
		    // in case of virtual delegation
		    Iterator __iter = __all_methods_map.iterator ();
		    ArrayList __parents = null;
		    try {
			//__parents = this.all_generic_parents 
			//(__interface, false, false);
			__parents = this.generic_parents 
			    (__interface, new ConcreteInterfaceFilter (), 
			     new InterfaceParentsExecutor (), false);
		    } catch (SymbolNotFoundException __ex) {
			__ex.printStackTrace ();
		    } catch (CannotInheritFromException __ex) {
			__ex.printStackTrace ();
		    }
		    while (__iter.hasNext ()) {
			Pair __pair = (Pair)__iter.next ();
			InterfaceElement __tmp_interface = (InterfaceElement)__pair.first;
			List __tmp_methods = (List)__pair.second;
			if (__parents.contains (__tmp_interface)) {
			    if (__tmp_methods.contains (__element)) {
				__interface_for_delegation = __tmp_interface;
				if (DEBUG)
				    System.out.println 
					("FOUND: parent for virtual delegation: "
					 + __interface_for_delegation);
				break;
			    }
			}
		    }
		}
		if (DEBUG)
		    System.out.println ("__interface_for_delegation: "
					+ __interface_for_delegation);
		String __parent_variable_name 
		    = this.create_field_name_for_parent (__interface_for_delegation);
		if (__operations.get (__i) instanceof AttributeElement) {
		    AttributeElement __attr = (AttributeElement)__operations.get (__i);
		    __methods = this.attribute2java (__attr);
		    for (int __j=0; __j<__methods.length; __j++) {
			String __body = ImplGenerator.DELEGATION_COMMENT;
			if (__methods[__j].getReturn () != Type.VOID)
			    __body += "return ";
			__body += __parent_variable_name + "." 
			    + __methods[__j].getName ().getName () + " (";
			MethodParameter[] __parameters = __methods[__j].getParameters ();
			for (int __k=0; __k<__parameters.length; __k++) {
			    __body += __parameters[__k].getName () + ", ";
			}
			if (__parameters.length > 0)
			    __body = __body.substring (0, __body.length () - 2);
			__body += ");\n";
			__methods[__j].setBody (__body);
		    }
		    //__clazz.addMethods (__methods);
                    this.add_attribute (__clazz, __methods);
		}
		if (__operations.get (__i) instanceof OperationElement) {
		    OperationElement __oper = (OperationElement)__operations.get (__i);
		    __method = this.operation2java (__oper);
		    String __body = ImplGenerator.DELEGATION_COMMENT;
		    if (__method.getReturn () != Type.VOID)
			__body += "return ";
		    __body += __parent_variable_name + "."
			+ __method.getName ().getName () + " (";
		    MethodParameter[] __parameters = __method.getParameters ();
		    for (int __j=0; __j<__parameters.length; __j++) {
			__body += __parameters[__j].getName () + ", ";
		    }
		    if (__parameters.length > 0)
			__body = __body.substring (0, __body.length () - 2);
		    __body += ");\n";
		    __method.setBody (__body);
		    //__clazz.addMethod (__method);
		    this.add_operation (__clazz, __method);
		}
	    }
	} catch (SourceException __ex) {
	}
    }


    private void generate_methods (ClassElement __clazz, List __operations)
	throws DuplicateExceptionException, SymbolNotFoundException {
	
	if (DEBUG)
	    System.out.println ("ImplGenerator::generate_methods () : " + __operations);

	try {
	    Iterator __iterator = __operations.iterator ();
	    //for (int __i=0; __i<__members.size (); __i++) {
	    while (__iterator.hasNext ()) {
		Object __oper = __iterator.next ();
		if (DEBUG)
		    System.out.println ("__oper: " + __oper);
		if (__oper instanceof AttributeElement) {
		    __clazz.addMethods
			(this.attribute2java ((AttributeElement)__oper));
		}
		if (__oper instanceof OperationElement) {
		    __clazz.addMethod
			(this.operation2java ((OperationElement)__oper));
		}
	    }
	} catch (SourceException __ex) {
	    __ex.printStackTrace ();
	}
    }


    private org.openide.src.Identifier getInterfaceByPP (ClassElement __source,
							 String __prefix,
							 String __postfix) {
	org.openide.src.Identifier[] __interfaces = __source.getInterfaces ();
	for (int __i=0; __i<__interfaces.length; __i++) {
	    if (__interfaces[__i].getName ().startsWith (__prefix)
		&& __interfaces[__i].getName ().endsWith (__postfix)) {
		return __interfaces[__i];
	    }
	}
	return null;
    }


    private org.openide.src.Identifier getSuperclassByPP (ClassElement __source,
							  String __prefix,
							  String __postfix) {
	org.openide.src.Identifier __id = __source.getSuperclass ();
	if (__id != null) {
	    if (__id.getName ().startsWith (__prefix)
		&& __id.getName ().endsWith (__postfix)) {
		return __id;
	    }
	}
	return null;
    }


    private void removeInterfaceByPP (ClassElement __source, String __prefix,
				      String __postfix) {
	org.openide.src.Identifier __id = null;
	if ((__id = this.getInterfaceByPP (__source, __prefix, __postfix)) != null) {
	    try {
		__source.removeInterface (__id);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	}
    }


    private void removeSuperclassByPP (ClassElement __source, String __prefix,
				       String __postfix) {
	org.openide.src.Identifier __id = null;
	if ((__id = this.getSuperclassByPP (__source, __prefix, __postfix)) != null) {
	    try {
		__source.setSuperclass (null);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	}
    }


    private void removeTIEInterfaces (ClassElement __source) {
	java.lang.Object[] __beans = _M_css.getBeans ();
	ORBSettings __settings = null;
	for (int __i=0; __i<__beans.length; __i++) {
	    __settings = (ORBSettings)__beans[__i];
	    this.removeInterfaceByPP (__source, __settings.getImplIntPrefix (),
				      __settings.getImplIntPostfix ());
	}
    }


    private void removeImplBaseSkeletons (ClassElement __source) {
	java.lang.Object[] __beans = _M_css.getBeans ();
	ORBSettings __settings = null;
	for (int __i=0; __i<__beans.length; __i++) {
	    __settings = (ORBSettings)__beans[__i];
	    this.removeSuperclassByPP (__source, __settings.getExtClassPrefix (),
				       __settings.getExtClassPostfix ());
	}
    }


    private JavaEditor editor_for_element (MemberElement __element) {
	ClassElement __class = __element.getDeclaringClass ();
	JavaEditor __editor = null;
	try {
	    JavaDataObject __jdo = (JavaDataObject)__class.getSource ().getCookie 
		(JavaDataObject.class);
	    __editor = (JavaEditor)__jdo.getCookie (JavaEditor.class);
	    __editor.openDocument ();
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	}
	return __editor;
    }

    private void remove_guarded_block (MemberElement __element) {
	Assertion.assert (__element instanceof ConstructorElement
			  || __element instanceof FieldElement);
	//System.out.println ("remove_garded_block from: " + __element);
	ClassElement __class = __element.getDeclaringClass ();
	String __block_name = this.create_block_name_from_element (__element);
	JavaEditor __editor = this.editor_for_element (__element);
	this.work_with_guarded (ImplGenerator.REMOVE_SECTION, __element, __block_name,
				__editor);
    }

    private void synchronize_init_methods (ClassElement __source, ClassElement __target)
	throws SourceException {
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	Type[] __empty_paramset = new Type [0];
	MethodElement __source_init = __source.getMethod
	    (org.openide.src.Identifier.create (ImplGenerator.INITIALIZE_INHERITANCE_TREE),
	     __empty_paramset);
	if (__source_init != null) {
	    // found _initialize_inheritance_tree in __source class
	    MethodElement __target_init = __target.getMethod
		(org.openide.src.Identifier.create
		 (ImplGenerator.INITIALIZE_INHERITANCE_TREE),
		 __empty_paramset);
	    if (__target_init != null) {
		if (DEBUG) {
		    System.out.println ("synchronize body of _initialize_inheritance_tree "
					+ "from: `"
					+ __source_init.getBody () + "'\n to: `"
					+ __target_init.getBody () + "'");
		}
		if (!__target_init.getBody ().equals (__source_init.getBody ())) {
		    if (DEBUG)
			System.out.println ("updating init body.");
		    __target_init.setBody (__source_init.getBody ());
		}
	    }
	    else {
		// create method _initialize_inheritance_tree
		MethodElement __init = new MethodElement ();
		__init.setName (org.openide.src.Identifier.create 
				(ImplGenerator.INITIALIZE_INHERITANCE_TREE));
		__init.setReturn (Type.VOID);
		__init.setParameters (new MethodParameter[0]);
		__init.setBody (__source_init.getBody ());
		__init.setModifiers (Modifier.PUBLIC);
		__target.addMethod (__init);
	    }
	}
	else {
	    // remove _initialize_inheritance_tree from __target
	    MethodElement __target_init = __target.getMethod
		(org.openide.src.Identifier.create
		 (ImplGenerator.INITIALIZE_INHERITANCE_TREE),
		 __empty_paramset);
	    if (__target_init != null) {
		this.remove_guarded_block (__target_init);
		__target.removeMethod (__target_init);
	    }
	}
    }


    private static List delegation_fields (ClassElement __source) {
	List __result = new ArrayList ();
	FieldElement[] __source_fields = __source.getFields ();
	for (int __i=0; __i<__source_fields.length; __i++) {
	    String __name = __source_fields[__i].getName ().getName ();
	    if (__name.startsWith (ImplGenerator.PREFIX_OF_FIELD_NAME)
		&& __name.endsWith (ImplGenerator.POSTFIX_OF_FIELD_NAME))
		__result.add (__source_fields[__i]);
	}
	return __result;
    }


    private void synchronize_delegation_fields (ClassElement __source,
						ClassElement __target)
	throws SourceException {
	if (DEBUG)
	    System.out.println ("synchronize_delegation_fields");
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	FieldElement[] __source_fields = __source.getFields ();
	FieldElement[] __target_fields = __target.getFields ();
	for (int __i=0; __i<__source_fields.length; __i++) {
	    FieldElement __tmp_field = __source_fields[__i];
	    if (__target.getField (__tmp_field.getName ()) == null) {
		//System.out.println ("adding field: " + __tmp_field);
		__target.addField (__tmp_field);
	    }
	}
	// remove delegation fields which are not in __source
	//List __sf = ImplGenerator.delegation_fields (__source);
	List __tf = ImplGenerator.delegation_fields (__target);
	Iterator __tf_iter = __tf.iterator ();
	while (__tf_iter.hasNext ()) {
	    FieldElement __field = (FieldElement)__tf_iter.next ();
	    if (DEBUG)
		System.out.println ("target field: " + __field);
	    if (__source.getField (__field.getName ()) == null) {
		// this field is not in __source
		if (DEBUG)
		    System.out.println ("field for remove: " + __field);
		this.remove_guarded_block (__field);
		__target.removeField (__field);
	    }
	}
    }


    private static boolean isWhitespaceString (String __str, int __start, int __end) {
	boolean __left_bracket = false;
	boolean __right_bracket = false;
	for (int __i=__start; __i<=__end; __i++) {
	    if (!Character.isWhitespace (__str.charAt (__i))) {
		if (__str.charAt (__i) == '(') {
		    if (__left_bracket)
			return false;
		    else {
			__left_bracket = true;
			continue;
		    }
		}
		if (__str.charAt (__i) == ')') {
		    if (__right_bracket)
			return false;
		    else {
			__right_bracket = true;
			continue;
		    }
		}
		return false;
	    }
	}
	return true;
    }


    private void synchronize_constructors (ClassElement __source,
					   ClassElement __target)
	throws SourceException {
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	ConstructorElement[] __source_constructors = __source.getConstructors ();
	for (int __i=0; __i<__source_constructors.length; __i++) {
	    ConstructorElement __tmp_constructor = __source_constructors[__i];
	    if ((__target.getConstructor (this.method_parameters2types
					  (__tmp_constructor.getParameters ()))) == null) {
		__target.addConstructor (__tmp_constructor);
	    }
	}
	ConstructorElement[] __target_constructors = __target.getConstructors ();
	MethodElement __target_init = __target.getMethod
	    (org.openide.src.Identifier.create (ImplGenerator.INITIALIZE_INHERITANCE_TREE),
	     new Type [0]);
	String __to_find = "this." + ImplGenerator.INITIALIZE_INHERITANCE_TREE;
	String __call = __to_find + " ();\n";
	for (int __i=0; __i<__target_constructors.length; __i++) {
	    // I have to check that all constructors call _initialize_inheritance_tree
	    // method in case of using delegation or not.
	    String __body = __target_constructors[__i].getBody ();
	    if (__body.indexOf (__to_find) < 0 && (__target_init != null)
		&& (!(_M_delegation.equals (ORBSettingsBundle.DELEGATION_NONE)))) {
		// call not found => I have to add it
		//__body += "\n" + __call;
		__body += __call;
		__target_constructors[__i].setBody (__body);
		continue;
	    }
	    int __start_pos = -1;
	    if ((__start_pos = __body.indexOf (__to_find)) > 0
		&& _M_delegation.equals (ORBSettingsBundle.DELEGATION_NONE)) {
		// call found => I have to remove it
		StringBuffer __buf = new StringBuffer (__body);
		int __new_line_index = __body.indexOf ('\n', __start_pos);
		int __semicolon_index = __body.indexOf (';', __start_pos);
		int __end_index = -1;
		if (__new_line_index < __semicolon_index) {
		    if (ImplGenerator.isWhitespaceString (__body, __new_line_index,
							  __semicolon_index - 1)) {
			__end_index = __semicolon_index;
		    }
		    else {
			__end_index = __new_line_index;
		    }
		}
		else {
		    __end_index = __semicolon_index;
		}
		if (__new_line_index == (__semicolon_index + 1))
		    __end_index = __new_line_index;
		__end_index++;
		__buf.replace (__start_pos, __end_index, "");
		String __new_body = __buf.toString ();
		__target_constructors[__i].setBody (__new_body);
	    }
	}
    }


    private String get_field_name_from_method_body (String __body, ClassElement __target) {
	List __fields = ImplGenerator.delegation_fields (__target);
	Iterator __f_iterator = __fields.iterator ();
	int __pos = -1;
	boolean __commented = false;
	while (__f_iterator.hasNext ()) {
	    FieldElement __field = (FieldElement)__f_iterator.next ();
	    String __name = ((org.openide.src.Identifier)__field.getName ()).getName ();
	    if ((__pos = __body.indexOf (__name)) > -1) {
		for (;;) {
		    --__pos;
		    if (__body.charAt (__pos) == '\n')
			break; // the field application isn't commented out
		    if ((__body.charAt (__pos) == '/' || __body.charAt (__pos) == '*')
			&& __body.charAt (__pos + 1) == '/') {
			// comented out
			__commented = true;
			break;
		    }
		}
		if (!__commented)
		    return __name;
	    }
	}
	return null;
    }


    private String get_method_name_from_method_body (String __body, ClassElement __target) {
	String __field_name = this.get_field_name_from_method_body (__body, __target);
	if (__field_name == null)
	    return null;
	int __pos = __body.indexOf (__field_name);
	int __dot_pos = __body.indexOf (".", __pos);
	if (__dot_pos == -1) // We've not found a call
	    return null;
	int __end_pos = __dot_pos + 1;
	for (;;) {
	    char __c = __body.charAt (__end_pos++);
	    if (Character.isJavaIdentifierStart (__c)
		|| Character.isJavaIdentifierPart (__c))
		continue;
	    __end_pos--;
	    break;
	}
	return __body.substring (++__dot_pos, __end_pos);
    }

    
    private void synchronize_delegation_methods (ClassElement __source,
						 ClassElement __target)
	throws SourceException {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("synchronize_delegation_methods");
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	MethodElement[] __t_methods = __target.getMethods ();
	MethodElement[] __s_methods = __source.getMethods ();
	//MethodElement[] __s_methods = __source.getMethods ();
	List __t_delegation_methods = new ArrayList ();
	List __s_delegation_methods = new ArrayList ();
	String __comment = ImplGenerator.DELEGATION_COMMENT;
	__comment = __comment.substring (1, __comment.length () - 1);
	if (DEBUG)
	    System.out.println ("__comment: `" + __comment + "'");
	for (int __i=0; __i<__t_methods.length; __i++) {
	    if (DEBUG) {
		System.out.println ("target method: " + __t_methods[__i]);
		System.out.println ("target method body: `" + __t_methods[__i].getBody ()
				    + "'");
	    }
	    if ((__t_methods[__i].getBody ().indexOf (__comment)) > -1)
		__t_delegation_methods.add (__t_methods[__i]);
	}
	for (int __i=0; __i<__s_methods.length; __i++) {
	    if (DEBUG) {
		System.out.println ("source method: " + __s_methods[__i]);
		System.out.println ("source method body: `" + __s_methods[__i].getBody ()
				    + "'");
	    }
	    if ((__s_methods[__i].getBody ().indexOf (__comment)) > -1)
		__s_delegation_methods.add (__s_methods[__i]);
	}
	if (DEBUG) {
	    System.out.println ("target delegation methods: " + __t_delegation_methods);
	    System.out.println ("source delegation methods: " + __s_delegation_methods);
	}
	Iterator __d_iter = __t_delegation_methods.iterator ();
	while (__d_iter.hasNext ()) {
	    MethodElement __m = (MethodElement)__d_iter.next ();
	    MethodElement __s = null;
	    if ((__s = __source.getMethod (__m.getName (), this.method_parameters2types
					   (__m.getParameters ()))) == null) {
		if (DEBUG)
		    System.out.println ("removing method: " + __m);
		this.remove_guarded_block (__m);
		__target.removeMethod (__m);
	    }
	    else {
		// I have to compare returned type of both methods
		boolean __ret_differ = false;
		if (!__s.getReturn ().equals (__m.getReturn ())) {
		    // int this case I have to remove guarded block from updated element
		    // because gblock name is generated from method name, its return type
		    // name and all params type names - so after updating I'm not able to 
		    // find the right g block for this element then generator try to create
		    // new one but JavaEditor throw IlegalArgumentException because
		    // there is already created g block but with different name
		    // so I decided to remove g block now and all thing will go well
		    this.remove_guarded_block (__m);
		    if (DEBUG)
			System.out.println ("different return types: " + __m.getReturn ()
					    + " -> " + __s.getReturn ());
                    if (Type.VOID == __m.getReturn() || Type.VOID == __s.getReturn())
                        __ret_differ = true;
		    __m.setReturn (__s.getReturn ());
		}
		// I have to check method params - the user can change some param name
		MethodParameter[] __tm_params = __m.getParameters ();
		MethodParameter[] __sm_params = __s.getParameters ();
		boolean __params_differ = false;
		for (int __i=0; __i<__sm_params.length; __i++) {
		    if (!__tm_params[__i].compareTo (__sm_params[__i], false, false)) {
			__params_differ = true;
			break;
		    }
		}
		if (__params_differ) {
		    if (DEBUG)
			System.out.println ("params differ");
		    __m.setParameters (__s.getParameters ());
		}
		// This method can be in source but might not use delegation
		// => I have to check if this method uses delegation
		if (__s.getBody ().indexOf (__comment) == -1) {
		    // __s method doesn't use delegation
		    if (DEBUG) {
			System.out.println ("rewriting delegation method into "
					    + "non-delegated");
			System.out.println (__m);
			System.out.println ("->");
			System.out.println (__s);
		    }
		    this.remove_guarded_block (__m);
		    __m.setBody (__s.getBody ());
		}
		else {
		    // this method can use different style of delegation
		    // so I have to test if these methods call the same method
		    // on the same object
		    String __s_field_name = this.get_field_name_from_method_body
			(__s.getBody (), __source);
		    String __t_field_name = this.get_field_name_from_method_body
			(__m.getBody (), __target);
		    String __s_call_name = this.get_method_name_from_method_body
			(__s.getBody (), __source);
		    String __t_call_name = this.get_method_name_from_method_body
			(__m.getBody (), __target);
		    if (DEBUG) {
			System.out.println ("__s_field_name: `" + __s_field_name + "'");
			System.out.println ("__t_field_name: `" + __t_field_name + "'");
			System.out.println ("__s_call_name: `" + __s_call_name + "'");
			System.out.println ("__t_call_name: `" + __t_call_name + "'");
		    }
		    if ((!__s_field_name.equals (__t_field_name))
			|| (!__s_call_name.equals (__t_call_name))
			|| __params_differ
                        || __ret_differ) {
			if (DEBUG)
			    System.out.println ("different call");
			__m.setBody (__s.getBody ());
		    }
		}
	    }
	}
	Iterator __s_iter = __s_delegation_methods.iterator ();
	while (__s_iter.hasNext ()) {
	    MethodElement __m = (MethodElement)__s_iter.next ();
	    MethodElement __s = null;
	    if ((__s = __target.getMethod (__m.getName (), this.method_parameters2types
					   (__m.getParameters ()))) == null) {
		if (DEBUG)
		    System.out.println ("adding method: " + __m);
		__target.addMethod (__m);
	    }
	    else {
		// I have to test if this method is user defined or generated
		// in case of user defined method I have to remove it from
		// the list of elements for guarding
		if (__s.getBody ().indexOf (__comment) == -1) {
		    if (DEBUG)
			System.out.println ("removing method from guarding: " + __s);
		    Iterator __i_guard = _M_elements_for_guard_blocks.iterator ();
		    Object __rm_object = null;
		    while (__i_guard.hasNext ()) {
			MemberElement __me = (MemberElement)__i_guard.next ();
			if (__me instanceof MethodElement) {
			    MethodElement __mm_method = (MethodElement)__me;
			    if (__s.getName ().equals (__mm_method.getName ())) {
				if (__s.getReturn ().equals (__mm_method.getReturn ())) {
				    if (this.compare_methods_parameters
					(__s.getParameters (),
					 __mm_method.getParameters ())) {
					if (DEBUG)
					    System.out.println ("found: " + __mm_method);
					//_M_elements_for_guard_blocks.remove (__mm_method);
					__rm_object = __mm_method;
				    }
				}
			    }
			}
		    }
		    if (DEBUG)
			System.out.println ("before: " + _M_elements_for_guard_blocks);
		    if (__rm_object != null)
			_M_elements_for_guard_blocks.remove (__rm_object);
		    //_M_elements_for_guard_blocks.remove (__s);
		    if (DEBUG)
			System.out.println ("after: " + _M_elements_for_guard_blocks);
		}
	    }
	}
    }


    private void synchronize_set_parent_methods (ClassElement __source,
						 ClassElement __target)
	throws SourceException {
	if (DEBUG)
	    System.out.println ("synchronize_set_parent_methods");
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	MethodElement[] __t_methods = __target.getMethods ();
	MethodElement[] __s_methods = __source.getMethods ();
	List __t_sp_methods = new ArrayList ();
	List __s_sp_methods = new ArrayList ();
	String __comment = ImplGenerator.SET_PARENT_METHOD_COMMENT;
	__comment = __comment.substring (1, __comment.length () - 1);
	//if (DEBUG)
	//System.out.println ("__comment: `" + __comment + "'");
	for (int __i=0; __i<__t_methods.length; __i++) {
	    //if (DEBUG) {
	    //System.out.println ("target method: " + __t_methods[__i]);
	    //System.out.println ("target method body: `" + __t_methods[__i].getBody ()
	    //+ "'");
	    //}
	    if ((__t_methods[__i].getBody ().indexOf (__comment)) > -1
		&& __t_methods[__i].getName ().getName ().startsWith
		(ImplGenerator.SET_PARENT_METHOD_PREFIX))
		__t_sp_methods.add (__t_methods[__i]);
	}
	for (int __i=0; __i<__s_methods.length; __i++) {
	    if ((__s_methods[__i].getBody ().indexOf (__comment)) > -1
		&& __s_methods[__i].getName ().getName ().startsWith
		(ImplGenerator.SET_PARENT_METHOD_PREFIX))
		__s_sp_methods.add (__s_methods[__i]);
	}
	if (DEBUG) {
	    System.out.println ("target set parents methods: " + __t_sp_methods);
	    System.out.println ("source set parents methods: " + __s_sp_methods);
	}
	Iterator __t_sp_iter = __t_sp_methods.iterator ();
	while (__t_sp_iter.hasNext ()) {
	    MethodElement __m = (MethodElement)__t_sp_iter.next ();
	    MethodElement __s = null;
	    if ((__s = __source.getMethod (__m.getName (), this.method_parameters2types
					   (__m.getParameters ()))) == null) {
		if (DEBUG)
		    System.out.println ("removing method: " + __m);
		this.remove_guarded_block (__m);
		__target.removeMethod (__m);
	    }
	}
	Iterator __s_sp_iter = __s_sp_methods.iterator ();
	while (__s_sp_iter.hasNext ()) {
	    MethodElement __m = (MethodElement)__s_sp_iter.next ();
	    MethodElement __s = null;
	    if ((__s = __target.getMethod (__m.getName (), this.method_parameters2types
					   (__m.getParameters ()))) == null) {
		if (DEBUG)
		    System.out.println ("adding method: " + __m);
		__target.addMethod (__m);
	    }
	}
    }


    private void remove_all_guarded_blocks (ClassElement __dest) {
	JavaEditor __editor = null; 
	try {
	    JavaDataObject __jdo = (JavaDataObject)__dest.getSource ().getCookie 
		(JavaDataObject.class);
	    __editor = (JavaEditor)__jdo.getCookie (JavaEditor.class);
	    __editor.openDocument ();
	} catch (Exception __ex) {
	    __ex.printStackTrace ();
	}
	Assertion.assert (__editor != null && __editor.getDocument () != null);
	if (DEBUG) {
	    System.out.println ("__dest: " + __dest);
	    System.out.println ("__editor: " + __editor);
	}
	Iterator __iterator = __editor.getGuardedSections ();
	while (__iterator.hasNext ()) {
	    JavaEditor.SimpleSection __section 
		= (JavaEditor.SimpleSection)__iterator.next ();
	    if (!__section.removeSection ())
		Assertion.assert (false);
	}
    }


    private void synchronize_implementations (ClassElement __source, 
					      ClassElement __target) {
	
	if (DEBUG) {
	    System.out.println ("orig class: " + __source.toString ()); // NOI18N
	    System.out.println ("new class: " + __target.toString ()); // NOI18N
	}	
	if (_M_settings.getSynchro () != ORBSettingsBundle.SYNCHRO_DISABLED) {
	    if (!_M_settings.getUseGuardedBlocks ()) {
		// remove all guarded blocks
		this.remove_all_guarded_blocks (__target);
	    }
	    // synchronize class headers
	    if (!this.TIE) {
		// impl-base skeletons
		org.openide.src.Identifier __source_id = __source.getSuperclass ();
		org.openide.src.Identifier __target_id = __target.getSuperclass ();
		//System.out.println ("try to synchornize classes headers");
		try {
		    //System.out.println ("before: __target.getSupperClass () -> "
		    //+ __target.getSuperclass ());
		    __target.setSuperclass (__source_id);
		    //System.out.println ("after: __target.getSupperClass () -> "
		    //+ __target.getSuperclass ());
		    
		} catch (SourceException __ex) {
		    __ex.printStackTrace ();
		}
		this.removeTIEInterfaces (__target);
	    }
	    else {
		// tie-based skeletons
		this.removeTIEInterfaces (__target);
		this.removeImplBaseSkeletons (__target);
		try {
		    __target.addInterface (__source.getInterfaces ()[0]);
		} catch (SourceException __ex) {
		    __ex.printStackTrace ();
		}
	    }
	    try {
		// synchornize classes fields
		//this.remove_delegation_fields (__target);
		this.synchronize_delegation_fields (__source, __target);		
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    try {
		// synchornize set parent methods
		this.synchronize_set_parent_methods (__source, __target);		
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    try {
		// synchronize init methods
		this.synchronize_init_methods (__source, __target);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    try {
		// synchronize constructor
		this.synchronize_constructors (__source, __target);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    try {
		// synchronize delegation methods
		this.synchronize_delegation_methods (__source, __target);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }

	    List __changes = new LinkedList ();
	    JavaConnections.compareMethods 
		(__target, __source, __changes, CORBASupport.ADD_METHOD,
		 CORBASupport.UPDATE_METHOD);
	    //System.out.println ("__changes: " + __changes);
	    if (__changes.size () > 0)
		JavaConnections.showChangesDialog 
		    (__changes, (byte)JavaConnections.TYPE_ALL);
	}
	else {
	    this.showMessage = false;
	}
    }


    private void write_implementation (ClassElement __clazz, String __package,
				       String __impl_name, String __id, 
				       FileObject __folder) throws java.io.IOException {
	final String __final_package = __package;
	final String __final_impl_name = __impl_name;
	final FileObject __final_folder = __folder;
	final String __repo_id = __id;
	final ClassElement __final_clazz = __clazz;
	__folder.getFileSystem ().runAtomicAction 
	    (new org.openide.filesystems.FileSystem.AtomicAction () {
		    public void run () throws java.io.IOException {
			final FileObject __final_impl = __final_folder.createData
			    (__final_impl_name, "java"); // NOI18N
			FileLock lock = null;
                        try {
                            lock =__final_impl.lock ();
                            PrintStream printer = new PrintStream
                                (__final_impl.getOutputStream (lock));

                            // add comment
                            printer.println ("//\n// This file was generated from "
					 + _M_ido.getPrimaryFile ().getName () + ".idl\n"
					 + "//"); // NOI18N
	  
                            //printer.println ("\n");
                            if (__final_package.length() > 0) {
                                // If it isn't in file system root
                                printer.println ("\npackage " + // NOI18N
					     __final_package // NOI18N
					     + ";\n"); // NOI18N
                            }
                            printer.println("//");
                            printer.println("// " + __repo_id);
                            printer.println("//\n");
                            
                            printer.println(__final_clazz.toString());
                            
                            printer.close();
                            _M_generated_impls.add (__final_impl);
			} finally {
                            if (lock != null)
                                lock.releaseLock ();
                        }
		    }
		});
	//return __result;
    }


    private void work_with_guarded_blocks (int __action, String __full_impl_name) {
	if (_M_elements_for_guard_blocks.size () > 0) {
	    if (DEBUG)
		System.out.println ("elements for guarded blocks: " 
				    + _M_elements_for_guard_blocks);
	    JavaEditor __editor = null; 
	    ClassElement __dest = null;
	    try {
		__dest = ClassElement.forName (__full_impl_name);
		JavaDataObject __jdo = (JavaDataObject)__dest.getSource ().getCookie 
		    (JavaDataObject.class);
		__editor = (JavaEditor)__jdo.getCookie (JavaEditor.class);
		__editor.openDocument ();
	    } catch (Exception __ex) {
		__ex.printStackTrace ();
	    }
	    Assertion.assert (__editor != null && 
			      __editor.getDocument () != null &&
			      _M_elements_for_guard_blocks != null);
	    if (DEBUG) {
		System.out.println ("__dest: " + __dest);
		System.out.println ("__editor: " + __editor);
	    }
	    if (__action == ImplGenerator.CREATE_SECTION) {
		Iterator __iterator = _M_elements_for_guard_blocks.iterator ();
		while (__iterator.hasNext ()) {
		    MemberElement __guarded_element = (MemberElement)__iterator.next ();
		    Assertion.assert (__guarded_element != null);		
		    this.work_with_guarded (__action, __dest, __guarded_element, __editor);
		}
	    }
	    if (__action == ImplGenerator.REMOVE_SECTION) {
		Iterator __iterator = __editor.getGuardedSections ();
		while (__iterator.hasNext ()) {
		    JavaEditor.SimpleSection __section 
			= (JavaEditor.SimpleSection)__iterator.next ();
		    if (!__section.removeSection ())
			Assertion.assert (false);
		}
	    }
	}
    }


    private void create_guarded_blocks (String __full_impl_name) {
	this.work_with_guarded_blocks (ImplGenerator.CREATE_SECTION, __full_impl_name);
    }


    private void remove_guarded_blocks (String __full_impl_name) {
	this.work_with_guarded_blocks (ImplGenerator.REMOVE_SECTION, __full_impl_name);
    }


    private String generating_message (String __msg) {
	if (__msg.startsWith ("."))
	    return __msg.substring (1);
	return __msg;
    }


    public MethodElement factory2java (InitDclElement __factory)
	throws SymbolNotFoundException {
	
	if (DEBUG)
	    System.out.println ("factory2java"); // NOI18N
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        IDLElement __parent_element = __factory.getParent ();
        //Type __rettype = this.type2java (__operation.getReturnType (), Parameter.IN, 
	//__package, __parent_element);
	Type __rettype = Type.createClass
	    (org.openide.src.Identifier.create (this.element2java_name (__parent_element, "",
							"")));
	MethodElement __method = new MethodElement ();
        try {
            if (DEBUG)
                System.out.println ("::id11 " + __factory.getName ()); // NOI18N
            __method.setName (org.openide.src.Identifier.create
			      (this.idl_name2java_name (__factory.getName (), false)));
            __method.setModifiers (Modifier.PUBLIC);
            __method.setReturn (__rettype);
            // parameters and context!!!
	    //Iterator __piter = __factory.getParams ().iterator ();
	    ArrayList __list = __factory.getParams ();
            MethodParameter[] __params = new MethodParameter[__list.size ()];
	    for (int __i=0; __i<__list.size (); __i++) {
		//while (__piter.hasNext ()) {
		//InitParamDeclElement __param = (InitParamDeclElement)__piter.next ();
		InitParamDeclElement __param = (InitParamDeclElement)__list.get (__i);
		/*
		  Type __ptype = this.type2java (__param.getType (), Parameter.IN, 
		  __package, __parent_element);
		*/
		Type __ptype = this.type2java (__param.getType (), Parameter.IN, 
					       __package, __factory);
                __params[__i] = new MethodParameter
		    (this.idl_name2java_name (__param.getName (), false), __ptype, false);
            }
            __method.setParameters (__params);
	    
            this.setBodyOfMethod (__method);
            //__clazz.addMethod (__oper); // now addMethod throws SourceExcetion
        } catch (SourceException e) {
            //e.printStackTrace ();
        }

	return __method;
    }


    private void value_factory2java (ClassElement __clazz, List __factories)
	throws SourceException, SymbolNotFoundException {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("value_factory2java");
	InitDclElement __element = (InitDclElement)__factories.get (0);
	ValueAbsElement __value = (ValueAbsElement)__element.getParent ();
	MethodElement __read_value = new MethodElement ();
	__read_value.setName (org.openide.src.Identifier.create ("read_value"));
	__read_value.setModifiers (Modifier.PUBLIC);
	__read_value.setReturn (Type.createClass (org.openide.src.Identifier.create
						  ("java.io.Serializable")));
	__read_value.setParameters (new MethodParameter[] {
	    new MethodParameter ("__in", Type.createClass
				 (org.openide.src.Identifier.create
				  ("org.omg.CORBA_2_3.portable.InputStream")), false)});
	String __body = "\njava.io.Serializable __tmp = new ";
	__body += this.value2java_impl_name (__value) + " ();\n";
	__body += "return __in.read_value (__tmp);\n";
	__read_value.setBody (__body);

	__clazz.addMethod (__read_value);

	Iterator __fiter = __factories.iterator ();
	while (__fiter.hasNext ()) {
	    __element = (InitDclElement)__fiter.next ();
	    __clazz.addMethod (this.factory2java (__element));
	}

	return;
    }

    private void value_factory2java (List __factories)
	throws SymbolNotFoundException, java.io.IOException {
	//boolean DEBUG=true;
	if (__factories.size () == 0)
	    return;
	InitDclElement __element = (InitDclElement)__factories.get (0);
	ValueAbsElement __value = (ValueAbsElement)__element.getParent ();
	if (DEBUG) {
	    System.out.println ("value_factory2java: " + __element.getName ()); // NOI18N
	    System.out.println ("of value: " + __value.getName ()); // NOI18N
	    System.out.println ("name: " + _M_ido.getPrimaryFile ().getName ()); // NOI18N
	}
	String __super_name = __value.getName () + "ValueFactory";
        String __impl_name = this.VALUE_FACTORY_IMPL_PREFIX + __value.getName ()
	    + this.VALUE_FACTORY_IMPL_POSTFIX;
        String __modules = this.modules2package (__value);
	List __folders = this.modules2list (__value);
	if (DEBUG)
	    System.out.println ("folders: " + __folders);
	FileObject __currect_folder = _M_ido.getPrimaryFile ().getParent ();
	__currect_folder = this.create_folders (__folders, __currect_folder);

        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
	if (!__modules.equals (""))
	    __package += "." + __modules.substring (0, __modules.length () - 1);

        if (DEBUG) {
            System.out.println ("modules:>" + __modules + "<"); // NOI18N
            System.out.println ("package:>" + __package + "<"); // NOI18N
        }

        // print to status line
	if (DEBUG)
	    System.out.println ("Generate " + __package + "." // NOI18N
				+ __impl_name + " ..."); // NOI18N
	String __msg = this.generating_message (__package + "." + __impl_name);
	java.lang.Object[] __arr = new Object[] {__msg};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

        try {
            final ClassElement __clazz = new ClassElement ();
            __clazz.setName (org.openide.src.Identifier.create (__impl_name));
	    __clazz.setModifiers (Modifier.PUBLIC);
	    //__clazz.setSuperclass (org.openide.src.Identifier.create (__super_name));
	    __clazz.setInterfaces (new org.openide.src.Identifier[]
		{org.openide.src.Identifier.create (__super_name)} );

            this.value_factory2java (__clazz, __factories);

	    final FileObject __folder = __currect_folder;
            final FileObject __impl;
	    String __full_impl_name = this.value_factory2java_impl_name (__element);
	    if (DEBUG) {
		System.out.println ("__impl_name: " + __impl_name);
		System.out.println ("__full_impl_name: " + __full_impl_name);
	    }
            if ((__impl = __folder.getFileObject (__impl_name, "java")) != null) { // NOI18N
                if (DEBUG)
                    System.out.println ("file exists"); // NOI18N
                ClassElement __dest = ClassElement.forName (__full_impl_name);
		this.synchronize_implementations (__clazz, __dest);
		_M_generated_impls.add (__dest);
            }
            else {
                if (DEBUG)
                    System.out.println ("file don't exists"); // NOI18N
		String __repo_id = this.element2repo_id (__element);
		this.write_implementation 
		    (__clazz, __package, __impl_name, __repo_id, __folder);
	    }

	    //this.create_guarded_blocks (__full_impl_name);

	} catch (org.openide.src.SourceException e) {
	}
    }


    private ClassElement prepare_implementation_class (IDLElement __element,
						       String __impl_name,
						       String __super_name, boolean __tie)
	throws java.io.IOException {
	//_M_elements_for_guard_blocks = new LinkedList ();

	/*
	  String __impl_name = this.interface2partial_java_impl_name (__element);
	  String __super_name = this.interface2java_impl_super_name (__element);
	*/
	String __modules = this.modules2package (__element);
	/*
	  List __folders = this.modules2list (__element);
	  if (DEBUG)
	  System.out.println ("folders: " + __folders);
	  FileObject __currect_folder = _M_ido.getPrimaryFile ().getParent ();
	  __currect_folder = this.create_folders (__folders, __currect_folder);
	*/
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
	if (!__modules.equals (""))
	    __package += "." + __modules.substring (0, __modules.length () - 1);

        if (DEBUG) {
            System.out.println ("modules:>" + __modules + "<"); // NOI18N
            System.out.println ("package:>" + __package + "<"); // NOI18N
        }
        // print to status line
	if (DEBUG)
	    System.out.println ("Generate " + __package + "." // NOI18N
				+ __impl_name + " ..."); // NOI18N
	String __msg = this.generating_message (__package + "." + __impl_name);
	java.lang.Object[] __arr = new Object[] {__msg};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

	ClassElement __clazz = null;

        try {
            __clazz = new ClassElement ();
            __clazz.setName (org.openide.src.Identifier.create (__impl_name));
	    __clazz.setModifiers (Modifier.PUBLIC);
            if (!__tie)
                __clazz.setSuperclass (org.openide.src.Identifier.create (__super_name));
            else
                __clazz.setInterfaces (new org.openide.src.Identifier[]
		    {org.openide.src.Identifier.create (__super_name)} );
	} catch (org.openide.src.SourceException e) {
	}
	return __clazz;
    }

    private ClassElement prepare_interface_class (InterfaceElement __element)
	throws java.io.IOException {
	_M_elements_for_guard_blocks = new LinkedList ();
	String __impl_name = this.interface2partial_java_impl_name (__element);
	String __super_name = this.interface2java_impl_super_name (__element);
	return this.prepare_implementation_class
	    (__element, __impl_name, __super_name, this.TIE);
    }
    
    private ClassElement prepare_value_class (ValueElement __element)
	throws  java.io.IOException {
	String __impl_name = this.VALUE_IMPL_PREFIX 
	    + this.idl_name2java_name (__element.getName (), false)
	    + this.VALUE_IMPL_POSTFIX;
        String __super_name = this.idl_name2java_name (__element.getName (), false);
	return this.prepare_implementation_class
	    (__element, __impl_name, __super_name, this.TIE);
    }

    private void synchronise_implementation_class (ClassElement __clazz,
						   IDLElement __element,
						   String __impl_name,
						   String __full_impl_name,
						   boolean __create_blocks)
	throws SymbolNotFoundException, java.io.IOException {
	/*
	  String __impl_name = this.interface2partial_java_impl_name (__element);
	  String __super_name = this.interface2java_impl_super_name (__element);
	*/
	String __modules = this.modules2package (__element);
	
	List __folders = this.modules2list (__element);
	if (DEBUG)
	    System.out.println ("folders: " + __folders);
	FileObject __current_folder = _M_ido.getPrimaryFile ().getParent ();
	__current_folder = this.create_folders (__folders, __current_folder);

        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
	if (!__modules.equals ("")) {
	    if (!__package.equals (""))
		__package += ".";
	    __package += __modules.substring (0, __modules.length () - 1);
	}
        if (DEBUG) {
            System.out.println ("modules:>" + __modules + "<"); // NOI18N
            System.out.println ("package:>" + __package + "<"); // NOI18N
        }

	final FileObject __folder = __current_folder;
	final FileObject __impl;
	/*
	  String __full_impl_name = this.interface2java_impl_name (__element);
	*/
	if ((__impl = __folder.getFileObject (__impl_name, "java")) != null) { // NOI18N
	    if (DEBUG)
		System.out.println ("file exists"); // NOI18N
	    ClassElement __dest = ClassElement.forName (__full_impl_name);
	    this.synchronize_implementations (__clazz, __dest);
	}
	else {
	    if (DEBUG)
		System.out.println ("file don't exists"); // NOI18N
	    String __repo_id = this.element2repo_id (__element);
	    this.write_implementation 
		(__clazz, __package, __impl_name, __repo_id, __folder);
	    _M_generated_impls.add (__impl_name);
	}
	if (__create_blocks)
	    this.create_guarded_blocks (__full_impl_name);
	
    }

    private void synchronise_interface_class (ClassElement __class,
					      InterfaceElement __element)
	throws SymbolNotFoundException, DuplicateExceptionException, java.io.IOException {
	String __impl_name = this.interface2partial_java_impl_name (__element);
	String __full_impl_name = this.interface2java_impl_name (__element);
	this.synchronise_implementation_class
	    (__class, __element, __impl_name, __full_impl_name, true);
    }

    private void synchronise_value_class (ClassElement __class, ValueElement __element)
	throws SymbolNotFoundException, DuplicateExceptionException, java.io.IOException {
	String __impl_name = this.VALUE_IMPL_PREFIX 
	    + this.idl_name2java_name (__element.getName (), false)
	    + this.VALUE_IMPL_POSTFIX;
	String __full_impl_name = this.value2java_impl_name (__element);
	this.synchronise_implementation_class
	    (__class, __element, __impl_name, __full_impl_name, false);
    }
    
    private static List element2list_name (IDLElement __element) {
	Assertion.assert (__element != null);
	ArrayList __name = new ArrayList ();
	__name.add (__element.getName ());
	IDLElement __tmp = __element;
	while (__tmp.getParent () != null) {
	    IDLElement __parent = __tmp.getParent ();
	    if (ImplGenerator.is_container (__parent)) {
		__name.add (__parent.getName ());
	    }
	    __tmp = __parent;
	}
	return __name;
    }

    private void check_interface_parents (InterfaceElement __element)
	throws SymbolNotFoundException, UndefinedInterfaceException,
	       CannotInheritFromException {
	//System.out.println ("check_interface_parents: " + __element.getName ());
	Vector __parents = __element.getParents ();
	//ArrayList __parents = this.generic_parents
	//(__element, new InterfaceFilter (), new InterfaceParentsExecutor (), false);
	//System.out.println ("__parents: " + __parents);
	for (int __i=0; __i<__parents.size (); __i++) {
	    String __name = (String)__parents.get (__i);
	    IDLElement __tmp = null;
	    //__tmp = (IDLElement)__parents.get (__i);
	    //System.out.println ("__tmp: " + __tmp);
	    //String __name = __tmp.getName ();
	    __tmp = ImplGenerator.find_element_by_name (__name, __element);
	    if (__tmp == null) {
		throw new SymbolNotFoundException (__name);
	    }
	    IDLElement __tmp_parent = __tmp;
	    __tmp = this.resolve_typedef (__tmp_parent);
	    __name = __tmp.getName ();
	    if (!(__tmp instanceof InterfaceElement)) {
		if (__tmp instanceof InterfaceForwardElement) {
		    throw new UndefinedInterfaceException (__name);
		}
		else {
		    throw new CannotInheritFromException (__name);
		}
	    }
	}
    }

    private void check_value_parents (ValueAbsElement __element)
	throws SymbolNotFoundException, UndefinedValueException,
	       UndefinedInterfaceException, CannotInheritFromException,
	       CannotSupportException {
	Vector __parents = __element.getParents ();
	for (int __i=0; __i<__parents.size (); __i++) {
	    String __name = (String)__parents.get (__i);
	    IDLElement __tmp = null;
	    __tmp = ImplGenerator.find_element_by_name (__name, __element);
	    if (__tmp == null) {
		throw new SymbolNotFoundException (__name);
	    }
	    IDLElement __tmp_parent = __tmp;
	    __tmp = this.resolve_typedef (__tmp_parent);
	    __name = __tmp.getName ();
	    if (!(__tmp instanceof ValueAbsElement)) {
		if (__tmp instanceof ValueForwardElement) {
		    throw new UndefinedValueException (__name);
		}
		else {
		    throw new CannotInheritFromException (__name);
		}
	    }
	}
	if (__element instanceof ValueElement) {
	    ValueElement __value = (ValueElement)__element;
	    Vector __supported = __value.getSupported ();
	    for (int __i=0; __i<__supported.size (); __i++) {
		String __name = (String)__supported.get (__i);
		IDLElement __tmp = null;
		__tmp = ImplGenerator.find_element_by_name (__name, __element);
		if (__tmp == null) {
		    throw new SymbolNotFoundException (__name);
		}
		IDLElement __tmp_parent = __tmp;
		__tmp = this.resolve_typedef (__tmp_parent);
		__name = __tmp.getName ();
		if (!(__tmp instanceof ValueAbsElement
		      || __tmp instanceof InterfaceElement)) {
		    if (__tmp instanceof ValueForwardElement) {
			//System.out.println ("class: " + __tmp.getClass ().getName ());
			throw new UndefinedValueException (__name);
		    }
		    else if (__tmp instanceof InterfaceForwardElement) {
			throw new UndefinedInterfaceException (__name);
		    }
		    else {
			throw new CannotSupportException (__name);
		    }
		}
	    }
	}
    }

    private void generate_type (IDLElement __element) throws Exception {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("generate_type (" + __element + ");");
	if (DEBUG)
	    System.out.println ("_S_symbol_table: " + _S_symbol_table);
	if (ImplGenerator.is_constructed_type (__element.getMember (0))) {
	    if (DEBUG)
		System.out.println ("c1");
	    List __name = ImplGenerator.element2list_name (__element.getMember (0));
	    Collections.reverse (__name);
	    if (_S_symbol_table.add_element (__name, __element.getMember (0)) != null)
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
	    if (ImplGenerator.is_struct (__element.getMember (0))) {
		this.generate_from_element (__element.getMember (0));
	    }
	    Vector __t_members = __element.getMembers ();
	    for (int __i=1; __i<__t_members.size (); __i++) {
		IDLElement __t_element = (IDLElement)__t_members.get (__i);
		List __t_name = this.element2list_name (__t_element);
		Collections.reverse (__t_name);
		if (_S_symbol_table.add_element (__t_name, __t_element) != null)
		    throw new AlreadyDefinedSymbolException
			(ImplGenerator.list2absolute_scope_name (__t_name));
	    }
	    return;
	}
	if (ImplGenerator.is_declarator (__element.getMember (0))) {
	    if (DEBUG)
		System.out.println ("d1");
	    List __name = this.element2list_name (__element.getMember (0));
	    Collections.reverse (__name);
	    if (_S_symbol_table.add_element (__name, __element.getMember (0)) != null)
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
	}
	if (ImplGenerator.is_struct (__element)) {
	    if (DEBUG)
		System.out.println ("struct (" + __element + ");");
	    List __name = this.element2list_name (__element);
	    Collections.reverse (__name);
	    if (DEBUG)
		System.out.println ("__name: " + __name);
	    if (_S_symbol_table.add_element (__name, __element) != null)
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
	    this.generate_from_element (__element);
	    Vector __t_members = __element.getMembers ();
	    for (int __i=1; __i<__t_members.size (); __i++) {
		IDLElement __t_element = (IDLElement)__t_members.get (__i);
                if (ImplGenerator.is_declarator (__t_element)) {
  		    List __t_name = this.element2list_name (__t_element);
		    Collections.reverse (__t_name);
		    if (_S_symbol_table.add_element (__t_name, __t_element) != null)
		        throw new AlreadyDefinedSymbolException
			    (ImplGenerator.list2absolute_scope_name (__t_name));
                }
	    }
	    return;
	}
	/*
	  if (ImplGenerator.is_declarator (__element)) {
	  //System.out.println ("d2");
	  List __name = this.element2list_name (__element);
	  Collections.reverse (__name);
	  if (_S_symbol_table.add_element (__name, __element) != null)
	  throw new AlreadyDefinedSymbolException
	  (ImplGenerator.list2absolute_scope_name (__name));
	  }
	*/
	if (__element.getMembers ().size () > 1) {
	    if (DEBUG)
		System.out.println ("ld");
	    // last declarator	    
	    // typedef x y;
	    // test if x is defined
            if (!ImplGenerator.is_declarator (__element.getMember (0))) {
	        TypeElement __p_element = (TypeElement)__element.getMember(0).getParent ();
	    /*
	      List __p_name = this.element2list_name (__p_element);
	      Collections.reverse (__p_name);
	      IDLElement __found = _S_symbol_table.get_element (__p_name);
	    */
	        IDLElement __found = ImplGenerator.find_element_by_type
		    (__p_element.getType (), __p_element);
	        if (__found == null)
		    throw new SymbolNotFoundException (__p_element.getName ());
            }
	    Vector __t_members = __element.getMembers ();
	    for (int __i=1; __i<__t_members.size (); __i++) {
		IDLElement __t_element = (IDLElement)__t_members.get (__i);
                if (ImplGenerator.is_declarator (__t_element)) {
		    List __t_name = this.element2list_name (__t_element);
		    Collections.reverse (__t_name);
		    if (_S_symbol_table.add_element (__t_name, __t_element) != null)
		        throw new AlreadyDefinedSymbolException
			    (ImplGenerator.list2absolute_scope_name (__t_name));
                }
	    }
/*	    List __name = this.element2list_name (__last_element);
	    Collections.reverse (__name);
//  	    System.out.println ("_S_symbol_table: " + _S_symbol_table);
//  	    System.out.println ("__name: " + __name);
//  	    System.out.println ("_S_symbol_table.get_element (__name): "
//  				+ _S_symbol_table.get_element (__name)); 
	    if (_S_symbol_table.add_element (__name, __last_element) != null)
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
 */
	}
    }

    private void generate_exception (IDLElement __element)
	throws AlreadyDefinedSymbolException {
	if (DEBUG)
	    System.out.println ("generate_exception (" + __element + ");");
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	if (_S_symbol_table.add_element (__name, __element) != null)
	    throw new AlreadyDefinedSymbolException
		(ImplGenerator.list2absolute_scope_name (__name));
    }

    private void generate_interface (IDLElement __element) throws Exception {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("generate_interface (" + __element + ");");
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	InterfaceElement __interface = (InterfaceElement)__element;
	this.check_interface_parents (__interface);
	IDLElement __orig_element = null;
	if ((__orig_element = _S_symbol_table.add_element (__name, __element)) != null)
	    if (!(__orig_element instanceof InterfaceForwardElement))
		throw new AlreadyDefinedSymbolException (list2absolute_scope_name (__name));
	if (__interface.isAbstract ())
	    return;
	if (!__interface.getFileName ().equals (_M_file_name)) {
	    _M_working_class = null;
	    // I have to generate all names inside interface into symbol table
	    this.generate_from_element (__element);
	    return;
	}
	try {
	    _M_working_class = this.prepare_interface_class (__interface);
	} catch (java.io.IOException __ex) {
	    __ex.printStackTrace ();
	}
	this.generate_from_element (__element);
	/*
	  List __parents = this.generic_parents (__interface, new InterfaceFilter (),
	  new InterfaceParentsExecutor (), false);
	  Iterator __iter = __parents.iterator ();
	  while (__iter.hasNext ()) {
	  IDLElement __t_parent = (IDLElement)__iter.next ();
	  this.generate_from_element (__t_parent);
	  }
	*/
	// method(s) from all inherited abstract parent(s)
	//ArrayList __abstract_parents = this.all_abstract_parents (__interface);
	//if (DEBUG)
	//  System.out.println ("all abstract parents: " + __abstract_parents);
	//Iterator __iterator = __abstract_parents.iterator ();
	//ArrayList __diapp = this.directly_implemented_interfaces (__interface);
	//if (DEBUG)
	//    System.out.println ("directly_implemented_interfaces: " + __diapp);
	List __own_methods = this.get_elements_from_element 
	    (__interface, new OperationFilter (), false);
	ArrayList __dioper = this.directly_implemented_methods (__interface);
	// own methods have been already generated
	__dioper.removeAll (__own_methods);
	if (DEBUG)
	    System.out.println ("directly_implemented_methods: " + __dioper);
	this.generate_methods (_M_working_class, __dioper);
	ArrayList __all_implemented_interfaces 
	    = this.all_implemented_interfaces (__interface);
	if (DEBUG)
	    System.out.println ("all interfaces: " + __all_implemented_interfaces);
	Iterator __iterator = __all_implemented_interfaces.iterator ();
	ArrayList __methods_map = new ArrayList ();
	ArrayList __all_methods_map = new ArrayList ();
	while (__iterator.hasNext ()) {
	    InterfaceElement __tmp_interface = (InterfaceElement)__iterator.next ();
	    ArrayList __methods = this.directly_implemented_methods (__tmp_interface);
	    ArrayList __all_methods = this.all_implemented_methods (__tmp_interface);
	    __methods_map.add (new Pair (__tmp_interface, __methods));
	    __all_methods_map.add (new Pair (__tmp_interface, __all_methods));
	}
	if (DEBUG) {
	    System.out.println ("methods map: " + __methods_map);
	    System.out.println ("all methods map: " + __all_methods_map);
	}
	ArrayList __all_oper = this.all_implemented_methods (__interface);
	__all_oper.removeAll (__own_methods);
	ArrayList __delegated_operations = new ArrayList (__all_oper);
	__delegated_operations.removeAll (__dioper);
	if (DEBUG) {
	    System.out.println ("all_implemented_methods: " + __all_oper);
	    System.out.println ("all delegated method: " + __delegated_operations);
	}
        // parents...
	ArrayList __parents = this.all_concrete_parents (__interface);
	if (DEBUG)
	    System.out.println ("__parents: " + __parents);
	
	if (_M_delegation.equals (ORBSettingsBundle.DELEGATION_NONE)) {
	    // don't delegate
	    this.generate_methods (_M_working_class, __delegated_operations);
	}
	else {
	    // use delegation
	    List __dependency_map = new ArrayList ();
	    __iterator = __parents.iterator ();
	    while (__iterator.hasNext ()) {
		InterfaceElement __int = (InterfaceElement)__iterator.next ();
		__dependency_map.add (new Pair (__int, this.all_concrete_parents (__int)));
	    }
	    //System.out.println ("all_parents of " + __interface + " : " + __parents);
	    //TreeSet __sorted_map = new TreeSet (new InheritanceDependencyComparator ());
	    //__sorted_map.addAll (__dependency_map);
	    Collections.sort (__dependency_map, new InheritanceDependencyComparator ());
	    if (DEBUG)
		System.out.println ("__sorted_map for " + __interface.getName ()
				    + " : " + __dependency_map);
	    __iterator = __dependency_map.iterator ();
	    String __body_of_init = ImplGenerator.SET_PARENT_METHOD_COMMENT;
	    ArrayList __setter_methods = new ArrayList ();
	    while (__iterator.hasNext ()) {
		Pair __pair = (Pair)__iterator.next ();
		InterfaceElement __tmp = (InterfaceElement)__pair.first;
		ArrayList __set_of_parents = (ArrayList)__pair.second;
		Iterator __iter = __set_of_parents.iterator ();
		String __parent = this.interface2java_impl_name (__tmp);
		if (DEBUG)
		    System.out.println ("parent: " + __parent);
		try {
		    FieldElement __field = this.create_field_for_parent (__tmp);
		    MethodElement __method 
			= this.create_set_method_for_parent (__tmp, __field);
		    //__clazz.addField (__field);
		    this.add_element (_M_working_class, __field);
		    __body_of_init += __field.getName () + " = new " + __parent + " ();\n";
		    if (__set_of_parents.size () > 0) {
			__body_of_init += "// set parents for " 
			    + this.element2repo_id (__tmp) + "\n";
			while (__iter.hasNext ()) {
			    InterfaceElement __tmp_parent
				= (InterfaceElement)__iter.next ();
			    String __name_of_class = __field.getName ().getName ();
			    String __name_of_method 
				= this.create_set_method_name_for_parent (__tmp_parent);
			    String __name_of_parent = this.create_field_name_for_parent 
				(__tmp_parent);
			    __body_of_init += __name_of_class + "." + __name_of_method
				+ " (" + __name_of_parent + ");\n";
			}
		    }
		    __body_of_init += "\n";
		    //__clazz.addMethod (__method);
		    __setter_methods.add (__method);
		} catch (SourceException __ex) {
		    __ex.printStackTrace ();
		}
	    }
	    try {
		ConstructorElement __constructor = new ConstructorElement ();
		__constructor.setModifiers (Modifier.PUBLIC);
		__constructor.setBody ("\nthis." + ImplGenerator.INITIALIZE_INHERITANCE_TREE
				       + " ();\n");
		_M_working_class.addConstructor (__constructor);
		//this.add_element (_M_working_class, __constructor);
		MethodElement __init = new MethodElement ();
		__init.setName (org.openide.src.Identifier.create 
				(ImplGenerator.INITIALIZE_INHERITANCE_TREE));
		__init.setParameters (new MethodParameter[0]);
		__init.setBody (__body_of_init);
		__init.setReturn (Type.VOID);
		__init.setModifiers (Modifier.PUBLIC);
		//__clazz.addMethod (__init);
		this.add_element (_M_working_class, __init);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    __iterator = __setter_methods.iterator ();
	    while (__iterator.hasNext ()) {
		//try {
		//__clazz.addMethod ((MethodElement)__iterator.next ());
		this.add_element (_M_working_class, (MethodElement)__iterator.next ());
		//} catch (SourceException __ex) {
		//__ex.printStackTrace ();
		//}
	    }
	    boolean __use_virtual_delegation = false;
	    if (_M_delegation.equals (ORBSettingsBundle.DELEGATION_VIRTUAL))
		__use_virtual_delegation = true;
	    if (DEBUG) {
		System.out.println ("_M_working_class.getName (): " + _M_working_class.getName ());
		System.out.println ("__interface.getName (): " + __interface.getName ());
		System.out.println ("__delegated_operations: " + __delegated_operations);
		System.out.println ("__methods_map: " + __methods_map);
		System.out.println ("__all_methods_map: " + __all_methods_map);
		System.out.println ("__use_virtual_delegation: " + __use_virtual_delegation);
	    }
	    this.generate_methods_for_parent (_M_working_class, __interface,
					      __delegated_operations,
					      __methods_map, __all_methods_map, 
					      __use_virtual_delegation);
	}

	this.synchronise_interface_class (_M_working_class, __interface);
	_M_working_class = null;
    }

    private void generate_concrete_value (IDLElement __element) throws Exception {
	//boolean DEBUG=true;
	if (DEBUG)
	    System.out.println ("generate_concrete_value: " + __element.getName ());
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	ValueElement __value = (ValueElement)__element;
	this.check_value_parents (__value);
	IDLElement __orig_element = null;
	if ((__orig_element = _S_symbol_table.add_element (__name, __element)) != null)
	    if (!(__orig_element instanceof ValueForwardElement))
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
	if (!__value.getFileName ().equals (_M_file_name)) {
	    _M_working_class = null;
	    // I have to generate all names inside interface into symbol table
	    this.generate_from_element (__element);
	    return;
	}
	try {
	    _M_working_class = this.prepare_value_class (__value);
	} catch (java.io.IOException __ex) {
	    __ex.printStackTrace ();
	}	
	this.generate_from_element (__element);

	ConstructorElement __constructor = new ConstructorElement ();
	__constructor.setModifiers (Modifier.PUBLIC);
	__constructor.setBody ("\n");
	_M_working_class.addConstructor (__constructor);

	List __all_parents_and_supported_interfaces
	    = this.all_parents_and_supported_interfaces (__value);
	if (DEBUG)
	    System.out.println ("all inherited values and supported interfaces: " 
				+ __all_parents_and_supported_interfaces);
	List __operations = new ArrayList ();
	Iterator __iterator = __all_parents_and_supported_interfaces.iterator ();
	while (__iterator.hasNext ()) {
	    IDLElement __tmp_element = (IDLElement)__iterator.next ();
	    __operations.addAll (this.get_elements_from_element (__tmp_element, 
								 new OperationFilter (),
								 false));
	}
	if (DEBUG)
	    System.out.println ("all implemented operations: " + __operations);
	//List __operations = this.get_elements_from_element 
	//(__value, new OperationFilter (), false);
	this.generate_methods (_M_working_class, __operations);
	if (__value.isCustom ()) {
	    // create methods for custom marshaling
	    MethodElement __marshal = new MethodElement ();
	    __marshal.setName (org.openide.src.Identifier.create ("marshal"));
	    __marshal.setModifiers (Modifier.PUBLIC);
	    __marshal.setReturn (Type.VOID);
	    __marshal.setParameters (new MethodParameter[] {
		new MethodParameter
		    ("os", this.create_type_from_name ("org.omg.CORBA.DataOutputStream", 0),
		     false) });
	    __marshal.setBody ("\n");
	    _M_working_class.addMethod (__marshal);
	    MethodElement __unmarshal = new MethodElement ();
	    __unmarshal.setName (org.openide.src.Identifier.create ("unmarshal"));
	    __unmarshal.setModifiers (Modifier.PUBLIC);
	    __unmarshal.setReturn (Type.VOID);
	    __unmarshal.setParameters (new MethodParameter[] {
		new MethodParameter
		    ("is", this.create_type_from_name ("org.omg.CORBA.DataInputStream", 0),
		     false) });
	    __unmarshal.setBody ("\n");
	    _M_working_class.addMethod (__unmarshal);
	}
	this.synchronise_value_class (_M_working_class, __value);
	_M_working_class = null;
	// generate factories
	List __factories = ImplGenerator.get_all_factories (__value);
	if (__factories.size () > 0) {
	    String __factory_super_name = __value.getName () + "ValueFactory";
	    String __factory_impl_name = this.VALUE_FACTORY_IMPL_PREFIX + __value.getName ()
		+ this.VALUE_FACTORY_IMPL_POSTFIX;
	    String __factory_full_impl_name = this.value_factory2java_impl_name
		((InitDclElement)__factories.get (0));
	    ClassElement __factory_class = this.prepare_implementation_class
		(__value, __factory_impl_name, __factory_super_name, true);
	    this.value_factory2java (__factory_class, __factories);
	    this.synchronise_implementation_class
		(__factory_class, __value, __factory_impl_name,
		 __factory_full_impl_name, false);
	}
    }

    private void generate_abstract_value (IDLElement __element) throws Exception {
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	ValueAbsElement __value = (ValueAbsElement)__element;
	this.check_value_parents (__value);
	IDLElement __orig_element = null;
	if ((__orig_element = _S_symbol_table.add_element (__name, __element)) != null)
	    if (!(__orig_element instanceof ValueForwardElement))
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
	//if (_M_working_class != null) {
	//    this.generate_from_element (__element);
	//}
    }

    private void generate_valuebox (IDLElement __element)
	throws AlreadyDefinedSymbolException {
	if (DEBUG)
	    System.out.println ("generate_valuebox (" + __element + ");");
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	IDLElement __orig_element = null;
	if ((__orig_element = _S_symbol_table.add_element (__name, __element)) != null)
	    if (!(__orig_element instanceof ValueForwardElement))
		throw new AlreadyDefinedSymbolException
		    (ImplGenerator.list2absolute_scope_name (__name));
    }

    private void generate_operation (IDLElement __element) throws Exception {
	if (DEBUG)
	    System.out.println ("generate_operation (" + __element + ");");
	//System.out.println ("_S_symbol_table: " + _S_symbol_table);
	List __name = ImplGenerator.element2list_name (__element);
	Collections.reverse (__name);
	if (_S_symbol_table.add_element (__name, __element) != null)
	    throw new AlreadyDefinedSymbolException
		(ImplGenerator.list2absolute_scope_name (__name));
	if (_M_working_class != null) {
	    OperationElement __oper = (OperationElement)__element;
	    MethodElement __method = this.operation2java (__oper);
            MethodElement[] methods = _M_working_class.getMethods();
            for (int i=0; i< methods.length; i++) {
                if (methods[i].getName().getName().equals( __method.getName().getName()))
                    throw new OperationAlreadyDefinedException (__method.getName().getName());
            }
	    _M_working_class.addMethod (__method);
	}
    }

    private void generate_attribute (IDLElement __element) throws Exception {
	if (DEBUG)
	    System.out.println ("generate_attribute (" + __element + ");");
	if (_M_working_class != null) {
	    AttributeElement __attr = (AttributeElement)__element;
	    MethodElement[] __methods = this.attribute2java (__attr);
            MethodElement[] methods = _M_working_class.getMethods();
            for (int i=0; i< methods.length; i++) {
                if (methods[i].getName().getName().equals( __methods[0].getName().getName()))
                    throw new OperationAlreadyDefinedException (__methods[0].getName().getName());
            }
	    _M_working_class.addMethods (__methods);
	}
    }

    private void generate_forward_interface (IDLElement __element) throws Exception {
	if (DEBUG)
	    System.out.println ("generate_forward_interface (" + __element + ");");
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	if (_S_symbol_table.add_element (__name, __element) != null)
	    throw new AlreadyDefinedSymbolException
		(ImplGenerator.list2absolute_scope_name (__name));
    }	
    
    private void generate_forward_value (IDLElement __element) throws Exception {
	if (DEBUG)
	    System.out.println ("generate_forward_value (" + __element + ");");
	List __name = this.element2list_name (__element);
	Collections.reverse (__name);
	if (_S_symbol_table.add_element (__name, __element) != null)
	    throw new AlreadyDefinedSymbolException
		(ImplGenerator.list2absolute_scope_name (__name));
    }	

    private void generate_member (IDLElement __element) throws Exception {
	this.generate_from_element (__element);
    }
    /*
      private void generate_value_factory (IDLElement __element) throws Exception {
      if (DEBUG)
      System.out.println ("generate_value_factory (" + __element + ");");
      InitDclElement __init = (InitDclElement)__element;
      _M_working_class.addMethod (this.value_factory2java (__init));
      }
    */
    private void generate_from_element (IDLElement __element) throws Exception {
	//boolean DEBUG=true;
	if (DEBUG) {
	    System.out.println ("generate_from_element (" + __element + ");");
	    System.out.println ("class: " + __element.getClass ().getName ());
	}
	Vector __members = __element.getMembers ();
	for (int __i=0; __i<__members.size (); __i++) {
	    IDLElement __tmp = (IDLElement)__members.get (__i);
	    if (ImplGenerator.is_module (__tmp))
		this.generate_from_element (__tmp);
	    else if (ImplGenerator.is_type (__tmp))
		this.generate_type (__tmp);
	    else if (ImplGenerator.is_exception (__tmp))
		this.generate_exception (__tmp);
	    else if (ImplGenerator.is_interface (__tmp))
		this.generate_interface (__tmp);
	    else if (ImplGenerator.is_concrete_value (__tmp))
		this.generate_concrete_value (__tmp);
	    else if (ImplGenerator.is_abstract_value (__tmp))
		this.generate_abstract_value (__tmp);
	    else if (ImplGenerator.is_valuebox (__tmp))
		this.generate_valuebox (__tmp);
	    else if (ImplGenerator.is_operation (__tmp))
		this.generate_operation (__tmp);
	    else if (ImplGenerator.is_attribute (__tmp))
		this.generate_attribute (__tmp);
	    else if (ImplGenerator.is_forward_interface (__tmp))
		this.generate_forward_interface (__tmp);
	    else if (ImplGenerator.is_forward_value (__tmp))
		this.generate_forward_value (__tmp);
	    else if (ImplGenerator.is_member (__tmp))
		this.generate_member (__tmp);
	    //else if (__tmp instanceof StructTypeElement)
	    //this.generate_struct (__tmp);
	    /*
	      else if (ImplGenerator.is_identifier (__tmp))
		continue;
	      else if (ImplGenerator.is_const (__tmp))
	      continue;
	      else if (ImplGenerator.is_state (__tmp))
	      continue;
	      else if (ImplGenerator.is_init (__tmp))
	      continue;
	      //else if (__tmp instanceof ValueInheritanceSpecElement)
	    */
	    else {
		//this.generate_from_element (__tmp);
		if (DEBUG) {
		    System.out.println ("can't generate for:" + __tmp);
		    System.out.println ("__tmp class: " + __tmp.getClass ().getName ());
		}
		//Assertion.assert (false);
	    }
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
	_M_elements_for_guard_blocks = new LinkedList ();
	_S_symbol_table = new SymbolTable ();

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
	//String __file_name = "";
	try {
	    //String __filesystem = _M_ido.getPrimaryFile ().getFileSystem ().getDisplayName ();
	    //__file_name = __filesystem + File.separator
	    //+ _M_ido.getPrimaryFile ().toString ();
	    _M_file_name = _M_ido.getRealFileName ();
	} catch (FileStateInvalidException __ex) {
	}
	try {
	    this.generate_from_element (_M_src);
	} catch (SymbolNotFoundException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.CANT_FIND_SYMBOL, 
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
	} catch (UndefinedInterfaceException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.UNDEFINED_INTERFACE, 
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
	} catch (UndefinedValueException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.UNDEFINED_VALUE, 
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
	} catch (DuplicateExceptionException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.DUPLICATE_EXCEPTION, 
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
	} catch (CannotInheritFromException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.CANNOT_INHERIT_FROM,
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
	} catch (CannotSupportException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.CANNOT_SUPPORT,
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
        } catch (OperationAlreadyDefinedException opDef) {
            if (!_M_run_testsuite) {
                TopManager.getDefault().notify ( new NotifyDescriptor.Message (MessageFormat.format(NbBundle.getBundle(ImplGenerator.class).getString("TXT_OperationDefined"),new Object[]{opDef.getSymbolName()}),NotifyDescriptor.ERROR_MESSAGE));
            }
            else {
               opDef.printStackTrace();
            }
	} catch (AlreadyDefinedSymbolException __ex) {
	    if (!_M_run_testsuite) {
		java.lang.Object[] __arr = new Object[] {__ex.getSymbolName ()};
		TopManager.getDefault ().notify 
		    (new NotifyDescriptor.Exception 
			(__ex, MessageFormat.format (CORBASupport.ALREADY_DEFINED_SYMBOL,
						     __arr)));
		_M_exception_occured = true;
	    }
	    else {
		__ex.printStackTrace ();
		throw new RuntimeException (); 
	    }
	    if (DEBUG_EXCEPT)
		__ex.printStackTrace ();
        } catch (Exception __x) {
	    if (!_M_run_testsuite)
		TopManager.getDefault ().getErrorManager ().notify (__x);
	    else
		__x.printStackTrace ();
	    if (DEBUG_EXCEPT)
		__x.printStackTrace ();
	}

	if (this.getOpen () && !_M_run_testsuite) {
	    // open all generated classes in IDE Editor
	    Iterator __iterator = _M_generated_impls.iterator ();
	    while (__iterator.hasNext ()) {
		FileObject __fo = null;
		try {
		    Object __object = __iterator.next ();
		    if (__object instanceof FileObject) {
			__fo = (FileObject)__object;
			JavaDataObject __jdo = (JavaDataObject)DataObject.find (__fo);
			OpenCookie __cookie = (OpenCookie)__jdo.getCookie
			    (OpenCookie.class);
			if (__cookie != null)
			    __cookie.open ();
		    }
		    if (__object instanceof Element) {
			Element __element = (Element)__object;
			OpenCookie __cookie = (OpenCookie)__element.getCookie
			    (OpenCookie.class);
			if (__cookie != null)
			    __cookie.open ();
		    }
		} catch (DataObjectNotFoundException __ex) {
		    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
			System.out.println ("can't find " + __fo.toString ()); // NOI18N
		}
	    }
	}
	if (this.showMessage) {
	    if (!this._M_exception_occured) { 
		// Bug Fix, when sync is disabled, don't show the message
		java.lang.Object[] __arr = new Object[]
		{_M_ido.getPrimaryFile ().getName ()};
		TopManager.getDefault ().setStatusText
		(MessageFormat.format (CORBASupport.SUCESS_GENERATED, __arr));
	    }
	    else {
		java.lang.Object[] __arr = new Object[]
		{_M_ido.getPrimaryFile ().getName ()};
		TopManager.getDefault ().setStatusText
		(MessageFormat.format (CORBASupport.GENERATOR_ERROR, __arr));
	    }
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
            Type type = method.getReturn();
            if (type.isPrimitive()) {
                if (type == Type.VOID)
                    method.setBody("\n");
                else if (type == Type.BOOLEAN)
                    method.setBody("\n  return false;\n"); // NOI18N
                else if (type == Type.CHAR)
                    method.setBody("\n  return '\\u0000';\n"); // NOI18N
                else    
                    method.setBody("\n  return 0;\n"); // NOI18N
            }
            else {
                method.setBody ("\n  return null;\n"); // NOI18N
            }
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
