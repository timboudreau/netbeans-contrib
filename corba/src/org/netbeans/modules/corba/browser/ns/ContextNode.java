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

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.HashMap;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */

public class ContextNode extends AbstractNode implements Node.Cookie {

    static final String ICON_BASE
    = "org/netbeans/modules/corba/browser/ns/resources/folder";
    static final String ICON_BASE_ROOT
    = "org/netbeans/modules/corba/browser/ns/resources/ns-root";
    static final String ICON_BASE_FAILED
    = "org/netbeans/modules/corba/browser/ns/resources/ns-failed";
    static final String JAVA_SPEC_VERSION
    = "java.specification.version";
    static final String JAVA_1_3
    = "1.3";

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    
    private static ContextNode singletonInstance;
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

    private static HashMap localNameServices;
    
    static {
        singletonInstance = null;
	localNameServices = new HashMap ();
    }
    
    class CosNamingCookieImpl implements CosNamingCookie {
	
	public void performInteractive () {
            final StartPanel panel = new StartPanel ();
            panel.setPort ((short)900);
            panel.setName (NbBundle.getBundle(ContextNode.class).getString("VAL_Local"));
	    panel.setKind (NbBundle.getBundle(ContextNode.class).getString
			   ("VAL_LocalKind"));
            DialogDescriptor dd = new DialogDescriptor (panel, NbBundle.getBundle(ContextNode.class).getString("TXT_LocalNS"),true,DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, DialogDescriptor.BOTTOM_ALIGN, null, null);
            Dialog dlg = TopManager.getDefault().createDialog(dd);
            dlg.setVisible(true);
            if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                dlg.setVisible (false);
                dlg.dispose();
                start (panel.getName(), panel.getKind (), panel.getPort());
            }
            else {
                dlg.setVisible (false);
                dlg.dispose();
            }
        }
	
