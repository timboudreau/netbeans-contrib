/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.netbeans.modules.properties.BundleStructure;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.PropertiesFileEntry;
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
            entry.getHandler().getStructure().addItem(key, value, comment);
            return entry.getHandler().getStructure().getItem(key);
        }
        return null;
    }

    public void createPropertyValues(BundleProperty bundleProperty) {
        for (Locale locale : locale2file.keySet()) {
            createNewItemElem(locale, bundleProperty.getKey(), "", "");
        }
    }

    private void setBundlePropertyValues(BundleProperty bundleProperty) {
        for (Locale locale : locale2file.keySet()) {
            ItemElem itemElem = getItemElem(locale, bundleProperty.getKey());
            BundlePropertyValue value;
            if (itemElem != null) {
                value = new BundlePropertyValue(bundleProperty, locale, itemElem.getValue(), itemElem.getComment());
            } else {
                value = new BundlePropertyValue(bundleProperty, locale, "", ""); //Not created yet
            }
            bundleProperty.addLocaleRepresentation(locale, value);
        }
    }

    public void createProperties() {
        for (int k = 0; k < bundleStructure.getKeyCount(); k++) {
            String propertyName = bundleStructure.getKeys()[k];
            BundleProperty bundleProperty = bundle.createBundleProperty(propertyName);
            setBundlePropertyValues(bundleProperty);
        }
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

    private ItemElem getItemElem(Locale locale, String key) {
        PropertiesFileEntry entry = locale2file.get(locale);
        if (entry != null) {
            return entry.getHandler().getStructure().getItem(key);
        }
        return null;
    }

    protected void bundleChanged(PropertyBundleEvent e) {
//        if (e.getChangeType() == PropertyBundleEvent.CHANGE_ITEM) {
//            PropertiesFileEntry entry = bundleStructure.getEntryByFileName(e.getEntryName());
//            Locale locale = getLocaleFromPropertiesFileEntry(entry);
//            BundleProperty property = bundle.getBundleProperty(e.getItemName());
//            if (property != null) {
//                ItemElem newItem = entry.getHandler().getStructure().getItem(e.getItemName());
//                property.getLocalRepresentation(locale).updateValue(newItem.getComment());
//                property.getLocalRepresentation(locale).updateComment(newItem.getValue());
//            }
//        }
    }
}
