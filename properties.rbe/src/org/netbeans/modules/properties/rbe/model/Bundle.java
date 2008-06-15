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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.rbe.spi.ResourceBundleEditorBridge;
import org.netbeans.modules.properties.rbe.spi.ResourceBundleEditorBridge.BridgeBundleEvent;
import org.netbeans.modules.properties.rbe.ResourceBundleEditorOptions;
import org.netbeans.modules.properties.rbe.model.visitor.AbstractTraversalTreeVisitor;
import org.netbeans.modules.properties.rbe.spi.ResourceBundleEditorBridge.Property;
import org.openide.util.Lookup;

/**
 * The Bundle
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class Bundle {

    /** The Log */
    private final static Logger log = Logger.getLogger(Bundle.class.getName());
    /** The all locales which contains bundle */
    private Set<Locale> locales;
    /** The properties */
    private SortedMap<String, BundleProperty> properties;
    /* Tree values */
    private TreeItem<BundleProperty> treeRoot;
    /* Constants */
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");
    /** The property change support */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /** The property change event names */
    public static final String PROPERTY_LOCALES = "PROPERTY_LOCALES";
    public static final String PROPERTY_PROPERTIES = "PROPERTY_PROPERTIES";
    /** The locale comparator */
    static final LocaleComparator LOCALE_COMPARATOR = new LocaleComparator();
    /** The bridge between RBE and current properties infrastructure */
    private ResourceBundleEditorBridge bridge;

    public Bundle(PropertiesDataObject dataObject) {
        ResourceBundleEditorBridge.Factory factory = Lookup.getDefault().lookup(ResourceBundleEditorBridge.Factory.class);
        if (factory == null) {
            throw new IllegalStateException("Cannot find the ResourceBundleEditorBridge implementation!");
        }
        bridge = factory.get(dataObject);
        bridge.addBridgeEventListener(new ResourceBundleEditorBridge.BridgeEventListener() {

            public void bundleChanged(BridgeBundleEvent event) {
                Bundle.this.bundleChanged(event);
            }
        });
    }

    protected void initLocales() {
        locales = new TreeSet<Locale>(LOCALE_COMPARATOR);
        locales.addAll(bridge.getLocales());
    }

    protected void initProperties() {
        if (locales == null) {
            initLocales();
        }
        properties = new TreeMap<String, BundleProperty>();
        for (String key : bridge.getKeys()) {
            BundleProperty bundleProperty = createBundleProperty(key);
            for (Locale locale : locales) {
                Property prop = bridge.getLocaleProperty(locale, key, false);
                if (prop != null) {
                    bundleProperty.addLocaleProperty(locale, new LocaleProperty(bundleProperty, locale, prop.getValue(), prop.getComment()));
                }
            }
        }
    }

    protected void initTree() {
        if (locales == null) {
            initLocales();
        }
        treeRoot = new TreeItem<BundleProperty>(null);
        treeRoot = createChildren(null, null, new TreeSet<BundleProperty>(properties.values()));
    }

    /**
     * Adds property
     * @param fullname
     */
    public synchronized BundleProperty createProperty(String key) {
        BundleProperty createdProperty = properties.get(key);
        if (createdProperty != null) {
            createLocaleProperties(createdProperty);
            return createdProperty;
        }

        TreeItem<BundleProperty> tree = treeRoot;
        int index;
        int offset = 0;
        boolean finded = true;
        key = proceedKey(key);
        if (treeRoot != null) {
            while (true) {
                index = key.indexOf(getTreeSeparator(), offset);
                String group = index == -1 ? key : key.substring(0, index);
                offset = group.length() + getTreeSeparator().length();

                if (finded) {
                    finded = false;
                    for (TreeItem<BundleProperty> item : tree.getChildren()) {
                        if (item.getValue().getKey().equals(group)) {
                            if (item.getValue().getKey().equals(key)) {
                                createLocaleProperties(item.getValue());
                            }
                            tree = item;
                            finded = true;
                            break;
                        }
                    }
                }
                if (!finded) {
                    BundleProperty property = createBundleProperty(group);
                    if (createdProperty != null) {
                        createdProperty = property;
                    }
                    createLocaleProperties(property);
                    TreeItem<BundleProperty> treeItem = new TreeItem<BundleProperty>(property);
                    tree.addChild(treeItem);
                    tree = treeItem;
                }
                if (index == -1) {
                    break;
                }
            }
        } else {
            createdProperty = createBundleProperty(key);
            createLocaleProperties(createdProperty);
        }
        firePropertyChange(PROPERTY_PROPERTIES, null, null);
        return createdProperty;
    }

    public Set<Locale> getLocales() {
        return Collections.unmodifiableSet(locales);
    }

    public synchronized SortedMap<String, BundleProperty> getProperties() {
        if (properties == null) {
            initProperties();
        }
        return Collections.unmodifiableSortedMap(properties);
    }

    public TreeItem<BundleProperty> getPropertyTree() {
        if (properties == null) {
            initProperties();
        }
        if (treeRoot == null) {
            initTree();
        }
        return treeRoot;
    }

    public BundleProperty getProperty(String key) {
        return properties.get(key);
    }

    public boolean isPropertyExists(Locale locale, String key) {
        return bridge.isPropertyExists(locale, key);
    }

    public void deleteProperty(final String key) {
        bridge.deleteProperty(key);
        // BundleStructure resorts properties after any change so its slow as hell!
        log.fine("Deleting resource property: " + key);
        properties.remove(key);
        treeRoot.accept(new AbstractTraversalTreeVisitor<BundleProperty>() {

            @Override
            protected void preVisit(TreeItem<BundleProperty> t) {
                if (t.getValue().getKey().equals(key)) {
                    if (t.isLeaf()) {
                        t.getParent().removeChild(t);
                    }
                    done = true;
                }
            }

            @Override
            protected void postVisit(TreeItem<BundleProperty> t) {
            }
        });
        firePropertyChange(PROPERTY_PROPERTIES, null, null);
    }

    private void createLocaleProperties(BundleProperty property) {
        for (Locale locale : locales) {
            Property prop = bridge.getLocaleProperty(locale, property.getKey(), true);
            property.addLocaleProperty(locale, new LocaleProperty(property, locale, prop.getValue(), prop.getComment()));
        }
    }

    /**
     * Creates child properties for specefic group property
     * @param groupProperty root of the current tree
     * @param groupProperties all properties of the group property subtree
     * @return child properties of the group property
     */
    private TreeItem<BundleProperty> createChildren(TreeItem<BundleProperty> tree, BundleProperty groupProperty, Collection<BundleProperty> groupProperties) {
        TreeItem<BundleProperty> subtree = new TreeItem<BundleProperty>(groupProperty);
        int offset = groupProperty == null ? 0 : groupProperty.getKey().length() + getTreeSeparator().length();
        BundleProperty subgroup = null;
        Set<BundleProperty> subgroupProperties = new TreeSet<BundleProperty>();
        for (BundleProperty property : groupProperties) {
            if (subgroup != null && property.getKey().startsWith(subgroup.getKey() + getTreeSeparator())) {
                //Property is from the current group property subtree
                subgroupProperties.add(property);
            } else {
                if (subgroup != null) {
                    //Property is from the different group
                    createChildren(subtree, subgroup, subgroupProperties);
                    subgroup = null;
                    subgroupProperties.clear();
                }
                //Finding the new subgroup property
                int index = property.getKey().indexOf(getTreeSeparator(), offset);
                if (index != -1) {
                    String fullname = property.getKey().substring(0, index);
                    subgroup = createBundleProperty(fullname);
                    subgroupProperties.add(property);
                    while ((index = property.getKey().indexOf(getTreeSeparator(), fullname.length() + getTreeSeparator().length())) != -1) {
                        fullname = property.getKey().substring(0, index);
                        subgroupProperties.add(createBundleProperty(fullname));
                    }
                } else {
                    subgroup = property;
                }

            }
        }
        if (subgroup != null) {
            createChildren(subtree, subgroup, subgroupProperties);
        }
        if (tree != null) {
            tree.addChild(subtree);
        }
        return subtree;
    }

    private String proceedKey(String key) {
        //Replace multiple occurrences of the separator
        //TODO: remove separator on the first and the last key position (".ssss.")
        return key.replaceAll(Pattern.quote(getTreeSeparator()) + "+", getTreeSeparator());
    }

    void setPropertyValue(Locale locale, String key, String value) {
        bridge.setPropertyValue(locale, key, value);
    }

    void setPropertyComment(Locale locale, String key, String comment) {
        bridge.setPropertyComment(locale, key, comment);
    }

    BundleProperty createBundleProperty(String key) {
        BundleProperty bundleProperty = new BundleProperty(this, getPropertyName(key), key);
        properties.put(key, bundleProperty);
        return bundleProperty;
    }

    void addLocale(Locale locale) {
        locales.add(locale);
        firePropertyChange(PROPERTY_LOCALES, null, null);
    }

    /**
     * Gets property name, property key "a.b.c" has name "c" where separator is '.'
     * @param fullname
     * @return
     */
    private String getPropertyName(String fullname) {
        int lastIndex = fullname.lastIndexOf(getTreeSeparator());
        return lastIndex == -1 ? fullname : fullname.substring(lastIndex + getTreeSeparator().length());
    }

    private String getTreeSeparator() {
        return ResourceBundleEditorOptions.getSeparator();
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void propertyChanged(Locale locale, String key, String value, String comment) {
        BundleProperty property = getProperties().get(key);
        if (property != null) {
            LocaleProperty localeProperty = property.getLocalProperty(locale);
            if (localeProperty != null) {
                localeProperty.updateValue(value);
                localeProperty.updateComment(comment);
            }
        }
    }

    protected void bundleChanged(BridgeBundleEvent event) {
        switch (event.getType()) {
            case PROPERTY_CHANGED:
                propertyChanged(event.getLocale(), event.getKey(), event.getValue(), event.getComment());
                break;
        }
    }
}

class LocaleComparator implements Comparator<Locale> {

    public int compare(Locale locale1, Locale locale2) {
        int diff = locale1.getLanguage().compareTo(locale2.getLanguage());
        if (diff == 0) {
            diff = locale1.getCountry().compareTo(locale2.getCountry());
            if (diff == 0) {
                diff = locale1.getVariant().compareTo(locale2.getVariant());
            }
        }
        return diff;
    }
}
