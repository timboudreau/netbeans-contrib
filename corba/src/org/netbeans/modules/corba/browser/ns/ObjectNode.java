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

package org.netbeans.modules.corba.browser.ns;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import java.io.*;
import java.net.*;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */

public class ObjectNode extends AbstractNode implements Node.Cookie {

    static final String ICON_BASE
    = "org/netbeans/modules/corba/browser/ns/resources/interface";

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    //private ORB orb;
    private Binding binding;
    private String name;
    private String kind;
    private String ior;

    public ObjectNode () {
        super (Children.LEAF);
        //super (Children.LEAF);
        init ();
    }

    public ObjectNode (Binding b, String ref) {
        super (Children.LEAF);
        binding = b;
        setName (binding.binding_name[0].id);
        setKind (binding.binding_name[0].kind);
        ior = ref;
        init ();
    }

    public void init () {
        if (DEBUG)
            System.out.println ("ObjectNode () :-)");
        setDisplayName (getName ());
        setIconBase (ICON_BASE);
        //CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
        //   (CORBASupportSettings.class, true);
        //orb = css.getORB ();

        systemActions = new SystemAction[] {
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.UnbindObject.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.CopyClientCode.class),
                            null,
                            SystemAction.get(org.openide.actions.PropertiesAction.class)
                        };
    }


    public Node.Cookie getCookie(Class c) {
        if (c.isInstance(this))
            return this;
        else
            return super.getCookie(c);
    }

    public void setName (String n) {
        name = n;
    }

    public String getName () {
        return name;
    }

    public void setKind (String n) {
        kind = n;
    }

    public String getKind () {
        return kind;
    }

    public void unbind () {
        NameComponent name_component = new NameComponent (getName (), getKind ()); // name, kind
        NameComponent[] context_name = new NameComponent[1];
        context_name[0] = name_component;
        try {
            ((ContextNode)getParentNode ()).getContext ().unbind (context_name);
            ((ContextChildren)((ContextNode)getParentNode ()).getChildren ()).addNotify ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("Name", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_Name"), NbBundle.getBundle(ContextNode.class).getString("TIP_ObjectName")) {
                    public java.lang.Object getValue () {
                        return name;
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("Kind", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_Kind"), NbBundle.getBundle(ContextNode.class).getString("TIP_ObjectType")) {
                    public java.lang.Object getValue () {
                        return getKind ();
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("IOR", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_IOR"), NbBundle.getBundle(ContextNode.class).getString("TIP_ObjectIOR")) {
                    public java.lang.Object getValue () {
                        return ior;
                    }
                });

        return s;
    }

}

/*
 * $Log
 * $
 */


