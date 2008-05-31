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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.Util;

/**
 * The Bundle
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class Bundle {

    Set<Locale> locales;
    BundleStructure bundleStructure;
    /** The default locale */
    public final static Locale DEFAULT_LOCALE = new Locale("__", "", "");

    public Bundle(BundleStructure bundleStructure) {
        this.bundleStructure = bundleStructure;
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public List<BundleProperty> getProperties() {
        locales = new HashSet<Locale>();
        List<BundleProperty> properties = new ArrayList<BundleProperty>();
        for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
            String propertyName = bundleStructure.getKeys()[k];
            BundleProperty bundleProperty = new BundleProperty(propertyName, propertyName, this);
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
        return properties;
    }

    public Set<BundleProperty> getPropertiesAsTree(String separator) {
        locales = new HashSet<Locale>();
        Map<String, BundleProperty> mapping = new HashMap<String, BundleProperty>();
        Set<BundleProperty> rootProperties = new TreeSet<BundleProperty>();
        for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
            String propertyName = bundleStructure.getKeys()[k];
            BundleProperty bundleProperty = null;
            BundleProperty childProperty = null;
            BundleProperty rootProperty = null;
            boolean end = false;
            while (!end) {
                int lastSeparator = propertyName.lastIndexOf(separator);
                rootProperty = mapping.get(propertyName);
                if (rootProperty != null) {
                    end = true;
                } else if (lastSeparator == -1) {
                    rootProperty = new BundleProperty(propertyName, propertyName, this);
                    rootProperties.add(rootProperty);
                    mapping.put(propertyName, rootProperty);
                    end = true;
                } else {
                    rootProperty = new BundleProperty(propertyName.substring(lastSeparator + 1), propertyName, this);
                    mapping.put(propertyName, rootProperty);
                    // Created property but it isnt root property -> continue
                    propertyName = propertyName.substring(0, lastSeparator);
                }
                if (childProperty != null) {
                    rootProperty.addChildProperty(childProperty);
                }
                if (bundleProperty == null) {
                    // first created/found property is our bundle property
                    bundleProperty = rootProperty;
                }
                childProperty = rootProperty;
            }
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
        }
        return rootProperties;
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
