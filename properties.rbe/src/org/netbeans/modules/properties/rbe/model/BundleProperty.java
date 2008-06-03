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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.netbeans.modules.properties.Element.ItemElem;

/**
 * The Nundle property
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public final class BundleProperty implements Comparable<BundleProperty> {

    /** The property bundle */
    private Bundle bundle;
    /** The name of the property */
    private String name;
    /** The fullname of the property */
    private String fullname;
    /** The different locale representation of the property */
    private Map<Locale, ItemElem> localeRepresentation;
    /** The property change support */
    private PropertyChangeSupport propertyChangeSupport;
    /** The property change event names */
    public static final String PROPERTY_LOCALES = "PROPERTY_LOCALES";

    public BundleProperty(Bundle bundle, String name, String fullname) {
        this.name = name;
        this.fullname = fullname;
        this.bundle = bundle;

        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Map<Locale, ItemElem> getLocaleRepresentation() {
        return localeRepresentation == null ? Collections.<Locale, ItemElem>emptyMap() : Collections.unmodifiableMap(localeRepresentation);
    }

    public boolean isEmpty() {
        return localeRepresentation == null ? true : localeRepresentation.isEmpty();
    }

    public void addLocaleRepresentation(Locale locale, ItemElem itemElem) {
        if (localeRepresentation == null) {
            localeRepresentation = new HashMap<Locale, ItemElem>();
        }
        localeRepresentation.put(locale, itemElem);
        propertyChangeSupport.firePropertyChange(PROPERTY_LOCALES, null, null);
    }

    public void removeLocaleRepresentation(Locale locale) {
        if (localeRepresentation.containsKey(locale)) {
            localeRepresentation.remove(locale);
            propertyChangeSupport.firePropertyChange(PROPERTY_LOCALES, null, null);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public int compareTo(BundleProperty o) {
        return this.fullname.compareTo(o.fullname);
    }
}
