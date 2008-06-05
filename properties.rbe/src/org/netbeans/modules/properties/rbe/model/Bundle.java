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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.PropertiesFileEntry;
import org.netbeans.modules.properties.PropertyBundleEvent;
import org.netbeans.modules.properties.PropertyBundleListener;
import org.netbeans.modules.properties.Util;
import org.netbeans.modules.properties.rbe.ui.ResourceBundleEditorOptions;


/**
 * The Bundle
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class Bundle implements PropertyBundleListener {

    /** The bundle structure form data object */
    private BundleStructure bundleStructure;
    /** The all locales which contains bundle */
    private Set<Locale> locales;
    /** The properties */
    private SortedMap<String, BundleProperty> properties;
    /* Tree values */
//    private Set<BundleProperty> treeRootProperties;
    private TreeItem<BundleProperty> treeRoot;
    /* Constants */
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");
    /** The property change event names */
    public static final String PROPERTY_LOCALES = "PROPERTY_LOCALES";
    static final LocaleComparator LOCALE_COMPARATOR = new LocaleComparator();

    public Bundle(BundleStructure bundleStructure) {
        this.bundleStructure = bundleStructure;
        locales = new TreeSet<Locale>(new LocaleComparator());
        bundleStructure.addPropertyBundleListener(this);
    }

    public Set<Locale> getLocales() {
        return Collections.unmodifiableSet(locales);
    }

    BundleStructure getBundleStructure() {
        return bundleStructure;
    }
    
    

    /**
     * Adds property
     * @param fullname
     */
    public synchronized void createProperty(String key) {
        TreeItem<BundleProperty> tree = treeRoot;
        int index;
        int offset = 0;
        boolean finded = true;
        key = key.replaceAll(Pattern.quote(getTreeSeparator()) + "+", getTreeSeparator());
        while (true) {
            index = key.indexOf(getTreeSeparator(), offset);
            String group = index == -1 ? key : key.substring(0, index);
            offset = group.length() + getTreeSeparator().length();

            if (finded) {
                finded = false;
                for (TreeItem<BundleProperty> item : tree.getChildren()) {
                    if (item.getValue().getKey().equals(group)) {
                        if (item.getValue().getKey().equals(key)) {
                            createBundlePropertyValues(item.getValue());
                        }
                        tree = item;
                        finded = true;
                        break;
                    }
                }
            }
            if (!finded) {
                BundleProperty property = createProperty(getPropertyName(group), group);
                createBundlePropertyValues(property);
                TreeItem<BundleProperty> treeItem = new TreeItem<BundleProperty>(property);
                tree.addChild(treeItem);
                tree = treeItem;
            }
            if (index == -1) {
                break;
            }
        }
    }

    ItemElem createNewItemElem(Locale locale, String key, String value, String comment) {
        for (int e = 0; e < bundleStructure.getEntryCount(); e++) {
            PropertiesFileEntry entry = bundleStructure.getNthEntry(e);
            if (locale.equals(getLocaleFromPropertiesFileEntry(entry))) {
                entry.getHandler().getStructure().addItem(key, value, comment);
                return entry.getHandler().getStructure().getItem(key);
            }
        }
        return null;
    }

    void createBundlePropertyValues(BundleProperty bundleProperty) {
        for (Locale locale : locales) {
            bundleProperty.addLocaleRepresentation(locale, createNewItemElem(locale, bundleProperty.getKey(), "", ""));
        }
    }

    public void save() {
        treeRoot.accept(new TreeVisitor<TreeItem<BundleProperty>>() {

            public void preVisit(TreeItem<BundleProperty> tree) {
                if (tree.getHeight() == 1) {
                    System.out.println();
                }
                System.out.println(tree.getValue() == null ? "" : tree.getValue().getKey());
            }

            public void postVisit(TreeItem<BundleProperty> tree) {
            }

            public boolean isDone() {
                return false;
            }
        });

    }

    protected synchronized SortedMap<String, BundleProperty> getProperties() {
        if (properties == null) {
            properties = new TreeMap<String, BundleProperty>();
            String replaceRegex = Pattern.quote(getTreeSeparator()) + "+";
            for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
                String propertyName = bundleStructure.getKeys()[k].replaceAll(replaceRegex, getTreeSeparator());
                BundleProperty bundleProperty = createProperty(getPropertyName(propertyName), propertyName);
                for (int e = 0; e < bundleStructure.getEntryCount(); e++) {
                    Locale locale = getLocaleFromPropertiesFileEntry(bundleStructure.getNthEntry(e));
                    ItemElem item = bundleStructure.getItem(e, k);
                    bundleProperty.addLocaleRepresentation(locale, item);
                    locales.add(locale);
                }
                properties.put(bundleProperty.getKey(), bundleProperty);
            }
        }

        return Collections.unmodifiableSortedMap(properties);
    }

    public TreeItem<BundleProperty> getPropertiesTree() {
        if (treeRoot == null) {
            treeRoot = new TreeItem<BundleProperty>(null);
            treeRoot = createChildren(null, null, new TreeSet<BundleProperty>(getProperties().values()));
        }
        return treeRoot;
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
                    subgroup = createProperty(getPropertyName(fullname), fullname);
                    subgroupProperties.add(property);
                    while ((index = property.getKey().indexOf(getTreeSeparator(), fullname.length() + getTreeSeparator().length())) != -1) {
                        fullname = property.getKey().substring(0, index);
                        subgroupProperties.add(createProperty(getPropertyName(fullname), fullname));
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

    private BundleProperty createProperty(String name, String fullname) {
        BundleProperty bundleProperty = new BundleProperty(this, name, fullname);
        properties.put(fullname, bundleProperty);
        return bundleProperty;
    }

    /**
     * Gets property name, fullname property "a.b.c" has name "c"
     * @param fullname
     * @return
     */
    private String getPropertyName(String fullname) {
        int lastIndex = fullname.lastIndexOf(getTreeSeparator());
        return lastIndex == -1 ? fullname : fullname.substring(lastIndex + getTreeSeparator().length());
    }

    private Locale getLocaleFromPropertiesFileEntry(PropertiesFileEntry entry) {
        String localeSuffix = Util.getLocaleSuffix(entry);
        return localeSuffix.length() == 0 ? DEFAULT_LOCALE : new Locale(Util.getLanguage(localeSuffix), Util.getCountry(localeSuffix), Util.getVariant(localeSuffix));
    }

    private String getTreeSeparator() {
        return ResourceBundleEditorOptions.getSeparator();
    }

    public void bundleChanged(PropertyBundleEvent e) {
        if (e.getChangeType() == PropertyBundleEvent.CHANGE_ITEM) {
            PropertiesFileEntry entry = bundleStructure.getEntryByFileName(e.getEntryName());
            Locale locale = getLocaleFromPropertiesFileEntry(entry);
            BundleProperty property = properties.get(e.getItemName());
            if (property != null) {
                property.getLocalRepresentation(locale).setItemElem(entry.getHandler().getStructure().getItem(e.getItemName()));
            }
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
