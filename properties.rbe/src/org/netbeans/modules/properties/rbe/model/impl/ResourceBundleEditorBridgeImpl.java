/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model.impl;

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

/**
 *
 * @author denis
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

        initLocales();
    }
//    public BundlePropertyValue createPropertyBundleValue(Locale locale, BundleProperty bundleProperty) {
//        createNewItemElem(locale, bundleProperty.getKey(), "", "");
//        return getBundlePropertyValue(locale, bundleProperty, true);
//    }
//    public LocaleProperty getLocaleProperty(Locale locale, BundleProperty bundleProperty, boolean createIfNotExists) {
//        ItemElem itemElem = getItemElem(locale, bundleProperty.getKey());
//        LocaleProperty localeProperty = null;
//        if (itemElem != null) {
//            localeProperty = new LocaleProperty(bundleProperty, locale, itemElem.getValue(), itemElem.getComment());
//        } else if (createIfNotExists) {
//            createNewItemElem(locale, bundleProperty.getKey(), "", "");
//            localeProperty = new LocaleProperty(bundleProperty, locale, "", ""); //Not created yet
//        }
//        return localeProperty;
//    }

    public Property getLocaleProperty(Locale locale, String key, boolean createIfNotExists) {
        ItemElem itemElem = getItemElem(locale, key);
        Property property = null;
        if (itemElem != null) {
            property = new Property(locale, key, itemElem.getValue(), itemElem.getComment());
        } else if (createIfNotExists) {
            createNewItemElem(locale, key, "", "");
            property = new Property(locale, key, "", "");
        }
        return property;
    }

    public Collection<String> getKeys() {
        return Arrays.asList(bundleStructure.getKeys());
    }

    public Collection<Locale> getLocales() {
        return locale2file.keySet();
    }

    public String getPropertyValue(Locale locale, String key) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            throw new IllegalArgumentException("Cannot find properties file for locale: " + locale.toString());
        }
        return itemElem.getValue();
    }

    public String getPropertyComment(Locale locale, String key) {
        ItemElem itemElem = getItemElem(locale, key);
        if (itemElem == null) {
            throw new IllegalArgumentException("Cannot find properties file for locale: " + locale.toString());
        }
        return itemElem.getComment();
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

    public void deleteProperty(String key) {
        bundleStructure.removeItem(key);
    }

    public boolean isPropertyExists(Locale locale, String key) {
        return getItemElem(locale, key) != null;
    }

    private void initLocales() {
        for (int e = 0; e < bundleStructure.getEntryCount(); e++) {
            PropertiesFileEntry entry = bundleStructure.getNthEntry(e);
            Locale locale = getLocaleFromPropertiesFileEntry(entry);
            locale2file.put(locale, entry);
        }
    }

    private Locale getLocaleFromPropertiesFileEntry(PropertiesFileEntry entry) {
        String localeSuffix = Util.getLocaleSuffix(entry);
        return localeSuffix.length() == 0 ? Bundle.DEFAULT_LOCALE : new Locale(Util.getLanguage(localeSuffix), Util.getCountry(localeSuffix), Util.getVariant(localeSuffix));
    }

    private ItemElem getItemElem(Locale locale, String key) {
        PropertiesFileEntry entry = locale2file.get(locale);
        if (entry != null) {
            return entry.getHandler().getStructure().getItem(key);
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
//            PropertiesFileEntry entry = bundleStructure.getEntryByFileName(e.getEntryName());
//            Locale locale = getLocaleFromPropertiesFileEntry(entry);
//            BundleProperty property = bundle.getProperty(e.getItemName());
//            if (property != null) {
//                ItemElem newItem = entry.getHandler().getStructure().getItem(e.getItemName());
//                LocaleProperty value = property.getLocalProperty(locale);
//                if (value == null) {
//                    property.addLocaleProperty(locale, new LocaleProperty(property, locale, newItem.getValue(), newItem.getComment()));
//                } else {
//                    value.updateValue(newItem.getValue());
//                    value.updateComment(newItem.getComment());
//                }
//            }
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
