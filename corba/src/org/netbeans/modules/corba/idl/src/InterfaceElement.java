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
        this.setName (header.getName ());
        this.setLine (header.getLine ());
        this.setColumn (header.getColumn ());
	this.setFileName (header.getFileName ());
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





