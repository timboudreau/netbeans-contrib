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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.*;
import java.util.Vector;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
import org.netbeans.modules.corba.utils.InvalidIORException;
import org.netbeans.modules.corba.browser.ir.util.AsyncTarget;
import org.netbeans.modules.corba.browser.ir.util.FromInitialReferencesCookie;
import org.netbeans.modules.corba.browser.ir.actions.FromInitialReferencesAction;
/*
 * @author Karel Gardas, Tomas Zezula
 */

public class IRRootNode extends AbstractNode implements Node.Cookie, FromInitialReferencesCookie, PropertyChangeListener {

    static final String ICON_BASE_ROOT
        = "org/netbeans/modules/corba/browser/ir/resources/ir-root";    // No I18N

    static final String INTERFACE_REPOSITORY_SERVICE = "InterfaceRepository";   // No I18N

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    private static IRRootNode instance;

    private ORB orb;
    
    private RequestProcessor rqProcessor;

    private CORBASupportSettings css;

    private boolean _loaded;

    private String name;

    static {
        instance = null;
    }
    
    
    private static class Request implements Runnable {
        
        private AsyncTarget target;
        
        public Request (AsyncTarget target) {
            this.target = target;
        }

        public void run() {
            this.target.preinvoke();
            this.target.invoke();
            this.target.postinvoke();
        }
        
    }

    public IRRootNode () {
        super (new IRRootNodeChildren ());
        instance = this;
        this.rqProcessor = new RequestProcessor ("CORBA-Browsers");
        setName (Util.getLocalizedString("CTL_CORBAInterfaceRepository"));
        init ();
    }

    public static IRRootNode getDefault(){
        return instance;
    }
    
    public void performAsync (AsyncTarget target) {
        this.rqProcessor.post ( new Request (target),0);
    }

    public void init () {
        if (DEBUG) {
            System.out.println ("IRRootNode::init ()");
        }
        setIconBase (ICON_BASE_ROOT);
        setDisplayName (getName ());

        ((IRRootNodeChildren)getChildren ()).setRootNode (this);

        systemActions = new SystemAction[] {
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.AddRepository.class),
            SystemAction.get (FromInitialReferencesAction.class),
            null,
            SystemAction.get (org.openide.actions.PropertiesAction.class)
        };
        
        TopManager.getDefault().addPropertyChangeListener (this);
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
                restoreRepository (child);
            } catch (Exception e) {
                // Handling the error while reading repisitories
                child.setFailed(true);
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


    public void restoreRepository (Repository repository)
        throws java.net.MalformedURLException,
        java.io.IOException {

            
        org.omg.CORBA.Container rep = null;
        String url = repository.getURL();
        String ior = repository.getIOR();
        String name = repository.getName();
        
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
        Contained[] contents = rep.contents (DefinitionKind.dk_all, false);
        repository.setRepository (rep);
    }


    public void addRepository (String name, String url, String ior)
        throws InvalidIORException {

        org.omg.CORBA.Container rep = null;

        if (DEBUG)
            System.out.println ("IRRootNode::addRepository (...);");
        if (!url.equals ("")) {
            BufferedReader in = null;
            try {
                URL uc = new URL (url);
                String ref;
                in = new BufferedReader(new InputStreamReader(uc.openStream ()));
                ref = in.readLine();
                if (ref == null)
                    throw new InvalidIORException (Util.getLocalizedString ("TXT_IOREmpty"));
                if (orb == null)
                    lazyInit();
                org.omg.CORBA.Object o = orb.string_to_object (ref);
                rep = ContainerHelper.narrow (o);
                if (rep == null)
                    throw new InvalidIORException (Util.getLocalizedString ("TXT_IORInvalid"));
            }catch (IOException exception) {
                throw new InvalidIORException (exception);
            }
            finally {
                if (in != null)
                    try {
                        in.close ();
                    } catch (IOException ioe) {}
            }
        }

        else if (!ior.equals ("")) { 
            if (orb == null)
                lazyInit();
            org.omg.CORBA.Object o = orb.string_to_object (ior);
            rep = ContainerHelper.narrow (o);
            if (rep == null)
                throw new RuntimeException();
        }
        else {
            TopManager.getDefault().notify ( new NotifyDescriptor.Message(Util.getLocalizedString("TXT_EmptyFieldsError"),NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        if (DEBUG)
            System.out.println ("loaded?: " + loaded ());
        if (this.css == null)
            this.lazyInit ();
        this.css.addInterfaceRepository (new Repository (name, rep, url, ior));
        if (loaded ())
            ((IRRootNodeChildren)getChildren ()).addNotify ();
    }


    public void removeRepository (String name) {
        if (this.css == null)
            this.lazyInit ();
        this.css.removeInterfaceRepository (name);
        ((IRRootNodeChildren)getChildren ()).addNotify ();
    }

    public void setName (String n) {
        name = n;
    }

    public String getName () {
        return name;
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
    
    public void fromInitialReferences () {
        try {
            NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine (Util.getLocalizedString("CTL_NameLabel"),Util.getLocalizedString("TXT_FromInitialReferencesDlgTitle"));
            desc.setInputText (Util.getLocalizedString("TXT_InterfaceRepository"));
            TopManager.getDefault().notify (desc);
            if (desc.getValue() == DialogDescriptor.OK_OPTION) {
                String name = desc.getInputText();
                if (name == null || name.length()==0) {
                    TopManager.getDefault().notify( new NotifyDescriptor.Message (Util.getLocalizedString("TXT_ObligatoryName"),NotifyDescriptor.ERROR_MESSAGE));
                    return;
                }
                if (this.orb == null)
                    lazyInit();
                org.omg.CORBA.Object ref = this.orb.resolve_initial_references (INTERFACE_REPOSITORY_SERVICE);
                String ior = this.orb.object_to_string (ref);
                this.addRepository (name,"",ior); // No I18N
            }
        }catch (org.omg.CORBA.ORBPackage.InvalidName invalidName) {
            TopManager.getDefault().getErrorManager().log (invalidName.toString());
            TopManager.getDefault().notify (new NotifyDescriptor.Message (Util.getLocalizedString("TXT_NoInitialReference"),NotifyDescriptor.ERROR_MESSAGE));
        }
        catch (Exception generalException) {
            TopManager.getDefault().getErrorManager().log (generalException.toString());
            TopManager.getDefault().notify (new NotifyDescriptor.Message (Util.getLocalizedString("TXT_InitialReferencesException"),NotifyDescriptor.ERROR_MESSAGE));
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (IRRootNode.class.getName());
    }
    
    public void propertyChange (PropertyChangeEvent event) {
        if (TopManager.PROP_PLACES.equals (event.getPropertyName())) {
            // Project has changed.
            // Rebuild cache of nodes.
            this.css = null;
            this.restore ();
            this.refresh ();
        }
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


