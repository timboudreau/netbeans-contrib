/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.quickfilechooser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * Install the proper UI.
 * @author Jesse Glick
 */
public class Install {
    
    private static final String KEY = "FileChooserUI";
    private static Class originalImpl;
    private static PropertyChangeListener pcl;

    /**
     * Register the new UI.
     */
    public static void main(String[] args) {
        install();
    }
    
    public static void install() {
        final UIDefaults uid = UIManager.getDefaults();
        originalImpl = uid.getUIClass(KEY);
        Class impl = ChooserComponentUI.class;
        final String val = impl.getName();
        uid.put(KEY, val);
        // To make it work in NetBeans too:
        uid.put(val, impl);
        // #61147: prevent NB from switching to a different UI later (under GTK):
        uid.addPropertyChangeListener(pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if ((name.equals(KEY) || name.equals("UIDefaults")) && !val.equals(uid.get(KEY))) {
                    uid.put(KEY, val);
                }
            }
        });
    }
    
    public static void uninstall() {
        if (isInstalled()) {
            assert pcl != null;
            UIDefaults uid = UIManager.getDefaults();
            uid.removePropertyChangeListener(pcl);
            pcl = null;
            String val = originalImpl.getName();
            uid.put(KEY, val);
            uid.put(val, originalImpl);
            originalImpl = null;
        }
    }
    
    public static boolean isInstalled() {
        return originalImpl != null;
    }
    
}
