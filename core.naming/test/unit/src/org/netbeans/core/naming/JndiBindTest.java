/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.naming;

import org.netbeans.junit.*;
import junit.textui.TestRunner;

import java.util.*;
import javax.naming.*;
import java.io.File;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author  Vitezslav Stejskal
 */
public class JndiBindTest extends NbTestCase {
    
    private Context context = null;
    private FileObject root = null;
    
    public JndiBindTest (String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(JndiFindTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);

        context = new Jndi().getInitialContext (null);
        root = Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    public void testContextCreation () throws Exception {
        Hashtable env = context.getEnvironment ();
        FileObject r = (FileObject) env.get ("rootObject");
        
        assertEquals ("Root FileObject for context of DefaultFileSystem", r, root);
        
        LocalFileSystem lfs = new LocalFileSystem ();
        lfs.setRootDirectory (getWorkDir ());
        r = lfs.getRoot ();
        
        env = new Hashtable ();
        env.put ("rootObject", r);
        Context ctx = new Jndi().getInitialContext (env);
        assertEquals ("Root FileObject for context of some filesystem",
            ctx.getEnvironment ().get ("rootObject"), r);
        
    }
    
    public void testSubcontext () throws Exception {
        Context subctx = context.createSubcontext ("subctx");
        FileObject f = root.getFileObject ("subctx");
        assertTrue ("Subcontext doesn't exist", f != null);
        assertTrue ("Subcontext isn't folder", f.isFolder ());
        
        // create chain of subcontexts
        context.createSubcontext ("subctx/foo");
        f = Repository.getDefault ().findResource ("subctx/foo");
        assertTrue ("Chain of subcontexts wasn't created", f != null);
        assertTrue ("Subcontext in chain isn't folder", f.isFolder ());
        
        // NameAlreadyBoundException when creating existing context
        {
            NamingException exc = null;
            try {
                subctx.createSubcontext ("foo");
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NameAlreadyBoundException expected when creating existing context",
                exc instanceof NameAlreadyBoundException);
        }
        
        // intermediate ctxs of the chain must exist
        {
            NamingException exc = null;
            try {
                context.createSubcontext ("ctx1/ctx2");
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NamingException expected for missing intermediate context", exc != null);
        }
        
        // ContextNotEmpty exception when destroying non-epmty ctx
        {
            NamingException exc = null;
            try {
                context.destroySubcontext ("subctx");
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("ContextNotEmptyException expected when destroying non-epmty ctx",
                exc instanceof ContextNotEmptyException);
        }
        
        // destroy context
        {
            context.destroySubcontext ("subctx/foo");
            f = Repository.getDefault ().findResource ("subctx/foo");
            assertTrue ("Folder isn't deleted for destroyed context", f == null);
        }
        
        // destroing non-existing context will succeed
        {
            NamingException exc = null;
            try {
                context.destroySubcontext ("subctx/foo");
            } catch (NamingException e) {
                exc = e;
            }
             assertTrue ("Destroying non-existent context failed", exc == null);
        }

        // but intermediate contexts must exist
        {
            NamingException exc = null;
            try {
                context.destroySubcontext ("subctx/foo/blah");
            } catch (NamingException e) {
                exc = e;
            }
             assertTrue ("Accessing contexts chain needs all intermediate ctxs to exist", exc != null);
        }
    }
    
    public void testAccessParentContext () throws Exception {
        Context subctx = context.createSubcontext ("subctxXXX");
        Context parent = (Context) subctx.lookup ("..");
        assertEquals ("Parent Context not found", parent, context);
    }
    
    public void testBindObject () throws Exception {
        Integer objA = new Integer (123);
        FileObject f;
        
        context.bind ("foo", objA);
        f = root.getFileObject ("foo", "settings");
        assertTrue ("Instance file wasn't created for binded object", f != null);
        assertTrue (f.isData ());

        // XXX:  hold IDO instance otherwise, it can be GCed and another instance of
        // objA will be lookuped from the context, this is bug of IDO (Intgere isn't
        // prop change events source)
        DataObject ido = DataObject.find (f);

        Object obj2 = context.lookup ("foo");
        assertTrue ("Bound object wasn't lookuped", obj2 != null);
        assertTrue ("Bind obj and lookup result are different", objA == obj2);
        
        // object and subcontext must coexist
        try {
            context.createSubcontext ("foo");
        } catch (Exception e) {
            fail ("Context with same name as object binding must coexist");
        }

        // but context has precedence when lookuped
        obj2 = context.lookup ("foo");
        assertTrue ("Subontext doesn't take precedence", obj2 instanceof Context);

        context.destroySubcontext ("foo");
        obj2 = context.lookup ("foo");
        assertTrue ("Subcontext destroyed, but object can't be found, obj2=" + obj2 + " objA=" + objA, obj2 == objA);

        // NameAlreadyBoundException when using the same name
        {
            NamingException exc = null;
            try {
                context.bind ("foo", objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NameAlreadyBoundException excpected when using the same name",
                exc != null);
        }
            
        // target context must exist
        {
            NamingException exc = null;
            try {
                context.bind ("ctx1/foo", objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NamingException expected when target context of bind operation doesn't exist",
                exc != null);
        }
        
        context.unbind ("foo");
        f = root.getFileObject ("foo", "settings");
        assertTrue ("Instance file wasn't destroyed for unbound object", f == null);
        
        // binding doesn't exist anymore
        {
            NamingException exc = null;
            try {
                obj2 = context.lookup ("foo");
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("Object is still reachable even if unbound", exc != null);
        }
        
        // unbind for non-existing binding succedes
        {
            NamingException exc = null;
            try {
                context.unbind ("foo");
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("unbind fails for non-exiting binding", exc == null);
        }
    }
    
    public void testRebind () throws Exception {
        Integer objA = new Integer (123);
        Integer objB = new Integer (321);
        Object obj;
        
        context.bind ("foo2", objA);
        obj = context.lookup ("foo2");
        assertEquals ("Original object not found", obj, objA);
        
        context.rebind ("foo2", objB);
        obj = context.lookup ("foo2");
        assertEquals ("New object not found", obj, objB);

        // rebind works even if object wasn't bound yet
        {
            NamingException exc = null;
            try {
                context.rebind ("foo3", objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("Rebind fails for non-exiting binding", exc == null);
        }

        // but target context and all intermediate contexts must exist
        {
            NamingException exc = null;
            try {
                context.rebind ("ctx1/ctx2/foo4", objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("Target context is neccessary for rebind", exc != null);
        }
    }
    
    public void testLinks () throws Exception {
        Integer objA = new Integer (123);
        context.bind ("fooXXX", objA);
        
        LinkRef link = new LinkRef ("fooXXX");
        context.bind ("link-to-fooXXX", link);
        
        Object o = context.lookup ("fooXXX");
        assertEquals ("foo not found", o, objA);
        
        o = context.lookupLink ("link-to-fooXXX");
        assertEquals ("link to foo not found", o, link);

        o = context.lookup ("link-to-fooXXX");
        assertEquals ("foo not found through the link", o, objA);
    }
    
    // XXX #27494 Names over 50 chars.
    public void testLongNameObjectSettings() throws Exception {
        implNameTest("ThisIsVeryLongNameOfInstanceFileToTestIssueDealingWithInstanceNamesWhichLenghtIsOver50Characters");
    }
    
    // XXX #27494 Names containing some special chars    
    public void testStrangeNameObjectSettings() throws Exception {
        implNameTest(":[]<>?*|.\"\u0020\u007E#");
    }

    // Almost the same like testBindObject
    private void implNameTest(String strangeName) throws Exception {
        Integer objA = new Integer (123);
        FileObject f;
        
        context.bind (strangeName, objA);

        Object obj2 = context.lookup (strangeName);
        assertTrue ("Bound object wasn't lookuped", obj2 != null);
        assertTrue ("Bind obj and lookup result are different", objA == obj2);
        
        // object and subcontext must coexist
        try {
            context.createSubcontext (strangeName);
        } catch (Exception e) {
            fail ("Context with same name as object binding must coexist");
        }

        // but context has precedence when lookuped
        obj2 = context.lookup (strangeName);
        assertTrue ("Subontext doesn't take precedence", obj2 instanceof Context);

        context.destroySubcontext (strangeName);
        obj2 = context.lookup (strangeName);

        // XXX Look at testBindObject: the IDO instance could be GC'ed, thus the new IDO created
        // and new instance too. This is a bug of IDO which we can't hold here some easy way.
//        assertTrue ("Subcontext destroyed, but object can't be found, obj2=" + obj2 + " objA=" + objA, obj2 == objA);
        assertEquals("Subcontext destroyed, but object can't be found, obj2=" + obj2 + " objA=" + objA, obj2, objA);

        // NameAlreadyBoundException when using the same name
        {
            NamingException exc = null;
            try {
                context.bind (strangeName, objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NameAlreadyBoundException excpected when using the same name",
                exc != null);
        }
            
        // target context must exist
        {
            NamingException exc = null;
            try {
                context.bind ("ctx1/"+strangeName, objA);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("NamingException expected when target context of bind operation doesn't exist",
                exc != null);
        }
        
        context.unbind (strangeName);
        f = root.getFileObject (strangeName, "settings");
        assertTrue ("Instance file wasn't destroyed for unbound object", f == null);
        
        // binding doesn't exist anymore
        {
            NamingException exc = null;
            try {
                obj2 = context.lookup (strangeName);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("Object is still reachable even if unbound", exc != null);
        }
        
        // unbind for non-existing binding succedes
        {
            NamingException exc = null;
            try {
                context.unbind (strangeName);
            } catch (NamingException e) {
                exc = e;
            }
            assertTrue ("unbind fails for non-exiting binding", exc == null);
        }
    }
    
}
