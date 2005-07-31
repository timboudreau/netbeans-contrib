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
package org.netbeans.modules.prefsdemo;

import org.netbeans.modules.prefsettings.api.PrefsNode;
import org.openide.loaders.DataObject;
/**
 *
 * @author Timothy Boudreau
 */
public class CustomPrefsNode extends PrefsNode {
    public CustomPrefsNode(DataObject ob) {
        super (ob);
    }

    protected void initProperties() {
        addProperty ("name", "My Name", "Joe Schmo");
        addProperty ("age", "Age", 37);
        addProperty ("appearance", "Appearance", "stately", new String[] {"stately", "rotund", "debonaire"});
        addProperty ("smoker", "Smoker", false);
    }

    protected String validate(String preferencesKey, Object value) {
        if ("age".equals(preferencesKey)) {
            int age = ((Integer) value).intValue();
            if (age < 0) {
                return "You can't have a negative age";
            }
        }
        return null;
    }
}
