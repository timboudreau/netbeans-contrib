/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

import java.util.Locale;
import org.netbeans.modules.properties.Element.ItemElem;

/**
 *
 * @author denis
 */
public class BundlePropertyValue implements Comparable<BundlePropertyValue> {

    private BundleProperty property;
    private Locale locale;
    private ItemElem itemElem;

    protected BundlePropertyValue(BundleProperty property, Locale locale, ItemElem itemElem) {
        this.property = property;
        this.locale = locale;
        this.itemElem = itemElem;
    }

    public String getKey() {
        return property.getKey();
    }

    public String getValue() {
        return itemElem == null ? "" : itemElem.getValue();
    }

    public String getComment() {
        return itemElem == null ? "" : itemElem.getComment();
    }

    public void setValue(String value) {
        if (itemElem == null) {
            itemElem = property.getBundle().createNewItemElem(locale, property.getKey(), value, "");
        } else {
            itemElem.setValue(value);
        }
    }

    public void setComment(String comment) {
        if (itemElem == null) {
            itemElem = property.getBundle().createNewItemElem(locale, property.getKey(), "", comment);
        } else {
            itemElem.setComment(comment);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public BundleProperty getProperty() {
        return property;
    }

    public int compareTo(BundlePropertyValue o) {
        if (!property.equals(o.property)) {
            return property.getKey().compareTo(o.property.getKey());
        }
        return Bundle.LOCALE_COMPARATOR.compare(locale, o.locale);
    }
}
