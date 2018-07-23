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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Andrei Badea
 */
public class ApplicationContextBasedLookup extends Lookup {

    // TODO support other bean scopes (prototype, etc.)?

    private final ApplicationContext appContext;

    public ApplicationContextBasedLookup(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        return lookupBean(clazz);
    }

    @Override
    public <T> Result<T> lookup(Template<T> template) {
        return new ResultImpl<T>(template);
    }

    private <T> T lookupBean(Class<T> type) {
        @SuppressWarnings("unchecked")
        Map<String, ? extends T> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext, type, false, true);
        for (T instance : beans.values()) {
            return instance;
        }
        return null;
    }

    private <T> T lookupBean(String id, Class<T> type) {
        try {
            @SuppressWarnings("unchecked")
            T bean = (T)appContext.getBean(id, type);
            return bean;
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private <T> Map<String, ? extends T> lookupBeans(Template<T> template) {
        String id = template.getId();
        Class<T> type = template.getType();
        T instance = template.getInstance();
        if (id != null) {
            T bean = lookupBean(id, type);
            if (instance != null && instance != bean) {
                bean = null;
            }
            return bean != null ? Collections.singletonMap(id, bean) : Collections.<String, T>emptyMap();
        } else {
            @SuppressWarnings("unchecked")
            Map<String, ? extends T> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext, type, false, true);
            if (instance != null) {
                Map<String, T> instanceMatch = new LinkedHashMap<String, T>();
                for (Entry<String, ? extends T> entry : beans.entrySet()) {
                    if (entry.getValue() == instance) {
                        instanceMatch.put(entry.getKey(), instance);
                    }
                }
                beans = instanceMatch;
            }
            Map<String, T> computed = new LinkedHashMap<String, T>(beans);
            for (Entry<String, ? extends T> entry : beans.entrySet()) {
                for (String alias : appContext.getAliases(entry.getKey())) {
                    computed.put(alias, entry.getValue());
                }
            }
            return computed;
        }
    }

    private final class ResultImpl<T> extends Result<T> {

        private final Template<T> template;

        // @GuardedBy("this")
        private Map<String, ? extends T> beans;
        // @GuardedBy("this")
        private List<Item<T>> items;
        // @GuardedBy("this")
        private Set<Class<? extends T>> classes;
        // @GuardedBy("this")
        private List<? extends T> instances;

        public ResultImpl(Template<T> template) {
            this.template = template;
        }

        @Override
        public synchronized Collection<? extends T> allInstances() {
            if (instances != null) {
                return instances;
            }
            List<T> newInstances = new ArrayList<T>();
            for (Entry<String, ? extends T> entry : allBeans().entrySet()) {
                newInstances.add(entry.getValue());
            }
            instances = Collections.unmodifiableList(newInstances);
            return instances;
        }

        @Override
        public synchronized Set<Class<? extends T>> allClasses() {
            if (classes != null) {
                return classes;
            }
            Set<Class<? extends T>> newClasses = new LinkedHashSet<Class<? extends T>>();
            for (Entry<String, ? extends T> entry : allBeans().entrySet()) {
                @SuppressWarnings("unchecked")
                Class<? extends T> clazz = (Class<? extends T>)entry.getValue().getClass();
                newClasses.add(clazz);
            }
            classes = Collections.<Class<? extends T>>unmodifiableSet(newClasses);
            return classes;
        }

        @Override
        public synchronized Collection<Item<T>> allItems() {
            if (items != null) {
                return items;
            }
            List<Item<T>> newItems = new ArrayList<Item<T>>();
            for (Entry<String, ? extends T> entry : allBeans().entrySet()) {
                newItems.add(new ItemImpl<T>(entry.getKey(), entry.getValue()));
            }
            items = Collections.unmodifiableList(newItems);
            return newItems;
        }

        private synchronized Map<String, ? extends T> allBeans() {
            if (beans == null) {
                beans = lookupBeans(template);
            }
            return beans;
        }

        @Override
        public void addLookupListener(LookupListener l) {
        }

        @Override
        public void removeLookupListener(LookupListener l) {
        }
    }

    private static final class ItemImpl<T> extends Item<T> {

        private final T instance;
        private final String id;

        public ItemImpl(String id, T instance) {
            this.id = id != null ? id : "";
            assert instance != null;
            this.instance = instance;
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