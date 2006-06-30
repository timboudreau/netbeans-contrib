/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.src;

import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

//import org.openide.nodes.Node;
import org.openide.cookies.OpenCookie;

/*
 * @author Karel Gardas
 */

public class Element extends SimpleNode
    implements Serializable, OpenCookie {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    private String name;
    private int line;
    private Vector members;

    private Element parent;

    static final long serialVersionUID =-3167109028199964338L;
    public Element (int i) {
        super (i);
        members = new Vector ();
    }

    public Element (IDLParser p, int i) {
        super (p, i);
        members = new Vector ();
    }

    public void setLine (int i) {
        if (DEBUG)
            System.out.println ("set line: " + i);
        line = i;
    }

    public int getLine () {
        return line;
    }

    public void setName (String v) {
        name = v;
    }

    public String getName () {
        return name;
    }

    public void addMember (Node x) {
        members.addElement (x);
    }

    public Vector getMembers () {
        return members;
    }
    /*
    public Object getMember (int i) {
       return members.elementAt (i);
}
    */

    public Element getMember (int i) {
        return (Element)members.elementAt (i);
    }

    public void setParent (Element e) {
        parent = e;
    }

    public Element getParent () {
        return parent;
    }

    public void open () {
        if (DEBUG)
            System.out.println ("open action :-))");
    }


    public void jjtClose () {
        if (DEBUG)
            System.out.println ("Element.jjtClose ()");
        for (int i=0; i<jjtGetNumChildren (); i++) {
            addMember (jjtGetChild (i));
        }
        for (int i=0; i<getMembers ().size (); i++) {
            ((Element)getMember (i)).setParent (this);
        }

    }

    public void xDump (String s) {
        //System.out.println ("dump: " + members);
        for (int i=0; i<members.size (); i++) {
            System.out.println (s + members.elementAt (i));
            ((Element)members.elementAt (i)).xDump (s + " ");
        }
    }

    public static Node jjtCreate(int id) {
        return new Element (id);
    }

    public static Node jjtCreate(IDLParser p, int id) {
        return new Element (p, id);
    }

}

/*
 * <<Log>>
 *  6    Gandalf   1.5         11/27/99 Patrik Knakal   
 *  5    Gandalf   1.4         11/4/99  Karel Gardas    - update from CVS
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */
