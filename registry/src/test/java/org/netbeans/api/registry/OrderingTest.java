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

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderingTest extends NbTestCase {
    public OrderingTest(String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(OrderingTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testOrdering() throws Exception {
        Context subctx = getContext().createSubcontext ("sorta");
        
        Context sub1 = subctx.createSubcontext("subo1");
        Context sub2 = subctx.createSubcontext("subo2");
        Context sub3 = subctx.createSubcontext("subo3");
        Context sub4 = subctx.createSubcontext("subo4");
        Context sub5 = subctx.createSubcontext("subo5");
        
        Object o1 = new JLabel("bino1");
        subctx.putObject("bin1", o1);
        Object o2 = new JLabel("bino2");
        subctx.putObject("bin2", o2);
        Object o3 = new JLabel("bino3");
        subctx.putObject("bin3", o3);
        Object o4 = new JLabel("bino4");
        subctx.putObject("bin4", o4);
        Object o5 = new JLabel("bino5");
        subctx.putObject("bin5", o5);
        
        Collection c = subctx.getOrderedObjects();
        // nothing known about order, but all objects must be there
        ArrayList ar = new ArrayList(c);
        assertTrue("Element not found in the ordered list.", ar.remove(sub1));
        assertTrue("Element not found in the ordered list.", ar.remove(sub2));
        assertTrue("Element not found in the ordered list.", ar.remove(sub3));
        assertTrue("Element not found in the ordered list.", ar.remove(sub4));
        assertTrue("Element not found in the ordered list.", ar.remove(sub5));
        assertTrue("Element not found in the ordered list.", ar.remove(o1));
        assertTrue("Element not found in the ordered list.", ar.remove(o2));
        assertTrue("Element not found in the ordered list.", ar.remove(o3));
        assertTrue("Element not found in the ordered list.", ar.remove(o4));
        assertTrue("Element not found in the ordered list.", ar.remove(o5));
        assertTrue("The collection must be empty now: "+ar, ar.size() == 0);
        
        ArrayList newOrder = new ArrayList();
        ArrayList newOrderValue = new ArrayList();
        newOrder.add("bin5");
        newOrderValue.add(o5);
        newOrder.add("subo5/");
        newOrderValue.add(sub5);
        newOrder.add("subo2/");
        newOrderValue.add(sub2);
        newOrder.add("bin2");
        newOrderValue.add(o2);
        newOrder.add("subo1/");
        newOrderValue.add(sub1);
        newOrder.add("subo3/");
        newOrderValue.add(sub3);
        newOrder.add("bin3");
        newOrderValue.add(o3);
        newOrder.add("bin1");
        newOrderValue.add(o1);
        newOrder.add("bin4");
        newOrderValue.add(o4);
        newOrder.add("subo4/");
        newOrderValue.add(sub4);
        
        subctx.orderContext(newOrder);
        List l = subctx.getOrderedObjects();
        assertTrue("The number of returned items must be the same.", l.size() == newOrderValue.size());
        for (int i=0; i<l.size(); i++) {
            assertTrue("Element not found in the ordered list: "+i+". "+
                l.get(i)+"] != ["+newOrderValue.get(i)+"]", l.get(i).equals(newOrderValue.get(i)));
        }
        
        newOrder.remove(6);
        newOrder.remove(6);
        newOrder.remove(6);
        newOrder.remove(6);
        subctx.orderContext(newOrder);
        l = subctx.getOrderedObjects();
        assertTrue("The number of returned items must be 10.", l.size() == 10);
        
        getContext().destroySubcontext("sorta");
    }
    
    public void testOrderingEmptyContext() throws Exception {
        Context subctx = getContext().createSubcontext ("sorta_empty");
        Collection c = subctx.getOrderedObjects();
        c = subctx.getOrderedNames();
        getContext().destroySubcontext("sorta_empty");
    }
    
    public void testOrderingNames() throws Exception {
        Context subctx = getContext().createSubcontext ("sortanama");
        
        Context sub1 = subctx.createSubcontext("subo1");
        Context sub2 = subctx.createSubcontext("subo2");
        Context sub3 = subctx.createSubcontext("subo3");
        Context sub4 = subctx.createSubcontext("subo4");
        Context sub5 = subctx.createSubcontext("subo5");
        
        Object o1 = new JLabel("bino1");
        subctx.putObject("bin1", o1);
        Object o2 = new JLabel("bino2");
        subctx.putObject("bin2", o2);
        Object o3 = new JLabel("bino3");
        subctx.putObject("bin3", o3);
        Object o4 = new JLabel("bino4");
        subctx.putObject("bin4", o4);
        Object o5 = new JLabel("bino5");
        subctx.putObject("bin5", o5);
        
        Collection c = subctx.getOrderedNames();
        // nothing known about order, but all objects must be there
        ArrayList ar = new ArrayList(c);
        assertTrue("Element not found in the ordered list.", ar.remove("subo1/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo2/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo3/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo4/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo5/"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin1"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin2"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin3"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin4"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin5"));
        assertTrue("The collection must be empty now: "+ar, ar.size() == 0);
        
        ArrayList newOrder = new ArrayList();
        newOrder.add("bin5");
        newOrder.add("subo5/");
        newOrder.add("subo2/");
        newOrder.add("bin2");
        newOrder.add("subo1/");
        newOrder.add("subo3/");
        newOrder.add("bin3");
        newOrder.add("bin1");
        newOrder.add("bin4");
        newOrder.add("subo4/");
        
        subctx.orderContext(newOrder);
        List l = subctx.getOrderedNames();
        assertTrue("The number of returned items must be the same.", l.size() == newOrder.size());
        for (int i=0; i<l.size(); i++) {
            assertTrue("Element not found in the ordered list: "+i+". ["+
                l.get(i)+"] != ["+newOrder.get(i)+"]", l.get(i).equals(newOrder.get(i)));
        }
        
        newOrder.remove(6);
        newOrder.remove(6);
        newOrder.remove(6);
        newOrder.remove(6);
        subctx.orderContext(newOrder);
        l = subctx.getOrderedNames();
        assertTrue("The number of returned items must be 10.", l.size() == 10);
        
        getContext().destroySubcontext("sortanama");
    }

    public void testPositionalAttrs() throws Exception {
        Context subctx = getContext().createSubcontext ("sortapos");
        
        Context sub1 = subctx.createSubcontext("subo1");
        sub1.setAttribute(null, "position", "10.5");
        Context sub2 = subctx.createSubcontext("subo2");
        sub2.setAttribute(null, "position", "10.49");
        
        Object o1 = new JLabel("bino1");
        subctx.putObject("bin1", o1);
        subctx.setAttribute("bin1", "position", "10.48");
        subctx.putObject("bin2", "someval");
        subctx.setAttribute("bin2", "position", "1");
        
        Collection c = subctx.getOrderedNames();
        ArrayList ar = new ArrayList(c);
        assertTrue("Element not found in the ordered list.", ar.remove("bin2"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin1"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo2/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo1/"));
        assertTrue("The collection must be empty now: "+ar, ar.size() == 0);
        
        subctx.setAttribute("bin2", "position", "10.482");
        c = subctx.getOrderedNames();
        ar = new ArrayList(c);
        assertTrue("Element not found in the ordered list.", ar.remove("bin1"));
        assertTrue("Element not found in the ordered list.", ar.remove("bin2"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo2/"));
        assertTrue("Element not found in the ordered list.", ar.remove("subo1/"));
        assertTrue("The collection must be empty now: "+ar, ar.size() == 0);

        getContext().destroySubcontext("sortapos");
    }
    
    protected Context getContext() {
        return Context.getDefault();    
    }
}
