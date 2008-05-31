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
package org.netbeans.modules.properties.rbe;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
    private Set<BundleProperty> properties;
    /* Tree values */
    /** Last builded tree separator, if null -> not builded tree */
    private String treeSeparator = null;
    private Set<BundleProperty> treeRootProperties;
    /* Constants */
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");

    public Bundle(BundleStructure bundleStructure) {
        this.bundleStructure = bundleStructure;
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public synchronized Set<BundleProperty> getProperties() {
        if (properties == null) {
            locales = new HashSet<Locale>();
            properties = new TreeSet<BundleProperty>();
            for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
                String propertyName = bundleStructure.getKeys()[k];
                BundleProperty bundleProperty = new BundleProperty(getPropertyName(propertyName), propertyName, this);
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
                properties.add(bundleProperty);
            }
        }
        return properties;
    }

    public Set<BundleProperty> getPropertiesAsTree(String separator) {
        if (treeRootProperties == null) {
            treeRootProperties = new TreeSet<BundleProperty>();
            treeRootProperties.addAll(createChildren(null, getProperties()));
            treeSeparator = separator;
        }
        return treeRootProperties;
    }

    /**
     * Creates children properties for specefic property
     * @param rootProperty root property
     * @param properties all childen properties
     * @return
     */
    private synchronized Set<BundleProperty> createChildren(BundleProperty rootProperty, Set<BundleProperty> properties) {
        int offset = rootProperty == null ? 0 : rootProperty.getFullname().length() + getTreeSeparator().length();
        Set<BundleProperty> rootProperties = new TreeSet<BundleProperty>();
        BundleProperty currentRoot = null;
        Set<BundleProperty> currentChildren = new TreeSet<BundleProperty>();
        for (BundleProperty property : properties) {
            if (currentRoot != null && property.getFullname().startsWith(currentRoot.getFullname() + getTreeSeparator())) {
                currentChildren.add(property);
            } else {
                if (currentRoot != null) {
                    currentRoot.addChildrenProperties(createChildren(currentRoot, currentChildren));
                    rootProperties.add(currentRoot);
                    currentChildren.clear();
                }
                int index = property.getFullname().indexOf(getTreeSeparator(), offset);
                if (index != -1) {
                    String fullname = property.getFullname().substring(0, index);
                    currentRoot = new BundleProperty(getPropertyName(fullname), fullname, this);
                    currentChildren.add(property);
                } else {
                    currentRoot = property;
                }
            }
        }
        if (currentRoot != null && !rootProperties.contains(currentRoot)) {
            currentRoot.addChildrenProperties(createChildren(currentRoot, currentChildren));
            rootProperties.add(currentRoot);
        }
        return rootProperties;
    }

    /**
     * Gets property name, fullname property "a.b.c" has name "c"
     * @param fullname
     * @return
     */
    private String getPropertyName(String fullname) {
        int lastIndex = fullname.lastIndexOf(ResourceBundleEditorOptions.getSeparator());
        return lastIndex == -1 ? fullname : fullname.substring(lastIndex + 1);
    }

    private String getTreeSeparator() {
        return ResourceBundleEditorOptions.getSeparator();
    }

    static class LocaleComparator implements Comparator<Locale> {

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
}
