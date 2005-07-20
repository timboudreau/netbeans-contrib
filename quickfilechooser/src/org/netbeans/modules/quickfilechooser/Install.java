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

    /**
     * Register the new UI.
     */
    public static void main(String[] args) {
        final UIDefaults uid = UIManager.getDefaults();
        final String key = "FileChooserUI";
        Class impl = ChooserComponentUI.class;
        final String val = impl.getName();
        uid.put(key, val);
        // To make it work in NetBeans too:
        uid.put(val, impl);
        // #61147: prevent NB from switching to a different UI later (under GTK):
        uid.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if ((name.equals(key) || name.equals("UIDefaults")) && !val.equals(uid.get(key))) {
                    uid.put(key, val);
                }
            }
        });
    }
    
}
