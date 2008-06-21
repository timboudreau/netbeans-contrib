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
package org.netbeans.modules.properties.rbe.spi;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Locale;
import org.openide.loaders.DataObject;

/**
 * The bridge between RBE and old properties module
 * @author @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public interface ResourceBundleEditorBridge {

    Collection<String> getKeys();

    Collection<Locale> getLocales();

    void createLocaleProperty(Locale locale, String key, String value, String comment);

    void setLocalePropertyComment(Locale locale, String key, String comment);

    String getLocalePropertyComment(Locale locale, String key);

    void setLocalePropertyValue(Locale locale, String key, String value);

    String getLocalePropertyValue(Locale locale, String key);

    boolean isLocalePropertyExists(Locale locale, String key);

    boolean isPropertyExists(String key);

    void deleteProperty(String key);

    void addBridgeEventListener(BridgeEventListener l);

    void removeBridgeEventListener(BridgeEventListener l);

    public interface Factory {

        ResourceBundleEditorBridge get(DataObject dataObject);
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
