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

package com.netbeans.enterprise.modules.corba.browser.ns;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import java.io.*;
import java.net.*;
import java.util.Vector;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import com.netbeans.enterprise.modules.corba.*;
import com.netbeans.enterprise.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */
 
public class ContextNode extends AbstractNode implements Node.Cookie {

  static final String ICON_BASE 
    = "com/netbeans/enterprise/modules/corba/browser/ns/resources/folder";
  static final String ICON_BASE_ROOT 
    = "com/netbeans/enterprise/modules/corba/browser/ns/resources/ns-root";

  //public static final boolean DEBUG = false;
  public static final boolean DEBUG = true;


  private ORB orb;
  private NamingContext context;
  private Binding binding;
  private String name;
  private String kind;

  private boolean _root = false;
  private boolean _loaded = false;

  private Vector contexts;
  private Vector naming_children;


  private CORBASupportSettings css;

  public ContextNode () {
    super (new ContextChildren ());
    //super (Children.LEAF);
    setName ("CORBA Naming Service");
    _root = true;
    init ();
  }

  public ContextNode (NamingContext nc, Binding b) {
    super (new ContextChildren ());
    if (nc == null) {
      if (DEBUG) 
	System.out.println ("nc is null");
    }
    else
      ((ContextChildren)getChildren ()).setContext (nc);
    binding = b;
    context = nc;
    setName (binding.binding_name[0].id);
    setKind (binding.binding_name[0].kind);
    init ();
  }

  public ContextNode (NamingContext nc) {
    super (new ContextChildren ());
    if (nc == null) {
      if (DEBUG) 
	System.out.println ("nc is null");
    }
    else
      ((ContextChildren)getChildren ()).setContext (nc);
    context = nc;
    init ();
  }
   

