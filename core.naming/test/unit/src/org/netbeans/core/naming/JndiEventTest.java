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


import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;


/** Checks whether changes in system filesystem are correctly propagated 
 * to listeners on our JNDI provider.
 *
 * @author Peter Zavadsky
 */
public class JndiEventTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject folder;
    /** root DataFolder */
    private DataFolder root;


    public JndiEventTest(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(JndiFindTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        folder = Repository.getDefault ().getDefaultFileSystem().getRoot ();
        root = DataFolder.findFolder (folder);
        
        FileObject fo = folder.getFileObject("My"); // NOI18N
        if (fo != null) {
            fo.delete ();
        }
    }
    
    protected void tearDown() throws Exception {
        FileObject fo = folder.getFileObject("My"); // NOI18N
        if(fo != null) {
            fo.delete();
        }
    }
    

    public void testAddObjectScopeListener() throws Exception {
        implTestAddListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testAddOneLevelScopeListener() throws Exception {
        implTestAddListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testAddSubTreeScopeListener() throws Exception {
        implTestAddListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestAddListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        context.addNamingListener(target, scope, l); // NOI18N
        subContext.bind("object", new Integer(1));
        context.removeNamingListener(l);

        assertNull("Remove event may not arrive", l.removedEvent);
        assertNull("Rename event may not arrive", l.renamedEvent);
        // Needs to be found out why there is always 
        // file changed event fired too. And if to hack it at the level.
        //assertNull("Change event may not arrive", l.changeEvent);
        
        NamingEvent evt = l.addedEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_ADDED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("New binding has wrong name",
                "My/object", evt.getNewBinding().getName());
    }
    
    
    public void testRemoveObjectScopeListener() throws Exception {
        implTestRemoveListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testRemoveOneLevelScopeListener() throws Exception {
        implTestRemoveListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testRemoveSubTreeScopeListener() throws Exception {
        implTestRemoveListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestRemoveListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        subContext.bind("object", new Integer(1));

        context.addNamingListener(target, scope, l); // NOI18N
        myFolder.getFileObject("object", "settings").delete();
        context.removeNamingListener(l);

        assertNull("Add event may not arrive", l.addedEvent);
        assertNull("Rename event may not arrive", l.renamedEvent);
        assertNull("Change event may not arrive", l.changeEvent);
        
        NamingEvent evt = l.removedEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_REMOVED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("Old binding has wrong name",
                "My/object", evt.getOldBinding().getName());
    }

    
    public void testRenameObjectScopeListener() throws Exception {
        implTestRenameListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testRenameOneLevelScopeListener() throws Exception {
        implTestRenameListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testRenameSubTreeScopeListener() throws Exception {
        implTestRenameListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestRenameListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        subContext.bind("object", new Integer(1));
        
        context.addNamingListener(target, scope, l); // NOI18N
        context.rename("My/object", "My/newObject");
        context.removeNamingListener(l);

        assertNull("Add event may not arrive", l.addedEvent);
        assertNull("Remove event may not arrive", l.removedEvent);
        assertNull("Change event may not arrive", l.changeEvent);
        
        NamingEvent evt = l.renamedEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_RENAMED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("New binding has wrong name",
                "My/newObject", evt.getNewBinding().getName());
        assertEquals("Old binding has wrong name",
                "My/object", evt.getOldBinding().getName());
    }
    
    public void testChangeObjectScopeListener() throws Exception {
        implTestChangeListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testChangeOneLevelScopeListener() throws Exception {
        implTestChangeListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testChangeSubTreeScopeListener() throws Exception {
        implTestChangeListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestChangeListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        subContext.bind("object", new Integer(1));
        
        context.addNamingListener(target, scope, l); // NOI18N
        InstanceDataObject.create(
            DataFolder.findFolder(myFolder),
            "object",
            new Integer(2),
            null);
        context.removeNamingListener(l);

        assertNull("Add event may not arrive", l.addedEvent);
        assertNull("Remove event may not arrive", l.removedEvent);
        assertNull("Rename event may not arrive", l.renamedEvent);
        
        NamingEvent evt = l.changeEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_CHANGED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("New binding has wrong name",
                "My/object", evt.getNewBinding().getName());
    }

    
    public void testInfoObjectScopeListener() throws Exception {
        implTestInfoListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testInfoOneLevelScopeListener() throws Exception {
        implTestInfoListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testInfoSubTreeScopeListener() throws Exception {
        implTestInfoListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestInfoListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        context.addNamingListener(target, scope, l); // NOI18N
        subContext.bind("object", new Integer(1));
        context.removeNamingListener(l);

        NamingEvent evt = l.addedEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_ADDED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("New binding has wrong name",
                "My/object", evt.getNewBinding().getName());
        assertSame("The info context is wrong", subContext, evt.getChangeInfo());
    }

    
    public void testCookieChangeObjectScopeListener() throws Exception {
        implTestCookieChangeListener("My/object", EventContext.OBJECT_SCOPE);
    }
    
    public void testCookieChangeOneLevelScopeListener() throws Exception {
        implTestCookieChangeListener("My", EventContext.ONELEVEL_SCOPE);
    }
    
    public void testCookieChangeSubTreeScopeListener() throws Exception {
        implTestCookieChangeListener("", EventContext.SUBTREE_SCOPE);
    }
    
    private void implTestCookieChangeListener(String target, int scope) throws Exception {
        EventContext context = (EventContext)new Jndi().getInitialContext(null);
        JndiNamingListener l = new JndiNamingListener();
        FileObject myFolder = folder.createFolder("My"); // NOI18N
        EventContext subContext = (EventContext)context.lookup("My"); // NOI18N
        subContext.bind("object", new Integer(1));

        MultiDataObject dobj = (MultiDataObject)DataObject.find(
                myFolder.getFileObject("object", "settings"));
        java.lang.reflect.Method m
                = MultiDataObject.class.getDeclaredMethod("getCookieSet", new Class[0]);
        m.setAccessible(true);
        CookieSet cookies = (CookieSet)m.invoke(dobj, new Class[0]);

        context.addNamingListener(target, scope, l); // NOI18N
        class DummyCookie implements Node.Cookie {
        };
        cookies.add(new DummyCookie());
        context.removeNamingListener(l);

        assertNull("Add event may not arrive", l.addedEvent);
        assertNull("Remove event may not arrive", l.removedEvent);
        assertNull("Rename event may not arrive", l.renamedEvent);
        
        NamingEvent evt = l.changeEvent;
        assertNotNull("Event has not arrived", evt);
        assertEquals("Event has wrong type",
                NamingEvent.OBJECT_CHANGED, evt.getType());
        assertSame("Event has wrong context", context, evt.getEventContext());
        assertEquals("New binding has wrong name",
                "My/object", evt.getNewBinding().getName());
    }

    private static class JndiNamingListener
    implements NamespaceChangeListener, ObjectChangeListener {
        private NamingEvent addedEvent;
        private NamingEvent removedEvent;
        private NamingEvent renamedEvent;
        private NamingEvent changeEvent;
        private NamingExceptionEvent exceptionEvent;
        
        // NamespaceChangeListener implementation>>
        public void objectAdded(NamingEvent evt) {
            addedEvent = evt;
        }

        public void objectRemoved(NamingEvent evt) {
            removedEvent = evt;
        }

        public void objectRenamed(NamingEvent evt) {
            renamedEvent = evt;
        }
        // NamespaceChangeListener implementation<<

        // ObjectChangeListener implementation>>
        public void objectChanged(NamingEvent evt) {
            changeEvent = evt;
        }
        // ObjectChangeListener implementation<<
        
        // NamingListener implementation>>
        public void namingExceptionThrown(NamingExceptionEvent evt) {
            exceptionEvent = evt;
        }
        // NamingListener implementation<<
    }    
    
}
