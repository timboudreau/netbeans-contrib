/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.naming;

import javax.naming.*;
import java.io.File;
import javax.swing.Action;
import java.util.*;

import org.netbeans.junit.*;
import junit.textui.TestRunner;

import org.openide.util.Lookup;
import org.openide.loaders.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;

/** Checks whether changes in system filesystem are correctly propagated 
 * to our JNDI provider.
 *
 * @author Jaroslav Tulach
 */
public class JndiFindTest extends NbTestCase {
    /** context to search for objects in */
    private Context context;
    /** root folder FileObject */
    private FileObject folder;
    /** root DataFolder */
    private DataFolder root;

    public JndiFindTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(JndiFindTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        
        context = new Jndi().getInitialContext (null);

        folder = Repository.getDefault ().getDefaultFileSystem().getRoot ();
        root = DataFolder.findFolder (folder);
        
        FileObject fo = folder.getFileObject("My");
        if (fo != null) {
            fo.delete ();
        }
    }
    
    
    public void testFindRootContext () throws Exception {
        Object o = context.lookup ("");
        assertTrue (o instanceof Context);
        Context c = (Context)o;
        assertTrue (c.getNameInNamespace ().equals (""));
        // "/" is the same as ""
        assertTrue (o == context.lookup ("/"));
        
    }

    
    public void testFindIDO () throws Exception {
        String fname = "My/Space/That";
        String iname = "Integer";
        InstanceDataObject.create (
            DataFolder.create (root, fname),
            iname,
            new Integer (100),
            null
        );
     
        Object o = context.lookup (fname + "/" + iname);
        
        assertTrue (o instanceof Integer);
        assertTrue (((Integer)o).intValue () == 100);
    }
    
    public void testFindInContext () throws Exception {
        String fname = "My/Space/Context";
        int cnt = 1;
        
        DataFolder df = DataFolder.create (root, fname);
        
        for (int i = 0; i < cnt; i++) {
            InstanceDataObject.create (
                df, null, new Integer (i), null
            );
        }
     
        Object o = context.lookup (fname);
        
        assertTrue (o instanceof Context);
        Context c = (Context)o;
        
        // check if bindings are ok
        {
            // check that each object is found just once and there is exact number of objects
            boolean[] all = new boolean[cnt];
            
            NamingEnumeration en = c.listBindings ("");
            int count = 0;
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                
                assertTrue (b.getClassName().equals ("java.lang.Integer"));
                assertTrue (b.getObject () instanceof Integer);
                
                int indx = ((Integer)b.getObject ()).intValue ();
                assertTrue ("Index: " + indx, indx >= 0);
                assertTrue ("Index: " + indx, indx < all.length);
                assertTrue ("Index: " + indx, !all[indx]);
                
                all[indx] = true;
            }
        }
        
        // add an object an make it first
        InstanceDataObject obj = InstanceDataObject.create (df, "Last", new Integer (-1), null);
        df.setOrder (new DataObject[] { obj });
        
        // should be first in context
        {
            NamingEnumeration en = c.listBindings ("");
            assertTrue (en.hasMore ());
            Object last = ((Binding)en.next()).getObject ();
            assertTrue ("Last: " + last, last instanceof Integer);
            assertTrue (((Integer)last).intValue () == -1);
        }
        
        obj.delete ();
        
        // should disappear
        {
            NamingEnumeration en = c.listBindings ("");
            int count = 0;
            while (en.hasMore ()) {
                en.next ();
                count++;
            }
            
            assertTrue (count == cnt);
        }
    }

    /** Mixing lookup of instance with lookup of context.
     */
    public void testSingleVsContext () throws Exception {
        String fname = "My/Space/That";
        String iname = "Integer";
        
        DataFolder df = DataFolder.create (root, fname);
        InstanceDataObject.create (
            df,
            iname,
            new Integer (100),
            null
        );
        
     
        Object o = context.lookup (fname + "/" + iname);
        assertTrue ("We have created instance of integer", o instanceof Integer);
        
        DataFolder n = DataFolder.create (df, iname);
        
        assertTrue ("The context should take preceedence now", context.lookup (fname + "/" + iname) instanceof Context);
        
        // be sure that the enumeration for that context also contains the 
        // object added before
        NamingEnumeration en = context.listBindings (fname + "/" + iname);
        assertTrue ("When collision between context and object the context contains nothing", en.hasMore ());
        o = ((Binding)en.next ()).getObject();
        assertTrue ("The context does not contain the correct object but " + o, o instanceof Integer);
        
        
        n.delete ();
        
        assertTrue ("Context is gone, the object should be there again", context.lookup (fname + "/" + iname) instanceof Integer);
    }
    
    /** Searching for single object using listBindings should work.
     */
    public void testSingleBinding () throws Exception {
        String fname = "My/Space/Single";
        String iname = "Integer";
        
        DataFolder df = DataFolder.create (root, fname);
        InstanceDataObject.create (
            df,
            iname,
            new Integer (100),
            null
        );
     
        assertNotNull ("Integer really exists", context.lookup (fname + "/" + iname));
        
        NamingEnumeration en = context.listBindings (fname + "/" + iname);
        
        assertTrue ("Enum has at least one member", en.hasMoreElements ());
        
        Binding b = (Binding)en.next();
        assertTrue ("It is integer", b.getObject () instanceof Integer);
        
        assertTrue ("And it is the last element", !en.hasMoreElements ());
    }

    public void testThatEmptyBindingReturnsEmptyEnumeration () throws Exception {
        String fname = "My/Space/NonExistingName";
        javax.naming.Name nname = context.getNameParser(fname).parse(fname);
        
        assertTrue ("Empty enum for listBindings (String)", !context.listBindings (fname).hasMoreElements ());
        assertTrue ("Empty enum for listBindings (Name)", !context.listBindings (nname).hasMoreElements ());
        assertTrue ("Empty enum for list (String)", !context.list (fname).hasMoreElements ());
        assertTrue ("Empty enum for list (Name)", !context.list (nname).hasMoreElements ());
    }

    /** Test own classloader.
     */
    public void testOwnClassLoader () throws Exception {
        Hashtable hashtable = new Hashtable ();
        hashtable.put (Context.INITIAL_CONTEXT_FACTORY, "mycontext");
     
        final IllegalStateException[] arr = new IllegalStateException [1];
        class CL extends ClassLoader {
            public Class findClass (String name) {
                if (name.equals ("mycontext")) {
                    arr[0] = new IllegalStateException ("Excelent!");
                    throw arr[0];
                }
                return null;
            }
        }
        
        // register a class loader to use
        hashtable.put (ClassLoader.class, new CL ());
        
        try {
            javax.naming.spi.NamingManager.setInitialContextFactoryBuilder(new Jndi());
            new InitialContext (hashtable);
        } catch (IllegalStateException ex) {
            if (ex == arr[0]) {
                return;
            }
        }
        
        fail ("The correct classloader has not been called");
    }
        
}
