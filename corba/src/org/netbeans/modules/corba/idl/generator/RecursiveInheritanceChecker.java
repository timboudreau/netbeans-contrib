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

import java.util.Stack;
import java.util.Vector;

import java.text.MessageFormat;

import org.netbeans.modules.corba.CORBASupport;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.InterfaceElement;
import org.netbeans.modules.corba.idl.src.DeclaratorElement;

import org.netbeans.modules.corba.utils.Assertion;
/*
 * @author Karel Gardas
 */

public class RecursiveInheritanceChecker extends java.lang.Object {

    protected RecursiveInheritanceChecker () 
	throws RecursiveInheritanceException, SymbolNotFoundException {
    }

    public static void check (InterfaceElement __interface) 
	throws RecursiveInheritanceException, SymbolNotFoundException {
	RecursiveInheritanceChecker __checker = new RecursiveInheritanceChecker ();
	__checker.check (__interface, new Stack ());
    }

    public String getAbsoluteName (IDLElement __element) {
	if (__element.getParent () != null)
	    return this.getAbsoluteName (__element.getParent ()) + "::" + __element.getName ();
	else
	    return "";
    }

    public void check (IDLElement __element, Stack __stack) 
	throws RecursiveInheritanceException, SymbolNotFoundException {
	//System.out.println ("check (" + __interface.getName () + ", " + __stack + ")");
        String __name = this.getAbsoluteName (__element);
	if (__stack.search (__name) != -1) {
	    //java.lang.Object[] __arr 
	    //= new java.lang.Object[] {__name, new Integer (__interface.getLine ())};
	    //String __msg = MessageFormat.format (CORBASupport.RECURSIVE_INHERITANCE, __arr);
	    //throw new RecursiveInheritanceException ("recursive inheritance for interface " + __name + " at line " + __interface.getLine () + ".");
	    //throw new RecursiveInheritanceException (__msg);
	    RecursiveInheritanceException __exc = new RecursiveInheritanceException ();
	    __exc.setLine (__element.getLine ());
	    __exc.setName (__name);
	    throw __exc;
	}
	__stack.push (__name);
	if (__element instanceof InterfaceElement) {
	    InterfaceElement __interface = (InterfaceElement)__element;
	    Vector __parents = __interface.getParents ();
	    for (int __i=0; __i<__parents.size (); __i++) {
		String __parent_name = (String)__parents.elementAt (__i);
		//InterfaceElement __parent = (InterfaceElement)ImplGenerator.findElementByName 
		//(__parent_name, __interface);
		IDLElement __parent = null;
		/*
		  IDLElement __parent = ImplGenerator.findElementByName 
		  (__parent_name, __interface);
		*/
		/*
		  while (!__parent instanceof InterfaceElement) {
		*/
		if (__parent == null) {
		    throw new SymbolNotFoundException (__parent_name);
		}
		this.check (__parent, __stack);
		int __how_many = __stack.search (__name);
		for (int __j=1; __j<__how_many; __j++)
		    __stack.pop ();
	    }
	}
	else if (__element instanceof DeclaratorElement) {
	    // typedefed interface e.g.
	    // interface a {};
	    // typedef a my_a;
	    // interface x : my_a {};
	    String __type_name = ((DeclaratorElement)__element).getType ().getName ();
	    System.out.println ("__type_name: " + __type_name);
	    IDLElement __type_element = null;
	    /*
	      IDLElement __type_element = ImplGenerator.findElementByName 
	      (__type_name, __element);
	    */
	    if (__type_element == null) {
		throw new SymbolNotFoundException (__type_name);
	    }
	}
	else {
	    // error
	    Assertion.assert (false);
	}
    }
		       
}

