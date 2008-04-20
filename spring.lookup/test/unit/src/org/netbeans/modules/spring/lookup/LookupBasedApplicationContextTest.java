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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.AbstractLookup.Content;
import org.openide.util.lookup.AbstractLookup.Pair;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Andrei Badea
 */
public class LookupBasedApplicationContextTest extends TestCase {

    // TODO test others ApplicationContext methods.

    public LookupBasedApplicationContextTest(String testName) {
        super(testName);
    }

    public void testBeanFactoryMethods() {
        Content content = new Content();
        ArrayList array = new ArrayList();
        LinkedList linked = new LinkedList();
        content.addPair(new PairImpl<Object>("array", array));
        content.addPair(new PairImpl<Object>("linked", linked));
        content.addPair(new PairImpl<Object>(null, new HashMap()));
        Lookup lookup = new AbstractLookup(content);
        ApplicationContext ctx = LookupBasedApplicationContext.create(lookup, this.getClass().getClassLoader());

        assertSame(array, ctx.getBean("array"));

        assertSame(array, ctx.getBean("array", ArrayList.class));
        // Order should be preserved.
        assertSame(array, ctx.getBean("array", List.class));
        assertNull(ctx.getBean("array", LinkedList.class));

        assertSame(array, ctx.getBean("array", new Object[0]));
        try {
            ctx.getBean("array", new Object[] { null });
            fail();
        } catch (BeanDefinitionStoreException e) {}
        try {
            ctx.getBean("foo", new Object[0]);
            fail();
        } catch (NoSuchBeanDefinitionException e) {}

        assertTrue(ctx.containsBean("array"));
        assertFalse(ctx.containsBean("foo"));

        assertTrue(ctx.isSingleton("array"));
        try {
            ctx.isSingleton("foo");
        } catch (NoSuchBeanDefinitionException e) {}

        assertFalse(ctx.isPrototype("array"));
        try {
            ctx.isPrototype("foo");
        } catch (NoSuchBeanDefinitionException e) {}

        assertTrue(ctx.isTypeMatch("array", ArrayList.class));
        assertTrue(ctx.isTypeMatch("array", List.class));
        assertFalse(ctx.isTypeMatch("linked", ArrayList.class));
        try {
            ctx.isTypeMatch("foo", Object.class);
        } catch (NoSuchBeanDefinitionException e) {}

        assertSame(ArrayList.class, ctx.getType("array"));

        assertTrue(ctx.containsBeanDefinition("array"));
        assertFalse(ctx.containsBeanDefinition("foo"));

        assertEquals(3, ctx.getBeanDefinitionCount());

        String[] names = ctx.getBeanDefinitionNames();
        assertEquals(2, names.length);
        assertEquals("array", names[0]);
        assertEquals("linked", names[1]);

        names = ctx.getBeanNamesForType(List.class);
        assertEquals(2, names.length);
        assertEquals("array", names[0]);
        assertEquals("linked", names[1]);

        Map beans = ctx.getBeansOfType(List.class);
        assertEquals(2, beans.size());
        assertSame(array, beans.get("array"));
        assertSame(linked, beans.get("linked"));

        assertTrue(ctx.containsLocalBean("array"));
        assertFalse(ctx.containsLocalBean("foo"));
    }

    private static final class PairImpl<T> extends Pair<T> {

        private final String id;
        private final T instance;

        public PairImpl(String id, T instance) {
            this.id = id;
            this.instance = instance;
        }

        @Override
        protected boolean instanceOf(Class<?> c) {
            return c.isInstance(instance);
        }

        @Override
        protected boolean creatorOf(Object obj) {
            return obj == instance;
        }

        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends T> getType() {
            return (Class<? extends T>)instance.getClass();
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDisplayName() {
            return getId();
        }
    }
}
