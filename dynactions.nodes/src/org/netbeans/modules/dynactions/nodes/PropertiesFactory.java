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
 *
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dynactions.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 * Factory for Property objects corresponding to bean properties on a pojo.
 * A bit less heavyweight than working with Introspector and beaninfos,
 * and less trouble than working with PropertySupport.Reflection for many
 * properties.
 * <p/>
 * Simply create it with a list of bean-property names, and get back a factory
 * that can create Sheet objects for Nodes with properties that operate over
 * the passed bean.
 *
 * @author Tim Boudreau
 */
public final class PropertiesFactory <T> {
    private String[] propertyNames;
    private final Class<T> type;
    private final InfoProvider provider;
    PropertiesFactory (Class<T> type,  InfoProvider provider, String... propertyNames) {
        this.type = type;
        this.propertyNames = propertyNames;
        this.provider = provider;
        assert validProperties();
    }

    /**
     * Create a properties factory.
     *
     * @param type The type of bean it will look up properties on
     * @param info An InfoProvider to provide localized names, etc.
     * @param propertyNames The names of the properties it will provide, in
     * the order in which they should be displayed.
     * @return
     */
    public static <T> PropertiesFactory<T> create (Class<T> type, InfoProvider info, String... propertyNames) {
        return new PropertiesFactory (type, info, propertyNames);
    }

    private boolean validProperties() {
        for (String name : propertyNames) {
            lookupGetter(name, type);
        }
        return true;
    }

    /**
     * Create a Sheet object with all the properties for the passed pojo.
     * @param pojo An object to look up properties on.  It must acually have
     * bean properties to match all the names passed to the constructor.
     * @return
     */
    public Sheet createSheet(T pojo) {
        Sheet result = Sheet.createDefault();
        populateSheet (result, pojo);
        return result;
    }

    /**
     * Populate an existing sheet with properties looked up on the passed bean
     * @param result
     * @param pojo
     */
    public void populateSheet (Sheet sheet, T pojo) {
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        for (String nm : propertyNames) {
            P p = new P(nm, pojo);
            set.put(p);
        }
        sheet.put (set);
    }

    /**
     * Create a Sheet object that shows a single <i>please wait</i> property
     * while loading in the background.
     *
     * @return A sheet
     */
    public Sheet createPleaseWaitSheet() {
        Sheet result = Sheet.createDefault();
        Sheet.Set set = result.get(Sheet.PROPERTIES);
        set.put (new WaitProperty());
        return result;
    }

    /**
     * Create a property that shows <i>please wait</i> feedback
     * @return
     */
    public Property createPleaseWaitProperty() {
        return new WaitProperty();
    }

    private static final class WaitProperty extends PropertySupport.ReadOnly {
        WaitProperty () {
            super ("wait", String.class, NbBundle.getMessage(PropertiesFactory.class, "LBL_Wait"), //NOI18N
                    NbBundle.getMessage(PropertiesFactory.class, "DESC_Wait")); //NOI18N
            setValue ("suppressCustomEditor", Boolean.TRUE); //NOI18N
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return "";
        }
    }

    private final class P<T> extends Property<T> {
        private String propName;
        private T pojo;
        P (String name, T pojo) {
            super (findType (name, pojo));
            assert type.isInstance(pojo);
            this.propName = name;
            this.pojo = pojo;
            setName(name);
            setDisplayName (provider.displayNameForProperty(propName));
            setShortDescription(provider.descriptionForProperty(propName));
        }
        
        private PropertyEditor editor = null;
        @Override
        public PropertyEditor getPropertyEditor() {
            if (editor == null) {
                editor = provider.propertyEditorForProperty(propName, super.getValueType());
                if (editor == null) {
                    editor = super.getPropertyEditor();
                }
            }
            return editor;
        }

        @Override
        public boolean canRead() {
            return findGetter() != null;
        }

        @Override
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            try {
                return cast(getValueType(), getter.invoke(pojo));
            } catch (IllegalAccessException ex) {
                try {
                    getter.setAccessible(true);

                    return cast(getValueType(), getter.invoke(pojo));
                } finally {
                    getter.setAccessible(false);
                }
            }
        }

        @Override
        public boolean canWrite() {
            return findSetter() != null;
        }

        @Override
        public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            findSetter().invoke(pojo, val);
            editor = null;
        }
        
        /**
         * Like {@link Class#cast} but handles primitive types.
         * See JDK #6456930.
         */
        <T> T cast(Class<T> c, Object o) {
            if (c.isPrimitive()) {
                return (T) o;
            } else {
                return c.cast(o);
            }
        }
        

        private Method getter;
        private Method setter;
        private Method findGetter() {
            if (getter == null) {
                getter = lookupGetter(propName, type);
            }
            return getter;
        }

        private Method findSetter() {
            if (setter == null) {
                setter = lookupSetter(propName, type, super.getValueType());
            }
            return setter;
        }
    }

    private static Class findType (String name, Object pojo) {
        Method m = lookupGetter (name, pojo.getClass());
        if (m == null) {
            throw new IllegalStateException ("No getter for property " + name +
                " on " + pojo);
        }
        return m.getReturnType();
    }

    private static Method lookupGetter(String name, Class type) {
        Method m = null;
        String cap = capitalize(name);
        try {
            m = type.getMethod("get" + cap, (Class[]) null);
        } catch (Exception e) {
            Logger.getLogger(PropertiesFactory.class.getName()).log(Level.FINEST, "Failed to " +
                    "find getter get" + cap + " on " + type, e);
            try {
                m = type.getMethod("is" + capitalize(name), (Class[]) null);
            } catch (Exception e1) {
                Logger.getLogger(PropertiesFactory.class.getName()).log(Level.FINEST, "Failed to " +
                    "find getter is" + cap + " on " + type, e1);
            }
        }
        return m;
    }

    private static Method lookupSetter(String name, Class type, Class argType) {
        Method m = null;
        String cap = capitalize(name);
        try {
            m = type.getMethod("set" + cap, argType);
        } catch (Exception e) {
            Logger.getLogger(PropertiesFactory.class.getName()).log(Level.FINEST, "Failed to " +
                    "find setter set" + cap + " on " + type, e);
        }
        return m;
    }

    private static String capitalize (String name) {
        StringBuilder sb = new StringBuilder (name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Provides information needed for the presentation of properties from
     * an ad-hoc JavaBean.
     */
    public interface InfoProvider {
        /**
         * Get a localized display name given a property name
         * @param propName The property name, one from the array passed to
         * the associated PropertiesFactory's constructor
         * @return A localized display name
         */
        String displayNameForProperty (String propName);
        /**
         * Get a description that will appear in the property sheet's
         * description area when this property is selected in the property
         * sheet.
         * @param propName The property name, one from the array passed to
         * the associated PropertiesFactory's constructor
         * @return
         */
        String descriptionForProperty (String propName);
        /**
         * Get a property editor for the property.  If null is returned,
         * a default property editor from java.beans.PropertyEditorManager
         * (or none if the type is unknown) is used.
         * <p/>
         * This method will be called only once per property, after which
         * the resulting property editor is cachced by those properties
         * generated from a PropertiesFactory.
         *
         * @param property The property name
         * @param valueType The type of the property value
         * @return A property editor or null
         */
        PropertyEditor propertyEditorForProperty (String property, Class valueType);
    }
}
