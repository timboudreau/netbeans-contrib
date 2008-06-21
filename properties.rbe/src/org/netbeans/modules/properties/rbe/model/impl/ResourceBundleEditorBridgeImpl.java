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
package org.netbeans.modules.properties.rbe.model.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.PropertiesFileEntry;
import org.netbeans.modules.properties.PropertiesStructure;
import org.netbeans.modules.properties.PropertyBundleEvent;
import org.netbeans.modules.properties.PropertyBundleListener;
import org.netbeans.modules.properties.Util;
import org.netbeans.modules.properties.rbe.model.Bundle;
import org.netbeans.modules.properties.rbe.spi.ResourceBundleEditorBridge;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject.Entry;

/**
 * The Resource Bundle Editor Bridge
 * @author @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class ResourceBundleEditorBridgeImpl implements ResourceBundleEditorBridge {

    private BundleStructure bundleStructure;
    private PropertiesDataObject propertiesDataObject;
    private Map<Locale, PropertiesFileEntry> locale2file = new HashMap<Locale, PropertiesFileEntry>();
    /** Listeners */
    private List<BridgeEventListener> listeners = new ArrayList<BridgeEventListener>();

    public ResourceBundleEditorBridgeImpl(PropertiesDataObject propertiesDataObject) {
        this.propertiesDataObject = propertiesDataObject;
        bundleStructure = propertiesDataObject.getBundleStructure();
        bundleStructure.addPropertyBundleListener(new PropertyBundleListener() {

            public void bundleChanged(PropertyBundleEvent e) {
                ResourceBundleEditorBridgeImpl.this.bundleChanged(e);
            }
        });

        propertiesDataObject.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            }
        });

        initLocales();
    }

    public Collection<String> getKeys() {
        return Arrays.asList(bundleStructure.getKeys());
    }

    public Collection<Locale> getLocales() {
        return locale2file.keySet();
    }

    public String getLocalePropertyValue(Locale locale, String key) {
        ItemElem itemElem = getItemElem(locale, key);
        return itemElem == null ? null : itemElem.getValue();
    }

    public String getLocalePropertyComment(Locale locale, String key) {
        ItemElem itemElem = getItemElem(locale, key);
        return itemElem == null ? null : itemElem.getComment();
    }

    public void setLocalePropertyValue(Locale locale, String key, String value) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            createNewItemElem(locale, key, value, "");
        } else {
            itemElem.setValue(value);
        }
    }

    public void setLocalePropertyComment(Locale locale, String key, String comment) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            createNewItemElem(locale, key, "", comment);
        } else {
            itemElem.setComment(comment);
        }
    }

    public void createLocaleProperty(Locale locale, String key, String value, String comment) {
        createNewItemElem(locale, key, value, comment);
    }

    public boolean isPropertyExists(String key) {
        for (Locale locale : getLocales()) {
            PropertiesFileEntry entry = locale2file.get(locale);
            if (entry != null && entry.getHandler().getStructure().getItem(key) != null) {
                return true;
            }
        }
        return false;
    }

    public void deleteProperty(String key) {
        for (Locale locale : getLocales()) {
            PropertiesFileEntry entry = locale2file.get(locale);
            if (entry != null) {
                entry.getHandler().getStructure().deleteItem(key);
            }
        }
    }

    public boolean isLocalePropertyExists(Locale locale, String key) {
        return getItemElem(locale, key) != null;
    }

    private void initLocales() {
        PropertiesFileEntry entry = (PropertiesFileEntry) propertiesDataObject.getPrimaryEntry();
        locale2file.put(getLocaleFromPropertiesFileEntry(entry), entry);
        for (Entry e : propertiesDataObject.secondaryEntries()) {
            entry = (PropertiesFileEntry) e;
            locale2file.put(getLocaleFromPropertiesFileEntry(entry), entry);
        }
    }

    private Locale getLocaleFromPropertiesFileEntry(PropertiesFileEntry entry) {
        String localeSuffix = Util.getLocaleSuffix(entry);
        return localeSuffix.length() == 0 ? Bundle.DEFAULT_LOCALE : new Locale(Util.getLanguage(localeSuffix), Util.getCountry(localeSuffix), Util.getVariant(localeSuffix));
    }

    private PropertiesStructure getPropertiesStructure(Locale locale) {
        PropertiesFileEntry entry = locale2file.get(locale);
        if (entry != null) {
            return entry.getHandler().getStructure();
        }
        return null;
    }

    private ItemElem getItemElem(Locale locale, String key) {
        PropertiesStructure propertiesStructure = getPropertiesStructure(locale);
        if (propertiesStructure != null) {
            return propertiesStructure.getItem(key);
        }
        return null;
    }

    private ItemElem createNewItemElem(Locale locale, String key, String value, String comment) {
        PropertiesFileEntry entry = locale2file.get(locale);
        if (entry != null) {
            PropertiesStructure structure = entry.getHandler().getStructure();
            if (structure.getItem(key) != null) {
                throw new IllegalArgumentException("Property key: " + key + " in the locale: " + locale + " already exists.");
            }
            structure.addItem(key, value, comment);
            return structure.getItem(key);
        }
        throw new IllegalArgumentException("Cannot find properties file for locale: " + locale.toString());
    }

    private void bundleChanged(PropertyBundleEvent e) {
        if (e.getChangeType() == PropertyBundleEvent.CHANGE_ITEM) {
            PropertiesFileEntry entry = bundleStructure.getEntryByFileName(e.getEntryName());
            Locale locale = getLocaleFromPropertiesFileEntry(entry);
            ItemElem item = entry.getHandler().getStructure().getItem(e.getItemName());
            firePropertyChangedEvent(locale, item.getKey(), item.getValue(), item.getComment());
        }
    }

    protected void firePropertyChangedEvent(Locale locale, String key, String value, String comment) {
        BridgeBundleEvent event = new BridgeBundleEvent(this, EventType.PROPERTY_CHANGED, locale, key, value, comment);
        for (BridgeEventListener l : listeners) {
            l.bundleChanged(event);
        }
    }

    public void addBridgeEventListener(BridgeEventListener l) {
        listeners.add(l);
    }

    public void removeBridgeEventListener(BridgeEventListener l) {
        listeners.remove(l);
    }

    public static class ImplFactory implements Factory {

        public ResourceBundleEditorBridge get(DataObject dataObject) {
            if (dataObject instanceof PropertiesDataObject) {
                return new ResourceBundleEditorBridgeImpl((PropertiesDataObject) dataObject);
            }
            throw new IllegalStateException("Cannot find BundleStructure in the data object " + dataObject.getName());
        }
    }
}
