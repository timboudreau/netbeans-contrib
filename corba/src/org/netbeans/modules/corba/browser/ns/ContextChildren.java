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

import org.omg.CosNaming.*;
import org.omg.CORBA.*;

import java.util.Vector;
import java.io.*;

import org.openide.nodes.*;
import org.openide.*;


import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.browser.ir.util.AsyncTarget;

/*
 * @author Karel Gardas
 */

public class ContextChildren extends Children.Keys implements AsyncTarget {

    private NamingContext context;
    private ContextNode _context_node;
    private int state;
    private Node waitNode;
    
    public static final short NOT_INITIALIZED = 0;
    public static final short TRANSIENT = 1;
    public static final short INITIALIZED = 2;

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public ContextChildren () {
        super ();
        this.state = NOT_INITIALIZED;
    }

    public void addNotify () {
        if (DEBUG)
            System.out.println ("addNotify ()");
	//Thread.dumpStack ();
        synchronized (this) {
            this.state = TRANSIENT;
        }
	if (waitNode == null) {
	    this.waitNode = new org.netbeans.modules.corba.browser.ir.nodes.WaitNode ();
	    this.add ( new Node[] { this.waitNode});
	}
        org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
    }


    public void createKeys () {
        //ORB orb = ORB.init ();
        Vector keys = new Vector ();
        try {
            ORB orb = getContextNode ().getORB ();
            if (DEBUG)
                System.out.println ("createKeys ();");
            if (getContextNode ().root ()) {
                if (DEBUG)
                    System.out.println ("context is null");
                if (!getContextNode ().loaded ()) {
                    getContextNode ().restore ();
                }
                setKeys (getContextNode ().getContexts ());
                return;
            }
            BindingIteratorHolder it = new BindingIteratorHolder ();
            BindingListHolder list = new BindingListHolder ();
            context.list (0, list, it);
            BindingHolder binding = new BindingHolder ();
            boolean next_exist = false;
            if (it.value == null)
                if (DEBUG)
                    System.out.println ("NULL");

            while (it.value != null && (next_exist = it.value.next_one (binding))) {
                for (int j=0; j<binding.value.binding_name.length; j++) {
                    if (DEBUG) {
                        System.out.println ("id: " + binding.value.binding_name[j].id);
                        System.out.println("kind: " + binding.value.binding_name[j].kind);
                    }
                    if (binding.value.binding_type == BindingType.nobject) {
                        if (DEBUG)
                            System.out.println("type: object");
                        try {
                            org.omg.CORBA.Object o = context.resolve (binding.value.binding_name);
                            if (DEBUG)
                                System.out.println (orb.object_to_string (o));
                            keys.addElement (new ObjectNode (binding.value, orb.object_to_string (o)));
                        } catch (Exception e) {
                            if (DEBUG)
                                System.out.println ("IOR: exception");
                            org.openide.TopManager.getDefault().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
                        }
                    }
                    else {
                        if (DEBUG)
                            System.out.println("type: context");
                        try {
                            org.omg.CORBA.Object o = context.resolve (binding.value.binding_name);
                            NamingContext tmp_context = NamingContextHelper.narrow (o);
                            keys.addElement (new ContextNode (tmp_context, binding.value));
                        } catch (Exception e) {
                            org.openide.TopManager.getDefault().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
                        }

                    }

                }
            }
        } catch (Exception e) {
            //System.out.println ("exception " + e);
            TopManager.getDefault ().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
            if (DEBUG)
                e.printStackTrace ();
        }
        setKeys (keys);
    }


    public void setContext (NamingContext nc) {
        context = nc;
    }

    public void setContextNode (ContextNode cn) {
        _context_node = cn;
    }

    public ContextNode getContextNode () {
        return _context_node;
    }

    public org.openide.nodes.Node[] createNodes (java.lang.Object key) {
        return new Node[] { (Node)key };
    }
    
    
    public void preinvoke () {
	//System.out.println ("preinvoke");
    }
    
    public void invoke () {
	//System.out.println ("invoke");
	createKeys ();
    }
    
    public void postinvoke () {
	//System.out.println ("postinvoke");
        if (this.waitNode != null) {
            remove ( new Node[] {this.waitNode});
            this.waitNode = null;
        }
        synchronized (this) {
            this.state = INITIALIZED;
        }
    }

}


/*
 * $Log
 * $
 */
