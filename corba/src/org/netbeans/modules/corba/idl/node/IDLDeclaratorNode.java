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

import java.util.Vector;

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.DeclaratorElement;

/**
 * Class IDLDeclaratorNode
 *
 * @author Karel Gardas
 */
public class IDLDeclaratorNode extends IDLAbstractNode {

    DeclaratorElement _declarator;
    String name;

    private static final String DECLARATOR_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/declarator";

    public IDLDeclaratorNode (DeclaratorElement value) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (DECLARATOR_ICON_BASE);
        _declarator = value;
        setCookieForDataObject (_declarator.getDataObject ());
        if (_declarator != null) {
            /*
            for (int i=0; i<_declarator.getDeclarators ().size (); i++)  {
            if (_declarator.getDeclarator (i) instanceof Identifier) {
            name = ((Identifier)_declarator.getDeclarator (i)).getName ();
            System.out.println ("found name: " + name + " at " + i + " position");
        }
        }
        }
            */
            name = _declarator.getName ();
        }
        else
            name = "NoName :)";
    }

    public IDLElement getIDLElement () {
        return _declarator;
    }

    public String getDisplayName () {
        return name;
    }

    public String getName () {
        return "declarator";
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of declarator") {
                    public Object getValue () {
                        return _declarator.getName ();
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of declarator") {
                    public Object getValue () {
                        return _declarator.getType ().getName ();
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("dimension", String.class, "dimension",
                                              "dimension of declarator") {
                    public Object getValue () {
                        String retval = "";
                        Vector dim = _declarator.getDimension ();
                        for (int i=0; i<dim.size (); i++) {
                            retval = retval + "[" + ((Integer)dim.elementAt (i)).toString () + "]";
                        }
                        return retval;
                    }
                });

        return s;
    }


}

/*
 * $Log
 * $
 */


