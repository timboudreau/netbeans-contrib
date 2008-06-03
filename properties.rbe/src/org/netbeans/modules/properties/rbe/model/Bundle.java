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
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.Util;
import org.netbeans.modules.properties.rbe.ui.ResourceBundleEditorOptions;

/**
 * The Bundle
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class Bundle {

    /** The bundle structure form data object */
    private BundleStructure bundleStructure;
    /** The all locales which contains bundle */
    private Set<Locale> locales;
    /** The properties */
    private SortedMap<String, BundleProperty> properties;
    /* Tree values */
    private Set<BundleProperty> treeRootProperties;
    /* Constants */
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");

    public Bundle(BundleStructure bundleStructure) {
        this.bundleStructure = bundleStructure;
    }

    public Set<Locale> getLocales() {
        return Collections.unmodifiableSet(locales);
    }

    /**
     * Adds property
     * @param fullname
     */
    public synchronized BundleProperty addProperty(String fullname) {
        String groupName = getPropertyGroupName(fullname);
        BundleProperty property = createProperty(getPropertyName(fullname), fullname);
        if (!"".equals(groupName)) {
            BundleProperty group = properties.get(groupName);
            if (group == null) {
                addProperty(groupName).addChildenProperty(property);
            } else {
                group.addChildenProperty(property);
            }
        } else if (treeRootProperties != null) {
            treeRootProperties.add(property);
        }
        return property;
    }

    public synchronized SortedMap<String, BundleProperty> getProperties() {
        if (properties == null) {
            locales = new TreeSet<Locale>(new LocaleComparator());
            properties = new TreeMap<String, BundleProperty>();
            for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
                String propertyName = bundleStructure.getKeys()[k];
                BundleProperty bundleProperty = createProperty(getPropertyName(propertyName), propertyName);
                for (int e = 0; e < bundleStructure.getEntryCount(); e++) {
                    Locale locale;
                    String localeSuffix = Util.getLocaleSuffix(bundleStructure.getNthEntry(e));
                    if ("".equals(localeSuffix)) {
                        locale = DEFAULT_LOCALE;
                    } else {
                        locale = new Locale(Util.getLanguage(localeSuffix), Util.getCountry(localeSuffix), Util.getVariant(localeSuffix));
                    }
                    ItemElem item = bundleStructure.getItem(e, k);
                    bundleProperty.addLocaleRepresentation(locale, item);
                    locales.add(locale);
                }
                properties.put(bundleProperty.getFullname(), bundleProperty);
            }
        }
        return Collections.unmodifiableSortedMap(properties);
    }

    public Set<BundleProperty> getPropertiesAsTree() {
        if (treeRootProperties == null) {
            treeRootProperties = new TreeSet<BundleProperty>();
            treeRootProperties.addAll(createChildren(null, new TreeSet<BundleProperty>(properties.values()))); //Copy to avoid concurrent modification
        }
        return treeRootProperties;
    }

    /**
     * Creates child properties for specefic group property
     * @param groupProperty root of the current tree
     * @param groupProperties all properties of the group property subtree
     * @return child properties of the group property
     */
    private synchronized Set<BundleProperty> createChildren(BundleProperty groupProperty, Collection<BundleProperty> groupProperties) {
        int offset = groupProperty == null ? 0 : groupProperty.getFullname().length() + getTreeSeparator().length();
        Set<BundleProperty> childProperties = new TreeSet<BundleProperty>();
        BundleProperty subgroup = null;
        Set<BundleProperty> subgroupProperties = new TreeSet<BundleProperty>();
        for (BundleProperty property : groupProperties) {
            if (subgroup != null && property.getFullname().startsWith(subgroup.getFullname() + getTreeSeparator())) {
                //Property is from the current group property subtree
                subgroupProperties.add(property);
            } else {
                if (subgroup != null) {
                    //Property is from the different group
                    subgroup.addChildrenProperties(createChildren(subgroup, subgroupProperties));
                    childProperties.add(subgroup);
                    subgroupProperties.clear();
                }
                //Finding the new subgroup property
                int index = property.getFullname().indexOf(getTreeSeparator(), offset);
                if (index != -1) {
                    String fullname = property.getFullname().substring(0, index);
                    subgroup = createProperty(getPropertyName(fullname), fullname);
                    subgroupProperties.add(property);
                } else {
                    subgroup = property;
                }
            }
        }
        if (subgroup != null && !childProperties.contains(subgroup)) {
            subgroup.addChildrenProperties(createChildren(subgroup, subgroupProperties));
            childProperties.add(subgroup);
        }
        return childProperties;
    }

    private BundleProperty createProperty(String name, String fullname) {
        BundleProperty bundleProperty = new BundleProperty(name, fullname, this);
        properties.put(fullname, bundleProperty);
        System.out.println("New property" + bundleProperty.getFullname());
        return bundleProperty;
    }

    /**
     * Gets property name, fullname property "a.b.c" has name "c"
     * @param fullname
     * @return
     */
    private String getPropertyName(String fullname) {
        int lastIndex = fullname.lastIndexOf(getTreeSeparator());
        return lastIndex == -1 ? fullname : fullname.substring(lastIndex + 1);
    }

    /**
     * Gets property group, fullname property "a.b.c" has name "a" 
     * or "" if property hasn't separator in the fullname
     * @param fullname
     * @return
     */
    private String getPropertyGroupName(String fullname) {
        int lastIndex = fullname.lastIndexOf(getTreeSeparator());
        return lastIndex == -1 ? "" : fullname.substring(0, lastIndex);
    }

    private String getTreeSeparator() {
        return ResourceBundleEditorOptions.getSeparator();
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
