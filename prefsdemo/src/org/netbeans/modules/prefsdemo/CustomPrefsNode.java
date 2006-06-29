/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
