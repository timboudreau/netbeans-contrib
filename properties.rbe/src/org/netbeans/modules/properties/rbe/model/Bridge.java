/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

/**
 *
 * @author denis
 */
public class Bridge {

    private Bundle bundle;
    private BundleStructure bundleStructure;
    private Map<Locale, PropertiesFileEntry> locale2file = new HashMap<Locale, PropertiesFileEntry>();

    private Bridge(BundleStructure bundleStructure, Bundle bundle) {
        this.bundleStructure = bundleStructure;
        this.bundle = bundle;
        bundleStructure.addPropertyBundleListener(new PropertyBundleListener() {

            public void bundleChanged(PropertyBundleEvent e) {
                Bridge.this.bundleChanged(e);
            }
        });

        initLocales();
    }

    public static Bridge get(PropertiesDataObject dataObject, Bundle bundle) {
        return new Bridge(dataObject.getBundleStructure(), bundle);
    }

//    public BundlePropertyValue createPropertyBundleValue(Locale locale, BundleProperty bundleProperty) {
//        createNewItemElem(locale, bundleProperty.getKey(), "", "");
//        return getBundlePropertyValue(locale, bundleProperty, true);
//    }
    public BundlePropertyValue getBundlePropertyValue(Locale locale, BundleProperty bundleProperty, boolean createIfNotExists) {
        ItemElem itemElem = getItemElem(locale, bundleProperty.getKey());
        BundlePropertyValue value = null;
        if (itemElem != null) {
            value = new BundlePropertyValue(bundleProperty, locale, itemElem.getValue(), itemElem.getComment());
        } else if (createIfNotExists) {
            value = new BundlePropertyValue(bundleProperty, locale, "", ""); //Not created yet
        }
        return value;
    }

    public Collection<String> getKeys() {
        return Arrays.asList(bundleStructure.getKeys());
    }

    public String getPropertyValue(Locale locale, String key) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            throw new IllegalArgumentException("Cannot find properties file for locale: " + locale.toString());
        }
        return itemElem.getValue();
    }

    public void setPropertyValue(Locale locale, String key, String value) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            createNewItemElem(locale, key, value, "");
        } else {
            itemElem.setValue(value);
        }
    }

    public void setPropertyComment(Locale locale, String key, String comment) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            createNewItemElem(locale, key, "", comment);
        } else {
            itemElem.setComment(comment);
        }
    }

    public boolean isPropertyExists(Locale locale, String key) {
        return getItemElem(locale, key) != null;
    }

    private void initLocales() {
        for (int e = 0; e < bundleStructure.getEntryCount(); e++) {
            PropertiesFileEntry entry = bundleStructure.getNthEntry(e);
            Locale locale = getLocaleFromPropertiesFileEntry(entry);
            locale2file.put(locale, entry);
            bundle.addLocale(locale);
        }
    }

    private Locale getLocaleFromPropertiesFileEntry(PropertiesFileEntry entry) {
        String localeSuffix = Util.getLocaleSuffix(entry);
        return localeSuffix.length() == 0 ? Bundle.DEFAULT_LOCALE : new Locale(Util.getLanguage(localeSuffix), Util.getCountry(localeSuffix), Util.getVariant(localeSuffix));
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

    private ItemElem getItemElem(Locale locale, String key) {
        PropertiesFileEntry entry = locale2file.get(locale);
        if (entry != null) {
            return entry.getHandler().getStructure().getItem(key);
        }
        return null;
    }

    private void bundleChanged(PropertyBundleEvent e) {
        if (e.getChangeType() == PropertyBundleEvent.CHANGE_ITEM) {
            PropertiesFileEntry entry = bundleStructure.getEntryByFileName(e.getEntryName());
            Locale locale = getLocaleFromPropertiesFileEntry(entry);
            BundleProperty property = bundle.getProperty(e.getItemName());
            if (property != null) {
                ItemElem newItem = entry.getHandler().getStructure().getItem(e.getItemName());
                property.getLocalRepresentation(locale).updateValue(newItem.getValue());
                property.getLocalRepresentation(locale).updateComment(newItem.getComment());
            }
        }
    }
}
