/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.spi;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Locale;
import org.openide.loaders.DataObject;

/**
 *
 * @author denis
 */
public interface ResourceBundleEditorBridge {

    Collection<String> getKeys();

    Collection<Locale> getLocales();

    Property getLocaleProperty(Locale locale, String key, boolean createIfNotExists);

    void setPropertyComment(Locale locale, String key, String comment);

    String getPropertyComment(Locale locale, String key);

    void setPropertyValue(Locale locale, String key, String value);

    String getPropertyValue(Locale locale, String key);

    boolean isPropertyExists(Locale locale, String key);

    void addBridgeEventListener(BridgeEventListener l);

    void removeBridgeEventListener(BridgeEventListener l);

    void deleteProperty(String key);

    public interface Factory {

        ResourceBundleEditorBridge get(DataObject dataObject);
    }

    public static class Property {

        protected Locale locale;
        protected String key;
        protected String value;
        protected String comment;

        public Property() {
        }

        public Property(Locale locale, String key, String value, String comment) {
            this.locale = locale;
            this.key = key;
            this.value = value;
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum EventType {

        LOCALE_ADDED, LOCALE_DELETED, PROPERTY_CREATED, PROPERTY_CHANGED, PROPERTY_DELETED
    }

    public static interface BridgeEventListener extends EventListener {

        void bundleChanged(BridgeBundleEvent event);
    }

    public static class BridgeBundleEvent extends EventObject {

        /** The event type */
        protected EventType type;
        /** The event locale */
        protected Locale locale;
        /** The key */
        protected String key;
        /** The value */
        protected String value;
        /** The comment */
        protected String comment;

        public BridgeBundleEvent(Object source, EventType type, Locale locale) {
            super(source);
            this.type = type;
            this.locale = locale;
        }

        public BridgeBundleEvent(Object source, EventType type, Locale locale, String key) {
            super(source);
            this.type = type;
            this.locale = locale;
            this.key = key;
        }

        public BridgeBundleEvent(Object source, EventType type, Locale locale, String key, String value, String comment) {
            super(source);
            this.type = type;
            this.locale = locale;
            this.key = key;
            this.value = value;
            this.comment = comment;
        }

        public EventType getType() {
            return type;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getComment() {
            return comment;
        }
    }
}