  public void init () {
    if (DEBUG) { 
      System.out.println ("ContextNode::init ()");
    }
    ((ContextChildren)getChildren ()).setContextNode (this);
    css = (CORBASupportSettings) CORBASupportSettings.findObject
      (CORBASupportSettings.class, true);
    orb = css.getORB ();
    contexts = new Vector ();
      
    if (context != null)
      setIconBase (ICON_BASE);
    else
      setIconBase (ICON_BASE_ROOT);
    setDisplayName (getName ());

    systemActions = new SystemAction[] { 
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.CreateNewContext.class),
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.BindNewContext.class),
      null,
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.UnbindContext.class),
      null,
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.CopyServerCode.class),
      null,
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.BindNewObject.class),
	 
      null,
      SystemAction.get (com.netbeans.enterprise.modules.corba.browser.ns.RefreshAction.class),
      null,
      SystemAction.get(org.openide.actions.PropertiesAction.class)
    };
  }


  public void restore () {
    if (DEBUG)
      System.out.println ("load from storage :-))");
    naming_children = css.getNamingServiceChildren ();
    if (DEBUG)
      System.out.println ("no of naming children: " + naming_children.size ());
      
    //if (naming_children.size () != null) 
    for (int i=0; i<naming_children.size (); i++) {
      NamingServiceChild child = (NamingServiceChild)naming_children.elementAt (i);
      try {
	bind_new_context (child.getName (), child.getKind (), child.getURL (), child.getIOR ());
      } catch (Exception e) {
	if (DEBUG)
	  e.printStackTrace ();
	naming_children.remove (i);
      }	 
    }
    _loaded = true;
    if (DEBUG)
      System.out.println ("on end of restore - loaded?: " + loaded ());
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

  public Vector getContexts () {
    return contexts;
  }

  public NamingContext getContext () {
    return context;
  }

  public ORB getORB () {
    return orb;
  }

  public boolean root () {
    return _root;
  }

  public boolean loaded () {
    return _loaded;
  }

  public void bind_new_context (String name, String kind, String url, String ior) 
    throws java.net.MalformedURLException, java.io.IOException, 
    org.omg.CosNaming.NamingContextPackage.NotFound, 
    org.omg.CosNaming.NamingContextPackage.CannotProceed,
    org.omg.CosNaming.NamingContextPackage.InvalidName,
    org.omg.CosNaming.NamingContextPackage.AlreadyBound {
    NamingContext nc = null;
    if (DEBUG) 
      System.out.println ("ContextNode::bind_new_context ();");
    if (!url.equals ("")) {
      //try {
      URL uc = new URL (url);
      String ref;
      //FileInputStream file = new FileInputStream(refFile);
      BufferedReader in =
	new BufferedReader(new InputStreamReader(uc.openStream ()));
      ref = in.readLine();
      org.omg.CORBA.Object o = orb.string_to_object (ref);
      nc = NamingContextHelper.narrow (o);
      if (nc == null)
	System.out.println ("error while binding!!!");
      //setName (name);
      //setKind ("");
      //((ContextChildren)getChildren ()).setContext (context);
      //((ContextChildren)getChildren ()).addNotify ();
      //file.close();
      //} catch (Exception e) {
      //e.printStackTrace ();
      //}
    }	 

    if (!ior.equals ("")) {
      org.omg.CORBA.Object o = orb.string_to_object (ior);
      nc = NamingContextHelper.narrow (o);
      if (nc == null)
	System.out.println ("can't bind to context");
    }
      
    //if (context == null) {
    if (root ()) {
      // try to list context - it succeed if context is alife
      BindingIteratorHolder it = new BindingIteratorHolder ();
      BindingListHolder list = new BindingListHolder ();
      nc.list (0, list, it);
      ContextNode cn = new ContextNode (nc);
      cn.setName (name);
      cn.setKind (kind);
      contexts.addElement (cn);
      if (root() && loaded ()) {
	naming_children.addElement (new NamingServiceChild (name, kind, url, ior));
	if (DEBUG) {
	  System.out.println ("no of naming children in CORBASupportSettings: " 
			      + css.getNamingServiceChildren ().size ());
	}
      }
    }
    else {
      if (DEBUG) 
	System.out.println ("pribindeni contextu");
      NameHolder context_name = new NameHolder ();
      NameComponent name_component = new NameComponent (name, kind); // name, kind
      //context_name.value = new NameComponent [1];
      context_name.value = new NameComponent [1];
      context_name.value[0] = name_component;
      //context_name.value[0].id = name;
      //context_name.value[0].kind = "";
      //try {
      context.bind_context (context_name.value, nc);
      //} catch (Exception e) {
      //e.printStackTrace ();
      //}
    }
    if (DEBUG)
      System.out.println ("loaded?: " + loaded ());
    if ((root () && loaded ()) || !root ()) {
      ((ContextChildren)getChildren ()).addNotify ();
    }

  }

  public void create_new_context (String name, String kind) 
    throws org.omg.CosNaming.NamingContextPackage.InvalidName, 
    org.omg.CosNaming.NamingContextPackage.AlreadyBound,
    org.omg.CosNaming.NamingContextPackage.NotFound,
    org.omg.CosNaming.NamingContextPackage.CannotProceed {
    if (!root ()) {
      //NameHolder context_name = new NameHolder ();
      NameComponent name_component = new NameComponent (name, kind); // name, kind
      /*
	context_name.value = new NameComponent [1];
	context_name.value[0] = name_component;
      */
      NameComponent[] context_name = new NameComponent[1];
      context_name[0] = name_component;
      //try {
      context.bind_new_context (context_name);
      //} catch (Exception e) {
      //e.printStackTrace ();
      //}
      ((ContextChildren)getChildren ()).addNotify ();
    }
  }

   
  public void unbind () {
    if (!root ()) {
      NameComponent name_component = new NameComponent (getName (), getKind ()); // name, kind
      NameComponent[] context_name = new NameComponent[1];
      context_name[0] = name_component;
      try {
	if (!((ContextNode)getParentNode ()).root ()) {
	  // isn't root
	  ((ContextNode)getParentNode ()).getContext ().unbind (context_name);
	  ((ContextChildren)((ContextNode)getParentNode ()).getChildren ()).addNotify ();
	}
	else {
	  // is root
	  ((ContextNode)getParentNode ()).getContexts ().remove (this);
	  for (int i=0; i<css.getNamingServiceChildren ().size (); i++) {
	    NamingServiceChild child 
	      = (NamingServiceChild)css.getNamingServiceChildren ().elementAt (i);
	    if (child.getName ().equals (getName ()) 
		&& child.getKind ().equals (getKind ())) {
	      css.getNamingServiceChildren ().remove (i);
	      break;
	    }
	  }
	  ((ContextChildren)((ContextNode)getParentNode ()).getChildren ()).addNotify ();
	}
	       
      } catch (Exception e) {
	e.printStackTrace ();
      }
      ((ContextChildren)getChildren ()).addNotify ();
    }
  }


  public void refresh () {
    ((ContextChildren)getChildren ()).addNotify ();
  }


  public void bind_new_object (String name, String url, String ior)
    throws java.net.MalformedURLException, java.io.IOException,
    org.omg.CosNaming.NamingContextPackage.NotFound,
    org.omg.CosNaming.NamingContextPackage.AlreadyBound,
    org.omg.CosNaming.NamingContextPackage.CannotProceed,
    org.omg.CosNaming.NamingContextPackage.InvalidName {
    org.omg.CORBA.Object obj = null;
    if (DEBUG) 
      System.out.println ("ContextNode::bind_new_object ();");
    if (!url.equals ("")) {
      //try {
      URL uc = new URL (url);
      String ref;
      //FileInputStream file = new FileInputStream(refFile);
      BufferedReader in =
	new BufferedReader(new InputStreamReader(uc.openStream ()));
      ref = in.readLine();
      obj = orb.string_to_object (ref);
      if (obj == null)
	System.out.println ("can't bind to object");
      //setName (name);
      //setKind ("");
      //}
      //catch (Exception e) {
      // e.printStackTrace ();
      //}
    } 

    if (!ior.equals ("")) {
      obj = orb.string_to_object (ior);
      if (obj == null)
	System.out.println ("can't bind to object");
    }
      
    if (context != null) {
      if (DEBUG) 
	System.out.println ("pribindeni objectu");
      NameHolder context_name = new NameHolder ();
      NameComponent name_component = new NameComponent (name, ""); // name, kind
      //context_name.value = new NameComponent [1];
      context_name.value = new NameComponent [1];
      context_name.value[0] = name_component;
      //context_name.value[0].id = name;
      //context_name.value[0].kind = "";
      //try {
      context.bind (context_name.value, obj);
      //} catch (Exception e) {
      //e.printStackTrace ();
      //}
    }
    ((ContextChildren)getChildren ()).addNotify ();
  }


  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("Name", String.class, "Name", "Name of Context") {
      public java.lang.Object getValue () {
	return name;
      }
    });
    ss.put (new PropertySupport.ReadOnly ("Kind", String.class, "Kind", "Kind of Context") {
      public java.lang.Object getValue () {
	return getKind ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("IOR", String.class, "IOR", "IOR of Context") {
      public java.lang.Object getValue () {
	return context != null ? orb.object_to_string (context) : "unknown";
      }
    });
      
    return s;
  }
}

/*
 * $Log
 * $
 */


