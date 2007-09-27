/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.api.registry.mergedctx;

import junit.textui.TestRunner;
import org.netbeans.api.registry.*;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import java.util.*;

public class ImplReferenceTest extends NbTestCase {
    public ImplReferenceTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ImplReferenceTest.class));
    }

    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        getMergeContext();
    }

    Context getMergeContext() {return SetUpUtils.getSimpleContext(Context.getDefault());}
    Context getActiveDelegate() {return SetUpUtils.getSubctx1();}
    Context getReadOnlyDelegate1() {return SetUpUtils.getSubctx2();}
    Context getReadOnlyDelegate2() {return SetUpUtils.getSubctx3();}

    public  void testInstances () throws Exception {
        // init
        Context rootCtx = getMergeContext();

        //test
        List list = new ArrayList();
        Context test = rootCtx;
        list.add(test);
        Enumeration en =  new StringTokenizer("/testRootOfMergeContext/A/B/C/D/E/F", "/");
        while (en.hasMoreElements()) {
            String s = (String)en.nextElement();
            test = test.createSubcontext(s);
            assertNotNull(test);
            assertEquals(-1, list.indexOf(test));
            list.add(test);
        }

        en =  new StringTokenizer(test.getAbsoluteContextName(), "/");
        while (en.hasMoreElements()) {
            en.nextElement();
            assertNotNull(test);
            assertTrue(list.indexOf(test) != -1);
            assertEquals(System.identityHashCode(test), System.identityHashCode(list.get(list.indexOf(test))));
            test = test.getParentContext();
        }

        // we reached root
        assertTrue(list.indexOf(test) != -1);
        assertNull(test.getParentContext());

        rootCtx.destroySubcontext("testRootOfMergeContext");
        en =  new StringTokenizer("/testRootOfMergeContext/A/B/C/D/E/F", "/");
        while (en.hasMoreElements()) {
            String s = (String)en.nextElement();
            test = test.createSubcontext(s);
            assertNotNull(test);
            assertEquals(-1, list.indexOf(test));
        }

        en =  new StringTokenizer(test.getAbsoluteContextName(), "/");
        while (en.hasMoreElements()) {
            en.nextElement();
            assertNotNull(test);
            assertTrue(list.indexOf(test) == -1);
            test = test.getParentContext();
        }

        // we reached root
        assertTrue(list.indexOf(test) != -1);
        assertNull(test.getParentContext());

    }

    /**
     * Destroy or creation of subcontext on delegates must affect merged context
     *
     */
    public void testMergeFunctionality () throws Exception {
        // init
        Context rootCtx = getMergeContext();
        Context activeDelegate = getActiveDelegate ();
        Context roDelegate1 = getReadOnlyDelegate1 ();
        Context roDelegate2 = getReadOnlyDelegate2();

        String subcontextName = "testMergeFunctionality";
        String activeDelegateName = "A";
        String roDelegate1Name = "B";
        String roDelegate2Name = "C";
        TestContextListener l = new TestContextListener();
        roDelegate1.createSubcontext(subcontextName).createSubcontext(roDelegate1Name);
        roDelegate2.createSubcontext(subcontextName).createSubcontext(roDelegate2Name);
        rootCtx.addContextListener(l);

        // test
        Context test = rootCtx.getSubcontext(subcontextName);
        assertNotNull(test);
        assertNull(test.getSubcontext(activeDelegateName));
        assertNotNull(test.getSubcontext(roDelegate1Name));
        assertNotNull(test.getSubcontext(roDelegate2Name));


        activeDelegate = activeDelegate.createSubcontext(subcontextName);
        assertEquals(0, l.s.size());// subcontextName already exist on r/o delegates
        assertEquals(0, l.all.size());

        activeDelegate.createSubcontext(activeDelegateName);
        assertEquals(1, l.s.size()); //activeDelegateName doesn't exist yet
        assertEquals(1, l.all.size());
        assertNotNull(test.getSubcontext(activeDelegateName)); //already exist
        assertNotNull(test.getSubcontext(roDelegate1Name));
        assertNotNull(test.getSubcontext(roDelegate2Name) );

        l = new TestContextListener();
        rootCtx.addContextListener(l);
        activeDelegate.destroySubcontext(activeDelegateName);
        assertEquals(1, l.s.size());
        assertEquals(1, l.all.size());
        assertNull(test.getSubcontext(activeDelegateName));//doesn't exist
        assertNotNull(test.getSubcontext(roDelegate1Name));
        assertNotNull(test.getSubcontext(roDelegate2Name));

        l = new TestContextListener();
        rootCtx.addContextListener(l);
        activeDelegate = activeDelegate.getParentContext();
        activeDelegate.destroySubcontext(subcontextName);
        assertEquals(0, l.s.size());
        assertEquals(0, l.all.size());

        assertNotNull(rootCtx.getSubcontext(subcontextName));
        assertNotNull(roDelegate1.getSubcontext(subcontextName));
        assertNotNull(roDelegate2.getSubcontext(subcontextName));
    }

    /**
     * Simply tests binding events
     */
    public void testSimpleBindingEvents  () throws Exception {
        Context rootCtx = getMergeContext();

        Context activeDelegate = getActiveDelegate ();
        Context roDelegate1 = getReadOnlyDelegate1 ();
        Context roDelegate2 = getReadOnlyDelegate2 ();

        String subcontextName = "testSimpleBindingEvents";
        String subSubcontextName1 = "TMA";
        String subSubcontextName2 = "TMB";
        String subSubcontextName3 = "TMC";

        activeDelegate = activeDelegate.createSubcontext(subcontextName).createSubcontext(subSubcontextName1);
        roDelegate1 = roDelegate1.createSubcontext(subcontextName).createSubcontext(subSubcontextName2);
        roDelegate2 = roDelegate2.createSubcontext(subcontextName).createSubcontext(subSubcontextName3);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);
        Context hard = rootCtx.getSubcontext("/"+subcontextName+"/"+subSubcontextName1);

        String bindingName = "tmBinding";
        activeDelegate.putString(bindingName, bindingName);

        assertEquals(1, l.b.size());
        assertEquals(1, l.all.size());
        assertEquals(BindingEvent.BINDING_ADDED,((BindingEvent)l.b.get(0)).getType());
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));

        System.setProperty("tst","tst");
        activeDelegate.putString(bindingName, null);
        System.setProperty("tst","");

        assertEquals(2, l.b.size());
        assertEquals(2, l.all.size());
        assertEquals(BindingEvent.BINDING_REMOVED,((BindingEvent)l.b.get(1)).getType());
        assertEquals("defVal",hard.getString(bindingName,"defVal"));
    }

    /**
     * No instance of merged context doesn't fire events
     */
    public void testNoInstanceNoBindingEvent () throws Exception {
        Context rootCtx = getMergeContext();
        Context activeDelegate = getActiveDelegate ();
        Context roDelegate1 = getReadOnlyDelegate1 ();
        Context roDelegate2 = getReadOnlyDelegate2 ();

        String subcontextName = "testNoInstanceNoBindingEvent";
        String activeDelegateName = "TMA2";
        String roDelegate1Name = "TMB2";
        String roDelegate2Name = "TMC2";
        String bindingName = "tmBinding2";

        activeDelegate = activeDelegate.createSubcontext(subcontextName).createSubcontext(activeDelegateName);
        roDelegate1 = roDelegate1.createSubcontext(subcontextName).createSubcontext(roDelegate1Name);
        roDelegate2 = roDelegate2.createSubcontext(subcontextName).createSubcontext(roDelegate2Name);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        // there doesn't exist merged context with activeDelegate as its delegate
        activeDelegate.putString(bindingName, bindingName);
        activeDelegate.putString(bindingName, null);
        assertEquals(0, l.b.size());
        assertEquals(0, l.all.size());

        Context test = rootCtx.getSubcontext(subcontextName).getSubcontext(activeDelegateName);
        activeDelegate.putString(bindingName, bindingName);
        activeDelegate.putString(bindingName, null);
        assertEquals(2, l.b.size());
        assertEquals(2, l.all.size());
    }

    public void testBindingAttributesAreNotMerged () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testBindingAttributesAreNotMerged";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName = "TMA3";

        ctx1 = ctx1.createSubcontext(subSubcontextName);
        ctx2 = ctx2.createSubcontext(subSubcontextName);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        Context hard = rootCtx.getSubcontext("/" + subcontextName + "/" + subSubcontextName);

        String bindingName = "tmBinding";
        String attrName = "tmAttr";

        ctx2.putString(bindingName, bindingName);
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        ctx2.setAttribute(bindingName, attrName, attrName);

        assertEquals(attrName,hard.getAttribute(bindingName, attrName, "defValue"));

        // new binding means no previous attributes are overtaken
        ctx1.putString(bindingName, bindingName);
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        assertEquals("defValue",hard.getAttribute(bindingName, attrName, "defValue"));
    }

    /**
     * Once deleted binding masks read-only delegates forever.
     *
     */
    public void testMaskedBindings () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testMaskedBindings";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA4";

        ctx1 = ctx1.createSubcontext(subSubcontextName1);
        ctx2 = ctx2.createSubcontext(subSubcontextName1);

        Context hard = rootCtx.getSubcontext("/"+ subcontextName + "/"+ subSubcontextName1);

        String bindingName = "tmBinding";
        String attrName = "tmAttr";

        ctx2.putString(bindingName, bindingName);
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        ctx2.setAttribute(bindingName, attrName, attrName);
        assertEquals(attrName,hard.getAttribute(bindingName, attrName, "defValue"));

        hard.putString(bindingName, null);
        assertEquals("defVal",hard.getString(bindingName,"defVal"));
        //ctx2.setAttribute(bindingName, attrName, attrName);
        assertEquals("defValue",hard.getAttribute(bindingName, attrName, "defValue"));

        ctx2.putString(bindingName, bindingName);
        assertEquals("defVal",hard.getString(bindingName,"defVal"));
        ctx2.setAttribute(bindingName, attrName, attrName);
        assertEquals("defValue",hard.getAttribute(bindingName, attrName, "defValue"));


        ctx1.putString(bindingName, bindingName);
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        ctx1.setAttribute(bindingName, attrName, attrName);
        assertEquals(attrName,hard.getAttribute(bindingName, attrName, "defValue"));
    }

    public void testBindingChangeFollowedByAttributeChange () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();

        String subcontextName = "testBindingChangeFollowedByAttributeChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA5";

        ctx1 = ctx1.createSubcontext(subSubcontextName1);
        ctx2 = ctx2.createSubcontext(subSubcontextName1);

        Context hard = rootCtx.getSubcontext("/"+subcontextName+"/"+subSubcontextName1);

        String bindingName = "tmBinding";
        String attrName = "tmAttr";

        TestContextListener l1 = new TestContextListener();
        rootCtx.addContextListener(l1);

        ctx2.putString(bindingName, bindingName);
        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        assertEquals(1, l1.b.size());
        assertEquals(BindingEvent.BINDING_ADDED, ((BindingEvent)l1.b.get(0)).getType());
        assertEquals(1, l1.all.size());

        ctx2.setAttribute(bindingName, attrName, attrName);
        assertEquals(attrName,hard.getAttribute(bindingName, attrName, "defValue"));

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        ctx1.putString(bindingName, bindingName);

        assertEquals(bindingName,hard.getString(bindingName,"defVal"));
        assertEquals("defValue",hard.getAttribute(bindingName, attrName, "defValue"));
        assertEquals(1, l.b.size());
        assertEquals(BindingEvent.BINDING_MODIFIED, ((BindingEvent)l.b.get(0)).getType());

        assertEquals(1, l.a.size());
        assertEquals(AttributeEvent.ATTRIBUTE_REMOVED, ((AttributeEvent)l.a.get(0)).getType());
    }

    public void testBindingChangeNotFollowedByAttributeChange () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();

        String subcontextName = "testBindingChangeNotFollowedByAttributeChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA";

        ctx1 = ctx1.createSubcontext(subSubcontextName1);
        ctx2 = ctx2.createSubcontext(subSubcontextName1);

        Context hard = rootCtx.getSubcontext("/"+subcontextName+"/"+subSubcontextName1);

        String bindingName = "tmBinding";

        TestContextListener l1 = new TestContextListener();
        rootCtx.addContextListener(l1);

        hard.putString(bindingName, bindingName);
        assertEquals(1, l1.b.size());
        assertEquals(BindingEvent.BINDING_ADDED, ((BindingEvent)l1.b.get(0)).getType());
        assertEquals(1, l1.all.size());

        hard.putString(bindingName, "newBinding");

        assertEquals(2, l1.b.size());
        assertEquals(BindingEvent.BINDING_MODIFIED, ((BindingEvent)l1.b.get(1)).getType());
        assertEquals(2, l1.all.size());

        hard.putString(bindingName, null);
        assertEquals(3, l1.b.size());
        assertEquals(BindingEvent.BINDING_REMOVED, ((BindingEvent)l1.b.get(2)).getType());
        assertEquals(3, l1.all.size());
    }

    public void testEventAfterSubCtxDestroy () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testEventAfterSubCtxDestroy";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA6";

        ctx1 = ctx1.createSubcontext(subSubcontextName1);
        ctx2 = ctx2.createSubcontext(subSubcontextName1);

        Context hard = rootCtx.getSubcontext("/" + subcontextName + "/" + subSubcontextName1);

        ctx1.createSubcontext(subSubcontextName1);
        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);
        ctx1.destroySubcontext(subSubcontextName1);

        assertNull(hard.getSubcontext(subSubcontextName1));
        assertEquals(1, l.s.size());
        assertEquals(1, l.all.size());
        assertEquals(SubcontextEvent.SUBCONTEXT_REMOVED, ((SubcontextEvent)l.s.get(0)).getType());
    }

    public void testEventsAfterRecursiveDelete () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testEventsAfterRecursiveDelete";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA7";

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);
        List list = new ArrayList();
        Context temp = hard;

        for (int i = 0; i < 10; i++) {
            Context temp2 = temp.createSubcontext(subSubcontextName1);
            ctx1 = ctx1.getSubcontext(subSubcontextName1);
            assertNotNull(ctx1);
            assertNotNull(temp2);
            assertNotNull(temp.getSubcontext(subSubcontextName1));
            temp = temp2;
            list.add(temp2);
        }

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);
        assertNotNull(hard.getSubcontext(subSubcontextName1));
        hard.destroySubcontext(subSubcontextName1);
        assertEquals(10, l.s.size());
    }

    public void testRecreatedContextCantContainBindings () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testRecreatedContextCantContainBindings";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName1 = "TMA8";

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);
        Context temp = hard;
        temp.putString(subSubcontextName1,subSubcontextName1);

        for (int i = 0; i < 10; i++) {
            temp  = temp.createSubcontext(subSubcontextName1);
            temp.putString(subSubcontextName1,subSubcontextName1);
            ctx1 = ctx1.getSubcontext(subSubcontextName1);
            assertNotNull(ctx1);
            assertNotNull(temp);
        }

        assertNotNull(hard.getSubcontext(subSubcontextName1));
        hard.destroySubcontext(subSubcontextName1);

        temp = hard;
        for (int i = 0; i < 10; i++) {
            temp  = temp.createSubcontext(subSubcontextName1);
            assertNull(temp.getObject(subSubcontextName1,null));
            assertNotNull(temp);
        }

    }

    public void testCtxChangeFollowedByBindingChange() throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testCtxChangeFollowedByBindingChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.putString("b1","b1");

        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString("b2","b2");

        ctx3 = ctx3.createSubcontext(subcontextName);
        ctx3.putString("b3","b3");

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        ctx1 = ctx1.getParentContext();
        assertNotNull(ctx1);

        System.setProperty("test1", "test1");
        ctx1.destroySubcontext(subcontextName);
        System.setProperty("test1", "");

        assertEquals(1,l.b.size());
        assertEquals(1,l.all.size());
    }

    public void testCtxChangeNotFollowedByBindingChange()  throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testCtxChangeNotFollowedByBindingChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.putString("b1","b1");

        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString("b2","b2");

        ctx3 = ctx3.createSubcontext(subcontextName);
        ctx3.putString("b3","b3");

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        hard = hard.getParentContext();
        assertNotNull(hard);

        System.setProperty("test1", "test1");
        hard.destroySubcontext(subcontextName);
        System.setProperty("test1", "");

        assertEquals(0,l.b.size()); //OK
        assertEquals(1,l.all.size());
    }

    public void testAttributes () throws Exception {
        String bindingName = "b";
        String attributeName = "a";

        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testAttributes";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subcontextName1 = "subcontextName1";
        ctx1 = ctx1.createSubcontext(subcontextName1);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName1);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName1);

        Context test = rootCtx.getSubcontext("/"+subcontextName + "/" + subcontextName1);

        TestContextListener testL =  new TestContextListener();
        TestContextListener rootL =  new TestContextListener();
        test.addContextListener(testL);
        rootCtx.addContextListener(rootL);


        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(2, rootL.a.size());
        assertEquals(2, rootL.all.size());

        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(2, rootL.a.size());
        assertEquals(2, rootL.all.size());


        ctx1 = ctx1.getParentContext();
        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(4, rootL.a.size());
        assertEquals(4, rootL.all.size());

        ctx2 = ctx2.getParentContext();
        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(4, rootL.a.size());
        assertEquals(4, rootL.all.size());
    }

    public void testAttributes2 () throws Exception {
        String bindingName = "b";
        String attributeName = "a";

        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testAttributes2";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subcontextName1 = "subcontextName1";
        ctx1 = ctx1.createSubcontext(subcontextName1);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName1);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName1);

        Context test = rootCtx.getSubcontext("/"+subcontextName + "/" + subcontextName1);

        TestContextListener testL =  new TestContextListener();
        TestContextListener rootL =  new TestContextListener();
        test.addContextListener(testL);
        rootCtx.addContextListener(rootL);

        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        ctx3.setAttribute(null, attributeName, attributeName);
        ctx3.setAttribute(bindingName, attributeName, attributeName);
        assertEquals("defVal",test.getAttribute(bindingName, attributeName, "defVal"));
        assertEquals("defVal",ctx3.getAttribute(bindingName, attributeName, "defVal"));

        assertEquals(1, testL.a.size());
        assertEquals(1, testL.all.size());
        assertEquals(1, rootL.a.size());
        assertEquals(1, rootL.all.size());

        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(3, testL.a.size());
        assertEquals(3, testL.all.size());
        assertEquals(3, rootL.a.size());
        assertEquals(3, rootL.all.size());


        ctx2 = ctx2.getParentContext();
        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        ctx3.setAttribute(null, attributeName, attributeName);
        ctx3.setAttribute(bindingName, attributeName, attributeName);
        assertEquals("defVal",test.getParentContext().getAttribute(bindingName, attributeName, "defVal"));
        assertEquals("defVal",ctx3.getParentContext().getAttribute(bindingName, attributeName, "defVal"));
        assertEquals(3, testL.a.size());
        assertEquals(3, testL.all.size());
        assertEquals(4, rootL.a.size());
        assertEquals(4, rootL.all.size());

        ctx1 = ctx1.getParentContext();
        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(3, testL.a.size());
        assertEquals(3, testL.all.size());
        assertEquals(6, rootL.a.size());
        assertEquals(6, rootL.all.size());
    }

    public void testAttributes3 () throws Exception {
        String bindingName = "b";
        String attributeName = "a";

        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testAttributes3";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subcontextName1 = "subcontextName1";
        ctx1 = ctx1.createSubcontext(subcontextName1);
        ctx1.putString(bindingName, bindingName);
        ctx2 = ctx2.createSubcontext(subcontextName1);
        ctx2.putString(bindingName, bindingName);
        ctx3 = ctx3.createSubcontext(subcontextName1);

        Context test = rootCtx.getSubcontext("/"+subcontextName + "/" + subcontextName1);

        TestContextListener testL =  new TestContextListener();
        TestContextListener rootL =  new TestContextListener();
        test.addContextListener(testL);
        rootCtx.addContextListener(rootL);

        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(1, testL.a.size());
        assertEquals(1, testL.all.size());
        assertEquals(1, rootL.a.size());
        assertEquals(1, rootL.all.size());

        ctx1.getParentContext() .setAttribute(bindingName, attributeName, attributeName);
        assertEquals(1, testL.a.size());
        assertEquals(1, testL.all.size());
        assertEquals(2, rootL.a.size());
        assertEquals(2, rootL.all.size());

    }

    public void testCopyAttributes () throws Exception {
        String bindingName = "b";
        String attributeName = "a";

        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testCopyAttributes";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString(bindingName, bindingName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        Context test = rootCtx.getSubcontext("/"+subcontextName);
        assertEquals(attributeName, test.getAttribute(bindingName, attributeName, "defVal"));

        test.putString(bindingName, bindingName);
        assertEquals(attributeName, test.getAttribute(bindingName, attributeName, "defVal"));

        test.putString(bindingName, null);
        assertEquals("defVal", test.getObject(bindingName, "defVal"));
        assertEquals("defVal", test.getAttribute(bindingName, attributeName, "defVal"));

        test.putString(bindingName, bindingName);
        assertEquals(bindingName, test.getObject(bindingName, "defVal"));
        assertEquals("defVal", test.getAttribute(bindingName, attributeName, "defVal"));
    }

    public void testNotCopyAttributes () throws Exception {
        String bindingName = "b";
        String attributeName = "a";

        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testNotCopyAttributes";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.putString(bindingName, bindingName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        Context test = rootCtx.getSubcontext("/"+subcontextName);
        assertEquals(attributeName, test.getAttribute(bindingName, attributeName, "defVal"));

        ctx1.putString(bindingName, bindingName);
        assertEquals("defVal", test.getAttribute(bindingName, attributeName, "defVal"));

        ctx1.putString(bindingName, null);
        assertEquals(bindingName, test.getObject(bindingName, "defVal"));
        assertEquals(attributeName, test.getAttribute(bindingName, attributeName, "defVal"));

        ctx1.putString(bindingName, bindingName);
        assertEquals("defVal", test.getAttribute(bindingName, attributeName, "defVal"));
    }

    public void testNotChangedAttributeNoEvent () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testNotChangedAttributeNoEvent";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subcontextName1 = "subcontextName1";
        ctx1 = ctx1.createSubcontext(subcontextName1);
        ctx2 = ctx2.createSubcontext(subcontextName1);
        ctx3 = ctx3.createSubcontext(subcontextName1);

        Context test = rootCtx.getSubcontext("/"+subcontextName + "/" + subcontextName1);
        String bindingName = "b";
        String attributeName = "b";

        ctx1 = ctx1.getParentContext();
        ctx1.putString(bindingName, bindingName);
        ctx2.putString(bindingName, bindingName);

        TestContextListener testL =  new TestContextListener();
        TestContextListener rootL =  new TestContextListener();
        test.addContextListener(testL);
        rootCtx.addContextListener(rootL);



        System.setProperty("kst","kst");
        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);
        System.setProperty("kst","");
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(2, rootL.a.size());
        assertEquals(2, rootL.all.size());

        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);

        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(2, rootL.a.size());
        assertEquals(2, rootL.all.size());


        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(4, rootL.a.size());
        assertEquals(4, rootL.all.size());

        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(2, testL.a.size());
        assertEquals(2, testL.all.size());
        assertEquals(4, rootL.a.size());
        assertEquals(4, rootL.all.size());
    }

    public void testNotChangedAttributeNoEvent2 () throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testNotChangedAttributeNoEvent2";
        String attributeName = "a";
        String bindingName = "a";

        ctx1 = ctx1.createSubcontext(subcontextName);

        ctx2.putString(bindingName, bindingName);
        ctx2.setAttribute(null, attributeName, attributeName);
        ctx2.setAttribute(bindingName, attributeName, attributeName);

        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subcontextName1 = "subcontextName1";
        ctx1 = ctx1.createSubcontext(subcontextName1);
        ctx1.putString(bindingName, bindingName);
        ctx1.setAttribute(null, attributeName, attributeName);
        ctx1.setAttribute(bindingName, attributeName, attributeName);

        ctx2 = ctx2.createSubcontext(subcontextName1);
        ctx3 = ctx3.createSubcontext(subcontextName1);

        Context test = rootCtx.getSubcontext("/"+subcontextName + "/" + subcontextName1);

        TestContextListener testL =  new TestContextListener();
        TestContextListener rootL =  new TestContextListener();
        test.addContextListener(testL);
        rootCtx.addContextListener(rootL);


        test.setAttribute(null, attributeName, attributeName);
        test.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(0, testL.a.size());
        assertEquals(0, testL.all.size());
        assertEquals(0, rootL.a.size());
        assertEquals(0, rootL.all.size());

        rootCtx.setAttribute(null, attributeName, attributeName);
        rootCtx.setAttribute(bindingName, attributeName, attributeName);
        assertEquals(0, testL.a.size());
        assertEquals(0, testL.all.size());
        assertEquals(0, rootL.a.size());
        assertEquals(0, rootL.all.size());
    }


    public void testCtxChangeFollowedByAttributeChange() throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();


        String subcontextName = "testCtxChangeFollowedByAttributeChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.setAttribute(null, "b1","b1");

        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.setAttribute(null, "b2","b2");

        ctx3 = ctx3.createSubcontext(subcontextName);
        ctx3.setAttribute(null, "b3","b3");

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        ctx1 = ctx1.getParentContext();
        assertNotNull(ctx1);


        System.setProperty("key", "key");
        System.setProperty("test1", "test1");
        ctx1.destroySubcontext(subcontextName);
        System.setProperty("test1", "");
        System.setProperty("key", "");

        assertEquals(1,l.a.size());
        assertEquals(1,l.all.size());
    }

    public void testCtxChangeNotFollowedByAttributeChange() throws Exception {
        Context rootCtx = getMergeContext();

        Context ctx1 = getActiveDelegate ();
        Context ctx2 = getReadOnlyDelegate1 ();
        Context ctx3 = getReadOnlyDelegate2 ();

        String subcontextName = "testCtxChangeNotFollowedByAttributeChange";
        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.setAttribute(null, "b1","b1");

        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx2.setAttribute(null, "b2","b2");

        ctx3 = ctx3.createSubcontext(subcontextName);
        ctx3.setAttribute(null, "b3","b3");

        Context hard = rootCtx.getSubcontext(subcontextName);
        assertNotNull(hard);

        TestContextListener l = new TestContextListener();
        rootCtx.addContextListener(l);

        hard = hard.getParentContext();
        assertNotNull(hard);


        System.setProperty("test1", "test1");
        hard.destroySubcontext(subcontextName);
        System.setProperty("test1", "");

        assertEquals(0,l.a.size());
        assertEquals(1,l.s.size());
        assertEquals(1,l.all.size());
    }

    private static class TestContextListener implements ContextListener {

        List s = new ArrayList();
        List b = new ArrayList();
        List a = new ArrayList();

        List all = new ArrayList();

        public void subcontextChanged(SubcontextEvent evt) {
            s.add(evt);
            all.add(evt);
            if (evt.getType() == SubcontextEvent.SUBCONTEXT_ADDED) {
                assertNotNull(evt.getContext().getSubcontext(evt.getSubcontextName()));
                Collection names = evt.getContext().getSubcontextNames();
                assertTrue(names.contains(evt.getSubcontextName()));
            }

            if (evt.getType() == SubcontextEvent.SUBCONTEXT_REMOVED) {
                assertNull(evt.getContext().getSubcontext(evt.getSubcontextName()));
                Collection names = evt.getContext().getSubcontextNames();
                assertFalse(names.contains(evt.getSubcontextName()));
            }
        }

        public void bindingChanged(BindingEvent evt) {
            b.add(evt);
            all.add(evt);
            if (evt.getType() == BindingEvent.BINDING_ADDED || evt.getType() == BindingEvent.BINDING_MODIFIED) {

                if ("tst".equals(System.getProperty("tst"))) {
                    System.out.println("ERR: " + evt + " value: "+ evt.getContext().getObject(evt.getBindingName(), null));
                }

                assertNotNull(evt.getContext().getObject(evt.getBindingName(), null));
                Collection names = evt.getContext().getBindingNames();
                assertTrue(names.contains(evt.getBindingName()));
            }

            if (evt.getType() == BindingEvent.BINDING_REMOVED) {
                assertEquals("defVal",evt.getContext().getObject(evt.getBindingName(), "defVal"));
                Collection names = evt.getContext().getBindingNames();
                assertFalse(names.contains(evt.getBindingName()));
            }
        }

        public void attributeChanged(AttributeEvent evt) {
            a.add(evt);
            all.add(evt);

            if (evt.getType() == AttributeEvent.ATTRIBUTE_ADDED || evt.getType() == AttributeEvent.ATTRIBUTE_MODIFIED) {
                assertNotNull(evt.getContext().getAttribute(evt.getBindingName(), evt.getAttributeName(), null));
                Collection names = evt.getContext().getAttributeNames(evt.getBindingName());
                assertTrue(names.contains(evt.getAttributeName()));
            }

            if (evt.getType() == AttributeEvent.ATTRIBUTE_REMOVED) {
                assertEquals("defVal", evt.getContext().getAttribute(evt.getBindingName(),evt.getAttributeName(), "defVal"));
                Collection names = evt.getContext().getAttributeNames(evt.getBindingName());
                assertFalse(names.contains(evt.getAttributeName()));
            }
        }
    }

}
