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

package org.netbeans.modules.corba.idl.node;

import javax.swing.text.Position.Bias;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

import org.openide.text.PositionRef;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.IDLNode;
import org.netbeans.modules.corba.IDLEditorSupport;

import org.netbeans.modules.corba.idl.src.IDLElement;
/*
 * @author Karel Gardas
 */

public abstract class IDLAbstractNode extends AbstractNode {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    public IDLAbstractNode (Children children) {
        super (children);
        if (DEBUG)
            System.out.println ("IDLAbstractNode (...)");
    }

    public void setCookieForDataObject (IDLDataObject ido) {
        CookieSet cookie = getCookieSet ();
        cookie.add (ido.getCookie (IDLEditorSupport.class));
    }

    public SystemAction getDefaultAction () {
        if (DEBUG)
            System.out.println ("getDefaultAction ()");
        SystemAction result = super.getDefaultAction();
        if (DEBUG)
            System.out.println ("result: " + result);
        //getIDLElement ().getDataObject ().setPositionRef (getPositionRef ());
        getIDLElement ().getDataObject ().setLinePosition (getIDLElement ().getLine ());
        getIDLElement ().getDataObject ().setColumnPosition (getIDLElement ().getColumn ());
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    abstract public IDLElement getIDLElement ();

    //public static int possition (long val) {
    //  return (int)(p & 0xFFFFFFFFL);
    //}

    public PositionRef getPositionRef () {
        int line = getIDLElement ().getLine ();
        if (DEBUG)
            System.out.println ("getPositionRef for line: " + line);
        IDLEditorSupport editor = (IDLEditorSupport)getIDLElement ().getDataObject ().getCookie
                                  (IDLEditorSupport.class);
        return editor.createPositionRef (line, Bias.Forward);
    }

}
/*
 * $Log
 * $
 */

