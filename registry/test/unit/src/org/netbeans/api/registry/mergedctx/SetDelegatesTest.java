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
package org.netbeans.api.registry.mergedctx;

import junit.textui.TestRunner;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextAdapter;
import org.netbeans.api.registry.SubcontextEvent;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.registry.ApiContextFactory;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.MergedContextProvider;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class SetDelegatesTest extends NbTestCase{
    public SetDelegatesTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(SetDelegatesTest.class));
    }

    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }

    public void testSetDelegates () throws Exception {
        Context defCtx = Context.getDefault();
        Context delegCtx1 = defCtx.createSubcontext("first");
        Context delegCtx2 = defCtx.createSubcontext("second");
        Context delegCtx3 = defCtx.createSubcontext("third");

        delegCtx1.createSubcontext("A");
        delegCtx2.createSubcontext("B");
        delegCtx3.createSubcontext("C");

        MergeContextProviderImpl mergeProvider =
                new MergeContextProviderImpl (new Context[] {delegCtx1, delegCtx2, delegCtx3});
        Context rootCtx = SpiUtils.createContext(SpiUtils.merge(mergeProvider));

        assertTrue(null !=  rootCtx.getSubcontext("A"));
        assertTrue(null !=  rootCtx.getSubcontext("B"));
        assertTrue (null != rootCtx.getSubcontext("C"));

        final List subEvtList = new ArrayList ();
        final List bindingEvtList = new ArrayList ();

        rootCtx.addContextListener(new ContextAdapter() {
            public void subcontextChanged(SubcontextEvent evt) {
                subEvtList.add(evt);
            }

            public void bindingChanged(BindingEvent evt) {
                bindingEvtList.add(evt);
            }
        });

        rootCtx.getSubcontextNames();
        String binding = "stBinding";
        delegCtx3.putString(binding, binding);
        assertEquals(binding,rootCtx.getObject(binding, "defValue"));
        assertEquals(1, bindingEvtList.size());
        BindingEvent be = (BindingEvent)bindingEvtList.get(0);
        assertEquals(BindingEvent.BINDING_ADDED, be.getType());
        bindingEvtList.clear();


        mergeProvider.setDelegates(new Context[] {delegCtx1, delegCtx2});
        assertEquals("defValue",rootCtx.getObject(binding, "defValue"));
        assertTrue(null !=  rootCtx.getSubcontext("A"));
        assertTrue(null !=  rootCtx.getSubcontext("B"));
        assertEquals(null, rootCtx.getSubcontext("C"));
        assertEquals(1, subEvtList.size());
        SubcontextEvent se = (SubcontextEvent)subEvtList.get(0);
        assertEquals(SubcontextEvent.SUBCONTEXT_REMOVED,  se.getType());
        assertEquals(1, bindingEvtList.size());
        be = (BindingEvent)bindingEvtList.get(0);
        assertEquals(BindingEvent.BINDING_REMOVED, be.getType());
        subEvtList.clear();
        bindingEvtList.clear();


        mergeProvider.setDelegates(new Context[] {delegCtx1, delegCtx2, delegCtx3});
        assertEquals(binding,rootCtx.getObject(binding, "defValue"));
        assertTrue(null !=  rootCtx.getSubcontext("A"));
        assertTrue(null !=  rootCtx.getSubcontext("B"));
        assertTrue (null != rootCtx.getSubcontext("C"));
        assertEquals(1, subEvtList.size());
        se = (SubcontextEvent)subEvtList.get(0);
        assertEquals(SubcontextEvent.SUBCONTEXT_ADDED,  se.getType());
        assertEquals(1, bindingEvtList.size());
        be = (BindingEvent)bindingEvtList.get(0);
        assertEquals(BindingEvent.BINDING_ADDED, be.getType());
        subEvtList.clear();
        bindingEvtList.clear();
    }

    private static final class MergeContextProviderImpl implements MergedContextProvider {
        BasicContext[] delegates;
        PropertyChangeSupport  pListenerSuport;
        public MergeContextProviderImpl (Context[] delegates){
            pListenerSuport = new PropertyChangeSupport(this);
            setDelegates(delegates, false);
        }

        public void setDelegates(Context[] delegates) {
            setDelegates(delegates, true);
        }

        private void setDelegates(Context[] delegates, boolean fire) {
            this.delegates =  new BasicContext[delegates.length];
            for (int i = 0; i < delegates.length; i++) {
                this.delegates[i] = ApiContextFactory.DEFAULT.getBasicContext(delegates[i]);
            }
            if (fire)
                pListenerSuport.firePropertyChange(PROP_DELEGATES, null, null);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pListenerSuport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pListenerSuport.removePropertyChangeListener(listener);
        }

        public BasicContext[] getDelegates() {
            return delegates;
        }
    }
}
