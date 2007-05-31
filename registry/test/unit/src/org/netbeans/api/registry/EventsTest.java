/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import javax.swing.*;
import java.util.ArrayList;

/**
 *
 * @author  David Konecny
 */
public class EventsTest extends NbTestCase {
    public EventsTest (String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(EventsTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }

    Context modificationContext;
    Context eventContext;
    boolean badMode = false;
    
    /** Test for issue #40498 */    
    public void testFromAtomicAction1 () throws Exception {
        //TODO:  #40578 - this test fails for MergedContexts         
        if (getClass().toString().indexOf("ReusedEventsTest") != -1) return;
        final Exception e[] = new Exception[1];
        final Context ctx = getRootContext().createSubcontext("testFromAtomicAction1");
        Listener listener = new Listener();
        ctx.addContextListener(listener);
        Repository.getDefault().getDefaultFileSystem().runAtomicAction(
            new FileSystem.AtomicAction() {
                public void run() throws java.io.IOException {
                    try {
                        String subcontextName = "sub1";
                        Context subcontext = ctx.createSubcontext(subcontextName);
                        assertEquals(subcontext, ctx.getSubcontext(subcontextName));
                        String bindingName = "binding";
                        subcontext.putString(bindingName, bindingName);
                        assertEquals(bindingName, subcontext.getString(bindingName, null));
                    } catch (Exception x) {
                        e[0] = x;
                    }
                }
            }
        );
        assertEquals(1,listener.s.size());
        assertEquals(1,listener.b.size());
        if (e[0] != null) {
            throw e[0];
        }
    }
    
    
    public void testEvents() throws Exception {
        Context ctx = getRootContext().createSubcontext("events");

        Listener listener = new Listener();
        ctx.addContextListener(listener);
        
        implTestEvents(listener, ctx, ctx);

        ctx.removeContextListener(listener);
        getRootContext().destroySubcontext("events");
    }
    
    public void testEvents2() throws Exception {
        Context ctx = getRootContext().createSubcontext("ev/en/ts");

        Listener listener = new Listener();
        getRootContext().addContextListener(listener);
        
        implTestEvents(listener, ctx, ctx);

        getRootContext().removeContextListener(listener);
        getRootContext().destroySubcontext("ev");
    }
    
    
    public void _EXCLUDE_testEvents3() throws Exception {

        /* This testcase is not very well supported at the moment.
         * Setting badMode to true which will ignore some inaccurate
         * values in events. This test is a bit hacky, but it does following:
         * it simulates separate registry based on the same root as the default one.
         * Any changes in this registry appear to the default registry as 
         * external modifications. Anyway, some events (although inaacurate)
         * should be fired.
         */
        badMode = true;
       
        BasicContext root = FileSystemContextFactory.createContext(Repository.getDefault().getDefaultFileSystem().getRoot());
        Context rootContext_  = SpiUtils.createContext(root);
        Context ctx = rootContext_.createSubcontext("ev1/en3/ts3");

        Listener listener = new Listener();
        getRootContext().addContextListener(listener);
        Context eventCtx = getRootContext().createSubcontext("ev1/en3/ts3");
        
        implTestEvents(listener, ctx, eventCtx);

        getRootContext().removeContextListener(listener);
        rootContext_.destroySubcontext("ev1");
    }
    
    public void implTestEvents(Listener listener, Context modificationCtx, Context eventCtx) throws Exception {
        modificationContext = modificationCtx;
        eventContext = eventCtx;

        // subcontext events 
        modificationContext.createSubcontext("abcd");
        assertTrue("Number of events does not match", listener.s.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((SubcontextEvent)listener.s.get(0)).getType() == SubcontextEvent.SUBCONTEXT_ADDED);
        assertTrue("Event name does not match", ((SubcontextEvent)listener.s.get(0)).getSubcontextName().equals("abcd"));
        assertTrue("Event context does not match", ((SubcontextEvent)listener.s.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.destroySubcontext("abcd");
        assertTrue("Number of events does not match", listener.s.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((SubcontextEvent)listener.s.get(0)).getType() == SubcontextEvent.SUBCONTEXT_REMOVED);
        assertTrue("Event name does not match", ((SubcontextEvent)listener.s.get(0)).getSubcontextName().equals("abcd"));
        assertTrue("Event context does not match", ((SubcontextEvent)listener.s.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        // binding events. objects

        // creating binding fires two events: BINDING_ADDED and BINDING_MODIFIED
        // is that OK? 
        modificationContext.putObject("dadada", new JLabel("456"));
        assertTrue("Number of events does not match", listener.b.size() == 2 && listener.all.size() == 2);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_ADDED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(1)).getType() == BindingEvent.BINDING_MODIFIED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(1)).getBindingName().equals("dadada"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(1)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.putObject("dadada", new JLabel("4565"));
        assertTrue("Number of events does not match", listener.b.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_MODIFIED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        // do not unbind. it will be used below for testing binding attributes

        // binding events. primitive data
        
        modificationContext.putInt("bind", 789);
        assertTrue("Number of events does not match", listener.b.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_ADDED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("bind"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.putInt("bind", 123);
        assertTrue("Number of events does not match", listener.b.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_MODIFIED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("bind"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        
        // attribute events 
        
        modificationContext.setAttribute("dadada", "aaatt", "value789");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_ADDED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.setAttribute("dadada", "aaatt", "value123");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_MODIFIED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        modificationContext.setAttribute("dadada", "aaatt", null);
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_REMOVED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        modificationContext.setAttribute("bind", "aaatt", "value789");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_ADDED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("bind"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.setAttribute("bind", "aaatt", "value123");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_MODIFIED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("bind"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        modificationContext.setAttribute("bind", "aaatt", null);
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_REMOVED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName().equals("bind"));
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        // binding event test - unbind the object
        
        modificationContext.putObject("dadada", null);
        assertTrue("Number of events does not match", listener.b.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_REMOVED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("dadada"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        modificationContext.putObject("bind", null);
        // does not test number of alll attribs, because there can be some ATTRIBUTE_REMOVED events
        assertTrue("Number of events does not match", listener.b.size() == 1);
        assertTrue("Event type does not match", ((BindingEvent)listener.b.get(0)).getType() == BindingEvent.BINDING_REMOVED);
        assertTrue("Event name does not match", ((BindingEvent)listener.b.get(0)).getBindingName().equals("bind"));
        assertTrue("Event context does not match", ((BindingEvent)listener.b.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        // attribute events for context
        
        modificationContext.setAttribute(null, "aaatt", "value789");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_ADDED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName() == null);
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
        
        modificationContext.setAttribute(null, "aaatt", "value123");
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_MODIFIED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName() == null);
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();

        modificationContext.setAttribute(null, "aaatt", null);
        assertTrue("Number of events does not match", listener.a.size() == 1 && listener.all.size() == 1);
        assertTrue("Event type does not match", ((AttributeEvent)listener.a.get(0)).getType() == AttributeEvent.ATTRIBUTE_REMOVED);
        assertTrue("Event bndname does not match", ((AttributeEvent)listener.a.get(0)).getBindingName() == null);
        assertTrue("Event name does not match", ((AttributeEvent)listener.a.get(0)).getAttributeName().equals("aaatt"));
        assertTrue("Event context does not match", ((AttributeEvent)listener.a.get(0)).getContext().equals(eventContext));
        assertNull(listener.error, listener.error);
        listener.reset();
    }

    protected Context getRootContext () {
        return Context.getDefault();    
    }


    public static class Listener implements ContextListener {

        ArrayList s;
        ArrayList a;
        ArrayList b;
        ArrayList all;
        String error = null;
        
        public Listener() {
            reset();
        }
        
        public void reset() {
            s = new ArrayList();
            b = new ArrayList();
            a = new ArrayList();
            all = new ArrayList();
            error = null;
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            s.add(evt);
            all.add(evt);
            if (evt.getType() == SubcontextEvent.SUBCONTEXT_ADDED) {
                if (evt.getContext().getSubcontext(evt.getSubcontextName()) == null) {
                    error = "Added subcontext must already exist: "+evt;
                }
            }
            if (evt.getType() == SubcontextEvent.SUBCONTEXT_REMOVED) {
                if (evt.getContext().getSubcontext(evt.getSubcontextName()) != null) {
                    error = "Removed subcontext must be already deleted: "+evt;
                }
            }
        }
    
        public void bindingChanged(BindingEvent evt) {
            b.add(evt);
            all.add(evt);
            if (evt.getType() == BindingEvent.BINDING_ADDED || evt.getType() == BindingEvent.BINDING_MODIFIED) {
                if (evt.getContext().getObject(evt.getBindingName(), null) == null) {
                    error = "Added or modified binding cannot have null value: "+evt;
                }
            }
            if (evt.getType() == BindingEvent.BINDING_REMOVED) {
                if (!evt.getContext().getObject(evt.getBindingName(), "abcd").equals("abcd")) {
                    error = "Removed binding must have null value: "+evt;
                }
            }
        }
        
        public void attributeChanged(AttributeEvent evt) {
            a.add(evt);
            all.add(evt);
            if (evt.getType() == AttributeEvent.ATTRIBUTE_ADDED || evt.getType() == AttributeEvent.ATTRIBUTE_MODIFIED) {
                if (evt.getContext().getAttribute(evt.getBindingName(), evt.getAttributeName(), null) == null) {
                    error = "Added or modified attribute cannot have null value: "+evt;
                }
            }
            if (evt.getType() == AttributeEvent.ATTRIBUTE_REMOVED) {
                if (!evt.getContext().getAttribute(evt.getBindingName(), evt.getAttributeName(), "abcd").equals("abcd")) {
                    error = "Removed attribute must have null value: "+evt;
                }
            }
        }
        
    }

}
