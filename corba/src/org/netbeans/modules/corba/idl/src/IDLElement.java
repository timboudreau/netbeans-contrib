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

import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

//import org.openide.nodes.Node;
import org.openide.cookies.OpenCookie;

import org.netbeans.modules.corba.IDLDataObject;

/*
 * @author Karel Gardas
 */

public class IDLElement extends SimpleNode
    implements Serializable, OpenCookie {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    private String _M_name;
    private int _M_line;
    private int _M_column;
    private Vector _M_members;

    private IDLElement _M_parent;

    private IDLDataObject _M_ido;

    public IDLElement (int i) {
        super (i);
        _M_members = new Vector ();
        _M_name = "";
    }

    public IDLElement (IDLParser p, int i) {
        super (p, i);
        _M_members = new Vector ();
        _M_name = "";
    }

    public void setDataObject (IDLDataObject val) {
        if (DEBUG)
            System.out.println ("IDLElement ``" + getName () + " '' ::setDataObject (val)");
        _M_ido = val;
        setDataObjectForMembers (val);
    }

    public void setDataObjectForMembers (IDLDataObject val) {
        for (int i=0; i<getMembers ().size (); i++) {
            ((IDLElement)getMember (i)).setDataObject (val);
        }
    }

    public IDLDataObject getDataObject () {
        return _M_ido;
    }

    public void setLine (int i) {
        if (DEBUG)
            System.out.println ("set line for " + getName () + " : " + i);
        _M_line = i;
        //getLine (); // debug check
    }

    public int getLine () {
        if (DEBUG)
            System.out.println ("get line for " + getName () + " : " + _M_line);
        return _M_line;
    }

    public void setColumn (int i) {
        if (DEBUG)
            System.out.println ("set column for " + getName () + " : " + i);
        _M_column = i;
        //getColumn (); // debug check
    }

    public int getColumn () {
        if (DEBUG)
            System.out.println ("get column for " + getName () + " : " + _M_column);
        return _M_column;
    }

    public void setName (String v) {
        if (DEBUG)
            System.out.println ("setName: " + v);
        _M_name = v;
    }

    public String getName () {
        if (DEBUG)
            System.out.println ("getName: " + _M_name);
        return _M_name;
    }

    public void addMember (Node x) {
        _M_members.addElement (x);
    }

    public Vector getMembers () {
        return _M_members;
    }

    public void setMembers (Vector __value) {
	_M_members = __value;
    }

    /*
         public Object getMember (int i) {
         return members.elementAt (i);
         }
    */

    public IDLElement getMember (int i) {
        return (IDLElement)_M_members.elementAt (i);
    }

    public void setParent (IDLElement e) {
        _M_parent = e;
    }

    public IDLElement getParent () {
        return _M_parent;
    }

    public void open () {
        if (DEBUG)
            System.out.println ("open action :-))");
    }

    public String deepToString (IDLElement element) {
        if (DEBUG)
            System.out.println ("IDLElement::deepToString (" + element + ");");

        // for tests
        //return element.getName ();

        String names = element.getName () + ":" + element.getLine () + ":"
                       + element.getColumn () + ":" + "(";
        Vector members = element.getMembers ();
        for (int i=0; i<members.size (); i++) {
            IDLElement tmp = (IDLElement)members.elementAt (i);
            //names = names + " " + tmp.getName () + " (" + deepToString (tmp) + ")";
            names = names + tmp.getName () + ":" + tmp.getLine () + ":" + tmp.getColumn () + ":"
                    + " (" + deepToString (tmp) + ")";
            //names = names + " " + tmp.getName ();
        }

        if (DEBUG)
            System.out.println ("-> " + names);
        return names + ")";

    }

    public boolean equals (Object obj) {
        IDLElement element;
        if (!(obj instanceof IDLElement)) {
            if (DEBUG) {
                System.out.println (this.getName () + "::equals (" + obj + ");");
                System.out.println ("isn't IDLElement");
            }
            return false;
        } else {
            element = (IDLElement)obj;
        }

        if (DEBUG)
            System.out.println (this.getName () + "::equals (" + ((IDLElement)element).getName ()
                                + ");");

        if (element.className ().equals (className ())) {
            IDLElement tmp_element = (IDLElement)element;
            String this_names = deepToString (this);
            String object_names = deepToString ((IDLElement)element);
            if (this_names.equals (object_names)) {
                if (DEBUG)
                    System.out.println ("return true;");
                return true;
            }
        }
        if (DEBUG)
            System.out.println ("return false;");
        return false;
    }


    public String className () {
        String tmp = this.getClass ().getName ();
        return tmp.substring (tmp.lastIndexOf (".") + 1, tmp.length ());
    }


    public int hashCode () {
        String name = className () + deepToString (this);
        int code = name.hashCode ();
        if (DEBUG)
            System.out.println ("IDLElement::hashCode () : " + name + " : " + code);
        return code;
    }


    public void jjtClose () {
        //if (DEBUG)
        //  System.out.println ("IDLElement.jjtClose ()");
        for (int i=0; i<jjtGetNumChildren (); i++) {
            addMember (jjtGetChild (i));
        }
        for (int i=0; i<getMembers ().size (); i++) {
            ((IDLElement)getMember (i)).setParent (this);
        }

    }

    public void xDump (String s) {
        //System.out.println ("dump: " + members);
        for (int i=0; i<_M_members.size (); i++) {
            System.out.println (s + _M_members.elementAt (i));
            ((IDLElement)_M_members.elementAt (i)).xDump (s + " ");
        }
    }

    public static Node jjtCreate(int id) {
        return new IDLElement (id);
    }

    public static Node jjtCreate(IDLParser p, int id) {
        return new IDLElement (p, id);
    }

}

/*
 * <<Log>>
 *  6    Gandalf   1.5         2/8/00   Karel Gardas    
 *  5    Gandalf   1.4         11/27/99 Patrik Knakal   
 *  4    Gandalf   1.3         11/4/99  Karel Gardas    - update from CVS
 *  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    Gandalf   1.1         10/5/99  Karel Gardas    
 *  1    Gandalf   1.0         8/3/99   Karel Gardas    initial revision
 * $
 */

