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

package org.netbeans.modules.corba.browser.ir;

import org.omg.CORBA.*;
//import org.omg.CosNaming.*;

import java.io.*;
import java.net.*;
import java.util.Vector;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */

public class IRRootNode extends AbstractNode implements Node.Cookie {

    static final String ICON_BASE_ROOT
        = "org/netbeans/modules/corba/browser/ir/resources/ir-root";

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    private static IRRootNode instance;

    private ORB orb;

    private Vector repositories;

    private CORBASupportSettings css;

    private boolean _loaded;

    private String name;

    static {
        instance = null;
    }

    public IRRootNode () {
        super (new IRRootNodeChildren ());
        instance = this;
        setName (NbBundle.getBundle(IRRootNode.class).getString("CTL_CORBAInterfaceRepository"));
        init ();
    }

    public static IRRootNode getDefault(){
        return instance;
    }

    public void init () {
        if (DEBUG) {
            System.out.println ("IRRootNode::init ()");
        }
        repositories = new Vector ();
        setIconBase (ICON_BASE_ROOT);
        setDisplayName (getName ());

        ((IRRootNodeChildren)getChildren ()).setRootNode (this);

        systemActions = new SystemAction[] {
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.AddRepository.class)
        };
    }


    public void restore () {
        if (DEBUG)
            System.out.println ("load from storage :-))");
        if (css == null)
            lazyInit();
        Vector tmp_repositories = css.getInterfaceRepositoryChildren ();

        for (int i=0; i<tmp_repositories.size (); i++) {
            Repository child = (Repository)tmp_repositories.elementAt (i);
            try {
                restoreRepository (child.getName (), child.getURL (), child.getIOR ());
            } catch (Exception e) {
                // Handling the error while reading repisitories
                child.setFailed(true);
                this.repositories.addElement(child);
            }
        }
        if (DEBUG)
            System.out.println ("no of IR children: " + repositories.size ());

        _loaded = true;
        if (DEBUG)
            System.out.println ("on end of restore - loaded?: " + loaded ());
        if (css == null)
            lazyInit();
        css.setInterfaceRepositoryChildren (repositories);
    }


    public Node.Cookie getCookie(Class c) {
        if (c.isInstance(this))
            return this;
        else
            return super.getCookie(c);
    }


    public void restoreRepository (String name, String url, String ior)
        throws java.net.MalformedURLException,
        java.io.IOException {

        org.omg.CORBA.Container rep = null;

        if (DEBUG)
            System.out.println ("IRRootNode::addRepository (...);");
        if (!url.equals ("")) {
            //try {
            URL uc = new URL (url);
            String ref;
            //FileInputStream file = new FileInputStream(refFile);
            BufferedReader in =
                new BufferedReader(new InputStreamReader(uc.openStream ()));
            ref = in.readLine();
            in.close();
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ref);
            rep = ContainerHelper.narrow (o);
            if (rep == null)
                throw new RuntimeException();
        }
        if (!ior.equals ("")) {
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ior);
            rep = ContainerHelper.narrow (o);
            if (rep == null)
                throw new RuntimeException();
        }
        if (DEBUG)
            System.out.println ("loaded?: " + loaded ());
        /*
          if ((root () && loaded ()) || !root ()) {
          ((ContextChildren)getChildren ()).addNotify ();
          }
        */
        boolean exc = false;
        try {
            Contained[] contents = rep.contents (DefinitionKind.dk_all, false);
        } catch (Exception e) {
            exc = true;
        }
        if (!exc)
            repositories.addElement (new Repository (name, rep, url, ior));
    }


    public void addRepository (String name, String url, String ior)
        throws java.net.MalformedURLException,
        java.io.IOException {

        org.omg.CORBA.Container rep = null;

        if (DEBUG)
            System.out.println ("IRRootNode::addRepository (...);");
        if (!url.equals ("")) {
            URL uc = new URL (url);
            String ref;
            BufferedReader in =
                new BufferedReader(new InputStreamReader(uc.openStream ()));
            ref = in.readLine();
            in.close();
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ref);
            rep = ContainerHelper.narrow (o);
            if (rep == null)
                throw new RuntimeException();
        }

        if (!ior.equals ("")) {
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ior);
            rep = ContainerHelper.narrow (o);
            if (rep == null)
                throw new RuntimeException();
        }
        if (DEBUG)
            System.out.println ("loaded?: " + loaded ());
        repositories.addElement (new Repository (name, rep, url, ior));

        if (loaded ())
            ((IRRootNodeChildren)getChildren ()).addNotify ();
    }


    public void removeRepository (String name) {
        for (int i=0; i<repositories.size (); i++) {
            if (((Repository)repositories.elementAt (i)).getName ().equals (name)) {
                repositories.remove (i);
                break;
            }
        }
        ((IRRootNodeChildren)getChildren ()).addNotify ();
    }

    public void setName (String n) {
        name = n;
    }

    public String getName () {
        return name;
    }

    public Vector getRepositories () {
        return repositories;
    }

    public ORB getORB () {
        if (orb == null)
            lazyInit();
        return orb;
    }

    public boolean loaded () {
        return _loaded;
    }

    public void refresh () {
        ((IRRootNodeChildren)getChildren ()).addNotify ();
    }
    
    private void lazyInit () {
        css = (CORBASupportSettings) CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
        orb = css.getORB ();
    }

}

/*
 * $Log
 * $
 */


