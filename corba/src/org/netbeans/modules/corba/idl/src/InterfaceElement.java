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

import java.util.Vector;

public class InterfaceElement extends IDLElement {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    private InterfaceHeaderElement header;
    private Vector body;

    public InterfaceElement(int id) {
        super(id);
        body = new Vector ();
    }

    public InterfaceElement(IDLParser p, int id) {
        super(p, id);
        body = new Vector ();
    }
    /*
    public void addParent (Identifier x) {
      inherited_from.addElement (x);
}

    public void setParent (Vector parents) {
      inherited_from = parents;
}
    */  
    public Vector getParents () {
        return header.getInheritedParents ();
    }
    /*
    public void setAbstract (boolean value) {
      is_abstract = value;
}
    */
    public boolean isAbstract () {
        return header.isAbstract ();
    }

    public void addMemberOfBody (IDLElement e) {
        body.addElement (e);
    }

    public Vector getMembersOfBody () {
        return body;
    }

    public void jjtClose () {
        super.jjtClose ();
        // first header
        if (DEBUG)
            System.out.println ("InterfaceElement.jjtClose ()"); // NOI18N
        Vector _members = super.getMembers ();
        header = (InterfaceHeaderElement)_members.elementAt (0);
        setName (header.getName ());
        setLine (header.getLine ());
        setColumn (header.getColumn ());
        // remove InterfaceHeader
        _members.remove (0);
        int max = super.getMembers ().size ();
        for (int i=0; i<max; i++) {
            addMemberOfBody ((IDLElement)_members.elementAt (i));
        }

        // reformating attributes from one attribute with other to many attribute
        for (int i=0; i<max; i++) {
            if (_members.elementAt (i) instanceof AttributeElement) {
                Vector attrs = ((AttributeElement)_members.elementAt (i)).getOther ();
                AttributeElement parent = (AttributeElement)_members.elementAt (i);
                //for (int j=0; j<attrs.size (); j++) {
                for (int j=attrs.size () - 1; j >= 0; j--) {
                    AttributeElement attr = new AttributeElement (-1);
                    //Identifier id = new Identifier (-1);
                    //id.setName ((String)attrs.elementAt (j));
                    attr.setName (((DeclaratorElement)attrs.elementAt (j)).getName ());
                    attr.setLine (((DeclaratorElement)attrs.elementAt (j)).getLine ());
                    attr.setColumn (((DeclaratorElement)attrs.elementAt (j)).getColumn ());
                    attr.setType (parent.getType ());
                    attr.setReadOnly (parent.getReadOnly ());
                    attr.setParent (this);
                    //attr.addMember (id);
                    getMembers ().insertElementAt (attr, i + 1);
                }
                parent.setOther (new Vector ());
            }
        }
    }

}





