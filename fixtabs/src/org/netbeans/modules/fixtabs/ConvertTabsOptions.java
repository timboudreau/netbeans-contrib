/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.fixtabs;

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
public class ConvertTabsOptions {

    public static final String PROP_HIGHLIGHTING_ENABLED = "highlightingEnabled"; // NOI18N
    public static final String PROP_HIGHLIGHTING_COLOR = "highlightingColor"; // NOI18N

    private static final String PROPERTIES_FILE = "fixtabs.properties"; // NOI18N

    private static ConvertTabsOptions DEFAULT = new ConvertTabsOptions();

    private Properties props = new Properties();
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

    public static ConvertTabsOptions getDefault() {
        return DEFAULT;
    }

    public ConvertTabsOptions() {
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
        String value = props.getProperty(PROP_HIGHLIGHTING_COLOR, "0xf08888"); // NOI18N
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
