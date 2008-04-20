/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.spring.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Andrei Badea
 */
public class ApplicationContextBasedLookupTest extends TestCase {

    public ApplicationContextBasedLookupTest(String testName) {
        super(testName);
    }

    public void testLookupSingle() throws Exception {
        ApplicationContext ctx = TestUtils.createAppContext(
                "<bean id='foo' class='java.util.ArrayList'/>" +
                "<bean id='baz' class='java.util.HashMap'/>");
        ArrayList foo = (ArrayList)ctx.getBean("foo");
        HashMap baz = (HashMap)ctx.getBean("baz");
        Lookup lookup = new ApplicationContextBasedLookup(ctx);

        assertSame(foo, lookup.lookup(List.class));
        assertSame(foo, lookup.lookup(ArrayList.class));
        assertSame(baz, lookup.lookup(Map.class));
        // Order should be preserved.
        assertSame(foo, lookup.lookup(Object.class));
    }

    public void testItem() throws Exception {
        ApplicationContext ctx = TestUtils.createAppContext(
                "<bean id='foo' class='java.util.ArrayList'/>");
        ArrayList foo = (ArrayList)ctx.getBean("foo");
        Lookup lookup = new ApplicationContextBasedLookup(ctx);

        Item item = lookup.lookupItem(new Template<List>(List.class, null, null));
        assertEquals("foo", item.getId());
        assertEquals("foo", item.getDisplayName());
        assertSame(ArrayList.class, item.getType());
        assertSame(foo, item.getInstance());
    }

    public void testLookupResult() throws Exception {
        ApplicationContext ctx = TestUtils.createAppContext(
                "<bean id='foo' class='java.util.ArrayList'/>" +
                "<bean id='bar' class='java.util.LinkedList'/>" +
                "<bean id='baz' class='java.util.HashMap'/>");
        ArrayList foo = (ArrayList)ctx.getBean("foo");
        LinkedList bar = (LinkedList)ctx.getBean("bar");
        HashMap baz = (HashMap)ctx.getBean("baz");
        Lookup lookup = new ApplicationContextBasedLookup(ctx);

        assertSame(foo, lookup.lookupItem(new Template<List>(List.class, null, foo)).getInstance());
        assertSame(bar, lookup.lookupItem(new Template<List>(List.class, "bar", bar)).getInstance());

        assertNull(lookup.lookupItem(new Template<List>(List.class, "none", null)));
        assertNull(lookup.lookupItem(new Template<List>(List.class, "foo", bar)));

        Result<List> listResult = lookup.lookupResult(List.class);
        Iterator<Class<? extends List>> listClassIterator = listResult.allClasses().iterator();
        assertSame(ArrayList.class, listClassIterator.next());
        assertSame(LinkedList.class, listClassIterator.next());
        assertFalse(listClassIterator.hasNext());
        Iterator<? extends List> listIterator = listResult.allInstances().iterator();
        assertSame(foo, listIterator.next());
        assertSame(bar, listIterator.next());
        assertFalse(listIterator.hasNext());
    }

    public void testAliases() throws Exception {
        ApplicationContext ctx = TestUtils.createAppContext(
                "<alias name='foo' alias='bar'/>" +
                "<bean id='foo' class='java.util.ArrayList'/>" +
                "<bean id='baz' class='java.util.HashMap'/>");
        ArrayList foo = (ArrayList)ctx.getBean("foo");
        Lookup lookup = new ApplicationContextBasedLookup(ctx);

        assertSame(foo, lookup.lookupItem(new Template<List>(List.class, "bar", null)).getInstance());

        Result<List> result = lookup.lookup(new Template<List>(List.class, null, foo));
        Iterator<? extends Item<List>> iterator = result.allItems().iterator();
        Set<String> ids = new HashSet<String>();
        Item item = iterator.next();
        assertSame(foo, item.getInstance());
        ids.add(item.getId());
        item = iterator.next();
        assertSame(foo, item.getInstance());
        ids.add(item.getId());
        assertEquals(2, ids.size());
        assertTrue(ids.contains("foo"));
        assertTrue(ids.contains("bar"));
    }

    public void testPrototypesIgnored() throws Exception {
        ApplicationContext ctx = TestUtils.createAppContext(
                "<bean id='foo' class='java.util.ArrayList' scope='prototype'/>");
        Lookup lookup = new ApplicationContextBasedLookup(ctx);

        assertNull(lookup.lookup(List.class));
    }
}
