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

package org.netbeans.modules.corba.poasupport;

import java.util.ResourceBundle;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.settings.*;
/*
 *
 * @author Dusan Balek
 */

public class POASupport implements java.beans.PropertyChangeListener {

    private static ResourceBundle bundle = NbBundle.getBundle (POASupport.class);
    private static POASupport instance;

    static {
        instance = new POASupport((CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true));
    }

    public static String getString(String str) {
        return bundle.getString(str);
    }
    
    public static POASettings getPOASettings() {
        return instance.settings;
    }

    public static CORBASupportSettings getCORBASettings() {
        return instance.corbaSettings;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if ("_M_orb_tag".equals(p1.getPropertyName())) { // NOI18N
            settings = corbaSettings.getActiveSetting().getPOASettings();
        }
    }

    private POASettings settings;
    private CORBASupportSettings corbaSettings;
    
    private POASupport(CORBASupportSettings _corbaSettings) {
        corbaSettings = _corbaSettings;
        settings = _corbaSettings.getActiveSetting().getPOASettings();
        _corbaSettings.addPropertyChangeListener(this);
    }
}