	public void start (String name, String __kind, short port) {
            try {
                org.netbeans.modules.corba.browser.ns.wrapper.AbstractWrapper wrapper;
                wrapper =  (org.netbeans.modules.corba.browser.ns.wrapper.AbstractWrapper)localNameServices.get( new Short (port));
		if (wrapper != null) {
                    DialogDescriptor.Message dd = new DialogDescriptor.Message (NbBundle.getBundle(ContextNode.class).getString("TXT_AlreadyRunning"));
                    TopManager.getDefault().notify(dd);
                    if (dd.getValue() != DialogDescriptor.OK_OPTION) {
                        return;
                    }
                }
                else {
                    try {
                        // Is it IBM JDK
                        Class.forName ("com.ibm.CosNaming.TransientNameServer");
			//System.out.println ("found com.ibm.CosNaming.TransientNameServer");
			wrapper = new org.netbeans.modules.corba.browser.ns.wrapper.IBMWrapper();
			wrapper.start (port);
                    } catch (ClassNotFoundException cnfe) {
                        try {
                            // Is it Sun 1.2 JDK, Apple JDK, Blackdown JDK
                            Class.forName ("com.sun.CosNaming.TransientNameServer");
                            //System.out.println ("found com.sun.CosNaming.TransientNameServer");
                            wrapper = new org.netbeans.modules.corba.browser.ns.wrapper.SunWrapper();
                            wrapper.start(port);
			} catch (ClassNotFoundException cnfe2) {
                            try {
                                // Is it Sun 1.3 JDK or JDK 1.4
				Class.forName ("com.sun.corba.se.internal.CosNaming.TransientNameServer");
				//System.out.println ("found com.sun.corba.se.internal.CosNaming.TransientNameServer");
                                String sval = System.getProperty (JAVA_SPEC_VERSION);
                                if (sval!=null && sval.equals(JAVA_1_3))
                                    wrapper = new org.netbeans.modules.corba.browser.ns.wrapper.Sun13Wrapper();
                                else
                                    wrapper = new org.netbeans.modules.corba.browser.ns.wrapper.Sun14Wrapper();
				wrapper.start(port);
                            } catch (ClassNotFoundException cnfe3) {
                                TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(ContextNode.class).getString("TXT_ClassNotFound"),NotifyDescriptor.Message.ERROR_MESSAGE));
				//cnfe3.printStackTrace ();
				return;
                            }
			}
                    }
                }
            	String ior = wrapper.getIOR();
                if (ior == null) {
                    //Error while startinf NS
                    TopManager.getDefault().notify(new NotifyDescriptor.Message (java.text.MessageFormat.format (NbBundle.getBundle(ContextNode.class).getString("TXT_BadPort"),new java.lang.Object[]{new Short (port)}),NotifyDescriptor.Message.ERROR_MESSAGE));
                    return;
		}		
		ContextNode.this.bind_new_context (name, __kind, "",ior, false);
                localNameServices.put (new Short (port), wrapper);
            }catch (Exception se) {
                TopManager.getDefault().notify (new NotifyDescriptor.Message (se.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
            }
	}
	
	public void stop () {
        }
	
    }

    public ContextNode () {
        super (new ContextChildren ());
        //super (Children.LEAF);
        setName (NbBundle.getBundle(ContextNode.class).getString("CTL_CORBANamingService")); 
        _root = true;
        singletonInstance = this;
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
    
    public ContextNode (String name, String kind, String url, String ior) {
        super (Children.LEAF);
        setName (name);
        setIconBase (ICON_BASE_FAILED);
        systemActions = new SystemAction[] {
                                SystemAction.get (org.netbeans.modules.corba.browser.ns.UnbindContext.class),
                            };
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
        contexts = new Vector ();

        if (context != null) {
            setIconBase (ICON_BASE);
            systemActions = new SystemAction[] {
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.CreateNewContext.class),
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.BindNewContext.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.UnbindContext.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.CopyServerCode.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.BindNewObject.class),

                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.RefreshAction.class),
                            null,
                            SystemAction.get(org.openide.actions.PropertiesAction.class)
                        };
        }
        else {
            setIconBase (ICON_BASE_ROOT);
            systemActions = new SystemAction[] {
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.BindNewContext.class),
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.StartLocal.class)
                        };
	    this.getCookieSet().add ( new CosNamingCookieImpl ());
        }
        setDisplayName (getName ());

    }


    public void restore () {
        if (DEBUG)
            System.out.println ("load from storage :-))");
        if (css == null)
            lazyInit();
        naming_children = css.getNamingServiceChildren ();
        if (DEBUG) {
            for (int i = 0; i< naming_children.size(); i++)
                System.out.println (i+"\t"+naming_children.get(i).getClass());
            System.out.println ("no of naming children: " + naming_children.size ());
        }

        if (naming_children != null){
            for (int i=0; i<naming_children.size (); i++) {
                NamingServiceChild child = (NamingServiceChild)naming_children.elementAt (i);
                try {
                    bind_new_context (child.getName (), child.getKind (), child.getURL (), child.getIOR ());
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace ();
                    ContextNode cn = new ContextNode (child.getName (), child.getKind (), child.getURL (), child.getIOR ());
                    contexts.addElement (cn);
                }
            }
        }
        else {
            naming_children = new Vector ();
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
        if (orb == null)
            lazyInit();
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
            this.bind_new_context (name, kind, url, ior, true);
    }
    
    public void bind_new_context (String name, String kind, String url, String ior, boolean persistent)
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
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ref);
            nc = NamingContextHelper.narrow (o);
            if (nc == null)
                TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(ContextNode.class).getString("CTL_CantBind"),NotifyDescriptor.Message.ERROR_MESSAGE));
            //setName (name);
            //setKind ("");
            //((ContextChildren)getChildren ()).setContext (context);
            //((ContextChildren)getChildren ()).addNotify ();
            //file.close();
            //} catch (Exception e) {
            //e.printStackTrace ();
            //}
        }

        else if (!ior.equals ("")) { 
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ior);
            nc = NamingContextHelper.narrow (o);
            if (nc == null)
                TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(ContextNode.class).getString("CTL_CantBind"),NotifyDescriptor.Message.ERROR_MESSAGE));
        }
        else {
            TopManager.getDefault().notify ( new NotifyDescriptor.Message (NbBundle.getBundle(ContextNode.class).getString ("CTL_InvalidParams"), NotifyDescriptor.Message.ERROR_MESSAGE));
            return;
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
            if (root() && loaded () && persistent) {
                naming_children.addElement (new NamingServiceChild (name, kind, url, ior));
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
                    if (css == null)
                        lazyInit();
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
                org.openide.TopManager.getDefault().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
            }
            ((ContextChildren)getChildren ()).addNotify ();
        }
    }


    public void refresh () {
        ((ContextChildren)getChildren ()).addNotify ();
    }


    public void bind_new_object (String __name, String __kind, String __url, String __ior)
	throws java.net.MalformedURLException, java.io.IOException,
	       org.omg.CosNaming.NamingContextPackage.NotFound,
	       org.omg.CosNaming.NamingContextPackage.AlreadyBound,
	       org.omg.CosNaming.NamingContextPackage.CannotProceed,
	       org.omg.CosNaming.NamingContextPackage.InvalidName {
        org.omg.CORBA.Object __obj = null;
        if (DEBUG)
            System.out.println ("ContextNode::bind_new_object ();");
        if (!__url.equals ("")) {
            //try {
            URL __uc = new URL (__url);
            String __ref;
            //FileInputStream file = new FileInputStream(refFile);
            BufferedReader __in =
                new BufferedReader(new InputStreamReader(__uc.openStream ()));
            __ref = __in.readLine();
            if (orb == null)
                this.lazyInit();
            __obj = orb.string_to_object (__ref);
            if (__obj == null)
                TopManager.getDefault().notify( new NotifyDescriptor.Message(NbBundle.getBundle(ContextNode.class).getString("CTL_CantBind"),NotifyDescriptor.Message.ERROR_MESSAGE));
        }

        if (!__ior.equals ("")) {
            if (orb == null)
                this.lazyInit();
            __obj = orb.string_to_object (__ior);
            if (__obj == null)
                TopManager.getDefault().notify( new NotifyDescriptor.Message(NbBundle.getBundle(ContextNode.class).getString("CTL_CantBind"),NotifyDescriptor.Message.ERROR_MESSAGE));
        }

        if (context != null) {
            if (DEBUG)
                System.out.println ("pribindeni objectu");
            NameHolder __context_name = new NameHolder ();
            NameComponent __name_component = new NameComponent (__name, __kind);
            //context_name.value = new NameComponent [1];
            __context_name.value = new NameComponent [1];
            __context_name.value[0] = __name_component;
            //context_name.value[0].id = name;
            //context_name.value[0].kind = "";
            //try {
            context.bind (__context_name.value, __obj);
            //} catch (Exception e) {
            //e.printStackTrace ();
            //}
        }
        ((ContextChildren)getChildren ()).addNotify ();
    }
    
    public static ContextNode getDefault () {
        return singletonInstance;
    }


    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("Name", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_Name"), NbBundle.getBundle(ContextNode.class).getString("TIP_Name")) {
                    public java.lang.Object getValue () {
                        return name;
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("Kind", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_Kind"), NbBundle.getBundle(ContextNode.class).getString("TIP_Kind")) {
                    public java.lang.Object getValue () {
                        return getKind ();
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("IOR", String.class, NbBundle.getBundle(ContextNode.class).getString("CTL_IOR"), NbBundle.getBundle(ContextNode.class).getString("TIP_IOR")) {
                    public java.lang.Object getValue () {
                        if (orb == null)
                            lazyInit();
                        return context != null ? orb.object_to_string (context) : NbBundle.getBundle(ContextNode.class).getString("TXT_Unknown");
                    }
                });

        return s;
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


