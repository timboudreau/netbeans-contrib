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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Template;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 *
 * @author Andrei Badea
 */
@SuppressWarnings("unchecked")
public class LookupBasedApplicationContext extends DefaultResourceLoader implements ApplicationContext {

    private final Lookup lookup;
    private final ResourcePatternResolver resourcePatternResolver;
    private final long startupDate = System.currentTimeMillis();

    public static ApplicationContext create(Lookup lookup) {
        ClassLoader loader = lookup.lookup(ClassLoader.class);
        if (loader == null) {
            throw new IllegalArgumentException();
        }
        return create(lookup, loader);
    }

    public static ApplicationContext create(Lookup lookup, ClassLoader loader) {
        return new LookupBasedApplicationContext(lookup, loader);
    }

    private LookupBasedApplicationContext(Lookup lookup, ClassLoader loader) {
        super(loader);
        this.lookup = lookup;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(this);
    }

    // ListableBeanFactory methods.

    public Object getBean(String name) throws BeansException {
        return getBean(name, (Class)null);
    }

    public Object getBean(String name, Class requiredType) throws BeansException {
        Item item = lookup.lookupItem(new Template(requiredType, name, null));
        return item != null ? item.getInstance() : null;
    }

    public Object getBean(String name, Object[] args) throws BeansException {
        Object instance = getBean(name);
        // Javadoc seems to imply that if arguments are given, a prototype bean
        // is expected.
        if (args.length == 0 && instance != null) {
            return instance;
        }
        if (instance != null) {
            throw new BeanDefinitionStoreException("No such bean " + name);
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    public boolean containsBean(String name) {
        return getBean(name) != null;
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (containsBean(name)) {
            return true;
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (containsBean(name)) {
            return false;
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
        Object bean = getBean(name);
        if (bean != null) {
            return targetType.isInstance(bean);
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    public Class getType(String name) throws NoSuchBeanDefinitionException {
        Object bean = getBean(name);
        if (bean != null) {
            return bean.getClass();
        }
        throw new NoSuchBeanDefinitionException(name);
    }

    public String[] getAliases(String name) {
        return new String[0];
    }

    public boolean containsBeanDefinition(String beanName) {
        return containsBean(beanName);
    }

    public int getBeanDefinitionCount() {
        return lookup.lookupAll(Object.class).size();
    }

    public String[] getBeanDefinitionNames() {
        return getBeanNamesForType(Object.class);
    }

    public String[] getBeanNamesForType(Class type) {
        return getBeanNamesForType(type, false, false);
    }

    public String[] getBeanNamesForType(Class type, boolean includePrototypes, boolean allowEagerInit) {
        List<String> names = new ArrayList<String>();
        for (Item item : lookup.lookupResult((Class<?>)type).allItems()) {
            String name = item.getId();
            if (name != null) {
                names.add(name);
            }
        }
        return names.toArray(new String[names.size()]);
    }

    public Map getBeansOfType(Class type) throws BeansException {
        return getBeansOfType(type, false, false);
    }

    public Map getBeansOfType(Class type, boolean includePrototypes, boolean allowEagerInit) throws BeansException {
        Map map = new HashMap();
        for (Item item : lookup.lookupResult((Class<?>)type).allItems()) {
            String name = item.getId();
            if (name != null) {
                map.put(name, item.getInstance());
            }
        }
        return map;
    }

    // ApplicationContext methods.

    public ApplicationContext getParent() {
        return null;
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        throw new IllegalStateException("No autowire capable bean factory");
    }

    public String getDisplayName() {
        return null;
    }

    public long getStartupDate() {
        return startupDate;
    }

    // HierarchicalBeanFactory methods.

    public BeanFactory getParentBeanFactory() {
        return null;
    }

    public boolean containsLocalBean(String name) {
        return containsBean(name);
    }

    // MessageSource methods.

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // ApplicationEventPublisher methods.

    public void publishEvent(ApplicationEvent event) {
    }

    // ResourcePatternResolver methods.

    public Resource[] getResources(String locationPattern) throws IOException {
        return resourcePatternResolver.getResources(locationPattern);
    }

    // ResourceLoader methods implemented in superclass.
}
