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

import java.lang.reflect.Modifier;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.cookies.OpenCookie;
import org.openide.cookies.SourceCookie;

import org.openide.src.Type;
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

    private String VALUE_IMPL_PREFIX;
    private String VALUE_IMPL_POSTFIX;
    private String VALUE_FACTORY_IMPL_PREFIX;
    private String VALUE_FACTORY_IMPL_POSTFIX;
    
    private int IN_MODULE_PACKAGE = 0;
    private int IN_IDL_PACKAGE = 1;

    private int where_generate = IN_MODULE_PACKAGE;

    private IDLDataObject _M_ido;

    private List _M_generated_impls;

    private List _M_elements_for_guard_blocks;

    private boolean WAS_TEMPLATE = false;
    // this variable indicate if in calling of hasTemplateParent is template type or not
    // => it must be setuped to

    private boolean _M_open = true;
    //private boolean _M_open = false;

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

    private static HashMap _S_java_keywords;
    private static HashMap _S_idl_mapping_names;

    static {


    }

    public ImplGenerator (IDLDataObject _do) {
	try {
        _M_ido = _do;

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
	_S_idl_mapping_names.put ("PackagePackage", __object);
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
	    if (__name.endsWith (__key))
		return true;
	}
	return false;
    }


    public static String idl_name2java_name (String __name) {
	if (DEBUG)
	    System.out.println ("idl_name2java_name () <- " + __name);
	String __result;
	if (__name.startsWith ("_")) { // NOI18N
	    __result = __name.substring (1, __name.length ());
	    if (!ImplGenerator.is_java_keyword (__result)) {
		if (DEBUG)
		    System.out.println ("idl_name2java_name () -> " + __result);
		return __result;
	    }
	}
	if (ImplGenerator.is_idl_mapping_name (__name)
	    ||ImplGenerator.is_java_keyword (__name)) {
	    __result = "_" + __name;
	    if (DEBUG)
		System.out.println ("idl_name2java_name () -> " + __result);
	    return __result;
	}
	else {
	    __result = __name;
	    if (DEBUG)
		System.out.println ("idl_name2java_name () -> " + __result);
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
		    (ImplGenerator.idl_name2java_name (__tmp.getParent ().getName ()));
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
            // transform modules names from list to string in package format
	    // added trsnformating with idl_name2java_name
            for (int __i = __mods.size () - 1; __i>=0; __i--) {
                if (DEBUG)
                    System.out.println ("transfrom: " + (String)__mods.get (__i)); // NOI18N
                __modules = __modules + this.idl_name2java_name ((String)__mods.get (__i))
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
                           IDLElement __element) {
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
        if (DEBUG)
            System.out.println ("-- is exception with absolute scope name"); // NOI18N

        if (this.isAbsoluteScopeName (__ex)) {
            // is absolute scope name
            IDLElement __tmp = this.findTopLevelModuleForName (__ex, __element);
            IDLElement __element_for_exception = this.findElementInElement (__ex, __tmp);
            String __full_name =""; // NOI18N
            if (__package.length() >0) 
                __full_name = __package + "."; // NOI18N
            __full_name += this.ctype2package (__element_for_exception);

            return __full_name;
        }
        if (DEBUG)
            System.out.println ("-- is exception with scope name"); // NOI18N
        if (this.isScopeName (__ex)) {
            IDLElement __tmp = this.findModuleForScopeName (__ex, __element);
            IDLElement __element_for_exception = this.findElementInElement (__ex, __tmp);
            String __full_name =""; // NOI18N
            if (__package.length() >0) 
                __full_name = __package + "."; // NOI18N
            __full_name += this.ctype2package (__element_for_exception);

            return __full_name;

        }
        if (DEBUG)
            System.out.println ("-- is exception with normal name"); // NOI18N
        IDLElement __element_for_exception = this.findElementByName (__ex, __element);
        if (DEBUG)
            System.out.println ("element_for_exception: " + __element_for_exception.getName () + " : " // NOI18N
                                + __element_for_exception);
        String __full_name =""; // NOI18N
	if (__package.length() >0) 
	    __full_name = __package + "."; // NOI18N
	__full_name += this.ctype2package (__element_for_exception);

        return __full_name;
    }


    public MethodElement[] attribute2java (AttributeElement __attr) {
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        IDLElement __parent_element = __attr.getParent ();
        Type __attr_type = this.type2java (__attr.getType (), Parameter.IN, 
					   __package, __parent_element);
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
			     (this.idl_name2java_name (__attr.getName ())));
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
				 (this.idl_name2java_name (__attr.getName ())));
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


    public MethodElement operation2java (OperationElement __operation) {
        if (DEBUG)
            System.out.println ("operation2java"); // NOI18N
        String __package = _M_ido.getPrimaryFile ().getParent ().getPackageName ('.');
        IDLElement __parent_element = __operation.getParent ();
        Type __rettype = this.type2java (__operation.getReturnType (), Parameter.IN, 
					 __package, __parent_element);
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
			    (this.idl_name2java_name (__operation.getName ())));
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
					       __package, __parent_element);
                __params[__i] = new MethodParameter
		    (this.idl_name2java_name (__p.getName ()), __ptype, false);
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
		      __package, __parent_element));
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


    public static List get_all_interfaces (IDLElement __element) {
	//return this.get_interfaces (this._M_src);
	return ImplGenerator.get_elements_from_element (__element, new InterfaceFilter (),
							true);
    }

    
    public List get_all_interfaces () {
	return ImplGenerator.get_all_interfaces (this._M_src);
    }


    public static List get_all_values (IDLElement __element) {
	return ImplGenerator.get_elements_from_element (__element, new ValueFilter (),
							true);
    }


    private List get_all_values () {
	return ImplGenerator.get_all_values (this._M_src);
    }


    public static List get_all_factories (IDLElement __element) {
	return ImplGenerator.get_elements_from_element (__element,
							new ValueFactoryFilter (),
							true);
    }


    private List get_all_factories () {
	return ImplGenerator.get_all_factories (this._M_src);
    }


    private ArrayList generic_parents (IDLElement __element,
				       ObjectFilter[] __filter,
				       ParentsExecutor[] __executor,
				       boolean __recursive)
	throws SymbolNotFoundException {
	Assertion.assert (__element != null && __filter.length == __executor.length);
	
	//Vector __parents = __interface.getParents ();
	ArrayList __result = new ArrayList ();
	ArrayList __parents_for_recursion = new ArrayList ();
	for (int __i=0; __i<__filter.length;__i++) {
	    //System.out.println ("__executor[" + __i + "]: " + __executor[__i]);
	    //System.out.println ("__element: " + __element);
	    List __tmp_parents = __executor[__i].getParents (__element);
	    for (int __j=0; __j<__tmp_parents.size (); __j++) {
		String __name_of_parent = (String)__tmp_parents.get (__j);
		IDLElement __parent
		    = this.findElementByName (__name_of_parent, __element);
		if (__parent == null) {
		    throw new SymbolNotFoundException (__name_of_parent);
		}
		if (__filter[__i].is (__parent)) {
		    __result.add (__parent);
		    if (__recursive)
			__parents_for_recursion.add (__parent);
		}
	    }
	}
	if (__recursive) {
	    Iterator __iterator = __parents_for_recursion.iterator ();
	    while (__iterator.hasNext ()) {
		IDLElement __tmp_element = (IDLElement)__iterator.next ();
		ArrayList __tmp_set = this.generic_parents (__tmp_element, 
							    __filter,
							    __executor,
							    __recursive);
		Iterator __tmp_iterator = __tmp_set.iterator ();
		while (__tmp_iterator.hasNext ()) {
		    Object __object = __tmp_iterator.next ();
		    if (!__result.contains (__object))
			__result.add (__object);
		}
	    }
	}
	return __result;
    }


    private ArrayList generic_parents (IDLElement __element, ObjectFilter __filter,
				       ParentsExecutor __executor, boolean __recursive)
	throws SymbolNotFoundException {
	return this.generic_parents (__element, new ObjectFilter[] {__filter},
				     new ParentsExecutor[] {__executor}, __recursive);
    }


    private ArrayList all_concrete_parents (InterfaceElement __interface)
	throws SymbolNotFoundException {
	return this.generic_parents (__interface, new ConcreteInterfaceFilter (),
				     new InterfaceParentsExecutor (), true);
    }


    private ArrayList all_abstract_parents (InterfaceElement __interface) 
	throws SymbolNotFoundException {
	return this.generic_parents (__interface, new AbstractInterfaceFilter (),
				     new InterfaceParentsExecutor (), true);
    }


    private ArrayList abstract_parents (InterfaceElement __interface) 
	throws SymbolNotFoundException {
	return this.generic_parents (__interface, new AbstractInterfaceFilter (),
				     new InterfaceParentsExecutor (), false);
    }

    
    private ArrayList all_parents_and_supported_interfaces (ValueElement __value) 
	throws SymbolNotFoundException {
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
	throws SymbolNotFoundException {
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
	throws SymbolNotFoundException {
	ArrayList __abstract_parents = this.abstract_parents (__interface);
	ArrayList __concrete_parents = this.all_concrete_parents (__interface);
	ArrayList __result = new ArrayList ();
	Iterator __iterator = __concrete_parents.iterator ();
	while (__iterator.hasNext ()) {
	    InterfaceElement __parent = (InterfaceElement)__iterator.next ();
	    ArrayList __tmp_abstract_parents = this.abstract_parents (__parent);
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
	throws SymbolNotFoundException {
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
	throws SymbolNotFoundException {
	
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
	    = __ido.getPrimaryFile ().getParent ().getPackageName ('.') + ".";
	String __package = ImplGenerator.modules2package (__element);
	String __name = "";
	//String __interface_name = this.element2repo_id (__interface, "", "_", "");
	String __element_name = ImplGenerator.idl_name2java_name (__element.getName ());
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
	String __prefix = "_set_parent_of_type_";
	String __postfix = "";
	return this.create_set_method_name_for_parent (__interface, __prefix, __postfix);
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
	__method.setBody ("\n" + __field.getName () + " = " + __parameter_name + ";\n");
	return __method;
    }


    private void add_element (ClassElement __clazz, MemberElement __element) {
	if (_M_use_guarded_blocks)
	    _M_elements_for_guard_blocks.add (__element);
	try {
	    if (__element instanceof FieldElement) {
		FieldElement __field = (FieldElement)__element;
		__clazz.addField (__field);
	    } else if (__element instanceof MethodElement) {
		MethodElement __method = (MethodElement)__element;;
		__clazz.addMethod (__method);
	    } else if (__element instanceof ConstructorElement) {
		ConstructorElement __constr = (ConstructorElement)__element;
		__clazz.addConstructor (__constr);
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
	System.out.println ("element: " + __element);
	Assertion.assert (false);
	return null;
    }


    private void work_with_guarded (int __action, MemberElement __guarded_element,
				    String __name_of_block, JavaEditor __editor) {
	try {
	    //if (DEBUG)
	    //System.out.println ("name of guarded block: " + __name_of_block);
	    Assertion.assert (__guarded_element != null);
	    Assertion.assert (__guarded_element.getDeclaringClass () != null);
	    //System.out.println ("__guarded_element: " + __guarded_element);
	    //System.out.println ("class: " + __guarded_element.getDeclaringClass ());
	    Assertion.assert (__editor != null);
	    //Assertion.assert (__editor.sourceToText 
	    //(__guarded_element.getDeclaringClass ()) != null);
	    SourceCookie.Editor __src_editor = (SourceCookie.Editor)
		__guarded_element.getDeclaringClass ().getCookie (SourceCookie.Editor.class);
	    Assertion.assert (__src_editor != null);
	    javax.swing.text.Document __root_document =  __src_editor.sourceToText
		(__guarded_element.getDeclaringClass ()).getDocument ();
	    Assertion.assert (__root_document != null);
	    String __root_text = __root_document.getText (0, __root_document.getLength ());
	    if (DEBUG)
		System.out.println ("__root_text: " + __root_text);
	    __src_editor = (SourceCookie.Editor)__guarded_element.getCookie
		(SourceCookie.Editor.class);
            javax.swing.text.Element __element = __src_editor.sourceToText
		(__guarded_element);
	    Assertion.assert (__element != null);
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
	    /*
	      javax.swing.text.Position __start_pos = __element.getDocument ().createPosition
	      (__start_offset);
	      javax.swing.text.Position __start_pos = __element.getDocument ().createPosition
	      (__new_start_offset);
	      javax.swing.text.Position __end_pos = __element.getDocument ().createPosition
	      (__end_offset);
	    */
	    //System.out.println("begin: " + __element.getBegin ());
	    PositionRef __start_pos_ref = __editor.createPositionRef 
		(__start_offset, null);
	    PositionRef __end_pos_ref = __editor.createPositionRef
		(__end_offset, null);
	    //System.out.println("__start_pos_ref: " + __start_pos_ref);
	    PositionBounds __bounds = new PositionBounds (__start_pos_ref, __end_pos_ref);
	    //System.out.println("bounds: " + __bounds);
	    JavaEditor.SimpleSection __guarded = __editor.findSimpleSection
		(__name_of_block);
	    if (__guarded != null) {
		if (__action == ImplGenerator.REMOVE_SECTION) {
		    if (!__guarded.removeSection ())
			Assertion.assert (false);
		    __editor.saveDocument ();
		}
	    }
	    else {
		// can't find guard block with name __name_of_block
		// we will create one
		if (__action == ImplGenerator.CREATE_SECTION) {
		    JavaEditor.SimpleSection __section = __editor.createSimpleSection 
			(__bounds, __name_of_block);
		    __editor.saveDocument ();
		}
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
	    if (__field == null)
		System.out.println ("can't find: " + __element.getName () + " in: " + __clazz);
	    
	    __name = this.create_block_name_from_element (__field);
	    this.work_with_guarded (__action, __field, __name, __editor);
	    return;
	}
	if (__element instanceof MethodElement) {	    
	    MethodElement __tmp = (MethodElement)__element;
	    MethodElement __method = __clazz.getMethod 
		(__tmp.getName (), this.method_parameters2types (__tmp.getParameters ()));
	    if (__method == null) {
		System.out.println ("can't find __tmp: " + __tmp);
		System.out.println ("in __clazz: " + __clazz);
	    }
	    __name = this.create_block_name_from_element (__method);
	    this.work_with_guarded (__action, __method, __name, __editor);
	    return;
	}
	if (__element instanceof ConstructorElement) {
	    ConstructorElement __tmp = (ConstructorElement)__element;
	    ConstructorElement __constr = __clazz.getConstructor 
		(this.method_parameters2types (__tmp.getParameters ()));
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
					      boolean __virtual_delegation) {
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
		String __parent_variable_name 
		    = this.create_field_name_for_parent (__interface_for_delegation);
		if (__operations.get (__i) instanceof AttributeElement) {
		    AttributeElement __attr = (AttributeElement)__operations.get (__i);
		    __methods = this.attribute2java (__attr);
		    for (int __j=0; __j<__methods.length; __j++) {
			String __body = "\n";
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
		    for (int __k=0; __k<__methods.length; __k++) {
			this.add_element (__clazz, __methods[__k]);
		    }
		}
		if (__operations.get (__i) instanceof OperationElement) {
		    OperationElement __oper = (OperationElement)__operations.get (__i);
		    __method = this.operation2java (__oper);
		    String __body = "\n";
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
		    this.add_element (__clazz, __method);
		}
	    }
	} catch (SourceException __ex) {
	}
    }


    private void generate_methods (ClassElement __clazz, List __operations) {
	
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


    public void interface2java (ClassElement __clazz, InterfaceElement __interface)
	throws SymbolNotFoundException {

        if (DEBUG)
	    System.out.println ("ImplGenerator::interface2java (__clazz, " 
				+ __interface.getName () + ");"); // NOI18N

	// first of all methods from this interface
	//this.generate_methods (__clazz, __interface);

	// method(s) from all inherited abstract parent(s)
	ArrayList __abstract_parents = this.all_abstract_parents (__interface);
	if (DEBUG)
	    System.out.println ("all abstract parents: " + __abstract_parents);
	Iterator __iterator = __abstract_parents.iterator ();
	ArrayList __diapp = this.directly_implemented_interfaces (__interface);
	if (DEBUG)
	    System.out.println ("directly_implemented_interfaces: " + __diapp);
	ArrayList __dioper = this.directly_implemented_methods (__interface);
	if (DEBUG)
	    System.out.println ("directly_implemented_methods: " + __dioper);
	this.generate_methods (__clazz, __dioper);

	ArrayList __all_implemented_interfaces 
	    = this.all_implemented_interfaces (__interface);
	if (DEBUG)
	    System.out.println ("all interfaces: " + __all_implemented_interfaces);
	__iterator = __all_implemented_interfaces.iterator ();
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
	    this.generate_methods (__clazz, __delegated_operations);
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
	    String __body_of_init = "\n";
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
		    this.add_element (__clazz, __field);
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
		__constructor.setBody ("\nthis." + ImplGenerator.INITIALIZE_INHERITANCE_TREE+ " ();\n");
		//__clazz.addConstructor (__constructor);
		this.add_element (__clazz, __constructor);
		MethodElement __init = new MethodElement ();
		__init.setName (org.openide.src.Identifier.create 
				(ImplGenerator.INITIALIZE_INHERITANCE_TREE));
		__init.setParameters (new MethodParameter[0]);
		__init.setBody (__body_of_init);
		__init.setReturn (Type.VOID);
		__init.setModifiers (Modifier.PUBLIC);
		//__clazz.addMethod (__init);
		this.add_element (__clazz, __init);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    }
	    __iterator = __setter_methods.iterator ();
	    while (__iterator.hasNext ()) {
		//try {
		//__clazz.addMethod ((MethodElement)__iterator.next ());
		this.add_element (__clazz, (MethodElement)__iterator.next ());
		//} catch (SourceException __ex) {
		//__ex.printStackTrace ();
		//}
	    }
	    boolean __use_virtual_delegation = false;
	    if (_M_delegation.equals (ORBSettingsBundle.DELEGATION_VIRTUAL))
		__use_virtual_delegation = true;
	    this.generate_methods_for_parent (__clazz, __interface, __delegated_operations,
					      __methods_map, __all_methods_map, 
					      __use_virtual_delegation);
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


    private void remove_delegation_fields (ClassElement __target)
	throws SourceException {
	Assertion.assert (__target != null);
	FieldElement[] __fields = __target.getFields ();
	ArrayList __delegation_fields = new ArrayList ();
	for (int __i=0; __i<__fields.length; __i++) {
	    String __name = __fields[__i].getName ().getName ();
	    if (__name.startsWith (ImplGenerator.PREFIX_OF_FIELD_NAME)
		&& __name.endsWith (ImplGenerator.POSTFIX_OF_FIELD_NAME))
		__delegation_fields.add (__fields[__i]);
	}
	FieldElement[] __tmp_fields = new FieldElement[0];
	FieldElement[] __fields_for_remove = (FieldElement[])__delegation_fields.toArray
	    (__tmp_fields);
	__target.removeFields (__fields_for_remove);
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
		/*
		  System.out.println ("synchronize body of _initialize_inheritance_tree from:"
		  + __source_init.getBody () + "\n to: "
		  + __target_init.getBody ());
		*/
		__target_init.setBody (__source_init.getBody ());
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
		__target.removeMethod (__target_init);
	    }
	}
    }


    private void synchronize_delegation_fields (ClassElement __source,
						ClassElement __target)
	throws SourceException {
	Assertion.assert (__source != null);
	Assertion.assert (__target != null);
	FieldElement[] __source_fields = __source.getFields ();
	for (int __i=0; __i<__source_fields.length; __i++) {
	    FieldElement __tmp_field = __source_fields[__i];
	    if (__target.getField (__tmp_field.getName ()) == null) {
		//System.out.println ("adding field: " + __tmp_field);
		__target.addField (__tmp_field);
	    }
	}
    }


    private void synchronize_implementations (ClassElement __source, 
					      ClassElement __target) {
	
	if (DEBUG) {
	    System.out.println ("orig class: " + __source.toString ()); // NOI18N
	    System.out.println ("new class: " + __target.toString ()); // NOI18N
	}	
	if (_M_settings.getSynchro () != ORBSettingsBundle.SYNCHRO_DISABLED) {
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
		this.remove_delegation_fields (__target);
		this.synchronize_delegation_fields (__source, __target);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    };
	    try {
		// synchronize init methods
		this.synchronize_init_methods (__source, __target);
	    } catch (SourceException __ex) {
		__ex.printStackTrace ();
	    };

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
			
			FileLock lock = __final_impl.lock ();
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
			printer.println ("//");
			printer.println ("// " + __repo_id);
			printer.println ("//\n");
			
			printer.println (__final_clazz.toString ());
			printer.close ();
			//_M_generated_impls.add (clazz);
			
			_M_generated_impls.add (__final_impl);
			//__result = __final_impl;
			lock.releaseLock ();
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


    private void interface2java (InterfaceElement __element)
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

	_M_elements_for_guard_blocks = new LinkedList ();

        String __impl_name = this.interface2partial_java_impl_name (__element);
        String __super_name = this.interface2java_impl_super_name (__element);
        String __modules = this.modules2package (__element);
	List __folders = this.modules2list (__element);
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

	/*
	  if (!TIE) {
	  __impl_name = IMPLBASE_IMPL_PREFIX + __element.getName ()
	  + IMPLBASE_IMPL_POSTFIX;
	  __super_name = EXT_CLASS_PREFIX + __element.getName () + EXT_CLASS_POSTFIX;
	  }
	  else {
	  __impl_name = TIE_IMPL_PREFIX + __element.getName () + TIE_IMPL_POSTFIX;
	  __super_name = IMPL_INT_PREFIX + __element.getName () + IMPL_INT_POSTFIX;
	  }
	*/
        // print to status line
	if (DEBUG)
	    System.out.println ("Generate " + __package + "." // NOI18N
				+ __impl_name + " ..."); // NOI18N
	java.lang.Object[] __arr = new Object[] {__package + "." + __impl_name};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

        try {
            final ClassElement __clazz = new ClassElement ();
            __clazz.setName (org.openide.src.Identifier.create (__impl_name));
	    __clazz.setModifiers (Modifier.PUBLIC);
            if (!TIE)
                __clazz.setSuperclass (org.openide.src.Identifier.create (__super_name));
            else
                __clazz.setInterfaces (new org.openide.src.Identifier[]
		    {org.openide.src.Identifier.create (__super_name)} );

            this.interface2java (__clazz, __element);

	    final FileObject __folder = __currect_folder;
            final FileObject __impl;
	    String __full_impl_name = this.interface2java_impl_name (__element);

            if ((__impl = __folder.getFileObject (__impl_name, "java")) != null) { // NOI18N
                if (DEBUG)
                    System.out.println ("file exists"); // NOI18N
                ClassElement __dest = ClassElement.forName (__full_impl_name);
		// before synchronization we need to remove all guarded blocks from __dest
		this.remove_guarded_blocks (__full_impl_name);
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

	    this.create_guarded_blocks (__full_impl_name);

	} catch (org.openide.src.SourceException e) {
	}
    }


    public void value2java (ClassElement __clazz, ValueElement __value)
        throws SymbolNotFoundException, SourceException {

        if (DEBUG)
	    System.out.println ("ImplGenerator::value2java (__clazz, " 
				+ __value.getName () + ");"); // NOI18N

	// default constructor
	ConstructorElement __constructor = new ConstructorElement ();
	__constructor.setModifiers (Modifier.PUBLIC);
	__constructor.setBody ("\n");
	__clazz.addConstructor (__constructor);

	ArrayList __operations = new ArrayList ();
	__operations.addAll 
	    (this.get_elements_from_element (__value, new OperationFilter (), false));
	List __all_parents_and_supported_interfaces
	    = this.all_parents_and_supported_interfaces (__value);
	//System.out.println ("all implemented interfaces: " 
	//+ __all_parents_and_supported_interfaces);
	Iterator __iterator = __all_parents_and_supported_interfaces.iterator ();
	while (__iterator.hasNext ()) {
	    IDLElement __tmp_element = (IDLElement)__iterator.next ();
	    __operations.addAll (this.get_elements_from_element (__tmp_element, 
								 new OperationFilter (),
								 false));
	}
	//System.out.println ("all implemented operations: " + __operations);
	//List __operations = this.get_elements_from_element 
	//(__value, new OperationFilter (), false);
	this.generate_methods (__clazz, __operations);
        // parents...
	/*
	  Vector __parents = __value.getParents ();
	  
	  for (int __i=0; __i<__parents.size (); __i++) {
	  String __name_of_parent = (String)__parents.elementAt (__i);
	  IDLElement __parent
	  = findElementByName (__name_of_parent, __element);
	  if (__parent == null) {
	  throw new SymbolNotFoundException (__name_of_parent);
	  }
	  this.value2java (__clazz, (IDLElement)__parent);
	  }
	*/
    }


    private void value2java (ValueElement __element)
	throws SymbolNotFoundException, RecursiveInheritanceException, java.io.IOException {
        if (DEBUG) {
            System.out.println ("value2java: " + __element.getName ()); // NOI18N
            System.out.println ("name: " + _M_ido.getPrimaryFile ().getName ()); // NOI18N
	}
	if (__element.isAbstract ()) {
	    if (DEBUG)
		System.out.println ("abstract value " + __element.getName ());
	    return;
	}

	//RecursiveInheritanceChecker.check (__element);

	//_M_elements_for_guard_blocks = new LinkedList ();

        String __impl_name = this.VALUE_IMPL_PREFIX 
	    + this.idl_name2java_name (__element.getName ())
	    + this.VALUE_IMPL_POSTFIX;
        String __super_name = this.idl_name2java_name (__element.getName ());
        String __modules = this.modules2package (__element);
	List __folders = this.modules2list (__element);
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
	java.lang.Object[] __arr = new Object[] {__package + "." + __impl_name};
	TopManager.getDefault ().setStatusText 
	    (MessageFormat.format (CORBASupport.GENERATE, __arr));

        try {
            final ClassElement __clazz = new ClassElement ();
            __clazz.setName (org.openide.src.Identifier.create (__impl_name));
	    __clazz.setModifiers (Modifier.PUBLIC);
	    __clazz.setSuperclass (org.openide.src.Identifier.create (__super_name));

            this.value2java (__clazz, __element);

	    final FileObject __folder = __currect_folder;
            final FileObject __impl;
	    String __full_impl_name = this.value2java_impl_name (__element);
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


    public MethodElement factory2java (InitDclElement __factory) {
	
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
			      (this.idl_name2java_name (__factory.getName ())));
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
                Type __ptype = this.type2java (__param.getType (), Parameter.IN, 
					       __package, __parent_element);
                __params[__i] = new MethodParameter
		    (this.idl_name2java_name (__param.getName ()), __ptype, false);
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
	throws SourceException {
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
	throws SymbolNotFoundException, RecursiveInheritanceException, java.io.IOException {
	/*
	  if (DEBUG) {
	  System.out.println ("value_factory2java: " + __element.getName ()); // NOI18N
	  System.out.println ("name: " + _M_ido.getPrimaryFile ().getName ()); // NOI18N
	  }
	*/
	if (__factories.size () == 0)
	    return;
	InitDclElement __element = (InitDclElement)__factories.get (0);
	ValueAbsElement __value = (ValueAbsElement)__element.getParent ();
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
	java.lang.Object[] __arr = new Object[] {__package + "." + __impl_name};
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
        List __members = new ArrayList ();
	__members.addAll (this.get_all_interfaces ());
	__members.addAll (this.get_all_values ());
	//__members.addAll (this.get_all_factories ());
        for (int i=0; i<__members.size (); i++) {
	    try {
		if (DEBUG)
		    System.out.println ("element: " + __members.get (i));
		if (__members.get (i) instanceof InterfaceElement)
                    this.interface2java ((InterfaceElement)__members.get (i));
		if (__members.get (i) instanceof ValueElement) {
                    this.value2java ((ValueElement)__members.get (i));
		    List __factories = this.get_all_factories ();
		    this.value_factory2java (__factories);
		}
		//if (__members.get (i) instanceof InitDclElement)
		//this.value_factory2java ((InitDclElement)__members.get (i));
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
		//TopManager.getDefault ().notify (new NotifyDescriptor.Exception (__ex));
		//__ex.printStackTrace ();
		TopManager.getDefault ().getErrorManager ().notify (__ex);
		_M_exception_occured = true;
	    }
        }

	if (this.getOpen ()) {
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
