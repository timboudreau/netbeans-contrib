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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.stripwhitespace;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.openide.ErrorManager;

/**
 * Not using SystemOptions because it's ugly and shouldn't be called
 * from ModuleInstall. I know, this class isn't very nice either...
 *
 * @author Andrei Badea
 */
public class StripWhitespaceOptions {

    public static final String PROP_HIGHLIGHTING_ENABLED = "highlightingEnabled"; // NOI18N
    public static final String PROP_HIGHLIGHTING_COLOR = "highlightingColor"; // NOI18N

    private static final String PROPERTIES_FILE = "stripwhitespace.properties"; // NOI18N

    private static StripWhitespaceOptions DEFAULT = new StripWhitespaceOptions();

    private Properties props = new Properties();
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

    public static StripWhitespaceOptions getDefault() {
        return DEFAULT;
    }

    public StripWhitespaceOptions() {
        readProperties();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public synchronized boolean getHighlightingEnabled() {
        String value = props.getProperty(PROP_HIGHLIGHTING_ENABLED, "true"); // NOI18N
        return Boolean.valueOf(value).booleanValue();
    }

    public synchronized void setHighlightingEnabled(boolean highlightingEnabled) {
        boolean old = getHighlightingEnabled();
        props.setProperty(PROP_HIGHLIGHTING_ENABLED, Boolean.toString(highlightingEnabled));
        propertySupport.firePropertyChange(PROP_HIGHLIGHTING_ENABLED, old, highlightingEnabled);

        writeProperties();
    }

    public synchronized Color getHighlightingColor() {
        String value = props.getProperty(PROP_HIGHLIGHTING_COLOR, "0xf0f0f0"); // NOI18N
        return Color.decode(value);
    }

    public synchronized void setHighlightingColor(Color highlightingColor) {
        Color old = getHighlightingColor();
        int color = (highlightingColor.getRed() << 16) +
                    (highlightingColor.getGreen() << 8) +
                    (highlightingColor.getBlue());
        props.setProperty(PROP_HIGHLIGHTING_COLOR, "0x" + Integer.toHexString(color)); // NOI18N
        propertySupport.firePropertyChange(PROP_HIGHLIGHTING_COLOR, old, highlightingColor);

        writeProperties();
    }

    private void readProperties() {
        File propsFile = getPropertiesFile();
        if (!propsFile.exists()) {
            return;
        }

        try {
            InputStream stream = new FileInputStream(propsFile);
            try {
                props.load(stream);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    private void writeProperties() {
        try {
            OutputStream stream = new FileOutputStream(getPropertiesFile());
            try {
                props.store(stream, null);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    private File getPropertiesFile() {
        return new File(System.getProperty("netbeans.user"), PROPERTIES_FILE); // NOI18N
    }
}
